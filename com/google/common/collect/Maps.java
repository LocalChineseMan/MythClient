package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Maps {
  static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
    return (Function<Map.Entry<K, ?>, K>)EntryFunction.KEY;
  }
  
  static <V> Function<Map.Entry<?, V>, V> valueFunction() {
    return (Function<Map.Entry<?, V>, V>)EntryFunction.VALUE;
  }
  
  static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
    return Iterators.transform(entryIterator, (Function)keyFunction());
  }
  
  static <K, V> Iterator<V> valueIterator(Iterator<Map.Entry<K, V>> entryIterator) {
    return Iterators.transform(entryIterator, (Function)valueFunction());
  }
  
  static <K, V> UnmodifiableIterator<V> valueIterator(UnmodifiableIterator<Map.Entry<K, V>> entryIterator) {
    return (UnmodifiableIterator<V>)new Object(entryIterator);
  }
  
  @GwtCompatible(serializable = true)
  @Beta
  public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
    if (map instanceof ImmutableEnumMap) {
      ImmutableEnumMap<K, V> result = (ImmutableEnumMap)map;
      return (ImmutableMap<K, V>)result;
    } 
    if (map.isEmpty())
      return ImmutableMap.of(); 
    for (Map.Entry<K, ? extends V> entry : map.entrySet()) {
      Preconditions.checkNotNull(entry.getKey());
      Preconditions.checkNotNull(entry.getValue());
    } 
    return ImmutableEnumMap.asImmutable(new EnumMap<K, V>(map));
  }
  
  public static <K, V> HashMap<K, V> newHashMap() {
    return new HashMap<K, V>();
  }
  
  public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return new HashMap<K, V>(capacity(expectedSize));
  }
  
  static int capacity(int expectedSize) {
    if (expectedSize < 3) {
      CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
      return expectedSize + 1;
    } 
    if (expectedSize < 1073741824)
      return expectedSize + expectedSize / 3; 
    return Integer.MAX_VALUE;
  }
  
  public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
    return new HashMap<K, V>(map);
  }
  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
    return new LinkedHashMap<K, V>();
  }
  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
    return new LinkedHashMap<K, V>(map);
  }
  
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
    return (new MapMaker()).makeMap();
  }
  
  public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
    return new TreeMap<K, V>();
  }
  
  public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
    return new TreeMap<K, V>(map);
  }
  
  public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<C> comparator) {
    return new TreeMap<K, V>(comparator);
  }
  
  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
    return new EnumMap<K, V>((Class<K>)Preconditions.checkNotNull(type));
  }
  
  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
    return new EnumMap<K, V>(map);
  }
  
  public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
    return new IdentityHashMap<K, V>();
  }
  
  public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
    if (left instanceof SortedMap) {
      SortedMap<K, ? extends V> sortedLeft = (SortedMap)left;
      SortedMapDifference<K, V> result = difference(sortedLeft, right);
      return (MapDifference<K, V>)result;
    } 
    return difference(left, right, Equivalence.equals());
  }
  
  @Beta
  public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence) {
    Preconditions.checkNotNull(valueEquivalence);
    Map<K, V> onlyOnLeft = newHashMap();
    Map<K, V> onlyOnRight = new HashMap<K, V>(right);
    Map<K, V> onBoth = newHashMap();
    Map<K, MapDifference.ValueDifference<V>> differences = newHashMap();
    doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
    return (MapDifference<K, V>)new MapDifferenceImpl(onlyOnLeft, onlyOnRight, onBoth, differences);
  }
  
  private static <K, V> void doDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
    for (Map.Entry<? extends K, ? extends V> entry : left.entrySet()) {
      K leftKey = entry.getKey();
      V leftValue = entry.getValue();
      if (right.containsKey(leftKey)) {
        V rightValue = onlyOnRight.remove(leftKey);
        if (valueEquivalence.equivalent(leftValue, rightValue)) {
          onBoth.put(leftKey, leftValue);
          continue;
        } 
        differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
        continue;
      } 
      onlyOnLeft.put(leftKey, leftValue);
    } 
  }
  
  private static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
    if (map instanceof SortedMap)
      return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>)map); 
    return Collections.unmodifiableMap(map);
  }
  
  public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right) {
    Preconditions.checkNotNull(left);
    Preconditions.checkNotNull(right);
    Comparator<? super K> comparator = orNaturalOrder(left.comparator());
    SortedMap<K, V> onlyOnLeft = newTreeMap(comparator);
    SortedMap<K, V> onlyOnRight = newTreeMap(comparator);
    onlyOnRight.putAll(right);
    SortedMap<K, V> onBoth = newTreeMap(comparator);
    SortedMap<K, MapDifference.ValueDifference<V>> differences = newTreeMap(comparator);
    doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
    return (SortedMapDifference<K, V>)new SortedMapDifferenceImpl(onlyOnLeft, onlyOnRight, onBoth, differences);
  }
  
  static <E> Comparator<? super E> orNaturalOrder(@Nullable Comparator<? super E> comparator) {
    if (comparator != null)
      return comparator; 
    return Ordering.natural();
  }
  
  @Beta
  public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
    if (set instanceof SortedSet)
      return asMap((SortedSet<K>)set, function); 
    return (Map<K, V>)new AsMapView(set, function);
  }
  
  @Beta
  public static <K, V> SortedMap<K, V> asMap(SortedSet<K> set, Function<? super K, V> function) {
    return Platform.mapsAsMapSortedSet(set, function);
  }
  
  static <K, V> SortedMap<K, V> asMapSortedIgnoreNavigable(SortedSet<K> set, Function<? super K, V> function) {
    return (SortedMap<K, V>)new SortedAsMapView(set, function);
  }
  
  @Beta
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> set, Function<? super K, V> function) {
    return (NavigableMap<K, V>)new NavigableAsMapView(set, function);
  }
  
  static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(Set<K> set, Function<? super K, V> function) {
    return (Iterator<Map.Entry<K, V>>)new Object(set.iterator(), function);
  }
  
  private static <E> Set<E> removeOnlySet(Set<E> set) {
    return (Set<E>)new Object(set);
  }
  
  private static <E> SortedSet<E> removeOnlySortedSet(SortedSet<E> set) {
    return (SortedSet<E>)new Object(set);
  }
  
  @GwtIncompatible("NavigableSet")
  private static <E> NavigableSet<E> removeOnlyNavigableSet(NavigableSet<E> set) {
    return (NavigableSet<E>)new Object(set);
  }
  
  @Beta
  public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> keys, Function<? super K, V> valueFunction) {
    return toMap(keys.iterator(), valueFunction);
  }
  
  @Beta
  public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> keys, Function<? super K, V> valueFunction) {
    Preconditions.checkNotNull(valueFunction);
    Map<K, V> builder = newLinkedHashMap();
    while (keys.hasNext()) {
      K key = keys.next();
      builder.put(key, (V)valueFunction.apply(key));
    } 
    return ImmutableMap.copyOf(builder);
  }
  
  public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction) {
    return uniqueIndex(values.iterator(), keyFunction);
  }
  
  public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction) {
    Preconditions.checkNotNull(keyFunction);
    ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
    while (values.hasNext()) {
      V value = values.next();
      builder.put((K)keyFunction.apply(value), value);
    } 
    return builder.build();
  }
  
  @GwtIncompatible("java.util.Properties")
  public static ImmutableMap<String, String> fromProperties(Properties properties) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      builder.put(key, properties.getProperty(key));
    } 
    return builder.build();
  }
  
  @GwtCompatible(serializable = true)
  public static <K, V> Map.Entry<K, V> immutableEntry(@Nullable K key, @Nullable V value) {
    return new ImmutableEntry<K, V>(key, value);
  }
  
  static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> entrySet) {
    return (Set<Map.Entry<K, V>>)new UnmodifiableEntrySet(Collections.unmodifiableSet(entrySet));
  }
  
  static <K, V> Map.Entry<K, V> unmodifiableEntry(Map.Entry<? extends K, ? extends V> entry) {
    Preconditions.checkNotNull(entry);
    return (Map.Entry<K, V>)new Object(entry);
  }
  
  @Beta
  public static <A, B> Converter<A, B> asConverter(BiMap<A, B> bimap) {
    return (Converter<A, B>)new BiMapConverter(bimap);
  }
  
  public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
    return Synchronized.biMap(bimap, null);
  }
  
  public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
    return (BiMap<K, V>)new UnmodifiableBiMap(bimap, null);
  }
  
  public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }
  
  public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }
  
  public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    if (fromMap instanceof SortedMap)
      return transformEntries((SortedMap<K, V1>)fromMap, transformer); 
    return (Map<K, V2>)new TransformedEntriesMap(fromMap, transformer);
  }
  
  public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return Platform.mapsTransformEntriesSortedMap(fromMap, transformer);
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return (NavigableMap<K, V2>)new TransformedEntriesNavigableMap(fromMap, transformer);
  }
  
  static <K, V1, V2> SortedMap<K, V2> transformEntriesIgnoreNavigable(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return (SortedMap<K, V2>)new TransformedEntriesSortedMap(fromMap, transformer);
  }
  
  static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(Function<? super V1, V2> function) {
    Preconditions.checkNotNull(function);
    return (EntryTransformer<K, V1, V2>)new Object(function);
  }
  
  static <K, V1, V2> Function<V1, V2> asValueToValueFunction(EntryTransformer<? super K, V1, V2> transformer, K key) {
    Preconditions.checkNotNull(transformer);
    return (Function<V1, V2>)new Object(transformer, key);
  }
  
  static <K, V1, V2> Function<Map.Entry<K, V1>, V2> asEntryToValueFunction(EntryTransformer<? super K, ? super V1, V2> transformer) {
    Preconditions.checkNotNull(transformer);
    return (Function<Map.Entry<K, V1>, V2>)new Object(transformer);
  }
  
  static <V2, K, V1> Map.Entry<K, V2> transformEntry(EntryTransformer<? super K, ? super V1, V2> transformer, Map.Entry<K, V1> entry) {
    Preconditions.checkNotNull(transformer);
    Preconditions.checkNotNull(entry);
    return (Map.Entry<K, V2>)new Object(entry, transformer);
  }
  
  static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> asEntryToEntryFunction(EntryTransformer<? super K, ? super V1, V2> transformer) {
    Preconditions.checkNotNull(transformer);
    return (Function<Map.Entry<K, V1>, Map.Entry<K, V2>>)new Object(transformer);
  }
  
  static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> keyPredicate) {
    return Predicates.compose(keyPredicate, keyFunction());
  }
  
  static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> valuePredicate) {
    return Predicates.compose(valuePredicate, valueFunction());
  }
  
  public static <K, V> Map<K, V> filterKeys(Map<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    if (unfiltered instanceof SortedMap)
      return filterKeys((SortedMap<K, V>)unfiltered, keyPredicate); 
    if (unfiltered instanceof BiMap)
      return filterKeys((BiMap<K, V>)unfiltered, keyPredicate); 
    Preconditions.checkNotNull(keyPredicate);
    Predicate<Map.Entry<K, ?>> entryPredicate = keyPredicateOnEntries(keyPredicate);
    return (unfiltered instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap<K, V>)unfiltered, (Predicate)entryPredicate) : (Map<K, V>)new FilteredKeyMap((Map)Preconditions.checkNotNull(unfiltered), keyPredicate, entryPredicate);
  }
  
  public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }
  
  public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    Preconditions.checkNotNull(keyPredicate);
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }
  
  public static <K, V> Map<K, V> filterValues(Map<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    if (unfiltered instanceof SortedMap)
      return filterValues((SortedMap<K, V>)unfiltered, valuePredicate); 
    if (unfiltered instanceof BiMap)
      return filterValues((BiMap<K, V>)unfiltered, valuePredicate); 
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }
  
  public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }
  
  public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }
  
  public static <K, V> Map<K, V> filterEntries(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    if (unfiltered instanceof SortedMap)
      return filterEntries((SortedMap<K, V>)unfiltered, entryPredicate); 
    if (unfiltered instanceof BiMap)
      return filterEntries((BiMap<K, V>)unfiltered, entryPredicate); 
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap<K, V>)unfiltered, entryPredicate) : (Map<K, V>)new FilteredEntryMap((Map)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }
  
  public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    return Platform.mapsFilterSortedMap(unfiltered, entryPredicate);
  }
  
  static <K, V> SortedMap<K, V> filterSortedIgnoreNavigable(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntrySortedMap) ? filterFiltered((FilteredEntrySortedMap<K, V>)unfiltered, entryPredicate) : (SortedMap<K, V>)new FilteredEntrySortedMap((SortedMap)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryNavigableMap) ? filterFiltered((FilteredEntryNavigableMap<K, V>)unfiltered, entryPredicate) : (NavigableMap<K, V>)new FilteredEntryNavigableMap((NavigableMap)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }
  
  public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(unfiltered);
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryBiMap) ? filterFiltered((FilteredEntryBiMap<K, V>)unfiltered, entryPredicate) : (BiMap<K, V>)new FilteredEntryBiMap(unfiltered, entryPredicate);
  }
  
  private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    return (Map<K, V>)new FilteredEntryMap(map.unfiltered, Predicates.and(map.predicate, entryPredicate));
  }
  
  private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
    return (SortedMap<K, V>)new FilteredEntrySortedMap(map.sortedMap(), predicate);
  }
  
  @GwtIncompatible("NavigableMap")
  private static <K, V> NavigableMap<K, V> filterFiltered(FilteredEntryNavigableMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(FilteredEntryNavigableMap.access$600(map), entryPredicate);
    return (NavigableMap<K, V>)new FilteredEntryNavigableMap(FilteredEntryNavigableMap.access$700(map), predicate);
  }
  
  private static <K, V> BiMap<K, V> filterFiltered(FilteredEntryBiMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
    return (BiMap<K, V>)new FilteredEntryBiMap(map.unfiltered(), predicate);
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, V> map) {
    Preconditions.checkNotNull(map);
    if (map instanceof UnmodifiableNavigableMap)
      return map; 
    return (NavigableMap<K, V>)new UnmodifiableNavigableMap(map);
  }
  
  @Nullable
  private static <K, V> Map.Entry<K, V> unmodifiableOrNull(@Nullable Map.Entry<K, V> entry) {
    return (entry == null) ? null : unmodifiableEntry(entry);
  }
  
  @GwtIncompatible("NavigableMap")
  public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
    return Synchronized.navigableMap(navigableMap);
  }
  
  private enum Maps {
  
  }
  
  static class Maps {}
  
  static class Maps {}
  
  static class Maps {}
  
  private static class Maps {}
  
  private static class Maps {}
  
  private static final class Maps {}
  
  static class Maps {}
  
  static class Maps {}
  
  private static final class Maps {}
  
  private static class Maps {}
  
  public static interface Maps {}
  
  static class Maps {}
  
  static class Maps {}
  
  private static class Maps {}
  
  private static abstract class Maps {}
  
  private static final class Maps {}
  
  private static class Maps {}
  
  static class Maps {}
  
  private static class Maps {}
  
  private static class Maps {}
  
  static final class Maps {}
  
  static class Maps {}
  
  @GwtCompatible
  static abstract class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {
    private transient Set<Map.Entry<K, V>> entrySet;
    
    private transient Set<K> keySet;
    
    private transient Collection<V> values;
    
    abstract Set<Map.Entry<K, V>> createEntrySet();
    
    public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> result = this.entrySet;
      return (result == null) ? (this.entrySet = createEntrySet()) : result;
    }
    
    public Set<K> keySet() {
      Set<K> result = this.keySet;
      return (result == null) ? (this.keySet = createKeySet()) : result;
    }
    
    Set<K> createKeySet() {
      return new Maps.KeySet<K, V>(this);
    }
    
    public Collection<V> values() {
      Collection<V> result = this.values;
      return (result == null) ? (this.values = createValues()) : result;
    }
    
    Collection<V> createValues() {
      return (Collection<V>)new Maps.Values(this);
    }
  }
  
  static <V> V safeGet(Map<?, V> map, @Nullable Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException e) {
      return null;
    } catch (NullPointerException e) {
      return null;
    } 
  }
  
  static boolean safeContainsKey(Map<?, ?> map, Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    } 
  }
  
  static <V> V safeRemove(Map<?, V> map, Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException e) {
      return null;
    } catch (NullPointerException e) {
      return null;
    } 
  }
  
  static boolean containsKeyImpl(Map<?, ?> map, @Nullable Object key) {
    return Iterators.contains(keyIterator(map.entrySet().iterator()), key);
  }
  
  static boolean containsValueImpl(Map<?, ?> map, @Nullable Object value) {
    return Iterators.contains(valueIterator(map.entrySet().iterator()), value);
  }
  
  static <K, V> boolean containsEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
    if (!(o instanceof Map.Entry))
      return false; 
    return c.contains(unmodifiableEntry((Map.Entry<?, ?>)o));
  }
  
  static <K, V> boolean removeEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
    if (!(o instanceof Map.Entry))
      return false; 
    return c.remove(unmodifiableEntry((Map.Entry<?, ?>)o));
  }
  
  static boolean equalsImpl(Map<?, ?> map, Object object) {
    if (map == object)
      return true; 
    if (object instanceof Map) {
      Map<?, ?> o = (Map<?, ?>)object;
      return map.entrySet().equals(o.entrySet());
    } 
    return false;
  }
  
  static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");
  
  static String toStringImpl(Map<?, ?> map) {
    StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
    STANDARD_JOINER.appendTo(sb, map);
    return sb.append('}').toString();
  }
  
  static <K, V> void putAllImpl(Map<K, V> self, Map<? extends K, ? extends V> map) {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
      self.put(entry.getKey(), entry.getValue()); 
  }
  
  static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
    final Map<K, V> map;
    
    KeySet(Map<K, V> map) {
      this.map = (Map<K, V>)Preconditions.checkNotNull(map);
    }
    
    Map<K, V> map() {
      return this.map;
    }
    
    public Iterator<K> iterator() {
      return Maps.keyIterator(map().entrySet().iterator());
    }
    
    public int size() {
      return map().size();
    }
    
    public boolean isEmpty() {
      return map().isEmpty();
    }
    
    public boolean contains(Object o) {
      return map().containsKey(o);
    }
    
    public boolean remove(Object o) {
      if (contains(o)) {
        map().remove(o);
        return true;
      } 
      return false;
    }
    
    public void clear() {
      map().clear();
    }
  }
  
  @Nullable
  static <K> K keyOrNull(@Nullable Map.Entry<K, ?> entry) {
    return (entry == null) ? null : entry.getKey();
  }
  
  @Nullable
  static <V> V valueOrNull(@Nullable Map.Entry<?, V> entry) {
    return (entry == null) ? null : entry.getValue();
  }
  
  static class Maps {}
  
  static class Maps {}
  
  static class Maps {}
  
  static abstract class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Map.Entry<K, V>> {
    abstract Map<K, V> map();
    
    public int size() {
      return map().size();
    }
    
    public void clear() {
      map().clear();
    }
    
    public boolean contains(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        Object key = entry.getKey();
        V value = Maps.safeGet(map(), key);
        return (Objects.equal(value, entry.getValue()) && (value != null || map().containsKey(key)));
      } 
      return false;
    }
    
    public boolean isEmpty() {
      return map().isEmpty();
    }
    
    public boolean remove(Object o) {
      if (contains(o)) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        return map().keySet().remove(entry.getKey());
      } 
      return false;
    }
    
    public boolean removeAll(Collection<?> c) {
      try {
        return super.removeAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        return Sets.removeAllImpl(this, c.iterator());
      } 
    }
    
    public boolean retainAll(Collection<?> c) {
      try {
        return super.retainAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
        for (Object o : c) {
          if (contains(o)) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
            keys.add(entry.getKey());
          } 
        } 
        return map().keySet().retainAll(keys);
      } 
    }
  }
  
  static abstract class Maps {}
}
