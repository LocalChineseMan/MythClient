package sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocketFactory;

public final class SSLSocketFactoryImpl extends SSLSocketFactory {
  private SSLContextImpl context;
  
  public SSLSocketFactoryImpl() throws Exception {
    this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
  }
  
  SSLSocketFactoryImpl(SSLContextImpl paramSSLContextImpl) {
    this.context = paramSSLContextImpl;
  }
  
  public Socket createSocket() {
    return new SSLSocketImpl(this.context);
  }
  
  public Socket createSocket(String paramString, int paramInt) throws IOException, UnknownHostException {
    return new SSLSocketImpl(this.context, paramString, paramInt);
  }
  
  public Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    return new SSLSocketImpl(this.context, paramSocket, paramString, paramInt, paramBoolean);
  }
  
  public Socket createSocket(Socket paramSocket, InputStream paramInputStream, boolean paramBoolean) throws IOException {
    if (paramSocket == null)
      throw new NullPointerException("the existing socket cannot be null"); 
    return new SSLSocketImpl(this.context, paramSocket, paramInputStream, paramBoolean);
  }
  
  public Socket createSocket(InetAddress paramInetAddress, int paramInt) throws IOException {
    return new SSLSocketImpl(this.context, paramInetAddress, paramInt);
  }
  
  public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException {
    return new SSLSocketImpl(this.context, paramString, paramInt1, paramInetAddress, paramInt2);
  }
  
  public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException {
    return new SSLSocketImpl(this.context, paramInetAddress1, paramInt1, paramInetAddress2, paramInt2);
  }
  
  public String[] getDefaultCipherSuites() {
    return this.context.getDefaultCipherSuiteList(false).toStringArray();
  }
  
  public String[] getSupportedCipherSuites() {
    return this.context.getSupportedCipherSuiteList().toStringArray();
  }
}
