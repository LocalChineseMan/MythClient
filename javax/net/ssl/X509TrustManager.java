package javax.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface X509TrustManager extends TrustManager {
  void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException;
  
  void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException;
  
  X509Certificate[] getAcceptedIssuers();
}
