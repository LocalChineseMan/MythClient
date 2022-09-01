package java.security;

import java.util.Random;
import java.util.regex.Matcher;
import sun.security.jca.GetInstance;
import sun.security.jca.Providers;
import sun.security.util.Debug;

public class SecureRandom extends Random {
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  
  private static final boolean skipDebug = (
    Debug.isOn("engine=") && !Debug.isOn("securerandom"));
  
  private Provider provider = null;
  
  private SecureRandomSpi secureRandomSpi = null;
  
  private String algorithm;
  
  private static volatile SecureRandom seedGenerator = null;
  
  static final long serialVersionUID = 4940670005562187L;
  
  private byte[] state;
  
  private MessageDigest digest;
  
  private byte[] randomBytes;
  
  private int randomBytesUsed;
  
  private long counter;
  
  public SecureRandom() {
    super(0L);
    this.digest = null;
    getDefaultPRNG(false, null);
  }
  
  public SecureRandom(byte[] paramArrayOfbyte) {
    super(0L);
    this.digest = null;
    getDefaultPRNG(true, paramArrayOfbyte);
  }
  
  private void getDefaultPRNG(boolean paramBoolean, byte[] paramArrayOfbyte) {
    String str = getPrngAlgorithm();
    if (str == null) {
      str = "SHA1PRNG";
      this.secureRandomSpi = new sun.security.provider.SecureRandom();
      this.provider = Providers.getSunProvider();
      if (paramBoolean)
        this.secureRandomSpi.engineSetSeed(paramArrayOfbyte); 
    } else {
      try {
        SecureRandom secureRandom = getInstance(str);
        this.secureRandomSpi = secureRandom.getSecureRandomSpi();
        this.provider = secureRandom.getProvider();
        if (paramBoolean)
          this.secureRandomSpi.engineSetSeed(paramArrayOfbyte); 
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new RuntimeException(noSuchAlgorithmException);
      } 
    } 
    if (getClass() == SecureRandom.class)
      this.algorithm = str; 
  }
  
  protected SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider) {
    this(paramSecureRandomSpi, paramProvider, null);
  }
  
  private SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider, String paramString) {
    super(0L);
    this.digest = null;
    this.secureRandomSpi = paramSecureRandomSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
    if (!skipDebug && pdebug != null)
      pdebug.println("SecureRandom." + paramString + " algorithm from: " + this.provider.getName()); 
  }
  
  public static SecureRandom getInstance(String paramString) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString);
  }
  
  public static SecureRandom getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString1, paramString2);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString1);
  }
  
  public static SecureRandom getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString, paramProvider);
    return new SecureRandom((SecureRandomSpi)instance.impl, instance.provider, paramString);
  }
  
  SecureRandomSpi getSecureRandomSpi() {
    return this.secureRandomSpi;
  }
  
  public final Provider getProvider() {
    return this.provider;
  }
  
  public String getAlgorithm() {
    return (this.algorithm != null) ? this.algorithm : "unknown";
  }
  
  public synchronized void setSeed(byte[] paramArrayOfbyte) {
    this.secureRandomSpi.engineSetSeed(paramArrayOfbyte);
  }
  
  public void setSeed(long paramLong) {
    if (paramLong != 0L)
      this.secureRandomSpi.engineSetSeed(longToByteArray(paramLong)); 
  }
  
  public synchronized void nextBytes(byte[] paramArrayOfbyte) {
    this.secureRandomSpi.engineNextBytes(paramArrayOfbyte);
  }
  
  protected final int next(int paramInt) {
    int i = (paramInt + 7) / 8;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    nextBytes(arrayOfByte);
    for (byte b = 0; b < i; b++)
      j = (j << 8) + (arrayOfByte[b] & 0xFF); 
    return j >>> i * 8 - paramInt;
  }
  
  public static byte[] getSeed(int paramInt) {
    if (seedGenerator == null)
      seedGenerator = new SecureRandom(); 
    return seedGenerator.generateSeed(paramInt);
  }
  
  public byte[] generateSeed(int paramInt) {
    return this.secureRandomSpi.engineGenerateSeed(paramInt);
  }
  
  private static byte[] longToByteArray(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = (byte)(int)paramLong;
      paramLong >>= 8L;
    } 
    return arrayOfByte;
  }
  
  private static String getPrngAlgorithm() {
    for (Provider provider : Providers.getProviderList().providers()) {
      for (Provider.Service service : provider.getServices()) {
        if (service.getType().equals("SecureRandom"))
          return service.getAlgorithm(); 
      } 
    } 
    return null;
  }
  
  public static SecureRandom getInstanceStrong() throws NoSuchAlgorithmException {
    String str1 = AccessController.<String>doPrivileged((PrivilegedAction<String>)new Object());
    if (str1 == null || str1.length() == 0)
      throw new NoSuchAlgorithmException("Null/empty securerandom.strongAlgorithms Security Property"); 
    String str2 = str1;
    while (str2 != null) {
      Matcher matcher;
      if ((matcher = StrongPatternHolder.access$000().matcher(str2)).matches()) {
        String str3 = matcher.group(1);
        String str4 = matcher.group(3);
        try {
          if (str4 == null)
            return getInstance(str3); 
          return getInstance(str3, str4);
        } catch (NoSuchAlgorithmException|NoSuchProviderException noSuchAlgorithmException) {
          str2 = matcher.group(5);
          continue;
        } 
      } 
      str2 = null;
    } 
    throw new NoSuchAlgorithmException("No strong SecureRandom impls available: " + str1);
  }
  
  private static final class SecureRandom {}
}
