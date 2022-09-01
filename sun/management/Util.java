package sun.management;

import java.lang.management.ManagementPermission;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util {
  static RuntimeException newException(Exception paramException) {
    throw new RuntimeException(paramException);
  }
  
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  static String[] toStringArray(List<String> paramList) {
    return paramList.<String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public static ObjectName newObjectName(String paramString1, String paramString2) {
    return newObjectName(paramString1 + ",name=" + paramString2);
  }
  
  public static ObjectName newObjectName(String paramString) {
    try {
      return ObjectName.getInstance(paramString);
    } catch (MalformedObjectNameException malformedObjectNameException) {
      throw new IllegalArgumentException(malformedObjectNameException);
    } 
  }
  
  private static ManagementPermission monitorPermission = new ManagementPermission("monitor");
  
  private static ManagementPermission controlPermission = new ManagementPermission("control");
  
  static void checkAccess(ManagementPermission paramManagementPermission) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(paramManagementPermission); 
  }
  
  static void checkMonitorAccess() throws SecurityException {
    checkAccess(monitorPermission);
  }
  
  static void checkControlAccess() throws SecurityException {
    checkAccess(controlPermission);
  }
}
