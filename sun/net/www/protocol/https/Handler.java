package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import sun.net.www.protocol.http.Handler;

public class Handler extends Handler {
  protected String proxy;
  
  protected int proxyPort;
  
  protected int getDefaultPort() {
    return 443;
  }
  
  public Handler() {
    this.proxy = null;
    this.proxyPort = -1;
  }
  
  public Handler(String paramString, int paramInt) {
    this.proxy = paramString;
    this.proxyPort = paramInt;
  }
  
  protected URLConnection openConnection(URL paramURL) throws IOException {
    return openConnection(paramURL, (Proxy)null);
  }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException {
    return new HttpsURLConnectionImpl(paramURL, paramProxy, this);
  }
}
