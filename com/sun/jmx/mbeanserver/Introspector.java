package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeData;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class Introspector {
  public static final boolean ALLOW_NONPUBLIC_MBEAN;
  
  static {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("jdk.jmx.mbeans.allowNonPublic"));
    ALLOW_NONPUBLIC_MBEAN = Boolean.parseBoolean(str);
  }
  
  public static final boolean isDynamic(Class<?> paramClass) {
    return DynamicMBean.class.isAssignableFrom(paramClass);
  }
  
  public static void testCreation(Class<?> paramClass) throws NotCompliantMBeanException {
    int i = paramClass.getModifiers();
    if (Modifier.isAbstract(i) || Modifier.isInterface(i))
      throw new NotCompliantMBeanException("MBean class must be concrete"); 
    Constructor[] arrayOfConstructor = (Constructor[])paramClass.getConstructors();
    if (arrayOfConstructor.length == 0)
      throw new NotCompliantMBeanException("MBean class must have public constructor"); 
  }
  
  public static void checkCompliance(Class<?> paramClass) throws NotCompliantMBeanException {
    if (DynamicMBean.class.isAssignableFrom(paramClass))
      return; 
    try {
      getStandardMBeanInterface(paramClass);
      return;
    } catch (NotCompliantMBeanException notCompliantMBeanException2) {
      NotCompliantMBeanException notCompliantMBeanException1 = notCompliantMBeanException2;
      try {
        getMXBeanInterface(paramClass);
        return;
      } catch (NotCompliantMBeanException notCompliantMBeanException) {
        notCompliantMBeanException2 = notCompliantMBeanException;
        String str = "MBean class " + paramClass.getName() + " does not implement " + "DynamicMBean, and neither follows the Standard MBean conventions (" + notCompliantMBeanException1.toString() + ") nor the MXBean conventions (" + notCompliantMBeanException2.toString() + ")";
        throw new NotCompliantMBeanException(str);
      } 
    } 
  }
  
  public static <T> DynamicMBean makeDynamicMBean(T paramT) throws NotCompliantMBeanException {
    if (paramT instanceof DynamicMBean)
      return (DynamicMBean)paramT; 
    Class<?> clazz = paramT.getClass();
    Class<T> clazz1 = null;
    try {
      clazz1 = (Class)Util.<Class<?>>cast(getStandardMBeanInterface(clazz));
    } catch (NotCompliantMBeanException notCompliantMBeanException) {}
    if (clazz1 != null)
      return new StandardMBeanSupport(paramT, clazz1); 
    try {
      clazz1 = Util.<Class<T>>cast(getMXBeanInterface(clazz));
    } catch (NotCompliantMBeanException notCompliantMBeanException) {}
    if (clazz1 != null)
      return new MXBeanSupport(paramT, clazz1); 
    checkCompliance(clazz);
    throw new NotCompliantMBeanException("Not compliant");
  }
  
  public static MBeanInfo testCompliance(Class<?> paramClass) throws NotCompliantMBeanException {
    if (isDynamic(paramClass))
      return null; 
    return testCompliance(paramClass, null);
  }
  
  public static void testComplianceMXBeanInterface(Class<?> paramClass) throws NotCompliantMBeanException {
    MXBeanIntrospector.getInstance().getAnalyzer(paramClass);
  }
  
  public static void testComplianceMBeanInterface(Class<?> paramClass) throws NotCompliantMBeanException {
    StandardMBeanIntrospector.getInstance().getAnalyzer(paramClass);
  }
  
  public static synchronized MBeanInfo testCompliance(Class<?> paramClass1, Class<?> paramClass2) throws NotCompliantMBeanException {
    if (paramClass2 == null)
      paramClass2 = getStandardMBeanInterface(paramClass1); 
    ReflectUtil.checkPackageAccess(paramClass2);
    StandardMBeanIntrospector standardMBeanIntrospector = StandardMBeanIntrospector.getInstance();
    return getClassMBeanInfo(standardMBeanIntrospector, paramClass1, paramClass2);
  }
  
  private static <M> MBeanInfo getClassMBeanInfo(MBeanIntrospector<M> paramMBeanIntrospector, Class<?> paramClass1, Class<?> paramClass2) throws NotCompliantMBeanException {
    PerInterface<M> perInterface = paramMBeanIntrospector.getPerInterface(paramClass2);
    return paramMBeanIntrospector.getClassMBeanInfo(paramClass1, perInterface);
  }
  
  public static Class<?> getMBeanInterface(Class<?> paramClass) {
    if (isDynamic(paramClass))
      return null; 
    try {
      return getStandardMBeanInterface(paramClass);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      return null;
    } 
  }
  
  public static <T> Class<? super T> getStandardMBeanInterface(Class<T> paramClass) throws NotCompliantMBeanException {
    Class<T> clazz = paramClass;
    Class<? super T> clazz1 = null;
    while (clazz != null) {
      clazz1 = findMBeanInterface(clazz, clazz.getName());
      if (clazz1 != null)
        break; 
      clazz = (Class)clazz.getSuperclass();
    } 
    if (clazz1 != null)
      return clazz1; 
    String str = "Class " + paramClass.getName() + " is not a JMX compliant Standard MBean";
    throw new NotCompliantMBeanException(str);
  }
  
  public static <T> Class<? super T> getMXBeanInterface(Class<T> paramClass) throws NotCompliantMBeanException {
    try {
      return MXBeanSupport.findMXBeanInterface(paramClass);
    } catch (Exception exception) {
      throw throwException(paramClass, exception);
    } 
  }
  
  private static <T> Class<? super T> findMBeanInterface(Class<T> paramClass, String paramString) {
    Class<T> clazz = paramClass;
    while (clazz != null) {
      Class[] arrayOfClass = clazz.getInterfaces();
      int i = arrayOfClass.length;
      for (byte b = 0; b < i; b++) {
        Class<?> clazz1 = Util.<Class<?>>cast(arrayOfClass[b]);
        clazz1 = implementsMBean(clazz1, paramString);
        if (clazz1 != null)
          return (Class)clazz1; 
      } 
      clazz = (Class)clazz.getSuperclass();
    } 
    return null;
  }
  
  public static Descriptor descriptorForElement(AnnotatedElement paramAnnotatedElement) {
    if (paramAnnotatedElement == null)
      return ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    Annotation[] arrayOfAnnotation = paramAnnotatedElement.getAnnotations();
    return descriptorForAnnotations(arrayOfAnnotation);
  }
  
  public static Descriptor descriptorForAnnotations(Annotation[] paramArrayOfAnnotation) {
    if (paramArrayOfAnnotation.length == 0)
      return ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Annotation annotation : paramArrayOfAnnotation) {
      Class<? extends Annotation> clazz = annotation.annotationType();
      Method[] arrayOfMethod = clazz.getMethods();
      boolean bool = false;
      for (Method method : arrayOfMethod) {
        DescriptorKey descriptorKey = method.<DescriptorKey>getAnnotation(DescriptorKey.class);
        if (descriptorKey != null) {
          String str = descriptorKey.value();
          try {
            if (!bool) {
              ReflectUtil.checkPackageAccess(clazz);
              bool = true;
            } 
            object1 = MethodUtil.invoke(method, annotation, null);
          } catch (RuntimeException runtimeException) {
            throw runtimeException;
          } catch (Exception exception) {
            throw new UndeclaredThrowableException(exception);
          } 
          Object object1 = annotationToField(object1);
          Object object2 = hashMap.put(str, object1);
          if (object2 != null && !equals(object2, object1)) {
            String str1 = "Inconsistent values for descriptor field " + str + " from annotations: " + object1 + " :: " + object2;
            throw new IllegalArgumentException(str1);
          } 
        } 
      } 
    } 
    if (hashMap.isEmpty())
      return ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    return new ImmutableDescriptor((Map)hashMap);
  }
  
  static NotCompliantMBeanException throwException(Class<?> paramClass, Throwable paramThrowable) throws NotCompliantMBeanException, SecurityException {
    if (paramThrowable instanceof SecurityException)
      throw (SecurityException)paramThrowable; 
    if (paramThrowable instanceof NotCompliantMBeanException)
      throw (NotCompliantMBeanException)paramThrowable; 
    String str1 = (paramClass == null) ? "null class" : paramClass.getName();
    String str2 = (paramThrowable == null) ? "Not compliant" : paramThrowable.getMessage();
    NotCompliantMBeanException notCompliantMBeanException = new NotCompliantMBeanException(str1 + ": " + str2);
    notCompliantMBeanException.initCause(paramThrowable);
    throw notCompliantMBeanException;
  }
  
  private static Object annotationToField(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Number || paramObject instanceof String || paramObject instanceof Character || paramObject instanceof Boolean || paramObject instanceof String[])
      return paramObject; 
    Class<?> clazz = paramObject.getClass();
    if (clazz.isArray()) {
      if (clazz.getComponentType().isPrimitive())
        return paramObject; 
      Object[] arrayOfObject = (Object[])paramObject;
      String[] arrayOfString = new String[arrayOfObject.length];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfString[b] = (String)annotationToField(arrayOfObject[b]); 
      return arrayOfString;
    } 
    if (paramObject instanceof Class)
      return ((Class)paramObject).getName(); 
    if (paramObject instanceof Enum)
      return ((Enum)paramObject).name(); 
    if (Proxy.isProxyClass(clazz))
      clazz = clazz.getInterfaces()[0]; 
    throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + clazz
        .getName());
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2) {
    return Arrays.deepEquals(new Object[] { paramObject1 }, new Object[] { paramObject2 });
  }
  
  private static <T> Class<? super T> implementsMBean(Class<T> paramClass, String paramString) {
    String str = paramString + "MBean";
    if (paramClass.getName().equals(str))
      return paramClass; 
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (arrayOfClass[b].getName().equals(str) && (
        Modifier.isPublic(arrayOfClass[b].getModifiers()) || ALLOW_NONPUBLIC_MBEAN))
        return Util.<Class<? super T>>cast(arrayOfClass[b]); 
    } 
    return null;
  }
  
  public static Object elementFromComplex(Object paramObject, String paramString) throws AttributeNotFoundException {
    try {
      if (paramObject.getClass().isArray() && paramString.equals("length"))
        return Integer.valueOf(Array.getLength(paramObject)); 
      if (paramObject instanceof CompositeData)
        return ((CompositeData)paramObject).get(paramString); 
      Class<?> clazz = paramObject.getClass();
      Method method = null;
      if (BeansHelper.isAvailable()) {
        Object object = BeansHelper.getBeanInfo(clazz);
        Object[] arrayOfObject = BeansHelper.getPropertyDescriptors(object);
        for (Object object1 : arrayOfObject) {
          if (BeansHelper.getPropertyName(object1).equals(paramString)) {
            method = BeansHelper.getReadMethod(object1);
            break;
          } 
        } 
      } else {
        method = SimpleIntrospector.getReadMethod(clazz, paramString);
      } 
      if (method != null) {
        ReflectUtil.checkPackageAccess(method.getDeclaringClass());
        return MethodUtil.invoke(method, paramObject, (Object[])new Class[0]);
      } 
      throw new AttributeNotFoundException("Could not find the getter method for the property " + paramString + " using the Java Beans introspector");
    } catch (InvocationTargetException invocationTargetException) {
      throw new IllegalArgumentException(invocationTargetException);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (Exception exception) {
      throw (AttributeNotFoundException)EnvHelp.initCause(new AttributeNotFoundException(exception
            .getMessage()), exception);
    } 
  }
  
  private static class Introspector {}
  
  private static class Introspector {}
}
