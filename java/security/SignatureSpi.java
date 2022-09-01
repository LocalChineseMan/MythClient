package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.jca.JCAUtil;

public abstract class SignatureSpi {
  protected SecureRandom appRandom = null;
  
  protected abstract void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException;
  
  protected abstract void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException;
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    this.appRandom = paramSecureRandom;
    engineInitSign(paramPrivateKey);
  }
  
  protected abstract void engineUpdate(byte paramByte) throws SignatureException;
  
  protected abstract void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException;
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) {
    if (!paramByteBuffer.hasRemaining())
      return; 
    try {
      if (paramByteBuffer.hasArray()) {
        byte[] arrayOfByte = paramByteBuffer.array();
        int i = paramByteBuffer.arrayOffset();
        int j = paramByteBuffer.position();
        int k = paramByteBuffer.limit();
        engineUpdate(arrayOfByte, i + j, k - j);
        paramByteBuffer.position(k);
      } else {
        int i = paramByteBuffer.remaining();
        byte[] arrayOfByte = new byte[JCAUtil.getTempArraySize(i)];
        while (i > 0) {
          int j = Math.min(i, arrayOfByte.length);
          paramByteBuffer.get(arrayOfByte, 0, j);
          engineUpdate(arrayOfByte, 0, j);
          i -= j;
        } 
      } 
    } catch (SignatureException signatureException) {
      throw new ProviderException("update() failed", signatureException);
    } 
  }
  
  protected abstract byte[] engineSign() throws SignatureException;
  
  protected int engineSign(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    byte[] arrayOfByte = engineSign();
    if (paramInt2 < arrayOfByte.length)
      throw new SignatureException("partial signatures not returned"); 
    if (paramArrayOfbyte.length - paramInt1 < arrayOfByte.length)
      throw new SignatureException("insufficient space in the output buffer to store the signature"); 
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt1, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected abstract boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException;
  
  protected boolean engineVerify(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    return engineVerify(arrayOfByte);
  }
  
  @Deprecated
  protected abstract void engineSetParameter(String paramString, Object paramObject) throws InvalidParameterException;
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    throw new UnsupportedOperationException();
  }
  
  protected AlgorithmParameters engineGetParameters() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  protected abstract Object engineGetParameter(String paramString) throws InvalidParameterException;
  
  public Object clone() throws CloneNotSupportedException {
    if (this instanceof Cloneable)
      return super.clone(); 
    throw new CloneNotSupportedException();
  }
}
