package java.awt;

import java.awt.image.BufferStrategy;
import java.awt.peer.CanvasPeer;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;

public class Canvas extends Component implements Accessible {
  private static final String base = "canvas";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -2284879212465893870L;
  
  public Canvas() {}
  
  public Canvas(GraphicsConfiguration paramGraphicsConfiguration) {
    this();
    setGraphicsConfiguration(paramGraphicsConfiguration);
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) {
    synchronized (getTreeLock()) {
      CanvasPeer canvasPeer = (CanvasPeer)getPeer();
      if (canvasPeer != null)
        paramGraphicsConfiguration = canvasPeer.getAppropriateGraphicsConfiguration(paramGraphicsConfiguration); 
      super.setGraphicsConfiguration(paramGraphicsConfiguration);
    } 
  }
  
  String constructComponentName() {
    synchronized (Canvas.class) {
      return "canvas" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createCanvas(this); 
      super.addNotify();
    } 
  }
  
  public void paint(Graphics paramGraphics) {
    paramGraphics.clearRect(0, 0, this.width, this.height);
  }
  
  public void update(Graphics paramGraphics) {
    paramGraphics.clearRect(0, 0, this.width, this.height);
    paint(paramGraphics);
  }
  
  boolean postsOldMouseEvents() {
    return true;
  }
  
  public void createBufferStrategy(int paramInt) {
    super.createBufferStrategy(paramInt);
  }
  
  public void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
    super.createBufferStrategy(paramInt, paramBufferCapabilities);
  }
  
  public BufferStrategy getBufferStrategy() {
    return super.getBufferStrategy();
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTCanvas(this); 
    return this.accessibleContext;
  }
  
  protected class Canvas {}
}
