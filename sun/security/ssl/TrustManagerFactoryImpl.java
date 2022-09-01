package sun.security.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXBuilderParameters;
import java.util.HashMap;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

abstract class TrustManagerFactoryImpl extends TrustManagerFactorySpi {
  private static final Debug debug = Debug.getInstance("ssl");
  
  private X509TrustManager trustManager = null;
  
  private boolean isInitialized = false;
  
  protected void engineInit(KeyStore paramKeyStore) throws KeyStoreException {
    if (paramKeyStore == null)
      try {
        paramKeyStore = getCacertsKeyStore("trustmanager");
      } catch (SecurityException securityException) {
        if (debug != null && Debug.isOn("trustmanager"))
          System.out.println("SunX509: skip default keystore: " + securityException); 
      } catch (Error error) {
        if (debug != null && Debug.isOn("trustmanager"))
          System.out.println("SunX509: skip default keystore: " + error); 
        throw error;
      } catch (RuntimeException runtimeException) {
        if (debug != null && Debug.isOn("trustmanager"))
          System.out.println("SunX509: skip default keystore: " + runtimeException); 
        throw runtimeException;
      } catch (Exception exception) {
        if (debug != null && Debug.isOn("trustmanager"))
          System.out.println("SunX509: skip default keystore: " + exception); 
        throw new KeyStoreException("problem accessing trust store" + exception);
      }  
    this.trustManager = getInstance(paramKeyStore);
    this.isInitialized = true;
  }
  
  abstract X509TrustManager getInstance(KeyStore paramKeyStore) throws KeyStoreException;
  
  abstract X509TrustManager getInstance(ManagerFactoryParameters paramManagerFactoryParameters) throws InvalidAlgorithmParameterException;
  
  protected void engineInit(ManagerFactoryParameters paramManagerFactoryParameters) throws InvalidAlgorithmParameterException {
    this.trustManager = getInstance(paramManagerFactoryParameters);
    this.isInitialized = true;
  }
  
  protected TrustManager[] engineGetTrustManagers() {
    if (!this.isInitialized)
      throw new IllegalStateException("TrustManagerFactoryImpl is not initialized"); 
    return new TrustManager[] { this.trustManager };
  }
  
  private static FileInputStream getFileInputStream(final File file) throws Exception {
    return AccessController.<FileInputStream>doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
          public FileInputStream run() throws Exception {
            try {
              if (file.exists())
                return new FileInputStream(file); 
              return null;
            } catch (FileNotFoundException fileNotFoundException) {
              return null;
            } 
          }
        });
  }
  
  static KeyStore getCacertsKeyStore(String paramString) throws Exception {
    String str1 = null;
    File file = null;
    FileInputStream fileInputStream = null;
    final HashMap<Object, Object> props = new HashMap<>();
    String str2 = File.separator;
    KeyStore keyStore = null;
    AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
          public Void run() throws Exception {
            props.put("trustStore", System.getProperty("javax.net.ssl.trustStore"));
            props.put("javaHome", System.getProperty("java.home"));
            props.put("trustStoreType", System.getProperty("javax.net.ssl.trustStoreType", 
                  
                  KeyStore.getDefaultType()));
            props.put("trustStoreProvider", System.getProperty("javax.net.ssl.trustStoreProvider", ""));
            props.put("trustStorePasswd", System.getProperty("javax.net.ssl.trustStorePassword", ""));
            return null;
          }
        });
    try {
      str1 = (String)hashMap.get("trustStore");
      if (!"NONE".equals(str1)) {
        if (str1 != null) {
          file = new File(str1);
          fileInputStream = getFileInputStream(file);
        } else {
          String str = (String)hashMap.get("javaHome");
          file = new File(str + str2 + "lib" + str2 + "security" + str2 + "jssecacerts");
          if ((fileInputStream = getFileInputStream(file)) == null) {
            file = new File(str + str2 + "lib" + str2 + "security" + str2 + "cacerts");
            fileInputStream = getFileInputStream(file);
          } 
        } 
        if (fileInputStream != null) {
          str1 = file.getPath();
        } else {
          str1 = "No File Available, using empty keystore.";
        } 
      } 
      String str3 = (String)hashMap.get("trustStoreType");
      String str4 = (String)hashMap.get("trustStoreProvider");
      if (debug != null && Debug.isOn(paramString)) {
        System.out.println("trustStore is: " + str1);
        System.out.println("trustStore type is : " + str3);
        System.out.println("trustStore provider is : " + str4);
      } 
      if (str3.length() != 0) {
        if (debug != null && Debug.isOn(paramString))
          System.out.println("init truststore"); 
        if (str4.length() == 0) {
          keyStore = KeyStore.getInstance(str3);
        } else {
          keyStore = KeyStore.getInstance(str3, str4);
        } 
        char[] arrayOfChar = null;
        String str = (String)hashMap.get("trustStorePasswd");
        if (str.length() != 0)
          arrayOfChar = str.toCharArray(); 
        keyStore.load(fileInputStream, arrayOfChar);
        if (arrayOfChar != null)
          for (byte b = 0; b < arrayOfChar.length; b++)
            arrayOfChar[b] = Character.MIN_VALUE;  
      } 
    } finally {
      if (fileInputStream != null)
        fileInputStream.close(); 
    } 
    return keyStore;
  }
  
  public static final class TrustManagerFactoryImpl {}
  
  public static final class PKIXFactory extends TrustManagerFactoryImpl {
    X509TrustManager getInstance(KeyStore param1KeyStore) throws KeyStoreException {
      return new X509TrustManagerImpl("PKIX", param1KeyStore);
    }
    
    X509TrustManager getInstance(ManagerFactoryParameters param1ManagerFactoryParameters) throws InvalidAlgorithmParameterException {
      if (!(param1ManagerFactoryParameters instanceof CertPathTrustManagerParameters))
        throw new InvalidAlgorithmParameterException("Parameters must be CertPathTrustManagerParameters"); 
      CertPathParameters certPathParameters = ((CertPathTrustManagerParameters)param1ManagerFactoryParameters).getParameters();
      if (!(certPathParameters instanceof PKIXBuilderParameters))
        throw new InvalidAlgorithmParameterException("Encapsulated parameters must be PKIXBuilderParameters"); 
      PKIXBuilderParameters pKIXBuilderParameters = (PKIXBuilderParameters)certPathParameters;
      return new X509TrustManagerImpl("PKIX", pKIXBuilderParameters);
    }
  }
}
