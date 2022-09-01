package sun.awt.image;

import java.awt.Image;
import java.util.List;

public interface MultiResolutionImage {
  Image getResolutionVariant(int paramInt1, int paramInt2);
  
  List<Image> getResolutionVariants();
}
