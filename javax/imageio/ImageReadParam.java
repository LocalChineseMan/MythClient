package javax.imageio;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageReadParam extends IIOParam {
  protected boolean canSetSourceRenderSize = false;
  
  protected Dimension sourceRenderSize = null;
  
  protected BufferedImage destination = null;
  
  protected int[] destinationBands = null;
  
  protected int minProgressivePass = 0;
  
  protected int numProgressivePasses = Integer.MAX_VALUE;
  
  public void setDestinationType(ImageTypeSpecifier paramImageTypeSpecifier) {
    super.setDestinationType(paramImageTypeSpecifier);
    setDestination(null);
  }
  
  public void setDestination(BufferedImage paramBufferedImage) {
    this.destination = paramBufferedImage;
  }
  
  public BufferedImage getDestination() {
    return this.destination;
  }
  
  public void setDestinationBands(int[] paramArrayOfint) {
    if (paramArrayOfint == null) {
      this.destinationBands = null;
    } else {
      int i = paramArrayOfint.length;
      for (byte b = 0; b < i; b++) {
        int j = paramArrayOfint[b];
        if (j < 0)
          throw new IllegalArgumentException("Band value < 0!"); 
        for (int k = b + 1; k < i; k++) {
          if (j == paramArrayOfint[k])
            throw new IllegalArgumentException("Duplicate band value!"); 
        } 
      } 
      this.destinationBands = (int[])paramArrayOfint.clone();
    } 
  }
  
  public int[] getDestinationBands() {
    if (this.destinationBands == null)
      return null; 
    return (int[])this.destinationBands.clone();
  }
  
  public boolean canSetSourceRenderSize() {
    return this.canSetSourceRenderSize;
  }
  
  public void setSourceRenderSize(Dimension paramDimension) throws UnsupportedOperationException {
    if (!canSetSourceRenderSize())
      throw new UnsupportedOperationException("Can't set source render size!"); 
    if (paramDimension == null) {
      this.sourceRenderSize = null;
    } else {
      if (paramDimension.width <= 0 || paramDimension.height <= 0)
        throw new IllegalArgumentException("width or height <= 0!"); 
      this.sourceRenderSize = (Dimension)paramDimension.clone();
    } 
  }
  
  public Dimension getSourceRenderSize() {
    return (this.sourceRenderSize == null) ? null : (Dimension)this.sourceRenderSize
      .clone();
  }
  
  public void setSourceProgressivePasses(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("minPass < 0!"); 
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("numPasses <= 0!"); 
    if (paramInt2 != Integer.MAX_VALUE && (paramInt1 + paramInt2 - 1 & Integer.MIN_VALUE) != 0)
      throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!"); 
    this.minProgressivePass = paramInt1;
    this.numProgressivePasses = paramInt2;
  }
  
  public int getSourceMinProgressivePass() {
    return this.minProgressivePass;
  }
  
  public int getSourceMaxProgressivePass() {
    if (this.numProgressivePasses == Integer.MAX_VALUE)
      return Integer.MAX_VALUE; 
    return this.minProgressivePass + this.numProgressivePasses - 1;
  }
  
  public int getSourceNumProgressivePasses() {
    return this.numProgressivePasses;
  }
}
