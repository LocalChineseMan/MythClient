package java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Condition {
  void await() throws InterruptedException;
  
  void awaitUninterruptibly();
  
  long awaitNanos(long paramLong) throws InterruptedException;
  
  boolean await(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  boolean awaitUntil(Date paramDate) throws InterruptedException;
  
  void signal();
  
  void signalAll();
}
