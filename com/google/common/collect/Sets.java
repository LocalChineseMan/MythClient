package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Sets {
  static class Sets {}
  
  static final class Sets {}
  
  private static final class Sets {}
  
  private static final class Sets {}
  
  private static final class Sets {}
  
  private static class Sets {}
  
  private static class Sets {}
  
  private static class Sets {}
  
  public static abstract class Sets {}
  
  static abstract class ImprovedAbstractSet<E> extends AbstractSet<E> {
    public boolean removeAll(Collection<?> c) {
      return Sets.removeAllImpl(this, c);
    }
    
    public boolean retainAll(Collection<?> c) {
      return super.retainAll((Collection)Preconditions.checkNotNull(c));
    }
  }
  
  @GwtCompatible(serializable = true)
  public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E anElement, E... otherElements) {
    return ImmutableEnumSet.asImmutable(EnumSet.of(anElement, otherElements));
  }
  
  @GwtCompatible(serializable = true)
  public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> elements) {
    if (elements instanceof ImmutableEnumSet)
      return (ImmutableSet<E>)elements; 
    if (elements instanceof Collection) {
      Collection<E> collection = (Collection<E>)elements;
      if (collection.isEmpty())
        return ImmutableSet.of(); 
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
    } 
    Iterator<E> itr = elements.iterator();
    if (itr.hasNext()) {
      EnumSet<E> enumSet = EnumSet.of(itr.next());
      Iterators.addAll(enumSet, itr);
      return ImmutableEnumSet.asImmutable(enumSet);
    } 
    return ImmutableSet.of();
  }
  
  public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
    EnumSet<E> set = EnumSet.noneOf(elementType);
    Iterables.addAll(set, iterable);
    return set;
  }
  
  public static <E> HashSet<E> newHashSet() {
    return new HashSet<E>();
  }
  
  public static <E> HashSet<E> newHashSet(E... elements) {
    HashSet<E> set = newHashSetWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }
  
  public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
    return new HashSet<E>(Maps.capacity(expectedSize));
  }
  
  public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
    return (elements instanceof Collection) ? new HashSet<E>(Collections2.cast(elements)) : newHashSet(elements.iterator());
  }
  
  public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
    HashSet<E> set = newHashSet();
    Iterators.addAll(set, elements);
    return set;
  }
  
  public static <E> Set<E> newConcurrentHashSet() {
    return newSetFromMap(new ConcurrentHashMap<E, Boolean>());
  }
  
  public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> elements) {
    Set<E> set = newConcurrentHashSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSet() {
    return new LinkedHashSet<E>();
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
    return new LinkedHashSet<E>(Maps.capacity(expectedSize));
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements) {
    if (elements instanceof Collection)
      return new LinkedHashSet<E>(Collections2.cast(elements)); 
    LinkedHashSet<E> set = newLinkedHashSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E extends Comparable> TreeSet<E> newTreeSet() {
    return new TreeSet<E>();
  }
  
  public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> elements) {
    TreeSet<E> set = newTreeSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
    return new TreeSet<E>((Comparator<? super E>)Preconditions.checkNotNull(comparator));
  }
  
  public static <E> Set<E> newIdentityHashSet() {
    return newSetFromMap(Maps.newIdentityHashMap());
  }
  
  @GwtIncompatible("CopyOnWriteArraySet")
  public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
    return new CopyOnWriteArraySet<E>();
  }
  
  @GwtIncompatible("CopyOnWriteArraySet")
  public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements) {
    Collection<? extends E> elementsCollection = (elements instanceof Collection) ? Collections2.<E>cast(elements) : Lists.<E>newArrayList(elements);
    return new CopyOnWriteArraySet<E>(elementsCollection);
  }
  
  public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection) {
    if (collection instanceof EnumSet)
      return EnumSet.complementOf((EnumSet<E>)collection); 
    Preconditions.checkArgument(!collection.isEmpty(), "collection is empty; use the other version of this method");
    Class<E> type = ((Enum<E>)collection.iterator().next()).getDeclaringClass();
    return makeComplementByHand(collection, type);
  }
  
  public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection, Class<E> type) {
    Preconditions.checkNotNull(collection);
    return (collection instanceof EnumSet) ? EnumSet.<E>complementOf((EnumSet<E>)collection) : makeComplementByHand(collection, type);
  }
  
  private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> collection, Class<E> type) {
    EnumSet<E> result = EnumSet.allOf(type);
    result.removeAll(collection);
    return result;
  }
  
  public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
    return Platform.newSetFromMap(map);
  }
  
  public static <E> SetView<E> union(Set<? extends E> set1, Set<? extends E> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    SetView<? extends E> setView = difference(set2, set1);
    return (SetView<E>)new Object(set1, (Set)setView, set2);
  }
  
  public static <E> SetView<E> intersection(Set<E> set1, Set<?> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    Predicate<Object> inSet2 = Predicates.in(set2);
    return (SetView<E>)new Object(set1, inSet2, set2);
  }
  
  public static <E> SetView<E> difference(Set<E> set1, Set<?> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    Predicate<Object> notInSet2 = Predicates.not(Predicates.in(set2));
    return (SetView<E>)new Object(set1, notInSet2, set2);
  }
  
  public static <E> SetView<E> symmetricDifference(Set<? extends E> set1, Set<? extends E> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    return difference((Set<E>)union(set1, set2), (Set<?>)intersection(set1, set2));
  }
  
  public static <E> Set<E> filter(Set<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof SortedSet)
      return filter((SortedSet<E>)unfiltered, predicate); 
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return (Set<E>)new FilteredSet((Set)filtered.unfiltered, combinedPredicate);
    } 
    return (Set<E>)new FilteredSet((Set)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
  }
  
  public static <E> SortedSet<E> filter(SortedSet<E> unfiltered, Predicate<? super E> predicate) {
    return Platform.setsFilterSortedSet(unfiltered, predicate);
  }
  
  static <E> SortedSet<E> filterSortedIgnoreNavigable(SortedSet<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return (SortedSet<E>)new FilteredSortedSet((SortedSet)filtered.unfiltered, combinedPredicate);
    } 
    return (SortedSet<E>)new FilteredSortedSet((SortedSet)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
  }
  
  @GwtIncompatible("NavigableSet")
  public static <E> NavigableSet<E> filter(NavigableSet<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return (NavigableSet<E>)new FilteredNavigableSet((NavigableSet)filtered.unfiltered, combinedPredicate);
    } 
    return (NavigableSet<E>)new FilteredNavigableSet((NavigableSet)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
  }
  
  public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> sets) {
    return CartesianSet.create(sets);
  }
  
  public static <B> Set<List<B>> cartesianProduct(Set<? extends B>... sets) {
    return cartesianProduct(Arrays.asList(sets));
  }
  
  @GwtCompatible(serializable = false)
  public static <E> Set<Set<E>> powerSet(Set<E> set) {
    return (Set<Set<E>>)new PowerSet(set);
  }
  
  static int hashCodeImpl(Set<?> s) {
    int hashCode = 0;
    for (Object o : s) {
      hashCode += (o != null) ? o.hashCode() : 0;
      hashCode = hashCode ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    } 
    return hashCode;
  }
  
  static boolean equalsImpl(Set<?> s, @Nullable Object object) {
    if (s == object)
      return true; 
    if (object instanceof Set) {
      Set<?> o = (Set)object;
      try {
        return (s.size() == o.size() && s.containsAll(o));
      } catch (NullPointerException ignored) {
        return false;
      } catch (ClassCastException ignored) {
        return false;
      } 
    } 
    return false;
  }
  
  @GwtIncompatible("NavigableSet")
  public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
    if (set instanceof ImmutableSortedSet || set instanceof UnmodifiableNavigableSet)
      return set; 
    return (NavigableSet<E>)new UnmodifiableNavigableSet(set);
  }
  
  @GwtIncompatible("NavigableSet")
  public static <E> NavigableSet<E> synchronizedNavigableSet(NavigableSet<E> navigableSet) {
    return Synchronized.navigableSet(navigableSet);
  }
  
  static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
    boolean changed = false;
    while (iterator.hasNext())
      changed |= set.remove(iterator.next()); 
    return changed;
  }
  
  static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
    Preconditions.checkNotNull(collection);
    if (collection instanceof Multiset)
      collection = ((Multiset)collection).elementSet(); 
    if (collection instanceof Set && collection.size() > set.size())
      return Iterators.removeAll(set.iterator(), collection); 
    return removeAllImpl(set, collection.iterator());
  }
}
