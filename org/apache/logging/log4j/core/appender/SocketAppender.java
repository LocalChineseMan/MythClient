package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.net.DatagramSocketManager;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.net.SocketOptions;
import org.apache.logging.log4j.core.net.SslSocketManager;
import org.apache.logging.log4j.core.net.TcpSocketManager;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(name = "Socket", category = "Core", elementType = "appender", printObject = true)
public class SocketAppender extends AbstractOutputStreamAppender<AbstractSocketManager> {
  private final Object advertisement;
  
  private final Advertiser advertiser;
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  protected SocketAppender(String name, Layout<? extends Serializable> layout, Filter filter, AbstractSocketManager manager, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser, Property[] properties) {
    super(name, layout, filter, ignoreExceptions, immediateFlush, properties, (M)manager);
    if (advertiser != null) {
      Map<String, String> configuration = new HashMap<>(layout.getContentFormat());
      configuration.putAll(manager.getContentFormat());
      configuration.put("contentType", layout.getContentType());
      configuration.put("name", name);
      this.advertisement = advertiser.advertise(configuration);
    } else {
      this.advertisement = null;
    } 
    this.advertiser = advertiser;
  }
  
  @Deprecated
  protected SocketAppender(String name, Layout<? extends Serializable> layout, Filter filter, AbstractSocketManager manager, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser) {
    this(name, layout, filter, manager, ignoreExceptions, immediateFlush, advertiser, Property.EMPTY_ARRAY);
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    stop(timeout, timeUnit, false);
    if (this.advertiser != null)
      this.advertiser.unadvertise(this.advertisement); 
    setStopped();
    return true;
  }
  
  @Deprecated
  @PluginFactory
  public static SocketAppender createAppender(String host, int port, Protocol protocol, SslConfiguration sslConfig, int connectTimeoutMillis, int reconnectDelayMillis, boolean immediateFail, String name, boolean immediateFlush, boolean ignoreExceptions, Layout<? extends Serializable> layout, Filter filter, boolean advertise, Configuration configuration) {
    return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)newBuilder()
      .withAdvertise(advertise))
      .setConfiguration(configuration))
      .withConnectTimeoutMillis(connectTimeoutMillis)).setFilter(filter))
      .withHost(host)).setIgnoreExceptions(ignoreExceptions))
      .withImmediateFail(immediateFail)).setLayout(layout)).setName(name))
      .withPort(port))
      .withProtocol(protocol))
      .withReconnectDelayMillis(reconnectDelayMillis))
      .withSslConfiguration(sslConfig))
      .build();
  }
  
  @Deprecated
  public static SocketAppender createAppender(String host, String portNum, String protocolIn, SslConfiguration sslConfig, int connectTimeoutMillis, String delayMillis, String immediateFail, String name, String immediateFlush, String ignore, Layout<? extends Serializable> layout, Filter filter, String advertise, Configuration config) {
    boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
    boolean isAdvertise = Boolean.parseBoolean(advertise);
    boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    boolean fail = Booleans.parseBoolean(immediateFail, true);
    int reconnectDelayMillis = AbstractAppender.parseInt(delayMillis, 0);
    int port = AbstractAppender.parseInt(portNum, 0);
    Protocol p = (protocolIn == null) ? Protocol.UDP : Protocol.valueOf(protocolIn);
    return createAppender(host, port, p, sslConfig, connectTimeoutMillis, reconnectDelayMillis, fail, name, isFlush, ignoreExceptions, layout, filter, isAdvertise, config);
  }
  
  @Deprecated
  protected static AbstractSocketManager createSocketManager(String name, Protocol protocol, String host, int port, int connectTimeoutMillis, SslConfiguration sslConfig, int reconnectDelayMillis, boolean immediateFail, Layout<? extends Serializable> layout, int bufferSize) {
    return createSocketManager(name, protocol, host, port, connectTimeoutMillis, sslConfig, reconnectDelayMillis, immediateFail, layout, bufferSize, (SocketOptions)null);
  }
  
  protected static AbstractSocketManager createSocketManager(String name, Protocol protocol, String host, int port, int connectTimeoutMillis, SslConfiguration sslConfig, int reconnectDelayMillis, boolean immediateFail, Layout<? extends Serializable> layout, int bufferSize, SocketOptions socketOptions) {
    if (protocol == Protocol.TCP && sslConfig != null)
      protocol = Protocol.SSL; 
    if (protocol != Protocol.SSL && sslConfig != null)
      LOGGER.info("Appender {} ignoring SSL configuration for {} protocol", name, protocol); 
    switch (null.$SwitchMap$org$apache$logging$log4j$core$net$Protocol[protocol.ordinal()]) {
      case 1:
        return (AbstractSocketManager)TcpSocketManager.getSocketManager(host, port, connectTimeoutMillis, reconnectDelayMillis, immediateFail, layout, bufferSize, socketOptions);
      case 2:
        return (AbstractSocketManager)DatagramSocketManager.getSocketManager(host, port, layout, bufferSize);
      case 3:
        return (AbstractSocketManager)SslSocketManager.getSocketManager(sslConfig, host, port, connectTimeoutMillis, reconnectDelayMillis, immediateFail, layout, bufferSize, socketOptions);
    } 
    throw new IllegalArgumentException(protocol.toString());
  }
  
  protected void directEncodeEvent(LogEvent event) {
    writeByteArrayToManager(event);
  }
  
  public static class SocketAppender {}
  
  public static abstract class SocketAppender {}
}
