package sun.java2d;

import java.awt.GraphicsConfiguration;
import sun.awt.image.BufImgVolatileSurfaceManager;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.d3d.D3DVolatileSurfaceManager;
import sun.java2d.opengl.WGLVolatileSurfaceManager;

public class WindowsSurfaceManagerFactory extends SurfaceManagerFactory {
  public VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject) {
    GraphicsConfiguration graphicsConfiguration = paramSunVolatileImage.getGraphicsConfig();
    if (graphicsConfiguration instanceof sun.java2d.d3d.D3DGraphicsConfig)
      return new D3DVolatileSurfaceManager(paramSunVolatileImage, paramObject); 
    if (graphicsConfiguration instanceof sun.java2d.opengl.WGLGraphicsConfig)
      return new WGLVolatileSurfaceManager(paramSunVolatileImage, paramObject); 
    return new BufImgVolatileSurfaceManager(paramSunVolatileImage, paramObject);
  }
}
