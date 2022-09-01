package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterable;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Int2ObjectMaps {
  public static <V> ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap<V> map) {
    ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    return (entries instanceof Int2ObjectMap.FastEntrySet) ? (
      (Int2ObjectMap.FastEntrySet)entries).fastIterator() : 
      entries.iterator();
  }
  
  public static <V> void fastForEach(Int2ObjectMap<V> map, Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
    ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    if (entries instanceof Int2ObjectMap.FastEntrySet) {
      ((Int2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
    } else {
      entries.forEach(consumer);
    } 
  }
  
  public static <V> ObjectIterable<Int2ObjectMap.Entry<V>> fastIterable(Int2ObjectMap<V> map) {
    ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    return (entries instanceof Int2ObjectMap.FastEntrySet) ? (ObjectIterable<Int2ObjectMap.Entry<V>>)new Object(entries) : 
      
      (ObjectIterable<Int2ObjectMap.Entry<V>>)entries;
  }
  
  public static class EmptyMap<V> extends Int2ObjectFunctions$EmptyFunction<V> implements Int2ObjectMap<V>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(Object v) {
      return false;
    }
    
    public void putAll(Map<? extends Integer, ? extends V> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
      return (ObjectSet<Int2ObjectMap.Entry<V>>)ObjectSets.EMPTY_SET;
    }
    
    public IntSet keySet() {
      return IntSets.EMPTY_SET;
    }
    
    public ObjectCollection<V> values() {
      return (ObjectCollection<V>)ObjectSets.EMPTY_SET;
    }
    
    public Object clone() {
      return Int2ObjectMaps.EMPTY_MAP;
    }
    
    public boolean isEmpty() {
      return true;
    }
    
    public int hashCode() {
      return 0;
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map))
        return false; 
      return ((Map)o).isEmpty();
    }
    
    public String toString() {
      return "{}";
    }
  }
  
  public static final EmptyMap EMPTY_MAP = new EmptyMap();
  
  public static <V> Int2ObjectMap<V> emptyMap() {
    return EMPTY_MAP;
  }
  
  public static <V> Int2ObjectMap<V> singleton(int key, V value) {
    return (Int2ObjectMap<V>)new Singleton(key, value);
  }
  
  public static <V> Int2ObjectMap<V> singleton(Integer key, V value) {
    return (Int2ObjectMap<V>)new Singleton(key.intValue(), value);
  }
  
  public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m) {
    return (Int2ObjectMap<V>)new SynchronizedMap(m);
  }
  
  public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m, Object sync) {
    return (Int2ObjectMap<V>)new SynchronizedMap(m, sync);
  }
  
  public static <V> Int2ObjectMap<V> unmodifiable(Int2ObjectMap<V> m) {
    return (Int2ObjectMap<V>)new UnmodifiableMap(m);
  }
  
  public static class Int2ObjectMaps {}
  
  public static class Int2ObjectMaps {}
  
  public static class Int2ObjectMaps {}
}
