package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public final class Collections2 {
  public static <E> Collection<E> filter(Collection<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof FilteredCollection)
      return ((FilteredCollection<E>)unfiltered).createCombined(predicate); 
    return new FilteredCollection<E>((Collection<E>)Preconditions.checkNotNull(unfiltered), (Predicate<? super E>)Preconditions.checkNotNull(predicate));
  }
  
  static boolean safeContains(Collection<?> collection, @Nullable Object object) {
    Preconditions.checkNotNull(collection);
    try {
      return collection.contains(object);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    } 
  }
  
  static boolean safeRemove(Collection<?> collection, @Nullable Object object) {
    Preconditions.checkNotNull(collection);
    try {
      return collection.remove(object);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    } 
  }
  
  private static class Collections2 {}
  
  private static final class Collections2 {}
  
  private static final class Collections2 {}
  
  private static final class Collections2 {}
  
  static class Collections2 {}
  
  static class FilteredCollection<E> extends AbstractCollection<E> {
    final Collection<E> unfiltered;
    
    final Predicate<? super E> predicate;
    
    FilteredCollection(Collection<E> unfiltered, Predicate<? super E> predicate) {
      this.unfiltered = unfiltered;
      this.predicate = predicate;
    }
    
    FilteredCollection<E> createCombined(Predicate<? super E> newPredicate) {
      return new FilteredCollection(this.unfiltered, Predicates.and(this.predicate, newPredicate));
    }
    
    public boolean add(E element) {
      Preconditions.checkArgument(this.predicate.apply(element));
      return this.unfiltered.add(element);
    }
    
    public boolean addAll(Collection<? extends E> collection) {
      for (E element : collection)
        Preconditions.checkArgument(this.predicate.apply(element)); 
      return this.unfiltered.addAll(collection);
    }
    
    public void clear() {
      Iterables.removeIf(this.unfiltered, this.predicate);
    }
    
    public boolean contains(@Nullable Object element) {
      if (Collections2.safeContains(this.unfiltered, element)) {
        E e = (E)element;
        return this.predicate.apply(e);
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      return Collections2.containsAllImpl(this, collection);
    }
    
    public boolean isEmpty() {
      return !Iterables.any(this.unfiltered, this.predicate);
    }
    
    public Iterator<E> iterator() {
      return Iterators.filter(this.unfiltered.iterator(), this.predicate);
    }
    
    public boolean remove(Object element) {
      return (contains(element) && this.unfiltered.remove(element));
    }
    
    public boolean removeAll(Collection<?> collection) {
      return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, Predicates.in(collection)));
    }
    
    public boolean retainAll(Collection<?> collection) {
      return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, Predicates.not(Predicates.in(collection))));
    }
    
    public int size() {
      return Iterators.size(iterator());
    }
    
    public Object[] toArray() {
      return Lists.<E>newArrayList(iterator()).toArray();
    }
    
    public <T> T[] toArray(T[] array) {
      return (T[])Lists.<E>newArrayList(iterator()).toArray((Object[])array);
    }
  }
  
  public static <F, T> Collection<T> transform(Collection<F> fromCollection, Function<? super F, T> function) {
    return (Collection<T>)new TransformedCollection(fromCollection, function);
  }
  
  static boolean containsAllImpl(Collection<?> self, Collection<?> c) {
    return Iterables.all(c, Predicates.in(self));
  }
  
  static String toStringImpl(Collection<?> collection) {
    StringBuilder sb = newStringBuilderForCollection(collection.size()).append('[');
    STANDARD_JOINER.appendTo(sb, Iterables.transform(collection, (Function<?, ?>)new Object(collection)));
    return sb.append(']').toString();
  }
  
  static StringBuilder newStringBuilderForCollection(int size) {
    CollectPreconditions.checkNonnegative(size, "size");
    return new StringBuilder((int)Math.min(size * 8L, 1073741824L));
  }
  
  static <T> Collection<T> cast(Iterable<T> iterable) {
    return (Collection<T>)iterable;
  }
  
  static final Joiner STANDARD_JOINER = Joiner.on(", ").useForNull("null");
  
  @Beta
  public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> elements) {
    return orderedPermutations(elements, Ordering.natural());
  }
  
  @Beta
  public static <E> Collection<List<E>> orderedPermutations(Iterable<E> elements, Comparator<? super E> comparator) {
    return (Collection<List<E>>)new OrderedPermutationCollection(elements, comparator);
  }
  
  @Beta
  public static <E> Collection<List<E>> permutations(Collection<E> elements) {
    return (Collection<List<E>>)new PermutationCollection(ImmutableList.copyOf(elements));
  }
  
  private static boolean isPermutation(List<?> first, List<?> second) {
    if (first.size() != second.size())
      return false; 
    HashMultiset hashMultiset1 = HashMultiset.create(first);
    HashMultiset hashMultiset2 = HashMultiset.create(second);
    return hashMultiset1.equals(hashMultiset2);
  }
  
  private static boolean isPositiveInt(long n) {
    return (n >= 0L && n <= 2147483647L);
  }
}
