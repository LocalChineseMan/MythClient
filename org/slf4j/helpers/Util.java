package org.slf4j.helpers;

public final class Util {
  private static ClassContextSecurityManager SECURITY_MANAGER;
  
  public static String safeGetSystemProperty(String key) {
    if (key == null)
      throw new IllegalArgumentException("null input"); 
    String result = null;
    try {
      result = System.getProperty(key);
    } catch (SecurityException securityException) {}
    return result;
  }
  
  public static boolean safeGetBooleanSystemProperty(String key) {
    String value = safeGetSystemProperty(key);
    if (value == null)
      return false; 
    return value.equalsIgnoreCase("true");
  }
  
  private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false;
  
  private static ClassContextSecurityManager getSecurityManager() {
    if (SECURITY_MANAGER != null)
      return SECURITY_MANAGER; 
    if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED)
      return null; 
    SECURITY_MANAGER = safeCreateSecurityManager();
    SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
    return SECURITY_MANAGER;
  }
  
  private static ClassContextSecurityManager safeCreateSecurityManager() {
    try {
      return new ClassContextSecurityManager(null);
    } catch (SecurityException sm) {
      return null;
    } 
  }
  
  public static Class<?> getCallingClass() {
    ClassContextSecurityManager securityManager = getSecurityManager();
    if (securityManager == null)
      return null; 
    Class<?>[] trace = securityManager.getClassContext();
    String thisClassName = Util.class.getName();
    int i;
    for (i = 0; i < trace.length && 
      !thisClassName.equals(trace[i].getName()); i++);
    if (i >= trace.length || i + 2 >= trace.length)
      throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen"); 
    return trace[i + 2];
  }
  
  public static final void report(String msg, Throwable t) {
    System.err.println(msg);
    System.err.println("Reported exception:");
    t.printStackTrace();
  }
  
  public static final void report(String msg) {
    System.err.println("SLF4J: " + msg);
  }
  
  private static final class Util {}
}
