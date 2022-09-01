package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.SurfaceData;

public abstract class SurfaceManager {
  private static ImageAccessor imgaccessor;
  
  private ConcurrentHashMap<Object, Object> cacheMap;
  
  public static interface SurfaceManager {}
  
  public static interface ProxiedGraphicsConfig {
    Object getProxyKey();
  }
  
  class SurfaceManager {}
  
  public static abstract class ImageAccessor {
    public abstract SurfaceManager getSurfaceManager(Image param1Image);
    
    public abstract void setSurfaceManager(Image param1Image, SurfaceManager param1SurfaceManager);
  }
  
  public static void setImageAccessor(ImageAccessor paramImageAccessor) {
    if (imgaccessor != null)
      throw new InternalError("Attempt to set ImageAccessor twice"); 
    imgaccessor = paramImageAccessor;
  }
  
  public static SurfaceManager getManager(Image paramImage) {
    SurfaceManager surfaceManager = imgaccessor.getSurfaceManager(paramImage);
    if (surfaceManager == null)
      try {
        BufferedImage bufferedImage = (BufferedImage)paramImage;
        surfaceManager = new BufImgSurfaceManager(bufferedImage);
        setManager(bufferedImage, surfaceManager);
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException("Invalid Image variant");
      }  
    return surfaceManager;
  }
  
  public static void setManager(Image paramImage, SurfaceManager paramSurfaceManager) {
    imgaccessor.setSurfaceManager(paramImage, paramSurfaceManager);
  }
  
  public Object getCacheData(Object paramObject) {
    return (this.cacheMap == null) ? null : this.cacheMap.get(paramObject);
  }
  
  public void setCacheData(Object paramObject1, Object paramObject2) {
    if (this.cacheMap == null)
      synchronized (this) {
        if (this.cacheMap == null)
          this.cacheMap = new ConcurrentHashMap<>(2); 
      }  
    this.cacheMap.put(paramObject1, paramObject2);
  }
  
  public abstract SurfaceData getPrimarySurfaceData();
  
  public abstract SurfaceData restoreContents();
  
  public void acceleratedSurfaceLost() {}
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration) {
    return new ImageCapabilitiesGc(this, paramGraphicsConfiguration);
  }
  
  public synchronized void flush() {
    flush(false);
  }
  
  synchronized void flush(boolean paramBoolean) {
    if (this.cacheMap != null) {
      Iterator<Object> iterator = this.cacheMap.values().iterator();
      while (iterator.hasNext()) {
        FlushableCacheData flushableCacheData = (FlushableCacheData)iterator.next();
        if (flushableCacheData instanceof FlushableCacheData && (
          (FlushableCacheData)flushableCacheData).flush(paramBoolean))
          iterator.remove(); 
      } 
    } 
  }
  
  public void setAccelerationPriority(float paramFloat) {
    if (paramFloat == 0.0F)
      flush(true); 
  }
  
  public static int getImageScale(Image paramImage) {
    if (!(paramImage instanceof java.awt.image.VolatileImage))
      return 1; 
    SurfaceManager surfaceManager = getManager(paramImage);
    return surfaceManager.getPrimarySurfaceData().getDefaultScale();
  }
}
