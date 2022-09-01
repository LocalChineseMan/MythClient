package java.net;

import java.io.IOException;
import java.util.Enumeration;

class Inet4AddressImpl implements InetAddressImpl {
  private InetAddress anyLocalAddress;
  
  private InetAddress loopbackAddress;
  
  public native String getLocalHostName() throws UnknownHostException;
  
  public native InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  public native String getHostByAddr(byte[] paramArrayOfbyte) throws UnknownHostException;
  
  private native boolean isReachable0(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) throws IOException;
  
  public synchronized InetAddress anyLocalAddress() {
    if (this.anyLocalAddress == null) {
      this.anyLocalAddress = new Inet4Address();
      (this.anyLocalAddress.holder()).hostName = "0.0.0.0";
    } 
    return this.anyLocalAddress;
  }
  
  public synchronized InetAddress loopbackAddress() {
    if (this.loopbackAddress == null) {
      byte[] arrayOfByte = { Byte.MAX_VALUE, 0, 0, 1 };
      this.loopbackAddress = new Inet4Address("localhost", arrayOfByte);
    } 
    return this.loopbackAddress;
  }
  
  public boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2) throws IOException {
    byte[] arrayOfByte = null;
    if (paramNetworkInterface != null) {
      Enumeration<InetAddress> enumeration = paramNetworkInterface.getInetAddresses();
      InetAddress inetAddress = null;
      while (!(inetAddress instanceof Inet4Address) && enumeration
        .hasMoreElements())
        inetAddress = enumeration.nextElement(); 
      if (inetAddress instanceof Inet4Address)
        arrayOfByte = inetAddress.getAddress(); 
    } 
    return isReachable0(paramInetAddress.getAddress(), paramInt1, arrayOfByte, paramInt2);
  }
}
