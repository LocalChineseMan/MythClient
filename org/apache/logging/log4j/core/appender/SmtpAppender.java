package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.apache.logging.log4j.core.net.SmtpManager;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(name = "SMTP", category = "Core", elementType = "appender", printObject = true)
public final class SmtpAppender extends AbstractAppender {
  private static final int DEFAULT_BUFFER_SIZE = 512;
  
  private final SmtpManager manager;
  
  private SmtpAppender(String name, Filter filter, Layout<? extends Serializable> layout, SmtpManager manager, boolean ignoreExceptions, Property[] properties) {
    super(name, filter, layout, ignoreExceptions, properties);
    this.manager = manager;
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  @Deprecated
  public static SmtpAppender createAppender(@PluginConfiguration Configuration config, @PluginAttribute("name") @Required String name, @PluginAttribute("to") String to, @PluginAttribute("cc") String cc, @PluginAttribute("bcc") String bcc, @PluginAttribute("from") String from, @PluginAttribute("replyTo") String replyTo, @PluginAttribute("subject") String subject, @PluginAttribute("smtpProtocol") String smtpProtocol, @PluginAttribute("smtpHost") String smtpHost, @PluginAttribute(value = "smtpPort", defaultString = "0") @ValidPort String smtpPortStr, @PluginAttribute("smtpUsername") String smtpUsername, @PluginAttribute(value = "smtpPassword", sensitive = true) String smtpPassword, @PluginAttribute("smtpDebug") String smtpDebug, @PluginAttribute("bufferSize") String bufferSizeStr, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filter") Filter filter, @PluginAttribute("ignoreExceptions") String ignore) {
    HtmlLayout htmlLayout;
    ThresholdFilter thresholdFilter;
    if (name == null) {
      LOGGER.error("No name provided for SmtpAppender");
      return null;
    } 
    boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    int smtpPort = AbstractAppender.parseInt(smtpPortStr, 0);
    boolean isSmtpDebug = Boolean.parseBoolean(smtpDebug);
    int bufferSize = (bufferSizeStr == null) ? 512 : Integer.parseInt(bufferSizeStr);
    if (layout == null)
      htmlLayout = HtmlLayout.createDefaultLayout(); 
    if (filter == null)
      thresholdFilter = ThresholdFilter.createFilter(null, null, null); 
    Configuration configuration = (config != null) ? config : (Configuration)new DefaultConfiguration();
    SmtpManager manager = SmtpManager.getSmtpManager(configuration, to, cc, bcc, from, replyTo, subject, smtpProtocol, smtpHost, smtpPort, smtpUsername, smtpPassword, isSmtpDebug, thresholdFilter
        .toString(), bufferSize, null);
    if (manager == null)
      return null; 
    return new SmtpAppender(name, (Filter)thresholdFilter, (Layout<? extends Serializable>)htmlLayout, manager, ignoreExceptions, null);
  }
  
  public boolean isFiltered(LogEvent event) {
    boolean filtered = super.isFiltered(event);
    if (filtered)
      this.manager.add(event); 
    return filtered;
  }
  
  public void append(LogEvent event) {
    this.manager.sendEvents(getLayout(), event);
  }
  
  public static class SmtpAppender {}
}
