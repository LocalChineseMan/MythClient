package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractExecutorService implements ExecutorService {
  protected <T> RunnableFuture<T> newTaskFor(Runnable paramRunnable, T paramT) {
    return new FutureTask<>(paramRunnable, paramT);
  }
  
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> paramCallable) {
    return new FutureTask<>(paramCallable);
  }
  
  public Future<?> submit(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    RunnableFuture<?> runnableFuture = newTaskFor(paramRunnable, null);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    RunnableFuture<T> runnableFuture = newTaskFor(paramRunnable, paramT);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    RunnableFuture<T> runnableFuture = newTaskFor(paramCallable);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  private <T> T doInvokeAny(Collection<? extends Callable<T>> paramCollection, boolean paramBoolean, long paramLong) throws InterruptedException, ExecutionException, TimeoutException {
    if (paramCollection == null)
      throw new NullPointerException(); 
    int i = paramCollection.size();
    if (i == 0)
      throw new IllegalArgumentException(); 
    ArrayList<Future> arrayList = new ArrayList(i);
    ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(this);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection) throws InterruptedException, ExecutionException {
    try {
      return doInvokeAny(paramCollection, false, 0L);
    } catch (TimeoutException timeoutException) {
      assert false;
      return null;
    } 
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    return doInvokeAny(paramCollection, true, paramTimeUnit.toNanos(paramLong));
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) throws InterruptedException {
    if (paramCollection == null)
      throw new NullPointerException(); 
    ArrayList<RunnableFuture<?>> arrayList = new ArrayList(paramCollection.size());
    boolean bool = false;
    try {
      for (Callable<?> callable : paramCollection) {
        RunnableFuture<?> runnableFuture = newTaskFor(callable);
        arrayList.add(runnableFuture);
        execute(runnableFuture);
      } 
      int i;
      for (byte b = 0; b < i; b++) {
        Future future = arrayList.get(b);
        if (!future.isDone())
          try {
            future.get();
          } catch (CancellationException cancellationException) {
          
          } catch (ExecutionException executionException) {} 
      } 
      bool = true;
      return (List)arrayList;
    } finally {
      if (!bool) {
        byte b;
        int i;
        for (b = 0, i = arrayList.size(); b < i; b++)
          ((Future)arrayList.get(b)).cancel(true); 
      } 
    } 
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramCollection == null)
      throw new NullPointerException(); 
    long l = paramTimeUnit.toNanos(paramLong);
    ArrayList<Runnable> arrayList = new ArrayList(paramCollection.size());
    boolean bool = false;
    try {
      for (Callable<?> callable : paramCollection)
        arrayList.add(newTaskFor(callable)); 
      long l1 = System.nanoTime() + l;
      int i = arrayList.size();
      byte b;
      for (b = 0; b < i; b++) {
        execute(arrayList.get(b));
        l = l1 - System.nanoTime();
        if (l <= 0L)
          return (List)arrayList; 
      } 
      for (b = 0; b < i; b++) {
        Future future = (Future)arrayList.get(b);
        if (!future.isDone()) {
          if (l <= 0L)
            return (List)arrayList; 
          try {
            future.get(l, TimeUnit.NANOSECONDS);
          } catch (CancellationException cancellationException) {
          
          } catch (ExecutionException executionException) {
          
          } catch (TimeoutException timeoutException) {
            return (List)arrayList;
          } 
          l = l1 - System.nanoTime();
        } 
      } 
      bool = true;
      return (List)arrayList;
    } finally {
      if (!bool) {
        byte b;
        int i;
        for (b = 0, i = arrayList.size(); b < i; b++)
          ((Future)arrayList.get(b)).cancel(true); 
      } 
    } 
  }
}
