package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Lists {
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList() {
    return new ArrayList<E>();
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(E... elements) {
    Preconditions.checkNotNull(elements);
    int capacity = computeArrayListCapacity(elements.length);
    ArrayList<E> list = new ArrayList<E>(capacity);
    Collections.addAll(list, elements);
    return list;
  }
  
  @VisibleForTesting
  static int computeArrayListCapacity(int arraySize) {
    CollectPreconditions.checkNonnegative(arraySize, "arraySize");
    return Ints.saturatedCast(5L + arraySize + (arraySize / 10));
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
    Preconditions.checkNotNull(elements);
    return (elements instanceof Collection) ? new ArrayList<E>(Collections2.cast(elements)) : newArrayList(elements.iterator());
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
    ArrayList<E> list = newArrayList();
    Iterators.addAll(list, elements);
    return list;
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
    CollectPreconditions.checkNonnegative(initialArraySize, "initialArraySize");
    return new ArrayList<E>(initialArraySize);
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
    return new ArrayList<E>(computeArrayListCapacity(estimatedSize));
  }
  
  @GwtCompatible(serializable = true)
  public static <E> LinkedList<E> newLinkedList() {
    return new LinkedList<E>();
  }
  
  @GwtCompatible(serializable = true)
  public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
    LinkedList<E> list = newLinkedList();
    Iterables.addAll(list, elements);
    return list;
  }
  
  @GwtIncompatible("CopyOnWriteArrayList")
  public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
    return new CopyOnWriteArrayList<E>();
  }
  
  @GwtIncompatible("CopyOnWriteArrayList")
  public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
    Collection<? extends E> elementsCollection = (elements instanceof Collection) ? Collections2.<E>cast(elements) : newArrayList(elements);
    return new CopyOnWriteArrayList<E>(elementsCollection);
  }
  
  public static <E> List<E> asList(@Nullable E first, E[] rest) {
    return (List<E>)new OnePlusArrayList(first, (Object[])rest);
  }
  
  public static <E> List<E> asList(@Nullable E first, @Nullable E second, E[] rest) {
    return (List<E>)new TwoPlusArrayList(first, second, (Object[])rest);
  }
  
  static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> lists) {
    return CartesianList.create(lists);
  }
  
  static <B> List<List<B>> cartesianProduct(List<? extends B>... lists) {
    return cartesianProduct(Arrays.asList(lists));
  }
  
  public static <F, T> List<T> transform(List<F> fromList, Function<? super F, ? extends T> function) {
    return (fromList instanceof RandomAccess) ? (List<T>)new TransformingRandomAccessList(fromList, function) : (List<T>)new TransformingSequentialList(fromList, function);
  }
  
  public static <T> List<List<T>> partition(List<T> list, int size) {
    Preconditions.checkNotNull(list);
    Preconditions.checkArgument((size > 0));
    return (list instanceof RandomAccess) ? (List<List<T>>)new RandomAccessPartition(list, size) : (List<List<T>>)new Partition(list, size);
  }
  
  @Beta
  public static ImmutableList<Character> charactersOf(String string) {
    return (ImmutableList<Character>)new StringAsImmutableList((String)Preconditions.checkNotNull(string));
  }
  
  @Beta
  public static List<Character> charactersOf(CharSequence sequence) {
    return (List<Character>)new CharSequenceAsList((CharSequence)Preconditions.checkNotNull(sequence));
  }
  
  public static <T> List<T> reverse(List<T> list) {
    if (list instanceof ImmutableList)
      return ((ImmutableList<T>)list).reverse(); 
    if (list instanceof ReverseList)
      return ((ReverseList<T>)list).getForwardList(); 
    if (list instanceof RandomAccess)
      return new RandomAccessReverseList<T>(list); 
    return new ReverseList<T>(list);
  }
  
  private static class Lists {}
  
  private static class Lists {}
  
  private static class Lists {}
  
  private static class Lists {}
  
  private static class Lists {}
  
  private static class Lists {}
  
  private static final class Lists {}
  
  private static final class Lists {}
  
  private static class ReverseList<T> extends AbstractList<T> {
    private final List<T> forwardList;
    
    ReverseList(List<T> forwardList) {
      this.forwardList = (List<T>)Preconditions.checkNotNull(forwardList);
    }
    
    List<T> getForwardList() {
      return this.forwardList;
    }
    
    private int reverseIndex(int index) {
      int size = size();
      Preconditions.checkElementIndex(index, size);
      return size - 1 - index;
    }
    
    private int reversePosition(int index) {
      int size = size();
      Preconditions.checkPositionIndex(index, size);
      return size - index;
    }
    
    public void add(int index, @Nullable T element) {
      this.forwardList.add(reversePosition(index), element);
    }
    
    public void clear() {
      this.forwardList.clear();
    }
    
    public T remove(int index) {
      return this.forwardList.remove(reverseIndex(index));
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
      subList(fromIndex, toIndex).clear();
    }
    
    public T set(int index, @Nullable T element) {
      return this.forwardList.set(reverseIndex(index), element);
    }
    
    public T get(int index) {
      return this.forwardList.get(reverseIndex(index));
    }
    
    public int size() {
      return this.forwardList.size();
    }
    
    public List<T> subList(int fromIndex, int toIndex) {
      Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
      return Lists.reverse(this.forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
    }
    
    public Iterator<T> iterator() {
      return listIterator();
    }
    
    public ListIterator<T> listIterator(int index) {
      int start = reversePosition(index);
      final ListIterator<T> forwardIterator = this.forwardList.listIterator(start);
      return new ListIterator<T>() {
          boolean canRemoveOrSet;
          
          public void add(T e) {
            forwardIterator.add(e);
            forwardIterator.previous();
            this.canRemoveOrSet = false;
          }
          
          public boolean hasNext() {
            return forwardIterator.hasPrevious();
          }
          
          public boolean hasPrevious() {
            return forwardIterator.hasNext();
          }
          
          public T next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            this.canRemoveOrSet = true;
            return forwardIterator.previous();
          }
          
          public int nextIndex() {
            return Lists.ReverseList.this.reversePosition(forwardIterator.nextIndex());
          }
          
          public T previous() {
            if (!hasPrevious())
              throw new NoSuchElementException(); 
            this.canRemoveOrSet = true;
            return forwardIterator.next();
          }
          
          public int previousIndex() {
            return nextIndex() - 1;
          }
          
          public void remove() {
            CollectPreconditions.checkRemove(this.canRemoveOrSet);
            forwardIterator.remove();
            this.canRemoveOrSet = false;
          }
          
          public void set(T e) {
            Preconditions.checkState(this.canRemoveOrSet);
            forwardIterator.set(e);
          }
        };
    }
  }
  
  private static class RandomAccessReverseList<T> extends ReverseList<T> implements RandomAccess {
    RandomAccessReverseList(List<T> forwardList) {
      super(forwardList);
    }
  }
  
  static int hashCodeImpl(List<?> list) {
    int hashCode = 1;
    for (Object o : list) {
      hashCode = 31 * hashCode + ((o == null) ? 0 : o.hashCode());
      hashCode = hashCode ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    } 
    return hashCode;
  }
  
  static boolean equalsImpl(List<?> list, @Nullable Object object) {
    if (object == Preconditions.checkNotNull(list))
      return true; 
    if (!(object instanceof List))
      return false; 
    List<?> o = (List)object;
    return (list.size() == o.size() && Iterators.elementsEqual(list.iterator(), o.iterator()));
  }
  
  static <E> boolean addAllImpl(List<E> list, int index, Iterable<? extends E> elements) {
    boolean changed = false;
    ListIterator<E> listIterator = list.listIterator(index);
    for (E e : elements) {
      listIterator.add(e);
      changed = true;
    } 
    return changed;
  }
  
  static int indexOfImpl(List<?> list, @Nullable Object element) {
    ListIterator<?> listIterator = list.listIterator();
    while (listIterator.hasNext()) {
      if (Objects.equal(element, listIterator.next()))
        return listIterator.previousIndex(); 
    } 
    return -1;
  }
  
  static int lastIndexOfImpl(List<?> list, @Nullable Object element) {
    ListIterator<?> listIterator = list.listIterator(list.size());
    while (listIterator.hasPrevious()) {
      if (Objects.equal(element, listIterator.previous()))
        return listIterator.nextIndex(); 
    } 
    return -1;
  }
  
  static <E> ListIterator<E> listIteratorImpl(List<E> list, int index) {
    return (new AbstractListWrapper(list)).listIterator(index);
  }
  
  static <E> List<E> subListImpl(List<E> list, int fromIndex, int toIndex) {
    Object object;
    if (list instanceof RandomAccess) {
      object = new Object(list);
    } else {
      object = new Object(list);
    } 
    return object.subList(fromIndex, toIndex);
  }
  
  static <T> List<T> cast(Iterable<T> iterable) {
    return (List<T>)iterable;
  }
  
  private static class Lists {}
  
  private static class Lists {}
}
