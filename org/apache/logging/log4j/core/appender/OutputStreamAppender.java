package org.apache.logging.log4j.core.appender;

import java.io.OutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.core.util.NullOutputStream;

@Plugin(name = "OutputStream", category = "Core", elementType = "appender", printObject = true)
public final class OutputStreamAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
  private static OutputStreamManagerFactory factory = new OutputStreamManagerFactory(null);
  
  @PluginFactory
  public static OutputStreamAppender createAppender(Layout<? extends Serializable> layout, Filter filter, OutputStream target, String name, boolean follow, boolean ignore) {
    PatternLayout patternLayout;
    if (name == null) {
      LOGGER.error("No name provided for OutputStreamAppender");
      return null;
    } 
    if (layout == null)
      patternLayout = PatternLayout.createDefaultLayout(); 
    return new OutputStreamAppender(name, (Layout<? extends Serializable>)patternLayout, filter, getManager(target, follow, (Layout<? extends Serializable>)patternLayout), ignore, null);
  }
  
  private static OutputStreamManager getManager(OutputStream target, boolean follow, Layout<? extends Serializable> layout) {
    OutputStream os = (target == null) ? (OutputStream)NullOutputStream.getInstance() : (OutputStream)new CloseShieldOutputStream(target);
    OutputStream targetRef = (target == null) ? os : target;
    String managerName = targetRef.getClass().getName() + "@" + Integer.toHexString(targetRef.hashCode()) + '.' + follow;
    return OutputStreamManager.getManager(managerName, new FactoryData(os, managerName, layout), (ManagerFactory<? extends OutputStreamManager, FactoryData>)factory);
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private OutputStreamAppender(String name, Layout<? extends Serializable> layout, Filter filter, OutputStreamManager manager, boolean ignoreExceptions, Property[] properties) {
    super(name, layout, filter, ignoreExceptions, true, properties, (M)manager);
  }
  
  private static class OutputStreamAppender {}
  
  private static class OutputStreamAppender {}
  
  public static class OutputStreamAppender {}
}
