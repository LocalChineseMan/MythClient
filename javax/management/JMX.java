package javax.management;

import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class JMX {
  static final JMX proof = new JMX();
  
  public static final String DEFAULT_VALUE_FIELD = "defaultValue";
  
  public static final String IMMUTABLE_INFO_FIELD = "immutableInfo";
  
  public static final String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";
  
  public static final String LEGAL_VALUES_FIELD = "legalValues";
  
  public static final String MAX_VALUE_FIELD = "maxValue";
  
  public static final String MIN_VALUE_FIELD = "minValue";
  
  public static final String MXBEAN_FIELD = "mxbean";
  
  public static final String OPEN_TYPE_FIELD = "openType";
  
  public static final String ORIGINAL_TYPE_FIELD = "originalType";
  
  public static <T> T newMBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass) {
    return newMBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, false);
  }
  
  public static <T> T newMBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean) {
    return createProxy(paramMBeanServerConnection, paramObjectName, paramClass, paramBoolean, false);
  }
  
  public static <T> T newMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass) {
    return newMXBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, false);
  }
  
  public static <T> T newMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean) {
    return createProxy(paramMBeanServerConnection, paramObjectName, paramClass, paramBoolean, true);
  }
  
  public static boolean isMXBeanInterface(Class<?> paramClass) {
    if (!paramClass.isInterface())
      return false; 
    if (!Modifier.isPublic(paramClass.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN)
      return false; 
    MXBean mXBean = paramClass.<MXBean>getAnnotation(MXBean.class);
    if (mXBean != null)
      return mXBean.value(); 
    return paramClass.getName().endsWith("MXBean");
  }
  
  private static <T> T createProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    Class[] arrayOfClass;
    try {
      if (paramBoolean2) {
        Introspector.testComplianceMXBeanInterface(paramClass);
      } else {
        Introspector.testComplianceMBeanInterface(paramClass);
      } 
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw new IllegalArgumentException(notCompliantMBeanException);
    } 
    MBeanServerInvocationHandler mBeanServerInvocationHandler = new MBeanServerInvocationHandler(paramMBeanServerConnection, paramObjectName, paramBoolean2);
    if (paramBoolean1) {
      arrayOfClass = new Class[] { paramClass, NotificationEmitter.class };
    } else {
      arrayOfClass = new Class[] { paramClass };
    } 
    Object object = Proxy.newProxyInstance(paramClass
        .getClassLoader(), arrayOfClass, mBeanServerInvocationHandler);
    return paramClass.cast(object);
  }
}
