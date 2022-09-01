package sun.font;

import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PhysicalStrike extends FontStrike {
  static final long INTMASK = 4294967295L;
  
  static boolean longAddresses;
  
  private PhysicalFont physicalFont;
  
  protected CharToGlyphMapper mapper;
  
  protected long pScalerContext;
  
  protected long[] longGlyphImages;
  
  protected int[] intGlyphImages;
  
  ConcurrentHashMap<Integer, Point2D.Float> glyphPointMapCache;
  
  protected boolean getImageWithAdvance;
  
  protected static final int complexTX = 124;
  
  static {
    switch (StrikeCache.nativeAddressSize) {
      case 8:
        longAddresses = true;
        return;
      case 4:
        longAddresses = false;
        return;
    } 
    throw new RuntimeException("Unexpected address size");
  }
  
  PhysicalStrike(PhysicalFont paramPhysicalFont, FontStrikeDesc paramFontStrikeDesc) {
    this.physicalFont = paramPhysicalFont;
    this.desc = paramFontStrikeDesc;
  }
  
  protected PhysicalStrike() {}
  
  public int getNumGlyphs() {
    return this.physicalFont.getNumGlyphs();
  }
  
  StrikeMetrics getFontMetrics() {
    if (this.strikeMetrics == null)
      this
        .strikeMetrics = this.physicalFont.getFontMetrics(this.pScalerContext); 
    return this.strikeMetrics;
  }
  
  float getCodePointAdvance(int paramInt) {
    return getGlyphAdvance(this.physicalFont.getMapper().charToGlyph(paramInt));
  }
  
  Point2D.Float getCharMetrics(char paramChar) {
    return getGlyphMetrics(this.physicalFont.getMapper().charToGlyph(paramChar));
  }
  
  int getSlot0GlyphImagePtrs(int[] paramArrayOfint, long[] paramArrayOflong, int paramInt) {
    return 0;
  }
  
  Point2D.Float getGlyphPoint(int paramInt1, int paramInt2) {
    Point2D.Float float_ = null;
    Integer integer = Integer.valueOf(paramInt1 << 16 | paramInt2);
    if (this.glyphPointMapCache == null) {
      synchronized (this) {
        if (this.glyphPointMapCache == null)
          this.glyphPointMapCache = new ConcurrentHashMap<>(); 
      } 
    } else {
      float_ = this.glyphPointMapCache.get(integer);
    } 
    if (float_ == null) {
      float_ = this.physicalFont.getGlyphPoint(this.pScalerContext, paramInt1, paramInt2);
      adjustPoint(float_);
      this.glyphPointMapCache.put(integer, float_);
    } 
    return float_;
  }
  
  protected void adjustPoint(Point2D.Float paramFloat) {}
}
