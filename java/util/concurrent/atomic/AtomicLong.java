package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements Serializable {
  private static final long serialVersionUID = 1927816293512124184L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();
  
  private volatile long value;
  
  static {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public AtomicLong(long paramLong) {
    this.value = paramLong;
  }
  
  public AtomicLong() {}
  
  public final long get() {
    return this.value;
  }
  
  public final void set(long paramLong) {
    this.value = paramLong;
  }
  
  public final void lazySet(long paramLong) {
    unsafe.putOrderedLong(this, valueOffset, paramLong);
  }
  
  public final long getAndSet(long paramLong) {
    return unsafe.getAndSetLong(this, valueOffset, paramLong);
  }
  
  public final boolean compareAndSet(long paramLong1, long paramLong2) {
    return unsafe.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2);
  }
  
  public final boolean weakCompareAndSet(long paramLong1, long paramLong2) {
    return unsafe.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2);
  }
  
  public final long getAndIncrement() {
    return unsafe.getAndAddLong(this, valueOffset, 1L);
  }
  
  public final long getAndDecrement() {
    return unsafe.getAndAddLong(this, valueOffset, -1L);
  }
  
  public final long getAndAdd(long paramLong) {
    return unsafe.getAndAddLong(this, valueOffset, paramLong);
  }
  
  public final long incrementAndGet() {
    return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
  }
  
  public final long decrementAndGet() {
    return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
  }
  
  public final long addAndGet(long paramLong) {
    return unsafe.getAndAddLong(this, valueOffset, paramLong) + paramLong;
  }
  
  public final long getAndUpdate(LongUnaryOperator paramLongUnaryOperator) {
    while (true) {
      long l1 = get();
      long l2 = paramLongUnaryOperator.applyAsLong(l1);
      if (compareAndSet(l1, l2))
        return l1; 
    } 
  }
  
  public final long updateAndGet(LongUnaryOperator paramLongUnaryOperator) {
    while (true) {
      long l1 = get();
      long l2 = paramLongUnaryOperator.applyAsLong(l1);
      if (compareAndSet(l1, l2))
        return l2; 
    } 
  }
  
  public final long getAndAccumulate(long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    while (true) {
      long l1 = get();
      long l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
      if (compareAndSet(l1, l2))
        return l1; 
    } 
  }
  
  public final long accumulateAndGet(long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    while (true) {
      long l1 = get();
      long l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
      if (compareAndSet(l1, l2))
        return l2; 
    } 
  }
  
  public String toString() {
    return Long.toString(get());
  }
  
  public int intValue() {
    return (int)get();
  }
  
  public long longValue() {
    return get();
  }
  
  public float floatValue() {
    return (float)get();
  }
  
  public double doubleValue() {
    return get();
  }
  
  private static native boolean VMSupportsCS8();
}
