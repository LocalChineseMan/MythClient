package sun.java2d.loops;

import java.awt.Font;
import sun.font.Font2D;
import sun.font.FontStrike;

public class FontInfo implements Cloneable {
  public Font font;
  
  public Font2D font2D;
  
  public FontStrike fontStrike;
  
  public double[] devTx;
  
  public double[] glyphTx;
  
  public int pixelHeight;
  
  public float originX;
  
  public float originY;
  
  public int aaHint;
  
  public boolean lcdRGBOrder;
  
  public boolean lcdSubPixPos;
  
  public String mtx(double[] paramArrayOfdouble) {
    return "[" + paramArrayOfdouble[0] + ", " + paramArrayOfdouble[1] + ", " + paramArrayOfdouble[2] + ", " + paramArrayOfdouble[3] + "]";
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String toString() {
    return "FontInfo[font=" + this.font + ", " + "devTx=" + 
      
      mtx(this.devTx) + ", " + "glyphTx=" + 
      mtx(this.glyphTx) + ", " + "pixelHeight=" + this.pixelHeight + ", " + "origin=(" + this.originX + "," + this.originY + "), " + "aaHint=" + this.aaHint + ", " + "lcdRGBOrder=" + (this.lcdRGBOrder ? "RGB" : "BGR") + "lcdSubPixPos=" + this.lcdSubPixPos + "]";
  }
}
