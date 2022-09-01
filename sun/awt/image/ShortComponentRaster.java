package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ShortComponentRaster extends SunWritableRaster {
  protected int bandOffset;
  
  protected int[] dataOffsets;
  
  protected int scanlineStride;
  
  protected int pixelStride;
  
  protected short[] data;
  
  int type;
  
  private int maxX;
  
  private int maxY;
  
  static {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
  
  public ShortComponentRaster(SampleModel paramSampleModel, Point paramPoint) {
    this(paramSampleModel, paramSampleModel
        .createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel
          
          .getWidth(), paramSampleModel
          .getHeight()), paramPoint, (ShortComponentRaster)null);
  }
  
  public ShortComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) {
    this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel
          
          .getWidth(), paramSampleModel
          .getHeight()), paramPoint, (ShortComponentRaster)null);
  }
  
  public ShortComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ShortComponentRaster paramShortComponentRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramShortComponentRaster);
    this.maxX = this.minX + this.width;
    this.maxY = this.minY + this.height;
    if (!(paramDataBuffer instanceof DataBufferUShort))
      throw new RasterFormatException("ShortComponentRasters must have short DataBuffers"); 
    DataBufferUShort dataBufferUShort = (DataBufferUShort)paramDataBuffer;
    this.data = stealData(dataBufferUShort, 0);
    if (dataBufferUShort.getNumBanks() != 1)
      throw new RasterFormatException("DataBuffer for ShortComponentRasters must only have 1 bank."); 
    int i = dataBufferUShort.getOffset();
    if (paramSampleModel instanceof ComponentSampleModel) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      this.type = 2;
      this.scanlineStride = componentSampleModel.getScanlineStride();
      this.pixelStride = componentSampleModel.getPixelStride();
      this.dataOffsets = componentSampleModel.getBandOffsets();
      int j = paramRectangle.x - paramPoint.x;
      int k = paramRectangle.y - paramPoint.y;
      for (byte b = 0; b < getNumDataElements(); b++)
        this.dataOffsets[b] = this.dataOffsets[b] + i + j * this.pixelStride + k * this.scanlineStride; 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      this.type = 8;
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = i;
      int j = paramRectangle.x - paramPoint.x;
      int k = paramRectangle.y - paramPoint.y;
      this.dataOffsets[0] = this.dataOffsets[0] + j + k * this.scanlineStride;
    } else {
      throw new RasterFormatException("ShortComponentRasters must haveComponentSampleModel or SinglePixelPackedSampleModel");
    } 
    this.bandOffset = this.dataOffsets[0];
    verify();
  }
  
  public int[] getDataOffsets() {
    return (int[])this.dataOffsets.clone();
  }
  
  public int getDataOffset(int paramInt) {
    return this.dataOffsets[paramInt];
  }
  
  public int getScanlineStride() {
    return this.scanlineStride;
  }
  
  public int getPixelStride() {
    return this.pixelStride;
  }
  
  public short[] getDataStorage() {
    return this.data;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[this.numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      arrayOfShort[b] = this.data[this.dataOffsets[b] + i]; 
    return arrayOfShort;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    short[] arrayOfShort;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfShort = new short[paramInt3 * paramInt4 * this.numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++, i += this.scanlineStride) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++, j += this.pixelStride) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          arrayOfShort[b1++] = this.data[this.dataOffsets[b3] + j]; 
      } 
    } 
    return arrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfshort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfshort == null)
      paramArrayOfshort = new short[this.numDataElements * paramInt3 * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(this.data, i, paramArrayOfshort, 0, paramInt3 * paramInt4);
      } else {
        for (byte b = 0; b < paramInt4; b++, i += this.scanlineStride) {
          System.arraycopy(this.data, i, paramArrayOfshort, j, paramInt3);
          j += paramInt3;
        } 
      } 
    } else {
      for (byte b = 0; b < paramInt4; b++, i += this.scanlineStride) {
        int k = i;
        for (byte b1 = 0; b1 < paramInt3; b1++, k += this.pixelStride)
          paramArrayOfshort[j++] = this.data[k]; 
      } 
    } 
    return paramArrayOfshort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfshort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfshort == null)
      paramArrayOfshort = new short[this.numDataElements * paramInt3 * paramInt4]; 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++, i += this.scanlineStride) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++, j += this.pixelStride) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          paramArrayOfshort[b1++] = this.data[this.dataOffsets[b3] + j]; 
      } 
    } 
    return paramArrayOfshort;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    for (byte b = 0; b < this.numDataElements; b++)
      this.data[this.dataOffsets[b] + i] = arrayOfShort[b]; 
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if (i < this.minX || j < this.minY || i + k > this.maxX || j + m > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    setDataElements(i, j, k, m, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Raster paramRaster) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return; 
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    Object object = null;
    for (byte b = 0; b < paramInt4; b++) {
      object = paramRaster.getDataElements(i, j + b, paramInt3, 1, object);
      setDataElements(paramInt1, paramInt2 + b, paramInt3, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++, i += this.scanlineStride) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++, j += this.pixelStride) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = arrayOfShort[b1++]; 
      } 
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfshort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride + this.dataOffsets[paramInt5];
    int j = 0;
    if (this.pixelStride == 1) {
      if (this.scanlineStride == paramInt3) {
        System.arraycopy(paramArrayOfshort, 0, this.data, i, paramInt3 * paramInt4);
      } else {
        for (byte b = 0; b < paramInt4; b++, i += this.scanlineStride) {
          System.arraycopy(paramArrayOfshort, j, this.data, i, paramInt3);
          j += paramInt3;
        } 
      } 
    } else {
      for (byte b = 0; b < paramInt4; b++, i += this.scanlineStride) {
        int k = i;
        for (byte b1 = 0; b1 < paramInt3; b1++, k += this.pixelStride)
          this.data[k] = paramArrayOfshort[j++]; 
      } 
    } 
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfshort) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = (paramInt2 - this.minY) * this.scanlineStride + (paramInt1 - this.minX) * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++, i += this.scanlineStride) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++, j += this.pixelStride) {
        for (byte b3 = 0; b3 < this.numDataElements; b3++)
          this.data[this.dataOffsets[b3] + j] = paramArrayOfshort[b1++]; 
      } 
    } 
    markDirty();
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfint) {
    return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfint);
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfint) {
    SampleModel sampleModel;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("x lies outside the raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("y lies outside the raster"); 
    if (paramInt1 + paramInt3 < paramInt1 || paramInt1 + paramInt3 > this.minX + this.width)
      throw new RasterFormatException("(x + width) is outside of Raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.minY + this.height)
      throw new RasterFormatException("(y + height) is outside of Raster"); 
    if (paramArrayOfint != null) {
      sampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfint);
    } else {
      sampleModel = this.sampleModel;
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new ShortComponentRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ShortComponentRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() {
    return createCompatibleWritableRaster(this.width, this.height);
  }
  
  protected final void verify() {
    if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width)
      throw new RasterFormatException("Invalid raster dimension"); 
    int i;
    for (i = 0; i < this.dataOffsets.length; i++) {
      if (this.dataOffsets[i] < 0)
        throw new RasterFormatException("Data offsets for band " + i + "(" + this.dataOffsets[i] + ") must be >= 0"); 
    } 
    if (this.minX - this.sampleModelTranslateX < 0L || this.minY - this.sampleModelTranslateY < 0L)
      throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride); 
    if (this.height > 1 || this.minY - this.sampleModelTranslateY > 0)
      if (this.scanlineStride > this.data.length)
        throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);  
    i = (this.height - 1) * this.scanlineStride;
    if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width || this.pixelStride > this.data.length)
      throw new RasterFormatException("Incorrect pixel stride: " + this.pixelStride); 
    int j = (this.width - 1) * this.pixelStride;
    if (j > Integer.MAX_VALUE - i)
      throw new RasterFormatException("Incorrect raster attributes"); 
    j += i;
    int k = 0;
    for (byte b = 0; b < this.numDataElements; b++) {
      if (this.dataOffsets[b] > Integer.MAX_VALUE - j)
        throw new RasterFormatException("Incorrect band offset: " + this.dataOffsets[b]); 
      int m = j + this.dataOffsets[b];
      if (m > k)
        k = m; 
    } 
    if (this.data.length <= k)
      throw new RasterFormatException("Data array too small (should be > " + k + " )"); 
  }
  
  public String toString() {
    return new String("ShortComponentRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements);
  }
  
  private static native void initIDs();
}
