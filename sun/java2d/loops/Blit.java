package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class Blit extends GraphicsPrimitive {
  public static final String methodSignature = "Blit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static Blit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return (Blit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public static Blit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (Blit)object; 
    Blit blit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (blit == null) {
      System.out.println("blit loop not found for:");
      System.out.println("src:  " + paramSurfaceType1);
      System.out.println("comp: " + paramCompositeType);
      System.out.println("dst:  " + paramSurfaceType2);
    } else {
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, blit);
    } 
    return blit;
  }
  
  protected Blit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public Blit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  static {
    GraphicsPrimitiveMgr.registerGeneral((GraphicsPrimitive)new Blit(null, null, null));
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (paramCompositeType.isDerivedFrom(CompositeType.Xor)) {
      GeneralXorBlit generalXorBlit = new GeneralXorBlit(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
      setupGeneralBinaryOp(generalXorBlit);
      return (GraphicsPrimitive)generalXorBlit;
    } 
    if (paramCompositeType.isDerivedFrom(CompositeType.AnyAlpha))
      return (GraphicsPrimitive)new GeneralMaskBlit(paramSurfaceType1, paramCompositeType, paramSurfaceType2); 
    return (GraphicsPrimitive)AnyBlit.instance;
  }
  
  private static class Blit {}
  
  private static class Blit {}
  
  private static class GeneralMaskBlit extends Blit {
    MaskBlit performop;
    
    public GeneralMaskBlit(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.performop = MaskBlit.locate(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
    }
    
    public void Blit(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      this.performop.MaskBlit(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, null, 0, 0);
    }
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceBlit(this);
  }
  
  public native void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private static class Blit {}
}
