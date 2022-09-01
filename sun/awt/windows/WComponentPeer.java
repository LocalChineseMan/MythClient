package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import sun.awt.AWTAccessor;
import sun.awt.CausedFocusEvent;
import sun.awt.PaintEventDispatcher;
import sun.awt.RepaintArea;
import sun.awt.SunToolkit;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsEnvironment;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.util.logging.PlatformLogger;

public abstract class WComponentPeer extends WObjectPeer implements ComponentPeer, DropTargetPeer {
  protected volatile long hwnd;
  
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WComponentPeer");
  
  private static final PlatformLogger shapeLog = PlatformLogger.getLogger("sun.awt.windows.shape.WComponentPeer");
  
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.windows.focus.WComponentPeer");
  
  SurfaceData surfaceData;
  
  private RepaintArea paintArea;
  
  protected Win32GraphicsConfig winGraphicsConfig;
  
  boolean isLayouting = false;
  
  boolean paintPending = false;
  
  int oldWidth = -1;
  
  int oldHeight = -1;
  
  private int numBackBuffers = 0;
  
  private VolatileImage backBuffer = null;
  
  private BufferCapabilities backBufferCaps = null;
  
  private Color foreground;
  
  private Color background;
  
  private Font font;
  
  int nDropTargets;
  
  long nativeDropTargetContext;
  
  public boolean canDetermineObscurity() {
    return true;
  }
  
  public long getHWnd() {
    return this.hwnd;
  }
  
  public void setVisible(boolean paramBoolean) {
    if (paramBoolean) {
      show();
    } else {
      hide();
    } 
  }
  
  public void show() {
    Dimension dimension = ((Component)this.target).getSize();
    this.oldHeight = dimension.height;
    this.oldWidth = dimension.width;
    pShow();
  }
  
  public void setEnabled(boolean paramBoolean) {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    } 
  }
  
  public int serialNum = 0;
  
  private static final double BANDING_DIVISOR = 4.0D;
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this.paintPending = (paramInt3 != this.oldWidth || paramInt4 != this.oldHeight);
    if ((paramInt5 & 0x4000) != 0) {
      reshapeNoCheck(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
    if (paramInt3 != this.oldWidth || paramInt4 != this.oldHeight) {
      try {
        replaceSurfaceData();
      } catch (InvalidPipeException invalidPipeException) {}
      this.oldWidth = paramInt3;
      this.oldHeight = paramInt4;
    } 
    this.serialNum++;
  }
  
  void dynamicallyLayoutContainer() {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      Container container1 = WToolkit.getNativeContainer((Component)this.target);
      if (container1 != null)
        log.fine("Assertion (parent == null) failed"); 
    } 
    Container container = (Container)this.target;
    WToolkit.executeOnEventHandlerThread(container, (Runnable)new Object(this, container));
  }
  
  void paintDamagedAreaImmediately() {
    updateWindow();
    SunToolkit.flushPendingEvents();
    this.paintArea.paint(this.target, shouldClearRectBeforePaint());
  }
  
  public void paint(Graphics paramGraphics) {
    ((Component)this.target).paint(paramGraphics);
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void print(Graphics paramGraphics) {
    Component component = (Component)this.target;
    int i = component.getWidth();
    int j = component.getHeight();
    int k = (int)(j / 4.0D);
    if (k == 0)
      k = j; 
    int m;
    for (m = 0; m < j; m += k) {
      int n = m + k - 1;
      if (n >= j)
        n = j - 1; 
      int i1 = n - m + 1;
      Color color = component.getBackground();
      int[] arrayOfInt = createPrintedPixels(0, m, i, i1, (color == null) ? 255 : color
          .getAlpha());
      if (arrayOfInt != null) {
        BufferedImage bufferedImage = new BufferedImage(i, i1, 2);
        bufferedImage.setRGB(0, 0, i, i1, arrayOfInt, 0, i);
        paramGraphics.drawImage(bufferedImage, 0, m, null);
        bufferedImage.flush();
      } 
    } 
    component.print(paramGraphics);
  }
  
  public void coalescePaintEvent(PaintEvent paramPaintEvent) {
    Rectangle rectangle = paramPaintEvent.getUpdateRect();
    if (!(paramPaintEvent instanceof sun.awt.event.IgnorePaintEvent))
      this.paintArea.add(rectangle, paramPaintEvent.getID()); 
    if (log.isLoggable(PlatformLogger.Level.FINEST))
      switch (paramPaintEvent.getID()) {
        case 801:
          log.finest("coalescePaintEvent: UPDATE: add: x = " + rectangle.x + ", y = " + rectangle.y + ", width = " + rectangle.width + ", height = " + rectangle.height);
          return;
        case 800:
          log.finest("coalescePaintEvent: PAINT: add: x = " + rectangle.x + ", y = " + rectangle.y + ", width = " + rectangle.width + ", height = " + rectangle.height);
          return;
      }  
  }
  
  public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent) {
    return false;
  }
  
  public void handleJavaMouseEvent(MouseEvent paramMouseEvent) {
    switch (paramMouseEvent.getID()) {
      case 501:
        if (this.target == paramMouseEvent.getSource() && 
          !((Component)this.target).isFocusOwner() && 
          WKeyboardFocusManagerPeer.shouldFocusOnClick((Component)this.target))
          WKeyboardFocusManagerPeer.requestFocusFor((Component)this.target, CausedFocusEvent.Cause.MOUSE_EVENT); 
        break;
    } 
  }
  
  public void handleEvent(AWTEvent paramAWTEvent) {
    int i = paramAWTEvent.getID();
    if (paramAWTEvent instanceof InputEvent && !((InputEvent)paramAWTEvent).isConsumed() && ((Component)this.target)
      .isEnabled())
      if (paramAWTEvent instanceof MouseEvent && !(paramAWTEvent instanceof java.awt.event.MouseWheelEvent)) {
        handleJavaMouseEvent((MouseEvent)paramAWTEvent);
      } else if (paramAWTEvent instanceof KeyEvent && 
        handleJavaKeyEvent((KeyEvent)paramAWTEvent)) {
        return;
      }  
    switch (i) {
      case 800:
        this.paintPending = false;
      case 801:
        if (!this.isLayouting && !this.paintPending)
          this.paintArea.paint(this.target, shouldClearRectBeforePaint()); 
        return;
      case 1004:
      case 1005:
        handleJavaFocusEvent((FocusEvent)paramAWTEvent);
        break;
    } 
    nativeHandleEvent(paramAWTEvent);
  }
  
  void handleJavaFocusEvent(FocusEvent paramFocusEvent) {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer(paramFocusEvent.toString()); 
    setFocus((paramFocusEvent.getID() == 1004));
  }
  
  public Dimension getMinimumSize() {
    return ((Component)this.target).getSize();
  }
  
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public void layout() {}
  
  public Rectangle getBounds() {
    return ((Component)this.target).getBounds();
  }
  
  public boolean isFocusable() {
    return false;
  }
  
  public GraphicsConfiguration getGraphicsConfiguration() {
    if (this.winGraphicsConfig != null)
      return this.winGraphicsConfig; 
    return ((Component)this.target).getGraphicsConfiguration();
  }
  
  public SurfaceData getSurfaceData() {
    return this.surfaceData;
  }
  
  public void replaceSurfaceData() {
    replaceSurfaceData(this.numBackBuffers, this.backBufferCaps);
  }
  
  public void createScreenSurface(boolean paramBoolean) {
    Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    ScreenUpdateManager screenUpdateManager = ScreenUpdateManager.getInstance();
    this.surfaceData = screenUpdateManager.createScreenSurface(win32GraphicsConfig, this, this.numBackBuffers, paramBoolean);
  }
  
  public void replaceSurfaceData(int paramInt, BufferCapabilities paramBufferCapabilities) {
    SurfaceData surfaceData = null;
    VolatileImage volatileImage = null;
    synchronized (((Component)this.target).getTreeLock()) {
      synchronized (this) {
        if (this.pData == 0L)
          return; 
        this.numBackBuffers = paramInt;
        ScreenUpdateManager screenUpdateManager = ScreenUpdateManager.getInstance();
        surfaceData = this.surfaceData;
        screenUpdateManager.dropScreenSurface(surfaceData);
        createScreenSurface(true);
        if (surfaceData != null)
          surfaceData.invalidate(); 
        volatileImage = this.backBuffer;
        if (this.numBackBuffers > 0) {
          this.backBufferCaps = paramBufferCapabilities;
          Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
          this.backBuffer = win32GraphicsConfig.createBackBuffer(this);
        } else if (this.backBuffer != null) {
          this.backBufferCaps = null;
          this.backBuffer = null;
        } 
      } 
    } 
    if (surfaceData != null) {
      surfaceData.flush();
      surfaceData = null;
    } 
    if (volatileImage != null) {
      volatileImage.flush();
      surfaceData = null;
    } 
  }
  
  public void replaceSurfaceDataLater() {
    Object object = new Object(this);
    Component component = (Component)this.target;
    if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing(component, (Runnable)object))
      postEvent(new InvocationEvent(component, (Runnable)object)); 
  }
  
  public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) {
    this.winGraphicsConfig = (Win32GraphicsConfig)paramGraphicsConfiguration;
    try {
      replaceSurfaceData();
    } catch (InvalidPipeException invalidPipeException) {}
    return false;
  }
  
  public ColorModel getColorModel() {
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    if (graphicsConfiguration != null)
      return graphicsConfiguration.getColorModel(); 
    return null;
  }
  
  public ColorModel getDeviceColorModel() {
    Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    if (win32GraphicsConfig != null)
      return win32GraphicsConfig.getDeviceColorModel(); 
    return null;
  }
  
  public ColorModel getColorModel(int paramInt) {
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    if (graphicsConfiguration != null)
      return graphicsConfiguration.getColorModel(paramInt); 
    return null;
  }
  
  static final Font defaultFont = new Font("Dialog", 0, 12);
  
  private int updateX1;
  
  private int updateY1;
  
  private int updateX2;
  
  private int updateY2;
  
  private volatile boolean isAccelCapable;
  
  public Graphics getGraphics() {
    if (isDisposed())
      return null; 
    Component component = (Component)getTarget();
    Window window = SunToolkit.getContainingWindow(component);
    if (window != null) {
      Graphics graphics = ((WWindowPeer)window.getPeer()).getTranslucentGraphics();
      if (graphics != null) {
        int i = 0, j = 0;
        for (Component component1 = component; component1 != window; component1 = component1.getParent()) {
          i += component1.getX();
          j += component1.getY();
        } 
        graphics.translate(i, j);
        graphics.clipRect(0, 0, component.getWidth(), component.getHeight());
        return graphics;
      } 
    } 
    SurfaceData surfaceData = this.surfaceData;
    if (surfaceData != null) {
      Color color1 = this.background;
      if (color1 == null)
        color1 = SystemColor.window; 
      Color color2 = this.foreground;
      if (color2 == null)
        color2 = SystemColor.windowText; 
      Font font = this.font;
      if (font == null)
        font = defaultFont; 
      ScreenUpdateManager screenUpdateManager = ScreenUpdateManager.getInstance();
      return screenUpdateManager.createGraphics(surfaceData, this, color2, color1, font);
    } 
    return null;
  }
  
  public FontMetrics getFontMetrics(Font paramFont) {
    return WFontMetrics.getFontMetrics(paramFont);
  }
  
  protected void disposeImpl() {
    SurfaceData surfaceData = this.surfaceData;
    this.surfaceData = null;
    ScreenUpdateManager.getInstance().dropScreenSurface(surfaceData);
    surfaceData.invalidate();
    WToolkit.targetDisposedPeer(this.target, this);
    _dispose();
  }
  
  public void disposeLater() {
    postEvent(new InvocationEvent(this.target, (Runnable)new Object(this)));
  }
  
  public synchronized void setForeground(Color paramColor) {
    this.foreground = paramColor;
    _setForeground(paramColor.getRGB());
  }
  
  public synchronized void setBackground(Color paramColor) {
    this.background = paramColor;
    _setBackground(paramColor.getRGB());
  }
  
  public Color getBackgroundNoSync() {
    return this.background;
  }
  
  public synchronized void setFont(Font paramFont) {
    this.font = paramFont;
    _setFont(paramFont);
  }
  
  public void updateCursorImmediately() {
    WGlobalCursorManager.getCursorManager().updateCursorImmediately();
  }
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause) {
    Window window;
    WWindowPeer wWindowPeer;
    boolean bool;
    if (WKeyboardFocusManagerPeer.processSynchronousLightweightTransfer((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong))
      return true; 
    int i = WKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong, paramCause);
    switch (i) {
      case 0:
        return false;
      case 2:
        if (focusLog.isLoggable(PlatformLogger.Level.FINER))
          focusLog.finer("Proceeding with request to " + paramComponent + " in " + this.target); 
        window = SunToolkit.getContainingWindow((Component)this.target);
        if (window == null)
          return rejectFocusRequestHelper("WARNING: Parent window is null"); 
        wWindowPeer = (WWindowPeer)window.getPeer();
        if (wWindowPeer == null)
          return rejectFocusRequestHelper("WARNING: Parent window's peer is null"); 
        bool = wWindowPeer.requestWindowFocus(paramCause);
        if (focusLog.isLoggable(PlatformLogger.Level.FINER))
          focusLog.finer("Requested window focus: " + bool); 
        if (!bool || !window.isFocused())
          return rejectFocusRequestHelper("Waiting for asynchronous processing of the request"); 
        return WKeyboardFocusManagerPeer.deliverFocus(paramComponent, (Component)this.target, paramBoolean1, paramBoolean2, paramLong, paramCause);
      case 1:
        return true;
    } 
    return false;
  }
  
  private boolean rejectFocusRequestHelper(String paramString) {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer(paramString); 
    WKeyboardFocusManagerPeer.removeLastFocusRequest((Component)this.target);
    return false;
  }
  
  public Image createImage(ImageProducer paramImageProducer) {
    return new ToolkitImage(paramImageProducer);
  }
  
  public Image createImage(int paramInt1, int paramInt2) {
    Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    return win32GraphicsConfig.createAcceleratedImage((Component)this.target, paramInt1, paramInt2);
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2) {
    return new SunVolatileImage((Component)this.target, paramInt1, paramInt2);
  }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    return Toolkit.getDefaultToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    return Toolkit.getDefaultToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public String toString() {
    return getClass().getName() + "[" + this.target + "]";
  }
  
  WComponentPeer getNativeParent() {
    Container container = SunToolkit.getNativeContainer((Component)this.target);
    return (WComponentPeer)WToolkit.targetToPeer(container);
  }
  
  protected void checkCreation() {
    if (this.hwnd == 0L || this.pData == 0L) {
      if (this.createError != null)
        throw this.createError; 
      throw new InternalError("couldn't create component peer");
    } 
  }
  
  void initialize() {
    if (((Component)this.target).isVisible())
      show(); 
    Color color = ((Component)this.target).getForeground();
    if (color != null)
      setForeground(color); 
    Font font = ((Component)this.target).getFont();
    if (font != null)
      setFont(font); 
    if (!((Component)this.target).isEnabled())
      disable(); 
    Rectangle rectangle = ((Component)this.target).getBounds();
    setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 3);
  }
  
  void handleRepaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  void handleExpose(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void handlePaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void postPaintIfNecessary(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint((Component)this.target)) {
      PaintEvent paintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component)this.target, paramInt1, paramInt2, paramInt3, paramInt4);
      if (paintEvent != null)
        postEvent(paintEvent); 
    } 
  }
  
  void postEvent(AWTEvent paramAWTEvent) {
    preprocessPostEvent(paramAWTEvent);
    WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent);
  }
  
  void preprocessPostEvent(AWTEvent paramAWTEvent) {}
  
  public void beginLayout() {
    this.isLayouting = true;
  }
  
  public void endLayout() {
    if (!this.paintArea.isEmpty() && !this.paintPending && 
      !((Component)this.target).getIgnoreRepaint())
      postEvent(new PaintEvent((Component)this.target, 800, new Rectangle())); 
    this.isLayouting = false;
  }
  
  public Dimension preferredSize() {
    return getPreferredSize();
  }
  
  public synchronized void addDropTarget(DropTarget paramDropTarget) {
    if (this.nDropTargets == 0)
      this.nativeDropTargetContext = addNativeDropTarget(); 
    this.nDropTargets++;
  }
  
  public synchronized void removeDropTarget(DropTarget paramDropTarget) {
    this.nDropTargets--;
    if (this.nDropTargets == 0) {
      removeNativeDropTarget();
      this.nativeDropTargetContext = 0L;
    } 
  }
  
  public boolean handlesWheelScrolling() {
    return nativeHandlesWheelScrolling();
  }
  
  public boolean isPaintPending() {
    return (this.paintPending && this.isLayouting);
  }
  
  public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
    Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    win32GraphicsConfig.assertOperationSupported((Component)this.target, paramInt, paramBufferCapabilities);
    try {
      replaceSurfaceData(paramInt - 1, paramBufferCapabilities);
    } catch (InvalidPipeException invalidPipeException) {
      throw new AWTException(invalidPipeException.getMessage());
    } 
  }
  
  public void destroyBuffers() {
    replaceSurfaceData(0, (BufferCapabilities)null);
  }
  
  public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents) {
    VolatileImage volatileImage = this.backBuffer;
    if (volatileImage == null)
      throw new IllegalStateException("Buffers have not been created"); 
    Win32GraphicsConfig win32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    win32GraphicsConfig.flip(this, (Component)this.target, volatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
  }
  
  public synchronized Image getBackBuffer() {
    VolatileImage volatileImage = this.backBuffer;
    if (volatileImage == null)
      throw new IllegalStateException("Buffers have not been created"); 
    return volatileImage;
  }
  
  public BufferCapabilities getBackBufferCaps() {
    return this.backBufferCaps;
  }
  
  public int getBackBuffersNum() {
    return this.numBackBuffers;
  }
  
  public boolean shouldClearRectBeforePaint() {
    return true;
  }
  
  public void reparent(ContainerPeer paramContainerPeer) {
    pSetParent(paramContainerPeer);
  }
  
  public boolean isReparentSupported() {
    return true;
  }
  
  public void setBoundsOperation(int paramInt) {}
  
  WComponentPeer(Component paramComponent) {
    this.isAccelCapable = true;
    this.target = paramComponent;
    this.paintArea = new RepaintArea();
    create(getNativeParent());
    checkCreation();
    createScreenSurface(false);
    initialize();
    start();
  }
  
  public boolean isAccelCapable() {
    if (!this.isAccelCapable || 
      !isContainingTopLevelAccelCapable((Component)this.target))
      return false; 
    boolean bool = SunToolkit.isContainingTopLevelTranslucent((Component)this.target);
    return (!bool || Win32GraphicsEnvironment.isVistaOS());
  }
  
  public void disableAcceleration() {
    this.isAccelCapable = false;
  }
  
  private static final boolean isContainingTopLevelAccelCapable(Component paramComponent) {
    while (paramComponent != null && !(paramComponent instanceof WEmbeddedFrame))
      paramComponent = paramComponent.getParent(); 
    if (paramComponent == null)
      return true; 
    return ((WEmbeddedFramePeer)paramComponent.getPeer()).isAccelCapable();
  }
  
  public void applyShape(Region paramRegion) {
    if (shapeLog.isLoggable(PlatformLogger.Level.FINER))
      shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; TARGET: " + this.target + "; SHAPE: " + paramRegion); 
    if (paramRegion != null) {
      setRectangularShape(paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), 
          paramRegion.isRectangular() ? null : paramRegion);
    } else {
      setRectangularShape(0, 0, 0, 0, (Region)null);
    } 
  }
  
  public void setZOrder(ComponentPeer paramComponentPeer) {
    long l = (paramComponentPeer != null) ? ((WComponentPeer)paramComponentPeer).getHWnd() : 0L;
    setZOrder(l);
  }
  
  public boolean isLightweightFramePeer() {
    return false;
  }
  
  public native boolean isObscured();
  
  private synchronized native void pShow();
  
  synchronized native void hide();
  
  synchronized native void enable();
  
  synchronized native void disable();
  
  public native Point getLocationOnScreen();
  
  private native void reshapeNoCheck(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  synchronized native void updateWindow();
  
  private native int[] createPrintedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public synchronized native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  native void nativeHandleEvent(AWTEvent paramAWTEvent);
  
  native void setFocus(boolean paramBoolean);
  
  private synchronized native void _dispose();
  
  private native void _setForeground(int paramInt);
  
  private native void _setBackground(int paramInt);
  
  synchronized native void _setFont(Font paramFont);
  
  abstract void create(WComponentPeer paramWComponentPeer);
  
  synchronized native void start();
  
  public native void beginValidate();
  
  public native void endValidate();
  
  native long addNativeDropTarget();
  
  native void removeNativeDropTarget();
  
  native boolean nativeHandlesWheelScrolling();
  
  native void pSetParent(ComponentPeer paramComponentPeer);
  
  native void setRectangularShape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion);
  
  private native void setZOrder(long paramLong);
}
