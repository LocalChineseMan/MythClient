package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractObject2IntMap<K> extends AbstractObject2IntFunction<K> implements Object2IntMap<K>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(int v) {
    return values().contains(v);
  }
  
  public boolean containsKey(Object k) {
    ObjectIterator<Object2IntMap.Entry<K>> i = object2IntEntrySet().iterator();
    while (i.hasNext()) {
      if (((Object2IntMap.Entry)i.next()).getKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public ObjectSet<K> keySet() {
    return (ObjectSet<K>)new Object(this);
  }
  
  public IntCollection values() {
    return (IntCollection)new Object(this);
  }
  
  public void putAll(Map<? extends K, ? extends Integer> m) {
    if (m instanceof Object2IntMap) {
      ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator((Object2IntMap)m);
      while (i.hasNext()) {
        Object2IntMap.Entry<? extends K> e = i.next();
        put(e.getKey(), e.getIntValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends K, ? extends Integer>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends K, ? extends Integer> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator((Object2IntMap)this);
    while (n-- != 0)
      h += ((Object2IntMap.Entry)i.next()).hashCode(); 
    return h;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Map))
      return false; 
    Map<?, ?> m = (Map<?, ?>)o;
    if (m.size() != size())
      return false; 
    return object2IntEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator((Object2IntMap)this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Object2IntMap.Entry<K> e = i.next();
      if (this == e.getKey()) {
        s.append("(this map)");
      } else {
        s.append(String.valueOf(e.getKey()));
      } 
      s.append("=>");
      s.append(String.valueOf(e.getIntValue()));
    } 
    s.append("}");
    return s.toString();
  }
  
  public static abstract class AbstractObject2IntMap {}
  
  public static class AbstractObject2IntMap {}
}
