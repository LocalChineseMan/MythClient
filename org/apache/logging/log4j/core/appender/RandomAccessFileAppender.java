package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(name = "RandomAccessFile", category = "Core", elementType = "appender", printObject = true)
public final class RandomAccessFileAppender extends AbstractOutputStreamAppender<RandomAccessFileManager> {
  private final String fileName;
  
  private Object advertisement;
  
  private final Advertiser advertiser;
  
  private RandomAccessFileAppender(String name, Layout<? extends Serializable> layout, Filter filter, RandomAccessFileManager manager, String filename, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser, Property[] properties) {
    super(name, layout, filter, ignoreExceptions, immediateFlush, properties, (M)manager);
    if (advertiser != null) {
      Map<String, String> configuration = new HashMap<>(layout.getContentFormat());
      configuration.putAll(manager.getContentFormat());
      configuration.put("contentType", layout.getContentType());
      configuration.put("name", name);
      this.advertisement = advertiser.advertise(configuration);
    } 
    this.fileName = filename;
    this.advertiser = advertiser;
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    stop(timeout, timeUnit, false);
    if (this.advertiser != null)
      this.advertiser.unadvertise(this.advertisement); 
    setStopped();
    return true;
  }
  
  public String getFileName() {
    return this.fileName;
  }
  
  public int getBufferSize() {
    return ((RandomAccessFileManager)getManager()).getBufferSize();
  }
  
  @Deprecated
  public static <B extends Builder<B>> RandomAccessFileAppender createAppender(String fileName, String append, String name, String immediateFlush, String bufferSizeStr, String ignore, Layout<? extends Serializable> layout, Filter filter, String advertise, String advertiseURI, Configuration configuration) {
    boolean isAppend = Booleans.parseBoolean(append, true);
    boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
    boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    boolean isAdvertise = Boolean.parseBoolean(advertise);
    int bufferSize = Integers.parseInt(bufferSizeStr, 262144);
    return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)newBuilder()
      .setAdvertise(isAdvertise)
      .setAdvertiseURI(advertiseURI)
      .setAppend(isAppend)
      .withBufferSize(bufferSize))
      .setConfiguration(configuration))
      .setFileName(fileName).setFilter(filter)).setIgnoreExceptions(ignoreExceptions))
      .withImmediateFlush(isFlush)).setLayout(layout)).setName(name))
      .build();
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  public static class RandomAccessFileAppender {}
}
