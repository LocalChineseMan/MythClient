package org.apache.logging.log4j.core.config.arbiters;

import javax.script.SimpleBindings;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.script.AbstractScript;

@Plugin(name = "ScriptArbiter", category = "Core", elementType = "Arbiter", deferChildren = true, printObject = true)
public class ScriptArbiter implements Arbiter {
  private final AbstractScript script;
  
  private final Configuration configuration;
  
  private ScriptArbiter(Configuration configuration, AbstractScript script) {
    this.configuration = configuration;
    this.script = script;
    if (!(script instanceof org.apache.logging.log4j.core.script.ScriptRef))
      configuration.getScriptManager().addScript(script); 
  }
  
  public boolean isCondition() {
    SimpleBindings bindings = new SimpleBindings();
    bindings.putAll(this.configuration.getProperties());
    bindings.put("substitutor", this.configuration.getStrSubstitutor());
    Object object = this.configuration.getScriptManager().execute(this.script.getName(), bindings);
    return Boolean.parseBoolean(object.toString());
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class ScriptArbiter {}
}
