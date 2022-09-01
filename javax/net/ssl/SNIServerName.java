package javax.net.ssl;

import java.util.Arrays;

public abstract class SNIServerName {
  private final int type;
  
  private final byte[] encoded;
  
  private static final char[] HEXES = "0123456789ABCDEF".toCharArray();
  
  protected SNIServerName(int paramInt, byte[] paramArrayOfbyte) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Server name type cannot be less than zero"); 
    if (paramInt > 255)
      throw new IllegalArgumentException("Server name type cannot be greater than 255"); 
    this.type = paramInt;
    if (paramArrayOfbyte == null)
      throw new NullPointerException("Server name encoded value cannot be null"); 
    this.encoded = (byte[])paramArrayOfbyte.clone();
  }
  
  public final int getType() {
    return this.type;
  }
  
  public final byte[] getEncoded() {
    return (byte[])this.encoded.clone();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    SNIServerName sNIServerName = (SNIServerName)paramObject;
    return (this.type == sNIServerName.type && 
      Arrays.equals(this.encoded, sNIServerName.encoded));
  }
  
  public int hashCode() {
    int i = 17;
    i = 31 * i + this.type;
    i = 31 * i + Arrays.hashCode(this.encoded);
    return i;
  }
  
  public String toString() {
    if (this.type == 0)
      return "type=host_name (0), value=" + toHexString(this.encoded); 
    return "type=(" + this.type + "), value=" + toHexString(this.encoded);
  }
  
  private static String toHexString(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length == 0)
      return "(empty)"; 
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfbyte.length * 3 - 1);
    boolean bool = true;
    for (byte b : paramArrayOfbyte) {
      if (bool) {
        bool = false;
      } else {
        stringBuilder.append(':');
      } 
      int i = b & 0xFF;
      stringBuilder.append(HEXES[i >>> 4]);
      stringBuilder.append(HEXES[i & 0xF]);
    } 
    return stringBuilder.toString();
  }
}
