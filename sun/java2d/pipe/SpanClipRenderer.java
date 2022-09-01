package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class SpanClipRenderer implements CompositePipe {
  CompositePipe outpipe;
  
  static Class RegionClass = Region.class;
  
  static Class RegionIteratorClass = RegionIterator.class;
  
  static {
    initIDs(RegionClass, RegionIteratorClass);
  }
  
  public SpanClipRenderer(CompositePipe paramCompositePipe) {
    this.outpipe = paramCompositePipe;
  }
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfint) {
    RegionIterator regionIterator = paramSunGraphics2D.clipRegion.getIterator();
    return new SCRcontext(this, regionIterator, this.outpipe.startSequence(paramSunGraphics2D, paramShape, paramRectangle, paramArrayOfint));
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    return this.outpipe.needTile(sCRcontext.outcontext, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, ShapeSpanIterator paramShapeSpanIterator) {
    renderPathTile(paramObject, paramArrayOfbyte, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    RegionIterator regionIterator = sCRcontext.iterator.createCopy();
    int[] arrayOfInt = sCRcontext.band;
    arrayOfInt[0] = paramInt3;
    arrayOfInt[1] = paramInt4;
    arrayOfInt[2] = paramInt3 + paramInt5;
    arrayOfInt[3] = paramInt4 + paramInt6;
    if (paramArrayOfbyte == null) {
      int i = paramInt5 * paramInt6;
      paramArrayOfbyte = sCRcontext.tile;
      if (paramArrayOfbyte != null && paramArrayOfbyte.length < i)
        paramArrayOfbyte = null; 
      if (paramArrayOfbyte == null) {
        paramArrayOfbyte = new byte[i];
        sCRcontext.tile = paramArrayOfbyte;
      } 
      paramInt1 = 0;
      paramInt2 = paramInt5;
      fillTile(regionIterator, paramArrayOfbyte, paramInt1, paramInt2, arrayOfInt);
    } else {
      eraseTile(regionIterator, paramArrayOfbyte, paramInt1, paramInt2, arrayOfInt);
    } 
    if (arrayOfInt[2] > arrayOfInt[0] && arrayOfInt[3] > arrayOfInt[1]) {
      paramInt1 += (arrayOfInt[1] - paramInt4) * paramInt2 + arrayOfInt[0] - paramInt3;
      this.outpipe.renderPathTile(sCRcontext.outcontext, paramArrayOfbyte, paramInt1, paramInt2, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    } 
  }
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    this.outpipe.skipTile(sCRcontext.outcontext, paramInt1, paramInt2);
  }
  
  public void endSequence(Object paramObject) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    this.outpipe.endSequence(sCRcontext.outcontext);
  }
  
  static native void initIDs(Class paramClass1, Class paramClass2);
  
  public native void fillTile(RegionIterator paramRegionIterator, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int[] paramArrayOfint);
  
  public native void eraseTile(RegionIterator paramRegionIterator, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int[] paramArrayOfint);
  
  class SpanClipRenderer {}
}
