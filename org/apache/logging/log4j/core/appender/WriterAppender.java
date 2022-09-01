package org.apache.logging.log4j.core.appender;

import java.io.Writer;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldWriter;

@Plugin(name = "Writer", category = "Core", elementType = "appender", printObject = true)
public final class WriterAppender extends AbstractWriterAppender<WriterManager> {
  private static WriterManagerFactory factory = new WriterManagerFactory(null);
  
  @PluginFactory
  public static WriterAppender createAppender(StringLayout layout, Filter filter, Writer target, String name, boolean follow, boolean ignore) {
    PatternLayout patternLayout;
    if (name == null) {
      LOGGER.error("No name provided for WriterAppender");
      return null;
    } 
    if (layout == null)
      patternLayout = PatternLayout.createDefaultLayout(); 
    return new WriterAppender(name, (StringLayout)patternLayout, filter, getManager(target, follow, (StringLayout)patternLayout), ignore, null);
  }
  
  private static WriterManager getManager(Writer target, boolean follow, StringLayout layout) {
    CloseShieldWriter closeShieldWriter = new CloseShieldWriter(target);
    String managerName = target.getClass().getName() + "@" + Integer.toHexString(target.hashCode()) + '.' + follow;
    return WriterManager.getManager(managerName, new FactoryData((Writer)closeShieldWriter, managerName, layout), (ManagerFactory)factory);
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private WriterAppender(String name, StringLayout layout, Filter filter, WriterManager manager, boolean ignoreExceptions, Property[] properties) {
    super(name, layout, filter, ignoreExceptions, true, properties, (M)manager);
  }
  
  private static class WriterAppender {}
  
  private static class WriterAppender {}
  
  public static class WriterAppender {}
}
