package sun.net;

import java.io.FileDescriptor;
import java.net.SocketOption;
import java.security.AccessController;
import jdk.net.NetworkPermission;
import jdk.net.SocketFlow;

public class ExtendedOptionsImpl {
  static {
    AccessController.doPrivileged(() -> {
          System.loadLibrary("net");
          return null;
        });
    init();
  }
  
  public static void checkSetOptionPermission(SocketOption<?> paramSocketOption) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    String str = "setOption." + paramSocketOption.name();
    securityManager.checkPermission(new NetworkPermission(str));
  }
  
  public static void checkGetOptionPermission(SocketOption<?> paramSocketOption) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    String str = "getOption." + paramSocketOption.name();
    securityManager.checkPermission(new NetworkPermission(str));
  }
  
  public static void checkValueType(Object paramObject, Class<?> paramClass) {
    if (!paramClass.isAssignableFrom(paramObject.getClass())) {
      String str = "Found: " + paramObject.getClass().toString() + " Expected: " + paramClass.toString();
      throw new IllegalArgumentException(str);
    } 
  }
  
  private static native void init();
  
  public static native void setFlowOption(FileDescriptor paramFileDescriptor, SocketFlow paramSocketFlow);
  
  public static native void getFlowOption(FileDescriptor paramFileDescriptor, SocketFlow paramSocketFlow);
  
  public static native boolean flowSupported();
}
