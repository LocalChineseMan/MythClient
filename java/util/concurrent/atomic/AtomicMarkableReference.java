package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicMarkableReference<V> {
  private volatile Pair<V> pair;
  
  private static class Pair<T> {
    final T reference;
    
    final boolean mark;
    
    private Pair(T param1T, boolean param1Boolean) {
      this.reference = param1T;
      this.mark = param1Boolean;
    }
    
    static <T> Pair<T> of(T param1T, boolean param1Boolean) {
      return new Pair<>(param1T, param1Boolean);
    }
  }
  
  public AtomicMarkableReference(V paramV, boolean paramBoolean) {
    this.pair = Pair.of(paramV, paramBoolean);
  }
  
  public V getReference() {
    return (V)this.pair.reference;
  }
  
  public boolean isMarked() {
    return this.pair.mark;
  }
  
  public V get(boolean[] paramArrayOfboolean) {
    Pair<V> pair = this.pair;
    paramArrayOfboolean[0] = pair.mark;
    return (V)pair.reference;
  }
  
  public boolean weakCompareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2) {
    return compareAndSet(paramV1, paramV2, paramBoolean1, paramBoolean2);
  }
  
  public boolean compareAndSet(V paramV1, V paramV2, boolean paramBoolean1, boolean paramBoolean2) {
    Pair<V> pair = this.pair;
    return (paramV1 == pair.reference && paramBoolean1 == pair.mark && ((paramV2 == pair.reference && paramBoolean2 == pair.mark) || 
      
      casPair(pair, Pair.of(paramV2, paramBoolean2))));
  }
  
  public void set(V paramV, boolean paramBoolean) {
    Pair<V> pair = this.pair;
    if (paramV != pair.reference || paramBoolean != pair.mark)
      this.pair = Pair.of(paramV, paramBoolean); 
  }
  
  public boolean attemptMark(V paramV, boolean paramBoolean) {
    Pair<V> pair = this.pair;
    return (paramV == pair.reference && (paramBoolean == pair.mark || 
      
      casPair(pair, Pair.of(paramV, paramBoolean))));
  }
  
  private static final Unsafe UNSAFE = Unsafe.getUnsafe();
  
  private static final long pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
  
  private boolean casPair(Pair<V> paramPair1, Pair<V> paramPair2) {
    return UNSAFE.compareAndSwapObject(this, pairOffset, paramPair1, paramPair2);
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass) {
    try {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    } catch (NoSuchFieldException noSuchFieldException) {
      NoSuchFieldError noSuchFieldError = new NoSuchFieldError(paramString);
      noSuchFieldError.initCause(noSuchFieldException);
      throw noSuchFieldError;
    } 
  }
}
