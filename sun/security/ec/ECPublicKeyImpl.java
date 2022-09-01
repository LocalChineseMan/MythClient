package sun.security.ec;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.ECUtil;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509Key;

public final class ECPublicKeyImpl extends X509Key implements ECPublicKey {
  private static final long serialVersionUID = -2462037275160462289L;
  
  private ECPoint w;
  
  private ECParameterSpec params;
  
  ECPublicKeyImpl(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec) throws InvalidKeyException {
    this.w = paramECPoint;
    this.params = paramECParameterSpec;
    this
      .algid = new AlgorithmId(AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(paramECParameterSpec));
    this.key = ECUtil.encodePoint(paramECPoint, paramECParameterSpec.getCurve());
  }
  
  ECPublicKeyImpl(byte[] paramArrayOfbyte) throws InvalidKeyException {
    decode(paramArrayOfbyte);
  }
  
  public String getAlgorithm() {
    return "EC";
  }
  
  public ECPoint getW() {
    return this.w;
  }
  
  public ECParameterSpec getParams() {
    return this.params;
  }
  
  public byte[] getEncodedPublicValue() {
    return (byte[])this.key.clone();
  }
  
  protected void parseKeyBits() throws InvalidKeyException {
    AlgorithmParameters algorithmParameters = this.algid.getParameters();
    if (algorithmParameters == null)
      throw new InvalidKeyException("EC domain parameters must be encoded in the algorithm identifier"); 
    try {
      this.params = algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
      this.w = ECUtil.decodePoint(this.key, this.params.getCurve());
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid EC key", iOException);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new InvalidKeyException("Invalid EC key", invalidParameterSpecException);
    } 
  }
  
  public String toString() {
    return "Sun EC public key, " + this.params.getCurve().getField().getFieldSize() + " bits\n  public x coord: " + this.w
      .getAffineX() + "\n  public y coord: " + this.w
      .getAffineY() + "\n  parameters: " + this.params;
  }
  
  protected Object writeReplace() throws ObjectStreamException {
    return new KeyRep(KeyRep.Type.PUBLIC, 
        getAlgorithm(), 
        getFormat(), 
        getEncoded());
  }
}
