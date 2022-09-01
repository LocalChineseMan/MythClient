package sun.invoke.util;

import java.lang.invoke.MethodType;
import sun.invoke.empty.Empty;

public class VerifyType {
  public static boolean isNullConversion(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean) {
    if (paramClass1 == paramClass2)
      return true; 
    if (!paramBoolean) {
      if (paramClass2.isInterface())
        paramClass2 = Object.class; 
      if (paramClass1.isInterface())
        paramClass1 = Object.class; 
      if (paramClass1 == paramClass2)
        return true; 
    } 
    if (isNullType(paramClass1))
      return !paramClass2.isPrimitive(); 
    if (!paramClass1.isPrimitive())
      return paramClass2.isAssignableFrom(paramClass1); 
    if (!paramClass2.isPrimitive())
      return false; 
    Wrapper wrapper1 = Wrapper.forPrimitiveType(paramClass1);
    if (paramClass2 == int.class)
      return wrapper1.isSubwordOrInt(); 
    Wrapper wrapper2 = Wrapper.forPrimitiveType(paramClass2);
    if (!wrapper1.isSubwordOrInt())
      return false; 
    if (!wrapper2.isSubwordOrInt())
      return false; 
    if (!wrapper2.isSigned() && wrapper1.isSigned())
      return false; 
    return (wrapper2.bitWidth() > wrapper1.bitWidth());
  }
  
  public static boolean isNullReferenceConversion(Class<?> paramClass1, Class<?> paramClass2) {
    assert !paramClass2.isPrimitive();
    if (paramClass2.isInterface())
      return true; 
    if (isNullType(paramClass1))
      return true; 
    return paramClass2.isAssignableFrom(paramClass1);
  }
  
  public static boolean isNullType(Class<?> paramClass) {
    if (paramClass == Void.class)
      return true; 
    if (paramClass == Empty.class)
      return true; 
    return false;
  }
  
  public static boolean isNullConversion(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean) {
    if (paramMethodType1 == paramMethodType2)
      return true; 
    int i = paramMethodType1.parameterCount();
    if (i != paramMethodType2.parameterCount())
      return false; 
    for (byte b = 0; b < i; b++) {
      if (!isNullConversion(paramMethodType1.parameterType(b), paramMethodType2.parameterType(b), paramBoolean))
        return false; 
    } 
    return isNullConversion(paramMethodType2.returnType(), paramMethodType1.returnType(), paramBoolean);
  }
  
  public static int canPassUnchecked(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return 1; 
    if (paramClass2.isPrimitive()) {
      if (paramClass2 == void.class)
        return 1; 
      if (paramClass1 == void.class)
        return 0; 
      if (!paramClass1.isPrimitive())
        return 0; 
      Wrapper wrapper1 = Wrapper.forPrimitiveType(paramClass1);
      Wrapper wrapper2 = Wrapper.forPrimitiveType(paramClass2);
      if (wrapper1.isSubwordOrInt() && wrapper2.isSubwordOrInt()) {
        if (wrapper1.bitWidth() >= wrapper2.bitWidth())
          return -1; 
        if (!wrapper2.isSigned() && wrapper1.isSigned())
          return -1; 
        return 1;
      } 
      if (paramClass1 == float.class || paramClass2 == float.class) {
        if (paramClass1 == double.class || paramClass2 == double.class)
          return -1; 
        return 0;
      } 
      return 0;
    } 
    if (paramClass1.isPrimitive())
      return 0; 
    if (isNullReferenceConversion(paramClass1, paramClass2))
      return 1; 
    return -1;
  }
  
  public static boolean isSpreadArgType(Class<?> paramClass) {
    return paramClass.isArray();
  }
  
  public static Class<?> spreadArgElementType(Class<?> paramClass, int paramInt) {
    return paramClass.getComponentType();
  }
}
