package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;
import sun.security.x509.X509CertImpl;

public final class UntrustedCertificates {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final String ALGORITHM_KEY = "Algorithm";
  
  private static final Properties props = new Properties();
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            File file = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");
            try (FileInputStream null = new FileInputStream(file)) {
              UntrustedCertificates.props.load(fileInputStream);
              for (Map.Entry<Object, Object> entry : UntrustedCertificates.props.entrySet())
                entry.setValue(UntrustedCertificates.stripColons(entry.getValue())); 
            } catch (IOException iOException) {
              if (UntrustedCertificates.debug != null)
                UntrustedCertificates.debug.println("Error parsing blacklisted.certs"); 
            } 
            return null;
          }
        });
  }
  
  private static final String algorithm = props.getProperty("Algorithm");
  
  private static String stripColons(Object paramObject) {
    String str = (String)paramObject;
    char[] arrayOfChar = str.toCharArray();
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      if (arrayOfChar[b2] != ':') {
        if (b2 != b1)
          arrayOfChar[b1] = arrayOfChar[b2]; 
        b1++;
      } 
    } 
    if (b1 == arrayOfChar.length)
      return str; 
    return new String(arrayOfChar, 0, b1);
  }
  
  public static boolean isUntrusted(X509Certificate paramX509Certificate) {
    String str;
    if (algorithm == null)
      return false; 
    if (paramX509Certificate instanceof X509CertImpl) {
      str = ((X509CertImpl)paramX509Certificate).getFingerprint(algorithm);
    } else {
      try {
        str = (new X509CertImpl(paramX509Certificate.getEncoded())).getFingerprint(algorithm);
      } catch (CertificateException certificateException) {
        return false;
      } 
    } 
    return props.containsKey(str);
  }
}
