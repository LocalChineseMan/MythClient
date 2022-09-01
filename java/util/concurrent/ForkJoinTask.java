package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public abstract class ForkJoinTask<V> implements Future<V>, Serializable {
  volatile int status;
  
  static final int DONE_MASK = -268435456;
  
  static final int NORMAL = -268435456;
  
  static final int CANCELLED = -1073741824;
  
  static final int EXCEPTIONAL = -2147483648;
  
  static final int SIGNAL = 65536;
  
  static final int SMASK = 65535;
  
  private static final ExceptionNode[] exceptionTable;
  
  private int setCompletion(int paramInt) {
    // Byte code:
    //   0: aload_0
    //   1: getfield status : I
    //   4: dup
    //   5: istore_2
    //   6: ifge -> 11
    //   9: iload_2
    //   10: ireturn
    //   11: getstatic java/util/concurrent/ForkJoinTask.U : Lsun/misc/Unsafe;
    //   14: aload_0
    //   15: getstatic java/util/concurrent/ForkJoinTask.STATUS : J
    //   18: iload_2
    //   19: iload_2
    //   20: iload_1
    //   21: ior
    //   22: invokevirtual compareAndSwapInt : (Ljava/lang/Object;JII)Z
    //   25: ifeq -> 0
    //   28: iload_2
    //   29: bipush #16
    //   31: iushr
    //   32: ifeq -> 55
    //   35: aload_0
    //   36: dup
    //   37: astore_3
    //   38: monitorenter
    //   39: aload_0
    //   40: invokevirtual notifyAll : ()V
    //   43: aload_3
    //   44: monitorexit
    //   45: goto -> 55
    //   48: astore #4
    //   50: aload_3
    //   51: monitorexit
    //   52: aload #4
    //   54: athrow
    //   55: iload_1
    //   56: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #268	-> 0
    //   #269	-> 9
    //   #270	-> 11
    //   #271	-> 28
    //   #272	-> 35
    //   #273	-> 55
    // Exception table:
    //   from	to	target	type
    //   39	45	48	finally
    //   48	52	48	finally
  }
  
  final int doExec() {
    int i;
    if ((i = this.status) >= 0) {
      boolean bool;
      try {
        bool = exec();
      } catch (Throwable throwable) {
        return setExceptionalCompletion(throwable);
      } 
      if (bool)
        i = setCompletion(-268435456); 
    } 
    return i;
  }
  
  final void internalWait(long paramLong) {
    int i;
    if ((i = this.status) >= 0 && U
      .compareAndSwapInt(this, STATUS, i, i | 0x10000))
      synchronized (this) {
        if (this.status >= 0) {
          try {
            wait(paramLong);
          } catch (InterruptedException interruptedException) {}
        } else {
          notifyAll();
        } 
      }  
  }
  
  private int externalAwaitDone() {
    int i = (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : (ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : 0);
    if (i && (i = this.status) >= 0) {
      boolean bool = false;
      while (true) {
        if (U.compareAndSwapInt(this, STATUS, i, i | 0x10000))
          synchronized (this) {
            if (this.status >= 0) {
              try {
                wait(0L);
              } catch (InterruptedException interruptedException) {
                bool = true;
              } 
            } else {
              notifyAll();
            } 
          }  
        if ((i = this.status) < 0) {
          if (bool)
            Thread.currentThread().interrupt(); 
          break;
        } 
      } 
    } 
    return i;
  }
  
  private int externalInterruptibleAwaitDone() throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    int i;
    if ((i = this.status) >= 0)
      if ((i = (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : (ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : 0)) >= 0)
        while ((i = this.status) >= 0) {
          if (U.compareAndSwapInt(this, STATUS, i, i | 0x10000))
            synchronized (this) {
              if (this.status >= 0) {
                wait(0L);
              } else {
                notifyAll();
              } 
            }  
        }   
    return i;
  }
  
  private int doJoin() {
    int i;
    Thread thread;
    ForkJoinWorkerThread forkJoinWorkerThread;
    ForkJoinPool.WorkQueue workQueue;
    return ((i = this.status) < 0) ? i : (
      (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) ? (((workQueue = (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).workQueue)
      
      .tryUnpush(this) && (i = doExec()) < 0) ? i : forkJoinWorkerThread.pool
      .awaitJoin(workQueue, this, 0L)) : 
      externalAwaitDone());
  }
  
  private int doInvoke() {
    int i;
    Thread thread;
    ForkJoinWorkerThread forkJoinWorkerThread;
    return ((i = doExec()) < 0) ? i : (
      (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) ? (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool
      
      .awaitJoin(forkJoinWorkerThread.workQueue, this, 0L) : 
      externalAwaitDone());
  }
  
  static final class ForkJoinTask {}
  
  static final class ForkJoinTask {}
  
  static final class ForkJoinTask {}
  
  static final class ForkJoinTask {}
  
  static final class ExceptionNode extends WeakReference<ForkJoinTask<?>> {
    final Throwable ex;
    
    ExceptionNode next;
    
    final long thrower;
    
    final int hashCode;
    
    ExceptionNode(ForkJoinTask<?> param1ForkJoinTask, Throwable param1Throwable, ExceptionNode param1ExceptionNode) {
      super(param1ForkJoinTask, (ReferenceQueue)ForkJoinTask.exceptionTableRefQueue);
      this.ex = param1Throwable;
      this.next = param1ExceptionNode;
      this.thrower = Thread.currentThread().getId();
      this.hashCode = System.identityHashCode(param1ForkJoinTask);
    }
  }
  
  final int recordExceptionalCompletion(Throwable paramThrowable) {
    int i;
    if ((i = this.status) >= 0) {
      int j = System.identityHashCode(this);
      ReentrantLock reentrantLock = exceptionTableLock;
      reentrantLock.lock();
      try {
        expungeStaleExceptions();
        ExceptionNode[] arrayOfExceptionNode = exceptionTable;
        int k = j & arrayOfExceptionNode.length - 1;
        for (ExceptionNode exceptionNode = arrayOfExceptionNode[k];; exceptionNode = exceptionNode.next) {
          if (exceptionNode == null) {
            arrayOfExceptionNode[k] = new ExceptionNode(this, paramThrowable, arrayOfExceptionNode[k]);
            break;
          } 
          if (exceptionNode.get() == this)
            break; 
        } 
      } finally {
        reentrantLock.unlock();
      } 
      i = setCompletion(-2147483648);
    } 
    return i;
  }
  
  private int setExceptionalCompletion(Throwable paramThrowable) {
    int i = recordExceptionalCompletion(paramThrowable);
    if ((i & 0xF0000000) == Integer.MIN_VALUE)
      internalPropagateException(paramThrowable); 
    return i;
  }
  
  void internalPropagateException(Throwable paramThrowable) {}
  
  static final void cancelIgnoringExceptions(ForkJoinTask<?> paramForkJoinTask) {
    if (paramForkJoinTask != null && paramForkJoinTask.status >= 0)
      try {
        paramForkJoinTask.cancel(false);
      } catch (Throwable throwable) {} 
  }
  
  private void clearExceptionalCompletion() {
    int i = System.identityHashCode(this);
    ReentrantLock reentrantLock = exceptionTableLock;
    reentrantLock.lock();
    try {
      ExceptionNode[] arrayOfExceptionNode = exceptionTable;
      int j = i & arrayOfExceptionNode.length - 1;
      ExceptionNode exceptionNode1 = arrayOfExceptionNode[j];
      ExceptionNode exceptionNode2 = null;
      while (exceptionNode1 != null) {
        ExceptionNode exceptionNode = exceptionNode1.next;
        if (exceptionNode1.get() == this) {
          if (exceptionNode2 == null) {
            arrayOfExceptionNode[j] = exceptionNode;
            break;
          } 
          exceptionNode2.next = exceptionNode;
          break;
        } 
        exceptionNode2 = exceptionNode1;
        exceptionNode1 = exceptionNode;
      } 
      expungeStaleExceptions();
      this.status = 0;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private Throwable getThrowableException() {
    ExceptionNode exceptionNode;
    if ((this.status & 0xF0000000) != Integer.MIN_VALUE)
      return null; 
    int i = System.identityHashCode(this);
    ReentrantLock reentrantLock = exceptionTableLock;
    reentrantLock.lock();
    try {
      expungeStaleExceptions();
      ExceptionNode[] arrayOfExceptionNode = exceptionTable;
      exceptionNode = arrayOfExceptionNode[i & arrayOfExceptionNode.length - 1];
      while (exceptionNode != null && exceptionNode.get() != this)
        exceptionNode = exceptionNode.next; 
    } finally {
      reentrantLock.unlock();
    } 
    Throwable throwable;
    if (exceptionNode == null || (throwable = exceptionNode.ex) == null)
      return null; 
    if (exceptionNode.thrower != Thread.currentThread().getId()) {
      Class<?> clazz = throwable.getClass();
      try {
        Constructor<Throwable> constructor = null;
        Constructor[] arrayOfConstructor = (Constructor[])clazz.getConstructors();
        for (byte b = 0; b < arrayOfConstructor.length; b++) {
          Constructor<Throwable> constructor1 = arrayOfConstructor[b];
          Class[] arrayOfClass = constructor1.getParameterTypes();
          if (arrayOfClass.length == 0) {
            constructor = constructor1;
          } else if (arrayOfClass.length == 1 && arrayOfClass[0] == Throwable.class) {
            Throwable throwable1 = constructor1.newInstance(new Object[] { throwable });
            return (throwable1 == null) ? throwable : throwable1;
          } 
        } 
        if (constructor != null) {
          Throwable throwable1 = constructor.newInstance(new Object[0]);
          if (throwable1 != null) {
            throwable1.initCause(throwable);
            return throwable1;
          } 
        } 
      } catch (Exception exception) {}
    } 
    return throwable;
  }
  
  private static void expungeStaleExceptions() {
    Reference<?> reference;
    while ((reference = exceptionTableRefQueue.poll()) != null) {
      if (reference instanceof ExceptionNode) {
        int i = ((ExceptionNode)reference).hashCode;
        ExceptionNode[] arrayOfExceptionNode = exceptionTable;
        int j = i & arrayOfExceptionNode.length - 1;
        ExceptionNode exceptionNode1 = arrayOfExceptionNode[j];
        ExceptionNode exceptionNode2 = null;
        while (exceptionNode1 != null) {
          ExceptionNode exceptionNode = exceptionNode1.next;
          if (exceptionNode1 == reference) {
            if (exceptionNode2 == null) {
              arrayOfExceptionNode[j] = exceptionNode;
              break;
            } 
            exceptionNode2.next = exceptionNode;
            break;
          } 
          exceptionNode2 = exceptionNode1;
          exceptionNode1 = exceptionNode;
        } 
      } 
    } 
  }
  
  static final void helpExpungeStaleExceptions() {
    ReentrantLock reentrantLock = exceptionTableLock;
    if (reentrantLock.tryLock())
      try {
        expungeStaleExceptions();
      } finally {
        reentrantLock.unlock();
      }  
  }
  
  static void rethrow(Throwable paramThrowable) {
    if (paramThrowable != null)
      uncheckedThrow(paramThrowable); 
  }
  
  static <T extends Throwable> void uncheckedThrow(Throwable paramThrowable) throws T {
    throw (T)paramThrowable;
  }
  
  private void reportException(int paramInt) {
    if (paramInt == -1073741824)
      throw new CancellationException(); 
    if (paramInt == Integer.MIN_VALUE)
      rethrow(getThrowableException()); 
  }
  
  public final ForkJoinTask<V> fork() {
    Thread thread;
    if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
      ((ForkJoinWorkerThread)thread).workQueue.push(this);
    } else {
      ForkJoinPool.common.externalPush(this);
    } 
    return this;
  }
  
  public final V join() {
    int i;
    if ((i = doJoin() & 0xF0000000) != -268435456)
      reportException(i); 
    return getRawResult();
  }
  
  public final V invoke() {
    int i;
    if ((i = doInvoke() & 0xF0000000) != -268435456)
      reportException(i); 
    return getRawResult();
  }
  
  public static void invokeAll(ForkJoinTask<?> paramForkJoinTask1, ForkJoinTask<?> paramForkJoinTask2) {
    paramForkJoinTask2.fork();
    int i;
    if ((i = paramForkJoinTask1.doInvoke() & 0xF0000000) != -268435456)
      paramForkJoinTask1.reportException(i); 
    int j;
    if ((j = paramForkJoinTask2.doJoin() & 0xF0000000) != -268435456)
      paramForkJoinTask2.reportException(j); 
  }
  
  public static void invokeAll(ForkJoinTask<?>... paramVarArgs) {
    Throwable throwable = null;
    int i = paramVarArgs.length - 1;
    int j;
    for (j = i; j >= 0; j--) {
      ForkJoinTask<?> forkJoinTask = paramVarArgs[j];
      if (forkJoinTask == null) {
        if (throwable == null)
          throwable = new NullPointerException(); 
      } else if (j != 0) {
        forkJoinTask.fork();
      } else if (forkJoinTask.doInvoke() < -268435456 && throwable == null) {
        throwable = forkJoinTask.getException();
      } 
    } 
    for (j = 1; j <= i; j++) {
      ForkJoinTask<?> forkJoinTask = paramVarArgs[j];
      if (forkJoinTask != null)
        if (throwable != null) {
          forkJoinTask.cancel(false);
        } else if (forkJoinTask.doJoin() < -268435456) {
          throwable = forkJoinTask.getException();
        }  
    } 
    if (throwable != null)
      rethrow(throwable); 
  }
  
  public static <T extends ForkJoinTask<?>> Collection<T> invokeAll(Collection<T> paramCollection) {
    if (!(paramCollection instanceof java.util.RandomAccess) || !(paramCollection instanceof List)) {
      invokeAll((ForkJoinTask<?>[])paramCollection.<ForkJoinTask>toArray(new ForkJoinTask[paramCollection.size()]));
      return paramCollection;
    } 
    List<ForkJoinTask> list = (List)paramCollection;
    Throwable throwable = null;
    int i = list.size() - 1;
    int j;
    for (j = i; j >= 0; j--) {
      ForkJoinTask forkJoinTask = list.get(j);
      if (forkJoinTask == null) {
        if (throwable == null)
          throwable = new NullPointerException(); 
      } else if (j != 0) {
        forkJoinTask.fork();
      } else if (forkJoinTask.doInvoke() < -268435456 && throwable == null) {
        throwable = forkJoinTask.getException();
      } 
    } 
    for (j = 1; j <= i; j++) {
      ForkJoinTask forkJoinTask = list.get(j);
      if (forkJoinTask != null)
        if (throwable != null) {
          forkJoinTask.cancel(false);
        } else if (forkJoinTask.doJoin() < -268435456) {
          throwable = forkJoinTask.getException();
        }  
    } 
    if (throwable != null)
      rethrow(throwable); 
    return paramCollection;
  }
  
  public boolean cancel(boolean paramBoolean) {
    return ((setCompletion(-1073741824) & 0xF0000000) == -1073741824);
  }
  
  public final boolean isDone() {
    return (this.status < 0);
  }
  
  public final boolean isCancelled() {
    return ((this.status & 0xF0000000) == -1073741824);
  }
  
  public final boolean isCompletedAbnormally() {
    return (this.status < -268435456);
  }
  
  public final boolean isCompletedNormally() {
    return ((this.status & 0xF0000000) == -268435456);
  }
  
  public final Throwable getException() {
    int i = this.status & 0xF0000000;
    return (i >= -268435456) ? null : ((i == -1073741824) ? new CancellationException() : 
      
      getThrowableException());
  }
  
  public void completeExceptionally(Throwable paramThrowable) {
    setExceptionalCompletion((paramThrowable instanceof RuntimeException || paramThrowable instanceof Error) ? paramThrowable : new RuntimeException(paramThrowable));
  }
  
  public void complete(V paramV) {
    try {
      setRawResult(paramV);
    } catch (Throwable throwable) {
      setExceptionalCompletion(throwable);
      return;
    } 
    setCompletion(-268435456);
  }
  
  public final void quietlyComplete() {
    setCompletion(-268435456);
  }
  
  public final V get() throws InterruptedException, ExecutionException {
    int i = (Thread.currentThread() instanceof ForkJoinWorkerThread) ? doJoin() : externalInterruptibleAwaitDone();
    if ((i &= 0xF0000000) == -1073741824)
      throw new CancellationException(); 
    Throwable throwable;
    if (i == Integer.MIN_VALUE && (throwable = getThrowableException()) != null)
      throw new ExecutionException(throwable); 
    return getRawResult();
  }
  
  public final V get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    long l = paramTimeUnit.toNanos(paramLong);
    if (Thread.interrupted())
      throw new InterruptedException(); 
    int i;
    if ((i = this.status) >= 0 && l > 0L) {
      long l1 = System.nanoTime() + l;
      long l2 = (l1 == 0L) ? 1L : l1;
      Thread thread = Thread.currentThread();
      if (thread instanceof ForkJoinWorkerThread) {
        ForkJoinWorkerThread forkJoinWorkerThread = (ForkJoinWorkerThread)thread;
        i = forkJoinWorkerThread.pool.awaitJoin(forkJoinWorkerThread.workQueue, this, l2);
      } else if ((i = (this instanceof CountedCompleter) ? ForkJoinPool.common.externalHelpComplete((CountedCompleter)this, 0) : (ForkJoinPool.common.tryExternalUnpush(this) ? doExec() : 0)) >= 0) {
        long l3;
        while ((i = this.status) >= 0 && (
          l3 = l2 - System.nanoTime()) > 0L) {
          long l4;
          if ((l4 = TimeUnit.NANOSECONDS.toMillis(l3)) > 0L && U
            .compareAndSwapInt(this, STATUS, i, i | 0x10000))
            synchronized (this) {
              if (this.status >= 0) {
                wait(l4);
              } else {
                notifyAll();
              } 
            }  
        } 
      } 
    } 
    if (i >= 0)
      i = this.status; 
    if ((i &= 0xF0000000) != -268435456) {
      if (i == -1073741824)
        throw new CancellationException(); 
      if (i != Integer.MIN_VALUE)
        throw new TimeoutException(); 
      Throwable throwable;
      if ((throwable = getThrowableException()) != null)
        throw new ExecutionException(throwable); 
    } 
    return getRawResult();
  }
  
  public final void quietlyJoin() {
    doJoin();
  }
  
  public final void quietlyInvoke() {
    doInvoke();
  }
  
  public static void helpQuiesce() {
    Thread thread;
    if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
      ForkJoinWorkerThread forkJoinWorkerThread = (ForkJoinWorkerThread)thread;
      forkJoinWorkerThread.pool.helpQuiescePool(forkJoinWorkerThread.workQueue);
    } else {
      ForkJoinPool.quiesceCommonPool();
    } 
  }
  
  public void reinitialize() {
    if ((this.status & 0xF0000000) == Integer.MIN_VALUE) {
      clearExceptionalCompletion();
    } else {
      this.status = 0;
    } 
  }
  
  public static ForkJoinPool getPool() {
    Thread thread = Thread.currentThread();
    return (thread instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)thread).pool : null;
  }
  
  public static boolean inForkJoinPool() {
    return Thread.currentThread() instanceof ForkJoinWorkerThread;
  }
  
  public boolean tryUnfork() {
    Thread thread;
    return (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)thread).workQueue
      .tryUnpush(this) : ForkJoinPool.common
      .tryExternalUnpush(this);
  }
  
  public static int getQueuedTaskCount() {
    ForkJoinPool.WorkQueue workQueue;
    Thread thread;
    if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
      workQueue = ((ForkJoinWorkerThread)thread).workQueue;
    } else {
      workQueue = ForkJoinPool.commonSubmitterQueue();
    } 
    return (workQueue == null) ? 0 : workQueue.queueSize();
  }
  
  public static int getSurplusQueuedTaskCount() {
    return ForkJoinPool.getSurplusQueuedTaskCount();
  }
  
  protected static ForkJoinTask<?> peekNextLocalTask() {
    ForkJoinPool.WorkQueue workQueue;
    Thread thread;
    if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
      workQueue = ((ForkJoinWorkerThread)thread).workQueue;
    } else {
      workQueue = ForkJoinPool.commonSubmitterQueue();
    } 
    return (workQueue == null) ? null : workQueue.peek();
  }
  
  protected static ForkJoinTask<?> pollNextLocalTask() {
    Thread thread;
    return (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) ? ((ForkJoinWorkerThread)thread).workQueue
      .nextLocalTask() : null;
  }
  
  protected static ForkJoinTask<?> pollTask() {
    Thread thread;
    ForkJoinWorkerThread forkJoinWorkerThread;
    return (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) ? (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool
      .nextTaskFor(forkJoinWorkerThread.workQueue) : null;
  }
  
  public final short getForkJoinTaskTag() {
    return (short)this.status;
  }
  
  public final short setForkJoinTaskTag(short paramShort) {
    int i;
    do {
    
    } while (!U.compareAndSwapInt(this, STATUS, i = this.status, i & 0xFFFF0000 | paramShort & 0xFFFF));
    return (short)i;
  }
  
  public final boolean compareAndSetForkJoinTaskTag(short paramShort1, short paramShort2) {
    int i;
    do {
      if ((short)(i = this.status) != paramShort1)
        return false; 
    } while (!U.compareAndSwapInt(this, STATUS, i, i & 0xFFFF0000 | paramShort2 & 0xFFFF));
    return true;
  }
  
  public static ForkJoinTask<?> adapt(Runnable paramRunnable) {
    return new AdaptedRunnableAction(paramRunnable);
  }
  
  public static <T> ForkJoinTask<T> adapt(Runnable paramRunnable, T paramT) {
    return new AdaptedRunnable<>(paramRunnable, paramT);
  }
  
  public static <T> ForkJoinTask<T> adapt(Callable<? extends T> paramCallable) {
    return new AdaptedCallable<>(paramCallable);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getException());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Object object = paramObjectInputStream.readObject();
    if (object != null)
      setExceptionalCompletion((Throwable)object); 
  }
  
  private static final ReentrantLock exceptionTableLock = new ReentrantLock();
  
  private static final ReferenceQueue<Object> exceptionTableRefQueue = new ReferenceQueue();
  
  private static final int EXCEPTION_MAP_CAPACITY = 32;
  
  private static final long serialVersionUID = -7721805057305804111L;
  
  private static final Unsafe U;
  
  private static final long STATUS;
  
  static {
    exceptionTable = new ExceptionNode[32];
    try {
      U = Unsafe.getUnsafe();
      Class<ForkJoinTask> clazz = ForkJoinTask.class;
      STATUS = U.objectFieldOffset(clazz.getDeclaredField("status"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public abstract V getRawResult();
  
  protected abstract void setRawResult(V paramV);
  
  protected abstract boolean exec();
}
