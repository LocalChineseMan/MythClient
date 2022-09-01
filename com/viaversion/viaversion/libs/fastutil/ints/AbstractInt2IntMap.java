package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2IntMap extends AbstractInt2IntFunction implements Int2IntMap, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(int v) {
    return values().contains(v);
  }
  
  public boolean containsKey(int k) {
    ObjectIterator<Int2IntMap.Entry> i = int2IntEntrySet().iterator();
    while (i.hasNext()) {
      if (((Int2IntMap.Entry)i.next()).getIntKey() == k)
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
  
  public IntCollection values() {
    return (IntCollection)new Object(this);
  }
  
  public void putAll(Map<? extends Integer, ? extends Integer> m) {
    if (m instanceof Int2IntMap) {
      ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator((Int2IntMap)m);
      while (i.hasNext()) {
        Int2IntMap.Entry e = (Int2IntMap.Entry)i.next();
        put(e.getIntKey(), e.getIntValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends Integer, ? extends Integer>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends Integer, ? extends Integer> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator((Int2IntMap)this);
    while (n-- != 0)
      h += ((Int2IntMap.Entry)i.next()).hashCode(); 
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
    return int2IntEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator((Int2IntMap)this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Int2IntMap.Entry e = (Int2IntMap.Entry)i.next();
      s.append(String.valueOf(e.getIntKey()));
      s.append("=>");
      s.append(String.valueOf(e.getIntValue()));
    } 
    s.append("}");
    return s.toString();
  }
  
  public static abstract class AbstractInt2IntMap {}
  
  public static class AbstractInt2IntMap {}
}
