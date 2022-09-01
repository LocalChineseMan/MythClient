package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2IntFunction<K> extends Function<K, Integer>, ToIntFunction<K> {
  default int applyAsInt(K operand) {
    return getInt(operand);
  }
  
  default int put(K key, int value) {
    throw new UnsupportedOperationException();
  }
  
  default int removeInt(Object key) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default Integer put(K key, Integer value) {
    K k = key;
    boolean containsKey = containsKey(k);
    int v = put(k, value.intValue());
    return containsKey ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer get(Object key) {
    Object k = key;
    int v = getInt(k);
    return (v != defaultReturnValue() || containsKey(k)) ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer remove(Object key) {
    Object k = key;
    return containsKey(k) ? Integer.valueOf(removeInt(k)) : null;
  }
  
  default void defaultReturnValue(int rv) {
    throw new UnsupportedOperationException();
  }
  
  default int defaultReturnValue() {
    return 0;
  }
  
  int getInt(Object paramObject);
}
