package com.sun.jmx.mbeanserver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

class WeakIdentityHashMap<K, V> {
  static <K, V> WeakIdentityHashMap<K, V> make() {
    return new WeakIdentityHashMap<>();
  }
  
  V get(K paramK) {
    expunge();
    WeakReference<K> weakReference = makeReference(paramK);
    return this.map.get(weakReference);
  }
  
  public V put(K paramK, V paramV) {
    expunge();
    if (paramK == null)
      throw new IllegalArgumentException("Null key"); 
    WeakReference<K> weakReference = makeReference(paramK, this.refQueue);
    return this.map.put(weakReference, paramV);
  }
  
  public V remove(K paramK) {
    expunge();
    WeakReference<K> weakReference = makeReference(paramK);
    return this.map.remove(weakReference);
  }
  
  private void expunge() {
    Reference<? extends K> reference;
    while ((reference = this.refQueue.poll()) != null)
      this.map.remove(reference); 
  }
  
  private WeakReference<K> makeReference(K paramK) {
    return new IdentityWeakReference<>(paramK);
  }
  
  private WeakReference<K> makeReference(K paramK, ReferenceQueue<K> paramReferenceQueue) {
    return new IdentityWeakReference<>(paramK, paramReferenceQueue);
  }
  
  private static class IdentityWeakReference<T> extends WeakReference<T> {
    private final int hashCode;
    
    IdentityWeakReference(T param1T) {
      this(param1T, null);
    }
    
    IdentityWeakReference(T param1T, ReferenceQueue<T> param1ReferenceQueue) {
      super(param1T, param1ReferenceQueue);
      this.hashCode = (param1T == null) ? 0 : System.identityHashCode(param1T);
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof IdentityWeakReference))
        return false; 
      IdentityWeakReference<T> identityWeakReference = (IdentityWeakReference)param1Object;
      T t = get();
      return (t != null && t == identityWeakReference.get());
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
  
  private Map<WeakReference<K>, V> map = Util.newMap();
  
  private ReferenceQueue<K> refQueue = new ReferenceQueue<>();
}
