package java.lang.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.WeakCache;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class WeakCache<K, P, V> {
  private final ReferenceQueue<K> refQueue = new ReferenceQueue<>();
  
  private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map = new ConcurrentHashMap<>();
  
  private final ConcurrentMap<Supplier<V>, Boolean> reverseMap = new ConcurrentHashMap<>();
  
  private final BiFunction<K, P, ?> subKeyFactory;
  
  private final BiFunction<K, P, V> valueFactory;
  
  public WeakCache(BiFunction<K, P, ?> paramBiFunction, BiFunction<K, P, V> paramBiFunction1) {
    this.subKeyFactory = Objects.<BiFunction<K, P, ?>>requireNonNull(paramBiFunction);
    this.valueFactory = Objects.<BiFunction<K, P, V>>requireNonNull(paramBiFunction1);
  }
  
  public V get(K paramK, P paramP) {
    Objects.requireNonNull(paramP);
    expungeStaleEntries();
    Object object = CacheKey.valueOf(paramK, this.refQueue);
    ConcurrentMap<Object, Object> concurrentMap = (ConcurrentMap)this.map.get(object);
    if (concurrentMap == null) {
      ConcurrentMap<Object, Object> concurrentMap1 = (ConcurrentMap)this.map.putIfAbsent(object, concurrentMap = new ConcurrentHashMap<>());
      if (concurrentMap1 != null)
        concurrentMap = concurrentMap1; 
    } 
    Object object1 = Objects.requireNonNull(this.subKeyFactory.apply(paramK, paramP));
    Supplier<Object> supplier = (Supplier)concurrentMap.get(object1);
    Factory factory = null;
    while (true) {
      if (supplier != null) {
        V v = (V)supplier.get();
        if (v != null)
          return v; 
      } 
      if (factory == null)
        factory = new Factory(paramK, paramP, object1, (ConcurrentMap)concurrentMap); 
      if (supplier == null) {
        supplier = (Supplier<Object>)concurrentMap.putIfAbsent(object1, factory);
        if (supplier == null)
          supplier = factory; 
        continue;
      } 
      if (concurrentMap.replace(object1, supplier, factory)) {
        supplier = factory;
        continue;
      } 
      supplier = (Supplier<Object>)concurrentMap.get(object1);
    } 
  }
  
  public boolean containsValue(V paramV) {
    Objects.requireNonNull(paramV);
    expungeStaleEntries();
    return this.reverseMap.containsKey(new LookupValue(paramV));
  }
  
  public int size() {
    expungeStaleEntries();
    return this.reverseMap.size();
  }
  
  private void expungeStaleEntries() {
    CacheKey cacheKey;
    while ((cacheKey = (CacheKey)this.refQueue.poll()) != null)
      cacheKey.expungeFrom(this.map, this.reverseMap); 
  }
  
  private final class Factory implements Supplier<V> {
    private final K key;
    
    private final P parameter;
    
    private final Object subKey;
    
    private final ConcurrentMap<Object, Supplier<V>> valuesMap;
    
    Factory(K param1K, P param1P, Object param1Object, ConcurrentMap<Object, Supplier<V>> param1ConcurrentMap) {
      this.key = param1K;
      this.parameter = param1P;
      this.subKey = param1Object;
      this.valuesMap = param1ConcurrentMap;
    }
    
    public synchronized V get() {
      Supplier supplier = this.valuesMap.get(this.subKey);
      if (supplier != this)
        return null; 
      V v = null;
      try {
        v = Objects.requireNonNull(WeakCache.this.valueFactory.apply(this.key, this.parameter));
      } finally {
        if (v == null)
          this.valuesMap.remove(this.subKey, this); 
      } 
      assert v != null;
      WeakCache.CacheValue<V> cacheValue = new WeakCache.CacheValue(v);
      if (this.valuesMap.replace(this.subKey, this, cacheValue)) {
        WeakCache.this.reverseMap.put((WeakCache.CacheValue)cacheValue, Boolean.TRUE);
      } else {
        throw new AssertionError("Should not reach here");
      } 
      return v;
    }
  }
  
  private static interface Value<V> extends Supplier<V> {}
  
  private static final class WeakCache {}
  
  private static final class CacheValue<V> extends WeakReference<V> implements Value<V> {
    private final int hash;
    
    CacheValue(V param1V) {
      super(param1V);
      this.hash = System.identityHashCode(param1V);
    }
    
    public int hashCode() {
      return this.hash;
    }
    
    public boolean equals(Object param1Object) {
      V v;
      return (param1Object == this || (param1Object instanceof WeakCache.Value && (
        
        v = get()) != null && v == ((WeakCache.Value<V>)param1Object)
        .get()));
    }
  }
  
  private static final class CacheKey<K> extends WeakReference<K> {
    private static final Object NULL_KEY = new Object();
    
    private final int hash;
    
    static <K> Object valueOf(K param1K, ReferenceQueue<K> param1ReferenceQueue) {
      return (param1K == null) ? NULL_KEY : new CacheKey<>(param1K, param1ReferenceQueue);
    }
    
    private CacheKey(K param1K, ReferenceQueue<K> param1ReferenceQueue) {
      super(param1K, param1ReferenceQueue);
      this.hash = System.identityHashCode(param1K);
    }
    
    public int hashCode() {
      return this.hash;
    }
    
    public boolean equals(Object param1Object) {
      K k;
      return (param1Object == this || (param1Object != null && param1Object
        
        .getClass() == getClass() && (
        
        k = get()) != null && k == ((CacheKey<K>)param1Object)
        
        .get()));
    }
    
    void expungeFrom(ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> param1ConcurrentMap, ConcurrentMap<?, Boolean> param1ConcurrentMap1) {
      ConcurrentMap concurrentMap = param1ConcurrentMap.remove(this);
      if (concurrentMap != null)
        for (Object object : concurrentMap.values())
          param1ConcurrentMap1.remove(object);  
    }
  }
}
