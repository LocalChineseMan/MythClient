package sun.net.www.protocol.https;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;
import sun.security.ssl.SSLSocketImpl;
import sun.security.util.HostnameChecker;
import sun.util.logging.PlatformLogger;

final class HttpsClient extends HttpClient implements HandshakeCompletedListener {
  private static final int httpsPortNumber = 443;
  
  private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
  
  private HostnameVerifier hv;
  
  private SSLSocketFactory sslSocketFactory;
  
  private SSLSession session;
  
  protected int getDefaultPort() {
    return 443;
  }
  
  private String[] getCipherSuites() {
    String arrayOfString[], str = AccessController.<String>doPrivileged(new GetPropertyAction("https.cipherSuites"));
    if (str == null || "".equals(str)) {
      arrayOfString = null;
    } else {
      Vector<String> vector = new Vector();
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
      while (stringTokenizer.hasMoreTokens())
        vector.addElement(stringTokenizer.nextToken()); 
      arrayOfString = new String[vector.size()];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = vector.elementAt(b); 
    } 
    return arrayOfString;
  }
  
  private String[] getProtocols() {
    String arrayOfString[], str = AccessController.<String>doPrivileged(new GetPropertyAction("https.protocols"));
    if (str == null || "".equals(str)) {
      arrayOfString = null;
    } else {
      Vector<String> vector = new Vector();
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
      while (stringTokenizer.hasMoreTokens())
        vector.addElement(stringTokenizer.nextToken()); 
      arrayOfString = new String[vector.size()];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = vector.elementAt(b); 
    } 
    return arrayOfString;
  }
  
  private String getUserAgent() {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("https.agent"));
    if (str == null || str.length() == 0)
      str = "JSSE"; 
    return str;
  }
  
  private static Proxy newHttpProxy(String paramString, int paramInt) {
    InetSocketAddress inetSocketAddress = null;
    String str = paramString;
    boolean bool = (paramInt < 0) ? true : paramInt;
    try {
      inetSocketAddress = AccessController.<InetSocketAddress>doPrivileged((PrivilegedExceptionAction<InetSocketAddress>)new Object(str, bool));
    } catch (PrivilegedActionException privilegedActionException) {}
    return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
  }
  
  private HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL) throws IOException {
    this(paramSSLSocketFactory, paramURL, (String)null, -1);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt) throws IOException {
    this(paramSSLSocketFactory, paramURL, paramString, paramInt, -1);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt1, int paramInt2) throws IOException {
    this(paramSSLSocketFactory, paramURL, (paramString == null) ? null : 
        
        newHttpProxy(paramString, paramInt1), paramInt2);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, Proxy paramProxy, int paramInt) throws IOException {
    this.proxy = paramProxy;
    setSSLSocketFactory(paramSSLSocketFactory);
    this.proxyDisabled = true;
    this.host = paramURL.getHost();
    this.url = paramURL;
    this.port = paramURL.getPort();
    if (this.port == -1)
      this.port = getDefaultPort(); 
    setConnectTimeout(paramInt);
    openServer();
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, HttpURLConnection paramHttpURLConnection) throws IOException {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, true, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, boolean paramBoolean, HttpURLConnection paramHttpURLConnection) throws IOException {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, (String)null, -1, paramBoolean, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, HttpURLConnection paramHttpURLConnection) throws IOException {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, true, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, boolean paramBoolean, HttpURLConnection paramHttpURLConnection) throws IOException {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, paramBoolean, -1, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, HttpURLConnection paramHttpURLConnection) throws IOException {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, (paramString == null) ? null : 
        
        newHttpProxy(paramString, paramInt1), paramBoolean, paramInt2, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, Proxy paramProxy, boolean paramBoolean, int paramInt, HttpURLConnection paramHttpURLConnection) throws IOException {
    if (paramProxy == null)
      paramProxy = Proxy.NO_PROXY; 
    HttpsClient httpsClient = null;
    if (paramBoolean) {
      httpsClient = (HttpsClient)kac.get(paramURL, paramSSLSocketFactory);
      if (httpsClient != null && paramHttpURLConnection != null && paramHttpURLConnection
        .streaming() && paramHttpURLConnection
        .getRequestMethod() == "POST" && 
        !httpsClient.available())
        httpsClient = null; 
      if (httpsClient != null)
        if ((httpsClient.proxy != null && httpsClient.proxy.equals(paramProxy)) || (httpsClient.proxy == null && paramProxy == null)) {
          synchronized (httpsClient) {
            httpsClient.cachedHttpClient = true;
            assert httpsClient.inCache;
            httpsClient.inCache = false;
            if (paramHttpURLConnection != null && httpsClient.needsTunneling())
              paramHttpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING); 
            PlatformLogger platformLogger = HttpURLConnection.getHttpLogger();
            if (platformLogger.isLoggable(PlatformLogger.Level.FINEST))
              platformLogger.finest("KeepAlive stream retrieved from the cache, " + httpsClient); 
          } 
        } else {
          synchronized (httpsClient) {
            httpsClient.inCache = false;
            httpsClient.closeServer();
          } 
          httpsClient = null;
        }  
    } 
    if (httpsClient == null) {
      httpsClient = new HttpsClient(paramSSLSocketFactory, paramURL, paramProxy, paramInt);
    } else {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        if (httpsClient.proxy == Proxy.NO_PROXY || httpsClient.proxy == null) {
          securityManager.checkConnect(InetAddress.getByName(paramURL.getHost()).getHostAddress(), paramURL.getPort());
        } else {
          securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
        }  
      httpsClient.url = paramURL;
    } 
    httpsClient.setHostnameVerifier(paramHostnameVerifier);
    return (HttpClient)httpsClient;
  }
  
  void setHostnameVerifier(HostnameVerifier paramHostnameVerifier) {
    this.hv = paramHostnameVerifier;
  }
  
  void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
    this.sslSocketFactory = paramSSLSocketFactory;
  }
  
  SSLSocketFactory getSSLSocketFactory() {
    return this.sslSocketFactory;
  }
  
  protected Socket createSocket() throws IOException {
    try {
      return this.sslSocketFactory.createSocket();
    } catch (SocketException socketException) {
      Throwable throwable = socketException.getCause();
      if (throwable != null && throwable instanceof UnsupportedOperationException)
        return super.createSocket(); 
      throw socketException;
    } 
  }
  
  public boolean needsTunneling() {
    return (this.proxy != null && this.proxy.type() != Proxy.Type.DIRECT && this.proxy
      .type() != Proxy.Type.SOCKS);
  }
  
  public void afterConnect() throws IOException, UnknownHostException {
    if (!isCachedConnection()) {
      SSLSocket sSLSocket = null;
      SSLSocketFactory sSLSocketFactory = this.sslSocketFactory;
      try {
        if (!(this.serverSocket instanceof SSLSocket)) {
          sSLSocket = (SSLSocket)sSLSocketFactory.createSocket(this.serverSocket, this.host, this.port, true);
        } else {
          sSLSocket = (SSLSocket)this.serverSocket;
          if (sSLSocket instanceof SSLSocketImpl)
            ((SSLSocketImpl)sSLSocket).setHost(this.host); 
        } 
      } catch (IOException iOException) {
        try {
          sSLSocket = (SSLSocket)sSLSocketFactory.createSocket(this.host, this.port);
        } catch (IOException iOException1) {
          throw iOException;
        } 
      } 
      String[] arrayOfString1 = getProtocols();
      String[] arrayOfString2 = getCipherSuites();
      if (arrayOfString1 != null)
        sSLSocket.setEnabledProtocols(arrayOfString1); 
      if (arrayOfString2 != null)
        sSLSocket.setEnabledCipherSuites(arrayOfString2); 
      sSLSocket.addHandshakeCompletedListener((HandshakeCompletedListener)this);
      boolean bool = true;
      String str = sSLSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
      if (str != null && str.length() != 0) {
        if (str.equalsIgnoreCase("HTTPS"))
          bool = false; 
      } else {
        boolean bool1 = false;
        if (this.hv != null) {
          String str1 = this.hv.getClass().getCanonicalName();
          if (str1 != null && str1
            .equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier"))
            bool1 = true; 
        } else {
          bool1 = true;
        } 
        if (bool1) {
          SSLParameters sSLParameters = sSLSocket.getSSLParameters();
          sSLParameters.setEndpointIdentificationAlgorithm("HTTPS");
          sSLSocket.setSSLParameters(sSLParameters);
          bool = false;
        } 
      } 
      sSLSocket.startHandshake();
      this.session = sSLSocket.getSession();
      this.serverSocket = sSLSocket;
      try {
        this
          .serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, encoding);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new InternalError(encoding + " encoding not found");
      } 
      if (bool)
        checkURLSpoofing(this.hv); 
    } else {
      this.session = ((SSLSocket)this.serverSocket).getSession();
    } 
  }
  
  private void checkURLSpoofing(HostnameVerifier paramHostnameVerifier) throws IOException {
    String str1 = this.url.getHost();
    if (str1 != null && str1.startsWith("[") && str1.endsWith("]"))
      str1 = str1.substring(1, str1.length() - 1); 
    Certificate[] arrayOfCertificate = null;
    String str2 = this.session.getCipherSuite();
    try {
      HostnameChecker hostnameChecker = HostnameChecker.getInstance((byte)1);
      if (str2.startsWith("TLS_KRB5")) {
        if (!HostnameChecker.match(str1, getPeerPrincipal()))
          throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos"); 
      } else {
        X509Certificate x509Certificate;
        arrayOfCertificate = this.session.getPeerCertificates();
        if (arrayOfCertificate[0] instanceof X509Certificate) {
          x509Certificate = (X509Certificate)arrayOfCertificate[0];
        } else {
          throw new SSLPeerUnverifiedException("");
        } 
        hostnameChecker.match(str1, x509Certificate);
      } 
      return;
    } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {
    
    } catch (CertificateException certificateException) {}
    if (str2 != null && str2.indexOf("_anon_") != -1)
      return; 
    if (paramHostnameVerifier != null && paramHostnameVerifier
      .verify(str1, this.session))
      return; 
    this.serverSocket.close();
    this.session.invalidate();
    throw new IOException("HTTPS hostname wrong:  should be <" + this.url
        .getHost() + ">");
  }
  
  protected void putInKeepAliveCache() {
    if (this.inCache) {
      assert false : "Duplicate put to keep alive cache";
      return;
    } 
    this.inCache = true;
    kac.put(this.url, this.sslSocketFactory, (HttpClient)this);
  }
  
  public void closeIdleConnection() {
    HttpClient httpClient = kac.get(this.url, this.sslSocketFactory);
    if (httpClient != null)
      httpClient.closeServer(); 
  }
  
  String getCipherSuite() {
    return this.session.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates() {
    return this.session.getLocalCertificates();
  }
  
  Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
    return this.session.getPeerCertificates();
  }
  
  X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
    return this.session.getPeerCertificateChain();
  }
  
  Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal = this.session.getPeerPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      Certificate[] arrayOfCertificate = this.session.getPeerCertificates();
      principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    } 
    return principal;
  }
  
  Principal getLocalPrincipal() {
    Principal principal;
    try {
      principal = this.session.getLocalPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      principal = null;
      Certificate[] arrayOfCertificate = this.session.getLocalCertificates();
      if (arrayOfCertificate != null)
        principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal(); 
    } 
    return principal;
  }
  
  public void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent) {
    this.session = paramHandshakeCompletedEvent.getSession();
  }
  
  public String getProxyHostUsed() {
    if (!needsTunneling())
      return null; 
    return super.getProxyHostUsed();
  }
  
  public int getProxyPortUsed() {
    return (this.proxy == null || this.proxy.type() == Proxy.Type.DIRECT || this.proxy
      .type() == Proxy.Type.SOCKS) ? -1 : ((InetSocketAddress)this.proxy
      .address()).getPort();
  }
}
