package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;

class ImageTypeProducer {
  private ImageTypeSpecifier type = null;
  
  boolean failed = false;
  
  private int csCode;
  
  public ImageTypeProducer(int paramInt) {
    this.csCode = paramInt;
  }
  
  public ImageTypeProducer() {
    this.csCode = -1;
  }
  
  public synchronized ImageTypeSpecifier getType() {
    if (!this.failed && this.type == null)
      try {
        this.type = produce();
      } catch (Throwable throwable) {
        this.failed = true;
      }  
    return this.type;
  }
  
  private static final ImageTypeProducer[] defaultTypes = new ImageTypeProducer[12];
  
  public static synchronized ImageTypeProducer getTypeProducer(int paramInt) {
    if (paramInt < 0 || paramInt >= 12)
      return null; 
    if (defaultTypes[paramInt] == null)
      defaultTypes[paramInt] = new ImageTypeProducer(paramInt); 
    return defaultTypes[paramInt];
  }
  
  protected ImageTypeSpecifier produce() {
    switch (this.csCode) {
      case 1:
        return ImageTypeSpecifier.createFromBufferedImageType(10);
      case 2:
        return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
      case 6:
        return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB, -16777216, 16711680, 65280, 255, 3, false);
      case 5:
        if (JPEG.JCS.getYCC() != null)
          return ImageTypeSpecifier.createInterleaved(
              JPEG.JCS.getYCC(), JPEG.bandOffsets[2], 0, false, false); 
        return null;
      case 10:
        if (JPEG.JCS.getYCC() != null)
          return ImageTypeSpecifier.createInterleaved(
              JPEG.JCS.getYCC(), JPEG.bandOffsets[3], 0, true, false); 
        return null;
    } 
    return null;
  }
}
