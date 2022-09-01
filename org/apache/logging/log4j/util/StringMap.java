package org.apache.logging.log4j.util;

public interface StringMap extends ReadOnlyStringMap {
  void clear();
  
  boolean equals(Object paramObject);
  
  void freeze();
  
  int hashCode();
  
  boolean isFrozen();
  
  void putAll(ReadOnlyStringMap paramReadOnlyStringMap);
  
  void putValue(String paramString, Object paramObject);
  
  void remove(String paramString);
}
