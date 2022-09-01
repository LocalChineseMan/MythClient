package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ObjectThreadContextMap extends CleanableThreadContextMap {
  <V> V getValue(String paramString);
  
  <V> void putValue(String paramString, V paramV);
  
  <V> void putAllValues(Map<String, V> paramMap);
}
