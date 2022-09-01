package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class MaskFill extends GraphicsPrimitive {
  public static final String methodSignature = "MaskFill(...)".toString();
  
  public static final String fillPgramSignature = "FillAAPgram(...)"
    .toString();
  
  public static final String drawPgramSignature = "DrawAAPgram(...)"
    .toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache fillcache = new RenderCache(10);
  
  public static MaskFill locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (MaskFill)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static MaskFill locatePrim(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (MaskFill)GraphicsPrimitiveMgr.locatePrim(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static MaskFill getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = fillcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (MaskFill)object; 
    MaskFill maskFill = locatePrim(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (maskFill != null)
      fillcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, maskFill); 
    return maskFill;
  }
  
  protected MaskFill(String paramString, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramString, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  protected MaskFill(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public MaskFill(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public boolean canDoParallelograms() {
    return (getNativePrim() != 0L);
  }
  
  static {
    GraphicsPrimitiveMgr.registerGeneral((GraphicsPrimitive)new MaskFill(null, null, null));
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (SurfaceType.OpaqueColor.equals(paramSurfaceType1) || SurfaceType.AnyColor
      .equals(paramSurfaceType1)) {
      if (CompositeType.Xor.equals(paramCompositeType))
        throw new InternalError("Cannot construct MaskFill for XOR mode"); 
      return (GraphicsPrimitive)new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    } 
    throw new InternalError("MaskFill can only fill with colors");
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceMaskFill(this);
  }
  
  public native void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfbyte, int paramInt5, int paramInt6);
  
  public native void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public native void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  private static class MaskFill {}
  
  private static class MaskFill {}
}
