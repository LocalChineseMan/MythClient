package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

public class GCMParameterSpec implements AlgorithmParameterSpec {
  private byte[] iv;
  
  private int tLen;
  
  public GCMParameterSpec(int paramInt, byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("src array is null"); 
    init(paramInt, paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public GCMParameterSpec(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    init(paramInt1, paramArrayOfbyte, paramInt2, paramInt3);
  }
  
  private void init(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Length argument is negative"); 
    this.tLen = paramInt1;
    if (paramArrayOfbyte == null || paramInt3 < 0 || paramInt2 < 0 || paramInt3 + paramInt2 > paramArrayOfbyte.length)
      throw new IllegalArgumentException("Invalid buffer arguments"); 
    this.iv = new byte[paramInt3];
    System.arraycopy(paramArrayOfbyte, paramInt2, this.iv, 0, paramInt3);
  }
  
  public int getTLen() {
    return this.tLen;
  }
  
  public byte[] getIV() {
    return (byte[])this.iv.clone();
  }
}
