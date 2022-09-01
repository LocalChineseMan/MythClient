package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IntIterable extends Iterable<Integer> {
  default void forEach(IntConsumer action) {
    Objects.requireNonNull(action);
    for (IntIterator iterator = iterator(); iterator.hasNext();)
      action.accept(iterator.nextInt()); 
  }
  
  @Deprecated
  default void forEach(Consumer<? super Integer> action) {
    Objects.requireNonNull(action);
    forEach(action::accept);
  }
  
  IntIterator iterator();
}
