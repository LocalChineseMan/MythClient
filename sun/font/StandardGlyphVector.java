package sun.font;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import sun.java2d.loops.FontInfo;

public class StandardGlyphVector extends GlyphVector {
  private Font font;
  
  private FontRenderContext frc;
  
  private int[] glyphs;
  
  private int[] userGlyphs;
  
  private float[] positions;
  
  private int[] charIndices;
  
  private int flags;
  
  private static final int UNINITIALIZED_FLAGS = -1;
  
  private GlyphTransformInfo gti;
  
  private AffineTransform ftx;
  
  private AffineTransform dtx;
  
  private AffineTransform invdtx;
  
  private AffineTransform frctx;
  
  private Font2D font2D;
  
  private SoftReference fsref;
  
  private SoftReference lbcacheRef;
  
  private SoftReference vbcacheRef;
  
  public static final int FLAG_USES_VERTICAL_BASELINE = 128;
  
  public static final int FLAG_USES_VERTICAL_METRICS = 256;
  
  public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;
  
  public StandardGlyphVector(Font paramFont, String paramString, FontRenderContext paramFontRenderContext) {
    init(paramFont, paramString.toCharArray(), 0, paramString.length(), paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfchar, FontRenderContext paramFontRenderContext) {
    init(paramFont, paramArrayOfchar, 0, paramArrayOfchar.length, paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, char[] paramArrayOfchar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    init(paramFont, paramArrayOfchar, paramInt1, paramInt2, paramFontRenderContext, -1);
  }
  
  private float getTracking(Font paramFont) {
    if (paramFont.hasLayoutAttributes()) {
      AttributeValues attributeValues = ((AttributeMap)paramFont.getAttributes()).getValues();
      return attributeValues.getTracking();
    } 
    return 0.0F;
  }
  
  public StandardGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfint1, float[] paramArrayOffloat, int[] paramArrayOfint2, int paramInt) {
    initGlyphVector(paramFont, paramFontRenderContext, paramArrayOfint1, paramArrayOffloat, paramArrayOfint2, paramInt);
    float f = getTracking(paramFont);
    if (f != 0.0F) {
      f *= paramFont.getSize2D();
      Point2D.Float float_ = new Point2D.Float(f, 0.0F);
      if (paramFont.isTransformed()) {
        AffineTransform affineTransform = paramFont.getTransform();
        affineTransform.deltaTransform(float_, float_);
      } 
      Font2D font2D = FontUtilities.getFont2D(paramFont);
      FontStrike fontStrike = font2D.getStrike(paramFont, paramFontRenderContext);
      float[] arrayOfFloat = { float_.x, float_.y };
      for (byte b = 0; b < arrayOfFloat.length; b++) {
        float f1 = arrayOfFloat[b];
        if (f1 != 0.0F) {
          float f2 = 0.0F;
          for (byte b1 = b, b2 = 0; b2 < paramArrayOfint1.length; b1 += 2) {
            if (fontStrike.getGlyphAdvance(paramArrayOfint1[b2++]) != 0.0F) {
              paramArrayOffloat[b1] = paramArrayOffloat[b1] + f2;
              f2 += f1;
            } 
          } 
          paramArrayOffloat[paramArrayOffloat.length - 2 + b] = paramArrayOffloat[paramArrayOffloat.length - 2 + b] + f2;
        } 
      } 
    } 
  }
  
  public void initGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfint1, float[] paramArrayOffloat, int[] paramArrayOfint2, int paramInt) {
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.glyphs = paramArrayOfint1;
    this.userGlyphs = paramArrayOfint1;
    this.positions = paramArrayOffloat;
    this.charIndices = paramArrayOfint2;
    this.flags = paramInt;
    initFontData();
  }
  
  public StandardGlyphVector(Font paramFont, CharacterIterator paramCharacterIterator, FontRenderContext paramFontRenderContext) {
    int i = paramCharacterIterator.getBeginIndex();
    char[] arrayOfChar = new char[paramCharacterIterator.getEndIndex() - i];
    char c = paramCharacterIterator.first();
    for (; c != Character.MAX_VALUE; 
      c = paramCharacterIterator.next())
      arrayOfChar[paramCharacterIterator.getIndex() - i] = c; 
    init(paramFont, arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext, -1);
  }
  
  public StandardGlyphVector(Font paramFont, int[] paramArrayOfint, FontRenderContext paramFontRenderContext) {
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.flags = -1;
    initFontData();
    this.userGlyphs = paramArrayOfint;
    this.glyphs = getValidatedGlyphs(this.userGlyphs);
  }
  
  public static StandardGlyphVector getStandardGV(GlyphVector paramGlyphVector, FontInfo paramFontInfo) {
    if (paramFontInfo.aaHint == 2) {
      Object object = paramGlyphVector.getFontRenderContext().getAntiAliasingHint();
      if (object != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && object != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP) {
        FontRenderContext fontRenderContext = paramGlyphVector.getFontRenderContext();
        fontRenderContext = new FontRenderContext(fontRenderContext.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, fontRenderContext.getFractionalMetricsHint());
        return new StandardGlyphVector(paramGlyphVector, fontRenderContext);
      } 
    } 
    if (paramGlyphVector instanceof StandardGlyphVector)
      return (StandardGlyphVector)paramGlyphVector; 
    return new StandardGlyphVector(paramGlyphVector, paramGlyphVector.getFontRenderContext());
  }
  
  public Font getFont() {
    return this.font;
  }
  
  public FontRenderContext getFontRenderContext() {
    return this.frc;
  }
  
  public void performDefaultLayout() {
    this.positions = null;
    if (getTracking(this.font) == 0.0F)
      clearFlags(2); 
  }
  
  public int getNumGlyphs() {
    return this.glyphs.length;
  }
  
  public int getGlyphCode(int paramInt) {
    return this.userGlyphs[paramInt];
  }
  
  public int[] getGlyphCodes(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("count = " + paramInt2); 
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("start = " + paramInt1); 
    if (paramInt1 > this.glyphs.length - paramInt2)
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2)); 
    if (paramArrayOfint == null)
      paramArrayOfint = new int[paramInt2]; 
    for (byte b = 0; b < paramInt2; b++)
      paramArrayOfint[b] = this.userGlyphs[b + paramInt1]; 
    return paramArrayOfint;
  }
  
  public int getGlyphCharIndex(int paramInt) {
    if (paramInt < 0 && paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("" + paramInt); 
    if (this.charIndices == null) {
      if ((getLayoutFlags() & 0x4) != 0)
        return this.glyphs.length - 1 - paramInt; 
      return paramInt;
    } 
    return this.charIndices[paramInt];
  }
  
  public int[] getGlyphCharIndices(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > this.glyphs.length - paramInt1)
      throw new IndexOutOfBoundsException("" + paramInt1 + ", " + paramInt2); 
    if (paramArrayOfint == null)
      paramArrayOfint = new int[paramInt2]; 
    if (this.charIndices == null) {
      if ((getLayoutFlags() & 0x4) != 0) {
        byte b = 0;
        int i = this.glyphs.length - 1 - paramInt1;
        for (; b < paramInt2; b++, i--)
          paramArrayOfint[b] = i; 
      } else {
        byte b;
        int i;
        for (b = 0, i = paramInt1; b < paramInt2; b++, i++)
          paramArrayOfint[b] = i; 
      } 
    } else {
      for (byte b = 0; b < paramInt2; b++)
        paramArrayOfint[b] = this.charIndices[b + paramInt1]; 
    } 
    return paramArrayOfint;
  }
  
  public Rectangle2D getLogicalBounds() {
    setFRCTX();
    initPositions();
    LineMetrics lineMetrics = this.font.getLineMetrics("", this.frc);
    float f1 = 0.0F;
    float f2 = -lineMetrics.getAscent();
    float f3 = 0.0F;
    float f4 = lineMetrics.getDescent() + lineMetrics.getLeading();
    if (this.glyphs.length > 0)
      f3 = this.positions[this.positions.length - 2]; 
    return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
  }
  
  public Rectangle2D getVisualBounds() {
    Rectangle2D rectangle2D = null;
    for (byte b = 0; b < this.glyphs.length; b++) {
      Rectangle2D rectangle2D1 = getGlyphVisualBounds(b).getBounds2D();
      if (!rectangle2D1.isEmpty())
        if (rectangle2D == null) {
          rectangle2D = rectangle2D1;
        } else {
          Rectangle2D.union(rectangle2D, rectangle2D1, rectangle2D);
        }  
    } 
    if (rectangle2D == null)
      rectangle2D = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F); 
    return rectangle2D;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) {
    return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, 0, this.glyphs.length);
  }
  
  public Shape getOutline() {
    return getGlyphsOutline(0, this.glyphs.length, 0.0F, 0.0F);
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2) {
    return getGlyphsOutline(0, this.glyphs.length, paramFloat1, paramFloat2);
  }
  
  public Shape getGlyphOutline(int paramInt) {
    return getGlyphsOutline(paramInt, 1, 0.0F, 0.0F);
  }
  
  public Shape getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
    return getGlyphsOutline(paramInt, 1, paramFloat1, paramFloat2);
  }
  
  public Point2D getGlyphPosition(int paramInt) {
    initPositions();
    paramInt *= 2;
    return new Point2D.Float(this.positions[paramInt], this.positions[paramInt + 1]);
  }
  
  public void setGlyphPosition(int paramInt, Point2D paramPoint2D) {
    initPositions();
    int i = paramInt << 1;
    this.positions[i] = (float)paramPoint2D.getX();
    this.positions[i + 1] = (float)paramPoint2D.getY();
    clearCaches(paramInt);
    addFlags(2);
  }
  
  public AffineTransform getGlyphTransform(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    if (this.gti != null)
      return this.gti.getGlyphTransform(paramInt); 
    return null;
  }
  
  public void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    if (this.gti == null) {
      if (paramAffineTransform == null || paramAffineTransform.isIdentity())
        return; 
      this.gti = new GlyphTransformInfo(this);
    } 
    this.gti.setGlyphTransform(paramInt, paramAffineTransform);
    if (this.gti.transformCount() == 0)
      this.gti = null; 
  }
  
  public int getLayoutFlags() {
    if (this.flags == -1) {
      this.flags = 0;
      if (this.charIndices != null && this.glyphs.length > 1) {
        boolean bool1 = true;
        boolean bool2 = true;
        int i = this.charIndices.length;
        for (byte b = 0; b < this.charIndices.length && (bool1 || bool2); b++) {
          int j = this.charIndices[b];
          bool1 = (bool1 && j == b) ? true : false;
          bool2 = (bool2 && j == --i) ? true : false;
        } 
        if (bool2)
          this.flags |= 0x4; 
        if (!bool2 && !bool1)
          this.flags |= 0x8; 
      } 
    } 
    return this.flags;
  }
  
  public float[] getGlyphPositions(int paramInt1, int paramInt2, float[] paramArrayOffloat) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("count = " + paramInt2); 
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("start = " + paramInt1); 
    if (paramInt1 > this.glyphs.length + 1 - paramInt2)
      throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2)); 
    return internalGetGlyphPositions(paramInt1, paramInt2, 0, paramArrayOffloat);
  }
  
  public Shape getGlyphLogicalBounds(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Shape[] arrayOfShape;
    if (this.lbcacheRef == null || (arrayOfShape = this.lbcacheRef.get()) == null) {
      arrayOfShape = new Shape[this.glyphs.length];
      this.lbcacheRef = new SoftReference<>(arrayOfShape);
    } 
    Shape shape = arrayOfShape[paramInt];
    if (shape == null) {
      setFRCTX();
      initPositions();
      ADL aDL = new ADL();
      GlyphStrike glyphStrike = getGlyphStrike(paramInt);
      glyphStrike.getADL(aDL);
      Point2D.Float float_ = glyphStrike.strike.getGlyphMetrics(this.glyphs[paramInt]);
      float f1 = float_.x;
      float f2 = float_.y;
      float f3 = aDL.descentX + aDL.leadingX + aDL.ascentX;
      float f4 = aDL.descentY + aDL.leadingY + aDL.ascentY;
      float f5 = this.positions[paramInt * 2] + glyphStrike.dx - aDL.ascentX;
      float f6 = this.positions[paramInt * 2 + 1] + glyphStrike.dy - aDL.ascentY;
      GeneralPath generalPath = new GeneralPath();
      generalPath.moveTo(f5, f6);
      generalPath.lineTo(f5 + f1, f6 + f2);
      generalPath.lineTo(f5 + f1 + f3, f6 + f2 + f4);
      generalPath.lineTo(f5 + f3, f6 + f4);
      generalPath.closePath();
      shape = new DelegatingShape(generalPath);
      arrayOfShape[paramInt] = shape;
    } 
    return shape;
  }
  
  public Shape getGlyphVisualBounds(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Shape[] arrayOfShape;
    if (this.vbcacheRef == null || (arrayOfShape = this.vbcacheRef.get()) == null) {
      arrayOfShape = new Shape[this.glyphs.length];
      this.vbcacheRef = new SoftReference<>(arrayOfShape);
    } 
    Shape shape = arrayOfShape[paramInt];
    if (shape == null) {
      shape = new DelegatingShape(getGlyphOutlineBounds(paramInt));
      arrayOfShape[paramInt] = shape;
    } 
    return shape;
  }
  
  public Rectangle getGlyphPixelBounds(int paramInt, FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) {
    return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, paramInt, 1);
  }
  
  public GlyphMetrics getGlyphMetrics(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    Rectangle2D rectangle2D = getGlyphVisualBounds(paramInt).getBounds2D();
    Point2D point2D = getGlyphPosition(paramInt);
    rectangle2D.setRect(rectangle2D.getMinX() - point2D.getX(), rectangle2D
        .getMinY() - point2D.getY(), rectangle2D
        .getWidth(), rectangle2D
        .getHeight());
    Point2D.Float float_ = (getGlyphStrike(paramInt)).strike.getGlyphMetrics(this.glyphs[paramInt]);
    return new GlyphMetrics(true, float_.x, float_.y, rectangle2D, (byte)0);
  }
  
  public GlyphJustificationInfo getGlyphJustificationInfo(int paramInt) {
    if (paramInt < 0 || paramInt >= this.glyphs.length)
      throw new IndexOutOfBoundsException("ix = " + paramInt); 
    return null;
  }
  
  public boolean equals(GlyphVector paramGlyphVector) {
    if (this == paramGlyphVector)
      return true; 
    if (paramGlyphVector == null)
      return false; 
    try {
      StandardGlyphVector standardGlyphVector = (StandardGlyphVector)paramGlyphVector;
      if (this.glyphs.length != standardGlyphVector.glyphs.length)
        return false; 
      byte b;
      for (b = 0; b < this.glyphs.length; b++) {
        if (this.glyphs[b] != standardGlyphVector.glyphs[b])
          return false; 
      } 
      if (!this.font.equals(standardGlyphVector.font))
        return false; 
      if (!this.frc.equals(standardGlyphVector.frc))
        return false; 
      if (((standardGlyphVector.positions == null) ? true : false) != ((this.positions == null) ? true : false))
        if (this.positions == null) {
          initPositions();
        } else {
          standardGlyphVector.initPositions();
        }  
      if (this.positions != null)
        for (b = 0; b < this.positions.length; b++) {
          if (this.positions[b] != standardGlyphVector.positions[b])
            return false; 
        }  
      if (this.gti == null)
        return (standardGlyphVector.gti == null); 
      return this.gti.equals(standardGlyphVector.gti);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public int hashCode() {
    return this.font.hashCode() ^ this.glyphs.length;
  }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((GlyphVector)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public StandardGlyphVector copy() {
    return (StandardGlyphVector)clone();
  }
  
  public Object clone() {
    try {
      StandardGlyphVector standardGlyphVector = (StandardGlyphVector)super.clone();
      standardGlyphVector.clearCaches();
      if (this.positions != null)
        standardGlyphVector.positions = (float[])this.positions.clone(); 
      if (this.gti != null)
        standardGlyphVector.gti = new GlyphTransformInfo(standardGlyphVector, this.gti); 
      return standardGlyphVector;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return this;
    } 
  }
  
  public void setGlyphPositions(float[] paramArrayOffloat, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 < 0)
      throw new IllegalArgumentException("count = " + paramInt3); 
    initPositions();
    for (int i = paramInt2 * 2, j = i + paramInt3 * 2, k = paramInt1; i < j; i++, k++)
      this.positions[i] = paramArrayOffloat[k]; 
    clearCaches();
    addFlags(2);
  }
  
  public void setGlyphPositions(float[] paramArrayOffloat) {
    int i = this.glyphs.length * 2 + 2;
    if (paramArrayOffloat.length != i)
      throw new IllegalArgumentException("srcPositions.length != " + i); 
    this.positions = (float[])paramArrayOffloat.clone();
    clearCaches();
    addFlags(2);
  }
  
  public float[] getGlyphPositions(float[] paramArrayOffloat) {
    return internalGetGlyphPositions(0, this.glyphs.length + 1, 0, paramArrayOffloat);
  }
  
  public AffineTransform[] getGlyphTransforms(int paramInt1, int paramInt2, AffineTransform[] paramArrayOfAffineTransform) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > this.glyphs.length)
      throw new IllegalArgumentException("start: " + paramInt1 + " count: " + paramInt2); 
    if (this.gti == null)
      return null; 
    if (paramArrayOfAffineTransform == null)
      paramArrayOfAffineTransform = new AffineTransform[paramInt2]; 
    for (byte b = 0; b < paramInt2; b++, paramInt1++)
      paramArrayOfAffineTransform[b] = this.gti.getGlyphTransform(paramInt1); 
    return paramArrayOfAffineTransform;
  }
  
  public AffineTransform[] getGlyphTransforms() {
    return getGlyphTransforms(0, this.glyphs.length, null);
  }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform, int paramInt1, int paramInt2, int paramInt3) {
    for (int i = paramInt2, j = paramInt2 + paramInt3; i < j; i++)
      setGlyphTransform(i, paramArrayOfAffineTransform[paramInt1 + i]); 
  }
  
  public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform) {
    setGlyphTransforms(paramArrayOfAffineTransform, 0, 0, this.glyphs.length);
  }
  
  public float[] getGlyphInfo() {
    setFRCTX();
    initPositions();
    float[] arrayOfFloat = new float[this.glyphs.length * 8];
    for (byte b1 = 0, b2 = 0; b1 < this.glyphs.length; b1++, b2 += 8) {
      float f1 = this.positions[b1 * 2];
      float f2 = this.positions[b1 * 2 + 1];
      arrayOfFloat[b2] = f1;
      arrayOfFloat[b2 + 1] = f2;
      int i = this.glyphs[b1];
      GlyphStrike glyphStrike = getGlyphStrike(b1);
      Point2D.Float float_ = glyphStrike.strike.getGlyphMetrics(i);
      arrayOfFloat[b2 + 2] = float_.x;
      arrayOfFloat[b2 + 3] = float_.y;
      Rectangle2D rectangle2D = getGlyphVisualBounds(b1).getBounds2D();
      arrayOfFloat[b2 + 4] = (float)rectangle2D.getMinX();
      arrayOfFloat[b2 + 5] = (float)rectangle2D.getMinY();
      arrayOfFloat[b2 + 6] = (float)rectangle2D.getWidth();
      arrayOfFloat[b2 + 7] = (float)rectangle2D.getHeight();
    } 
    return arrayOfFloat;
  }
  
  public void pixellate(FontRenderContext paramFontRenderContext, Point2D paramPoint2D, Point paramPoint) {
    if (paramFontRenderContext == null)
      paramFontRenderContext = this.frc; 
    AffineTransform affineTransform = paramFontRenderContext.getTransform();
    affineTransform.transform(paramPoint2D, paramPoint2D);
    paramPoint.x = (int)paramPoint2D.getX();
    paramPoint.y = (int)paramPoint2D.getY();
    paramPoint2D.setLocation(paramPoint.x, paramPoint.y);
    try {
      affineTransform.inverseTransform(paramPoint2D, paramPoint2D);
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      throw new IllegalArgumentException("must be able to invert frc transform");
    } 
  }
  
  boolean needsPositions(double[] paramArrayOfdouble) {
    return (this.gti != null || (
      getLayoutFlags() & 0x2) != 0 || 
      !matchTX(paramArrayOfdouble, this.frctx));
  }
  
  Object setupGlyphImages(long[] paramArrayOflong, float[] paramArrayOffloat, double[] paramArrayOfdouble) {
    initPositions();
    setRenderTransform(paramArrayOfdouble);
    if (this.gti != null)
      return this.gti.setupGlyphImages(paramArrayOflong, paramArrayOffloat, this.dtx); 
    GlyphStrike glyphStrike = getDefaultStrike();
    glyphStrike.strike.getGlyphImagePtrs(this.glyphs, paramArrayOflong, this.glyphs.length);
    if (paramArrayOffloat != null)
      if (this.dtx.isIdentity()) {
        System.arraycopy(this.positions, 0, paramArrayOffloat, 0, this.glyphs.length * 2);
      } else {
        this.dtx.transform(this.positions, 0, paramArrayOffloat, 0, this.glyphs.length);
      }  
    return glyphStrike;
  }
  
  private static boolean matchTX(double[] paramArrayOfdouble, AffineTransform paramAffineTransform) {
    return (paramArrayOfdouble[0] == paramAffineTransform
      .getScaleX() && paramArrayOfdouble[1] == paramAffineTransform
      .getShearY() && paramArrayOfdouble[2] == paramAffineTransform
      .getShearX() && paramArrayOfdouble[3] == paramAffineTransform
      .getScaleY());
  }
  
  private static AffineTransform getNonTranslateTX(AffineTransform paramAffineTransform) {
    if (paramAffineTransform.getTranslateX() != 0.0D || paramAffineTransform.getTranslateY() != 0.0D)
      paramAffineTransform = new AffineTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D); 
    return paramAffineTransform;
  }
  
  private static boolean equalNonTranslateTX(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2) {
    return (paramAffineTransform1.getScaleX() == paramAffineTransform2.getScaleX() && paramAffineTransform1
      .getShearY() == paramAffineTransform2.getShearY() && paramAffineTransform1
      .getShearX() == paramAffineTransform2.getShearX() && paramAffineTransform1
      .getScaleY() == paramAffineTransform2.getScaleY());
  }
  
  private void setRenderTransform(double[] paramArrayOfdouble) {
    assert paramArrayOfdouble.length == 4;
    if (!matchTX(paramArrayOfdouble, this.dtx))
      resetDTX(new AffineTransform(paramArrayOfdouble)); 
  }
  
  private final void setDTX(AffineTransform paramAffineTransform) {
    if (!equalNonTranslateTX(this.dtx, paramAffineTransform))
      resetDTX(getNonTranslateTX(paramAffineTransform)); 
  }
  
  private final void setFRCTX() {
    if (!equalNonTranslateTX(this.frctx, this.dtx))
      resetDTX(getNonTranslateTX(this.frctx)); 
  }
  
  private final void resetDTX(AffineTransform paramAffineTransform) {
    this.fsref = null;
    this.dtx = paramAffineTransform;
    this.invdtx = null;
    if (!this.dtx.isIdentity())
      try {
        this.invdtx = this.dtx.createInverse();
      } catch (NoninvertibleTransformException noninvertibleTransformException) {} 
    if (this.gti != null)
      this.gti.strikesRef = null; 
  }
  
  private StandardGlyphVector(GlyphVector paramGlyphVector, FontRenderContext paramFontRenderContext) {
    this.font = paramGlyphVector.getFont();
    this.frc = paramFontRenderContext;
    initFontData();
    int i = paramGlyphVector.getNumGlyphs();
    this.userGlyphs = paramGlyphVector.getGlyphCodes(0, i, null);
    if (paramGlyphVector instanceof StandardGlyphVector) {
      this.glyphs = this.userGlyphs;
    } else {
      this.glyphs = getValidatedGlyphs(this.userGlyphs);
    } 
    this.flags = paramGlyphVector.getLayoutFlags() & 0xF;
    if ((this.flags & 0x2) != 0)
      this.positions = paramGlyphVector.getGlyphPositions(0, i + 1, null); 
    if ((this.flags & 0x8) != 0)
      this.charIndices = paramGlyphVector.getGlyphCharIndices(0, i, null); 
    if ((this.flags & 0x1) != 0) {
      AffineTransform[] arrayOfAffineTransform = new AffineTransform[i];
      for (byte b = 0; b < i; b++)
        arrayOfAffineTransform[b] = paramGlyphVector.getGlyphTransform(b); 
      setGlyphTransforms(arrayOfAffineTransform);
    } 
  }
  
  int[] getValidatedGlyphs(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfint[b] == 65534 || paramArrayOfint[b] == 65535) {
        arrayOfInt[b] = paramArrayOfint[b];
      } else {
        arrayOfInt[b] = this.font2D.getValidatedGlyphCode(paramArrayOfint[b]);
      } 
    } 
    return arrayOfInt;
  }
  
  private void init(Font paramFont, char[] paramArrayOfchar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext, int paramInt3) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfchar.length)
      throw new ArrayIndexOutOfBoundsException("start or count out of bounds"); 
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.flags = paramInt3;
    if (getTracking(paramFont) != 0.0F)
      addFlags(2); 
    if (paramInt1 != 0) {
      char[] arrayOfChar = new char[paramInt2];
      System.arraycopy(paramArrayOfchar, paramInt1, arrayOfChar, 0, paramInt2);
      paramArrayOfchar = arrayOfChar;
    } 
    initFontData();
    this.glyphs = new int[paramInt2];
    this.userGlyphs = this.glyphs;
    this.font2D.getMapper().charsToGlyphs(paramInt2, paramArrayOfchar, this.glyphs);
  }
  
  private void initFontData() {
    this.font2D = FontUtilities.getFont2D(this.font);
    float f = this.font.getSize2D();
    if (this.font.isTransformed()) {
      this.ftx = this.font.getTransform();
      if (this.ftx.getTranslateX() != 0.0D || this.ftx.getTranslateY() != 0.0D)
        addFlags(2); 
      this.ftx.setTransform(this.ftx.getScaleX(), this.ftx.getShearY(), this.ftx.getShearX(), this.ftx.getScaleY(), 0.0D, 0.0D);
      this.ftx.scale(f, f);
    } else {
      this.ftx = AffineTransform.getScaleInstance(f, f);
    } 
    this.frctx = this.frc.getTransform();
    resetDTX(getNonTranslateTX(this.frctx));
  }
  
  private float[] internalGetGlyphPositions(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOffloat) {
    if (paramArrayOffloat == null)
      paramArrayOffloat = new float[paramInt3 + paramInt2 * 2]; 
    initPositions();
    for (int i = paramInt3, j = paramInt3 + paramInt2 * 2, k = paramInt1 * 2; i < j; i++, k++)
      paramArrayOffloat[i] = this.positions[k]; 
    return paramArrayOffloat;
  }
  
  private Rectangle2D getGlyphOutlineBounds(int paramInt) {
    setFRCTX();
    initPositions();
    return getGlyphStrike(paramInt).getGlyphOutlineBounds(this.glyphs[paramInt], this.positions[paramInt * 2], this.positions[paramInt * 2 + 1]);
  }
  
  private Shape getGlyphsOutline(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2) {
    setFRCTX();
    initPositions();
    GeneralPath generalPath = new GeneralPath(1);
    for (int i = paramInt1, j = paramInt1 + paramInt2, k = paramInt1 * 2; i < j; i++, k += 2) {
      float f1 = paramFloat1 + this.positions[k];
      float f2 = paramFloat2 + this.positions[k + 1];
      getGlyphStrike(i).appendGlyphOutline(this.glyphs[i], generalPath, f1, f2);
    } 
    return generalPath;
  }
  
  private Rectangle getGlyphsPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {
    initPositions();
    AffineTransform affineTransform = null;
    if (paramFontRenderContext == null || paramFontRenderContext.equals(this.frc)) {
      affineTransform = this.frctx;
    } else {
      affineTransform = paramFontRenderContext.getTransform();
    } 
    setDTX(affineTransform);
    if (this.gti != null)
      return this.gti.getGlyphsPixelBounds(affineTransform, paramFloat1, paramFloat2, paramInt1, paramInt2); 
    FontStrike fontStrike = (getDefaultStrike()).strike;
    Rectangle rectangle1 = null;
    Rectangle rectangle2 = new Rectangle();
    Point2D.Float float_ = new Point2D.Float();
    int i = paramInt1 * 2;
    while (--paramInt2 >= 0) {
      float_.x = paramFloat1 + this.positions[i++];
      float_.y = paramFloat2 + this.positions[i++];
      affineTransform.transform(float_, float_);
      fontStrike.getGlyphImageBounds(this.glyphs[paramInt1++], float_, rectangle2);
      if (!rectangle2.isEmpty()) {
        if (rectangle1 == null) {
          rectangle1 = new Rectangle(rectangle2);
          continue;
        } 
        rectangle1.add(rectangle2);
      } 
    } 
    return (rectangle1 != null) ? rectangle1 : rectangle2;
  }
  
  private void clearCaches(int paramInt) {
    if (this.lbcacheRef != null) {
      Shape[] arrayOfShape = this.lbcacheRef.get();
      if (arrayOfShape != null)
        arrayOfShape[paramInt] = null; 
    } 
    if (this.vbcacheRef != null) {
      Shape[] arrayOfShape = this.vbcacheRef.get();
      if (arrayOfShape != null)
        arrayOfShape[paramInt] = null; 
    } 
  }
  
  private void clearCaches() {
    this.lbcacheRef = null;
    this.vbcacheRef = null;
  }
  
  private void initPositions() {
    if (this.positions == null) {
      setFRCTX();
      this.positions = new float[this.glyphs.length * 2 + 2];
      Point2D.Float float_1 = null;
      float f = getTracking(this.font);
      if (f != 0.0F) {
        f *= this.font.getSize2D();
        float_1 = new Point2D.Float(f, 0.0F);
      } 
      Point2D.Float float_2 = new Point2D.Float(0.0F, 0.0F);
      if (this.font.isTransformed()) {
        AffineTransform affineTransform = this.font.getTransform();
        affineTransform.transform(float_2, float_2);
        this.positions[0] = float_2.x;
        this.positions[1] = float_2.y;
        if (float_1 != null)
          affineTransform.deltaTransform(float_1, float_1); 
      } 
      for (byte b1 = 0, b2 = 2; b1 < this.glyphs.length; b1++, b2 += 2) {
        getGlyphStrike(b1).addDefaultGlyphAdvance(this.glyphs[b1], float_2);
        if (float_1 != null) {
          float_2.x += float_1.x;
          float_2.y += float_1.y;
        } 
        this.positions[b2] = float_2.x;
        this.positions[b2 + 1] = float_2.y;
      } 
    } 
  }
  
  private void addFlags(int paramInt) {
    this.flags = getLayoutFlags() | paramInt;
  }
  
  private void clearFlags(int paramInt) {
    this.flags = getLayoutFlags() & (paramInt ^ 0xFFFFFFFF);
  }
  
  private GlyphStrike getGlyphStrike(int paramInt) {
    if (this.gti == null)
      return getDefaultStrike(); 
    return this.gti.getStrike(paramInt);
  }
  
  private GlyphStrike getDefaultStrike() {
    GlyphStrike glyphStrike = null;
    if (this.fsref != null)
      glyphStrike = this.fsref.get(); 
    if (glyphStrike == null) {
      glyphStrike = GlyphStrike.create(this, this.dtx, null);
      this.fsref = new SoftReference<>(glyphStrike);
    } 
    return glyphStrike;
  }
  
  static class StandardGlyphVector {}
  
  public static final class GlyphStrike {
    StandardGlyphVector sgv;
    
    FontStrike strike;
    
    float dx;
    
    float dy;
    
    static GlyphStrike create(StandardGlyphVector param1StandardGlyphVector, AffineTransform param1AffineTransform1, AffineTransform param1AffineTransform2) {
      float f1 = 0.0F;
      float f2 = 0.0F;
      AffineTransform affineTransform = param1StandardGlyphVector.ftx;
      if (!param1AffineTransform1.isIdentity() || param1AffineTransform2 != null) {
        affineTransform = new AffineTransform(param1StandardGlyphVector.ftx);
        if (param1AffineTransform2 != null) {
          affineTransform.preConcatenate(param1AffineTransform2);
          f1 = (float)affineTransform.getTranslateX();
          f2 = (float)affineTransform.getTranslateY();
        } 
        if (!param1AffineTransform1.isIdentity())
          affineTransform.preConcatenate(param1AffineTransform1); 
      } 
      int i = 1;
      Object object = param1StandardGlyphVector.frc.getAntiAliasingHint();
      if (object == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
        if (!affineTransform.isIdentity() && (affineTransform
          .getType() & 0xFFFFFFFE) != 0) {
          double d = affineTransform.getShearX();
          if (d != 0.0D) {
            double d1 = affineTransform.getScaleY();
            i = (int)Math.sqrt(d * d + d1 * d1);
          } else {
            i = (int)Math.abs(affineTransform.getScaleY());
          } 
        }  
      int j = FontStrikeDesc.getAAHintIntVal(object, param1StandardGlyphVector.font2D, i);
      int k = FontStrikeDesc.getFMHintIntVal(param1StandardGlyphVector.frc.getFractionalMetricsHint());
      FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(param1AffineTransform1, affineTransform, param1StandardGlyphVector.font.getStyle(), j, k);
      FontStrike fontStrike = param1StandardGlyphVector.font2D.handle.font2D.getStrike(fontStrikeDesc);
      return new GlyphStrike(param1StandardGlyphVector, fontStrike, f1, f2);
    }
    
    private GlyphStrike(StandardGlyphVector param1StandardGlyphVector, FontStrike param1FontStrike, float param1Float1, float param1Float2) {
      this.sgv = param1StandardGlyphVector;
      this.strike = param1FontStrike;
      this.dx = param1Float1;
      this.dy = param1Float2;
    }
    
    void getADL(StandardGlyphVector.ADL param1ADL) {
      StrikeMetrics strikeMetrics = this.strike.getFontMetrics();
      Point2D.Float float_ = null;
      if (this.sgv.font.isTransformed()) {
        float_ = new Point2D.Float();
        float_.x = (float)this.sgv.font.getTransform().getTranslateX();
        float_.y = (float)this.sgv.font.getTransform().getTranslateY();
      } 
      param1ADL.ascentX = -strikeMetrics.ascentX;
      param1ADL.ascentY = -strikeMetrics.ascentY;
      param1ADL.descentX = strikeMetrics.descentX;
      param1ADL.descentY = strikeMetrics.descentY;
      param1ADL.leadingX = strikeMetrics.leadingX;
      param1ADL.leadingY = strikeMetrics.leadingY;
    }
    
    void getGlyphPosition(int param1Int1, int param1Int2, float[] param1ArrayOffloat1, float[] param1ArrayOffloat2) {
      param1ArrayOffloat2[param1Int2] = param1ArrayOffloat1[param1Int2] + this.dx;
      param1Int2++;
      param1ArrayOffloat2[param1Int2] = param1ArrayOffloat1[param1Int2] + this.dy;
    }
    
    void addDefaultGlyphAdvance(int param1Int, Point2D.Float param1Float) {
      Point2D.Float float_ = this.strike.getGlyphMetrics(param1Int);
      param1Float.x += float_.x + this.dx;
      param1Float.y += float_.y + this.dy;
    }
    
    Rectangle2D getGlyphOutlineBounds(int param1Int, float param1Float1, float param1Float2) {
      Rectangle2D rectangle2D = null;
      if (this.sgv.invdtx == null) {
        rectangle2D = new Rectangle2D.Float();
        rectangle2D.setRect(this.strike.getGlyphOutlineBounds(param1Int));
      } else {
        GeneralPath generalPath = this.strike.getGlyphOutline(param1Int, 0.0F, 0.0F);
        generalPath.transform(this.sgv.invdtx);
        rectangle2D = generalPath.getBounds2D();
      } 
      if (!rectangle2D.isEmpty())
        rectangle2D.setRect(rectangle2D.getMinX() + param1Float1 + this.dx, rectangle2D
            .getMinY() + param1Float2 + this.dy, rectangle2D
            .getWidth(), rectangle2D.getHeight()); 
      return rectangle2D;
    }
    
    void appendGlyphOutline(int param1Int, GeneralPath param1GeneralPath, float param1Float1, float param1Float2) {
      GeneralPath generalPath = null;
      if (this.sgv.invdtx == null) {
        generalPath = this.strike.getGlyphOutline(param1Int, param1Float1 + this.dx, param1Float2 + this.dy);
      } else {
        generalPath = this.strike.getGlyphOutline(param1Int, 0.0F, 0.0F);
        generalPath.transform(this.sgv.invdtx);
        generalPath.transform(AffineTransform.getTranslateInstance((param1Float1 + this.dx), (param1Float2 + this.dy)));
      } 
      PathIterator pathIterator = generalPath.getPathIterator((AffineTransform)null);
      param1GeneralPath.append(pathIterator, false);
    }
  }
  
  public String toString() {
    return appendString(null).toString();
  }
  
  StringBuffer appendString(StringBuffer paramStringBuffer) {
    if (paramStringBuffer == null)
      paramStringBuffer = new StringBuffer(); 
    try {
      paramStringBuffer.append("SGV{font: ");
      paramStringBuffer.append(this.font.toString());
      paramStringBuffer.append(", frc: ");
      paramStringBuffer.append(this.frc.toString());
      paramStringBuffer.append(", glyphs: (");
      paramStringBuffer.append(this.glyphs.length);
      paramStringBuffer.append(")[");
      byte b;
      for (b = 0; b < this.glyphs.length; b++) {
        if (b > 0)
          paramStringBuffer.append(", "); 
        paramStringBuffer.append(Integer.toHexString(this.glyphs[b]));
      } 
      paramStringBuffer.append("]");
      if (this.positions != null) {
        paramStringBuffer.append(", positions: (");
        paramStringBuffer.append(this.positions.length);
        paramStringBuffer.append(")[");
        for (b = 0; b < this.positions.length; b += 2) {
          if (b > 0)
            paramStringBuffer.append(", "); 
          paramStringBuffer.append(this.positions[b]);
          paramStringBuffer.append("@");
          paramStringBuffer.append(this.positions[b + 1]);
        } 
        paramStringBuffer.append("]");
      } 
      if (this.charIndices != null) {
        paramStringBuffer.append(", indices: (");
        paramStringBuffer.append(this.charIndices.length);
        paramStringBuffer.append(")[");
        for (b = 0; b < this.charIndices.length; b++) {
          if (b > 0)
            paramStringBuffer.append(", "); 
          paramStringBuffer.append(this.charIndices[b]);
        } 
        paramStringBuffer.append("]");
      } 
      paramStringBuffer.append(", flags:");
      if (getLayoutFlags() == 0) {
        paramStringBuffer.append(" default");
      } else {
        if ((this.flags & 0x1) != 0)
          paramStringBuffer.append(" tx"); 
        if ((this.flags & 0x2) != 0)
          paramStringBuffer.append(" pos"); 
        if ((this.flags & 0x4) != 0)
          paramStringBuffer.append(" rtl"); 
        if ((this.flags & 0x8) != 0)
          paramStringBuffer.append(" complex"); 
      } 
    } catch (Exception exception) {
      paramStringBuffer.append(" " + exception.getMessage());
    } 
    paramStringBuffer.append("}");
    return paramStringBuffer;
  }
  
  static final class StandardGlyphVector {}
}
