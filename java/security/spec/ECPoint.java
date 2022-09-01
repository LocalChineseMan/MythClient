package java.security.spec;

import java.math.BigInteger;

public class ECPoint {
  private final BigInteger x;
  
  private final BigInteger y;
  
  public static final ECPoint POINT_INFINITY = new ECPoint();
  
  private ECPoint() {
    this.x = null;
    this.y = null;
  }
  
  public ECPoint(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    if (paramBigInteger1 == null || paramBigInteger2 == null)
      throw new NullPointerException("affine coordinate x or y is null"); 
    this.x = paramBigInteger1;
    this.y = paramBigInteger2;
  }
  
  public BigInteger getAffineX() {
    return this.x;
  }
  
  public BigInteger getAffineY() {
    return this.y;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (this == POINT_INFINITY)
      return false; 
    if (paramObject instanceof ECPoint)
      return (this.x.equals(((ECPoint)paramObject).x) && this.y
        .equals(((ECPoint)paramObject).y)); 
    return false;
  }
  
  public int hashCode() {
    if (this == POINT_INFINITY)
      return 0; 
    return this.x.hashCode() << 5 + this.y.hashCode();
  }
}
