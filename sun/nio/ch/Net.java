package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import jdk.net.NetworkPermission;
import jdk.net.SocketFlow;
import sun.net.ExtendedOptionsImpl;

public class Net {
  static final ProtocolFamily UNSPEC = new ProtocolFamily() {
      public String name() {
        return "UNSPEC";
      }
    };
  
  private static final boolean exclusiveBind;
  
  static {
    int i = isExclusiveBindAvailable();
    if (i >= 0) {
      String str = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
            public String run() {
              return System.getProperty("sun.net.useExclusiveBind");
            }
          });
      if (str != null) {
        exclusiveBind = (str.length() == 0) ? true : Boolean.parseBoolean(str);
      } else if (i == 1) {
        exclusiveBind = true;
      } else {
        exclusiveBind = false;
      } 
    } else {
      exclusiveBind = false;
    } 
  }
  
  private static volatile boolean checkedIPv6 = false;
  
  private static volatile boolean isIPv6Available;
  
  public static final int SHUT_RD = 0;
  
  public static final int SHUT_WR = 1;
  
  public static final int SHUT_RDWR = 2;
  
  static boolean isIPv6Available() {
    if (!checkedIPv6) {
      isIPv6Available = isIPv6Available0();
      checkedIPv6 = true;
    } 
    return isIPv6Available;
  }
  
  static boolean useExclusiveBind() {
    return exclusiveBind;
  }
  
  static boolean canIPv6SocketJoinIPv4Group() {
    return canIPv6SocketJoinIPv4Group0();
  }
  
  static boolean canJoin6WithIPv4Group() {
    return canJoin6WithIPv4Group0();
  }
  
  public static InetSocketAddress checkAddress(SocketAddress paramSocketAddress) {
    if (paramSocketAddress == null)
      throw new NullPointerException(); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new UnsupportedAddressTypeException(); 
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (inetSocketAddress.isUnresolved())
      throw new UnresolvedAddressException(); 
    InetAddress inetAddress = inetSocketAddress.getAddress();
    if (!(inetAddress instanceof Inet4Address) && !(inetAddress instanceof java.net.Inet6Address))
      throw new IllegalArgumentException("Invalid address type"); 
    return inetSocketAddress;
  }
  
  static InetSocketAddress asInetSocketAddress(SocketAddress paramSocketAddress) {
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new UnsupportedAddressTypeException(); 
    return (InetSocketAddress)paramSocketAddress;
  }
  
  static void translateToSocketException(Exception paramException) throws SocketException {
    if (paramException instanceof SocketException)
      throw (SocketException)paramException; 
    Exception exception = paramException;
    if (paramException instanceof java.nio.channels.ClosedChannelException) {
      exception = new SocketException("Socket is closed");
    } else if (paramException instanceof java.nio.channels.NotYetConnectedException) {
      exception = new SocketException("Socket is not connected");
    } else if (paramException instanceof java.nio.channels.AlreadyBoundException) {
      exception = new SocketException("Already bound");
    } else if (paramException instanceof java.nio.channels.NotYetBoundException) {
      exception = new SocketException("Socket is not bound yet");
    } else if (paramException instanceof UnsupportedAddressTypeException) {
      exception = new SocketException("Unsupported address type");
    } else if (paramException instanceof UnresolvedAddressException) {
      exception = new SocketException("Unresolved address");
    } 
    if (exception != paramException)
      exception.initCause(paramException); 
    if (exception instanceof SocketException)
      throw (SocketException)exception; 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    throw new Error("Untranslated exception", exception);
  }
  
  static void translateException(Exception paramException, boolean paramBoolean) throws IOException {
    if (paramException instanceof IOException)
      throw (IOException)paramException; 
    if (paramBoolean && paramException instanceof UnresolvedAddressException)
      throw new UnknownHostException(); 
    translateToSocketException(paramException);
  }
  
  static void translateException(Exception paramException) throws IOException {
    translateException(paramException, false);
  }
  
  static InetSocketAddress getRevealedLocalAddress(InetSocketAddress paramInetSocketAddress) {
    SecurityManager securityManager = System.getSecurityManager();
    if (paramInetSocketAddress == null || securityManager == null)
      return paramInetSocketAddress; 
    try {
      securityManager.checkConnect(paramInetSocketAddress.getAddress().getHostAddress(), -1);
    } catch (SecurityException securityException) {
      paramInetSocketAddress = getLoopbackAddress(paramInetSocketAddress.getPort());
    } 
    return paramInetSocketAddress;
  }
  
  static String getRevealedLocalAddressAsString(InetSocketAddress paramInetSocketAddress) {
    return (System.getSecurityManager() == null) ? paramInetSocketAddress.toString() : 
      getLoopbackAddress(paramInetSocketAddress.getPort()).toString();
  }
  
  private static InetSocketAddress getLoopbackAddress(int paramInt) {
    return new InetSocketAddress(InetAddress.getLoopbackAddress(), paramInt);
  }
  
  static Inet4Address anyInet4Address(NetworkInterface paramNetworkInterface) {
    return AccessController.<Inet4Address>doPrivileged((PrivilegedAction<Inet4Address>)new Object(paramNetworkInterface));
  }
  
  static int inet4AsInt(InetAddress paramInetAddress) {
    if (paramInetAddress instanceof Inet4Address) {
      byte[] arrayOfByte = paramInetAddress.getAddress();
      int i = arrayOfByte[3] & 0xFF;
      i |= arrayOfByte[2] << 8 & 0xFF00;
      i |= arrayOfByte[1] << 16 & 0xFF0000;
      i |= arrayOfByte[0] << 24 & 0xFF000000;
      return i;
    } 
    throw new AssertionError("Should not reach here");
  }
  
  static InetAddress inet4FromInt(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(paramInt >>> 24 & 0xFF);
    arrayOfByte[1] = (byte)(paramInt >>> 16 & 0xFF);
    arrayOfByte[2] = (byte)(paramInt >>> 8 & 0xFF);
    arrayOfByte[3] = (byte)(paramInt & 0xFF);
    try {
      return InetAddress.getByAddress(arrayOfByte);
    } catch (UnknownHostException unknownHostException) {
      throw new AssertionError("Should not reach here");
    } 
  }
  
  static byte[] inet6AsByteArray(InetAddress paramInetAddress) {
    if (paramInetAddress instanceof java.net.Inet6Address)
      return paramInetAddress.getAddress(); 
    if (paramInetAddress instanceof Inet4Address) {
      byte[] arrayOfByte1 = paramInetAddress.getAddress();
      byte[] arrayOfByte2 = new byte[16];
      arrayOfByte2[10] = -1;
      arrayOfByte2[11] = -1;
      arrayOfByte2[12] = arrayOfByte1[0];
      arrayOfByte2[13] = arrayOfByte1[1];
      arrayOfByte2[14] = arrayOfByte1[2];
      arrayOfByte2[15] = arrayOfByte1[3];
      return arrayOfByte2;
    } 
    throw new AssertionError("Should not reach here");
  }
  
  static void setSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption, Object paramObject) throws IOException {
    boolean bool1;
    if (paramObject == null)
      throw new IllegalArgumentException("Invalid option value"); 
    Class<?> clazz = paramSocketOption.type();
    if (clazz == SocketFlow.class) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new NetworkPermission("setOption.SO_FLOW_SLA")); 
      ExtendedOptionsImpl.setFlowOption(paramFileDescriptor, (SocketFlow)paramObject);
      return;
    } 
    if (clazz != Integer.class && clazz != Boolean.class)
      throw new AssertionError("Should not reach here"); 
    if (paramSocketOption == StandardSocketOptions.SO_RCVBUF || paramSocketOption == StandardSocketOptions.SO_SNDBUF) {
      int i = ((Integer)paramObject).intValue();
      if (i < 0)
        throw new IllegalArgumentException("Invalid send/receive buffer size"); 
    } 
    if (paramSocketOption == StandardSocketOptions.SO_LINGER) {
      int i = ((Integer)paramObject).intValue();
      if (i < 0)
        paramObject = Integer.valueOf(-1); 
      if (i > 65535)
        paramObject = Integer.valueOf(65535); 
    } 
    if (paramSocketOption == StandardSocketOptions.IP_TOS) {
      int i = ((Integer)paramObject).intValue();
      if (i < 0 || i > 255)
        throw new IllegalArgumentException("Invalid IP_TOS value"); 
    } 
    if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) {
      int i = ((Integer)paramObject).intValue();
      if (i < 0 || i > 255)
        throw new IllegalArgumentException("Invalid TTL/hop value"); 
    } 
    OptionKey optionKey = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
    if (optionKey == null)
      throw new AssertionError("Option not found"); 
    if (clazz == Integer.class) {
      bool1 = ((Integer)paramObject).intValue();
    } else {
      boolean bool = ((Boolean)paramObject).booleanValue();
      bool1 = bool ? true : false;
    } 
    boolean bool2 = (paramProtocolFamily == UNSPEC) ? true : false;
    boolean bool3 = (paramProtocolFamily == StandardProtocolFamily.INET6) ? true : false;
    setIntOption0(paramFileDescriptor, bool2, optionKey.level(), optionKey.name(), bool1, bool3);
  }
  
  static Object getSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption) throws IOException {
    Class<?> clazz = paramSocketOption.type();
    if (clazz == SocketFlow.class) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new NetworkPermission("getOption.SO_FLOW_SLA")); 
      SocketFlow socketFlow = SocketFlow.create();
      ExtendedOptionsImpl.getFlowOption(paramFileDescriptor, socketFlow);
      return socketFlow;
    } 
    if (clazz != Integer.class && clazz != Boolean.class)
      throw new AssertionError("Should not reach here"); 
    OptionKey optionKey = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
    if (optionKey == null)
      throw new AssertionError("Option not found"); 
    boolean bool = (paramProtocolFamily == UNSPEC) ? true : false;
    int i = getIntOption0(paramFileDescriptor, bool, optionKey.level(), optionKey.name());
    if (clazz == Integer.class)
      return Integer.valueOf(i); 
    return (i == 0) ? Boolean.FALSE : Boolean.TRUE;
  }
  
  static FileDescriptor socket(boolean paramBoolean) throws IOException {
    return socket(UNSPEC, paramBoolean);
  }
  
  static FileDescriptor socket(ProtocolFamily paramProtocolFamily, boolean paramBoolean) throws IOException {
    boolean bool = (isIPv6Available() && paramProtocolFamily != StandardProtocolFamily.INET) ? true : false;
    return IOUtil.newFD(socket0(bool, paramBoolean, false));
  }
  
  static FileDescriptor serverSocket(boolean paramBoolean) {
    return IOUtil.newFD(socket0(isIPv6Available(), paramBoolean, true));
  }
  
  public static void bind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {
    bind(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
  }
  
  static void bind(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {
    boolean bool = (isIPv6Available() && paramProtocolFamily != StandardProtocolFamily.INET) ? true : false;
    bind0(paramFileDescriptor, bool, exclusiveBind, paramInetAddress, paramInt);
  }
  
  static int connect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {
    return connect(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
  }
  
  static int connect(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {
    boolean bool = (isIPv6Available() && paramProtocolFamily != StandardProtocolFamily.INET) ? true : false;
    return connect0(bool, paramFileDescriptor, paramInetAddress, paramInt);
  }
  
  public static InetSocketAddress localAddress(FileDescriptor paramFileDescriptor) throws IOException {
    return new InetSocketAddress(localInetAddress(paramFileDescriptor), localPort(paramFileDescriptor));
  }
  
  static InetSocketAddress remoteAddress(FileDescriptor paramFileDescriptor) throws IOException {
    return new InetSocketAddress(remoteInetAddress(paramFileDescriptor), remotePort(paramFileDescriptor));
  }
  
  static int join4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    return joinOrDrop4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
  }
  
  static void drop4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    joinOrDrop4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
  }
  
  static int block4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    return blockOrUnblock4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
  }
  
  static void unblock4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    blockOrUnblock4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
  }
  
  static int join6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException {
    return joinOrDrop6(true, paramFileDescriptor, paramArrayOfbyte1, paramInt, paramArrayOfbyte2);
  }
  
  static void drop6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException {
    joinOrDrop6(false, paramFileDescriptor, paramArrayOfbyte1, paramInt, paramArrayOfbyte2);
  }
  
  static int block6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException {
    return blockOrUnblock6(true, paramFileDescriptor, paramArrayOfbyte1, paramInt, paramArrayOfbyte2);
  }
  
  static void unblock6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException {
    blockOrUnblock6(false, paramFileDescriptor, paramArrayOfbyte1, paramInt, paramArrayOfbyte2);
  }
  
  static {
    IOUtil.load();
    initIDs();
  }
  
  public static final short POLLIN = pollinValue();
  
  public static final short POLLOUT = polloutValue();
  
  public static final short POLLERR = pollerrValue();
  
  public static final short POLLHUP = pollhupValue();
  
  public static final short POLLNVAL = pollnvalValue();
  
  public static final short POLLCONN = pollconnValue();
  
  private static native boolean isIPv6Available0();
  
  private static native int isExclusiveBindAvailable();
  
  private static native boolean canIPv6SocketJoinIPv4Group0();
  
  private static native boolean canJoin6WithIPv4Group0();
  
  private static native int socket0(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  private static native void bind0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, InetAddress paramInetAddress, int paramInt) throws IOException;
  
  static native void listen(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
  
  private static native int connect0(boolean paramBoolean, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException;
  
  static native void shutdown(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
  
  private static native int localPort(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native InetAddress localInetAddress(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native int remotePort(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native InetAddress remoteInetAddress(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native int getIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean, int paramInt1, int paramInt2) throws IOException;
  
  private static native void setIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean2) throws IOException;
  
  static native int poll(FileDescriptor paramFileDescriptor, int paramInt, long paramLong) throws IOException;
  
  private static native int joinOrDrop4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException;
  
  private static native int blockOrUnblock4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3) throws IOException;
  
  private static native int joinOrDrop6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException;
  
  static native int blockOrUnblock6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) throws IOException;
  
  static native void setInterface4(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
  
  static native int getInterface4(FileDescriptor paramFileDescriptor) throws IOException;
  
  static native void setInterface6(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
  
  static native int getInterface6(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native void initIDs();
  
  static native short pollinValue();
  
  static native short polloutValue();
  
  static native short pollerrValue();
  
  static native short pollhupValue();
  
  static native short pollnvalValue();
  
  static native short pollconnValue();
}
