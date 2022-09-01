package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.ColumnMapping;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Plugin(name = "Column", category = "Core", printObject = true)
public final class ColumnConfig {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final String columnName;
  
  private final String columnNameKey;
  
  private final PatternLayout layout;
  
  private final String literalValue;
  
  private final boolean eventTimestamp;
  
  private final boolean unicode;
  
  private final boolean clob;
  
  @Deprecated
  public static ColumnConfig createColumnConfig(Configuration config, String name, String pattern, String literalValue, String eventTimestamp, String unicode, String clob) {
    if (Strings.isEmpty(name)) {
      LOGGER.error("The column config is not valid because it does not contain a column name.");
      return null;
    } 
    boolean isEventTimestamp = Boolean.parseBoolean(eventTimestamp);
    boolean isUnicode = Booleans.parseBoolean(unicode, true);
    boolean isClob = Boolean.parseBoolean(clob);
    return newBuilder()
      .setConfiguration(config)
      .setName(name)
      .setPattern(pattern)
      .setLiteral(literalValue)
      .setEventTimestamp(isEventTimestamp)
      .setUnicode(isUnicode)
      .setClob(isClob)
      .build();
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  private ColumnConfig(String columnName, PatternLayout layout, String literalValue, boolean eventDate, boolean unicode, boolean clob) {
    this.columnName = columnName;
    this.columnNameKey = ColumnMapping.toKey(columnName);
    this.layout = layout;
    this.literalValue = literalValue;
    this.eventTimestamp = eventDate;
    this.unicode = unicode;
    this.clob = clob;
  }
  
  public String getColumnName() {
    return this.columnName;
  }
  
  public String getColumnNameKey() {
    return this.columnNameKey;
  }
  
  public PatternLayout getLayout() {
    return this.layout;
  }
  
  public String getLiteralValue() {
    return this.literalValue;
  }
  
  public boolean isClob() {
    return this.clob;
  }
  
  public boolean isEventTimestamp() {
    return this.eventTimestamp;
  }
  
  public boolean isUnicode() {
    return this.unicode;
  }
  
  public String toString() {
    return "{ name=" + this.columnName + ", layout=" + this.layout + ", literal=" + this.literalValue + ", timestamp=" + this.eventTimestamp + " }";
  }
  
  public static class ColumnConfig {}
}
