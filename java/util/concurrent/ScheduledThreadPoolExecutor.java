package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {
  private volatile boolean continueExistingPeriodicTasksAfterShutdown;
  
  private volatile boolean executeExistingDelayedTasksAfterShutdown = true;
  
  private volatile boolean removeOnCancel = false;
  
  private static final AtomicLong sequencer = new AtomicLong();
  
  final long now() {
    return System.nanoTime();
  }
  
  private class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
    private final long sequenceNumber;
    
    private long time;
    
    private final long period;
    
    RunnableScheduledFuture<V> outerTask = this;
    
    int heapIndex;
    
    ScheduledFutureTask(Runnable param1Runnable, V param1V, long param1Long) {
      super(param1Runnable, param1V);
      this.time = param1Long;
      this.period = 0L;
      this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    ScheduledFutureTask(Runnable param1Runnable, V param1V, long param1Long1, long param1Long2) {
      super(param1Runnable, param1V);
      this.time = param1Long1;
      this.period = param1Long2;
      this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    ScheduledFutureTask(Callable<V> param1Callable, long param1Long) {
      super(param1Callable);
      this.time = param1Long;
      this.period = 0L;
      this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    public long getDelay(TimeUnit param1TimeUnit) {
      return param1TimeUnit.convert(this.time - ScheduledThreadPoolExecutor.this.now(), TimeUnit.NANOSECONDS);
    }
    
    public int compareTo(Delayed param1Delayed) {
      if (param1Delayed == this)
        return 0; 
      if (param1Delayed instanceof ScheduledFutureTask) {
        ScheduledFutureTask scheduledFutureTask = (ScheduledFutureTask)param1Delayed;
        long l1 = this.time - scheduledFutureTask.time;
        if (l1 < 0L)
          return -1; 
        if (l1 > 0L)
          return 1; 
        if (this.sequenceNumber < scheduledFutureTask.sequenceNumber)
          return -1; 
        return 1;
      } 
      long l = getDelay(TimeUnit.NANOSECONDS) - param1Delayed.getDelay(TimeUnit.NANOSECONDS);
      return (l < 0L) ? -1 : ((l > 0L) ? 1 : 0);
    }
    
    public boolean isPeriodic() {
      return (this.period != 0L);
    }
    
    private void setNextRunTime() {
      long l = this.period;
      if (l > 0L) {
        this.time += l;
      } else {
        this.time = ScheduledThreadPoolExecutor.this.triggerTime(-l);
      } 
    }
    
    public boolean cancel(boolean param1Boolean) {
      boolean bool = super.cancel(param1Boolean);
      if (bool && ScheduledThreadPoolExecutor.this.removeOnCancel && this.heapIndex >= 0)
        ScheduledThreadPoolExecutor.this.remove(this); 
      return bool;
    }
    
    public void run() {
      boolean bool = isPeriodic();
      if (!ScheduledThreadPoolExecutor.this.canRunInCurrentRunState(bool)) {
        cancel(false);
      } else if (!bool) {
        run();
      } else if (runAndReset()) {
        setNextRunTime();
        ScheduledThreadPoolExecutor.this.reExecutePeriodic(this.outerTask);
      } 
    }
  }
  
  boolean canRunInCurrentRunState(boolean paramBoolean) {
    return isRunningOrShutdown(paramBoolean ? this.continueExistingPeriodicTasksAfterShutdown : this.executeExistingDelayedTasksAfterShutdown);
  }
  
  private void delayedExecute(RunnableScheduledFuture<?> paramRunnableScheduledFuture) {
    if (isShutdown()) {
      reject(paramRunnableScheduledFuture);
    } else {
      super.getQueue().add(paramRunnableScheduledFuture);
      if (isShutdown() && 
        !canRunInCurrentRunState(paramRunnableScheduledFuture.isPeriodic()) && 
        remove(paramRunnableScheduledFuture)) {
        paramRunnableScheduledFuture.cancel(false);
      } else {
        ensurePrestart();
      } 
    } 
  }
  
  void reExecutePeriodic(RunnableScheduledFuture<?> paramRunnableScheduledFuture) {
    if (canRunInCurrentRunState(true)) {
      super.getQueue().add(paramRunnableScheduledFuture);
      if (!canRunInCurrentRunState(true) && remove(paramRunnableScheduledFuture)) {
        paramRunnableScheduledFuture.cancel(false);
      } else {
        ensurePrestart();
      } 
    } 
  }
  
  void onShutdown() {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial getQueue : ()Ljava/util/concurrent/BlockingQueue;
    //   4: astore_1
    //   5: aload_0
    //   6: invokevirtual getExecuteExistingDelayedTasksAfterShutdownPolicy : ()Z
    //   9: istore_2
    //   10: aload_0
    //   11: invokevirtual getContinueExistingPeriodicTasksAfterShutdownPolicy : ()Z
    //   14: istore_3
    //   15: iload_2
    //   16: ifne -> 88
    //   19: iload_3
    //   20: ifne -> 88
    //   23: aload_1
    //   24: invokeinterface toArray : ()[Ljava/lang/Object;
    //   29: astore #4
    //   31: aload #4
    //   33: arraylength
    //   34: istore #5
    //   36: iconst_0
    //   37: istore #6
    //   39: iload #6
    //   41: iload #5
    //   43: if_icmpge -> 79
    //   46: aload #4
    //   48: iload #6
    //   50: aaload
    //   51: astore #7
    //   53: aload #7
    //   55: instanceof java/util/concurrent/RunnableScheduledFuture
    //   58: ifeq -> 73
    //   61: aload #7
    //   63: checkcast java/util/concurrent/RunnableScheduledFuture
    //   66: iconst_0
    //   67: invokeinterface cancel : (Z)Z
    //   72: pop
    //   73: iinc #6, 1
    //   76: goto -> 39
    //   79: aload_1
    //   80: invokeinterface clear : ()V
    //   85: goto -> 190
    //   88: aload_1
    //   89: invokeinterface toArray : ()[Ljava/lang/Object;
    //   94: astore #4
    //   96: aload #4
    //   98: arraylength
    //   99: istore #5
    //   101: iconst_0
    //   102: istore #6
    //   104: iload #6
    //   106: iload #5
    //   108: if_icmpge -> 190
    //   111: aload #4
    //   113: iload #6
    //   115: aaload
    //   116: astore #7
    //   118: aload #7
    //   120: instanceof java/util/concurrent/RunnableScheduledFuture
    //   123: ifeq -> 184
    //   126: aload #7
    //   128: checkcast java/util/concurrent/RunnableScheduledFuture
    //   131: astore #8
    //   133: aload #8
    //   135: invokeinterface isPeriodic : ()Z
    //   140: ifeq -> 150
    //   143: iload_3
    //   144: ifne -> 154
    //   147: goto -> 164
    //   150: iload_2
    //   151: ifeq -> 164
    //   154: aload #8
    //   156: invokeinterface isCancelled : ()Z
    //   161: ifeq -> 184
    //   164: aload_1
    //   165: aload #8
    //   167: invokeinterface remove : (Ljava/lang/Object;)Z
    //   172: ifeq -> 184
    //   175: aload #8
    //   177: iconst_0
    //   178: invokeinterface cancel : (Z)Z
    //   183: pop
    //   184: iinc #6, 1
    //   187: goto -> 104
    //   190: aload_0
    //   191: invokevirtual tryTerminate : ()V
    //   194: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #359	-> 0
    //   #360	-> 5
    //   #361	-> 6
    //   #362	-> 10
    //   #363	-> 11
    //   #364	-> 15
    //   #365	-> 23
    //   #366	-> 53
    //   #367	-> 61
    //   #365	-> 73
    //   #368	-> 79
    //   #372	-> 88
    //   #373	-> 118
    //   #374	-> 126
    //   #376	-> 133
    //   #377	-> 156
    //   #378	-> 164
    //   #379	-> 175
    //   #372	-> 184
    //   #384	-> 190
    //   #385	-> 194
  }
  
  protected <V> RunnableScheduledFuture<V> decorateTask(Runnable paramRunnable, RunnableScheduledFuture<V> paramRunnableScheduledFuture) {
    return paramRunnableScheduledFuture;
  }
  
  protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> paramCallable, RunnableScheduledFuture<V> paramRunnableScheduledFuture) {
    return paramRunnableScheduledFuture;
  }
  
  public ScheduledThreadPoolExecutor(int paramInt) {
    super(paramInt, 2147483647, 0L, TimeUnit.NANOSECONDS, (BlockingQueue<Runnable>)new DelayedWorkQueue());
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, ThreadFactory paramThreadFactory) {
    super(paramInt, 2147483647, 0L, TimeUnit.NANOSECONDS, (BlockingQueue<Runnable>)new DelayedWorkQueue(), paramThreadFactory);
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, RejectedExecutionHandler paramRejectedExecutionHandler) {
    super(paramInt, 2147483647, 0L, TimeUnit.NANOSECONDS, (BlockingQueue<Runnable>)new DelayedWorkQueue(), paramRejectedExecutionHandler);
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, ThreadFactory paramThreadFactory, RejectedExecutionHandler paramRejectedExecutionHandler) {
    super(paramInt, 2147483647, 0L, TimeUnit.NANOSECONDS, (BlockingQueue<Runnable>)new DelayedWorkQueue(), paramThreadFactory, paramRejectedExecutionHandler);
  }
  
  private long triggerTime(long paramLong, TimeUnit paramTimeUnit) {
    return triggerTime(paramTimeUnit.toNanos((paramLong < 0L) ? 0L : paramLong));
  }
  
  long triggerTime(long paramLong) {
    return now() + ((paramLong < 4611686018427387903L) ? paramLong : overflowFree(paramLong));
  }
  
  private long overflowFree(long paramLong) {
    Delayed delayed = (Delayed)super.getQueue().peek();
    if (delayed != null) {
      long l = delayed.getDelay(TimeUnit.NANOSECONDS);
      if (l < 0L && paramLong - l < 0L)
        paramLong = Long.MAX_VALUE + l; 
    } 
    return paramLong;
  }
  
  public ScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) {
    if (paramRunnable == null || paramTimeUnit == null)
      throw new NullPointerException(); 
    RunnableScheduledFuture<?> runnableScheduledFuture = decorateTask(paramRunnable, new ScheduledFutureTask(paramRunnable, null, 
          
          triggerTime(paramLong, paramTimeUnit)));
    delayedExecute(runnableScheduledFuture);
    return runnableScheduledFuture;
  }
  
  public <V> ScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit) {
    if (paramCallable == null || paramTimeUnit == null)
      throw new NullPointerException(); 
    RunnableScheduledFuture<V> runnableScheduledFuture = decorateTask(paramCallable, new ScheduledFutureTask<>(paramCallable, 
          
          triggerTime(paramLong, paramTimeUnit)));
    delayedExecute(runnableScheduledFuture);
    return runnableScheduledFuture;
  }
  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit) {
    if (paramRunnable == null || paramTimeUnit == null)
      throw new NullPointerException(); 
    if (paramLong2 <= 0L)
      throw new IllegalArgumentException(); 
    ScheduledFutureTask<?> scheduledFutureTask = new ScheduledFutureTask(paramRunnable, null, triggerTime(paramLong1, paramTimeUnit), paramTimeUnit.toNanos(paramLong2));
    RunnableScheduledFuture<?> runnableScheduledFuture = decorateTask(paramRunnable, scheduledFutureTask);
    scheduledFutureTask.outerTask = runnableScheduledFuture;
    delayedExecute(runnableScheduledFuture);
    return runnableScheduledFuture;
  }
  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit) {
    if (paramRunnable == null || paramTimeUnit == null)
      throw new NullPointerException(); 
    if (paramLong2 <= 0L)
      throw new IllegalArgumentException(); 
    ScheduledFutureTask<?> scheduledFutureTask = new ScheduledFutureTask(paramRunnable, null, triggerTime(paramLong1, paramTimeUnit), paramTimeUnit.toNanos(-paramLong2));
    RunnableScheduledFuture<?> runnableScheduledFuture = decorateTask(paramRunnable, scheduledFutureTask);
    scheduledFutureTask.outerTask = runnableScheduledFuture;
    delayedExecute(runnableScheduledFuture);
    return runnableScheduledFuture;
  }
  
  public void execute(Runnable paramRunnable) {
    schedule(paramRunnable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public Future<?> submit(Runnable paramRunnable) {
    return schedule(paramRunnable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT) {
    return schedule(Executors.callable(paramRunnable, paramT), 0L, TimeUnit.NANOSECONDS);
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable) {
    return schedule(paramCallable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean paramBoolean) {
    this.continueExistingPeriodicTasksAfterShutdown = paramBoolean;
    if (!paramBoolean && isShutdown())
      onShutdown(); 
  }
  
  public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
    return this.continueExistingPeriodicTasksAfterShutdown;
  }
  
  public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean paramBoolean) {
    this.executeExistingDelayedTasksAfterShutdown = paramBoolean;
    if (!paramBoolean && isShutdown())
      onShutdown(); 
  }
  
  public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
    return this.executeExistingDelayedTasksAfterShutdown;
  }
  
  public void setRemoveOnCancelPolicy(boolean paramBoolean) {
    this.removeOnCancel = paramBoolean;
  }
  
  public boolean getRemoveOnCancelPolicy() {
    return this.removeOnCancel;
  }
  
  public void shutdown() {
    super.shutdown();
  }
  
  public List<Runnable> shutdownNow() {
    return super.shutdownNow();
  }
  
  public BlockingQueue<Runnable> getQueue() {
    return super.getQueue();
  }
  
  static class DelayedWorkQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {
    private static final int INITIAL_CAPACITY = 16;
    
    private RunnableScheduledFuture<?>[] queue = (RunnableScheduledFuture<?>[])new RunnableScheduledFuture[16];
    
    private final ReentrantLock lock = new ReentrantLock();
    
    private int size = 0;
    
    private Thread leader = null;
    
    private final Condition available = this.lock.newCondition();
    
    private void setIndex(RunnableScheduledFuture<?> param1RunnableScheduledFuture, int param1Int) {
      if (param1RunnableScheduledFuture instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask)
        ((ScheduledThreadPoolExecutor.ScheduledFutureTask)param1RunnableScheduledFuture).heapIndex = param1Int; 
    }
    
    private void siftUp(int param1Int, RunnableScheduledFuture<?> param1RunnableScheduledFuture) {
      while (param1Int > 0) {
        int i = param1Int - 1 >>> 1;
        RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[i];
        if (param1RunnableScheduledFuture.compareTo(runnableScheduledFuture) >= 0)
          break; 
        this.queue[param1Int] = runnableScheduledFuture;
        setIndex(runnableScheduledFuture, param1Int);
        param1Int = i;
      } 
      this.queue[param1Int] = param1RunnableScheduledFuture;
      setIndex(param1RunnableScheduledFuture, param1Int);
    }
    
    private void siftDown(int param1Int, RunnableScheduledFuture<?> param1RunnableScheduledFuture) {
      int i = this.size >>> 1;
      while (param1Int < i) {
        int j = (param1Int << 1) + 1;
        RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[j];
        int k = j + 1;
        if (k < this.size && runnableScheduledFuture.compareTo(this.queue[k]) > 0)
          runnableScheduledFuture = this.queue[j = k]; 
        if (param1RunnableScheduledFuture.compareTo(runnableScheduledFuture) <= 0)
          break; 
        this.queue[param1Int] = runnableScheduledFuture;
        setIndex(runnableScheduledFuture, param1Int);
        param1Int = j;
      } 
      this.queue[param1Int] = param1RunnableScheduledFuture;
      setIndex(param1RunnableScheduledFuture, param1Int);
    }
    
    private void grow() {
      int i = this.queue.length;
      int j = i + (i >> 1);
      if (j < 0)
        j = Integer.MAX_VALUE; 
      this.queue = (RunnableScheduledFuture<?>[])Arrays.<RunnableScheduledFuture>copyOf((RunnableScheduledFuture[])this.queue, j);
    }
    
    private int indexOf(Object param1Object) {
      if (param1Object != null)
        if (param1Object instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask) {
          int i = ((ScheduledThreadPoolExecutor.ScheduledFutureTask)param1Object).heapIndex;
          if (i >= 0 && i < this.size && this.queue[i] == param1Object)
            return i; 
        } else {
          for (byte b = 0; b < this.size; b++) {
            if (param1Object.equals(this.queue[b]))
              return b; 
          } 
        }  
      return -1;
    }
    
    public boolean contains(Object param1Object) {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        return (indexOf(param1Object) != -1);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean remove(Object param1Object) {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        int i = indexOf(param1Object);
        if (i < 0)
          return false; 
        setIndex(this.queue[i], -1);
        int j = --this.size;
        RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[j];
        this.queue[j] = null;
        if (j != i) {
          siftDown(i, runnableScheduledFuture);
          if (this.queue[i] == runnableScheduledFuture)
            siftUp(i, runnableScheduledFuture); 
        } 
        return true;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public int size() {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        return this.size;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean isEmpty() {
      return (size() == 0);
    }
    
    public int remainingCapacity() {
      return Integer.MAX_VALUE;
    }
    
    public RunnableScheduledFuture<?> peek() {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        return this.queue[0];
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean offer(Runnable param1Runnable) {
      if (param1Runnable == null)
        throw new NullPointerException(); 
      RunnableScheduledFuture<?> runnableScheduledFuture = (RunnableScheduledFuture)param1Runnable;
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        int i = this.size;
        if (i >= this.queue.length)
          grow(); 
        this.size = i + 1;
        if (i == 0) {
          this.queue[0] = runnableScheduledFuture;
          setIndex(runnableScheduledFuture, 0);
        } else {
          siftUp(i, runnableScheduledFuture);
        } 
        if (this.queue[0] == runnableScheduledFuture) {
          this.leader = null;
          this.available.signal();
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return true;
    }
    
    public void put(Runnable param1Runnable) {
      offer(param1Runnable);
    }
    
    public boolean add(Runnable param1Runnable) {
      return offer(param1Runnable);
    }
    
    public boolean offer(Runnable param1Runnable, long param1Long, TimeUnit param1TimeUnit) {
      return offer(param1Runnable);
    }
    
    private RunnableScheduledFuture<?> finishPoll(RunnableScheduledFuture<?> param1RunnableScheduledFuture) {
      int i = --this.size;
      RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[i];
      this.queue[i] = null;
      if (i != 0)
        siftDown(0, runnableScheduledFuture); 
      setIndex(param1RunnableScheduledFuture, -1);
      return param1RunnableScheduledFuture;
    }
    
    public RunnableScheduledFuture<?> poll() {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[0];
        if (runnableScheduledFuture == null || runnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS) > 0L)
          return null; 
        return finishPoll(runnableScheduledFuture);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public RunnableScheduledFuture<?> take() throws InterruptedException {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lockInterruptibly();
      try {
        while (true) {
          RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[0];
          if (runnableScheduledFuture == null) {
            this.available.await();
            continue;
          } 
          long l = runnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS);
          if (l <= 0L)
            return finishPoll(runnableScheduledFuture); 
          runnableScheduledFuture = null;
          if (this.leader != null) {
            this.available.await();
            continue;
          } 
          Thread thread = Thread.currentThread();
          this.leader = thread;
          try {
            this.available.awaitNanos(l);
          } finally {
            if (this.leader == thread)
              this.leader = null; 
          } 
        } 
      } finally {
        if (this.leader == null && this.queue[0] != null)
          this.available.signal(); 
        reentrantLock.unlock();
      } 
    }
    
    public RunnableScheduledFuture<?> poll(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException {
      long l = param1TimeUnit.toNanos(param1Long);
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lockInterruptibly();
      try {
        while (true) {
          RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[0];
          if (runnableScheduledFuture == null) {
            if (l <= 0L)
              return null; 
            l = this.available.awaitNanos(l);
            continue;
          } 
          long l1 = runnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS);
          if (l1 <= 0L)
            return finishPoll(runnableScheduledFuture); 
          if (l <= 0L)
            return null; 
          runnableScheduledFuture = null;
          if (l < l1 || this.leader != null) {
            l = this.available.awaitNanos(l);
            continue;
          } 
          Thread thread = Thread.currentThread();
          this.leader = thread;
          try {
            long l2 = this.available.awaitNanos(l1);
            l -= l1 - l2;
          } finally {
            if (this.leader == thread)
              this.leader = null; 
          } 
        } 
      } finally {
        if (this.leader == null && this.queue[0] != null)
          this.available.signal(); 
        reentrantLock.unlock();
      } 
    }
    
    public void clear() {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        for (byte b = 0; b < this.size; b++) {
          RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[b];
          if (runnableScheduledFuture != null) {
            this.queue[b] = null;
            setIndex(runnableScheduledFuture, -1);
          } 
        } 
        this.size = 0;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    private RunnableScheduledFuture<?> peekExpired() {
      RunnableScheduledFuture<?> runnableScheduledFuture = this.queue[0];
      return (runnableScheduledFuture == null || runnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS) > 0L) ? null : runnableScheduledFuture;
    }
    
    public int drainTo(Collection<? super Runnable> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      if (param1Collection == this)
        throw new IllegalArgumentException(); 
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        byte b = 0;
        RunnableScheduledFuture<?> runnableScheduledFuture;
        while ((runnableScheduledFuture = peekExpired()) != null) {
          param1Collection.add(runnableScheduledFuture);
          finishPoll(runnableScheduledFuture);
          b++;
        } 
        return b;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public int drainTo(Collection<? super Runnable> param1Collection, int param1Int) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      if (param1Collection == this)
        throw new IllegalArgumentException(); 
      if (param1Int <= 0)
        return 0; 
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        byte b = 0;
        RunnableScheduledFuture<?> runnableScheduledFuture;
        while (b < param1Int && (runnableScheduledFuture = peekExpired()) != null) {
          param1Collection.add(runnableScheduledFuture);
          finishPoll(runnableScheduledFuture);
          b++;
        } 
        return b;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public Object[] toArray() {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        return Arrays.copyOf(this.queue, this.size, Object[].class);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      ReentrantLock reentrantLock = this.lock;
      reentrantLock.lock();
      try {
        if (param1ArrayOfT.length < this.size)
          return (T[])Arrays.<Object, RunnableScheduledFuture<?>>copyOf(this.queue, this.size, (Class)param1ArrayOfT.getClass()); 
        System.arraycopy(this.queue, 0, param1ArrayOfT, 0, this.size);
        if (param1ArrayOfT.length > this.size)
          param1ArrayOfT[this.size] = null; 
        return param1ArrayOfT;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public Iterator<Runnable> iterator() {
      return new Itr(this, (RunnableScheduledFuture<?>[])Arrays.<RunnableScheduledFuture>copyOf((RunnableScheduledFuture[])this.queue, this.size));
    }
    
    private class DelayedWorkQueue {}
  }
}
