package org.apache.logging.log4j.core.appender.mom;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "JMS", category = "Core", elementType = "appender", printObject = true)
@PluginAliases({"JMSQueue", "JMSTopic"})
public class JmsAppender extends AbstractAppender {
  private volatile JmsManager manager;
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder(null);
  }
  
  protected JmsAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties, JmsManager manager) throws JMSException {
    super(name, filter, layout, ignoreExceptions, properties);
    this.manager = manager;
  }
  
  @Deprecated
  protected JmsAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, JmsManager manager) throws JMSException {
    super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    this.manager = manager;
  }
  
  public void append(LogEvent event) {
    this.manager.send(event, toSerializable(event));
  }
  
  public JmsManager getManager() {
    return this.manager;
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    boolean stopped = stop(timeout, timeUnit, false);
    stopped &= this.manager.stop(timeout, timeUnit);
    setStopped();
    return stopped;
  }
  
  public static class JmsAppender {}
}
