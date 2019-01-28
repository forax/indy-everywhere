package fr.umlv.indyeverywhere;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public class RT {
  public static CallSite bsm_field(Lookup lookup, String name, MethodType type) throws NoSuchFieldException, IllegalAccessException {
    System.out.println("bsm_field: lookup " + lookup + ' ' + name + type);
    MethodHandle mh;
    if (type.returnType() == void.class) {
      mh = lookup.findSetter(type.parameterType(0), name, type.parameterType(1));
    } else {
      mh = lookup.findGetter(type.parameterType(0), name, type.returnType());
    }
    return new ConstantCallSite(mh);
  }
  
  public static CallSite bsm_meth(Lookup lookup, String name, MethodType type)  throws NoSuchMethodException, IllegalAccessException {
    System.out.println("bsm_meth: lookup " + lookup + ' ' + name + type);
    var mh = lookup.findVirtual(type.parameterType(0), name, type.dropParameterTypes(0, 1));
    return new ConstantCallSite(mh);
  }
}
