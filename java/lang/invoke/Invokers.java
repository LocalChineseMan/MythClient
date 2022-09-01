package java.lang.invoke;

import java.io.Serializable;
import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.CallSite;
import java.lang.invoke.DirectMethodHandle;
import java.lang.invoke.ForceInline;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.Invokers;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.Stable;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Array;
import java.util.Arrays;

class Invokers {
  private final MethodType targetType;
  
  @Stable
  private final MethodHandle[] invokers = new MethodHandle[3];
  
  static final int INV_EXACT = 0;
  
  static final int INV_GENERIC = 1;
  
  static final int INV_BASIC = 2;
  
  static final int INV_LIMIT = 3;
  
  private static final int MH_LINKER_ARG_APPENDED = 1;
  
  private static final LambdaForm.NamedFunction NF_checkExactType;
  
  private static final LambdaForm.NamedFunction NF_checkGenericType;
  
  private static final LambdaForm.NamedFunction NF_getCallSiteTarget;
  
  Invokers(MethodType paramMethodType) {
    this.targetType = paramMethodType;
  }
  
  MethodHandle exactInvoker() {
    MethodHandle methodHandle = cachedInvoker(0);
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = makeExactOrGeneralInvoker(true);
    return setCachedInvoker(0, methodHandle);
  }
  
  MethodHandle genericInvoker() {
    MethodHandle methodHandle = cachedInvoker(1);
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = makeExactOrGeneralInvoker(false);
    return setCachedInvoker(1, methodHandle);
  }
  
  MethodHandle basicInvoker() {
    MethodHandle methodHandle = cachedInvoker(2);
    if (methodHandle != null)
      return methodHandle; 
    MethodType methodType = this.targetType.basicType();
    if (methodType != this.targetType)
      return setCachedInvoker(2, methodType.invokers().basicInvoker()); 
    methodHandle = methodType.form().cachedMethodHandle(0);
    if (methodHandle == null) {
      MemberName memberName = invokeBasicMethod(methodType);
      methodHandle = DirectMethodHandle.make(memberName);
      assert checkInvoker(methodHandle);
      methodHandle = methodType.form().setCachedMethodHandle(0, methodHandle);
    } 
    return setCachedInvoker(2, methodHandle);
  }
  
  private MethodHandle cachedInvoker(int paramInt) {
    return this.invokers[paramInt];
  }
  
  private synchronized MethodHandle setCachedInvoker(int paramInt, MethodHandle paramMethodHandle) {
    MethodHandle methodHandle = this.invokers[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    this.invokers[paramInt] = paramMethodHandle;
    return paramMethodHandle;
  }
  
  private MethodHandle makeExactOrGeneralInvoker(boolean paramBoolean) {
    MethodType methodType1 = this.targetType;
    MethodType methodType2 = methodType1.invokerType();
    byte b = paramBoolean ? 11 : 13;
    LambdaForm lambdaForm = invokeHandleForm(methodType1, false, b);
    BoundMethodHandle boundMethodHandle = BoundMethodHandle.bindSingle(methodType2, lambdaForm, methodType1);
    String str = paramBoolean ? "invokeExact" : "invoke";
    MethodHandle methodHandle = boundMethodHandle.withInternalMemberName(MemberName.makeMethodHandleInvoke(str, methodType1), false);
    assert checkInvoker(methodHandle);
    maybeCompileToBytecode(methodHandle);
    return methodHandle;
  }
  
  private void maybeCompileToBytecode(MethodHandle paramMethodHandle) {
    if (this.targetType == this.targetType.erase() && this.targetType
      .parameterCount() < 10)
      paramMethodHandle.form.compileToBytecode(); 
  }
  
  static MemberName invokeBasicMethod(MethodType paramMethodType) {
    assert paramMethodType == paramMethodType.basicType();
    try {
      return MethodHandles.Lookup.IMPL_LOOKUP.resolveOrFail((byte)5, MethodHandle.class, "invokeBasic", paramMethodType);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError("JVM cannot find invoker for " + paramMethodType, reflectiveOperationException);
    } 
  }
  
  private boolean checkInvoker(MethodHandle paramMethodHandle) {
    assert this.targetType.invokerType().equals(paramMethodHandle.type()) : 
      Arrays.asList((T[])new Object[] { this.targetType, this.targetType.invokerType(), paramMethodHandle });
    assert paramMethodHandle.internalMemberName() == null || paramMethodHandle
      .internalMemberName().getMethodType().equals(this.targetType);
    assert !paramMethodHandle.isVarargsCollector();
    return true;
  }
  
  MethodHandle spreadInvoker(int paramInt) {
    int i = this.targetType.parameterCount() - paramInt;
    MethodType methodType1 = this.targetType;
    Class<?> clazz = impliedRestargType(methodType1, paramInt);
    if (methodType1.parameterSlotCount() <= 253)
      return genericInvoker().asSpreader(clazz, i); 
    MethodType methodType2 = methodType1.replaceParameterTypes(paramInt, methodType1.parameterCount(), new Class[] { clazz });
    MethodHandle methodHandle1 = MethodHandles.invoker(methodType2);
    MethodHandle methodHandle2 = MethodHandles.insertArguments(Lazy.access$000(), 1, new Object[] { clazz, Integer.valueOf(i) });
    return MethodHandles.filterArgument(methodHandle1, 0, methodHandle2);
  }
  
  private static Class<?> impliedRestargType(MethodType paramMethodType, int paramInt) {
    if (paramMethodType.isGeneric())
      return Object[].class; 
    int i = paramMethodType.parameterCount();
    if (paramInt >= i)
      return Object[].class; 
    Class<?> clazz = paramMethodType.parameterType(paramInt);
    for (int j = paramInt + 1; j < i; j++) {
      if (clazz != paramMethodType.parameterType(j))
        throw MethodHandleStatics.newIllegalArgumentException("need homogeneous rest arguments", paramMethodType); 
    } 
    if (clazz == Object.class)
      return Object[].class; 
    return Array.newInstance(clazz, 0).getClass();
  }
  
  public String toString() {
    return "Invokers" + this.targetType;
  }
  
  static MemberName methodHandleInvokeLinkerMethod(String paramString, MethodType paramMethodType, Object[] paramArrayOfObject) {
    byte b;
    LambdaForm lambdaForm;
    switch (paramString) {
      case "invokeExact":
        b = 10;
        break;
      case "invoke":
        b = 12;
        break;
      default:
        throw new InternalError("not invoker: " + paramString);
    } 
    if (paramMethodType.parameterSlotCount() <= 253) {
      lambdaForm = invokeHandleForm(paramMethodType, false, b);
      paramArrayOfObject[0] = paramMethodType;
    } else {
      lambdaForm = invokeHandleForm(paramMethodType, true, b);
    } 
    return lambdaForm.vmentry;
  }
  
  private static LambdaForm invokeHandleForm(MethodType paramMethodType, boolean paramBoolean, int paramInt) {
    boolean bool1, bool2, bool3;
    String str;
    if (!paramBoolean) {
      paramMethodType = paramMethodType.basicType();
      bool1 = true;
    } else {
      bool1 = false;
    } 
    switch (paramInt) {
      case 10:
        bool2 = true;
        bool3 = false;
        str = "invokeExact_MT";
        break;
      case 11:
        bool2 = false;
        bool3 = false;
        str = "exactInvoker";
        break;
      case 12:
        bool2 = true;
        bool3 = true;
        str = "invoke_MT";
        break;
      case 13:
        bool2 = false;
        bool3 = true;
        str = "invoker";
        break;
      default:
        throw new InternalError();
    } 
    if (bool1) {
      LambdaForm lambdaForm1 = paramMethodType.form().cachedLambdaForm(paramInt);
      if (lambdaForm1 != null)
        return lambdaForm1; 
    } 
    int i = 0 + (bool2 ? 0 : 1);
    int j = i + 1;
    int k = j + paramMethodType.parameterCount();
    int m = k + ((bool2 && !paramBoolean) ? 1 : 0);
    int n = k;
    byte b = paramBoolean ? -1 : n++;
    int i1 = n++;
    int i2 = n++;
    MethodType methodType1 = paramMethodType.invokerType();
    if (bool2) {
      if (!paramBoolean)
        methodType1 = methodType1.appendParameterTypes(new Class[] { MemberName.class }); 
    } else {
      methodType1 = methodType1.invokerType();
    } 
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(n - m, methodType1);
    assert arrayOfName.length == n : 
      Arrays.asList((T[])new Serializable[] { paramMethodType, Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt), Integer.valueOf(n), Integer.valueOf(arrayOfName.length) });
    if (b >= m) {
      assert arrayOfName[b] == null;
      BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_L();
      arrayOfName[0] = arrayOfName[0].withConstraint(speciesData);
      LambdaForm.NamedFunction namedFunction = speciesData.getterFunction(0);
      arrayOfName[b] = new LambdaForm.Name(namedFunction, new Object[] { arrayOfName[0] });
    } 
    MethodType methodType2 = paramMethodType.basicType();
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, i, k, Object[].class);
    Object object = paramBoolean ? paramMethodType : arrayOfName[b];
    if (!bool3) {
      arrayOfName[i1] = new LambdaForm.Name(NF_checkExactType, new Object[] { arrayOfName[i], object });
    } else {
      arrayOfName[i1] = new LambdaForm.Name(NF_checkGenericType, new Object[] { arrayOfName[i], object });
      arrayOfObject[0] = arrayOfName[i1];
    } 
    arrayOfName[i2] = new LambdaForm.Name(methodType2, arrayOfObject);
    LambdaForm lambdaForm = new LambdaForm(str, m, arrayOfName);
    if (bool2)
      lambdaForm.compileToBytecode(); 
    if (bool1)
      lambdaForm = paramMethodType.form().setCachedLambdaForm(paramInt, lambdaForm); 
    return lambdaForm;
  }
  
  static WrongMethodTypeException newWrongMethodTypeException(MethodType paramMethodType1, MethodType paramMethodType2) {
    return new WrongMethodTypeException("expected " + paramMethodType2 + " but found " + paramMethodType1);
  }
  
  @ForceInline
  static void checkExactType(Object paramObject1, Object paramObject2) {
    MethodHandle methodHandle = (MethodHandle)paramObject1;
    MethodType methodType1 = (MethodType)paramObject2;
    MethodType methodType2 = methodHandle.type();
    if (methodType2 != methodType1)
      throw newWrongMethodTypeException(methodType1, methodType2); 
  }
  
  @ForceInline
  static Object checkGenericType(Object paramObject1, Object paramObject2) {
    MethodHandle methodHandle = (MethodHandle)paramObject1;
    MethodType methodType = (MethodType)paramObject2;
    return methodHandle.asType(methodType);
  }
  
  static MemberName linkToCallSiteMethod(MethodType paramMethodType) {
    LambdaForm lambdaForm = callSiteForm(paramMethodType, false);
    return lambdaForm.vmentry;
  }
  
  static MemberName linkToTargetMethod(MethodType paramMethodType) {
    LambdaForm lambdaForm = callSiteForm(paramMethodType, true);
    return lambdaForm.vmentry;
  }
  
  private static LambdaForm callSiteForm(MethodType paramMethodType, boolean paramBoolean) {
    paramMethodType = paramMethodType.basicType();
    byte b = paramBoolean ? 15 : 14;
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(b);
    if (lambdaForm != null)
      return lambdaForm; 
    int i = 0 + paramMethodType.parameterCount();
    int j = i + 1;
    int k = i;
    int m = k++;
    boolean bool = paramBoolean ? true : m;
    int n = paramBoolean ? m : k++;
    int i1 = k++;
    MethodType methodType = paramMethodType.appendParameterTypes(new Class[] { paramBoolean ? MethodHandle.class : CallSite.class });
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(k - j, methodType);
    assert arrayOfName.length == k;
    assert arrayOfName[m] != null;
    if (!paramBoolean)
      arrayOfName[n] = new LambdaForm.Name(NF_getCallSiteTarget, new Object[] { arrayOfName[bool] }); 
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, i + 1, Object[].class);
    System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, arrayOfObject.length - 1);
    arrayOfObject[0] = arrayOfName[n];
    arrayOfName[i1] = new LambdaForm.Name(paramMethodType, arrayOfObject);
    lambdaForm = new LambdaForm(paramBoolean ? "linkToTargetMethod" : "linkToCallSite", j, arrayOfName);
    lambdaForm.compileToBytecode();
    lambdaForm = paramMethodType.form().setCachedLambdaForm(b, lambdaForm);
    return lambdaForm;
  }
  
  @ForceInline
  static Object getCallSiteTarget(Object paramObject) {
    return ((CallSite)paramObject).getTarget();
  }
  
  static {
    try {
      LambdaForm.NamedFunction[] arrayOfNamedFunction = { NF_checkExactType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkExactType", new Class[] { Object.class, Object.class })), NF_checkGenericType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkGenericType", new Class[] { Object.class, Object.class })), NF_getCallSiteTarget = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("getCallSiteTarget", new Class[] { Object.class })) };
      for (LambdaForm.NamedFunction namedFunction : arrayOfNamedFunction) {
        assert InvokerBytecodeGenerator.isStaticallyInvocable(namedFunction.member) : namedFunction;
        namedFunction.resolve();
      } 
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw MethodHandleStatics.newInternalError(reflectiveOperationException);
    } 
  }
  
  private static class Invokers {}
}
