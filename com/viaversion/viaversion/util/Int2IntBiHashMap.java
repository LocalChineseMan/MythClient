package com.viaversion.viaversion.util;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Set;

public class Int2IntBiHashMap implements Int2IntBiMap {
  private final Int2IntMap map;
  
  private final Int2IntBiHashMap inverse;
  
  public Int2IntBiHashMap() {
    this.map = (Int2IntMap)new Int2IntOpenHashMap();
    this.inverse = new Int2IntBiHashMap(this);
  }
  
  private Int2IntBiHashMap(Int2IntBiHashMap inverse) {
    this.map = (Int2IntMap)new Int2IntOpenHashMap();
    this.inverse = inverse;
  }
  
  public Int2IntBiMap inverse() {
    return this.inverse;
  }
  
  public int put(int key, int value) {
    if (containsKey(key) && value == get(key))
      return value; 
    Preconditions.checkArgument(!containsValue(value), "value already present: %s", new Object[] { Integer.valueOf(value) });
    this.map.put(key, value);
    this.inverse.map.put(value, key);
    return defaultReturnValue();
  }
  
  public boolean remove(int key, int value) {
    this.map.remove(key, value);
    return this.inverse.map.remove(key, value);
  }
  
  public int get(int key) {
    return this.map.get(key);
  }
  
  public void clear() {
    this.map.clear();
    this.inverse.map.clear();
  }
  
  public int size() {
    return this.map.size();
  }
  
  public boolean isEmpty() {
    return this.map.isEmpty();
  }
  
  public void defaultReturnValue(int rv) {
    this.map.defaultReturnValue(rv);
    this.inverse.map.defaultReturnValue(rv);
  }
  
  public int defaultReturnValue() {
    return this.map.defaultReturnValue();
  }
  
  public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
    return this.map.int2IntEntrySet();
  }
  
  public IntSet keySet() {
    return this.map.keySet();
  }
  
  public IntSet values() {
    return this.inverse.map.keySet();
  }
  
  public boolean containsKey(int key) {
    return this.map.containsKey(key);
  }
  
  public boolean containsValue(int value) {
    return this.inverse.map.containsKey(value);
  }
}
