package sun.net.www.protocol.https;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

public class HttpsURLConnectionImpl extends HttpsURLConnection {
  protected DelegateHttpsURLConnection delegate;
  
  HttpsURLConnectionImpl(URL paramURL, Handler paramHandler) throws IOException {
    this(paramURL, null, paramHandler);
  }
  
  HttpsURLConnectionImpl(URL paramURL, Proxy paramProxy, Handler paramHandler) throws IOException {
    super(paramURL);
    this.delegate = new DelegateHttpsURLConnection(this.url, paramProxy, paramHandler, this);
  }
  
  protected HttpsURLConnectionImpl(URL paramURL) throws IOException {
    super(paramURL);
  }
  
  protected void setNewClient(URL paramURL) throws IOException {
    this.delegate.setNewClient(paramURL, false);
  }
  
  protected void setNewClient(URL paramURL, boolean paramBoolean) throws IOException {
    this.delegate.setNewClient(paramURL, paramBoolean);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt) throws IOException {
    this.delegate.setProxiedClient(paramURL, paramString, paramInt);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    this.delegate.setProxiedClient(paramURL, paramString, paramInt, paramBoolean);
  }
  
  public void connect() throws IOException {
    this.delegate.connect();
  }
  
  protected boolean isConnected() {
    return this.delegate.isConnected();
  }
  
  protected void setConnected(boolean paramBoolean) {
    this.delegate.setConnected(paramBoolean);
  }
  
  public String getCipherSuite() {
    return this.delegate.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates() {
    return this.delegate.getLocalCertificates();
  }
  
  public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
    return this.delegate.getServerCertificates();
  }
  
  public X509Certificate[] getServerCertificateChain() {
    try {
      return this.delegate.getServerCertificateChain();
    } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {
      return null;
    } 
  }
  
  public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    return this.delegate.getPeerPrincipal();
  }
  
  public Principal getLocalPrincipal() {
    return this.delegate.getLocalPrincipal();
  }
  
  public synchronized OutputStream getOutputStream() throws IOException {
    return this.delegate.getOutputStream();
  }
  
  public synchronized InputStream getInputStream() throws IOException {
    return this.delegate.getInputStream();
  }
  
  public InputStream getErrorStream() {
    return this.delegate.getErrorStream();
  }
  
  public void disconnect() {
    this.delegate.disconnect();
  }
  
  public boolean usingProxy() {
    return this.delegate.usingProxy();
  }
  
  public Map<String, List<String>> getHeaderFields() {
    return this.delegate.getHeaderFields();
  }
  
  public String getHeaderField(String paramString) {
    return this.delegate.getHeaderField(paramString);
  }
  
  public String getHeaderField(int paramInt) {
    return this.delegate.getHeaderField(paramInt);
  }
  
  public String getHeaderFieldKey(int paramInt) {
    return this.delegate.getHeaderFieldKey(paramInt);
  }
  
  public void setRequestProperty(String paramString1, String paramString2) {
    this.delegate.setRequestProperty(paramString1, paramString2);
  }
  
  public void addRequestProperty(String paramString1, String paramString2) {
    this.delegate.addRequestProperty(paramString1, paramString2);
  }
  
  public int getResponseCode() throws IOException {
    return this.delegate.getResponseCode();
  }
  
  public String getRequestProperty(String paramString) {
    return this.delegate.getRequestProperty(paramString);
  }
  
  public Map<String, List<String>> getRequestProperties() {
    return this.delegate.getRequestProperties();
  }
  
  public void setInstanceFollowRedirects(boolean paramBoolean) {
    this.delegate.setInstanceFollowRedirects(paramBoolean);
  }
  
  public boolean getInstanceFollowRedirects() {
    return this.delegate.getInstanceFollowRedirects();
  }
  
  public void setRequestMethod(String paramString) throws ProtocolException {
    this.delegate.setRequestMethod(paramString);
  }
  
  public String getRequestMethod() {
    return this.delegate.getRequestMethod();
  }
  
  public String getResponseMessage() throws IOException {
    return this.delegate.getResponseMessage();
  }
  
  public long getHeaderFieldDate(String paramString, long paramLong) {
    return this.delegate.getHeaderFieldDate(paramString, paramLong);
  }
  
  public Permission getPermission() throws IOException {
    return this.delegate.getPermission();
  }
  
  public URL getURL() {
    return this.delegate.getURL();
  }
  
  public int getContentLength() {
    return this.delegate.getContentLength();
  }
  
  public long getContentLengthLong() {
    return this.delegate.getContentLengthLong();
  }
  
  public String getContentType() {
    return this.delegate.getContentType();
  }
  
  public String getContentEncoding() {
    return this.delegate.getContentEncoding();
  }
  
  public long getExpiration() {
    return this.delegate.getExpiration();
  }
  
  public long getDate() {
    return this.delegate.getDate();
  }
  
  public long getLastModified() {
    return this.delegate.getLastModified();
  }
  
  public int getHeaderFieldInt(String paramString, int paramInt) {
    return this.delegate.getHeaderFieldInt(paramString, paramInt);
  }
  
  public long getHeaderFieldLong(String paramString, long paramLong) {
    return this.delegate.getHeaderFieldLong(paramString, paramLong);
  }
  
  public Object getContent() throws IOException {
    return this.delegate.getContent();
  }
  
  public Object getContent(Class[] paramArrayOfClass) throws IOException {
    return this.delegate.getContent(paramArrayOfClass);
  }
  
  public String toString() {
    return this.delegate.toString();
  }
  
  public void setDoInput(boolean paramBoolean) {
    this.delegate.setDoInput(paramBoolean);
  }
  
  public boolean getDoInput() {
    return this.delegate.getDoInput();
  }
  
  public void setDoOutput(boolean paramBoolean) {
    this.delegate.setDoOutput(paramBoolean);
  }
  
  public boolean getDoOutput() {
    return this.delegate.getDoOutput();
  }
  
  public void setAllowUserInteraction(boolean paramBoolean) {
    this.delegate.setAllowUserInteraction(paramBoolean);
  }
  
  public boolean getAllowUserInteraction() {
    return this.delegate.getAllowUserInteraction();
  }
  
  public void setUseCaches(boolean paramBoolean) {
    this.delegate.setUseCaches(paramBoolean);
  }
  
  public boolean getUseCaches() {
    return this.delegate.getUseCaches();
  }
  
  public void setIfModifiedSince(long paramLong) {
    this.delegate.setIfModifiedSince(paramLong);
  }
  
  public long getIfModifiedSince() {
    return this.delegate.getIfModifiedSince();
  }
  
  public boolean getDefaultUseCaches() {
    return this.delegate.getDefaultUseCaches();
  }
  
  public void setDefaultUseCaches(boolean paramBoolean) {
    this.delegate.setDefaultUseCaches(paramBoolean);
  }
  
  protected void finalize() throws Throwable {
    this.delegate.dispose();
  }
  
  public boolean equals(Object paramObject) {
    return this.delegate.equals(paramObject);
  }
  
  public int hashCode() {
    return this.delegate.hashCode();
  }
  
  public void setConnectTimeout(int paramInt) {
    this.delegate.setConnectTimeout(paramInt);
  }
  
  public int getConnectTimeout() {
    return this.delegate.getConnectTimeout();
  }
  
  public void setReadTimeout(int paramInt) {
    this.delegate.setReadTimeout(paramInt);
  }
  
  public int getReadTimeout() {
    return this.delegate.getReadTimeout();
  }
  
  public void setFixedLengthStreamingMode(int paramInt) {
    this.delegate.setFixedLengthStreamingMode(paramInt);
  }
  
  public void setFixedLengthStreamingMode(long paramLong) {
    this.delegate.setFixedLengthStreamingMode(paramLong);
  }
  
  public void setChunkedStreamingMode(int paramInt) {
    this.delegate.setChunkedStreamingMode(paramInt);
  }
}
