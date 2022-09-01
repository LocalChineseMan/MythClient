package java.util.concurrent.atomic;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E> implements Serializable {
  private static final long serialVersionUID = -6209656149925076980L;
  
  private static final Unsafe unsafe;
  
  private static final int base;
  
  private static final int shift;
  
  private static final long arrayFieldOffset;
  
  private final Object[] array;
  
  static {
    try {
      unsafe = Unsafe.getUnsafe();
      arrayFieldOffset = unsafe.objectFieldOffset(AtomicReferenceArray.class.getDeclaredField("array"));
      base = unsafe.arrayBaseOffset(Object[].class);
      int i = unsafe.arrayIndexScale(Object[].class);
      if ((i & i - 1) != 0)
        throw new Error("data type scale not a power of two"); 
      shift = 31 - Integer.numberOfLeadingZeros(i);
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  private long checkedByteOffset(int paramInt) {
    if (paramInt < 0 || paramInt >= this.array.length)
      throw new IndexOutOfBoundsException("index " + paramInt); 
    return byteOffset(paramInt);
  }
  
  private static long byteOffset(int paramInt) {
    return (paramInt << shift) + base;
  }
  
  public AtomicReferenceArray(int paramInt) {
    this.array = new Object[paramInt];
  }
  
  public AtomicReferenceArray(E[] paramArrayOfE) {
    this.array = Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class);
  }
  
  public final int length() {
    return this.array.length;
  }
  
  public final E get(int paramInt) {
    return getRaw(checkedByteOffset(paramInt));
  }
  
  private E getRaw(long paramLong) {
    return (E)unsafe.getObjectVolatile(this.array, paramLong);
  }
  
  public final void set(int paramInt, E paramE) {
    unsafe.putObjectVolatile(this.array, checkedByteOffset(paramInt), paramE);
  }
  
  public final void lazySet(int paramInt, E paramE) {
    unsafe.putOrderedObject(this.array, checkedByteOffset(paramInt), paramE);
  }
  
  public final E getAndSet(int paramInt, E paramE) {
    return (E)unsafe.getAndSetObject(this.array, checkedByteOffset(paramInt), paramE);
  }
  
  public final boolean compareAndSet(int paramInt, E paramE1, E paramE2) {
    return compareAndSetRaw(checkedByteOffset(paramInt), paramE1, paramE2);
  }
  
  private boolean compareAndSetRaw(long paramLong, E paramE1, E paramE2) {
    return unsafe.compareAndSwapObject(this.array, paramLong, paramE1, paramE2);
  }
  
  public final boolean weakCompareAndSet(int paramInt, E paramE1, E paramE2) {
    return compareAndSet(paramInt, paramE1, paramE2);
  }
  
  public final E getAndUpdate(int paramInt, UnaryOperator<E> paramUnaryOperator) {
    long l = checkedByteOffset(paramInt);
    while (true) {
      E e1 = getRaw(l);
      E e2 = paramUnaryOperator.apply(e1);
      if (compareAndSetRaw(l, e1, e2))
        return e1; 
    } 
  }
  
  public final E updateAndGet(int paramInt, UnaryOperator<E> paramUnaryOperator) {
    long l = checkedByteOffset(paramInt);
    while (true) {
      E e1 = getRaw(l);
      E e2 = paramUnaryOperator.apply(e1);
      if (compareAndSetRaw(l, e1, e2))
        return e2; 
    } 
  }
  
  public final E getAndAccumulate(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator) {
    long l = checkedByteOffset(paramInt);
    while (true) {
      E e1 = getRaw(l);
      E e2 = paramBinaryOperator.apply(e1, paramE);
      if (compareAndSetRaw(l, e1, e2))
        return e1; 
    } 
  }
  
  public final E accumulateAndGet(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator) {
    long l = checkedByteOffset(paramInt);
    while (true) {
      E e1 = getRaw(l);
      E e2 = paramBinaryOperator.apply(e1, paramE);
      if (compareAndSetRaw(l, e1, e2))
        return e2; 
    } 
  }
  
  public String toString() {
    int i = this.array.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (int j = 0;; j++) {
      stringBuilder.append(getRaw(byteOffset(j)));
      if (j == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(',').append(' ');
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException, InvalidObjectException {
    Object object = paramObjectInputStream.readFields().get("array", (Object)null);
    if (object == null || !object.getClass().isArray())
      throw new InvalidObjectException("Not array type"); 
    if (object.getClass() != Object[].class)
      object = Arrays.copyOf((Object[])object, Array.getLength(object), Object[].class); 
    unsafe.putObjectVolatile(this, arrayFieldOffset, object);
  }
}
