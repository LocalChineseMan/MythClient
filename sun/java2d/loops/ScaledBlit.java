package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class ScaledBlit extends GraphicsPrimitive {
  public static final String methodSignature = "ScaledBlit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static ScaledBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (ScaledBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static ScaledBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (ScaledBlit)object; 
    ScaledBlit scaledBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (scaledBlit != null)
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, scaledBlit); 
    return scaledBlit;
  }
  
  protected ScaledBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public ScaledBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  static {
    GraphicsPrimitiveMgr.registerGeneral((GraphicsPrimitive)new ScaledBlit(null, null, null));
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return null;
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceScaledBlit(this);
  }
  
  public native void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  private static class ScaledBlit {}
}
