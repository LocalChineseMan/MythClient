package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
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
import java.util.function.IntUnaryOperator;

public class Int2IntOpenHashMap extends AbstractInt2IntMap implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient int[] key;
  
  protected transient int[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  protected transient Int2IntMap.FastEntrySet entries;
  
  protected transient IntSet keys;
  
  protected transient IntCollection values;
  
  public Int2IntOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = new int[this.n + 1];
    this.value = new int[this.n + 1];
  }
  
  public Int2IntOpenHashMap(int expected) {
    this(expected, 0.75F);
  }
  
  public Int2IntOpenHashMap() {
    this(16, 0.75F);
  }
  
  public Int2IntOpenHashMap(Map<? extends Integer, ? extends Integer> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2IntOpenHashMap(Map<? extends Integer, ? extends Integer> m) {
    this(m, 0.75F);
  }
  
  public Int2IntOpenHashMap(Int2IntMap m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Int2IntOpenHashMap(Int2IntMap m) {
    this(m, 0.75F);
  }
  
  public Int2IntOpenHashMap(int[] k, int[] v, float f) {
    this(k.length, f);
    if (k.length != v.length)
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")"); 
    for (int i = 0; i < k.length; i++)
      put(k[i], v[i]); 
  }
  
  public Int2IntOpenHashMap(int[] k, int[] v) {
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
    int oldValue = this.value[this.n];
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  public void putAll(Map<? extends Integer, ? extends Integer> m) {
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
  
  private void insert(int pos, int k, int v) {
    if (pos == this.n)
      this.containsNullKey = true; 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
  }
  
  public int put(int k, int v) {
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
  
  public int addTo(int k, int incr) {
    int pos;
    if (k == 0) {
      if (this.containsNullKey)
        return addToValue(this.n, incr); 
      pos = this.n;
      this.containsNullKey = true;
    } else {
      int[] key = this.key;
      int curr;
      if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
        if (curr == k)
          return addToValue(pos, incr); 
        while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
          if (curr == k)
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
    int[] key = this.key;
    while (true) {
      int curr, last;
      pos = (last = pos) + 1 & this.mask;
      while (true) {
        if ((curr = key[pos]) == 0) {
          key[last] = 0;
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
  
  public int remove(int k) {
    if (k == 0) {
      if (this.containsNullKey)
        return removeNullEntry(); 
      return this.defRetValue;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return this.defRetValue; 
    if (k == curr)
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return this.defRetValue; 
      if (k == curr)
        return removeEntry(pos); 
    } 
  }
  
  public int get(int k) {
    if (k == 0)
      return this.containsNullKey ? this.value[this.n] : this.defRetValue; 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return this.defRetValue; 
    if (k == curr)
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return this.defRetValue; 
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
  
  public boolean containsValue(int v) {
    int[] value = this.value;
    int[] key = this.key;
    if (this.containsNullKey && value[this.n] == v)
      return true; 
    for (int i = this.n; i-- != 0;) {
      if (key[i] != 0 && value[i] == v)
        return true; 
    } 
    return false;
  }
  
  public int getOrDefault(int k, int defaultValue) {
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
  
  public int putIfAbsent(int k, int v) {
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    insert(-pos - 1, k, v);
    return this.defRetValue;
  }
  
  public boolean remove(int k, int v) {
    if (k == 0) {
      if (this.containsNullKey && v == this.value[this.n]) {
        removeNullEntry();
        return true;
      } 
      return false;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return false; 
    if (k == curr && v == this.value[pos]) {
      removeEntry(pos);
      return true;
    } 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return false; 
      if (k == curr && v == this.value[pos]) {
        removeEntry(pos);
        return true;
      } 
    } 
  }
  
  public boolean replace(int k, int oldValue, int v) {
    int pos = find(k);
    if (pos < 0 || oldValue != this.value[pos])
      return false; 
    this.value[pos] = v;
    return true;
  }
  
  public int replace(int k, int v) {
    int pos = find(k);
    if (pos < 0)
      return this.defRetValue; 
    int oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  public int computeIfAbsent(int k, IntUnaryOperator mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    int newValue = mappingFunction.applyAsInt(k);
    insert(-pos - 1, k, newValue);
    return newValue;
  }
  
  public int computeIfAbsentNullable(int k, IntFunction<? extends Integer> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    int pos = find(k);
    if (pos >= 0)
      return this.value[pos]; 
    Integer newValue = mappingFunction.apply(k);
    if (newValue == null)
      return this.defRetValue; 
    int v = newValue.intValue();
    insert(-pos - 1, k, v);
    return v;
  }
  
  public int computeIfPresent(int k, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0)
      return this.defRetValue; 
    Integer newValue = remappingFunction.apply(Integer.valueOf(k), Integer.valueOf(this.value[pos]));
    if (newValue == null) {
      if (k == 0) {
        removeNullEntry();
      } else {
        removeEntry(pos);
      } 
      return this.defRetValue;
    } 
    this.value[pos] = newValue.intValue();
    return newValue.intValue();
  }
  
  public int compute(int k, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    Integer newValue = remappingFunction.apply(Integer.valueOf(k), 
        (pos >= 0) ? Integer.valueOf(this.value[pos]) : null);
    if (newValue == null) {
      if (pos >= 0)
        if (k == 0) {
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
  
  public int merge(int k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return v;
    } 
    Integer newValue = remappingFunction.apply(Integer.valueOf(this.value[pos]), Integer.valueOf(v));
    if (newValue == null) {
      if (k == 0) {
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
    Arrays.fill(this.key, 0);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  final class MapEntry implements Int2IntMap.Entry, Map.Entry<Integer, Integer> {
    int index;
    
    MapEntry(int index) {
      this.index = index;
    }
    
    MapEntry() {}
    
    public int getIntKey() {
      return Int2IntOpenHashMap.this.key[this.index];
    }
    
    public int getIntValue() {
      return Int2IntOpenHashMap.this.value[this.index];
    }
    
    public int setValue(int v) {
      int oldValue = Int2IntOpenHashMap.this.value[this.index];
      Int2IntOpenHashMap.this.value[this.index] = v;
      return oldValue;
    }
    
    @Deprecated
    public Integer getKey() {
      return Integer.valueOf(Int2IntOpenHashMap.this.key[this.index]);
    }
    
    @Deprecated
    public Integer getValue() {
      return Integer.valueOf(Int2IntOpenHashMap.this.value[this.index]);
    }
    
    @Deprecated
    public Integer setValue(Integer v) {
      return Integer.valueOf(setValue(v.intValue()));
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>)o;
      return (Int2IntOpenHashMap.this.key[this.index] == ((Integer)e.getKey()).intValue() && Int2IntOpenHashMap.this.value[this.index] == ((Integer)e.getValue()).intValue());
    }
    
    public int hashCode() {
      return Int2IntOpenHashMap.this.key[this.index] ^ Int2IntOpenHashMap.this.value[this.index];
    }
    
    public String toString() {
      return Int2IntOpenHashMap.this.key[this.index] + "=>" + Int2IntOpenHashMap.this.value[this.index];
    }
  }
  
  private class MapIterator {
    int pos = Int2IntOpenHashMap.this.n;
    
    int last = -1;
    
    int c = Int2IntOpenHashMap.this.size;
    
    boolean mustReturnNullKey = Int2IntOpenHashMap.this.containsNullKey;
    
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
        return this.last = Int2IntOpenHashMap.this.n;
      } 
      int[] key = Int2IntOpenHashMap.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          int k = this.wrapped.getInt(-this.pos - 1);
          int p = HashCommon.mix(k) & Int2IntOpenHashMap.this.mask;
          while (k != key[p])
            p = p + 1 & Int2IntOpenHashMap.this.mask; 
          return p;
        } 
        if (key[this.pos] != 0)
          return this.last = this.pos; 
      } 
    }
    
    private void shiftKeys(int pos) {
      int[] key = Int2IntOpenHashMap.this.key;
      while (true) {
        int curr, last;
        pos = (last = pos) + 1 & Int2IntOpenHashMap.this.mask;
        while (true) {
          if ((curr = key[pos]) == 0) {
            key[last] = 0;
            return;
          } 
          int slot = HashCommon.mix(curr) & Int2IntOpenHashMap.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & Int2IntOpenHashMap.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new IntArrayList(2); 
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
        Int2IntOpenHashMap.this.value[last] = Int2IntOpenHashMap.this.value[pos];
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == Int2IntOpenHashMap.this.n) {
        Int2IntOpenHashMap.this.containsNullKey = false;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        Int2IntOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
        this.last = -1;
        return;
      } 
      Int2IntOpenHashMap.this.size--;
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
  
  private class EntryIterator extends MapIterator implements ObjectIterator<Int2IntMap.Entry> {
    private Int2IntOpenHashMap.MapEntry entry;
    
    private EntryIterator() {}
    
    public Int2IntOpenHashMap.MapEntry next() {
      return this.entry = new Int2IntOpenHashMap.MapEntry(nextEntry());
    }
    
    public void remove() {
      super.remove();
      this.entry.index = -1;
    }
  }
  
  private final class MapEntrySet extends AbstractObjectSet<Int2IntMap.Entry> implements Int2IntMap.FastEntrySet {
    private MapEntrySet() {}
    
    public ObjectIterator<Int2IntMap.Entry> iterator() {
      return new Int2IntOpenHashMap.EntryIterator();
    }
    
    public ObjectIterator<Int2IntMap.Entry> fastIterator() {
      return (ObjectIterator<Int2IntMap.Entry>)new Int2IntOpenHashMap.FastEntryIterator(Int2IntOpenHashMap.this, null);
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      int v = ((Integer)e.getValue()).intValue();
      if (k == 0)
        return (Int2IntOpenHashMap.this.containsNullKey && Int2IntOpenHashMap.this.value[Int2IntOpenHashMap.this.n] == v); 
      int[] key = Int2IntOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2IntOpenHashMap.this.mask]) == 0)
        return false; 
      if (k == curr)
        return (Int2IntOpenHashMap.this.value[pos] == v); 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2IntOpenHashMap.this.mask]) == 0)
          return false; 
        if (k == curr)
          return (Int2IntOpenHashMap.this.value[pos] == v); 
      } 
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      int v = ((Integer)e.getValue()).intValue();
      if (k == 0) {
        if (Int2IntOpenHashMap.this.containsNullKey && Int2IntOpenHashMap.this.value[Int2IntOpenHashMap.this.n] == v) {
          Int2IntOpenHashMap.this.removeNullEntry();
          return true;
        } 
        return false;
      } 
      int[] key = Int2IntOpenHashMap.this.key;
      int curr, pos;
      if ((curr = key[pos = HashCommon.mix(k) & Int2IntOpenHashMap.this.mask]) == 0)
        return false; 
      if (curr == k) {
        if (Int2IntOpenHashMap.this.value[pos] == v) {
          Int2IntOpenHashMap.this.removeEntry(pos);
          return true;
        } 
        return false;
      } 
      while (true) {
        if ((curr = key[pos = pos + 1 & Int2IntOpenHashMap.this.mask]) == 0)
          return false; 
        if (curr == k && 
          Int2IntOpenHashMap.this.value[pos] == v) {
          Int2IntOpenHashMap.this.removeEntry(pos);
          return true;
        } 
      } 
    }
    
    public int size() {
      return Int2IntOpenHashMap.this.size;
    }
    
    public void clear() {
      Int2IntOpenHashMap.this.clear();
    }
    
    public void forEach(Consumer<? super Int2IntMap.Entry> consumer) {
      if (Int2IntOpenHashMap.this.containsNullKey)
        consumer.accept(new AbstractInt2IntMap.BasicEntry(Int2IntOpenHashMap.this.key[Int2IntOpenHashMap.this.n], Int2IntOpenHashMap.this.value[Int2IntOpenHashMap.this.n])); 
      for (int pos = Int2IntOpenHashMap.this.n; pos-- != 0;) {
        if (Int2IntOpenHashMap.this.key[pos] != 0)
          consumer.accept(new AbstractInt2IntMap.BasicEntry(Int2IntOpenHashMap.this.key[pos], Int2IntOpenHashMap.this.value[pos])); 
      } 
    }
    
    public void fastForEach(Consumer<? super Int2IntMap.Entry> consumer) {
      AbstractInt2IntMap.BasicEntry entry = new AbstractInt2IntMap.BasicEntry();
      if (Int2IntOpenHashMap.this.containsNullKey) {
        entry.key = Int2IntOpenHashMap.this.key[Int2IntOpenHashMap.this.n];
        entry.value = Int2IntOpenHashMap.this.value[Int2IntOpenHashMap.this.n];
        consumer.accept(entry);
      } 
      for (int pos = Int2IntOpenHashMap.this.n; pos-- != 0;) {
        if (Int2IntOpenHashMap.this.key[pos] != 0) {
          entry.key = Int2IntOpenHashMap.this.key[pos];
          entry.value = Int2IntOpenHashMap.this.value[pos];
          consumer.accept(entry);
        } 
      } 
    }
  }
  
  public Int2IntMap.FastEntrySet int2IntEntrySet() {
    if (this.entries == null)
      this.entries = new MapEntrySet(); 
    return this.entries;
  }
  
  public IntSet keySet() {
    if (this.keys == null)
      this.keys = (IntSet)new KeySet(this, null); 
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
    int[] key = this.key;
    int[] value = this.value;
    int mask = newN - 1;
    int[] newKey = new int[newN + 1];
    int[] newValue = new int[newN + 1];
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
  
  public Int2IntOpenHashMap clone() {
    Int2IntOpenHashMap c;
    try {
      c = (Int2IntOpenHashMap)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.keys = null;
    c.values = null;
    c.entries = null;
    c.containsNullKey = this.containsNullKey;
    c.key = (int[])this.key.clone();
    c.value = (int[])this.value.clone();
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0, t = 0; j-- != 0; ) {
      while (this.key[i] == 0)
        i++; 
      t = this.key[i];
      t ^= this.value[i];
      h += t;
      i++;
    } 
    if (this.containsNullKey)
      h += this.value[this.n]; 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    int[] key = this.key;
    int[] value = this.value;
    MapIterator i = new MapIterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0; ) {
      int e = i.nextEntry();
      s.writeInt(key[e]);
      s.writeInt(value[e]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    int[] key = this.key = new int[this.n + 1];
    int[] value = this.value = new int[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos, k = s.readInt();
      int v = s.readInt();
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
  
  private final class Int2IntOpenHashMap {}
  
  private final class Int2IntOpenHashMap {}
  
  private final class Int2IntOpenHashMap {}
  
  private class Int2IntOpenHashMap {}
}
