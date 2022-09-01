package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Ordering<T> implements Comparator<T> {
  static final int LEFT_IS_GREATER = 1;
  
  static final int RIGHT_IS_GREATER = -1;
  
  @GwtCompatible(serializable = true)
  public static <C extends Comparable> Ordering<C> natural() {
    return NaturalOrdering.INSTANCE;
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> from(Comparator<T> comparator) {
    return (comparator instanceof Ordering) ? (Ordering<T>)comparator : new ComparatorOrdering<T>(comparator);
  }
  
  @Deprecated
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> from(Ordering<T> ordering) {
    return (Ordering<T>)Preconditions.checkNotNull(ordering);
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> explicit(List<T> valuesInOrder) {
    return (Ordering<T>)new ExplicitOrdering(valuesInOrder);
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> explicit(T leastValue, T... remainingValuesInOrder) {
    return explicit(Lists.asList(leastValue, remainingValuesInOrder));
  }
  
  @GwtCompatible(serializable = true)
  public static Ordering<Object> allEqual() {
    return (Ordering<Object>)AllEqualOrdering.INSTANCE;
  }
  
  @GwtCompatible(serializable = true)
  public static Ordering<Object> usingToString() {
    return (Ordering<Object>)UsingToStringOrdering.INSTANCE;
  }
  
  public static Ordering<Object> arbitrary() {
    return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
  }
  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> reverse() {
    return new ReverseOrdering<S>(this);
  }
  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> nullsFirst() {
    return (Ordering<S>)new NullsFirstOrdering(this);
  }
  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> nullsLast() {
    return (Ordering<S>)new NullsLastOrdering(this);
  }
  
  @GwtCompatible(serializable = true)
  public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
    return new ByFunctionOrdering<F, T>(function, this);
  }
  
  <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
    return onResultOf(Maps.keyFunction());
  }
  
  @GwtCompatible(serializable = true)
  public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator) {
    return (Ordering<U>)new CompoundOrdering((Comparator)this, (Comparator)Preconditions.checkNotNull(secondaryComparator));
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> comparators) {
    return (Ordering<T>)new CompoundOrdering(comparators);
  }
  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<Iterable<S>> lexicographical() {
    return (Ordering<Iterable<S>>)new LexicographicalOrdering(this);
  }
  
  public abstract int compare(@Nullable T paramT1, @Nullable T paramT2);
  
  public <E extends T> E min(Iterator<E> iterator) {
    E minSoFar = iterator.next();
    while (iterator.hasNext())
      minSoFar = min(minSoFar, iterator.next()); 
    return minSoFar;
  }
  
  public <E extends T> E min(Iterable<E> iterable) {
    return min(iterable.iterator());
  }
  
  public <E extends T> E min(@Nullable E a, @Nullable E b) {
    return (compare((T)a, (T)b) <= 0) ? a : b;
  }
  
  public <E extends T> E min(@Nullable E a, @Nullable E b, @Nullable E c, E... rest) {
    E minSoFar = min(min(a, b), c);
    for (E r : rest)
      minSoFar = min(minSoFar, r); 
    return minSoFar;
  }
  
  public <E extends T> E max(Iterator<E> iterator) {
    E maxSoFar = iterator.next();
    while (iterator.hasNext())
      maxSoFar = max(maxSoFar, iterator.next()); 
    return maxSoFar;
  }
  
  public <E extends T> E max(Iterable<E> iterable) {
    return max(iterable.iterator());
  }
  
  public <E extends T> E max(@Nullable E a, @Nullable E b) {
    return (compare((T)a, (T)b) >= 0) ? a : b;
  }
  
  public <E extends T> E max(@Nullable E a, @Nullable E b, @Nullable E c, E... rest) {
    E maxSoFar = max(max(a, b), c);
    for (E r : rest)
      maxSoFar = max(maxSoFar, r); 
    return maxSoFar;
  }
  
  public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
    if (iterable instanceof Collection) {
      Collection<E> collection = (Collection<E>)iterable;
      if (collection.size() <= 2L * k) {
        E[] array = (E[])collection.toArray();
        Arrays.sort(array, (Comparator<? super E>)this);
        if (array.length > k)
          array = ObjectArrays.arraysCopyOf(array, k); 
        return Collections.unmodifiableList(Arrays.asList(array));
      } 
    } 
    return leastOf(iterable.iterator(), k);
  }
  
  public <E extends T> List<E> leastOf(Iterator<E> elements, int k) {
    Preconditions.checkNotNull(elements);
    CollectPreconditions.checkNonnegative(k, "k");
    if (k == 0 || !elements.hasNext())
      return ImmutableList.of(); 
    if (k >= 1073741823) {
      ArrayList<E> list = Lists.newArrayList(elements);
      Collections.sort(list, (Comparator<? super E>)this);
      if (list.size() > k)
        list.subList(k, list.size()).clear(); 
      list.trimToSize();
      return Collections.unmodifiableList(list);
    } 
    int bufferCap = k * 2;
    E[] buffer = (E[])new Object[bufferCap];
    E threshold = elements.next();
    buffer[0] = threshold;
    int bufferSize = 1;
    while (bufferSize < k && elements.hasNext()) {
      E e = elements.next();
      buffer[bufferSize++] = e;
      threshold = max(threshold, e);
    } 
    while (elements.hasNext()) {
      E e = elements.next();
      if (compare((T)e, (T)threshold) >= 0)
        continue; 
      buffer[bufferSize++] = e;
      if (bufferSize == bufferCap) {
        int left = 0;
        int right = bufferCap - 1;
        int minThresholdPosition = 0;
        while (left < right) {
          int pivotIndex = left + right + 1 >>> 1;
          int pivotNewIndex = partition(buffer, left, right, pivotIndex);
          if (pivotNewIndex > k) {
            right = pivotNewIndex - 1;
            continue;
          } 
          if (pivotNewIndex < k) {
            left = Math.max(pivotNewIndex, left + 1);
            minThresholdPosition = pivotNewIndex;
          } 
        } 
        bufferSize = k;
        threshold = buffer[minThresholdPosition];
        for (int i = minThresholdPosition + 1; i < bufferSize; i++)
          threshold = max(threshold, buffer[i]); 
      } 
    } 
    Arrays.sort(buffer, 0, bufferSize, (Comparator<? super E>)this);
    bufferSize = Math.min(bufferSize, k);
    return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(buffer, bufferSize)));
  }
  
  private <E extends T> int partition(E[] values, int left, int right, int pivotIndex) {
    E pivotValue = values[pivotIndex];
    values[pivotIndex] = values[right];
    values[right] = pivotValue;
    int storeIndex = left;
    for (int i = left; i < right; i++) {
      if (compare((T)values[i], (T)pivotValue) < 0) {
        ObjectArrays.swap((Object[])values, storeIndex, i);
        storeIndex++;
      } 
    } 
    ObjectArrays.swap((Object[])values, right, storeIndex);
    return storeIndex;
  }
  
  public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
    return reverse().leastOf(iterable, k);
  }
  
  public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
    return reverse().leastOf(iterator, k);
  }
  
  public <E extends T> List<E> sortedCopy(Iterable<E> elements) {
    E[] array = (E[])Iterables.toArray(elements);
    Arrays.sort(array, (Comparator<? super E>)this);
    return Lists.newArrayList(Arrays.asList(array));
  }
  
  public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements) {
    E[] array = (E[])Iterables.toArray(elements);
    for (E e : array)
      Preconditions.checkNotNull(e); 
    Arrays.sort(array, (Comparator<? super E>)this);
    return ImmutableList.asImmutableList((Object[])array);
  }
  
  public boolean isOrdered(Iterable<? extends T> iterable) {
    Iterator<? extends T> it = iterable.iterator();
    if (it.hasNext()) {
      T prev = it.next();
      while (it.hasNext()) {
        T next = it.next();
        if (compare(prev, next) > 0)
          return false; 
        prev = next;
      } 
    } 
    return true;
  }
  
  public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
    Iterator<? extends T> it = iterable.iterator();
    if (it.hasNext()) {
      T prev = it.next();
      while (it.hasNext()) {
        T next = it.next();
        if (compare(prev, next) >= 0)
          return false; 
        prev = next;
      } 
    } 
    return true;
  }
  
  public int binarySearch(List<? extends T> sortedList, @Nullable T key) {
    return Collections.binarySearch(sortedList, key, (Comparator<? super T>)this);
  }
  
  static class Ordering {}
  
  static class Ordering {}
  
  private static class Ordering {}
}
