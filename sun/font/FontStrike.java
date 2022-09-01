package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class FontStrike {
  protected FontStrikeDisposer disposer;
  
  protected FontStrikeDesc desc;
  
  protected StrikeMetrics strikeMetrics;
  
  protected boolean algoStyle = false;
  
  protected float boldness = 1.0F;
  
  protected float italic = 0.0F;
  
  public abstract int getNumGlyphs();
  
  abstract StrikeMetrics getFontMetrics();
  
  abstract void getGlyphImagePtrs(int[] paramArrayOfint, long[] paramArrayOflong, int paramInt);
  
  abstract long getGlyphImagePtr(int paramInt);
  
  abstract void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle);
  
  abstract Point2D.Float getGlyphMetrics(int paramInt);
  
  abstract Point2D.Float getCharMetrics(char paramChar);
  
  abstract float getGlyphAdvance(int paramInt);
  
  abstract float getCodePointAdvance(int paramInt);
  
  abstract Rectangle2D.Float getGlyphOutlineBounds(int paramInt);
  
  abstract GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2);
  
  abstract GeneralPath getGlyphVectorOutline(int[] paramArrayOfint, float paramFloat1, float paramFloat2);
}
