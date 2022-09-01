package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.LoggerFields;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.util.EnglishEnums;

@Plugin(name = "Syslog", category = "Core", elementType = "appender", printObject = true)
public class SyslogAppender extends SocketAppender {
  protected static final String RFC5424 = "RFC5424";
  
  protected SyslogAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, AbstractSocketManager manager, Advertiser advertiser, Property[] properties) {
    super(name, layout, filter, manager, ignoreExceptions, immediateFlush, advertiser, properties);
  }
  
  @Deprecated
  protected SyslogAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, AbstractSocketManager manager, Advertiser advertiser) {
    super(name, layout, filter, manager, ignoreExceptions, immediateFlush, advertiser, Property.EMPTY_ARRAY);
  }
  
  @Deprecated
  public static <B extends Builder<B>> SyslogAppender createAppender(String host, int port, String protocolStr, SslConfiguration sslConfiguration, int connectTimeoutMillis, int reconnectDelayMillis, boolean immediateFail, String name, boolean immediateFlush, boolean ignoreExceptions, Facility facility, String id, int enterpriseNumber, boolean includeMdc, String mdcId, String mdcPrefix, String eventPrefix, boolean newLine, String escapeNL, String appName, String msgId, String excludes, String includes, String required, String format, Filter filter, Configuration configuration, Charset charset, String exceptionPattern, LoggerFields[] loggerFields, boolean advertise) {
    return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)newSyslogAppenderBuilder()
      .withHost(host))
      .withPort(port))
      .withProtocol((Protocol)EnglishEnums.valueOf(Protocol.class, protocolStr)))
      .withSslConfiguration(sslConfiguration))
      .withConnectTimeoutMillis(connectTimeoutMillis))
      .withReconnectDelayMillis(reconnectDelayMillis))
      .withImmediateFail(immediateFail)).setName(appName))
      .withImmediateFlush(immediateFlush)).setIgnoreExceptions(ignoreExceptions)).setFilter(filter))
      .setConfiguration(configuration))
      .withAdvertise(advertise))
      .setFacility(facility)
      .setId(id)
      .setEnterpriseNumber(enterpriseNumber)
      .setIncludeMdc(includeMdc)
      .setMdcId(mdcId)
      .setMdcPrefix(mdcPrefix)
      .setEventPrefix(eventPrefix)
      .setNewLine(newLine)
      .setAppName(appName)
      .setMsgId(msgId)
      .setExcludes(excludes)
      .setIncludeMdc(includeMdc)
      .setRequired(required)
      .setFormat(format)
      .setCharsetName(charset)
      .setExceptionPattern(exceptionPattern)
      .setLoggerFields(loggerFields)
      .build();
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newSyslogAppenderBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  public static class SyslogAppender {}
}
