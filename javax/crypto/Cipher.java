package javax.crypto;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import sun.security.jca.GetInstance;
import sun.security.jca.ServiceId;
import sun.security.util.Debug;

public class Cipher {
  private static final Debug debug = Debug.getInstance("jca", "Cipher");
  
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("cipher"));
  
  public static final int ENCRYPT_MODE = 1;
  
  public static final int DECRYPT_MODE = 2;
  
  public static final int WRAP_MODE = 3;
  
  public static final int UNWRAP_MODE = 4;
  
  public static final int PUBLIC_KEY = 1;
  
  public static final int PRIVATE_KEY = 2;
  
  public static final int SECRET_KEY = 3;
  
  private Provider provider;
  
  private CipherSpi spi;
  
  private String transformation;
  
  private CryptoPermission cryptoPerm;
  
  private ExemptionMechanism exmech;
  
  private boolean initialized = false;
  
  private int opmode = 0;
  
  private static final String KEY_USAGE_EXTENSION_OID = "2.5.29.15";
  
  private CipherSpi firstSpi;
  
  private Provider.Service firstService;
  
  private Iterator<Provider.Service> serviceIterator;
  
  private List<Transform> transforms;
  
  private final Object lock;
  
  private static final String ATTR_MODE = "SupportedModes";
  
  private static final String ATTR_PAD = "SupportedPaddings";
  
  private static final int S_NO = 0;
  
  private static final int S_MAYBE = 1;
  
  private static final int S_YES = 2;
  
  protected Cipher(CipherSpi paramCipherSpi, Provider paramProvider, String paramString) {
    if (!JceSecurityManager.INSTANCE.isCallerTrusted())
      throw new NullPointerException(); 
    this.spi = paramCipherSpi;
    this.provider = paramProvider;
    this.transformation = paramString;
    this.cryptoPerm = CryptoAllPermission.INSTANCE;
    this.lock = null;
  }
  
  Cipher(CipherSpi paramCipherSpi, String paramString) {
    this.spi = paramCipherSpi;
    this.transformation = paramString;
    this.cryptoPerm = CryptoAllPermission.INSTANCE;
    this.lock = null;
  }
  
  private Cipher(CipherSpi paramCipherSpi, Provider.Service paramService, Iterator<Provider.Service> paramIterator, String paramString, List<Transform> paramList) {
    this.firstSpi = paramCipherSpi;
    this.firstService = paramService;
    this.serviceIterator = paramIterator;
    this.transforms = paramList;
    this.transformation = paramString;
    this.lock = new Object();
  }
  
  private static String[] tokenizeTransformation(String paramString) throws NoSuchAlgorithmException {
    if (paramString == null)
      throw new NoSuchAlgorithmException("No transformation given"); 
    String[] arrayOfString = new String[3];
    byte b = 0;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "/");
    try {
      while (stringTokenizer.hasMoreTokens() && b < 3)
        arrayOfString[b++] = stringTokenizer.nextToken().trim(); 
      if (b == 0 || b == 2 || stringTokenizer.hasMoreTokens())
        throw new NoSuchAlgorithmException("Invalid transformation format:" + paramString); 
    } catch (NoSuchElementException noSuchElementException) {
      throw new NoSuchAlgorithmException("Invalid transformation format:" + paramString);
    } 
    if (arrayOfString[0] == null || arrayOfString[0].length() == 0)
      throw new NoSuchAlgorithmException("Invalid transformation:algorithm not specified-" + paramString); 
    return arrayOfString;
  }
  
  private static class Transform {
    final String transform;
    
    final String suffix;
    
    final String mode;
    
    final String pad;
    
    Transform(String param1String1, String param1String2, String param1String3, String param1String4) {
      this.transform = param1String1 + param1String2;
      this.suffix = param1String2.toUpperCase(Locale.ENGLISH);
      this.mode = param1String3;
      this.pad = param1String4;
    }
    
    void setModePadding(CipherSpi param1CipherSpi) throws NoSuchAlgorithmException, NoSuchPaddingException {
      if (this.mode != null)
        param1CipherSpi.engineSetMode(this.mode); 
      if (this.pad != null)
        param1CipherSpi.engineSetPadding(this.pad); 
    }
    
    int supportsModePadding(Provider.Service param1Service) {
      int i = supportsMode(param1Service);
      if (i == 0)
        return i; 
      int j = supportsPadding(param1Service);
      return Math.min(i, j);
    }
    
    int supportsMode(Provider.Service param1Service) {
      return supports(param1Service, "SupportedModes", this.mode);
    }
    
    int supportsPadding(Provider.Service param1Service) {
      return supports(param1Service, "SupportedPaddings", this.pad);
    }
    
    private static int supports(Provider.Service param1Service, String param1String1, String param1String2) {
      if (param1String2 == null)
        return 2; 
      String str = param1Service.getAttribute(param1String1);
      if (str == null)
        return 1; 
      return matches(str, param1String2) ? 2 : 0;
    }
    
    private static final ConcurrentMap<String, Pattern> patternCache = new ConcurrentHashMap<>();
    
    private static boolean matches(String param1String1, String param1String2) {
      Pattern pattern = patternCache.get(param1String1);
      if (pattern == null) {
        pattern = Pattern.compile(param1String1);
        patternCache.putIfAbsent(param1String1, pattern);
      } 
      return pattern.matcher(param1String2.toUpperCase(Locale.ENGLISH)).matches();
    }
  }
  
  private static List<Transform> getTransforms(String paramString) throws NoSuchAlgorithmException {
    String[] arrayOfString = tokenizeTransformation(paramString);
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    String str3 = arrayOfString[2];
    if (str2 != null && str2.length() == 0)
      str2 = null; 
    if (str3 != null && str3.length() == 0)
      str3 = null; 
    if (str2 == null && str3 == null) {
      Transform transform = new Transform(str1, "", null, null);
      return Collections.singletonList(transform);
    } 
    ArrayList<Transform> arrayList = new ArrayList(4);
    arrayList.add(new Transform(str1, "/" + str2 + "/" + str3, null, null));
    arrayList.add(new Transform(str1, "/" + str2, null, str3));
    arrayList.add(new Transform(str1, "//" + str3, str2, null));
    arrayList.add(new Transform(str1, "", str2, str3));
    return arrayList;
  }
  
  private static Transform getTransform(Provider.Service paramService, List<Transform> paramList) {
    String str = paramService.getAlgorithm().toUpperCase(Locale.ENGLISH);
    for (Transform transform : paramList) {
      if (str.endsWith(transform.suffix))
        return transform; 
    } 
    return null;
  }
  
  public static final Cipher getInstance(String paramString) throws NoSuchAlgorithmException, NoSuchPaddingException {
    List<Transform> list = getTransforms(paramString);
    ArrayList<ServiceId> arrayList = new ArrayList(list.size());
    for (Transform transform : list)
      arrayList.add(new ServiceId("Cipher", transform.transform)); 
    List<Provider.Service> list1 = GetInstance.getServices(arrayList);
    Iterator<Provider.Service> iterator = list1.iterator();
    Exception exception = null;
    while (iterator.hasNext()) {
      Provider.Service service = iterator.next();
      if (!JceSecurity.canUseProvider(service.getProvider()))
        continue; 
      Transform transform = getTransform(service, list);
      if (transform == null)
        continue; 
      int i = transform.supportsModePadding(service);
      if (i == 0)
        continue; 
      if (i == 2)
        return new Cipher(null, service, iterator, paramString, list); 
      try {
        CipherSpi cipherSpi = (CipherSpi)service.newInstance(null);
        transform.setModePadding(cipherSpi);
        return new Cipher(cipherSpi, service, iterator, paramString, list);
      } catch (Exception exception1) {
        exception = exception1;
      } 
    } 
    throw new NoSuchAlgorithmException("Cannot find any provider supporting " + paramString, exception);
  }
  
  public static final Cipher getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("Missing provider"); 
    Provider provider = Security.getProvider(paramString2);
    if (provider == null)
      throw new NoSuchProviderException("No such provider: " + paramString2); 
    return getInstance(paramString1, provider);
  }
  
  public static final Cipher getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException, NoSuchPaddingException {
    if (paramProvider == null)
      throw new IllegalArgumentException("Missing provider"); 
    Exception exception = null;
    List<Transform> list = getTransforms(paramString);
    boolean bool = false;
    String str = null;
    for (Transform transform : list) {
      Provider.Service service = paramProvider.getService("Cipher", transform.transform);
      if (service == null)
        continue; 
      if (!bool) {
        Exception exception1 = JceSecurity.getVerificationResult(paramProvider);
        if (exception1 != null) {
          String str1 = "JCE cannot authenticate the provider " + paramProvider.getName();
          throw new SecurityException(str1, exception1);
        } 
        bool = true;
      } 
      if (transform.supportsMode(service) == 0)
        continue; 
      if (transform.supportsPadding(service) == 0) {
        str = transform.pad;
        continue;
      } 
      try {
        CipherSpi cipherSpi = (CipherSpi)service.newInstance(null);
        transform.setModePadding(cipherSpi);
        Cipher cipher = new Cipher(cipherSpi, paramString);
        cipher.provider = service.getProvider();
        cipher.initCryptoPermission();
        return cipher;
      } catch (Exception exception1) {
        exception = exception1;
      } 
    } 
    if (exception instanceof NoSuchPaddingException)
      throw (NoSuchPaddingException)exception; 
    if (str != null)
      throw new NoSuchPaddingException("Padding not supported: " + str); 
    throw new NoSuchAlgorithmException("No such algorithm: " + paramString, exception);
  }
  
  private void initCryptoPermission() throws NoSuchAlgorithmException {
    if (!JceSecurity.isRestricted()) {
      this.cryptoPerm = CryptoAllPermission.INSTANCE;
      this.exmech = null;
      return;
    } 
    this.cryptoPerm = getConfiguredPermission(this.transformation);
    String str = this.cryptoPerm.getExemptionMechanism();
    if (str != null)
      this.exmech = ExemptionMechanism.getInstance(str); 
  }
  
  private static int warnCount = 10;
  
  private static final int I_KEY = 1;
  
  private static final int I_PARAMSPEC = 2;
  
  private static final int I_PARAMS = 3;
  
  private static final int I_CERT = 4;
  
  void chooseFirstProvider() {
    if (this.spi != null)
      return; 
    synchronized (this.lock) {
      if (this.spi != null)
        return; 
      if (debug != null) {
        int i = --warnCount;
        if (i >= 0) {
          debug.println("Cipher.init() not first method called, disabling delayed provider selection");
          if (i == 0)
            debug.println("Further warnings of this type will be suppressed"); 
          (new Exception("Call trace")).printStackTrace();
        } 
      } 
      Exception exception = null;
      while (this.firstService != null || this.serviceIterator.hasNext()) {
        Provider.Service service;
        CipherSpi cipherSpi;
        if (this.firstService != null) {
          service = this.firstService;
          cipherSpi = this.firstSpi;
          this.firstService = null;
          this.firstSpi = null;
        } else {
          service = this.serviceIterator.next();
          cipherSpi = null;
        } 
        if (!JceSecurity.canUseProvider(service.getProvider()))
          continue; 
        Transform transform = getTransform(service, this.transforms);
        if (transform == null)
          continue; 
        if (transform.supportsModePadding(service) == 0)
          continue; 
        try {
          if (cipherSpi == null) {
            Object object = service.newInstance(null);
            if (!(object instanceof CipherSpi))
              continue; 
            cipherSpi = (CipherSpi)object;
          } 
          transform.setModePadding(cipherSpi);
          initCryptoPermission();
          this.spi = cipherSpi;
          this.provider = service.getProvider();
          this.firstService = null;
          this.serviceIterator = null;
          this.transforms = null;
          return;
        } catch (Exception exception1) {
          exception = exception1;
        } 
      } 
      ProviderException providerException = new ProviderException("Could not construct CipherSpi instance");
      if (exception != null)
        providerException.initCause(exception); 
      throw providerException;
    } 
  }
  
  private void implInit(CipherSpi paramCipherSpi, int paramInt1, int paramInt2, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    switch (paramInt1) {
      case 1:
        checkCryptoPerm(paramCipherSpi, paramKey);
        paramCipherSpi.engineInit(paramInt2, paramKey, paramSecureRandom);
        return;
      case 2:
        checkCryptoPerm(paramCipherSpi, paramKey, paramAlgorithmParameterSpec);
        paramCipherSpi.engineInit(paramInt2, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
        return;
      case 3:
        checkCryptoPerm(paramCipherSpi, paramKey, paramAlgorithmParameters);
        paramCipherSpi.engineInit(paramInt2, paramKey, paramAlgorithmParameters, paramSecureRandom);
        return;
      case 4:
        checkCryptoPerm(paramCipherSpi, paramKey);
        paramCipherSpi.engineInit(paramInt2, paramKey, paramSecureRandom);
        return;
    } 
    throw new AssertionError("Internal Cipher error: " + paramInt1);
  }
  
  private void chooseProvider(int paramInt1, int paramInt2, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    synchronized (this.lock) {
      if (this.spi != null) {
        implInit(this.spi, paramInt1, paramInt2, paramKey, paramAlgorithmParameterSpec, paramAlgorithmParameters, paramSecureRandom);
        return;
      } 
      Exception exception = null;
      while (this.firstService != null || this.serviceIterator.hasNext()) {
        Provider.Service service;
        CipherSpi cipherSpi;
        if (this.firstService != null) {
          service = this.firstService;
          cipherSpi = this.firstSpi;
          this.firstService = null;
          this.firstSpi = null;
        } else {
          service = this.serviceIterator.next();
          cipherSpi = null;
        } 
        if (!service.supportsParameter(paramKey))
          continue; 
        if (!JceSecurity.canUseProvider(service.getProvider()))
          continue; 
        Transform transform = getTransform(service, this.transforms);
        if (transform == null)
          continue; 
        if (transform.supportsModePadding(service) == 0)
          continue; 
        try {
          if (cipherSpi == null)
            cipherSpi = (CipherSpi)service.newInstance(null); 
          transform.setModePadding(cipherSpi);
          initCryptoPermission();
          implInit(cipherSpi, paramInt1, paramInt2, paramKey, paramAlgorithmParameterSpec, paramAlgorithmParameters, paramSecureRandom);
          this.provider = service.getProvider();
          this.spi = cipherSpi;
          this.firstService = null;
          this.serviceIterator = null;
          this.transforms = null;
          return;
        } catch (Exception exception1) {
          if (exception == null)
            exception = exception1; 
        } 
      } 
      if (exception instanceof InvalidKeyException)
        throw (InvalidKeyException)exception; 
      if (exception instanceof InvalidAlgorithmParameterException)
        throw (InvalidAlgorithmParameterException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      String str = (paramKey != null) ? paramKey.getClass().getName() : "(null)";
      throw new InvalidKeyException("No installed provider supports this key: " + str, exception);
    } 
  }
  
  public final Provider getProvider() {
    chooseFirstProvider();
    return this.provider;
  }
  
  public final String getAlgorithm() {
    return this.transformation;
  }
  
  public final int getBlockSize() {
    chooseFirstProvider();
    return this.spi.engineGetBlockSize();
  }
  
  public final int getOutputSize(int paramInt) {
    if (!this.initialized && !(this instanceof NullCipher))
      throw new IllegalStateException("Cipher not initialized"); 
    if (paramInt < 0)
      throw new IllegalArgumentException("Input size must be equal to or greater than zero"); 
    chooseFirstProvider();
    return this.spi.engineGetOutputSize(paramInt);
  }
  
  public final byte[] getIV() {
    chooseFirstProvider();
    return this.spi.engineGetIV();
  }
  
  public final AlgorithmParameters getParameters() {
    chooseFirstProvider();
    return this.spi.engineGetParameters();
  }
  
  public final ExemptionMechanism getExemptionMechanism() {
    chooseFirstProvider();
    return this.exmech;
  }
  
  private void checkCryptoPerm(CipherSpi paramCipherSpi, Key paramKey) throws InvalidKeyException {
    AlgorithmParameterSpec algorithmParameterSpec;
    if (this.cryptoPerm == CryptoAllPermission.INSTANCE)
      return; 
    try {
      algorithmParameterSpec = getAlgorithmParameterSpec(paramCipherSpi.engineGetParameters());
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new InvalidKeyException("Unsupported default algorithm parameters");
    } 
    if (!passCryptoPermCheck(paramCipherSpi, paramKey, algorithmParameterSpec))
      throw new InvalidKeyException("Illegal key size or default parameters"); 
  }
  
  private void checkCryptoPerm(CipherSpi paramCipherSpi, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (this.cryptoPerm == CryptoAllPermission.INSTANCE)
      return; 
    if (!passCryptoPermCheck(paramCipherSpi, paramKey, null))
      throw new InvalidKeyException("Illegal key size"); 
    if (paramAlgorithmParameterSpec != null && !passCryptoPermCheck(paramCipherSpi, paramKey, paramAlgorithmParameterSpec))
      throw new InvalidAlgorithmParameterException("Illegal parameters"); 
  }
  
  private void checkCryptoPerm(CipherSpi paramCipherSpi, Key paramKey, AlgorithmParameters paramAlgorithmParameters) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AlgorithmParameterSpec algorithmParameterSpec;
    if (this.cryptoPerm == CryptoAllPermission.INSTANCE)
      return; 
    try {
      algorithmParameterSpec = getAlgorithmParameterSpec(paramAlgorithmParameters);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new InvalidAlgorithmParameterException("Failed to retrieve algorithm parameter specification");
    } 
    checkCryptoPerm(paramCipherSpi, paramKey, algorithmParameterSpec);
  }
  
  private boolean passCryptoPermCheck(CipherSpi paramCipherSpi, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException {
    String str2, str1 = this.cryptoPerm.getExemptionMechanism();
    int i = paramCipherSpi.engineGetKeySize(paramKey);
    int j = this.transformation.indexOf('/');
    if (j != -1) {
      str2 = this.transformation.substring(0, j);
    } else {
      str2 = this.transformation;
    } 
    CryptoPermission cryptoPermission = new CryptoPermission(str2, i, paramAlgorithmParameterSpec, str1);
    if (!this.cryptoPerm.implies(cryptoPermission)) {
      if (debug != null) {
        debug.println("Crypto Permission check failed");
        debug.println("granted: " + this.cryptoPerm);
        debug.println("requesting: " + cryptoPermission);
      } 
      return false;
    } 
    if (this.exmech == null)
      return true; 
    try {
      if (!this.exmech.isCryptoAllowed(paramKey)) {
        if (debug != null)
          debug.println(this.exmech.getName() + " isn't enforced"); 
        return false;
      } 
    } catch (ExemptionMechanismException exemptionMechanismException) {
      if (debug != null) {
        debug.println("Cannot determine whether " + this.exmech
            .getName() + " has been enforced");
        exemptionMechanismException.printStackTrace();
      } 
      return false;
    } 
    return true;
  }
  
  private static void checkOpmode(int paramInt) {
    if (paramInt < 1 || paramInt > 4)
      throw new InvalidParameterException("Invalid operation mode"); 
  }
  
  private static String getOpmodeString(int paramInt) {
    switch (paramInt) {
      case 1:
        return "encryption";
      case 2:
        return "decryption";
      case 3:
        return "key wrapping";
      case 4:
        return "key unwrapping";
    } 
    return "";
  }
  
  public final void init(int paramInt, Key paramKey) throws InvalidKeyException {
    init(paramInt, paramKey, JceSecurity.RANDOM);
  }
  
  public final void init(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.initialized = false;
    checkOpmode(paramInt);
    if (this.spi != null) {
      checkCryptoPerm(this.spi, paramKey);
      this.spi.engineInit(paramInt, paramKey, paramSecureRandom);
    } else {
      try {
        chooseProvider(1, paramInt, paramKey, null, null, paramSecureRandom);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new InvalidKeyException(invalidAlgorithmParameterException);
      } 
    } 
    this.initialized = true;
    this.opmode = paramInt;
    if (!skipDebug && pdebug != null)
      pdebug.println("Cipher." + this.transformation + " " + 
          getOpmodeString(paramInt) + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void init(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    init(paramInt, paramKey, paramAlgorithmParameterSpec, JceSecurity.RANDOM);
  }
  
  public final void init(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.initialized = false;
    checkOpmode(paramInt);
    if (this.spi != null) {
      checkCryptoPerm(this.spi, paramKey, paramAlgorithmParameterSpec);
      this.spi.engineInit(paramInt, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
    } else {
      chooseProvider(2, paramInt, paramKey, paramAlgorithmParameterSpec, null, paramSecureRandom);
    } 
    this.initialized = true;
    this.opmode = paramInt;
    if (!skipDebug && pdebug != null)
      pdebug.println("Cipher." + this.transformation + " " + 
          getOpmodeString(paramInt) + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void init(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters) throws InvalidKeyException, InvalidAlgorithmParameterException {
    init(paramInt, paramKey, paramAlgorithmParameters, JceSecurity.RANDOM);
  }
  
  public final void init(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.initialized = false;
    checkOpmode(paramInt);
    if (this.spi != null) {
      checkCryptoPerm(this.spi, paramKey, paramAlgorithmParameters);
      this.spi.engineInit(paramInt, paramKey, paramAlgorithmParameters, paramSecureRandom);
    } else {
      chooseProvider(3, paramInt, paramKey, null, paramAlgorithmParameters, paramSecureRandom);
    } 
    this.initialized = true;
    this.opmode = paramInt;
    if (!skipDebug && pdebug != null)
      pdebug.println("Cipher." + this.transformation + " " + 
          getOpmodeString(paramInt) + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void init(int paramInt, Certificate paramCertificate) throws InvalidKeyException {
    init(paramInt, paramCertificate, JceSecurity.RANDOM);
  }
  
  public final void init(int paramInt, Certificate paramCertificate, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.initialized = false;
    checkOpmode(paramInt);
    if (paramCertificate instanceof X509Certificate) {
      X509Certificate x509Certificate = (X509Certificate)paramCertificate;
      Set<String> set = x509Certificate.getCriticalExtensionOIDs();
      if (set != null && !set.isEmpty() && set
        .contains("2.5.29.15")) {
        boolean[] arrayOfBoolean = x509Certificate.getKeyUsage();
        if (arrayOfBoolean != null && ((paramInt == 1 && arrayOfBoolean.length > 3 && !arrayOfBoolean[3]) || (paramInt == 3 && arrayOfBoolean.length > 2 && !arrayOfBoolean[2])))
          throw new InvalidKeyException("Wrong key usage"); 
      } 
    } 
    PublicKey publicKey = (paramCertificate == null) ? null : paramCertificate.getPublicKey();
    if (this.spi != null) {
      checkCryptoPerm(this.spi, publicKey);
      this.spi.engineInit(paramInt, publicKey, paramSecureRandom);
    } else {
      try {
        chooseProvider(4, paramInt, publicKey, null, null, paramSecureRandom);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new InvalidKeyException(invalidAlgorithmParameterException);
      } 
    } 
    this.initialized = true;
    this.opmode = paramInt;
    if (!skipDebug && pdebug != null)
      pdebug.println("Cipher." + this.transformation + " " + 
          getOpmodeString(paramInt) + " algorithm from: " + this.provider
          .getName()); 
  }
  
  private void checkCipherState() {
    if (!(this instanceof NullCipher)) {
      if (!this.initialized)
        throw new IllegalStateException("Cipher not initialized"); 
      if (this.opmode != 1 && this.opmode != 2)
        throw new IllegalStateException("Cipher not initialized for encryption/decryption"); 
    } 
  }
  
  public final byte[] update(byte[] paramArrayOfbyte) {
    checkCipherState();
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("Null input buffer"); 
    chooseFirstProvider();
    if (paramArrayOfbyte.length == 0)
      return null; 
    return this.spi.engineUpdate(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    checkCipherState();
    if (paramArrayOfbyte == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte.length - paramInt1 || paramInt2 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    if (paramInt2 == 0)
      return null; 
    return this.spi.engineUpdate(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public final int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2) throws ShortBufferException {
    checkCipherState();
    if (paramArrayOfbyte1 == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte1.length - paramInt1 || paramInt2 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    if (paramInt2 == 0)
      return 0; 
    return this.spi.engineUpdate(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, 0);
  }
  
  public final int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    checkCipherState();
    if (paramArrayOfbyte1 == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte1.length - paramInt1 || paramInt2 < 0 || paramInt3 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    if (paramInt2 == 0)
      return 0; 
    return this.spi.engineUpdate(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  public final int update(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws ShortBufferException {
    checkCipherState();
    if (paramByteBuffer1 == null || paramByteBuffer2 == null)
      throw new IllegalArgumentException("Buffers must not be null"); 
    if (paramByteBuffer1 == paramByteBuffer2)
      throw new IllegalArgumentException("Input and output buffers must not be the same object, consider using buffer.duplicate()"); 
    if (paramByteBuffer2.isReadOnly())
      throw new ReadOnlyBufferException(); 
    chooseFirstProvider();
    return this.spi.engineUpdate(paramByteBuffer1, paramByteBuffer2);
  }
  
  public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    chooseFirstProvider();
    return this.spi.engineDoFinal(null, 0, 0);
  }
  
  public final int doFinal(byte[] paramArrayOfbyte, int paramInt) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
    checkCipherState();
    if (paramArrayOfbyte == null || paramInt < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(null, 0, 0, paramArrayOfbyte, paramInt);
  }
  
  public final byte[] doFinal(byte[] paramArrayOfbyte) throws IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("Null input buffer"); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    if (paramArrayOfbyte == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte.length - paramInt1 || paramInt2 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public final int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    if (paramArrayOfbyte1 == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte1.length - paramInt1 || paramInt2 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, 0);
  }
  
  public final int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    if (paramArrayOfbyte1 == null || paramInt1 < 0 || paramInt2 > paramArrayOfbyte1.length - paramInt1 || paramInt2 < 0 || paramInt3 < 0)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  public final int doFinal(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    checkCipherState();
    if (paramByteBuffer1 == null || paramByteBuffer2 == null)
      throw new IllegalArgumentException("Buffers must not be null"); 
    if (paramByteBuffer1 == paramByteBuffer2)
      throw new IllegalArgumentException("Input and output buffers must not be the same object, consider using buffer.duplicate()"); 
    if (paramByteBuffer2.isReadOnly())
      throw new ReadOnlyBufferException(); 
    chooseFirstProvider();
    return this.spi.engineDoFinal(paramByteBuffer1, paramByteBuffer2);
  }
  
  public final byte[] wrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    if (!(this instanceof NullCipher)) {
      if (!this.initialized)
        throw new IllegalStateException("Cipher not initialized"); 
      if (this.opmode != 3)
        throw new IllegalStateException("Cipher not initialized for wrapping keys"); 
    } 
    chooseFirstProvider();
    return this.spi.engineWrap(paramKey);
  }
  
  public final Key unwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
    if (!(this instanceof NullCipher)) {
      if (!this.initialized)
        throw new IllegalStateException("Cipher not initialized"); 
      if (this.opmode != 4)
        throw new IllegalStateException("Cipher not initialized for unwrapping keys"); 
    } 
    if (paramInt != 3 && paramInt != 2 && paramInt != 1)
      throw new InvalidParameterException("Invalid key type"); 
    chooseFirstProvider();
    return this.spi.engineUnwrap(paramArrayOfbyte, paramString, paramInt);
  }
  
  private AlgorithmParameterSpec getAlgorithmParameterSpec(AlgorithmParameters paramAlgorithmParameters) throws InvalidParameterSpecException {
    if (paramAlgorithmParameters == null)
      return null; 
    String str = paramAlgorithmParameters.getAlgorithm().toUpperCase(Locale.ENGLISH);
    if (str.equalsIgnoreCase("RC2"))
      return paramAlgorithmParameters.getParameterSpec((Class)RC2ParameterSpec.class); 
    if (str.equalsIgnoreCase("RC5"))
      return paramAlgorithmParameters.getParameterSpec((Class)RC5ParameterSpec.class); 
    if (str.startsWith("PBE"))
      return paramAlgorithmParameters.getParameterSpec((Class)PBEParameterSpec.class); 
    if (str.startsWith("DES"))
      return paramAlgorithmParameters.getParameterSpec((Class)IvParameterSpec.class); 
    return null;
  }
  
  private static CryptoPermission getConfiguredPermission(String paramString) throws NullPointerException, NoSuchAlgorithmException {
    if (paramString == null)
      throw new NullPointerException(); 
    String[] arrayOfString = tokenizeTransformation(paramString);
    return JceSecurityManager.INSTANCE.getCryptoPermission(arrayOfString[0]);
  }
  
  public static final int getMaxAllowedKeyLength(String paramString) throws NoSuchAlgorithmException {
    CryptoPermission cryptoPermission = getConfiguredPermission(paramString);
    return cryptoPermission.getMaxKeySize();
  }
  
  public static final AlgorithmParameterSpec getMaxAllowedParameterSpec(String paramString) throws NoSuchAlgorithmException {
    CryptoPermission cryptoPermission = getConfiguredPermission(paramString);
    return cryptoPermission.getAlgorithmParameterSpec();
  }
  
  public final void updateAAD(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("src buffer is null"); 
    updateAAD(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final void updateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    checkCipherState();
    if (paramArrayOfbyte == null || paramInt1 < 0 || paramInt2 < 0 || paramInt2 + paramInt1 > paramArrayOfbyte.length)
      throw new IllegalArgumentException("Bad arguments"); 
    chooseFirstProvider();
    if (paramInt2 == 0)
      return; 
    this.spi.engineUpdateAAD(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public final void updateAAD(ByteBuffer paramByteBuffer) {
    checkCipherState();
    if (paramByteBuffer == null)
      throw new IllegalArgumentException("src ByteBuffer is null"); 
    chooseFirstProvider();
    if (paramByteBuffer.remaining() == 0)
      return; 
    this.spi.engineUpdateAAD(paramByteBuffer);
  }
}
