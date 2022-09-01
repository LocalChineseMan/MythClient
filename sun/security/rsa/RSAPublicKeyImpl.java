package sun.security.rsa;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.interfaces.RSAPublicKey;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509Key;

public final class RSAPublicKeyImpl extends X509Key implements RSAPublicKey {
  private static final long serialVersionUID = 2644735423591199609L;
  
  private BigInteger n;
  
  private BigInteger e;
  
  public RSAPublicKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws InvalidKeyException {
    this.n = paramBigInteger1;
    this.e = paramBigInteger2;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    this.algid = RSAPrivateCrtKeyImpl.rsaId;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(paramBigInteger1);
      derOutputStream.putInteger(paramBigInteger2);
      byte[] arrayOfByte = (new DerValue((byte)48, derOutputStream.toByteArray())).toByteArray();
      setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public RSAPublicKeyImpl(byte[] paramArrayOfbyte) throws InvalidKeyException {
    decode(paramArrayOfbyte);
    RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
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
  
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream derInputStream1 = new DerInputStream(getKey().toByteArray());
      DerValue derValue = derInputStream1.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Not a SEQUENCE"); 
      DerInputStream derInputStream2 = derValue.data;
      this.n = RSAPrivateCrtKeyImpl.getBigInteger(derInputStream2);
      this.e = RSAPrivateCrtKeyImpl.getBigInteger(derInputStream2);
      if (derValue.data.available() != 0)
        throw new IOException("Extra data available"); 
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid RSA public key", iOException);
    } 
  }
  
  public String toString() {
    return "Sun RSA public key, " + this.n.bitLength() + " bits\n  modulus: " + this.n + "\n  public exponent: " + this.e;
  }
  
  protected Object writeReplace() throws ObjectStreamException {
    return new KeyRep(KeyRep.Type.PUBLIC, 
        getAlgorithm(), 
        getFormat(), 
        getEncoded());
  }
}
