package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

final class ForEachOps {
  public static <T> TerminalOp<T, Void> makeRef(Consumer<? super T> paramConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramConsumer);
    return (TerminalOp)new ForEachOp.OfRef<>(paramConsumer, paramBoolean);
  }
  
  public static TerminalOp<Integer, Void> makeInt(IntConsumer paramIntConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramIntConsumer);
    return (TerminalOp<Integer, Void>)new ForEachOp.OfInt(paramIntConsumer, paramBoolean);
  }
  
  public static TerminalOp<Long, Void> makeLong(LongConsumer paramLongConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramLongConsumer);
    return (TerminalOp<Long, Void>)new ForEachOp.OfLong(paramLongConsumer, paramBoolean);
  }
  
  public static TerminalOp<Double, Void> makeDouble(DoubleConsumer paramDoubleConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramDoubleConsumer);
    return (TerminalOp<Double, Void>)new ForEachOp.OfDouble(paramDoubleConsumer, paramBoolean);
  }
  
  static final class ForEachOps {}
  
  static final class ForEachOps {}
  
  static abstract class ForEachOp<T> implements TerminalOp<T, Void>, TerminalSink<T, Void> {
    private final boolean ordered;
    
    protected ForEachOp(boolean param1Boolean) {
      this.ordered = param1Boolean;
    }
    
    public int getOpFlags() {
      return this.ordered ? 0 : StreamOpFlag.NOT_ORDERED;
    }
    
    public <S> Void evaluateSequential(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) {
      return ((ForEachOp)param1PipelineHelper.wrapAndCopyInto(this, param1Spliterator)).get();
    }
    
    public <S> Void evaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) {
      if (this.ordered) {
        (new ForEachOps.ForEachOrderedTask<>(param1PipelineHelper, param1Spliterator, (Sink<?>)this)).invoke();
      } else {
        (new ForEachOps.ForEachTask<>(param1PipelineHelper, param1Spliterator, param1PipelineHelper.wrapSink((Sink<T>)this))).invoke();
      } 
      return null;
    }
    
    public Void get() {
      return null;
    }
    
    static final class ForEachOp {}
    
    static final class ForEachOp {}
    
    static final class ForEachOp {}
    
    static final class OfRef<T> extends ForEachOp<T> {
      final Consumer<? super T> consumer;
      
      OfRef(Consumer<? super T> param2Consumer, boolean param2Boolean) {
        super(param2Boolean);
        this.consumer = param2Consumer;
      }
      
      public void accept(T param2T) {
        this.consumer.accept(param2T);
      }
    }
  }
  
  static final class OfRef<T> extends ForEachOp<T> {
    final Consumer<? super T> consumer;
    
    OfRef(Consumer<? super T> param1Consumer, boolean param1Boolean) {
      super(param1Boolean);
      this.consumer = param1Consumer;
    }
    
    public void accept(T param1T) {
      this.consumer.accept(param1T);
    }
  }
  
  static final class ForEachOps {}
  
  static final class ForEachOps {}
  
  static final class ForEachOps {}
}
