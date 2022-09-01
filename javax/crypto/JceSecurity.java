package javax.crypto;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import sun.security.jca.GetInstance;

final class JceSecurity {
  static final SecureRandom RANDOM = new SecureRandom();
  
  private static CryptoPermissions defaultPolicy = null;
  
  private static CryptoPermissions exemptPolicy = null;
  
  private static final Map<Provider, Object> verificationResults = new IdentityHashMap<>();
  
  private static final Map<Provider, Object> verifyingProviders = new IdentityHashMap<>();
  
  private static boolean isRestricted = true;
  
  static {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception {
              JceSecurity.setupJurisdictionPolicies();
              return null;
            }
          });
      isRestricted = !defaultPolicy.implies(CryptoAllPermission.INSTANCE);
    } catch (Exception exception) {
      throw new SecurityException("Can not initialize cryptographic mechanism", exception);
    } 
  }
  
  static GetInstance.Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, String paramString3) throws NoSuchAlgorithmException, NoSuchProviderException {
    Provider.Service service = GetInstance.getService(paramString1, paramString2, paramString3);
    Exception exception = getVerificationResult(service.getProvider());
    if (exception != null) {
      String str = "JCE cannot authenticate the provider " + paramString3;
      throw (NoSuchProviderException)(new NoSuchProviderException(str))
        .initCause(exception);
    } 
    return GetInstance.getInstance(service, paramClass);
  }
  
  static GetInstance.Instance getInstance(String paramString1, Class<?> paramClass, String paramString2, Provider paramProvider) throws NoSuchAlgorithmException {
    Provider.Service service = GetInstance.getService(paramString1, paramString2, paramProvider);
    Exception exception = getVerificationResult(paramProvider);
    if (exception != null) {
      String str = "JCE cannot authenticate the provider " + paramProvider.getName();
      throw new SecurityException(str, exception);
    } 
    return GetInstance.getInstance(service, paramClass);
  }
  
  static GetInstance.Instance getInstance(String paramString1, Class<?> paramClass, String paramString2) throws NoSuchAlgorithmException {
    List<Provider.Service> list = GetInstance.getServices(paramString1, paramString2);
    NoSuchAlgorithmException noSuchAlgorithmException = null;
    for (Provider.Service service : list) {
      if (!canUseProvider(service.getProvider()))
        continue; 
      try {
        return GetInstance.getInstance(service, paramClass);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
        noSuchAlgorithmException = noSuchAlgorithmException1;
      } 
    } 
    throw new NoSuchAlgorithmException("Algorithm " + paramString2 + " not available", noSuchAlgorithmException);
  }
  
  static CryptoPermissions verifyExemptJar(URL paramURL) throws Exception {
    JarVerifier jarVerifier = new JarVerifier(paramURL, true);
    jarVerifier.verify();
    return jarVerifier.getPermissions();
  }
  
  static void verifyProviderJar(URL paramURL) throws Exception {
    JarVerifier jarVerifier = new JarVerifier(paramURL, false);
    jarVerifier.verify();
  }
  
  private static final Object PROVIDER_VERIFIED = Boolean.TRUE;
  
  private static final URL NULL_URL;
  
  static synchronized Exception getVerificationResult(Provider paramProvider) {
    Object object = verificationResults.get(paramProvider);
    if (object == PROVIDER_VERIFIED)
      return null; 
    if (object != null)
      return (Exception)object; 
    if (verifyingProviders.get(paramProvider) != null)
      return new NoSuchProviderException("Recursion during verification"); 
    try {
      verifyingProviders.put(paramProvider, Boolean.FALSE);
      URL uRL = getCodeBase(paramProvider.getClass());
      verifyProviderJar(uRL);
      verificationResults.put(paramProvider, PROVIDER_VERIFIED);
      return null;
    } catch (Exception exception) {
      verificationResults.put(paramProvider, exception);
      return exception;
    } finally {
      verifyingProviders.remove(paramProvider);
    } 
  }
  
  static boolean canUseProvider(Provider paramProvider) {
    return (getVerificationResult(paramProvider) == null);
  }
  
  static {
    try {
      NULL_URL = new URL("http://null.sun.com/");
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  private static final Map<Class<?>, URL> codeBaseCacheRef = new WeakHashMap<>();
  
  static URL getCodeBase(final Class<?> clazz) {
    synchronized (codeBaseCacheRef) {
      URL uRL = codeBaseCacheRef.get(clazz);
      if (uRL == null) {
        uRL = AccessController.<URL>doPrivileged(new PrivilegedAction<URL>() {
              public URL run() {
                ProtectionDomain protectionDomain = clazz.getProtectionDomain();
                if (protectionDomain != null) {
                  CodeSource codeSource = protectionDomain.getCodeSource();
                  if (codeSource != null)
                    return codeSource.getLocation(); 
                } 
                return JceSecurity.NULL_URL;
              }
            });
        codeBaseCacheRef.put(clazz, uRL);
      } 
      return (uRL == NULL_URL) ? null : uRL;
    } 
  }
  
  private static void setupJurisdictionPolicies() throws Exception {
    String str1 = System.getProperty("java.home");
    String str2 = File.separator;
    String str3 = str1 + str2 + "lib" + str2 + "security" + str2;
    File file1 = new File(str3, "US_export_policy.jar");
    File file2 = new File(str3, "local_policy.jar");
    URL uRL = ClassLoader.getSystemResource("javax/crypto/Cipher.class");
    if (uRL == null || 
      !file1.exists() || !file2.exists())
      throw new SecurityException("Cannot locate policy or framework files!"); 
    CryptoPermissions cryptoPermissions1 = new CryptoPermissions();
    CryptoPermissions cryptoPermissions2 = new CryptoPermissions();
    loadPolicies(file1, cryptoPermissions1, cryptoPermissions2);
    CryptoPermissions cryptoPermissions3 = new CryptoPermissions();
    CryptoPermissions cryptoPermissions4 = new CryptoPermissions();
    loadPolicies(file2, cryptoPermissions3, cryptoPermissions4);
    if (cryptoPermissions1.isEmpty() || cryptoPermissions3.isEmpty())
      throw new SecurityException("Missing mandatory jurisdiction policy files"); 
    defaultPolicy = cryptoPermissions1.getMinimum(cryptoPermissions3);
    if (cryptoPermissions2.isEmpty()) {
      exemptPolicy = cryptoPermissions4.isEmpty() ? null : cryptoPermissions4;
    } else {
      exemptPolicy = cryptoPermissions2.getMinimum(cryptoPermissions4);
    } 
  }
  
  private static void loadPolicies(File paramFile, CryptoPermissions paramCryptoPermissions1, CryptoPermissions paramCryptoPermissions2) throws Exception {
    JarFile jarFile = new JarFile(paramFile);
    Enumeration<JarEntry> enumeration = jarFile.entries();
    while (enumeration.hasMoreElements()) {
      JarEntry jarEntry = enumeration.nextElement();
      InputStream inputStream = null;
      try {
        if (jarEntry.getName().startsWith("default_")) {
          inputStream = jarFile.getInputStream(jarEntry);
          paramCryptoPermissions1.load(inputStream);
        } else if (jarEntry.getName().startsWith("exempt_")) {
          inputStream = jarFile.getInputStream(jarEntry);
          paramCryptoPermissions2.load(inputStream);
        } else {
          if (inputStream != null)
            inputStream.close(); 
          continue;
        } 
      } finally {
        if (inputStream != null)
          inputStream.close(); 
      } 
      JarVerifier.verifyPolicySigned(jarEntry.getCertificates());
    } 
    jarFile.close();
    jarFile = null;
  }
  
  static CryptoPermissions getDefaultPolicy() {
    return defaultPolicy;
  }
  
  static CryptoPermissions getExemptPolicy() {
    return exemptPolicy;
  }
  
  static boolean isRestricted() {
    return isRestricted;
  }
}
