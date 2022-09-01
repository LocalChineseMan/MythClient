package sun.security.ssl;

import com.sun.net.ssl.internal.ssl.Provider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;

public abstract class SunJSSE extends Provider {
  private static final long serialVersionUID = 3231825739635378733L;
  
  private static String info = "Sun JSSE provider(PKCS12, SunX509/PKIX key/trust factories, SSLv3/TLSv1/TLSv1.1/TLSv1.2)";
  
  private static String fipsInfo = "Sun JSSE provider (FIPS mode, crypto provider ";
  
  private static Boolean fips;
  
  static Provider cryptoProvider;
  
  protected static synchronized boolean isFIPS() {
    if (fips == null)
      fips = Boolean.valueOf(false); 
    return fips.booleanValue();
  }
  
  private static synchronized void ensureFIPS(Provider paramProvider) {
    if (fips == null) {
      fips = Boolean.valueOf(true);
      cryptoProvider = paramProvider;
    } else {
      if (!fips.booleanValue())
        throw new ProviderException("SunJSSE already initialized in non-FIPS mode"); 
      if (cryptoProvider != paramProvider)
        throw new ProviderException("SunJSSE already initialized with FIPS crypto provider " + cryptoProvider); 
    } 
  }
  
  protected SunJSSE() {
    super("SunJSSE", 1.8D, info);
    subclassCheck();
    if (Boolean.TRUE.equals(fips))
      throw new ProviderException("SunJSSE is already initialized in FIPS mode"); 
    registerAlgorithms(false);
  }
  
  protected SunJSSE(Provider paramProvider) {
    this(checkNull(paramProvider), paramProvider.getName());
  }
  
  protected SunJSSE(String paramString) {
    this(null, checkNull(paramString));
  }
  
  private static <T> T checkNull(T paramT) {
    if (paramT == null)
      throw new ProviderException("cryptoProvider must not be null"); 
    return paramT;
  }
  
  private SunJSSE(Provider paramProvider, String paramString) {
    super("SunJSSE", 1.8D, fipsInfo + paramString + ")");
    subclassCheck();
    if (paramProvider == null) {
      paramProvider = Security.getProvider(paramString);
      if (paramProvider == null)
        throw new ProviderException("Crypto provider not installed: " + paramString); 
    } 
    ensureFIPS(paramProvider);
    registerAlgorithms(true);
  }
  
  private void registerAlgorithms(final boolean isfips) {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            SunJSSE.this.doRegister(isfips);
            return null;
          }
        });
  }
  
  private void doRegister(boolean paramBoolean) {
    if (!paramBoolean) {
      put("KeyFactory.RSA", "sun.security.rsa.RSAKeyFactory");
      put("Alg.Alias.KeyFactory.1.2.840.113549.1.1", "RSA");
      put("Alg.Alias.KeyFactory.OID.1.2.840.113549.1.1", "RSA");
      put("KeyPairGenerator.RSA", "sun.security.rsa.RSAKeyPairGenerator");
      put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1", "RSA");
      put("Alg.Alias.KeyPairGenerator.OID.1.2.840.113549.1.1", "RSA");
      put("Signature.MD2withRSA", "sun.security.rsa.RSASignature$MD2withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.2", "MD2withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.2", "MD2withRSA");
      put("Signature.MD5withRSA", "sun.security.rsa.RSASignature$MD5withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.4", "MD5withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.4", "MD5withRSA");
      put("Signature.SHA1withRSA", "sun.security.rsa.RSASignature$SHA1withRSA");
      put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1withRSA");
      put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5", "SHA1withRSA");
      put("Alg.Alias.Signature.1.3.14.3.2.29", "SHA1withRSA");
      put("Alg.Alias.Signature.OID.1.3.14.3.2.29", "SHA1withRSA");
    } 
    put("Signature.MD5andSHA1withRSA", "sun.security.ssl.RSASignature");
    put("KeyManagerFactory.SunX509", "sun.security.ssl.KeyManagerFactoryImpl$SunX509");
    put("KeyManagerFactory.NewSunX509", "sun.security.ssl.KeyManagerFactoryImpl$X509");
    put("Alg.Alias.KeyManagerFactory.PKIX", "NewSunX509");
    put("TrustManagerFactory.SunX509", "sun.security.ssl.TrustManagerFactoryImpl$SimpleFactory");
    put("TrustManagerFactory.PKIX", "sun.security.ssl.TrustManagerFactoryImpl$PKIXFactory");
    put("Alg.Alias.TrustManagerFactory.SunPKIX", "PKIX");
    put("Alg.Alias.TrustManagerFactory.X509", "PKIX");
    put("Alg.Alias.TrustManagerFactory.X.509", "PKIX");
    put("SSLContext.TLSv1", "sun.security.ssl.SSLContextImpl$TLS10Context");
    put("SSLContext.TLSv1.1", "sun.security.ssl.SSLContextImpl$TLS11Context");
    put("SSLContext.TLSv1.2", "sun.security.ssl.SSLContextImpl$TLS12Context");
    put("SSLContext.TLS", "sun.security.ssl.SSLContextImpl$TLSContext");
    if (!paramBoolean) {
      put("Alg.Alias.SSLContext.SSL", "TLS");
      put("Alg.Alias.SSLContext.SSLv3", "TLSv1");
    } 
    put("SSLContext.Default", "sun.security.ssl.SSLContextImpl$DefaultSSLContext");
    put("KeyStore.PKCS12", "sun.security.pkcs12.PKCS12KeyStore");
  }
  
  private void subclassCheck() {
    if (getClass() != Provider.class)
      throw new AssertionError("Illegal subclass: " + getClass()); 
  }
  
  protected final void finalize() throws Throwable {
    super.finalize();
  }
}
