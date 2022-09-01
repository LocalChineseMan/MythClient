package java.awt.event;

import java.util.EventListener;

public interface ComponentListener extends EventListener {
  void componentResized(ComponentEvent paramComponentEvent);
  
  void componentMoved(ComponentEvent paramComponentEvent);
  
  void componentShown(ComponentEvent paramComponentEvent);
  
  void componentHidden(ComponentEvent paramComponentEvent);
}
