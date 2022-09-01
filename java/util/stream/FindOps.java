package java.util.stream;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class FindOps {
  public static <T> TerminalOp<T, Optional<T>> makeRef(boolean paramBoolean) {
    return new FindOp<>(paramBoolean, StreamShape.REFERENCE, Optional.empty(), Optional::isPresent, OfRef::new);
  }
  
  public static TerminalOp<Integer, OptionalInt> makeInt(boolean paramBoolean) {
    return new FindOp<>(paramBoolean, StreamShape.INT_VALUE, OptionalInt.empty(), OptionalInt::isPresent, OfInt::new);
  }
  
  public static TerminalOp<Long, OptionalLong> makeLong(boolean paramBoolean) {
    return new FindOp<>(paramBoolean, StreamShape.LONG_VALUE, OptionalLong.empty(), OptionalLong::isPresent, OfLong::new);
  }
  
  public static TerminalOp<Double, OptionalDouble> makeDouble(boolean paramBoolean) {
    return new FindOp<>(paramBoolean, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), OptionalDouble::isPresent, OfDouble::new);
  }
  
  private static final class FindOp<T, O> implements TerminalOp<T, O> {
    private final StreamShape shape;
    
    final boolean mustFindFirst;
    
    final O emptyValue;
    
    final Predicate<O> presentPredicate;
    
    final Supplier<TerminalSink<T, O>> sinkSupplier;
    
    FindOp(boolean param1Boolean, StreamShape param1StreamShape, O param1O, Predicate<O> param1Predicate, Supplier<TerminalSink<T, O>> param1Supplier) {
      this.mustFindFirst = param1Boolean;
      this.shape = param1StreamShape;
      this.emptyValue = param1O;
      this.presentPredicate = param1Predicate;
      this.sinkSupplier = param1Supplier;
    }
    
    public int getOpFlags() {
      return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED);
    }
    
    public StreamShape inputShape() {
      return this.shape;
    }
    
    public <S> O evaluateSequential(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) {
      Object object = ((TerminalSink)param1PipelineHelper.wrapAndCopyInto(this.sinkSupplier.get(), param1Spliterator)).get();
      return (object != null) ? (O)object : this.emptyValue;
    }
    
    public <P_IN> O evaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
      return (new FindOps.FindTask<>(this, param1PipelineHelper, param1Spliterator)).invoke();
    }
  }
  
  private static abstract class FindSink<T, O> implements TerminalSink<T, O> {
    boolean hasValue;
    
    T value;
    
    public void accept(T param1T) {
      if (!this.hasValue) {
        this.hasValue = true;
        this.value = param1T;
      } 
    }
    
    public boolean cancellationRequested() {
      return this.hasValue;
    }
    
    static final class FindSink {}
    
    static final class FindSink {}
    
    static final class FindSink {}
    
    static final class OfRef<T> extends FindSink<T, Optional<T>> {
      public Optional<T> get() {
        return this.hasValue ? Optional.<T>of(this.value) : null;
      }
    }
  }
  
  static final class OfRef<T> extends FindSink<T, Optional<T>> {
    public Optional<T> get() {
      return this.hasValue ? Optional.<T>of(this.value) : null;
    }
  }
  
  private static final class FindOps {}
  
  static final class FindOps {}
  
  static final class FindOps {}
  
  static final class FindOps {}
}
