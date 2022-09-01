package sun.java2d;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public abstract class SurfaceManagerFactory {
  private static SurfaceManagerFactory instance;
  
  public static synchronized SurfaceManagerFactory getInstance() {
    if (instance == null)
      throw new IllegalStateException("No SurfaceManagerFactory set."); 
    return instance;
  }
  
  public static synchronized void setInstance(SurfaceManagerFactory paramSurfaceManagerFactory) {
    if (paramSurfaceManagerFactory == null)
      throw new IllegalArgumentException("factory must be non-null"); 
    if (instance != null)
      throw new IllegalStateException("The surface manager factory is already initialized"); 
    instance = paramSurfaceManagerFactory;
  }
  
  public abstract VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject);
}
