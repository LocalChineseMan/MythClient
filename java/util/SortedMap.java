package java.util;

public interface SortedMap<K, V> extends Map<K, V> {
  Comparator<? super K> comparator();
  
  SortedMap<K, V> subMap(K paramK1, K paramK2);
  
  SortedMap<K, V> headMap(K paramK);
  
  SortedMap<K, V> tailMap(K paramK);
  
  K firstKey();
  
  K lastKey();
  
  Set<K> keySet();
  
  Collection<V> values();
  
  Set<Map.Entry<K, V>> entrySet();
}
