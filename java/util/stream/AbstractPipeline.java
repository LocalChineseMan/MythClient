package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

abstract class AbstractPipeline<E_IN, E_OUT, S extends BaseStream<E_OUT, S>> extends PipelineHelper<E_OUT> implements BaseStream<E_OUT, S> {
  private static final String MSG_STREAM_LINKED = "stream has already been operated upon or closed";
  
  private static final String MSG_CONSUMED = "source already consumed or closed";
  
  private final AbstractPipeline sourceStage;
  
  private final AbstractPipeline previousStage;
  
  protected final int sourceOrOpFlags;
  
  private AbstractPipeline nextStage;
  
  private int depth;
  
  private int combinedFlags;
  
  private Spliterator<?> sourceSpliterator;
  
  private Supplier<? extends Spliterator<?>> sourceSupplier;
  
  private boolean linkedOrConsumed;
  
  private boolean sourceAnyStateful;
  
  private Runnable sourceCloseAction;
  
  private boolean parallel;
  
  AbstractPipeline(Supplier<? extends Spliterator<?>> paramSupplier, int paramInt, boolean paramBoolean) {
    this.previousStage = null;
    this.sourceSupplier = paramSupplier;
    this.sourceStage = this;
    this.sourceOrOpFlags = paramInt & StreamOpFlag.STREAM_MASK;
    this.combinedFlags = (this.sourceOrOpFlags << 1 ^ 0xFFFFFFFF) & StreamOpFlag.INITIAL_OPS_VALUE;
    this.depth = 0;
    this.parallel = paramBoolean;
  }
  
  AbstractPipeline(Spliterator<?> paramSpliterator, int paramInt, boolean paramBoolean) {
    this.previousStage = null;
    this.sourceSpliterator = paramSpliterator;
    this.sourceStage = this;
    this.sourceOrOpFlags = paramInt & StreamOpFlag.STREAM_MASK;
    this.combinedFlags = (this.sourceOrOpFlags << 1 ^ 0xFFFFFFFF) & StreamOpFlag.INITIAL_OPS_VALUE;
    this.depth = 0;
    this.parallel = paramBoolean;
  }
  
  AbstractPipeline(AbstractPipeline<?, E_IN, ?> paramAbstractPipeline, int paramInt) {
    if (paramAbstractPipeline.linkedOrConsumed)
      throw new IllegalStateException("stream has already been operated upon or closed"); 
    paramAbstractPipeline.linkedOrConsumed = true;
    paramAbstractPipeline.nextStage = this;
    this.previousStage = paramAbstractPipeline;
    this.sourceOrOpFlags = paramInt & StreamOpFlag.OP_MASK;
    this.combinedFlags = StreamOpFlag.combineOpFlags(paramInt, paramAbstractPipeline.combinedFlags);
    this.sourceStage = paramAbstractPipeline.sourceStage;
    if (opIsStateful())
      this.sourceStage.sourceAnyStateful = true; 
    this.depth = paramAbstractPipeline.depth + 1;
  }
  
  final <R> R evaluate(TerminalOp<E_OUT, R> paramTerminalOp) {
    assert getOutputShape() == paramTerminalOp.inputShape();
    if (this.linkedOrConsumed)
      throw new IllegalStateException("stream has already been operated upon or closed"); 
    this.linkedOrConsumed = true;
    return isParallel() ? paramTerminalOp
      .evaluateParallel(this, sourceSpliterator(paramTerminalOp.getOpFlags())) : paramTerminalOp
      .evaluateSequential(this, sourceSpliterator(paramTerminalOp.getOpFlags()));
  }
  
  final Node<E_OUT> evaluateToArrayNode(IntFunction<E_OUT[]> paramIntFunction) {
    if (this.linkedOrConsumed)
      throw new IllegalStateException("stream has already been operated upon or closed"); 
    this.linkedOrConsumed = true;
    if (isParallel() && this.previousStage != null && opIsStateful())
      return opEvaluateParallel(this.previousStage, this.previousStage.sourceSpliterator(0), paramIntFunction); 
    return evaluate(sourceSpliterator(0), true, paramIntFunction);
  }
  
  final Spliterator<E_OUT> sourceStageSpliterator() {
    if (this != this.sourceStage)
      throw new IllegalStateException(); 
    if (this.linkedOrConsumed)
      throw new IllegalStateException("stream has already been operated upon or closed"); 
    this.linkedOrConsumed = true;
    if (this.sourceStage.sourceSpliterator != null) {
      Spliterator<?> spliterator = this.sourceStage.sourceSpliterator;
      this.sourceStage.sourceSpliterator = null;
      return (Spliterator)spliterator;
    } 
    if (this.sourceStage.sourceSupplier != null) {
      Spliterator<E_OUT> spliterator = (Spliterator)this.sourceStage.sourceSupplier.get();
      this.sourceStage.sourceSupplier = null;
      return spliterator;
    } 
    throw new IllegalStateException("source already consumed or closed");
  }
  
  public final S sequential() {
    this.sourceStage.parallel = false;
    return (S)this;
  }
  
  public final S parallel() {
    this.sourceStage.parallel = true;
    return (S)this;
  }
  
  public void close() {
    this.linkedOrConsumed = true;
    this.sourceSupplier = null;
    this.sourceSpliterator = null;
    if (this.sourceStage.sourceCloseAction != null) {
      Runnable runnable = this.sourceStage.sourceCloseAction;
      this.sourceStage.sourceCloseAction = null;
      runnable.run();
    } 
  }
  
  public S onClose(Runnable paramRunnable) {
    Runnable runnable = this.sourceStage.sourceCloseAction;
    this.sourceStage
      
      .sourceCloseAction = (runnable == null) ? paramRunnable : Streams.composeWithExceptions(runnable, paramRunnable);
    return (S)this;
  }
  
  public Spliterator<E_OUT> spliterator() {
    if (this.linkedOrConsumed)
      throw new IllegalStateException("stream has already been operated upon or closed"); 
    this.linkedOrConsumed = true;
    if (this == this.sourceStage) {
      if (this.sourceStage.sourceSpliterator != null) {
        Spliterator<?> spliterator = this.sourceStage.sourceSpliterator;
        this.sourceStage.sourceSpliterator = null;
        return (Spliterator)spliterator;
      } 
      if (this.sourceStage.sourceSupplier != null) {
        Supplier<? extends Spliterator<?>> supplier = this.sourceStage.sourceSupplier;
        this.sourceStage.sourceSupplier = null;
        return lazySpliterator((Supplier)supplier);
      } 
      throw new IllegalStateException("source already consumed or closed");
    } 
    return wrap(this, () -> sourceSpliterator(0), isParallel());
  }
  
  public final boolean isParallel() {
    return this.sourceStage.parallel;
  }
  
  final int getStreamFlags() {
    return StreamOpFlag.toStreamFlags(this.combinedFlags);
  }
  
  private void parallelPrepare(int paramInt) {
    AbstractPipeline abstractPipeline = this.sourceStage;
    if (this.sourceStage.sourceAnyStateful) {
      byte b = 1;
      AbstractPipeline abstractPipeline1 = this.sourceStage, abstractPipeline2 = this.sourceStage.nextStage;
      for (; abstractPipeline2 != null; 
        abstractPipeline1 = abstractPipeline2, abstractPipeline2 = abstractPipeline2.nextStage) {
        int i = abstractPipeline2.sourceOrOpFlags;
        if (abstractPipeline2.opIsStateful()) {
          if (StreamOpFlag.SHORT_CIRCUIT.isKnown(i)) {
            abstractPipeline = abstractPipeline2;
            i &= StreamOpFlag.IS_SHORT_CIRCUIT ^ 0xFFFFFFFF;
          } 
          b = 0;
          i = i & (StreamOpFlag.NOT_SIZED ^ 0xFFFFFFFF) | StreamOpFlag.IS_SIZED;
        } 
        abstractPipeline2.depth = b++;
        abstractPipeline2.combinedFlags = StreamOpFlag.combineOpFlags(i, abstractPipeline1.combinedFlags);
      } 
    } 
    if (paramInt != 0) {
      int i = paramInt & StreamOpFlag.UPSTREAM_TERMINAL_OP_MASK;
      for (AbstractPipeline abstractPipeline1 = abstractPipeline; abstractPipeline1.nextStage != null; abstractPipeline1 = abstractPipeline1.nextStage)
        abstractPipeline1.combinedFlags = StreamOpFlag.combineOpFlags(i, abstractPipeline1.combinedFlags); 
      this.combinedFlags = StreamOpFlag.combineOpFlags(paramInt, this.combinedFlags);
    } 
  }
  
  private Spliterator<?> sourceSpliterator(int paramInt) {
    Spliterator<?> spliterator = null;
    if (this.sourceStage.sourceSpliterator != null) {
      spliterator = this.sourceStage.sourceSpliterator;
      this.sourceStage.sourceSpliterator = null;
    } else if (this.sourceStage.sourceSupplier != null) {
      spliterator = this.sourceStage.sourceSupplier.get();
      this.sourceStage.sourceSupplier = null;
    } else {
      throw new IllegalStateException("source already consumed or closed");
    } 
    if (isParallel()) {
      parallelPrepare(paramInt);
      AbstractPipeline abstractPipeline1 = this.sourceStage, abstractPipeline2 = this.sourceStage.nextStage, abstractPipeline3 = this;
      for (; abstractPipeline1 != abstractPipeline3; 
        abstractPipeline1 = abstractPipeline2, abstractPipeline2 = abstractPipeline2.nextStage) {
        if (abstractPipeline2.opIsStateful())
          spliterator = abstractPipeline2.opEvaluateParallelLazy(abstractPipeline1, spliterator); 
      } 
    } else if (paramInt != 0) {
      this.combinedFlags = StreamOpFlag.combineOpFlags(paramInt, this.combinedFlags);
    } 
    return spliterator;
  }
  
  final StreamShape getSourceShape() {
    AbstractPipeline abstractPipeline = this;
    while (abstractPipeline.depth > 0)
      abstractPipeline = abstractPipeline.previousStage; 
    return abstractPipeline.getOutputShape();
  }
  
  final <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> paramSpliterator) {
    return StreamOpFlag.SIZED.isKnown(getStreamAndOpFlags()) ? paramSpliterator.getExactSizeIfKnown() : -1L;
  }
  
  final <P_IN, S extends Sink<E_OUT>> S wrapAndCopyInto(S paramS, Spliterator<P_IN> paramSpliterator) {
    copyInto(wrapSink(Objects.<Sink>requireNonNull((Sink)paramS)), paramSpliterator);
    return paramS;
  }
  
  final <P_IN> void copyInto(Sink<P_IN> paramSink, Spliterator<P_IN> paramSpliterator) {
    Objects.requireNonNull(paramSink);
    if (!StreamOpFlag.SHORT_CIRCUIT.isKnown(getStreamAndOpFlags())) {
      paramSink.begin(paramSpliterator.getExactSizeIfKnown());
      paramSpliterator.forEachRemaining(paramSink);
      paramSink.end();
    } else {
      copyIntoWithCancel(paramSink, paramSpliterator);
    } 
  }
  
  final <P_IN> void copyIntoWithCancel(Sink<P_IN> paramSink, Spliterator<P_IN> paramSpliterator) {
    AbstractPipeline abstractPipeline = this;
    while (abstractPipeline.depth > 0)
      abstractPipeline = abstractPipeline.previousStage; 
    paramSink.begin(paramSpliterator.getExactSizeIfKnown());
    abstractPipeline.forEachWithCancel(paramSpliterator, paramSink);
    paramSink.end();
  }
  
  final int getStreamAndOpFlags() {
    return this.combinedFlags;
  }
  
  final boolean isOrdered() {
    return StreamOpFlag.ORDERED.isKnown(this.combinedFlags);
  }
  
  final <P_IN> Sink<P_IN> wrapSink(Sink<E_OUT> paramSink) {
    Objects.requireNonNull(paramSink);
    for (AbstractPipeline abstractPipeline = this; abstractPipeline.depth > 0; abstractPipeline = abstractPipeline.previousStage)
      paramSink = abstractPipeline.opWrapSink(abstractPipeline.previousStage.combinedFlags, paramSink); 
    return paramSink;
  }
  
  final <P_IN> Spliterator<E_OUT> wrapSpliterator(Spliterator<P_IN> paramSpliterator) {
    if (this.depth == 0)
      return paramSpliterator; 
    return wrap(this, () -> paramSpliterator, isParallel());
  }
  
  final <P_IN> Node<E_OUT> evaluate(Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<E_OUT[]> paramIntFunction) {
    if (isParallel())
      return evaluateToNode(this, paramSpliterator, paramBoolean, paramIntFunction); 
    Node.Builder<E_OUT> builder = makeNodeBuilder(
        exactOutputSizeIfKnown(paramSpliterator), paramIntFunction);
    return ((Node.Builder<E_OUT>)wrapAndCopyInto(builder, paramSpliterator)).build();
  }
  
  <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, IntFunction<E_OUT[]> paramIntFunction) {
    throw new UnsupportedOperationException("Parallel evaluation is not supported");
  }
  
  <P_IN> Spliterator<E_OUT> opEvaluateParallelLazy(PipelineHelper<E_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator) {
    return opEvaluateParallel(paramPipelineHelper, paramSpliterator, paramInt -> new Object[paramInt]).spliterator();
  }
  
  abstract StreamShape getOutputShape();
  
  abstract <P_IN> Node<E_OUT> evaluateToNode(PipelineHelper<E_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<E_OUT[]> paramIntFunction);
  
  abstract <P_IN> Spliterator<E_OUT> wrap(PipelineHelper<E_OUT> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean);
  
  abstract Spliterator<E_OUT> lazySpliterator(Supplier<? extends Spliterator<E_OUT>> paramSupplier);
  
  abstract void forEachWithCancel(Spliterator<E_OUT> paramSpliterator, Sink<E_OUT> paramSink);
  
  abstract Node.Builder<E_OUT> makeNodeBuilder(long paramLong, IntFunction<E_OUT[]> paramIntFunction);
  
  abstract boolean opIsStateful();
  
  abstract Sink<E_IN> opWrapSink(int paramInt, Sink<E_OUT> paramSink);
}
