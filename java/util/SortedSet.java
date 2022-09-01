package java.util;

public interface SortedSet<E> extends Set<E> {
  Comparator<? super E> comparator();
  
  SortedSet<E> subSet(E paramE1, E paramE2);
  
  SortedSet<E> headSet(E paramE);
  
  SortedSet<E> tailSet(E paramE);
  
  E first();
  
  E last();
  
  default Spliterator<E> spliterator() {
    return (Spliterator<E>)new Object(this, (Collection)this, 21);
  }
}
