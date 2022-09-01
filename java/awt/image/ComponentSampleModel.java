package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel extends SampleModel {
  protected int[] bandOffsets;
  
  protected int[] bankIndices;
  
  protected int numBands = 1;
  
  protected int numBanks = 1;
  
  protected int scanlineStride;
  
  protected int pixelStride;
  
  static {
    ColorModel.loadLibraries();
    initIDs();
  }
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfint.length);
    this.dataType = paramInt1;
    this.pixelStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.bandOffsets = (int[])paramArrayOfint.clone();
    this.numBands = this.bandOffsets.length;
    if (paramInt4 < 0)
      throw new IllegalArgumentException("Pixel stride must be >= 0"); 
    if (paramInt5 < 0)
      throw new IllegalArgumentException("Scanline stride must be >= 0"); 
    if (this.numBands < 1)
      throw new IllegalArgumentException("Must have at least one band."); 
    if (paramInt1 < 0 || paramInt1 > 5)
      throw new IllegalArgumentException("Unsupported dataType."); 
    this.bankIndices = new int[this.numBands];
    for (byte b = 0; b < this.numBands; b++)
      this.bankIndices[b] = 0; 
    verify();
  }
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfint2.length);
    this.dataType = paramInt1;
    this.pixelStride = paramInt4;
    this.scanlineStride = paramInt5;
    this.bandOffsets = (int[])paramArrayOfint2.clone();
    this.bankIndices = (int[])paramArrayOfint1.clone();
    if (paramInt4 < 0)
      throw new IllegalArgumentException("Pixel stride must be >= 0"); 
    if (paramInt5 < 0)
      throw new IllegalArgumentException("Scanline stride must be >= 0"); 
    if (paramInt1 < 0 || paramInt1 > 5)
      throw new IllegalArgumentException("Unsupported dataType."); 
    int i = this.bankIndices[0];
    if (i < 0)
      throw new IllegalArgumentException("Index of bank 0 is less than 0 (" + i + ")"); 
    for (byte b = 1; b < this.bankIndices.length; b++) {
      if (this.bankIndices[b] > i) {
        i = this.bankIndices[b];
      } else if (this.bankIndices[b] < 0) {
        throw new IllegalArgumentException("Index of bank " + b + " is less than 0 (" + i + ")");
      } 
    } 
    this.numBanks = i + 1;
    this.numBands = this.bandOffsets.length;
    if (this.bandOffsets.length != this.bankIndices.length)
      throw new IllegalArgumentException("Length of bandOffsets must equal length of bankIndices."); 
    verify();
  }
  
  private void verify() {
    int i = getBufferSize();
  }
  
  private int getBufferSize() {
    int i = this.bandOffsets[0];
    int j;
    for (j = 1; j < this.bandOffsets.length; j++)
      i = Math.max(i, this.bandOffsets[j]); 
    if (i < 0 || i > 2147483646)
      throw new IllegalArgumentException("Invalid band offset"); 
    if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width)
      throw new IllegalArgumentException("Invalid pixel stride"); 
    if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height)
      throw new IllegalArgumentException("Invalid scanline stride"); 
    j = i + 1;
    int k = this.pixelStride * (this.width - 1);
    if (k > Integer.MAX_VALUE - j)
      throw new IllegalArgumentException("Invalid pixel stride"); 
    j += k;
    k = this.scanlineStride * (this.height - 1);
    if (k > Integer.MAX_VALUE - j)
      throw new IllegalArgumentException("Invalid scan stride"); 
    j += k;
    return j;
  }
  
  int[] orderBands(int[] paramArrayOfint, int paramInt) {
    int[] arrayOfInt1 = new int[paramArrayOfint.length];
    int[] arrayOfInt2 = new int[paramArrayOfint.length];
    byte b;
    for (b = 0; b < arrayOfInt1.length; ) {
      arrayOfInt1[b] = b;
      b++;
    } 
    for (b = 0; b < arrayOfInt2.length; b++) {
      int i = b;
      for (int j = b + 1; j < arrayOfInt2.length; j++) {
        if (paramArrayOfint[arrayOfInt1[i]] > paramArrayOfint[arrayOfInt1[j]])
          i = j; 
      } 
      arrayOfInt2[arrayOfInt1[i]] = b * paramInt;
      arrayOfInt1[i] = arrayOfInt1[b];
    } 
    return arrayOfInt2;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    Object object = null;
    int i = this.bandOffsets[0];
    int j = this.bandOffsets[0];
    int k;
    for (k = 1; k < this.bandOffsets.length; k++) {
      i = Math.min(i, this.bandOffsets[k]);
      j = Math.max(j, this.bandOffsets[k]);
    } 
    j -= i;
    k = this.bandOffsets.length;
    int m = Math.abs(this.pixelStride);
    int n = Math.abs(this.scanlineStride);
    int i1 = Math.abs(j);
    if (m > n) {
      if (m > i1) {
        if (n > i1) {
          arrayOfInt = new int[this.bandOffsets.length];
          for (byte b1 = 0; b1 < k; b1++)
            arrayOfInt[b1] = this.bandOffsets[b1] - i; 
          n = i1 + 1;
          m = n * paramInt2;
        } else {
          arrayOfInt = orderBands(this.bandOffsets, n * paramInt2);
          m = k * n * paramInt2;
        } 
      } else {
        m = n * paramInt2;
        arrayOfInt = orderBands(this.bandOffsets, m * paramInt1);
      } 
    } else if (m > i1) {
      arrayOfInt = new int[this.bandOffsets.length];
      for (byte b1 = 0; b1 < k; b1++)
        arrayOfInt[b1] = this.bandOffsets[b1] - i; 
      m = i1 + 1;
      n = m * paramInt1;
    } else if (n > i1) {
      arrayOfInt = orderBands(this.bandOffsets, m * paramInt1);
      n = k * m * paramInt1;
    } else {
      n = m * paramInt1;
      arrayOfInt = orderBands(this.bandOffsets, n * paramInt2);
    } 
    int i2 = 0;
    if (this.scanlineStride < 0) {
      i2 += n * paramInt2;
      n *= -1;
    } 
    if (this.pixelStride < 0) {
      i2 += m * paramInt1;
      m *= -1;
    } 
    for (byte b = 0; b < k; b++)
      arrayOfInt[b] = arrayOfInt[b] + i2; 
    return new ComponentSampleModel(this.dataType, paramInt1, paramInt2, m, n, this.bankIndices, arrayOfInt);
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfint) {
    if (paramArrayOfint.length > this.bankIndices.length)
      throw new RasterFormatException("There are only " + this.bankIndices.length + " bands"); 
    int[] arrayOfInt1 = new int[paramArrayOfint.length];
    int[] arrayOfInt2 = new int[paramArrayOfint.length];
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      arrayOfInt1[b] = this.bankIndices[paramArrayOfint[b]];
      arrayOfInt2[b] = this.bandOffsets[paramArrayOfint[b]];
    } 
    return new ComponentSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, arrayOfInt1, arrayOfInt2);
  }
  
  public DataBuffer createDataBuffer() {
    DataBufferUShort dataBufferUShort;
    DataBufferShort dataBufferShort;
    DataBufferInt dataBufferInt;
    DataBufferFloat dataBufferFloat;
    DataBufferDouble dataBufferDouble;
    DataBufferByte dataBufferByte = null;
    int i = getBufferSize();
    switch (this.dataType) {
      case 0:
        dataBufferByte = new DataBufferByte(i, this.numBanks);
        break;
      case 1:
        dataBufferUShort = new DataBufferUShort(i, this.numBanks);
        break;
      case 2:
        dataBufferShort = new DataBufferShort(i, this.numBanks);
        break;
      case 3:
        dataBufferInt = new DataBufferInt(i, this.numBanks);
        break;
      case 4:
        dataBufferFloat = new DataBufferFloat(i, this.numBanks);
        break;
      case 5:
        dataBufferDouble = new DataBufferDouble(i, this.numBanks);
        break;
    } 
    return dataBufferDouble;
  }
  
  public int getOffset(int paramInt1, int paramInt2) {
    return paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[0];
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3) {
    return paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3];
  }
  
  public final int[] getSampleSize() {
    int[] arrayOfInt = new int[this.numBands];
    int i = getSampleSize(0);
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = i; 
    return arrayOfInt;
  }
  
  public final int getSampleSize(int paramInt) {
    return DataBuffer.getDataTypeSize(this.dataType);
  }
  
  public final int[] getBankIndices() {
    return (int[])this.bankIndices.clone();
  }
  
  public final int[] getBandOffsets() {
    return (int[])this.bandOffsets.clone();
  }
  
  public final int getScanlineStride() {
    return this.scanlineStride;
  }
  
  public final int getPixelStride() {
    return this.pixelStride;
  }
  
  public final int getNumDataElements() {
    return getNumBands();
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    byte b1;
    short[] arrayOfShort;
    byte b2;
    int[] arrayOfInt;
    byte b3;
    float[] arrayOfFloat;
    byte b4;
    double[] arrayOfDouble;
    byte b5;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[j];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        for (b1 = 0; b1 < j; b1++)
          arrayOfByte[b1] = (byte)paramDataBuffer.getElem(this.bankIndices[b1], k + this.bandOffsets[b1]); 
        paramObject = arrayOfByte;
        break;
      case 1:
      case 2:
        if (paramObject == null) {
          arrayOfShort = new short[j];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (b2 = 0; b2 < j; b2++)
          arrayOfShort[b2] = (short)paramDataBuffer.getElem(this.bankIndices[b2], k + this.bandOffsets[b2]); 
        paramObject = arrayOfShort;
        break;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[j];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        for (b3 = 0; b3 < j; b3++)
          arrayOfInt[b3] = paramDataBuffer.getElem(this.bankIndices[b3], k + this.bandOffsets[b3]); 
        paramObject = arrayOfInt;
        break;
      case 4:
        if (paramObject == null) {
          arrayOfFloat = new float[j];
        } else {
          arrayOfFloat = (float[])paramObject;
        } 
        for (b4 = 0; b4 < j; b4++)
          arrayOfFloat[b4] = paramDataBuffer.getElemFloat(this.bankIndices[b4], k + this.bandOffsets[b4]); 
        paramObject = arrayOfFloat;
        break;
      case 5:
        if (paramObject == null) {
          arrayOfDouble = new double[j];
        } else {
          arrayOfDouble = (double[])paramObject;
        } 
        for (b5 = 0; b5 < j; b5++)
          arrayOfDouble[b5] = paramDataBuffer.getElemDouble(this.bankIndices[b5], k + this.bandOffsets[b5]); 
        paramObject = arrayOfDouble;
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
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = paramDataBuffer.getElem(this.bankIndices[b], i + this.bandOffsets[b]); 
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int arrayOfInt[], i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt2 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    } 
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = k;
      for (byte b = 0; b < paramInt3; b++) {
        for (byte b3 = 0; b3 < this.numBands; b3++)
          arrayOfInt[b1++] = paramDataBuffer
            .getElem(this.bankIndices[b3], m + this.bandOffsets[b3]); 
        m += this.pixelStride;
      } 
      k += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    return paramDataBuffer.getElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3]);
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
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt5];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++) {
        arrayOfInt[b1++] = paramDataBuffer.getElem(this.bankIndices[paramInt5], j);
        j += this.pixelStride;
      } 
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    byte b1;
    short[] arrayOfShort;
    byte b2;
    int[] arrayOfInt;
    byte b3;
    float[] arrayOfFloat;
    byte b4;
    double[] arrayOfDouble;
    byte b5;
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    switch (i) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        for (b1 = 0; b1 < j; b1++)
          paramDataBuffer.setElem(this.bankIndices[b1], k + this.bandOffsets[b1], arrayOfByte[b1] & 0xFF); 
        break;
      case 1:
      case 2:
        arrayOfShort = (short[])paramObject;
        for (b2 = 0; b2 < j; b2++)
          paramDataBuffer.setElem(this.bankIndices[b2], k + this.bandOffsets[b2], arrayOfShort[b2] & 0xFFFF); 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        for (b3 = 0; b3 < j; b3++)
          paramDataBuffer.setElem(this.bankIndices[b3], k + this.bandOffsets[b3], arrayOfInt[b3]); 
        break;
      case 4:
        arrayOfFloat = (float[])paramObject;
        for (b4 = 0; b4 < j; b4++)
          paramDataBuffer.setElemFloat(this.bankIndices[b4], k + this.bandOffsets[b4], arrayOfFloat[b4]); 
        break;
      case 5:
        arrayOfDouble = (double[])paramObject;
        for (b5 = 0; b5 < j; b5++)
          paramDataBuffer.setElemDouble(this.bankIndices[b5], k + this.bandOffsets[b5], arrayOfDouble[b5]); 
        break;
    } 
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    for (byte b = 0; b < this.numBands; b++)
      paramDataBuffer.setElem(this.bankIndices[b], i + this.bandOffsets[b], paramArrayOfint[b]); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int k = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int m = k;
      for (byte b = 0; b < paramInt3; b++) {
        for (byte b3 = 0; b3 < this.numBands; b3++)
          paramDataBuffer.setElem(this.bankIndices[b3], m + this.bandOffsets[b3], paramArrayOfint[b1++]); 
        m += this.pixelStride;
      } 
      k += this.scanlineStride;
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramInt4);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramFloat);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.width || paramInt2 >= this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    paramDataBuffer.setElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt3], paramDouble);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt3 > this.width || paramInt2 + paramInt4 > this.height)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int i = paramInt2 * this.scanlineStride + paramInt1 * this.pixelStride + this.bandOffsets[paramInt5];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt4; b2++) {
      int j = i;
      for (byte b = 0; b < paramInt3; b++) {
        paramDataBuffer.setElem(this.bankIndices[paramInt5], j, paramArrayOfint[b1++]);
        j += this.pixelStride;
      } 
      i += this.scanlineStride;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof ComponentSampleModel))
      return false; 
    ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramObject;
    return (this.width == componentSampleModel.width && this.height == componentSampleModel.height && this.numBands == componentSampleModel.numBands && this.dataType == componentSampleModel.dataType && 
      
      Arrays.equals(this.bandOffsets, componentSampleModel.bandOffsets) && 
      Arrays.equals(this.bankIndices, componentSampleModel.bankIndices) && this.numBands == componentSampleModel.numBands && this.numBanks == componentSampleModel.numBanks && this.scanlineStride == componentSampleModel.scanlineStride && this.pixelStride == componentSampleModel.pixelStride);
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
    for (b = 0; b < this.bandOffsets.length; b++) {
      i ^= this.bandOffsets[b];
      i <<= 8;
    } 
    for (b = 0; b < this.bankIndices.length; b++) {
      i ^= this.bankIndices[b];
      i <<= 8;
    } 
    i ^= this.numBands;
    i <<= 8;
    i ^= this.numBanks;
    i <<= 8;
    i ^= this.scanlineStride;
    i <<= 8;
    i ^= this.pixelStride;
    return i;
  }
  
  private static native void initIDs();
}
