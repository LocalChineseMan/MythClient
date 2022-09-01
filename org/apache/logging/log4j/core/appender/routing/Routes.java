package org.apache.logging.log4j.core.appender.routing;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import javax.script.Bindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name = "Routes", category = "Core", printObject = true)
public final class Routes {
  private static final String LOG_EVENT_KEY = "logEvent";
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final Configuration configuration;
  
  private final String pattern;
  
  private final AbstractScript patternScript;
  
  private final Route[] routes;
  
  @Deprecated
  public static Routes createRoutes(String pattern, Route... routes) {
    if (routes == null || routes.length == 0) {
      LOGGER.error("No routes configured");
      return null;
    } 
    return new Routes(null, null, pattern, routes);
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  private Routes(Configuration configuration, AbstractScript patternScript, String pattern, Route... routes) {
    this.configuration = configuration;
    this.patternScript = patternScript;
    this.pattern = pattern;
    this.routes = routes;
  }
  
  public String getPattern(LogEvent event, ConcurrentMap<Object, Object> scriptStaticVariables) {
    if (this.patternScript != null) {
      ScriptManager scriptManager = this.configuration.getScriptManager();
      Bindings bindings = scriptManager.createBindings(this.patternScript);
      bindings.put("staticVariables", scriptStaticVariables);
      bindings.put("logEvent", event);
      Object object = scriptManager.execute(this.patternScript.getName(), bindings);
      bindings.remove("logEvent");
      return Objects.toString(object, null);
    } 
    return this.pattern;
  }
  
  public AbstractScript getPatternScript() {
    return this.patternScript;
  }
  
  public Route getRoute(String key) {
    for (Route route : this.routes) {
      if (Objects.equals(route.getKey(), key))
        return route; 
    } 
    return null;
  }
  
  public Route[] getRoutes() {
    return this.routes;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Route route : this.routes) {
      if (!first)
        sb.append(','); 
      first = false;
      sb.append(route.toString());
    } 
    sb.append('}');
    return sb.toString();
  }
  
  public static class Routes {}
}
