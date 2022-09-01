package java.util.concurrent;

public interface RejectedExecutionHandler {
  void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor);
}
