package java.awt.image;

import java.awt.Rectangle;
import java.util.Vector;

public interface RenderedImage {
  Vector<RenderedImage> getSources();
  
  Object getProperty(String paramString);
  
  String[] getPropertyNames();
  
  ColorModel getColorModel();
  
  SampleModel getSampleModel();
  
  int getWidth();
  
  int getHeight();
  
  int getMinX();
  
  int getMinY();
  
  int getNumXTiles();
  
  int getNumYTiles();
  
  int getMinTileX();
  
  int getMinTileY();
  
  int getTileWidth();
  
  int getTileHeight();
  
  int getTileGridXOffset();
  
  int getTileGridYOffset();
  
  Raster getTile(int paramInt1, int paramInt2);
  
  Raster getData();
  
  Raster getData(Rectangle paramRectangle);
  
  WritableRaster copyData(WritableRaster paramWritableRaster);
}
