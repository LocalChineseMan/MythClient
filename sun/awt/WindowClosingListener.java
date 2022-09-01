package sun.awt;

import java.awt.event.WindowEvent;

public interface WindowClosingListener {
  RuntimeException windowClosingNotify(WindowEvent paramWindowEvent);
  
  RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent);
}
