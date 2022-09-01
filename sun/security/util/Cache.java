package sun.security.util;

import java.util.Arrays;

public abstract class Cache<K, V> {
  public abstract int size();
  
  public abstract void clear();
  
  public abstract void put(K paramK, V paramV);
  
  public abstract V get(Object paramObject);
  
  public abstract void remove(Object paramObject);
  
  public abstract void setCapacity(int paramInt);
  
  public abstract void setTimeout(int paramInt);
  
  public abstract void accept(CacheVisitor<K, V> paramCacheVisitor);
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt) {
    return new MemoryCache<>(true, paramInt);
  }
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt1, int paramInt2) {
    return new MemoryCache<>(true, paramInt1, paramInt2);
  }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt) {
    return new MemoryCache<>(false, paramInt);
  }
  
  public static <K, V> Cache<K, V> newNullCache() {
    return (Cache)NullCache.INSTANCE;
  }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt1, int paramInt2) {
    return new MemoryCache<>(false, paramInt1, paramInt2);
  }
  
  public static interface Cache {}
  
  public static class EqualByteArray {
    private final byte[] b;
    
    private volatile int hash;
    
    public EqualByteArray(byte[] param1ArrayOfbyte) {
      this.b = param1ArrayOfbyte;
    }
    
    public int hashCode() {
      int i = this.hash;
      if (i == 0) {
        i = this.b.length + 1;
        for (byte b = 0; b < this.b.length; b++)
          i += (this.b[b] & 0xFF) * 37; 
        this.hash = i;
      } 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof EqualByteArray))
        return false; 
      EqualByteArray equalByteArray = (EqualByteArray)param1Object;
      return Arrays.equals(this.b, equalByteArray.b);
    }
  }
}
