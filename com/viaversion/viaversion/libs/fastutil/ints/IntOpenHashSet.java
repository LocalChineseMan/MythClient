package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class IntOpenHashSet extends AbstractIntSet implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient int[] key;
  
  protected transient int mask;
  
  protected transient boolean containsNull;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  public IntOpenHashSet(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = new int[this.n + 1];
  }
  
  public IntOpenHashSet(int expected) {
    this(expected, 0.75F);
  }
  
  public IntOpenHashSet() {
    this(16, 0.75F);
  }
  
  public IntOpenHashSet(Collection<? extends Integer> c, float f) {
    this(c.size(), f);
    addAll(c);
  }
  
  public IntOpenHashSet(Collection<? extends Integer> c) {
    this(c, 0.75F);
  }
  
  public IntOpenHashSet(IntCollection c, float f) {
    this(c.size(), f);
    addAll(c);
  }
  
  public IntOpenHashSet(IntCollection c) {
    this(c, 0.75F);
  }
  
  public IntOpenHashSet(IntIterator i, float f) {
    this(16, f);
    while (i.hasNext())
      add(i.nextInt()); 
  }
  
  public IntOpenHashSet(IntIterator i) {
    this(i, 0.75F);
  }
  
  public IntOpenHashSet(Iterator<?> i, float f) {
    this(IntIterators.asIntIterator(i), f);
  }
  
  public IntOpenHashSet(Iterator<?> i) {
    this(IntIterators.asIntIterator(i));
  }
  
  public IntOpenHashSet(int[] a, int offset, int length, float f) {
    this((length < 0) ? 0 : length, f);
    IntArrays.ensureOffsetLength(a, offset, length);
    for (int i = 0; i < length; i++)
      add(a[offset + i]); 
  }
  
  public IntOpenHashSet(int[] a, int offset, int length) {
    this(a, offset, length, 0.75F);
  }
  
  public IntOpenHashSet(int[] a, float f) {
    this(a, 0, a.length, f);
  }
  
  public IntOpenHashSet(int[] a) {
    this(a, 0.75F);
  }
  
  private int realSize() {
    return this.containsNull ? (this.size - 1) : this.size;
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
  
  public boolean addAll(IntCollection c) {
    if (this.f <= 0.5D) {
      ensureCapacity(c.size());
    } else {
      tryCapacity((size() + c.size()));
    } 
    return super.addAll(c);
  }
  
  public boolean addAll(Collection<? extends Integer> c) {
    if (this.f <= 0.5D) {
      ensureCapacity(c.size());
    } else {
      tryCapacity((size() + c.size()));
    } 
    return super.addAll(c);
  }
  
  public boolean add(int k) {
    if (k == 0) {
      if (this.containsNull)
        return false; 
      this.containsNull = true;
    } else {
      int[] key = this.key;
      int pos, curr;
      if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
        if (curr == k)
          return false; 
        while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
          if (curr == k)
            return false; 
        } 
      } 
      key[pos] = k;
    } 
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
    return true;
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
    } 
  }
  
  private boolean removeEntry(int pos) {
    this.size--;
    shiftKeys(pos);
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return true;
  }
  
  private boolean removeNullEntry() {
    this.containsNull = false;
    this.key[this.n] = 0;
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return true;
  }
  
  public boolean remove(int k) {
    if (k == 0) {
      if (this.containsNull)
        return removeNullEntry(); 
      return false;
    } 
    int[] key = this.key;
    int curr, pos;
    if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0)
      return false; 
    if (k == curr)
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0)
        return false; 
      if (k == curr)
        return removeEntry(pos); 
    } 
  }
  
  public boolean contains(int k) {
    if (k == 0)
      return this.containsNull; 
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
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNull = false;
    Arrays.fill(this.key, 0);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public IntIterator iterator() {
    return (IntIterator)new SetIterator(this, null);
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
    int mask = newN - 1;
    int[] newKey = new int[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == 0);
      int pos;
      if (newKey[pos = HashCommon.mix(key[i]) & mask] != 0)
        while (newKey[pos = pos + 1 & mask] != 0); 
      newKey[pos] = key[i];
    } 
    this.n = newN;
    this.mask = mask;
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.key = newKey;
  }
  
  public IntOpenHashSet clone() {
    IntOpenHashSet c;
    try {
      c = (IntOpenHashSet)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.key = (int[])this.key.clone();
    c.containsNull = this.containsNull;
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0; j-- != 0; ) {
      while (this.key[i] == 0)
        i++; 
      h += this.key[i];
      i++;
    } 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    IntIterator i = iterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0;)
      s.writeInt(i.nextInt()); 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    int[] key = this.key = new int[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos, k = s.readInt();
      if (k == 0) {
        pos = this.n;
        this.containsNull = true;
      } else if (key[pos = HashCommon.mix(k) & this.mask] != 0) {
        while (key[pos = pos + 1 & this.mask] != 0);
      } 
      key[pos] = k;
    } 
  }
  
  private void checkTable() {}
  
  private class IntOpenHashSet {}
}
