package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.net.ResourceManager;

class TwoStacksPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl {
  private FileDescriptor fd1;
  
  private InetAddress anyLocalBoundAddr = null;
  
  private int fduse = -1;
  
  private int lastfd = -1;
  
  private final boolean exclusiveBind;
  
  private boolean reuseAddressEmulated;
  
  private boolean isReuseAddress;
  
  static {
    init();
  }
  
  TwoStacksPlainDatagramSocketImpl(boolean paramBoolean) {
    this.exclusiveBind = paramBoolean;
  }
  
  protected synchronized void create() throws SocketException {
    this.fd1 = new FileDescriptor();
    try {
      super.create();
    } catch (SocketException socketException) {
      this.fd1 = null;
      throw socketException;
    } 
  }
  
  protected synchronized void bind(int paramInt, InetAddress paramInetAddress) throws SocketException {
    super.bind(paramInt, paramInetAddress);
    if (paramInetAddress.isAnyLocalAddress())
      this.anyLocalBoundAddr = paramInetAddress; 
  }
  
  protected synchronized void bind0(int paramInt, InetAddress paramInetAddress) throws SocketException {
    bind0(paramInt, paramInetAddress, this.exclusiveBind);
  }
  
  protected synchronized void receive(DatagramPacket paramDatagramPacket) throws IOException {
    try {
      receive0(paramDatagramPacket);
    } finally {
      this.fduse = -1;
    } 
  }
  
  public Object getOption(int paramInt) throws SocketException {
    if (isClosed())
      throw new SocketException("Socket Closed"); 
    if (paramInt == 15) {
      if (this.fd != null && this.fd1 != null && !this.connected)
        return this.anyLocalBoundAddr; 
      boolean bool = (this.connectedAddress == null) ? true : this.connectedAddress.holder().getFamily();
      return socketLocalAddress(bool);
    } 
    if (paramInt == 4 && this.reuseAddressEmulated)
      return Boolean.valueOf(this.isReuseAddress); 
    return super.getOption(paramInt);
  }
  
  protected void socketSetOption(int paramInt, Object paramObject) throws SocketException {
    if (paramInt == 4 && this.exclusiveBind && this.localPort != 0) {
      this.reuseAddressEmulated = true;
      this.isReuseAddress = ((Boolean)paramObject).booleanValue();
    } else {
      socketNativeSetOption(paramInt, paramObject);
    } 
  }
  
  protected boolean isClosed() {
    return (this.fd == null && this.fd1 == null);
  }
  
  protected void close() {
    if (this.fd != null || this.fd1 != null) {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      this.fd = null;
      this.fd1 = null;
    } 
  }
  
  protected synchronized native void bind0(int paramInt, InetAddress paramInetAddress, boolean paramBoolean) throws SocketException;
  
  protected native void send(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected synchronized native int peek(InetAddress paramInetAddress) throws IOException;
  
  protected synchronized native int peekData(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected synchronized native void receive0(DatagramPacket paramDatagramPacket) throws IOException;
  
  protected native void setTimeToLive(int paramInt) throws IOException;
  
  protected native int getTimeToLive() throws IOException;
  
  @Deprecated
  protected native void setTTL(byte paramByte) throws IOException;
  
  @Deprecated
  protected native byte getTTL() throws IOException;
  
  protected native void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected native void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  protected native void datagramSocketCreate() throws SocketException;
  
  protected native void datagramSocketClose();
  
  protected native void socketNativeSetOption(int paramInt, Object paramObject) throws SocketException;
  
  protected native Object socketGetOption(int paramInt) throws SocketException;
  
  protected native void connect0(InetAddress paramInetAddress, int paramInt) throws SocketException;
  
  protected native Object socketLocalAddress(int paramInt) throws SocketException;
  
  protected native void disconnect0(int paramInt);
  
  private static native void init();
}
