package org.apache.logging.log4j.core.util;

import java.util.List;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

public abstract class AbstractWatcher implements Watcher {
  private final Reconfigurable reconfigurable;
  
  private final List<ConfigurationListener> configurationListeners;
  
  private final Log4jThreadFactory threadFactory;
  
  private final Configuration configuration;
  
  private Source source;
  
  public AbstractWatcher(Configuration configuration, Reconfigurable reconfigurable, List<ConfigurationListener> configurationListeners) {
    this.configuration = configuration;
    this.reconfigurable = reconfigurable;
    this.configurationListeners = configurationListeners;
    this
      .threadFactory = (configurationListeners != null) ? Log4jThreadFactory.createDaemonThreadFactory("ConfiguratonFileWatcher") : null;
  }
  
  public List<ConfigurationListener> getListeners() {
    return this.configurationListeners;
  }
  
  public void modified() {
    for (ConfigurationListener configurationListener : this.configurationListeners) {
      Thread thread = this.threadFactory.newThread((Runnable)new ReconfigurationRunnable(configurationListener, this.reconfigurable));
      thread.start();
    } 
  }
  
  public Configuration getConfiguration() {
    return this.configuration;
  }
  
  public abstract long getLastModified();
  
  public abstract boolean isModified();
  
  public void watching(Source source) {
    this.source = source;
  }
  
  public Source getSource() {
    return this.source;
  }
  
  public static class AbstractWatcher {}
}
