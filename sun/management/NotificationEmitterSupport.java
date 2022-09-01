package sun.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

abstract class NotificationEmitterSupport implements NotificationEmitter {
  private Object listenerLock = new Object();
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    if (paramNotificationListener == null)
      throw new IllegalArgumentException("Listener can't be null"); 
    synchronized (this.listenerLock) {
      ArrayList<ListenerInfo> arrayList = new ArrayList(this.listenerList.size() + 1);
      arrayList.addAll(this.listenerList);
      arrayList.add(new ListenerInfo(this, paramNotificationListener, paramNotificationFilter, paramObject));
      this.listenerList = arrayList;
    } 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    synchronized (this.listenerLock) {
      ArrayList<ListenerInfo> arrayList = new ArrayList<>(this.listenerList);
      for (int i = arrayList.size() - 1; i >= 0; i--) {
        ListenerInfo listenerInfo = arrayList.get(i);
        if (listenerInfo.listener == paramNotificationListener)
          arrayList.remove(i); 
      } 
      if (arrayList.size() == this.listenerList.size())
        throw new ListenerNotFoundException("Listener not registered"); 
      this.listenerList = arrayList;
    } 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {
    boolean bool = false;
    synchronized (this.listenerLock) {
      ArrayList<ListenerInfo> arrayList = new ArrayList<>(this.listenerList);
      int i = arrayList.size();
      for (byte b = 0; b < i; b++) {
        ListenerInfo listenerInfo = arrayList.get(b);
        if (listenerInfo.listener == paramNotificationListener) {
          bool = true;
          if (listenerInfo.filter == paramNotificationFilter && listenerInfo.handback == paramObject) {
            arrayList.remove(b);
            this.listenerList = arrayList;
            return;
          } 
        } 
      } 
    } 
    if (bool)
      throw new ListenerNotFoundException("Listener not registered with this filter and handback"); 
    throw new ListenerNotFoundException("Listener not registered");
  }
  
  void sendNotification(Notification paramNotification) {
    List<ListenerInfo> list;
    if (paramNotification == null)
      return; 
    synchronized (this.listenerLock) {
      list = this.listenerList;
    } 
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      ListenerInfo listenerInfo = list.get(b);
      if (listenerInfo.filter == null || listenerInfo.filter
        .isNotificationEnabled(paramNotification))
        try {
          listenerInfo.listener.handleNotification(paramNotification, listenerInfo.handback);
        } catch (Exception exception) {
          exception.printStackTrace();
          throw new AssertionError("Error in invoking listener");
        }  
    } 
  }
  
  boolean hasListeners() {
    synchronized (this.listenerLock) {
      return !this.listenerList.isEmpty();
    } 
  }
  
  private List<ListenerInfo> listenerList = Collections.emptyList();
  
  public abstract MBeanNotificationInfo[] getNotificationInfo();
  
  private class NotificationEmitterSupport {}
}
