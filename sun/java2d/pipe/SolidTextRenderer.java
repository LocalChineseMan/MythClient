package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class SolidTextRenderer extends GlyphListLoopPipe implements LoopBasedPipe {
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) {
    paramSunGraphics2D.loops.drawGlyphListLoop.DrawGlyphList(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
  }
}
