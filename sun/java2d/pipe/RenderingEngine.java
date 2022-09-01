package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import sun.awt.geom.PathConsumer2D;
import sun.security.action.GetPropertyAction;

public abstract class RenderingEngine {
  private static RenderingEngine reImpl;
  
  public static synchronized RenderingEngine getInstance() {
    if (reImpl != null)
      return reImpl; 
    reImpl = AccessController.<RenderingEngine>doPrivileged(new PrivilegedAction<RenderingEngine>() {
          public RenderingEngine run() {
            String str = System.getProperty("sun.java2d.renderer", "sun.dc.DuctusRenderingEngine");
            if (str.equals("sun.dc.DuctusRenderingEngine"))
              try {
                Class<?> clazz = Class.forName("sun.dc.DuctusRenderingEngine");
                return (RenderingEngine)clazz.newInstance();
              } catch (ReflectiveOperationException reflectiveOperationException) {} 
            ServiceLoader<RenderingEngine> serviceLoader = ServiceLoader.loadInstalled(RenderingEngine.class);
            RenderingEngine renderingEngine = null;
            for (RenderingEngine renderingEngine1 : serviceLoader) {
              renderingEngine = renderingEngine1;
              if (renderingEngine1.getClass().getName().equals(str))
                break; 
            } 
            return renderingEngine;
          }
        });
    if (reImpl == null)
      throw new InternalError("No RenderingEngine module found"); 
    GetPropertyAction getPropertyAction = new GetPropertyAction("sun.java2d.renderer.trace");
    String str = AccessController.<String>doPrivileged(getPropertyAction);
    if (str != null)
      reImpl = new Tracer(reImpl); 
    return reImpl;
  }
  
  public abstract Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOffloat, float paramFloat3);
  
  public abstract void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D);
  
  public abstract AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfint);
  
  public abstract AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfint);
  
  public abstract float getMinimumAAPenSize();
  
  static class RenderingEngine {}
  
  public static void feedConsumer(PathIterator paramPathIterator, PathConsumer2D paramPathConsumer2D) {
    float[] arrayOfFloat = new float[6];
    while (!paramPathIterator.isDone()) {
      switch (paramPathIterator.currentSegment(arrayOfFloat)) {
        case 0:
          paramPathConsumer2D.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 1:
          paramPathConsumer2D.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2:
          paramPathConsumer2D.quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3:
          paramPathConsumer2D.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4:
          paramPathConsumer2D.closePath();
          break;
      } 
      paramPathIterator.next();
    } 
  }
}
