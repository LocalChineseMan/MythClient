package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class RC5ParameterSpec implements AlgorithmParameterSpec {
  private byte[] iv = null;
  
  private int version;
  
  private int rounds;
  
  private int wordSize;
  
  public RC5ParameterSpec(int paramInt1, int paramInt2, int paramInt3) {
    this.version = paramInt1;
    this.rounds = paramInt2;
    this.wordSize = paramInt3;
  }
  
  public RC5ParameterSpec(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte) {
    this(paramInt1, paramInt2, paramInt3, paramArrayOfbyte, 0);
  }
  
  public RC5ParameterSpec(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte, int paramInt4) {
    this.version = paramInt1;
    this.rounds = paramInt2;
    this.wordSize = paramInt3;
    if (paramArrayOfbyte == null)
      throw new IllegalArgumentException("IV missing"); 
    int i = paramInt3 / 8 * 2;
    if (paramArrayOfbyte.length - paramInt4 < i)
      throw new IllegalArgumentException("IV too short"); 
    this.iv = new byte[i];
    System.arraycopy(paramArrayOfbyte, paramInt4, this.iv, 0, i);
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public int getRounds() {
    return this.rounds;
  }
  
  public int getWordSize() {
    return this.wordSize;
  }
  
  public byte[] getIV() {
    return (this.iv == null) ? null : (byte[])this.iv.clone();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof RC5ParameterSpec))
      return false; 
    RC5ParameterSpec rC5ParameterSpec = (RC5ParameterSpec)paramObject;
    return (this.version == rC5ParameterSpec.version && this.rounds == rC5ParameterSpec.rounds && this.wordSize == rC5ParameterSpec.wordSize && 
      
      Arrays.equals(this.iv, rC5ParameterSpec.iv));
  }
  
  public int hashCode() {
    int i = 0;
    if (this.iv != null)
      for (byte b = 1; b < this.iv.length; b++)
        i += this.iv[b] * b;  
    i += this.version + this.rounds + this.wordSize;
    return i;
  }
}
