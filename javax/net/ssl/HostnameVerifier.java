package javax.net.ssl;

public interface HostnameVerifier {
  boolean verify(String paramString, SSLSession paramSSLSession);
}
