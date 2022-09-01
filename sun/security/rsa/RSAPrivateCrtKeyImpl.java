package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public final class RSAPrivateCrtKeyImpl extends PKCS8Key implements RSAPrivateCrtKey {
  private static final long serialVersionUID = -1326088454257084918L;
  
  private BigInteger n;
  
  private BigInteger e;
  
  private BigInteger d;
  
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger pe;
  
  private BigInteger qe;
  
  private BigInteger coeff;
  
  static final AlgorithmId rsaId = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
  
  public static RSAPrivateKey newKey(byte[] paramArrayOfbyte) throws InvalidKeyException {
    RSAPrivateCrtKeyImpl rSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(paramArrayOfbyte);
    if (rSAPrivateCrtKeyImpl.getPublicExponent().signum() == 0)
      return new RSAPrivateKeyImpl(rSAPrivateCrtKeyImpl
          .getModulus(), rSAPrivateCrtKeyImpl
          .getPrivateExponent()); 
    return rSAPrivateCrtKeyImpl;
  }
  
  RSAPrivateCrtKeyImpl(byte[] paramArrayOfbyte) throws InvalidKeyException {
    decode(paramArrayOfbyte);
    RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
  }
  
  RSAPrivateCrtKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8) throws InvalidKeyException {
    this.n = paramBigInteger1;
    this.e = paramBigInteger2;
    this.d = paramBigInteger3;
    this.p = paramBigInteger4;
    this.q = paramBigInteger5;
    this.pe = paramBigInteger6;
    this.qe = paramBigInteger7;
    this.coeff = paramBigInteger8;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    this.algid = rsaId;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(paramBigInteger1);
      derOutputStream.putInteger(paramBigInteger2);
      derOutputStream.putInteger(paramBigInteger3);
      derOutputStream.putInteger(paramBigInteger4);
      derOutputStream.putInteger(paramBigInteger5);
      derOutputStream.putInteger(paramBigInteger6);
      derOutputStream.putInteger(paramBigInteger7);
      derOutputStream.putInteger(paramBigInteger8);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      this.key = derValue.toByteArray();
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public String getAlgorithm() {
    return "RSA";
  }
  
  public BigInteger getModulus() {
    return this.n;
  }
  
  public BigInteger getPublicExponent() {
    return this.e;
  }
  
  public BigInteger getPrivateExponent() {
    return this.d;
  }
  
  public BigInteger getPrimeP() {
    return this.p;
  }
  
  public BigInteger getPrimeQ() {
    return this.q;
  }
  
  public BigInteger getPrimeExponentP() {
    return this.pe;
  }
  
  public BigInteger getPrimeExponentQ() {
    return this.qe;
  }
  
  public BigInteger getCrtCoefficient() {
    return this.coeff;
  }
  
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream derInputStream1 = new DerInputStream(this.key);
      DerValue derValue = derInputStream1.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Not a SEQUENCE"); 
      DerInputStream derInputStream2 = derValue.data;
      int i = derInputStream2.getInteger();
      if (i != 0)
        throw new IOException("Version must be 0"); 
      this.n = getBigInteger(derInputStream2);
      this.e = getBigInteger(derInputStream2);
      this.d = getBigInteger(derInputStream2);
      this.p = getBigInteger(derInputStream2);
      this.q = getBigInteger(derInputStream2);
      this.pe = getBigInteger(derInputStream2);
      this.qe = getBigInteger(derInputStream2);
      this.coeff = getBigInteger(derInputStream2);
      if (derValue.data.available() != 0)
        throw new IOException("Extra data available"); 
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid RSA private key", iOException);
    } 
  }
  
  static BigInteger getBigInteger(DerInputStream paramDerInputStream) throws IOException {
    BigInteger bigInteger = paramDerInputStream.getBigInteger();
    if (bigInteger.signum() < 0)
      bigInteger = new BigInteger(1, bigInteger.toByteArray()); 
    return bigInteger;
  }
}
