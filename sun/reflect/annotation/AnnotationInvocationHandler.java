package sun.reflect.annotation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;

class AnnotationInvocationHandler implements InvocationHandler, Serializable {
  private static final long serialVersionUID = 6182022883658399397L;
  
  private final Class<? extends Annotation> type;
  
  private final Map<String, Object> memberValues;
  
  AnnotationInvocationHandler(Class<? extends Annotation> paramClass, Map<String, Object> paramMap) {
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (!paramClass.isAnnotation() || arrayOfClass.length != 1 || arrayOfClass[0] != Annotation.class)
      throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type."); 
    this.type = paramClass;
    this.memberValues = paramMap;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    if (str.equals("equals") && arrayOfClass.length == 1 && arrayOfClass[0] == Object.class)
      return equalsImpl(paramArrayOfObject[0]); 
    if (arrayOfClass.length != 0)
      throw new AssertionError("Too many parameters for an annotation method"); 
    switch (str) {
      case "toString":
        return toStringImpl();
      case "hashCode":
        return Integer.valueOf(hashCodeImpl());
      case "annotationType":
        return this.type;
    } 
    Object object = this.memberValues.get(str);
    if (object == null)
      throw new IncompleteAnnotationException(this.type, str); 
    if (object instanceof ExceptionProxy)
      throw ((ExceptionProxy)object).generateException(); 
    if (object.getClass().isArray() && Array.getLength(object) != 0)
      object = cloneArray(object); 
    return object;
  }
  
  private Object cloneArray(Object paramObject) {
    Class<?> clazz = paramObject.getClass();
    if (clazz == byte[].class) {
      byte[] arrayOfByte = (byte[])paramObject;
      return arrayOfByte.clone();
    } 
    if (clazz == char[].class) {
      char[] arrayOfChar = (char[])paramObject;
      return arrayOfChar.clone();
    } 
    if (clazz == double[].class) {
      double[] arrayOfDouble = (double[])paramObject;
      return arrayOfDouble.clone();
    } 
    if (clazz == float[].class) {
      float[] arrayOfFloat = (float[])paramObject;
      return arrayOfFloat.clone();
    } 
    if (clazz == int[].class) {
      int[] arrayOfInt = (int[])paramObject;
      return arrayOfInt.clone();
    } 
    if (clazz == long[].class) {
      long[] arrayOfLong = (long[])paramObject;
      return arrayOfLong.clone();
    } 
    if (clazz == short[].class) {
      short[] arrayOfShort = (short[])paramObject;
      return arrayOfShort.clone();
    } 
    if (clazz == boolean[].class) {
      boolean[] arrayOfBoolean = (boolean[])paramObject;
      return arrayOfBoolean.clone();
    } 
    Object[] arrayOfObject = (Object[])paramObject;
    return arrayOfObject.clone();
  }
  
  private String toStringImpl() {
    StringBuilder stringBuilder = new StringBuilder(128);
    stringBuilder.append('@');
    stringBuilder.append(this.type.getName());
    stringBuilder.append('(');
    boolean bool = true;
    for (Map.Entry<String, Object> entry : this.memberValues.entrySet()) {
      if (bool) {
        bool = false;
      } else {
        stringBuilder.append(", ");
      } 
      stringBuilder.append((String)entry.getKey());
      stringBuilder.append('=');
      stringBuilder.append(memberValueToString(entry.getValue()));
    } 
    stringBuilder.append(')');
    return stringBuilder.toString();
  }
  
  private static String memberValueToString(Object paramObject) {
    Class<?> clazz = paramObject.getClass();
    if (!clazz.isArray())
      return paramObject.toString(); 
    if (clazz == byte[].class)
      return Arrays.toString((byte[])paramObject); 
    if (clazz == char[].class)
      return Arrays.toString((char[])paramObject); 
    if (clazz == double[].class)
      return Arrays.toString((double[])paramObject); 
    if (clazz == float[].class)
      return Arrays.toString((float[])paramObject); 
    if (clazz == int[].class)
      return Arrays.toString((int[])paramObject); 
    if (clazz == long[].class)
      return Arrays.toString((long[])paramObject); 
    if (clazz == short[].class)
      return Arrays.toString((short[])paramObject); 
    if (clazz == boolean[].class)
      return Arrays.toString((boolean[])paramObject); 
    return Arrays.toString((Object[])paramObject);
  }
  
  private Boolean equalsImpl(Object paramObject) {
    if (paramObject == this)
      return Boolean.valueOf(true); 
    if (!this.type.isInstance(paramObject))
      return Boolean.valueOf(false); 
    for (Method method : getMemberMethods()) {
      String str = method.getName();
      Object object1 = this.memberValues.get(str);
      Object object2 = null;
      AnnotationInvocationHandler annotationInvocationHandler = asOneOfUs(paramObject);
      if (annotationInvocationHandler != null) {
        object2 = annotationInvocationHandler.memberValues.get(str);
      } else {
        try {
          object2 = method.invoke(paramObject, new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
          return Boolean.valueOf(false);
        } catch (IllegalAccessException illegalAccessException) {
          throw new AssertionError(illegalAccessException);
        } 
      } 
      if (!memberValueEquals(object1, object2))
        return Boolean.valueOf(false); 
    } 
    return Boolean.valueOf(true);
  }
  
  private AnnotationInvocationHandler asOneOfUs(Object paramObject) {
    if (Proxy.isProxyClass(paramObject.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      if (invocationHandler instanceof AnnotationInvocationHandler)
        return (AnnotationInvocationHandler)invocationHandler; 
    } 
    return null;
  }
  
  private static boolean memberValueEquals(Object paramObject1, Object paramObject2) {
    Class<?> clazz = paramObject1.getClass();
    if (!clazz.isArray())
      return paramObject1.equals(paramObject2); 
    if (paramObject1 instanceof Object[] && paramObject2 instanceof Object[])
      return Arrays.equals((Object[])paramObject1, (Object[])paramObject2); 
    if (paramObject2.getClass() != clazz)
      return false; 
    if (clazz == byte[].class)
      return Arrays.equals((byte[])paramObject1, (byte[])paramObject2); 
    if (clazz == char[].class)
      return Arrays.equals((char[])paramObject1, (char[])paramObject2); 
    if (clazz == double[].class)
      return Arrays.equals((double[])paramObject1, (double[])paramObject2); 
    if (clazz == float[].class)
      return Arrays.equals((float[])paramObject1, (float[])paramObject2); 
    if (clazz == int[].class)
      return Arrays.equals((int[])paramObject1, (int[])paramObject2); 
    if (clazz == long[].class)
      return Arrays.equals((long[])paramObject1, (long[])paramObject2); 
    if (clazz == short[].class)
      return Arrays.equals((short[])paramObject1, (short[])paramObject2); 
    assert clazz == boolean[].class;
    return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
  }
  
  private Method[] getMemberMethods() {
    if (this.memberMethods == null)
      this.memberMethods = AccessController.<Method[]>doPrivileged((PrivilegedAction<Method>)new Object(this)); 
    return this.memberMethods;
  }
  
  private volatile transient Method[] memberMethods = null;
  
  private void validateAnnotationMethods(Method[] paramArrayOfMethod) {
    boolean bool = true;
    for (Method method : paramArrayOfMethod) {
      if (method.getModifiers() != 1025 || method
        .isDefault() || method
        .getParameterCount() != 0 || (method
        .getExceptionTypes()).length != 0) {
        bool = false;
        break;
      } 
      Class<?> clazz = method.getReturnType();
      if (clazz.isArray()) {
        clazz = clazz.getComponentType();
        if (clazz.isArray()) {
          bool = false;
          break;
        } 
      } 
      if ((!clazz.isPrimitive() || clazz == void.class) && clazz != String.class && clazz != Class.class && 
        
        !clazz.isEnum() && 
        !clazz.isAnnotation()) {
        bool = false;
        break;
      } 
      String str = method.getName();
      if ((str.equals("toString") && clazz == String.class) || (str
        .equals("hashCode") && clazz == int.class) || (str
        .equals("annotationType") && clazz == Class.class)) {
        bool = false;
        break;
      } 
    } 
    if (bool)
      return; 
    throw new AnnotationFormatError("Malformed method on an annotation type");
  }
  
  private int hashCodeImpl() {
    int i = 0;
    for (Map.Entry<String, Object> entry : this.memberValues.entrySet())
      i += 127 * ((String)entry.getKey()).hashCode() ^ 
        memberValueHashCode(entry.getValue()); 
    return i;
  }
  
  private static int memberValueHashCode(Object paramObject) {
    Class<?> clazz = paramObject.getClass();
    if (!clazz.isArray())
      return paramObject.hashCode(); 
    if (clazz == byte[].class)
      return Arrays.hashCode((byte[])paramObject); 
    if (clazz == char[].class)
      return Arrays.hashCode((char[])paramObject); 
    if (clazz == double[].class)
      return Arrays.hashCode((double[])paramObject); 
    if (clazz == float[].class)
      return Arrays.hashCode((float[])paramObject); 
    if (clazz == int[].class)
      return Arrays.hashCode((int[])paramObject); 
    if (clazz == long[].class)
      return Arrays.hashCode((long[])paramObject); 
    if (clazz == short[].class)
      return Arrays.hashCode((short[])paramObject); 
    if (clazz == boolean[].class)
      return Arrays.hashCode((boolean[])paramObject); 
    return Arrays.hashCode((Object[])paramObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    AnnotationType annotationType = null;
    try {
      annotationType = AnnotationType.getInstance(this.type);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException("Non-annotation type in annotation serial stream");
    } 
    Map<String, Class<?>> map = annotationType.memberTypes();
    for (Map.Entry<String, Object> entry : this.memberValues.entrySet()) {
      String str = (String)entry.getKey();
      Class clazz = map.get(str);
      if (clazz != null) {
        Object object = entry.getValue();
        if (!clazz.isInstance(object) && !(object instanceof ExceptionProxy))
          entry.setValue((new AnnotationTypeMismatchExceptionProxy(object
                
                .getClass() + "[" + object + "]")).setMember(annotationType
                .members().get(str))); 
      } 
    } 
  }
}
