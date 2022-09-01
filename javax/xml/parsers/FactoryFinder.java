package javax.xml.parsers;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder {
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  
  private static boolean debug = false;
  
  private static final Properties cacheProps = new Properties();
  
  static volatile boolean firstTime = true;
  
  private static final SecuritySupport ss = new SecuritySupport();
  
  static {
    try {
      String val = ss.getSystemProperty("jaxp.debug");
      debug = (val != null && !"false".equals(val));
    } catch (SecurityException se) {
      debug = false;
    } 
  }
  
  private static void dPrint(String msg) {
    if (debug)
      System.err.println("JAXP: " + msg); 
  }
  
  private static Class<?> getProviderClass(String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws ClassNotFoundException {
    try {
      if (cl == null) {
        if (useBSClsLoader)
          return Class.forName(className, false, FactoryFinder.class.getClassLoader()); 
        cl = ss.getContextClassLoader();
        if (cl == null)
          throw new ClassNotFoundException(); 
        return Class.forName(className, false, cl);
      } 
      return Class.forName(className, false, cl);
    } catch (ClassNotFoundException e1) {
      if (doFallback)
        return Class.forName(className, false, FactoryFinder.class.getClassLoader()); 
      throw e1;
    } 
  }
  
  static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback) throws FactoryConfigurationError {
    return newInstance(type, className, cl, doFallback, false);
  }
  
  static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws FactoryConfigurationError {
    assert type != null;
    if (System.getSecurityManager() != null && 
      className != null && className.startsWith("com.sun.org.apache.xerces.internal")) {
      cl = null;
      useBSClsLoader = true;
    } 
    try {
      Class<?> providerClass = getProviderClass(className, cl, doFallback, useBSClsLoader);
      if (!type.isAssignableFrom(providerClass))
        throw new ClassCastException(className + " cannot be cast to " + type.getName()); 
      Object instance = providerClass.newInstance();
      if (debug)
        dPrint("created new instance of " + providerClass + " using ClassLoader: " + cl); 
      return type.cast(instance);
    } catch (ClassNotFoundException x) {
      throw new FactoryConfigurationError(x, "Provider " + className + " not found");
    } catch (Exception x) {
      throw new FactoryConfigurationError(x, "Provider " + className + " could not be instantiated: " + x);
    } 
  }
  
  static <T> T find(Class<T> type, String fallbackClassName) throws FactoryConfigurationError {
    String factoryId = type.getName();
    dPrint("find factoryId =" + factoryId);
    try {
      String systemProp = ss.getSystemProperty(factoryId);
      if (systemProp != null) {
        dPrint("found system property, value=" + systemProp);
        return newInstance(type, systemProp, null, true);
      } 
    } catch (SecurityException se) {
      if (debug)
        se.printStackTrace(); 
    } 
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            String configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File f = new File(configFile);
            firstTime = false;
            if (ss.doesFileExist(f)) {
              dPrint("Read properties file " + f);
              cacheProps.load(ss.getFileInputStream(f));
            } 
          } 
        }  
      String factoryClassName = cacheProps.getProperty(factoryId);
      if (factoryClassName != null) {
        dPrint("found in $java.home/jaxp.properties, value=" + factoryClassName);
        return newInstance(type, factoryClassName, null, true);
      } 
    } catch (Exception ex) {
      if (debug)
        ex.printStackTrace(); 
    } 
    T provider = findServiceProvider(type);
    if (provider != null)
      return provider; 
    if (fallbackClassName == null)
      throw new FactoryConfigurationError("Provider for " + factoryId + " cannot be found"); 
    dPrint("loaded from fallback value: " + fallbackClassName);
    return newInstance(type, fallbackClassName, null, true);
  }
  
  private static <T> T findServiceProvider(final Class<T> type) {
    try {
      return AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
              ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
              Iterator<T> iterator = serviceLoader.iterator();
              if (iterator.hasNext())
                return iterator.next(); 
              return null;
            }
          });
    } catch (ServiceConfigurationError e) {
      RuntimeException x = new RuntimeException("Provider for " + type + " cannot be created", e);
      FactoryConfigurationError error = new FactoryConfigurationError(x, x.getMessage());
      throw error;
    } 
  }
}
