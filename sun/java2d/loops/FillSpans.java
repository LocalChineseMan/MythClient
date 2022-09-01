package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

public class FillSpans extends GraphicsPrimitive {
  public static final String methodSignature = "FillSpans(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillSpans locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (FillSpans)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected FillSpans(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public FillSpans(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  private native void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt, long paramLong, SpanIterator paramSpanIterator);
  
  public void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, SpanIterator paramSpanIterator) {
    FillSpans(paramSunGraphics2D, paramSurfaceData, paramSunGraphics2D.pixel, paramSpanIterator.getNativeIterator(), paramSpanIterator);
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    throw new InternalError("FillSpans not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceFillSpans(this);
  }
  
  private static class FillSpans {}
}
