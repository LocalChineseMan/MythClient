package java.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {
  ReferencePipeline(Supplier<? extends Spliterator<?>> paramSupplier, int paramInt, boolean paramBoolean) {
    super(paramSupplier, paramInt, paramBoolean);
  }
  
  ReferencePipeline(Spliterator<?> paramSpliterator, int paramInt, boolean paramBoolean) {
    super(paramSpliterator, paramInt, paramBoolean);
  }
  
  ReferencePipeline(AbstractPipeline<?, P_IN, ?> paramAbstractPipeline, int paramInt) {
    super(paramAbstractPipeline, paramInt);
  }
  
  final StreamShape getOutputShape() {
    return StreamShape.REFERENCE;
  }
  
  final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<P_OUT[]> paramIntFunction) {
    return Nodes.collect(paramPipelineHelper, paramSpliterator, paramBoolean, paramIntFunction);
  }
  
  final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean) {
    return new StreamSpliterators.WrappingSpliterator<>(paramPipelineHelper, paramSupplier, paramBoolean);
  }
  
  final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> paramSupplier) {
    return new StreamSpliterators.DelegatingSpliterator<>(paramSupplier);
  }
  
  final void forEachWithCancel(Spliterator<P_OUT> paramSpliterator, Sink<P_OUT> paramSink) {
    do {
    
    } while (!paramSink.cancellationRequested() && paramSpliterator.tryAdvance(paramSink));
  }
  
  final Node.Builder<P_OUT> makeNodeBuilder(long paramLong, IntFunction<P_OUT[]> paramIntFunction) {
    return Nodes.builder(paramLong, paramIntFunction);
  }
  
  public final Iterator<P_OUT> iterator() {
    return Spliterators.iterator(spliterator());
  }
  
  public Stream<P_OUT> unordered() {
    if (!isOrdered())
      return (Stream<P_OUT>)this; 
    return (Stream<P_OUT>)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED);
  }
  
  public final Stream<P_OUT> filter(final Predicate<? super P_OUT> predicate) {
    Objects.requireNonNull(predicate);
    return (Stream)new StatelessOp<P_OUT, P_OUT>((AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<P_OUT> param1Sink) {
          return new Sink.ChainedReference<P_OUT, P_OUT>(param1Sink) {
              public void begin(long param2Long) {
                this.downstream.begin(-1L);
              }
              
              public void accept(P_OUT param2P_OUT) {
                if (predicate.test(param2P_OUT))
                  this.downstream.accept(param2P_OUT); 
              }
            };
        }
      };
  }
  
  public final <R> Stream<R> map(Function<? super P_OUT, ? extends R> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (Stream<R>)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT, paramFunction);
  }
  
  public final IntStream mapToInt(ToIntFunction<? super P_OUT> paramToIntFunction) {
    Objects.requireNonNull(paramToIntFunction);
    return (IntStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT, paramToIntFunction);
  }
  
  public final LongStream mapToLong(ToLongFunction<? super P_OUT> paramToLongFunction) {
    Objects.requireNonNull(paramToLongFunction);
    return (LongStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT, paramToLongFunction);
  }
  
  public final DoubleStream mapToDouble(ToDoubleFunction<? super P_OUT> paramToDoubleFunction) {
    Objects.requireNonNull(paramToDoubleFunction);
    return (DoubleStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT, paramToDoubleFunction);
  }
  
  public final <R> Stream<R> flatMap(Function<? super P_OUT, ? extends Stream<? extends R>> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (Stream<R>)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED, paramFunction);
  }
  
  public final IntStream flatMapToInt(Function<? super P_OUT, ? extends IntStream> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (IntStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED, paramFunction);
  }
  
  public final DoubleStream flatMapToDouble(Function<? super P_OUT, ? extends DoubleStream> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (DoubleStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED, paramFunction);
  }
  
  public final LongStream flatMapToLong(Function<? super P_OUT, ? extends LongStream> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (LongStream)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED, paramFunction);
  }
  
  public final Stream<P_OUT> peek(Consumer<? super P_OUT> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    return (Stream<P_OUT>)new Object(this, (AbstractPipeline)this, StreamShape.REFERENCE, 0, paramConsumer);
  }
  
  public final Stream<P_OUT> distinct() {
    return (Stream)DistinctOps.makeRef((AbstractPipeline<?, ?, ?>)this);
  }
  
  public final Stream<P_OUT> sorted() {
    return SortedOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this);
  }
  
  public final Stream<P_OUT> sorted(Comparator<? super P_OUT> paramComparator) {
    return SortedOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, paramComparator);
  }
  
  public final Stream<P_OUT> limit(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return SliceOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, 0L, paramLong);
  }
  
  public final Stream<P_OUT> skip(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    if (paramLong == 0L)
      return (Stream<P_OUT>)this; 
    return SliceOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, paramLong, -1L);
  }
  
  public void forEach(Consumer<? super P_OUT> paramConsumer) {
    evaluate(ForEachOps.makeRef(paramConsumer, false));
  }
  
  public void forEachOrdered(Consumer<? super P_OUT> paramConsumer) {
    evaluate(ForEachOps.makeRef(paramConsumer, true));
  }
  
  public final <A> A[] toArray(IntFunction<A[]> paramIntFunction) {
    IntFunction<A[]> intFunction = paramIntFunction;
    return (A[])Nodes.<Object>flatten(evaluateToArrayNode(intFunction), (IntFunction)intFunction)
      .asArray((IntFunction)intFunction);
  }
  
  public final Object[] toArray() {
    return toArray(paramInt -> new Object[paramInt]);
  }
  
  public final boolean anyMatch(Predicate<? super P_OUT> paramPredicate) {
    return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.ANY))).booleanValue();
  }
  
  public final boolean allMatch(Predicate<? super P_OUT> paramPredicate) {
    return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.ALL))).booleanValue();
  }
  
  public final boolean noneMatch(Predicate<? super P_OUT> paramPredicate) {
    return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.NONE))).booleanValue();
  }
  
  public final Optional<P_OUT> findFirst() {
    return (Optional<P_OUT>)evaluate(FindOps.makeRef(true));
  }
  
  public final Optional<P_OUT> findAny() {
    return (Optional<P_OUT>)evaluate(FindOps.makeRef(false));
  }
  
  public final P_OUT reduce(P_OUT paramP_OUT, BinaryOperator<P_OUT> paramBinaryOperator) {
    return (P_OUT)evaluate(ReduceOps.makeRef(paramP_OUT, paramBinaryOperator, paramBinaryOperator));
  }
  
  public final Optional<P_OUT> reduce(BinaryOperator<P_OUT> paramBinaryOperator) {
    return (Optional<P_OUT>)evaluate(ReduceOps.makeRef(paramBinaryOperator));
  }
  
  public final <R> R reduce(R paramR, BiFunction<R, ? super P_OUT, R> paramBiFunction, BinaryOperator<R> paramBinaryOperator) {
    return (R)evaluate(ReduceOps.makeRef(paramR, paramBiFunction, paramBinaryOperator));
  }
  
  public final <R, A> R collect(Collector<? super P_OUT, A, R> paramCollector) {
    Object object;
    if (isParallel() && paramCollector
      .characteristics().contains(Collector.Characteristics.CONCURRENT) && (
      !isOrdered() || paramCollector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
      object = paramCollector.supplier().get();
      BiConsumer<A, ? super P_OUT> biConsumer = paramCollector.accumulator();
      forEach(paramObject2 -> paramBiConsumer.accept(paramObject1, paramObject2));
    } else {
      object = evaluate(ReduceOps.makeRef(paramCollector));
    } 
    return paramCollector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH) ? (R)object : paramCollector
      
      .finisher().apply(object);
  }
  
  public final <R> R collect(Supplier<R> paramSupplier, BiConsumer<R, ? super P_OUT> paramBiConsumer, BiConsumer<R, R> paramBiConsumer1) {
    return (R)evaluate(ReduceOps.makeRef(paramSupplier, paramBiConsumer, paramBiConsumer1));
  }
  
  public final Optional<P_OUT> max(Comparator<? super P_OUT> paramComparator) {
    return reduce(BinaryOperator.maxBy(paramComparator));
  }
  
  public final Optional<P_OUT> min(Comparator<? super P_OUT> paramComparator) {
    return reduce(BinaryOperator.minBy(paramComparator));
  }
  
  public final long count() {
    return mapToLong(paramObject -> 1L).sum();
  }
  
  static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
    Head(Supplier<? extends Spliterator<?>> param1Supplier, int param1Int, boolean param1Boolean) {
      super(param1Supplier, param1Int, param1Boolean);
    }
    
    Head(Spliterator<?> param1Spliterator, int param1Int, boolean param1Boolean) {
      super(param1Spliterator, param1Int, param1Boolean);
    }
    
    final boolean opIsStateful() {
      throw new UnsupportedOperationException();
    }
    
    final Sink<E_IN> opWrapSink(int param1Int, Sink<E_OUT> param1Sink) {
      throw new UnsupportedOperationException();
    }
    
    public void forEach(Consumer<? super E_OUT> param1Consumer) {
      if (!isParallel()) {
        sourceStageSpliterator().forEachRemaining(param1Consumer);
      } else {
        super.forEach(param1Consumer);
      } 
    }
    
    public void forEachOrdered(Consumer<? super E_OUT> param1Consumer) {
      if (!isParallel()) {
        sourceStageSpliterator().forEachRemaining(param1Consumer);
      } else {
        super.forEachOrdered(param1Consumer);
      } 
    }
  }
  
  static abstract class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
    StatelessOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() {
      return false;
    }
  }
  
  static abstract class ReferencePipeline {}
}
