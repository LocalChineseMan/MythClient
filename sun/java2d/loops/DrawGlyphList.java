package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawGlyphList extends GraphicsPrimitive {
  public static final String methodSignature = "DrawGlyphList(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphList locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (DrawGlyphList)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected DrawGlyphList(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public DrawGlyphList(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  static {
    GraphicsPrimitiveMgr.registerGeneral((GraphicsPrimitive)new DrawGlyphList(null, null, null));
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (GraphicsPrimitive)new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceDrawGlyphList(this);
  }
  
  public native void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  private static class DrawGlyphList {}
  
  private static class DrawGlyphList {}
}
