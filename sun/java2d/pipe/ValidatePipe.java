package sun.java2d.pipe;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;

public class ValidatePipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, TextPipe, DrawImagePipe {
  public boolean validate(SunGraphics2D paramSunGraphics2D) {
    paramSunGraphics2D.validatePipe();
    return true;
  }
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawLine(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.fillpipe.fillRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawRoundRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); 
  }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.fillpipe.fillRoundRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); 
  }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawOval(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.fillpipe.fillOval(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawArc(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); 
  }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.fillpipe.fillArc(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); 
  }
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawPolyline(paramSunGraphics2D, paramArrayOfint1, paramArrayOfint2, paramInt); 
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.drawpipe.drawPolygon(paramSunGraphics2D, paramArrayOfint1, paramArrayOfint2, paramInt); 
  }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.fillpipe.fillPolygon(paramSunGraphics2D, paramArrayOfint1, paramArrayOfint2, paramInt); 
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, paramShape); 
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, paramShape); 
  }
  
  public void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.textpipe.drawString(paramSunGraphics2D, paramString, paramDouble1, paramDouble2); 
  }
  
  public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.textpipe.drawGlyphVector(paramSunGraphics2D, paramGlyphVector, paramFloat1, paramFloat2); 
  }
  
  public void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.textpipe.drawChars(paramSunGraphics2D, paramArrayOfchar, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    if (validate(paramSunGraphics2D))
      return paramSunGraphics2D.imagepipe.copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver); 
    return false;
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver) {
    if (validate(paramSunGraphics2D))
      return paramSunGraphics2D.imagepipe.copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver); 
    return false;
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    if (validate(paramSunGraphics2D))
      return paramSunGraphics2D.imagepipe.scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver); 
    return false;
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (validate(paramSunGraphics2D))
      return paramSunGraphics2D.imagepipe.scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver); 
    return false;
  }
  
  public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    if (validate(paramSunGraphics2D))
      return paramSunGraphics2D.imagepipe.transformImage(paramSunGraphics2D, paramImage, paramAffineTransform, paramImageObserver); 
    return false;
  }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (validate(paramSunGraphics2D))
      paramSunGraphics2D.imagepipe.transformImage(paramSunGraphics2D, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2); 
  }
}
