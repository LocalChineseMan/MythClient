package java.util.stream;

import java.util.Objects;
import java.util.function.Consumer;

interface Sink<T> extends Consumer<T> {
  default void begin(long paramLong) {}
  
  default void end() {}
  
  default boolean cancellationRequested() {
    return false;
  }
  
  default void accept(int paramInt) {
    throw new IllegalStateException("called wrong accept method");
  }
  
  default void accept(long paramLong) {
    throw new IllegalStateException("called wrong accept method");
  }
  
  default void accept(double paramDouble) {
    throw new IllegalStateException("called wrong accept method");
  }
  
  public static abstract class Sink {}
  
  public static abstract class Sink {}
  
  public static abstract class Sink {}
  
  public static abstract class ChainedReference<T, E_OUT> implements Sink<T> {
    protected final Sink<? super E_OUT> downstream;
    
    public ChainedReference(Sink<? super E_OUT> param1Sink) {
      this.downstream = Objects.<Sink<? super E_OUT>>requireNonNull(param1Sink);
    }
    
    public void begin(long param1Long) {
      this.downstream.begin(param1Long);
    }
    
    public void end() {
      this.downstream.end();
    }
    
    public boolean cancellationRequested() {
      return this.downstream.cancellationRequested();
    }
  }
  
  public static interface Sink {}
  
  public static interface Sink {}
  
  public static interface Sink {}
}
