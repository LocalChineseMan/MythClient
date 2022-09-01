package org.apache.logging.log4j.core.util;

import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "KeyValuePair", category = "Core", printObject = true)
public final class KeyValuePair {
  public static final KeyValuePair[] EMPTY_ARRAY = new KeyValuePair[0];
  
  private final String key;
  
  private final String value;
  
  public KeyValuePair(String key, String value) {
    this.key = key;
    this.value = value;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String toString() {
    return this.key + '=' + this.value;
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.key, this.value });
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    KeyValuePair other = (KeyValuePair)obj;
    if (!Objects.equals(this.key, other.key))
      return false; 
    if (!Objects.equals(this.value, other.value))
      return false; 
    return true;
  }
  
  public static class KeyValuePair {}
}
