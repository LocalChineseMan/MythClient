package java.util.concurrent.atomic;

import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public abstract class AtomicLongFieldUpdater<T> {
  @CallerSensitive
  public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString) {
    Class<?> clazz = Reflection.getCallerClass();
    if (AtomicLong.VM_SUPPORTS_LONG_CAS)
      return new CASUpdater<>(paramClass, paramString, clazz); 
    return new LockedUpdater<>(paramClass, paramString, clazz);
  }
  
  public long getAndSet(T paramT, long paramLong) {
    while (true) {
      long l = get(paramT);
      if (compareAndSet(paramT, l, paramLong))
        return l; 
    } 
  }
  
  public long getAndIncrement(T paramT) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 + 1L;
      if (compareAndSet(paramT, l1, l2))
        return l1; 
    } 
  }
  
  public long getAndDecrement(T paramT) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 - 1L;
      if (compareAndSet(paramT, l1, l2))
        return l1; 
    } 
  }
  
  public long getAndAdd(T paramT, long paramLong) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 + paramLong;
      if (compareAndSet(paramT, l1, l2))
        return l1; 
    } 
  }
  
  public long incrementAndGet(T paramT) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 + 1L;
      if (compareAndSet(paramT, l1, l2))
        return l2; 
    } 
  }
  
  public long decrementAndGet(T paramT) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 - 1L;
      if (compareAndSet(paramT, l1, l2))
        return l2; 
    } 
  }
  
  public long addAndGet(T paramT, long paramLong) {
    while (true) {
      long l1 = get(paramT);
      long l2 = l1 + paramLong;
      if (compareAndSet(paramT, l1, l2))
        return l2; 
    } 
  }
  
  public final long getAndUpdate(T paramT, LongUnaryOperator paramLongUnaryOperator) {
    while (true) {
      long l1 = get(paramT);
      long l2 = paramLongUnaryOperator.applyAsLong(l1);
      if (compareAndSet(paramT, l1, l2))
        return l1; 
    } 
  }
  
  public final long updateAndGet(T paramT, LongUnaryOperator paramLongUnaryOperator) {
    while (true) {
      long l1 = get(paramT);
      long l2 = paramLongUnaryOperator.applyAsLong(l1);
      if (compareAndSet(paramT, l1, l2))
        return l2; 
    } 
  }
  
  public final long getAndAccumulate(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    while (true) {
      long l1 = get(paramT);
      long l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
      if (compareAndSet(paramT, l1, l2))
        return l1; 
    } 
  }
  
  public final long accumulateAndGet(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    while (true) {
      long l1 = get(paramT);
      long l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
      if (compareAndSet(paramT, l1, l2))
        return l2; 
    } 
  }
  
  private static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) {
    ClassLoader classLoader = paramClassLoader1;
    while (true) {
      classLoader = classLoader.getParent();
      if (paramClassLoader2 == classLoader)
        return true; 
      if (classLoader == null)
        return false; 
    } 
  }
  
  public abstract boolean compareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract void set(T paramT, long paramLong);
  
  public abstract void lazySet(T paramT, long paramLong);
  
  public abstract long get(T paramT);
  
  private static class AtomicLongFieldUpdater {}
  
  private static class AtomicLongFieldUpdater {}
}
