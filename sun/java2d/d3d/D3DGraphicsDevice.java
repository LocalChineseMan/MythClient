package sun.java2d.d3d;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.awt.peer.WindowPeer;
import java.util.ArrayList;
import sun.awt.Win32GraphicsDevice;
import sun.awt.windows.WWindowPeer;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.windows.WindowsFlags;
import sun.misc.PerfCounter;

public class D3DGraphicsDevice extends Win32GraphicsDevice {
  private D3DContext context;
  
  static {
    Toolkit.getDefaultToolkit();
  }
  
  private static boolean d3dAvailable = initD3D();
  
  private ContextCapabilities d3dCaps;
  
  private boolean fsStatus;
  
  private Rectangle ownerOrigBounds;
  
  private boolean ownerWasVisible;
  
  private Window realFSWindow;
  
  private WindowListener fsWindowListener;
  
  private boolean fsWindowWasAlwaysOnTop;
  
  static {
    if (d3dAvailable) {
      pfDisabled = true;
      PerfCounter.getD3DAvailable().set(1L);
    } else {
      PerfCounter.getD3DAvailable().set(0L);
    } 
  }
  
  public static D3DGraphicsDevice createDevice(int paramInt) {
    if (!d3dAvailable)
      return null; 
    ContextCapabilities contextCapabilities = getDeviceCaps(paramInt);
    if ((contextCapabilities.getCaps() & 0x40000) == 0) {
      if (WindowsFlags.isD3DVerbose())
        System.out.println("Could not enable Direct3D pipeline on screen " + paramInt); 
      return null;
    } 
    if (WindowsFlags.isD3DVerbose())
      System.out.println("Direct3D pipeline enabled on screen " + paramInt); 
    return new D3DGraphicsDevice(paramInt, contextCapabilities);
  }
  
  private static ContextCapabilities getDeviceCaps(final int screen) {
    D3DContext.D3DContextCaps d3DContextCaps = null;
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    class Result {
      int caps;
      
      String id;
    };
    try {
      final Result res = new Result();
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() {
              res.caps = D3DGraphicsDevice.getDeviceCapsNative(screen);
              res.id = D3DGraphicsDevice.getDeviceIdNative(screen);
            }
          });
      d3DContextCaps = new D3DContext.D3DContextCaps(result.caps, result.id);
    } finally {
      d3DRenderQueue.unlock();
    } 
    return (d3DContextCaps != null) ? d3DContextCaps : new D3DContext.D3DContextCaps(0, null);
  }
  
  private static class D3DGraphicsDevice {}
  
  public final boolean isCapPresent(int paramInt) {
    return ((this.d3dCaps.getCaps() & paramInt) != 0);
  }
  
  private D3DGraphicsDevice(int paramInt, ContextCapabilities paramContextCapabilities) {
    super(paramInt);
    this.ownerOrigBounds = null;
    this.descString = "D3DGraphicsDevice[screen=" + paramInt;
    this.d3dCaps = paramContextCapabilities;
    this.context = new D3DContext(D3DRenderQueue.getInstance(), this);
  }
  
  public boolean isD3DEnabledOnDevice() {
    return (isValid() && isCapPresent(262144));
  }
  
  public static boolean isD3DAvailable() {
    return d3dAvailable;
  }
  
  private Frame getToplevelOwner(Window paramWindow) {
    Window window = paramWindow;
    while (window != null) {
      window = window.getOwner();
      if (window instanceof Frame)
        return (Frame)window; 
    } 
    return null;
  }
  
  protected void enterFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer) {
    WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, wWindowPeer, paramInt));
    } finally {
      d3DRenderQueue.unlock();
    } 
    if (!this.fsStatus)
      super.enterFullScreenExclusive(paramInt, paramWindowPeer); 
  }
  
  protected void exitFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer) {
    if (this.fsStatus) {
      D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
      d3DRenderQueue.lock();
      try {
        d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, paramInt));
      } finally {
        d3DRenderQueue.unlock();
      } 
    } else {
      super.exitFullScreenExclusive(paramInt, paramWindowPeer);
    } 
  }
  
  protected void addFSWindowListener(Window paramWindow) {
    if (!(paramWindow instanceof Frame) && !(paramWindow instanceof java.awt.Dialog) && (this
      .realFSWindow = getToplevelOwner(paramWindow)) != null) {
      this.ownerOrigBounds = this.realFSWindow.getBounds();
      WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
      this.ownerWasVisible = this.realFSWindow.isVisible();
      Rectangle rectangle = paramWindow.getBounds();
      wWindowPeer.reshape(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      wWindowPeer.setVisible(true);
    } else {
      this.realFSWindow = paramWindow;
    } 
    this.fsWindowWasAlwaysOnTop = this.realFSWindow.isAlwaysOnTop();
    ((WWindowPeer)this.realFSWindow.getPeer()).setAlwaysOnTop(true);
    this.fsWindowListener = new D3DFSWindowAdapter(null);
    this.realFSWindow.addWindowListener(this.fsWindowListener);
  }
  
  protected void removeFSWindowListener(Window paramWindow) {
    this.realFSWindow.removeWindowListener(this.fsWindowListener);
    this.fsWindowListener = null;
    WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
    if (wWindowPeer != null) {
      if (this.ownerOrigBounds != null) {
        if (this.ownerOrigBounds.width == 0)
          this.ownerOrigBounds.width = 1; 
        if (this.ownerOrigBounds.height == 0)
          this.ownerOrigBounds.height = 1; 
        wWindowPeer.reshape(this.ownerOrigBounds.x, this.ownerOrigBounds.y, this.ownerOrigBounds.width, this.ownerOrigBounds.height);
        if (!this.ownerWasVisible)
          wWindowPeer.setVisible(false); 
        this.ownerOrigBounds = null;
      } 
      if (!this.fsWindowWasAlwaysOnTop)
        wWindowPeer.setAlwaysOnTop(false); 
    } 
    this.realFSWindow = null;
  }
  
  protected DisplayMode getCurrentDisplayMode(int paramInt) {
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      Result result = new Result(this);
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, result, paramInt));
      if (result.dm == null)
        return super.getCurrentDisplayMode(paramInt); 
      return result.dm;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  protected void configDisplayMode(int paramInt1, WindowPeer paramWindowPeer, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (!this.fsStatus) {
      super.configDisplayMode(paramInt1, paramWindowPeer, paramInt2, paramInt3, paramInt4, paramInt5);
      return;
    } 
    WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
    if (getFullScreenWindow() != this.realFSWindow) {
      Rectangle rectangle = getDefaultConfiguration().getBounds();
      wWindowPeer.reshape(rectangle.x, rectangle.y, paramInt2, paramInt3);
    } 
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, wWindowPeer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5));
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  protected void enumDisplayModes(int paramInt, ArrayList<DisplayMode> paramArrayList) {
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, paramInt, paramArrayList));
      if (paramArrayList.size() == 0)
        paramArrayList.add(getCurrentDisplayModeNative(paramInt)); 
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public int getAvailableAcceleratedMemory() {
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      Result result = new Result(this);
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(this, result));
      return (int)result.mem;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public GraphicsConfiguration[] getConfigurations() {
    if (this.configs == null && 
      isD3DEnabledOnDevice()) {
      this.defaultConfig = getDefaultConfiguration();
      if (this.defaultConfig != null) {
        this.configs = new GraphicsConfiguration[1];
        this.configs[0] = this.defaultConfig;
        return (GraphicsConfiguration[])this.configs.clone();
      } 
    } 
    return super.getConfigurations();
  }
  
  public GraphicsConfiguration getDefaultConfiguration() {
    if (this.defaultConfig == null)
      if (isD3DEnabledOnDevice()) {
        this.defaultConfig = new D3DGraphicsConfig(this);
      } else {
        this.defaultConfig = super.getDefaultConfiguration();
      }  
    return this.defaultConfig;
  }
  
  public static boolean isD3DAvailableOnDevice(int paramInt) {
    if (!d3dAvailable)
      return false; 
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      Result result = new Result();
      d3DRenderQueue.flushAndInvokeNow((Runnable)new Object(result, paramInt));
      return result.avail;
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  D3DContext getContext() {
    return this.context;
  }
  
  ContextCapabilities getContextCapabilities() {
    return this.d3dCaps;
  }
  
  public void displayChanged() {
    super.displayChanged();
    if (d3dAvailable)
      this.d3dCaps = getDeviceCaps(getScreen()); 
  }
  
  protected void invalidate(int paramInt) {
    super.invalidate(paramInt);
    this.d3dCaps = new D3DContext.D3DContextCaps(0, null);
  }
  
  private static native boolean initD3D();
  
  private static native int getDeviceCapsNative(int paramInt);
  
  private static native String getDeviceIdNative(int paramInt);
  
  private static native boolean enterFullScreenExclusiveNative(int paramInt, long paramLong);
  
  private static native boolean exitFullScreenExclusiveNative(int paramInt);
  
  private static native DisplayMode getCurrentDisplayModeNative(int paramInt);
  
  private static native void configDisplayModeNative(int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private static native void enumDisplayModesNative(int paramInt, ArrayList paramArrayList);
  
  private static native long getAvailableAcceleratedMemoryNative(int paramInt);
  
  private static native boolean isD3DAvailableOnDeviceNative(int paramInt);
}
