package java.lang.invoke;

import java.lang.invoke.AbstractValidatingLambdaMetafactory;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import sun.invoke.util.Wrapper;

abstract class AbstractValidatingLambdaMetafactory {
  final Class<?> targetClass;
  
  final MethodType invokedType;
  
  final Class<?> samBase;
  
  final String samMethodName;
  
  final MethodType samMethodType;
  
  final MethodHandle implMethod;
  
  final MethodHandleInfo implInfo;
  
  final int implKind;
  
  final boolean implIsInstanceMethod;
  
  final Class<?> implDefiningClass;
  
  final MethodType implMethodType;
  
  final MethodType instantiatedMethodType;
  
  final boolean isSerializable;
  
  final Class<?>[] markerInterfaces;
  
  final MethodType[] additionalBridges;
  
  AbstractValidatingLambdaMetafactory(MethodHandles.Lookup paramLookup, MethodType paramMethodType1, String paramString, MethodType paramMethodType2, MethodHandle paramMethodHandle, MethodType paramMethodType3, boolean paramBoolean, Class<?>[] paramArrayOfClass, MethodType[] paramArrayOfMethodType) throws LambdaConversionException {
    if ((paramLookup.lookupModes() & 0x2) == 0)
      throw new LambdaConversionException(String.format("Invalid caller: %s", new Object[] { paramLookup
              
              .lookupClass().getName() })); 
    this.targetClass = paramLookup.lookupClass();
    this.invokedType = paramMethodType1;
    this.samBase = paramMethodType1.returnType();
    this.samMethodName = paramString;
    this.samMethodType = paramMethodType2;
    this.implMethod = paramMethodHandle;
    this.implInfo = paramLookup.revealDirect(paramMethodHandle);
    this.implKind = this.implInfo.getReferenceKind();
    this.implIsInstanceMethod = (this.implKind == 5 || this.implKind == 7 || this.implKind == 9);
    this.implDefiningClass = this.implInfo.getDeclaringClass();
    this.implMethodType = this.implInfo.getMethodType();
    this.instantiatedMethodType = paramMethodType3;
    this.isSerializable = paramBoolean;
    this.markerInterfaces = paramArrayOfClass;
    this.additionalBridges = paramArrayOfMethodType;
    if (!this.samBase.isInterface())
      throw new LambdaConversionException(String.format("Functional interface %s is not an interface", new Object[] { this.samBase
              
              .getName() })); 
    for (Class<?> clazz : paramArrayOfClass) {
      if (!clazz.isInterface())
        throw new LambdaConversionException(String.format("Marker interface %s is not an interface", new Object[] { clazz
                
                .getName() })); 
    } 
  }
  
  abstract CallSite buildCallSite() throws LambdaConversionException;
  
  void validateMetafactoryArgs() throws LambdaConversionException {
    byte b2, b3;
    switch (this.implKind) {
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
        break;
      default:
        throw new LambdaConversionException(String.format("Unsupported MethodHandle kind: %s", new Object[] { this.implInfo }));
    } 
    int i = this.implMethodType.parameterCount();
    byte b1 = this.implIsInstanceMethod ? 1 : 0;
    int j = this.invokedType.parameterCount();
    int k = this.samMethodType.parameterCount();
    int m = this.instantiatedMethodType.parameterCount();
    if (i + b1 != j + k)
      throw new LambdaConversionException(
          String.format("Incorrect number of parameters for %s method %s; %d captured parameters, %d functional interface method parameters, %d implementation parameters", new Object[] { this.implIsInstanceMethod ? "instance" : "static", this.implInfo, Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i) })); 
    if (m != k)
      throw new LambdaConversionException(
          String.format("Incorrect number of parameters for %s method %s; %d instantiated parameters, %d functional interface method parameters", new Object[] { this.implIsInstanceMethod ? "instance" : "static", this.implInfo, Integer.valueOf(m), Integer.valueOf(k) })); 
    for (MethodType methodType : this.additionalBridges) {
      if (methodType.parameterCount() != k)
        throw new LambdaConversionException(
            String.format("Incorrect number of parameters for bridge signature %s; incompatible with %s", new Object[] { methodType, this.samMethodType })); 
    } 
    if (this.implIsInstanceMethod) {
      Class<?> clazz4;
      if (j == 0) {
        b2 = 0;
        b3 = 1;
        clazz4 = this.instantiatedMethodType.parameterType(0);
      } else {
        b2 = 1;
        b3 = 0;
        clazz4 = this.invokedType.parameterType(0);
      } 
      if (!this.implDefiningClass.isAssignableFrom(clazz4))
        throw new LambdaConversionException(
            String.format("Invalid receiver type %s; not a subtype of implementation type %s", new Object[] { clazz4, this.implDefiningClass })); 
      Class<?> clazz5 = this.implMethod.type().parameterType(0);
      if (clazz5 != this.implDefiningClass && !clazz5.isAssignableFrom(clazz4))
        throw new LambdaConversionException(
            String.format("Invalid receiver type %s; not a subtype of implementation receiver type %s", new Object[] { clazz4, clazz5 })); 
    } else {
      b2 = 0;
      b3 = 0;
    } 
    int n = j - b2;
    int i1;
    for (i1 = 0; i1 < n; i1++) {
      Class<?> clazz4 = this.implMethodType.parameterType(i1);
      Class<?> clazz5 = this.invokedType.parameterType(i1 + b2);
      if (!clazz5.equals(clazz4))
        throw new LambdaConversionException(
            String.format("Type mismatch in captured lambda parameter %d: expecting %s, found %s", new Object[] { Integer.valueOf(i1), clazz5, clazz4 })); 
    } 
    i1 = b3 - n;
    for (int i2 = n; i2 < i; i2++) {
      Class<?> clazz4 = this.implMethodType.parameterType(i2);
      Class<?> clazz5 = this.instantiatedMethodType.parameterType(i2 + i1);
      if (!isAdaptableTo(clazz5, clazz4, true))
        throw new LambdaConversionException(
            String.format("Type mismatch for lambda argument %d: %s is not convertible to %s", new Object[] { Integer.valueOf(i2), clazz5, clazz4 })); 
    } 
    Class<?> clazz1 = this.instantiatedMethodType.returnType();
    Class<?> clazz2 = (this.implKind == 8) ? this.implDefiningClass : this.implMethodType.returnType();
    Class<?> clazz3 = this.samMethodType.returnType();
    if (!isAdaptableToAsReturn(clazz2, clazz1))
      throw new LambdaConversionException(
          String.format("Type mismatch for lambda return: %s is not convertible to %s", new Object[] { clazz2, clazz1 })); 
    if (!isAdaptableToAsReturnStrict(clazz1, clazz3))
      throw new LambdaConversionException(
          String.format("Type mismatch for lambda expected return: %s is not convertible to %s", new Object[] { clazz1, clazz3 })); 
    for (MethodType methodType : this.additionalBridges) {
      if (!isAdaptableToAsReturnStrict(clazz1, methodType.returnType()))
        throw new LambdaConversionException(
            String.format("Type mismatch for lambda expected return: %s is not convertible to %s", new Object[] { clazz1, methodType.returnType() })); 
    } 
  }
  
  private boolean isAdaptableTo(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean) {
    if (paramClass1.equals(paramClass2))
      return true; 
    if (paramClass1.isPrimitive()) {
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass1);
      if (paramClass2.isPrimitive()) {
        Wrapper wrapper1 = Wrapper.forPrimitiveType(paramClass2);
        return wrapper1.isConvertibleFrom(wrapper);
      } 
      return paramClass2.isAssignableFrom(wrapper.wrapperType());
    } 
    if (paramClass2.isPrimitive()) {
      Wrapper wrapper;
      if (Wrapper.isWrapperType(paramClass1) && (wrapper = Wrapper.forWrapperType(paramClass1)).primitiveType().isPrimitive()) {
        Wrapper wrapper1 = Wrapper.forPrimitiveType(paramClass2);
        return wrapper1.isConvertibleFrom(wrapper);
      } 
      return !paramBoolean;
    } 
    return (!paramBoolean || paramClass2.isAssignableFrom(paramClass1));
  }
  
  private boolean isAdaptableToAsReturn(Class<?> paramClass1, Class<?> paramClass2) {
    return (paramClass2.equals(void.class) || (
      !paramClass1.equals(void.class) && isAdaptableTo(paramClass1, paramClass2, false)));
  }
  
  private boolean isAdaptableToAsReturnStrict(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1.equals(void.class))
      return paramClass2.equals(void.class); 
    return isAdaptableTo(paramClass1, paramClass2, true);
  }
}
