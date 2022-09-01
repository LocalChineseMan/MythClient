package sun.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;

public final class NetHooks {
  public static void beforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {}
  
  public static void beforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt) throws IOException {}
}
