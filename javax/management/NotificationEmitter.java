package javax.management;

public interface NotificationEmitter extends NotificationBroadcaster {
  void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException;
}
