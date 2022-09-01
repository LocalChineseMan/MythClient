package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public interface IntSortedSet extends IntSet, SortedSet<Integer>, IntBidirectionalIterable {
  @Deprecated
  default IntSortedSet subSet(Integer from, Integer to) {
    return subSet(from.intValue(), to.intValue());
  }
  
  @Deprecated
  default IntSortedSet headSet(Integer to) {
    return headSet(to.intValue());
  }
  
  @Deprecated
  default IntSortedSet tailSet(Integer from) {
    return tailSet(from.intValue());
  }
  
  @Deprecated
  default Integer first() {
    return Integer.valueOf(firstInt());
  }
  
  @Deprecated
  default Integer last() {
    return Integer.valueOf(lastInt());
  }
  
  IntBidirectionalIterator iterator(int paramInt);
  
  IntBidirectionalIterator iterator();
  
  IntSortedSet subSet(int paramInt1, int paramInt2);
  
  IntSortedSet headSet(int paramInt);
  
  IntSortedSet tailSet(int paramInt);
  
  IntComparator comparator();
  
  int firstInt();
  
  int lastInt();
}
