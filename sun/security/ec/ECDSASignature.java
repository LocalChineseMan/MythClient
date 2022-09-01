package sun.security.ec;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ECUtil;

abstract class ECDSASignature extends SignatureSpi {
  private final MessageDigest messageDigest;
  
  private SecureRandom random;
  
  private boolean needsReset;
  
  private ECPrivateKey privateKey;
  
  private ECPublicKey publicKey;
  
  ECDSASignature() {
    this.messageDigest = null;
  }
  
  ECDSASignature(String paramString) {
    try {
      this.messageDigest = MessageDigest.getInstance(paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new ProviderException(noSuchAlgorithmException);
    } 
    this.needsReset = false;
  }
  
  public static final class Raw extends ECDSASignature {
    private static final int RAW_ECDSA_MAX = 64;
    
    private final byte[] precomputedDigest;
    
    private int offset = 0;
    
    public Raw() {
      this.precomputedDigest = new byte[64];
    }
    
    protected void engineUpdate(byte param1Byte) throws SignatureException {
      if (this.offset >= this.precomputedDigest.length) {
        this.offset = 65;
        return;
      } 
      this.precomputedDigest[this.offset++] = param1Byte;
    }
    
    protected void engineUpdate(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws SignatureException {
      if (this.offset >= this.precomputedDigest.length) {
        this.offset = 65;
        return;
      } 
      System.arraycopy(param1ArrayOfbyte, param1Int1, this.precomputedDigest, this.offset, param1Int2);
      this.offset += param1Int2;
    }
    
    protected void engineUpdate(ByteBuffer param1ByteBuffer) {
      int i = param1ByteBuffer.remaining();
      if (i <= 0)
        return; 
      if (this.offset + i >= this.precomputedDigest.length) {
        this.offset = 65;
        return;
      } 
      param1ByteBuffer.get(this.precomputedDigest, this.offset, i);
      this.offset += i;
    }
    
    protected void resetDigest() {
      this.offset = 0;
    }
    
    protected byte[] getDigestValue() throws SignatureException {
      if (this.offset > 64)
        throw new SignatureException("Message digest is too long"); 
      byte[] arrayOfByte = new byte[this.offset];
      System.arraycopy(this.precomputedDigest, 0, arrayOfByte, 0, this.offset);
      this.offset = 0;
      return arrayOfByte;
    }
  }
  
  public static final class SHA1 extends ECDSASignature {
    public SHA1() {
      super("SHA1");
    }
  }
  
  public static final class ECDSASignature {}
  
  public static final class SHA256 extends ECDSASignature {
    public SHA256() {
      super("SHA-256");
    }
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    this.publicKey = (ECPublicKey)ECKeyFactory.toECKey(paramPublicKey);
    this.privateKey = null;
    resetDigest();
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    engineInitSign(paramPrivateKey, null);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.privateKey = (ECPrivateKey)ECKeyFactory.toECKey(paramPrivateKey);
    this.publicKey = null;
    this.random = paramSecureRandom;
    resetDigest();
  }
  
  protected void resetDigest() {
    if (this.needsReset) {
      if (this.messageDigest != null)
        this.messageDigest.reset(); 
      this.needsReset = false;
    } 
  }
  
  protected byte[] getDigestValue() throws SignatureException {
    this.needsReset = false;
    return this.messageDigest.digest();
  }
  
  protected void engineUpdate(byte paramByte) throws SignatureException {
    this.messageDigest.update(paramByte);
    this.needsReset = true;
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    this.messageDigest.update(paramArrayOfbyte, paramInt1, paramInt2);
    this.needsReset = true;
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) {
    int i = paramByteBuffer.remaining();
    if (i <= 0)
      return; 
    this.messageDigest.update(paramByteBuffer);
    this.needsReset = true;
  }
  
  protected byte[] engineSign() throws SignatureException {
    byte[] arrayOfByte1 = this.privateKey.getS().toByteArray();
    ECParameterSpec eCParameterSpec = this.privateKey.getParams();
    byte[] arrayOfByte2 = ECUtil.encodeECParameterSpec(null, eCParameterSpec);
    int i = eCParameterSpec.getCurve().getField().getFieldSize();
    byte[] arrayOfByte3 = new byte[((i + 7 >> 3) + 1) * 2];
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    this.random.nextBytes(arrayOfByte3);
    try {
      return encodeSignature(
          signDigest(getDigestValue(), arrayOfByte1, arrayOfByte2, arrayOfByte3));
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SignatureException("Could not sign data", generalSecurityException);
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    byte[] arrayOfByte1;
    ECParameterSpec eCParameterSpec = this.publicKey.getParams();
    byte[] arrayOfByte2 = ECUtil.encodeECParameterSpec(null, eCParameterSpec);
    if (this.publicKey instanceof ECPublicKeyImpl) {
      arrayOfByte1 = ((ECPublicKeyImpl)this.publicKey).getEncodedPublicValue();
    } else {
      arrayOfByte1 = ECUtil.encodePoint(this.publicKey.getW(), eCParameterSpec.getCurve());
    } 
    try {
      return verifySignedDigest(
          decodeSignature(paramArrayOfbyte), getDigestValue(), arrayOfByte1, arrayOfByte2);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SignatureException("Could not verify signature", generalSecurityException);
    } 
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject) throws InvalidParameterException {
    throw new UnsupportedOperationException("setParameter() not supported");
  }
  
  @Deprecated
  protected Object engineGetParameter(String paramString) throws InvalidParameterException {
    throw new UnsupportedOperationException("getParameter() not supported");
  }
  
  private byte[] encodeSignature(byte[] paramArrayOfbyte) throws SignatureException {
    try {
      int i = paramArrayOfbyte.length >> 1;
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, i);
      BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
      System.arraycopy(paramArrayOfbyte, i, arrayOfByte, 0, i);
      BigInteger bigInteger2 = new BigInteger(1, arrayOfByte);
      DerOutputStream derOutputStream = new DerOutputStream(paramArrayOfbyte.length + 10);
      derOutputStream.putInteger(bigInteger1);
      derOutputStream.putInteger(bigInteger2);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      return derValue.toByteArray();
    } catch (Exception exception) {
      throw new SignatureException("Could not encode signature", exception);
    } 
  }
  
  private byte[] decodeSignature(byte[] paramArrayOfbyte) throws SignatureException {
    try {
      DerInputStream derInputStream = new DerInputStream(paramArrayOfbyte);
      DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
      BigInteger bigInteger1 = arrayOfDerValue[0].getPositiveBigInteger();
      BigInteger bigInteger2 = arrayOfDerValue[1].getPositiveBigInteger();
      byte[] arrayOfByte1 = trimZeroes(bigInteger1.toByteArray());
      byte[] arrayOfByte2 = trimZeroes(bigInteger2.toByteArray());
      int i = Math.max(arrayOfByte1.length, arrayOfByte2.length);
      byte[] arrayOfByte3 = new byte[i << 1];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i - arrayOfByte1.length, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
      return arrayOfByte3;
    } catch (Exception exception) {
      throw new SignatureException("Could not decode signature", exception);
    } 
  }
  
  private static byte[] trimZeroes(byte[] paramArrayOfbyte) {
    byte b = 0;
    while (b < paramArrayOfbyte.length - 1 && paramArrayOfbyte[b] == 0)
      b++; 
    if (b == 0)
      return paramArrayOfbyte; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length - b];
    System.arraycopy(paramArrayOfbyte, b, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  private static native byte[] signDigest(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) throws GeneralSecurityException;
  
  private static native boolean verifySignedDigest(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) throws GeneralSecurityException;
  
  public static final class ECDSASignature {}
  
  public static final class ECDSASignature {}
}
