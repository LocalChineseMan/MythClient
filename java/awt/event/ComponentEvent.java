package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Rectangle;

public class ComponentEvent extends AWTEvent {
  public static final int COMPONENT_FIRST = 100;
  
  public static final int COMPONENT_LAST = 103;
  
  public static final int COMPONENT_MOVED = 100;
  
  public static final int COMPONENT_RESIZED = 101;
  
  public static final int COMPONENT_SHOWN = 102;
  
  public static final int COMPONENT_HIDDEN = 103;
  
  private static final long serialVersionUID = 8101406823902992965L;
  
  public ComponentEvent(Component paramComponent, int paramInt) {
    super(paramComponent, paramInt);
  }
  
  public Component getComponent() {
    return (this.source instanceof Component) ? (Component)this.source : null;
  }
  
  public String paramString() {
    Rectangle rectangle = (this.source != null) ? ((Component)this.source).getBounds() : null;
    switch (this.id) {
      case 102:
        str = "COMPONENT_SHOWN";
        return str;
      case 103:
        str = "COMPONENT_HIDDEN";
        return str;
      case 100:
        str = "COMPONENT_MOVED (" + rectangle.x + "," + rectangle.y + " " + rectangle.width + "x" + rectangle.height + ")";
        return str;
      case 101:
        str = "COMPONENT_RESIZED (" + rectangle.x + "," + rectangle.y + " " + rectangle.width + "x" + rectangle.height + ")";
        return str;
    } 
    String str = "unknown type";
    return str;
  }
}
