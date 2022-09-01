package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface IntCollection extends Collection<Integer>, IntIterable {
  @Deprecated
  default boolean add(Integer key) {
    return add(key.intValue());
  }
  
  @Deprecated
  default boolean contains(Object key) {
    if (key == null)
      return false; 
    return contains(((Integer)key).intValue());
  }
  
  @Deprecated
  default boolean remove(Object key) {
    if (key == null)
      return false; 
    return rem(((Integer)key).intValue());
  }
  
  @Deprecated
  default boolean removeIf(Predicate<? super Integer> filter) {
    return removeIf(key -> filter.test(Integer.valueOf(key)));
  }
  
  default boolean removeIf(IntPredicate filter) {
    Objects.requireNonNull(filter);
    boolean removed = false;
    IntIterator each = iterator();
    while (each.hasNext()) {
      if (filter.test(each.nextInt())) {
        each.remove();
        removed = true;
      } 
    } 
    return removed;
  }
  
  IntIterator iterator();
  
  boolean add(int paramInt);
  
  boolean contains(int paramInt);
  
  boolean rem(int paramInt);
  
  int[] toIntArray();
  
  @Deprecated
  int[] toIntArray(int[] paramArrayOfint);
  
  int[] toArray(int[] paramArrayOfint);
  
  boolean addAll(IntCollection paramIntCollection);
  
  boolean containsAll(IntCollection paramIntCollection);
  
  boolean removeAll(IntCollection paramIntCollection);
  
  boolean retainAll(IntCollection paramIntCollection);
}
