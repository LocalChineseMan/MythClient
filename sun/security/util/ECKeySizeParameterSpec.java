package sun.security.util;

import java.security.spec.AlgorithmParameterSpec;

public class ECKeySizeParameterSpec implements AlgorithmParameterSpec {
  private int keySize;
  
  public ECKeySizeParameterSpec(int paramInt) {
    this.keySize = paramInt;
  }
  
  public int getKeySize() {
    return this.keySize;
  }
}
