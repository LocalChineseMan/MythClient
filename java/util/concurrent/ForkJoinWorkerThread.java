package java.util.concurrent;

import java.security.AccessControlContext;
import sun.misc.Unsafe;

public class ForkJoinWorkerThread extends Thread {
  final ForkJoinPool pool;
  
  final ForkJoinPool.WorkQueue workQueue;
  
  private static final Unsafe U;
  
  private static final long THREADLOCALS;
  
  private static final long INHERITABLETHREADLOCALS;
  
  private static final long INHERITEDACCESSCONTROLCONTEXT;
  
  protected ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool) {
    super("aForkJoinWorkerThread");
    this.pool = paramForkJoinPool;
    this.workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool, ThreadGroup paramThreadGroup, AccessControlContext paramAccessControlContext) {
    super(paramThreadGroup, null, "aForkJoinWorkerThread");
    U.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, paramAccessControlContext);
    eraseThreadLocals();
    this.pool = paramForkJoinPool;
    this.workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  public ForkJoinPool getPool() {
    return this.pool;
  }
  
  public int getPoolIndex() {
    return this.workQueue.getPoolIndex();
  }
  
  protected void onStart() {}
  
  protected void onTermination(Throwable paramThrowable) {}
  
  public void run() {
    if (this.workQueue.array == null) {
      Throwable throwable = null;
      try {
        onStart();
        this.pool.runWorker(this.workQueue);
      } catch (Throwable throwable1) {
        throwable = throwable1;
      } finally {
        try {
          onTermination(throwable);
        } catch (Throwable throwable1) {
          if (throwable == null)
            throwable = throwable1; 
        } finally {
          this.pool.deregisterWorker(this, throwable);
        } 
      } 
    } 
  }
  
  final void eraseThreadLocals() {
    U.putObject(this, THREADLOCALS, null);
    U.putObject(this, INHERITABLETHREADLOCALS, null);
  }
  
  void afterTopLevelExec() {}
  
  static {
    try {
      U = Unsafe.getUnsafe();
      Class<Thread> clazz = Thread.class;
      THREADLOCALS = U.objectFieldOffset(clazz.getDeclaredField("threadLocals"));
      INHERITABLETHREADLOCALS = U.objectFieldOffset(clazz.getDeclaredField("inheritableThreadLocals"));
      INHERITEDACCESSCONTROLCONTEXT = U.objectFieldOffset(clazz.getDeclaredField("inheritedAccessControlContext"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class ForkJoinWorkerThread {}
}
