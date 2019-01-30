package fr.umlv.indyeverywhere.tool;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.walk;
import static java.nio.file.Files.write;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class Rewriter {
  private static final Handle FIELD_BSM = new Handle(H_INVOKESTATIC, "fr/umlv/indyeverywhere/RT",
      "bsm_field",
      MethodType.methodType(CallSite.class, Lookup.class, String.class, MethodType.class).toMethodDescriptorString(),
      false);
  private static final Handle METH_BSM = new Handle(H_INVOKESTATIC, "fr/umlv/indyeverywhere/RT",
      "bsm_meth",
      MethodType.methodType(CallSite.class, Lookup.class, String.class, MethodType.class).toMethodDescriptorString(),
      false);
  private static final Pattern PATCHER_PATTERN = Pattern.compile("_+([^_]*)_+");
  
  private static String patchName(String name, String descriptor) {
    var matcher = PATCHER_PATTERN.matcher(name);
    if (!matcher.matches()) {  // patch method name ?
      return name;
    }
    var newName = matcher.group(1);
    System.out.println("  patch method name " + name + descriptor + " to " + newName + descriptor);
    return newName;
  }
  
  private static boolean needRewrite(String owner, String name, String descriptor) {
    return !owner.startsWith("java");
  }
  
  public static byte[] rewrite(byte[] code) {
    var reader = new ClassReader(code);
    var writer = new ClassWriter(reader, 0);
    
    reader.accept(new ClassVisitor(ASM7, writer) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        var mv = super.visitMethod(access, patchName(name, descriptor), descriptor, signature, exceptions);
        return new MethodVisitor(ASM7, mv) {
          @Override
          public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (needRewrite(owner, name, descriptor)) {
              switch(opcode) {
              case GETFIELD:
                System.out.println("  getfield " + owner + '.' + name + ' ' + descriptor);
                super.visitInvokeDynamicInsn(name, "(L" + owner + ";)" + descriptor, FIELD_BSM);
                return;
              case PUTFIELD:
                System.out.println("  putfield " + owner + '.' + name + ' ' + descriptor);
                super.visitInvokeDynamicInsn(name, "(L" + owner + ';' + descriptor + ")V", FIELD_BSM);
                return;
              default:
              //case GETSTATIC:
              //case PUTSTATIC:
              } 
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
          }
          
          @Override
          public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            var newName = patchName(name, descriptor);
            if (needRewrite(owner, newName, descriptor)) {
              switch(opcode) {
              case INVOKEINTERFACE:
              case INVOKEVIRTUAL:
                System.out.println("  invoke[virtual|interface] " + owner + '.' + newName + ' ' + descriptor);
                super.visitInvokeDynamicInsn(newName,  "(L" + owner + ';' + descriptor.substring(1), METH_BSM);
                return;
              default:
              //case INVOKESPECIAL:
              //case INVOKESTATIC:
              } 
            }
            super.visitMethodInsn(opcode, owner, newName, descriptor, isInterface);
          }
        };
      }
    }, 0);
    return writer.toByteArray();
  }
  
  public static void main(String[] args) throws IOException {
    var directory = Path.of(args[0]);
    System.out.println("rewrite " + directory + " to use indy everywhere");
    
    try(var stream = walk(directory)) {
      stream
        .filter(path -> path.getFileName().toString().endsWith(".class"))
        .forEach(path -> {
          try {
            System.out.println("rewrite file " + path);
            write(path, rewrite(readAllBytes(path)));
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
    } catch(UncheckedIOException e) {
      throw e.getCause();
    }
  }
}
