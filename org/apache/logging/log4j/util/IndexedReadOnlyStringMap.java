package org.apache.logging.log4j.util;

public interface IndexedReadOnlyStringMap extends ReadOnlyStringMap {
  String getKeyAt(int paramInt);
  
  <V> V getValueAt(int paramInt);
  
  int indexOfKey(String paramString);
}
