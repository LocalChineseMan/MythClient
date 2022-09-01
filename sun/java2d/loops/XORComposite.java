package sun.java2d.loops;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;
import sun.java2d.SurfaceData;

public final class XORComposite implements Composite {
  Color xorColor;
  
  int xorPixel;
  
  int alphaMask;
  
  public XORComposite(Color paramColor, SurfaceData paramSurfaceData) {
    this.xorColor = paramColor;
    SurfaceType surfaceType = paramSurfaceData.getSurfaceType();
    this.xorPixel = paramSurfaceData.pixelFor(paramColor.getRGB());
    this.alphaMask = surfaceType.getAlphaMask();
  }
  
  public Color getXorColor() {
    return this.xorColor;
  }
  
  public int getXorPixel() {
    return this.xorPixel;
  }
  
  public int getAlphaMask() {
    return this.alphaMask;
  }
  
  public CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints) {
    return new SunCompositeContext(this, paramColorModel1, paramColorModel2);
  }
}
