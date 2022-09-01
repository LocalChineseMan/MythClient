package org.apache.logging.log4j.core.appender.mom.kafka;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "Kafka", category = "Core", elementType = "appender", printObject = true)
public final class KafkaAppender extends AbstractAppender {
  private final Integer retryCount;
  
  private final KafkaManager manager;
  
  @Deprecated
  public static KafkaAppender createAppender(Layout<? extends Serializable> layout, Filter filter, String name, boolean ignoreExceptions, String topic, Property[] properties, Configuration configuration, String key) {
    if (layout == null) {
      AbstractLifeCycle.LOGGER.error("No layout provided for KafkaAppender");
      return null;
    } 
    KafkaManager kafkaManager = KafkaManager.getManager(configuration.getLoggerContext(), name, topic, true, properties, key);
    return new KafkaAppender(name, layout, filter, ignoreExceptions, kafkaManager, null, null);
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private KafkaAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, KafkaManager manager, Property[] properties, Integer retryCount) {
    super(name, filter, layout, ignoreExceptions, properties);
    this.manager = Objects.<KafkaManager>requireNonNull(manager, "manager");
    this.retryCount = retryCount;
  }
  
  public void append(LogEvent event) {
    if (event.getLoggerName() != null && event.getLoggerName().startsWith("org.apache.kafka")) {
      LOGGER.warn("Recursive logging from [{}] for appender [{}].", event.getLoggerName(), getName());
    } else {
      try {
        tryAppend(event);
      } catch (Exception e) {
        if (this.retryCount != null) {
          int currentRetryAttempt = 0;
          while (currentRetryAttempt < this.retryCount.intValue()) {
            currentRetryAttempt++;
            try {
              tryAppend(event);
              break;
            } catch (Exception exception) {}
          } 
        } 
        error("Unable to write to Kafka in appender [" + getName() + "]", event, e);
      } 
    } 
  }
  
  private void tryAppend(LogEvent event) throws ExecutionException, InterruptedException, TimeoutException {
    byte[] data;
    Layout<? extends Serializable> layout = getLayout();
    if (layout instanceof org.apache.logging.log4j.core.layout.SerializedLayout) {
      byte[] header = layout.getHeader();
      byte[] body = layout.toByteArray(event);
      data = new byte[header.length + body.length];
      System.arraycopy(header, 0, data, 0, header.length);
      System.arraycopy(body, 0, data, header.length, body.length);
    } else {
      data = layout.toByteArray(event);
    } 
    this.manager.send(data);
  }
  
  public void start() {
    super.start();
    this.manager.startup();
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    boolean stopped = stop(timeout, timeUnit, false);
    stopped &= this.manager.stop(timeout, timeUnit);
    setStopped();
    return stopped;
  }
  
  public String toString() {
    return "KafkaAppender{name=" + getName() + ", state=" + getState() + ", topic=" + this.manager.getTopic() + '}';
  }
  
  public static class KafkaAppender {}
}
