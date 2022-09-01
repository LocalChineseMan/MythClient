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

@Plugin(name = "File", category = "Core", elementType = "appender", printObject = true)
public final class FileAppender extends AbstractOutputStreamAppender<FileManager> {
  public static final String PLUGIN_NAME = "File";
  
  private static final int DEFAULT_BUFFER_SIZE = 8192;
  
  private final String fileName;
  
  private final Advertiser advertiser;
  
  private final Object advertisement;
  
  @Deprecated
  public static <B extends Builder<B>> FileAppender createAppender(String fileName, String append, String locking, String name, String immediateFlush, String ignoreExceptions, String bufferedIo, String bufferSizeStr, Layout<? extends Serializable> layout, Filter filter, String advertise, String advertiseUri, Configuration config) {
    return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)newBuilder()
      .withAdvertise(Boolean.parseBoolean(advertise))
      .withAdvertiseUri(advertiseUri)
      .withAppend(Booleans.parseBoolean(append, true))
      .withBufferedIo(Booleans.parseBoolean(bufferedIo, true)))
      .withBufferSize(Integers.parseInt(bufferSizeStr, 8192)))
      .setConfiguration(config))
      .withFileName(fileName).setFilter(filter)).setIgnoreExceptions(Booleans.parseBoolean(ignoreExceptions, true)))
      .withImmediateFlush(Booleans.parseBoolean(immediateFlush, true))).setLayout(layout))
      .withLocking(Boolean.parseBoolean(locking)).setName(name))
      .build();
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private FileAppender(String name, Layout<? extends Serializable> layout, Filter filter, FileManager manager, String filename, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser, Property[] properties) {
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
    this.fileName = filename;
    this.advertiser = advertiser;
  }
  
  public String getFileName() {
    return this.fileName;
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    stop(timeout, timeUnit, false);
    if (this.advertiser != null)
      this.advertiser.unadvertise(this.advertisement); 
    setStopped();
    return true;
  }
  
  public static class FileAppender {}
}
