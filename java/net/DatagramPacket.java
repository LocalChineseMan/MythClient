package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DatagramPacket {
  byte[] buf;
  
  int offset;
  
  int length;
  
  int bufLength;
  
  InetAddress address;
  
  int port;
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    init();
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    setData(paramArrayOfbyte, paramInt1, paramInt2);
    this.address = null;
    this.port = -1;
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt) {
    this(paramArrayOfbyte, 0, paramInt);
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, InetAddress paramInetAddress, int paramInt3) {
    setData(paramArrayOfbyte, paramInt1, paramInt2);
    setAddress(paramInetAddress);
    setPort(paramInt3);
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, SocketAddress paramSocketAddress) {
    setData(paramArrayOfbyte, paramInt1, paramInt2);
    setSocketAddress(paramSocketAddress);
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt1, InetAddress paramInetAddress, int paramInt2) {
    this(paramArrayOfbyte, 0, paramInt1, paramInetAddress, paramInt2);
  }
  
  public DatagramPacket(byte[] paramArrayOfbyte, int paramInt, SocketAddress paramSocketAddress) {
    this(paramArrayOfbyte, 0, paramInt, paramSocketAddress);
  }
  
  public synchronized InetAddress getAddress() {
    return this.address;
  }
  
  public synchronized int getPort() {
    return this.port;
  }
  
  public synchronized byte[] getData() {
    return this.buf;
  }
  
  public synchronized int getOffset() {
    return this.offset;
  }
  
  public synchronized int getLength() {
    return this.length;
  }
  
  public synchronized void setData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0 || paramInt1 < 0 || paramInt2 + paramInt1 < 0 || paramInt2 + paramInt1 > paramArrayOfbyte.length)
      throw new IllegalArgumentException("illegal length or offset"); 
    this.buf = paramArrayOfbyte;
    this.length = paramInt2;
    this.bufLength = paramInt2;
    this.offset = paramInt1;
  }
  
  public synchronized void setAddress(InetAddress paramInetAddress) {
    this.address = paramInetAddress;
  }
  
  public synchronized void setPort(int paramInt) {
    if (paramInt < 0 || paramInt > 65535)
      throw new IllegalArgumentException("Port out of range:" + paramInt); 
    this.port = paramInt;
  }
  
  public synchronized void setSocketAddress(SocketAddress paramSocketAddress) {
    if (paramSocketAddress == null || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("unsupported address type"); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new IllegalArgumentException("unresolved address"); 
    setAddress(inetSocketAddress.getAddress());
    setPort(inetSocketAddress.getPort());
  }
  
  public synchronized SocketAddress getSocketAddress() {
    return new InetSocketAddress(getAddress(), getPort());
  }
  
  public synchronized void setData(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("null packet buffer"); 
    this.buf = paramArrayOfbyte;
    this.offset = 0;
    this.length = paramArrayOfbyte.length;
    this.bufLength = paramArrayOfbyte.length;
  }
  
  public synchronized void setLength(int paramInt) {
    if (paramInt + this.offset > this.buf.length || paramInt < 0 || paramInt + this.offset < 0)
      throw new IllegalArgumentException("illegal length"); 
    this.length = paramInt;
    this.bufLength = this.length;
  }
  
  private static native void init();
}
