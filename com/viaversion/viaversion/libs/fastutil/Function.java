package com.viaversion.viaversion.libs.fastutil;

import java.util.function.Function;

@FunctionalInterface
public interface Function<K, V> extends Function<K, V> {
  default V apply(K key) {
    return get(key);
  }
  
  default V put(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  V get(Object paramObject);
  
  default boolean containsKey(Object key) {
    return true;
  }
  
  default V remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  default int size() {
    return -1;
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
}
