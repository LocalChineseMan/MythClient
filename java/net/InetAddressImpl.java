package java.net;

import java.io.IOException;

interface InetAddressImpl {
  String getLocalHostName() throws UnknownHostException;
  
  InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  String getHostByAddr(byte[] paramArrayOfbyte) throws UnknownHostException;
  
  InetAddress anyLocalAddress();
  
  InetAddress loopbackAddress();
  
  boolean isReachable(InetAddress paramInetAddress, int paramInt1, NetworkInterface paramNetworkInterface, int paramInt2) throws IOException;
}
