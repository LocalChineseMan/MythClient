package sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLSocket;

abstract class BaseSSLSocketImpl extends SSLSocket {
  private final Socket self;
  
  private final InputStream consumedInput;
  
  private static final String PROP_NAME = "com.sun.net.ssl.requireCloseNotify";
  
  BaseSSLSocketImpl() {
    this.self = this;
    this.consumedInput = null;
  }
  
  BaseSSLSocketImpl(Socket paramSocket) {
    this.self = paramSocket;
    this.consumedInput = null;
  }
  
  BaseSSLSocketImpl(Socket paramSocket, InputStream paramInputStream) {
    this.self = paramSocket;
    this.consumedInput = paramInputStream;
  }
  
  static final boolean requireCloseNotify = Debug.getBooleanProperty("com.sun.net.ssl.requireCloseNotify", false);
  
  public final SocketChannel getChannel() {
    if (this.self == this)
      return super.getChannel(); 
    return this.self.getChannel();
  }
  
  public void bind(SocketAddress paramSocketAddress) throws IOException {
    if (this.self == this) {
      super.bind(paramSocketAddress);
    } else {
      throw new IOException("Underlying socket should already be connected");
    } 
  }
  
  public SocketAddress getLocalSocketAddress() {
    if (this.self == this)
      return super.getLocalSocketAddress(); 
    return this.self.getLocalSocketAddress();
  }
  
  public SocketAddress getRemoteSocketAddress() {
    if (this.self == this)
      return super.getRemoteSocketAddress(); 
    return this.self.getRemoteSocketAddress();
  }
  
  public final void connect(SocketAddress paramSocketAddress) throws IOException {
    connect(paramSocketAddress, 0);
  }
  
  public final boolean isConnected() {
    if (this.self == this)
      return super.isConnected(); 
    return this.self.isConnected();
  }
  
  public final boolean isBound() {
    if (this.self == this)
      return super.isBound(); 
    return this.self.isBound();
  }
  
  public final void shutdownInput() throws IOException {
    throw new UnsupportedOperationException("The method shutdownInput() is not supported in SSLSocket");
  }
  
  public final void shutdownOutput() throws IOException {
    throw new UnsupportedOperationException("The method shutdownOutput() is not supported in SSLSocket");
  }
  
  public final boolean isInputShutdown() {
    if (this.self == this)
      return super.isInputShutdown(); 
    return this.self.isInputShutdown();
  }
  
  public final boolean isOutputShutdown() {
    if (this.self == this)
      return super.isOutputShutdown(); 
    return this.self.isOutputShutdown();
  }
  
  protected final void finalize() throws Throwable {
    try {
      close();
    } catch (IOException iOException) {
      try {
        if (this.self == this)
          super.close(); 
      } catch (IOException iOException1) {}
    } finally {
      super.finalize();
    } 
  }
  
  public final InetAddress getInetAddress() {
    if (this.self == this)
      return super.getInetAddress(); 
    return this.self.getInetAddress();
  }
  
  public final InetAddress getLocalAddress() {
    if (this.self == this)
      return super.getLocalAddress(); 
    return this.self.getLocalAddress();
  }
  
  public final int getPort() {
    if (this.self == this)
      return super.getPort(); 
    return this.self.getPort();
  }
  
  public final int getLocalPort() {
    if (this.self == this)
      return super.getLocalPort(); 
    return this.self.getLocalPort();
  }
  
  public final void setTcpNoDelay(boolean paramBoolean) throws SocketException {
    if (this.self == this) {
      super.setTcpNoDelay(paramBoolean);
    } else {
      this.self.setTcpNoDelay(paramBoolean);
    } 
  }
  
  public final boolean getTcpNoDelay() throws SocketException {
    if (this.self == this)
      return super.getTcpNoDelay(); 
    return this.self.getTcpNoDelay();
  }
  
  public final void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {
    if (this.self == this) {
      super.setSoLinger(paramBoolean, paramInt);
    } else {
      this.self.setSoLinger(paramBoolean, paramInt);
    } 
  }
  
  public final int getSoLinger() throws SocketException {
    if (this.self == this)
      return super.getSoLinger(); 
    return this.self.getSoLinger();
  }
  
  public final void sendUrgentData(int paramInt) throws SocketException {
    throw new SocketException("This method is not supported by SSLSockets");
  }
  
  public final void setOOBInline(boolean paramBoolean) throws SocketException {
    throw new SocketException("This method is ineffective, since sending urgent data is not supported by SSLSockets");
  }
  
  public final boolean getOOBInline() throws SocketException {
    throw new SocketException("This method is ineffective, since sending urgent data is not supported by SSLSockets");
  }
  
  public final int getSoTimeout() throws SocketException {
    if (this.self == this)
      return super.getSoTimeout(); 
    return this.self.getSoTimeout();
  }
  
  public final void setSendBufferSize(int paramInt) throws SocketException {
    if (this.self == this) {
      super.setSendBufferSize(paramInt);
    } else {
      this.self.setSendBufferSize(paramInt);
    } 
  }
  
  public final int getSendBufferSize() throws SocketException {
    if (this.self == this)
      return super.getSendBufferSize(); 
    return this.self.getSendBufferSize();
  }
  
  public final void setReceiveBufferSize(int paramInt) throws SocketException {
    if (this.self == this) {
      super.setReceiveBufferSize(paramInt);
    } else {
      this.self.setReceiveBufferSize(paramInt);
    } 
  }
  
  public final int getReceiveBufferSize() throws SocketException {
    if (this.self == this)
      return super.getReceiveBufferSize(); 
    return this.self.getReceiveBufferSize();
  }
  
  public final void setKeepAlive(boolean paramBoolean) throws SocketException {
    if (this.self == this) {
      super.setKeepAlive(paramBoolean);
    } else {
      this.self.setKeepAlive(paramBoolean);
    } 
  }
  
  public final boolean getKeepAlive() throws SocketException {
    if (this.self == this)
      return super.getKeepAlive(); 
    return this.self.getKeepAlive();
  }
  
  public final void setTrafficClass(int paramInt) throws SocketException {
    if (this.self == this) {
      super.setTrafficClass(paramInt);
    } else {
      this.self.setTrafficClass(paramInt);
    } 
  }
  
  public final int getTrafficClass() throws SocketException {
    if (this.self == this)
      return super.getTrafficClass(); 
    return this.self.getTrafficClass();
  }
  
  public final void setReuseAddress(boolean paramBoolean) throws SocketException {
    if (this.self == this) {
      super.setReuseAddress(paramBoolean);
    } else {
      this.self.setReuseAddress(paramBoolean);
    } 
  }
  
  public final boolean getReuseAddress() throws SocketException {
    if (this.self == this)
      return super.getReuseAddress(); 
    return this.self.getReuseAddress();
  }
  
  public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {
    if (this.self == this) {
      super.setPerformancePreferences(paramInt1, paramInt2, paramInt3);
    } else {
      this.self.setPerformancePreferences(paramInt1, paramInt2, paramInt3);
    } 
  }
  
  public String toString() {
    if (this.self == this)
      return super.toString(); 
    return this.self.toString();
  }
  
  public InputStream getInputStream() throws IOException {
    if (this.self == this)
      return super.getInputStream(); 
    if (this.consumedInput != null)
      return new SequenceInputStream(this.consumedInput, this.self
          .getInputStream()); 
    return this.self.getInputStream();
  }
  
  public OutputStream getOutputStream() throws IOException {
    if (this.self == this)
      return super.getOutputStream(); 
    return this.self.getOutputStream();
  }
  
  public synchronized void close() throws IOException {
    if (this.self == this) {
      super.close();
    } else {
      this.self.close();
    } 
  }
  
  public synchronized void setSoTimeout(int paramInt) throws SocketException {
    if (this.self == this) {
      super.setSoTimeout(paramInt);
    } else {
      this.self.setSoTimeout(paramInt);
    } 
  }
  
  boolean isLayered() {
    return (this.self != this);
  }
}
