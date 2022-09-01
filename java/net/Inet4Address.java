package java.net;

import java.io.ObjectStreamException;

public final class Inet4Address extends InetAddress {
  static final int INADDRSZ = 4;
  
  private static final long serialVersionUID = 3286316764910316507L;
  
  static {
    init();
  }
  
  Inet4Address() {
    (holder()).hostName = null;
    (holder()).address = 0;
    (holder()).family = 1;
  }
  
  Inet4Address(String paramString, byte[] paramArrayOfbyte) {
    (holder()).hostName = paramString;
    (holder()).family = 1;
    if (paramArrayOfbyte != null && 
      paramArrayOfbyte.length == 4) {
      int i = paramArrayOfbyte[3] & 0xFF;
      i |= paramArrayOfbyte[2] << 8 & 0xFF00;
      i |= paramArrayOfbyte[1] << 16 & 0xFF0000;
      i |= paramArrayOfbyte[0] << 24 & 0xFF000000;
      (holder()).address = i;
    } 
  }
  
  Inet4Address(String paramString, int paramInt) {
    (holder()).hostName = paramString;
    (holder()).family = 1;
    (holder()).address = paramInt;
  }
  
  private Object writeReplace() throws ObjectStreamException {
    InetAddress inetAddress = new InetAddress();
    (inetAddress.holder()).hostName = holder().getHostName();
    (inetAddress.holder()).address = holder().getAddress();
    (inetAddress.holder()).family = 2;
    return inetAddress;
  }
  
  public boolean isMulticastAddress() {
    return ((holder().getAddress() & 0xF0000000) == -536870912);
  }
  
  public boolean isAnyLocalAddress() {
    return (holder().getAddress() == 0);
  }
  
  public boolean isLoopbackAddress() {
    byte[] arrayOfByte = getAddress();
    return (arrayOfByte[0] == Byte.MAX_VALUE);
  }
  
  public boolean isLinkLocalAddress() {
    int i = holder().getAddress();
    return ((i >>> 24 & 0xFF) == 169 && (i >>> 16 & 0xFF) == 254);
  }
  
  public boolean isSiteLocalAddress() {
    int i = holder().getAddress();
    return ((i >>> 24 & 0xFF) == 10 || ((i >>> 24 & 0xFF) == 172 && (i >>> 16 & 0xF0) == 16) || ((i >>> 24 & 0xFF) == 192 && (i >>> 16 & 0xFF) == 168));
  }
  
  public boolean isMCGlobal() {
    byte[] arrayOfByte = getAddress();
    return ((arrayOfByte[0] & 0xFF) >= 224 && (arrayOfByte[0] & 0xFF) <= 238 && ((arrayOfByte[0] & 0xFF) != 224 || arrayOfByte[1] != 0 || arrayOfByte[2] != 0));
  }
  
  public boolean isMCNodeLocal() {
    return false;
  }
  
  public boolean isMCLinkLocal() {
    int i = holder().getAddress();
    return ((i >>> 24 & 0xFF) == 224 && (i >>> 16 & 0xFF) == 0 && (i >>> 8 & 0xFF) == 0);
  }
  
  public boolean isMCSiteLocal() {
    int i = holder().getAddress();
    return ((i >>> 24 & 0xFF) == 239 && (i >>> 16 & 0xFF) == 255);
  }
  
  public boolean isMCOrgLocal() {
    int i = holder().getAddress();
    return ((i >>> 24 & 0xFF) == 239 && (i >>> 16 & 0xFF) >= 192 && (i >>> 16 & 0xFF) <= 195);
  }
  
  public byte[] getAddress() {
    int i = holder().getAddress();
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(i >>> 24 & 0xFF);
    arrayOfByte[1] = (byte)(i >>> 16 & 0xFF);
    arrayOfByte[2] = (byte)(i >>> 8 & 0xFF);
    arrayOfByte[3] = (byte)(i & 0xFF);
    return arrayOfByte;
  }
  
  public String getHostAddress() {
    return numericToTextFormat(getAddress());
  }
  
  public int hashCode() {
    return holder().getAddress();
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject != null && paramObject instanceof Inet4Address && ((InetAddress)paramObject)
      .holder().getAddress() == holder().getAddress());
  }
  
  static String numericToTextFormat(byte[] paramArrayOfbyte) {
    return (paramArrayOfbyte[0] & 0xFF) + "." + (paramArrayOfbyte[1] & 0xFF) + "." + (paramArrayOfbyte[2] & 0xFF) + "." + (paramArrayOfbyte[3] & 0xFF);
  }
  
  private static native void init();
}
