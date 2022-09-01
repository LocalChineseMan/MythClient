package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import sun.misc.Unsafe;

public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
  volatile Object result;
  
  volatile Completion stack;
  
  final boolean internalComplete(Object paramObject) {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, paramObject);
  }
  
  final boolean casStack(Completion paramCompletion1, Completion paramCompletion2) {
    return UNSAFE.compareAndSwapObject(this, STACK, paramCompletion1, paramCompletion2);
  }
  
  final boolean tryPushStack(Completion paramCompletion) {
    Completion completion = this.stack;
    lazySetNext(paramCompletion, completion);
    return UNSAFE.compareAndSwapObject(this, STACK, completion, paramCompletion);
  }
  
  final void pushStack(Completion paramCompletion) {
    do {
    
    } while (!tryPushStack(paramCompletion));
  }
  
  static final class AltResult {
    final Throwable ex;
    
    AltResult(Throwable param1Throwable) {
      this.ex = param1Throwable;
    }
  }
  
  static final AltResult NIL = new AltResult(null);
  
  final boolean completeNull() {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, NIL);
  }
  
  final Object encodeValue(T paramT) {
    return (paramT == null) ? NIL : paramT;
  }
  
  final boolean completeValue(T paramT) {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, (paramT == null) ? NIL : paramT);
  }
  
  static AltResult encodeThrowable(Throwable paramThrowable) {
    return new AltResult((paramThrowable instanceof CompletionException) ? paramThrowable : new CompletionException(paramThrowable));
  }
  
  final boolean completeThrowable(Throwable paramThrowable) {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, 
        encodeThrowable(paramThrowable));
  }
  
  static Object encodeThrowable(Throwable paramThrowable, Object paramObject) {
    if (!(paramThrowable instanceof CompletionException)) {
      paramThrowable = new CompletionException(paramThrowable);
    } else if (paramObject instanceof AltResult && paramThrowable == ((AltResult)paramObject).ex) {
      return paramObject;
    } 
    return new AltResult(paramThrowable);
  }
  
  final boolean completeThrowable(Throwable paramThrowable, Object paramObject) {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, 
        encodeThrowable(paramThrowable, paramObject));
  }
  
  Object encodeOutcome(T paramT, Throwable paramThrowable) {
    return (paramThrowable == null) ? ((paramT == null) ? NIL : paramT) : encodeThrowable(paramThrowable);
  }
  
  static Object encodeRelay(Object paramObject) {
    Throwable throwable;
    return (paramObject instanceof AltResult && (throwable = ((AltResult)paramObject).ex) != null && !(throwable instanceof CompletionException)) ? new AltResult(new CompletionException(throwable)) : paramObject;
  }
  
  final boolean completeRelay(Object paramObject) {
    return UNSAFE.compareAndSwapObject(this, RESULT, null, 
        encodeRelay(paramObject));
  }
  
  private static <T> T reportGet(Object paramObject) throws InterruptedException, ExecutionException {
    if (paramObject == null)
      throw new InterruptedException(); 
    if (paramObject instanceof AltResult) {
      Throwable throwable1;
      if ((throwable1 = ((AltResult)paramObject).ex) == null)
        return null; 
      if (throwable1 instanceof CancellationException)
        throw (CancellationException)throwable1; 
      Throwable throwable2;
      if (throwable1 instanceof CompletionException && (
        throwable2 = throwable1.getCause()) != null)
        throwable1 = throwable2; 
      throw new ExecutionException(throwable1);
    } 
    return (T)paramObject;
  }
  
  private static <T> T reportJoin(Object paramObject) {
    if (paramObject instanceof AltResult) {
      Throwable throwable;
      if ((throwable = ((AltResult)paramObject).ex) == null)
        return null; 
      if (throwable instanceof CancellationException)
        throw (CancellationException)throwable; 
      if (throwable instanceof CompletionException)
        throw (CompletionException)throwable; 
      throw new CompletionException(throwable);
    } 
    return (T)paramObject;
  }
  
  private static final boolean useCommonPool = (ForkJoinPool.getCommonPoolParallelism() > 1);
  
  private static final Executor asyncPool = useCommonPool ? 
    ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
  
  static final int SYNC = 0;
  
  static final int ASYNC = 1;
  
  static final int NESTED = -1;
  
  private static final Unsafe UNSAFE;
  
  private static final long RESULT;
  
  private static final long STACK;
  
  private static final long NEXT;
  
  static Executor screenExecutor(Executor paramExecutor) {
    if (!useCommonPool && paramExecutor == ForkJoinPool.commonPool())
      return asyncPool; 
    if (paramExecutor == null)
      throw new NullPointerException(); 
    return paramExecutor;
  }
  
  public static interface AsynchronousCompletionTask {}
  
  static final class CompletableFuture {}
  
  static abstract class Completion extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionTask {
    volatile Completion next;
    
    public final void run() {
      tryFire(1);
    }
    
    public final boolean exec() {
      tryFire(1);
      return true;
    }
    
    public final Void getRawResult() {
      return null;
    }
    
    public final void setRawResult(Void param1Void) {}
    
    abstract CompletableFuture<?> tryFire(int param1Int);
    
    abstract boolean isLive();
  }
  
  static void lazySetNext(Completion paramCompletion1, Completion paramCompletion2) {
    UNSAFE.putOrderedObject(paramCompletion1, NEXT, paramCompletion2);
  }
  
  final void postComplete() {
    CompletableFuture completableFuture = this;
    Completion completion;
    while ((completion = completableFuture.stack) != null || (completableFuture != this && (completion = (completableFuture = this).stack) != null)) {
      Completion completion1;
      if (completableFuture.casStack(completion, completion1 = completion.next)) {
        if (completion1 != null) {
          if (completableFuture != this) {
            pushStack(completion);
            continue;
          } 
          completion.next = null;
        } 
        CompletableFuture<?> completableFuture1;
        completableFuture = ((completableFuture1 = completion.tryFire(-1)) == null) ? this : completableFuture1;
      } 
    } 
  }
  
  final void cleanStack() {
    for (Completion completion1 = null, completion2 = this.stack; completion2 != null; ) {
      Completion completion = completion2.next;
      if (completion2.isLive()) {
        completion1 = completion2;
        completion2 = completion;
        continue;
      } 
      if (completion1 == null) {
        casStack(completion2, completion);
        completion2 = this.stack;
        continue;
      } 
      completion1.next = completion;
      if (completion1.isLive()) {
        completion2 = completion;
        continue;
      } 
      completion1 = null;
      completion2 = this.stack;
    } 
  }
  
  static abstract class UniCompletion<T, V> extends Completion {
    Executor executor;
    
    CompletableFuture<V> dep;
    
    CompletableFuture<T> src;
    
    UniCompletion(Executor param1Executor, CompletableFuture<V> param1CompletableFuture, CompletableFuture<T> param1CompletableFuture1) {
      this.executor = param1Executor;
      this.dep = param1CompletableFuture;
      this.src = param1CompletableFuture1;
    }
    
    final boolean claim() {
      Executor executor = this.executor;
      if (compareAndSetForkJoinTaskTag((short)0, (short)1)) {
        if (executor == null)
          return true; 
        this.executor = null;
        executor.execute(this);
      } 
      return false;
    }
    
    final boolean isLive() {
      return (this.dep != null);
    }
  }
  
  final void push(UniCompletion<?, ?> paramUniCompletion) {
    if (paramUniCompletion != null)
      while (this.result == null && !tryPushStack(paramUniCompletion))
        lazySetNext(paramUniCompletion, null);  
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture, int paramInt) {
    if (paramCompletableFuture != null && paramCompletableFuture.stack != null)
      if (paramInt < 0 || paramCompletableFuture.result == null) {
        paramCompletableFuture.cleanStack();
      } else {
        paramCompletableFuture.postComplete();
      }  
    if (this.result != null && this.stack != null) {
      if (paramInt < 0)
        return this; 
      postComplete();
    } 
    return null;
  }
  
  final <S> boolean uniApply(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends T> paramFunction, UniApply<S, T> paramUniApply) {
    // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 106
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 106
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_0
    //   82: aload_2
    //   83: aload #6
    //   85: invokeinterface apply : (Ljava/lang/Object;)Ljava/lang/Object;
    //   90: invokevirtual completeValue : (Ljava/lang/Object;)Z
    //   93: pop
    //   94: goto -> 106
    //   97: astore #6
    //   99: aload_0
    //   100: aload #6
    //   102: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   105: pop
    //   106: iconst_1
    //   107: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #588	-> 0
    //   #589	-> 18
    //   #590	-> 20
    //   #591	-> 27
    //   #592	-> 35
    //   #593	-> 49
    //   #594	-> 58
    //   #596	-> 61
    //   #599	-> 64
    //   #600	-> 75
    //   #601	-> 77
    //   #602	-> 81
    //   #605	-> 94
    //   #603	-> 97
    //   #604	-> 99
    //   #607	-> 106
    // Exception table:
    //   from	to	target	type
    //   64	76	97	java/lang/Throwable
    //   77	94	97	java/lang/Throwable
  }
  
  private <V> CompletableFuture<V> uniApplyStage(Executor paramExecutor, Function<? super T, ? extends V> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniApply(this, paramFunction, null)) {
      UniApply<T, V> uniApply = new UniApply<>(paramExecutor, completableFuture, this, paramFunction);
      push(uniApply);
      uniApply.tryFire(0);
    } 
    return completableFuture;
  }
  
  final <S> boolean uniAccept(CompletableFuture<S> paramCompletableFuture, Consumer<? super S> paramConsumer, UniAccept<S> paramUniAccept) {
    // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 106
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 106
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_2
    //   82: aload #6
    //   84: invokeinterface accept : (Ljava/lang/Object;)V
    //   89: aload_0
    //   90: invokevirtual completeNull : ()Z
    //   93: pop
    //   94: goto -> 106
    //   97: astore #6
    //   99: aload_0
    //   100: aload #6
    //   102: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   105: pop
    //   106: iconst_1
    //   107: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #642	-> 0
    //   #643	-> 18
    //   #644	-> 20
    //   #645	-> 27
    //   #646	-> 35
    //   #647	-> 49
    //   #648	-> 58
    //   #650	-> 61
    //   #653	-> 64
    //   #654	-> 75
    //   #655	-> 77
    //   #656	-> 81
    //   #657	-> 89
    //   #660	-> 94
    //   #658	-> 97
    //   #659	-> 99
    //   #662	-> 106
    // Exception table:
    //   from	to	target	type
    //   64	76	97	java/lang/Throwable
    //   77	94	97	java/lang/Throwable
  }
  
  private CompletableFuture<Void> uniAcceptStage(Executor paramExecutor, Consumer<? super T> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniAccept(this, paramConsumer, null)) {
      UniAccept<T> uniAccept = new UniAccept<>(paramExecutor, completableFuture, this, paramConsumer);
      push(uniAccept);
      uniAccept.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniRun(CompletableFuture<?> paramCompletableFuture, Runnable paramRunnable, UniRun<?> paramUniRun) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramRunnable == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
        completeThrowable(throwable, object);
      } else {
        try {
          if (paramUniRun != null && !paramUniRun.claim())
            return false; 
          paramRunnable.run();
          completeNull();
        } catch (Throwable throwable1) {
          completeThrowable(throwable1);
        } 
      } 
    } 
    return true;
  }
  
  private CompletableFuture<Void> uniRunStage(Executor paramExecutor, Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniRun(this, paramRunnable, null)) {
      UniRun<?, ?> uniRun = new UniRun(paramExecutor, completableFuture, this, paramRunnable);
      push(uniRun);
      uniRun.tryFire(0);
    } 
    return completableFuture;
  }
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class UniWhenComplete<T> extends UniCompletion<T, T> {
    BiConsumer<? super T, ? super Throwable> fn;
    
    UniWhenComplete(Executor param1Executor, CompletableFuture<T> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, BiConsumer<? super T, ? super Throwable> param1BiConsumer) {
      super(param1Executor, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1BiConsumer;
    }
    
    final CompletableFuture<T> tryFire(int param1Int) {
      CompletableFuture<T> completableFuture1;
      CompletableFuture<T> completableFuture2;
      if ((completableFuture1 = this.dep) == null || 
        !completableFuture1.uniWhenComplete(completableFuture2 = this.src, this.fn, (param1Int > 0) ? null : this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  final boolean uniWhenComplete(CompletableFuture<T> paramCompletableFuture, BiConsumer<? super T, ? super Throwable> paramBiConsumer, UniWhenComplete<T> paramUniWhenComplete) {
    Throwable throwable = null;
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramBiConsumer == null)
      return false; 
    if (this.result == null) {
      try {
        Object object1;
        if (paramUniWhenComplete != null && !paramUniWhenComplete.claim())
          return false; 
        if (object instanceof AltResult) {
          throwable = ((AltResult)object).ex;
          object1 = null;
        } else {
          Object object2 = object;
          object1 = object2;
        } 
        paramBiConsumer.accept((T)object1, throwable);
        if (throwable == null) {
          internalComplete(object);
          return true;
        } 
      } catch (Throwable throwable1) {
        if (throwable == null)
          throwable = throwable1; 
      } 
      completeThrowable(throwable, object);
    } 
    return true;
  }
  
  private CompletableFuture<T> uniWhenCompleteStage(Executor paramExecutor, BiConsumer<? super T, ? super Throwable> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniWhenComplete(this, paramBiConsumer, null)) {
      UniWhenComplete<T> uniWhenComplete = new UniWhenComplete<>(paramExecutor, completableFuture, this, paramBiConsumer);
      push(uniWhenComplete);
      uniWhenComplete.tryFire(0);
    } 
    return completableFuture;
  }
  
  final <S> boolean uniHandle(CompletableFuture<S> paramCompletableFuture, BiFunction<? super S, Throwable, ? extends T> paramBiFunction, UniHandle<S, T> paramUniHandle) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramBiFunction == null)
      return false; 
    if (this.result == null)
      try {
        Object object1;
        Throwable throwable;
        if (paramUniHandle != null && !paramUniHandle.claim())
          return false; 
        if (object instanceof AltResult) {
          throwable = ((AltResult)object).ex;
          object1 = null;
        } else {
          throwable = null;
          Object object2 = object;
          object1 = object2;
        } 
        completeValue(paramBiFunction.apply((S)object1, throwable));
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <V> CompletableFuture<V> uniHandleStage(Executor paramExecutor, BiFunction<? super T, Throwable, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramExecutor != null || !completableFuture.uniHandle(this, paramBiFunction, null)) {
      UniHandle<T, V> uniHandle = new UniHandle<>(paramExecutor, completableFuture, this, paramBiFunction);
      push(uniHandle);
      uniHandle.tryFire(0);
    } 
    return completableFuture;
  }
  
  static final class CompletableFuture {}
  
  static final class UniExceptionally<T> extends UniCompletion<T, T> {
    Function<? super Throwable, ? extends T> fn;
    
    UniExceptionally(CompletableFuture<T> param1CompletableFuture1, CompletableFuture<T> param1CompletableFuture2, Function<? super Throwable, ? extends T> param1Function) {
      super((Executor)null, param1CompletableFuture1, param1CompletableFuture2);
      this.fn = param1Function;
    }
    
    final CompletableFuture<T> tryFire(int param1Int) {
      CompletableFuture<T> completableFuture1;
      CompletableFuture<T> completableFuture2;
      if ((completableFuture1 = this.dep) == null || !completableFuture1.uniExceptionally(completableFuture2 = this.src, this.fn, this))
        return null; 
      this.dep = null;
      this.src = null;
      this.fn = null;
      return completableFuture1.postFire(completableFuture2, param1Int);
    }
  }
  
  final boolean uniExceptionally(CompletableFuture<T> paramCompletableFuture, Function<? super Throwable, ? extends T> paramFunction, UniExceptionally<T> paramUniExceptionally) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null || paramFunction == null)
      return false; 
    if (this.result == null)
      try {
        Throwable throwable;
        if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
          if (paramUniExceptionally != null && !paramUniExceptionally.claim())
            return false; 
          completeValue(paramFunction.apply(throwable));
        } else {
          internalComplete(object);
        } 
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private CompletableFuture<T> uniExceptionallyStage(Function<Throwable, ? extends T> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    if (!completableFuture.uniExceptionally(this, paramFunction, null)) {
      UniExceptionally<T> uniExceptionally = new UniExceptionally<>(completableFuture, this, paramFunction);
      push(uniExceptionally);
      uniExceptionally.tryFire(0);
    } 
    return completableFuture;
  }
  
  final boolean uniRelay(CompletableFuture<T> paramCompletableFuture) {
    Object object;
    if (paramCompletableFuture == null || (object = paramCompletableFuture.result) == null)
      return false; 
    if (this.result == null)
      completeRelay(object); 
    return true;
  }
  
  final <S> boolean uniCompose(CompletableFuture<S> paramCompletableFuture, Function<? super S, ? extends CompletionStage<T>> paramFunction, UniCompose<S, T> paramUniCompose) {
    // Byte code:
    //   0: aload_1
    //   1: ifnull -> 18
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #4
    //   11: ifnull -> 18
    //   14: aload_2
    //   15: ifnonnull -> 20
    //   18: iconst_0
    //   19: ireturn
    //   20: aload_0
    //   21: getfield result : Ljava/lang/Object;
    //   24: ifnonnull -> 163
    //   27: aload #4
    //   29: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   32: ifeq -> 64
    //   35: aload #4
    //   37: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   40: getfield ex : Ljava/lang/Throwable;
    //   43: dup
    //   44: astore #5
    //   46: ifnull -> 61
    //   49: aload_0
    //   50: aload #5
    //   52: aload #4
    //   54: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 163
    //   61: aconst_null
    //   62: astore #4
    //   64: aload_3
    //   65: ifnull -> 77
    //   68: aload_3
    //   69: invokevirtual claim : ()Z
    //   72: ifne -> 77
    //   75: iconst_0
    //   76: ireturn
    //   77: aload #4
    //   79: astore #6
    //   81: aload_2
    //   82: aload #6
    //   84: invokeinterface apply : (Ljava/lang/Object;)Ljava/lang/Object;
    //   89: checkcast java/util/concurrent/CompletionStage
    //   92: invokeinterface toCompletableFuture : ()Ljava/util/concurrent/CompletableFuture;
    //   97: astore #7
    //   99: aload #7
    //   101: getfield result : Ljava/lang/Object;
    //   104: ifnull -> 116
    //   107: aload_0
    //   108: aload #7
    //   110: invokevirtual uniRelay : (Ljava/util/concurrent/CompletableFuture;)Z
    //   113: ifne -> 151
    //   116: new java/util/concurrent/CompletableFuture$UniRelay
    //   119: dup
    //   120: aload_0
    //   121: aload #7
    //   123: invokespecial <init> : (Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;)V
    //   126: astore #8
    //   128: aload #7
    //   130: aload #8
    //   132: invokevirtual push : (Ljava/util/concurrent/CompletableFuture$UniCompletion;)V
    //   135: aload #8
    //   137: iconst_0
    //   138: invokevirtual tryFire : (I)Ljava/util/concurrent/CompletableFuture;
    //   141: pop
    //   142: aload_0
    //   143: getfield result : Ljava/lang/Object;
    //   146: ifnonnull -> 151
    //   149: iconst_0
    //   150: ireturn
    //   151: goto -> 163
    //   154: astore #6
    //   156: aload_0
    //   157: aload #6
    //   159: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   162: pop
    //   163: iconst_1
    //   164: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #938	-> 0
    //   #939	-> 18
    //   #940	-> 20
    //   #941	-> 27
    //   #942	-> 35
    //   #943	-> 49
    //   #944	-> 58
    //   #946	-> 61
    //   #949	-> 64
    //   #950	-> 75
    //   #951	-> 77
    //   #952	-> 81
    //   #953	-> 99
    //   #954	-> 116
    //   #955	-> 128
    //   #956	-> 135
    //   #957	-> 142
    //   #958	-> 149
    //   #962	-> 151
    //   #960	-> 154
    //   #961	-> 156
    //   #964	-> 163
    // Exception table:
    //   from	to	target	type
    //   64	76	154	java/lang/Throwable
    //   77	150	154	java/lang/Throwable
  }
  
  private <V> CompletableFuture<V> uniComposeStage(Executor paramExecutor, Function<? super T, ? extends CompletionStage<V>> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    Object object;
    if (paramExecutor == null && (object = this.result) != null) {
      if (object instanceof AltResult) {
        Throwable throwable;
        if ((throwable = ((AltResult)object).ex) != null)
          return new CompletableFuture(encodeThrowable(throwable, object)); 
        object = null;
      } 
      try {
        Object object1 = object;
        return ((CompletionStage<V>)paramFunction.apply((T)object1)).toCompletableFuture();
      } catch (Throwable throwable) {
        return new CompletableFuture(encodeThrowable(throwable));
      } 
    } 
    CompletableFuture completableFuture = new CompletableFuture();
    UniCompose<T, V> uniCompose = new UniCompose<>(paramExecutor, completableFuture, this, paramFunction);
    push(uniCompose);
    uniCompose.tryFire(0);
    return completableFuture;
  }
  
  final void bipush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion) {
    if (paramBiCompletion != null) {
      Object object;
      while ((object = this.result) == null && !tryPushStack(paramBiCompletion))
        lazySetNext(paramBiCompletion, null); 
      if (paramCompletableFuture != null && paramCompletableFuture != this && paramCompletableFuture.result == null) {
        Completion completion = (Completion)((object != null) ? paramBiCompletion : new CoCompletion(paramBiCompletion));
        while (paramCompletableFuture.result == null && !paramCompletableFuture.tryPushStack(completion))
          lazySetNext(completion, null); 
      } 
    } 
  }
  
  final CompletableFuture<T> postFire(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, int paramInt) {
    if (paramCompletableFuture2 != null && paramCompletableFuture2.stack != null)
      if (paramInt < 0 || paramCompletableFuture2.result == null) {
        paramCompletableFuture2.cleanStack();
      } else {
        paramCompletableFuture2.postComplete();
      }  
    return postFire(paramCompletableFuture1, paramInt);
  }
  
  final <R, S> boolean biApply(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, BiFunction<? super R, ? super S, ? extends T> paramBiFunction, BiApply<R, S, T> paramBiApply) {
    // Byte code:
    //   0: aload_1
    //   1: ifnull -> 32
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #5
    //   11: ifnull -> 32
    //   14: aload_2
    //   15: ifnull -> 32
    //   18: aload_2
    //   19: getfield result : Ljava/lang/Object;
    //   22: dup
    //   23: astore #6
    //   25: ifnull -> 32
    //   28: aload_3
    //   29: ifnonnull -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: getfield result : Ljava/lang/Object;
    //   38: ifnonnull -> 165
    //   41: aload #5
    //   43: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   46: ifeq -> 78
    //   49: aload #5
    //   51: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   54: getfield ex : Ljava/lang/Throwable;
    //   57: dup
    //   58: astore #7
    //   60: ifnull -> 75
    //   63: aload_0
    //   64: aload #7
    //   66: aload #5
    //   68: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   71: pop
    //   72: goto -> 165
    //   75: aconst_null
    //   76: astore #5
    //   78: aload #6
    //   80: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   83: ifeq -> 115
    //   86: aload #6
    //   88: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   91: getfield ex : Ljava/lang/Throwable;
    //   94: dup
    //   95: astore #7
    //   97: ifnull -> 112
    //   100: aload_0
    //   101: aload #7
    //   103: aload #6
    //   105: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   108: pop
    //   109: goto -> 165
    //   112: aconst_null
    //   113: astore #6
    //   115: aload #4
    //   117: ifnull -> 130
    //   120: aload #4
    //   122: invokevirtual claim : ()Z
    //   125: ifne -> 130
    //   128: iconst_0
    //   129: ireturn
    //   130: aload #5
    //   132: astore #8
    //   134: aload #6
    //   136: astore #9
    //   138: aload_0
    //   139: aload_3
    //   140: aload #8
    //   142: aload #9
    //   144: invokeinterface apply : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   149: invokevirtual completeValue : (Ljava/lang/Object;)Z
    //   152: pop
    //   153: goto -> 165
    //   156: astore #8
    //   158: aload_0
    //   159: aload #8
    //   161: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   164: pop
    //   165: iconst_1
    //   166: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1074	-> 0
    //   #1076	-> 32
    //   #1077	-> 34
    //   #1078	-> 41
    //   #1079	-> 49
    //   #1080	-> 63
    //   #1081	-> 72
    //   #1083	-> 75
    //   #1085	-> 78
    //   #1086	-> 86
    //   #1087	-> 100
    //   #1088	-> 109
    //   #1090	-> 112
    //   #1093	-> 115
    //   #1094	-> 128
    //   #1095	-> 130
    //   #1096	-> 134
    //   #1097	-> 138
    //   #1100	-> 153
    //   #1098	-> 156
    //   #1099	-> 158
    //   #1102	-> 165
    // Exception table:
    //   from	to	target	type
    //   115	129	156	java/lang/Throwable
    //   130	153	156	java/lang/Throwable
  }
  
  private <U, V> CompletableFuture<V> biApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) {
    CompletableFuture<U> completableFuture;
    if (paramBiFunction == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.biApply(this, completableFuture, paramBiFunction, null)) {
      BiApply<T, U, V> biApply = new BiApply<>(paramExecutor, completableFuture1, this, completableFuture, paramBiFunction);
      bipush(completableFuture, biApply);
      biApply.tryFire(0);
    } 
    return completableFuture1;
  }
  
  final <R, S> boolean biAccept(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, BiConsumer<? super R, ? super S> paramBiConsumer, BiAccept<R, S> paramBiAccept) {
    // Byte code:
    //   0: aload_1
    //   1: ifnull -> 32
    //   4: aload_1
    //   5: getfield result : Ljava/lang/Object;
    //   8: dup
    //   9: astore #5
    //   11: ifnull -> 32
    //   14: aload_2
    //   15: ifnull -> 32
    //   18: aload_2
    //   19: getfield result : Ljava/lang/Object;
    //   22: dup
    //   23: astore #6
    //   25: ifnull -> 32
    //   28: aload_3
    //   29: ifnonnull -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: getfield result : Ljava/lang/Object;
    //   38: ifnonnull -> 165
    //   41: aload #5
    //   43: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   46: ifeq -> 78
    //   49: aload #5
    //   51: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   54: getfield ex : Ljava/lang/Throwable;
    //   57: dup
    //   58: astore #7
    //   60: ifnull -> 75
    //   63: aload_0
    //   64: aload #7
    //   66: aload #5
    //   68: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   71: pop
    //   72: goto -> 165
    //   75: aconst_null
    //   76: astore #5
    //   78: aload #6
    //   80: instanceof java/util/concurrent/CompletableFuture$AltResult
    //   83: ifeq -> 115
    //   86: aload #6
    //   88: checkcast java/util/concurrent/CompletableFuture$AltResult
    //   91: getfield ex : Ljava/lang/Throwable;
    //   94: dup
    //   95: astore #7
    //   97: ifnull -> 112
    //   100: aload_0
    //   101: aload #7
    //   103: aload #6
    //   105: invokevirtual completeThrowable : (Ljava/lang/Throwable;Ljava/lang/Object;)Z
    //   108: pop
    //   109: goto -> 165
    //   112: aconst_null
    //   113: astore #6
    //   115: aload #4
    //   117: ifnull -> 130
    //   120: aload #4
    //   122: invokevirtual claim : ()Z
    //   125: ifne -> 130
    //   128: iconst_0
    //   129: ireturn
    //   130: aload #5
    //   132: astore #8
    //   134: aload #6
    //   136: astore #9
    //   138: aload_3
    //   139: aload #8
    //   141: aload #9
    //   143: invokeinterface accept : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   148: aload_0
    //   149: invokevirtual completeNull : ()Z
    //   152: pop
    //   153: goto -> 165
    //   156: astore #8
    //   158: aload_0
    //   159: aload #8
    //   161: invokevirtual completeThrowable : (Ljava/lang/Throwable;)Z
    //   164: pop
    //   165: iconst_1
    //   166: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1145	-> 0
    //   #1147	-> 32
    //   #1148	-> 34
    //   #1149	-> 41
    //   #1150	-> 49
    //   #1151	-> 63
    //   #1152	-> 72
    //   #1154	-> 75
    //   #1156	-> 78
    //   #1157	-> 86
    //   #1158	-> 100
    //   #1159	-> 109
    //   #1161	-> 112
    //   #1164	-> 115
    //   #1165	-> 128
    //   #1166	-> 130
    //   #1167	-> 134
    //   #1168	-> 138
    //   #1169	-> 148
    //   #1172	-> 153
    //   #1170	-> 156
    //   #1171	-> 158
    //   #1174	-> 165
    // Exception table:
    //   from	to	target	type
    //   115	129	156	java/lang/Throwable
    //   130	153	156	java/lang/Throwable
  }
  
  private <U> CompletableFuture<Void> biAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) {
    CompletableFuture<U> completableFuture;
    if (paramBiConsumer == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.biAccept(this, completableFuture, paramBiConsumer, null)) {
      BiAccept<T, U> biAccept = new BiAccept<>(paramExecutor, completableFuture1, this, completableFuture, paramBiConsumer);
      bipush(completableFuture, biAccept);
      biAccept.tryFire(0);
    } 
    return completableFuture1;
  }
  
  final boolean biRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, BiRun<?, ?> paramBiRun) {
    Object object1;
    Object object2;
    if (paramCompletableFuture1 == null || (object1 = paramCompletableFuture1.result) == null || paramCompletableFuture2 == null || (object2 = paramCompletableFuture2.result) == null || paramRunnable == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object1 instanceof AltResult && (throwable = ((AltResult)object1).ex) != null) {
        completeThrowable(throwable, object1);
      } else if (object2 instanceof AltResult && (throwable = ((AltResult)object2).ex) != null) {
        completeThrowable(throwable, object2);
      } else {
        try {
          if (paramBiRun != null && !paramBiRun.claim())
            return false; 
          paramRunnable.run();
          completeNull();
        } catch (Throwable throwable1) {
          completeThrowable(throwable1);
        } 
      } 
    } 
    return true;
  }
  
  private CompletableFuture<Void> biRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    CompletableFuture<?> completableFuture;
    if (paramRunnable == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.biRun(this, completableFuture, paramRunnable, null)) {
      BiRun<Object, Object> biRun = new BiRun<>(paramExecutor, completableFuture1, this, completableFuture, paramRunnable);
      bipush(completableFuture, biRun);
      biRun.tryFire(0);
    } 
    return completableFuture1;
  }
  
  boolean biRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2) {
    Object object1;
    Object object2;
    if (paramCompletableFuture1 == null || (object1 = paramCompletableFuture1.result) == null || paramCompletableFuture2 == null || (object2 = paramCompletableFuture2.result) == null)
      return false; 
    if (this.result == null) {
      Throwable throwable;
      if (object1 instanceof AltResult && (throwable = ((AltResult)object1).ex) != null) {
        completeThrowable(throwable, object1);
      } else if (object2 instanceof AltResult && (throwable = ((AltResult)object2).ex) != null) {
        completeThrowable(throwable, object2);
      } else {
        completeNull();
      } 
    } 
    return true;
  }
  
  static CompletableFuture<Void> andTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2) {
    // Byte code:
    //   0: new java/util/concurrent/CompletableFuture
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore_3
    //   8: iload_1
    //   9: iload_2
    //   10: if_icmple -> 23
    //   13: aload_3
    //   14: getstatic java/util/concurrent/CompletableFuture.NIL : Ljava/util/concurrent/CompletableFuture$AltResult;
    //   17: putfield result : Ljava/lang/Object;
    //   20: goto -> 143
    //   23: iload_1
    //   24: iload_2
    //   25: iadd
    //   26: iconst_1
    //   27: iushr
    //   28: istore #6
    //   30: iload_1
    //   31: iload #6
    //   33: if_icmpne -> 42
    //   36: aload_0
    //   37: iload_1
    //   38: aaload
    //   39: goto -> 49
    //   42: aload_0
    //   43: iload_1
    //   44: iload #6
    //   46: invokestatic andTree : ([Ljava/util/concurrent/CompletableFuture;II)Ljava/util/concurrent/CompletableFuture;
    //   49: dup
    //   50: astore #4
    //   52: ifnull -> 94
    //   55: iload_1
    //   56: iload_2
    //   57: if_icmpne -> 65
    //   60: aload #4
    //   62: goto -> 88
    //   65: iload_2
    //   66: iload #6
    //   68: iconst_1
    //   69: iadd
    //   70: if_icmpne -> 79
    //   73: aload_0
    //   74: iload_2
    //   75: aaload
    //   76: goto -> 88
    //   79: aload_0
    //   80: iload #6
    //   82: iconst_1
    //   83: iadd
    //   84: iload_2
    //   85: invokestatic andTree : ([Ljava/util/concurrent/CompletableFuture;II)Ljava/util/concurrent/CompletableFuture;
    //   88: dup
    //   89: astore #5
    //   91: ifnonnull -> 102
    //   94: new java/lang/NullPointerException
    //   97: dup
    //   98: invokespecial <init> : ()V
    //   101: athrow
    //   102: aload_3
    //   103: aload #4
    //   105: aload #5
    //   107: invokevirtual biRelay : (Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;)Z
    //   110: ifne -> 143
    //   113: new java/util/concurrent/CompletableFuture$BiRelay
    //   116: dup
    //   117: aload_3
    //   118: aload #4
    //   120: aload #5
    //   122: invokespecial <init> : (Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;)V
    //   125: astore #7
    //   127: aload #4
    //   129: aload #5
    //   131: aload #7
    //   133: invokevirtual bipush : (Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture$BiCompletion;)V
    //   136: aload #7
    //   138: iconst_0
    //   139: invokevirtual tryFire : (I)Ljava/util/concurrent/CompletableFuture;
    //   142: pop
    //   143: aload_3
    //   144: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1288	-> 0
    //   #1289	-> 8
    //   #1290	-> 13
    //   #1293	-> 23
    //   #1294	-> 30
    //   #1295	-> 46
    //   #1297	-> 85
    //   #1298	-> 94
    //   #1299	-> 102
    //   #1300	-> 113
    //   #1301	-> 127
    //   #1302	-> 136
    //   #1305	-> 143
  }
  
  final void orpush(CompletableFuture<?> paramCompletableFuture, BiCompletion<?, ?, ?> paramBiCompletion) {
    if (paramBiCompletion != null)
      while ((paramCompletableFuture == null || paramCompletableFuture.result == null) && this.result == null) {
        if (tryPushStack(paramBiCompletion)) {
          if (paramCompletableFuture != null && paramCompletableFuture != this && paramCompletableFuture.result == null) {
            CoCompletion coCompletion = new CoCompletion(paramBiCompletion);
            while (this.result == null && paramCompletableFuture.result == null && 
              !paramCompletableFuture.tryPushStack(coCompletion))
              lazySetNext(coCompletion, null); 
          } 
          break;
        } 
        lazySetNext(paramBiCompletion, null);
      }  
  }
  
  final <R, S extends R> boolean orApply(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, Function<? super R, ? extends T> paramFunction, OrApply<R, S, T> paramOrApply) {
    Object object;
    if (paramCompletableFuture == null || paramCompletableFuture1 == null || ((object = paramCompletableFuture.result) == null && (object = paramCompletableFuture1.result) == null) || paramFunction == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrApply != null && !paramOrApply.claim())
          return false; 
        if (object instanceof AltResult) {
          Throwable throwable;
          if ((throwable = ((AltResult)object).ex) != null) {
            completeThrowable(throwable, object);
          } else {
            object = null;
            Object object2 = object;
            completeValue(paramFunction.apply((R)object2));
          } 
          return true;
        } 
        Object object1 = object;
        completeValue(paramFunction.apply((R)object1));
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <U extends T, V> CompletableFuture<V> orApplyStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Function<? super T, ? extends V> paramFunction) {
    CompletableFuture<U> completableFuture;
    if (paramFunction == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.orApply(this, completableFuture, paramFunction, null)) {
      OrApply<T, U, V> orApply = new OrApply<>(paramExecutor, completableFuture1, this, completableFuture, paramFunction);
      orpush(completableFuture, orApply);
      orApply.tryFire(0);
    } 
    return completableFuture1;
  }
  
  final <R, S extends R> boolean orAccept(CompletableFuture<R> paramCompletableFuture, CompletableFuture<S> paramCompletableFuture1, Consumer<? super R> paramConsumer, OrAccept<R, S> paramOrAccept) {
    Object object;
    if (paramCompletableFuture == null || paramCompletableFuture1 == null || ((object = paramCompletableFuture.result) == null && (object = paramCompletableFuture1.result) == null) || paramConsumer == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrAccept != null && !paramOrAccept.claim())
          return false; 
        if (object instanceof AltResult) {
          Throwable throwable;
          if ((throwable = ((AltResult)object).ex) != null) {
            completeThrowable(throwable, object);
          } else {
            object = null;
            Object object2 = object;
            paramConsumer.accept((R)object2);
            completeNull();
          } 
          return true;
        } 
        Object object1 = object;
        paramConsumer.accept((R)object1);
        completeNull();
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private <U extends T> CompletableFuture<Void> orAcceptStage(Executor paramExecutor, CompletionStage<U> paramCompletionStage, Consumer<? super T> paramConsumer) {
    CompletableFuture<U> completableFuture;
    if (paramConsumer == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.orAccept(this, completableFuture, paramConsumer, null)) {
      OrAccept<T, U> orAccept = new OrAccept<>(paramExecutor, completableFuture1, this, completableFuture, paramConsumer);
      orpush(completableFuture, orAccept);
      orAccept.tryFire(0);
    } 
    return completableFuture1;
  }
  
  final boolean orRun(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2, Runnable paramRunnable, OrRun<?, ?> paramOrRun) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null) || paramRunnable == null)
      return false; 
    if (this.result == null)
      try {
        if (paramOrRun != null && !paramOrRun.claim())
          return false; 
        Throwable throwable;
        if (object instanceof AltResult && (throwable = ((AltResult)object).ex) != null) {
          completeThrowable(throwable, object);
        } else {
          paramRunnable.run();
          completeNull();
        } 
      } catch (Throwable throwable) {
        completeThrowable(throwable);
      }  
    return true;
  }
  
  private CompletableFuture<Void> orRunStage(Executor paramExecutor, CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    CompletableFuture<?> completableFuture;
    if (paramRunnable == null || (completableFuture = paramCompletionStage.toCompletableFuture()) == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture1 = new CompletableFuture();
    if (paramExecutor != null || !completableFuture1.orRun(this, completableFuture, paramRunnable, null)) {
      OrRun<Object, Object> orRun = new OrRun<>(paramExecutor, completableFuture1, this, completableFuture, paramRunnable);
      orpush(completableFuture, orRun);
      orRun.tryFire(0);
    } 
    return completableFuture1;
  }
  
  final boolean orRelay(CompletableFuture<?> paramCompletableFuture1, CompletableFuture<?> paramCompletableFuture2) {
    Object object;
    if (paramCompletableFuture1 == null || paramCompletableFuture2 == null || ((object = paramCompletableFuture1.result) == null && (object = paramCompletableFuture2.result) == null))
      return false; 
    if (this.result == null)
      completeRelay(object); 
    return true;
  }
  
  static CompletableFuture<Object> orTree(CompletableFuture<?>[] paramArrayOfCompletableFuture, int paramInt1, int paramInt2) {
    CompletableFuture completableFuture = new CompletableFuture();
    if (paramInt1 <= paramInt2) {
      CompletableFuture<?> completableFuture2;
      int i = paramInt1 + paramInt2 >>> 1;
      CompletableFuture<?> completableFuture1;
      if ((completableFuture1 = (CompletableFuture<?>)((paramInt1 == i) ? paramArrayOfCompletableFuture[paramInt1] : orTree(paramArrayOfCompletableFuture, paramInt1, i))) != null) {
        if ((completableFuture2 = (CompletableFuture<?>)((paramInt1 == paramInt2) ? completableFuture1 : ((paramInt2 == i + 1) ? paramArrayOfCompletableFuture[paramInt2] : orTree(paramArrayOfCompletableFuture, i + 1, paramInt2)))) == null)
          throw new NullPointerException(); 
      } else {
        throw new NullPointerException();
      } 
      if (!completableFuture.orRelay(completableFuture1, completableFuture2)) {
        OrRelay orRelay = new OrRelay(completableFuture, completableFuture1, completableFuture2);
        completableFuture1.orpush(completableFuture2, (BiCompletion<?, ?, ?>)orRelay);
        orRelay.tryFire(0);
      } 
    } 
    return completableFuture;
  }
  
  static <U> CompletableFuture<U> asyncSupplyStage(Executor paramExecutor, Supplier<U> paramSupplier) {
    if (paramSupplier == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncSupply<>(completableFuture, paramSupplier));
    return completableFuture;
  }
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static abstract class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class CompletableFuture {}
  
  static final class AsyncRun extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionTask {
    CompletableFuture<Void> dep;
    
    Runnable fn;
    
    AsyncRun(CompletableFuture<Void> param1CompletableFuture, Runnable param1Runnable) {
      this.dep = param1CompletableFuture;
      this.fn = param1Runnable;
    }
    
    public final Void getRawResult() {
      return null;
    }
    
    public final void setRawResult(Void param1Void) {}
    
    public final boolean exec() {
      run();
      return true;
    }
    
    public void run() {
      CompletableFuture<Void> completableFuture;
      Runnable runnable;
      if ((completableFuture = this.dep) != null && (runnable = this.fn) != null) {
        this.dep = null;
        this.fn = null;
        if (completableFuture.result == null)
          try {
            runnable.run();
            completableFuture.completeNull();
          } catch (Throwable throwable) {
            completableFuture.completeThrowable(throwable);
          }  
        completableFuture.postComplete();
      } 
    }
  }
  
  static CompletableFuture<Void> asyncRunStage(Executor paramExecutor, Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    CompletableFuture completableFuture = new CompletableFuture();
    paramExecutor.execute(new AsyncRun(completableFuture, paramRunnable));
    return completableFuture;
  }
  
  static final class Signaller extends Completion implements ForkJoinPool.ManagedBlocker {
    long nanos;
    
    final long deadline;
    
    volatile int interruptControl;
    
    volatile Thread thread;
    
    Signaller(boolean param1Boolean, long param1Long1, long param1Long2) {
      this.thread = Thread.currentThread();
      this.interruptControl = param1Boolean ? 1 : 0;
      this.nanos = param1Long1;
      this.deadline = param1Long2;
    }
    
    final CompletableFuture<?> tryFire(int param1Int) {
      Thread thread;
      if ((thread = this.thread) != null) {
        this.thread = null;
        LockSupport.unpark(thread);
      } 
      return null;
    }
    
    public boolean isReleasable() {
      if (this.thread == null)
        return true; 
      if (Thread.interrupted()) {
        int i = this.interruptControl;
        this.interruptControl = -1;
        if (i > 0)
          return true; 
      } 
      if (this.deadline != 0L && (this.nanos <= 0L || (this
        .nanos = this.deadline - System.nanoTime()) <= 0L)) {
        this.thread = null;
        return true;
      } 
      return false;
    }
    
    public boolean block() {
      if (isReleasable())
        return true; 
      if (this.deadline == 0L) {
        LockSupport.park(this);
      } else if (this.nanos > 0L) {
        LockSupport.parkNanos(this, this.nanos);
      } 
      return isReleasable();
    }
    
    final boolean isLive() {
      return (this.thread != null);
    }
  }
  
  private Object waitingGet(boolean paramBoolean) {
    Signaller signaller = null;
    boolean bool = false;
    byte b = -1;
    Object object;
    while ((object = this.result) == null) {
      if (b < 0) {
        b = (Runtime.getRuntime().availableProcessors() > 1) ? 256 : 0;
        continue;
      } 
      if (b > 0) {
        if (ThreadLocalRandom.nextSecondarySeed() >= 0)
          b--; 
        continue;
      } 
      if (signaller == null) {
        signaller = new Signaller(paramBoolean, 0L, 0L);
        continue;
      } 
      if (!bool) {
        bool = tryPushStack(signaller);
        continue;
      } 
      if (paramBoolean && signaller.interruptControl < 0) {
        signaller.thread = null;
        cleanStack();
        return null;
      } 
      if (signaller.thread != null && this.result == null)
        try {
          ForkJoinPool.managedBlock(signaller);
        } catch (InterruptedException interruptedException) {
          signaller.interruptControl = -1;
        }  
    } 
    if (signaller != null) {
      signaller.thread = null;
      if (signaller.interruptControl < 0)
        if (paramBoolean) {
          object = null;
        } else {
          Thread.currentThread().interrupt();
        }  
    } 
    postComplete();
    return object;
  }
  
  private Object timedGet(long paramLong) throws TimeoutException {
    if (Thread.interrupted())
      return null; 
    if (paramLong <= 0L)
      throw new TimeoutException(); 
    long l = System.nanoTime() + paramLong;
    Signaller signaller = new Signaller(true, paramLong, (l == 0L) ? 1L : l);
    boolean bool = false;
    Object object;
    while ((object = this.result) == null) {
      if (!bool) {
        bool = tryPushStack(signaller);
        continue;
      } 
      if (signaller.interruptControl < 0 || signaller.nanos <= 0L) {
        signaller.thread = null;
        cleanStack();
        if (signaller.interruptControl < 0)
          return null; 
        throw new TimeoutException();
      } 
      if (signaller.thread != null && this.result == null)
        try {
          ForkJoinPool.managedBlock(signaller);
        } catch (InterruptedException interruptedException) {
          signaller.interruptControl = -1;
        }  
    } 
    if (signaller.interruptControl < 0)
      object = null; 
    signaller.thread = null;
    postComplete();
    return object;
  }
  
  public CompletableFuture() {}
  
  private CompletableFuture(Object paramObject) {
    this.result = paramObject;
  }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier) {
    return asyncSupplyStage(asyncPool, paramSupplier);
  }
  
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> paramSupplier, Executor paramExecutor) {
    return asyncSupplyStage(screenExecutor(paramExecutor), paramSupplier);
  }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable) {
    return asyncRunStage(asyncPool, paramRunnable);
  }
  
  public static CompletableFuture<Void> runAsync(Runnable paramRunnable, Executor paramExecutor) {
    return asyncRunStage(screenExecutor(paramExecutor), paramRunnable);
  }
  
  public static <U> CompletableFuture<U> completedFuture(U paramU) {
    return new CompletableFuture((paramU == null) ? NIL : paramU);
  }
  
  public boolean isDone() {
    return (this.result != null);
  }
  
  public T get() throws InterruptedException, ExecutionException {
    Object object;
    return reportGet(((object = this.result) == null) ? waitingGet(true) : object);
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    long l = paramTimeUnit.toNanos(paramLong);
    Object object;
    return reportGet(((object = this.result) == null) ? timedGet(l) : object);
  }
  
  public T join() {
    Object object;
    return reportJoin(((object = this.result) == null) ? waitingGet(false) : object);
  }
  
  public T getNow(T paramT) {
    Object object;
    return ((object = this.result) == null) ? paramT : reportJoin(object);
  }
  
  public boolean complete(T paramT) {
    boolean bool = completeValue(paramT);
    postComplete();
    return bool;
  }
  
  public boolean completeExceptionally(Throwable paramThrowable) {
    if (paramThrowable == null)
      throw new NullPointerException(); 
    boolean bool = internalComplete(new AltResult(paramThrowable));
    postComplete();
    return bool;
  }
  
  public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> paramFunction) {
    return uniApplyStage(null, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction) {
    return uniApplyStage(asyncPool, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> paramFunction, Executor paramExecutor) {
    return uniApplyStage(screenExecutor(paramExecutor), paramFunction);
  }
  
  public CompletableFuture<Void> thenAccept(Consumer<? super T> paramConsumer) {
    return uniAcceptStage(null, paramConsumer);
  }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer) {
    return uniAcceptStage(asyncPool, paramConsumer);
  }
  
  public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> paramConsumer, Executor paramExecutor) {
    return uniAcceptStage(screenExecutor(paramExecutor), paramConsumer);
  }
  
  public CompletableFuture<Void> thenRun(Runnable paramRunnable) {
    return uniRunStage(null, paramRunnable);
  }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable) {
    return uniRunStage(asyncPool, paramRunnable);
  }
  
  public CompletableFuture<Void> thenRunAsync(Runnable paramRunnable, Executor paramExecutor) {
    return uniRunStage(screenExecutor(paramExecutor), paramRunnable);
  }
  
  public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) {
    return biApplyStage(null, paramCompletionStage, paramBiFunction);
  }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction) {
    return biApplyStage(asyncPool, paramCompletionStage, paramBiFunction);
  }
  
  public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> paramCompletionStage, BiFunction<? super T, ? super U, ? extends V> paramBiFunction, Executor paramExecutor) {
    return biApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiFunction);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) {
    return biAcceptStage(null, paramCompletionStage, paramBiConsumer);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer) {
    return biAcceptStage(asyncPool, paramCompletionStage, paramBiConsumer);
  }
  
  public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> paramCompletionStage, BiConsumer<? super T, ? super U> paramBiConsumer, Executor paramExecutor) {
    return biAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramBiConsumer);
  }
  
  public CompletableFuture<Void> runAfterBoth(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    return biRunStage(null, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    return biRunStage(asyncPool, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor) {
    return biRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable);
  }
  
  public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction) {
    return (CompletableFuture)orApplyStage(null, paramCompletionStage, paramFunction);
  }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction) {
    return (CompletableFuture)orApplyStage(asyncPool, paramCompletionStage, paramFunction);
  }
  
  public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> paramCompletionStage, Function<? super T, U> paramFunction, Executor paramExecutor) {
    return (CompletableFuture)orApplyStage(screenExecutor(paramExecutor), paramCompletionStage, paramFunction);
  }
  
  public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer) {
    return orAcceptStage(null, paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer) {
    return orAcceptStage(asyncPool, paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> paramCompletionStage, Consumer<? super T> paramConsumer, Executor paramExecutor) {
    return orAcceptStage(screenExecutor(paramExecutor), paramCompletionStage, paramConsumer);
  }
  
  public CompletableFuture<Void> runAfterEither(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    return orRunStage(null, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable) {
    return orRunStage(asyncPool, paramCompletionStage, paramRunnable);
  }
  
  public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> paramCompletionStage, Runnable paramRunnable, Executor paramExecutor) {
    return orRunStage(screenExecutor(paramExecutor), paramCompletionStage, paramRunnable);
  }
  
  public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> paramFunction) {
    return uniComposeStage(null, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction) {
    return uniComposeStage(asyncPool, paramFunction);
  }
  
  public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> paramFunction, Executor paramExecutor) {
    return uniComposeStage(screenExecutor(paramExecutor), paramFunction);
  }
  
  public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer) {
    return uniWhenCompleteStage(null, paramBiConsumer);
  }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer) {
    return uniWhenCompleteStage(asyncPool, paramBiConsumer);
  }
  
  public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> paramBiConsumer, Executor paramExecutor) {
    return uniWhenCompleteStage(screenExecutor(paramExecutor), paramBiConsumer);
  }
  
  public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> paramBiFunction) {
    return uniHandleStage(null, paramBiFunction);
  }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction) {
    return uniHandleStage(asyncPool, paramBiFunction);
  }
  
  public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> paramBiFunction, Executor paramExecutor) {
    return uniHandleStage(screenExecutor(paramExecutor), paramBiFunction);
  }
  
  public CompletableFuture<T> toCompletableFuture() {
    return this;
  }
  
  public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> paramFunction) {
    return uniExceptionallyStage(paramFunction);
  }
  
  public static CompletableFuture<Void> allOf(CompletableFuture<?>... paramVarArgs) {
    return andTree(paramVarArgs, 0, paramVarArgs.length - 1);
  }
  
  public static CompletableFuture<Object> anyOf(CompletableFuture<?>... paramVarArgs) {
    return orTree(paramVarArgs, 0, paramVarArgs.length - 1);
  }
  
  public boolean cancel(boolean paramBoolean) {
    boolean bool = (this.result == null && internalComplete(new AltResult(new CancellationException()))) ? true : false;
    postComplete();
    return (bool || isCancelled());
  }
  
  public boolean isCancelled() {
    Object object;
    return (object = this.result instanceof AltResult && ((AltResult)object).ex instanceof CancellationException);
  }
  
  public boolean isCompletedExceptionally() {
    Object object;
    return (object = this.result instanceof AltResult && object != NIL);
  }
  
  public void obtrudeValue(T paramT) {
    this.result = (paramT == null) ? NIL : paramT;
    postComplete();
  }
  
  public void obtrudeException(Throwable paramThrowable) {
    if (paramThrowable == null)
      throw new NullPointerException(); 
    this.result = new AltResult(paramThrowable);
    postComplete();
  }
  
  public int getNumberOfDependents() {
    byte b = 0;
    for (Completion completion = this.stack; completion != null; completion = completion.next)
      b++; 
    return b;
  }
  
  public String toString() {
    Object object = this.result;
    int i;
    return super.toString() + ((object == null) ? (
      
      ((i = getNumberOfDependents()) == 0) ? "[Not completed]" : ("[Not completed, " + i + " dependents]")) : ((object instanceof AltResult && ((AltResult)object).ex != null) ? "[Completed exceptionally]" : "[Completed normally]"));
  }
  
  static {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      Class<CompletableFuture> clazz = CompletableFuture.class;
      RESULT = unsafe.objectFieldOffset(clazz.getDeclaredField("result"));
      STACK = unsafe.objectFieldOffset(clazz.getDeclaredField("stack"));
      NEXT = unsafe.objectFieldOffset(Completion.class.getDeclaredField("next"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}
