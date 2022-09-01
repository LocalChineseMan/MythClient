package java.util;

public interface NavigableSet<E> extends SortedSet<E> {
  E lower(E paramE);
  
  E floor(E paramE);
  
  E ceiling(E paramE);
  
  E higher(E paramE);
  
  E pollFirst();
  
  E pollLast();
  
  Iterator<E> iterator();
  
  NavigableSet<E> descendingSet();
  
  Iterator<E> descendingIterator();
  
  NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2);
  
  NavigableSet<E> headSet(E paramE, boolean paramBoolean);
  
  NavigableSet<E> tailSet(E paramE, boolean paramBoolean);
  
  SortedSet<E> subSet(E paramE1, E paramE2);
  
  SortedSet<E> headSet(E paramE);
  
  SortedSet<E> tailSet(E paramE);
}
