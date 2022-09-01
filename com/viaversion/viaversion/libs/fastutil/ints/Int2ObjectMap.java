package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface Int2ObjectMap<V> extends Int2ObjectFunction<V>, Map<Integer, V> {
  public static interface FastEntrySet<V> extends ObjectSet<Entry<V>> {
    ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator();
    
    default void fastForEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      forEach(consumer);
    }
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default ObjectSet<Map.Entry<Integer, V>> entrySet() {
    return (ObjectSet)int2ObjectEntrySet();
  }
  
  @Deprecated
  default V put(Integer key, V value) {
    return super.put(key, value);
  }
  
  @Deprecated
  default V get(Object key) {
    return super.get(key);
  }
  
  @Deprecated
  default V remove(Object key) {
    return super.remove(key);
  }
  
  @Deprecated
  default boolean containsKey(Object key) {
    return super.containsKey(key);
  }
  
  default V getOrDefault(int key, V defaultValue) {
    V v;
    return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
  }
  
  default V putIfAbsent(int key, V value) {
    V v = get(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    put(key, value);
    return drv;
  }
  
  default boolean remove(int key, Object value) {
    V curValue = get(key);
    if (!Objects.equals(curValue, value) || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    remove(key);
    return true;
  }
  
  default boolean replace(int key, V oldValue, V newValue) {
    V curValue = get(key);
    if (!Objects.equals(curValue, oldValue) || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    put(key, newValue);
    return true;
  }
  
  default V replace(int key, V value) {
    return containsKey(key) ? put(key, value) : defaultReturnValue();
  }
  
  default V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v = get(key);
    if (v != defaultReturnValue() || containsKey(key))
      return v; 
    V newValue = mappingFunction.apply(key);
    put(key, newValue);
    return newValue;
  }
  
  default V computeIfAbsentPartial(int key, Int2ObjectFunction<? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v = get(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    if (!mappingFunction.containsKey(key))
      return drv; 
    V newValue = mappingFunction.get(key);
    put(key, newValue);
    return newValue;
  }
  
  default V computeIfPresent(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    V oldValue = get(key), drv = defaultReturnValue();
    if (oldValue == drv && !containsKey(key))
      return drv; 
    V newValue = remappingFunction.apply(Integer.valueOf(key), oldValue);
    if (newValue == null) {
      remove(key);
      return drv;
    } 
    put(key, newValue);
    return newValue;
  }
  
  default V compute(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    V oldValue = get(key), drv = defaultReturnValue();
    boolean contained = (oldValue != drv || containsKey(key));
    V newValue = remappingFunction.apply(Integer.valueOf(key), contained ? oldValue : null);
    if (newValue == null) {
      if (contained)
        remove(key); 
      return drv;
    } 
    put(key, newValue);
    return newValue;
  }
  
  default V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    V newValue;
    Objects.requireNonNull(remappingFunction);
    Objects.requireNonNull(value);
    V oldValue = get(key), drv = defaultReturnValue();
    if (oldValue != drv || containsKey(key)) {
      V mergedValue = remappingFunction.apply(oldValue, value);
      if (mergedValue == null) {
        remove(key);
        return drv;
      } 
      newValue = mergedValue;
    } else {
      newValue = value;
    } 
    put(key, newValue);
    return newValue;
  }
  
  @Deprecated
  default V getOrDefault(Object key, V defaultValue) {
    return super.getOrDefault(key, defaultValue);
  }
  
  @Deprecated
  default V putIfAbsent(Integer key, V value) {
    return super.putIfAbsent(key, value);
  }
  
  @Deprecated
  default boolean remove(Object key, Object value) {
    return super.remove(key, value);
  }
  
  @Deprecated
  default boolean replace(Integer key, V oldValue, V newValue) {
    return super.replace(key, oldValue, newValue);
  }
  
  @Deprecated
  default V replace(Integer key, V value) {
    return super.replace(key, value);
  }
  
  @Deprecated
  default V computeIfAbsent(Integer key, Function<? super Integer, ? extends V> mappingFunction) {
    return super.computeIfAbsent(key, mappingFunction);
  }
  
  @Deprecated
  default V computeIfPresent(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    return super.computeIfPresent(key, remappingFunction);
  }
  
  @Deprecated
  default V compute(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    return super.compute(key, remappingFunction);
  }
  
  @Deprecated
  default V merge(Integer key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return super.merge(key, value, remappingFunction);
  }
  
  int size();
  
  void defaultReturnValue(V paramV);
  
  V defaultReturnValue();
  
  ObjectSet<Entry<V>> int2ObjectEntrySet();
  
  IntSet keySet();
  
  ObjectCollection<V> values();
  
  boolean containsKey(int paramInt);
  
  public static interface Entry<V> extends Map.Entry<Integer, V> {
    @Deprecated
    default Integer getKey() {
      return Integer.valueOf(getIntKey());
    }
    
    int getIntKey();
  }
}
