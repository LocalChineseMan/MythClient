package sun.java2d.pipe;

import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface ShapeDrawPipe {
  void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape);
  
  void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape);
}
