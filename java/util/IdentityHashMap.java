package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, Cloneable {
  private static final int DEFAULT_CAPACITY = 32;
  
  private static final int MINIMUM_CAPACITY = 4;
  
  private static final int MAXIMUM_CAPACITY = 536870912;
  
  transient Object[] table;
  
  int size;
  
  transient int modCount;
  
  static final Object NULL_KEY = new Object();
  
  private transient Set<Map.Entry<K, V>> entrySet;
  
  private static final long serialVersionUID = 8188218128353913216L;
  
  private static Object maskNull(Object paramObject) {
    return (paramObject == null) ? NULL_KEY : paramObject;
  }
  
  static final Object unmaskNull(Object paramObject) {
    return (paramObject == NULL_KEY) ? null : paramObject;
  }
  
  public IdentityHashMap() {
    init(32);
  }
  
  public IdentityHashMap(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("expectedMaxSize is negative: " + paramInt); 
    init(capacity(paramInt));
  }
  
  private static int capacity(int paramInt) {
    return (paramInt > 178956970) ? 536870912 : ((paramInt <= 2) ? 4 : 
      
      Integer.highestOneBit(paramInt + (paramInt << 1)));
  }
  
  private void init(int paramInt) {
    this.table = new Object[2 * paramInt];
  }
  
  public IdentityHashMap(Map<? extends K, ? extends V> paramMap) {
    this((int)((1 + paramMap.size()) * 1.1D));
    putAll(paramMap);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  private static int hash(Object paramObject, int paramInt) {
    int i = System.identityHashCode(paramObject);
    return (i << 1) - (i << 8) & paramInt - 1;
  }
  
  private static int nextKeyIndex(int paramInt1, int paramInt2) {
    return (paramInt1 + 2 < paramInt2) ? (paramInt1 + 2) : 0;
  }
  
  public V get(Object paramObject) {
    Object object = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object, i);
    while (true) {
      Object object1 = arrayOfObject[j];
      if (object1 == object)
        return (V)arrayOfObject[j + 1]; 
      if (object1 == null)
        return null; 
      j = nextKeyIndex(j, i);
    } 
  }
  
  public boolean containsKey(Object paramObject) {
    Object object = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object, i);
    while (true) {
      Object object1 = arrayOfObject[j];
      if (object1 == object)
        return true; 
      if (object1 == null)
        return false; 
      j = nextKeyIndex(j, i);
    } 
  }
  
  public boolean containsValue(Object paramObject) {
    Object[] arrayOfObject = this.table;
    for (byte b = 1; b < arrayOfObject.length; b += 2) {
      if (arrayOfObject[b] == paramObject && arrayOfObject[b - 1] != null)
        return true; 
    } 
    return false;
  }
  
  private boolean containsMapping(Object paramObject1, Object paramObject2) {
    Object object = maskNull(paramObject1);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object, i);
    while (true) {
      Object object1 = arrayOfObject[j];
      if (object1 == object)
        return (arrayOfObject[j + 1] == paramObject2); 
      if (object1 == null)
        return false; 
      j = nextKeyIndex(j, i);
    } 
  }
  
  public V put(K paramK, V paramV) {
    Object[] arrayOfObject;
    int i, j;
    Object object = maskNull(paramK);
    while (true) {
      arrayOfObject = this.table;
      int k = arrayOfObject.length;
      i = hash(object, k);
      Object object1;
      for (; (object1 = arrayOfObject[i]) != null; 
        i = nextKeyIndex(i, k)) {
        if (object1 == object) {
          Object object2 = arrayOfObject[i + 1];
          arrayOfObject[i + 1] = paramV;
          return (V)object2;
        } 
      } 
      j = this.size + 1;
      if (j + (j << 1) > k && resize(k))
        continue; 
      break;
    } 
    this.modCount++;
    arrayOfObject[i] = object;
    arrayOfObject[i + 1] = paramV;
    this.size = j;
    return null;
  }
  
  private boolean resize(int paramInt) {
    int i = paramInt * 2;
    Object[] arrayOfObject1 = this.table;
    int j = arrayOfObject1.length;
    if (j == 1073741824) {
      if (this.size == 536870911)
        throw new IllegalStateException("Capacity exhausted."); 
      return false;
    } 
    if (j >= i)
      return false; 
    Object[] arrayOfObject2 = new Object[i];
    for (byte b = 0; b < j; b += 2) {
      Object object = arrayOfObject1[b];
      if (object != null) {
        Object object1 = arrayOfObject1[b + 1];
        arrayOfObject1[b] = null;
        arrayOfObject1[b + 1] = null;
        int k = hash(object, i);
        while (arrayOfObject2[k] != null)
          k = nextKeyIndex(k, i); 
        arrayOfObject2[k] = object;
        arrayOfObject2[k + 1] = object1;
      } 
    } 
    this.table = arrayOfObject2;
    return true;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    int i = paramMap.size();
    if (i == 0)
      return; 
    if (i > this.size)
      resize(capacity(i)); 
    for (Map.Entry<? extends K, ? extends V> entry : paramMap.entrySet())
      put((K)entry.getKey(), (V)entry.getValue()); 
  }
  
  public V remove(Object paramObject) {
    Object object = maskNull(paramObject);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object, i);
    while (true) {
      Object object1 = arrayOfObject[j];
      if (object1 == object) {
        this.modCount++;
        this.size--;
        Object object2 = arrayOfObject[j + 1];
        arrayOfObject[j + 1] = null;
        arrayOfObject[j] = null;
        closeDeletion(j);
        return (V)object2;
      } 
      if (object1 == null)
        return null; 
      j = nextKeyIndex(j, i);
    } 
  }
  
  private boolean removeMapping(Object paramObject1, Object paramObject2) {
    Object object = maskNull(paramObject1);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object, i);
    while (true) {
      Object object1 = arrayOfObject[j];
      if (object1 == object) {
        if (arrayOfObject[j + 1] != paramObject2)
          return false; 
        this.modCount++;
        this.size--;
        arrayOfObject[j] = null;
        arrayOfObject[j + 1] = null;
        closeDeletion(j);
        return true;
      } 
      if (object1 == null)
        return false; 
      j = nextKeyIndex(j, i);
    } 
  }
  
  private void closeDeletion(int paramInt) {
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    Object object;
    int j;
    for (j = nextKeyIndex(paramInt, i); (object = arrayOfObject[j]) != null; 
      j = nextKeyIndex(j, i)) {
      int k = hash(object, i);
      if ((j < k && (k <= paramInt || paramInt <= j)) || (k <= paramInt && paramInt <= j)) {
        arrayOfObject[paramInt] = object;
        arrayOfObject[paramInt + 1] = arrayOfObject[j + 1];
        arrayOfObject[j] = null;
        arrayOfObject[j + 1] = null;
        paramInt = j;
      } 
    } 
  }
  
  public void clear() {
    this.modCount++;
    Object[] arrayOfObject = this.table;
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfObject[b] = null; 
    this.size = 0;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof IdentityHashMap) {
      IdentityHashMap identityHashMap = (IdentityHashMap)paramObject;
      if (identityHashMap.size() != this.size)
        return false; 
      Object[] arrayOfObject = identityHashMap.table;
      for (byte b = 0; b < arrayOfObject.length; b += 2) {
        Object object = arrayOfObject[b];
        if (object != null && !containsMapping(object, arrayOfObject[b + 1]))
          return false; 
      } 
      return true;
    } 
    if (paramObject instanceof Map) {
      Map map = (Map)paramObject;
      return entrySet().equals(map.entrySet());
    } 
    return false;
  }
  
  public int hashCode() {
    int i = 0;
    Object[] arrayOfObject = this.table;
    for (byte b = 0; b < arrayOfObject.length; b += 2) {
      Object object = arrayOfObject[b];
      if (object != null) {
        Object object1 = unmaskNull(object);
        i += System.identityHashCode(object1) ^ 
          System.identityHashCode(arrayOfObject[b + 1]);
      } 
    } 
    return i;
  }
  
  public Object clone() {
    try {
      IdentityHashMap identityHashMap = (IdentityHashMap)super.clone();
      identityHashMap.entrySet = null;
      identityHashMap.table = (Object[])this.table.clone();
      return identityHashMap;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private abstract class IdentityHashMapIterator<T> implements Iterator<T> {
    int index = (IdentityHashMap.this.size != 0) ? 0 : IdentityHashMap.this.table.length;
    
    int expectedModCount = IdentityHashMap.this.modCount;
    
    int lastReturnedIndex = -1;
    
    boolean indexValid;
    
    Object[] traversalTable = IdentityHashMap.this.table;
    
    public boolean hasNext() {
      Object[] arrayOfObject = this.traversalTable;
      for (int i = this.index; i < arrayOfObject.length; i += 2) {
        Object object = arrayOfObject[i];
        if (object != null) {
          this.index = i;
          return this.indexValid = true;
        } 
      } 
      this.index = arrayOfObject.length;
      return false;
    }
    
    protected int nextIndex() {
      if (IdentityHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (!this.indexValid && !hasNext())
        throw new NoSuchElementException(); 
      this.indexValid = false;
      this.lastReturnedIndex = this.index;
      this.index += 2;
      return this.lastReturnedIndex;
    }
    
    public void remove() {
      if (this.lastReturnedIndex == -1)
        throw new IllegalStateException(); 
      if (IdentityHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.expectedModCount = ++IdentityHashMap.this.modCount;
      int i = this.lastReturnedIndex;
      this.lastReturnedIndex = -1;
      this.index = i;
      this.indexValid = false;
      Object[] arrayOfObject = this.traversalTable;
      int j = arrayOfObject.length;
      int k = i;
      Object object1 = arrayOfObject[k];
      arrayOfObject[k] = null;
      arrayOfObject[k + 1] = null;
      if (arrayOfObject != IdentityHashMap.this.table) {
        IdentityHashMap.this.remove(object1);
        this.expectedModCount = IdentityHashMap.this.modCount;
        return;
      } 
      IdentityHashMap.this.size--;
      Object object2;
      int m;
      for (m = IdentityHashMap.nextKeyIndex(k, j); (object2 = arrayOfObject[m]) != null; 
        m = IdentityHashMap.nextKeyIndex(m, j)) {
        int n = IdentityHashMap.hash(object2, j);
        if ((m < n && (n <= k || k <= m)) || (n <= k && k <= m)) {
          if (m < i && k >= i && this.traversalTable == IdentityHashMap.this.table) {
            int i1 = j - i;
            Object[] arrayOfObject1 = new Object[i1];
            System.arraycopy(arrayOfObject, i, arrayOfObject1, 0, i1);
            this.traversalTable = arrayOfObject1;
            this.index = 0;
          } 
          arrayOfObject[k] = object2;
          arrayOfObject[k + 1] = arrayOfObject[m + 1];
          arrayOfObject[m] = null;
          arrayOfObject[m + 1] = null;
          k = m;
        } 
      } 
    }
    
    private IdentityHashMapIterator() {}
  }
  
  private class KeyIterator extends IdentityHashMapIterator<K> {
    private KeyIterator() {}
    
    public K next() {
      return (K)IdentityHashMap.unmaskNull(this.traversalTable[nextIndex()]);
    }
  }
  
  private class ValueIterator extends IdentityHashMapIterator<V> {
    private ValueIterator() {}
    
    public V next() {
      return (V)this.traversalTable[nextIndex() + 1];
    }
  }
  
  private class EntryIterator extends IdentityHashMapIterator<Map.Entry<K, V>> {
    private Entry lastReturnedEntry;
    
    private EntryIterator() {}
    
    public Map.Entry<K, V> next() {
      this.lastReturnedEntry = new Entry(nextIndex());
      return this.lastReturnedEntry;
    }
    
    public void remove() {
      this
        .lastReturnedIndex = (null == this.lastReturnedEntry) ? -1 : this.lastReturnedEntry.index;
      super.remove();
      this.lastReturnedEntry.index = this.lastReturnedIndex;
      this.lastReturnedEntry = null;
    }
    
    private class Entry implements Map.Entry<K, V> {
      private int index;
      
      private Entry(int param2Int) {
        this.index = param2Int;
      }
      
      public K getKey() {
        checkIndexForEntryUse();
        return (K)IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index]);
      }
      
      public V getValue() {
        checkIndexForEntryUse();
        return (V)IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1];
      }
      
      public V setValue(V param2V) {
        checkIndexForEntryUse();
        Object object = IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1];
        IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1] = param2V;
        if (IdentityHashMap.EntryIterator.this.traversalTable != IdentityHashMap.this.table)
          IdentityHashMap.this.put((K)IdentityHashMap.EntryIterator.this.traversalTable[this.index], param2V); 
        return (V)object;
      }
      
      public boolean equals(Object param2Object) {
        if (this.index < 0)
          return super.equals(param2Object); 
        if (!(param2Object instanceof Map.Entry))
          return false; 
        Map.Entry entry = (Map.Entry)param2Object;
        return (entry.getKey() == IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index]) && entry
          .getValue() == IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1]);
      }
      
      public int hashCode() {
        if (IdentityHashMap.EntryIterator.this.lastReturnedIndex < 0)
          return super.hashCode(); 
        return System.identityHashCode(IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index])) ^ System.identityHashCode(IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1]);
      }
      
      public String toString() {
        if (this.index < 0)
          return super.toString(); 
        return IdentityHashMap.unmaskNull(IdentityHashMap.EntryIterator.this.traversalTable[this.index]) + "=" + IdentityHashMap.EntryIterator.this.traversalTable[this.index + 1];
      }
      
      private void checkIndexForEntryUse() {
        if (this.index < 0)
          throw new IllegalStateException("Entry was removed"); 
      }
    }
  }
  
  public Set<K> keySet() {
    Set<K> set = this.keySet;
    if (set != null)
      return set; 
    return this.keySet = new KeySet();
  }
  
  private class KeySet extends AbstractSet<K> {
    private KeySet() {}
    
    public Iterator<K> iterator() {
      return new IdentityHashMap.KeyIterator();
    }
    
    public int size() {
      return IdentityHashMap.this.size;
    }
    
    public boolean contains(Object param1Object) {
      return IdentityHashMap.this.containsKey(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      int i = IdentityHashMap.this.size;
      IdentityHashMap.this.remove(param1Object);
      return (IdentityHashMap.this.size != i);
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      Objects.requireNonNull(param1Collection);
      boolean bool = false;
      for (Iterator<K> iterator = iterator(); iterator.hasNext();) {
        if (param1Collection.contains(iterator.next())) {
          iterator.remove();
          bool = true;
        } 
      } 
      return bool;
    }
    
    public void clear() {
      IdentityHashMap.this.clear();
    }
    
    public int hashCode() {
      int i = 0;
      for (K k : this)
        i += System.identityHashCode(k); 
      return i;
    }
    
    public Object[] toArray() {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (param1ArrayOfT.length < j)
        param1ArrayOfT = (T[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), j); 
      Object[] arrayOfObject = IdentityHashMap.this.table;
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject.length; b2 += 2) {
        Object object;
        if ((object = arrayOfObject[b2]) != null) {
          if (b1 >= j)
            throw new ConcurrentModificationException(); 
          param1ArrayOfT[b1++] = (T)IdentityHashMap.unmaskNull(object);
        } 
      } 
      if (b1 < j || i != IdentityHashMap.this.modCount)
        throw new ConcurrentModificationException(); 
      if (b1 < param1ArrayOfT.length)
        param1ArrayOfT[b1] = null; 
      return param1ArrayOfT;
    }
    
    public Spliterator<K> spliterator() {
      return new IdentityHashMap.KeySpliterator<>(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
  
  public Collection<V> values() {
    Collection<V> collection = this.values;
    if (collection != null)
      return collection; 
    return this.values = new Values();
  }
  
  private class Values extends AbstractCollection<V> {
    private Values() {}
    
    public Iterator<V> iterator() {
      return new IdentityHashMap.ValueIterator();
    }
    
    public int size() {
      return IdentityHashMap.this.size;
    }
    
    public boolean contains(Object param1Object) {
      return IdentityHashMap.this.containsValue(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      for (Iterator<V> iterator = iterator(); iterator.hasNext();) {
        if (iterator.next() == param1Object) {
          iterator.remove();
          return true;
        } 
      } 
      return false;
    }
    
    public void clear() {
      IdentityHashMap.this.clear();
    }
    
    public Object[] toArray() {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (param1ArrayOfT.length < j)
        param1ArrayOfT = (T[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), j); 
      Object[] arrayOfObject = IdentityHashMap.this.table;
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject.length; b2 += 2) {
        if (arrayOfObject[b2] != null) {
          if (b1 >= j)
            throw new ConcurrentModificationException(); 
          param1ArrayOfT[b1++] = (T)arrayOfObject[b2 + 1];
        } 
      } 
      if (b1 < j || i != IdentityHashMap.this.modCount)
        throw new ConcurrentModificationException(); 
      if (b1 < param1ArrayOfT.length)
        param1ArrayOfT[b1] = null; 
      return param1ArrayOfT;
    }
    
    public Spliterator<V> spliterator() {
      return new IdentityHashMap.ValueSpliterator<>(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> set = this.entrySet;
    if (set != null)
      return set; 
    return this.entrySet = new EntrySet();
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new IdentityHashMap.EntryIterator();
    }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return IdentityHashMap.this.containsMapping(entry.getKey(), entry.getValue());
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return IdentityHashMap.this.removeMapping(entry.getKey(), entry.getValue());
    }
    
    public int size() {
      return IdentityHashMap.this.size;
    }
    
    public void clear() {
      IdentityHashMap.this.clear();
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      Objects.requireNonNull(param1Collection);
      boolean bool = false;
      for (Iterator<Map.Entry<K, V>> iterator = iterator(); iterator.hasNext();) {
        if (param1Collection.contains(iterator.next())) {
          iterator.remove();
          bool = true;
        } 
      } 
      return bool;
    }
    
    public Object[] toArray() {
      return toArray(new Object[0]);
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = IdentityHashMap.this.modCount;
      int j = size();
      if (param1ArrayOfT.length < j)
        param1ArrayOfT = (T[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), j); 
      Object[] arrayOfObject = IdentityHashMap.this.table;
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject.length; b2 += 2) {
        Object object;
        if ((object = arrayOfObject[b2]) != null) {
          if (b1 >= j)
            throw new ConcurrentModificationException(); 
          param1ArrayOfT[b1++] = (T)new AbstractMap.SimpleEntry<>(IdentityHashMap.unmaskNull(object), arrayOfObject[b2 + 1]);
        } 
      } 
      if (b1 < j || i != IdentityHashMap.this.modCount)
        throw new ConcurrentModificationException(); 
      if (b1 < param1ArrayOfT.length)
        param1ArrayOfT[b1] = null; 
      return param1ArrayOfT;
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator() {
      return new IdentityHashMap.EntrySpliterator<>(IdentityHashMap.this, 0, -1, 0, 0);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    Object[] arrayOfObject = this.table;
    for (byte b = 0; b < arrayOfObject.length; b += 2) {
      Object object = arrayOfObject[b];
      if (object != null) {
        paramObjectOutputStream.writeObject(unmaskNull(object));
        paramObjectOutputStream.writeObject(arrayOfObject[b + 1]);
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i < 0)
      throw new StreamCorruptedException("Illegal mappings count: " + i); 
    init(capacity(i));
    for (byte b = 0; b < i; b++) {
      Object object1 = paramObjectInputStream.readObject();
      Object object2 = paramObjectInputStream.readObject();
      putForCreate((K)object1, (V)object2);
    } 
  }
  
  private void putForCreate(K paramK, V paramV) throws StreamCorruptedException {
    Object object1 = maskNull(paramK);
    Object[] arrayOfObject = this.table;
    int i = arrayOfObject.length;
    int j = hash(object1, i);
    Object object2;
    while ((object2 = arrayOfObject[j]) != null) {
      if (object2 == object1)
        throw new StreamCorruptedException(); 
      j = nextKeyIndex(j, i);
    } 
    arrayOfObject[j] = object1;
    arrayOfObject[j + 1] = paramV;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    Object[] arrayOfObject = this.table;
    for (byte b = 0; b < arrayOfObject.length; b += 2) {
      Object object = arrayOfObject[b];
      if (object != null)
        paramBiConsumer.accept((K)unmaskNull(object), (V)arrayOfObject[b + 1]); 
      if (this.modCount != i)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    Object[] arrayOfObject = this.table;
    for (byte b = 0; b < arrayOfObject.length; b += 2) {
      Object object = arrayOfObject[b];
      if (object != null)
        arrayOfObject[b + 1] = paramBiFunction.apply((K)unmaskNull(object), (V)arrayOfObject[b + 1]); 
      if (this.modCount != i)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  static class IdentityHashMap {}
  
  static final class IdentityHashMap {}
  
  static final class IdentityHashMap {}
  
  static final class IdentityHashMap {}
}
