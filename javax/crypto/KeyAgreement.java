package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.util.Debug;

public class KeyAgreement {
  private static final Debug debug = Debug.getInstance("jca", "KeyAgreement");
  
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("keyagreement"));
  
  private Provider provider;
  
  private KeyAgreementSpi spi;
  
  private final String algorithm;
  
  private Provider.Service firstService;
  
  private Iterator<Provider.Service> serviceIterator;
  
  private final Object lock;
  
  protected KeyAgreement(KeyAgreementSpi paramKeyAgreementSpi, Provider paramProvider, String paramString) {
    this.spi = paramKeyAgreementSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
    this.lock = null;
  }
  
  private KeyAgreement(Provider.Service paramService, Iterator<Provider.Service> paramIterator, String paramString) {
    this.firstService = paramService;
    this.serviceIterator = paramIterator;
    this.algorithm = paramString;
    this.lock = new Object();
  }
  
  public final String getAlgorithm() {
    return this.algorithm;
  }
  
  public static final KeyAgreement getInstance(String paramString) throws NoSuchAlgorithmException {
    List<Provider.Service> list = GetInstance.getServices("KeyAgreement", paramString);
    Iterator<Provider.Service> iterator = list.iterator();
    while (iterator.hasNext()) {
      Provider.Service service = iterator.next();
      if (!JceSecurity.canUseProvider(service.getProvider()))
        continue; 
      return new KeyAgreement(service, iterator, paramString);
    } 
    throw new NoSuchAlgorithmException("Algorithm " + paramString + " not available");
  }
  
  public static final KeyAgreement getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = JceSecurity.getInstance("KeyAgreement", KeyAgreementSpi.class, paramString1, paramString2);
    return new KeyAgreement((KeyAgreementSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static final KeyAgreement getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = JceSecurity.getInstance("KeyAgreement", KeyAgreementSpi.class, paramString, paramProvider);
    return new KeyAgreement((KeyAgreementSpi)instance.impl, instance.provider, paramString);
  }
  
  private static int warnCount = 10;
  
  private static final int I_NO_PARAMS = 1;
  
  private static final int I_PARAMS = 2;
  
  void chooseFirstProvider() {
    if (this.spi != null)
      return; 
    synchronized (this.lock) {
      if (this.spi != null)
        return; 
      if (debug != null) {
        int i = --warnCount;
        if (i >= 0) {
          debug.println("KeyAgreement.init() not first method called, disabling delayed provider selection");
          if (i == 0)
            debug.println("Further warnings of this type will be suppressed"); 
          (new Exception("Call trace")).printStackTrace();
        } 
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
        if (!JceSecurity.canUseProvider(service.getProvider()))
          continue; 
        try {
          Object object = service.newInstance(null);
          if (!(object instanceof KeyAgreementSpi))
            continue; 
          this.spi = (KeyAgreementSpi)object;
          this.provider = service.getProvider();
          this.firstService = null;
          this.serviceIterator = null;
          return;
        } catch (Exception exception1) {
          exception = exception1;
        } 
      } 
      ProviderException providerException = new ProviderException("Could not construct KeyAgreementSpi instance");
      if (exception != null)
        providerException.initCause(exception); 
      throw providerException;
    } 
  }
  
  private void implInit(KeyAgreementSpi paramKeyAgreementSpi, int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramInt == 1) {
      paramKeyAgreementSpi.engineInit(paramKey, paramSecureRandom);
    } else {
      paramKeyAgreementSpi.engineInit(paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
    } 
  }
  
  private void chooseProvider(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    synchronized (this.lock) {
      if (this.spi != null) {
        implInit(this.spi, paramInt, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
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
        if (!service.supportsParameter(paramKey))
          continue; 
        if (!JceSecurity.canUseProvider(service.getProvider()))
          continue; 
        try {
          KeyAgreementSpi keyAgreementSpi = (KeyAgreementSpi)service.newInstance(null);
          implInit(keyAgreementSpi, paramInt, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
          this.provider = service.getProvider();
          this.spi = keyAgreementSpi;
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
  
  public final void init(Key paramKey) throws InvalidKeyException {
    init(paramKey, JceSecurity.RANDOM);
  }
  
  public final void init(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    if (this.spi != null) {
      this.spi.engineInit(paramKey, paramSecureRandom);
    } else {
      try {
        chooseProvider(1, paramKey, null, paramSecureRandom);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new InvalidKeyException(invalidAlgorithmParameterException);
      } 
    } 
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyAgreement." + this.algorithm + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final void init(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    init(paramKey, paramAlgorithmParameterSpec, JceSecurity.RANDOM);
  }
  
  public final void init(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (this.spi != null) {
      this.spi.engineInit(paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
    } else {
      chooseProvider(2, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
    } 
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyAgreement." + this.algorithm + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final Key doPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    chooseFirstProvider();
    return this.spi.engineDoPhase(paramKey, paramBoolean);
  }
  
  public final byte[] generateSecret() throws IllegalStateException {
    chooseFirstProvider();
    return this.spi.engineGenerateSecret();
  }
  
  public final int generateSecret(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, ShortBufferException {
    chooseFirstProvider();
    return this.spi.engineGenerateSecret(paramArrayOfbyte, paramInt);
  }
  
  public final SecretKey generateSecret(String paramString) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
    chooseFirstProvider();
    return this.spi.engineGenerateSecret(paramString);
  }
}
