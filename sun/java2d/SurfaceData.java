package sun.java2d;

import java.awt.AWTPermission;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.security.Permission;
import sun.awt.image.SurfaceManager;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.DrawGlyphList;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.loops.DrawLine;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.DrawPath;
import sun.java2d.loops.DrawPolygons;
import sun.java2d.loops.DrawRect;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillPath;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.FillSpans;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderCache;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.AAShapePipe;
import sun.java2d.pipe.AATextRenderer;
import sun.java2d.pipe.AlphaColorPipe;
import sun.java2d.pipe.AlphaPaintPipe;
import sun.java2d.pipe.CompositePipe;
import sun.java2d.pipe.DrawImage;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.GeneralCompositePipe;
import sun.java2d.pipe.LCDTextRenderer;
import sun.java2d.pipe.LoopBasedPipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.OutlineTextRenderer;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.SolidTextRenderer;
import sun.java2d.pipe.SpanClipRenderer;
import sun.java2d.pipe.SpanShapeRenderer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.TextRenderer;

public abstract class SurfaceData implements Transparency, DisposerTarget, StateTrackable, Surface {
  private long pData;
  
  private boolean valid;
  
  private boolean surfaceLost;
  
  private SurfaceType surfaceType;
  
  private ColorModel colorModel;
  
  private Object disposerReferent = new Object();
  
  private Object blitProxyKey;
  
  private StateTrackableDelegate stateDelegate;
  
  static {
    initIDs();
  }
  
  protected SurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel) {
    this(StateTrackable.State.STABLE, paramSurfaceType, paramColorModel);
  }
  
  protected SurfaceData(StateTrackable.State paramState, SurfaceType paramSurfaceType, ColorModel paramColorModel) {
    this(StateTrackableDelegate.createInstance(paramState), paramSurfaceType, paramColorModel);
  }
  
  protected SurfaceData(StateTrackableDelegate paramStateTrackableDelegate, SurfaceType paramSurfaceType, ColorModel paramColorModel) {
    this.stateDelegate = paramStateTrackableDelegate;
    this.colorModel = paramColorModel;
    this.surfaceType = paramSurfaceType;
    this.valid = true;
  }
  
  protected SurfaceData(StateTrackable.State paramState) {
    this.stateDelegate = StateTrackableDelegate.createInstance(paramState);
    this.valid = true;
  }
  
  protected void setBlitProxyKey(Object paramObject) {
    if (SurfaceDataProxy.isCachingAllowed())
      this.blitProxyKey = paramObject; 
  }
  
  public SurfaceData getSourceSurfaceData(Image paramImage, int paramInt, CompositeType paramCompositeType, Color paramColor) {
    SurfaceManager surfaceManager = SurfaceManager.getManager(paramImage);
    SurfaceData surfaceData = surfaceManager.getPrimarySurfaceData();
    if (paramImage.getAccelerationPriority() > 0.0F && this.blitProxyKey != null) {
      SurfaceDataProxy surfaceDataProxy = (SurfaceDataProxy)surfaceManager.getCacheData(this.blitProxyKey);
      if (surfaceDataProxy == null || !surfaceDataProxy.isValid()) {
        if (surfaceData.getState() == StateTrackable.State.UNTRACKABLE) {
          surfaceDataProxy = SurfaceDataProxy.UNCACHED;
        } else {
          surfaceDataProxy = makeProxyFor(surfaceData);
        } 
        surfaceManager.setCacheData(this.blitProxyKey, surfaceDataProxy);
      } 
      surfaceData = surfaceDataProxy.replaceData(surfaceData, paramInt, paramCompositeType, paramColor);
    } 
    return surfaceData;
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData) {
    return SurfaceDataProxy.UNCACHED;
  }
  
  public static SurfaceData getPrimarySurfaceData(Image paramImage) {
    SurfaceManager surfaceManager = SurfaceManager.getManager(paramImage);
    return surfaceManager.getPrimarySurfaceData();
  }
  
  public static SurfaceData restoreContents(Image paramImage) {
    SurfaceManager surfaceManager = SurfaceManager.getManager(paramImage);
    return surfaceManager.restoreContents();
  }
  
  public StateTrackable.State getState() {
    return this.stateDelegate.getState();
  }
  
  public StateTracker getStateTracker() {
    return this.stateDelegate.getStateTracker();
  }
  
  public final void markDirty() {
    this.stateDelegate.markDirty();
  }
  
  public void setSurfaceLost(boolean paramBoolean) {
    this.surfaceLost = paramBoolean;
    this.stateDelegate.markDirty();
  }
  
  public boolean isSurfaceLost() {
    return this.surfaceLost;
  }
  
  public final boolean isValid() {
    return this.valid;
  }
  
  public Object getDisposerReferent() {
    return this.disposerReferent;
  }
  
  public long getNativeOps() {
    return this.pData;
  }
  
  public void invalidate() {
    this.valid = false;
    this.stateDelegate.markDirty();
  }
  
  static class PixelToShapeLoopConverter extends PixelToShapeConverter implements LoopBasedPipe {
    public PixelToShapeLoopConverter(ShapeDrawPipe param1ShapeDrawPipe) {
      super(param1ShapeDrawPipe);
    }
  }
  
  static class PixelToPgramLoopConverter extends PixelToParallelogramConverter implements LoopBasedPipe {
    public PixelToPgramLoopConverter(ShapeDrawPipe param1ShapeDrawPipe, ParallelogramPipe param1ParallelogramPipe, double param1Double1, double param1Double2, boolean param1Boolean) {
      super(param1ShapeDrawPipe, param1ParallelogramPipe, param1Double1, param1Double2, param1Boolean);
    }
  }
  
  private static PixelToParallelogramConverter makeConverter(AAShapePipe paramAAShapePipe, ParallelogramPipe paramParallelogramPipe) {
    return new PixelToParallelogramConverter(paramAAShapePipe, paramParallelogramPipe, 0.125D, 0.499D, false);
  }
  
  private static PixelToParallelogramConverter makeConverter(AAShapePipe paramAAShapePipe) {
    return makeConverter(paramAAShapePipe, paramAAShapePipe);
  }
  
  protected static final LoopPipe colorPrimitives = new LoopPipe();
  
  public static final TextPipe outlineTextRenderer = new OutlineTextRenderer();
  
  public static final TextPipe solidTextRenderer = new SolidTextRenderer();
  
  public static final TextPipe aaTextRenderer = new AATextRenderer();
  
  public static final TextPipe lcdTextRenderer = new LCDTextRenderer();
  
  protected static final AlphaColorPipe colorPipe = new AlphaColorPipe();
  
  protected static final PixelToShapeConverter colorViaShape = new PixelToShapeLoopConverter(colorPrimitives);
  
  protected static final PixelToParallelogramConverter colorViaPgram = new PixelToPgramLoopConverter(colorPrimitives, colorPrimitives, 1.0D, 0.25D, true);
  
  protected static final TextPipe colorText = new TextRenderer(colorPipe);
  
  protected static final CompositePipe clipColorPipe = new SpanClipRenderer(colorPipe);
  
  protected static final TextPipe clipColorText = new TextRenderer(clipColorPipe);
  
  protected static final AAShapePipe AAColorShape = new AAShapePipe(colorPipe);
  
  protected static final PixelToParallelogramConverter AAColorViaShape = makeConverter(AAColorShape);
  
  protected static final PixelToParallelogramConverter AAColorViaPgram = makeConverter(AAColorShape, colorPipe);
  
  protected static final AAShapePipe AAClipColorShape = new AAShapePipe(clipColorPipe);
  
  protected static final PixelToParallelogramConverter AAClipColorViaShape = makeConverter(AAClipColorShape);
  
  protected static final CompositePipe paintPipe = new AlphaPaintPipe();
  
  protected static final SpanShapeRenderer paintShape = new SpanShapeRenderer.Composite(paintPipe);
  
  protected static final PixelToShapeConverter paintViaShape = new PixelToShapeConverter(paintShape);
  
  protected static final TextPipe paintText = new TextRenderer(paintPipe);
  
  protected static final CompositePipe clipPaintPipe = new SpanClipRenderer(paintPipe);
  
  protected static final TextPipe clipPaintText = new TextRenderer(clipPaintPipe);
  
  protected static final AAShapePipe AAPaintShape = new AAShapePipe(paintPipe);
  
  protected static final PixelToParallelogramConverter AAPaintViaShape = makeConverter(AAPaintShape);
  
  protected static final AAShapePipe AAClipPaintShape = new AAShapePipe(clipPaintPipe);
  
  protected static final PixelToParallelogramConverter AAClipPaintViaShape = makeConverter(AAClipPaintShape);
  
  protected static final CompositePipe compPipe = new GeneralCompositePipe();
  
  protected static final SpanShapeRenderer compShape = new SpanShapeRenderer.Composite(compPipe);
  
  protected static final PixelToShapeConverter compViaShape = new PixelToShapeConverter(compShape);
  
  protected static final TextPipe compText = new TextRenderer(compPipe);
  
  protected static final CompositePipe clipCompPipe = new SpanClipRenderer(compPipe);
  
  protected static final TextPipe clipCompText = new TextRenderer(clipCompPipe);
  
  protected static final AAShapePipe AACompShape = new AAShapePipe(compPipe);
  
  protected static final PixelToParallelogramConverter AACompViaShape = makeConverter(AACompShape);
  
  protected static final AAShapePipe AAClipCompShape = new AAShapePipe(clipCompPipe);
  
  protected static final PixelToParallelogramConverter AAClipCompViaShape = makeConverter(AAClipCompShape);
  
  protected static final DrawImagePipe imagepipe = new DrawImage();
  
  static final int LOOP_UNKNOWN = 0;
  
  static final int LOOP_FOUND = 1;
  
  static final int LOOP_NOTFOUND = 2;
  
  int haveLCDLoop;
  
  int havePgramXORLoop;
  
  int havePgramSolidLoop;
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D) {
    if (paramSunGraphics2D.compositeState <= 0 && paramSunGraphics2D.paintState <= 1 && paramSunGraphics2D.clipState <= 1 && paramSunGraphics2D.surfaceData
      
      .getTransparency() == 1) {
      if (this.haveLCDLoop == 0) {
        DrawGlyphListLCD drawGlyphListLCD = DrawGlyphListLCD.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, 
            
            getSurfaceType());
        this.haveLCDLoop = (drawGlyphListLCD != null) ? 1 : 2;
      } 
      return (this.haveLCDLoop == 1);
    } 
    return false;
  }
  
  public boolean canRenderParallelograms(SunGraphics2D paramSunGraphics2D) {
    if (paramSunGraphics2D.paintState <= 1) {
      if (paramSunGraphics2D.compositeState == 2) {
        if (this.havePgramXORLoop == 0) {
          FillParallelogram fillParallelogram = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.Xor, 
              
              getSurfaceType());
          this.havePgramXORLoop = (fillParallelogram != null) ? 1 : 2;
        } 
        return (this.havePgramXORLoop == 1);
      } 
      if (paramSunGraphics2D.compositeState <= 0 && paramSunGraphics2D.antialiasHint != 2 && paramSunGraphics2D.clipState != 2) {
        if (this.havePgramSolidLoop == 0) {
          FillParallelogram fillParallelogram = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, 
              
              getSurfaceType());
          this.havePgramSolidLoop = (fillParallelogram != null) ? 1 : 2;
        } 
        return (this.havePgramSolidLoop == 1);
      } 
    } 
    return false;
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D) {
    paramSunGraphics2D.imagepipe = imagepipe;
    if (paramSunGraphics2D.compositeState == 2) {
      if (paramSunGraphics2D.paintState > 1) {
        paramSunGraphics2D.drawpipe = paintViaShape;
        paramSunGraphics2D.fillpipe = paintViaShape;
        paramSunGraphics2D.shapepipe = paintShape;
        paramSunGraphics2D.textpipe = outlineTextRenderer;
      } else {
        PixelToShapeConverter pixelToShapeConverter;
        if (canRenderParallelograms(paramSunGraphics2D)) {
          pixelToShapeConverter = colorViaPgram;
          paramSunGraphics2D.shapepipe = colorViaPgram;
        } else {
          pixelToShapeConverter = colorViaShape;
          paramSunGraphics2D.shapepipe = colorPrimitives;
        } 
        if (paramSunGraphics2D.clipState == 2) {
          paramSunGraphics2D.drawpipe = pixelToShapeConverter;
          paramSunGraphics2D.fillpipe = pixelToShapeConverter;
          paramSunGraphics2D.textpipe = outlineTextRenderer;
        } else {
          if (paramSunGraphics2D.transformState >= 3) {
            paramSunGraphics2D.drawpipe = pixelToShapeConverter;
            paramSunGraphics2D.fillpipe = pixelToShapeConverter;
          } else {
            if (paramSunGraphics2D.strokeState != 0) {
              paramSunGraphics2D.drawpipe = pixelToShapeConverter;
            } else {
              paramSunGraphics2D.drawpipe = colorPrimitives;
            } 
            paramSunGraphics2D.fillpipe = colorPrimitives;
          } 
          paramSunGraphics2D.textpipe = solidTextRenderer;
        } 
      } 
    } else if (paramSunGraphics2D.compositeState == 3) {
      if (paramSunGraphics2D.antialiasHint == 2) {
        if (paramSunGraphics2D.clipState == 2) {
          paramSunGraphics2D.drawpipe = AAClipCompViaShape;
          paramSunGraphics2D.fillpipe = AAClipCompViaShape;
          paramSunGraphics2D.shapepipe = AAClipCompViaShape;
          paramSunGraphics2D.textpipe = clipCompText;
        } else {
          paramSunGraphics2D.drawpipe = AACompViaShape;
          paramSunGraphics2D.fillpipe = AACompViaShape;
          paramSunGraphics2D.shapepipe = AACompViaShape;
          paramSunGraphics2D.textpipe = compText;
        } 
      } else {
        paramSunGraphics2D.drawpipe = compViaShape;
        paramSunGraphics2D.fillpipe = compViaShape;
        paramSunGraphics2D.shapepipe = compShape;
        if (paramSunGraphics2D.clipState == 2) {
          paramSunGraphics2D.textpipe = clipCompText;
        } else {
          paramSunGraphics2D.textpipe = compText;
        } 
      } 
    } else if (paramSunGraphics2D.antialiasHint == 2) {
      paramSunGraphics2D.alphafill = getMaskFill(paramSunGraphics2D);
      if (paramSunGraphics2D.alphafill != null) {
        if (paramSunGraphics2D.clipState == 2) {
          paramSunGraphics2D.drawpipe = AAClipColorViaShape;
          paramSunGraphics2D.fillpipe = AAClipColorViaShape;
          paramSunGraphics2D.shapepipe = AAClipColorViaShape;
          paramSunGraphics2D.textpipe = clipColorText;
        } else {
          PixelToParallelogramConverter pixelToParallelogramConverter = paramSunGraphics2D.alphafill.canDoParallelograms() ? AAColorViaPgram : AAColorViaShape;
          paramSunGraphics2D.drawpipe = pixelToParallelogramConverter;
          paramSunGraphics2D.fillpipe = pixelToParallelogramConverter;
          paramSunGraphics2D.shapepipe = pixelToParallelogramConverter;
          if (paramSunGraphics2D.paintState > 1 || paramSunGraphics2D.compositeState > 0) {
            paramSunGraphics2D.textpipe = colorText;
          } else {
            paramSunGraphics2D.textpipe = getTextPipe(paramSunGraphics2D, true);
          } 
        } 
      } else if (paramSunGraphics2D.clipState == 2) {
        paramSunGraphics2D.drawpipe = AAClipPaintViaShape;
        paramSunGraphics2D.fillpipe = AAClipPaintViaShape;
        paramSunGraphics2D.shapepipe = AAClipPaintViaShape;
        paramSunGraphics2D.textpipe = clipPaintText;
      } else {
        paramSunGraphics2D.drawpipe = AAPaintViaShape;
        paramSunGraphics2D.fillpipe = AAPaintViaShape;
        paramSunGraphics2D.shapepipe = AAPaintViaShape;
        paramSunGraphics2D.textpipe = paintText;
      } 
    } else if (paramSunGraphics2D.paintState > 1 || paramSunGraphics2D.compositeState > 0 || paramSunGraphics2D.clipState == 2) {
      paramSunGraphics2D.drawpipe = paintViaShape;
      paramSunGraphics2D.fillpipe = paintViaShape;
      paramSunGraphics2D.shapepipe = paintShape;
      paramSunGraphics2D.alphafill = getMaskFill(paramSunGraphics2D);
      if (paramSunGraphics2D.alphafill != null) {
        if (paramSunGraphics2D.clipState == 2) {
          paramSunGraphics2D.textpipe = clipColorText;
        } else {
          paramSunGraphics2D.textpipe = colorText;
        } 
      } else if (paramSunGraphics2D.clipState == 2) {
        paramSunGraphics2D.textpipe = clipPaintText;
      } else {
        paramSunGraphics2D.textpipe = paintText;
      } 
    } else {
      PixelToShapeConverter pixelToShapeConverter;
      if (canRenderParallelograms(paramSunGraphics2D)) {
        pixelToShapeConverter = colorViaPgram;
        paramSunGraphics2D.shapepipe = colorViaPgram;
      } else {
        pixelToShapeConverter = colorViaShape;
        paramSunGraphics2D.shapepipe = colorPrimitives;
      } 
      if (paramSunGraphics2D.transformState >= 3) {
        paramSunGraphics2D.drawpipe = pixelToShapeConverter;
        paramSunGraphics2D.fillpipe = pixelToShapeConverter;
      } else {
        if (paramSunGraphics2D.strokeState != 0) {
          paramSunGraphics2D.drawpipe = pixelToShapeConverter;
        } else {
          paramSunGraphics2D.drawpipe = colorPrimitives;
        } 
        paramSunGraphics2D.fillpipe = colorPrimitives;
      } 
      paramSunGraphics2D.textpipe = getTextPipe(paramSunGraphics2D, false);
    } 
    if (paramSunGraphics2D.textpipe instanceof LoopBasedPipe || paramSunGraphics2D.shapepipe instanceof LoopBasedPipe || paramSunGraphics2D.fillpipe instanceof LoopBasedPipe || paramSunGraphics2D.drawpipe instanceof LoopBasedPipe || paramSunGraphics2D.imagepipe instanceof LoopBasedPipe)
      paramSunGraphics2D.loops = getRenderLoops(paramSunGraphics2D); 
  }
  
  private TextPipe getTextPipe(SunGraphics2D paramSunGraphics2D, boolean paramBoolean) {
    switch (paramSunGraphics2D.textAntialiasHint) {
      case 0:
        if (paramBoolean)
          return aaTextRenderer; 
        return solidTextRenderer;
      case 1:
        return solidTextRenderer;
      case 2:
        return aaTextRenderer;
    } 
    switch ((paramSunGraphics2D.getFontInfo()).aaHint) {
      case 4:
      case 6:
        return lcdTextRenderer;
      case 2:
        return aaTextRenderer;
      case 1:
        return solidTextRenderer;
    } 
    if (paramBoolean)
      return aaTextRenderer; 
    return solidTextRenderer;
  }
  
  private static SurfaceType getPaintSurfaceType(SunGraphics2D paramSunGraphics2D) {
    switch (paramSunGraphics2D.paintState) {
      case 0:
        return SurfaceType.OpaqueColor;
      case 1:
        return SurfaceType.AnyColor;
      case 2:
        if (paramSunGraphics2D.paint.getTransparency() == 1)
          return SurfaceType.OpaqueGradientPaint; 
        return SurfaceType.GradientPaint;
      case 3:
        if (paramSunGraphics2D.paint.getTransparency() == 1)
          return SurfaceType.OpaqueLinearGradientPaint; 
        return SurfaceType.LinearGradientPaint;
      case 4:
        if (paramSunGraphics2D.paint.getTransparency() == 1)
          return SurfaceType.OpaqueRadialGradientPaint; 
        return SurfaceType.RadialGradientPaint;
      case 5:
        if (paramSunGraphics2D.paint.getTransparency() == 1)
          return SurfaceType.OpaqueTexturePaint; 
        return SurfaceType.TexturePaint;
    } 
    return SurfaceType.AnyPaint;
  }
  
  private static CompositeType getFillCompositeType(SunGraphics2D paramSunGraphics2D) {
    CompositeType compositeType = paramSunGraphics2D.imageComp;
    if (paramSunGraphics2D.compositeState == 0)
      if (compositeType == CompositeType.SrcOverNoEa) {
        compositeType = CompositeType.OpaqueSrcOverNoEa;
      } else {
        compositeType = CompositeType.SrcNoEa;
      }  
    return compositeType;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D) {
    SurfaceType surfaceType1 = getPaintSurfaceType(paramSunGraphics2D);
    CompositeType compositeType = getFillCompositeType(paramSunGraphics2D);
    SurfaceType surfaceType2 = getSurfaceType();
    return MaskFill.getFromCache(surfaceType1, compositeType, surfaceType2);
  }
  
  private static RenderCache loopcache = new RenderCache(30);
  
  static Permission compPermission;
  
  public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) {
    SurfaceType surfaceType1 = getPaintSurfaceType(paramSunGraphics2D);
    CompositeType compositeType = getFillCompositeType(paramSunGraphics2D);
    SurfaceType surfaceType2 = paramSunGraphics2D.getSurfaceData().getSurfaceType();
    Object object = loopcache.get(surfaceType1, compositeType, surfaceType2);
    if (object != null)
      return (RenderLoops)object; 
    RenderLoops renderLoops = makeRenderLoops(surfaceType1, compositeType, surfaceType2);
    loopcache.put(surfaceType1, compositeType, surfaceType2, renderLoops);
    return renderLoops;
  }
  
  public static RenderLoops makeRenderLoops(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    RenderLoops renderLoops = new RenderLoops();
    renderLoops.drawLineLoop = DrawLine.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.fillRectLoop = FillRect.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawRectLoop = DrawRect.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawPolygonsLoop = DrawPolygons.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawPathLoop = DrawPath.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.fillPathLoop = FillPath.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.fillSpansLoop = FillSpans.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.fillParallelogramLoop = FillParallelogram.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawParallelogramLoop = DrawParallelogram.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawGlyphListLoop = DrawGlyphList.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawGlyphListAALoop = DrawGlyphListAA.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    renderLoops.drawGlyphListLCDLoop = DrawGlyphListLCD.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    return renderLoops;
  }
  
  public final SurfaceType getSurfaceType() {
    return this.surfaceType;
  }
  
  public final ColorModel getColorModel() {
    return this.colorModel;
  }
  
  public int getTransparency() {
    return getColorModel().getTransparency();
  }
  
  public boolean useTightBBoxes() {
    return true;
  }
  
  public int pixelFor(int paramInt) {
    return this.surfaceType.pixelFor(paramInt, this.colorModel);
  }
  
  public int pixelFor(Color paramColor) {
    return pixelFor(paramColor.getRGB());
  }
  
  public int rgbFor(int paramInt) {
    return this.surfaceType.rgbFor(paramInt, this.colorModel);
  }
  
  protected void checkCustomComposite() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (compPermission == null)
        compPermission = new AWTPermission("readDisplayPixels"); 
      securityManager.checkPermission(compPermission);
    } 
  }
  
  public static boolean isNull(SurfaceData paramSurfaceData) {
    if (paramSurfaceData == null || paramSurfaceData == NullSurfaceData.theInstance)
      return true; 
    return false;
  }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    return false;
  }
  
  public void flush() {}
  
  public int getDefaultScale() {
    return 1;
  }
  
  private static native void initIDs();
  
  public abstract SurfaceData getReplacement();
  
  public abstract GraphicsConfiguration getDeviceConfiguration();
  
  public abstract Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract Rectangle getBounds();
  
  protected static native boolean isOpaqueGray(IndexColorModel paramIndexColorModel);
  
  public abstract Object getDestination();
}
