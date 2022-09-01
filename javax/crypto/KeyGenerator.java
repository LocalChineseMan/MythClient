package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.util.Debug;

public class KeyGenerator {
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("keygenerator"));
  
  private static final int I_NONE = 1;
  
  private static final int I_RANDOM = 2;
  
  private static final int I_PARAMS = 3;
  
  private static final int I_SIZE = 4;
  
  private Provider provider;
  
  private volatile KeyGeneratorSpi spi;
  
  private final String algorithm;
  
  private final Object lock = new Object();
  
  private Iterator<Provider.Service> serviceIterator;
  
  private int initType;
  
  private int initKeySize;
  
  private AlgorithmParameterSpec initParams;
  
  private SecureRandom initRandom;
  
  protected KeyGenerator(KeyGeneratorSpi paramKeyGeneratorSpi, Provider paramProvider, String paramString) {
    this.spi = paramKeyGeneratorSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyGenerator." + paramString + " algorithm from: " + this.provider
          .getName()); 
  }
  
  private KeyGenerator(String paramString) throws NoSuchAlgorithmException {
    this.algorithm = paramString;
    List<Provider.Service> list = GetInstance.getServices("KeyGenerator", paramString);
    this.serviceIterator = list.iterator();
    this.initType = 1;
    if (nextSpi(null, false) == null)
      throw new NoSuchAlgorithmException(paramString + " KeyGenerator not available"); 
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyGenerator." + paramString + " algorithm from: " + this.provider
          .getName()); 
  }
  
  public final String getAlgorithm() {
    return this.algorithm;
  }
  
  public static final KeyGenerator getInstance(String paramString) throws NoSuchAlgorithmException {
    return new KeyGenerator(paramString);
  }
  
  public static final KeyGenerator getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = JceSecurity.getInstance("KeyGenerator", KeyGeneratorSpi.class, paramString1, paramString2);
    return new KeyGenerator((KeyGeneratorSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static final KeyGenerator getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = JceSecurity.getInstance("KeyGenerator", KeyGeneratorSpi.class, paramString, paramProvider);
    return new KeyGenerator((KeyGeneratorSpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() {
    synchronized (this.lock) {
      disableFailover();
      return this.provider;
    } 
  }
  
  private KeyGeneratorSpi nextSpi(KeyGeneratorSpi paramKeyGeneratorSpi, boolean paramBoolean) {
    synchronized (this.lock) {
      if (paramKeyGeneratorSpi != null && paramKeyGeneratorSpi != this.spi)
        return this.spi; 
      if (this.serviceIterator == null)
        return null; 
      while (this.serviceIterator.hasNext()) {
        Provider.Service service = this.serviceIterator.next();
        if (!JceSecurity.canUseProvider(service.getProvider()))
          continue; 
        try {
          Object object = service.newInstance(null);
          if (!(object instanceof KeyGeneratorSpi))
            continue; 
          KeyGeneratorSpi keyGeneratorSpi = (KeyGeneratorSpi)object;
          if (paramBoolean)
            if (this.initType == 4) {
              keyGeneratorSpi.engineInit(this.initKeySize, this.initRandom);
            } else if (this.initType == 3) {
              keyGeneratorSpi.engineInit(this.initParams, this.initRandom);
            } else if (this.initType == 2) {
              keyGeneratorSpi.engineInit(this.initRandom);
            } else if (this.initType != 1) {
              throw new AssertionError("KeyGenerator initType: " + this.initType);
            }  
          this.provider = service.getProvider();
          this.spi = keyGeneratorSpi;
          return keyGeneratorSpi;
        } catch (Exception exception) {}
      } 
      disableFailover();
      return null;
    } 
  }
  
  void disableFailover() {
    this.serviceIterator = null;
    this.initType = 0;
    this.initParams = null;
    this.initRandom = null;
  }
  
  public final void init(SecureRandom paramSecureRandom) {
    if (this.serviceIterator == null) {
      this.spi.engineInit(paramSecureRandom);
      return;
    } 
    RuntimeException runtimeException = null;
    KeyGeneratorSpi keyGeneratorSpi = this.spi;
    while (true) {
      try {
        keyGeneratorSpi.engineInit(paramSecureRandom);
        this.initType = 2;
        this.initKeySize = 0;
        this.initParams = null;
        this.initRandom = paramSecureRandom;
        return;
      } catch (RuntimeException runtimeException1) {
        if (runtimeException == null)
          runtimeException = runtimeException1; 
        keyGeneratorSpi = nextSpi(keyGeneratorSpi, false);
        if (keyGeneratorSpi == null)
          throw runtimeException; 
      } 
    } 
  }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    init(paramAlgorithmParameterSpec, JceSecurity.RANDOM);
  }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (this.serviceIterator == null) {
      this.spi.engineInit(paramAlgorithmParameterSpec, paramSecureRandom);
      return;
    } 
    Exception exception = null;
    KeyGeneratorSpi keyGeneratorSpi = this.spi;
    while (true) {
      try {
        keyGeneratorSpi.engineInit(paramAlgorithmParameterSpec, paramSecureRandom);
        this.initType = 3;
        this.initKeySize = 0;
        this.initParams = paramAlgorithmParameterSpec;
        this.initRandom = paramSecureRandom;
        return;
      } catch (Exception exception1) {
        if (exception == null)
          exception = exception1; 
        keyGeneratorSpi = nextSpi(keyGeneratorSpi, false);
        if (keyGeneratorSpi == null) {
          if (exception instanceof InvalidAlgorithmParameterException)
            throw (InvalidAlgorithmParameterException)exception; 
          if (exception instanceof RuntimeException)
            throw (RuntimeException)exception; 
          throw new InvalidAlgorithmParameterException("init() failed", exception);
        } 
      } 
    } 
  }
  
  public final void init(int paramInt) {
    init(paramInt, JceSecurity.RANDOM);
  }
  
  public final void init(int paramInt, SecureRandom paramSecureRandom) {
    if (this.serviceIterator == null) {
      this.spi.engineInit(paramInt, paramSecureRandom);
      return;
    } 
    RuntimeException runtimeException = null;
    KeyGeneratorSpi keyGeneratorSpi = this.spi;
    while (true) {
      try {
        keyGeneratorSpi.engineInit(paramInt, paramSecureRandom);
        this.initType = 4;
        this.initKeySize = paramInt;
        this.initParams = null;
        this.initRandom = paramSecureRandom;
        return;
      } catch (RuntimeException runtimeException1) {
        if (runtimeException == null)
          runtimeException = runtimeException1; 
        keyGeneratorSpi = nextSpi(keyGeneratorSpi, false);
        if (keyGeneratorSpi == null)
          throw runtimeException; 
      } 
    } 
  }
  
  public final SecretKey generateKey() {
    if (this.serviceIterator == null)
      return this.spi.engineGenerateKey(); 
    RuntimeException runtimeException = null;
    KeyGeneratorSpi keyGeneratorSpi = this.spi;
    while (true) {
      try {
        return keyGeneratorSpi.engineGenerateKey();
      } catch (RuntimeException runtimeException1) {
        if (runtimeException == null)
          runtimeException = runtimeException1; 
        keyGeneratorSpi = nextSpi(keyGeneratorSpi, true);
        if (keyGeneratorSpi == null)
          throw runtimeException; 
      } 
    } 
  }
}
