package java.util;

class TimerThread extends Thread {
  boolean newTasksMayBeScheduled = true;
  
  private TaskQueue queue;
  
  TimerThread(TaskQueue paramTaskQueue) {
    this.queue = paramTaskQueue;
  }
  
  public void run() {
    try {
      mainLoop();
    } finally {
      synchronized (this.queue) {
        this.newTasksMayBeScheduled = false;
        this.queue.clear();
      } 
    } 
  }
  
  private void mainLoop() {
    while (true) {
      try {
        TimerTask timerTask;
        boolean bool;
        synchronized (this.queue) {
          long l1, l2;
          while (this.queue.isEmpty() && this.newTasksMayBeScheduled)
            this.queue.wait(); 
          if (this.queue.isEmpty())
            break; 
          timerTask = this.queue.getMin();
          synchronized (timerTask.lock) {
            if (timerTask.state == 3) {
              this.queue.removeMin();
              continue;
            } 
            l1 = System.currentTimeMillis();
            l2 = timerTask.nextExecutionTime;
            if (bool = (l2 <= l1) ? true : false)
              if (timerTask.period == 0L) {
                this.queue.removeMin();
                timerTask.state = 2;
              } else {
                this.queue.rescheduleMin((timerTask.period < 0L) ? (l1 - timerTask.period) : (l2 + timerTask.period));
              }  
          } 
          if (!bool)
            this.queue.wait(l2 - l1); 
        } 
        if (bool)
          timerTask.run(); 
      } catch (InterruptedException interruptedException) {}
    } 
  }
}
