package notthatuwu.xyz.mythrecode.api.event;

import java.lang.reflect.InvocationTargetException;

public abstract class Event {
  private boolean cancelled;
  
  public Event call() {
    this.cancelled = false;
    call(this);
    return this;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  private static final void call(Event event) {
    ArrayHelper<Data> dataList = EventManager.get((Class)event.getClass());
    if (dataList != null)
      for (Data data : dataList) {
        try {
          data.target.invoke(data.source, new Object[] { event });
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        } 
      }  
  }
  
  public enum Event {
  
  }
}
