import static com.github.forax.pro.Pro.*;
import static com.github.forax.pro.builder.Builders.*;

var rewriter = command("rewriter", () -> {  // rewrite bytecode to use indy instead of getfield/pufield/invokevirtual/invokeinterface
  runner.
    modulePath(location("target/main/artifact/")).
    module("fr.umlv.indyeverywhere").
    mainArguments("target/main/exploded/fr.umlv.indyeverywhere.test/");
  run(runner);
})

resolver.
    dependencies(
        // ASM
        "org.objectweb.asm:7.0"
    )

compiler.
    sourceRelease(11)
   
packager.
    modules(
        "fr.umlv.indyeverywhere@1.0/fr.umlv.indyeverywhere.tool.Rewriter",
        "fr.umlv.indyeverywhere.test@1.0"
    )   
    
runner.
    modulePath(location("target/main/exploded/")).  // patched code
    module("fr.umlv.indyeverywhere.test/fr.umlv.indyeverywhere.test.IndyEveryWhereTest")    
    
run(resolver, compiler,  packager, rewriter, runner)

/exit errorCode()
