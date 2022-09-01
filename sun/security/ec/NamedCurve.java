package sun.security.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

class NamedCurve extends ECParameterSpec {
  private final String name;
  
  private final String oid;
  
  private final byte[] encoded;
  
  NamedCurve(String paramString1, String paramString2, EllipticCurve paramEllipticCurve, ECPoint paramECPoint, BigInteger paramBigInteger, int paramInt) {
    super(paramEllipticCurve, paramECPoint, paramBigInteger, paramInt);
    this.name = paramString1;
    this.oid = paramString2;
    DerOutputStream derOutputStream = new DerOutputStream();
    try {
      derOutputStream.putOID(new ObjectIdentifier(paramString2));
    } catch (IOException iOException) {
      throw new RuntimeException("Internal error", iOException);
    } 
    this.encoded = derOutputStream.toByteArray();
  }
  
  String getName() {
    return this.name;
  }
  
  byte[] getEncoded() {
    return (byte[])this.encoded.clone();
  }
  
  String getObjectId() {
    return this.oid;
  }
  
  public String toString() {
    return this.name + " (" + this.oid + ")";
  }
}
