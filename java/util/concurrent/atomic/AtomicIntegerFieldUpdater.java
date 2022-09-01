package java.util.concurrent.atomic;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public abstract class AtomicIntegerFieldUpdater<T> {
  @CallerSensitive
  public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString) {
    return new AtomicIntegerFieldUpdaterImpl<>(paramClass, paramString, 
        Reflection.getCallerClass());
  }
  
  public abstract boolean compareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract void set(T paramT, int paramInt);
  
  public abstract void lazySet(T paramT, int paramInt);
  
  public abstract int get(T paramT);
  
  public int getAndSet(T paramT, int paramInt) {
    while (true) {
      int i = get(paramT);
      if (compareAndSet(paramT, i, paramInt))
        return i; 
    } 
  }
  
  public int getAndIncrement(T paramT) {
    while (true) {
      int i = get(paramT);
      int j = i + 1;
      if (compareAndSet(paramT, i, j))
        return i; 
    } 
  }
  
  public int getAndDecrement(T paramT) {
    while (true) {
      int i = get(paramT);
      int j = i - 1;
      if (compareAndSet(paramT, i, j))
        return i; 
    } 
  }
  
  public int getAndAdd(T paramT, int paramInt) {
    while (true) {
      int i = get(paramT);
      int j = i + paramInt;
      if (compareAndSet(paramT, i, j))
        return i; 
    } 
  }
  
  public int incrementAndGet(T paramT) {
    while (true) {
      int i = get(paramT);
      int j = i + 1;
      if (compareAndSet(paramT, i, j))
        return j; 
    } 
  }
  
  public int decrementAndGet(T paramT) {
    while (true) {
      int i = get(paramT);
      int j = i - 1;
      if (compareAndSet(paramT, i, j))
        return j; 
    } 
  }
  
  public int addAndGet(T paramT, int paramInt) {
    while (true) {
      int i = get(paramT);
      int j = i + paramInt;
      if (compareAndSet(paramT, i, j))
        return j; 
    } 
  }
  
  public final int getAndUpdate(T paramT, IntUnaryOperator paramIntUnaryOperator) {
    while (true) {
      int i = get(paramT);
      int j = paramIntUnaryOperator.applyAsInt(i);
      if (compareAndSet(paramT, i, j))
        return i; 
    } 
  }
  
  public final int updateAndGet(T paramT, IntUnaryOperator paramIntUnaryOperator) {
    while (true) {
      int i = get(paramT);
      int j = paramIntUnaryOperator.applyAsInt(i);
      if (compareAndSet(paramT, i, j))
        return j; 
    } 
  }
  
  public final int getAndAccumulate(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    while (true) {
      int i = get(paramT);
      int j = paramIntBinaryOperator.applyAsInt(i, paramInt);
      if (compareAndSet(paramT, i, j))
        return i; 
    } 
  }
  
  public final int accumulateAndGet(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    while (true) {
      int i = get(paramT);
      int j = paramIntBinaryOperator.applyAsInt(i, paramInt);
      if (compareAndSet(paramT, i, j))
        return j; 
    } 
  }
  
  private static class AtomicIntegerFieldUpdater {}
}
