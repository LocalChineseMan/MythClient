package java.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class Semaphore implements Serializable {
  private static final long serialVersionUID = -3222578661600680210L;
  
  private final Sync sync;
  
  static abstract class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 1192457210091910933L;
    
    Sync(int param1Int) {
      setState(param1Int);
    }
    
    final int getPermits() {
      return getState();
    }
    
    final int nonfairTryAcquireShared(int param1Int) {
      int i;
      int j;
      do {
        i = getState();
        j = i - param1Int;
      } while (j >= 0 && 
        !compareAndSetState(i, j));
      return j;
    }
    
    protected final boolean tryReleaseShared(int param1Int) {
      while (true) {
        int i = getState();
        int j = i + param1Int;
        if (j < i)
          throw new Error("Maximum permit count exceeded"); 
        if (compareAndSetState(i, j))
          return true; 
      } 
    }
    
    final void reducePermits(int param1Int) {
      int i;
      int j;
      do {
        i = getState();
        j = i - param1Int;
        if (j > i)
          throw new Error("Permit count underflow"); 
      } while (!compareAndSetState(i, j));
    }
    
    final int drainPermits() {
      int i;
      do {
        i = getState();
      } while (i != 0 && !compareAndSetState(i, 0));
      return i;
    }
  }
  
  static final class NonfairSync extends Sync {
    private static final long serialVersionUID = -2694183684443567898L;
    
    NonfairSync(int param1Int) {
      super(param1Int);
    }
    
    protected int tryAcquireShared(int param1Int) {
      return nonfairTryAcquireShared(param1Int);
    }
  }
  
  public Semaphore(int paramInt) {
    this.sync = new NonfairSync(paramInt);
  }
  
  public Semaphore(int paramInt, boolean paramBoolean) {
    this.sync = paramBoolean ? new FairSync(paramInt) : new NonfairSync(paramInt);
  }
  
  public void acquire() throws InterruptedException {
    this.sync.acquireSharedInterruptibly(1);
  }
  
  public void acquireUninterruptibly() {
    this.sync.acquireShared(1);
  }
  
  public boolean tryAcquire() {
    return (this.sync.nonfairTryAcquireShared(1) >= 0);
  }
  
  public boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    return this.sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
  }
  
  public void release() {
    this.sync.releaseShared(1);
  }
  
  public void acquire(int paramInt) throws InterruptedException {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.sync.acquireSharedInterruptibly(paramInt);
  }
  
  public void acquireUninterruptibly(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.sync.acquireShared(paramInt);
  }
  
  public boolean tryAcquire(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return (this.sync.nonfairTryAcquireShared(paramInt) >= 0);
  }
  
  public boolean tryAcquire(int paramInt, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return this.sync.tryAcquireSharedNanos(paramInt, paramTimeUnit.toNanos(paramLong));
  }
  
  public void release(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.sync.releaseShared(paramInt);
  }
  
  public int availablePermits() {
    return this.sync.getPermits();
  }
  
  public int drainPermits() {
    return this.sync.drainPermits();
  }
  
  protected void reducePermits(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.sync.reducePermits(paramInt);
  }
  
  public boolean isFair() {
    return this.sync instanceof FairSync;
  }
  
  public final boolean hasQueuedThreads() {
    return this.sync.hasQueuedThreads();
  }
  
  public final int getQueueLength() {
    return this.sync.getQueueLength();
  }
  
  protected Collection<Thread> getQueuedThreads() {
    return this.sync.getQueuedThreads();
  }
  
  public String toString() {
    return super.toString() + "[Permits = " + this.sync.getPermits() + "]";
  }
  
  static final class Semaphore {}
}
