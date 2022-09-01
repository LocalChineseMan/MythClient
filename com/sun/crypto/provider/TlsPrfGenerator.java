package com.sun.crypto.provider;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.security.internal.spec.TlsPrfParameterSpec;

abstract class TlsPrfGenerator extends KeyGeneratorSpi {
  private static final byte[] B0 = new byte[0];
  
  static final byte[] LABEL_MASTER_SECRET = new byte[] { 
      109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 
      114, 101, 116 };
  
  static final byte[] LABEL_KEY_EXPANSION = new byte[] { 
      107, 101, 121, 32, 101, 120, 112, 97, 110, 115, 
      105, 111, 110 };
  
  static final byte[] LABEL_CLIENT_WRITE_KEY = new byte[] { 
      99, 108, 105, 101, 110, 116, 32, 119, 114, 105, 
      116, 101, 32, 107, 101, 121 };
  
  static final byte[] LABEL_SERVER_WRITE_KEY = new byte[] { 
      115, 101, 114, 118, 101, 114, 32, 119, 114, 105, 
      116, 101, 32, 107, 101, 121 };
  
  static final byte[] LABEL_IV_BLOCK = new byte[] { 73, 86, 32, 98, 108, 111, 99, 107 };
  
  private static final byte[] HMAC_ipad64 = genPad((byte)54, 64);
  
  private static final byte[] HMAC_ipad128 = genPad((byte)54, 128);
  
  private static final byte[] HMAC_opad64 = genPad((byte)92, 64);
  
  private static final byte[] HMAC_opad128 = genPad((byte)92, 128);
  
  static final byte[][] SSL3_CONST = genConst();
  
  private static final String MSG = "TlsPrfGenerator must be initialized using a TlsPrfParameterSpec";
  
  private TlsPrfParameterSpec spec;
  
  static byte[] genPad(byte paramByte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    Arrays.fill(arrayOfByte, paramByte);
    return arrayOfByte;
  }
  
  static byte[] concat(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = paramArrayOfbyte1.length;
    int j = paramArrayOfbyte2.length;
    byte[] arrayOfByte = new byte[i + j];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, i);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, i, j);
    return arrayOfByte;
  }
  
  private static byte[][] genConst() {
    byte b1 = 10;
    byte[][] arrayOfByte = new byte[b1][];
    for (byte b2 = 0; b2 < b1; b2++) {
      byte[] arrayOfByte1 = new byte[b2 + 1];
      Arrays.fill(arrayOfByte1, (byte)(65 + b2));
      arrayOfByte[b2] = arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  protected void engineInit(SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof TlsPrfParameterSpec))
      throw new InvalidAlgorithmParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec"); 
    this.spec = (TlsPrfParameterSpec)paramAlgorithmParameterSpec;
    SecretKey secretKey = this.spec.getSecret();
    if (secretKey != null && !"RAW".equals(secretKey.getFormat()))
      throw new InvalidAlgorithmParameterException("Key encoding format must be RAW"); 
  }
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
  }
  
  SecretKey engineGenerateKey0(boolean paramBoolean) {
    if (this.spec == null)
      throw new IllegalStateException("TlsPrfGenerator must be initialized"); 
    SecretKey secretKey = this.spec.getSecret();
    byte[] arrayOfByte = (secretKey == null) ? null : secretKey.getEncoded();
    try {
      byte[] arrayOfByte1 = this.spec.getLabel().getBytes("UTF8");
      int i = this.spec.getOutputLength();
      byte[] arrayOfByte2 = paramBoolean ? doTLS12PRF(arrayOfByte, arrayOfByte1, this.spec.getSeed(), i, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : doTLS10PRF(arrayOfByte, arrayOfByte1, this.spec.getSeed(), i);
      return new SecretKeySpec(arrayOfByte2, "TlsPrf");
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ProviderException("Could not generate PRF", generalSecurityException);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new ProviderException("Could not generate PRF", unsupportedEncodingException);
    } 
  }
  
  static byte[] doTLS12PRF(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt1, String paramString, int paramInt2, int paramInt3) throws NoSuchAlgorithmException, DigestException {
    if (paramString == null)
      throw new NoSuchAlgorithmException("Unspecified PRF algorithm"); 
    MessageDigest messageDigest = MessageDigest.getInstance(paramString);
    return doTLS12PRF(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramInt1, messageDigest, paramInt2, paramInt3);
  }
  
  static byte[] doTLS12PRF(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt1, MessageDigest paramMessageDigest, int paramInt2, int paramInt3) throws DigestException {
    byte[] arrayOfByte2, arrayOfByte3;
    if (paramArrayOfbyte1 == null)
      paramArrayOfbyte1 = B0; 
    if (paramArrayOfbyte1.length > paramInt3)
      paramArrayOfbyte1 = paramMessageDigest.digest(paramArrayOfbyte1); 
    byte[] arrayOfByte1 = new byte[paramInt1];
    switch (paramInt3) {
      case 64:
        arrayOfByte2 = (byte[])HMAC_ipad64.clone();
        arrayOfByte3 = (byte[])HMAC_opad64.clone();
        expand(paramMessageDigest, paramInt2, paramArrayOfbyte1, 0, paramArrayOfbyte1.length, paramArrayOfbyte2, paramArrayOfbyte3, arrayOfByte1, arrayOfByte2, arrayOfByte3);
        return arrayOfByte1;
      case 128:
        arrayOfByte2 = (byte[])HMAC_ipad128.clone();
        arrayOfByte3 = (byte[])HMAC_opad128.clone();
        expand(paramMessageDigest, paramInt2, paramArrayOfbyte1, 0, paramArrayOfbyte1.length, paramArrayOfbyte2, paramArrayOfbyte3, arrayOfByte1, arrayOfByte2, arrayOfByte3);
        return arrayOfByte1;
    } 
    throw new DigestException("Unexpected block size.");
  }
  
  static byte[] doTLS10PRF(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt) throws NoSuchAlgorithmException, DigestException {
    MessageDigest messageDigest1 = MessageDigest.getInstance("MD5");
    MessageDigest messageDigest2 = MessageDigest.getInstance("SHA1");
    return doTLS10PRF(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramInt, messageDigest1, messageDigest2);
  }
  
  static byte[] doTLS10PRF(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt, MessageDigest paramMessageDigest1, MessageDigest paramMessageDigest2) throws DigestException {
    if (paramArrayOfbyte1 == null)
      paramArrayOfbyte1 = B0; 
    int i = paramArrayOfbyte1.length >> 1;
    int j = i + (paramArrayOfbyte1.length & 0x1);
    byte[] arrayOfByte1 = paramArrayOfbyte1;
    int k = j;
    byte[] arrayOfByte2 = new byte[paramInt];
    if (j > 64) {
      paramMessageDigest1.update(paramArrayOfbyte1, 0, j);
      arrayOfByte1 = paramMessageDigest1.digest();
      k = arrayOfByte1.length;
    } 
    expand(paramMessageDigest1, 16, arrayOfByte1, 0, k, paramArrayOfbyte2, paramArrayOfbyte3, arrayOfByte2, (byte[])HMAC_ipad64
        .clone(), (byte[])HMAC_opad64.clone());
    if (j > 64) {
      paramMessageDigest2.update(paramArrayOfbyte1, i, j);
      arrayOfByte1 = paramMessageDigest2.digest();
      k = arrayOfByte1.length;
      i = 0;
    } 
    expand(paramMessageDigest2, 20, arrayOfByte1, i, k, paramArrayOfbyte2, paramArrayOfbyte3, arrayOfByte2, (byte[])HMAC_ipad64
        .clone(), (byte[])HMAC_opad64.clone());
    return arrayOfByte2;
  }
  
  private static void expand(MessageDigest paramMessageDigest, int paramInt1, byte[] paramArrayOfbyte1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, byte[] paramArrayOfbyte5, byte[] paramArrayOfbyte6) throws DigestException {
    for (byte b1 = 0; b1 < paramInt3; b1++) {
      paramArrayOfbyte5[b1] = (byte)(paramArrayOfbyte5[b1] ^ paramArrayOfbyte1[b1 + paramInt2]);
      paramArrayOfbyte6[b1] = (byte)(paramArrayOfbyte6[b1] ^ paramArrayOfbyte1[b1 + paramInt2]);
    } 
    byte[] arrayOfByte1 = new byte[paramInt1];
    byte[] arrayOfByte2 = null;
    int i = paramArrayOfbyte4.length;
    byte b2 = 0;
    while (i > 0) {
      paramMessageDigest.update(paramArrayOfbyte5);
      if (arrayOfByte2 == null) {
        paramMessageDigest.update(paramArrayOfbyte2);
        paramMessageDigest.update(paramArrayOfbyte3);
      } else {
        paramMessageDigest.update(arrayOfByte2);
      } 
      paramMessageDigest.digest(arrayOfByte1, 0, paramInt1);
      paramMessageDigest.update(paramArrayOfbyte6);
      paramMessageDigest.update(arrayOfByte1);
      if (arrayOfByte2 == null)
        arrayOfByte2 = new byte[paramInt1]; 
      paramMessageDigest.digest(arrayOfByte2, 0, paramInt1);
      paramMessageDigest.update(paramArrayOfbyte5);
      paramMessageDigest.update(arrayOfByte2);
      paramMessageDigest.update(paramArrayOfbyte2);
      paramMessageDigest.update(paramArrayOfbyte3);
      paramMessageDigest.digest(arrayOfByte1, 0, paramInt1);
      paramMessageDigest.update(paramArrayOfbyte6);
      paramMessageDigest.update(arrayOfByte1);
      paramMessageDigest.digest(arrayOfByte1, 0, paramInt1);
      int j = Math.min(paramInt1, i);
      for (byte b = 0; b < j; b++)
        paramArrayOfbyte4[b2++] = (byte)(paramArrayOfbyte4[b2++] ^ arrayOfByte1[b]); 
      i -= j;
    } 
  }
  
  public static class TlsPrfGenerator {}
  
  public static class V12 extends TlsPrfGenerator {
    protected SecretKey engineGenerateKey() {
      return engineGenerateKey0(true);
    }
  }
}
