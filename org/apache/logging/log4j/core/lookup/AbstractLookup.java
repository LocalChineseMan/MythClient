package org.apache.logging.log4j.core.lookup;

public abstract class AbstractLookup implements StrLookup {
  public String lookup(String key) {
    return lookup(null, key);
  }
}
