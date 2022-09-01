package javax.management;

import com.sun.jmx.remote.util.ClassLogger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class NotificationBroadcasterSupport implements NotificationEmitter {
  private List<ListenerInfo> listenerList;
  
  private final Executor executor;
  
  private final MBeanNotificationInfo[] notifInfo;
  
  public NotificationBroadcasterSupport() {
    this(null, (MBeanNotificationInfo[])null);
  }
  
  public NotificationBroadcasterSupport(Executor paramExecutor) {
    this(paramExecutor, (MBeanNotificationInfo[])null);
  }
  
  public NotificationBroadcasterSupport(MBeanNotificationInfo... paramVarArgs) {
    this(null, paramVarArgs);
  }
  
  public NotificationBroadcasterSupport(Executor paramExecutor, MBeanNotificationInfo... paramVarArgs) {
    this.listenerList = new CopyOnWriteArrayList<>();
    this.executor = (paramExecutor != null) ? paramExecutor : defaultExecutor;
    this.notifInfo = (paramVarArgs == null) ? NO_NOTIFICATION_INFO : (MBeanNotificationInfo[])paramVarArgs.clone();
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    if (paramNotificationListener == null)
      throw new IllegalArgumentException("Listener can't be null"); 
    this.listenerList.add(new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject));
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    WildcardListenerInfo wildcardListenerInfo = new WildcardListenerInfo(paramNotificationListener);
    boolean bool = this.listenerList.removeAll(Collections.singleton(wildcardListenerInfo));
    if (!bool)
      throw new ListenerNotFoundException("Listener not registered"); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {
    ListenerInfo listenerInfo = new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool = this.listenerList.remove(listenerInfo);
    if (!bool)
      throw new ListenerNotFoundException("Listener not registered (with this filter and handback)"); 
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (this.notifInfo.length == 0)
      return this.notifInfo; 
    return (MBeanNotificationInfo[])this.notifInfo.clone();
  }
  
  public void sendNotification(Notification paramNotification) {
    if (paramNotification == null)
      return; 
    for (ListenerInfo listenerInfo : this.listenerList) {
      boolean bool;
      try {
        bool = (listenerInfo.filter == null || listenerInfo.filter.isNotificationEnabled(paramNotification)) ? true : false;
      } catch (Exception exception) {
        if (logger.debugOn())
          logger.debug("sendNotification", exception); 
        continue;
      } 
      if (bool)
        this.executor.execute(new SendNotifJob(this, paramNotification, listenerInfo)); 
    } 
  }
  
  protected void handleNotification(NotificationListener paramNotificationListener, Notification paramNotification, Object paramObject) {
    paramNotificationListener.handleNotification(paramNotification, paramObject);
  }
  
  private static final Executor defaultExecutor = new Executor() {
      public void execute(Runnable param1Runnable) {
        param1Runnable.run();
      }
    };
  
  private class NotificationBroadcasterSupport {}
  
  private static class NotificationBroadcasterSupport {}
  
  private static class NotificationBroadcasterSupport {}
  
  private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
  
  private static final ClassLogger logger = new ClassLogger("javax.management", "NotificationBroadcasterSupport");
}
