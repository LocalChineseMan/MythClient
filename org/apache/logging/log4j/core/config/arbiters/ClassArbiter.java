package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.util.LoaderUtil;

@Plugin(name = "ClassArbiter", category = "Core", elementType = "Arbiter", printObject = true, deferChildren = true)
public class ClassArbiter implements Arbiter {
  private final String className;
  
  private ClassArbiter(String className) {
    this.className = className;
  }
  
  public boolean isCondition() {
    return LoaderUtil.isClassAvailable(this.className);
  }
  
  @PluginBuilderFactory
  public static SystemPropertyArbiter.Builder newBuilder() {
    return new SystemPropertyArbiter.Builder();
  }
  
  public static class ClassArbiter {}
}
