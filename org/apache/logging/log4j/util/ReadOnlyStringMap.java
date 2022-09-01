package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.Map;

public interface ReadOnlyStringMap extends Serializable {
  Map<String, String> toMap();
  
  boolean containsKey(String paramString);
  
  <V> void forEach(BiConsumer<String, ? super V> paramBiConsumer);
  
  <V, S> void forEach(TriConsumer<String, ? super V, S> paramTriConsumer, S paramS);
  
  <V> V getValue(String paramString);
  
  boolean isEmpty();
  
  int size();
}
