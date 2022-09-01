package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import sun.reflect.ConstantPool;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class AnnotationParser {
  public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(byte[] paramArrayOfbyte, ConstantPool paramConstantPool, Class<?> paramClass) {
    if (paramArrayOfbyte == null)
      return Collections.emptyMap(); 
    try {
      return parseAnnotations2(paramArrayOfbyte, paramConstantPool, paramClass, null);
    } catch (BufferUnderflowException bufferUnderflowException) {
      throw new AnnotationFormatError("Unexpected end of annotations.");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new AnnotationFormatError(illegalArgumentException);
    } 
  }
  
  @SafeVarargs
  static Map<Class<? extends Annotation>, Annotation> parseSelectAnnotations(byte[] paramArrayOfbyte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>... paramVarArgs) {
    if (paramArrayOfbyte == null)
      return Collections.emptyMap(); 
    try {
      return parseAnnotations2(paramArrayOfbyte, paramConstantPool, paramClass, paramVarArgs);
    } catch (BufferUnderflowException bufferUnderflowException) {
      throw new AnnotationFormatError("Unexpected end of annotations.");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new AnnotationFormatError(illegalArgumentException);
    } 
  }
  
  private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(byte[] paramArrayOfbyte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>[] paramArrayOfClass) {
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte);
    int i = byteBuffer.getShort() & 0xFFFF;
    for (byte b = 0; b < i; b++) {
      Annotation annotation = parseAnnotation2(byteBuffer, paramConstantPool, paramClass, false, paramArrayOfClass);
      if (annotation != null) {
        Class<? extends Annotation> clazz = annotation.annotationType();
        if (AnnotationType.getInstance(clazz).retention() == RetentionPolicy.RUNTIME && linkedHashMap
          .put(clazz, annotation) != null)
          throw new AnnotationFormatError("Duplicate annotation for class: " + clazz + ": " + annotation); 
      } 
    } 
    return (Map)linkedHashMap;
  }
  
  public static Annotation[][] parseParameterAnnotations(byte[] paramArrayOfbyte, ConstantPool paramConstantPool, Class<?> paramClass) {
    try {
      return parseParameterAnnotations2(paramArrayOfbyte, paramConstantPool, paramClass);
    } catch (BufferUnderflowException bufferUnderflowException) {
      throw new AnnotationFormatError("Unexpected end of parameter annotations.");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new AnnotationFormatError(illegalArgumentException);
    } 
  }
  
  private static Annotation[][] parseParameterAnnotations2(byte[] paramArrayOfbyte, ConstantPool paramConstantPool, Class<?> paramClass) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte);
    int i = byteBuffer.get() & 0xFF;
    Annotation[][] arrayOfAnnotation = new Annotation[i][];
    for (byte b = 0; b < i; b++) {
      int j = byteBuffer.getShort() & 0xFFFF;
      ArrayList<Annotation> arrayList = new ArrayList(j);
      for (byte b1 = 0; b1 < j; b1++) {
        Annotation annotation = parseAnnotation(byteBuffer, paramConstantPool, paramClass, false);
        if (annotation != null) {
          AnnotationType annotationType = AnnotationType.getInstance(annotation
              .annotationType());
          if (annotationType.retention() == RetentionPolicy.RUNTIME)
            arrayList.add(annotation); 
        } 
      } 
      arrayOfAnnotation[b] = arrayList.<Annotation>toArray(EMPTY_ANNOTATIONS_ARRAY);
    } 
    return arrayOfAnnotation;
  }
  
  private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
  
  static Annotation parseAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean) {
    return parseAnnotation2(paramByteBuffer, paramConstantPool, paramClass, paramBoolean, null);
  }
  
  private static Annotation parseAnnotation2(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean, Class<? extends Annotation>[] paramArrayOfClass) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    Class<?> clazz = null;
    String str = "[unknown]";
    try {
      try {
        str = paramConstantPool.getUTF8At(i);
        clazz = parseSig(str, paramClass);
      } catch (IllegalArgumentException illegalArgumentException) {
        clazz = paramConstantPool.getClassAt(i);
      } 
    } catch (NoClassDefFoundError noClassDefFoundError) {
      if (paramBoolean)
        throw new TypeNotPresentException(str, noClassDefFoundError); 
      skipAnnotation(paramByteBuffer, false);
      return null;
    } catch (TypeNotPresentException typeNotPresentException) {
      if (paramBoolean)
        throw typeNotPresentException; 
      skipAnnotation(paramByteBuffer, false);
      return null;
    } 
    if (paramArrayOfClass != null && !contains((Object[])paramArrayOfClass, clazz)) {
      skipAnnotation(paramByteBuffer, false);
      return null;
    } 
    AnnotationType annotationType = null;
    try {
      annotationType = AnnotationType.getInstance((Class)clazz);
    } catch (IllegalArgumentException illegalArgumentException) {
      skipAnnotation(paramByteBuffer, false);
      return null;
    } 
    Map<String, Class<?>> map = annotationType.memberTypes();
    LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(annotationType.memberDefaults());
    int j = paramByteBuffer.getShort() & 0xFFFF;
    for (byte b = 0; b < j; b++) {
      int k = paramByteBuffer.getShort() & 0xFFFF;
      String str1 = paramConstantPool.getUTF8At(k);
      Class<?> clazz1 = map.get(str1);
      if (clazz1 == null) {
        skipMemberValue(paramByteBuffer);
      } else {
        Object object = parseMemberValue(clazz1, paramByteBuffer, paramConstantPool, paramClass);
        if (object instanceof AnnotationTypeMismatchExceptionProxy)
          ((AnnotationTypeMismatchExceptionProxy)object)
            .setMember(annotationType.members().get(str1)); 
        linkedHashMap.put(str1, object);
      } 
    } 
    return annotationForMap((Class)clazz, linkedHashMap);
  }
  
  public static Annotation annotationForMap(final Class<? extends Annotation> type, final Map<String, Object> memberValues) {
    return AccessController.<Annotation>doPrivileged(new PrivilegedAction<Annotation>() {
          public Annotation run() {
            return (Annotation)Proxy.newProxyInstance(type
                .getClassLoader(), new Class[] { this.val$type }, new AnnotationInvocationHandler(type, memberValues));
          }
        });
  }
  
  public static Object parseMemberValue(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2) {
    Object object = null;
    byte b = paramByteBuffer.get();
    switch (b) {
      case 101:
        return parseEnumValue((Class)paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
      case 99:
        object = parseClassValue(paramByteBuffer, paramConstantPool, paramClass2);
        break;
      case 64:
        object = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass2, true);
        break;
      case 91:
        return parseArray(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
      default:
        object = parseConst(b, paramByteBuffer, paramConstantPool);
        break;
    } 
    if (!(object instanceof ExceptionProxy) && 
      !paramClass1.isInstance(object))
      object = new AnnotationTypeMismatchExceptionProxy(object.getClass() + "[" + object + "]"); 
    return object;
  }
  
  private static Object parseConst(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    switch (paramInt) {
      case 66:
        return Byte.valueOf((byte)paramConstantPool.getIntAt(i));
      case 67:
        return Character.valueOf((char)paramConstantPool.getIntAt(i));
      case 68:
        return Double.valueOf(paramConstantPool.getDoubleAt(i));
      case 70:
        return Float.valueOf(paramConstantPool.getFloatAt(i));
      case 73:
        return Integer.valueOf(paramConstantPool.getIntAt(i));
      case 74:
        return Long.valueOf(paramConstantPool.getLongAt(i));
      case 83:
        return Short.valueOf((short)paramConstantPool.getIntAt(i));
      case 90:
        return Boolean.valueOf((paramConstantPool.getIntAt(i) != 0));
      case 115:
        return paramConstantPool.getUTF8At(i);
    } 
    throw new AnnotationFormatError("Invalid member-value tag in annotation: " + paramInt);
  }
  
  private static Object parseClassValue(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    try {
      String str = paramConstantPool.getUTF8At(i);
      return parseSig(str, paramClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      return paramConstantPool.getClassAt(i);
    } catch (NoClassDefFoundError noClassDefFoundError) {
      return new TypeNotPresentExceptionProxy("[unknown]", noClassDefFoundError);
    } catch (TypeNotPresentException typeNotPresentException) {
      return new TypeNotPresentExceptionProxy(typeNotPresentException.typeName(), typeNotPresentException.getCause());
    } 
  }
  
  private static Class<?> parseSig(String paramString, Class<?> paramClass) {
    if (paramString.equals("V"))
      return void.class; 
    SignatureParser signatureParser = SignatureParser.make();
    TypeSignature typeSignature = signatureParser.parseTypeSig(paramString);
    CoreReflectionFactory coreReflectionFactory = CoreReflectionFactory.make(paramClass, ClassScope.make(paramClass));
    Reifier reifier = Reifier.make(coreReflectionFactory);
    typeSignature.accept(reifier);
    Type type = reifier.getResult();
    return toClass(type);
  }
  
  static Class<?> toClass(Type paramType) {
    if (paramType instanceof GenericArrayType)
      return Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass(); 
    return (Class)paramType;
  }
  
  private static Object parseEnumValue(Class<? extends Enum> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    String str1 = paramConstantPool.getUTF8At(i);
    int j = paramByteBuffer.getShort() & 0xFFFF;
    String str2 = paramConstantPool.getUTF8At(j);
    if (!str1.endsWith(";")) {
      if (!paramClass.getName().equals(str1))
        return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2); 
    } else if (paramClass != parseSig(str1, paramClass1)) {
      return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
    } 
    try {
      return Enum.valueOf(paramClass, str2);
    } catch (IllegalArgumentException illegalArgumentException) {
      return new EnumConstantNotPresentExceptionProxy((Class)paramClass, str2);
    } 
  }
  
  private static Object parseArray(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    Class<?> clazz = paramClass1.getComponentType();
    if (clazz == byte.class)
      return parseByteArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == char.class)
      return parseCharArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == double.class)
      return parseDoubleArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == float.class)
      return parseFloatArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == int.class)
      return parseIntArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == long.class)
      return parseLongArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == short.class)
      return parseShortArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == boolean.class)
      return parseBooleanArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == String.class)
      return parseStringArray(i, paramByteBuffer, paramConstantPool); 
    if (clazz == Class.class)
      return parseClassArray(i, paramByteBuffer, paramConstantPool, paramClass2); 
    if (clazz.isEnum())
      return parseEnumArray(i, (Class)clazz, paramByteBuffer, paramConstantPool, paramClass2); 
    assert clazz.isAnnotation();
    return parseAnnotationArray(i, (Class)clazz, paramByteBuffer, paramConstantPool, paramClass2);
  }
  
  private static Object parseByteArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    byte[] arrayOfByte = new byte[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 66) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfByte[b1] = (byte)paramConstantPool.getIntAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfByte;
  }
  
  private static Object parseCharArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    char[] arrayOfChar = new char[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 67) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfChar[b1] = (char)paramConstantPool.getIntAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfChar;
  }
  
  private static Object parseDoubleArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    double[] arrayOfDouble = new double[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 68) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfDouble[b1] = paramConstantPool.getDoubleAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfDouble;
  }
  
  private static Object parseFloatArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    float[] arrayOfFloat = new float[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 70) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfFloat[b1] = paramConstantPool.getFloatAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfFloat;
  }
  
  private static Object parseIntArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    int[] arrayOfInt = new int[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 73) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfInt[b1] = paramConstantPool.getIntAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfInt;
  }
  
  private static Object parseLongArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    long[] arrayOfLong = new long[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 74) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfLong[b1] = paramConstantPool.getLongAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfLong;
  }
  
  private static Object parseShortArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    short[] arrayOfShort = new short[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 83) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfShort[b1] = (short)paramConstantPool.getIntAt(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfShort;
  }
  
  private static Object parseBooleanArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    boolean[] arrayOfBoolean = new boolean[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 90) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfBoolean[b1] = (paramConstantPool.getIntAt(i) != 0);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfBoolean;
  }
  
  private static Object parseStringArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool) {
    String[] arrayOfString = new String[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 115) {
        int i = paramByteBuffer.getShort() & 0xFFFF;
        arrayOfString[b1] = paramConstantPool.getUTF8At(i);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfString;
  }
  
  private static Object parseClassArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass) {
    Class[] arrayOfClass = new Class[paramInt];
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 99) {
        arrayOfClass[b1] = (Class)parseClassValue(paramByteBuffer, paramConstantPool, paramClass);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfClass;
  }
  
  private static Object parseEnumArray(int paramInt, Class<? extends Enum<?>> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1) {
    Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 101) {
        arrayOfObject[b1] = parseEnumValue((Class)paramClass, paramByteBuffer, paramConstantPool, paramClass1);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfObject;
  }
  
  private static Object parseAnnotationArray(int paramInt, Class<? extends Annotation> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1) {
    Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
    boolean bool = false;
    byte b = 0;
    for (byte b1 = 0; b1 < paramInt; b1++) {
      b = paramByteBuffer.get();
      if (b == 64) {
        arrayOfObject[b1] = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass1, true);
      } else {
        skipMemberValue(b, paramByteBuffer);
        bool = true;
      } 
    } 
    return bool ? exceptionProxy(b) : arrayOfObject;
  }
  
  private static ExceptionProxy exceptionProxy(int paramInt) {
    return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + paramInt);
  }
  
  private static void skipAnnotation(ByteBuffer paramByteBuffer, boolean paramBoolean) {
    if (paramBoolean)
      paramByteBuffer.getShort(); 
    int i = paramByteBuffer.getShort() & 0xFFFF;
    for (byte b = 0; b < i; b++) {
      paramByteBuffer.getShort();
      skipMemberValue(paramByteBuffer);
    } 
  }
  
  private static void skipMemberValue(ByteBuffer paramByteBuffer) {
    byte b = paramByteBuffer.get();
    skipMemberValue(b, paramByteBuffer);
  }
  
  private static void skipMemberValue(int paramInt, ByteBuffer paramByteBuffer) {
    switch (paramInt) {
      case 101:
        paramByteBuffer.getInt();
        return;
      case 64:
        skipAnnotation(paramByteBuffer, true);
        return;
      case 91:
        skipArray(paramByteBuffer);
        return;
    } 
    paramByteBuffer.getShort();
  }
  
  private static void skipArray(ByteBuffer paramByteBuffer) {
    int i = paramByteBuffer.getShort() & 0xFFFF;
    for (byte b = 0; b < i; b++)
      skipMemberValue(paramByteBuffer); 
  }
  
  private static boolean contains(Object[] paramArrayOfObject, Object paramObject) {
    for (Object object : paramArrayOfObject) {
      if (object == paramObject)
        return true; 
    } 
    return false;
  }
  
  private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
  
  public static Annotation[] toArray(Map<Class<? extends Annotation>, Annotation> paramMap) {
    return (Annotation[])paramMap.values().toArray((Object[])EMPTY_ANNOTATION_ARRAY);
  }
  
  static Annotation[] getEmptyAnnotationArray() {
    return EMPTY_ANNOTATION_ARRAY;
  }
}
