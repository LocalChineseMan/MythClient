package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

public class IvParameterSpec implements AlgorithmParameterSpec {
  private byte[] iv;
  
  public IvParameterSpec(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public IvParameterSpec(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("IV missing"); 
    if (paramArrayOfbyte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("IV buffer too short for given offset/length combination"); 
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException("len is negative"); 
    this.iv = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, this.iv, 0, paramInt2);
  }
  
  public byte[] getIV() {
    return (byte[])this.iv.clone();
  }
}
