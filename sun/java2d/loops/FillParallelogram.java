package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillParallelogram extends GraphicsPrimitive {
  public static final String methodSignature = "FillParallelogram(...)"
    .toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillParallelogram locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (FillParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected FillParallelogram(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public FillParallelogram(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void FillParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    throw new InternalError("FillParallelogram not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceFillParallelogram(this);
  }
  
  private static class FillParallelogram {}
}
