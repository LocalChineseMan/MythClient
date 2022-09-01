package javax.management;

import java.util.EventListener;

public interface NotificationListener extends EventListener {
  void handleNotification(Notification paramNotification, Object paramObject);
}
