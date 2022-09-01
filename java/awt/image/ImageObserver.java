package java.awt.image;

import java.awt.Image;

public interface ImageObserver {
  public static final int WIDTH = 1;
  
  public static final int HEIGHT = 2;
  
  public static final int PROPERTIES = 4;
  
  public static final int SOMEBITS = 8;
  
  public static final int FRAMEBITS = 16;
  
  public static final int ALLBITS = 32;
  
  public static final int ERROR = 64;
  
  public static final int ABORT = 128;
  
  boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
}
