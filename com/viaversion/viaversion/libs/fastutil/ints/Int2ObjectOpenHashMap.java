package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
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
import java.util.function.IntFunction;

public class Int2ObjectOpenHashMap<V> extends AbstractInt2ObjectMap<V> implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient int[] key;
  
  protected transient V[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  protected transient Int2ObjectMap.FastEntrySet<V> entries;
  
  protected transient IntSet keys;
  
  protected transient ObjectCollection<V> values;
  
  public Int2ObjectOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = new int[this.n + 1];
    this.value = (V[])new Object[this.n + 1];
  }
  
  public Int2ObjectOpenHashMap(int expected) {
    this(expected, 0.75F);
  }
  
  public Int2ObjectOpenHashMap() {
    this(16, 0.75F);
  }
  
  public Int2ObjectOpenHashMap(Map<? extends Integer, ? extends V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2ObjectOpenHashMap(Map<? extends Integer, ? extends V> m) {
    this(m, 0.75F);
  }
  
  public Int2ObjectOpenHashMap(Int2ObjectMap<V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2ObjectOpenHashMap(Int2ObjectMap<V> m) {
    this(m, 0.75F);
  }
  
  public Int2ObjectOpenHashMap(int[] k, V[] v, float f) {
    this(k.length, f);
    if (k.length != v.length)
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")"); 
    for (int i = 0; i < k.length; i++)
      put(k[i], v[i]); 
  }
  
  public Int2ObjectOpenHashMap(int[] k, V[] v) {
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
  
  private V removeEntry(int pos) {
    V oldValue = this.value[pos];
    this.value[pos] = null;
    this.size--;
    shiftKeys(pos);
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  private V removeNullEntry() {
    this.containsNullKey = false;
    V oldValue = this.value[this.n];
    this.value[this.n] = null;
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  public void putAll(Map<? extends Integer, ? extends V> m) {
    if (this.f <= 0.5D) {
      ensureCapacity(m.size());
    } else {
      tryCapacity((size() + m.size()));
    } 
    super.putAll(m);
  }
  
  private int find(int k) {
    if (k == 0)
      return this.containsNullKey ? this.n : -(this.n + 1); 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return -(pos + 1); 
    if (k == curr)
      return pos; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return -(pos + 1); 
      if (k == curr)
        return pos; 
    } 
  }
  
  private void insert(int pos, int k, V v) {
    if (pos == this.n)
      this.containsNullKey = true; 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
  }
  
  public V put(int k, V v) {
    int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return (V)this.defRetValue;
    } 
    V oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  protected final void shiftKeys(int pos) {
    int[] key = this.key;
    while (true) {
      int curr, last;
      pos = (last = pos) + 1 & this.mask;
      while (true) {
        if ((curr = key[pos]) == 0) {
          key[last] = 0;
          this.value[last] = null;
          return;
        } 
        int slot = HashCommon.mix(curr) & this.mask;
        if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
          break; 
        pos = pos + 1 & this.mask;
      } 
      key[last] = curr;
      this.value[last] = this.value[pos];
    } 
  }
  
  public V remove(int k) {
    if (k == 0) {
      if (this.containsNullKey)
        return removeNullEntry(); 
      return (V)this.defRetValue;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return (V)this.defRetValue; 
    if (k == curr)
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return (V)this.defRetValue; 
      if (k == curr)
        return removeEntry(pos); 
    } 
  }
  
  public V get(int k) {
    if (k == 0)
      return this.containsNullKey ? this.value[this.n] : (V)this.defRetValue; 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return (V)this.defRetValue; 
    if (k == curr)
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return (V)this.defRetValue; 
      if (k == curr)
        return this.value[pos]; 
    } 
  }
  
  public boolean containsKey(int k) {
    if (k == 0)
      return this.containsNullKey; 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return false; 
    if (k == curr)
      return true; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return false; 
      if (k == curr)
        return true; 
    } 
  }
  
  public boolean containsValue(Object v) {
    V[] value = this.value;
    int[] key = this.key;
    if (this.containsNullKey && Objects.equals(value[this.n], v))
      return true; 
    for (int i = this.n; i-- != 0;) {
      if (key[i] != 0 && Objects.equals(value[i], v))
        return true; 
    } 
    return false;
  }
  
  public V getOrDefault(int k, V defaultValue) {
    if (k == 0)
      return this.containsNullKey ? this.value[this.n] : defaultValue; 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return defaultValue; 
    if (k == curr)
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return defaultValue; 
      if (k == curr)
        return this.value[pos]; 
    } 
  }
  
  public V putIfAbsent(int k, V v) {
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    insert(-pos - 1, k, v);
    return (V)this.defRetValue;
  }
  
  public boolean remove(int k, Object v) {
    if (k == 0) {
      if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
        removeNullEntry();
        return true;
      } 
      return false;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return false; 
    if (k == curr && Objects.equals(v, this.value[pos])) {
      removeEntry(pos);
      return true;
    } 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return false; 
      if (k == curr && Objects.equals(v, this.value[pos])) {
        removeEntry(pos);
        return true;
      } 
    } 
  }
  
  public boolean replace(int k, V oldValue, V v) {
    int pos = find(k);
    if (pos < 0 || !Objects.equals(oldValue, this.value[pos]))
      return false; 
    this.value[pos] = v;
    return true;
  }
  
  public V replace(int k, V v) {
    int pos = find(k);
    if (pos < 0)
      return (V)this.defRetValue; 
    V oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  public V computeIfAbsent(int k, IntFunction<? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    V newValue = mappingFunction.apply(k);
    insert(-pos - 1, k, newValue);
    return newValue;
  }
  
  public V computeIfPresent(int k, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0)
      return (V)this.defRetValue; 
    V newValue = remappingFunction.apply(Integer.valueOf(k), this.value[pos]);
    if (newValue == null) {
      if (k == 0) {
        removeNullEntry();
      } else {
        removeEntry(pos);
      } 
      return (V)this.defRetValue;
    } 
    this.value[pos] = newValue;
    return newValue;
  }
  
  public V compute(int k, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    V newValue = remappingFunction.apply(Integer.valueOf(k), (pos >= 0) ? this.value[pos] : null);
    if (newValue == null) {
      if (pos >= 0)
        if (k == 0) {
          removeNullEntry();
        } else {
          removeEntry(pos);
        }  
      return (V)this.defRetValue;
    } 
    V newVal = newValue;
    if (pos < 0) {
      insert(-pos - 1, k, newVal);
      return newVal;
    } 
    this.value[pos] = newVal;
    return newVal;
  }
  
  public V merge(int k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0 || this.value[pos] == null) {
      if (v == null)
        return (V)this.defRetValue; 
      insert(-pos - 1, k, v);
      return v;
    } 
    V newValue = remappingFunction.apply(this.value[pos], v);
    if (newValue == null) {
      if (k == 0) {
        removeNullEntry();
      } else {
        removeEntry(pos);
      } 
      return (V)this.defRetValue;
    } 
    this.value[pos] = newValue;
    return newValue;
  }
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNullKey = false;
    Arrays.fill(this.key, 0);
    Arrays.fill((Object[])this.value, (Object)null);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  final class MapEntry implements Int2ObjectMap.Entry<V>, Map.Entry<Integer, V> {
    int index;
    
    MapEntry(int index) {
      this.index = index;
    }
    
    MapEntry() {}
    
    public int getIntKey() {
      return Int2ObjectOpenHashMap.this.key[this.index];
    }
    
    public V getValue() {
      return Int2ObjectOpenHashMap.this.value[this.index];
    }
    
    public V setValue(V v) {
      V oldValue = Int2ObjectOpenHashMap.this.value[this.index];
      Int2ObjectOpenHashMap.this.value[this.index] = v;
      return oldValue;
    }
    
    @Deprecated
    public Integer getKey() {
      return Integer.valueOf(Int2ObjectOpenHashMap.this.key[this.index]);
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<Integer, V> e = (Map.Entry<Integer, V>)o;
      return (Int2ObjectOpenHashMap.this.key[this.index] == ((Integer)e.getKey()).intValue() && 
        Objects.equals(Int2ObjectOpenHashMap.this.value[this.index], e.getValue()));
    }
    
    public int hashCode() {
      return Int2ObjectOpenHashMap.this.key[this.index] ^ ((Int2ObjectOpenHashMap.this.value[this.index] == null) ? 0 : Int2ObjectOpenHashMap.this.value[this.index].hashCode());
    }
    
    public String toString() {
      return Int2ObjectOpenHashMap.this.key[this.index] + "=>" + Int2ObjectOpenHashMap.this.value[this.index];
    }
  }
  
  private class MapIterator {
    int pos = Int2ObjectOpenHashMap.this.n;
    
    int last = -1;
    
    int c = Int2ObjectOpenHashMap.this.size;
    
    boolean mustReturnNullKey = Int2ObjectOpenHashMap.this.containsNullKey;
    
    IntArrayList wrapped;
    
    public boolean hasNext() {
      return (this.c != 0);
    }
    
    public int nextEntry() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.c--;
      if (this.mustReturnNullKey) {
        this.mustReturnNullKey = false;
        return this.last = Int2ObjectOpenHashMap.this.n;
      } 
      int[] key = Int2ObjectOpenHashMap.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          int k = this.wrapped.getInt(-this.pos - 1);
          int p = HashCommon.mix(k) & Int2ObjectOpenHashMap.this.mask;
          while (k != key[p])
            p = p + 1 & Int2ObjectOpenHashMap.this.mask; 
          return p;
        } 
        if (key[this.pos] != 0)
          return this.last = this.pos; 
      } 
    }
    
    private void shiftKeys(int pos) {
      int[] key = Int2ObjectOpenHashMap.this.key;
      while (true) {
        int curr, last;
        pos = (last = pos) + 1 & Int2ObjectOpenHashMap.this.mask;
        while (true) {
          if ((curr = key[pos]) == 0) {
            key[last] = 0;
            Int2ObjectOpenHashMap.this.value[last] = null;
            return;
          } 
          int slot = HashCommon.mix(curr) & Int2ObjectOpenHashMap.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & Int2ObjectOpenHashMap.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new IntArrayList(2); 
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
        Int2ObjectOpenHashMap.this.value[last] = Int2ObjectOpenHashMap.this.value[pos];
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == Int2ObjectOpenHashMap.this.n) {
        Int2ObjectOpenHashMap.this.containsNullKey = false;
        Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n] = null;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        Int2ObjectOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
        this.last = -1;
        return;
      } 
      Int2ObjectOpenHashMap.this.size--;
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
  
  private class EntryIterator extends MapIterator implements ObjectIterator<Int2ObjectMap.Entry<V>> {
    private Int2ObjectOpenHashMap<V>.MapEntry entry;
    
    private EntryIterator() {}
    
    public Int2ObjectOpenHashMap<V>.MapEntry next() {
      return this.entry = new Int2ObjectOpenHashMap.MapEntry(nextEntry());
    }
    
    public void remove() {
      super.remove();
      this.entry.index = -1;
    }
  }
  
  private final class MapEntrySet extends AbstractObjectSet<Int2ObjectMap.Entry<V>> implements Int2ObjectMap.FastEntrySet<V> {
    private MapEntrySet() {}
    
    public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
      return new Int2ObjectOpenHashMap.EntryIterator();
    }
    
    public ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator() {
      return (ObjectIterator<Int2ObjectMap.Entry<V>>)new Int2ObjectOpenHashMap.FastEntryIterator(Int2ObjectOpenHashMap.this, null);
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      V v = (V)e.getValue();
      if (k == 0)
        return (Int2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n], v)); 
      int[] key = Int2ObjectOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2ObjectOpenHashMap.this.mask]) == 0)
        return false; 
      if (k == curr)
        return Objects.equals(Int2ObjectOpenHashMap.this.value[pos], v); 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2ObjectOpenHashMap.this.mask]) == 0)
          return false; 
        if (k == curr)
          return Objects.equals(Int2ObjectOpenHashMap.this.value[pos], v); 
      } 
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      V v = (V)e.getValue();
      if (k == 0) {
        if (Int2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n], v)) {
          Int2ObjectOpenHashMap.this.removeNullEntry();
          return true;
        } 
        return false;
      } 
      int[] key = Int2ObjectOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2ObjectOpenHashMap.this.mask]) == 0)
        return false; 
      if (curr == k) {
        if (Objects.equals(Int2ObjectOpenHashMap.this.value[pos], v)) {
          Int2ObjectOpenHashMap.this.removeEntry(pos);
          return true;
        } 
        return false;
      } 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2ObjectOpenHashMap.this.mask]) == 0)
          return false; 
        if (curr == k && 
          Objects.equals(Int2ObjectOpenHashMap.this.value[pos], v)) {
          Int2ObjectOpenHashMap.this.removeEntry(pos);
          return true;
        } 
      } 
    }
    
    public int size() {
      return Int2ObjectOpenHashMap.this.size;
    }
    
    public void clear() {
      Int2ObjectOpenHashMap.this.clear();
    }
    
    public void forEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      if (Int2ObjectOpenHashMap.this.containsNullKey)
        consumer.accept(new AbstractInt2ObjectMap.BasicEntry(Int2ObjectOpenHashMap.this.key[Int2ObjectOpenHashMap.this.n], Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n])); 
      for (int pos = Int2ObjectOpenHashMap.this.n; pos-- != 0;) {
        if (Int2ObjectOpenHashMap.this.key[pos] != 0)
          consumer.accept(new AbstractInt2ObjectMap.BasicEntry(Int2ObjectOpenHashMap.this.key[pos], Int2ObjectOpenHashMap.this.value[pos])); 
      } 
    }
    
    public void fastForEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      AbstractInt2ObjectMap.BasicEntry<V> entry = new AbstractInt2ObjectMap.BasicEntry();
      if (Int2ObjectOpenHashMap.this.containsNullKey) {
        entry.key = Int2ObjectOpenHashMap.this.key[Int2ObjectOpenHashMap.this.n];
        entry.value = Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n];
        consumer.accept(entry);
      } 
      for (int pos = Int2ObjectOpenHashMap.this.n; pos-- != 0;) {
        if (Int2ObjectOpenHashMap.this.key[pos] != 0) {
          entry.key = Int2ObjectOpenHashMap.this.key[pos];
          entry.value = Int2ObjectOpenHashMap.this.value[pos];
          consumer.accept(entry);
        } 
      } 
    }
  }
  
  public Int2ObjectMap.FastEntrySet<V> int2ObjectEntrySet() {
    if (this.entries == null)
      this.entries = new MapEntrySet(); 
    return this.entries;
  }
  
  public IntSet keySet() {
    if (this.keys == null)
      this.keys = (IntSet)new KeySet(this, null); 
    return this.keys;
  }
  
  private final class ValueIterator extends MapIterator implements ObjectIterator<V> {
    public V next() {
      return Int2ObjectOpenHashMap.this.value[nextEntry()];
    }
  }
  
  public ObjectCollection<V> values() {
    if (this.values == null)
      this.values = (ObjectCollection<V>)new AbstractObjectCollection<V>() {
          public ObjectIterator<V> iterator() {
            return new Int2ObjectOpenHashMap.ValueIterator();
          }
          
          public int size() {
            return Int2ObjectOpenHashMap.this.size;
          }
          
          public boolean contains(Object v) {
            return Int2ObjectOpenHashMap.this.containsValue(v);
          }
          
          public void clear() {
            Int2ObjectOpenHashMap.this.clear();
          }
          
          public void forEach(Consumer<? super V> consumer) {
            if (Int2ObjectOpenHashMap.this.containsNullKey)
              consumer.accept(Int2ObjectOpenHashMap.this.value[Int2ObjectOpenHashMap.this.n]); 
            for (int pos = Int2ObjectOpenHashMap.this.n; pos-- != 0;) {
              if (Int2ObjectOpenHashMap.this.key[pos] != 0)
                consumer.accept(Int2ObjectOpenHashMap.this.value[pos]); 
            } 
          }
        }; 
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
    int[] key = this.key;
    V[] value = this.value;
    int mask = newN - 1;
    int[] newKey = new int[newN + 1];
    V[] newValue = (V[])new Object[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == 0);
      int pos;
      if (newKey[pos = HashCommon.mix(key[i]) & mask] != 0)
        while (newKey[pos = pos + 1 & mask] != 0); 
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
  
  public Int2ObjectOpenHashMap<V> clone() {
    Int2ObjectOpenHashMap<V> c;
    try {
      c = (Int2ObjectOpenHashMap<V>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.keys = null;
    c.values = null;
    c.entries = null;
    c.containsNullKey = this.containsNullKey;
    c.key = (int[])this.key.clone();
    c.value = (V[])this.value.clone();
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0, t = 0; j-- != 0; ) {
      while (this.key[i] == 0)
        i++; 
      t = this.key[i];
      if (this != this.value[i])
        t ^= (this.value[i] == null) ? 0 : this.value[i].hashCode(); 
      h += t;
      i++;
    } 
    if (this.containsNullKey)
      h += (this.value[this.n] == null) ? 0 : this.value[this.n].hashCode(); 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    int[] key = this.key;
    V[] value = this.value;
    MapIterator i = new MapIterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0; ) {
      int e = i.nextEntry();
      s.writeInt(key[e]);
      s.writeObject(value[e]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    int[] key = this.key = new int[this.n + 1];
    V[] value = this.value = (V[])new Object[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos, k = s.readInt();
      V v = (V)s.readObject();
      if (k == 0) {
        pos = this.n;
        this.containsNullKey = true;
      } else {
        pos = HashCommon.mix(k) & this.mask;
        while (key[pos] != 0)
          pos = pos + 1 & this.mask; 
      } 
      key[pos] = k;
      value[pos] = v;
    } 
  }
  
  private void checkTable() {}
  
  private final class Int2ObjectOpenHashMap {}
  
  private final class Int2ObjectOpenHashMap {}
  
  private class Int2ObjectOpenHashMap {}
}
