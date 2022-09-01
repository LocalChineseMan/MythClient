package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.hw.AccelSurface;

public abstract class BufferedContext {
  public static final int NO_CONTEXT_FLAGS = 0;
  
  public static final int SRC_IS_OPAQUE = 1;
  
  public static final int USE_MASK = 2;
  
  protected RenderQueue rq;
  
  protected RenderBuffer buf;
  
  protected static BufferedContext currentContext;
  
  private AccelSurface validatedSrcData;
  
  private AccelSurface validatedDstData;
  
  private Region validatedClip;
  
  private Composite validatedComp;
  
  private Paint validatedPaint;
  
  private boolean isValidatedPaintJustAColor;
  
  private int validatedRGB;
  
  private int validatedFlags;
  
  private boolean xformInUse;
  
  private AffineTransform transform;
  
  protected BufferedContext(RenderQueue paramRenderQueue) {
    this.rq = paramRenderQueue;
    this.buf = paramRenderQueue.getBuffer();
  }
  
  public static void validateContext(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt) {
    BufferedContext bufferedContext = paramAccelSurface2.getContext();
    bufferedContext.validate(paramAccelSurface1, paramAccelSurface2, paramRegion, paramComposite, paramAffineTransform, paramPaint, paramSunGraphics2D, paramInt);
  }
  
  public static void validateContext(AccelSurface paramAccelSurface) {
    validateContext(paramAccelSurface, paramAccelSurface, null, null, null, null, null, 0);
  }
  
  public void validate(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt) {
    boolean bool1 = false;
    boolean bool2 = false;
    if (!paramAccelSurface2.isValid() || paramAccelSurface2
      .isSurfaceLost() || paramAccelSurface1.isSurfaceLost()) {
      invalidateContext();
      throw new InvalidPipeException("bounds changed or surface lost");
    } 
    if (paramPaint instanceof Color) {
      int i = ((Color)paramPaint).getRGB();
      if (this.isValidatedPaintJustAColor) {
        if (i != this.validatedRGB) {
          this.validatedRGB = i;
          bool2 = true;
        } 
      } else {
        this.validatedRGB = i;
        bool2 = true;
        this.isValidatedPaintJustAColor = true;
      } 
    } else if (this.validatedPaint != paramPaint) {
      bool2 = true;
      this.isValidatedPaintJustAColor = false;
    } 
    if (currentContext != this || paramAccelSurface1 != this.validatedSrcData || paramAccelSurface2 != this.validatedDstData) {
      if (paramAccelSurface2 != this.validatedDstData)
        bool1 = true; 
      if (paramPaint == null)
        bool2 = true; 
      setSurfaces(paramAccelSurface1, paramAccelSurface2);
      currentContext = this;
      this.validatedSrcData = paramAccelSurface1;
      this.validatedDstData = paramAccelSurface2;
    } 
    if (paramRegion != this.validatedClip || bool1) {
      if (paramRegion != null) {
        if (bool1 || this.validatedClip == null || 
          
          !this.validatedClip.isRectangular() || !paramRegion.isRectangular() || paramRegion
          .getLoX() != this.validatedClip.getLoX() || paramRegion
          .getLoY() != this.validatedClip.getLoY() || paramRegion
          .getHiX() != this.validatedClip.getHiX() || paramRegion
          .getHiY() != this.validatedClip.getHiY())
          setClip(paramRegion); 
      } else {
        resetClip();
      } 
      this.validatedClip = paramRegion;
    } 
    if (paramComposite != this.validatedComp || paramInt != this.validatedFlags) {
      if (paramComposite != null) {
        setComposite(paramComposite, paramInt);
      } else {
        resetComposite();
      } 
      bool2 = true;
      this.validatedComp = paramComposite;
      this.validatedFlags = paramInt;
    } 
    boolean bool3 = false;
    if (paramAffineTransform == null) {
      if (this.xformInUse) {
        resetTransform();
        this.xformInUse = false;
        bool3 = true;
      } else if (paramSunGraphics2D != null && !paramSunGraphics2D.transform.equals(this.transform)) {
        bool3 = true;
      } 
      if (paramSunGraphics2D != null && bool3)
        this.transform = new AffineTransform(paramSunGraphics2D.transform); 
    } else {
      setTransform(paramAffineTransform);
      this.xformInUse = true;
      bool3 = true;
    } 
    if (!this.isValidatedPaintJustAColor && bool3)
      bool2 = true; 
    if (bool2) {
      if (paramPaint != null) {
        BufferedPaints.setPaint(this.rq, paramSunGraphics2D, paramPaint, paramInt);
      } else {
        BufferedPaints.resetPaint(this.rq);
      } 
      this.validatedPaint = paramPaint;
    } 
    paramAccelSurface2.markDirty();
  }
  
  public void invalidateSurfaces() {
    this.validatedSrcData = null;
    this.validatedDstData = null;
  }
  
  private void setSurfaces(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2) {
    this.rq.ensureCapacityAndAlignment(20, 4);
    this.buf.putInt(70);
    this.buf.putLong(paramAccelSurface1.getNativeOps());
    this.buf.putLong(paramAccelSurface2.getNativeOps());
  }
  
  private void resetClip() {
    this.rq.ensureCapacity(4);
    this.buf.putInt(55);
  }
  
  private void setClip(Region paramRegion) {
    if (paramRegion.isRectangular()) {
      this.rq.ensureCapacity(20);
      this.buf.putInt(51);
      this.buf.putInt(paramRegion.getLoX()).putInt(paramRegion.getLoY());
      this.buf.putInt(paramRegion.getHiX()).putInt(paramRegion.getHiY());
    } else {
      this.rq.ensureCapacity(28);
      this.buf.putInt(52);
      this.buf.putInt(53);
      int i = this.buf.position();
      this.buf.putInt(0);
      byte b = 0;
      int j = this.buf.remaining() / 16;
      int[] arrayOfInt = new int[4];
      SpanIterator spanIterator = paramRegion.getSpanIterator();
      while (spanIterator.nextSpan(arrayOfInt)) {
        if (j == 0) {
          this.buf.putInt(i, b);
          this.rq.flushNow();
          this.buf.putInt(53);
          i = this.buf.position();
          this.buf.putInt(0);
          b = 0;
          j = this.buf.remaining() / 16;
        } 
        this.buf.putInt(arrayOfInt[0]);
        this.buf.putInt(arrayOfInt[1]);
        this.buf.putInt(arrayOfInt[2]);
        this.buf.putInt(arrayOfInt[3]);
        b++;
        j--;
      } 
      this.buf.putInt(i, b);
      this.rq.ensureCapacity(4);
      this.buf.putInt(54);
    } 
  }
  
  private void resetComposite() {
    this.rq.ensureCapacity(4);
    this.buf.putInt(58);
  }
  
  private void setComposite(Composite paramComposite, int paramInt) {
    if (paramComposite instanceof AlphaComposite) {
      AlphaComposite alphaComposite = (AlphaComposite)paramComposite;
      this.rq.ensureCapacity(16);
      this.buf.putInt(56);
      this.buf.putInt(alphaComposite.getRule());
      this.buf.putFloat(alphaComposite.getAlpha());
      this.buf.putInt(paramInt);
    } else if (paramComposite instanceof XORComposite) {
      int i = ((XORComposite)paramComposite).getXorPixel();
      this.rq.ensureCapacity(8);
      this.buf.putInt(57);
      this.buf.putInt(i);
    } else {
      throw new InternalError("not yet implemented");
    } 
  }
  
  private void resetTransform() {
    this.rq.ensureCapacity(4);
    this.buf.putInt(60);
  }
  
  private void setTransform(AffineTransform paramAffineTransform) {
    this.rq.ensureCapacityAndAlignment(52, 4);
    this.buf.putInt(59);
    this.buf.putDouble(paramAffineTransform.getScaleX());
    this.buf.putDouble(paramAffineTransform.getShearY());
    this.buf.putDouble(paramAffineTransform.getShearX());
    this.buf.putDouble(paramAffineTransform.getScaleY());
    this.buf.putDouble(paramAffineTransform.getTranslateX());
    this.buf.putDouble(paramAffineTransform.getTranslateY());
  }
  
  public void invalidateContext() {
    resetTransform();
    resetComposite();
    resetClip();
    BufferedPaints.resetPaint(this.rq);
    invalidateSurfaces();
    this.validatedComp = null;
    this.validatedClip = null;
    this.validatedPaint = null;
    this.isValidatedPaintJustAColor = false;
    this.xformInUse = false;
  }
  
  public abstract RenderQueue getRenderQueue();
  
  public abstract void saveState();
  
  public abstract void restoreState();
}
