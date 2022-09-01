package sun.invoke.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum Wrapper {
  BOOLEAN(Boolean.class, boolean.class, 'Z', Boolean.valueOf(false), new boolean[0], Format.unsigned(1)),
  BYTE(Byte.class, byte.class, 'B', Byte.valueOf((byte)0), new byte[0], Format.signed(8)),
  SHORT(Short.class, short.class, 'S', Short.valueOf((short)0), new short[0], Format.signed(16)),
  CHAR(Character.class, char.class, 'C', Character.valueOf(false), new char[0], Format.unsigned(16)),
  INT(Integer.class, int.class, 'I', Integer.valueOf(0), new int[0], Format.signed(32)),
  LONG(Long.class, long.class, 'J', Long.valueOf(0L), new long[0], Format.signed(64)),
  FLOAT(Float.class, float.class, 'F', Float.valueOf(0.0F), new float[0], Format.floating(32)),
  DOUBLE(Double.class, double.class, 'D', Double.valueOf(0.0D), new double[0], Format.floating(64)),
  OBJECT(Object.class, Object.class, 'L', null, new Object[0], Format.other(1)),
  VOID(Void.class, void.class, 'V', null, null, Format.other(0));
  
  private final Class<?> wrapperType;
  
  private final Class<?> primitiveType;
  
  private final char basicTypeChar;
  
  private final Object zero;
  
  private final Object emptyArray;
  
  private final int format;
  
  private final String wrapperSimpleName;
  
  private final String primitiveSimpleName;
  
  private static final Wrapper[] FROM_PRIM;
  
  private static final Wrapper[] FROM_WRAP;
  
  private static final Wrapper[] FROM_CHAR;
  
  Wrapper(Class<?> paramClass1, Class<?> paramClass2, char paramChar, Object paramObject1, Object paramObject2, int paramInt1) {
    this.wrapperType = paramClass1;
    this.primitiveType = paramClass2;
    this.basicTypeChar = paramChar;
    this.zero = paramObject1;
    this.emptyArray = paramObject2;
    this.format = paramInt1;
    this.wrapperSimpleName = paramClass1.getSimpleName();
    this.primitiveSimpleName = paramClass2.getSimpleName();
  }
  
  public String detailString() {
    return this.wrapperSimpleName + 
      Arrays.<Object>asList(new Object[] { this.wrapperType, this.primitiveType, Character.valueOf(this.basicTypeChar), this.zero, "0x" + 
          Integer.toHexString(this.format) });
  }
  
  private static abstract class Format {
    static final int SLOT_SHIFT = 0;
    
    static final int SIZE_SHIFT = 2;
    
    static final int KIND_SHIFT = 12;
    
    static final int SIGNED = -4096;
    
    static final int UNSIGNED = 0;
    
    static final int FLOATING = 4096;
    
    static final int SLOT_MASK = 3;
    
    static final int SIZE_MASK = 1023;
    
    static final int INT = -3967;
    
    static final int SHORT = -4031;
    
    static final int BOOLEAN = 5;
    
    static final int CHAR = 65;
    
    static final int FLOAT = 4225;
    
    static final int VOID = 0;
    
    static final int NUM_MASK = -4;
    
    static int format(int param1Int1, int param1Int2, int param1Int3) {
      assert param1Int1 >> 12 << 12 == param1Int1;
      assert (param1Int2 & param1Int2 - 1) == 0;
      assert false;
      throw new AssertionError();
    }
    
    static int signed(int param1Int) {
      return format(-4096, param1Int, (param1Int > 32) ? 2 : 1);
    }
    
    static int unsigned(int param1Int) {
      return format(0, param1Int, (param1Int > 32) ? 2 : 1);
    }
    
    static int floating(int param1Int) {
      return format(4096, param1Int, (param1Int > 32) ? 2 : 1);
    }
    
    static int other(int param1Int) {
      return param1Int << 0;
    }
  }
  
  public int bitWidth() {
    return this.format >> 2 & 0x3FF;
  }
  
  public int stackSlots() {
    return this.format >> 0 & 0x3;
  }
  
  public boolean isSingleWord() {
    return ((this.format & 0x1) != 0);
  }
  
  public boolean isDoubleWord() {
    return ((this.format & 0x2) != 0);
  }
  
  public boolean isNumeric() {
    return ((this.format & 0xFFFFFFFC) != 0);
  }
  
  public boolean isIntegral() {
    return (isNumeric() && this.format < 4225);
  }
  
  public boolean isSubwordOrInt() {
    return (isIntegral() && isSingleWord());
  }
  
  public boolean isSigned() {
    return (this.format < 0);
  }
  
  public boolean isUnsigned() {
    return (this.format >= 5 && this.format < 4225);
  }
  
  public boolean isFloating() {
    return (this.format >= 4225);
  }
  
  public boolean isOther() {
    return ((this.format & 0xFFFFFFFC) == 0);
  }
  
  public boolean isConvertibleFrom(Wrapper paramWrapper) {
    if (this == paramWrapper)
      return true; 
    if (compareTo(paramWrapper) < 0)
      return false; 
    boolean bool = ((this.format & paramWrapper.format & 0xFFFFF000) != 0) ? true : false;
    if (!bool) {
      if (isOther())
        return true; 
      if (paramWrapper.format == 65)
        return true; 
      return false;
    } 
    assert isFloating() || isSigned();
    assert paramWrapper.isFloating() || paramWrapper.isSigned();
    return true;
  }
  
  static {
    assert checkConvertibleFrom();
    FROM_PRIM = new Wrapper[16];
    FROM_WRAP = new Wrapper[16];
    FROM_CHAR = new Wrapper[16];
    for (Wrapper wrapper : values()) {
      int i = hashPrim(wrapper.primitiveType);
      int j = hashWrap(wrapper.wrapperType);
      int k = hashChar(wrapper.basicTypeChar);
      assert FROM_PRIM[i] == null;
      assert FROM_WRAP[j] == null;
      assert FROM_CHAR[k] == null;
      FROM_PRIM[i] = wrapper;
      FROM_WRAP[j] = wrapper;
      FROM_CHAR[k] = wrapper;
    } 
  }
  
  private static boolean checkConvertibleFrom() {
    for (Wrapper wrapper : values()) {
      assert wrapper.isConvertibleFrom(wrapper);
      assert VOID.isConvertibleFrom(wrapper);
      if (wrapper != VOID) {
        assert OBJECT.isConvertibleFrom(wrapper);
        assert !wrapper.isConvertibleFrom(VOID);
      } 
      if (wrapper != CHAR) {
        assert !CHAR.isConvertibleFrom(wrapper);
        if (!wrapper.isConvertibleFrom(INT) && !$assertionsDisabled && wrapper.isConvertibleFrom(CHAR))
          throw new AssertionError(); 
      } 
      if (wrapper != BOOLEAN) {
        assert !BOOLEAN.isConvertibleFrom(wrapper);
        if (wrapper != VOID && wrapper != OBJECT && !$assertionsDisabled && wrapper.isConvertibleFrom(BOOLEAN))
          throw new AssertionError(); 
      } 
      if (wrapper.isSigned())
        for (Wrapper wrapper1 : values()) {
          if (wrapper != wrapper1)
            if (wrapper1.isFloating()) {
              assert !wrapper.isConvertibleFrom(wrapper1);
            } else if (wrapper1.isSigned()) {
              if (wrapper.compareTo(wrapper1) < 0) {
                assert !wrapper.isConvertibleFrom(wrapper1);
              } else {
                assert wrapper.isConvertibleFrom(wrapper1);
              } 
            }  
        }  
      if (wrapper.isFloating())
        for (Wrapper wrapper1 : values()) {
          if (wrapper != wrapper1)
            if (wrapper1.isSigned()) {
              assert wrapper.isConvertibleFrom(wrapper1);
            } else if (wrapper1.isFloating()) {
              if (wrapper.compareTo(wrapper1) < 0) {
                assert !wrapper.isConvertibleFrom(wrapper1);
              } else {
                assert wrapper.isConvertibleFrom(wrapper1);
              } 
            }  
        }  
    } 
    return true;
  }
  
  public Object zero() {
    return this.zero;
  }
  
  public <T> T zero(Class<T> paramClass) {
    return convert(this.zero, paramClass);
  }
  
  public static Wrapper forPrimitiveType(Class<?> paramClass) {
    Wrapper wrapper = findPrimitiveType(paramClass);
    if (wrapper != null)
      return wrapper; 
    if (paramClass.isPrimitive())
      throw new InternalError(); 
    throw newIllegalArgumentException("not primitive: " + paramClass);
  }
  
  static Wrapper findPrimitiveType(Class<?> paramClass) {
    Wrapper wrapper = FROM_PRIM[hashPrim(paramClass)];
    if (wrapper != null && wrapper.primitiveType == paramClass)
      return wrapper; 
    return null;
  }
  
  public static Wrapper forWrapperType(Class<?> paramClass) {
    Wrapper wrapper = findWrapperType(paramClass);
    if (wrapper != null)
      return wrapper; 
    for (Wrapper wrapper1 : values()) {
      if (wrapper1.wrapperType == paramClass)
        throw new InternalError(); 
    } 
    throw newIllegalArgumentException("not wrapper: " + paramClass);
  }
  
  static Wrapper findWrapperType(Class<?> paramClass) {
    Wrapper wrapper = FROM_WRAP[hashWrap(paramClass)];
    if (wrapper != null && wrapper.wrapperType == paramClass)
      return wrapper; 
    return null;
  }
  
  public static Wrapper forBasicType(char paramChar) {
    Wrapper wrapper = FROM_CHAR[hashChar(paramChar)];
    if (wrapper != null && wrapper.basicTypeChar == paramChar)
      return wrapper; 
    for (Wrapper wrapper1 : values()) {
      if (wrapper.basicTypeChar == paramChar)
        throw new InternalError(); 
    } 
    throw newIllegalArgumentException("not basic type char: " + paramChar);
  }
  
  public static Wrapper forBasicType(Class<?> paramClass) {
    if (paramClass.isPrimitive())
      return forPrimitiveType(paramClass); 
    return OBJECT;
  }
  
  private static int hashPrim(Class<?> paramClass) {
    String str = paramClass.getName();
    if (str.length() < 3)
      return 0; 
    return (str.charAt(0) + str.charAt(2)) % 16;
  }
  
  private static int hashWrap(Class<?> paramClass) {
    String str = paramClass.getName();
    assert 10 == "java.lang.".length();
    if (str.length() < 13)
      return 0; 
    return (3 * str.charAt(11) + str.charAt(12)) % 16;
  }
  
  private static int hashChar(char paramChar) {
    return (paramChar + (paramChar >> 1)) % 16;
  }
  
  public Class<?> primitiveType() {
    return this.primitiveType;
  }
  
  public Class<?> wrapperType() {
    return this.wrapperType;
  }
  
  public <T> Class<T> wrapperType(Class<T> paramClass) {
    if (paramClass == this.wrapperType)
      return paramClass; 
    if (paramClass == this.primitiveType || this.wrapperType == Object.class || paramClass
      
      .isInterface())
      return forceType(this.wrapperType, paramClass); 
    throw newClassCastException(paramClass, this.primitiveType);
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass1, Class<?> paramClass2) {
    return new ClassCastException(paramClass1 + " is not compatible with " + paramClass2);
  }
  
  public static <T> Class<T> asWrapperType(Class<T> paramClass) {
    if (paramClass.isPrimitive())
      return forPrimitiveType(paramClass).wrapperType(paramClass); 
    return paramClass;
  }
  
  public static <T> Class<T> asPrimitiveType(Class<T> paramClass) {
    Wrapper wrapper = findWrapperType(paramClass);
    if (wrapper != null)
      return forceType(wrapper.primitiveType(), paramClass); 
    return paramClass;
  }
  
  public static boolean isWrapperType(Class<?> paramClass) {
    return (findWrapperType(paramClass) != null);
  }
  
  public static boolean isPrimitiveType(Class<?> paramClass) {
    return paramClass.isPrimitive();
  }
  
  public static char basicTypeChar(Class<?> paramClass) {
    if (!paramClass.isPrimitive())
      return 'L'; 
    return forPrimitiveType(paramClass).basicTypeChar();
  }
  
  public char basicTypeChar() {
    return this.basicTypeChar;
  }
  
  public String wrapperSimpleName() {
    return this.wrapperSimpleName;
  }
  
  public String primitiveSimpleName() {
    return this.primitiveSimpleName;
  }
  
  public <T> T cast(Object paramObject, Class<T> paramClass) {
    return convert(paramObject, paramClass, true);
  }
  
  public <T> T convert(Object paramObject, Class<T> paramClass) {
    return convert(paramObject, paramClass, false);
  }
  
  private <T> T convert(Object paramObject, Class<T> paramClass, boolean paramBoolean) {
    if (this == OBJECT) {
      assert !paramClass.isPrimitive();
      if (!paramClass.isInterface())
        paramClass.cast(paramObject); 
      return (T)paramObject;
    } 
    Class<T> clazz = wrapperType(paramClass);
    if (clazz.isInstance(paramObject))
      return clazz.cast(paramObject); 
    if (!paramBoolean) {
      Class<?> clazz1 = paramObject.getClass();
      Wrapper wrapper = findWrapperType(clazz1);
      if (wrapper == null || !isConvertibleFrom(wrapper))
        throw newClassCastException(clazz, clazz1); 
    } else if (paramObject == null) {
      return (T)this.zero;
    } 
    Object object = wrap(paramObject);
    assert ((object == null) ? (Class)Void.class : (Class)object.getClass()) == clazz;
    return (T)object;
  }
  
  static <T> Class<T> forceType(Class<?> paramClass, Class<T> paramClass1) {
    boolean bool = (paramClass == paramClass1 || (paramClass.isPrimitive() && forPrimitiveType(paramClass) == findWrapperType(paramClass1)) || (paramClass1.isPrimitive() && forPrimitiveType(paramClass1) == findWrapperType(paramClass)) || (paramClass == Object.class && !paramClass1.isPrimitive())) ? true : false;
    if (!bool)
      System.out.println(paramClass + " <= " + paramClass1); 
    assert (paramClass1
      .isPrimitive() && forPrimitiveType(paramClass1) == findWrapperType(paramClass)) || (paramClass == Object.class && 
      !paramClass1.isPrimitive());
    return (Class)paramClass;
  }
  
  public Object wrap(Object paramObject) {
    switch (this.basicTypeChar) {
      case 'L':
        return paramObject;
      case 'V':
        return null;
    } 
    Number number = numberValue(paramObject);
    switch (this.basicTypeChar) {
      case 'I':
        return Integer.valueOf(number.intValue());
      case 'J':
        return Long.valueOf(number.longValue());
      case 'F':
        return Float.valueOf(number.floatValue());
      case 'D':
        return Double.valueOf(number.doubleValue());
      case 'S':
        return Short.valueOf((short)number.intValue());
      case 'B':
        return Byte.valueOf((byte)number.intValue());
      case 'C':
        return Character.valueOf((char)number.intValue());
      case 'Z':
        return Boolean.valueOf(boolValue(number.byteValue()));
    } 
    throw new InternalError("bad wrapper");
  }
  
  public Object wrap(int paramInt) {
    if (this.basicTypeChar == 'L')
      return Integer.valueOf(paramInt); 
    switch (this.basicTypeChar) {
      case 'L':
        throw newIllegalArgumentException("cannot wrap to object type");
      case 'V':
        return null;
      case 'I':
        return Integer.valueOf(paramInt);
      case 'J':
        return Long.valueOf(paramInt);
      case 'F':
        return Float.valueOf(paramInt);
      case 'D':
        return Double.valueOf(paramInt);
      case 'S':
        return Short.valueOf((short)paramInt);
      case 'B':
        return Byte.valueOf((byte)paramInt);
      case 'C':
        return Character.valueOf((char)paramInt);
      case 'Z':
        return Boolean.valueOf(boolValue((byte)paramInt));
    } 
    throw new InternalError("bad wrapper");
  }
  
  private static Number numberValue(Object paramObject) {
    if (paramObject instanceof Number)
      return (Number)paramObject; 
    if (paramObject instanceof Character)
      return Integer.valueOf(((Character)paramObject).charValue()); 
    if (paramObject instanceof Boolean)
      return Integer.valueOf(((Boolean)paramObject).booleanValue() ? 1 : 0); 
    return (Number)paramObject;
  }
  
  private static boolean boolValue(byte paramByte) {
    paramByte = (byte)(paramByte & 0x1);
    return (paramByte != 0);
  }
  
  private static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) {
    return newIllegalArgumentException(paramString + paramObject);
  }
  
  private static RuntimeException newIllegalArgumentException(String paramString) {
    return new IllegalArgumentException(paramString);
  }
  
  public Object makeArray(int paramInt) {
    return Array.newInstance(this.primitiveType, paramInt);
  }
  
  public Class<?> arrayType() {
    return this.emptyArray.getClass();
  }
  
  public void copyArrayUnboxing(Object[] paramArrayOfObject, int paramInt1, Object paramObject, int paramInt2, int paramInt3) {
    if (paramObject.getClass() != arrayType())
      arrayType().cast(paramObject); 
    for (byte b = 0; b < paramInt3; b++) {
      Object object = paramArrayOfObject[b + paramInt1];
      object = convert(object, this.primitiveType);
      Array.set(paramObject, b + paramInt2, object);
    } 
  }
  
  public void copyArrayBoxing(Object paramObject, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3) {
    if (paramObject.getClass() != arrayType())
      arrayType().cast(paramObject); 
    for (byte b = 0; b < paramInt3; b++) {
      Object object = Array.get(paramObject, b + paramInt1);
      assert object.getClass() == this.wrapperType;
      paramArrayOfObject[b + paramInt2] = object;
    } 
  }
}
