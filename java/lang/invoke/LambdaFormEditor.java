package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaFormBuffer;
import java.lang.invoke.LambdaFormEditor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.Wrapper;

class LambdaFormEditor {
  final LambdaForm lambdaForm;
  
  private static final int MIN_CACHE_ARRAY_SIZE = 4;
  
  private static final int MAX_CACHE_ARRAY_SIZE = 16;
  
  private LambdaFormEditor(LambdaForm paramLambdaForm) {
    this.lambdaForm = paramLambdaForm;
  }
  
  static LambdaFormEditor lambdaFormEditor(LambdaForm paramLambdaForm) {
    return new LambdaFormEditor(paramLambdaForm);
  }
  
  private static final class Transform extends SoftReference<LambdaForm> {
    final long packedBytes;
    
    final byte[] fullBytes;
    
    private static final boolean STRESS_TEST = false;
    
    private static final int PACKED_BYTE_SIZE = 4;
    
    private static final int PACKED_BYTE_MASK = 15;
    
    private static final int PACKED_BYTE_MAX_LENGTH = 16;
    
    private enum Kind {
      NO_KIND, BIND_ARG, ADD_ARG, DUP_ARG, SPREAD_ARGS, FILTER_ARG, FILTER_RETURN, FILTER_RETURN_TO_ZERO, COLLECT_ARGS, COLLECT_ARGS_TO_VOID, COLLECT_ARGS_TO_ARRAY, FOLD_ARGS, FOLD_ARGS_TO_VOID, PERMUTE_ARGS;
    }
    
    private static long packedBytes(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte.length > 16)
        return 0L; 
      long l = 0L;
      int i = 0;
      for (byte b = 0; b < param1ArrayOfbyte.length; b++) {
        int j = param1ArrayOfbyte[b] & 0xFF;
        i |= j;
        l |= j << b * 4;
      } 
      if (!inRange(i))
        return 0L; 
      return l;
    }
    
    private static long packedBytes(int param1Int1, int param1Int2) {
      assert inRange(param1Int1 | param1Int2);
      return (param1Int1 << 0 | param1Int2 << 4);
    }
    
    private static long packedBytes(int param1Int1, int param1Int2, int param1Int3) {
      assert inRange(param1Int1 | param1Int2 | param1Int3);
      return (param1Int1 << 0 | param1Int2 << 4 | param1Int3 << 8);
    }
    
    private static long packedBytes(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      assert inRange(param1Int1 | param1Int2 | param1Int3 | param1Int4);
      return (param1Int1 << 0 | param1Int2 << 4 | param1Int3 << 8 | param1Int4 << 12);
    }
    
    private static boolean inRange(int param1Int) {
      assert (param1Int & 0xFF) == param1Int;
      return ((param1Int & 0xFFFFFFF0) == 0);
    }
    
    private static byte[] fullBytes(int... param1VarArgs) {
      byte[] arrayOfByte = new byte[param1VarArgs.length];
      byte b = 0;
      for (int i : param1VarArgs)
        arrayOfByte[b++] = bval(i); 
      assert packedBytes(arrayOfByte) == 0L;
      return arrayOfByte;
    }
    
    private byte byteAt(int param1Int) {
      long l = this.packedBytes;
      if (l == 0L) {
        if (param1Int >= this.fullBytes.length)
          return 0; 
        return this.fullBytes[param1Int];
      } 
      assert this.fullBytes == null;
      if (param1Int > 16)
        return 0; 
      int i = param1Int * 4;
      return (byte)(int)(l >>> i & 0xFL);
    }
    
    Kind kind() {
      return Kind.values()[byteAt(0)];
    }
    
    private Transform(long param1Long, byte[] param1ArrayOfbyte, LambdaForm param1LambdaForm) {
      super(param1LambdaForm);
      this.packedBytes = param1Long;
      this.fullBytes = param1ArrayOfbyte;
    }
    
    private Transform(long param1Long) {
      this(param1Long, null, null);
      assert param1Long != 0L;
    }
    
    private Transform(byte[] param1ArrayOfbyte) {
      this(0L, param1ArrayOfbyte, null);
    }
    
    private static byte bval(int param1Int) {
      assert (param1Int & 0xFF) == param1Int;
      return (byte)param1Int;
    }
    
    private static byte bval(Kind param1Kind) {
      return bval(param1Kind.ordinal());
    }
    
    static Transform of(Kind param1Kind, int param1Int) {
      byte b = bval(param1Kind);
      if (inRange(b | param1Int))
        return new Transform(packedBytes(b, param1Int)); 
      return new Transform(fullBytes(new int[] { b, param1Int }));
    }
    
    static Transform of(Kind param1Kind, int param1Int1, int param1Int2) {
      byte b = (byte)param1Kind.ordinal();
      if (inRange(b | param1Int1 | param1Int2))
        return new Transform(packedBytes(b, param1Int1, param1Int2)); 
      return new Transform(fullBytes(new int[] { b, param1Int1, param1Int2 }));
    }
    
    static Transform of(Kind param1Kind, int param1Int1, int param1Int2, int param1Int3) {
      byte b = (byte)param1Kind.ordinal();
      if (inRange(b | param1Int1 | param1Int2 | param1Int3))
        return new Transform(packedBytes(b, param1Int1, param1Int2, param1Int3)); 
      return new Transform(fullBytes(new int[] { b, param1Int1, param1Int2, param1Int3 }));
    }
    
    private static final byte[] NO_BYTES = new byte[0];
    
    static Transform of(Kind param1Kind, int... param1VarArgs) {
      return ofBothArrays(param1Kind, param1VarArgs, NO_BYTES);
    }
    
    static Transform of(Kind param1Kind, int param1Int, byte[] param1ArrayOfbyte) {
      return ofBothArrays(param1Kind, new int[] { param1Int }, param1ArrayOfbyte);
    }
    
    static Transform of(Kind param1Kind, int param1Int1, int param1Int2, byte[] param1ArrayOfbyte) {
      return ofBothArrays(param1Kind, new int[] { param1Int1, param1Int2 }, param1ArrayOfbyte);
    }
    
    private static Transform ofBothArrays(Kind param1Kind, int[] param1ArrayOfint, byte[] param1ArrayOfbyte) {
      byte[] arrayOfByte = new byte[1 + param1ArrayOfint.length + param1ArrayOfbyte.length];
      byte b = 0;
      arrayOfByte[b++] = bval(param1Kind);
      for (int i : param1ArrayOfint)
        arrayOfByte[b++] = bval(i); 
      for (byte b1 : param1ArrayOfbyte)
        arrayOfByte[b++] = b1; 
      long l = packedBytes(arrayOfByte);
      if (l != 0L)
        return new Transform(l); 
      return new Transform(arrayOfByte);
    }
    
    Transform withResult(LambdaForm param1LambdaForm) {
      return new Transform(this.packedBytes, this.fullBytes, param1LambdaForm);
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof Transform && equals((Transform)param1Object));
    }
    
    public boolean equals(Transform param1Transform) {
      return (this.packedBytes == param1Transform.packedBytes && Arrays.equals(this.fullBytes, param1Transform.fullBytes));
    }
    
    public int hashCode() {
      if (this.packedBytes != 0L) {
        assert this.fullBytes == null;
        return Long.hashCode(this.packedBytes);
      } 
      return Arrays.hashCode(this.fullBytes);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      long l = this.packedBytes;
      if (l != 0L) {
        stringBuilder.append("(");
        while (l != 0L) {
          stringBuilder.append(l & 0xFL);
          l >>>= 4L;
          if (l != 0L)
            stringBuilder.append(","); 
        } 
        stringBuilder.append(")");
      } 
      if (this.fullBytes != null) {
        stringBuilder.append("unpacked");
        stringBuilder.append(Arrays.toString(this.fullBytes));
      } 
      LambdaForm lambdaForm = get();
      if (lambdaForm != null) {
        stringBuilder.append(" result=");
        stringBuilder.append(lambdaForm);
      } 
      return stringBuilder.toString();
    }
  }
  
  private enum Kind {
    NO_KIND, BIND_ARG, ADD_ARG, DUP_ARG, SPREAD_ARGS, FILTER_ARG, FILTER_RETURN, FILTER_RETURN_TO_ZERO, COLLECT_ARGS, COLLECT_ARGS_TO_VOID, COLLECT_ARGS_TO_ARRAY, FOLD_ARGS, FOLD_ARGS_TO_VOID, PERMUTE_ARGS;
  }
  
  private LambdaForm getInCache(Transform paramTransform) {
    assert paramTransform.get() == null;
    Object object = this.lambdaForm.transformCache;
    Transform transform = null;
    if (object instanceof ConcurrentHashMap) {
      ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)object;
      transform = (Transform)concurrentHashMap.get(paramTransform);
    } else {
      if (object == null)
        return null; 
      if (object instanceof Transform) {
        Transform transform1 = (Transform)object;
        if (transform1.equals(paramTransform))
          transform = transform1; 
      } else {
        Transform[] arrayOfTransform = (Transform[])object;
        for (byte b = 0; b < arrayOfTransform.length; b++) {
          Transform transform1 = arrayOfTransform[b];
          if (transform1 == null)
            break; 
          if (transform1.equals(paramTransform)) {
            transform = transform1;
            break;
          } 
        } 
      } 
    } 
    assert transform == null || paramTransform.equals(transform);
    return (transform != null) ? transform.get() : null;
  }
  
  private LambdaForm putInCache(Transform paramTransform, LambdaForm paramLambdaForm) {
    paramTransform = paramTransform.withResult(paramLambdaForm);
    for (byte b = 0;; b++) {
      Object object = this.lambdaForm.transformCache;
      if (object instanceof ConcurrentHashMap) {
        ConcurrentHashMap<Transform, Transform> concurrentHashMap = (ConcurrentHashMap)object;
        Transform transform = concurrentHashMap.putIfAbsent(paramTransform, paramTransform);
        if (transform == null)
          return paramLambdaForm; 
        LambdaForm lambdaForm = transform.get();
        if (lambdaForm != null)
          return lambdaForm; 
        if (concurrentHashMap.replace(paramTransform, transform, paramTransform))
          return paramLambdaForm; 
      } else {
        assert !b;
        synchronized (this.lambdaForm) {
          object = this.lambdaForm.transformCache;
          if (object instanceof ConcurrentHashMap) {
          
          } else {
            Transform[] arrayOfTransform;
            if (object == null) {
              this.lambdaForm.transformCache = paramTransform;
              return paramLambdaForm;
            } 
            if (object instanceof Transform) {
              Transform transform = (Transform)object;
              if (transform.equals(paramTransform)) {
                LambdaForm lambdaForm = transform.get();
                if (lambdaForm == null) {
                  this.lambdaForm.transformCache = paramTransform;
                  return paramLambdaForm;
                } 
                return lambdaForm;
              } 
              if (transform.get() == null) {
                this.lambdaForm.transformCache = paramTransform;
                return paramLambdaForm;
              } 
              arrayOfTransform = new Transform[4];
              arrayOfTransform[0] = transform;
              this.lambdaForm.transformCache = arrayOfTransform;
            } else {
              arrayOfTransform = (Transform[])object;
            } 
            int i = arrayOfTransform.length;
            byte b1 = -1;
            byte b2;
            for (b2 = 0; b2 < i; b2++) {
              Transform transform = arrayOfTransform[b2];
              if (transform == null)
                break; 
              if (transform.equals(paramTransform)) {
                LambdaForm lambdaForm = transform.get();
                if (lambdaForm == null) {
                  arrayOfTransform[b2] = paramTransform;
                  return paramLambdaForm;
                } 
                return lambdaForm;
              } 
              if (b1 < 0 && transform.get() == null)
                b1 = b2; 
            } 
            if (b2 >= i && b1 < 0)
              if (i < 16) {
                i = Math.min(i * 2, 16);
                arrayOfTransform = Arrays.<Transform>copyOf(arrayOfTransform, i);
                this.lambdaForm.transformCache = arrayOfTransform;
              } else {
                ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>(32);
                for (Transform transform : arrayOfTransform)
                  concurrentHashMap.put(transform, transform); 
                this.lambdaForm.transformCache = concurrentHashMap;
                b++;
              }  
            boolean bool = (b1 >= 0) ? b1 : b2;
            arrayOfTransform[bool] = paramTransform;
            return paramLambdaForm;
          } 
        } 
      } 
    } 
  }
  
  private LambdaFormBuffer buffer() {
    return new LambdaFormBuffer(this.lambdaForm);
  }
  
  private BoundMethodHandle.SpeciesData oldSpeciesData() {
    return BoundMethodHandle.speciesData(this.lambdaForm);
  }
  
  private BoundMethodHandle.SpeciesData newSpeciesData(LambdaForm.BasicType paramBasicType) {
    return oldSpeciesData().extendWith(paramBasicType);
  }
  
  BoundMethodHandle bindArgumentL(BoundMethodHandle paramBoundMethodHandle, int paramInt, Object paramObject) {
    assert paramBoundMethodHandle.speciesData() == oldSpeciesData();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.L_TYPE;
    MethodType methodType = bindArgumentType(paramBoundMethodHandle, paramInt, basicType);
    LambdaForm lambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendL(methodType, lambdaForm, paramObject);
  }
  
  BoundMethodHandle bindArgumentI(BoundMethodHandle paramBoundMethodHandle, int paramInt1, int paramInt2) {
    assert paramBoundMethodHandle.speciesData() == oldSpeciesData();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.I_TYPE;
    MethodType methodType = bindArgumentType(paramBoundMethodHandle, paramInt1, basicType);
    LambdaForm lambdaForm = bindArgumentForm(1 + paramInt1);
    return paramBoundMethodHandle.copyWithExtendI(methodType, lambdaForm, paramInt2);
  }
  
  BoundMethodHandle bindArgumentJ(BoundMethodHandle paramBoundMethodHandle, int paramInt, long paramLong) {
    assert paramBoundMethodHandle.speciesData() == oldSpeciesData();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.J_TYPE;
    MethodType methodType = bindArgumentType(paramBoundMethodHandle, paramInt, basicType);
    LambdaForm lambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendJ(methodType, lambdaForm, paramLong);
  }
  
  BoundMethodHandle bindArgumentF(BoundMethodHandle paramBoundMethodHandle, int paramInt, float paramFloat) {
    assert paramBoundMethodHandle.speciesData() == oldSpeciesData();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.F_TYPE;
    MethodType methodType = bindArgumentType(paramBoundMethodHandle, paramInt, basicType);
    LambdaForm lambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendF(methodType, lambdaForm, paramFloat);
  }
  
  BoundMethodHandle bindArgumentD(BoundMethodHandle paramBoundMethodHandle, int paramInt, double paramDouble) {
    assert paramBoundMethodHandle.speciesData() == oldSpeciesData();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.D_TYPE;
    MethodType methodType = bindArgumentType(paramBoundMethodHandle, paramInt, basicType);
    LambdaForm lambdaForm = bindArgumentForm(1 + paramInt);
    return paramBoundMethodHandle.copyWithExtendD(methodType, lambdaForm, paramDouble);
  }
  
  private MethodType bindArgumentType(BoundMethodHandle paramBoundMethodHandle, int paramInt, LambdaForm.BasicType paramBasicType) {
    assert paramBoundMethodHandle.form == this.lambdaForm;
    assert (paramBoundMethodHandle.form.names[1 + paramInt]).type == paramBasicType;
    assert LambdaForm.BasicType.basicType(paramBoundMethodHandle.type().parameterType(paramInt)) == paramBasicType;
    return paramBoundMethodHandle.type().dropParameterTypes(paramInt, paramInt + 1);
  }
  
  LambdaForm bindArgumentForm(int paramInt) {
    Transform transform = Transform.of(Transform.Kind.BIND_ARG, paramInt);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.parameterConstraint(0) == newSpeciesData(this.lambdaForm.parameterType(paramInt));
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    BoundMethodHandle.SpeciesData speciesData1 = oldSpeciesData();
    BoundMethodHandle.SpeciesData speciesData2 = newSpeciesData(this.lambdaForm.parameterType(paramInt));
    LambdaForm.Name name = this.lambdaForm.parameter(0);
    LambdaForm.NamedFunction namedFunction = speciesData2.getterFunction(speciesData1.fieldCount());
    if (paramInt != 0) {
      lambdaFormBuffer.replaceFunctions(speciesData1.getterFunctions(), speciesData2.getterFunctions(), new Object[] { name });
      LambdaForm.Name name1 = name.withConstraint(speciesData2);
      lambdaFormBuffer.renameParameter(0, name1);
      lambdaFormBuffer.replaceParameterByNewExpression(paramInt, new LambdaForm.Name(namedFunction, new Object[] { name1 }));
    } else {
      assert speciesData1 == BoundMethodHandle.SpeciesData.EMPTY;
      LambdaForm.Name name1 = (new LambdaForm.Name(LambdaForm.BasicType.L_TYPE)).withConstraint(speciesData2);
      lambdaFormBuffer.replaceParameterByNewExpression(0, new LambdaForm.Name(namedFunction, new Object[] { name1 }));
      lambdaFormBuffer.insertParameter(0, name1);
    } 
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm addArgumentForm(int paramInt, LambdaForm.BasicType paramBasicType) {
    Transform transform = Transform.of(Transform.Kind.ADD_ARG, paramInt, paramBasicType.ordinal());
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity + 1;
      assert lambdaForm.parameterType(paramInt) == paramBasicType;
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    lambdaFormBuffer.insertParameter(paramInt, new LambdaForm.Name(paramBasicType));
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm dupArgumentForm(int paramInt1, int paramInt2) {
    Transform transform = Transform.of(Transform.Kind.DUP_ARG, paramInt1, paramInt2);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity - 1;
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    assert (this.lambdaForm.parameter(paramInt1)).constraint == null;
    assert (this.lambdaForm.parameter(paramInt2)).constraint == null;
    lambdaFormBuffer.replaceParameterByCopy(paramInt2, paramInt1);
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm spreadArgumentsForm(int paramInt1, Class<?> paramClass, int paramInt2) {
    Class<?> clazz1 = paramClass.getComponentType();
    Class<?> clazz2 = paramClass;
    if (!clazz1.isPrimitive())
      clazz2 = Object[].class; 
    LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(clazz1);
    int i = basicType.ordinal();
    if (basicType.basicTypeClass() != clazz1 && 
      clazz1.isPrimitive())
      i = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(clazz1).ordinal(); 
    Transform transform = Transform.of(Transform.Kind.SPREAD_ARGS, paramInt1, i, paramInt2);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity - paramInt2 + 1;
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    assert paramInt1 <= 255;
    assert paramInt1 + paramInt2 <= this.lambdaForm.arity;
    assert paramInt1 > 0;
    LambdaForm.Name name1 = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE);
    LambdaForm.Name name2 = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_checkSpreadArgument, new Object[] { name1, Integer.valueOf(paramInt2) });
    int j = this.lambdaForm.arity();
    lambdaFormBuffer.insertExpression(j++, name2);
    MethodHandle methodHandle = MethodHandles.arrayElementGetter(clazz2);
    for (byte b = 0; b < paramInt2; b++) {
      LambdaForm.Name name = new LambdaForm.Name(methodHandle, new Object[] { name1, Integer.valueOf(b) });
      lambdaFormBuffer.insertExpression(j + b, name);
      lambdaFormBuffer.replaceParameterByCopy(paramInt1 + b, j + b);
    } 
    lambdaFormBuffer.insertParameter(paramInt1, name1);
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm collectArgumentsForm(int paramInt, MethodType paramMethodType) {
    int i = paramMethodType.parameterCount();
    boolean bool = (paramMethodType.returnType() == void.class) ? true : false;
    if (i == 1 && !bool)
      return filterArgumentForm(paramInt, LambdaForm.BasicType.basicType(paramMethodType.parameterType(0))); 
    LambdaForm.BasicType[] arrayOfBasicType = LambdaForm.BasicType.basicTypes(paramMethodType.parameterList());
    Transform.Kind kind = bool ? Transform.Kind.COLLECT_ARGS_TO_VOID : Transform.Kind.COLLECT_ARGS;
    if (bool && i == 0)
      paramInt = 1; 
    Transform transform = Transform.of(kind, paramInt, i, LambdaForm.BasicType.basicTypesOrd(arrayOfBasicType));
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity - (bool ? 0 : 1) + i;
      return lambdaForm;
    } 
    lambdaForm = makeArgumentCombinationForm(paramInt, paramMethodType, false, bool);
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm collectArgumentArrayForm(int paramInt, MethodHandle paramMethodHandle) {
    MethodType methodType = paramMethodHandle.type();
    int i = methodType.parameterCount();
    assert paramMethodHandle.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY;
    Class<?> clazz1 = methodType.returnType();
    Class<?> clazz2 = clazz1.getComponentType();
    LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(clazz2);
    int j = basicType.ordinal();
    if (basicType.basicTypeClass() != clazz2) {
      if (!clazz2.isPrimitive())
        return null; 
      j = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(clazz2).ordinal();
    } 
    assert methodType.parameterList().equals(Collections.nCopies(i, clazz2));
    Transform.Kind kind = Transform.Kind.COLLECT_ARGS_TO_ARRAY;
    Transform transform = Transform.of(kind, paramInt, i, j);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity - 1 + i;
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    assert paramInt + 1 <= this.lambdaForm.arity;
    assert paramInt > 0;
    LambdaForm.Name[] arrayOfName = new LambdaForm.Name[i];
    for (byte b = 0; b < i; b++)
      arrayOfName[b] = new LambdaForm.Name(paramInt + b, basicType); 
    LambdaForm.Name name = new LambdaForm.Name(paramMethodHandle, (Object[])arrayOfName);
    int k = this.lambdaForm.arity();
    lambdaFormBuffer.insertExpression(k, name);
    int m = paramInt + 1;
    for (LambdaForm.Name name1 : arrayOfName)
      lambdaFormBuffer.insertParameter(m++, name1); 
    assert lambdaFormBuffer.lastIndexOf(name) == k + arrayOfName.length;
    lambdaFormBuffer.replaceParameterByCopy(paramInt, k + arrayOfName.length);
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm filterArgumentForm(int paramInt, LambdaForm.BasicType paramBasicType) {
    Transform transform = Transform.of(Transform.Kind.FILTER_ARG, paramInt, paramBasicType.ordinal());
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity;
      assert lambdaForm.parameterType(paramInt) == paramBasicType;
      return lambdaForm;
    } 
    LambdaForm.BasicType basicType = this.lambdaForm.parameterType(paramInt);
    MethodType methodType = MethodType.methodType(basicType.basicTypeClass(), paramBasicType
        .basicTypeClass());
    lambdaForm = makeArgumentCombinationForm(paramInt, methodType, false, false);
    return putInCache(transform, lambdaForm);
  }
  
  private LambdaForm makeArgumentCombinationForm(int paramInt, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2) {
    LambdaForm.Name[] arrayOfName;
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    int i = paramMethodType.parameterCount();
    byte b = paramBoolean2 ? 0 : 1;
    assert paramInt <= 255;
    assert paramInt + b + (paramBoolean1 ? i : 0) <= this.lambdaForm.arity;
    assert paramInt > 0;
    assert paramMethodType == paramMethodType.basicType();
    assert paramMethodType.returnType() != void.class || paramBoolean2;
    BoundMethodHandle.SpeciesData speciesData1 = oldSpeciesData();
    BoundMethodHandle.SpeciesData speciesData2 = newSpeciesData(LambdaForm.BasicType.L_TYPE);
    LambdaForm.Name name1 = this.lambdaForm.parameter(0);
    lambdaFormBuffer.replaceFunctions(speciesData1.getterFunctions(), speciesData2.getterFunctions(), new Object[] { name1 });
    LambdaForm.Name name2 = name1.withConstraint(speciesData2);
    lambdaFormBuffer.renameParameter(0, name2);
    LambdaForm.Name name3 = new LambdaForm.Name(speciesData2.getterFunction(speciesData1.fieldCount()), new Object[] { name2 });
    Object[] arrayOfObject = new Object[1 + i];
    arrayOfObject[0] = name3;
    if (paramBoolean1) {
      arrayOfName = new LambdaForm.Name[0];
      System.arraycopy(this.lambdaForm.names, paramInt + b, arrayOfObject, 1, i);
    } else {
      arrayOfName = new LambdaForm.Name[i];
      LambdaForm.BasicType[] arrayOfBasicType = LambdaForm.BasicType.basicTypes(paramMethodType.parameterList());
      for (byte b1 = 0; b1 < arrayOfBasicType.length; b1++)
        arrayOfName[b1] = new LambdaForm.Name(paramInt + b1, arrayOfBasicType[b1]); 
      System.arraycopy(arrayOfName, 0, arrayOfObject, 1, i);
    } 
    LambdaForm.Name name4 = new LambdaForm.Name(paramMethodType, arrayOfObject);
    int j = this.lambdaForm.arity();
    lambdaFormBuffer.insertExpression(j + 0, name3);
    lambdaFormBuffer.insertExpression(j + 1, name4);
    int k = paramInt + b;
    for (LambdaForm.Name name : arrayOfName)
      lambdaFormBuffer.insertParameter(k++, name); 
    assert lambdaFormBuffer.lastIndexOf(name4) == j + 1 + arrayOfName.length;
    if (!paramBoolean2)
      lambdaFormBuffer.replaceParameterByCopy(paramInt, j + 1 + arrayOfName.length); 
    return lambdaFormBuffer.endEdit();
  }
  
  LambdaForm filterReturnForm(LambdaForm.BasicType paramBasicType, boolean paramBoolean) {
    LambdaForm.Name name;
    Transform.Kind kind = paramBoolean ? Transform.Kind.FILTER_RETURN_TO_ZERO : Transform.Kind.FILTER_RETURN;
    Transform transform = Transform.of(kind, paramBasicType.ordinal());
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity;
      assert lambdaForm.returnType() == paramBasicType;
      return lambdaForm;
    } 
    LambdaFormBuffer lambdaFormBuffer = buffer();
    lambdaFormBuffer.startEdit();
    int i = this.lambdaForm.names.length;
    if (paramBoolean) {
      if (paramBasicType == LambdaForm.BasicType.V_TYPE) {
        name = null;
      } else {
        name = new LambdaForm.Name(LambdaForm.constantZero(paramBasicType), new Object[0]);
      } 
    } else {
      BoundMethodHandle.SpeciesData speciesData1 = oldSpeciesData();
      BoundMethodHandle.SpeciesData speciesData2 = newSpeciesData(LambdaForm.BasicType.L_TYPE);
      LambdaForm.Name name1 = this.lambdaForm.parameter(0);
      lambdaFormBuffer.replaceFunctions(speciesData1.getterFunctions(), speciesData2.getterFunctions(), new Object[] { name1 });
      LambdaForm.Name name2 = name1.withConstraint(speciesData2);
      lambdaFormBuffer.renameParameter(0, name2);
      LambdaForm.Name name3 = new LambdaForm.Name(speciesData2.getterFunction(speciesData1.fieldCount()), new Object[] { name2 });
      lambdaFormBuffer.insertExpression(i++, name3);
      LambdaForm.BasicType basicType = this.lambdaForm.returnType();
      if (basicType == LambdaForm.BasicType.V_TYPE) {
        MethodType methodType = MethodType.methodType(paramBasicType.basicTypeClass());
        name = new LambdaForm.Name(methodType, new Object[] { name3 });
      } else {
        MethodType methodType = MethodType.methodType(paramBasicType.basicTypeClass(), basicType.basicTypeClass());
        name = new LambdaForm.Name(methodType, new Object[] { name3, this.lambdaForm.names[this.lambdaForm.result] });
      } 
    } 
    if (name != null)
      lambdaFormBuffer.insertExpression(i++, name); 
    lambdaFormBuffer.setResult(name);
    lambdaForm = lambdaFormBuffer.endEdit();
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm foldArgumentsForm(int paramInt, boolean paramBoolean, MethodType paramMethodType) {
    int i = paramMethodType.parameterCount();
    Transform.Kind kind = paramBoolean ? Transform.Kind.FOLD_ARGS_TO_VOID : Transform.Kind.FOLD_ARGS;
    Transform transform = Transform.of(kind, paramInt, i);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == this.lambdaForm.arity - ((kind == Transform.Kind.FOLD_ARGS) ? 1 : 0);
      return lambdaForm;
    } 
    lambdaForm = makeArgumentCombinationForm(paramInt, paramMethodType, true, paramBoolean);
    return putInCache(transform, lambdaForm);
  }
  
  LambdaForm permuteArgumentsForm(int paramInt, int[] paramArrayOfint) {
    assert paramInt == 1;
    int i = this.lambdaForm.names.length;
    int j = paramArrayOfint.length;
    int k = 0;
    boolean bool = true;
    for (byte b1 = 0; b1 < paramArrayOfint.length; b1++) {
      int i3 = paramArrayOfint[b1];
      if (i3 != b1)
        bool = false; 
      k = Math.max(k, i3 + 1);
    } 
    assert paramInt + paramArrayOfint.length == this.lambdaForm.arity;
    if (bool)
      return this.lambdaForm; 
    Transform transform = Transform.of(Transform.Kind.PERMUTE_ARGS, paramArrayOfint);
    LambdaForm lambdaForm = getInCache(transform);
    if (lambdaForm != null) {
      assert lambdaForm.arity == paramInt + k : lambdaForm;
      return lambdaForm;
    } 
    LambdaForm.BasicType[] arrayOfBasicType = new LambdaForm.BasicType[k];
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      int i3 = paramArrayOfint[b2];
      arrayOfBasicType[i3] = (this.lambdaForm.names[paramInt + b2]).type;
    } 
    assert paramInt + j == this.lambdaForm.arity;
    assert permutedTypesMatch(paramArrayOfint, arrayOfBasicType, this.lambdaForm.names, paramInt);
    b2 = 0;
    while (b2 < j && paramArrayOfint[b2] == b2)
      b2++; 
    LambdaForm.Name[] arrayOfName = new LambdaForm.Name[i - j + k];
    System.arraycopy(this.lambdaForm.names, 0, arrayOfName, 0, paramInt + b2);
    int m = i - this.lambdaForm.arity;
    System.arraycopy(this.lambdaForm.names, paramInt + j, arrayOfName, paramInt + k, m);
    int n = arrayOfName.length - m;
    int i1 = this.lambdaForm.result;
    if (i1 >= 0)
      if (i1 < paramInt + j) {
        i1 = paramArrayOfint[i1 - paramInt];
      } else {
        i1 = i1 - j + k;
      }  
    int i2;
    for (i2 = b2; i2 < j; i2++) {
      LambdaForm.Name name1 = this.lambdaForm.names[paramInt + i2];
      int i3 = paramArrayOfint[i2];
      LambdaForm.Name name2 = arrayOfName[paramInt + i3];
      if (name2 == null) {
        arrayOfName[paramInt + i3] = name2 = new LambdaForm.Name(arrayOfBasicType[i3]);
      } else {
        assert name2.type == arrayOfBasicType[i3];
      } 
      for (int i4 = n; i4 < arrayOfName.length; i4++)
        arrayOfName[i4] = arrayOfName[i4].replaceName(name1, name2); 
    } 
    for (i2 = paramInt + b2; i2 < n; i2++) {
      if (arrayOfName[i2] == null)
        arrayOfName[i2] = LambdaForm.argument(i2, arrayOfBasicType[i2 - paramInt]); 
    } 
    for (i2 = this.lambdaForm.arity; i2 < this.lambdaForm.names.length; i2++) {
      int i3 = i2 - this.lambdaForm.arity + n;
      LambdaForm.Name name1 = this.lambdaForm.names[i2];
      LambdaForm.Name name2 = arrayOfName[i3];
      if (name1 != name2)
        for (int i4 = i3 + 1; i4 < arrayOfName.length; i4++)
          arrayOfName[i4] = arrayOfName[i4].replaceName(name1, name2);  
    } 
    lambdaForm = new LambdaForm(this.lambdaForm.debugName, n, arrayOfName, i1);
    return putInCache(transform, lambdaForm);
  }
  
  static boolean permutedTypesMatch(int[] paramArrayOfint, LambdaForm.BasicType[] paramArrayOfBasicType, LambdaForm.Name[] paramArrayOfName, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      assert paramArrayOfName[paramInt + b].isParam();
      assert (paramArrayOfName[paramInt + b]).type == paramArrayOfBasicType[paramArrayOfint[b]];
    } 
    return true;
  }
}
