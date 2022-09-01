package java.util.concurrent;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicInteger;

public class Executors {
  public static ExecutorService newFixedThreadPool(int paramInt) {
    return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
  }
  
  public static ExecutorService newWorkStealingPool(int paramInt) {
    return new ForkJoinPool(paramInt, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  }
  
  public static ExecutorService newWorkStealingPool() {
    return new ForkJoinPool(
        Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  }
  
  public static ExecutorService newFixedThreadPool(int paramInt, ThreadFactory paramThreadFactory) {
    return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), paramThreadFactory);
  }
  
  public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
  }
  
  public static ExecutorService newSingleThreadExecutor(ThreadFactory paramThreadFactory) {
    return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), paramThreadFactory));
  }
  
  public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
  }
  
  public static ExecutorService newCachedThreadPool(ThreadFactory paramThreadFactory) {
    return new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), paramThreadFactory);
  }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
    return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1));
  }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory paramThreadFactory) {
    return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1, paramThreadFactory));
  }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt) {
    return new ScheduledThreadPoolExecutor(paramInt);
  }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt, ThreadFactory paramThreadFactory) {
    return new ScheduledThreadPoolExecutor(paramInt, paramThreadFactory);
  }
  
  public static ExecutorService unconfigurableExecutorService(ExecutorService paramExecutorService) {
    if (paramExecutorService == null)
      throw new NullPointerException(); 
    return new DelegatedExecutorService(paramExecutorService);
  }
  
  public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService paramScheduledExecutorService) {
    if (paramScheduledExecutorService == null)
      throw new NullPointerException(); 
    return new DelegatedScheduledExecutorService(paramScheduledExecutorService);
  }
  
  public static ThreadFactory defaultThreadFactory() {
    return new DefaultThreadFactory();
  }
  
  public static ThreadFactory privilegedThreadFactory() {
    return new PrivilegedThreadFactory();
  }
  
  public static <T> Callable<T> callable(Runnable paramRunnable, T paramT) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    return new RunnableAdapter<>(paramRunnable, paramT);
  }
  
  public static Callable<Object> callable(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    return new RunnableAdapter(paramRunnable, null);
  }
  
  public static Callable<Object> callable(PrivilegedAction<?> paramPrivilegedAction) {
    if (paramPrivilegedAction == null)
      throw new NullPointerException(); 
    return (Callable<Object>)new Object(paramPrivilegedAction);
  }
  
  public static Callable<Object> callable(PrivilegedExceptionAction<?> paramPrivilegedExceptionAction) {
    if (paramPrivilegedExceptionAction == null)
      throw new NullPointerException(); 
    return (Callable<Object>)new Object(paramPrivilegedExceptionAction);
  }
  
  public static <T> Callable<T> privilegedCallable(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    return new PrivilegedCallable<>(paramCallable);
  }
  
  public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    return new PrivilegedCallableUsingCurrentClassLoader<>(paramCallable);
  }
  
  static final class RunnableAdapter<T> implements Callable<T> {
    final Runnable task;
    
    final T result;
    
    RunnableAdapter(Runnable param1Runnable, T param1T) {
      this.task = param1Runnable;
      this.result = param1T;
    }
    
    public T call() {
      this.task.run();
      return this.result;
    }
  }
  
  static final class Executors {}
  
  static final class Executors {}
  
  static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    
    private final ThreadGroup group;
    
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    private final String namePrefix;
    
    DefaultThreadFactory() {
      SecurityManager securityManager = System.getSecurityManager();
      this
        .group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this
        .namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }
    
    public Thread newThread(Runnable param1Runnable) {
      Thread thread = new Thread(this.group, param1Runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      if (thread.isDaemon())
        thread.setDaemon(false); 
      if (thread.getPriority() != 5)
        thread.setPriority(5); 
      return thread;
    }
  }
  
  static class Executors {}
  
  static class Executors {}
  
  static class Executors {}
  
  static class Executors {}
}
