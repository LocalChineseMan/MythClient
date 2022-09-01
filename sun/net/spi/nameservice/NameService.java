package sun.net.spi.nameservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface NameService {
  InetAddress[] lookupAllHostAddr(String paramString) throws UnknownHostException;
  
  String getHostByAddr(byte[] paramArrayOfbyte) throws UnknownHostException;
}
