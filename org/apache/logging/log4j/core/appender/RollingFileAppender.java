package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(name = "RollingFile", category = "Core", elementType = "appender", printObject = true)
public final class RollingFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {
  public static final String PLUGIN_NAME = "RollingFile";
  
  private static final int DEFAULT_BUFFER_SIZE = 8192;
  
  private final String fileName;
  
  private final String filePattern;
  
  private Object advertisement;
  
  private final Advertiser advertiser;
  
  private RollingFileAppender(String name, Layout<? extends Serializable> layout, Filter filter, RollingFileManager manager, String fileName, String filePattern, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser, Property[] properties) {
    super(name, layout, filter, ignoreExceptions, immediateFlush, properties, (M)manager);
    if (advertiser != null) {
      Map<String, String> configuration = new HashMap<>(layout.getContentFormat());
      configuration.put("contentType", layout.getContentType());
      configuration.put("name", name);
      this.advertisement = advertiser.advertise(configuration);
    } 
    this.fileName = fileName;
    this.filePattern = filePattern;
    this.advertiser = advertiser;
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    boolean stopped = stop(timeout, timeUnit, false);
    if (this.advertiser != null)
      this.advertiser.unadvertise(this.advertisement); 
    setStopped();
    return stopped;
  }
  
  public void append(LogEvent event) {
    ((RollingFileManager)getManager()).checkRollover(event);
    super.append(event);
  }
  
  public String getFileName() {
    return this.fileName;
  }
  
  public String getFilePattern() {
    return this.filePattern;
  }
  
  public <T extends TriggeringPolicy> T getTriggeringPolicy() {
    return (T)((RollingFileManager)getManager()).getTriggeringPolicy();
  }
  
  @Deprecated
  public static <B extends Builder<B>> RollingFileAppender createAppender(String fileName, String filePattern, String append, String name, String bufferedIO, String bufferSizeStr, String immediateFlush, TriggeringPolicy policy, RolloverStrategy strategy, Layout<? extends Serializable> layout, Filter filter, String ignore, String advertise, String advertiseUri, Configuration config) {
    int bufferSize = Integers.parseInt(bufferSizeStr, 8192);
    return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)newBuilder()
      .withAdvertise(Boolean.parseBoolean(advertise))
      .withAdvertiseUri(advertiseUri)
      .withAppend(Booleans.parseBoolean(append, true))
      .withBufferedIo(Booleans.parseBoolean(bufferedIO, true)))
      .withBufferSize(bufferSize))
      .setConfiguration(config))
      .withFileName(fileName)
      .withFilePattern(filePattern).setFilter(filter)).setIgnoreExceptions(Booleans.parseBoolean(ignore, true)))
      .withImmediateFlush(Booleans.parseBoolean(immediateFlush, true))).setLayout(layout))
      .withCreateOnDemand(false)
      .withLocking(false).setName(name))
      .withPolicy(policy)
      .withStrategy(strategy)
      .build();
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  public static class RollingFileAppender {}
}
