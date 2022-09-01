package javax.crypto.spec;

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;

public class SecretKeySpec implements KeySpec, SecretKey {
  private static final long serialVersionUID = 6577238317307289933L;
  
  private byte[] key;
  
  private String algorithm;
  
  public SecretKeySpec(byte[] paramArrayOfbyte, String paramString) {
    if (paramArrayOfbyte == null || paramString == null)
      throw new IllegalArgumentException("Missing argument"); 
    if (paramArrayOfbyte.length == 0)
      throw new IllegalArgumentException("Empty key"); 
    this.key = (byte[])paramArrayOfbyte.clone();
    this.algorithm = paramString;
  }
  
  public SecretKeySpec(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, String paramString) {
    if (paramArrayOfbyte == null || paramString == null)
      throw new IllegalArgumentException("Missing argument"); 
    if (paramArrayOfbyte.length == 0)
      throw new IllegalArgumentException("Empty key"); 
    if (paramArrayOfbyte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("Invalid offset/length combination"); 
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException("len is negative"); 
    this.key = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, this.key, 0, paramInt2);
    this.algorithm = paramString;
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public String getFormat() {
    return "RAW";
  }
  
  public byte[] getEncoded() {
    return (byte[])this.key.clone();
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 1; b < this.key.length; b++)
      i += this.key[b] * b; 
    if (this.algorithm.equalsIgnoreCase("TripleDES"))
      return i ^= "desede".hashCode(); 
    return i ^= this.algorithm.toLowerCase().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SecretKey))
      return false; 
    String str = ((SecretKey)paramObject).getAlgorithm();
    if (!str.equalsIgnoreCase(this.algorithm) && (
      !str.equalsIgnoreCase("DESede") || 
      !this.algorithm.equalsIgnoreCase("TripleDES")) && (
      !str.equalsIgnoreCase("TripleDES") || 
      !this.algorithm.equalsIgnoreCase("DESede")))
      return false; 
    byte[] arrayOfByte = ((SecretKey)paramObject).getEncoded();
    return MessageDigest.isEqual(this.key, arrayOfByte);
  }
}
