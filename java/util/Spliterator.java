package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface Spliterator<T> {
  public static final int ORDERED = 16;
  
  public static final int DISTINCT = 1;
  
  public static final int SORTED = 4;
  
  public static final int SIZED = 64;
  
  public static final int NONNULL = 256;
  
  public static final int IMMUTABLE = 1024;
  
  public static final int CONCURRENT = 4096;
  
  public static final int SUBSIZED = 16384;
  
  boolean tryAdvance(Consumer<? super T> paramConsumer);
  
  default void forEachRemaining(Consumer<? super T> paramConsumer) {
    do {
    
    } while (tryAdvance(paramConsumer));
  }
  
  Spliterator<T> trySplit();
  
  long estimateSize();
  
  default long getExactSizeIfKnown() {
    return ((characteristics() & 0x40) == 0) ? -1L : estimateSize();
  }
  
  int characteristics();
  
  default boolean hasCharacteristics(int paramInt) {
    return ((characteristics() & paramInt) == paramInt);
  }
  
  default Comparator<? super T> getComparator() {
    throw new IllegalStateException();
  }
  
  public static interface OfPrimitive<T, T_CONS, T_SPLITR extends OfPrimitive<T, T_CONS, T_SPLITR>> extends Spliterator<T> {
    default void forEachRemaining(T_CONS param1T_CONS) {
      do {
      
      } while (tryAdvance(param1T_CONS));
    }
    
    boolean tryAdvance(T_CONS param1T_CONS);
    
    T_SPLITR trySplit();
  }
  
  public static interface OfInt extends OfPrimitive<Integer, IntConsumer, OfInt> {
    default void forEachRemaining(IntConsumer param1IntConsumer) {
      do {
      
      } while (tryAdvance(param1IntConsumer));
    }
    
    default boolean tryAdvance(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer)
        return tryAdvance((IntConsumer)param1Consumer); 
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)"); 
      return tryAdvance(param1Consumer::accept);
    }
    
    default void forEachRemaining(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer) {
        forEachRemaining((IntConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
    
    OfInt trySplit();
    
    boolean tryAdvance(IntConsumer param1IntConsumer);
  }
  
  public static interface OfLong extends OfPrimitive<Long, LongConsumer, OfLong> {
    default void forEachRemaining(LongConsumer param1LongConsumer) {
      do {
      
      } while (tryAdvance(param1LongConsumer));
    }
    
    default boolean tryAdvance(Consumer<? super Long> param1Consumer) {
      if (param1Consumer instanceof LongConsumer)
        return tryAdvance((LongConsumer)param1Consumer); 
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)"); 
      return tryAdvance(param1Consumer::accept);
    }
    
    default void forEachRemaining(Consumer<? super Long> param1Consumer) {
      if (param1Consumer instanceof LongConsumer) {
        forEachRemaining((LongConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
    
    OfLong trySplit();
    
    boolean tryAdvance(LongConsumer param1LongConsumer);
  }
  
  public static interface OfDouble extends OfPrimitive<Double, DoubleConsumer, OfDouble> {
    default void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      do {
      
      } while (tryAdvance(param1DoubleConsumer));
    }
    
    default boolean tryAdvance(Consumer<? super Double> param1Consumer) {
      if (param1Consumer instanceof DoubleConsumer)
        return tryAdvance((DoubleConsumer)param1Consumer); 
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)"); 
      return tryAdvance(param1Consumer::accept);
    }
    
    default void forEachRemaining(Consumer<? super Double> param1Consumer) {
      if (param1Consumer instanceof DoubleConsumer) {
        forEachRemaining((DoubleConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
    
    OfDouble trySplit();
    
    boolean tryAdvance(DoubleConsumer param1DoubleConsumer);
  }
}
