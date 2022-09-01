package com.sun.crypto.provider;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import sun.security.internal.spec.TlsKeyMaterialSpec;

public final class TlsKeyMaterialGenerator extends KeyGeneratorSpi {
  private static final String MSG = "TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec";
  
  private TlsKeyMaterialParameterSpec spec;
  
  private int protocolVersion;
  
  protected void engineInit(SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof TlsKeyMaterialParameterSpec))
      throw new InvalidAlgorithmParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec"); 
    this.spec = (TlsKeyMaterialParameterSpec)paramAlgorithmParameterSpec;
    if (!"RAW".equals(this.spec.getMasterSecret().getFormat()))
      throw new InvalidAlgorithmParameterException("Key format must be RAW"); 
    this
      .protocolVersion = this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion();
    if (this.protocolVersion < 768 || this.protocolVersion > 771)
      throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported"); 
  }
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
  }
  
  protected SecretKey engineGenerateKey() {
    if (this.spec == null)
      throw new IllegalStateException("TlsKeyMaterialGenerator must be initialized"); 
    try {
      return engineGenerateKey0();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ProviderException(generalSecurityException);
    } 
  }
  
  private SecretKey engineGenerateKey0() throws GeneralSecurityException {
    byte[] arrayOfByte1 = this.spec.getMasterSecret().getEncoded();
    byte[] arrayOfByte2 = this.spec.getClientRandom();
    byte[] arrayOfByte3 = this.spec.getServerRandom();
    SecretKeySpec secretKeySpec1 = null;
    SecretKeySpec secretKeySpec2 = null;
    SecretKeySpec secretKeySpec3 = null;
    SecretKeySpec secretKeySpec4 = null;
    IvParameterSpec ivParameterSpec1 = null;
    IvParameterSpec ivParameterSpec2 = null;
    int i = this.spec.getMacKeyLength();
    int j = this.spec.getExpandedCipherKeyLength();
    boolean bool = (j != 0) ? true : false;
    int k = this.spec.getCipherKeyLength();
    int m = this.spec.getIvLength();
    int n = i + k + (bool ? 0 : m);
    n <<= 1;
    byte[] arrayOfByte4 = new byte[n];
    MessageDigest messageDigest1 = null;
    MessageDigest messageDigest2 = null;
    if (this.protocolVersion >= 771) {
      byte[] arrayOfByte = TlsPrfGenerator.concat(arrayOfByte3, arrayOfByte2);
      arrayOfByte4 = TlsPrfGenerator.doTLS12PRF(arrayOfByte1, TlsPrfGenerator.LABEL_KEY_EXPANSION, arrayOfByte, n, this.spec
          .getPRFHashAlg(), this.spec
          .getPRFHashLength(), this.spec.getPRFBlockSize());
    } else if (this.protocolVersion >= 769) {
      messageDigest1 = MessageDigest.getInstance("MD5");
      messageDigest2 = MessageDigest.getInstance("SHA1");
      byte[] arrayOfByte = TlsPrfGenerator.concat(arrayOfByte3, arrayOfByte2);
      arrayOfByte4 = TlsPrfGenerator.doTLS10PRF(arrayOfByte1, TlsPrfGenerator.LABEL_KEY_EXPANSION, arrayOfByte, n, messageDigest1, messageDigest2);
    } else {
      messageDigest1 = MessageDigest.getInstance("MD5");
      messageDigest2 = MessageDigest.getInstance("SHA1");
      arrayOfByte4 = new byte[n];
      byte[] arrayOfByte = new byte[20];
      byte b = 0;
      int i2 = n;
      for (; i2 > 0; 
        b++, i2 -= 16) {
        messageDigest2.update(TlsPrfGenerator.SSL3_CONST[b]);
        messageDigest2.update(arrayOfByte1);
        messageDigest2.update(arrayOfByte3);
        messageDigest2.update(arrayOfByte2);
        messageDigest2.digest(arrayOfByte, 0, 20);
        messageDigest1.update(arrayOfByte1);
        messageDigest1.update(arrayOfByte);
        if (i2 >= 16) {
          messageDigest1.digest(arrayOfByte4, b << 4, 16);
        } else {
          messageDigest1.digest(arrayOfByte, 0, 16);
          System.arraycopy(arrayOfByte, 0, arrayOfByte4, b << 4, i2);
        } 
      } 
    } 
    int i1 = 0;
    if (i != 0) {
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(arrayOfByte4, i1, arrayOfByte, 0, i);
      i1 += i;
      secretKeySpec1 = new SecretKeySpec(arrayOfByte, "Mac");
      System.arraycopy(arrayOfByte4, i1, arrayOfByte, 0, i);
      i1 += i;
      secretKeySpec2 = new SecretKeySpec(arrayOfByte, "Mac");
    } 
    if (k == 0)
      return new TlsKeyMaterialSpec(secretKeySpec1, secretKeySpec2); 
    String str = this.spec.getCipherAlgorithm();
    byte[] arrayOfByte5 = new byte[k];
    System.arraycopy(arrayOfByte4, i1, arrayOfByte5, 0, k);
    i1 += k;
    byte[] arrayOfByte6 = new byte[k];
    System.arraycopy(arrayOfByte4, i1, arrayOfByte6, 0, k);
    i1 += k;
    if (!bool) {
      secretKeySpec3 = new SecretKeySpec(arrayOfByte5, str);
      secretKeySpec4 = new SecretKeySpec(arrayOfByte6, str);
      if (m != 0) {
        byte[] arrayOfByte = new byte[m];
        System.arraycopy(arrayOfByte4, i1, arrayOfByte, 0, m);
        i1 += m;
        ivParameterSpec1 = new IvParameterSpec(arrayOfByte);
        System.arraycopy(arrayOfByte4, i1, arrayOfByte, 0, m);
        i1 += m;
        ivParameterSpec2 = new IvParameterSpec(arrayOfByte);
      } 
    } else {
      if (this.protocolVersion >= 770)
        throw new RuntimeException("Internal Error:  TLS 1.1+ should not be negotiatingexportable ciphersuites"); 
      if (this.protocolVersion == 769) {
        byte[] arrayOfByte7 = TlsPrfGenerator.concat(arrayOfByte2, arrayOfByte3);
        byte[] arrayOfByte8 = TlsPrfGenerator.doTLS10PRF(arrayOfByte5, TlsPrfGenerator.LABEL_CLIENT_WRITE_KEY, arrayOfByte7, j, messageDigest1, messageDigest2);
        secretKeySpec3 = new SecretKeySpec(arrayOfByte8, str);
        arrayOfByte8 = TlsPrfGenerator.doTLS10PRF(arrayOfByte6, TlsPrfGenerator.LABEL_SERVER_WRITE_KEY, arrayOfByte7, j, messageDigest1, messageDigest2);
        secretKeySpec4 = new SecretKeySpec(arrayOfByte8, str);
        if (m != 0) {
          arrayOfByte8 = new byte[m];
          byte[] arrayOfByte = TlsPrfGenerator.doTLS10PRF(null, TlsPrfGenerator.LABEL_IV_BLOCK, arrayOfByte7, m << 1, messageDigest1, messageDigest2);
          System.arraycopy(arrayOfByte, 0, arrayOfByte8, 0, m);
          ivParameterSpec1 = new IvParameterSpec(arrayOfByte8);
          System.arraycopy(arrayOfByte, m, arrayOfByte8, 0, m);
          ivParameterSpec2 = new IvParameterSpec(arrayOfByte8);
        } 
      } else {
        byte[] arrayOfByte = new byte[j];
        messageDigest1.update(arrayOfByte5);
        messageDigest1.update(arrayOfByte2);
        messageDigest1.update(arrayOfByte3);
        System.arraycopy(messageDigest1.digest(), 0, arrayOfByte, 0, j);
        secretKeySpec3 = new SecretKeySpec(arrayOfByte, str);
        messageDigest1.update(arrayOfByte6);
        messageDigest1.update(arrayOfByte3);
        messageDigest1.update(arrayOfByte2);
        System.arraycopy(messageDigest1.digest(), 0, arrayOfByte, 0, j);
        secretKeySpec4 = new SecretKeySpec(arrayOfByte, str);
        if (m != 0) {
          arrayOfByte = new byte[m];
          messageDigest1.update(arrayOfByte2);
          messageDigest1.update(arrayOfByte3);
          System.arraycopy(messageDigest1.digest(), 0, arrayOfByte, 0, m);
          ivParameterSpec1 = new IvParameterSpec(arrayOfByte);
          messageDigest1.update(arrayOfByte3);
          messageDigest1.update(arrayOfByte2);
          System.arraycopy(messageDigest1.digest(), 0, arrayOfByte, 0, m);
          ivParameterSpec2 = new IvParameterSpec(arrayOfByte);
        } 
      } 
    } 
    return new TlsKeyMaterialSpec(secretKeySpec1, secretKeySpec2, secretKeySpec3, ivParameterSpec1, secretKeySpec4, ivParameterSpec2);
  }
}
