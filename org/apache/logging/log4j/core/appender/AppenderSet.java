package org.apache.logging.log4j.core.appender;

import java.util.Map;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name = "AppenderSet", category = "Core", printObject = true, deferChildren = true)
public class AppenderSet {
  private static final StatusLogger LOGGER = StatusLogger.getLogger();
  
  private final Configuration configuration;
  
  private final Map<String, Node> nodeMap;
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  private AppenderSet(Configuration configuration, Map<String, Node> appenders) {
    this.configuration = configuration;
    this.nodeMap = appenders;
  }
  
  public Appender createAppender(String actualAppenderName, String sourceAppenderName) {
    Node node = this.nodeMap.get(actualAppenderName);
    if (node == null) {
      LOGGER.error("No node named {} in {}", actualAppenderName, this);
      return null;
    } 
    node.getAttributes().put("name", sourceAppenderName);
    if (node.getType().getElementName().equals("appender")) {
      Node appNode = new Node(node);
      this.configuration.createConfiguration(appNode, null);
      if (appNode.getObject() instanceof Appender) {
        Appender app = (Appender)appNode.getObject();
        app.start();
        return app;
      } 
      LOGGER.error("Unable to create Appender of type " + node.getName());
      return null;
    } 
    LOGGER.error("No Appender was configured for name {} " + actualAppenderName);
    return null;
  }
  
  public static class AppenderSet {}
}
