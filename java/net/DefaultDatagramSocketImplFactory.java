package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

class DefaultDatagramSocketImplFactory {
  static Class<?> prefixImplClass = null;
  
  private static float version;
  
  private static boolean preferIPv4Stack = false;
  
  private static boolean useDualStackImpl = false;
  
  private static String exclBindProp;
  
  private static boolean exclusiveBind = true;
  
  static {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            DefaultDatagramSocketImplFactory.version = 0.0F;
            try {
              DefaultDatagramSocketImplFactory.version = Float.parseFloat(System.getProperties()
                  .getProperty("os.version"));
              DefaultDatagramSocketImplFactory.preferIPv4Stack = Boolean.parseBoolean(
                  System.getProperties()
                  .getProperty("java.net.preferIPv4Stack"));
              DefaultDatagramSocketImplFactory.exclBindProp = System.getProperty("sun.net.useExclusiveBind");
            } catch (NumberFormatException numberFormatException) {
              assert false : numberFormatException;
            } 
            return null;
          }
        });
    if (version >= 6.0D && !preferIPv4Stack)
      useDualStackImpl = true; 
    if (exclBindProp != null) {
      exclusiveBind = (exclBindProp.length() == 0) ? true : Boolean.parseBoolean(exclBindProp);
    } else if (version < 6.0D) {
      exclusiveBind = false;
    } 
    String str = null;
    try {
      str = AccessController.<String>doPrivileged(new GetPropertyAction("impl.prefix", null));
      if (str != null)
        prefixImplClass = Class.forName("java.net." + str + "DatagramSocketImpl"); 
    } catch (Exception exception) {
      System.err.println("Can't find class: java.net." + str + "DatagramSocketImpl: check impl.prefix property");
    } 
  }
  
  static DatagramSocketImpl createDatagramSocketImpl(boolean paramBoolean) throws SocketException {
    if (prefixImplClass != null)
      try {
        return (DatagramSocketImpl)prefixImplClass.newInstance();
      } catch (Exception exception) {
        throw new SocketException("can't instantiate DatagramSocketImpl");
      }  
    if (paramBoolean)
      exclusiveBind = false; 
    if (useDualStackImpl && !paramBoolean)
      return (DatagramSocketImpl)new DualStackPlainDatagramSocketImpl(exclusiveBind); 
    return new TwoStacksPlainDatagramSocketImpl(exclusiveBind);
  }
}
