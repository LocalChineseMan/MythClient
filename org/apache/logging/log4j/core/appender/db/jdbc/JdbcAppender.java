package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.Serializable;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(name = "JDBC", category = "Core", elementType = "appender", printObject = true)
public final class JdbcAppender extends AbstractDatabaseAppender<JdbcDatabaseManager> {
  private final String description;
  
  @Deprecated
  public static <B extends Builder<B>> JdbcAppender createAppender(String name, String ignore, Filter filter, ConnectionSource connectionSource, String bufferSize, String tableName, ColumnConfig[] columnConfigs) {
    Assert.requireNonEmpty(name, "Name cannot be empty");
    Objects.requireNonNull(connectionSource, "ConnectionSource cannot be null");
    Assert.requireNonEmpty(tableName, "Table name cannot be empty");
    Assert.requireNonEmpty(columnConfigs, "ColumnConfigs cannot be empty");
    int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
    boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    return ((Builder)((Builder)((Builder)newBuilder()
      .setBufferSize(bufferSizeInt)
      .setColumnConfigs(columnConfigs)
      .setConnectionSource(connectionSource)
      .setTableName(tableName).setName(name)).setIgnoreExceptions(ignoreExceptions)).setFilter(filter))
      .build();
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  private JdbcAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties, JdbcDatabaseManager manager) {
    super(name, filter, layout, ignoreExceptions, properties, (AbstractDatabaseManager)manager);
    this.description = getName() + "{ manager=" + getManager() + " }";
  }
  
  public String toString() {
    return this.description;
  }
  
  public static class JdbcAppender {}
}
