package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSortedSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectListIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class Int2ObjectLinkedOpenHashMap<V> extends AbstractInt2ObjectSortedMap<V> implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient int[] key;
  
  protected transient V[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int first = -1;
  
  protected transient int last = -1;
  
  protected transient long[] link;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  protected transient Int2ObjectSortedMap.FastSortedEntrySet<V> entries;
  
  protected transient IntSortedSet keys;
  
  protected transient ObjectCollection<V> values;
  
  public Int2ObjectLinkedOpenHashMap(int expected, float f) {
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
    this.link = new long[this.n + 1];
  }
  
  public Int2ObjectLinkedOpenHashMap(int expected) {
    this(expected, 0.75F);
  }
  
  public Int2ObjectLinkedOpenHashMap() {
    this(16, 0.75F);
  }
  
  public Int2ObjectLinkedOpenHashMap(Map<? extends Integer, ? extends V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2ObjectLinkedOpenHashMap(Map<? extends Integer, ? extends V> m) {
    this(m, 0.75F);
  }
  
  public Int2ObjectLinkedOpenHashMap(Int2ObjectMap<V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2ObjectLinkedOpenHashMap(Int2ObjectMap<V> m) {
    this(m, 0.75F);
  }
  
  public Int2ObjectLinkedOpenHashMap(int[] k, V[] v, float f) {
    this(k.length, f);
    if (k.length != v.length)
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")"); 
    for (int i = 0; i < k.length; i++)
      put(k[i], v[i]); 
  }
  
  public Int2ObjectLinkedOpenHashMap(int[] k, V[] v) {
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
    fixPointers(pos);
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
    fixPointers(this.n);
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
    if (this.size == 0) {
      this.first = this.last = pos;
      this.link[pos] = -1L;
    } else {
      this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      this.link[pos] = (this.last & 0xFFFFFFFFL) << 32L | 0xFFFFFFFFL;
      this.last = pos;
    } 
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
      fixPointers(pos, last);
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
  
  private V setValue(int pos, V v) {
    V oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  public V removeFirst() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    int pos = this.first;
    this.first = (int)this.link[pos];
    if (0 <= this.first)
      this.link[this.first] = this.link[this.first] | 0xFFFFFFFF00000000L; 
    this.size--;
    V v = this.value[pos];
    if (pos == this.n) {
      this.containsNullKey = false;
      this.value[this.n] = null;
    } else {
      shiftKeys(pos);
    } 
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return v;
  }
  
  public V removeLast() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    int pos = this.last;
    this.last = (int)(this.link[pos] >>> 32L);
    if (0 <= this.last)
      this.link[this.last] = this.link[this.last] | 0xFFFFFFFFL; 
    this.size--;
    V v = this.value[pos];
    if (pos == this.n) {
      this.containsNullKey = false;
      this.value[this.n] = null;
    } else {
      shiftKeys(pos);
    } 
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return v;
  }
  
  private void moveIndexToFirst(int i) {
    if (this.size == 1 || this.first == i)
      return; 
    if (this.last == i) {
      this.last = (int)(this.link[i] >>> 32L);
      this.link[this.last] = this.link[this.last] | 0xFFFFFFFFL;
    } else {
      long linki = this.link[i];
      int prev = (int)(linki >>> 32L);
      int next = (int)linki;
      this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      this.link[next] = this.link[next] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
    } 
    this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (i & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
    this.link[i] = 0xFFFFFFFF00000000L | this.first & 0xFFFFFFFFL;
    this.first = i;
  }
  
  private void moveIndexToLast(int i) {
    if (this.size == 1 || this.last == i)
      return; 
    if (this.first == i) {
      this.first = (int)this.link[i];
      this.link[this.first] = this.link[this.first] | 0xFFFFFFFF00000000L;
    } else {
      long linki = this.link[i];
      int prev = (int)(linki >>> 32L);
      int next = (int)linki;
      this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      this.link[next] = this.link[next] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
    } 
    this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ i & 0xFFFFFFFFL) & 0xFFFFFFFFL;
    this.link[i] = (this.last & 0xFFFFFFFFL) << 32L | 0xFFFFFFFFL;
    this.last = i;
  }
  
  public V getAndMoveToFirst(int k) {
    if (k == 0) {
      if (this.containsNullKey) {
        moveIndexToFirst(this.n);
        return this.value[this.n];
      } 
      return (V)this.defRetValue;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return (V)this.defRetValue; 
    if (k == curr) {
      moveIndexToFirst(pos);
      return this.value[pos];
    } 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return (V)this.defRetValue; 
      if (k == curr) {
        moveIndexToFirst(pos);
        return this.value[pos];
      } 
    } 
  }
  
  public V getAndMoveToLast(int k) {
    if (k == 0) {
      if (this.containsNullKey) {
        moveIndexToLast(this.n);
        return this.value[this.n];
      } 
      return (V)this.defRetValue;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return (V)this.defRetValue; 
    if (k == curr) {
      moveIndexToLast(pos);
      return this.value[pos];
    } 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return (V)this.defRetValue; 
      if (k == curr) {
        moveIndexToLast(pos);
        return this.value[pos];
      } 
    } 
  }
  
  public V putAndMoveToFirst(int k, V v) {
    int pos;
    if (k == 0) {
      if (this.containsNullKey) {
        moveIndexToFirst(this.n);
        return setValue(this.n, v);
      } 
      this.containsNullKey = true;
      pos = this.n;
    } else {
      int[] key = this.key;
      int curr;
      if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
        if (curr == k) {
          moveIndexToFirst(pos);
          return setValue(pos, v);
        } 
        while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
          if (curr == k) {
            moveIndexToFirst(pos);
            return setValue(pos, v);
          } 
        } 
      } 
    } 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size == 0) {
      this.first = this.last = pos;
      this.link[pos] = -1L;
    } else {
      this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (pos & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
      this.link[pos] = 0xFFFFFFFF00000000L | this.first & 0xFFFFFFFFL;
      this.first = pos;
    } 
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size, this.f)); 
    return (V)this.defRetValue;
  }
  
  public V putAndMoveToLast(int k, V v) {
    int pos;
    if (k == 0) {
      if (this.containsNullKey) {
        moveIndexToLast(this.n);
        return setValue(this.n, v);
      } 
      this.containsNullKey = true;
      pos = this.n;
    } else {
      int[] key = this.key;
      int curr;
      if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
        if (curr == k) {
          moveIndexToLast(pos);
          return setValue(pos, v);
        } 
        while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
          if (curr == k) {
            moveIndexToLast(pos);
            return setValue(pos, v);
          } 
        } 
      } 
    } 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size == 0) {
      this.first = this.last = pos;
      this.link[pos] = -1L;
    } else {
      this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      this.link[pos] = (this.last & 0xFFFFFFFFL) << 32L | 0xFFFFFFFFL;
      this.last = pos;
    } 
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size, this.f)); 
    return (V)this.defRetValue;
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
    this.first = this.last = -1;
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
      return Int2ObjectLinkedOpenHashMap.this.key[this.index];
    }
    
    public V getValue() {
      return Int2ObjectLinkedOpenHashMap.this.value[this.index];
    }
    
    public V setValue(V v) {
      V oldValue = Int2ObjectLinkedOpenHashMap.this.value[this.index];
      Int2ObjectLinkedOpenHashMap.this.value[this.index] = v;
      return oldValue;
    }
    
    @Deprecated
    public Integer getKey() {
      return Integer.valueOf(Int2ObjectLinkedOpenHashMap.this.key[this.index]);
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<Integer, V> e = (Map.Entry<Integer, V>)o;
      return (Int2ObjectLinkedOpenHashMap.this.key[this.index] == ((Integer)e.getKey()).intValue() && 
        Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[this.index], e.getValue()));
    }
    
    public int hashCode() {
      return Int2ObjectLinkedOpenHashMap.this.key[this.index] ^ ((Int2ObjectLinkedOpenHashMap.this.value[this.index] == null) ? 0 : Int2ObjectLinkedOpenHashMap.this.value[this.index].hashCode());
    }
    
    public String toString() {
      return Int2ObjectLinkedOpenHashMap.this.key[this.index] + "=>" + Int2ObjectLinkedOpenHashMap.this.value[this.index];
    }
  }
  
  protected void fixPointers(int i) {
    if (this.size == 0) {
      this.first = this.last = -1;
      return;
    } 
    if (this.first == i) {
      this.first = (int)this.link[i];
      if (0 <= this.first)
        this.link[this.first] = this.link[this.first] | 0xFFFFFFFF00000000L; 
      return;
    } 
    if (this.last == i) {
      this.last = (int)(this.link[i] >>> 32L);
      if (0 <= this.last)
        this.link[this.last] = this.link[this.last] | 0xFFFFFFFFL; 
      return;
    } 
    long linki = this.link[i];
    int prev = (int)(linki >>> 32L);
    int next = (int)linki;
    this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
    this.link[next] = this.link[next] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
  }
  
  protected void fixPointers(int s, int d) {
    if (this.size == 1) {
      this.first = this.last = d;
      this.link[d] = -1L;
      return;
    } 
    if (this.first == s) {
      this.first = d;
      this.link[(int)this.link[s]] = this.link[(int)this.link[s]] ^ (this.link[(int)this.link[s]] ^ (d & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
      this.link[d] = this.link[s];
      return;
    } 
    if (this.last == s) {
      this.last = d;
      this.link[(int)(this.link[s] >>> 32L)] = this.link[(int)(this.link[s] >>> 32L)] ^ (this.link[(int)(this.link[s] >>> 32L)] ^ d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      this.link[d] = this.link[s];
      return;
    } 
    long links = this.link[s];
    int prev = (int)(links >>> 32L);
    int next = (int)links;
    this.link[prev] = this.link[prev] ^ (this.link[prev] ^ d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
    this.link[next] = this.link[next] ^ (this.link[next] ^ (d & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
    this.link[d] = links;
  }
  
  public int firstIntKey() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    return this.key[this.first];
  }
  
  public int lastIntKey() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    return this.key[this.last];
  }
  
  public Int2ObjectSortedMap<V> tailMap(int from) {
    throw new UnsupportedOperationException();
  }
  
  public Int2ObjectSortedMap<V> headMap(int to) {
    throw new UnsupportedOperationException();
  }
  
  public Int2ObjectSortedMap<V> subMap(int from, int to) {
    throw new UnsupportedOperationException();
  }
  
  public IntComparator comparator() {
    return null;
  }
  
  private class MapIterator {
    int prev = -1;
    
    int next = -1;
    
    int curr = -1;
    
    int index = -1;
    
    protected MapIterator() {
      this.next = Int2ObjectLinkedOpenHashMap.this.first;
      this.index = 0;
    }
    
    private MapIterator(int from) {
      if (from == 0) {
        if (Int2ObjectLinkedOpenHashMap.this.containsNullKey) {
          this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[Int2ObjectLinkedOpenHashMap.this.n];
          this.prev = Int2ObjectLinkedOpenHashMap.this.n;
          return;
        } 
        throw new NoSuchElementException("The key " + from + " does not belong to this map.");
      } 
      if (Int2ObjectLinkedOpenHashMap.this.key[Int2ObjectLinkedOpenHashMap.this.last] == from) {
        this.prev = Int2ObjectLinkedOpenHashMap.this.last;
        this.index = Int2ObjectLinkedOpenHashMap.this.size;
        return;
      } 
      int pos = HashCommon.mix(from) & Int2ObjectLinkedOpenHashMap.this.mask;
      while (Int2ObjectLinkedOpenHashMap.this.key[pos] != 0) {
        if (Int2ObjectLinkedOpenHashMap.this.key[pos] == from) {
          this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[pos];
          this.prev = pos;
          return;
        } 
        pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
      } 
      throw new NoSuchElementException("The key " + from + " does not belong to this map.");
    }
    
    public boolean hasNext() {
      return (this.next != -1);
    }
    
    public boolean hasPrevious() {
      return (this.prev != -1);
    }
    
    private final void ensureIndexKnown() {
      if (this.index >= 0)
        return; 
      if (this.prev == -1) {
        this.index = 0;
        return;
      } 
      if (this.next == -1) {
        this.index = Int2ObjectLinkedOpenHashMap.this.size;
        return;
      } 
      int pos = Int2ObjectLinkedOpenHashMap.this.first;
      this.index = 1;
      while (pos != this.prev) {
        pos = (int)Int2ObjectLinkedOpenHashMap.this.link[pos];
        this.index++;
      } 
    }
    
    public int nextIndex() {
      ensureIndexKnown();
      return this.index;
    }
    
    public int previousIndex() {
      ensureIndexKnown();
      return this.index - 1;
    }
    
    public int nextEntry() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.curr = this.next;
      this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[this.curr];
      this.prev = this.curr;
      if (this.index >= 0)
        this.index++; 
      return this.curr;
    }
    
    public int previousEntry() {
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      this.curr = this.prev;
      this.prev = (int)(Int2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32L);
      this.next = this.curr;
      if (this.index >= 0)
        this.index--; 
      return this.curr;
    }
    
    public void remove() {
      ensureIndexKnown();
      if (this.curr == -1)
        throw new IllegalStateException(); 
      if (this.curr == this.prev) {
        this.index--;
        this.prev = (int)(Int2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32L);
      } else {
        this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[this.curr];
      } 
      Int2ObjectLinkedOpenHashMap.this.size--;
      if (this.prev == -1) {
        Int2ObjectLinkedOpenHashMap.this.first = this.next;
      } else {
        Int2ObjectLinkedOpenHashMap.this.link[this.prev] = Int2ObjectLinkedOpenHashMap.this.link[this.prev] ^ (Int2ObjectLinkedOpenHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
      } 
      if (this.next == -1) {
        Int2ObjectLinkedOpenHashMap.this.last = this.prev;
      } else {
        Int2ObjectLinkedOpenHashMap.this.link[this.next] = Int2ObjectLinkedOpenHashMap.this.link[this.next] ^ (Int2ObjectLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
      } 
      int pos = this.curr;
      this.curr = -1;
      if (pos == Int2ObjectLinkedOpenHashMap.this.n) {
        Int2ObjectLinkedOpenHashMap.this.containsNullKey = false;
        Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n] = null;
      } else {
        int[] key = Int2ObjectLinkedOpenHashMap.this.key;
        while (true) {
          int curr, last;
          pos = (last = pos) + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
          while (true) {
            if ((curr = key[pos]) == 0) {
              key[last] = 0;
              Int2ObjectLinkedOpenHashMap.this.value[last] = null;
              return;
            } 
            int slot = HashCommon.mix(curr) & Int2ObjectLinkedOpenHashMap.this.mask;
            if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
              break; 
            pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
          } 
          key[last] = curr;
          Int2ObjectLinkedOpenHashMap.this.value[last] = Int2ObjectLinkedOpenHashMap.this.value[pos];
          if (this.next == pos)
            this.next = last; 
          if (this.prev == pos)
            this.prev = last; 
          Int2ObjectLinkedOpenHashMap.this.fixPointers(pos, last);
        } 
      } 
    }
    
    public int skip(int n) {
      int i = n;
      while (i-- != 0 && hasNext())
        nextEntry(); 
      return n - i - 1;
    }
    
    public int back(int n) {
      int i = n;
      while (i-- != 0 && hasPrevious())
        previousEntry(); 
      return n - i - 1;
    }
    
    public void set(Int2ObjectMap.Entry<V> ok) {
      throw new UnsupportedOperationException();
    }
    
    public void add(Int2ObjectMap.Entry<V> ok) {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator extends MapIterator implements ObjectListIterator<Int2ObjectMap.Entry<V>> {
    private Int2ObjectLinkedOpenHashMap<V>.MapEntry entry;
    
    public EntryIterator() {}
    
    public EntryIterator(int from) {
      super(from);
    }
    
    public Int2ObjectLinkedOpenHashMap<V>.MapEntry next() {
      return this.entry = new Int2ObjectLinkedOpenHashMap.MapEntry(nextEntry());
    }
    
    public Int2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
      return this.entry = new Int2ObjectLinkedOpenHashMap.MapEntry(previousEntry());
    }
    
    public void remove() {
      super.remove();
      this.entry.index = -1;
    }
  }
  
  private class FastEntryIterator extends MapIterator implements ObjectListIterator<Int2ObjectMap.Entry<V>> {
    final Int2ObjectLinkedOpenHashMap<V>.MapEntry entry = new Int2ObjectLinkedOpenHashMap.MapEntry();
    
    public FastEntryIterator(int from) {
      super(from);
    }
    
    public Int2ObjectLinkedOpenHashMap<V>.MapEntry next() {
      this.entry.index = nextEntry();
      return this.entry;
    }
    
    public Int2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
      this.entry.index = previousEntry();
      return this.entry;
    }
    
    public FastEntryIterator() {}
  }
  
  private final class MapEntrySet extends AbstractObjectSortedSet<Int2ObjectMap.Entry<V>> implements Int2ObjectSortedMap.FastSortedEntrySet<V> {
    private MapEntrySet() {}
    
    public ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> iterator() {
      return (ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>>)new Int2ObjectLinkedOpenHashMap.EntryIterator();
    }
    
    public Comparator<? super Int2ObjectMap.Entry<V>> comparator() {
      return null;
    }
    
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> subSet(Int2ObjectMap.Entry<V> fromElement, Int2ObjectMap.Entry<V> toElement) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> headSet(Int2ObjectMap.Entry<V> toElement) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> tailSet(Int2ObjectMap.Entry<V> fromElement) {
      throw new UnsupportedOperationException();
    }
    
    public Int2ObjectMap.Entry<V> first() {
      if (Int2ObjectLinkedOpenHashMap.this.size == 0)
        throw new NoSuchElementException(); 
      return new Int2ObjectLinkedOpenHashMap.MapEntry(Int2ObjectLinkedOpenHashMap.this.first);
    }
    
    public Int2ObjectMap.Entry<V> last() {
      if (Int2ObjectLinkedOpenHashMap.this.size == 0)
        throw new NoSuchElementException(); 
      return new Int2ObjectLinkedOpenHashMap.MapEntry(Int2ObjectLinkedOpenHashMap.this.last);
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
        return (Int2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n], v)); 
      int[] key = Int2ObjectLinkedOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2ObjectLinkedOpenHashMap.this.mask]) == 0)
        return false; 
      if (k == curr)
        return Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v); 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask]) == 0)
          return false; 
        if (k == curr)
          return Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v); 
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
        if (Int2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n], v)) {
          Int2ObjectLinkedOpenHashMap.this.removeNullEntry();
          return true;
        } 
        return false;
      } 
      int[] key = Int2ObjectLinkedOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2ObjectLinkedOpenHashMap.this.mask]) == 0)
        return false; 
      if (curr == k) {
        if (Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v)) {
          Int2ObjectLinkedOpenHashMap.this.removeEntry(pos);
          return true;
        } 
        return false;
      } 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask]) == 0)
          return false; 
        if (curr == k && 
          Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v)) {
          Int2ObjectLinkedOpenHashMap.this.removeEntry(pos);
          return true;
        } 
      } 
    }
    
    public int size() {
      return Int2ObjectLinkedOpenHashMap.this.size;
    }
    
    public void clear() {
      Int2ObjectLinkedOpenHashMap.this.clear();
    }
    
    public ObjectListIterator<Int2ObjectMap.Entry<V>> iterator(Int2ObjectMap.Entry<V> from) {
      return new Int2ObjectLinkedOpenHashMap.EntryIterator(from.getIntKey());
    }
    
    public ObjectListIterator<Int2ObjectMap.Entry<V>> fastIterator() {
      return new Int2ObjectLinkedOpenHashMap.FastEntryIterator();
    }
    
    public ObjectListIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap.Entry<V> from) {
      return new Int2ObjectLinkedOpenHashMap.FastEntryIterator(from.getIntKey());
    }
    
    public void forEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      for (int i = Int2ObjectLinkedOpenHashMap.this.size, next = Int2ObjectLinkedOpenHashMap.this.first; i-- != 0; ) {
        int curr = next;
        next = (int)Int2ObjectLinkedOpenHashMap.this.link[curr];
        consumer.accept(new AbstractInt2ObjectMap.BasicEntry(Int2ObjectLinkedOpenHashMap.this.key[curr], Int2ObjectLinkedOpenHashMap.this.value[curr]));
      } 
    }
    
    public void fastForEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      AbstractInt2ObjectMap.BasicEntry<V> entry = new AbstractInt2ObjectMap.BasicEntry();
      for (int i = Int2ObjectLinkedOpenHashMap.this.size, next = Int2ObjectLinkedOpenHashMap.this.first; i-- != 0; ) {
        int curr = next;
        next = (int)Int2ObjectLinkedOpenHashMap.this.link[curr];
        entry.key = Int2ObjectLinkedOpenHashMap.this.key[curr];
        entry.value = Int2ObjectLinkedOpenHashMap.this.value[curr];
        consumer.accept(entry);
      } 
    }
  }
  
  public Int2ObjectSortedMap.FastSortedEntrySet<V> int2ObjectEntrySet() {
    if (this.entries == null)
      this.entries = new MapEntrySet(); 
    return this.entries;
  }
  
  public IntSortedSet keySet() {
    if (this.keys == null)
      this.keys = (IntSortedSet)new KeySet(this, null); 
    return this.keys;
  }
  
  public ObjectCollection<V> values() {
    if (this.values == null)
      this.values = (ObjectCollection<V>)new AbstractObjectCollection<V>() {
          public ObjectIterator<V> iterator() {
            return (ObjectIterator<V>)new Int2ObjectLinkedOpenHashMap.ValueIterator(Int2ObjectLinkedOpenHashMap.this);
          }
          
          public int size() {
            return Int2ObjectLinkedOpenHashMap.this.size;
          }
          
          public boolean contains(Object v) {
            return Int2ObjectLinkedOpenHashMap.this.containsValue(v);
          }
          
          public void clear() {
            Int2ObjectLinkedOpenHashMap.this.clear();
          }
          
          public void forEach(Consumer<? super V> consumer) {
            if (Int2ObjectLinkedOpenHashMap.this.containsNullKey)
              consumer.accept(Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n]); 
            for (int pos = Int2ObjectLinkedOpenHashMap.this.n; pos-- != 0;) {
              if (Int2ObjectLinkedOpenHashMap.this.key[pos] != 0)
                consumer.accept(Int2ObjectLinkedOpenHashMap.this.value[pos]); 
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
    int i = this.first, prev = -1, newPrev = -1;
    long[] link = this.link;
    long[] newLink = new long[newN + 1];
    this.first = -1;
    for (int j = this.size; j-- != 0; ) {
      int pos;
      if (key[i] == 0) {
        pos = newN;
      } else {
        pos = HashCommon.mix(key[i]) & mask;
        while (newKey[pos] != 0)
          pos = pos + 1 & mask; 
      } 
      newKey[pos] = key[i];
      newValue[pos] = value[i];
      if (prev != -1) {
        newLink[newPrev] = newLink[newPrev] ^ (newLink[newPrev] ^ pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        newLink[pos] = newLink[pos] ^ (newLink[pos] ^ (newPrev & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
        newPrev = pos;
      } else {
        newPrev = this.first = pos;
        newLink[pos] = -1L;
      } 
      int t = i;
      i = (int)link[i];
      prev = t;
    } 
    this.link = newLink;
    this.last = newPrev;
    if (newPrev != -1)
      newLink[newPrev] = newLink[newPrev] | 0xFFFFFFFFL; 
    this.n = newN;
    this.mask = mask;
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.key = newKey;
    this.value = newValue;
  }
  
  public Int2ObjectLinkedOpenHashMap<V> clone() {
    Int2ObjectLinkedOpenHashMap<V> c;
    try {
      c = (Int2ObjectLinkedOpenHashMap<V>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.keys = null;
    c.values = null;
    c.entries = null;
    c.containsNullKey = this.containsNullKey;
    c.key = (int[])this.key.clone();
    c.value = (V[])this.value.clone();
    c.link = (long[])this.link.clone();
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
    long[] link = this.link = new long[this.n + 1];
    int prev = -1;
    this.first = this.last = -1;
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
      if (this.first != -1) {
        link[prev] = link[prev] ^ (link[prev] ^ pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        link[pos] = link[pos] ^ (link[pos] ^ (prev & 0xFFFFFFFFL) << 32L) & 0xFFFFFFFF00000000L;
        prev = pos;
        continue;
      } 
      prev = this.first = pos;
      link[pos] = link[pos] | 0xFFFFFFFF00000000L;
    } 
    this.last = prev;
    if (prev != -1)
      link[prev] = link[prev] | 0xFFFFFFFFL; 
  }
  
  private void checkTable() {}
  
  private final class Int2ObjectLinkedOpenHashMap {}
  
  private final class Int2ObjectLinkedOpenHashMap {}
  
  private final class Int2ObjectLinkedOpenHashMap {}
}
