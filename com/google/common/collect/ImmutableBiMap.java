package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableBiMap<K, V> extends ImmutableMap<K, V> implements BiMap<K, V> {
  public static <K, V> ImmutableBiMap<K, V> of() {
    return EmptyImmutableBiMap.INSTANCE;
  }
  
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1) {
    return new SingletonImmutableBiMap<K, V>(k1, v1);
  }
  
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2) {
    return (ImmutableBiMap<K, V>)new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2) });
  }
  
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return (ImmutableBiMap<K, V>)new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
  }
  
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return (ImmutableBiMap<K, V>)new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
  }
  
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return (ImmutableBiMap<K, V>)new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
  }
  
  public static <K, V> Builder<K, V> builder() {
    return new Builder();
  }
  
  public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    Map.Entry<K, V> entry;
    if (map instanceof ImmutableBiMap) {
      ImmutableBiMap<K, V> bimap = (ImmutableBiMap)map;
      if (!bimap.isPartialView())
        return bimap; 
    } 
    Map.Entry[] arrayOfEntry = (Map.Entry[])map.entrySet().toArray((Object[])EMPTY_ENTRY_ARRAY);
    switch (arrayOfEntry.length) {
      case 0:
        return of();
      case 1:
        entry = arrayOfEntry[0];
        return of(entry.getKey(), entry.getValue());
    } 
    return (ImmutableBiMap<K, V>)new RegularImmutableBiMap(arrayOfEntry);
  }
  
  private static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = (Map.Entry<?, ?>[])new Map.Entry[0];
  
  public ImmutableSet<V> values() {
    return inverse().keySet();
  }
  
  @Deprecated
  public V forcePut(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  Object writeReplace() {
    return new SerializedForm(this);
  }
  
  public abstract ImmutableBiMap<V, K> inverse();
  
  private static class ImmutableBiMap {}
  
  public static final class ImmutableBiMap {}
}
