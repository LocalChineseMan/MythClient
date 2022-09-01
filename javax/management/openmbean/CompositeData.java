package javax.management.openmbean;

import java.util.Collection;

public interface CompositeData {
  CompositeType getCompositeType();
  
  Object get(String paramString);
  
  Object[] getAll(String[] paramArrayOfString);
  
  boolean containsKey(String paramString);
  
  boolean containsValue(Object paramObject);
  
  Collection<?> values();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
