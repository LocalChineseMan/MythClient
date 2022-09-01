package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface IntIterator extends PrimitiveIterator.OfInt {
  @Deprecated
  default Integer next() {
    return Integer.valueOf(nextInt());
  }
  
  @Deprecated
  default void forEachRemaining(Consumer<? super Integer> action) {
    Objects.requireNonNull(action);
    forEachRemaining(action::accept);
  }
  
  default int skip(int n) {
    if (n < 0)
      throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
    int i = n;
    while (i-- != 0 && hasNext())
      nextInt(); 
    return n - i - 1;
  }
  
  int nextInt();
}
