package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class RC2ParameterSpec implements AlgorithmParameterSpec {
  private byte[] iv = null;
  
  private int effectiveKeyBits;
  
  public RC2ParameterSpec(int paramInt) {
    this.effectiveKeyBits = paramInt;
  }
  
  public RC2ParameterSpec(int paramInt, byte[] paramArrayOfbyte) {
    this(paramInt, paramArrayOfbyte, 0);
  }
  
  public RC2ParameterSpec(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    this.effectiveKeyBits = paramInt1;
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("IV missing"); 
    byte b = 8;
    if (paramArrayOfbyte.length - paramInt2 < b)
      throw new IllegalArgumentException("IV too short"); 
    this.iv = new byte[b];
    System.arraycopy(paramArrayOfbyte, paramInt2, this.iv, 0, b);
  }
  
  public int getEffectiveKeyBits() {
    return this.effectiveKeyBits;
  }
  
  public byte[] getIV() {
    return (this.iv == null) ? null : (byte[])this.iv.clone();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof RC2ParameterSpec))
      return false; 
    RC2ParameterSpec rC2ParameterSpec = (RC2ParameterSpec)paramObject;
    return (this.effectiveKeyBits == rC2ParameterSpec.effectiveKeyBits && 
      Arrays.equals(this.iv, rC2ParameterSpec.iv));
  }
  
  public int hashCode() {
    int i = 0;
    if (this.iv != null)
      for (byte b = 1; b < this.iv.length; b++)
        i += this.iv[b] * b;  
    return i += this.effectiveKeyBits;
  }
}
