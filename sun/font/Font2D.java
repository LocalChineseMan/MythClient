package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Font2D {
  public static final int FONT_CONFIG_RANK = 2;
  
  public static final int JRE_RANK = 2;
  
  public static final int TTF_RANK = 3;
  
  public static final int TYPE1_RANK = 4;
  
  public static final int NATIVE_RANK = 5;
  
  public static final int UNKNOWN_RANK = 6;
  
  public static final int DEFAULT_RANK = 4;
  
  private static final String[] boldNames = new String[] { "bold", "demibold", "demi-bold", "demi bold", "negreta", "demi" };
  
  private static final String[] italicNames = new String[] { "italic", "cursiva", "oblique", "inclined" };
  
  private static final String[] boldItalicNames = new String[] { "bolditalic", "bold-italic", "bold italic", "boldoblique", "bold-oblique", "bold oblique", "demibold italic", "negreta cursiva", "demi oblique" };
  
  private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
  
  public Font2DHandle handle;
  
  protected String familyName;
  
  protected String fullName;
  
  protected int style = 0;
  
  protected FontFamily family;
  
  protected int fontRank = 4;
  
  protected CharToGlyphMapper mapper;
  
  protected ConcurrentHashMap<FontStrikeDesc, Reference> strikeCache = new ConcurrentHashMap<>();
  
  protected Reference lastFontStrike = new SoftReference(null);
  
  public int getStyle() {
    return this.style;
  }
  
  protected void setStyle() {
    String str = this.fullName.toLowerCase();
    byte b;
    for (b = 0; b < boldItalicNames.length; b++) {
      if (str.indexOf(boldItalicNames[b]) != -1) {
        this.style = 3;
        return;
      } 
    } 
    for (b = 0; b < italicNames.length; b++) {
      if (str.indexOf(italicNames[b]) != -1) {
        this.style = 2;
        return;
      } 
    } 
    for (b = 0; b < boldNames.length; b++) {
      if (str.indexOf(boldNames[b]) != -1) {
        this.style = 1;
        return;
      } 
    } 
  }
  
  int getRank() {
    return this.fontRank;
  }
  
  void setRank(int paramInt) {
    this.fontRank = paramInt;
  }
  
  abstract CharToGlyphMapper getMapper();
  
  protected int getValidatedGlyphCode(int paramInt) {
    if (paramInt < 0 || paramInt >= getMapper().getNumGlyphs())
      paramInt = getMapper().getMissingGlyphCode(); 
    return paramInt;
  }
  
  abstract FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc);
  
  public FontStrike getStrike(Font paramFont) {
    FontStrike fontStrike = this.lastFontStrike.get();
    if (fontStrike != null)
      return fontStrike; 
    return getStrike(paramFont, DEFAULT_FRC);
  }
  
  public FontStrike getStrike(Font paramFont, AffineTransform paramAffineTransform, int paramInt1, int paramInt2) {
    double d = paramFont.getSize2D();
    AffineTransform affineTransform = (AffineTransform)paramAffineTransform.clone();
    affineTransform.scale(d, d);
    if (paramFont.isTransformed())
      affineTransform.concatenate(paramFont.getTransform()); 
    if (affineTransform.getTranslateX() != 0.0D || affineTransform.getTranslateY() != 0.0D)
      affineTransform.setTransform(affineTransform.getScaleX(), affineTransform
          .getShearY(), affineTransform
          .getShearX(), affineTransform
          .getScaleY(), 0.0D, 0.0D); 
    FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(paramAffineTransform, affineTransform, paramFont.getStyle(), paramInt1, paramInt2);
    return getStrike(fontStrikeDesc, false);
  }
  
  public FontStrike getStrike(Font paramFont, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2) {
    FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(paramAffineTransform1, paramAffineTransform2, paramFont.getStyle(), paramInt1, paramInt2);
    return getStrike(fontStrikeDesc, false);
  }
  
  public FontStrike getStrike(Font paramFont, FontRenderContext paramFontRenderContext) {
    AffineTransform affineTransform = paramFontRenderContext.getTransform();
    double d = paramFont.getSize2D();
    affineTransform.scale(d, d);
    if (paramFont.isTransformed()) {
      affineTransform.concatenate(paramFont.getTransform());
      if (affineTransform.getTranslateX() != 0.0D || affineTransform.getTranslateY() != 0.0D)
        affineTransform.setTransform(affineTransform.getScaleX(), affineTransform
            .getShearY(), affineTransform
            .getShearX(), affineTransform
            .getScaleY(), 0.0D, 0.0D); 
    } 
    int i = FontStrikeDesc.getAAHintIntVal(this, paramFont, paramFontRenderContext);
    int j = FontStrikeDesc.getFMHintIntVal(paramFontRenderContext.getFractionalMetricsHint());
    FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(paramFontRenderContext.getTransform(), affineTransform, paramFont.getStyle(), i, j);
    return getStrike(fontStrikeDesc, false);
  }
  
  FontStrike getStrike(FontStrikeDesc paramFontStrikeDesc) {
    return getStrike(paramFontStrikeDesc, true);
  }
  
  private FontStrike getStrike(FontStrikeDesc paramFontStrikeDesc, boolean paramBoolean) {
    FontStrike fontStrike = this.lastFontStrike.get();
    if (fontStrike != null && paramFontStrikeDesc.equals(fontStrike.desc))
      return fontStrike; 
    Reference<FontStrike> reference = this.strikeCache.get(paramFontStrikeDesc);
    if (reference != null) {
      fontStrike = reference.get();
      if (fontStrike != null) {
        this.lastFontStrike = new SoftReference<>(fontStrike);
        StrikeCache.refStrike(fontStrike);
        return fontStrike;
      } 
    } 
    if (paramBoolean)
      paramFontStrikeDesc = new FontStrikeDesc(paramFontStrikeDesc); 
    fontStrike = createStrike(paramFontStrikeDesc);
    int i = paramFontStrikeDesc.glyphTx.getType();
    if (i == 32 || ((i & 0x10) != 0 && this.strikeCache
      
      .size() > 10)) {
      reference = StrikeCache.getStrikeRef(fontStrike, true);
    } else {
      reference = StrikeCache.getStrikeRef(fontStrike);
    } 
    this.strikeCache.put(paramFontStrikeDesc, reference);
    this.lastFontStrike = new SoftReference<>(fontStrike);
    StrikeCache.refStrike(fontStrike);
    return fontStrike;
  }
  
  void removeFromCache(FontStrikeDesc paramFontStrikeDesc) {
    Reference<Object> reference = this.strikeCache.get(paramFontStrikeDesc);
    if (reference != null) {
      Object object = reference.get();
      if (object == null)
        this.strikeCache.remove(paramFontStrikeDesc); 
    } 
  }
  
  public void getFontMetrics(Font paramFont, AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2, float[] paramArrayOffloat) {
    int i = FontStrikeDesc.getAAHintIntVal(paramObject1, this, paramFont.getSize());
    int j = FontStrikeDesc.getFMHintIntVal(paramObject2);
    FontStrike fontStrike = getStrike(paramFont, paramAffineTransform, i, j);
    StrikeMetrics strikeMetrics = fontStrike.getFontMetrics();
    paramArrayOffloat[0] = strikeMetrics.getAscent();
    paramArrayOffloat[1] = strikeMetrics.getDescent();
    paramArrayOffloat[2] = strikeMetrics.getLeading();
    paramArrayOffloat[3] = strikeMetrics.getMaxAdvance();
    getStyleMetrics(paramFont.getSize2D(), paramArrayOffloat, 4);
  }
  
  public void getStyleMetrics(float paramFloat, float[] paramArrayOffloat, int paramInt) {
    paramArrayOffloat[paramInt] = -paramArrayOffloat[0] / 2.5F;
    paramArrayOffloat[paramInt + 1] = paramFloat / 12.0F;
    paramArrayOffloat[paramInt + 2] = paramArrayOffloat[paramInt + 1] / 1.5F;
    paramArrayOffloat[paramInt + 3] = paramArrayOffloat[paramInt + 1];
  }
  
  public void getFontMetrics(Font paramFont, FontRenderContext paramFontRenderContext, float[] paramArrayOffloat) {
    StrikeMetrics strikeMetrics = getStrike(paramFont, paramFontRenderContext).getFontMetrics();
    paramArrayOffloat[0] = strikeMetrics.getAscent();
    paramArrayOffloat[1] = strikeMetrics.getDescent();
    paramArrayOffloat[2] = strikeMetrics.getLeading();
    paramArrayOffloat[3] = strikeMetrics.getMaxAdvance();
  }
  
  byte[] getTableBytes(int paramInt) {
    return null;
  }
  
  protected long getUnitsPerEm() {
    return 2048L;
  }
  
  boolean supportsEncoding(String paramString) {
    return false;
  }
  
  public boolean canDoStyle(int paramInt) {
    return (paramInt == this.style);
  }
  
  public boolean useAAForPtSize(int paramInt) {
    return true;
  }
  
  public boolean hasSupplementaryChars() {
    return false;
  }
  
  public String getPostscriptName() {
    return this.fullName;
  }
  
  public String getFontName(Locale paramLocale) {
    return this.fullName;
  }
  
  public String getFamilyName(Locale paramLocale) {
    return this.familyName;
  }
  
  public int getNumGlyphs() {
    return getMapper().getNumGlyphs();
  }
  
  public int charToGlyph(int paramInt) {
    return getMapper().charToGlyph(paramInt);
  }
  
  public int getMissingGlyphCode() {
    return getMapper().getMissingGlyphCode();
  }
  
  public boolean canDisplay(char paramChar) {
    return getMapper().canDisplay(paramChar);
  }
  
  public boolean canDisplay(int paramInt) {
    return getMapper().canDisplay(paramInt);
  }
  
  public byte getBaselineFor(char paramChar) {
    return 0;
  }
  
  public float getItalicAngle(Font paramFont, AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2) {
    int i = FontStrikeDesc.getAAHintIntVal(paramObject1, this, 12);
    int j = FontStrikeDesc.getFMHintIntVal(paramObject2);
    FontStrike fontStrike = getStrike(paramFont, paramAffineTransform, i, j);
    StrikeMetrics strikeMetrics = fontStrike.getFontMetrics();
    if (strikeMetrics.ascentY == 0.0F || strikeMetrics.ascentX == 0.0F)
      return 0.0F; 
    return strikeMetrics.ascentX / -strikeMetrics.ascentY;
  }
}
