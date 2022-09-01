package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel extends SampleModel {
  private int[] bitMasks;
  
  private int[] bitOffsets;
  
  private int[] bitSizes;
  
  private int maxBitSize;
  
  private int scanlineStride;
  
  static {
    ColorModel.loadLibraries();
    initIDs();
  }
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
    this(paramInt1, paramInt2, paramInt3, paramInt2, paramArrayOfint);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
  }
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfint.length);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
    this.dataType = paramInt1;
    this.bitMasks = (int[])paramArrayOfint.clone();
    this.scanlineStride = paramInt4;
    this.bitOffsets = new int[this.numBands];
    this.bitSizes = new int[this.numBands];
    int i = (int)((1L << DataBuffer.getDataTypeSize(paramInt1)) - 1L);
    this.maxBitSize = 0;
    for (byte b = 0; b < this.numBands; b++) {
      byte b1 = 0, b2 = 0;
      this.bitMasks[b] = this.bitMasks[b] & i;
      int j = this.bitMasks[b];
      if (j != 0) {
        while ((j & 0x1) == 0) {
          j >>>= 1;
          b1++;
        } 
        while ((j & 0x1) == 1) {
          j >>>= 1;
          b2++;
        } 
        if (j != 0)
          throw new IllegalArgumentException("Mask " + paramArrayOfint[b] + " must be contiguous"); 
      } 
      this.bitOffsets[b] = b1;
      this.bitSizes[b] = b2;
      if (b2 > this.maxBitSize)
        this.maxBitSize = b2; 
    } 
  }
  
  public int getNumDataElements() {
    return 1;
  }
  
  private long getBufferSize() {
    return (this.scanlineStride * (this.height - 1) + this.width);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    return new SinglePixelPackedSampleModel(this.dataType, paramInt1, paramInt2, this.bitMasks);
  }
  
  public DataBuffer createDataBuffer() {
    DataBufferUShort dataBufferUShort;
    DataBufferInt dataBufferInt;
    DataBufferByte dataBufferByte = null;
    int i = (int)getBufferSize();
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i);
        break;
    } 
    return dataBufferInt;
  }
  
  public int[] getSampleSize() {
    return (int[])this.bitSizes.clone();
  }
  
  public int getSampleSize(int paramInt) {
    return this.bitSizes[paramInt];
  }
  
  public int getOffset(int paramInt1, int paramInt2) {
    return paramInt2 * this.scanlineStride + paramInt1;
  }
  
  public int[] getBitOffsets() {
    return (int[])this.bitOffsets.clone();
  }
  
  public int[] getBitMasks() {
    return (int[])this.bitMasks.clone();
  }
  
  public int getScanlineStride() {
    return this.scanlineStride;
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfint) {
    if (paramArrayOfint.length > this.numBands)
      throw new RasterFormatException("There are only " + this.numBands + " bands"); 
    int[] arrayOfInt = new int[paramArrayOfint.length];
    for (byte b = 0; b < paramArrayOfint.length; b++)
      arrayOfInt[b] = this.bitMasks[paramArrayOfint[b]]; 
    return new SinglePixelPackedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, arrayOfInt);
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[1];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        arrayOfByte[0] = (byte)paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        paramObject = arrayOfByte;
        break;
      case 1:
        if (paramObject == null) {
          arrayOfShort = new short[1];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        arrayOfShort[0] = (short)paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        paramObject = arrayOfShort;
        break;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[1];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        arrayOfInt[0] = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
        paramObject = arrayOfInt;
        break;
    } 
    return paramObject;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfint == null) {
      arrayOfInt = new int[this.numBands];
    } else {
      arrayOfInt = paramArrayOfint;
    } 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = (i & this.bitMasks[b]) >>> this.bitOffsets[b]; 
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int arrayOfInt[], i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    } 
    int k = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (byte b = 0; b < paramInt3; b++) {
        int m = paramDataBuffer.getElem(k + b);
        for (byte b3 = 0; b3 < this.numBands; b3++)
          arrayOfInt[b1++] = (m & this.bitMasks[b3]) >>> this.bitOffsets[b3]; 
      } 
      k += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    return (i & this.bitMasks[paramInt3]) >>> this.bitOffsets[paramInt3];
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (byte b = 0; b < paramInt3; b++) {
        int j = paramDataBuffer.getElem(i + b);
        arrayOfInt[b1++] = (j & this.bitMasks[paramInt5]) >>> this.bitOffsets[paramInt5];
      } 
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfByte[0] & 0xFF);
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfShort[0] & 0xFFFF);
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, arrayOfInt[0]);
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    int j = paramDataBuffer.getElem(i);
    for (byte b = 0; b < this.numBands; b++) {
      j &= this.bitMasks[b] ^ 0xFFFFFFFF;
      j |= paramArrayOfint[b] << this.bitOffsets[b] & this.bitMasks[b];
    } 
    paramDataBuffer.setElem(i, j);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int k = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (byte b = 0; b < paramInt3; b++) {
        int m = paramDataBuffer.getElem(k + b);
        for (byte b3 = 0; b3 < this.numBands; b3++) {
          m &= this.bitMasks[b3] ^ 0xFFFFFFFF;
          int n = paramArrayOfint[b1++];
          m |= n << this.bitOffsets[b3] & this.bitMasks[b3];
        } 
        paramDataBuffer.setElem(k + b, m);
      } 
      k += this.scanlineStride;
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + paramInt1);
    i &= this.bitMasks[paramInt3] ^ 0xFFFFFFFF;
    i |= paramInt4 << this.bitOffsets[paramInt3] & this.bitMasks[paramInt3];
    paramDataBuffer.setElem(paramInt2 * this.scanlineStride + paramInt1, i);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      for (byte b = 0; b < paramInt3; b++) {
        int j = paramDataBuffer.getElem(i + b);
        j &= this.bitMasks[paramInt5] ^ 0xFFFFFFFF;
        int k = paramArrayOfint[b1++];
        j |= k << this.bitOffsets[paramInt5] & this.bitMasks[paramInt5];
        paramDataBuffer.setElem(i + b, j);
      } 
      i += this.scanlineStride;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof SinglePixelPackedSampleModel))
      return false; 
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramObject;
    return (this.width == singlePixelPackedSampleModel.width && this.height == singlePixelPackedSampleModel.height && this.numBands == singlePixelPackedSampleModel.numBands && this.dataType == singlePixelPackedSampleModel.dataType && 
      
      Arrays.equals(this.bitMasks, singlePixelPackedSampleModel.bitMasks) && 
      Arrays.equals(this.bitOffsets, singlePixelPackedSampleModel.bitOffsets) && 
      Arrays.equals(this.bitSizes, singlePixelPackedSampleModel.bitSizes) && this.maxBitSize == singlePixelPackedSampleModel.maxBitSize && this.scanlineStride == singlePixelPackedSampleModel.scanlineStride);
  }
  
  public int hashCode() {
    int i = 0;
    i = this.width;
    i <<= 8;
    i ^= this.height;
    i <<= 8;
    i ^= this.numBands;
    i <<= 8;
    i ^= this.dataType;
    i <<= 8;
    byte b;
    for (b = 0; b < this.bitMasks.length; b++) {
      i ^= this.bitMasks[b];
      i <<= 8;
    } 
    for (b = 0; b < this.bitOffsets.length; b++) {
      i ^= this.bitOffsets[b];
      i <<= 8;
    } 
    for (b = 0; b < this.bitSizes.length; b++) {
      i ^= this.bitSizes[b];
      i <<= 8;
    } 
    i ^= this.maxBitSize;
    i <<= 8;
    i ^= this.scanlineStride;
    return i;
  }
  
  private static native void initIDs();
}
