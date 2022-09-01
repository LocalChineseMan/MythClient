package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.Set;

public interface NetworkChannel extends Channel {
  NetworkChannel bind(SocketAddress paramSocketAddress) throws IOException;
  
  SocketAddress getLocalAddress() throws IOException;
  
  <T> NetworkChannel setOption(SocketOption<T> paramSocketOption, T paramT) throws IOException;
  
  <T> T getOption(SocketOption<T> paramSocketOption) throws IOException;
  
  Set<SocketOption<?>> supportedOptions();
}
