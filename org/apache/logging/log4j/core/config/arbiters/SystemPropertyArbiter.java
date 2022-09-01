package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "SystemPropertyArbiter", category = "Core", elementType = "Arbiter", deferChildren = true, printObject = true)
public class SystemPropertyArbiter implements Arbiter {
  private final String propertyName;
  
  private final String propertyValue;
  
  private SystemPropertyArbiter(String propertyName, String propertyValue) {
    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }
  
  public boolean isCondition() {
    String value = System.getProperty(this.propertyName);
    return (value != null && (this.propertyValue == null || value.equals(this.propertyValue)));
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class SystemPropertyArbiter {}
}
