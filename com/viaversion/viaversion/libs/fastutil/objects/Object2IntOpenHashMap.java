package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class Object2IntOpenHashMap<K> extends AbstractObject2IntMap<K> implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient K[] key;
  
  protected transient int[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  protected transient Object2IntMap.FastEntrySet<K> entries;
  
  protected transient ObjectSet<K> keys;
  
  protected transient IntCollection values;
  
  public Object2IntOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = (K[])new Object[this.n + 1];
    this.value = new int[this.n + 1];
  }
  
  public Object2IntOpenHashMap(int expected) {
    this(expected, 0.75F);
  }
  
  public Object2IntOpenHashMap() {
    this(16, 0.75F);
  }
  
  public Object2IntOpenHashMap(Map<? extends K, ? extends Integer> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Object2IntOpenHashMap(Map<? extends K, ? extends Integer> m) {
    this(m, 0.75F);
  }
  
  public Object2IntOpenHashMap(Object2IntMap<K> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Object2IntOpenHashMap(Object2IntMap<K> m) {
    this(m, 0.75F);
  }
  
  public Object2IntOpenHashMap(K[] k, int[] v, float f) {
    this(k.length, f);
    if (k.length != v.length)
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")"); 
    for (int i = 0; i < k.length; i++)
      put(k[i], v[i]); 
  }
  
  public Object2IntOpenHashMap(K[] k, int[] v) {
    this(k, v, 0.75F);
  }
  
  private int realSize() {
    return this.containsNullKey ? (this.size - 1) : this.size;
  }
  
  private void ensureCapacity(int capacity) {
    int needed = HashCommon.arraySize(capacity, this.f);
    if (needed > this.n)
      rehash(needed); 
  }
  
  private void tryCapacity(long capacity) {
    int needed = (int)Math.min(1073741824L, 
        Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil(((float)capacity / this.f)))));
    if (needed > this.n)
      rehash(needed); 
  }
  
  private int removeEntry(int pos) {
    int oldValue = this.value[pos];
    this.size--;
    shiftKeys(pos);
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  private int removeNullEntry() {
    this.containsNullKey = false;
    this.key[this.n] = null;
    int oldValue = this.value[this.n];
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  public void putAll(Map<? extends K, ? extends Integer> m) {
    if (this.f <= 0.5D) {
      ensureCapacity(m.size());
    } else {
      tryCapacity((size() + m.size()));
    } 
    super.putAll(m);
  }
  
  private int find(K k) {
    if (k == null)
      return this.containsNullKey ? this.n : -(this.n + 1); 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return -(pos + 1); 
    if (k.equals(curr))
      return pos; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return -(pos + 1); 
      if (k.equals(curr))
        return pos; 
    } 
  }
  
  private void insert(int pos, K k, int v) {
    if (pos == this.n)
      this.containsNullKey = true; 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
  }
  
  public int put(K k, int v) {
    int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return this.defRetValue;
    } 
    int oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  private int addToValue(int pos, int incr) {
    int oldValue = this.value[pos];
    this.value[pos] = oldValue + incr;
    return oldValue;
  }
  
  public int addTo(K k, int incr) {
    int pos;
    if (k == null) {
      if (this.containsNullKey)
        return addToValue(this.n, incr); 
      pos = this.n;
      this.containsNullKey = true;
    } else {
      K[] key = this.key;
      K curr;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
        if (curr.equals(k))
          return addToValue(pos, incr); 
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
          if (curr.equals(k))
            return addToValue(pos, incr); 
        } 
      } 
    } 
    this.key[pos] = k;
    this.value[pos] = this.defRetValue + incr;
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
    return this.defRetValue;
  }
  
  protected final void shiftKeys(int pos) {
    K[] key = this.key;
    while (true) {
      K curr;
      int last;
      pos = (last = pos) + 1 & this.mask;
      while (true) {
        if ((curr = key[pos]) == null) {
          key[last] = null;
          return;
        } 
        int slot = HashCommon.mix(curr.hashCode()) & this.mask;
        if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
          break; 
        pos = pos + 1 & this.mask;
      } 
      key[last] = curr;
      this.value[last] = this.value[pos];
    } 
  }
  
  public int removeInt(Object k) {
    if (k == null) {
      if (this.containsNullKey)
        return removeNullEntry(); 
      return this.defRetValue;
    } 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return this.defRetValue; 
    if (k.equals(curr))
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return this.defRetValue; 
      if (k.equals(curr))
        return removeEntry(pos); 
    } 
  }
  
  public int getInt(Object k) {
    if (k == null)
      return this.containsNullKey ? this.value[this.n] : this.defRetValue; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return this.defRetValue; 
    if (k.equals(curr))
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return this.defRetValue; 
      if (k.equals(curr))
        return this.value[pos]; 
    } 
  }
  
  public boolean containsKey(Object k) {
    if (k == null)
      return this.containsNullKey; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return false; 
    if (k.equals(curr))
      return true; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return false; 
      if (k.equals(curr))
        return true; 
    } 
  }
  
  public boolean containsValue(int v) {
    int[] value = this.value;
    K[] key = this.key;
    if (this.containsNullKey && value[this.n] == v)
      return true; 
    for (int i = this.n; i-- != 0;) {
      if (key[i] != null && value[i] == v)
        return true; 
    } 
    return false;
  }
  
  public int getOrDefault(Object k, int defaultValue) {
    if (k == null)
      return this.containsNullKey ? this.value[this.n] : defaultValue; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return defaultValue; 
    if (k.equals(curr))
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return defaultValue; 
      if (k.equals(curr))
        return this.value[pos]; 
    } 
  }
  
  public int putIfAbsent(K k, int v) {
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    insert(-pos - 1, k, v);
    return this.defRetValue;
  }
  
  public boolean remove(Object k, int v) {
    if (k == null) {
      if (this.containsNullKey && v == this.value[this.n]) {
        removeNullEntry();
        return true;
      } 
      return false;
    } 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return false; 
    if (k.equals(curr) && v == this.value[pos]) {
      removeEntry(pos);
      return true;
    } 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return false; 
      if (k.equals(curr) && v == this.value[pos]) {
        removeEntry(pos);
        return true;
      } 
    } 
  }
  
  public boolean replace(K k, int oldValue, int v) {
    int pos = find(k);
    if (pos < 0 || oldValue != this.value[pos])
      return false; 
    this.value[pos] = v;
    return true;
  }
  
  public int replace(K k, int v) {
    int pos = find(k);
    if (pos < 0)
      return this.defRetValue; 
    int oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  public int computeIntIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    int newValue = mappingFunction.applyAsInt(k);
    insert(-pos - 1, k, newValue);
    return newValue;
  }
  
  public int computeIntIfPresent(K k, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0)
      return this.defRetValue; 
    Integer newValue = remappingFunction.apply(k, Integer.valueOf(this.value[pos]));
    if (newValue == null) {
      if (k == null) {
        removeNullEntry();
      } else {
        removeEntry(pos);
      } 
      return this.defRetValue;
    } 
    this.value[pos] = newValue.intValue();
    return newValue.intValue();
  }
  
  public int computeInt(K k, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    Integer newValue = remappingFunction.apply(k, (pos >= 0) ? Integer.valueOf(this.value[pos]) : null);
    if (newValue == null) {
      if (pos >= 0)
        if (k == null) {
          removeNullEntry();
        } else {
          removeEntry(pos);
        }  
      return this.defRetValue;
    } 
    int newVal = newValue.intValue();
    if (pos < 0) {
      insert(-pos - 1, k, newVal);
      return newVal;
    } 
    this.value[pos] = newVal;
    return newVal;
  }
  
  public int mergeInt(K k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return v;
    } 
    Integer newValue = remappingFunction.apply(Integer.valueOf(this.value[pos]), Integer.valueOf(v));
    if (newValue == null) {
      if (k == null) {
        removeNullEntry();
      } else {
        removeEntry(pos);
      } 
      return this.defRetValue;
    } 
    this.value[pos] = newValue.intValue();
    return newValue.intValue();
  }
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNullKey = false;
    Arrays.fill((Object[])this.key, (Object)null);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  final class MapEntry implements Object2IntMap.Entry<K>, Map.Entry<K, Integer> {
    int index;
    
    MapEntry(int index) {
      this.index = index;
    }
    
    MapEntry() {}
    
    public K getKey() {
      return Object2IntOpenHashMap.this.key[this.index];
    }
    
    public int getIntValue() {
      return Object2IntOpenHashMap.this.value[this.index];
    }
    
    public int setValue(int v) {
      int oldValue = Object2IntOpenHashMap.this.value[this.index];
      Object2IntOpenHashMap.this.value[this.index] = v;
      return oldValue;
    }
    
    @Deprecated
    public Integer getValue() {
      return Integer.valueOf(Object2IntOpenHashMap.this.value[this.index]);
    }
    
    @Deprecated
    public Integer setValue(Integer v) {
      return Integer.valueOf(setValue(v.intValue()));
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<K, Integer> e = (Map.Entry<K, Integer>)o;
      return (Objects.equals(Object2IntOpenHashMap.this.key[this.index], e.getKey()) && Object2IntOpenHashMap.this.value[this.index] == ((Integer)e
        .getValue()).intValue());
    }
    
    public int hashCode() {
      return ((Object2IntOpenHashMap.this.key[this.index] == null) ? 0 : Object2IntOpenHashMap.this.key[this.index].hashCode()) ^ Object2IntOpenHashMap.this.value[this.index];
    }
    
    public String toString() {
      return (new StringBuilder()).append(Object2IntOpenHashMap.this.key[this.index]).append("=>").append(Object2IntOpenHashMap.this.value[this.index]).toString();
    }
  }
  
  private class MapIterator {
    int pos = Object2IntOpenHashMap.this.n;
    
    int last = -1;
    
    int c = Object2IntOpenHashMap.this.size;
    
    boolean mustReturnNullKey = Object2IntOpenHashMap.this.containsNullKey;
    
    ObjectArrayList<K> wrapped;
    
    public boolean hasNext() {
      return (this.c != 0);
    }
    
    public int nextEntry() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.c--;
      if (this.mustReturnNullKey) {
        this.mustReturnNullKey = false;
        return this.last = Object2IntOpenHashMap.this.n;
      } 
      K[] key = Object2IntOpenHashMap.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          K k = (K)this.wrapped.get(-this.pos - 1);
          int p = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask;
          while (!k.equals(key[p]))
            p = p + 1 & Object2IntOpenHashMap.this.mask; 
          return p;
        } 
        if (key[this.pos] != null)
          return this.last = this.pos; 
      } 
    }
    
    private void shiftKeys(int pos) {
      K[] key = Object2IntOpenHashMap.this.key;
      while (true) {
        K curr;
        int last;
        pos = (last = pos) + 1 & Object2IntOpenHashMap.this.mask;
        while (true) {
          if ((curr = key[pos]) == null) {
            key[last] = null;
            return;
          } 
          int slot = HashCommon.mix(curr.hashCode()) & Object2IntOpenHashMap.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & Object2IntOpenHashMap.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new ObjectArrayList(2); 
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
        Object2IntOpenHashMap.this.value[last] = Object2IntOpenHashMap.this.value[pos];
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == Object2IntOpenHashMap.this.n) {
        Object2IntOpenHashMap.this.containsNullKey = false;
        Object2IntOpenHashMap.this.key[Object2IntOpenHashMap.this.n] = null;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        Object2IntOpenHashMap.this.removeInt(this.wrapped.set(-this.pos - 1, null));
        this.last = -1;
        return;
      } 
      Object2IntOpenHashMap.this.size--;
      this.last = -1;
    }
    
    public int skip(int n) {
      int i = n;
      while (i-- != 0 && hasNext())
        nextEntry(); 
      return n - i - 1;
    }
    
    private MapIterator() {}
  }
  
  private class EntryIterator extends MapIterator implements ObjectIterator<Object2IntMap.Entry<K>> {
    private Object2IntOpenHashMap<K>.MapEntry entry;
    
    private EntryIterator() {}
    
    public Object2IntOpenHashMap<K>.MapEntry next() {
      return this.entry = new Object2IntOpenHashMap.MapEntry(nextEntry());
    }
    
    public void remove() {
      super.remove();
      this.entry.index = -1;
    }
  }
  
  private final class MapEntrySet extends AbstractObjectSet<Object2IntMap.Entry<K>> implements Object2IntMap.FastEntrySet<K> {
    private MapEntrySet() {}
    
    public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
      return new Object2IntOpenHashMap.EntryIterator();
    }
    
    public ObjectIterator<Object2IntMap.Entry<K>> fastIterator() {
      return (ObjectIterator<Object2IntMap.Entry<K>>)new Object2IntOpenHashMap.FastEntryIterator(Object2IntOpenHashMap.this, null);
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      K k = (K)e.getKey();
      int v = ((Integer)e.getValue()).intValue();
      if (k == null)
        return (Object2IntOpenHashMap.this.containsNullKey && Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n] == v); 
      K[] key = Object2IntOpenHashMap.this.key;
      K curr;
      int pos;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask]) == null)
        return false; 
      if (k.equals(curr))
        return (Object2IntOpenHashMap.this.value[pos] == v); 
      while (true) {
        if ((curr = key[pos = pos + 1 & Object2IntOpenHashMap.this.mask]) == null)
          return false; 
        if (k.equals(curr))
          return (Object2IntOpenHashMap.this.value[pos] == v); 
      } 
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      K k = (K)e.getKey();
      int v = ((Integer)e.getValue()).intValue();
      if (k == null) {
        if (Object2IntOpenHashMap.this.containsNullKey && Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n] == v) {
          Object2IntOpenHashMap.this.removeNullEntry();
          return true;
        } 
        return false;
      } 
      K[] key = Object2IntOpenHashMap.this.key;
      K curr;
      int pos;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask]) == null)
        return false; 
      if (curr.equals(k)) {
        if (Object2IntOpenHashMap.this.value[pos] == v) {
          Object2IntOpenHashMap.this.removeEntry(pos);
          return true;
        } 
        return false;
      } 
      while (true) {
        if ((curr = key[pos = pos + 1 & Object2IntOpenHashMap.this.mask]) == null)
          return false; 
        if (curr.equals(k) && 
          Object2IntOpenHashMap.this.value[pos] == v) {
          Object2IntOpenHashMap.this.removeEntry(pos);
          return true;
        } 
      } 
    }
    
    public int size() {
      return Object2IntOpenHashMap.this.size;
    }
    
    public void clear() {
      Object2IntOpenHashMap.this.clear();
    }
    
    public void forEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
      if (Object2IntOpenHashMap.this.containsNullKey)
        consumer.accept(new AbstractObject2IntMap.BasicEntry(Object2IntOpenHashMap.this.key[Object2IntOpenHashMap.this.n], Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n])); 
      for (int pos = Object2IntOpenHashMap.this.n; pos-- != 0;) {
        if (Object2IntOpenHashMap.this.key[pos] != null)
          consumer.accept(new AbstractObject2IntMap.BasicEntry(Object2IntOpenHashMap.this.key[pos], Object2IntOpenHashMap.this.value[pos])); 
      } 
    }
    
    public void fastForEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
      AbstractObject2IntMap.BasicEntry<K> entry = new AbstractObject2IntMap.BasicEntry();
      if (Object2IntOpenHashMap.this.containsNullKey) {
        entry.key = Object2IntOpenHashMap.this.key[Object2IntOpenHashMap.this.n];
        entry.value = Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n];
        consumer.accept(entry);
      } 
      for (int pos = Object2IntOpenHashMap.this.n; pos-- != 0;) {
        if (Object2IntOpenHashMap.this.key[pos] != null) {
          entry.key = Object2IntOpenHashMap.this.key[pos];
          entry.value = Object2IntOpenHashMap.this.value[pos];
          consumer.accept(entry);
        } 
      } 
    }
  }
  
  public Object2IntMap.FastEntrySet<K> object2IntEntrySet() {
    if (this.entries == null)
      this.entries = new MapEntrySet(); 
    return this.entries;
  }
  
  public ObjectSet<K> keySet() {
    if (this.keys == null)
      this.keys = (ObjectSet<K>)new KeySet(this, null); 
    return this.keys;
  }
  
  public IntCollection values() {
    if (this.values == null)
      this.values = (IntCollection)new Object(this); 
    return this.values;
  }
  
  public boolean trim() {
    return trim(this.size);
  }
  
  public boolean trim(int n) {
    int l = HashCommon.nextPowerOfTwo((int)Math.ceil((n / this.f)));
    if (l >= this.n || this.size > HashCommon.maxFill(l, this.f))
      return true; 
    try {
      rehash(l);
    } catch (OutOfMemoryError cantDoIt) {
      return false;
    } 
    return true;
  }
  
  protected void rehash(int newN) {
    K[] key = this.key;
    int[] value = this.value;
    int mask = newN - 1;
    K[] newKey = (K[])new Object[newN + 1];
    int[] newValue = new int[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == null);
      int pos;
      if (newKey[pos = HashCommon.mix(key[i].hashCode()) & mask] != null)
        while (newKey[pos = pos + 1 & mask] != null); 
      newKey[pos] = key[i];
      newValue[pos] = value[i];
    } 
    newValue[newN] = value[this.n];
    this.n = newN;
    this.mask = mask;
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.key = newKey;
    this.value = newValue;
  }
  
  public Object2IntOpenHashMap<K> clone() {
    Object2IntOpenHashMap<K> c;
    try {
      c = (Object2IntOpenHashMap<K>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.keys = null;
    c.values = null;
    c.entries = null;
    c.containsNullKey = this.containsNullKey;
    c.key = (K[])this.key.clone();
    c.value = (int[])this.value.clone();
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0, t = 0; j-- != 0; ) {
      while (this.key[i] == null)
        i++; 
      if (this != this.key[i])
        t = this.key[i].hashCode(); 
      t ^= this.value[i];
      h += t;
      i++;
    } 
    if (this.containsNullKey)
      h += this.value[this.n]; 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    K[] key = this.key;
    int[] value = this.value;
    MapIterator i = new MapIterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0; ) {
      int e = i.nextEntry();
      s.writeObject(key[e]);
      s.writeInt(value[e]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    K[] key = this.key = (K[])new Object[this.n + 1];
    int[] value = this.value = new int[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos;
      K k = (K)s.readObject();
      int v = s.readInt();
      if (k == null) {
        pos = this.n;
        this.containsNullKey = true;
      } else {
        pos = HashCommon.mix(k.hashCode()) & this.mask;
        while (key[pos] != null)
          pos = pos + 1 & this.mask; 
      } 
      key[pos] = k;
      value[pos] = v;
    } 
  }
  
  private void checkTable() {}
  
  private final class Object2IntOpenHashMap {}
  
  private final class Object2IntOpenHashMap {}
  
  private final class Object2IntOpenHashMap {}
  
  private class Object2IntOpenHashMap {}
}
