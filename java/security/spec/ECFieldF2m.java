package java.security.spec;

import java.math.BigInteger;
import java.util.Arrays;

public class ECFieldF2m implements ECField {
  private int m;
  
  private int[] ks;
  
  private BigInteger rp;
  
  public ECFieldF2m(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("m is not positive"); 
    this.m = paramInt;
    this.ks = null;
    this.rp = null;
  }
  
  public ECFieldF2m(int paramInt, BigInteger paramBigInteger) {
    this.m = paramInt;
    this.rp = paramBigInteger;
    if (paramInt <= 0)
      throw new IllegalArgumentException("m is not positive"); 
    int i = this.rp.bitCount();
    if (!this.rp.testBit(0) || !this.rp.testBit(paramInt) || (i != 3 && i != 5))
      throw new IllegalArgumentException("rp does not represent a valid reduction polynomial"); 
    BigInteger bigInteger = this.rp.clearBit(0).clearBit(paramInt);
    this.ks = new int[i - 2];
    for (int j = this.ks.length - 1; j >= 0; j--) {
      int k = bigInteger.getLowestSetBit();
      this.ks[j] = k;
      bigInteger = bigInteger.clearBit(k);
    } 
  }
  
  public ECFieldF2m(int paramInt, int[] paramArrayOfint) {
    this.m = paramInt;
    this.ks = (int[])paramArrayOfint.clone();
    if (paramInt <= 0)
      throw new IllegalArgumentException("m is not positive"); 
    if (this.ks.length != 1 && this.ks.length != 3)
      throw new IllegalArgumentException("length of ks is neither 1 nor 3"); 
    byte b;
    for (b = 0; b < this.ks.length; b++) {
      if (this.ks[b] < 1 || this.ks[b] > paramInt - 1)
        throw new IllegalArgumentException("ks[" + b + "] is out of range"); 
      if (b != 0 && this.ks[b] >= this.ks[b - 1])
        throw new IllegalArgumentException("values in ks are not in descending order"); 
    } 
    this.rp = BigInteger.ONE;
    this.rp = this.rp.setBit(paramInt);
    for (b = 0; b < this.ks.length; b++)
      this.rp = this.rp.setBit(this.ks[b]); 
  }
  
  public int getFieldSize() {
    return this.m;
  }
  
  public int getM() {
    return this.m;
  }
  
  public BigInteger getReductionPolynomial() {
    return this.rp;
  }
  
  public int[] getMidTermsOfReductionPolynomial() {
    if (this.ks == null)
      return null; 
    return (int[])this.ks.clone();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ECFieldF2m)
      return (this.m == ((ECFieldF2m)paramObject).m && 
        Arrays.equals(this.ks, ((ECFieldF2m)paramObject).ks)); 
    return false;
  }
  
  public int hashCode() {
    int i = this.m << 5;
    i += (this.rp == null) ? 0 : this.rp.hashCode();
    return i;
  }
}
