package java.util.concurrent;

public interface Future<V> {
  boolean cancel(boolean paramBoolean);
  
  boolean isCancelled();
  
  boolean isDone();
  
  V get() throws InterruptedException, ExecutionException;
  
  V get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException;
}
