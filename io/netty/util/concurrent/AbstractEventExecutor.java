package io.netty.util.concurrent;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {
  public EventExecutor next() {
    return (EventExecutor)this;
  }
  
  public boolean inEventLoop() {
    return inEventLoop(Thread.currentThread());
  }
  
  public Iterator<EventExecutor> iterator() {
    return (Iterator<EventExecutor>)new EventExecutorIterator(this, null);
  }
  
  public Future<?> shutdownGracefully() {
    return shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
  }
  
  @Deprecated
  public List<Runnable> shutdownNow() {
    shutdown();
    return Collections.emptyList();
  }
  
  public <V> Promise<V> newPromise() {
    return new DefaultPromise<V>((EventExecutor)this);
  }
  
  public <V> ProgressivePromise<V> newProgressivePromise() {
    return (ProgressivePromise<V>)new DefaultProgressivePromise((EventExecutor)this);
  }
  
  public <V> Future<V> newSucceededFuture(V result) {
    return (Future<V>)new SucceededFuture((EventExecutor)this, result);
  }
  
  public <V> Future<V> newFailedFuture(Throwable cause) {
    return new FailedFuture<V>((EventExecutor)this, cause);
  }
  
  public Future<?> submit(Runnable task) {
    return (Future)super.submit(task);
  }
  
  public <T> Future<T> submit(Runnable task, T result) {
    return (Future<T>)super.<T>submit(task, result);
  }
  
  public <T> Future<T> submit(Callable<T> task) {
    return (Future<T>)super.<T>submit(task);
  }
  
  protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return new PromiseTask<T>((EventExecutor)this, runnable, value);
  }
  
  protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    return new PromiseTask<T>((EventExecutor)this, callable);
  }
  
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
  
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public abstract void shutdown();
  
  private final class AbstractEventExecutor {}
}
