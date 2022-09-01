package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2IntMap<K> extends Object2IntFunction<K>, Map<K, Integer> {
  public static interface FastEntrySet<K> extends ObjectSet<Entry<K>> {
    ObjectIterator<Object2IntMap.Entry<K>> fastIterator();
    
    default void fastForEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
      forEach(consumer);
    }
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default ObjectSet<Map.Entry<K, Integer>> entrySet() {
    return (ObjectSet)object2IntEntrySet();
  }
  
  @Deprecated
  default Integer put(K key, Integer value) {
    return super.put(key, value);
  }
  
  @Deprecated
  default Integer get(Object key) {
    return super.get(key);
  }
  
  @Deprecated
  default Integer remove(Object key) {
    return super.remove(key);
  }
  
  @Deprecated
  default boolean containsValue(Object value) {
    return (value == null) ? false : containsValue(((Integer)value).intValue());
  }
  
  default int getOrDefault(Object key, int defaultValue) {
    int v;
    return ((v = getInt(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
  }
  
  default int putIfAbsent(K key, int value) {
    int v = getInt(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    put(key, value);
    return drv;
  }
  
  default boolean remove(Object key, int value) {
    int curValue = getInt(key);
    if (curValue != value || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    removeInt(key);
    return true;
  }
  
  default boolean replace(K key, int oldValue, int newValue) {
    int curValue = getInt(key);
    if (curValue != oldValue || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    put(key, newValue);
    return true;
  }
  
  default int replace(K key, int value) {
    return containsKey(key) ? put(key, value) : defaultReturnValue();
  }
  
  default int computeIntIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int v = getInt(key);
    if (v != defaultReturnValue() || containsKey(key))
      return v; 
    int newValue = mappingFunction.applyAsInt(key);
    put(key, newValue);
    return newValue;
  }
  
  default int computeIntIfAbsentPartial(K key, Object2IntFunction<? super K> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int v = getInt(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    if (!mappingFunction.containsKey(key))
      return drv; 
    int newValue = mappingFunction.getInt(key);
    put(key, newValue);
    return newValue;
  }
  
  default int computeIntIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int oldValue = getInt(key), drv = defaultReturnValue();
    if (oldValue == drv && !containsKey(key))
      return drv; 
    Integer newValue = remappingFunction.apply(key, Integer.valueOf(oldValue));
    if (newValue == null) {
      removeInt(key);
      return drv;
    } 
    int newVal = newValue.intValue();
    put(key, newVal);
    return newVal;
  }
  
  default int computeInt(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int oldValue = getInt(key), drv = defaultReturnValue();
    boolean contained = (oldValue != drv || containsKey(key));
    Integer newValue = remappingFunction.apply(key, contained ? Integer.valueOf(oldValue) : null);
    if (newValue == null) {
      if (contained)
        removeInt(key); 
      return drv;
    } 
    int newVal = newValue.intValue();
    put(key, newVal);
    return newVal;
  }
  
  default int mergeInt(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    int newValue;
    Objects.requireNonNull(remappingFunction);
    int oldValue = getInt(key), drv = defaultReturnValue();
    if (oldValue != drv || containsKey(key)) {
      Integer mergedValue = remappingFunction.apply(Integer.valueOf(oldValue), Integer.valueOf(value));
      if (mergedValue == null) {
        removeInt(key);
        return drv;
      } 
      newValue = mergedValue.intValue();
    } else {
      newValue = value;
    } 
    put(key, newValue);
    return newValue;
  }
  
  @Deprecated
  default Integer getOrDefault(Object key, Integer defaultValue) {
    return super.getOrDefault(key, defaultValue);
  }
  
  @Deprecated
  default Integer putIfAbsent(K key, Integer value) {
    return super.putIfAbsent(key, value);
  }
  
  @Deprecated
  default boolean remove(Object key, Object value) {
    return super.remove(key, value);
  }
  
  @Deprecated
  default boolean replace(K key, Integer oldValue, Integer newValue) {
    return super.replace(key, oldValue, newValue);
  }
  
  @Deprecated
  default Integer replace(K key, Integer value) {
    return super.replace(key, value);
  }
  
  @Deprecated
  default Integer merge(K key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    return super.merge(key, value, remappingFunction);
  }
  
  int size();
  
  void defaultReturnValue(int paramInt);
  
  int defaultReturnValue();
  
  ObjectSet<Entry<K>> object2IntEntrySet();
  
  ObjectSet<K> keySet();
  
  IntCollection values();
  
  boolean containsKey(Object paramObject);
  
  boolean containsValue(int paramInt);
  
  public static interface Entry<K> extends Map.Entry<K, Integer> {
    @Deprecated
    default Integer getValue() {
      return Integer.valueOf(getIntValue());
    }
    
    @Deprecated
    default Integer setValue(Integer value) {
      return Integer.valueOf(setValue(value.intValue()));
    }
    
    int getIntValue();
    
    int setValue(int param1Int);
  }
}
