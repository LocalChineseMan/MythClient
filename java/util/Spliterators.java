package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class Spliterators {
  public static <T> Spliterator<T> emptySpliterator() {
    return (Spliterator)EMPTY_SPLITERATOR;
  }
  
  private static final Spliterator<Object> EMPTY_SPLITERATOR = new EmptySpliterator.OfRef();
  
  public static Spliterator.OfInt emptyIntSpliterator() {
    return EMPTY_INT_SPLITERATOR;
  }
  
  private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new EmptySpliterator.OfInt();
  
  public static Spliterator.OfLong emptyLongSpliterator() {
    return EMPTY_LONG_SPLITERATOR;
  }
  
  private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new EmptySpliterator.OfLong();
  
  public static Spliterator.OfDouble emptyDoubleSpliterator() {
    return EMPTY_DOUBLE_SPLITERATOR;
  }
  
  private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new EmptySpliterator.OfDouble();
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt) {
    return new ArraySpliterator<>(Objects.<Object[]>requireNonNull(paramArrayOfObject), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds(((Object[])Objects.requireNonNull((T)paramArrayOfObject)).length, paramInt1, paramInt2);
    return new ArraySpliterator<>(paramArrayOfObject, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfint, int paramInt) {
    return new IntArraySpliterator(Objects.<int[]>requireNonNull(paramArrayOfint), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds(((int[])Objects.requireNonNull((T)paramArrayOfint)).length, paramInt1, paramInt2);
    return new IntArraySpliterator(paramArrayOfint, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOflong, int paramInt) {
    return new LongArraySpliterator(Objects.<long[]>requireNonNull(paramArrayOflong), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds(((long[])Objects.requireNonNull((T)paramArrayOflong)).length, paramInt1, paramInt2);
    return new LongArraySpliterator(paramArrayOflong, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfdouble, int paramInt) {
    return new DoubleArraySpliterator(Objects.<double[]>requireNonNull(paramArrayOfdouble), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfdouble, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds(((double[])Objects.requireNonNull((T)paramArrayOfdouble)).length, paramInt1, paramInt2);
    return new DoubleArraySpliterator(paramArrayOfdouble, paramInt1, paramInt2, paramInt3);
  }
  
  private static void checkFromToBounds(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 > paramInt3)
      throw new ArrayIndexOutOfBoundsException("origin(" + paramInt2 + ") > fence(" + paramInt3 + ")"); 
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt2); 
    if (paramInt3 > paramInt1)
      throw new ArrayIndexOutOfBoundsException(paramInt3); 
  }
  
  public static <T> Spliterator<T> spliterator(Collection<? extends T> paramCollection, int paramInt) {
    return new IteratorSpliterator<>(Objects.<Collection<? extends T>>requireNonNull(paramCollection), paramInt);
  }
  
  public static <T> Spliterator<T> spliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt) {
    return new IteratorSpliterator<>(Objects.<Iterator<? extends T>>requireNonNull(paramIterator), paramLong, paramInt);
  }
  
  public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> paramIterator, int paramInt) {
    return new IteratorSpliterator<>(Objects.<Iterator<? extends T>>requireNonNull(paramIterator), paramInt);
  }
  
  public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt) {
    return new IntIteratorSpliterator(Objects.<PrimitiveIterator.OfInt>requireNonNull(paramOfInt), paramLong, paramInt);
  }
  
  public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt paramOfInt, int paramInt) {
    return new IntIteratorSpliterator(Objects.<PrimitiveIterator.OfInt>requireNonNull(paramOfInt), paramInt);
  }
  
  public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt) {
    return new LongIteratorSpliterator(Objects.<PrimitiveIterator.OfLong>requireNonNull(paramOfLong), paramLong, paramInt);
  }
  
  public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong paramOfLong, int paramInt) {
    return new LongIteratorSpliterator(Objects.<PrimitiveIterator.OfLong>requireNonNull(paramOfLong), paramInt);
  }
  
  public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt) {
    return new DoubleIteratorSpliterator(Objects.<PrimitiveIterator.OfDouble>requireNonNull(paramOfDouble), paramLong, paramInt);
  }
  
  public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble paramOfDouble, int paramInt) {
    return new DoubleIteratorSpliterator(Objects.<PrimitiveIterator.OfDouble>requireNonNull(paramOfDouble), paramInt);
  }
  
  public static <T> Iterator<T> iterator(Spliterator<? extends T> paramSpliterator) {
    Objects.requireNonNull(paramSpliterator);
    return new Adapter(paramSpliterator);
  }
  
  public static PrimitiveIterator.OfInt iterator(Spliterator.OfInt paramOfInt) {
    Objects.requireNonNull(paramOfInt);
    return new Adapter(paramOfInt);
  }
  
  public static PrimitiveIterator.OfLong iterator(Spliterator.OfLong paramOfLong) {
    Objects.requireNonNull(paramOfLong);
    return new Adapter(paramOfLong);
  }
  
  public static PrimitiveIterator.OfDouble iterator(Spliterator.OfDouble paramOfDouble) {
    Objects.requireNonNull(paramOfDouble);
    return new Adapter(paramOfDouble);
  }
  
  private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C> {
    public S trySplit() {
      return null;
    }
    
    public boolean tryAdvance(C param1C) {
      Objects.requireNonNull(param1C);
      return false;
    }
    
    public void forEachRemaining(C param1C) {
      Objects.requireNonNull(param1C);
    }
    
    public long estimateSize() {
      return 0L;
    }
    
    public int characteristics() {
      return 16448;
    }
    
    private static final class OfRef<T> extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {}
    
    private static final class OfInt extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {}
    
    private static final class OfLong extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {}
    
    private static final class OfDouble extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {}
  }
  
  private static final class OfRef<T> extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {}
  
  private static final class OfInt extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {}
  
  private static final class OfLong extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {}
  
  private static final class OfDouble extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {}
  
  static final class ArraySpliterator<T> implements Spliterator<T> {
    private final Object[] array;
    
    private int index;
    
    private final int fence;
    
    private final int characteristics;
    
    public ArraySpliterator(Object[] param1ArrayOfObject, int param1Int) {
      this(param1ArrayOfObject, 0, param1ArrayOfObject.length, param1Int);
    }
    
    public ArraySpliterator(Object[] param1ArrayOfObject, int param1Int1, int param1Int2, int param1Int3) {
      this.array = param1ArrayOfObject;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.characteristics = param1Int3 | 0x40 | 0x4000;
    }
    
    public Spliterator<T> trySplit() {
      int i = this.index, j = i + this.fence >>> 1;
      return (i >= j) ? null : new ArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Object[] arrayOfObject;
      int i;
      int j;
      if ((arrayOfObject = this.array).length >= (j = this.fence) && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1Consumer.accept((T)arrayOfObject[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.index >= 0 && this.index < this.fence) {
        Object object = this.array[this.index++];
        param1Consumer.accept((T)object);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() {
      return (this.fence - this.index);
    }
    
    public int characteristics() {
      return this.characteristics;
    }
    
    public Comparator<? super T> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static class IteratorSpliterator<T> implements Spliterator<T> {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private final Collection<? extends T> collection;
    
    private Iterator<? extends T> it;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    public IteratorSpliterator(Collection<? extends T> param1Collection, int param1Int) {
      this.collection = param1Collection;
      this.it = null;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public IteratorSpliterator(Iterator<? extends T> param1Iterator, long param1Long, int param1Int) {
      this.collection = null;
      this.it = param1Iterator;
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public IteratorSpliterator(Iterator<? extends T> param1Iterator, int param1Int) {
      this.collection = null;
      this.it = param1Iterator;
      this.est = Long.MAX_VALUE;
      this.characteristics = param1Int & 0xFFFFBFBF;
    }
    
    public Spliterator<T> trySplit() {
      Iterator<? extends T> iterator = this.it = this.collection.iterator();
      long l = this.est = this.collection.size();
      l = this.est;
      if (l > 1L && iterator.hasNext()) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        Object[] arrayOfObject = new Object[i];
        byte b = 0;
        do {
          arrayOfObject[b] = iterator.next();
        } while (++b < i && iterator.hasNext());
        this.batch = b;
        if (this.est != Long.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.ArraySpliterator<>(arrayOfObject, 0, b, this.characteristics);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Iterator<? extends T> iterator;
      if ((iterator = this.it) == null) {
        iterator = this.it = this.collection.iterator();
        this.est = this.collection.size();
      } 
      iterator.forEachRemaining(param1Consumer);
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.it == null) {
        this.it = this.collection.iterator();
        this.est = this.collection.size();
      } 
      if (this.it.hasNext()) {
        param1Consumer.accept(this.it.next());
        return true;
      } 
      return false;
    }
    
    public long estimateSize() {
      if (this.it == null) {
        this.it = this.collection.iterator();
        return this.est = this.collection.size();
      } 
      return this.est;
    }
    
    public int characteristics() {
      return this.characteristics;
    }
    
    public Comparator<? super T> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class Spliterators {}
  
  static final class Spliterators {}
  
  static final class Spliterators {}
  
  public static abstract class Spliterators {}
  
  public static abstract class Spliterators {}
  
  public static abstract class Spliterators {}
  
  public static abstract class Spliterators {}
  
  static final class Spliterators {}
  
  static final class Spliterators {}
  
  static final class Spliterators {}
}
