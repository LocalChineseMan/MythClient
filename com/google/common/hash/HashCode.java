package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.security.MessageDigest;
import javax.annotation.Nullable;

@Beta
public abstract class HashCode {
  public abstract int bits();
  
  public abstract int asInt();
  
  public abstract long asLong();
  
  public abstract long padToLong();
  
  public abstract byte[] asBytes();
  
  public int writeBytesTo(byte[] dest, int offset, int maxLength) {
    maxLength = Ints.min(new int[] { maxLength, bits() / 8 });
    Preconditions.checkPositionIndexes(offset, offset + maxLength, dest.length);
    writeBytesToImpl(dest, offset, maxLength);
    return maxLength;
  }
  
  abstract void writeBytesToImpl(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  byte[] getBytesInternal() {
    return asBytes();
  }
  
  public static HashCode fromInt(int hash) {
    return (HashCode)new IntHashCode(hash);
  }
  
  public static HashCode fromLong(long hash) {
    return (HashCode)new LongHashCode(hash);
  }
  
  public static HashCode fromBytes(byte[] bytes) {
    Preconditions.checkArgument((bytes.length >= 1), "A HashCode must contain at least 1 byte.");
    return fromBytesNoCopy((byte[])bytes.clone());
  }
  
  static HashCode fromBytesNoCopy(byte[] bytes) {
    return new BytesHashCode(bytes);
  }
  
  private static final class BytesHashCode extends HashCode implements Serializable {
    final byte[] bytes;
    
    private static final long serialVersionUID = 0L;
    
    BytesHashCode(byte[] bytes) {
      this.bytes = (byte[])Preconditions.checkNotNull(bytes);
    }
    
    public int bits() {
      return this.bytes.length * 8;
    }
    
    public byte[] asBytes() {
      return (byte[])this.bytes.clone();
    }
    
    public int asInt() {
      Preconditions.checkState((this.bytes.length >= 4), "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", new Object[] { Integer.valueOf(this.bytes.length) });
      return this.bytes[0] & 0xFF | (this.bytes[1] & 0xFF) << 8 | (this.bytes[2] & 0xFF) << 16 | (this.bytes[3] & 0xFF) << 24;
    }
    
    public long asLong() {
      Preconditions.checkState((this.bytes.length >= 8), "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", new Object[] { Integer.valueOf(this.bytes.length) });
      return padToLong();
    }
    
    public long padToLong() {
      long retVal = (this.bytes[0] & 0xFF);
      for (int i = 1; i < Math.min(this.bytes.length, 8); i++)
        retVal |= (this.bytes[i] & 0xFFL) << i * 8; 
      return retVal;
    }
    
    void writeBytesToImpl(byte[] dest, int offset, int maxLength) {
      System.arraycopy(this.bytes, 0, dest, offset, maxLength);
    }
    
    byte[] getBytesInternal() {
      return this.bytes;
    }
  }
  
  public static HashCode fromString(String string) {
    Preconditions.checkArgument((string.length() >= 2), "input string (%s) must have at least 2 characters", new Object[] { string });
    Preconditions.checkArgument((string.length() % 2 == 0), "input string (%s) must have an even number of characters", new Object[] { string });
    byte[] bytes = new byte[string.length() / 2];
    for (int i = 0; i < string.length(); i += 2) {
      int ch1 = decode(string.charAt(i)) << 4;
      int ch2 = decode(string.charAt(i + 1));
      bytes[i / 2] = (byte)(ch1 + ch2);
    } 
    return fromBytesNoCopy(bytes);
  }
  
  private static int decode(char ch) {
    if (ch >= '0' && ch <= '9')
      return ch - 48; 
    if (ch >= 'a' && ch <= 'f')
      return ch - 97 + 10; 
    throw new IllegalArgumentException("Illegal hexadecimal character: " + ch);
  }
  
  public final boolean equals(@Nullable Object object) {
    if (object instanceof HashCode) {
      HashCode that = (HashCode)object;
      return MessageDigest.isEqual(asBytes(), that.asBytes());
    } 
    return false;
  }
  
  public final int hashCode() {
    if (bits() >= 32)
      return asInt(); 
    byte[] bytes = asBytes();
    int val = bytes[0] & 0xFF;
    for (int i = 1; i < bytes.length; i++)
      val |= (bytes[i] & 0xFF) << i * 8; 
    return val;
  }
  
  public final String toString() {
    byte[] bytes = asBytes();
    StringBuilder sb = new StringBuilder(2 * bytes.length);
    for (byte b : bytes)
      sb.append(hexDigits[b >> 4 & 0xF]).append(hexDigits[b & 0xF]); 
    return sb.toString();
  }
  
  private static final char[] hexDigits = "0123456789abcdef".toCharArray();
  
  private static final class HashCode {}
  
  private static final class HashCode {}
}
