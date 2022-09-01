package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2ObjectMap<V> extends AbstractInt2ObjectFunction<V> implements Int2ObjectMap<V>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(Object v) {
    return values().contains(v);
  }
  
  public boolean containsKey(int k) {
    ObjectIterator<Int2ObjectMap.Entry<V>> i = int2ObjectEntrySet().iterator();
    while (i.hasNext()) {
      if (((Int2ObjectMap.Entry)i.next()).getIntKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public IntSet keySet() {
    return (IntSet)new Object(this);
  }
  
  public ObjectCollection<V> values() {
    return (ObjectCollection<V>)new Object(this);
  }
  
  public void putAll(Map<? extends Integer, ? extends V> m) {
    if (m instanceof Int2ObjectMap) {
      ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator((Int2ObjectMap)m);
      while (i.hasNext()) {
        Int2ObjectMap.Entry<? extends V> e = (Int2ObjectMap.Entry<? extends V>)i.next();
        put(e.getIntKey(), e.getValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends Integer, ? extends V>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends Integer, ? extends V> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator((Int2ObjectMap<V>)this);
    while (n-- != 0)
      h += ((Int2ObjectMap.Entry)i.next()).hashCode(); 
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
    return int2ObjectEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator((Int2ObjectMap<V>)this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry<V>)i.next();
      s.append(String.valueOf(e.getIntKey()));
      s.append("=>");
      if (this == e.getValue()) {
        s.append("(this map)");
        continue;
      } 
      s.append(String.valueOf(e.getValue()));
    } 
    s.append("}");
    return s.toString();
  }
  
  public static abstract class AbstractInt2ObjectMap {}
  
  public static class AbstractInt2ObjectMap {}
}
