package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "ScriptAppenderSelector", category = "Core", elementType = "appender", printObject = true)
public class ScriptAppenderSelector extends AbstractAppender {
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  private ScriptAppenderSelector(String name, Filter filter, Layout<? extends Serializable> layout, Property[] properties) {
    super(name, filter, layout, true, Property.EMPTY_ARRAY);
  }
  
  public void append(LogEvent event) {}
  
  public static final class ScriptAppenderSelector {}
}
