package javax.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleBindings implements Bindings {
  private Map<String, Object> map;
  
  public SimpleBindings(Map<String, Object> paramMap) {
    if (paramMap == null)
      throw new NullPointerException(); 
    this.map = paramMap;
  }
  
  public SimpleBindings() {
    this(new HashMap<>());
  }
  
  public Object put(String paramString, Object paramObject) {
    checkKey(paramString);
    return this.map.put(paramString, paramObject);
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap) {
    if (paramMap == null)
      throw new NullPointerException("toMerge map is null"); 
    for (Map.Entry<? extends String, ? extends Object> entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      checkKey(str);
      put(str, entry.getValue());
    } 
  }
  
  public void clear() {
    this.map.clear();
  }
  
  public boolean containsKey(Object paramObject) {
    checkKey(paramObject);
    return this.map.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject) {
    return this.map.containsValue(paramObject);
  }
  
  public Set<Map.Entry<String, Object>> entrySet() {
    return this.map.entrySet();
  }
  
  public Object get(Object paramObject) {
    checkKey(paramObject);
    return this.map.get(paramObject);
  }
  
  public boolean isEmpty() {
    return this.map.isEmpty();
  }
  
  public Set<String> keySet() {
    return this.map.keySet();
  }
  
  public Object remove(Object paramObject) {
    checkKey(paramObject);
    return this.map.remove(paramObject);
  }
  
  public int size() {
    return this.map.size();
  }
  
  public Collection<Object> values() {
    return this.map.values();
  }
  
  private void checkKey(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException("key can not be null"); 
    if (!(paramObject instanceof String))
      throw new ClassCastException("key should be a String"); 
    if (paramObject.equals(""))
      throw new IllegalArgumentException("key can not be empty"); 
  }
}
