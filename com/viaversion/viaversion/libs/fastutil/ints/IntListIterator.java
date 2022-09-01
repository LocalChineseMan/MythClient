package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.ListIterator;

public interface IntListIterator extends IntBidirectionalIterator, ListIterator<Integer> {
  default void set(int k) {
    throw new UnsupportedOperationException();
  }
  
  default void add(int k) {
    throw new UnsupportedOperationException();
  }
  
  default void remove() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default void set(Integer k) {
    set(k.intValue());
  }
  
  @Deprecated
  default void add(Integer k) {
    add(k.intValue());
  }
  
  @Deprecated
  default Integer next() {
    return super.next();
  }
  
  @Deprecated
  default Integer previous() {
    return super.previous();
  }
}
