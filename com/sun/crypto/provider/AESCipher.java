package com.sun.crypto.provider;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

abstract class AESCipher extends CipherSpi {
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  public static final class AESCipher {}
  
  static abstract class AESCipher {}
  
  public static final class General extends AESCipher {
    public General() {
      super(-1);
    }
  }
  
  static final void checkKeySize(Key paramKey, int paramInt) throws InvalidKeyException {
    if (paramInt != -1) {
      if (paramKey == null)
        throw new InvalidKeyException("The key must not be null"); 
      byte[] arrayOfByte = paramKey.getEncoded();
      if (arrayOfByte == null)
        throw new InvalidKeyException("Key encoding must not be null"); 
      if (arrayOfByte.length != paramInt)
        throw new InvalidKeyException("The key must be " + (paramInt * 8) + " bits"); 
    } 
  }
  
  private CipherCore core = null;
  
  private final int fixedKeySize;
  
  protected AESCipher(int paramInt) {
    this.core = new CipherCore(new AESCrypt(), 16);
    this.fixedKeySize = paramInt;
  }
  
  protected void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    this.core.setMode(paramString);
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    this.core.setPadding(paramString);
  }
  
  protected int engineGetBlockSize() {
    return 16;
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return this.core.getOutputSize(paramInt);
  }
  
  protected byte[] engineGetIV() {
    return this.core.getIV();
  }
  
  protected AlgorithmParameters engineGetParameters() {
    return this.core.getParameters("AES");
  }
  
  protected void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    checkKeySize(paramKey, this.fixedKeySize);
    this.core.init(paramInt, paramKey, paramSecureRandom);
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    checkKeySize(paramKey, this.fixedKeySize);
    this.core.init(paramInt, paramKey, paramAlgorithmParameterSpec, paramSecureRandom);
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    checkKeySize(paramKey, this.fixedKeySize);
    this.core.init(paramInt, paramKey, paramAlgorithmParameters, paramSecureRandom);
  }
  
  protected byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    return this.core.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    return this.core.update(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    return this.core.doFinal(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
    return this.core.doFinal(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  protected int engineGetKeySize(Key paramKey) throws InvalidKeyException {
    byte[] arrayOfByte = paramKey.getEncoded();
    if (!AESCrypt.isKeySizeValid(arrayOfByte.length))
      throw new InvalidKeyException("Invalid AES key length: " + arrayOfByte.length + " bytes"); 
    return arrayOfByte.length * 8;
  }
  
  protected byte[] engineWrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    return this.core.wrap(paramKey);
  }
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
    return this.core.unwrap(paramArrayOfbyte, paramString, paramInt);
  }
  
  protected void engineUpdateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.core.updateAAD(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected void engineUpdateAAD(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer != null) {
      int i = paramByteBuffer.limit() - paramByteBuffer.position();
      if (i != 0)
        if (paramByteBuffer.hasArray()) {
          int j = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
          this.core.updateAAD(paramByteBuffer.array(), j, i);
          paramByteBuffer.position(paramByteBuffer.limit());
        } else {
          byte[] arrayOfByte = new byte[i];
          paramByteBuffer.get(arrayOfByte);
          this.core.updateAAD(arrayOfByte, 0, i);
        }  
    } 
  }
}
