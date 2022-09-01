package sun.security.ec;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.Provider;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import sun.security.jca.JCAUtil;
import sun.security.util.ECUtil;

public final class ECKeyPairGenerator extends KeyPairGeneratorSpi {
  private static final int KEY_SIZE_MIN = 112;
  
  private static final int KEY_SIZE_MAX = 571;
  
  private static final int KEY_SIZE_DEFAULT = 256;
  
  private SecureRandom random;
  
  private int keySize;
  
  private AlgorithmParameterSpec params = null;
  
  public ECKeyPairGenerator() {
    initialize(256, (SecureRandom)null);
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    checkKeySize(paramInt);
    this.params = ECUtil.getECParameterSpec((Provider)null, paramInt);
    if (this.params == null)
      throw new InvalidParameterException("No EC parameters available for key size " + paramInt + " bits"); 
    this.random = paramSecureRandom;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec instanceof ECParameterSpec) {
      this.params = ECUtil.getECParameterSpec((Provider)null, (ECParameterSpec)paramAlgorithmParameterSpec);
      if (this.params == null)
        throw new InvalidAlgorithmParameterException("Unsupported curve: " + paramAlgorithmParameterSpec); 
    } else if (paramAlgorithmParameterSpec instanceof ECGenParameterSpec) {
      String str = ((ECGenParameterSpec)paramAlgorithmParameterSpec).getName();
      this.params = ECUtil.getECParameterSpec((Provider)null, str);
      if (this.params == null)
        throw new InvalidAlgorithmParameterException("Unknown curve name: " + str); 
    } else {
      throw new InvalidAlgorithmParameterException("ECParameterSpec or ECGenParameterSpec required for EC");
    } 
    this
      .keySize = ((ECParameterSpec)this.params).getCurve().getField().getFieldSize();
    this.random = paramSecureRandom;
  }
  
  public KeyPair generateKeyPair() {
    byte[] arrayOfByte1 = ECUtil.encodeECParameterSpec(null, (ECParameterSpec)this.params);
    byte[] arrayOfByte2 = new byte[((this.keySize + 7 >> 3) + 1) * 2];
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    this.random.nextBytes(arrayOfByte2);
    try {
      Object[] arrayOfObject = generateECKeyPair(this.keySize, arrayOfByte1, arrayOfByte2);
      BigInteger bigInteger = new BigInteger(1, (byte[])arrayOfObject[0]);
      ECPrivateKeyImpl eCPrivateKeyImpl = new ECPrivateKeyImpl(bigInteger, (ECParameterSpec)this.params);
      ECPoint eCPoint = ECUtil.decodePoint((byte[])arrayOfObject[1], ((ECParameterSpec)this.params)
          .getCurve());
      ECPublicKeyImpl eCPublicKeyImpl = new ECPublicKeyImpl(eCPoint, (ECParameterSpec)this.params);
      return new KeyPair(eCPublicKeyImpl, eCPrivateKeyImpl);
    } catch (Exception exception) {
      throw new ProviderException(exception);
    } 
  }
  
  private void checkKeySize(int paramInt) throws InvalidParameterException {
    if (paramInt < 112)
      throw new InvalidParameterException("Key size must be at least 112 bits"); 
    if (paramInt > 571)
      throw new InvalidParameterException("Key size must be at most 571 bits"); 
    this.keySize = paramInt;
  }
  
  private static native Object[] generateECKeyPair(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws GeneralSecurityException;
}
