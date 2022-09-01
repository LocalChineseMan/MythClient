package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import sun.net.www.protocol.http.Handler;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection {
  public HttpsURLConnection httpsURLConnection;
  
  DelegateHttpsURLConnection(URL paramURL, Handler paramHandler, HttpsURLConnection paramHttpsURLConnection) throws IOException {
    this(paramURL, null, paramHandler, paramHttpsURLConnection);
  }
  
  DelegateHttpsURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler, HttpsURLConnection paramHttpsURLConnection) throws IOException {
    super(paramURL, paramProxy, paramHandler);
    this.httpsURLConnection = paramHttpsURLConnection;
  }
  
  protected SSLSocketFactory getSSLSocketFactory() {
    return this.httpsURLConnection.getSSLSocketFactory();
  }
  
  protected HostnameVerifier getHostnameVerifier() {
    return this.httpsURLConnection.getHostnameVerifier();
  }
  
  protected void dispose() throws Throwable {
    finalize();
  }
}
