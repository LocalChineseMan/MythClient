package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.security.cert.X509Certificate;

public interface SSLSession {
  byte[] getId();
  
  SSLSessionContext getSessionContext();
  
  long getCreationTime();
  
  long getLastAccessedTime();
  
  void invalidate();
  
  boolean isValid();
  
  void putValue(String paramString, Object paramObject);
  
  Object getValue(String paramString);
  
  void removeValue(String paramString);
  
  String[] getValueNames();
  
  Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException;
  
  Certificate[] getLocalCertificates();
  
  X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException;
  
  Principal getPeerPrincipal() throws SSLPeerUnverifiedException;
  
  Principal getLocalPrincipal();
  
  String getCipherSuite();
  
  String getProtocol();
  
  String getPeerHost();
  
  int getPeerPort();
  
  int getPacketBufferSize();
  
  int getApplicationBufferSize();
}
