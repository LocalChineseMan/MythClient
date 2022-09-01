package java.util;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {
  void forEachRemaining(T_CONS paramT_CONS);
  
  public static interface PrimitiveIterator {}
  
  public static interface PrimitiveIterator {}
  
  public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
    default void forEachRemaining(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      while (hasNext())
        param1IntConsumer.accept(nextInt()); 
    }
    
    default Integer next() {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()"); 
      return Integer.valueOf(nextInt());
    }
    
    default void forEachRemaining(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer) {
        forEachRemaining((IntConsumer)param1Consumer);
      } else {
        Objects.requireNonNull(param1Consumer);
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
    
    int nextInt();
  }
}
