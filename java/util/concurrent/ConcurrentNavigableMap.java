package java.util.concurrent;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public interface ConcurrentNavigableMap<K, V> extends ConcurrentMap<K, V>, NavigableMap<K, V> {
  ConcurrentNavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2);
  
  ConcurrentNavigableMap<K, V> headMap(K paramK, boolean paramBoolean);
  
  ConcurrentNavigableMap<K, V> tailMap(K paramK, boolean paramBoolean);
  
  ConcurrentNavigableMap<K, V> subMap(K paramK1, K paramK2);
  
  ConcurrentNavigableMap<K, V> headMap(K paramK);
  
  ConcurrentNavigableMap<K, V> tailMap(K paramK);
  
  ConcurrentNavigableMap<K, V> descendingMap();
  
  NavigableSet<K> navigableKeySet();
  
  NavigableSet<K> keySet();
  
  NavigableSet<K> descendingKeySet();
}
