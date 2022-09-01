package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.Debug;

public class KeyStore {
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("keystore"));
  
  private static final String KEYSTORE_TYPE = "keystore.type";
  
  private String type;
  
  private Provider provider;
  
  private KeyStoreSpi keyStoreSpi;
  
  private boolean initialized = false;
  
  protected KeyStore(KeyStoreSpi paramKeyStoreSpi, Provider paramProvider, String paramString) {
    this.keyStoreSpi = paramKeyStoreSpi;
    this.provider = paramProvider;
    this.type = paramString;
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyStore." + paramString.toUpperCase() + " type from: " + this.provider
          .getName()); 
  }
  
  public static KeyStore getInstance(String paramString) throws KeyStoreException {
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", (String)null);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString + " not found", noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new KeyStoreException(paramString + " not found", noSuchProviderException);
    } 
  }
  
  public static KeyStore getInstance(String paramString1, String paramString2) throws KeyStoreException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    try {
      Object[] arrayOfObject = Security.getImpl(paramString1, "KeyStore", paramString2);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString1 + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static KeyStore getInstance(String paramString, Provider paramProvider) throws KeyStoreException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "KeyStore", paramProvider);
      return new KeyStore((KeyStoreSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException(paramString + " not found", noSuchAlgorithmException);
    } 
  }
  
  public static final String getDefaultType() {
    String str = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            return Security.getProperty("keystore.type");
          }
        });
    if (str == null)
      str = "jks"; 
    return str;
  }
  
  static class KeyStore {}
  
  public static abstract class KeyStore {}
  
  public static final class KeyStore {}
  
  public static final class KeyStore {}
  
  public static final class KeyStore {}
  
  public static interface KeyStore {}
  
  public static class KeyStore {}
  
  public static class KeyStore {}
  
  public static interface KeyStore {}
  
  public static interface KeyStore {}
  
  public final Provider getProvider() {
    return this.provider;
  }
  
  public final String getType() {
    return this.type;
  }
  
  public final Key getKey(String paramString, char[] paramArrayOfchar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetKey(paramString, paramArrayOfchar);
  }
  
  public final Certificate[] getCertificateChain(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificateChain(paramString);
  }
  
  public final Certificate getCertificate(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificate(paramString);
  }
  
  public final Date getCreationDate(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCreationDate(paramString);
  }
  
  public final void setKeyEntry(String paramString, Key paramKey, char[] paramArrayOfchar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    if (paramKey instanceof PrivateKey && (paramArrayOfCertificate == null || paramArrayOfCertificate.length == 0))
      throw new IllegalArgumentException("Private key must be accompanied by certificate chain"); 
    this.keyStoreSpi.engineSetKeyEntry(paramString, paramKey, paramArrayOfchar, paramArrayOfCertificate);
  }
  
  public final void setKeyEntry(String paramString, byte[] paramArrayOfbyte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetKeyEntry(paramString, paramArrayOfbyte, paramArrayOfCertificate);
  }
  
  public final void setCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetCertificateEntry(paramString, paramCertificate);
  }
  
  public final void deleteEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineDeleteEntry(paramString);
  }
  
  public final Enumeration<String> aliases() throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineAliases();
  }
  
  public final boolean containsAlias(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineContainsAlias(paramString);
  }
  
  public final int size() throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineSize();
  }
  
  public final boolean isKeyEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineIsKeyEntry(paramString);
  }
  
  public final boolean isCertificateEntry(String paramString) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineIsCertificateEntry(paramString);
  }
  
  public final String getCertificateAlias(Certificate paramCertificate) throws KeyStoreException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetCertificateAlias(paramCertificate);
  }
  
  public final void store(OutputStream paramOutputStream, char[] paramArrayOfchar) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineStore(paramOutputStream, paramArrayOfchar);
  }
  
  public final void store(LoadStoreParameter paramLoadStoreParameter) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineStore(paramLoadStoreParameter);
  }
  
  public final void load(InputStream paramInputStream, char[] paramArrayOfchar) throws IOException, NoSuchAlgorithmException, CertificateException {
    this.keyStoreSpi.engineLoad(paramInputStream, paramArrayOfchar);
    this.initialized = true;
  }
  
  public final void load(LoadStoreParameter paramLoadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
    this.keyStoreSpi.engineLoad(paramLoadStoreParameter);
    this.initialized = true;
  }
  
  public final Entry getEntry(String paramString, ProtectionParameter paramProtectionParameter) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
    if (paramString == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineGetEntry(paramString, paramProtectionParameter);
  }
  
  public final void setEntry(String paramString, Entry paramEntry, ProtectionParameter paramProtectionParameter) throws KeyStoreException {
    if (paramString == null || paramEntry == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    this.keyStoreSpi.engineSetEntry(paramString, paramEntry, paramProtectionParameter);
  }
  
  public final boolean entryInstanceOf(String paramString, Class<? extends Entry> paramClass) throws KeyStoreException {
    if (paramString == null || paramClass == null)
      throw new NullPointerException("invalid null input"); 
    if (!this.initialized)
      throw new KeyStoreException("Uninitialized keystore"); 
    return this.keyStoreSpi.engineEntryInstanceOf(paramString, paramClass);
  }
}
