package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "upper", category = "Lookup")
public class UpperLookup implements StrLookup {
  public String lookup(String key) {
    return (key != null) ? key.toUpperCase() : null;
  }
  
  public String lookup(LogEvent event, String key) {
    return lookup(key);
  }
}
