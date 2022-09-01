package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;
import java.util.SortedSet;

public interface ObjectSortedSet<K> extends ObjectSet<K>, SortedSet<K>, ObjectBidirectionalIterable<K> {
  ObjectBidirectionalIterator<K> iterator(K paramK);
  
  ObjectBidirectionalIterator<K> iterator();
  
  ObjectSortedSet<K> subSet(K paramK1, K paramK2);
  
  ObjectSortedSet<K> headSet(K paramK);
  
  ObjectSortedSet<K> tailSet(K paramK);
}
