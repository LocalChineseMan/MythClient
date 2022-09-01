package java.lang.invoke;

import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodTypeForm;
import java.lang.invoke.Stable;
import java.lang.ref.SoftReference;
import sun.invoke.util.Wrapper;

final class MethodTypeForm {
  final int[] argToSlotTable;
  
  final int[] slotToArgTable;
  
  final long argCounts;
  
  final long primCounts;
  
  final MethodType erasedType;
  
  final MethodType basicType;
  
  @Stable
  final SoftReference<MethodHandle>[] methodHandles;
  
  static final int MH_BASIC_INV = 0;
  
  static final int MH_NF_INV = 1;
  
  static final int MH_UNINIT_CS = 2;
  
  static final int MH_LIMIT = 3;
  
  @Stable
  final SoftReference<LambdaForm>[] lambdaForms;
  
  static final int LF_INVVIRTUAL = 0;
  
  static final int LF_INVSTATIC = 1;
  
  static final int LF_INVSPECIAL = 2;
  
  static final int LF_NEWINVSPECIAL = 3;
  
  static final int LF_INVINTERFACE = 4;
  
  static final int LF_INVSTATIC_INIT = 5;
  
  static final int LF_INTERPRET = 6;
  
  static final int LF_REBIND = 7;
  
  static final int LF_DELEGATE = 8;
  
  static final int LF_DELEGATE_BLOCK_INLINING = 9;
  
  static final int LF_EX_LINKER = 10;
  
  static final int LF_EX_INVOKER = 11;
  
  static final int LF_GEN_LINKER = 12;
  
  static final int LF_GEN_INVOKER = 13;
  
  static final int LF_CS_LINKER = 14;
  
  static final int LF_MH_LINKER = 15;
  
  static final int LF_GWC = 16;
  
  static final int LF_GWT = 17;
  
  static final int LF_LIMIT = 18;
  
  public static final int NO_CHANGE = 0;
  
  public static final int ERASE = 1;
  
  public static final int WRAP = 2;
  
  public static final int UNWRAP = 3;
  
  public static final int INTS = 4;
  
  public static final int LONGS = 5;
  
  public static final int RAW_RETURN = 6;
  
  public MethodType erasedType() {
    return this.erasedType;
  }
  
  public MethodType basicType() {
    return this.basicType;
  }
  
  private boolean assertIsBasicType() {
    assert this.erasedType == this.basicType : "erasedType: " + this.erasedType + " != basicType: " + this.basicType;
    return true;
  }
  
  public MethodHandle cachedMethodHandle(int paramInt) {
    assert assertIsBasicType();
    SoftReference<MethodHandle> softReference = this.methodHandles[paramInt];
    return (softReference != null) ? softReference.get() : null;
  }
  
  public synchronized MethodHandle setCachedMethodHandle(int paramInt, MethodHandle paramMethodHandle) {
    SoftReference<MethodHandle> softReference = this.methodHandles[paramInt];
    if (softReference != null) {
      MethodHandle methodHandle = softReference.get();
      if (methodHandle != null)
        return methodHandle; 
    } 
    this.methodHandles[paramInt] = new SoftReference<>(paramMethodHandle);
    return paramMethodHandle;
  }
  
  public LambdaForm cachedLambdaForm(int paramInt) {
    assert assertIsBasicType();
    SoftReference<LambdaForm> softReference = this.lambdaForms[paramInt];
    return (softReference != null) ? softReference.get() : null;
  }
  
  public synchronized LambdaForm setCachedLambdaForm(int paramInt, LambdaForm paramLambdaForm) {
    SoftReference<LambdaForm> softReference = this.lambdaForms[paramInt];
    if (softReference != null) {
      LambdaForm lambdaForm = softReference.get();
      if (lambdaForm != null)
        return lambdaForm; 
    } 
    this.lambdaForms[paramInt] = new SoftReference<>(paramLambdaForm);
    return paramLambdaForm;
  }
  
  protected MethodTypeForm(MethodType paramMethodType) {
    this.erasedType = paramMethodType;
    Class[] arrayOfClass1 = paramMethodType.ptypes();
    int i = arrayOfClass1.length;
    int j = i;
    boolean bool = true;
    int k = 1;
    int[] arrayOfInt1 = null, arrayOfInt2 = null;
    byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
    Class[] arrayOfClass2 = arrayOfClass1;
    Class[] arrayOfClass3 = arrayOfClass2;
    for (byte b5 = 0; b5 < arrayOfClass2.length; b5++) {
      Class<Object> clazz = arrayOfClass2[b5];
      if (clazz != Object.class) {
        b1++;
        Wrapper wrapper = Wrapper.forPrimitiveType(clazz);
        if (wrapper.isDoubleWord())
          b2++; 
        if (wrapper.isSubwordOrInt() && clazz != int.class) {
          if (arrayOfClass3 == arrayOfClass2)
            arrayOfClass3 = (Class[])arrayOfClass3.clone(); 
          arrayOfClass3[b5] = int.class;
        } 
      } 
    } 
    j += b2;
    Class<?> clazz1 = paramMethodType.returnType();
    Class<?> clazz2 = clazz1;
    if (clazz1 != Object.class) {
      b3++;
      Wrapper wrapper = Wrapper.forPrimitiveType(clazz1);
      if (wrapper.isDoubleWord())
        b4++; 
      if (wrapper.isSubwordOrInt() && clazz1 != int.class)
        clazz2 = int.class; 
      if (clazz1 == void.class) {
        bool = k = 0;
      } else {
        k += b4;
      } 
    } 
    if (arrayOfClass2 == arrayOfClass3 && clazz2 == clazz1) {
      this.basicType = paramMethodType;
    } else {
      this.basicType = MethodType.makeImpl(clazz2, arrayOfClass3, true);
      MethodTypeForm methodTypeForm = this.basicType.form();
      assert this != methodTypeForm;
      this.primCounts = methodTypeForm.primCounts;
      this.argCounts = methodTypeForm.argCounts;
      this.argToSlotTable = methodTypeForm.argToSlotTable;
      this.slotToArgTable = methodTypeForm.slotToArgTable;
      this.methodHandles = null;
      this.lambdaForms = null;
      return;
    } 
    if (b2 != 0) {
      int m = i + b2;
      arrayOfInt2 = new int[m + 1];
      arrayOfInt1 = new int[1 + i];
      arrayOfInt1[0] = m;
      for (byte b = 0; b < arrayOfClass2.length; b++) {
        Class<?> clazz = arrayOfClass2[b];
        Wrapper wrapper = Wrapper.forBasicType(clazz);
        if (wrapper.isDoubleWord())
          m--; 
        m--;
        arrayOfInt2[m] = b + 1;
        arrayOfInt1[1 + b] = m;
      } 
      assert m == 0;
    } else if (b1 != 0) {
      assert i == j;
      MethodTypeForm methodTypeForm = MethodType.genericMethodType(i).form();
      assert this != methodTypeForm;
      arrayOfInt2 = methodTypeForm.slotToArgTable;
      arrayOfInt1 = methodTypeForm.argToSlotTable;
    } else {
      int m = i;
      arrayOfInt2 = new int[m + 1];
      arrayOfInt1 = new int[1 + i];
      arrayOfInt1[0] = m;
      for (byte b = 0; b < i; b++) {
        m--;
        arrayOfInt2[m] = b + 1;
        arrayOfInt1[1 + b] = m;
      } 
    } 
    this.primCounts = pack(b4, b3, b2, b1);
    this.argCounts = pack(k, bool, j, i);
    this.argToSlotTable = arrayOfInt1;
    this.slotToArgTable = arrayOfInt2;
    if (j >= 256)
      throw MethodHandleStatics.newIllegalArgumentException("too many arguments"); 
    assert this.basicType == paramMethodType;
    this.lambdaForms = (SoftReference<LambdaForm>[])new SoftReference[18];
    this.methodHandles = (SoftReference<MethodHandle>[])new SoftReference[3];
  }
  
  private static long pack(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert ((paramInt1 | paramInt2 | paramInt3 | paramInt4) & 0xFFFF0000) == 0;
    long l1 = (paramInt1 << 16 | paramInt2), l2 = (paramInt3 << 16 | paramInt4);
    return l1 << 32L | l2;
  }
  
  private static char unpack(long paramLong, int paramInt) {
    assert paramInt <= 3;
    return (char)(int)(paramLong >> (3 - paramInt) * 16);
  }
  
  public int parameterCount() {
    return unpack(this.argCounts, 3);
  }
  
  public int parameterSlotCount() {
    return unpack(this.argCounts, 2);
  }
  
  public int returnCount() {
    return unpack(this.argCounts, 1);
  }
  
  public int returnSlotCount() {
    return unpack(this.argCounts, 0);
  }
  
  public int primitiveParameterCount() {
    return unpack(this.primCounts, 3);
  }
  
  public int longPrimitiveParameterCount() {
    return unpack(this.primCounts, 2);
  }
  
  public int primitiveReturnCount() {
    return unpack(this.primCounts, 1);
  }
  
  public int longPrimitiveReturnCount() {
    return unpack(this.primCounts, 0);
  }
  
  public boolean hasPrimitives() {
    return (this.primCounts != 0L);
  }
  
  public boolean hasNonVoidPrimitives() {
    if (this.primCounts == 0L)
      return false; 
    if (primitiveParameterCount() != 0)
      return true; 
    return (primitiveReturnCount() != 0 && returnCount() != 0);
  }
  
  public boolean hasLongPrimitives() {
    return ((longPrimitiveParameterCount() | longPrimitiveReturnCount()) != 0);
  }
  
  public int parameterToArgSlot(int paramInt) {
    return this.argToSlotTable[1 + paramInt];
  }
  
  public int argSlotToParameter(int paramInt) {
    return this.slotToArgTable[paramInt] - 1;
  }
  
  static MethodTypeForm findForm(MethodType paramMethodType) {
    MethodType methodType = canonicalize(paramMethodType, 1, 1);
    if (methodType == null)
      return new MethodTypeForm(paramMethodType); 
    return methodType.form();
  }
  
  public static MethodType canonicalize(MethodType paramMethodType, int paramInt1, int paramInt2) {
    Class[] arrayOfClass1 = paramMethodType.ptypes();
    Class[] arrayOfClass2 = canonicalizeAll(arrayOfClass1, paramInt2);
    Class<?> clazz1 = paramMethodType.returnType();
    Class<?> clazz2 = canonicalize(clazz1, paramInt1);
    if (arrayOfClass2 == null && clazz2 == null)
      return null; 
    if (clazz2 == null)
      clazz2 = clazz1; 
    if (arrayOfClass2 == null)
      arrayOfClass2 = arrayOfClass1; 
    return MethodType.makeImpl(clazz2, arrayOfClass2, true);
  }
  
  static Class<?> canonicalize(Class<?> paramClass, int paramInt) {
    if (paramClass != Object.class)
      if (!paramClass.isPrimitive()) {
        Class<?> clazz;
        switch (paramInt) {
          case 3:
            clazz = Wrapper.asPrimitiveType(paramClass);
            if (clazz != paramClass)
              return clazz; 
            break;
          case 1:
          case 6:
            return Object.class;
        } 
      } else if (paramClass == void.class) {
        switch (paramInt) {
          case 6:
            return int.class;
          case 2:
            return Void.class;
        } 
      } else {
        switch (paramInt) {
          case 2:
            return Wrapper.asWrapperType(paramClass);
          case 4:
            if (paramClass == int.class || paramClass == long.class)
              return null; 
            if (paramClass == double.class)
              return long.class; 
            return int.class;
          case 5:
            if (paramClass == long.class)
              return null; 
            return long.class;
          case 6:
            if (paramClass == int.class || paramClass == long.class || paramClass == float.class || paramClass == double.class)
              return null; 
            return int.class;
        } 
      }  
    return null;
  }
  
  static Class<?>[] canonicalizeAll(Class<?>[] paramArrayOfClass, int paramInt) {
    Class[] arrayOfClass = null;
    int i;
    byte b;
    for (i = paramArrayOfClass.length, b = 0; b < i; b++) {
      Class<?> clazz = canonicalize(paramArrayOfClass[b], paramInt);
      if (clazz == void.class)
        clazz = null; 
      if (clazz != null) {
        if (arrayOfClass == null)
          arrayOfClass = (Class[])paramArrayOfClass.clone(); 
        arrayOfClass[b] = clazz;
      } 
    } 
    return arrayOfClass;
  }
  
  public String toString() {
    return "Form" + this.erasedType;
  }
}
