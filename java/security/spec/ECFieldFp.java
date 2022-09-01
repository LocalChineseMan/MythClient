package java.security.spec;

import java.math.BigInteger;

public class ECFieldFp implements ECField {
  private BigInteger p;
  
  public ECFieldFp(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() != 1)
      throw new IllegalArgumentException("p is not positive"); 
    this.p = paramBigInteger;
  }
  
  public int getFieldSize() {
    return this.p.bitLength();
  }
  
  public BigInteger getP() {
    return this.p;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ECFieldFp)
      return this.p.equals(((ECFieldFp)paramObject).p); 
    return false;
  }
  
  public int hashCode() {
    return this.p.hashCode();
  }
}
