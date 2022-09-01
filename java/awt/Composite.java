package java.awt;

import java.awt.image.ColorModel;

public interface Composite {
  CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints);
}
