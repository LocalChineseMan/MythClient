package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "lower", category = "Lookup")
public class LowerLookup implements StrLookup {
  public String lookup(String key) {
    return (key != null) ? key.toLowerCase() : null;
  }
  
  public String lookup(LogEvent event, String key) {
    return lookup(key);
  }
}
