package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class IntSortedSets {
  public static class EmptySet extends IntSets$EmptySet implements IntSortedSet, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public IntBidirectionalIterator iterator(int from) {
      return IntIterators.EMPTY_ITERATOR;
    }
    
    public IntSortedSet subSet(int from, int to) {
      return IntSortedSets.EMPTY_SET;
    }
    
    public IntSortedSet headSet(int from) {
      return IntSortedSets.EMPTY_SET;
    }
    
    public IntSortedSet tailSet(int to) {
      return IntSortedSets.EMPTY_SET;
    }
    
    public int firstInt() {
      throw new NoSuchElementException();
    }
    
    public int lastInt() {
      throw new NoSuchElementException();
    }
    
    public IntComparator comparator() {
      return null;
    }
    
    @Deprecated
    public IntSortedSet subSet(Integer from, Integer to) {
      return IntSortedSets.EMPTY_SET;
    }
    
    @Deprecated
    public IntSortedSet headSet(Integer from) {
      return IntSortedSets.EMPTY_SET;
    }
    
    @Deprecated
    public IntSortedSet tailSet(Integer to) {
      return IntSortedSets.EMPTY_SET;
    }
    
    @Deprecated
    public Integer first() {
      throw new NoSuchElementException();
    }
    
    @Deprecated
    public Integer last() {
      throw new NoSuchElementException();
    }
    
    public Object clone() {
      return IntSortedSets.EMPTY_SET;
    }
    
    private Object readResolve() {
      return IntSortedSets.EMPTY_SET;
    }
  }
  
  public static final EmptySet EMPTY_SET = new EmptySet();
  
  public static class IntSortedSets {}
  
  public static class IntSortedSets {}
  
  public static class Singleton extends IntSets$Singleton implements IntSortedSet, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    final IntComparator comparator;
    
    protected Singleton(int element, IntComparator comparator) {
      super(element);
      this.comparator = comparator;
    }
    
    private Singleton(int element) {
      this(element, (IntComparator)null);
    }
    
    final int compare(int k1, int k2) {
      return (this.comparator == null) ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
    }
    
    public IntBidirectionalIterator iterator(int from) {
      IntBidirectionalIterator i = iterator();
      if (compare(this.element, from) <= 0)
        i.nextInt(); 
      return i;
    }
    
    public IntComparator comparator() {
      return this.comparator;
    }
    
    public IntSortedSet subSet(int from, int to) {
      if (compare(from, this.element) <= 0 && compare(this.element, to) < 0)
        return this; 
      return IntSortedSets.EMPTY_SET;
    }
    
    public IntSortedSet headSet(int to) {
      if (compare(this.element, to) < 0)
        return this; 
      return IntSortedSets.EMPTY_SET;
    }
    
    public IntSortedSet tailSet(int from) {
      if (compare(from, this.element) <= 0)
        return this; 
      return IntSortedSets.EMPTY_SET;
    }
    
    public int firstInt() {
      return this.element;
    }
    
    public int lastInt() {
      return this.element;
    }
    
    @Deprecated
    public IntSortedSet subSet(Integer from, Integer to) {
      return subSet(from.intValue(), to.intValue());
    }
    
    @Deprecated
    public IntSortedSet headSet(Integer to) {
      return headSet(to.intValue());
    }
    
    @Deprecated
    public IntSortedSet tailSet(Integer from) {
      return tailSet(from.intValue());
    }
    
    @Deprecated
    public Integer first() {
      return Integer.valueOf(this.element);
    }
    
    @Deprecated
    public Integer last() {
      return Integer.valueOf(this.element);
    }
  }
  
  public static IntSortedSet singleton(int element) {
    return new Singleton(element);
  }
  
  public static IntSortedSet singleton(int element, IntComparator comparator) {
    return new Singleton(element, comparator);
  }
  
  public static IntSortedSet singleton(Object element) {
    return new Singleton(((Integer)element).intValue());
  }
  
  public static IntSortedSet singleton(Object element, IntComparator comparator) {
    return new Singleton(((Integer)element).intValue(), comparator);
  }
  
  public static IntSortedSet synchronize(IntSortedSet s) {
    return (IntSortedSet)new SynchronizedSortedSet(s);
  }
  
  public static IntSortedSet synchronize(IntSortedSet s, Object sync) {
    return (IntSortedSet)new SynchronizedSortedSet(s, sync);
  }
  
  public static IntSortedSet unmodifiable(IntSortedSet s) {
    return (IntSortedSet)new UnmodifiableSortedSet(s);
  }
}
