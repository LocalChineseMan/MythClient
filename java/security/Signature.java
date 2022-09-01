package java.security;

import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import sun.security.jca.GetInstance;
import sun.security.jca.ServiceId;
import sun.security.util.Debug;

public abstract class Signature extends SignatureSpi {
  private static final Debug debug = Debug.getInstance("jca", "Signature");
  
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("signature"));
  
  private String algorithm;
  
  Provider provider;
  
  protected static final int UNINITIALIZED = 0;
  
  protected static final int SIGN = 2;
  
  protected static final int VERIFY = 3;
  
  protected int state = 0;
  
  private static final String RSA_SIGNATURE = "NONEwithRSA";
  
  private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
  
  protected Signature(String paramString) {
    this.algorithm = paramString;
  }
  
  private static final List<ServiceId> rsaIds = Arrays.asList(new ServiceId[] { new ServiceId("Signature", "NONEwithRSA"), new ServiceId("Cipher", "RSA/ECB/PKCS1Padding"), new ServiceId("Cipher", "RSA/ECB"), new ServiceId("Cipher", "RSA//PKCS1Padding"), new ServiceId("Cipher", "RSA") });
  
  public static Signature getInstance(String paramString) throws NoSuchAlgorithmException {
    List<Provider.Service> list;
    if (paramString.equalsIgnoreCase("NONEwithRSA")) {
      list = GetInstance.getServices(rsaIds);
    } else {
      list = GetInstance.getServices("Signature", paramString);
    } 
    Iterator<Provider.Service> iterator = list.iterator();
    if (!iterator.hasNext())
      throw new NoSuchAlgorithmException(paramString + " Signature not available"); 
    while (true) {
      Provider.Service service = iterator.next();
      if (isSpi(service))
        return new Delegate(service, iterator, paramString); 
      try {
        GetInstance.Instance instance = GetInstance.getInstance(service, SignatureSpi.class);
        return getInstance(instance, paramString);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException2) {
        NoSuchAlgorithmException noSuchAlgorithmException1 = noSuchAlgorithmException2;
        if (!iterator.hasNext())
          throw noSuchAlgorithmException1; 
      } 
    } 
  }
  
  private static Signature getInstance(GetInstance.Instance paramInstance, String paramString) {
    Signature signature;
    if (paramInstance.impl instanceof Signature) {
      signature = (Signature)paramInstance.impl;
      signature.algorithm = paramString;
    } else {
      SignatureSpi signatureSpi = (SignatureSpi)paramInstance.impl;
      signature = new Delegate(signatureSpi, paramString);
    } 
    signature.provider = paramInstance.provider;
    return signature;
  }
  
  private static final Map<String, Boolean> signatureInfo = new ConcurrentHashMap<>();
  
  static {
    Boolean bool = Boolean.TRUE;
    signatureInfo.put("sun.security.provider.DSA$RawDSA", bool);
    signatureInfo.put("sun.security.provider.DSA$SHA1withDSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$MD2withRSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$MD5withRSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA1withRSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA256withRSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA384withRSA", bool);
    signatureInfo.put("sun.security.rsa.RSASignature$SHA512withRSA", bool);
    signatureInfo.put("com.sun.net.ssl.internal.ssl.RSASignature", bool);
    signatureInfo.put("sun.security.pkcs11.P11Signature", bool);
  }
  
  private static boolean isSpi(Provider.Service paramService) {
    if (paramService.getType().equals("Cipher"))
      return true; 
    String str = paramService.getClassName();
    Boolean bool = signatureInfo.get(str);
    if (bool == null)
      try {
        Object object = paramService.newInstance(null);
        boolean bool1 = (object instanceof SignatureSpi && !(object instanceof Signature)) ? true : false;
        if (debug != null && !bool1) {
          debug.println("Not a SignatureSpi " + str);
          debug.println("Delayed provider selection may not be available for algorithm " + paramService
              .getAlgorithm());
        } 
        bool = Boolean.valueOf(bool1);
        signatureInfo.put(str, bool);
      } catch (Exception exception) {
        return false;
      }  
    return bool.booleanValue();
  }
  
  public static Signature getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString1.equalsIgnoreCase("NONEwithRSA")) {
      if (paramString2 == null || paramString2.length() == 0)
        throw new IllegalArgumentException("missing provider"); 
      Provider provider = Security.getProvider(paramString2);
      if (provider == null)
        throw new NoSuchProviderException("no such provider: " + paramString2); 
      return getInstanceRSA(provider);
    } 
    GetInstance.Instance instance = GetInstance.getInstance("Signature", SignatureSpi.class, paramString1, paramString2);
    return getInstance(instance, paramString1);
  }
  
  public static Signature getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramString.equalsIgnoreCase("NONEwithRSA")) {
      if (paramProvider == null)
        throw new IllegalArgumentException("missing provider"); 
      return getInstanceRSA(paramProvider);
    } 
    GetInstance.Instance instance = GetInstance.getInstance("Signature", SignatureSpi.class, paramString, paramProvider);
    return getInstance(instance, paramString);
  }
  
  private static Signature getInstanceRSA(Provider paramProvider) throws NoSuchAlgorithmException {
    Provider.Service service = paramProvider.getService("Signature", "NONEwithRSA");
    if (service != null) {
      GetInstance.Instance instance = GetInstance.getInstance(service, SignatureSpi.class);
      return getInstance(instance, "NONEwithRSA");
    } 
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", paramProvider);
      return new Delegate(new CipherAdapter(cipher), "NONEwithRSA");
    } catch (GeneralSecurityException generalSecurityException) {
      throw new NoSuchAlgorithmException("no such algorithm: NONEwithRSA for provider " + paramProvider
          .getName(), generalSecurityException);
    } 
  }
  
  public final Provider getProvider() {
    chooseFirstProvider();
    return this.provider;
  }
  
  void chooseFirstProvider() {}
  
  public final void initVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    engineInitVerify(paramPublicKey);
    this.state = 3;
    if (!skipDebug && pdebug != null)
      pdebug.println("Signature." + this.algorithm + " verification algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void initVerify(Certificate paramCertificate) throws InvalidKeyException {
    if (paramCertificate instanceof X509Certificate) {
      X509Certificate x509Certificate = (X509Certificate)paramCertificate;
      Set<String> set = x509Certificate.getCriticalExtensionOIDs();
      if (set != null && !set.isEmpty() && set
        .contains("2.5.29.15")) {
        boolean[] arrayOfBoolean = x509Certificate.getKeyUsage();
        if (arrayOfBoolean != null && !arrayOfBoolean[0])
          throw new InvalidKeyException("Wrong key usage"); 
      } 
    } 
    PublicKey publicKey = paramCertificate.getPublicKey();
    engineInitVerify(publicKey);
    this.state = 3;
    if (!skipDebug && pdebug != null)
      pdebug.println("Signature." + this.algorithm + " verification algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void initSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    engineInitSign(paramPrivateKey);
    this.state = 2;
    if (!skipDebug && pdebug != null)
      pdebug.println("Signature." + this.algorithm + " signing algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void initSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    engineInitSign(paramPrivateKey, paramSecureRandom);
    this.state = 2;
    if (!skipDebug && pdebug != null)
      pdebug.println("Signature." + this.algorithm + " signing algorithm from: " + this.provider
          .getName()); 
  }
  
  public final byte[] sign() throws SignatureException {
    if (this.state == 2)
      return engineSign(); 
    throw new SignatureException("object not initialized for signing");
  }
  
  public final int sign(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("No output buffer given"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("offset or len is less than 0"); 
    if (paramArrayOfbyte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("Output buffer too small for specified offset and length"); 
    if (this.state != 2)
      throw new SignatureException("object not initialized for signing"); 
    return engineSign(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public final boolean verify(byte[] paramArrayOfbyte) throws SignatureException {
    if (this.state == 3)
      return engineVerify(paramArrayOfbyte); 
    throw new SignatureException("object not initialized for verification");
  }
  
  public final boolean verify(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    if (this.state == 3) {
      if (paramArrayOfbyte == null)
        throw new IllegalArgumentException("signature is null"); 
      if (paramInt1 < 0 || paramInt2 < 0)
        throw new IllegalArgumentException("offset or length is less than 0"); 
      if (paramArrayOfbyte.length - paramInt1 < paramInt2)
        throw new IllegalArgumentException("signature too small for specified offset and length"); 
      return engineVerify(paramArrayOfbyte, paramInt1, paramInt2);
    } 
    throw new SignatureException("object not initialized for verification");
  }
  
  public final void update(byte paramByte) throws SignatureException {
    if (this.state == 3 || this.state == 2) {
      engineUpdate(paramByte);
    } else {
      throw new SignatureException("object not initialized for signature or verification");
    } 
  }
  
  public final void update(byte[] paramArrayOfbyte) throws SignatureException {
    update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    if (this.state == 2 || this.state == 3) {
      if (paramArrayOfbyte == null)
        throw new IllegalArgumentException("data is null"); 
      if (paramInt1 < 0 || paramInt2 < 0)
        throw new IllegalArgumentException("off or len is less than 0"); 
      if (paramArrayOfbyte.length - paramInt1 < paramInt2)
        throw new IllegalArgumentException("data too small for specified offset and length"); 
      engineUpdate(paramArrayOfbyte, paramInt1, paramInt2);
    } else {
      throw new SignatureException("object not initialized for signature or verification");
    } 
  }
  
  public final void update(ByteBuffer paramByteBuffer) throws SignatureException {
    if (this.state != 2 && this.state != 3)
      throw new SignatureException("object not initialized for signature or verification"); 
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    engineUpdate(paramByteBuffer);
  }
  
  public final String getAlgorithm() {
    return this.algorithm;
  }
  
  public String toString() {
    String str = "";
    switch (this.state) {
      case 0:
        str = "<not initialized>";
        break;
      case 3:
        str = "<initialized for verifying>";
        break;
      case 2:
        str = "<initialized for signing>";
        break;
    } 
    return "Signature object: " + getAlgorithm() + str;
  }
  
  @Deprecated
  public final void setParameter(String paramString, Object paramObject) throws InvalidParameterException {
    engineSetParameter(paramString, paramObject);
  }
  
  public final void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    engineSetParameter(paramAlgorithmParameterSpec);
  }
  
  public final AlgorithmParameters getParameters() {
    return engineGetParameters();
  }
  
  @Deprecated
  public final Object getParameter(String paramString) throws InvalidParameterException {
    return engineGetParameter(paramString);
  }
  
  public Object clone() throws CloneNotSupportedException {
    if (this instanceof Cloneable)
      return super.clone(); 
    throw new CloneNotSupportedException();
  }
  
  private static class Signature {}
  
  private static class Delegate extends Signature {
    private SignatureSpi sigSpi;
    
    private final Object lock;
    
    private Provider.Service firstService;
    
    private Iterator<Provider.Service> serviceIterator;
    
    Delegate(SignatureSpi param1SignatureSpi, String param1String) {
      super(param1String);
      this.sigSpi = param1SignatureSpi;
      this.lock = null;
    }
    
    Delegate(Provider.Service param1Service, Iterator<Provider.Service> param1Iterator, String param1String) {
      super(param1String);
      this.firstService = param1Service;
      this.serviceIterator = param1Iterator;
      this.lock = new Object();
    }
    
    public Object clone() throws CloneNotSupportedException {
      chooseFirstProvider();
      if (this.sigSpi instanceof Cloneable) {
        SignatureSpi signatureSpi = (SignatureSpi)this.sigSpi.clone();
        Delegate delegate = new Delegate(signatureSpi, this.algorithm);
        delegate.provider = this.provider;
        return delegate;
      } 
      throw new CloneNotSupportedException();
    }
    
    private static SignatureSpi newInstance(Provider.Service param1Service) throws NoSuchAlgorithmException {
      if (param1Service.getType().equals("Cipher"))
        try {
          Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", param1Service.getProvider());
          return new Signature.CipherAdapter(cipher);
        } catch (NoSuchPaddingException noSuchPaddingException) {
          throw new NoSuchAlgorithmException(noSuchPaddingException);
        }  
      Object object = param1Service.newInstance(null);
      if (!(object instanceof SignatureSpi))
        throw new NoSuchAlgorithmException("Not a SignatureSpi: " + object
            .getClass().getName()); 
      return (SignatureSpi)object;
    }
    
    private static int warnCount = 10;
    
    private static final int I_PUB = 1;
    
    private static final int I_PRIV = 2;
    
    private static final int I_PRIV_SR = 3;
    
    void chooseFirstProvider() {
      if (this.sigSpi != null)
        return; 
      synchronized (this.lock) {
        if (this.sigSpi != null)
          return; 
        if (Signature.debug != null) {
          int i = --warnCount;
          if (i >= 0) {
            Signature.debug.println("Signature.init() not first method called, disabling delayed provider selection");
            if (i == 0)
              Signature.debug.println("Further warnings of this type will be suppressed"); 
            (new Exception("Call trace")).printStackTrace();
          } 
        } 
        NoSuchAlgorithmException noSuchAlgorithmException = null;
        while (this.firstService != null || this.serviceIterator.hasNext()) {
          Provider.Service service;
          if (this.firstService != null) {
            service = this.firstService;
            this.firstService = null;
          } else {
            service = this.serviceIterator.next();
          } 
          if (!Signature.isSpi(service))
            continue; 
          try {
            this.sigSpi = newInstance(service);
            this.provider = service.getProvider();
            this.firstService = null;
            this.serviceIterator = null;
            return;
          } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
            noSuchAlgorithmException = noSuchAlgorithmException1;
          } 
        } 
        ProviderException providerException = new ProviderException("Could not construct SignatureSpi instance");
        if (noSuchAlgorithmException != null)
          providerException.initCause(noSuchAlgorithmException); 
        throw providerException;
      } 
    }
    
    private void chooseProvider(int param1Int, Key param1Key, SecureRandom param1SecureRandom) throws InvalidKeyException {
      synchronized (this.lock) {
        if (this.sigSpi != null) {
          init(this.sigSpi, param1Int, param1Key, param1SecureRandom);
          return;
        } 
        Exception exception = null;
        while (this.firstService != null || this.serviceIterator.hasNext()) {
          Provider.Service service;
          if (this.firstService != null) {
            service = this.firstService;
            this.firstService = null;
          } else {
            service = this.serviceIterator.next();
          } 
          if (!service.supportsParameter(param1Key))
            continue; 
          if (!Signature.isSpi(service))
            continue; 
          try {
            SignatureSpi signatureSpi = newInstance(service);
            init(signatureSpi, param1Int, param1Key, param1SecureRandom);
            this.provider = service.getProvider();
            this.sigSpi = signatureSpi;
            this.firstService = null;
            this.serviceIterator = null;
            return;
          } catch (Exception exception1) {
            if (exception == null)
              exception = exception1; 
          } 
        } 
        if (exception instanceof InvalidKeyException)
          throw (InvalidKeyException)exception; 
        if (exception instanceof RuntimeException)
          throw (RuntimeException)exception; 
        String str = (param1Key != null) ? param1Key.getClass().getName() : "(null)";
        throw new InvalidKeyException("No installed provider supports this key: " + str, exception);
      } 
    }
    
    private void init(SignatureSpi param1SignatureSpi, int param1Int, Key param1Key, SecureRandom param1SecureRandom) throws InvalidKeyException {
      switch (param1Int) {
        case 1:
          param1SignatureSpi.engineInitVerify((PublicKey)param1Key);
          return;
        case 2:
          param1SignatureSpi.engineInitSign((PrivateKey)param1Key);
          return;
        case 3:
          param1SignatureSpi.engineInitSign((PrivateKey)param1Key, param1SecureRandom);
          return;
      } 
      throw new AssertionError("Internal error: " + param1Int);
    }
    
    protected void engineInitVerify(PublicKey param1PublicKey) throws InvalidKeyException {
      if (this.sigSpi != null) {
        this.sigSpi.engineInitVerify(param1PublicKey);
      } else {
        chooseProvider(1, param1PublicKey, null);
      } 
    }
    
    protected void engineInitSign(PrivateKey param1PrivateKey) throws InvalidKeyException {
      if (this.sigSpi != null) {
        this.sigSpi.engineInitSign(param1PrivateKey);
      } else {
        chooseProvider(2, param1PrivateKey, null);
      } 
    }
    
    protected void engineInitSign(PrivateKey param1PrivateKey, SecureRandom param1SecureRandom) throws InvalidKeyException {
      if (this.sigSpi != null) {
        this.sigSpi.engineInitSign(param1PrivateKey, param1SecureRandom);
      } else {
        chooseProvider(3, param1PrivateKey, param1SecureRandom);
      } 
    }
    
    protected void engineUpdate(byte param1Byte) throws SignatureException {
      chooseFirstProvider();
      this.sigSpi.engineUpdate(param1Byte);
    }
    
    protected void engineUpdate(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws SignatureException {
      chooseFirstProvider();
      this.sigSpi.engineUpdate(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    protected void engineUpdate(ByteBuffer param1ByteBuffer) {
      chooseFirstProvider();
      this.sigSpi.engineUpdate(param1ByteBuffer);
    }
    
    protected byte[] engineSign() throws SignatureException {
      chooseFirstProvider();
      return this.sigSpi.engineSign();
    }
    
    protected int engineSign(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws SignatureException {
      chooseFirstProvider();
      return this.sigSpi.engineSign(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    protected boolean engineVerify(byte[] param1ArrayOfbyte) throws SignatureException {
      chooseFirstProvider();
      return this.sigSpi.engineVerify(param1ArrayOfbyte);
    }
    
    protected boolean engineVerify(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws SignatureException {
      chooseFirstProvider();
      return this.sigSpi.engineVerify(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    protected void engineSetParameter(String param1String, Object param1Object) throws InvalidParameterException {
      chooseFirstProvider();
      this.sigSpi.engineSetParameter(param1String, param1Object);
    }
    
    protected void engineSetParameter(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
      chooseFirstProvider();
      this.sigSpi.engineSetParameter(param1AlgorithmParameterSpec);
    }
    
    protected Object engineGetParameter(String param1String) throws InvalidParameterException {
      chooseFirstProvider();
      return this.sigSpi.engineGetParameter(param1String);
    }
    
    protected AlgorithmParameters engineGetParameters() {
      chooseFirstProvider();
      return this.sigSpi.engineGetParameters();
    }
  }
}
