package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, Serializable {
  private final Comparator<? super K> comparator;
  
  private transient Entry<K, V> root;
  
  private transient int size = 0;
  
  private transient int modCount = 0;
  
  private transient EntrySet entrySet;
  
  private transient KeySet<K> navigableKeySet;
  
  private transient NavigableMap<K, V> descendingMap;
  
  public TreeMap() {
    this.comparator = null;
  }
  
  public TreeMap(Comparator<? super K> paramComparator) {
    this.comparator = paramComparator;
  }
  
  public TreeMap(Map<? extends K, ? extends V> paramMap) {
    this.comparator = null;
    putAll(paramMap);
  }
  
  public TreeMap(SortedMap<K, ? extends V> paramSortedMap) {
    this.comparator = paramSortedMap.comparator();
    try {
      buildFromSorted(paramSortedMap.size(), paramSortedMap.entrySet().iterator(), (ObjectInputStream)null, (V)null);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean containsKey(Object paramObject) {
    return (getEntry(paramObject) != null);
  }
  
  public boolean containsValue(Object paramObject) {
    for (Entry<K, V> entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      if (valEquals(paramObject, entry.value))
        return true; 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    Entry<K, V> entry = getEntry(paramObject);
    return (entry == null) ? null : entry.value;
  }
  
  public Comparator<? super K> comparator() {
    return this.comparator;
  }
  
  public K firstKey() {
    return key(getFirstEntry());
  }
  
  public K lastKey() {
    return key(getLastEntry());
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    int i = paramMap.size();
    if (this.size == 0 && i != 0 && paramMap instanceof SortedMap) {
      Comparator<? super K> comparator = ((SortedMap)paramMap).comparator();
      if (comparator == this.comparator || (comparator != null && comparator.equals(this.comparator))) {
        this.modCount++;
        try {
          buildFromSorted(i, paramMap.entrySet().iterator(), (ObjectInputStream)null, (V)null);
        } catch (IOException iOException) {
        
        } catch (ClassNotFoundException classNotFoundException) {}
        return;
      } 
    } 
    super.putAll(paramMap);
  }
  
  final Entry<K, V> getEntry(Object paramObject) {
    if (this.comparator != null)
      return getEntryUsingComparator(paramObject); 
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparable<K> comparable = (Comparable)paramObject;
    Entry<K, V> entry = this.root;
    while (entry != null) {
      int i = comparable.compareTo(entry.key);
      if (i < 0) {
        entry = entry.left;
        continue;
      } 
      if (i > 0) {
        entry = entry.right;
        continue;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getEntryUsingComparator(Object paramObject) {
    Object object = paramObject;
    Comparator<? super K> comparator = this.comparator;
    if (comparator != null) {
      Entry<K, V> entry = this.root;
      while (entry != null) {
        int i = comparator.compare((K)object, entry.key);
        if (i < 0) {
          entry = entry.left;
          continue;
        } 
        if (i > 0) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
    } 
    return null;
  }
  
  final Entry<K, V> getCeilingEntry(K paramK) {
    Entry<K, V> entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        return entry;
      } 
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        Entry<K, V> entry1 = entry.parent;
        Entry<K, V> entry2 = entry;
        while (entry1 != null && entry2 == entry1.right) {
          entry2 = entry1;
          entry1 = entry1.parent;
        } 
        return entry1;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getFloorEntry(K paramK) {
    Entry<K, V> entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        Entry<K, V> entry1 = entry.parent;
        Entry<K, V> entry2 = entry;
        while (entry1 != null && entry2 == entry1.left) {
          entry2 = entry1;
          entry1 = entry1.parent;
        } 
        return entry1;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getHigherEntry(K paramK) {
    Entry<K, V> entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        return entry;
      } 
      if (entry.right != null) {
        entry = entry.right;
        continue;
      } 
      Entry<K, V> entry1 = entry.parent;
      Entry<K, V> entry2 = entry;
      while (entry1 != null && entry2 == entry1.right) {
        entry2 = entry1;
        entry1 = entry1.parent;
      } 
      return entry1;
    } 
    return null;
  }
  
  final Entry<K, V> getLowerEntry(K paramK) {
    Entry<K, V> entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
      if (entry.left != null) {
        entry = entry.left;
        continue;
      } 
      Entry<K, V> entry1 = entry.parent;
      Entry<K, V> entry2 = entry;
      while (entry1 != null && entry2 == entry1.left) {
        entry2 = entry1;
        entry1 = entry1.parent;
      } 
      return entry1;
    } 
    return null;
  }
  
  public V put(K paramK, V paramV) {
    int i;
    Entry<K, V> entry2, entry1 = this.root;
    if (entry1 == null) {
      compare(paramK, paramK);
      this.root = new Entry<>(paramK, paramV, null);
      this.size = 1;
      this.modCount++;
      return null;
    } 
    Comparator<? super K> comparator = this.comparator;
    if (comparator != null) {
      do {
        entry2 = entry1;
        i = comparator.compare(paramK, entry1.key);
        if (i < 0) {
          entry1 = entry1.left;
        } else if (i > 0) {
          entry1 = entry1.right;
        } else {
          return entry1.setValue(paramV);
        } 
      } while (entry1 != null);
    } else {
      if (paramK == null)
        throw new NullPointerException(); 
      Comparable<K> comparable = (Comparable)paramK;
      do {
        entry2 = entry1;
        i = comparable.compareTo(entry1.key);
        if (i < 0) {
          entry1 = entry1.left;
        } else if (i > 0) {
          entry1 = entry1.right;
        } else {
          return entry1.setValue(paramV);
        } 
      } while (entry1 != null);
    } 
    Entry<K, V> entry3 = new Entry<>(paramK, paramV, entry2);
    if (i < 0) {
      entry2.left = entry3;
    } else {
      entry2.right = entry3;
    } 
    fixAfterInsertion(entry3);
    this.size++;
    this.modCount++;
    return null;
  }
  
  public V remove(Object paramObject) {
    Entry<K, V> entry = getEntry(paramObject);
    if (entry == null)
      return null; 
    V v = entry.value;
    deleteEntry(entry);
    return v;
  }
  
  public void clear() {
    this.modCount++;
    this.size = 0;
    this.root = null;
  }
  
  public Object clone() {
    TreeMap treeMap;
    try {
      treeMap = (TreeMap)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    treeMap.root = null;
    treeMap.size = 0;
    treeMap.modCount = 0;
    treeMap.entrySet = null;
    treeMap.navigableKeySet = null;
    treeMap.descendingMap = null;
    try {
      treeMap.buildFromSorted(this.size, entrySet().iterator(), (ObjectInputStream)null, (V)null);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
    return treeMap;
  }
  
  public Map.Entry<K, V> firstEntry() {
    return exportEntry(getFirstEntry());
  }
  
  public Map.Entry<K, V> lastEntry() {
    return exportEntry(getLastEntry());
  }
  
  public Map.Entry<K, V> pollFirstEntry() {
    Entry<K, V> entry = getFirstEntry();
    Map.Entry<K, V> entry1 = exportEntry(entry);
    if (entry != null)
      deleteEntry(entry); 
    return entry1;
  }
  
  public Map.Entry<K, V> pollLastEntry() {
    Entry<K, V> entry = getLastEntry();
    Map.Entry<K, V> entry1 = exportEntry(entry);
    if (entry != null)
      deleteEntry(entry); 
    return entry1;
  }
  
  public Map.Entry<K, V> lowerEntry(K paramK) {
    return exportEntry(getLowerEntry(paramK));
  }
  
  public K lowerKey(K paramK) {
    return keyOrNull(getLowerEntry(paramK));
  }
  
  public Map.Entry<K, V> floorEntry(K paramK) {
    return exportEntry(getFloorEntry(paramK));
  }
  
  public K floorKey(K paramK) {
    return keyOrNull(getFloorEntry(paramK));
  }
  
  public Map.Entry<K, V> ceilingEntry(K paramK) {
    return exportEntry(getCeilingEntry(paramK));
  }
  
  public K ceilingKey(K paramK) {
    return keyOrNull(getCeilingEntry(paramK));
  }
  
  public Map.Entry<K, V> higherEntry(K paramK) {
    return exportEntry(getHigherEntry(paramK));
  }
  
  public K higherKey(K paramK) {
    return keyOrNull(getHigherEntry(paramK));
  }
  
  public Set<K> keySet() {
    return navigableKeySet();
  }
  
  public NavigableSet<K> navigableKeySet() {
    KeySet<K> keySet = this.navigableKeySet;
    return (keySet != null) ? keySet : (this.navigableKeySet = new KeySet<>((NavigableMap<K, ?>)this));
  }
  
  public NavigableSet<K> descendingKeySet() {
    return descendingMap().navigableKeySet();
  }
  
  public Collection<V> values() {
    Collection<V> collection = this.values;
    return (collection != null) ? collection : (this.values = new Values());
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySet entrySet = this.entrySet;
    return (entrySet != null) ? entrySet : (this.entrySet = new EntrySet());
  }
  
  public NavigableMap<K, V> descendingMap() {
    NavigableMap<K, V> navigableMap = this.descendingMap;
    return (navigableMap != null) ? navigableMap : (this.descendingMap = (NavigableMap<K, V>)new DescendingSubMap(this, true, null, true, true, null, true));
  }
  
  public NavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2) {
    return (NavigableMap)new AscendingSubMap<>(this, false, paramK1, paramBoolean1, false, paramK2, paramBoolean2);
  }
  
  public NavigableMap<K, V> headMap(K paramK, boolean paramBoolean) {
    return (NavigableMap)new AscendingSubMap<>(this, true, null, true, false, paramK, paramBoolean);
  }
  
  public NavigableMap<K, V> tailMap(K paramK, boolean paramBoolean) {
    return (NavigableMap)new AscendingSubMap<>(this, false, paramK, paramBoolean, true, null, true);
  }
  
  public SortedMap<K, V> subMap(K paramK1, K paramK2) {
    return subMap(paramK1, true, paramK2, false);
  }
  
  public SortedMap<K, V> headMap(K paramK) {
    return headMap(paramK, false);
  }
  
  public SortedMap<K, V> tailMap(K paramK) {
    return tailMap(paramK, true);
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    Entry<K, V> entry = getEntry(paramK);
    if (entry != null && Objects.equals(paramV1, entry.value)) {
      entry.value = paramV2;
      return true;
    } 
    return false;
  }
  
  public V replace(K paramK, V paramV) {
    Entry<K, V> entry = getEntry(paramK);
    if (entry != null) {
      V v = entry.value;
      entry.value = paramV;
      return v;
    } 
    return null;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    for (Entry<K, V> entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      paramBiConsumer.accept(entry.key, entry.value);
      if (i != this.modCount)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    for (Entry<K, V> entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      entry.value = paramBiFunction.apply(entry.key, entry.value);
      if (i != this.modCount)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  class Values extends AbstractCollection<V> {
    public Iterator<V> iterator() {
      return new TreeMap.ValueIterator(TreeMap.this.getFirstEntry());
    }
    
    public int size() {
      return TreeMap.this.size();
    }
    
    public boolean contains(Object param1Object) {
      return TreeMap.this.containsValue(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      for (TreeMap.Entry<K, V> entry = TreeMap.this.getFirstEntry(); entry != null; entry = TreeMap.successor(entry)) {
        if (TreeMap.valEquals(entry.getValue(), param1Object)) {
          TreeMap.this.deleteEntry(entry);
          return true;
        } 
      } 
      return false;
    }
    
    public void clear() {
      TreeMap.this.clear();
    }
    
    public Spliterator<V> spliterator() {
      return new TreeMap.ValueSpliterator<>(TreeMap.this, null, null, 0, -1, 0);
    }
  }
  
  class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    public Iterator<Map.Entry<K, V>> iterator() {
      return new TreeMap.EntryIterator(TreeMap.this.getFirstEntry());
    }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getValue();
      TreeMap.Entry<K, V> entry1 = TreeMap.this.getEntry(entry.getKey());
      return (entry1 != null && TreeMap.valEquals(entry1.getValue(), object));
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getValue();
      TreeMap.Entry<K, V> entry1 = TreeMap.this.getEntry(entry.getKey());
      if (entry1 != null && TreeMap.valEquals(entry1.getValue(), object)) {
        TreeMap.this.deleteEntry(entry1);
        return true;
      } 
      return false;
    }
    
    public int size() {
      return TreeMap.this.size();
    }
    
    public void clear() {
      TreeMap.this.clear();
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator() {
      return new TreeMap.EntrySpliterator<>(TreeMap.this, null, null, 0, -1, 0);
    }
  }
  
  Iterator<K> keyIterator() {
    return new KeyIterator(getFirstEntry());
  }
  
  Iterator<K> descendingKeyIterator() {
    return new DescendingKeyIterator(this, getLastEntry());
  }
  
  static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final NavigableMap<E, ?> m;
    
    KeySet(NavigableMap<E, ?> param1NavigableMap) {
      this.m = param1NavigableMap;
    }
    
    public Iterator<E> iterator() {
      if (this.m instanceof TreeMap)
        return ((TreeMap)this.m).keyIterator(); 
      return ((TreeMap.NavigableSubMap)this.m).keyIterator();
    }
    
    public Iterator<E> descendingIterator() {
      if (this.m instanceof TreeMap)
        return ((TreeMap)this.m).descendingKeyIterator(); 
      return ((TreeMap.NavigableSubMap)this.m).descendingKeyIterator();
    }
    
    public int size() {
      return this.m.size();
    }
    
    public boolean isEmpty() {
      return this.m.isEmpty();
    }
    
    public boolean contains(Object param1Object) {
      return this.m.containsKey(param1Object);
    }
    
    public void clear() {
      this.m.clear();
    }
    
    public E lower(E param1E) {
      return this.m.lowerKey(param1E);
    }
    
    public E floor(E param1E) {
      return this.m.floorKey(param1E);
    }
    
    public E ceiling(E param1E) {
      return this.m.ceilingKey(param1E);
    }
    
    public E higher(E param1E) {
      return this.m.higherKey(param1E);
    }
    
    public E first() {
      return this.m.firstKey();
    }
    
    public E last() {
      return this.m.lastKey();
    }
    
    public Comparator<? super E> comparator() {
      return this.m.comparator();
    }
    
    public E pollFirst() {
      Map.Entry<E, ?> entry = this.m.pollFirstEntry();
      return (entry == null) ? null : entry.getKey();
    }
    
    public E pollLast() {
      Map.Entry<E, ?> entry = this.m.pollLastEntry();
      return (entry == null) ? null : entry.getKey();
    }
    
    public boolean remove(Object param1Object) {
      int i = size();
      this.m.remove(param1Object);
      return (size() != i);
    }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) {
      return new KeySet(this.m.subMap(param1E1, param1Boolean1, param1E2, param1Boolean2));
    }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) {
      return new KeySet(this.m.headMap(param1E, param1Boolean));
    }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) {
      return new KeySet(this.m.tailMap(param1E, param1Boolean));
    }
    
    public SortedSet<E> subSet(E param1E1, E param1E2) {
      return subSet(param1E1, true, param1E2, false);
    }
    
    public SortedSet<E> headSet(E param1E) {
      return headSet(param1E, false);
    }
    
    public SortedSet<E> tailSet(E param1E) {
      return tailSet(param1E, true);
    }
    
    public NavigableSet<E> descendingSet() {
      return new KeySet(this.m.descendingMap());
    }
    
    public Spliterator<E> spliterator() {
      return TreeMap.keySpliteratorFor(this.m);
    }
  }
  
  abstract class PrivateEntryIterator<T> implements Iterator<T> {
    TreeMap.Entry<K, V> next;
    
    TreeMap.Entry<K, V> lastReturned;
    
    int expectedModCount;
    
    PrivateEntryIterator(TreeMap.Entry<K, V> param1Entry) {
      this.expectedModCount = TreeMap.this.modCount;
      this.lastReturned = null;
      this.next = param1Entry;
    }
    
    public final boolean hasNext() {
      return (this.next != null);
    }
    
    final TreeMap.Entry<K, V> nextEntry() {
      TreeMap.Entry<K, V> entry = this.next;
      if (entry == null)
        throw new NoSuchElementException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.next = TreeMap.successor(entry);
      this.lastReturned = entry;
      return entry;
    }
    
    final TreeMap.Entry<K, V> prevEntry() {
      TreeMap.Entry<K, V> entry = this.next;
      if (entry == null)
        throw new NoSuchElementException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.next = TreeMap.predecessor(entry);
      this.lastReturned = entry;
      return entry;
    }
    
    public void remove() {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (this.lastReturned.left != null && this.lastReturned.right != null)
        this.next = this.lastReturned; 
      TreeMap.this.deleteEntry(this.lastReturned);
      this.expectedModCount = TreeMap.this.modCount;
      this.lastReturned = null;
    }
  }
  
  final class EntryIterator extends PrivateEntryIterator<Map.Entry<K, V>> {
    EntryIterator(TreeMap.Entry<K, V> param1Entry) {
      super(param1Entry);
    }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  
  final class ValueIterator extends PrivateEntryIterator<V> {
    ValueIterator(TreeMap.Entry<K, V> param1Entry) {
      super(param1Entry);
    }
    
    public V next() {
      return (nextEntry()).value;
    }
  }
  
  final class KeyIterator extends PrivateEntryIterator<K> {
    KeyIterator(TreeMap.Entry<K, V> param1Entry) {
      super(param1Entry);
    }
    
    public K next() {
      return (nextEntry()).key;
    }
  }
  
  final int compare(Object paramObject1, Object paramObject2) {
    return (this.comparator == null) ? ((Comparable<Object>)paramObject1).compareTo(paramObject2) : this.comparator
      .compare((K)paramObject1, (K)paramObject2);
  }
  
  static final boolean valEquals(Object paramObject1, Object paramObject2) {
    return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2);
  }
  
  static <K, V> Map.Entry<K, V> exportEntry(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? null : new AbstractMap.SimpleImmutableEntry<>(paramEntry);
  }
  
  static <K, V> K keyOrNull(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? null : paramEntry.key;
  }
  
  static <K> K key(Entry<K, ?> paramEntry) {
    if (paramEntry == null)
      throw new NoSuchElementException(); 
    return paramEntry.key;
  }
  
  private static final Object UNBOUNDED = new Object();
  
  private static final boolean RED = false;
  
  private static final boolean BLACK = true;
  
  private static final long serialVersionUID = 919286545866124006L;
  
  final class TreeMap {}
  
  static abstract class NavigableSubMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Serializable {
    private static final long serialVersionUID = -2102997345730753016L;
    
    final TreeMap<K, V> m;
    
    final K lo;
    
    final K hi;
    
    final boolean fromStart;
    
    final boolean toEnd;
    
    final boolean loInclusive;
    
    final boolean hiInclusive;
    
    transient NavigableMap<K, V> descendingMapView;
    
    transient EntrySetView entrySetView;
    
    transient TreeMap.KeySet<K> navigableKeySetView;
    
    NavigableSubMap(TreeMap<K, V> param1TreeMap, boolean param1Boolean1, K param1K1, boolean param1Boolean2, boolean param1Boolean3, K param1K2, boolean param1Boolean4) {
      if (!param1Boolean1 && !param1Boolean3) {
        if (param1TreeMap.compare(param1K1, param1K2) > 0)
          throw new IllegalArgumentException("fromKey > toKey"); 
      } else {
        if (!param1Boolean1)
          param1TreeMap.compare(param1K1, param1K1); 
        if (!param1Boolean3)
          param1TreeMap.compare(param1K2, param1K2); 
      } 
      this.m = param1TreeMap;
      this.fromStart = param1Boolean1;
      this.lo = param1K1;
      this.loInclusive = param1Boolean2;
      this.toEnd = param1Boolean3;
      this.hi = param1K2;
      this.hiInclusive = param1Boolean4;
    }
    
    final boolean tooLow(Object param1Object) {
      if (!this.fromStart) {
        int i = this.m.compare(param1Object, this.lo);
        if (i < 0 || (i == 0 && !this.loInclusive))
          return true; 
      } 
      return false;
    }
    
    final boolean tooHigh(Object param1Object) {
      if (!this.toEnd) {
        int i = this.m.compare(param1Object, this.hi);
        if (i > 0 || (i == 0 && !this.hiInclusive))
          return true; 
      } 
      return false;
    }
    
    final boolean inRange(Object param1Object) {
      return (!tooLow(param1Object) && !tooHigh(param1Object));
    }
    
    final boolean inClosedRange(Object param1Object) {
      return ((this.fromStart || this.m.compare(param1Object, this.lo) >= 0) && (this.toEnd || this.m
        .compare(this.hi, param1Object) >= 0));
    }
    
    final boolean inRange(Object param1Object, boolean param1Boolean) {
      return param1Boolean ? inRange(param1Object) : inClosedRange(param1Object);
    }
    
    final TreeMap.Entry<K, V> absLowest() {
      TreeMap.Entry<K, V> entry = this.fromStart ? this.m.getFirstEntry() : (this.loInclusive ? this.m.getCeilingEntry(this.lo) : this.m.getHigherEntry(this.lo));
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHighest() {
      TreeMap.Entry<K, V> entry = this.toEnd ? this.m.getLastEntry() : (this.hiInclusive ? this.m.getFloorEntry(this.hi) : this.m.getLowerEntry(this.hi));
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absCeiling(K param1K) {
      if (tooLow(param1K))
        return absLowest(); 
      TreeMap.Entry<K, V> entry = this.m.getCeilingEntry(param1K);
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHigher(K param1K) {
      if (tooLow(param1K))
        return absLowest(); 
      TreeMap.Entry<K, V> entry = this.m.getHigherEntry(param1K);
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absFloor(K param1K) {
      if (tooHigh(param1K))
        return absHighest(); 
      TreeMap.Entry<K, V> entry = this.m.getFloorEntry(param1K);
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absLower(K param1K) {
      if (tooHigh(param1K))
        return absHighest(); 
      TreeMap.Entry<K, V> entry = this.m.getLowerEntry(param1K);
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHighFence() {
      return this.toEnd ? null : (this.hiInclusive ? this.m
        .getHigherEntry(this.hi) : this.m
        .getCeilingEntry(this.hi));
    }
    
    final TreeMap.Entry<K, V> absLowFence() {
      return this.fromStart ? null : (this.loInclusive ? this.m
        .getLowerEntry(this.lo) : this.m
        .getFloorEntry(this.lo));
    }
    
    abstract TreeMap.Entry<K, V> subLowest();
    
    abstract TreeMap.Entry<K, V> subHighest();
    
    abstract TreeMap.Entry<K, V> subCeiling(K param1K);
    
    abstract TreeMap.Entry<K, V> subHigher(K param1K);
    
    abstract TreeMap.Entry<K, V> subFloor(K param1K);
    
    abstract TreeMap.Entry<K, V> subLower(K param1K);
    
    abstract Iterator<K> keyIterator();
    
    abstract Spliterator<K> keySpliterator();
    
    abstract Iterator<K> descendingKeyIterator();
    
    public boolean isEmpty() {
      return (this.fromStart && this.toEnd) ? this.m.isEmpty() : entrySet().isEmpty();
    }
    
    public int size() {
      return (this.fromStart && this.toEnd) ? this.m.size() : entrySet().size();
    }
    
    public final boolean containsKey(Object param1Object) {
      return (inRange(param1Object) && this.m.containsKey(param1Object));
    }
    
    public final V put(K param1K, V param1V) {
      if (!inRange(param1K))
        throw new IllegalArgumentException("key out of range"); 
      return this.m.put(param1K, param1V);
    }
    
    public final V get(Object param1Object) {
      return !inRange(param1Object) ? null : this.m.get(param1Object);
    }
    
    public final V remove(Object param1Object) {
      return !inRange(param1Object) ? null : this.m.remove(param1Object);
    }
    
    public final Map.Entry<K, V> ceilingEntry(K param1K) {
      return TreeMap.exportEntry(subCeiling(param1K));
    }
    
    public final K ceilingKey(K param1K) {
      return TreeMap.keyOrNull(subCeiling(param1K));
    }
    
    public final Map.Entry<K, V> higherEntry(K param1K) {
      return TreeMap.exportEntry(subHigher(param1K));
    }
    
    public final K higherKey(K param1K) {
      return TreeMap.keyOrNull(subHigher(param1K));
    }
    
    public final Map.Entry<K, V> floorEntry(K param1K) {
      return TreeMap.exportEntry(subFloor(param1K));
    }
    
    public final K floorKey(K param1K) {
      return TreeMap.keyOrNull(subFloor(param1K));
    }
    
    public final Map.Entry<K, V> lowerEntry(K param1K) {
      return TreeMap.exportEntry(subLower(param1K));
    }
    
    public final K lowerKey(K param1K) {
      return TreeMap.keyOrNull(subLower(param1K));
    }
    
    public final K firstKey() {
      return TreeMap.key(subLowest());
    }
    
    public final K lastKey() {
      return TreeMap.key(subHighest());
    }
    
    public final Map.Entry<K, V> firstEntry() {
      return TreeMap.exportEntry(subLowest());
    }
    
    public final Map.Entry<K, V> lastEntry() {
      return TreeMap.exportEntry(subHighest());
    }
    
    public final Map.Entry<K, V> pollFirstEntry() {
      TreeMap.Entry<K, V> entry = subLowest();
      Map.Entry<K, V> entry1 = TreeMap.exportEntry(entry);
      if (entry != null)
        this.m.deleteEntry(entry); 
      return entry1;
    }
    
    public final Map.Entry<K, V> pollLastEntry() {
      TreeMap.Entry<K, V> entry = subHighest();
      Map.Entry<K, V> entry1 = TreeMap.exportEntry(entry);
      if (entry != null)
        this.m.deleteEntry(entry); 
      return entry1;
    }
    
    public final NavigableSet<K> navigableKeySet() {
      TreeMap.KeySet<K> keySet = this.navigableKeySetView;
      return (keySet != null) ? keySet : (this.navigableKeySetView = new TreeMap.KeySet<>((NavigableMap<K, ?>)this));
    }
    
    public final Set<K> keySet() {
      return navigableKeySet();
    }
    
    public NavigableSet<K> descendingKeySet() {
      return descendingMap().navigableKeySet();
    }
    
    public final SortedMap<K, V> subMap(K param1K1, K param1K2) {
      return subMap(param1K1, true, param1K2, false);
    }
    
    public final SortedMap<K, V> headMap(K param1K) {
      return headMap(param1K, false);
    }
    
    public final SortedMap<K, V> tailMap(K param1K) {
      return tailMap(param1K, true);
    }
    
    abstract class NavigableSubMap {}
    
    abstract class SubMapIterator<T> implements Iterator<T> {
      TreeMap.Entry<K, V> lastReturned;
      
      TreeMap.Entry<K, V> next;
      
      final Object fenceKey;
      
      int expectedModCount;
      
      SubMapIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) {
        this.expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
        this.lastReturned = null;
        this.next = param2Entry1;
        this.fenceKey = (param2Entry2 == null) ? TreeMap.UNBOUNDED : param2Entry2.key;
      }
      
      public final boolean hasNext() {
        return (this.next != null && this.next.key != this.fenceKey);
      }
      
      final TreeMap.Entry<K, V> nextEntry() {
        TreeMap.Entry<K, V> entry = this.next;
        if (entry == null || entry.key == this.fenceKey)
          throw new NoSuchElementException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        this.next = TreeMap.successor(entry);
        this.lastReturned = entry;
        return entry;
      }
      
      final TreeMap.Entry<K, V> prevEntry() {
        TreeMap.Entry<K, V> entry = this.next;
        if (entry == null || entry.key == this.fenceKey)
          throw new NoSuchElementException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        this.next = TreeMap.predecessor(entry);
        this.lastReturned = entry;
        return entry;
      }
      
      final void removeAscending() {
        if (this.lastReturned == null)
          throw new IllegalStateException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        if (this.lastReturned.left != null && this.lastReturned.right != null)
          this.next = this.lastReturned; 
        TreeMap.NavigableSubMap.this.m.deleteEntry(this.lastReturned);
        this.lastReturned = null;
        this.expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
      }
      
      final void removeDescending() {
        if (this.lastReturned == null)
          throw new IllegalStateException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        TreeMap.NavigableSubMap.this.m.deleteEntry(this.lastReturned);
        this.lastReturned = null;
        this.expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
      }
    }
    
    final class NavigableSubMap {}
    
    final class NavigableSubMap {}
    
    final class NavigableSubMap {}
    
    final class DescendingSubMapKeyIterator extends SubMapIterator<K> implements Spliterator<K> {
      DescendingSubMapKeyIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) {
        super(param2Entry1, param2Entry2);
      }
      
      public K next() {
        return (prevEntry()).key;
      }
      
      public void remove() {
        removeDescending();
      }
      
      public Spliterator<K> trySplit() {
        return null;
      }
      
      public void forEachRemaining(Consumer<? super K> param2Consumer) {
        while (hasNext())
          param2Consumer.accept(next()); 
      }
      
      public boolean tryAdvance(Consumer<? super K> param2Consumer) {
        if (hasNext()) {
          param2Consumer.accept(next());
          return true;
        } 
        return false;
      }
      
      public long estimateSize() {
        return Long.MAX_VALUE;
      }
      
      public int characteristics() {
        return 17;
      }
    }
  }
  
  static final class TreeMap {}
  
  static final class DescendingSubMap<K, V> extends NavigableSubMap<K, V> {
    private static final long serialVersionUID = 912986545866120460L;
    
    private final Comparator<? super K> reverseComparator;
    
    DescendingSubMap(TreeMap<K, V> param1TreeMap, boolean param1Boolean1, K param1K1, boolean param1Boolean2, boolean param1Boolean3, K param1K2, boolean param1Boolean4) {
      super(param1TreeMap, param1Boolean1, param1K1, param1Boolean2, param1Boolean3, param1K2, param1Boolean4);
      this
        .reverseComparator = Collections.reverseOrder(this.m.comparator);
    }
    
    public Comparator<? super K> comparator() {
      return this.reverseComparator;
    }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      if (!inRange(param1K1, param1Boolean1))
        throw new IllegalArgumentException("fromKey out of range"); 
      if (!inRange(param1K2, param1Boolean2))
        throw new IllegalArgumentException("toKey out of range"); 
      return (NavigableMap<K, V>)new DescendingSubMap(this.m, false, param1K2, param1Boolean2, false, param1K1, param1Boolean1);
    }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("toKey out of range"); 
      return (NavigableMap<K, V>)new DescendingSubMap(this.m, false, param1K, param1Boolean, this.toEnd, (K)this.hi, this.hiInclusive);
    }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("fromKey out of range"); 
      return (NavigableMap<K, V>)new DescendingSubMap(this.m, this.fromStart, (K)this.lo, this.loInclusive, false, param1K, param1Boolean);
    }
    
    public NavigableMap<K, V> descendingMap() {
      NavigableMap<K, V> navigableMap = this.descendingMapView;
      return (navigableMap != null) ? navigableMap : (this.descendingMapView = (NavigableMap)new TreeMap.AscendingSubMap<>(this.m, this.fromStart, this.lo, this.loInclusive, this.toEnd, this.hi, this.hiInclusive));
    }
    
    Iterator<K> keyIterator() {
      return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator((TreeMap.NavigableSubMap)this, absHighest(), absLowFence());
    }
    
    Spliterator<K> keySpliterator() {
      return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator((TreeMap.NavigableSubMap)this, absHighest(), absLowFence());
    }
    
    Iterator<K> descendingKeyIterator() {
      return new TreeMap.NavigableSubMap.SubMapKeyIterator((TreeMap.NavigableSubMap)this, absLowest(), absHighFence());
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      TreeMap.NavigableSubMap.EntrySetView entrySetView = this.entrySetView;
      return (entrySetView != null) ? entrySetView : (this.entrySetView = new DescendingEntrySetView(this));
    }
    
    TreeMap.Entry<K, V> subLowest() {
      return absHighest();
    }
    
    TreeMap.Entry<K, V> subHighest() {
      return absLowest();
    }
    
    TreeMap.Entry<K, V> subCeiling(K param1K) {
      return absFloor(param1K);
    }
    
    TreeMap.Entry<K, V> subHigher(K param1K) {
      return absLower(param1K);
    }
    
    TreeMap.Entry<K, V> subFloor(K param1K) {
      return absCeiling(param1K);
    }
    
    TreeMap.Entry<K, V> subLower(K param1K) {
      return absHigher(param1K);
    }
    
    final class DescendingSubMap {}
  }
  
  private class TreeMap {}
  
  static final class Entry<K, V> implements Map.Entry<K, V> {
    K key;
    
    V value;
    
    Entry<K, V> left;
    
    Entry<K, V> right;
    
    Entry<K, V> parent;
    
    boolean color = true;
    
    Entry(K param1K, V param1V, Entry<K, V> param1Entry) {
      this.key = param1K;
      this.value = param1V;
      this.parent = param1Entry;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public V setValue(V param1V) {
      V v = this.value;
      this.value = param1V;
      return v;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (TreeMap.valEquals(this.key, entry.getKey()) && TreeMap.valEquals(this.value, entry.getValue()));
    }
    
    public int hashCode() {
      boolean bool1 = (this.key == null) ? false : this.key.hashCode();
      boolean bool2 = (this.value == null) ? false : this.value.hashCode();
      return bool1 ^ bool2;
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
    }
  }
  
  final Entry<K, V> getFirstEntry() {
    Entry<K, V> entry = this.root;
    if (entry != null)
      while (entry.left != null)
        entry = entry.left;  
    return entry;
  }
  
  final Entry<K, V> getLastEntry() {
    Entry<K, V> entry = this.root;
    if (entry != null)
      while (entry.right != null)
        entry = entry.right;  
    return entry;
  }
  
  static <K, V> Entry<K, V> successor(Entry<K, V> paramEntry) {
    if (paramEntry == null)
      return null; 
    if (paramEntry.right != null) {
      Entry<K, V> entry = paramEntry.right;
      while (entry.left != null)
        entry = entry.left; 
      return entry;
    } 
    Entry<K, V> entry1 = paramEntry.parent;
    Entry<K, V> entry2 = paramEntry;
    while (entry1 != null && entry2 == entry1.right) {
      entry2 = entry1;
      entry1 = entry1.parent;
    } 
    return entry1;
  }
  
  static <K, V> Entry<K, V> predecessor(Entry<K, V> paramEntry) {
    if (paramEntry == null)
      return null; 
    if (paramEntry.left != null) {
      Entry<K, V> entry = paramEntry.left;
      while (entry.right != null)
        entry = entry.right; 
      return entry;
    } 
    Entry<K, V> entry1 = paramEntry.parent;
    Entry<K, V> entry2 = paramEntry;
    while (entry1 != null && entry2 == entry1.left) {
      entry2 = entry1;
      entry1 = entry1.parent;
    } 
    return entry1;
  }
  
  private static <K, V> boolean colorOf(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? true : paramEntry.color;
  }
  
  private static <K, V> Entry<K, V> parentOf(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? null : paramEntry.parent;
  }
  
  private static <K, V> void setColor(Entry<K, V> paramEntry, boolean paramBoolean) {
    if (paramEntry != null)
      paramEntry.color = paramBoolean; 
  }
  
  private static <K, V> Entry<K, V> leftOf(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? null : paramEntry.left;
  }
  
  private static <K, V> Entry<K, V> rightOf(Entry<K, V> paramEntry) {
    return (paramEntry == null) ? null : paramEntry.right;
  }
  
  private void rotateLeft(Entry<K, V> paramEntry) {
    if (paramEntry != null) {
      Entry<K, V> entry = paramEntry.right;
      paramEntry.right = entry.left;
      if (entry.left != null)
        entry.left.parent = paramEntry; 
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry.parent.left == paramEntry) {
        paramEntry.parent.left = entry;
      } else {
        paramEntry.parent.right = entry;
      } 
      entry.left = paramEntry;
      paramEntry.parent = entry;
    } 
  }
  
  private void rotateRight(Entry<K, V> paramEntry) {
    if (paramEntry != null) {
      Entry<K, V> entry = paramEntry.left;
      paramEntry.left = entry.right;
      if (entry.right != null)
        entry.right.parent = paramEntry; 
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry.parent.right == paramEntry) {
        paramEntry.parent.right = entry;
      } else {
        paramEntry.parent.left = entry;
      } 
      entry.right = paramEntry;
      paramEntry.parent = entry;
    } 
  }
  
  private void fixAfterInsertion(Entry<K, V> paramEntry) {
    paramEntry.color = false;
    while (paramEntry != null && paramEntry != this.root && !paramEntry.parent.color) {
      if (parentOf(paramEntry) == leftOf(parentOf(parentOf(paramEntry)))) {
        Entry<?, ?> entry1 = rightOf(parentOf(parentOf(paramEntry)));
        if (!colorOf(entry1)) {
          setColor(parentOf(paramEntry), true);
          setColor(entry1, true);
          setColor(parentOf(parentOf(paramEntry)), false);
          paramEntry = parentOf(parentOf(paramEntry));
          continue;
        } 
        if (paramEntry == rightOf(parentOf(paramEntry))) {
          paramEntry = parentOf(paramEntry);
          rotateLeft(paramEntry);
        } 
        setColor(parentOf(paramEntry), true);
        setColor(parentOf(parentOf(paramEntry)), false);
        rotateRight(parentOf(parentOf(paramEntry)));
        continue;
      } 
      Entry<?, ?> entry = leftOf(parentOf(parentOf(paramEntry)));
      if (!colorOf(entry)) {
        setColor(parentOf(paramEntry), true);
        setColor(entry, true);
        setColor(parentOf(parentOf(paramEntry)), false);
        paramEntry = parentOf(parentOf(paramEntry));
        continue;
      } 
      if (paramEntry == leftOf(parentOf(paramEntry))) {
        paramEntry = parentOf(paramEntry);
        rotateRight(paramEntry);
      } 
      setColor(parentOf(paramEntry), true);
      setColor(parentOf(parentOf(paramEntry)), false);
      rotateLeft(parentOf(parentOf(paramEntry)));
    } 
    this.root.color = true;
  }
  
  private void deleteEntry(Entry<K, V> paramEntry) {
    this.modCount++;
    this.size--;
    if (paramEntry.left != null && paramEntry.right != null) {
      Entry<K, V> entry1 = successor(paramEntry);
      paramEntry.key = entry1.key;
      paramEntry.value = entry1.value;
      paramEntry = entry1;
    } 
    Entry<K, V> entry = (paramEntry.left != null) ? paramEntry.left : paramEntry.right;
    if (entry != null) {
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry == paramEntry.parent.left) {
        paramEntry.parent.left = entry;
      } else {
        paramEntry.parent.right = entry;
      } 
      paramEntry.left = paramEntry.right = paramEntry.parent = null;
      if (paramEntry.color == true)
        fixAfterDeletion(entry); 
    } else if (paramEntry.parent == null) {
      this.root = null;
    } else {
      if (paramEntry.color == true)
        fixAfterDeletion(paramEntry); 
      if (paramEntry.parent != null) {
        if (paramEntry == paramEntry.parent.left) {
          paramEntry.parent.left = null;
        } else if (paramEntry == paramEntry.parent.right) {
          paramEntry.parent.right = null;
        } 
        paramEntry.parent = null;
      } 
    } 
  }
  
  private void fixAfterDeletion(Entry<K, V> paramEntry) {
    while (paramEntry != this.root && colorOf(paramEntry) == true) {
      if (paramEntry == leftOf(parentOf(paramEntry))) {
        Entry<?, ?> entry1 = rightOf(parentOf(paramEntry));
        if (!colorOf(entry1)) {
          setColor(entry1, true);
          setColor(parentOf(paramEntry), false);
          rotateLeft(parentOf(paramEntry));
          entry1 = rightOf(parentOf(paramEntry));
        } 
        if (colorOf(leftOf(entry1)) == true && 
          colorOf(rightOf(entry1)) == true) {
          setColor(entry1, false);
          paramEntry = parentOf(paramEntry);
          continue;
        } 
        if (colorOf(rightOf(entry1)) == true) {
          setColor(leftOf(entry1), true);
          setColor(entry1, false);
          rotateRight((Entry)entry1);
          entry1 = rightOf(parentOf(paramEntry));
        } 
        setColor(entry1, colorOf(parentOf(paramEntry)));
        setColor(parentOf(paramEntry), true);
        setColor(rightOf(entry1), true);
        rotateLeft(parentOf(paramEntry));
        paramEntry = this.root;
        continue;
      } 
      Entry<?, ?> entry = leftOf(parentOf(paramEntry));
      if (!colorOf(entry)) {
        setColor(entry, true);
        setColor(parentOf(paramEntry), false);
        rotateRight(parentOf(paramEntry));
        entry = leftOf(parentOf(paramEntry));
      } 
      if (colorOf(rightOf(entry)) == true && 
        colorOf(leftOf(entry)) == true) {
        setColor(entry, false);
        paramEntry = parentOf(paramEntry);
        continue;
      } 
      if (colorOf(leftOf(entry)) == true) {
        setColor(rightOf(entry), true);
        setColor(entry, false);
        rotateLeft((Entry)entry);
        entry = leftOf(parentOf(paramEntry));
      } 
      setColor(entry, colorOf(parentOf(paramEntry)));
      setColor(parentOf(paramEntry), true);
      setColor(leftOf(entry), true);
      rotateRight(parentOf(paramEntry));
      paramEntry = this.root;
    } 
    setColor(paramEntry, true);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    for (Map.Entry<K, V> entry : entrySet()) {
      paramObjectOutputStream.writeObject(entry.getKey());
      paramObjectOutputStream.writeObject(entry.getValue());
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    buildFromSorted(i, (Iterator<?>)null, paramObjectInputStream, (V)null);
  }
  
  void readTreeSet(int paramInt, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException {
    buildFromSorted(paramInt, (Iterator<?>)null, paramObjectInputStream, paramV);
  }
  
  void addAllForTreeSet(SortedSet<? extends K> paramSortedSet, V paramV) {
    try {
      buildFromSorted(paramSortedSet.size(), paramSortedSet.iterator(), (ObjectInputStream)null, paramV);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
  }
  
  private void buildFromSorted(int paramInt, Iterator<?> paramIterator, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException {
    this.size = paramInt;
    this.root = buildFromSorted(0, 0, paramInt - 1, computeRedLevel(paramInt), paramIterator, paramObjectInputStream, paramV);
  }
  
  private final Entry<K, V> buildFromSorted(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Iterator<?> paramIterator, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException {
    Object object;
    Object object1;
    if (paramInt3 < paramInt2)
      return null; 
    int i = paramInt2 + paramInt3 >>> 1;
    Entry<K, V> entry = null;
    if (paramInt2 < i)
      entry = buildFromSorted(paramInt1 + 1, paramInt2, i - 1, paramInt4, paramIterator, paramObjectInputStream, paramV); 
    if (paramIterator != null) {
      if (paramV == null) {
        Map.Entry entry2 = (Map.Entry)paramIterator.next();
        object = entry2.getKey();
        object1 = entry2.getValue();
      } else {
        object = paramIterator.next();
        V v = paramV;
      } 
    } else {
      object = paramObjectInputStream.readObject();
      object1 = (paramV != null) ? (Object)paramV : paramObjectInputStream.readObject();
    } 
    Entry<Object, Object> entry1 = new Entry<>(object, object1, null);
    if (paramInt1 == paramInt4)
      entry1.color = false; 
    if (entry != null) {
      entry1.left = (Entry)entry;
      entry.parent = (Entry)entry1;
    } 
    if (i < paramInt3) {
      Entry<K, V> entry2 = buildFromSorted(paramInt1 + 1, i + 1, paramInt3, paramInt4, paramIterator, paramObjectInputStream, paramV);
      entry1.right = (Entry)entry2;
      entry2.parent = (Entry)entry1;
    } 
    return (Entry)entry1;
  }
  
  private static int computeRedLevel(int paramInt) {
    byte b = 0;
    for (int i = paramInt - 1; i >= 0; i = i / 2 - 1)
      b++; 
    return b;
  }
  
  static <K> Spliterator<K> keySpliteratorFor(NavigableMap<K, ?> paramNavigableMap) {
    if (paramNavigableMap instanceof TreeMap) {
      TreeMap treeMap = (TreeMap)paramNavigableMap;
      return treeMap.keySpliterator();
    } 
    if (paramNavigableMap instanceof DescendingSubMap) {
      DescendingSubMap descendingSubMap = (DescendingSubMap)paramNavigableMap;
      TreeMap treeMap = descendingSubMap.m;
      if (descendingSubMap == treeMap.descendingMap) {
        TreeMap treeMap1 = treeMap;
        return treeMap1.descendingKeySpliterator();
      } 
    } 
    NavigableSubMap navigableSubMap = (NavigableSubMap)paramNavigableMap;
    return navigableSubMap.keySpliterator();
  }
  
  final Spliterator<K> keySpliterator() {
    return new KeySpliterator<>(this, null, null, 0, -1, 0);
  }
  
  final Spliterator<K> descendingKeySpliterator() {
    return new DescendingKeySpliterator<>(this, null, null, 0, -2, 0);
  }
  
  static class TreeMap {}
  
  static final class TreeMap {}
  
  static final class TreeMap {}
  
  static final class TreeMap {}
  
  static final class TreeMap {}
}
