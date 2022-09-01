package java.util;

public abstract class EventListenerProxy<T extends EventListener> implements EventListener {
  private final T listener;
  
  public EventListenerProxy(T paramT) {
    this.listener = paramT;
  }
  
  public T getListener() {
    return this.listener;
  }
}
