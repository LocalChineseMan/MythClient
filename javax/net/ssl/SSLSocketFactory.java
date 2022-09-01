package javax.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Locale;
import javax.net.SocketFactory;
import sun.security.action.GetPropertyAction;

public abstract class SSLSocketFactory extends SocketFactory {
  private static SSLSocketFactory theFactory;
  
  private static boolean propertyChecked;
  
  static final boolean DEBUG;
  
  static {
    String str = ((String)AccessController.<String>doPrivileged(new GetPropertyAction("javax.net.debug", ""))).toLowerCase(Locale.ENGLISH);
    DEBUG = (str.contains("all") || str.contains("ssl"));
  }
  
  private static void log(String paramString) {
    if (DEBUG)
      System.out.println(paramString); 
  }
  
  public static synchronized SocketFactory getDefault() {
    if (theFactory != null)
      return theFactory; 
    if (!propertyChecked) {
      propertyChecked = true;
      String str = getSecurityProperty("ssl.SocketFactory.provider");
      if (str != null) {
        log("setting up default SSLSocketFactory");
        try {
          Class<?> clazz = null;
          try {
            clazz = Class.forName(str);
          } catch (ClassNotFoundException classNotFoundException) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            if (classLoader != null)
              clazz = classLoader.loadClass(str); 
          } 
          log("class " + str + " is loaded");
          SSLSocketFactory sSLSocketFactory = (SSLSocketFactory)clazz.newInstance();
          log("instantiated an instance of class " + str);
          theFactory = sSLSocketFactory;
          return sSLSocketFactory;
        } catch (Exception exception) {
          log("SSLSocketFactory instantiation failed: " + exception.toString());
          theFactory = new DefaultSSLSocketFactory(exception);
          return theFactory;
        } 
      } 
    } 
    try {
      return SSLContext.getDefault().getSocketFactory();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return new DefaultSSLSocketFactory(noSuchAlgorithmException);
    } 
  }
  
  static String getSecurityProperty(final String name) {
    return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            String str = Security.getProperty(name);
            if (str != null) {
              str = str.trim();
              if (str.length() == 0)
                str = null; 
            } 
            return str;
          }
        });
  }
  
  public Socket createSocket(Socket paramSocket, InputStream paramInputStream, boolean paramBoolean) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public abstract String[] getDefaultCipherSuites();
  
  public abstract String[] getSupportedCipherSuites();
  
  public abstract Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean) throws IOException;
}
