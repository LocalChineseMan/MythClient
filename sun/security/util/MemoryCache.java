package sun.security.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MemoryCache<K, V> extends Cache<K, V> {
  private static final float LOAD_FACTOR = 0.75F;
  
  private static final boolean DEBUG = false;
  
  private final Map<K, CacheEntry<K, V>> cacheMap;
  
  private int maxSize;
  
  private long lifetime;
  
  private final ReferenceQueue<V> queue;
  
  public MemoryCache(boolean paramBoolean, int paramInt) {
    this(paramBoolean, paramInt, 0);
  }
  
  public MemoryCache(boolean paramBoolean, int paramInt1, int paramInt2) {
    this.maxSize = paramInt1;
    this.lifetime = (paramInt2 * 1000);
    if (paramBoolean) {
      this.queue = new ReferenceQueue<>();
    } else {
      this.queue = null;
    } 
    int i = (int)(paramInt1 / 0.75F) + 1;
    this.cacheMap = new LinkedHashMap<>(i, 0.75F, true);
  }
  
  private void emptyQueue() {
    if (this.queue == null)
      return; 
    int i = this.cacheMap.size();
    while (true) {
      CacheEntry cacheEntry = (CacheEntry)this.queue.poll();
      if (cacheEntry == null)
        break; 
      Object object = cacheEntry.getKey();
      if (object == null)
        continue; 
      CacheEntry<K, V> cacheEntry1 = this.cacheMap.remove(object);
      if (cacheEntry1 != null && cacheEntry != cacheEntry1)
        this.cacheMap.put((K)object, cacheEntry1); 
    } 
  }
  
  private void expungeExpiredEntries() {
    emptyQueue();
    if (this.lifetime == 0L)
      return; 
    byte b = 0;
    long l = System.currentTimeMillis();
    Iterator<CacheEntry> iterator = this.cacheMap.values().iterator();
    while (iterator.hasNext()) {
      CacheEntry cacheEntry = iterator.next();
      if (!cacheEntry.isValid(l)) {
        iterator.remove();
        b++;
      } 
    } 
  }
  
  public synchronized int size() {
    expungeExpiredEntries();
    return this.cacheMap.size();
  }
  
  public synchronized void clear() {
    if (this.queue != null) {
      for (CacheEntry<K, V> cacheEntry : this.cacheMap.values())
        cacheEntry.invalidate(); 
      while (this.queue.poll() != null);
    } 
    this.cacheMap.clear();
  }
  
  public synchronized void put(K paramK, V paramV) {
    emptyQueue();
    long l = (this.lifetime == 0L) ? 0L : (System.currentTimeMillis() + this.lifetime);
    CacheEntry<K, V> cacheEntry = newEntry(paramK, paramV, l, this.queue);
    CacheEntry cacheEntry1 = this.cacheMap.put(paramK, cacheEntry);
    if (cacheEntry1 != null) {
      cacheEntry1.invalidate();
      return;
    } 
    if (this.maxSize > 0 && this.cacheMap.size() > this.maxSize) {
      expungeExpiredEntries();
      if (this.cacheMap.size() > this.maxSize) {
        Iterator<CacheEntry> iterator = this.cacheMap.values().iterator();
        CacheEntry cacheEntry2 = iterator.next();
        iterator.remove();
        cacheEntry2.invalidate();
      } 
    } 
  }
  
  public synchronized V get(Object paramObject) {
    emptyQueue();
    CacheEntry cacheEntry = this.cacheMap.get(paramObject);
    if (cacheEntry == null)
      return null; 
    long l = (this.lifetime == 0L) ? 0L : System.currentTimeMillis();
    if (!cacheEntry.isValid(l)) {
      this.cacheMap.remove(paramObject);
      return null;
    } 
    return (V)cacheEntry.getValue();
  }
  
  public synchronized void remove(Object paramObject) {
    emptyQueue();
    CacheEntry cacheEntry = this.cacheMap.remove(paramObject);
    if (cacheEntry != null)
      cacheEntry.invalidate(); 
  }
  
  public synchronized void setCapacity(int paramInt) {
    expungeExpiredEntries();
    if (paramInt > 0 && this.cacheMap.size() > paramInt) {
      Iterator<CacheEntry> iterator = this.cacheMap.values().iterator();
      for (int i = this.cacheMap.size() - paramInt; i > 0; i--) {
        CacheEntry cacheEntry = iterator.next();
        iterator.remove();
        cacheEntry.invalidate();
      } 
    } 
    this.maxSize = (paramInt > 0) ? paramInt : 0;
  }
  
  public synchronized void setTimeout(int paramInt) {
    emptyQueue();
    this.lifetime = (paramInt > 0) ? (paramInt * 1000L) : 0L;
  }
  
  public synchronized void accept(Cache.CacheVisitor<K, V> paramCacheVisitor) {
    expungeExpiredEntries();
    Map<K, V> map = getCachedEntries();
    paramCacheVisitor.visit(map);
  }
  
  private Map<K, V> getCachedEntries() {
    HashMap<Object, Object> hashMap = new HashMap<>(this.cacheMap.size());
    for (CacheEntry<K, V> cacheEntry : this.cacheMap.values())
      hashMap.put(cacheEntry.getKey(), cacheEntry.getValue()); 
    return (Map)hashMap;
  }
  
  protected CacheEntry<K, V> newEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue) {
    if (paramReferenceQueue != null)
      return new SoftCacheEntry<>(paramK, paramV, paramLong, paramReferenceQueue); 
    return new HardCacheEntry<>(paramK, paramV, paramLong);
  }
  
  private static class SoftCacheEntry<K, V> extends SoftReference<V> implements CacheEntry<K, V> {
    private K key;
    
    private long expirationTime;
    
    SoftCacheEntry(K param1K, V param1V, long param1Long, ReferenceQueue<V> param1ReferenceQueue) {
      super(param1V, param1ReferenceQueue);
      this.key = param1K;
      this.expirationTime = param1Long;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return get();
    }
    
    public boolean isValid(long param1Long) {
      boolean bool = (param1Long <= this.expirationTime && get() != null) ? true : false;
      if (!bool)
        invalidate(); 
      return bool;
    }
    
    public void invalidate() {
      clear();
      this.key = null;
      this.expirationTime = -1L;
    }
  }
  
  private static class MemoryCache {}
  
  private static interface CacheEntry<K, V> {
    boolean isValid(long param1Long);
    
    void invalidate();
    
    K getKey();
    
    V getValue();
  }
}
