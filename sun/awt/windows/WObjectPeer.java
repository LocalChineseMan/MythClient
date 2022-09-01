package sun.awt.windows;

abstract class WObjectPeer {
  long pData;
  
  static {
    initIDs();
  }
  
  boolean destroyed = false;
  
  Object target;
  
  private volatile boolean disposed;
  
  protected Error createError = null;
  
  private final Object stateLock = new Object();
  
  public static WObjectPeer getPeerForTarget(Object paramObject) {
    return (WObjectPeer)WToolkit.targetToPeer(paramObject);
  }
  
  public long getData() {
    return this.pData;
  }
  
  public Object getTarget() {
    return this.target;
  }
  
  public final Object getStateLock() {
    return this.stateLock;
  }
  
  public final void dispose() {
    boolean bool = false;
    synchronized (this) {
      if (!this.disposed)
        this.disposed = bool = true; 
    } 
    if (bool)
      disposeImpl(); 
  }
  
  protected final boolean isDisposed() {
    return this.disposed;
  }
  
  protected abstract void disposeImpl();
  
  private static native void initIDs();
}
