package org.apache.logging.log4j.core.appender.db;

import java.util.Locale;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name = "ColumnMapping", category = "Core", printObject = true)
public class ColumnMapping {
  public static final ColumnMapping[] EMPTY_ARRAY = new ColumnMapping[0];
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final StringLayout layout;
  
  private final String literalValue;
  
  private final String name;
  
  private final String nameKey;
  
  private final String parameter;
  
  private final String source;
  
  private final Class<?> type;
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static String toKey(String name) {
    return name.toUpperCase(Locale.ROOT);
  }
  
  private ColumnMapping(String name, String source, StringLayout layout, String literalValue, String parameter, Class<?> type) {
    this.name = name;
    this.nameKey = toKey(name);
    this.source = source;
    this.layout = layout;
    this.literalValue = literalValue;
    this.parameter = parameter;
    this.type = type;
  }
  
  public StringLayout getLayout() {
    return this.layout;
  }
  
  public String getLiteralValue() {
    return this.literalValue;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getNameKey() {
    return this.nameKey;
  }
  
  public String getParameter() {
    return this.parameter;
  }
  
  public String getSource() {
    return this.source;
  }
  
  public Class<?> getType() {
    return this.type;
  }
  
  public String toString() {
    return "ColumnMapping [name=" + this.name + ", source=" + this.source + ", literalValue=" + this.literalValue + ", parameter=" + this.parameter + ", type=" + this.type + ", layout=" + this.layout + "]";
  }
  
  public static class ColumnMapping {}
}
