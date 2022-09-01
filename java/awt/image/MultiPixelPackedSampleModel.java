package java.awt.image;

public class MultiPixelPackedSampleModel extends SampleModel {
  int pixelBitStride;
  
  int bitMask;
  
  int pixelsPerDataElement;
  
  int dataElementSize;
  
  int dataBitOffset;
  
  int scanlineStride;
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (paramInt2 * paramInt4 + 
        
        DataBuffer.getDataTypeSize(paramInt1) - 1) / 
        DataBuffer.getDataTypeSize(paramInt1), 0);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
  }
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    super(paramInt1, paramInt2, paramInt3, 1);
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 3)
      throw new IllegalArgumentException("Unsupported data type " + paramInt1); 
    this.dataType = paramInt1;
    this.pixelBitStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.dataBitOffset = paramInt6;
    this.dataElementSize = DataBuffer.getDataTypeSize(paramInt1);
    this.pixelsPerDataElement = this.dataElementSize / paramInt4;
    if (this.pixelsPerDataElement * paramInt4 != this.dataElementSize)
      throw new RasterFormatException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries"); 
    this.bitMask = (1 << paramInt4) - 1;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    return new MultiPixelPackedSampleModel(this.dataType, paramInt1, paramInt2, this.pixelBitStride);
  }
  
  public DataBuffer createDataBuffer() {
    DataBufferUShort dataBufferUShort;
    DataBufferInt dataBufferInt;
    DataBufferByte dataBufferByte = null;
    int i = this.scanlineStride * this.height;
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i + (this.dataBitOffset + 7) / 8);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i + (this.dataBitOffset + 15) / 16);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i + (this.dataBitOffset + 31) / 32);
        break;
    } 
    return dataBufferInt;
  }
  
  public int getNumDataElements() {
    return 1;
  }
  
  public int[] getSampleSize() {
    return new int[] { this.pixelBitStride };
  }
  
  public int getSampleSize(int paramInt) {
    return this.pixelBitStride;
  }
  
  public int getOffset(int paramInt1, int paramInt2) {
    int i = paramInt2 * this.scanlineStride;
    i += (paramInt1 * this.pixelBitStride + this.dataBitOffset) / this.dataElementSize;
    return i;
  }
  
  public int getBitOffset(int paramInt) {
    return (paramInt * this.pixelBitStride + this.dataBitOffset) % this.dataElementSize;
  }
  
  public int getScanlineStride() {
    return this.scanlineStride;
  }
  
  public int getPixelBitStride() {
    return this.pixelBitStride;
  }
  
  public int getDataBitOffset() {
    return this.dataBitOffset;
  }
  
  public int getTransferType() {
    if (this.pixelBitStride > 16)
      return 3; 
    if (this.pixelBitStride > 8)
      return 1; 
    return 0;
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfint) {
    if (paramArrayOfint != null && 
      paramArrayOfint.length != 1)
      throw new RasterFormatException("MultiPixelPackedSampleModel has only one band."); 
    return createCompatibleSampleModel(this.width, this.height);
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height || paramInt3 != 0)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + i / this.dataElementSize);
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    return j >> k & this.bitMask;
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height || paramInt3 != 0)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramInt2 * this.scanlineStride + i / this.dataElementSize;
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= this.bitMask << k ^ 0xFFFFFFFF;
    m |= (paramInt4 & this.bitMask) << k;
    paramDataBuffer.setElem(j, m);
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int k = this.dataElementSize - (j & this.dataElementSize - 1) - this.pixelBitStride;
    int m = 0;
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[1];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfByte[0] = (byte)(m >> k & this.bitMask);
        paramObject = arrayOfByte;
        break;
      case 1:
        if (paramObject == null) {
          arrayOfShort = new short[1];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfShort[0] = (short)(m >> k & this.bitMask);
        paramObject = arrayOfShort;
        break;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[1];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        m = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + j / this.dataElementSize);
        arrayOfInt[0] = m >> k & this.bitMask;
        paramObject = arrayOfInt;
        break;
    } 
    return paramObject;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[this.numBands];
    } 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * this.scanlineStride + i / this.dataElementSize);
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    arrayOfInt[0] = j >> k & this.bitMask;
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int k = paramInt2 * this.scanlineStride + j / this.dataElementSize;
    int m = this.dataElementSize - (j & this.dataElementSize - 1) - this.pixelBitStride;
    int n = paramDataBuffer.getElem(k);
    n &= this.bitMask << m ^ 0xFFFFFFFF;
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        n |= (arrayOfByte[0] & 0xFF & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        n |= (arrayOfShort[0] & 0xFFFF & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        n |= (arrayOfInt[0] & this.bitMask) << m;
        paramDataBuffer.setElem(k, n);
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = this.dataBitOffset + paramInt1 * this.pixelBitStride;
    int j = paramInt2 * this.scanlineStride + i / this.dataElementSize;
    int k = this.dataElementSize - (i & this.dataElementSize - 1) - this.pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= this.bitMask << k ^ 0xFFFFFFFF;
    m |= (paramArrayOfint[0] & this.bitMask) << k;
    paramDataBuffer.setElem(j, m);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof MultiPixelPackedSampleModel))
      return false; 
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramObject;
    return (this.width == multiPixelPackedSampleModel.width && this.height == multiPixelPackedSampleModel.height && this.numBands == multiPixelPackedSampleModel.numBands && this.dataType == multiPixelPackedSampleModel.dataType && this.pixelBitStride == multiPixelPackedSampleModel.pixelBitStride && this.bitMask == multiPixelPackedSampleModel.bitMask && this.pixelsPerDataElement == multiPixelPackedSampleModel.pixelsPerDataElement && this.dataElementSize == multiPixelPackedSampleModel.dataElementSize && this.dataBitOffset == multiPixelPackedSampleModel.dataBitOffset && this.scanlineStride == multiPixelPackedSampleModel.scanlineStride);
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
    i ^= this.pixelBitStride;
    i <<= 8;
    i ^= this.bitMask;
    i <<= 8;
    i ^= this.pixelsPerDataElement;
    i <<= 8;
    i ^= this.dataElementSize;
    i <<= 8;
    i ^= this.dataBitOffset;
    i <<= 8;
    i ^= this.scanlineStride;
    return i;
  }
}
