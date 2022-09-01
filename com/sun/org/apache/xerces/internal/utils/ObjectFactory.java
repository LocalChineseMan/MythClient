package com.sun.org.apache.xerces.internal.utils;

public final class ObjectFactory {
  private static final String JAXP_INTERNAL = "com.sun.org.apache";
  
  private static final String STAX_INTERNAL = "com.sun.xml.internal";
  
  private static final boolean DEBUG = isDebugEnabled();
  
  private static boolean isDebugEnabled() {
    try {
      String val = SecuritySupport.getSystemProperty("xerces.debug");
      return (val != null && !"false".equals(val));
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  private static void debugPrintln(String msg) {
    if (DEBUG)
      System.err.println("XERCES: " + msg); 
  }
  
  public static ClassLoader findClassLoader() throws ConfigurationError {
    if (System.getSecurityManager() != null)
      return null; 
    ClassLoader context = SecuritySupport.getContextClassLoader();
    ClassLoader system = SecuritySupport.getSystemClassLoader();
    ClassLoader chain = system;
    while (true) {
      if (context == chain) {
        ClassLoader current = ObjectFactory.class.getClassLoader();
        chain = system;
        while (true) {
          if (current == chain)
            return system; 
          if (chain == null)
            break; 
          chain = SecuritySupport.getParentClassLoader(chain);
        } 
        return current;
      } 
      if (chain == null)
        break; 
      chain = SecuritySupport.getParentClassLoader(chain);
    } 
    return context;
  }
  
  public static Object newInstance(String className, boolean doFallback) throws ConfigurationError {
    if (System.getSecurityManager() != null)
      return newInstance(className, null, doFallback); 
    return newInstance(className, 
        findClassLoader(), doFallback);
  }
  
  public static Object newInstance(String className, ClassLoader cl, boolean doFallback) throws ConfigurationError {
    try {
      Class providerClass = findProviderClass(className, cl, doFallback);
      Object instance = providerClass.newInstance();
      if (DEBUG)
        debugPrintln("created new instance of " + providerClass + " using ClassLoader: " + cl); 
      return instance;
    } catch (ClassNotFoundException x) {
      throw new ConfigurationError("Provider " + className + " not found", x);
    } catch (Exception x) {
      throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
    } 
  }
  
  public static Class findProviderClass(String className, boolean doFallback) throws ClassNotFoundException, ConfigurationError {
    return findProviderClass(className, 
        findClassLoader(), doFallback);
  }
  
  public static Class findProviderClass(String className, ClassLoader cl, boolean doFallback) throws ClassNotFoundException, ConfigurationError {
    Class<?> providerClass;
    SecurityManager security = System.getSecurityManager();
    if (security != null)
      if (className.startsWith("com.sun.org.apache") || className
        .startsWith("com.sun.xml.internal")) {
        cl = null;
      } else {
        int lastDot = className.lastIndexOf(".");
        String packageName = className;
        if (lastDot != -1)
          packageName = className.substring(0, lastDot); 
        security.checkPackageAccess(packageName);
      }  
    if (cl == null) {
      providerClass = Class.forName(className, false, ObjectFactory.class.getClassLoader());
    } else {
      try {
        providerClass = cl.loadClass(className);
      } catch (ClassNotFoundException x) {
        if (doFallback) {
          ClassLoader current = ObjectFactory.class.getClassLoader();
          if (current == null) {
            providerClass = Class.forName(className);
          } else if (cl != current) {
            cl = current;
            providerClass = cl.loadClass(className);
          } else {
            throw x;
          } 
        } else {
          throw x;
        } 
      } 
    } 
    return providerClass;
  }
}
