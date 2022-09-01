package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "DefaultArbiter", category = "Core", elementType = "Arbiter", deferChildren = true, printObject = true)
public class DefaultArbiter implements Arbiter {
  public boolean isCondition() {
    return true;
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class DefaultArbiter {}
}
