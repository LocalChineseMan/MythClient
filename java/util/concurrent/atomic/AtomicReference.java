package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReference<V> implements Serializable {
  private static final long serialVersionUID = -1848883965231344442L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  private volatile V value;
  
  static {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicReference.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public AtomicReference(V paramV) {
    this.value = paramV;
  }
  
  public AtomicReference() {}
  
  public final V get() {
    return this.value;
  }
  
  public final void set(V paramV) {
    this.value = paramV;
  }
  
  public final void lazySet(V paramV) {
    unsafe.putOrderedObject(this, valueOffset, paramV);
  }
  
  public final boolean compareAndSet(V paramV1, V paramV2) {
    return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2);
  }
  
  public final boolean weakCompareAndSet(V paramV1, V paramV2) {
    return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2);
  }
  
  public final V getAndSet(V paramV) {
    return (V)unsafe.getAndSetObject(this, valueOffset, paramV);
  }
  
  public final V getAndUpdate(UnaryOperator<V> paramUnaryOperator) {
    while (true) {
      V v1 = get();
      V v2 = paramUnaryOperator.apply(v1);
      if (compareAndSet(v1, v2))
        return v1; 
    } 
  }
  
  public final V updateAndGet(UnaryOperator<V> paramUnaryOperator) {
    while (true) {
      V v1 = get();
      V v2 = paramUnaryOperator.apply(v1);
      if (compareAndSet(v1, v2))
        return v2; 
    } 
  }
  
  public final V getAndAccumulate(V paramV, BinaryOperator<V> paramBinaryOperator) {
    while (true) {
      V v1 = get();
      V v2 = paramBinaryOperator.apply(v1, paramV);
      if (compareAndSet(v1, v2))
        return v1; 
    } 
  }
  
  public final V accumulateAndGet(V paramV, BinaryOperator<V> paramBinaryOperator) {
    while (true) {
      V v1 = get();
      V v2 = paramBinaryOperator.apply(v1, paramV);
      if (compareAndSet(v1, v2))
        return v2; 
    } 
  }
  
  public String toString() {
    return String.valueOf(get());
  }
}
