package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

public interface PaintContext {
  void dispose();
  
  ColorModel getColorModel();
  
  Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}
