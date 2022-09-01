package java.lang;

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
  protected T childValue(T paramT) {
    return paramT;
  }
  
  ThreadLocal.ThreadLocalMap getMap(Thread paramThread) {
    return paramThread.inheritableThreadLocals;
  }
  
  void createMap(Thread paramThread, T paramT) {
    paramThread.inheritableThreadLocals = new ThreadLocal.ThreadLocalMap(this, paramT);
  }
}
