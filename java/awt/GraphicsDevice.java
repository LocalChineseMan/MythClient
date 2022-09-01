package java.awt;

import java.awt.image.ColorModel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public abstract class GraphicsDevice {
  private Window fullScreenWindow;
  
  private AppContext fullScreenAppContext;
  
  private final Object fsAppContextLock = new Object();
  
  private Rectangle windowedModeBounds;
  
  public static final int TYPE_RASTER_SCREEN = 0;
  
  public static final int TYPE_PRINTER = 1;
  
  public static final int TYPE_IMAGE_BUFFER = 2;
  
  public abstract int getType();
  
  public abstract String getIDstring();
  
  public abstract GraphicsConfiguration[] getConfigurations();
  
  public abstract GraphicsConfiguration getDefaultConfiguration();
  
  public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate paramGraphicsConfigTemplate) {
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    return paramGraphicsConfigTemplate.getBestConfiguration(arrayOfGraphicsConfiguration);
  }
  
  public boolean isFullScreenSupported() {
    return false;
  }
  
  public void setFullScreenWindow(Window paramWindow) {
    if (paramWindow != null) {
      if (paramWindow.getShape() != null)
        paramWindow.setShape((Shape)null); 
      if (paramWindow.getOpacity() < 1.0F)
        paramWindow.setOpacity(1.0F); 
      if (!paramWindow.isOpaque()) {
        Color color = paramWindow.getBackground();
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        paramWindow.setBackground(color);
      } 
      GraphicsConfiguration graphicsConfiguration = paramWindow.getGraphicsConfiguration();
      if (graphicsConfiguration != null && graphicsConfiguration.getDevice() != this && graphicsConfiguration
        .getDevice().getFullScreenWindow() == paramWindow)
        graphicsConfiguration.getDevice().setFullScreenWindow(null); 
    } 
    if (this.fullScreenWindow != null && this.windowedModeBounds != null) {
      if (this.windowedModeBounds.width == 0)
        this.windowedModeBounds.width = 1; 
      if (this.windowedModeBounds.height == 0)
        this.windowedModeBounds.height = 1; 
      this.fullScreenWindow.setBounds(this.windowedModeBounds);
    } 
    synchronized (this.fsAppContextLock) {
      if (paramWindow == null) {
        this.fullScreenAppContext = null;
      } else {
        this.fullScreenAppContext = AppContext.getAppContext();
      } 
      this.fullScreenWindow = paramWindow;
    } 
    if (this.fullScreenWindow != null) {
      this.windowedModeBounds = this.fullScreenWindow.getBounds();
      GraphicsConfiguration graphicsConfiguration = getDefaultConfiguration();
      Rectangle rectangle = graphicsConfiguration.getBounds();
      if (SunToolkit.isDispatchThreadForAppContext(this.fullScreenWindow))
        this.fullScreenWindow.setGraphicsConfiguration(graphicsConfiguration); 
      this.fullScreenWindow.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      this.fullScreenWindow.setVisible(true);
      this.fullScreenWindow.toFront();
    } 
  }
  
  public Window getFullScreenWindow() {
    Window window = null;
    synchronized (this.fsAppContextLock) {
      if (this.fullScreenAppContext == AppContext.getAppContext())
        window = this.fullScreenWindow; 
    } 
    return window;
  }
  
  public boolean isDisplayChangeSupported() {
    return false;
  }
  
  public void setDisplayMode(DisplayMode paramDisplayMode) {
    throw new UnsupportedOperationException("Cannot change display mode");
  }
  
  public DisplayMode getDisplayMode() {
    GraphicsConfiguration graphicsConfiguration = getDefaultConfiguration();
    Rectangle rectangle = graphicsConfiguration.getBounds();
    ColorModel colorModel = graphicsConfiguration.getColorModel();
    return new DisplayMode(rectangle.width, rectangle.height, colorModel.getPixelSize(), 0);
  }
  
  public DisplayMode[] getDisplayModes() {
    return new DisplayMode[] { getDisplayMode() };
  }
  
  public int getAvailableAcceleratedMemory() {
    return -1;
  }
  
  public boolean isWindowTranslucencySupported(WindowTranslucency paramWindowTranslucency) {
    switch (null.$SwitchMap$java$awt$GraphicsDevice$WindowTranslucency[paramWindowTranslucency.ordinal()]) {
      case 1:
        return isWindowShapingSupported();
      case 2:
        return isWindowOpacitySupported();
      case 3:
        return isWindowPerpixelTranslucencySupported();
    } 
    return false;
  }
  
  static boolean isWindowShapingSupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit))
      return false; 
    return ((SunToolkit)toolkit).isWindowShapingSupported();
  }
  
  static boolean isWindowOpacitySupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit))
      return false; 
    return ((SunToolkit)toolkit).isWindowOpacitySupported();
  }
  
  boolean isWindowPerpixelTranslucencySupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit))
      return false; 
    if (!((SunToolkit)toolkit).isWindowTranslucencySupported())
      return false; 
    return (getTranslucencyCapableGC() != null);
  }
  
  GraphicsConfiguration getTranslucencyCapableGC() {
    GraphicsConfiguration graphicsConfiguration = getDefaultConfiguration();
    if (graphicsConfiguration.isTranslucencyCapable())
      return graphicsConfiguration; 
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    for (byte b = 0; b < arrayOfGraphicsConfiguration.length; b++) {
      if (arrayOfGraphicsConfiguration[b].isTranslucencyCapable())
        return arrayOfGraphicsConfiguration[b]; 
    } 
    return null;
  }
  
  public enum GraphicsDevice {
  
  }
}
