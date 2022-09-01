package sun.awt.image;

import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufImgSurfaceManager extends SurfaceManager {
  protected BufferedImage bImg;
  
  protected SurfaceData sdDefault;
  
  public BufImgSurfaceManager(BufferedImage paramBufferedImage) {
    this.bImg = paramBufferedImage;
    this.sdDefault = BufImgSurfaceData.createData(paramBufferedImage);
  }
  
  public SurfaceData getPrimarySurfaceData() {
    return this.sdDefault;
  }
  
  public SurfaceData restoreContents() {
    return this.sdDefault;
  }
}
