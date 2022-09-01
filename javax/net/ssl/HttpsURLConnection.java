package javax.net.ssl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public abstract class HttpsURLConnection extends HttpURLConnection {
  protected HttpsURLConnection(URL paramURL) {
    super(paramURL);
    this.hostnameVerifier = defaultHostnameVerifier;
    this.sslSocketFactory = getDefaultSSLSocketFactory();
  }
  
  public abstract String getCipherSuite();
  
  public abstract Certificate[] getLocalCertificates();
  
  public abstract Certificate[] getServerCertificates() throws SSLPeerUnverifiedException;
  
  public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    Certificate[] arrayOfCertificate = getServerCertificates();
    return ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
  }
  
  public Principal getLocalPrincipal() {
    Certificate[] arrayOfCertificate = getLocalCertificates();
    if (arrayOfCertificate != null)
      return ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal(); 
    return null;
  }
  
  private static HostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier();
  
  protected HostnameVerifier hostnameVerifier;
  
  private static class DefaultHostnameVerifier implements HostnameVerifier {
    private DefaultHostnameVerifier() {}
    
    public boolean verify(String param1String, SSLSession param1SSLSession) {
      return false;
    }
  }
  
  public static void setDefaultHostnameVerifier(HostnameVerifier paramHostnameVerifier) {
    if (paramHostnameVerifier == null)
      throw new IllegalArgumentException("no default HostnameVerifier specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SSLPermission("setHostnameVerifier")); 
    defaultHostnameVerifier = paramHostnameVerifier;
  }
  
  public static HostnameVerifier getDefaultHostnameVerifier() {
    return defaultHostnameVerifier;
  }
  
  public void setHostnameVerifier(HostnameVerifier paramHostnameVerifier) {
    if (paramHostnameVerifier == null)
      throw new IllegalArgumentException("no HostnameVerifier specified"); 
    this.hostnameVerifier = paramHostnameVerifier;
  }
  
  public HostnameVerifier getHostnameVerifier() {
    return this.hostnameVerifier;
  }
  
  private static SSLSocketFactory defaultSSLSocketFactory = null;
  
  private SSLSocketFactory sslSocketFactory;
  
  public static void setDefaultSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
    if (paramSSLSocketFactory == null)
      throw new IllegalArgumentException("no default SSLSocketFactory specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    defaultSSLSocketFactory = paramSSLSocketFactory;
  }
  
  public static SSLSocketFactory getDefaultSSLSocketFactory() {
    if (defaultSSLSocketFactory == null)
      defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault(); 
    return defaultSSLSocketFactory;
  }
  
  public void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
    if (paramSSLSocketFactory == null)
      throw new IllegalArgumentException("no SSLSocketFactory specified"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    this.sslSocketFactory = paramSSLSocketFactory;
  }
  
  public SSLSocketFactory getSSLSocketFactory() {
    return this.sslSocketFactory;
  }
}
