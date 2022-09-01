package sun.net.www.protocol.http;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.ResponseCache;
import java.net.SocketPermission;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLPermission;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import sun.misc.JavaNetHttpCookieAccess;
import sun.misc.SharedSecrets;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.ParseUtil;
import sun.net.www.http.ChunkedOutputStream;
import sun.net.www.http.HttpClient;
import sun.net.www.http.PosterOutputStream;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class HttpURLConnection extends HttpURLConnection {
  static String HTTP_CONNECT = "CONNECT";
  
  static final String version;
  
  public static final String userAgent;
  
  static final int defaultmaxRedirects = 20;
  
  static final int maxRedirects;
  
  static final boolean validateProxy;
  
  static final boolean validateServer;
  
  private StreamingOutputStream strOutputStream;
  
  private static final String RETRY_MSG1 = "cannot retry due to proxy authentication, in streaming mode";
  
  private static final String RETRY_MSG2 = "cannot retry due to server authentication, in streaming mode";
  
  private static final String RETRY_MSG3 = "cannot retry due to redirection, in streaming mode";
  
  private static boolean enableESBuffer = false;
  
  private static int timeout4ESBuffer = 0;
  
  private static int bufSize4ES = 0;
  
  private static final boolean allowRestrictedHeaders;
  
  private static final Set<String> restrictedHeaderSet;
  
  private static final String[] restrictedHeaders = new String[] { 
      "Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Content-Transfer-Encoding", "Host", "Keep-Alive", "Origin", "Trailer", "Transfer-Encoding", 
      "Upgrade", "Via" };
  
  static final String httpVersion = "HTTP/1.1";
  
  static final String acceptString = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
  
  private static final String[] EXCLUDE_HEADERS;
  
  private static final String[] EXCLUDE_HEADERS2;
  
  protected HttpClient http;
  
  protected Handler handler;
  
  protected Proxy instProxy;
  
  private CookieHandler cookieHandler;
  
  private final ResponseCache cacheHandler;
  
  protected CacheResponse cachedResponse;
  
  private MessageHeader cachedHeaders;
  
  private InputStream cachedInputStream;
  
  static {
    maxRedirects = ((Integer)AccessController.<Integer>doPrivileged(new GetIntegerAction("http.maxRedirects", 20))).intValue();
    version = AccessController.<String>doPrivileged(new GetPropertyAction("java.version"));
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("http.agent"));
    if (str == null) {
      str = "Java/" + version;
    } else {
      str = str + " Java/" + version;
    } 
    userAgent = str;
    validateProxy = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("http.auth.digest.validateProxy"))).booleanValue();
    validateServer = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("http.auth.digest.validateServer"))).booleanValue();
    enableESBuffer = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("sun.net.http.errorstream.enableBuffering"))).booleanValue();
    timeout4ESBuffer = ((Integer)AccessController.<Integer>doPrivileged(new GetIntegerAction("sun.net.http.errorstream.timeout", 300))).intValue();
    if (timeout4ESBuffer <= 0)
      timeout4ESBuffer = 300; 
    bufSize4ES = ((Integer)AccessController.<Integer>doPrivileged(new GetIntegerAction("sun.net.http.errorstream.bufferSize", 4096))).intValue();
    if (bufSize4ES <= 0)
      bufSize4ES = 4096; 
    allowRestrictedHeaders = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("sun.net.http.allowRestrictedHeaders"))).booleanValue();
    if (!allowRestrictedHeaders) {
      restrictedHeaderSet = new HashSet<>(restrictedHeaders.length);
      for (byte b = 0; b < restrictedHeaders.length; b++)
        restrictedHeaderSet.add(restrictedHeaders[b].toLowerCase()); 
    } else {
      restrictedHeaderSet = null;
    } 
    EXCLUDE_HEADERS = new String[] { "Proxy-Authorization", "Authorization" };
    EXCLUDE_HEADERS2 = new String[] { "Proxy-Authorization", "Authorization", "Cookie", "Cookie2" };
    logger = PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
  }
  
  protected PrintStream ps = null;
  
  private InputStream errorStream = null;
  
  private boolean setUserCookies = true;
  
  private String userCookies = null;
  
  private String userCookies2 = null;
  
  @Deprecated
  private static HttpAuthenticator defaultAuth;
  
  private MessageHeader requests;
  
  private MessageHeader userHeaders;
  
  private boolean connecting = false;
  
  String domain;
  
  DigestAuthentication.Parameters digestparams;
  
  AuthenticationInfo currentProxyCredentials = null;
  
  AuthenticationInfo currentServerCredentials = null;
  
  boolean needToCheck = true;
  
  private boolean doingNTLM2ndStage = false;
  
  private boolean doingNTLMp2ndStage = false;
  
  private boolean tryTransparentNTLMServer = true;
  
  private boolean tryTransparentNTLMProxy = true;
  
  private boolean useProxyResponseCode = false;
  
  private Object authObj;
  
  boolean isUserServerAuth;
  
  boolean isUserProxyAuth;
  
  String serverAuthKey;
  
  String proxyAuthKey;
  
  protected ProgressSource pi;
  
  private MessageHeader responses;
  
  private InputStream inputStream = null;
  
  private PosterOutputStream poster = null;
  
  private boolean setRequests = false;
  
  private boolean failedOnce = false;
  
  private Exception rememberedException = null;
  
  private HttpClient reuseClient = null;
  
  public enum TunnelState {
    NONE, SETUP, TUNNELING;
  }
  
  private TunnelState tunnelState = TunnelState.NONE;
  
  private int connectTimeout = -1;
  
  private int readTimeout = -1;
  
  private SocketPermission socketPermission;
  
  private static final PlatformLogger logger;
  
  String requestURI;
  
  byte[] cdata;
  
  private static final String SET_COOKIE = "set-cookie";
  
  private static final String SET_COOKIE2 = "set-cookie2";
  
  private Map<String, List<String>> filteredHeaders;
  
  private static PasswordAuthentication privilegedRequestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4, URL paramURL, Authenticator.RequestorType paramRequestorType) {
    return AccessController.<PasswordAuthentication>doPrivileged((PrivilegedAction<PasswordAuthentication>)new Object(paramString1, paramURL, paramInetAddress, paramInt, paramString2, paramString3, paramString4, paramRequestorType));
  }
  
  private boolean isRestrictedHeader(String paramString1, String paramString2) {
    if (allowRestrictedHeaders)
      return false; 
    paramString1 = paramString1.toLowerCase();
    if (restrictedHeaderSet.contains(paramString1)) {
      if (paramString1.equals("connection") && paramString2.equalsIgnoreCase("close"))
        return false; 
      return true;
    } 
    if (paramString1.startsWith("sec-"))
      return true; 
    return false;
  }
  
  private boolean isExternalMessageHeaderAllowed(String paramString1, String paramString2) {
    checkMessageHeader(paramString1, paramString2);
    if (!isRestrictedHeader(paramString1, paramString2))
      return true; 
    return false;
  }
  
  public static PlatformLogger getHttpLogger() {
    return logger;
  }
  
  public Object authObj() {
    return this.authObj;
  }
  
  public void authObj(Object paramObject) {
    this.authObj = paramObject;
  }
  
  private void checkMessageHeader(String paramString1, String paramString2) {
    byte b = 10;
    int i = paramString1.indexOf(b);
    if (i != -1)
      throw new IllegalArgumentException("Illegal character(s) in message header field: " + paramString1); 
    if (paramString2 == null)
      return; 
    i = paramString2.indexOf(b);
    while (i != -1) {
      i++;
      if (i < paramString2.length()) {
        char c = paramString2.charAt(i);
        if (c == ' ' || c == '\t') {
          i = paramString2.indexOf(b, i);
          continue;
        } 
      } 
      throw new IllegalArgumentException("Illegal character(s) in message header value: " + paramString2);
    } 
  }
  
  public synchronized void setRequestMethod(String paramString) throws ProtocolException {
    if (this.connecting)
      throw new IllegalStateException("connect in progress"); 
    super.setRequestMethod(paramString);
  }
  
  private void writeRequests() throws IOException {
    if (this.http.usingProxy && tunnelState() != TunnelState.TUNNELING)
      setPreemptiveProxyAuthentication(this.requests); 
    if (!this.setRequests) {
      if (!this.failedOnce) {
        checkURLFile();
        this.requests.prepend(this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
      } 
      if (!getUseCaches()) {
        this.requests.setIfNotSet("Cache-Control", "no-cache");
        this.requests.setIfNotSet("Pragma", "no-cache");
      } 
      this.requests.setIfNotSet("User-Agent", userAgent);
      int i = this.url.getPort();
      String str1 = this.url.getHost();
      if (i != -1 && i != this.url.getDefaultPort())
        str1 = str1 + ":" + String.valueOf(i); 
      String str2 = this.requests.findValue("Host");
      if (str2 == null || (
        !str2.equalsIgnoreCase(str1) && !checkSetHost()))
        this.requests.set("Host", str1); 
      this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
      if (!this.failedOnce && this.http.getHttpKeepAliveSet()) {
        if (this.http.usingProxy && tunnelState() != TunnelState.TUNNELING) {
          this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
        } else {
          this.requests.setIfNotSet("Connection", "keep-alive");
        } 
      } else {
        this.requests.setIfNotSet("Connection", "close");
      } 
      long l = getIfModifiedSince();
      if (l != 0L) {
        Date date = new Date(l);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.requests.setIfNotSet("If-Modified-Since", simpleDateFormat.format(date));
      } 
      AuthenticationInfo authenticationInfo = AuthenticationInfo.getServerAuth(this.url);
      if (authenticationInfo != null && authenticationInfo.supportsPreemptiveAuthorization()) {
        this.requests.setIfNotSet(authenticationInfo.getHeaderName(), authenticationInfo.getHeaderValue(this.url, this.method));
        this.currentServerCredentials = authenticationInfo;
      } 
      if (!this.method.equals("PUT") && (this.poster != null || streaming()))
        this.requests.setIfNotSet("Content-type", "application/x-www-form-urlencoded"); 
      boolean bool = false;
      if (streaming()) {
        if (this.chunkLength != -1) {
          this.requests.set("Transfer-Encoding", "chunked");
          bool = true;
        } else if (this.fixedContentLengthLong != -1L) {
          this.requests.set("Content-Length", 
              String.valueOf(this.fixedContentLengthLong));
        } else if (this.fixedContentLength != -1) {
          this.requests.set("Content-Length", 
              String.valueOf(this.fixedContentLength));
        } 
      } else if (this.poster != null) {
        synchronized (this.poster) {
          this.poster.close();
          this.requests.set("Content-Length", 
              String.valueOf(this.poster.size()));
        } 
      } 
      if (!bool && 
        this.requests.findValue("Transfer-Encoding") != null) {
        this.requests.remove("Transfer-Encoding");
        if (logger.isLoggable(PlatformLogger.Level.WARNING))
          logger.warning("use streaming mode for chunked encoding"); 
      } 
      setCookieHeader();
      this.setRequests = true;
    } 
    if (logger.isLoggable(PlatformLogger.Level.FINE))
      logger.fine(this.requests.toString()); 
    this.http.writeRequests(this.requests, this.poster, streaming());
    if (this.ps.checkError()) {
      String str = this.http.getProxyHostUsed();
      int i = this.http.getProxyPortUsed();
      disconnectInternal();
      if (this.failedOnce)
        throw new IOException("Error writing to server"); 
      this.failedOnce = true;
      if (str != null) {
        setProxiedClient(this.url, str, i);
      } else {
        setNewClient(this.url);
      } 
      this.ps = (PrintStream)this.http.getOutputStream();
      this.connected = true;
      this.responses = new MessageHeader();
      this.setRequests = false;
      writeRequests();
    } 
  }
  
  private boolean checkSetHost() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      String str = securityManager.getClass().getName();
      if (str.equals("sun.plugin2.applet.AWTAppletSecurityManager") || str
        .equals("sun.plugin2.applet.FXAppletSecurityManager") || str
        .equals("com.sun.javaws.security.JavaWebStartSecurity") || str
        .equals("sun.plugin.security.ActivatorSecurityManager")) {
        byte b = -2;
        try {
          securityManager.checkConnect(this.url.toExternalForm(), b);
        } catch (SecurityException securityException) {
          return false;
        } 
      } 
    } 
    return true;
  }
  
  private void checkURLFile() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      String str = securityManager.getClass().getName();
      if (str.equals("sun.plugin2.applet.AWTAppletSecurityManager") || str
        .equals("sun.plugin2.applet.FXAppletSecurityManager") || str
        .equals("com.sun.javaws.security.JavaWebStartSecurity") || str
        .equals("sun.plugin.security.ActivatorSecurityManager")) {
        byte b = -3;
        try {
          securityManager.checkConnect(this.url.toExternalForm(), b);
        } catch (SecurityException securityException) {
          throw new SecurityException("denied access outside a permitted URL subpath", securityException);
        } 
      } 
    } 
  }
  
  protected void setNewClient(URL paramURL) throws IOException {
    setNewClient(paramURL, false);
  }
  
  protected void setNewClient(URL paramURL, boolean paramBoolean) throws IOException {
    this.http = HttpClient.New(paramURL, (String)null, -1, paramBoolean, this.connectTimeout, this);
    this.http.setReadTimeout(this.readTimeout);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt) throws IOException {
    setProxiedClient(paramURL, paramString, paramInt, false);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    proxiedConnect(paramURL, paramString, paramInt, paramBoolean);
  }
  
  protected void proxiedConnect(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    this.http = HttpClient.New(paramURL, paramString, paramInt, paramBoolean, this.connectTimeout, this);
    this.http.setReadTimeout(this.readTimeout);
  }
  
  protected HttpURLConnection(URL paramURL, Handler paramHandler) throws IOException {
    this(paramURL, (Proxy)null, paramHandler);
  }
  
  public HttpURLConnection(URL paramURL, String paramString, int paramInt) {
    this(paramURL, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(paramString, paramInt)));
  }
  
  public HttpURLConnection(URL paramURL, Proxy paramProxy) {
    this(paramURL, paramProxy, new Handler());
  }
  
  protected HttpURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler) {
    super(paramURL);
    this.requestURI = null;
    this.cdata = new byte[128];
    this.requests = new MessageHeader();
    this.responses = new MessageHeader();
    this.userHeaders = new MessageHeader();
    this.handler = paramHandler;
    this.instProxy = paramProxy;
    if (this.instProxy instanceof sun.net.ApplicationProxy) {
      try {
        this.cookieHandler = CookieHandler.getDefault();
      } catch (SecurityException securityException) {}
    } else {
      this.cookieHandler = AccessController.<CookieHandler>doPrivileged(new PrivilegedAction<CookieHandler>() {
            public CookieHandler run() {
              return CookieHandler.getDefault();
            }
          });
    } 
    this.cacheHandler = AccessController.<ResponseCache>doPrivileged(new PrivilegedAction<ResponseCache>() {
          public ResponseCache run() {
            return ResponseCache.getDefault();
          }
        });
  }
  
  @Deprecated
  public static void setDefaultAuthenticator(HttpAuthenticator paramHttpAuthenticator) {
    defaultAuth = paramHttpAuthenticator;
  }
  
  public static InputStream openConnectionCheckRedirects(URLConnection paramURLConnection) throws IOException {
    byte b = 0;
    while (true) {
      if (paramURLConnection instanceof HttpURLConnection)
        ((HttpURLConnection)paramURLConnection).setInstanceFollowRedirects(false); 
      InputStream inputStream = paramURLConnection.getInputStream();
      boolean bool = false;
      if (paramURLConnection instanceof HttpURLConnection) {
        HttpURLConnection httpURLConnection = (HttpURLConnection)paramURLConnection;
        int i = httpURLConnection.getResponseCode();
        if (i >= 300 && i <= 307 && i != 306 && i != 304) {
          URL uRL1 = httpURLConnection.getURL();
          String str = httpURLConnection.getHeaderField("Location");
          URL uRL2 = null;
          if (str != null)
            uRL2 = new URL(uRL1, str); 
          httpURLConnection.disconnect();
          if (uRL2 == null || !uRL1.getProtocol().equals(uRL2.getProtocol()) || uRL1.getPort() != uRL2.getPort() || !hostsEqual(uRL1, uRL2) || b >= 5)
            throw new SecurityException("illegal URL redirect"); 
          bool = true;
          paramURLConnection = uRL2.openConnection();
          b++;
        } 
      } 
      if (!bool)
        return inputStream; 
    } 
  }
  
  private static boolean hostsEqual(URL paramURL1, URL paramURL2) {
    String str1 = paramURL1.getHost();
    String str2 = paramURL2.getHost();
    if (str1 == null)
      return (str2 == null); 
    if (str2 == null)
      return false; 
    if (str1.equalsIgnoreCase(str2))
      return true; 
    boolean[] arrayOfBoolean = { false };
    AccessController.doPrivileged((PrivilegedAction<?>)new Object(str1, str2, arrayOfBoolean));
    return arrayOfBoolean[0];
  }
  
  public void connect() throws IOException {
    synchronized (this) {
      this.connecting = true;
    } 
    plainConnect();
  }
  
  private boolean checkReuseConnection() {
    if (this.connected)
      return true; 
    if (this.reuseClient != null) {
      this.http = this.reuseClient;
      this.http.setReadTimeout(getReadTimeout());
      this.http.reuse = false;
      this.reuseClient = null;
      this.connected = true;
      return true;
    } 
    return false;
  }
  
  private String getHostAndPort(URL paramURL) {
    String str1 = paramURL.getHost();
    String str2 = str1;
    try {
      str1 = AccessController.<String>doPrivileged((PrivilegedExceptionAction<String>)new Object(this, str2));
    } catch (PrivilegedActionException privilegedActionException) {}
    int i = paramURL.getPort();
    if (i == -1) {
      String str = paramURL.getProtocol();
      if ("http".equals(str))
        return str1 + ":80"; 
      return str1 + ":443";
    } 
    return str1 + ":" + Integer.toString(i);
  }
  
  protected void plainConnect() throws IOException {
    synchronized (this) {
      if (this.connected)
        return; 
    } 
    SocketPermission socketPermission = URLtoSocketPermission(this.url);
    if (socketPermission != null) {
      try {
        AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Object(this), (AccessControlContext)null, new Permission[] { socketPermission });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      } 
    } else {
      plainConnect0();
    } 
  }
  
  SocketPermission URLtoSocketPermission(URL paramURL) throws IOException {
    if (this.socketPermission != null)
      return this.socketPermission; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return null; 
    SocketPermission socketPermission = new SocketPermission(getHostAndPort(paramURL), "connect");
    String str1 = getRequestMethod() + ":" + getUserSetHeaders().getHeaderNamesInList();
    String str2 = paramURL.getProtocol() + "://" + paramURL.getAuthority() + paramURL.getPath();
    URLPermission uRLPermission = new URLPermission(str2, str1);
    try {
      securityManager.checkPermission(uRLPermission);
      this.socketPermission = socketPermission;
      return this.socketPermission;
    } catch (SecurityException securityException) {
      return null;
    } 
  }
  
  protected void plainConnect0() throws IOException {
    if (this.cacheHandler != null && getUseCaches()) {
      try {
        URI uRI = ParseUtil.toURI(this.url);
        if (uRI != null) {
          this.cachedResponse = this.cacheHandler.get(uRI, getRequestMethod(), getUserSetHeaders().getHeaders());
          if ("https".equalsIgnoreCase(uRI.getScheme()) && !(this.cachedResponse instanceof java.net.SecureCacheResponse))
            this.cachedResponse = null; 
          if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
            logger.finest("Cache Request for " + uRI + " / " + getRequestMethod());
            logger.finest("From cache: " + ((this.cachedResponse != null) ? this.cachedResponse.toString() : "null"));
          } 
          if (this.cachedResponse != null) {
            this.cachedHeaders = mapToMessageHeader(this.cachedResponse.getHeaders());
            this.cachedInputStream = this.cachedResponse.getBody();
          } 
        } 
      } catch (IOException iOException) {}
      if (this.cachedHeaders != null && this.cachedInputStream != null) {
        this.connected = true;
        return;
      } 
      this.cachedResponse = null;
    } 
    try {
      if (this.instProxy == null) {
        ProxySelector proxySelector = AccessController.<ProxySelector>doPrivileged(new PrivilegedAction<ProxySelector>() {
              public ProxySelector run() {
                return ProxySelector.getDefault();
              }
            });
        if (proxySelector != null) {
          URI uRI = ParseUtil.toURI(this.url);
          if (logger.isLoggable(PlatformLogger.Level.FINEST))
            logger.finest("ProxySelector Request for " + uRI); 
          Iterator<Proxy> iterator = proxySelector.select(uRI).iterator();
          while (iterator.hasNext()) {
            Proxy proxy = iterator.next();
            try {
              if (!this.failedOnce) {
                this.http = getNewHttpClient(this.url, proxy, this.connectTimeout);
                this.http.setReadTimeout(this.readTimeout);
              } else {
                this.http = getNewHttpClient(this.url, proxy, this.connectTimeout, false);
                this.http.setReadTimeout(this.readTimeout);
              } 
              if (logger.isLoggable(PlatformLogger.Level.FINEST) && proxy != null)
                logger.finest("Proxy used: " + proxy.toString()); 
            } catch (IOException iOException) {
              if (proxy != Proxy.NO_PROXY) {
                proxySelector.connectFailed(uRI, proxy.address(), iOException);
                if (!iterator.hasNext()) {
                  this.http = getNewHttpClient(this.url, (Proxy)null, this.connectTimeout, false);
                  this.http.setReadTimeout(this.readTimeout);
                  break;
                } 
                continue;
              } 
              throw iOException;
            } 
          } 
        } else if (!this.failedOnce) {
          this.http = getNewHttpClient(this.url, (Proxy)null, this.connectTimeout);
          this.http.setReadTimeout(this.readTimeout);
        } else {
          this.http = getNewHttpClient(this.url, (Proxy)null, this.connectTimeout, false);
          this.http.setReadTimeout(this.readTimeout);
        } 
      } else if (!this.failedOnce) {
        this.http = getNewHttpClient(this.url, this.instProxy, this.connectTimeout);
        this.http.setReadTimeout(this.readTimeout);
      } else {
        this.http = getNewHttpClient(this.url, this.instProxy, this.connectTimeout, false);
        this.http.setReadTimeout(this.readTimeout);
      } 
      this.ps = (PrintStream)this.http.getOutputStream();
    } catch (IOException iOException) {
      throw iOException;
    } 
    this.connected = true;
  }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt) throws IOException {
    return HttpClient.New(paramURL, paramProxy, paramInt, this);
  }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean) throws IOException {
    return HttpClient.New(paramURL, paramProxy, paramInt, paramBoolean, this);
  }
  
  private void expect100Continue() throws IOException {
    int i = this.http.getReadTimeout();
    boolean bool1 = false;
    boolean bool2 = false;
    if (i <= 0) {
      this.http.setReadTimeout(5000);
      bool1 = true;
    } 
    try {
      this.http.parseHTTP(this.responses, this.pi, this);
    } catch (SocketTimeoutException socketTimeoutException) {
      if (!bool1)
        throw socketTimeoutException; 
      bool2 = true;
      this.http.setIgnoreContinue(true);
    } 
    if (!bool2) {
      String str = this.responses.getValue(0);
      if (str != null && str.startsWith("HTTP/")) {
        String[] arrayOfString = str.split("\\s+");
        this.responseCode = -1;
        try {
          if (arrayOfString.length > 1)
            this.responseCode = Integer.parseInt(arrayOfString[1]); 
        } catch (NumberFormatException numberFormatException) {}
      } 
      if (this.responseCode != 100)
        throw new ProtocolException("Server rejected operation"); 
    } 
    this.http.setReadTimeout(i);
    this.responseCode = -1;
    this.responses.reset();
  }
  
  public synchronized OutputStream getOutputStream() throws IOException {
    this.connecting = true;
    SocketPermission socketPermission = URLtoSocketPermission(this.url);
    if (socketPermission != null)
      try {
        return AccessController.<OutputStream>doPrivileged((PrivilegedExceptionAction<OutputStream>)new Object(this), (AccessControlContext)null, new Permission[] { socketPermission });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      }  
    return getOutputStream0();
  }
  
  private synchronized OutputStream getOutputStream0() throws IOException {
    try {
      if (!this.doOutput)
        throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)"); 
      if (this.method.equals("GET"))
        this.method = "POST"; 
      if ("TRACE".equals(this.method) && "http".equals(this.url.getProtocol()))
        throw new ProtocolException("HTTP method TRACE doesn't support output"); 
      if (this.inputStream != null)
        throw new ProtocolException("Cannot write output after reading input."); 
      if (!checkReuseConnection())
        connect(); 
      boolean bool = false;
      String str = this.requests.findValue("Expect");
      if ("100-Continue".equalsIgnoreCase(str) && streaming()) {
        this.http.setIgnoreContinue(false);
        bool = true;
      } 
      if (streaming() && this.strOutputStream == null)
        writeRequests(); 
      if (bool)
        expect100Continue(); 
      this.ps = (PrintStream)this.http.getOutputStream();
      if (streaming()) {
        if (this.strOutputStream == null)
          if (this.chunkLength != -1) {
            this.strOutputStream = new StreamingOutputStream(this, new ChunkedOutputStream(this.ps, this.chunkLength), -1L);
          } else {
            long l = 0L;
            if (this.fixedContentLengthLong != -1L) {
              l = this.fixedContentLengthLong;
            } else if (this.fixedContentLength != -1) {
              l = this.fixedContentLength;
            } 
            this.strOutputStream = new StreamingOutputStream(this, this.ps, l);
          }  
        return this.strOutputStream;
      } 
      if (this.poster == null)
        this.poster = new PosterOutputStream(); 
      return this.poster;
    } catch (RuntimeException runtimeException) {
      disconnectInternal();
      throw runtimeException;
    } catch (ProtocolException protocolException) {
      int i = this.responseCode;
      disconnectInternal();
      this.responseCode = i;
      throw protocolException;
    } catch (IOException iOException) {
      disconnectInternal();
      throw iOException;
    } 
  }
  
  public boolean streaming() {
    return (this.fixedContentLength != -1 || this.fixedContentLengthLong != -1L || this.chunkLength != -1);
  }
  
  private void setCookieHeader() throws IOException {
    if (this.cookieHandler != null) {
      synchronized (this) {
        if (this.setUserCookies) {
          int i = this.requests.getKey("Cookie");
          if (i != -1)
            this.userCookies = this.requests.getValue(i); 
          i = this.requests.getKey("Cookie2");
          if (i != -1)
            this.userCookies2 = this.requests.getValue(i); 
          this.setUserCookies = false;
        } 
      } 
      this.requests.remove("Cookie");
      this.requests.remove("Cookie2");
      URI uRI = ParseUtil.toURI(this.url);
      if (uRI != null) {
        if (logger.isLoggable(PlatformLogger.Level.FINEST))
          logger.finest("CookieHandler request for " + uRI); 
        Map<String, List<String>> map = this.cookieHandler.get(uRI, this.requests.getHeaders(EXCLUDE_HEADERS));
        if (!map.isEmpty()) {
          if (logger.isLoggable(PlatformLogger.Level.FINEST))
            logger.finest("Cookies retrieved: " + map.toString()); 
          for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String str = (String)entry.getKey();
            if (!"Cookie".equalsIgnoreCase(str) && !"Cookie2".equalsIgnoreCase(str))
              continue; 
            List list = (List)entry.getValue();
            if (list != null && !list.isEmpty()) {
              StringBuilder stringBuilder = new StringBuilder();
              for (String str1 : list)
                stringBuilder.append(str1).append("; "); 
              try {
                this.requests.add(str, stringBuilder.substring(0, stringBuilder.length() - 2));
              } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
            } 
          } 
        } 
      } 
      if (this.userCookies != null) {
        int i;
        if ((i = this.requests.getKey("Cookie")) != -1) {
          this.requests.set("Cookie", this.requests.getValue(i) + ";" + this.userCookies);
        } else {
          this.requests.set("Cookie", this.userCookies);
        } 
      } 
      if (this.userCookies2 != null) {
        int i;
        if ((i = this.requests.getKey("Cookie2")) != -1) {
          this.requests.set("Cookie2", this.requests.getValue(i) + ";" + this.userCookies2);
        } else {
          this.requests.set("Cookie2", this.userCookies2);
        } 
      } 
    } 
  }
  
  public synchronized InputStream getInputStream() throws IOException {
    this.connecting = true;
    SocketPermission socketPermission = URLtoSocketPermission(this.url);
    if (socketPermission != null)
      try {
        return AccessController.<InputStream>doPrivileged((PrivilegedExceptionAction<InputStream>)new Object(this), (AccessControlContext)null, new Permission[] { socketPermission });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      }  
    return getInputStream0();
  }
  
  private synchronized InputStream getInputStream0() throws IOException {
    if (!this.doInput)
      throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))"); 
    if (this.rememberedException != null) {
      if (this.rememberedException instanceof RuntimeException)
        throw new RuntimeException(this.rememberedException); 
      throw getChainedException((IOException)this.rememberedException);
    } 
    if (this.inputStream != null)
      return this.inputStream; 
    if (streaming()) {
      if (this.strOutputStream == null)
        getOutputStream(); 
      this.strOutputStream.close();
      if (!this.strOutputStream.writtenOK())
        throw new IOException("Incomplete output stream"); 
    } 
    byte b = 0;
    int i = 0;
    long l = -1L;
    AuthenticationInfo authenticationInfo1 = null;
    AuthenticationInfo authenticationInfo2 = null;
    AuthenticationHeader authenticationHeader = null;
    boolean bool1 = false;
    boolean bool2 = false;
    this.isUserServerAuth = (this.requests.getKey("Authorization") != -1);
    this.isUserProxyAuth = (this.requests.getKey("Proxy-Authorization") != -1);
    try {
      do {
        if (!checkReuseConnection())
          connect(); 
        if (this.cachedInputStream != null)
          return this.cachedInputStream; 
        boolean bool3 = ProgressMonitor.getDefault().shouldMeterInput(this.url, this.method);
        if (bool3) {
          this.pi = new ProgressSource(this.url, this.method);
          this.pi.beginTracking();
        } 
        this.ps = (PrintStream)this.http.getOutputStream();
        if (!streaming())
          writeRequests(); 
        this.http.parseHTTP(this.responses, this.pi, this);
        if (logger.isLoggable(PlatformLogger.Level.FINE))
          logger.fine(this.responses.toString()); 
        boolean bool4 = this.responses.filterNTLMResponses("WWW-Authenticate");
        boolean bool5 = this.responses.filterNTLMResponses("Proxy-Authenticate");
        if ((bool4 || bool5) && logger.isLoggable(PlatformLogger.Level.FINE)) {
          logger.fine(">>>> Headers are filtered");
          logger.fine(this.responses.toString());
        } 
        this.inputStream = this.http.getInputStream();
        i = getResponseCode();
        if (i == -1) {
          disconnectInternal();
          throw new IOException("Invalid Http response");
        } 
        if (i == 407) {
          if (streaming()) {
            disconnectInternal();
            throw new HttpRetryException("cannot retry due to proxy authentication, in streaming mode", 407);
          } 
          boolean bool = false;
          Iterator<String> iterator = this.responses.multiValueIterator("Proxy-Authenticate");
          while (iterator.hasNext()) {
            String str = ((String)iterator.next()).trim();
            if (str.equalsIgnoreCase("Negotiate") || str.equalsIgnoreCase("Kerberos")) {
              if (!bool2) {
                bool2 = true;
                break;
              } 
              bool = true;
              this.doingNTLMp2ndStage = false;
              authenticationInfo2 = null;
              break;
            } 
          } 
          AuthenticationHeader authenticationHeader1 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), bool);
          if (!this.doingNTLMp2ndStage) {
            authenticationInfo2 = resetProxyAuthentication(authenticationInfo2, authenticationHeader1);
            if (authenticationInfo2 != null) {
              b++;
              disconnectInternal();
              continue;
            } 
          } else {
            String str = this.responses.findValue("Proxy-Authenticate");
            reset();
            if (!authenticationInfo2.setHeaders(this, authenticationHeader1.headerParser(), str)) {
              disconnectInternal();
              throw new IOException("Authentication failure");
            } 
            if (authenticationInfo1 != null && authenticationHeader != null && !authenticationInfo1.setHeaders(this, authenticationHeader.headerParser(), str)) {
              disconnectInternal();
              throw new IOException("Authentication failure");
            } 
            this.authObj = null;
            this.doingNTLMp2ndStage = false;
            continue;
          } 
        } else {
          bool2 = false;
          this.doingNTLMp2ndStage = false;
          if (!this.isUserProxyAuth)
            this.requests.remove("Proxy-Authorization"); 
        } 
        if (authenticationInfo2 != null)
          authenticationInfo2.addToCache(); 
        if (i == 401) {
          if (streaming()) {
            disconnectInternal();
            throw new HttpRetryException("cannot retry due to server authentication, in streaming mode", 401);
          } 
          boolean bool = false;
          Iterator<String> iterator = this.responses.multiValueIterator("WWW-Authenticate");
          while (iterator.hasNext()) {
            String str1 = ((String)iterator.next()).trim();
            if (str1.equalsIgnoreCase("Negotiate") || str1.equalsIgnoreCase("Kerberos")) {
              if (!bool1) {
                bool1 = true;
                break;
              } 
              bool = true;
              this.doingNTLM2ndStage = false;
              authenticationInfo1 = null;
              break;
            } 
          } 
          authenticationHeader = new AuthenticationHeader("WWW-Authenticate", this.responses, new HttpCallerInfo(this.url), bool);
          String str = authenticationHeader.raw();
          if (!this.doingNTLM2ndStage) {
            if (authenticationInfo1 != null && authenticationInfo1.getAuthScheme() != AuthScheme.NTLM) {
              if (authenticationInfo1.isAuthorizationStale(str)) {
                disconnectWeb();
                b++;
                this.requests.set(authenticationInfo1.getHeaderName(), authenticationInfo1.getHeaderValue(this.url, this.method));
                this.currentServerCredentials = authenticationInfo1;
                setCookieHeader();
                continue;
              } 
              authenticationInfo1.removeFromCache();
            } 
            authenticationInfo1 = getServerAuthentication(authenticationHeader);
            this.currentServerCredentials = authenticationInfo1;
            if (authenticationInfo1 != null) {
              disconnectWeb();
              b++;
              setCookieHeader();
              continue;
            } 
          } else {
            reset();
            if (!authenticationInfo1.setHeaders(this, (HeaderParser)null, str)) {
              disconnectWeb();
              throw new IOException("Authentication failure");
            } 
            this.doingNTLM2ndStage = false;
            this.authObj = null;
            setCookieHeader();
            continue;
          } 
        } 
        if (authenticationInfo1 != null)
          if (!(authenticationInfo1 instanceof DigestAuthentication) || this.domain == null) {
            if (authenticationInfo1 instanceof BasicAuthentication) {
              String str1 = AuthenticationInfo.reducePath(this.url.getPath());
              String str2 = authenticationInfo1.path;
              if (!str2.startsWith(str1) || str1.length() >= str2.length())
                str1 = BasicAuthentication.getRootPath(str2, str1); 
              BasicAuthentication basicAuthentication = (BasicAuthentication)authenticationInfo1.clone();
              authenticationInfo1.removeFromCache();
              basicAuthentication.path = str1;
              authenticationInfo1 = basicAuthentication;
            } 
            authenticationInfo1.addToCache();
          } else {
            DigestAuthentication digestAuthentication = (DigestAuthentication)authenticationInfo1;
            StringTokenizer stringTokenizer = new StringTokenizer(this.domain, " ");
            String str = digestAuthentication.realm;
            PasswordAuthentication passwordAuthentication = digestAuthentication.pw;
            this.digestparams = digestAuthentication.params;
            while (stringTokenizer.hasMoreTokens()) {
              String str1 = stringTokenizer.nextToken();
              try {
                URL uRL = new URL(this.url, str1);
                DigestAuthentication digestAuthentication1 = new DigestAuthentication(false, uRL, str, "Digest", passwordAuthentication, this.digestparams);
                digestAuthentication1.addToCache();
              } catch (Exception exception) {}
            } 
          }  
        bool1 = false;
        bool2 = false;
        this.doingNTLMp2ndStage = false;
        this.doingNTLM2ndStage = false;
        if (!this.isUserServerAuth)
          this.requests.remove("Authorization"); 
        if (!this.isUserProxyAuth)
          this.requests.remove("Proxy-Authorization"); 
        if (i == 200) {
          checkResponseCredentials(false);
        } else {
          this.needToCheck = false;
        } 
        this.needToCheck = true;
        if (followRedirect()) {
          b++;
          setCookieHeader();
        } else {
          try {
            l = Long.parseLong(this.responses.findValue("content-length"));
          } catch (Exception exception) {}
          if (this.method.equals("HEAD") || l == 0L || i == 304 || i == 204) {
            if (this.pi != null) {
              this.pi.finishTracking();
              this.pi = null;
            } 
            this.http.finished();
            this.http = null;
            this.inputStream = new EmptyInputStream();
            this.connected = false;
          } 
          if (i == 200 || i == 203 || i == 206 || i == 300 || i == 301 || i == 410)
            if (this.cacheHandler != null && getUseCaches()) {
              URI uRI = ParseUtil.toURI(this.url);
              if (uRI != null) {
                URLConnection uRLConnection;
                HttpURLConnection httpURLConnection = this;
                if ("https".equalsIgnoreCase(uRI.getScheme()))
                  try {
                    uRLConnection = (URLConnection)getClass().getField("httpsURLConnection").get(this);
                  } catch (IllegalAccessException|NoSuchFieldException illegalAccessException) {} 
                CacheRequest cacheRequest = this.cacheHandler.put(uRI, uRLConnection);
                if (cacheRequest != null && this.http != null) {
                  this.http.setCacheRequest(cacheRequest);
                  this.inputStream = new HttpInputStream(this.inputStream, cacheRequest);
                } 
              } 
            }  
          if (!(this.inputStream instanceof HttpInputStream))
            this.inputStream = new HttpInputStream(this.inputStream); 
          if (i >= 400) {
            if (i == 404 || i == 410)
              throw new FileNotFoundException(this.url.toString()); 
            throw new IOException("Server returned HTTP response code: " + i + " for URL: " + this.url.toString());
          } 
          this.poster = null;
          this.strOutputStream = null;
          return this.inputStream;
        } 
      } while (b < maxRedirects);
      throw new ProtocolException("Server redirected too many  times (" + b + ")");
    } catch (RuntimeException runtimeException) {
      disconnectInternal();
      this.rememberedException = runtimeException;
      throw runtimeException;
    } catch (IOException iOException) {
      this.rememberedException = iOException;
      String str = this.responses.findValue("Transfer-Encoding");
      if (this.http != null && this.http.isKeepingAlive() && enableESBuffer && (l > 0L || (str != null && str.equalsIgnoreCase("chunked"))))
        this.errorStream = ErrorStream.getErrorStream(this.inputStream, l, this.http); 
      throw iOException;
    } finally {
      if (this.proxyAuthKey != null)
        AuthenticationInfo.endAuthRequest(this.proxyAuthKey); 
      if (this.serverAuthKey != null)
        AuthenticationInfo.endAuthRequest(this.serverAuthKey); 
    } 
  }
  
  private IOException getChainedException(IOException paramIOException) {
    try {
      Object[] arrayOfObject = { paramIOException.getMessage() };
      IOException iOException = AccessController.<IOException>doPrivileged((PrivilegedExceptionAction<IOException>)new Object(this, paramIOException, arrayOfObject));
      iOException.initCause(paramIOException);
      return iOException;
    } catch (Exception exception) {
      return paramIOException;
    } 
  }
  
  public InputStream getErrorStream() {
    if (this.connected && this.responseCode >= 400) {
      if (this.errorStream != null)
        return this.errorStream; 
      if (this.inputStream != null)
        return this.inputStream; 
    } 
    return null;
  }
  
  private AuthenticationInfo resetProxyAuthentication(AuthenticationInfo paramAuthenticationInfo, AuthenticationHeader paramAuthenticationHeader) throws IOException {
    if (paramAuthenticationInfo != null && paramAuthenticationInfo.getAuthScheme() != AuthScheme.NTLM) {
      String str = paramAuthenticationHeader.raw();
      if (paramAuthenticationInfo.isAuthorizationStale(str)) {
        String str1;
        if (paramAuthenticationInfo instanceof DigestAuthentication) {
          DigestAuthentication digestAuthentication = (DigestAuthentication)paramAuthenticationInfo;
          if (tunnelState() == TunnelState.SETUP) {
            str1 = digestAuthentication.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
          } else {
            str1 = digestAuthentication.getHeaderValue(getRequestURI(), this.method);
          } 
        } else {
          str1 = paramAuthenticationInfo.getHeaderValue(this.url, this.method);
        } 
        this.requests.set(paramAuthenticationInfo.getHeaderName(), str1);
        this.currentProxyCredentials = paramAuthenticationInfo;
        return paramAuthenticationInfo;
      } 
      paramAuthenticationInfo.removeFromCache();
    } 
    paramAuthenticationInfo = getHttpProxyAuthentication(paramAuthenticationHeader);
    this.currentProxyCredentials = paramAuthenticationInfo;
    return paramAuthenticationInfo;
  }
  
  TunnelState tunnelState() {
    return this.tunnelState;
  }
  
  public void setTunnelState(TunnelState paramTunnelState) {
    this.tunnelState = paramTunnelState;
  }
  
  public synchronized void doTunneling() throws IOException {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: ldc_w ''
    //   5: astore_2
    //   6: iconst_0
    //   7: istore_3
    //   8: aconst_null
    //   9: astore #4
    //   11: aconst_null
    //   12: astore #5
    //   14: iconst_m1
    //   15: istore #6
    //   17: aload_0
    //   18: getfield requests : Lsun/net/www/MessageHeader;
    //   21: astore #7
    //   23: aload_0
    //   24: new sun/net/www/MessageHeader
    //   27: dup
    //   28: invokespecial <init> : ()V
    //   31: putfield requests : Lsun/net/www/MessageHeader;
    //   34: iconst_0
    //   35: istore #8
    //   37: aload_0
    //   38: getstatic sun/net/www/protocol/http/HttpURLConnection$TunnelState.SETUP : Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;
    //   41: invokevirtual setTunnelState : (Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;)V
    //   44: aload_0
    //   45: invokespecial checkReuseConnection : ()Z
    //   48: ifne -> 64
    //   51: aload_0
    //   52: aload_0
    //   53: getfield url : Ljava/net/URL;
    //   56: aload #5
    //   58: iload #6
    //   60: iconst_0
    //   61: invokevirtual proxiedConnect : (Ljava/net/URL;Ljava/lang/String;IZ)V
    //   64: aload_0
    //   65: invokespecial sendCONNECTRequest : ()V
    //   68: aload_0
    //   69: getfield responses : Lsun/net/www/MessageHeader;
    //   72: invokevirtual reset : ()V
    //   75: aload_0
    //   76: getfield http : Lsun/net/www/http/HttpClient;
    //   79: aload_0
    //   80: getfield responses : Lsun/net/www/MessageHeader;
    //   83: aconst_null
    //   84: aload_0
    //   85: invokevirtual parseHTTP : (Lsun/net/www/MessageHeader;Lsun/net/ProgressSource;Lsun/net/www/protocol/http/HttpURLConnection;)Z
    //   88: pop
    //   89: getstatic sun/net/www/protocol/http/HttpURLConnection.logger : Lsun/util/logging/PlatformLogger;
    //   92: getstatic sun/util/logging/PlatformLogger$Level.FINE : Lsun/util/logging/PlatformLogger$Level;
    //   95: invokevirtual isLoggable : (Lsun/util/logging/PlatformLogger$Level;)Z
    //   98: ifeq -> 114
    //   101: getstatic sun/net/www/protocol/http/HttpURLConnection.logger : Lsun/util/logging/PlatformLogger;
    //   104: aload_0
    //   105: getfield responses : Lsun/net/www/MessageHeader;
    //   108: invokevirtual toString : ()Ljava/lang/String;
    //   111: invokevirtual fine : (Ljava/lang/String;)V
    //   114: aload_0
    //   115: getfield responses : Lsun/net/www/MessageHeader;
    //   118: ldc_w 'Proxy-Authenticate'
    //   121: invokevirtual filterNTLMResponses : (Ljava/lang/String;)Z
    //   124: ifeq -> 161
    //   127: getstatic sun/net/www/protocol/http/HttpURLConnection.logger : Lsun/util/logging/PlatformLogger;
    //   130: getstatic sun/util/logging/PlatformLogger$Level.FINE : Lsun/util/logging/PlatformLogger$Level;
    //   133: invokevirtual isLoggable : (Lsun/util/logging/PlatformLogger$Level;)Z
    //   136: ifeq -> 161
    //   139: getstatic sun/net/www/protocol/http/HttpURLConnection.logger : Lsun/util/logging/PlatformLogger;
    //   142: ldc_w '>>>> Headers are filtered'
    //   145: invokevirtual fine : (Ljava/lang/String;)V
    //   148: getstatic sun/net/www/protocol/http/HttpURLConnection.logger : Lsun/util/logging/PlatformLogger;
    //   151: aload_0
    //   152: getfield responses : Lsun/net/www/MessageHeader;
    //   155: invokevirtual toString : ()Ljava/lang/String;
    //   158: invokevirtual fine : (Ljava/lang/String;)V
    //   161: aload_0
    //   162: getfield responses : Lsun/net/www/MessageHeader;
    //   165: iconst_0
    //   166: invokevirtual getValue : (I)Ljava/lang/String;
    //   169: astore_2
    //   170: new java/util/StringTokenizer
    //   173: dup
    //   174: aload_2
    //   175: invokespecial <init> : (Ljava/lang/String;)V
    //   178: astore #9
    //   180: aload #9
    //   182: invokevirtual nextToken : ()Ljava/lang/String;
    //   185: pop
    //   186: aload #9
    //   188: invokevirtual nextToken : ()Ljava/lang/String;
    //   191: invokevirtual trim : ()Ljava/lang/String;
    //   194: invokestatic parseInt : (Ljava/lang/String;)I
    //   197: istore_3
    //   198: iload_3
    //   199: sipush #407
    //   202: if_icmpne -> 448
    //   205: iconst_0
    //   206: istore #10
    //   208: aload_0
    //   209: getfield responses : Lsun/net/www/MessageHeader;
    //   212: ldc_w 'Proxy-Authenticate'
    //   215: invokevirtual multiValueIterator : (Ljava/lang/String;)Ljava/util/Iterator;
    //   218: astore #11
    //   220: aload #11
    //   222: invokeinterface hasNext : ()Z
    //   227: ifeq -> 295
    //   230: aload #11
    //   232: invokeinterface next : ()Ljava/lang/Object;
    //   237: checkcast java/lang/String
    //   240: invokevirtual trim : ()Ljava/lang/String;
    //   243: astore #12
    //   245: aload #12
    //   247: ldc_w 'Negotiate'
    //   250: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
    //   253: ifne -> 267
    //   256: aload #12
    //   258: ldc_w 'Kerberos'
    //   261: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
    //   264: ifeq -> 292
    //   267: iload #8
    //   269: ifne -> 278
    //   272: iconst_1
    //   273: istore #8
    //   275: goto -> 295
    //   278: iconst_1
    //   279: istore #10
    //   281: aload_0
    //   282: iconst_0
    //   283: putfield doingNTLMp2ndStage : Z
    //   286: aconst_null
    //   287: astore #4
    //   289: goto -> 295
    //   292: goto -> 220
    //   295: new sun/net/www/protocol/http/AuthenticationHeader
    //   298: dup
    //   299: ldc_w 'Proxy-Authenticate'
    //   302: aload_0
    //   303: getfield responses : Lsun/net/www/MessageHeader;
    //   306: new sun/net/www/protocol/http/HttpCallerInfo
    //   309: dup
    //   310: aload_0
    //   311: getfield url : Ljava/net/URL;
    //   314: aload_0
    //   315: getfield http : Lsun/net/www/http/HttpClient;
    //   318: invokevirtual getProxyHostUsed : ()Ljava/lang/String;
    //   321: aload_0
    //   322: getfield http : Lsun/net/www/http/HttpClient;
    //   325: invokevirtual getProxyPortUsed : ()I
    //   328: invokespecial <init> : (Ljava/net/URL;Ljava/lang/String;I)V
    //   331: iload #10
    //   333: invokespecial <init> : (Ljava/lang/String;Lsun/net/www/MessageHeader;Lsun/net/www/protocol/http/HttpCallerInfo;Z)V
    //   336: astore #12
    //   338: aload_0
    //   339: getfield doingNTLMp2ndStage : Z
    //   342: ifne -> 388
    //   345: aload_0
    //   346: aload #4
    //   348: aload #12
    //   350: invokespecial resetProxyAuthentication : (Lsun/net/www/protocol/http/AuthenticationInfo;Lsun/net/www/protocol/http/AuthenticationHeader;)Lsun/net/www/protocol/http/AuthenticationInfo;
    //   353: astore #4
    //   355: aload #4
    //   357: ifnull -> 448
    //   360: aload_0
    //   361: getfield http : Lsun/net/www/http/HttpClient;
    //   364: invokevirtual getProxyHostUsed : ()Ljava/lang/String;
    //   367: astore #5
    //   369: aload_0
    //   370: getfield http : Lsun/net/www/http/HttpClient;
    //   373: invokevirtual getProxyPortUsed : ()I
    //   376: istore #6
    //   378: aload_0
    //   379: invokespecial disconnectInternal : ()V
    //   382: iinc #1, 1
    //   385: goto -> 489
    //   388: aload_0
    //   389: getfield responses : Lsun/net/www/MessageHeader;
    //   392: ldc_w 'Proxy-Authenticate'
    //   395: invokevirtual findValue : (Ljava/lang/String;)Ljava/lang/String;
    //   398: astore #13
    //   400: aload_0
    //   401: invokespecial reset : ()V
    //   404: aload #4
    //   406: aload_0
    //   407: aload #12
    //   409: invokevirtual headerParser : ()Lsun/net/www/HeaderParser;
    //   412: aload #13
    //   414: invokevirtual setHeaders : (Lsun/net/www/protocol/http/HttpURLConnection;Lsun/net/www/HeaderParser;Ljava/lang/String;)Z
    //   417: ifne -> 435
    //   420: aload_0
    //   421: invokespecial disconnectInternal : ()V
    //   424: new java/io/IOException
    //   427: dup
    //   428: ldc_w 'Authentication failure'
    //   431: invokespecial <init> : (Ljava/lang/String;)V
    //   434: athrow
    //   435: aload_0
    //   436: aconst_null
    //   437: putfield authObj : Ljava/lang/Object;
    //   440: aload_0
    //   441: iconst_0
    //   442: putfield doingNTLMp2ndStage : Z
    //   445: goto -> 489
    //   448: aload #4
    //   450: ifnull -> 458
    //   453: aload #4
    //   455: invokevirtual addToCache : ()V
    //   458: iload_3
    //   459: sipush #200
    //   462: if_icmpne -> 475
    //   465: aload_0
    //   466: getstatic sun/net/www/protocol/http/HttpURLConnection$TunnelState.TUNNELING : Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;
    //   469: invokevirtual setTunnelState : (Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;)V
    //   472: goto -> 496
    //   475: aload_0
    //   476: invokespecial disconnectInternal : ()V
    //   479: aload_0
    //   480: getstatic sun/net/www/protocol/http/HttpURLConnection$TunnelState.NONE : Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;
    //   483: invokevirtual setTunnelState : (Lsun/net/www/protocol/http/HttpURLConnection$TunnelState;)V
    //   486: goto -> 496
    //   489: iload_1
    //   490: getstatic sun/net/www/protocol/http/HttpURLConnection.maxRedirects : I
    //   493: if_icmplt -> 44
    //   496: iload_1
    //   497: getstatic sun/net/www/protocol/http/HttpURLConnection.maxRedirects : I
    //   500: if_icmpge -> 510
    //   503: iload_3
    //   504: sipush #200
    //   507: if_icmpeq -> 544
    //   510: new java/io/IOException
    //   513: dup
    //   514: new java/lang/StringBuilder
    //   517: dup
    //   518: invokespecial <init> : ()V
    //   521: ldc_w 'Unable to tunnel through proxy. Proxy returns "'
    //   524: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   527: aload_2
    //   528: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   531: ldc_w '"'
    //   534: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   537: invokevirtual toString : ()Ljava/lang/String;
    //   540: invokespecial <init> : (Ljava/lang/String;)V
    //   543: athrow
    //   544: aload_0
    //   545: getfield proxyAuthKey : Ljava/lang/String;
    //   548: ifnull -> 580
    //   551: aload_0
    //   552: getfield proxyAuthKey : Ljava/lang/String;
    //   555: invokestatic endAuthRequest : (Ljava/lang/String;)V
    //   558: goto -> 580
    //   561: astore #14
    //   563: aload_0
    //   564: getfield proxyAuthKey : Ljava/lang/String;
    //   567: ifnull -> 577
    //   570: aload_0
    //   571: getfield proxyAuthKey : Ljava/lang/String;
    //   574: invokestatic endAuthRequest : (Ljava/lang/String;)V
    //   577: aload #14
    //   579: athrow
    //   580: aload_0
    //   581: aload #7
    //   583: putfield requests : Lsun/net/www/MessageHeader;
    //   586: aload_0
    //   587: getfield responses : Lsun/net/www/MessageHeader;
    //   590: invokevirtual reset : ()V
    //   593: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1971	-> 0
    //   #1972	-> 2
    //   #1973	-> 6
    //   #1974	-> 8
    //   #1975	-> 11
    //   #1976	-> 14
    //   #1979	-> 17
    //   #1980	-> 23
    //   #1983	-> 34
    //   #1987	-> 37
    //   #1990	-> 44
    //   #1991	-> 51
    //   #1995	-> 64
    //   #1996	-> 68
    //   #2000	-> 75
    //   #2003	-> 89
    //   #2004	-> 101
    //   #2007	-> 114
    //   #2008	-> 127
    //   #2009	-> 139
    //   #2010	-> 148
    //   #2014	-> 161
    //   #2015	-> 170
    //   #2016	-> 180
    //   #2017	-> 186
    //   #2018	-> 198
    //   #2020	-> 205
    //   #2021	-> 208
    //   #2022	-> 220
    //   #2023	-> 230
    //   #2024	-> 245
    //   #2025	-> 261
    //   #2026	-> 267
    //   #2027	-> 272
    //   #2029	-> 278
    //   #2030	-> 281
    //   #2031	-> 286
    //   #2033	-> 289
    //   #2035	-> 292
    //   #2037	-> 295
    //   #2039	-> 318
    //   #2040	-> 325
    //   #2043	-> 338
    //   #2044	-> 345
    //   #2045	-> 350
    //   #2046	-> 355
    //   #2047	-> 360
    //   #2048	-> 369
    //   #2049	-> 378
    //   #2050	-> 382
    //   #2051	-> 385
    //   #2054	-> 388
    //   #2055	-> 400
    //   #2056	-> 404
    //   #2057	-> 409
    //   #2056	-> 414
    //   #2058	-> 420
    //   #2059	-> 424
    //   #2061	-> 435
    //   #2062	-> 440
    //   #2063	-> 445
    //   #2067	-> 448
    //   #2069	-> 453
    //   #2072	-> 458
    //   #2073	-> 465
    //   #2074	-> 472
    //   #2078	-> 475
    //   #2079	-> 479
    //   #2080	-> 486
    //   #2081	-> 489
    //   #2083	-> 496
    //   #2084	-> 510
    //   #2089	-> 544
    //   #2090	-> 551
    //   #2089	-> 561
    //   #2090	-> 570
    //   #2095	-> 580
    //   #2098	-> 586
    //   #2099	-> 593
    // Exception table:
    //   from	to	target	type
    //   37	544	561	finally
    //   561	563	561	finally
  }
  
  static String connectRequestURI(URL paramURL) {
    String str = paramURL.getHost();
    int i = paramURL.getPort();
    i = (i != -1) ? i : paramURL.getDefaultPort();
    return str + ":" + i;
  }
  
  private void sendCONNECTRequest() throws IOException {
    int i = this.url.getPort();
    this.requests.set(0, HTTP_CONNECT + " " + connectRequestURI(this.url) + " " + "HTTP/1.1", null);
    this.requests.setIfNotSet("User-Agent", userAgent);
    String str = this.url.getHost();
    if (i != -1 && i != this.url.getDefaultPort())
      str = str + ":" + String.valueOf(i); 
    this.requests.setIfNotSet("Host", str);
    this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
    if (this.http.getHttpKeepAliveSet())
      this.requests.setIfNotSet("Proxy-Connection", "keep-alive"); 
    setPreemptiveProxyAuthentication(this.requests);
    if (logger.isLoggable(PlatformLogger.Level.FINE))
      logger.fine(this.requests.toString()); 
    this.http.writeRequests(this.requests, (PosterOutputStream)null);
  }
  
  private void setPreemptiveProxyAuthentication(MessageHeader paramMessageHeader) throws IOException {
    AuthenticationInfo authenticationInfo = AuthenticationInfo.getProxyAuth(this.http.getProxyHostUsed(), this.http.getProxyPortUsed());
    if (authenticationInfo != null && authenticationInfo.supportsPreemptiveAuthorization()) {
      String str;
      if (authenticationInfo instanceof DigestAuthentication) {
        DigestAuthentication digestAuthentication = (DigestAuthentication)authenticationInfo;
        if (tunnelState() == TunnelState.SETUP) {
          str = digestAuthentication.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
        } else {
          str = digestAuthentication.getHeaderValue(getRequestURI(), this.method);
        } 
      } else {
        str = authenticationInfo.getHeaderValue(this.url, this.method);
      } 
      paramMessageHeader.set(authenticationInfo.getHeaderName(), str);
      this.currentProxyCredentials = authenticationInfo;
    } 
  }
  
  private AuthenticationInfo getHttpProxyAuthentication(AuthenticationHeader paramAuthenticationHeader) {
    AuthenticationInfo authenticationInfo = null;
    String str1 = paramAuthenticationHeader.raw();
    String str2 = this.http.getProxyHostUsed();
    int i = this.http.getProxyPortUsed();
    if (str2 != null && paramAuthenticationHeader.isPresent()) {
      HeaderParser headerParser = paramAuthenticationHeader.headerParser();
      String str3 = headerParser.findValue("realm");
      String str4 = paramAuthenticationHeader.scheme();
      AuthScheme authScheme = AuthScheme.UNKNOWN;
      if ("basic".equalsIgnoreCase(str4)) {
        authScheme = AuthScheme.BASIC;
      } else if ("digest".equalsIgnoreCase(str4)) {
        authScheme = AuthScheme.DIGEST;
      } else if ("ntlm".equalsIgnoreCase(str4)) {
        authScheme = AuthScheme.NTLM;
        this.doingNTLMp2ndStage = true;
      } else if ("Kerberos".equalsIgnoreCase(str4)) {
        authScheme = AuthScheme.KERBEROS;
        this.doingNTLMp2ndStage = true;
      } else if ("Negotiate".equalsIgnoreCase(str4)) {
        authScheme = AuthScheme.NEGOTIATE;
        this.doingNTLMp2ndStage = true;
      } 
      if (str3 == null)
        str3 = ""; 
      this.proxyAuthKey = AuthenticationInfo.getProxyAuthKey(str2, i, str3, authScheme);
      authenticationInfo = AuthenticationInfo.getProxyAuth(this.proxyAuthKey);
      if (authenticationInfo == null) {
        InetAddress inetAddress;
        PasswordAuthentication passwordAuthentication;
        switch (null.$SwitchMap$sun$net$www$protocol$http$AuthScheme[authScheme.ordinal()]) {
          case 1:
            inetAddress = null;
            try {
              String str = str2;
              inetAddress = AccessController.<InetAddress>doPrivileged((PrivilegedExceptionAction<InetAddress>)new Object(this, str));
            } catch (PrivilegedActionException privilegedActionException) {}
            passwordAuthentication = privilegedRequestPasswordAuthentication(str2, inetAddress, i, "http", str3, str4, this.url, Authenticator.RequestorType.PROXY);
            if (passwordAuthentication != null)
              authenticationInfo = new BasicAuthentication(true, str2, i, str3, passwordAuthentication); 
            break;
          case 2:
            passwordAuthentication = privilegedRequestPasswordAuthentication(str2, (InetAddress)null, i, this.url.getProtocol(), str3, str4, this.url, Authenticator.RequestorType.PROXY);
            if (passwordAuthentication != null) {
              DigestAuthentication.Parameters parameters = new DigestAuthentication.Parameters();
              authenticationInfo = new DigestAuthentication(true, str2, i, str3, str4, passwordAuthentication, parameters);
            } 
            break;
          case 3:
            if (NTLMAuthenticationProxy.supported) {
              if (this.tryTransparentNTLMProxy) {
                this.tryTransparentNTLMProxy = NTLMAuthenticationProxy.supportsTransparentAuth;
                if (this.tryTransparentNTLMProxy && this.useProxyResponseCode)
                  this.tryTransparentNTLMProxy = false; 
              } 
              passwordAuthentication = null;
              if (this.tryTransparentNTLMProxy) {
                logger.finest("Trying Transparent NTLM authentication");
              } else {
                passwordAuthentication = privilegedRequestPasswordAuthentication(str2, (InetAddress)null, i, this.url.getProtocol(), "", str4, this.url, Authenticator.RequestorType.PROXY);
              } 
              if (this.tryTransparentNTLMProxy || (!this.tryTransparentNTLMProxy && passwordAuthentication != null))
                authenticationInfo = NTLMAuthenticationProxy.proxy.create(true, str2, i, passwordAuthentication); 
              this.tryTransparentNTLMProxy = false;
            } 
            break;
          case 4:
            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Negotiate"));
            break;
          case 5:
            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Kerberos"));
            break;
          case 6:
            if (logger.isLoggable(PlatformLogger.Level.FINEST))
              logger.finest("Unknown/Unsupported authentication scheme: " + str4); 
          default:
            throw new AssertionError("should not reach here");
        } 
      } 
      if (authenticationInfo == null && defaultAuth != null && defaultAuth.schemeSupported(str4))
        try {
          URL uRL = new URL("http", str2, i, "/");
          String str = defaultAuth.authString(uRL, str4, str3);
          if (str != null)
            authenticationInfo = new BasicAuthentication(true, str2, i, str3, str); 
        } catch (MalformedURLException malformedURLException) {} 
      if (authenticationInfo != null && !authenticationInfo.setHeaders(this, headerParser, str1))
        authenticationInfo = null; 
    } 
    if (logger.isLoggable(PlatformLogger.Level.FINER))
      logger.finer("Proxy Authentication for " + paramAuthenticationHeader.toString() + " returned " + ((authenticationInfo != null) ? authenticationInfo.toString() : "null")); 
    return authenticationInfo;
  }
  
  private AuthenticationInfo getServerAuthentication(AuthenticationHeader paramAuthenticationHeader) {
    AuthenticationInfo authenticationInfo = null;
    String str = paramAuthenticationHeader.raw();
    if (paramAuthenticationHeader.isPresent()) {
      HeaderParser headerParser = paramAuthenticationHeader.headerParser();
      String str1 = headerParser.findValue("realm");
      String str2 = paramAuthenticationHeader.scheme();
      AuthScheme authScheme = AuthScheme.UNKNOWN;
      if ("basic".equalsIgnoreCase(str2)) {
        authScheme = AuthScheme.BASIC;
      } else if ("digest".equalsIgnoreCase(str2)) {
        authScheme = AuthScheme.DIGEST;
      } else if ("ntlm".equalsIgnoreCase(str2)) {
        authScheme = AuthScheme.NTLM;
        this.doingNTLM2ndStage = true;
      } else if ("Kerberos".equalsIgnoreCase(str2)) {
        authScheme = AuthScheme.KERBEROS;
        this.doingNTLM2ndStage = true;
      } else if ("Negotiate".equalsIgnoreCase(str2)) {
        authScheme = AuthScheme.NEGOTIATE;
        this.doingNTLM2ndStage = true;
      } 
      this.domain = headerParser.findValue("domain");
      if (str1 == null)
        str1 = ""; 
      this.serverAuthKey = AuthenticationInfo.getServerAuthKey(this.url, str1, authScheme);
      authenticationInfo = AuthenticationInfo.getServerAuth(this.serverAuthKey);
      InetAddress inetAddress = null;
      if (authenticationInfo == null)
        try {
          inetAddress = InetAddress.getByName(this.url.getHost());
        } catch (UnknownHostException unknownHostException) {} 
      int i = this.url.getPort();
      if (i == -1)
        i = this.url.getDefaultPort(); 
      if (authenticationInfo == null) {
        PasswordAuthentication passwordAuthentication;
        switch (null.$SwitchMap$sun$net$www$protocol$http$AuthScheme[authScheme.ordinal()]) {
          case 5:
            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Kerberos"));
            break;
          case 4:
            authenticationInfo = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Negotiate"));
            break;
          case 1:
            passwordAuthentication = privilegedRequestPasswordAuthentication(this.url.getHost(), inetAddress, i, this.url.getProtocol(), str1, str2, this.url, Authenticator.RequestorType.SERVER);
            if (passwordAuthentication != null)
              authenticationInfo = new BasicAuthentication(false, this.url, str1, passwordAuthentication); 
            break;
          case 2:
            passwordAuthentication = privilegedRequestPasswordAuthentication(this.url.getHost(), inetAddress, i, this.url.getProtocol(), str1, str2, this.url, Authenticator.RequestorType.SERVER);
            if (passwordAuthentication != null) {
              this.digestparams = new DigestAuthentication.Parameters();
              authenticationInfo = new DigestAuthentication(false, this.url, str1, str2, passwordAuthentication, this.digestparams);
            } 
            break;
          case 3:
            if (NTLMAuthenticationProxy.supported) {
              URL uRL;
              try {
                uRL = new URL(this.url, "/");
              } catch (Exception exception) {
                uRL = this.url;
              } 
              if (this.tryTransparentNTLMServer) {
                this.tryTransparentNTLMServer = NTLMAuthenticationProxy.supportsTransparentAuth;
                if (this.tryTransparentNTLMServer)
                  this.tryTransparentNTLMServer = NTLMAuthenticationProxy.isTrustedSite(this.url); 
              } 
              passwordAuthentication = null;
              if (this.tryTransparentNTLMServer) {
                logger.finest("Trying Transparent NTLM authentication");
              } else {
                passwordAuthentication = privilegedRequestPasswordAuthentication(this.url.getHost(), inetAddress, i, this.url.getProtocol(), "", str2, this.url, Authenticator.RequestorType.SERVER);
              } 
              if (this.tryTransparentNTLMServer || (!this.tryTransparentNTLMServer && passwordAuthentication != null))
                authenticationInfo = NTLMAuthenticationProxy.proxy.create(false, uRL, passwordAuthentication); 
              this.tryTransparentNTLMServer = false;
            } 
            break;
          case 6:
            if (logger.isLoggable(PlatformLogger.Level.FINEST))
              logger.finest("Unknown/Unsupported authentication scheme: " + str2); 
          default:
            throw new AssertionError("should not reach here");
        } 
      } 
      if (authenticationInfo == null && defaultAuth != null && defaultAuth.schemeSupported(str2)) {
        String str3 = defaultAuth.authString(this.url, str2, str1);
        if (str3 != null)
          authenticationInfo = new BasicAuthentication(false, this.url, str1, str3); 
      } 
      if (authenticationInfo != null && !authenticationInfo.setHeaders(this, headerParser, str))
        authenticationInfo = null; 
    } 
    if (logger.isLoggable(PlatformLogger.Level.FINER))
      logger.finer("Server Authentication for " + paramAuthenticationHeader.toString() + " returned " + ((authenticationInfo != null) ? authenticationInfo.toString() : "null")); 
    return authenticationInfo;
  }
  
  private void checkResponseCredentials(boolean paramBoolean) throws IOException {
    try {
      if (!this.needToCheck)
        return; 
      if (validateProxy && this.currentProxyCredentials != null && this.currentProxyCredentials instanceof DigestAuthentication) {
        String str = this.responses.findValue("Proxy-Authentication-Info");
        if (paramBoolean || str != null) {
          DigestAuthentication digestAuthentication = (DigestAuthentication)this.currentProxyCredentials;
          digestAuthentication.checkResponse(str, this.method, getRequestURI());
          this.currentProxyCredentials = null;
        } 
      } 
      if (validateServer && this.currentServerCredentials != null && this.currentServerCredentials instanceof DigestAuthentication) {
        String str = this.responses.findValue("Authentication-Info");
        if (paramBoolean || str != null) {
          DigestAuthentication digestAuthentication = (DigestAuthentication)this.currentServerCredentials;
          digestAuthentication.checkResponse(str, this.method, this.url);
          this.currentServerCredentials = null;
        } 
      } 
      if (this.currentServerCredentials == null && this.currentProxyCredentials == null)
        this.needToCheck = false; 
    } catch (IOException iOException) {
      disconnectInternal();
      this.connected = false;
      throw iOException;
    } 
  }
  
  String getRequestURI() throws IOException {
    if (this.requestURI == null)
      this.requestURI = this.http.getURLFile(); 
    return this.requestURI;
  }
  
  private boolean followRedirect() throws IOException {
    URL uRL1;
    if (!getInstanceFollowRedirects())
      return false; 
    int i = getResponseCode();
    if (i < 300 || i > 307 || i == 306 || i == 304)
      return false; 
    String str = getHeaderField("Location");
    if (str == null)
      return false; 
    try {
      uRL1 = new URL(str);
      if (!this.url.getProtocol().equalsIgnoreCase(uRL1.getProtocol()))
        return false; 
    } catch (MalformedURLException malformedURLException) {
      uRL1 = new URL(this.url, str);
    } 
    URL uRL2 = uRL1;
    this.socketPermission = null;
    SocketPermission socketPermission = URLtoSocketPermission(uRL1);
    if (socketPermission != null)
      try {
        return ((Boolean)AccessController.<Boolean>doPrivileged((PrivilegedExceptionAction<Boolean>)new Object(this, str, i, uRL2), (AccessControlContext)null, new Permission[] { socketPermission })).booleanValue();
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      }  
    return followRedirect0(str, i, uRL1);
  }
  
  private boolean followRedirect0(String paramString, int paramInt, URL paramURL) throws IOException {
    disconnectInternal();
    if (streaming())
      throw new HttpRetryException("cannot retry due to redirection, in streaming mode", paramInt, paramString); 
    if (logger.isLoggable(PlatformLogger.Level.FINE))
      logger.fine("Redirected from " + this.url + " to " + paramURL); 
    this.responses = new MessageHeader();
    if (paramInt == 305) {
      String str = paramURL.getHost();
      int i = paramURL.getPort();
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkConnect(str, i); 
      setProxiedClient(this.url, str, i);
      this.requests.set(0, this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
      this.connected = true;
      this.useProxyResponseCode = true;
    } else {
      this.url = paramURL;
      this.requestURI = null;
      if (this.method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect") && paramInt != 307) {
        this.requests = new MessageHeader();
        this.setRequests = false;
        super.setRequestMethod("GET");
        this.poster = null;
        if (!checkReuseConnection())
          connect(); 
      } else {
        if (!checkReuseConnection())
          connect(); 
        if (this.http != null) {
          this.requests.set(0, this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
          int i = this.url.getPort();
          String str = this.url.getHost();
          if (i != -1 && i != this.url.getDefaultPort())
            str = str + ":" + String.valueOf(i); 
          this.requests.set("Host", str);
        } 
      } 
    } 
    return true;
  }
  
  private void reset() throws IOException {
    this.http.reuse = true;
    this.reuseClient = this.http;
    InputStream inputStream = this.http.getInputStream();
    if (!this.method.equals("HEAD")) {
      try {
        if (inputStream instanceof sun.net.www.http.ChunkedInputStream || inputStream instanceof sun.net.www.MeteredStream) {
          while (inputStream.read(this.cdata) > 0);
        } else {
          long l1 = 0L;
          int i = 0;
          String str = this.responses.findValue("Content-Length");
          if (str != null)
            try {
              l1 = Long.parseLong(str);
            } catch (NumberFormatException numberFormatException) {
              l1 = 0L;
            }  
          long l2;
          for (l2 = 0L; l2 < l1 && (
            i = inputStream.read(this.cdata)) != -1;)
            l2 += i; 
        } 
      } catch (IOException iOException) {
        this.http.reuse = false;
        this.reuseClient = null;
        disconnectInternal();
        return;
      } 
      try {
        if (inputStream instanceof sun.net.www.MeteredStream)
          inputStream.close(); 
      } catch (IOException iOException) {}
    } 
    this.responseCode = -1;
    this.responses = new MessageHeader();
    this.connected = false;
  }
  
  private void disconnectWeb() throws IOException {
    if (usingProxy() && this.http.isKeepingAlive()) {
      this.responseCode = -1;
      reset();
    } else {
      disconnectInternal();
    } 
  }
  
  private void disconnectInternal() {
    this.responseCode = -1;
    this.inputStream = null;
    if (this.pi != null) {
      this.pi.finishTracking();
      this.pi = null;
    } 
    if (this.http != null) {
      this.http.closeServer();
      this.http = null;
      this.connected = false;
    } 
  }
  
  public void disconnect() {
    this.responseCode = -1;
    if (this.pi != null) {
      this.pi.finishTracking();
      this.pi = null;
    } 
    if (this.http != null) {
      if (this.inputStream != null) {
        HttpClient httpClient = this.http;
        boolean bool = httpClient.isKeepingAlive();
        try {
          this.inputStream.close();
        } catch (IOException iOException) {}
        if (bool)
          httpClient.closeIdleConnection(); 
      } else {
        this.http.setDoNotRetry(true);
        this.http.closeServer();
      } 
      this.http = null;
      this.connected = false;
    } 
    this.cachedInputStream = null;
    if (this.cachedHeaders != null)
      this.cachedHeaders.reset(); 
  }
  
  public boolean usingProxy() {
    if (this.http != null)
      return (this.http.getProxyHostUsed() != null); 
    return false;
  }
  
  private String filterHeaderField(String paramString1, String paramString2) {
    if (paramString2 == null)
      return null; 
    if ("set-cookie".equalsIgnoreCase(paramString1) || "set-cookie2"
      .equalsIgnoreCase(paramString1)) {
      if (this.cookieHandler == null || paramString2.length() == 0)
        return paramString2; 
      JavaNetHttpCookieAccess javaNetHttpCookieAccess = SharedSecrets.getJavaNetHttpCookieAccess();
      StringBuilder stringBuilder = new StringBuilder();
      List list = javaNetHttpCookieAccess.parse(paramString2);
      boolean bool = false;
      for (HttpCookie httpCookie : list) {
        if (httpCookie.isHttpOnly())
          continue; 
        if (bool)
          stringBuilder.append(','); 
        stringBuilder.append(javaNetHttpCookieAccess.header(httpCookie));
        bool = true;
      } 
      return (stringBuilder.length() == 0) ? "" : stringBuilder.toString();
    } 
    return paramString2;
  }
  
  private Map<String, List<String>> getFilteredHeaderFields() {
    Map<String, List<String>> map;
    if (this.filteredHeaders != null)
      return this.filteredHeaders; 
    HashMap<Object, Object> hashMap = new HashMap<>();
    if (this.cachedHeaders != null) {
      map = this.cachedHeaders.getHeaders();
    } else {
      map = this.responses.getHeaders();
    } 
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      String str = (String)entry.getKey();
      List list = (List)entry.getValue();
      ArrayList<String> arrayList = new ArrayList();
      for (String str1 : list) {
        String str2 = filterHeaderField(str, str1);
        if (str2 != null)
          arrayList.add(str2); 
      } 
      if (!arrayList.isEmpty())
        hashMap.put(str, Collections.unmodifiableList(arrayList)); 
    } 
    return this.filteredHeaders = Collections.unmodifiableMap(hashMap);
  }
  
  public String getHeaderField(String paramString) {
    try {
      getInputStream();
    } catch (IOException iOException) {}
    if (this.cachedHeaders != null)
      return filterHeaderField(paramString, this.cachedHeaders.findValue(paramString)); 
    return filterHeaderField(paramString, this.responses.findValue(paramString));
  }
  
  public Map<String, List<String>> getHeaderFields() {
    try {
      getInputStream();
    } catch (IOException iOException) {}
    return getFilteredHeaderFields();
  }
  
  public String getHeaderField(int paramInt) {
    try {
      getInputStream();
    } catch (IOException iOException) {}
    if (this.cachedHeaders != null)
      return filterHeaderField(this.cachedHeaders.getKey(paramInt), this.cachedHeaders
          .getValue(paramInt)); 
    return filterHeaderField(this.responses.getKey(paramInt), this.responses.getValue(paramInt));
  }
  
  public String getHeaderFieldKey(int paramInt) {
    try {
      getInputStream();
    } catch (IOException iOException) {}
    if (this.cachedHeaders != null)
      return this.cachedHeaders.getKey(paramInt); 
    return this.responses.getKey(paramInt);
  }
  
  public synchronized void setRequestProperty(String paramString1, String paramString2) {
    if (this.connected || this.connecting)
      throw new IllegalStateException("Already connected"); 
    if (paramString1 == null)
      throw new NullPointerException("key is null"); 
    if (isExternalMessageHeaderAllowed(paramString1, paramString2)) {
      this.requests.set(paramString1, paramString2);
      if (!paramString1.equalsIgnoreCase("Content-Type"))
        this.userHeaders.set(paramString1, paramString2); 
    } 
  }
  
  MessageHeader getUserSetHeaders() {
    return this.userHeaders;
  }
  
  public synchronized void addRequestProperty(String paramString1, String paramString2) {
    if (this.connected || this.connecting)
      throw new IllegalStateException("Already connected"); 
    if (paramString1 == null)
      throw new NullPointerException("key is null"); 
    if (isExternalMessageHeaderAllowed(paramString1, paramString2)) {
      this.requests.add(paramString1, paramString2);
      if (!paramString1.equalsIgnoreCase("Content-Type"))
        this.userHeaders.add(paramString1, paramString2); 
    } 
  }
  
  public void setAuthenticationProperty(String paramString1, String paramString2) {
    checkMessageHeader(paramString1, paramString2);
    this.requests.set(paramString1, paramString2);
  }
  
  public synchronized String getRequestProperty(String paramString) {
    if (paramString == null)
      return null; 
    for (byte b = 0; b < EXCLUDE_HEADERS.length; b++) {
      if (paramString.equalsIgnoreCase(EXCLUDE_HEADERS[b]))
        return null; 
    } 
    if (!this.setUserCookies) {
      if (paramString.equalsIgnoreCase("Cookie"))
        return this.userCookies; 
      if (paramString.equalsIgnoreCase("Cookie2"))
        return this.userCookies2; 
    } 
    return this.requests.findValue(paramString);
  }
  
  public synchronized Map<String, List<String>> getRequestProperties() {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    if (this.setUserCookies)
      return this.requests.getHeaders(EXCLUDE_HEADERS); 
    HashMap<Object, Object> hashMap = null;
    if (this.userCookies != null || this.userCookies2 != null) {
      hashMap = new HashMap<>();
      if (this.userCookies != null)
        hashMap.put("Cookie", Arrays.asList(new String[] { this.userCookies })); 
      if (this.userCookies2 != null)
        hashMap.put("Cookie2", Arrays.asList(new String[] { this.userCookies2 })); 
    } 
    return this.requests.filterAndAddHeaders(EXCLUDE_HEADERS2, (Map)hashMap);
  }
  
  public void setConnectTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeouts can't be negative"); 
    this.connectTimeout = paramInt;
  }
  
  public int getConnectTimeout() {
    return (this.connectTimeout < 0) ? 0 : this.connectTimeout;
  }
  
  public void setReadTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeouts can't be negative"); 
    this.readTimeout = paramInt;
  }
  
  public int getReadTimeout() {
    return (this.readTimeout < 0) ? 0 : this.readTimeout;
  }
  
  public CookieHandler getCookieHandler() {
    return this.cookieHandler;
  }
  
  String getMethod() {
    return this.method;
  }
  
  private MessageHeader mapToMessageHeader(Map<String, List<String>> paramMap) {
    MessageHeader messageHeader = new MessageHeader();
    if (paramMap == null || paramMap.isEmpty())
      return messageHeader; 
    for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      List list = (List)entry.getValue();
      for (String str1 : list) {
        if (str == null) {
          messageHeader.prepend(str, str1);
          continue;
        } 
        messageHeader.add(str, str1);
      } 
    } 
    return messageHeader;
  }
  
  class HttpInputStream extends FilterInputStream {
    private CacheRequest cacheRequest;
    
    private OutputStream outputStream;
    
    private boolean marked = false;
    
    private int inCache = 0;
    
    private int markCount = 0;
    
    private boolean closed;
    
    private byte[] skipBuffer;
    
    private static final int SKIP_BUFFER_SIZE = 8096;
    
    public HttpInputStream(InputStream param1InputStream) {
      super(param1InputStream);
      this.cacheRequest = null;
      this.outputStream = null;
    }
    
    public HttpInputStream(InputStream param1InputStream, CacheRequest param1CacheRequest) {
      super(param1InputStream);
      this.cacheRequest = param1CacheRequest;
      try {
        this.outputStream = param1CacheRequest.getBody();
      } catch (IOException iOException) {
        this.cacheRequest.abort();
        this.cacheRequest = null;
        this.outputStream = null;
      } 
    }
    
    public synchronized void mark(int param1Int) {
      super.mark(param1Int);
      if (this.cacheRequest != null) {
        this.marked = true;
        this.markCount = 0;
      } 
    }
    
    public synchronized void reset() throws IOException {
      super.reset();
      if (this.cacheRequest != null) {
        this.marked = false;
        this.inCache += this.markCount;
      } 
    }
    
    private void ensureOpen() throws IOException {
      if (this.closed)
        throw new IOException("stream is closed"); 
    }
    
    public int read() throws IOException {
      ensureOpen();
      try {
        byte[] arrayOfByte = new byte[1];
        int i = read(arrayOfByte);
        return (i == -1) ? i : (arrayOfByte[0] & 0xFF);
      } catch (IOException iOException) {
        if (this.cacheRequest != null)
          this.cacheRequest.abort(); 
        throw iOException;
      } 
    }
    
    public int read(byte[] param1ArrayOfbyte) throws IOException {
      return read(param1ArrayOfbyte, 0, param1ArrayOfbyte.length);
    }
    
    public int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      ensureOpen();
      try {
        int j, i = super.read(param1ArrayOfbyte, param1Int1, param1Int2);
        if (this.inCache > 0) {
          if (this.inCache >= i) {
            this.inCache -= i;
            j = 0;
          } else {
            j = i - this.inCache;
            this.inCache = 0;
          } 
        } else {
          j = i;
        } 
        if (j > 0 && this.outputStream != null)
          this.outputStream.write(param1ArrayOfbyte, param1Int1 + i - j, j); 
        if (this.marked)
          this.markCount += i; 
        return i;
      } catch (IOException iOException) {
        if (this.cacheRequest != null)
          this.cacheRequest.abort(); 
        throw iOException;
      } 
    }
    
    public long skip(long param1Long) throws IOException {
      ensureOpen();
      long l = param1Long;
      if (this.skipBuffer == null)
        this.skipBuffer = new byte[8096]; 
      byte[] arrayOfByte = this.skipBuffer;
      if (param1Long <= 0L)
        return 0L; 
      while (l > 0L) {
        int i = read(arrayOfByte, 0, 
            (int)Math.min(8096L, l));
        if (i < 0)
          break; 
        l -= i;
      } 
      return param1Long - l;
    }
    
    public void close() throws IOException {
      if (this.closed)
        return; 
      try {
        if (this.outputStream != null)
          if (read() != -1) {
            this.cacheRequest.abort();
          } else {
            this.outputStream.close();
          }  
        super.close();
      } catch (IOException iOException) {
        if (this.cacheRequest != null)
          this.cacheRequest.abort(); 
        throw iOException;
      } finally {
        this.closed = true;
        HttpURLConnection.this.http = null;
        HttpURLConnection.this.checkResponseCredentials(true);
      } 
    }
  }
  
  static class HttpURLConnection {}
  
  class HttpURLConnection {}
}
