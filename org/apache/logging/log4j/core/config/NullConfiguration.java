package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;

public class NullConfiguration extends AbstractConfiguration {
  public static final String NULL_NAME = "Null";
  
  public NullConfiguration() {
    super(null, ConfigurationSource.NULL_SOURCE);
    setName("Null");
    LoggerConfig root = getRootLogger();
    root.setLevel(Level.OFF);
  }
}
