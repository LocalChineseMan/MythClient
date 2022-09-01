package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DelegatingMethodHandle;
import java.lang.invoke.ForceInline;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaForm.Hidden;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SimpleMethodHandle;
import java.lang.invoke.Stable;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import sun.invoke.empty.Empty;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;

abstract class MethodHandleImpl {
  private static final int MAX_ARITY;
  
  static {
    final Object[] values = { Integer.valueOf(255) };
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            values[0] = Integer.getInteger(MethodHandleImpl.class.getName() + ".MAX_ARITY", 255);
            return null;
          }
        });
    MAX_ARITY = ((Integer)arrayOfObject[0]).intValue();
  }
  
  static void initStatics() {
    MemberName.Factory.INSTANCE.getClass();
  }
  
  static MethodHandle makeArrayElementAccessor(Class<?> paramClass, boolean paramBoolean) {
    if (paramClass == Object[].class)
      return paramBoolean ? ArrayAccessor.OBJECT_ARRAY_SETTER : ArrayAccessor.OBJECT_ARRAY_GETTER; 
    if (!paramClass.isArray())
      throw MethodHandleStatics.newIllegalArgumentException("not an array: " + paramClass); 
    MethodHandle[] arrayOfMethodHandle = ArrayAccessor.TYPED_ACCESSORS.get(paramClass);
    boolean bool = paramBoolean ? true : false;
    MethodHandle methodHandle = arrayOfMethodHandle[bool];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = ArrayAccessor.getAccessor(paramClass, paramBoolean);
    MethodType methodType = ArrayAccessor.correctType(paramClass, paramBoolean);
    if (methodHandle.type() != methodType) {
      assert methodHandle.type().parameterType(0) == Object[].class;
      assert (paramBoolean ? (Class)methodHandle.type().parameterType(2) : (Class)methodHandle.type().returnType()) == Object.class;
      assert paramBoolean || methodType.parameterType(0).getComponentType() == methodType.returnType();
      methodHandle = methodHandle.viewAsType(methodType, false);
    } 
    methodHandle = makeIntrinsic(methodHandle, paramBoolean ? Intrinsic.ARRAY_STORE : Intrinsic.ARRAY_LOAD);
    synchronized (arrayOfMethodHandle) {
      if (arrayOfMethodHandle[bool] == null) {
        arrayOfMethodHandle[bool] = methodHandle;
      } else {
        methodHandle = arrayOfMethodHandle[bool];
      } 
    } 
    return methodHandle;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    MethodType methodType = paramMethodHandle.type();
    if (paramMethodType == methodType)
      return paramMethodHandle; 
    return makePairwiseConvertByEditor(paramMethodHandle, paramMethodType, paramBoolean1, paramBoolean2);
  }
  
  private static int countNonNull(Object[] paramArrayOfObject) {
    byte b = 0;
    for (Object object : paramArrayOfObject) {
      if (object != null)
        b++; 
    } 
    return b;
  }
  
  static MethodHandle makePairwiseConvertByEditor(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    Object[] arrayOfObject = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = countNonNull(arrayOfObject);
    if (i == 0)
      return paramMethodHandle.viewAsType(paramMethodType, paramBoolean1); 
    MethodType methodType1 = paramMethodType.basicType();
    MethodType methodType2 = paramMethodHandle.type().basicType();
    BoundMethodHandle boundMethodHandle = paramMethodHandle.rebind();
    for (byte b = 0; b < arrayOfObject.length - 1; b++) {
      Object object1 = arrayOfObject[b];
      if (object1 != null) {
        MethodHandle methodHandle;
        if (object1 instanceof Class) {
          methodHandle = Lazy.MH_castReference.bindTo(object1);
        } else {
          methodHandle = (MethodHandle)object1;
        } 
        Class<?> clazz = methodType1.parameterType(b);
        if (--i == 0) {
          methodType2 = paramMethodType;
        } else {
          methodType2 = methodType2.changeParameterType(b, clazz);
        } 
        LambdaForm lambdaForm = boundMethodHandle.editor().filterArgumentForm(1 + b, LambdaForm.BasicType.basicType(clazz));
        boundMethodHandle = boundMethodHandle.copyWithExtendL(methodType2, lambdaForm, methodHandle);
        boundMethodHandle = boundMethodHandle.rebind();
      } 
    } 
    Object object = arrayOfObject[arrayOfObject.length - 1];
    if (object != null) {
      MethodHandle methodHandle;
      if (object instanceof Class) {
        if (object == void.class) {
          methodHandle = null;
        } else {
          methodHandle = Lazy.MH_castReference.bindTo(object);
        } 
      } else {
        methodHandle = (MethodHandle)object;
      } 
      Class<?> clazz = methodType1.returnType();
      assert --i == 0;
      methodType2 = paramMethodType;
      if (methodHandle != null) {
        boundMethodHandle = boundMethodHandle.rebind();
        LambdaForm lambdaForm = boundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType(clazz), false);
        boundMethodHandle = boundMethodHandle.copyWithExtendL(methodType2, lambdaForm, methodHandle);
      } else {
        LambdaForm lambdaForm = boundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType(clazz), true);
        boundMethodHandle = boundMethodHandle.copyWith(methodType2, lambdaForm);
      } 
    } 
    assert i == 0;
    assert boundMethodHandle.type().equals(paramMethodType);
    return boundMethodHandle;
  }
  
  static MethodHandle makePairwiseConvertIndirect(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    assert paramMethodHandle.type().parameterCount() == paramMethodType.parameterCount();
    Object[] arrayOfObject1 = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = paramMethodType.parameterCount();
    int j = countNonNull(arrayOfObject1);
    boolean bool1 = (arrayOfObject1[i] != null) ? true : false;
    boolean bool2 = (paramMethodType.returnType() == void.class) ? true : false;
    if (bool1 && bool2) {
      j--;
      bool1 = false;
    } 
    int k = 1 + i;
    int m = k + j + 1;
    boolean bool3 = !bool1 ? true : (m - 1);
    int n = (!bool1 ? m : bool3) - 1;
    boolean bool4 = bool2 ? true : (m - 1);
    MethodType methodType = paramMethodType.basicType().invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(m - k, methodType);
    Object[] arrayOfObject2 = new Object[0 + i];
    int i1 = k;
    for (byte b = 0; b < i; b++) {
      Object object1 = arrayOfObject1[b];
      if (object1 == null) {
        arrayOfObject2[0 + b] = arrayOfName[1 + b];
      } else {
        LambdaForm.Name name;
        if (object1 instanceof Class) {
          Class clazz = (Class)object1;
          name = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { clazz, arrayOfName[1 + b] });
        } else {
          MethodHandle methodHandle = (MethodHandle)object1;
          name = new LambdaForm.Name(methodHandle, new Object[] { arrayOfName[1 + b] });
        } 
        assert arrayOfName[i1] == null;
        arrayOfName[i1++] = name;
        assert arrayOfObject2[0 + b] == null;
        arrayOfObject2[0 + b] = name;
      } 
    } 
    assert i1 == n;
    arrayOfName[n] = new LambdaForm.Name(paramMethodHandle, arrayOfObject2);
    Object object = arrayOfObject1[i];
    if (!bool1) {
      assert n == arrayOfName.length - 1;
    } else {
      LambdaForm.Name name;
      if (object == void.class) {
        name = new LambdaForm.Name(LambdaForm.constantZero(LambdaForm.BasicType.basicType(paramMethodType.returnType())), new Object[0]);
      } else if (object instanceof Class) {
        Class clazz = (Class)object;
        name = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { clazz, arrayOfName[n] });
      } else {
        MethodHandle methodHandle = (MethodHandle)object;
        if (methodHandle.type().parameterCount() == 0) {
          name = new LambdaForm.Name(methodHandle, new Object[0]);
        } else {
          name = new LambdaForm.Name(methodHandle, new Object[] { arrayOfName[n] });
        } 
      } 
      assert arrayOfName[bool3] == null;
      arrayOfName[bool3] = name;
      assert bool3 == arrayOfName.length - 1;
    } 
    LambdaForm lambdaForm = new LambdaForm("convert", methodType.parameterCount(), arrayOfName, bool4);
    return SimpleMethodHandle.make(paramMethodType, lambdaForm);
  }
  
  @ForceInline
  static <T, U> T castReference(Class<? extends T> paramClass, U paramU) {
    if (paramU != null && !paramClass.isInstance(paramU))
      throw newClassCastException(paramClass, paramU); 
    return (T)paramU;
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass, Object paramObject) {
    return new ClassCastException("Cannot cast " + paramObject.getClass().getName() + " to " + paramClass.getName());
  }
  
  static Object[] computeValueConversions(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramMethodType1.parameterCount();
    Object[] arrayOfObject = new Object[i + 1];
    for (byte b = 0; b <= i; b++) {
      boolean bool = (b == i) ? true : false;
      Class<?> clazz1 = bool ? paramMethodType2.returnType() : paramMethodType1.parameterType(b);
      Class<?> clazz2 = bool ? paramMethodType1.returnType() : paramMethodType2.parameterType(b);
      if (!VerifyType.isNullConversion(clazz1, clazz2, paramBoolean1))
        arrayOfObject[b] = valueConversion(clazz1, clazz2, paramBoolean1, paramBoolean2); 
    } 
    return arrayOfObject;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean) {
    return makePairwiseConvert(paramMethodHandle, paramMethodType, paramBoolean, false);
  }
  
  static Object valueConversion(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean1, boolean paramBoolean2) {
    MethodHandle methodHandle;
    assert !VerifyType.isNullConversion(paramClass1, paramClass2, paramBoolean1);
    if (paramClass2 == void.class)
      return paramClass2; 
    if (paramClass1.isPrimitive()) {
      if (paramClass1 == void.class)
        return void.class; 
      if (paramClass2.isPrimitive()) {
        methodHandle = ValueConversions.convertPrimitive(paramClass1, paramClass2);
      } else {
        Wrapper wrapper = Wrapper.forPrimitiveType(paramClass1);
        methodHandle = ValueConversions.boxExact(wrapper);
        assert methodHandle.type().parameterType(0) == wrapper.primitiveType();
        assert methodHandle.type().returnType() == wrapper.wrapperType();
        if (!VerifyType.isNullConversion(wrapper.wrapperType(), paramClass2, paramBoolean1)) {
          MethodType methodType = MethodType.methodType(paramClass2, paramClass1);
          if (paramBoolean1) {
            methodHandle = methodHandle.asType(methodType);
          } else {
            methodHandle = makePairwiseConvert(methodHandle, methodType, false);
          } 
        } 
      } 
    } else if (paramClass2.isPrimitive()) {
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass2);
      if (paramBoolean2 || paramClass1 == wrapper.wrapperType()) {
        methodHandle = ValueConversions.unboxExact(wrapper, paramBoolean1);
      } else {
        methodHandle = paramBoolean1 ? ValueConversions.unboxWiden(wrapper) : ValueConversions.unboxCast(wrapper);
      } 
    } else {
      return paramClass2;
    } 
    assert methodHandle.type().parameterCount() <= 1 : "pc" + Arrays.asList((T[])new Object[] { paramClass1.getSimpleName(), paramClass2.getSimpleName(), methodHandle });
    return methodHandle;
  }
  
  static MethodHandle makeVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass) {
    MethodType methodType = paramMethodHandle.type();
    int i = methodType.parameterCount() - 1;
    if (methodType.parameterType(i) != paramClass)
      paramMethodHandle = paramMethodHandle.asType(methodType.changeParameterType(i, paramClass)); 
    paramMethodHandle = paramMethodHandle.asFixedArity();
    return new AsVarargsCollector(paramMethodHandle, paramClass);
  }
  
  private static final class AsVarargsCollector extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private final Class<?> arrayType;
    
    @Stable
    private MethodHandle asCollectorCache;
    
    AsVarargsCollector(MethodHandle param1MethodHandle, Class<?> param1Class) {
      this(param1MethodHandle.type(), param1MethodHandle, param1Class);
    }
    
    AsVarargsCollector(MethodType param1MethodType, MethodHandle param1MethodHandle, Class<?> param1Class) {
      super(param1MethodType, param1MethodHandle);
      this.target = param1MethodHandle;
      this.arrayType = param1Class;
      this.asCollectorCache = param1MethodHandle.asCollector(param1Class, 0);
    }
    
    public boolean isVarargsCollector() {
      return true;
    }
    
    protected MethodHandle getTarget() {
      return this.target;
    }
    
    public MethodHandle asFixedArity() {
      return this.target;
    }
    
    MethodHandle setVarargs(MemberName param1MemberName) {
      if (param1MemberName.isVarargs())
        return this; 
      return asFixedArity();
    }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) {
      MethodHandle methodHandle2;
      MethodType methodType = type();
      int i = methodType.parameterCount() - 1;
      int j = param1MethodType.parameterCount();
      if (j == i + 1 && methodType
        .parameterType(i).isAssignableFrom(param1MethodType.parameterType(i)))
        return this.asTypeCache = asFixedArity().asType(param1MethodType); 
      MethodHandle methodHandle1 = this.asCollectorCache;
      if (methodHandle1 != null && methodHandle1.type().parameterCount() == j)
        return this.asTypeCache = methodHandle1.asType(param1MethodType); 
      int k = j - i;
      try {
        methodHandle2 = asFixedArity().asCollector(this.arrayType, k);
        assert methodHandle2.type().parameterCount() == j : "newArity=" + j + " but collector=" + methodHandle2;
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new WrongMethodTypeException("cannot build collector", illegalArgumentException);
      } 
      this.asCollectorCache = methodHandle2;
      return this.asTypeCache = methodHandle2.asType(param1MethodType);
    }
    
    boolean viewAsTypeChecks(MethodType param1MethodType, boolean param1Boolean) {
      super.viewAsTypeChecks(param1MethodType, true);
      if (param1Boolean)
        return true; 
      assert type().lastParameterType().getComponentType()
        .isAssignableFrom(param1MethodType
          .lastParameterType().getComponentType()) : 
        Arrays.asList((T[])new Object[] { this, param1MethodType });
      return true;
    }
  }
  
  static MethodHandle makeSpreadArguments(MethodHandle paramMethodHandle, Class<?> paramClass, int paramInt1, int paramInt2) {
    MethodType methodType1 = paramMethodHandle.type();
    for (byte b1 = 0; b1 < paramInt2; b1++) {
      Class<?> clazz = VerifyType.spreadArgElementType(paramClass, b1);
      if (clazz == null)
        clazz = Object.class; 
      methodType1 = methodType1.changeParameterType(paramInt1 + b1, clazz);
    } 
    paramMethodHandle = paramMethodHandle.asType(methodType1);
    MethodType methodType2 = methodType1.replaceParameterTypes(paramInt1, paramInt1 + paramInt2, new Class[] { paramClass });
    MethodType methodType3 = methodType2.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(paramInt2 + 2, methodType3);
    int i = methodType3.parameterCount();
    int[] arrayOfInt = new int[methodType1.parameterCount()];
    byte b3;
    for (byte b2 = 0; b2 < methodType1.parameterCount() + 1; b2++, b3++) {
      Class<?> clazz = methodType3.parameterType(b2);
      if (b2 == paramInt1) {
        MethodHandle methodHandle = MethodHandles.arrayElementGetter(paramClass);
        LambdaForm.Name name = arrayOfName1[b3];
        arrayOfName1[i++] = new LambdaForm.Name(Lazy.NF_checkSpreadArgument, new Object[] { name, Integer.valueOf(paramInt2) });
        for (byte b = 0; b < paramInt2; b2++, b++) {
          arrayOfInt[b2] = i;
          arrayOfName1[i++] = new LambdaForm.Name(methodHandle, new Object[] { name, Integer.valueOf(b) });
        } 
      } else if (b2 < arrayOfInt.length) {
        arrayOfInt[b2] = b3;
      } 
    } 
    assert i == arrayOfName1.length - 1;
    LambdaForm.Name[] arrayOfName2 = new LambdaForm.Name[methodType1.parameterCount()];
    for (b3 = 0; b3 < methodType1.parameterCount(); b3++) {
      int j = arrayOfInt[b3];
      arrayOfName2[b3] = arrayOfName1[j];
    } 
    arrayOfName1[arrayOfName1.length - 1] = new LambdaForm.Name(paramMethodHandle, (Object[])arrayOfName2);
    LambdaForm lambdaForm = new LambdaForm("spread", methodType3.parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(methodType2, lambdaForm);
  }
  
  static void checkSpreadArgument(Object paramObject, int paramInt) {
    if (paramObject == null) {
      if (paramInt == 0)
        return; 
    } else if (paramObject instanceof Object[]) {
      int i = ((Object[])paramObject).length;
      if (i == paramInt)
        return; 
    } else {
      int i = Array.getLength(paramObject);
      if (i == paramInt)
        return; 
    } 
    throw MethodHandleStatics.newIllegalArgumentException("array is not of length " + paramInt);
  }
  
  static class Lazy {
    private static final Class<?> MHI = MethodHandleImpl.class;
    
    private static final MethodHandle[] ARRAYS = MethodHandleImpl.makeArrays();
    
    private static final MethodHandle[] FILL_ARRAYS = MethodHandleImpl.makeFillArrays();
    
    static final LambdaForm.NamedFunction NF_checkSpreadArgument;
    
    static final LambdaForm.NamedFunction NF_guardWithCatch;
    
    static final LambdaForm.NamedFunction NF_throwException;
    
    static final MethodHandle MH_castReference;
    
    static final MethodHandle MH_selectAlternative;
    
    static final MethodHandle MH_copyAsPrimitiveArray;
    
    static final MethodHandle MH_fillNewTypedArray;
    
    static final MethodHandle MH_fillNewArray;
    
    static final MethodHandle MH_arrayIdentity;
    
    static {
      try {
        NF_checkSpreadArgument = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("checkSpreadArgument", new Class[] { Object.class, int.class }));
        NF_guardWithCatch = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("guardWithCatch", new Class[] { MethodHandle.class, Class.class, MethodHandle.class, Object[].class }));
        NF_throwException = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("throwException", new Class[] { Throwable.class }));
        NF_checkSpreadArgument.resolve();
        NF_guardWithCatch.resolve();
        NF_throwException.resolve();
        MH_castReference = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "castReference", 
            MethodType.methodType(Object.class, Class.class, new Class[] { Object.class }));
        MH_copyAsPrimitiveArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "copyAsPrimitiveArray", 
            MethodType.methodType(Object.class, Wrapper.class, new Class[] { Object[].class }));
        MH_arrayIdentity = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "identity", 
            MethodType.methodType(Object[].class, Object[].class));
        MH_fillNewArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewArray", 
            MethodType.methodType(Object[].class, Integer.class, new Class[] { Object[].class }));
        MH_fillNewTypedArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewTypedArray", 
            MethodType.methodType(Object[].class, Object[].class, new Class[] { Integer.class, Object[].class }));
        MH_selectAlternative = MethodHandleImpl.makeIntrinsic(MethodHandles.Lookup.IMPL_LOOKUP
            .findStatic(MHI, "selectAlternative", 
              MethodType.methodType(MethodHandle.class, boolean.class, new Class[] { MethodHandle.class, MethodHandle.class })), MethodHandleImpl.Intrinsic.SELECT_ALTERNATIVE);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.newInternalError(reflectiveOperationException);
      } 
    }
  }
  
  static MethodHandle makeCollectArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt, boolean paramBoolean) {
    MethodType methodType1 = paramMethodHandle1.type();
    MethodType methodType2 = paramMethodHandle2.type();
    int i = methodType2.parameterCount();
    Class<?> clazz = methodType2.returnType();
    byte b = (clazz == void.class) ? 0 : 1;
    MethodType methodType3 = methodType1.dropParameterTypes(paramInt, paramInt + b);
    if (!paramBoolean)
      methodType3 = methodType3.insertParameterTypes(paramInt, methodType2.parameterList()); 
    MethodType methodType4 = methodType3.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(2, methodType4);
    int j = arrayOfName1.length - 2;
    int k = arrayOfName1.length - 1;
    LambdaForm.Name[] arrayOfName2 = Arrays.<LambdaForm.Name>copyOfRange(arrayOfName1, 1 + paramInt, 1 + paramInt + i);
    arrayOfName1[j] = new LambdaForm.Name(paramMethodHandle2, (Object[])arrayOfName2);
    LambdaForm.Name[] arrayOfName3 = new LambdaForm.Name[methodType1.parameterCount()];
    int m = 1;
    int n = 0;
    int i1 = paramInt;
    System.arraycopy(arrayOfName1, m, arrayOfName3, n, i1);
    m += i1;
    n += i1;
    if (clazz != void.class)
      arrayOfName3[n++] = arrayOfName1[j]; 
    i1 = i;
    if (paramBoolean) {
      System.arraycopy(arrayOfName1, m, arrayOfName3, n, i1);
      n += i1;
    } 
    m += i1;
    i1 = arrayOfName3.length - n;
    System.arraycopy(arrayOfName1, m, arrayOfName3, n, i1);
    assert m + i1 == j;
    arrayOfName1[k] = new LambdaForm.Name(paramMethodHandle1, (Object[])arrayOfName3);
    LambdaForm lambdaForm = new LambdaForm("collect", methodType4.parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(methodType3, lambdaForm);
  }
  
  @Hidden
  static MethodHandle selectAlternative(boolean paramBoolean, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
    return paramBoolean ? paramMethodHandle1 : paramMethodHandle2;
  }
  
  static MethodHandle makeGuardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
    BoundMethodHandle boundMethodHandle;
    MethodType methodType1 = paramMethodHandle2.type();
    assert paramMethodHandle1.type().equals(methodType1.changeReturnType(boolean.class)) && paramMethodHandle3.type().equals(methodType1);
    MethodType methodType2 = methodType1.basicType();
    LambdaForm lambdaForm = makeGuardWithTestForm(methodType2);
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLL();
    try {
      boundMethodHandle = speciesData.constructor().invokeBasic(methodType1, lambdaForm, paramMethodHandle1, 
          profile(paramMethodHandle2), profile(paramMethodHandle3));
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
    assert boundMethodHandle.type() == methodType1;
    return boundMethodHandle;
  }
  
  static MethodHandle profile(MethodHandle paramMethodHandle) {
    if (MethodHandleStatics.DONT_INLINE_THRESHOLD >= 0)
      return makeBlockInlningWrapper(paramMethodHandle); 
    return paramMethodHandle;
  }
  
  static MethodHandle makeBlockInlningWrapper(MethodHandle paramMethodHandle) {
    LambdaForm lambdaForm = PRODUCE_BLOCK_INLINING_FORM.apply(paramMethodHandle);
    return new CountingWrapper(paramMethodHandle, lambdaForm, PRODUCE_BLOCK_INLINING_FORM, PRODUCE_REINVOKER_FORM, MethodHandleStatics.DONT_INLINE_THRESHOLD, null);
  }
  
  private static final Function<MethodHandle, LambdaForm> PRODUCE_BLOCK_INLINING_FORM = new Function<MethodHandle, LambdaForm>() {
      public LambdaForm apply(MethodHandle param1MethodHandle) {
        return DelegatingMethodHandle.makeReinvokerForm(param1MethodHandle, 9, MethodHandleImpl.CountingWrapper.class, "reinvoker.dontInline", false, DelegatingMethodHandle.NF_getTarget, MethodHandleImpl.CountingWrapper.NF_maybeStopCounting);
      }
    };
  
  private static final Function<MethodHandle, LambdaForm> PRODUCE_REINVOKER_FORM = new Function<MethodHandle, LambdaForm>() {
      public LambdaForm apply(MethodHandle param1MethodHandle) {
        return DelegatingMethodHandle.makeReinvokerForm(param1MethodHandle, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget);
      }
    };
  
  static LambdaForm makeGuardWithTestForm(MethodType paramMethodType) {
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(17);
    if (lambdaForm != null)
      return lambdaForm; 
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = j++;
    int i2 = j++;
    int i3 = j++;
    assert i3 == i2 + 1;
    MethodType methodType1 = paramMethodType.invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType1);
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(speciesData);
    arrayOfName[k] = new LambdaForm.Name(speciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(speciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(speciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, i, Object[].class);
    MethodType methodType2 = paramMethodType.changeReturnType(boolean.class).basicType();
    arrayOfObject[0] = arrayOfName[k];
    arrayOfName[i1] = new LambdaForm.Name(methodType2, arrayOfObject);
    arrayOfName[i2] = new LambdaForm.Name(Lazy.MH_selectAlternative, new Object[] { arrayOfName[i1], arrayOfName[m], arrayOfName[n] });
    arrayOfObject[0] = arrayOfName[i2];
    arrayOfName[i3] = new LambdaForm.Name(paramMethodType, arrayOfObject);
    lambdaForm = new LambdaForm("guard", methodType1.parameterCount(), arrayOfName);
    return paramMethodType.form().setCachedLambdaForm(17, lambdaForm);
  }
  
  private static LambdaForm makeGuardWithCatchForm(MethodType paramMethodType) {
    MethodType methodType1 = paramMethodType.invokerType();
    LambdaForm lambdaForm = paramMethodType.form().cachedLambdaForm(16);
    if (lambdaForm != null)
      return lambdaForm; 
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = j++;
    int i2 = j++;
    int i3 = j++;
    int i4 = j++;
    int i5 = j++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, methodType1);
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLLLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(speciesData);
    arrayOfName[k] = new LambdaForm.Name(speciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(speciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(speciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    arrayOfName[i1] = new LambdaForm.Name(speciesData.getterFunction(3), new Object[] { arrayOfName[0] });
    arrayOfName[i2] = new LambdaForm.Name(speciesData.getterFunction(4), new Object[] { arrayOfName[0] });
    MethodType methodType2 = paramMethodType.changeReturnType(Object.class);
    MethodHandle methodHandle1 = MethodHandles.basicInvoker(methodType2);
    Object[] arrayOfObject1 = new Object[methodHandle1.type().parameterCount()];
    arrayOfObject1[0] = arrayOfName[i1];
    System.arraycopy(arrayOfName, 1, arrayOfObject1, 1, i - 1);
    arrayOfName[i3] = new LambdaForm.Name(makeIntrinsic(methodHandle1, Intrinsic.GUARD_WITH_CATCH), arrayOfObject1);
    Object[] arrayOfObject2 = { arrayOfName[k], arrayOfName[m], arrayOfName[n], arrayOfName[i3] };
    arrayOfName[i4] = new LambdaForm.Name(Lazy.NF_guardWithCatch, arrayOfObject2);
    MethodHandle methodHandle2 = MethodHandles.basicInvoker(MethodType.methodType(paramMethodType.rtype(), Object.class));
    Object[] arrayOfObject3 = { arrayOfName[i2], arrayOfName[i4] };
    arrayOfName[i5] = new LambdaForm.Name(methodHandle2, arrayOfObject3);
    lambdaForm = new LambdaForm("guardWithCatch", methodType1.parameterCount(), arrayOfName);
    return paramMethodType.form().setCachedLambdaForm(16, lambdaForm);
  }
  
  static MethodHandle makeGuardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2) {
    MethodHandle methodHandle2;
    BoundMethodHandle boundMethodHandle;
    MethodType methodType1 = paramMethodHandle1.type();
    LambdaForm lambdaForm = makeGuardWithCatchForm(methodType1.basicType());
    MethodType methodType2 = methodType1.changeReturnType(Object[].class);
    MethodHandle methodHandle1 = varargsArray(methodType1.parameterCount()).asType(methodType2);
    Class<?> clazz = methodType1.returnType();
    if (clazz.isPrimitive()) {
      if (clazz == void.class) {
        methodHandle2 = ValueConversions.ignore();
      } else {
        Wrapper wrapper = Wrapper.forPrimitiveType(methodType1.returnType());
        methodHandle2 = ValueConversions.unboxExact(wrapper);
      } 
    } else {
      methodHandle2 = MethodHandles.identity(Object.class);
    } 
    BoundMethodHandle.SpeciesData speciesData = BoundMethodHandle.speciesData_LLLLL();
    try {
      boundMethodHandle = speciesData.constructor().invokeBasic(methodType1, lambdaForm, paramMethodHandle1, paramClass, paramMethodHandle2, methodHandle1, methodHandle2);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
    assert boundMethodHandle.type() == methodType1;
    return boundMethodHandle;
  }
  
  @Hidden
  static Object guardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2, Object... paramVarArgs) throws Throwable {
    try {
      return paramMethodHandle1.asFixedArity().invokeWithArguments(paramVarArgs);
    } catch (Throwable throwable) {
      if (!paramClass.isInstance(throwable))
        throw throwable; 
      return paramMethodHandle2.asFixedArity().invokeWithArguments(prepend(throwable, paramVarArgs));
    } 
  }
  
  @Hidden
  private static Object[] prepend(Object paramObject, Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length + 1];
    arrayOfObject[0] = paramObject;
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 1, paramArrayOfObject.length);
    return arrayOfObject;
  }
  
  static MethodHandle throwException(MethodType paramMethodType) {
    assert Throwable.class.isAssignableFrom(paramMethodType.parameterType(0));
    int i = paramMethodType.parameterCount();
    if (i > 1) {
      MethodHandle methodHandle = throwException(paramMethodType.dropParameterTypes(1, i));
      methodHandle = MethodHandles.dropArguments(methodHandle, 1, paramMethodType.parameterList().subList(1, i));
      return methodHandle;
    } 
    return makePairwiseConvert(Lazy.NF_throwException.resolvedHandle(), paramMethodType, false, true);
  }
  
  static <T extends Throwable> Empty throwException(T paramT) throws T {
    throw paramT;
  }
  
  static MethodHandle[] FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
  
  static MethodHandle fakeMethodHandleInvoke(MemberName paramMemberName) {
    boolean bool;
    assert paramMemberName.isMethodHandleInvoke();
    switch (paramMemberName.getName()) {
      case "invoke":
        bool = false;
        break;
      case "invokeExact":
        bool = true;
        break;
      default:
        throw new InternalError(paramMemberName.getName());
    } 
    MethodHandle methodHandle = FAKE_METHOD_HANDLE_INVOKE[bool];
    if (methodHandle != null)
      return methodHandle; 
    MethodType methodType = MethodType.methodType(Object.class, UnsupportedOperationException.class, new Class[] { MethodHandle.class, Object[].class });
    methodHandle = throwException(methodType);
    methodHandle = methodHandle.bindTo(new UnsupportedOperationException("cannot reflectively invoke MethodHandle"));
    if (!paramMemberName.getInvocationType().equals(methodHandle.type()))
      throw new InternalError(paramMemberName.toString()); 
    methodHandle = methodHandle.withInternalMemberName(paramMemberName, false);
    methodHandle = methodHandle.asVarargsCollector(Object[].class);
    assert paramMemberName.isVarargs();
    FAKE_METHOD_HANDLE_INVOKE[bool] = methodHandle;
    return methodHandle;
  }
  
  static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass) {
    return BindCaller.bindCaller(paramMethodHandle, paramClass);
  }
  
  static MethodHandle makeWrappedMember(MethodHandle paramMethodHandle, MemberName paramMemberName, boolean paramBoolean) {
    if (paramMemberName.equals(paramMethodHandle.internalMemberName()) && paramBoolean == paramMethodHandle.isInvokeSpecial())
      return paramMethodHandle; 
    return new WrappedMember(paramMethodHandle, paramMethodHandle.type(), paramMemberName, paramBoolean, null, null);
  }
  
  enum Intrinsic {
    SELECT_ALTERNATIVE, GUARD_WITH_CATCH, NEW_ARRAY, ARRAY_LOAD, ARRAY_STORE, IDENTITY, ZERO, NONE;
  }
  
  private static final class IntrinsicMethodHandle extends DelegatingMethodHandle {
    private final MethodHandle target;
    
    private final MethodHandleImpl.Intrinsic intrinsicName;
    
    IntrinsicMethodHandle(MethodHandle param1MethodHandle, MethodHandleImpl.Intrinsic param1Intrinsic) {
      super(param1MethodHandle.type(), param1MethodHandle);
      this.target = param1MethodHandle;
      this.intrinsicName = param1Intrinsic;
    }
    
    protected MethodHandle getTarget() {
      return this.target;
    }
    
    MethodHandleImpl.Intrinsic intrinsicName() {
      return this.intrinsicName;
    }
    
    public MethodHandle asTypeUncached(MethodType param1MethodType) {
      return this.asTypeCache = this.target.asType(param1MethodType);
    }
    
    String internalProperties() {
      return super.internalProperties() + "\n& Intrinsic=" + this.intrinsicName;
    }
    
    public MethodHandle asCollector(Class<?> param1Class, int param1Int) {
      if (this.intrinsicName == MethodHandleImpl.Intrinsic.IDENTITY) {
        MethodType methodType = type().asCollectorType(param1Class, param1Int);
        MethodHandle methodHandle = MethodHandleImpl.varargsArray(param1Class, param1Int);
        return methodHandle.asType(methodType);
      } 
      return super.asCollector(param1Class, param1Int);
    }
  }
  
  static MethodHandle makeIntrinsic(MethodHandle paramMethodHandle, Intrinsic paramIntrinsic) {
    if (paramIntrinsic == paramMethodHandle.intrinsicName())
      return paramMethodHandle; 
    return new IntrinsicMethodHandle(paramMethodHandle, paramIntrinsic);
  }
  
  static MethodHandle makeIntrinsic(MethodType paramMethodType, LambdaForm paramLambdaForm, Intrinsic paramIntrinsic) {
    return new IntrinsicMethodHandle(SimpleMethodHandle.make(paramMethodType, paramLambdaForm), paramIntrinsic);
  }
  
  private static MethodHandle findCollector(String paramString, int paramInt, Class<?> paramClass, Class<?>... paramVarArgs) {
    MethodType methodType = MethodType.genericMethodType(paramInt).changeReturnType(paramClass).insertParameterTypes(0, paramVarArgs);
    try {
      return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, paramString, methodType);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      return null;
    } 
  }
  
  private static final Object[] NO_ARGS_ARRAY = new Object[0];
  
  private static final int FILL_ARRAYS_COUNT = 11;
  
  private static final int LEFT_ARGS = 10;
  
  private static Object[] makeArray(Object... paramVarArgs) {
    return paramVarArgs;
  }
  
  private static Object[] array() {
    return NO_ARGS_ARRAY;
  }
  
  private static Object[] array(Object paramObject) {
    return makeArray(new Object[] { paramObject });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2) {
    return makeArray(new Object[] { paramObject1, paramObject2 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
  }
  
  private static MethodHandle[] makeArrays() {
    ArrayList<MethodHandle> arrayList = new ArrayList();
    while (true) {
      MethodHandle methodHandle = findCollector("array", arrayList.size(), Object[].class, new Class[0]);
      if (methodHandle == null)
        break; 
      methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY);
      arrayList.add(methodHandle);
    } 
    assert arrayList.size() == 11;
    return arrayList.<MethodHandle>toArray(new MethodHandle[MAX_ARITY + 1]);
  }
  
  private static Object[] fillNewArray(Integer paramInteger, Object[] paramArrayOfObject) {
    Object[] arrayOfObject = new Object[paramInteger.intValue()];
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject);
    return arrayOfObject;
  }
  
  private static Object[] fillNewTypedArray(Object[] paramArrayOfObject1, Integer paramInteger, Object[] paramArrayOfObject2) {
    Object[] arrayOfObject = Arrays.copyOf(paramArrayOfObject1, paramInteger.intValue());
    assert arrayOfObject.getClass() != Object[].class;
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject2);
    return arrayOfObject;
  }
  
  private static void fillWithArguments(Object[] paramArrayOfObject1, int paramInt, Object... paramVarArgs1) {
    System.arraycopy(paramVarArgs1, 0, paramArrayOfObject1, paramInt, paramVarArgs1.length);
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
    return paramArrayOfObject;
  }
  
  private static MethodHandle[] makeFillArrays() {
    ArrayList<MethodHandle> arrayList = new ArrayList();
    arrayList.add(null);
    while (true) {
      MethodHandle methodHandle = findCollector("fillArray", arrayList.size(), Object[].class, new Class[] { Integer.class, Object[].class });
      if (methodHandle == null)
        break; 
      arrayList.add(methodHandle);
    } 
    assert arrayList.size() == 11;
    return arrayList.<MethodHandle>toArray(new MethodHandle[0]);
  }
  
  private static Object copyAsPrimitiveArray(Wrapper paramWrapper, Object... paramVarArgs) {
    Object object = paramWrapper.makeArray(paramVarArgs.length);
    paramWrapper.copyArrayUnboxing(paramVarArgs, 0, object, 0, paramVarArgs.length);
    return object;
  }
  
  static MethodHandle varargsArray(int paramInt) {
    MethodHandle methodHandle = Lazy.ARRAYS[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = findCollector("array", paramInt, Object[].class, new Class[0]);
    if (methodHandle != null)
      methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY); 
    if (methodHandle != null) {
      Lazy.ARRAYS[paramInt] = methodHandle;
      return methodHandle;
    } 
    methodHandle = buildVarargsArray(Lazy.MH_fillNewArray, Lazy.MH_arrayIdentity, paramInt);
    assert assertCorrectArity(methodHandle, paramInt);
    methodHandle = makeIntrinsic(methodHandle, Intrinsic.NEW_ARRAY);
    Lazy.ARRAYS[paramInt] = methodHandle;
    return methodHandle;
  }
  
  private static boolean assertCorrectArity(MethodHandle paramMethodHandle, int paramInt) {
    assert paramMethodHandle.type().parameterCount() == paramInt : "arity != " + paramInt + ": " + paramMethodHandle;
    return true;
  }
  
  static <T> T[] identity(T[] paramArrayOfT) {
    return paramArrayOfT;
  }
  
  private static MethodHandle buildVarargsArray(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt) {
    int i = Math.min(paramInt, 10);
    int j = paramInt - i;
    MethodHandle methodHandle1 = paramMethodHandle1.bindTo(Integer.valueOf(paramInt));
    methodHandle1 = methodHandle1.asCollector(Object[].class, i);
    MethodHandle methodHandle2 = paramMethodHandle2;
    if (j > 0) {
      MethodHandle methodHandle = fillToRight(10 + j);
      if (methodHandle2 == Lazy.MH_arrayIdentity) {
        methodHandle2 = methodHandle;
      } else {
        methodHandle2 = MethodHandles.collectArguments(methodHandle2, 0, methodHandle);
      } 
    } 
    if (methodHandle2 == Lazy.MH_arrayIdentity) {
      methodHandle2 = methodHandle1;
    } else {
      methodHandle2 = MethodHandles.collectArguments(methodHandle2, 0, methodHandle1);
    } 
    return methodHandle2;
  }
  
  private static final MethodHandle[] FILL_ARRAY_TO_RIGHT = new MethodHandle[MAX_ARITY + 1];
  
  private static MethodHandle fillToRight(int paramInt) {
    MethodHandle methodHandle = FILL_ARRAY_TO_RIGHT[paramInt];
    if (methodHandle != null)
      return methodHandle; 
    methodHandle = buildFiller(paramInt);
    assert assertCorrectArity(methodHandle, paramInt - 10 + 1);
    FILL_ARRAY_TO_RIGHT[paramInt] = methodHandle;
    return methodHandle;
  }
  
  private static MethodHandle buildFiller(int paramInt) {
    if (paramInt <= 10)
      return Lazy.MH_arrayIdentity; 
    int i = paramInt % 10;
    int j = paramInt - i;
    if (i == 0) {
      j = paramInt - (i = 10);
      if (FILL_ARRAY_TO_RIGHT[j] == null)
        for (byte b = 0; b < j; b += 10) {
          if (b > 10)
            fillToRight(b); 
        }  
    } 
    if (j < 10)
      i = paramInt - (j = 10); 
    assert i > 0;
    MethodHandle methodHandle1 = fillToRight(j);
    MethodHandle methodHandle2 = Lazy.FILL_ARRAYS[i].bindTo(Integer.valueOf(j));
    assert methodHandle1.type().parameterCount() == 1 + j - 10;
    assert methodHandle2.type().parameterCount() == 1 + i;
    if (j == 10)
      return methodHandle2; 
    return MethodHandles.collectArguments(methodHandle2, 0, methodHandle1);
  }
  
  private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS = new ClassValue<MethodHandle[]>() {
      protected MethodHandle[] computeValue(Class<?> param1Class) {
        return new MethodHandle[256];
      }
    };
  
  static final int MAX_JVM_ARITY = 255;
  
  static MethodHandle varargsArray(Class<?> paramClass, int paramInt) {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual getComponentType : ()Ljava/lang/Class;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnonnull -> 36
    //   9: new java/lang/IllegalArgumentException
    //   12: dup
    //   13: new java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial <init> : ()V
    //   20: ldc 'not an array: '
    //   22: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_0
    //   26: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   29: invokevirtual toString : ()Ljava/lang/String;
    //   32: invokespecial <init> : (Ljava/lang/String;)V
    //   35: athrow
    //   36: iload_1
    //   37: bipush #126
    //   39: if_icmplt -> 116
    //   42: iload_1
    //   43: istore_3
    //   44: iload_3
    //   45: sipush #254
    //   48: if_icmpgt -> 68
    //   51: aload_2
    //   52: invokevirtual isPrimitive : ()Z
    //   55: ifeq -> 68
    //   58: iload_3
    //   59: aload_2
    //   60: invokestatic forPrimitiveType : (Ljava/lang/Class;)Lsun/invoke/util/Wrapper;
    //   63: invokevirtual stackSlots : ()I
    //   66: imul
    //   67: istore_3
    //   68: iload_3
    //   69: sipush #254
    //   72: if_icmple -> 116
    //   75: new java/lang/IllegalArgumentException
    //   78: dup
    //   79: new java/lang/StringBuilder
    //   82: dup
    //   83: invokespecial <init> : ()V
    //   86: ldc_w 'too many arguments: '
    //   89: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: aload_0
    //   93: invokevirtual getSimpleName : ()Ljava/lang/String;
    //   96: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: ldc_w ', length '
    //   102: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: iload_1
    //   106: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   109: invokevirtual toString : ()Ljava/lang/String;
    //   112: invokespecial <init> : (Ljava/lang/String;)V
    //   115: athrow
    //   116: aload_2
    //   117: ldc java/lang/Object
    //   119: if_acmpne -> 127
    //   122: iload_1
    //   123: invokestatic varargsArray : (I)Ljava/lang/invoke/MethodHandle;
    //   126: areturn
    //   127: getstatic java/lang/invoke/MethodHandleImpl.TYPED_COLLECTORS : Ljava/lang/ClassValue;
    //   130: aload_2
    //   131: invokevirtual get : (Ljava/lang/Class;)Ljava/lang/Object;
    //   134: checkcast [Ljava/lang/invoke/MethodHandle;
    //   137: astore_3
    //   138: iload_1
    //   139: aload_3
    //   140: arraylength
    //   141: if_icmpge -> 150
    //   144: aload_3
    //   145: iload_1
    //   146: aaload
    //   147: goto -> 151
    //   150: aconst_null
    //   151: astore #4
    //   153: aload #4
    //   155: ifnull -> 161
    //   158: aload #4
    //   160: areturn
    //   161: iload_1
    //   162: ifne -> 186
    //   165: aload_0
    //   166: invokevirtual getComponentType : ()Ljava/lang/Class;
    //   169: iconst_0
    //   170: invokestatic newInstance : (Ljava/lang/Class;I)Ljava/lang/Object;
    //   173: astore #5
    //   175: aload_0
    //   176: aload #5
    //   178: invokestatic constant : (Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;
    //   181: astore #4
    //   183: goto -> 261
    //   186: aload_2
    //   187: invokevirtual isPrimitive : ()Z
    //   190: ifeq -> 217
    //   193: getstatic java/lang/invoke/MethodHandleImpl$Lazy.MH_fillNewArray : Ljava/lang/invoke/MethodHandle;
    //   196: astore #5
    //   198: aload_0
    //   199: invokestatic buildArrayProducer : (Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;
    //   202: astore #6
    //   204: aload #5
    //   206: aload #6
    //   208: iload_1
    //   209: invokestatic buildVarargsArray : (Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodHandle;I)Ljava/lang/invoke/MethodHandle;
    //   212: astore #4
    //   214: goto -> 261
    //   217: aload_0
    //   218: ldc [Ljava/lang/Object;
    //   220: invokevirtual asSubclass : (Ljava/lang/Class;)Ljava/lang/Class;
    //   223: astore #5
    //   225: getstatic java/lang/invoke/MethodHandleImpl.NO_ARGS_ARRAY : [Ljava/lang/Object;
    //   228: iconst_0
    //   229: aload #5
    //   231: invokestatic copyOf : ([Ljava/lang/Object;ILjava/lang/Class;)[Ljava/lang/Object;
    //   234: astore #6
    //   236: getstatic java/lang/invoke/MethodHandleImpl$Lazy.MH_fillNewTypedArray : Ljava/lang/invoke/MethodHandle;
    //   239: aload #6
    //   241: invokevirtual bindTo : (Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;
    //   244: astore #7
    //   246: getstatic java/lang/invoke/MethodHandleImpl$Lazy.MH_arrayIdentity : Ljava/lang/invoke/MethodHandle;
    //   249: astore #8
    //   251: aload #7
    //   253: aload #8
    //   255: iload_1
    //   256: invokestatic buildVarargsArray : (Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodHandle;I)Ljava/lang/invoke/MethodHandle;
    //   259: astore #4
    //   261: aload #4
    //   263: aload_0
    //   264: iload_1
    //   265: aload_2
    //   266: invokestatic nCopies : (ILjava/lang/Object;)Ljava/util/List;
    //   269: invokestatic methodType : (Ljava/lang/Class;Ljava/util/List;)Ljava/lang/invoke/MethodType;
    //   272: invokevirtual asType : (Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;
    //   275: astore #4
    //   277: aload #4
    //   279: getstatic java/lang/invoke/MethodHandleImpl$Intrinsic.NEW_ARRAY : Ljava/lang/invoke/MethodHandleImpl$Intrinsic;
    //   282: invokestatic makeIntrinsic : (Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodHandleImpl$Intrinsic;)Ljava/lang/invoke/MethodHandle;
    //   285: astore #4
    //   287: getstatic java/lang/invoke/MethodHandleImpl.$assertionsDisabled : Z
    //   290: ifne -> 310
    //   293: aload #4
    //   295: iload_1
    //   296: invokestatic assertCorrectArity : (Ljava/lang/invoke/MethodHandle;I)Z
    //   299: ifne -> 310
    //   302: new java/lang/AssertionError
    //   305: dup
    //   306: invokespecial <init> : ()V
    //   309: athrow
    //   310: iload_1
    //   311: aload_3
    //   312: arraylength
    //   313: if_icmpge -> 321
    //   316: aload_3
    //   317: iload_1
    //   318: aload #4
    //   320: aastore
    //   321: aload #4
    //   323: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1574	-> 0
    //   #1575	-> 5
    //   #1577	-> 36
    //   #1578	-> 42
    //   #1580	-> 44
    //   #1581	-> 58
    //   #1582	-> 68
    //   #1583	-> 75
    //   #1585	-> 116
    //   #1586	-> 122
    //   #1588	-> 127
    //   #1589	-> 138
    //   #1590	-> 153
    //   #1591	-> 161
    //   #1592	-> 165
    //   #1593	-> 175
    //   #1594	-> 183
    //   #1595	-> 193
    //   #1596	-> 198
    //   #1597	-> 204
    //   #1598	-> 214
    //   #1599	-> 217
    //   #1600	-> 225
    //   #1601	-> 236
    //   #1602	-> 246
    //   #1603	-> 251
    //   #1605	-> 261
    //   #1606	-> 277
    //   #1607	-> 287
    //   #1608	-> 310
    //   #1609	-> 316
    //   #1610	-> 321
  }
  
  private static MethodHandle buildArrayProducer(Class<?> paramClass) {
    Class<?> clazz = paramClass.getComponentType();
    assert clazz.isPrimitive();
    return Lazy.MH_copyAsPrimitiveArray.bindTo(Wrapper.forPrimitiveType(clazz));
  }
  
  private static final class MethodHandleImpl {}
  
  private static class MethodHandleImpl {}
  
  static class MethodHandleImpl {}
  
  static final class MethodHandleImpl {}
}
