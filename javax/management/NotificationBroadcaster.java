package javax.management;

public interface NotificationBroadcaster {
  void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException;
  
  void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException;
  
  MBeanNotificationInfo[] getNotificationInfo();
}
