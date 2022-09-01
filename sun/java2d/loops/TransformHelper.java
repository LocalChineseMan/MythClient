package sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class TransformHelper extends GraphicsPrimitive {
  public static final String methodSignature = "TransformHelper(...)"
    .toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache helpercache = new RenderCache(10);
  
  public static TransformHelper locate(SurfaceType paramSurfaceType) {
    return (TransformHelper)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
  }
  
  public static synchronized TransformHelper getFromCache(SurfaceType paramSurfaceType) {
    Object object = helpercache.get(paramSurfaceType, null, null);
    if (object != null)
      return (TransformHelper)object; 
    TransformHelper transformHelper = locate(paramSurfaceType);
    if (transformHelper != null)
      helpercache.put(paramSurfaceType, null, null, transformHelper); 
    return transformHelper;
  }
  
  protected TransformHelper(SurfaceType paramSurfaceType) {
    super(methodSignature, primTypeID, paramSurfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
  }
  
  public TransformHelper(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
  }
  
  public native void Transform(MaskBlit paramMaskBlit, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int[] paramArrayOfint, int paramInt10, int paramInt11);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return null;
  }
  
  public GraphicsPrimitive traceWrap() {
    return (GraphicsPrimitive)new TraceTransformHelper(this);
  }
  
  private static class TransformHelper {}
}
