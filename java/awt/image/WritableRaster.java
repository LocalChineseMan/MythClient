package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class WritableRaster extends Raster {
  protected WritableRaster(SampleModel paramSampleModel, Point paramPoint) {
    this(paramSampleModel, paramSampleModel
        .createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel
          
          .getWidth(), paramSampleModel
          .getHeight()), paramPoint, (WritableRaster)null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) {
    this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel
          
          .getWidth(), paramSampleModel
          .getHeight()), paramPoint, (WritableRaster)null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, WritableRaster paramWritableRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramWritableRaster);
  }
  
  public WritableRaster getWritableParent() {
    return (WritableRaster)this.parent;
  }
  
  public WritableRaster createWritableTranslatedChild(int paramInt1, int paramInt2) {
    return createWritableChild(this.minX, this.minY, this.width, this.height, paramInt1, paramInt2, (int[])null);
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfint) {
    SampleModel sampleModel;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("parentX lies outside raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("parentY lies outside raster"); 
    if (paramInt1 + paramInt3 < paramInt1 || paramInt1 + paramInt3 > this.width + this.minX)
      throw new RasterFormatException("(parentX + width) is outside raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.height + this.minY)
      throw new RasterFormatException("(parentY + height) is outside raster"); 
    if (paramArrayOfint != null) {
      sampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfint);
    } else {
      sampleModel = this.sampleModel;
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new WritableRaster(sampleModel, 
        getDataBuffer(), new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    this.sampleModel.setDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramObject, this.dataBuffer);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if (i < this.minX || j < this.minY || i + k > this.minX + this.width || j + m > this.minY + this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int n = paramRaster.getMinX();
    int i1 = paramRaster.getMinY();
    Object object = null;
    for (byte b = 0; b < m; b++) {
      object = paramRaster.getDataElements(n, i1 + b, k, 1, object);
      setDataElements(i, j + b, k, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    this.sampleModel.setDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramObject, this.dataBuffer);
  }
  
  public void setRect(Raster paramRaster) {
    setRect(0, 0, paramRaster);
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster) {
    int[] arrayOfInt;
    byte b1;
    float[] arrayOfFloat;
    byte b2;
    double[] arrayOfDouble;
    byte b3;
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    if (n < this.minX) {
      int i2 = this.minX - n;
      i -= i2;
      k += i2;
      n = this.minX;
    } 
    if (i1 < this.minY) {
      int i2 = this.minY - i1;
      j -= i2;
      m += i2;
      i1 = this.minY;
    } 
    if (n + i > this.minX + this.width)
      i = this.minX + this.width - n; 
    if (i1 + j > this.minY + this.height)
      j = this.minY + this.height - i1; 
    if (i <= 0 || j <= 0)
      return; 
    switch (paramRaster.getSampleModel().getDataType()) {
      case 0:
      case 1:
      case 2:
      case 3:
        arrayOfInt = null;
        for (b1 = 0; b1 < j; b1++) {
          arrayOfInt = paramRaster.getPixels(k, m + b1, i, 1, arrayOfInt);
          setPixels(n, i1 + b1, i, 1, arrayOfInt);
        } 
        break;
      case 4:
        arrayOfFloat = null;
        for (b2 = 0; b2 < j; b2++) {
          arrayOfFloat = paramRaster.getPixels(k, m + b2, i, 1, arrayOfFloat);
          setPixels(n, i1 + b2, i, 1, arrayOfFloat);
        } 
        break;
      case 5:
        arrayOfDouble = null;
        for (b3 = 0; b3 < j; b3++) {
          arrayOfDouble = paramRaster.getPixels(k, m + b3, i, 1, arrayOfDouble);
          setPixels(n, i1 + b3, i, 1, arrayOfDouble);
        } 
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfint, this.dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOffloat) {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOffloat, this.dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfdouble) {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfdouble, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfint, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOffloat) {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOffloat, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfdouble) {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfdouble, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat) {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramFloat, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble) {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramDouble, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint) {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfint, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOffloat) {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOffloat, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfdouble) {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfdouble, this.dataBuffer);
  }
}
