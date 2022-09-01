package javax.net.ssl;

import java.util.Enumeration;

public interface SSLSessionContext {
  SSLSession getSession(byte[] paramArrayOfbyte);
  
  Enumeration<byte[]> getIds();
  
  void setSessionTimeout(int paramInt) throws IllegalArgumentException;
  
  int getSessionTimeout();
  
  void setSessionCacheSize(int paramInt) throws IllegalArgumentException;
  
  int getSessionCacheSize();
}
