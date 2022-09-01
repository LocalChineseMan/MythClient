package org.apache.logging.log4j.core.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "SocketAddress", category = "Core", printObject = true)
public class SocketAddress {
  private final InetSocketAddress socketAddress;
  
  public static SocketAddress getLoopback() {
    return new SocketAddress(InetAddress.getLoopbackAddress(), 0);
  }
  
  private SocketAddress(InetAddress host, int port) {
    this.socketAddress = new InetSocketAddress(host, port);
  }
  
  public InetSocketAddress getSocketAddress() {
    return this.socketAddress;
  }
  
  public int getPort() {
    return this.socketAddress.getPort();
  }
  
  public InetAddress getAddress() {
    return this.socketAddress.getAddress();
  }
  
  public String getHostName() {
    return this.socketAddress.getHostName();
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public String toString() {
    return this.socketAddress.toString();
  }
  
  public static class SocketAddress {}
}
