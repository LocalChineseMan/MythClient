package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class AbstractMapBasedMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable {
  private transient Map<K, Collection<V>> map;
  
  private transient int totalSize;
  
  private static final long serialVersionUID = 2447537837011683357L;
  
  protected AbstractMapBasedMultimap(Map<K, Collection<V>> map) {
    Preconditions.checkArgument(map.isEmpty());
    this.map = map;
  }
  
  final void setMap(Map<K, Collection<V>> map) {
    this.map = map;
    this.totalSize = 0;
    for (Collection<V> values : map.values()) {
      Preconditions.checkArgument(!values.isEmpty());
      this.totalSize += values.size();
    } 
  }
  
  Collection<V> createUnmodifiableEmptyCollection() {
    return unmodifiableCollectionSubclass(createCollection());
  }
  
  Collection<V> createCollection(@Nullable K key) {
    return createCollection();
  }
  
  Map<K, Collection<V>> backingMap() {
    return this.map;
  }
  
  public int size() {
    return this.totalSize;
  }
  
  public boolean containsKey(@Nullable Object key) {
    return this.map.containsKey(key);
  }
  
  public boolean put(@Nullable K key, @Nullable V value) {
    Collection<V> collection = this.map.get(key);
    if (collection == null) {
      collection = createCollection(key);
      if (collection.add(value)) {
        this.totalSize++;
        this.map.put(key, collection);
        return true;
      } 
      throw new AssertionError("New Collection violated the Collection spec");
    } 
    if (collection.add(value)) {
      this.totalSize++;
      return true;
    } 
    return false;
  }
  
  private Collection<V> getOrCreateCollection(@Nullable K key) {
    Collection<V> collection = this.map.get(key);
    if (collection == null) {
      collection = createCollection(key);
      this.map.put(key, collection);
    } 
    return collection;
  }
  
  public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
    Iterator<? extends V> iterator = values.iterator();
    if (!iterator.hasNext())
      return removeAll(key); 
    Collection<V> collection = getOrCreateCollection(key);
    Collection<V> oldValues = createCollection();
    oldValues.addAll(collection);
    this.totalSize -= collection.size();
    collection.clear();
    while (iterator.hasNext()) {
      if (collection.add(iterator.next()))
        this.totalSize++; 
    } 
    return unmodifiableCollectionSubclass(oldValues);
  }
  
  public Collection<V> removeAll(@Nullable Object key) {
    Collection<V> collection = this.map.remove(key);
    if (collection == null)
      return createUnmodifiableEmptyCollection(); 
    Collection<V> output = createCollection();
    output.addAll(collection);
    this.totalSize -= collection.size();
    collection.clear();
    return unmodifiableCollectionSubclass(output);
  }
  
  Collection<V> unmodifiableCollectionSubclass(Collection<V> collection) {
    if (collection instanceof SortedSet)
      return Collections.unmodifiableSortedSet((SortedSet<V>)collection); 
    if (collection instanceof Set)
      return Collections.unmodifiableSet((Set<? extends V>)collection); 
    if (collection instanceof List)
      return Collections.unmodifiableList((List<? extends V>)collection); 
    return Collections.unmodifiableCollection(collection);
  }
  
  public void clear() {
    for (Collection<V> collection : this.map.values())
      collection.clear(); 
    this.map.clear();
    this.totalSize = 0;
  }
  
  public Collection<V> get(@Nullable K key) {
    Collection<V> collection = this.map.get(key);
    if (collection == null)
      collection = createCollection(key); 
    return wrapCollection(key, collection);
  }
  
  Collection<V> wrapCollection(@Nullable K key, Collection<V> collection) {
    if (collection instanceof SortedSet)
      return (Collection<V>)new WrappedSortedSet(this, key, (SortedSet)collection, null); 
    if (collection instanceof Set)
      return new WrappedSet(key, (Set<V>)collection); 
    if (collection instanceof List)
      return wrapList(key, (List<V>)collection, null); 
    return new WrappedCollection(key, collection, null);
  }
  
  private List<V> wrapList(@Nullable K key, List<V> list, @Nullable WrappedCollection ancestor) {
    return (list instanceof java.util.RandomAccess) ? (List<V>)new RandomAccessWrappedList(this, key, list, ancestor) : (List<V>)new WrappedList(this, key, list, ancestor);
  }
  
  private class WrappedCollection extends AbstractCollection<V> {
    final K key;
    
    Collection<V> delegate;
    
    final WrappedCollection ancestor;
    
    final Collection<V> ancestorDelegate;
    
    WrappedCollection(K key, @Nullable Collection<V> delegate, WrappedCollection ancestor) {
      this.key = key;
      this.delegate = delegate;
      this.ancestor = ancestor;
      this.ancestorDelegate = (ancestor == null) ? null : ancestor.getDelegate();
    }
    
    void refreshIfEmpty() {
      if (this.ancestor != null) {
        this.ancestor.refreshIfEmpty();
        if (this.ancestor.getDelegate() != this.ancestorDelegate)
          throw new ConcurrentModificationException(); 
      } else if (this.delegate.isEmpty()) {
        Collection<V> newDelegate = (Collection<V>)AbstractMapBasedMultimap.this.map.get(this.key);
        if (newDelegate != null)
          this.delegate = newDelegate; 
      } 
    }
    
    void removeIfEmpty() {
      if (this.ancestor != null) {
        this.ancestor.removeIfEmpty();
      } else if (this.delegate.isEmpty()) {
        AbstractMapBasedMultimap.this.map.remove(this.key);
      } 
    }
    
    K getKey() {
      return this.key;
    }
    
    void addToMap() {
      if (this.ancestor != null) {
        this.ancestor.addToMap();
      } else {
        AbstractMapBasedMultimap.this.map.put(this.key, (Collection)this.delegate);
      } 
    }
    
    public int size() {
      refreshIfEmpty();
      return this.delegate.size();
    }
    
    public boolean equals(@Nullable Object object) {
      if (object == this)
        return true; 
      refreshIfEmpty();
      return this.delegate.equals(object);
    }
    
    public int hashCode() {
      refreshIfEmpty();
      return this.delegate.hashCode();
    }
    
    public String toString() {
      refreshIfEmpty();
      return this.delegate.toString();
    }
    
    Collection<V> getDelegate() {
      return this.delegate;
    }
    
    public Iterator<V> iterator() {
      refreshIfEmpty();
      return new WrappedIterator();
    }
    
    class WrappedIterator implements Iterator<V> {
      final Iterator<V> delegateIterator;
      
      final Collection<V> originalDelegate = AbstractMapBasedMultimap.WrappedCollection.this.delegate;
      
      WrappedIterator() {
        this.delegateIterator = AbstractMapBasedMultimap.this.iteratorOrListIterator(AbstractMapBasedMultimap.WrappedCollection.this.delegate);
      }
      
      WrappedIterator(Iterator<V> delegateIterator) {
        this.delegateIterator = delegateIterator;
      }
      
      void validateIterator() {
        AbstractMapBasedMultimap.WrappedCollection.this.refreshIfEmpty();
        if (AbstractMapBasedMultimap.WrappedCollection.this.delegate != this.originalDelegate)
          throw new ConcurrentModificationException(); 
      }
      
      public boolean hasNext() {
        validateIterator();
        return this.delegateIterator.hasNext();
      }
      
      public V next() {
        validateIterator();
        return this.delegateIterator.next();
      }
      
      public void remove() {
        this.delegateIterator.remove();
        AbstractMapBasedMultimap.this.totalSize--;
        AbstractMapBasedMultimap.WrappedCollection.this.removeIfEmpty();
      }
      
      Iterator<V> getDelegateIterator() {
        validateIterator();
        return this.delegateIterator;
      }
    }
    
    public boolean add(V value) {
      refreshIfEmpty();
      boolean wasEmpty = this.delegate.isEmpty();
      boolean changed = this.delegate.add(value);
      if (changed) {
        AbstractMapBasedMultimap.this.totalSize++;
        if (wasEmpty)
          addToMap(); 
      } 
      return changed;
    }
    
    WrappedCollection getAncestor() {
      return this.ancestor;
    }
    
    public boolean addAll(Collection<? extends V> collection) {
      if (collection.isEmpty())
        return false; 
      int oldSize = size();
      boolean changed = this.delegate.addAll(collection);
      if (changed) {
        int newSize = this.delegate.size();
        AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
        if (oldSize == 0)
          addToMap(); 
      } 
      return changed;
    }
    
    public boolean contains(Object o) {
      refreshIfEmpty();
      return this.delegate.contains(o);
    }
    
    public boolean containsAll(Collection<?> c) {
      refreshIfEmpty();
      return this.delegate.containsAll(c);
    }
    
    public void clear() {
      int oldSize = size();
      if (oldSize == 0)
        return; 
      this.delegate.clear();
      AbstractMapBasedMultimap.this.totalSize -= oldSize;
      removeIfEmpty();
    }
    
    public boolean remove(Object o) {
      refreshIfEmpty();
      boolean changed = this.delegate.remove(o);
      if (changed) {
        AbstractMapBasedMultimap.this.totalSize--;
        removeIfEmpty();
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> c) {
      if (c.isEmpty())
        return false; 
      int oldSize = size();
      boolean changed = this.delegate.removeAll(c);
      if (changed) {
        int newSize = this.delegate.size();
        AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
        removeIfEmpty();
      } 
      return changed;
    }
    
    public boolean retainAll(Collection<?> c) {
      Preconditions.checkNotNull(c);
      int oldSize = size();
      boolean changed = this.delegate.retainAll(c);
      if (changed) {
        int newSize = this.delegate.size();
        AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
        removeIfEmpty();
      } 
      return changed;
    }
  }
  
  private Iterator<V> iteratorOrListIterator(Collection<V> collection) {
    return (collection instanceof List) ? ((List<V>)collection).listIterator() : collection.iterator();
  }
  
  private class WrappedSet extends WrappedCollection implements Set<V> {
    WrappedSet(K key, Set<V> delegate) {
      super(key, delegate, null);
    }
    
    public boolean removeAll(Collection<?> c) {
      if (c.isEmpty())
        return false; 
      int oldSize = size();
      boolean changed = Sets.removeAllImpl((Set)this.delegate, c);
      if (changed) {
        int newSize = this.delegate.size();
        AbstractMapBasedMultimap.this.totalSize += newSize - oldSize;
        removeIfEmpty();
      } 
      return changed;
    }
  }
  
  Set<K> createKeySet() {
    return (this.map instanceof SortedMap) ? (Set<K>)new SortedKeySet(this, (SortedMap)this.map) : (Set<K>)new KeySet(this, this.map);
  }
  
  private int removeValuesForKey(Object key) {
    Collection<V> collection = Maps.<Collection<V>>safeRemove(this.map, key);
    int count = 0;
    if (collection != null) {
      count = collection.size();
      collection.clear();
      this.totalSize -= count;
    } 
    return count;
  }
  
  private abstract class Itr<T> implements Iterator<T> {
    final Iterator<Map.Entry<K, Collection<V>>> keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
    
    K key = null;
    
    Collection<V> collection = null;
    
    Iterator<V> valueIterator = Iterators.emptyModifiableIterator();
    
    abstract T output(K param1K, V param1V);
    
    public boolean hasNext() {
      return (this.keyIterator.hasNext() || this.valueIterator.hasNext());
    }
    
    public T next() {
      if (!this.valueIterator.hasNext()) {
        Map.Entry<K, Collection<V>> mapEntry = this.keyIterator.next();
        this.key = mapEntry.getKey();
        this.collection = mapEntry.getValue();
        this.valueIterator = this.collection.iterator();
      } 
      return output(this.key, this.valueIterator.next());
    }
    
    public void remove() {
      this.valueIterator.remove();
      if (this.collection.isEmpty())
        this.keyIterator.remove(); 
      AbstractMapBasedMultimap.this.totalSize--;
    }
  }
  
  public Collection<V> values() {
    return super.values();
  }
  
  Iterator<V> valueIterator() {
    return (Iterator<V>)new Object(this);
  }
  
  public Collection<Map.Entry<K, V>> entries() {
    return super.entries();
  }
  
  Iterator<Map.Entry<K, V>> entryIterator() {
    return new Itr<Map.Entry<K, V>>() {
        Map.Entry<K, V> output(K key, V value) {
          return Maps.immutableEntry(key, value);
        }
      };
  }
  
  Map<K, Collection<V>> createAsMap() {
    return (this.map instanceof SortedMap) ? (Map<K, Collection<V>>)new SortedAsMap(this, (SortedMap)this.map) : (Map<K, Collection<V>>)new AsMap(this.map);
  }
  
  abstract Collection<V> createCollection();
  
  private class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>> {
    final transient Map<K, Collection<V>> submap;
    
    AsMap(Map<K, Collection<V>> submap) {
      this.submap = submap;
    }
    
    protected Set<Map.Entry<K, Collection<V>>> createEntrySet() {
      return (Set<Map.Entry<K, Collection<V>>>)new AsMapEntries(this);
    }
    
    public boolean containsKey(Object key) {
      return Maps.safeContainsKey(this.submap, key);
    }
    
    public Collection<V> get(Object key) {
      Collection<V> collection = Maps.<Collection<V>>safeGet(this.submap, key);
      if (collection == null)
        return null; 
      K k = (K)key;
      return AbstractMapBasedMultimap.this.wrapCollection(k, collection);
    }
    
    public Set<K> keySet() {
      return AbstractMapBasedMultimap.this.keySet();
    }
    
    public int size() {
      return this.submap.size();
    }
    
    public Collection<V> remove(Object key) {
      Collection<V> collection = this.submap.remove(key);
      if (collection == null)
        return null; 
      Collection<V> output = AbstractMapBasedMultimap.this.createCollection();
      output.addAll(collection);
      AbstractMapBasedMultimap.this.totalSize -= collection.size();
      collection.clear();
      return output;
    }
    
    public boolean equals(@Nullable Object object) {
      return (this == object || this.submap.equals(object));
    }
    
    public int hashCode() {
      return this.submap.hashCode();
    }
    
    public String toString() {
      return this.submap.toString();
    }
    
    public void clear() {
      if (this.submap == AbstractMapBasedMultimap.this.map) {
        AbstractMapBasedMultimap.this.clear();
      } else {
        Iterators.clear((Iterator<?>)new AsMapIterator(this));
      } 
    }
    
    Map.Entry<K, Collection<V>> wrapEntry(Map.Entry<K, Collection<V>> entry) {
      K key = entry.getKey();
      return Maps.immutableEntry(key, AbstractMapBasedMultimap.this.wrapCollection(key, entry.getValue()));
    }
    
    class AsMap {}
    
    class AsMap {}
  }
  
  class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
  
  class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
  
  class AbstractMapBasedMultimap {}
  
  private class AbstractMapBasedMultimap {}
}
