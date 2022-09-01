package javax.management.openmbean;

import java.util.Collection;
import java.util.Set;

public interface TabularData {
  TabularType getTabularType();
  
  Object[] calculateIndex(CompositeData paramCompositeData);
  
  int size();
  
  boolean isEmpty();
  
  boolean containsKey(Object[] paramArrayOfObject);
  
  boolean containsValue(CompositeData paramCompositeData);
  
  CompositeData get(Object[] paramArrayOfObject);
  
  void put(CompositeData paramCompositeData);
  
  CompositeData remove(Object[] paramArrayOfObject);
  
  void putAll(CompositeData[] paramArrayOfCompositeData);
  
  void clear();
  
  Set<?> keySet();
  
  Collection<?> values();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
