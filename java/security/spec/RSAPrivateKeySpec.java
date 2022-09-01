package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateKeySpec implements KeySpec {
  private BigInteger modulus;
  
  private BigInteger privateExponent;
  
  public RSAPrivateKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.modulus = paramBigInteger1;
    this.privateExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus() {
    return this.modulus;
  }
  
  public BigInteger getPrivateExponent() {
    return this.privateExponent;
  }
}
