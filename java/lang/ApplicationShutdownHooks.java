package java.lang;

import java.util.IdentityHashMap;
import java.util.Set;

class ApplicationShutdownHooks {
  private static IdentityHashMap<Thread, Thread> hooks;
  
  static {
    try {
      Shutdown.add(1, false, new Runnable() {
            public void run() {
              ApplicationShutdownHooks.runHooks();
            }
          });
      hooks = new IdentityHashMap<>();
    } catch (IllegalStateException illegalStateException) {
      hooks = null;
    } 
  }
  
  static synchronized void add(Thread paramThread) {
    if (hooks == null)
      throw new IllegalStateException("Shutdown in progress"); 
    if (paramThread.isAlive())
      throw new IllegalArgumentException("Hook already running"); 
    if (hooks.containsKey(paramThread))
      throw new IllegalArgumentException("Hook previously registered"); 
    hooks.put(paramThread, paramThread);
  }
  
  static synchronized boolean remove(Thread paramThread) {
    if (hooks == null)
      throw new IllegalStateException("Shutdown in progress"); 
    if (paramThread == null)
      throw new NullPointerException(); 
    return (hooks.remove(paramThread) != null);
  }
  
  static void runHooks() {
    Set<Thread> set;
    synchronized (ApplicationShutdownHooks.class) {
      set = hooks.keySet();
      hooks = null;
    } 
    for (Thread thread : set)
      thread.start(); 
    for (Thread thread : set) {
      try {
        thread.join();
      } catch (InterruptedException interruptedException) {}
    } 
  }
}
