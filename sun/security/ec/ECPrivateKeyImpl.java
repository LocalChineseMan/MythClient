package sun.security.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ECUtil;
import sun.security.x509.AlgorithmId;

public final class ECPrivateKeyImpl extends PKCS8Key implements ECPrivateKey {
  private static final long serialVersionUID = 88695385615075129L;
  
  private BigInteger s;
  
  private ECParameterSpec params;
  
  ECPrivateKeyImpl(byte[] paramArrayOfbyte) throws InvalidKeyException {
    decode(paramArrayOfbyte);
  }
  
  ECPrivateKeyImpl(BigInteger paramBigInteger, ECParameterSpec paramECParameterSpec) throws InvalidKeyException {
    this.s = paramBigInteger;
    this.params = paramECParameterSpec;
    this
      .algid = new AlgorithmId(AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(paramECParameterSpec));
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(1);
      byte[] arrayOfByte = ECUtil.trimZeroes(paramBigInteger.toByteArray());
      derOutputStream.putOctetString(arrayOfByte);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      this.key = derValue.toByteArray();
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public String getAlgorithm() {
    return "EC";
  }
  
  public BigInteger getS() {
    return this.s;
  }
  
  public ECParameterSpec getParams() {
    return this.params;
  }
  
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream derInputStream1 = new DerInputStream(this.key);
      DerValue derValue = derInputStream1.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Not a SEQUENCE"); 
      DerInputStream derInputStream2 = derValue.data;
      int i = derInputStream2.getInteger();
      if (i != 1)
        throw new IOException("Version must be 1"); 
      byte[] arrayOfByte = derInputStream2.getOctetString();
      this.s = new BigInteger(1, arrayOfByte);
      while (derInputStream2.available() != 0) {
        DerValue derValue1 = derInputStream2.getDerValue();
        if (derValue1.isContextSpecific((byte)0))
          continue; 
        if (derValue1.isContextSpecific((byte)1))
          continue; 
        throw new InvalidKeyException("Unexpected value: " + derValue1);
      } 
      AlgorithmParameters algorithmParameters = this.algid.getParameters();
      if (algorithmParameters == null)
        throw new InvalidKeyException("EC domain parameters must be encoded in the algorithm identifier"); 
      this.params = algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid EC private key", iOException);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new InvalidKeyException("Invalid EC private key", invalidParameterSpecException);
    } 
  }
}
