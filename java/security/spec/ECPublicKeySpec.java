package java.security.spec;

public class ECPublicKeySpec implements KeySpec {
  private ECPoint w;
  
  private ECParameterSpec params;
  
  public ECPublicKeySpec(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec) {
    if (paramECPoint == null)
      throw new NullPointerException("w is null"); 
    if (paramECParameterSpec == null)
      throw new NullPointerException("params is null"); 
    if (paramECPoint == ECPoint.POINT_INFINITY)
      throw new IllegalArgumentException("w is ECPoint.POINT_INFINITY"); 
    this.w = paramECPoint;
    this.params = paramECParameterSpec;
  }
  
  public ECPoint getW() {
    return this.w;
  }
  
  public ECParameterSpec getParams() {
    return this.params;
  }
}
