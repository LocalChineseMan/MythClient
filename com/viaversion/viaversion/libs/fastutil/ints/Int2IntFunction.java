package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2IntFunction extends Function<Integer, Integer>, IntUnaryOperator {
  default int applyAsInt(int operand) {
    return get(operand);
  }
  
  default int put(int key, int value) {
    throw new UnsupportedOperationException();
  }
  
  default int remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default Integer put(Integer key, Integer value) {
    int k = key.intValue();
    boolean containsKey = containsKey(k);
    int v = put(k, value.intValue());
    return containsKey ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer get(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    int v = get(k);
    return (v != defaultReturnValue() || containsKey(k)) ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer remove(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    return containsKey(k) ? Integer.valueOf(remove(k)) : null;
  }
  
  default boolean containsKey(int key) {
    return true;
  }
  
  @Deprecated
  default boolean containsKey(Object key) {
    return (key == null) ? false : containsKey(((Integer)key).intValue());
  }
  
  default void defaultReturnValue(int rv) {
    throw new UnsupportedOperationException();
  }
  
  default int defaultReturnValue() {
    return 0;
  }
  
  int get(int paramInt);
}
