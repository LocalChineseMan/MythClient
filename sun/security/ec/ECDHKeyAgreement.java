package sun.security.ec;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import sun.security.util.ECUtil;

public final class ECDHKeyAgreement extends KeyAgreementSpi {
  private ECPrivateKey privateKey;
  
  private byte[] publicValue;
  
  private int secretLen;
  
  protected void engineInit(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    if (!(paramKey instanceof java.security.PrivateKey))
      throw new InvalidKeyException("Key must be instance of PrivateKey"); 
    this.privateKey = (ECPrivateKey)ECKeyFactory.toECKey(paramKey);
    this.publicValue = null;
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec != null)
      throw new InvalidAlgorithmParameterException("Parameters not supported"); 
    engineInit(paramKey, paramSecureRandom);
  }
  
  protected Key engineDoPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    if (this.privateKey == null)
      throw new IllegalStateException("Not initialized"); 
    if (this.publicValue != null)
      throw new IllegalStateException("Phase already executed"); 
    if (!paramBoolean)
      throw new IllegalStateException("Only two party agreement supported, lastPhase must be true"); 
    if (!(paramKey instanceof ECPublicKey))
      throw new InvalidKeyException("Key must be a PublicKey with algorithm EC"); 
    ECPublicKey eCPublicKey = (ECPublicKey)paramKey;
    ECParameterSpec eCParameterSpec = eCPublicKey.getParams();
    if (eCPublicKey instanceof ECPublicKeyImpl) {
      this.publicValue = ((ECPublicKeyImpl)eCPublicKey).getEncodedPublicValue();
    } else {
      this
        .publicValue = ECUtil.encodePoint(eCPublicKey.getW(), eCParameterSpec.getCurve());
    } 
    int i = eCParameterSpec.getCurve().getField().getFieldSize();
    this.secretLen = i + 7 >> 3;
    return null;
  }
  
  protected byte[] engineGenerateSecret() throws IllegalStateException {
    if (this.privateKey == null || this.publicValue == null)
      throw new IllegalStateException("Not initialized correctly"); 
    byte[] arrayOfByte1 = this.privateKey.getS().toByteArray();
    byte[] arrayOfByte2 = ECUtil.encodeECParameterSpec(null, this.privateKey.getParams());
    try {
      return deriveKey(arrayOfByte1, this.publicValue, arrayOfByte2);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ProviderException("Could not derive key", generalSecurityException);
    } 
  }
  
  protected int engineGenerateSecret(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, ShortBufferException {
    if (paramInt + this.secretLen > paramArrayOfbyte.length)
      throw new ShortBufferException("Need " + this.secretLen + " bytes, only " + (paramArrayOfbyte.length - paramInt) + " available"); 
    byte[] arrayOfByte = engineGenerateSecret();
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected SecretKey engineGenerateSecret(String paramString) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
    if (paramString == null)
      throw new NoSuchAlgorithmException("Algorithm must not be null"); 
    if (!paramString.equals("TlsPremasterSecret"))
      throw new NoSuchAlgorithmException("Only supported for algorithm TlsPremasterSecret"); 
    return new SecretKeySpec(engineGenerateSecret(), "TlsPremasterSecret");
  }
  
  private static native byte[] deriveKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) throws GeneralSecurityException;
}
