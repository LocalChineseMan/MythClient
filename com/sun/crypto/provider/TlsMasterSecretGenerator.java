package com.sun.crypto.provider;

import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import sun.security.internal.interfaces.TlsMasterSecret;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;

public final class TlsMasterSecretGenerator extends KeyGeneratorSpi {
  private static final String MSG = "TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec";
  
  private TlsMasterSecretParameterSpec spec;
  
  private int protocolVersion;
  
  protected void engineInit(SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof TlsMasterSecretParameterSpec))
      throw new InvalidAlgorithmParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec"); 
    this.spec = (TlsMasterSecretParameterSpec)paramAlgorithmParameterSpec;
    if (!"RAW".equals(this.spec.getPremasterSecret().getFormat()))
      throw new InvalidAlgorithmParameterException("Key format must be RAW"); 
    this
      .protocolVersion = this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion();
    if (this.protocolVersion < 768 || this.protocolVersion > 771)
      throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported"); 
  }
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
  }
  
  protected SecretKey engineGenerateKey() {
    byte b1, b2;
    if (this.spec == null)
      throw new IllegalStateException("TlsMasterSecretGenerator must be initialized"); 
    SecretKey secretKey = this.spec.getPremasterSecret();
    byte[] arrayOfByte = secretKey.getEncoded();
    if (secretKey.getAlgorithm().equals("TlsRsaPremasterSecret")) {
      b1 = arrayOfByte[0] & 0xFF;
      b2 = arrayOfByte[1] & 0xFF;
    } else {
      b1 = -1;
      b2 = -1;
    } 
    try {
      byte[] arrayOfByte1, arrayOfByte2 = this.spec.getClientRandom();
      byte[] arrayOfByte3 = this.spec.getServerRandom();
      if (this.protocolVersion >= 769) {
        byte[] arrayOfByte4 = TlsPrfGenerator.concat(arrayOfByte2, arrayOfByte3);
        arrayOfByte1 = (this.protocolVersion >= 771) ? TlsPrfGenerator.doTLS12PRF(arrayOfByte, TlsPrfGenerator.LABEL_MASTER_SECRET, arrayOfByte4, 48, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : TlsPrfGenerator.doTLS10PRF(arrayOfByte, TlsPrfGenerator.LABEL_MASTER_SECRET, arrayOfByte4, 48);
      } else {
        arrayOfByte1 = new byte[48];
        MessageDigest messageDigest1 = MessageDigest.getInstance("MD5");
        MessageDigest messageDigest2 = MessageDigest.getInstance("SHA");
        byte[] arrayOfByte4 = new byte[20];
        for (byte b = 0; b < 3; b++) {
          messageDigest2.update(TlsPrfGenerator.SSL3_CONST[b]);
          messageDigest2.update(arrayOfByte);
          messageDigest2.update(arrayOfByte2);
          messageDigest2.update(arrayOfByte3);
          messageDigest2.digest(arrayOfByte4, 0, 20);
          messageDigest1.update(arrayOfByte);
          messageDigest1.update(arrayOfByte4);
          messageDigest1.digest(arrayOfByte1, b << 4, 16);
        } 
      } 
      return new TlsMasterSecretKey(arrayOfByte1, b1, b2);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new ProviderException(noSuchAlgorithmException);
    } catch (DigestException digestException) {
      throw new ProviderException(digestException);
    } 
  }
  
  private static final class TlsMasterSecretKey implements TlsMasterSecret {
    private static final long serialVersionUID = 1019571680375368880L;
    
    private byte[] key;
    
    private final int majorVersion;
    
    private final int minorVersion;
    
    TlsMasterSecretKey(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      this.key = param1ArrayOfbyte;
      this.majorVersion = param1Int1;
      this.minorVersion = param1Int2;
    }
    
    public int getMajorVersion() {
      return this.majorVersion;
    }
    
    public int getMinorVersion() {
      return this.minorVersion;
    }
    
    public String getAlgorithm() {
      return "TlsMasterSecret";
    }
    
    public String getFormat() {
      return "RAW";
    }
    
    public byte[] getEncoded() {
      return (byte[])this.key.clone();
    }
  }
}
