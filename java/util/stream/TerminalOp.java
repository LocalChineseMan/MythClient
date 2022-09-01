package java.util.stream;

import java.util.Spliterator;

interface TerminalOp<E_IN, R> {
  default StreamShape inputShape() {
    return StreamShape.REFERENCE;
  }
  
  default int getOpFlags() {
    return 0;
  }
  
  default <P_IN> R evaluateParallel(PipelineHelper<E_IN> paramPipelineHelper, Spliterator<P_IN> paramSpliterator) {
    if (Tripwire.ENABLED)
      Tripwire.trip(getClass(), "{0} triggering TerminalOp.evaluateParallel serial default"); 
    return evaluateSequential(paramPipelineHelper, paramSpliterator);
  }
  
  <P_IN> R evaluateSequential(PipelineHelper<E_IN> paramPipelineHelper, Spliterator<P_IN> paramSpliterator);
}
