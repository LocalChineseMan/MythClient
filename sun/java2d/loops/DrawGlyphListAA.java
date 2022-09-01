package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawGlyphListAA extends GraphicsPrimitive {
  public static final String methodSignature = "DrawGlyphListAA(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphListAA locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (DrawGlyphListAA)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawGlyphListAA(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawGlyphListAA(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  static {
    GraphicsPrimitiveMgr.registerGeneral((GraphicsPrimitive)new DrawGlyphListAA(null, null, null));
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (GraphicsPrimitive)new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceDrawGlyphListAA(this);
  }
  
  public native void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  private static class DrawGlyphListAA {}
  
  public static class DrawGlyphListAA {}
}
