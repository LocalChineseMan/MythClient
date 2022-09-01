package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "DriverManager", category = "Core", elementType = "connectionSource", printObject = true)
public class DriverManagerConnectionSource extends AbstractDriverManagerConnectionSource {
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  public DriverManagerConnectionSource(String driverClassName, String connectionString, String actualConnectionString, char[] userName, char[] password, Property[] properties) {
    super(driverClassName, connectionString, actualConnectionString, userName, password, properties);
  }
  
  public static class DriverManagerConnectionSource {}
}
