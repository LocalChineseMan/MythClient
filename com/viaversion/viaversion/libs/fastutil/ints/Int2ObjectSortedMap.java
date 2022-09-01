package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Int2ObjectSortedMap<V> extends Int2ObjectMap<V>, SortedMap<Integer, V> {
  @Deprecated
  default Int2ObjectSortedMap<V> subMap(Integer from, Integer to) {
    return subMap(from.intValue(), to.intValue());
  }
  
  @Deprecated
  default Int2ObjectSortedMap<V> headMap(Integer to) {
    return headMap(to.intValue());
  }
  
  @Deprecated
  default Int2ObjectSortedMap<V> tailMap(Integer from) {
    return tailMap(from.intValue());
  }
  
  @Deprecated
  default Integer firstKey() {
    return Integer.valueOf(firstIntKey());
  }
  
  @Deprecated
  default Integer lastKey() {
    return Integer.valueOf(lastIntKey());
  }
  
  public static interface FastSortedEntrySet<V> extends ObjectSortedSet<Int2ObjectMap.Entry<V>>, Int2ObjectMap.FastEntrySet<V> {
    ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator();
    
    ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap.Entry<V> param1Entry);
  }
  
  @Deprecated
  default ObjectSortedSet<Map.Entry<Integer, V>> entrySet() {
    return (ObjectSortedSet)int2ObjectEntrySet();
  }
  
  Int2ObjectSortedMap<V> subMap(int paramInt1, int paramInt2);
  
  Int2ObjectSortedMap<V> headMap(int paramInt);
  
  Int2ObjectSortedMap<V> tailMap(int paramInt);
  
  int firstIntKey();
  
  int lastIntKey();
  
  ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet();
  
  IntSortedSet keySet();
  
  ObjectCollection<V> values();
  
  IntComparator comparator();
}
