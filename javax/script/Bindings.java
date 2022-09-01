package javax.script;

import java.util.Map;

public interface Bindings extends Map<String, Object> {
  Object put(String paramString, Object paramObject);
  
  void putAll(Map<? extends String, ? extends Object> paramMap);
  
  boolean containsKey(Object paramObject);
  
  Object get(Object paramObject);
  
  Object remove(Object paramObject);
}
