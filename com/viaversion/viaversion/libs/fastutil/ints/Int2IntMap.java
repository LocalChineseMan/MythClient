package com.viaversion.viaversion.libs.fastutil.ints;

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
import java.util.function.IntUnaryOperator;

public interface Int2IntMap extends Int2IntFunction, Map<Integer, Integer> {
  public static interface FastEntrySet extends ObjectSet<Entry> {
    ObjectIterator<Int2IntMap.Entry> fastIterator();
    
    default void fastForEach(Consumer<? super Int2IntMap.Entry> consumer) {
      forEach(consumer);
    }
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
    return (ObjectSet)int2IntEntrySet();
  }
  
  @Deprecated
  default Integer put(Integer key, Integer value) {
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
  default boolean containsKey(Object key) {
    return super.containsKey(key);
  }
  
  @Deprecated
  default boolean containsValue(Object value) {
    return (value == null) ? false : containsValue(((Integer)value).intValue());
  }
  
  default int getOrDefault(int key, int defaultValue) {
    int v;
    return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
  }
  
  default int putIfAbsent(int key, int value) {
    int v = get(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    put(key, value);
    return drv;
  }
  
  default boolean remove(int key, int value) {
    int curValue = get(key);
    if (curValue != value || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    remove(key);
    return true;
  }
  
  default boolean replace(int key, int oldValue, int newValue) {
    int curValue = get(key);
    if (curValue != oldValue || (curValue == defaultReturnValue() && !containsKey(key)))
      return false; 
    put(key, newValue);
    return true;
  }
  
  default int replace(int key, int value) {
    return containsKey(key) ? put(key, value) : defaultReturnValue();
  }
  
  default int computeIfAbsent(int key, IntUnaryOperator mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int v = get(key);
    if (v != defaultReturnValue() || containsKey(key))
      return v; 
    int newValue = mappingFunction.applyAsInt(key);
    put(key, newValue);
    return newValue;
  }
  
  default int computeIfAbsentNullable(int key, IntFunction<? extends Integer> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int v = get(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    Integer mappedValue = mappingFunction.apply(key);
    if (mappedValue == null)
      return drv; 
    int newValue = mappedValue.intValue();
    put(key, newValue);
    return newValue;
  }
  
  default int computeIfAbsentPartial(int key, Int2IntFunction mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int v = get(key), drv = defaultReturnValue();
    if (v != drv || containsKey(key))
      return v; 
    if (!mappingFunction.containsKey(key))
      return drv; 
    int newValue = mappingFunction.get(key);
    put(key, newValue);
    return newValue;
  }
  
  default int computeIfPresent(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int oldValue = get(key), drv = defaultReturnValue();
    if (oldValue == drv && !containsKey(key))
      return drv; 
    Integer newValue = remappingFunction.apply(Integer.valueOf(key), Integer.valueOf(oldValue));
    if (newValue == null) {
      remove(key);
      return drv;
    } 
    int newVal = newValue.intValue();
    put(key, newVal);
    return newVal;
  }
  
  default int compute(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int oldValue = get(key), drv = defaultReturnValue();
    boolean contained = (oldValue != drv || containsKey(key));
    Integer newValue = remappingFunction.apply(Integer.valueOf(key), 
        contained ? Integer.valueOf(oldValue) : null);
    if (newValue == null) {
      if (contained)
        remove(key); 
      return drv;
    } 
    int newVal = newValue.intValue();
    put(key, newVal);
    return newVal;
  }
  
  default int merge(int key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    int newValue;
    Objects.requireNonNull(remappingFunction);
    int oldValue = get(key), drv = defaultReturnValue();
    if (oldValue != drv || containsKey(key)) {
      Integer mergedValue = remappingFunction.apply(Integer.valueOf(oldValue), Integer.valueOf(value));
      if (mergedValue == null) {
        remove(key);
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
  default Integer putIfAbsent(Integer key, Integer value) {
    return super.putIfAbsent(key, value);
  }
  
  @Deprecated
  default boolean remove(Object key, Object value) {
    return super.remove(key, value);
  }
  
  @Deprecated
  default boolean replace(Integer key, Integer oldValue, Integer newValue) {
    return super.replace(key, oldValue, newValue);
  }
  
  @Deprecated
  default Integer replace(Integer key, Integer value) {
    return super.replace(key, value);
  }
  
  @Deprecated
  default Integer computeIfAbsent(Integer key, Function<? super Integer, ? extends Integer> mappingFunction) {
    return super.computeIfAbsent(key, mappingFunction);
  }
  
  @Deprecated
  default Integer computeIfPresent(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    return super.computeIfPresent(key, remappingFunction);
  }
  
  @Deprecated
  default Integer compute(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    return super.compute(key, remappingFunction);
  }
  
  @Deprecated
  default Integer merge(Integer key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    return super.merge(key, value, remappingFunction);
  }
  
  int size();
  
  void defaultReturnValue(int paramInt);
  
  int defaultReturnValue();
  
  ObjectSet<Entry> int2IntEntrySet();
  
  IntSet keySet();
  
  IntCollection values();
  
  boolean containsKey(int paramInt);
  
  boolean containsValue(int paramInt);
  
  public static interface Entry extends Map.Entry<Integer, Integer> {
    @Deprecated
    default Integer getKey() {
      return Integer.valueOf(getIntKey());
    }
    
    @Deprecated
    default Integer getValue() {
      return Integer.valueOf(getIntValue());
    }
    
    @Deprecated
    default Integer setValue(Integer value) {
      return Integer.valueOf(setValue(value.intValue()));
    }
    
    int getIntKey();
    
    int getIntValue();
    
    int setValue(int param1Int);
  }
}
