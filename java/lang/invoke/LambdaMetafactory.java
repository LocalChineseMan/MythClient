package java.lang.invoke;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.InnerClassLambdaMetafactory;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

public class LambdaMetafactory {
  public static final int FLAG_SERIALIZABLE = 1;
  
  public static final int FLAG_MARKERS = 2;
  
  public static final int FLAG_BRIDGES = 4;
  
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
  
  private static final MethodType[] EMPTY_MT_ARRAY = new MethodType[0];
  
  public static CallSite metafactory(MethodHandles.Lookup paramLookup, String paramString, MethodType paramMethodType1, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3) throws LambdaConversionException {
    InnerClassLambdaMetafactory innerClassLambdaMetafactory = new InnerClassLambdaMetafactory(paramLookup, paramMethodType1, paramString, paramMethodType2, paramMethodHandle, paramMethodType3, false, EMPTY_CLASS_ARRAY, EMPTY_MT_ARRAY);
    innerClassLambdaMetafactory.validateMetafactoryArgs();
    return innerClassLambdaMetafactory.buildCallSite();
  }
  
  public static CallSite altMetafactory(MethodHandles.Lookup paramLookup, String paramString, MethodType paramMethodType, Object... paramVarArgs) throws LambdaConversionException {
    Class<?>[] arrayOfClass;
    MethodType arrayOfMethodType[], methodType1 = (MethodType)paramVarArgs[0];
    MethodHandle methodHandle = (MethodHandle)paramVarArgs[1];
    MethodType methodType2 = (MethodType)paramVarArgs[2];
    int i = ((Integer)paramVarArgs[3]).intValue();
    int j = 4;
    if ((i & 0x2) != 0) {
      int k = ((Integer)paramVarArgs[j++]).intValue();
      arrayOfClass = new Class[k];
      System.arraycopy(paramVarArgs, j, arrayOfClass, 0, k);
      j += k;
    } else {
      arrayOfClass = EMPTY_CLASS_ARRAY;
    } 
    if ((i & 0x4) != 0) {
      int k = ((Integer)paramVarArgs[j++]).intValue();
      arrayOfMethodType = new MethodType[k];
      System.arraycopy(paramVarArgs, j, arrayOfMethodType, 0, k);
      j += k;
    } else {
      arrayOfMethodType = EMPTY_MT_ARRAY;
    } 
    boolean bool = ((i & 0x1) != 0) ? true : false;
    if (bool) {
      boolean bool1 = Serializable.class.isAssignableFrom(paramMethodType.returnType());
      for (Class<?> clazz : arrayOfClass)
        bool1 |= Serializable.class.isAssignableFrom(clazz); 
      if (!bool1) {
        arrayOfClass = (Class[])Arrays.<Class<?>[]>copyOf((Class<?>[][])arrayOfClass, arrayOfClass.length + 1);
        arrayOfClass[arrayOfClass.length - 1] = Serializable.class;
      } 
    } 
    InnerClassLambdaMetafactory innerClassLambdaMetafactory = new InnerClassLambdaMetafactory(paramLookup, paramMethodType, paramString, methodType1, methodHandle, methodType2, bool, arrayOfClass, arrayOfMethodType);
    innerClassLambdaMetafactory.validateMetafactoryArgs();
    return innerClassLambdaMetafactory.buildCallSite();
  }
}
