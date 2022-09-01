package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "Http", category = "Core", elementType = "appender", printObject = true)
public final class HttpAppender extends AbstractAppender {
  private final HttpManager manager;
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private HttpAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, HttpManager manager, Property[] properties) {
    super(name, filter, layout, ignoreExceptions, properties);
    Objects.requireNonNull(layout, "layout");
    this.manager = Objects.<HttpManager>requireNonNull(manager, "manager");
  }
  
  public void start() {
    super.start();
    this.manager.startup();
  }
  
  public void append(LogEvent event) {
    try {
      this.manager.send(getLayout(), event);
    } catch (Exception e) {
      error("Unable to send HTTP in appender [" + getName() + "]", event, e);
    } 
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    boolean stopped = stop(timeout, timeUnit, false);
    stopped &= this.manager.stop(timeout, timeUnit);
    setStopped();
    return stopped;
  }
  
  public String toString() {
    return "HttpAppender{name=" + 
      getName() + ", state=" + 
      getState() + '}';
  }
  
  public static class HttpAppender {}
}
