package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MoreExecutors {
  @Beta
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
    return (new Application()).getExitingExecutorService(executor, terminationTimeout, timeUnit);
  }
  
  @Beta
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
    return (new Application()).getExitingScheduledExecutorService(executor, terminationTimeout, timeUnit);
  }
  
  @Beta
  public static void addDelayedShutdownHook(ExecutorService service, long terminationTimeout, TimeUnit timeUnit) {
    (new Application()).addDelayedShutdownHook(service, terminationTimeout, timeUnit);
  }
  
  @Beta
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor) {
    return (new Application()).getExitingExecutorService(executor);
  }
  
  @Beta
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor) {
    return (new Application()).getExitingScheduledExecutorService(executor);
  }
  
  private static void useDaemonThreadFactory(ThreadPoolExecutor executor) {
    executor.setThreadFactory((new ThreadFactoryBuilder()).setDaemon(true).setThreadFactory(executor.getThreadFactory()).build());
  }
  
  public static ListeningExecutorService sameThreadExecutor() {
    return new SameThreadExecutorService();
  }
  
  private static class SameThreadExecutorService extends AbstractListeningExecutorService {
    private final Lock lock = new ReentrantLock();
    
    private final Condition termination = this.lock.newCondition();
    
    private int runningTasks = 0;
    
    private boolean shutdown = false;
    
    public void execute(Runnable command) {
      startTask();
      try {
        command.run();
      } finally {
        endTask();
      } 
    }
    
    public boolean isShutdown() {
      this.lock.lock();
      try {
        return this.shutdown;
      } finally {
        this.lock.unlock();
      } 
    }
    
    public void shutdown() {
      this.lock.lock();
      try {
        this.shutdown = true;
      } finally {
        this.lock.unlock();
      } 
    }
    
    public List<Runnable> shutdownNow() {
      shutdown();
      return Collections.emptyList();
    }
    
    public boolean isTerminated() {
      this.lock.lock();
      try {
        return (this.shutdown && this.runningTasks == 0);
      } finally {
        this.lock.unlock();
      } 
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long nanos = unit.toNanos(timeout);
      this.lock.lock();
      try {
        while (true) {
          if (isTerminated())
            return true; 
          if (nanos <= 0L)
            return false; 
          nanos = this.termination.awaitNanos(nanos);
        } 
      } finally {
        this.lock.unlock();
      } 
    }
    
    private void startTask() {
      this.lock.lock();
      try {
        if (isShutdown())
          throw new RejectedExecutionException("Executor already shutdown"); 
        this.runningTasks++;
      } finally {
        this.lock.unlock();
      } 
    }
    
    private void endTask() {
      this.lock.lock();
      try {
        this.runningTasks--;
        if (isTerminated())
          this.termination.signalAll(); 
      } finally {
        this.lock.unlock();
      } 
    }
    
    private SameThreadExecutorService() {}
  }
  
  public static ListeningExecutorService listeningDecorator(ExecutorService delegate) {
    return (delegate instanceof ListeningExecutorService) ? (ListeningExecutorService)delegate : ((delegate instanceof ScheduledExecutorService) ? (ListeningExecutorService)new ScheduledListeningDecorator((ScheduledExecutorService)delegate) : new ListeningDecorator(delegate));
  }
  
  public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService delegate) {
    return (delegate instanceof ListeningScheduledExecutorService) ? (ListeningScheduledExecutorService)delegate : (ListeningScheduledExecutorService)new ScheduledListeningDecorator(delegate);
  }
  
  private static class ListeningDecorator extends AbstractListeningExecutorService {
    private final ExecutorService delegate;
    
    ListeningDecorator(ExecutorService delegate) {
      this.delegate = (ExecutorService)Preconditions.checkNotNull(delegate);
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate.awaitTermination(timeout, unit);
    }
    
    public boolean isShutdown() {
      return this.delegate.isShutdown();
    }
    
    public boolean isTerminated() {
      return this.delegate.isTerminated();
    }
    
    public void shutdown() {
      this.delegate.shutdown();
    }
    
    public List<Runnable> shutdownNow() {
      return this.delegate.shutdownNow();
    }
    
    public void execute(Runnable command) {
      this.delegate.execute(command);
    }
  }
  
  static <T> T invokeAnyImpl(ListeningExecutorService executorService, Collection<? extends Callable<T>> tasks, boolean timed, long nanos) throws InterruptedException, ExecutionException, TimeoutException {
    Preconditions.checkNotNull(executorService);
    int ntasks = tasks.size();
    Preconditions.checkArgument((ntasks > 0));
    List<Future<T>> futures = Lists.newArrayListWithCapacity(ntasks);
    BlockingQueue<Future<T>> futureQueue = Queues.newLinkedBlockingQueue();
  }
  
  private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService executorService, Callable<T> task, final BlockingQueue<Future<T>> queue) {
    final ListenableFuture<T> future = executorService.submit(task);
    future.addListener(new Runnable() {
          public void run() {
            queue.add(future);
          }
        },  sameThreadExecutor());
    return future;
  }
  
  @Beta
  public static ThreadFactory platformThreadFactory() {
    if (!isAppEngine())
      return Executors.defaultThreadFactory(); 
    try {
      return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", new Class[0]).invoke(null, new Object[0]);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (InvocationTargetException e) {
      throw Throwables.propagate(e.getCause());
    } 
  }
  
  private static boolean isAppEngine() {
    if (System.getProperty("com.google.appengine.runtime.environment") == null)
      return false; 
    try {
      return (Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null);
    } catch (ClassNotFoundException e) {
      return false;
    } catch (InvocationTargetException e) {
      return false;
    } catch (IllegalAccessException e) {
      return false;
    } catch (NoSuchMethodException e) {
      return false;
    } 
  }
  
  static Thread newThread(String name, Runnable runnable) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(runnable);
    Thread result = platformThreadFactory().newThread(runnable);
    try {
      result.setName(name);
    } catch (SecurityException e) {}
    return result;
  }
  
  static Executor renamingDecorator(Executor executor, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(executor);
    Preconditions.checkNotNull(nameSupplier);
    if (isAppEngine())
      return executor; 
    return (Executor)new Object(executor, nameSupplier);
  }
  
  static ExecutorService renamingDecorator(ExecutorService service, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(service);
    Preconditions.checkNotNull(nameSupplier);
    if (isAppEngine())
      return service; 
    return (ExecutorService)new Object(service, nameSupplier);
  }
  
  static ScheduledExecutorService renamingDecorator(ScheduledExecutorService service, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(service);
    Preconditions.checkNotNull(nameSupplier);
    if (isAppEngine())
      return service; 
    return (ScheduledExecutorService)new Object(service, nameSupplier);
  }
  
  @Beta
  public static boolean shutdownAndAwaitTermination(ExecutorService service, long timeout, TimeUnit unit) {
    Preconditions.checkNotNull(unit);
    service.shutdown();
    try {
      long halfTimeoutNanos = TimeUnit.NANOSECONDS.convert(timeout, unit) / 2L;
      if (!service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS)) {
        service.shutdownNow();
        service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS);
      } 
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      service.shutdownNow();
    } 
    return service.isTerminated();
  }
  
  private static class MoreExecutors {}
  
  static class MoreExecutors {}
}
