package java.awt.image;

public abstract class SampleModel {
  protected int width;
  
  protected int height;
  
  protected int numBands;
  
  protected int dataType;
  
  static {
    ColorModel.loadLibraries();
    initIDs();
  }
  
  public SampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l = paramInt2 * paramInt3;
    if (paramInt2 <= 0 || paramInt3 <= 0)
      throw new IllegalArgumentException("Width (" + paramInt2 + ") and height (" + paramInt3 + ") must be > 0"); 
    if (l >= 2147483647L)
      throw new IllegalArgumentException("Dimensions (width=" + paramInt2 + " height=" + paramInt3 + ") are too large"); 
    if (paramInt1 < 0 || (paramInt1 > 5 && paramInt1 != 32))
      throw new IllegalArgumentException("Unsupported dataType: " + paramInt1); 
    if (paramInt4 <= 0)
      throw new IllegalArgumentException("Number of bands must be > 0"); 
    this.dataType = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.numBands = paramInt4;
  }
  
  public final int getWidth() {
    return this.width;
  }
  
  public final int getHeight() {
    return this.height;
  }
  
  public final int getNumBands() {
    return this.numBands;
  }
  
  public final int getDataType() {
    return this.dataType;
  }
  
  public int getTransferType() {
    return this.dataType;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfInt[b] = getSample(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfInt;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte;
    int n;
    short[] arrayOfShort;
    int i1, arrayOfInt[], i2;
    float[] arrayOfFloat;
    int i3;
    double[] arrayOfDouble;
    int i4, i = getTransferType();
    int j = getNumDataElements();
    byte b = 0;
    Object object = null;
    int k = paramInt1 + paramInt3;
    int m = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || k < 0 || k > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || m < 0 || m > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    switch (i) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[j * paramInt3 * paramInt4];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        for (n = paramInt2; n < m; n++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, n, object, paramDataBuffer);
            byte[] arrayOfByte1 = (byte[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfByte[b++] = arrayOfByte1[b1]; 
          } 
        } 
        paramObject = arrayOfByte;
        break;
      case 1:
      case 2:
        if (paramObject == null) {
          arrayOfShort = new short[j * paramInt3 * paramInt4];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (i1 = paramInt2; i1 < m; i1++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i1, object, paramDataBuffer);
            short[] arrayOfShort1 = (short[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfShort[b++] = arrayOfShort1[b1]; 
          } 
        } 
        paramObject = arrayOfShort;
        break;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[j * paramInt3 * paramInt4];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        for (i2 = paramInt2; i2 < m; i2++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i2, object, paramDataBuffer);
            int[] arrayOfInt1 = (int[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfInt[b++] = arrayOfInt1[b1]; 
          } 
        } 
        paramObject = arrayOfInt;
        break;
      case 4:
        if (paramObject == null) {
          arrayOfFloat = new float[j * paramInt3 * paramInt4];
        } else {
          arrayOfFloat = (float[])paramObject;
        } 
        for (i3 = paramInt2; i3 < m; i3++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i3, object, paramDataBuffer);
            float[] arrayOfFloat1 = (float[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfFloat[b++] = arrayOfFloat1[b1]; 
          } 
        } 
        paramObject = arrayOfFloat;
        break;
      case 5:
        if (paramObject == null) {
          arrayOfDouble = new double[j * paramInt3 * paramInt4];
        } else {
          arrayOfDouble = (double[])paramObject;
        } 
        for (i4 = paramInt2; i4 < m; i4++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            object = getDataElements(i5, i4, object, paramDataBuffer);
            double[] arrayOfDouble1 = (double[])object;
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfDouble[b++] = arrayOfDouble1[b1]; 
          } 
        } 
        paramObject = arrayOfDouble;
        break;
    } 
    return paramObject;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer) {
    byte[] arrayOfByte1, arrayOfByte2;
    int n;
    short[] arrayOfShort1, arrayOfShort2;
    int i1, arrayOfInt1[], arrayOfInt2[], i2;
    float[] arrayOfFloat1, arrayOfFloat2;
    int i3;
    double[] arrayOfDouble1, arrayOfDouble2;
    int i4;
    byte b = 0;
    Object object = null;
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt1 + paramInt3;
    int m = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || k < 0 || k > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || m < 0 || m > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    switch (i) {
      case 0:
        arrayOfByte1 = (byte[])paramObject;
        arrayOfByte2 = new byte[j];
        for (n = paramInt2; n < m; n++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfByte2[b1] = arrayOfByte1[b++]; 
            setDataElements(i5, n, arrayOfByte2, paramDataBuffer);
          } 
        } 
        break;
      case 1:
      case 2:
        arrayOfShort1 = (short[])paramObject;
        arrayOfShort2 = new short[j];
        for (i1 = paramInt2; i1 < m; i1++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfShort2[b1] = arrayOfShort1[b++]; 
            setDataElements(i5, i1, arrayOfShort2, paramDataBuffer);
          } 
        } 
        break;
      case 3:
        arrayOfInt1 = (int[])paramObject;
        arrayOfInt2 = new int[j];
        for (i2 = paramInt2; i2 < m; i2++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfInt2[b1] = arrayOfInt1[b++]; 
            setDataElements(i5, i2, arrayOfInt2, paramDataBuffer);
          } 
        } 
        break;
      case 4:
        arrayOfFloat1 = (float[])paramObject;
        arrayOfFloat2 = new float[j];
        for (i3 = paramInt2; i3 < m; i3++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfFloat2[b1] = arrayOfFloat1[b++]; 
            setDataElements(i5, i3, arrayOfFloat2, paramDataBuffer);
          } 
        } 
        break;
      case 5:
        arrayOfDouble1 = (double[])paramObject;
        arrayOfDouble2 = new double[j];
        for (i4 = paramInt2; i4 < m; i4++) {
          for (int i5 = paramInt1; i5 < k; i5++) {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfDouble2[b1] = arrayOfDouble1[b++]; 
            setDataElements(i5, i4, arrayOfDouble2, paramDataBuffer);
          } 
        } 
        break;
    } 
  }
  
  public float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    if (paramArrayOffloat != null) {
      arrayOfFloat = paramArrayOffloat;
    } else {
      arrayOfFloat = new float[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfFloat[b] = getSampleFloat(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfFloat;
  }
  
  public double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    if (paramArrayOfdouble != null) {
      arrayOfDouble = paramArrayOfdouble;
    } else {
      arrayOfDouble = new double[this.numBands];
    } 
    for (byte b = 0; b < this.numBands; b++)
      arrayOfDouble[b] = getSampleDouble(paramInt1, paramInt2, b, paramDataBuffer); 
    return arrayOfDouble;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfInt[b++] = getSample(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfInt;
  }
  
  public float[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOffloat != null) {
      arrayOfFloat = paramArrayOffloat;
    } else {
      arrayOfFloat = new float[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfFloat[b++] = getSampleFloat(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfFloat;
  }
  
  public double[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfdouble != null) {
      arrayOfDouble = paramArrayOfdouble;
    } else {
      arrayOfDouble = new double[this.numBands * paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          arrayOfDouble[b++] = getSampleDouble(m, k, b1, paramDataBuffer); 
      } 
    } 
    return arrayOfDouble;
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    return getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer);
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer) {
    return getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer);
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    int[] arrayOfInt;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    if (paramArrayOfint != null) {
      arrayOfInt = paramArrayOfint;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfInt[b++] = getSample(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfInt;
  }
  
  public float[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    float[] arrayOfFloat;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates"); 
    if (paramArrayOffloat != null) {
      arrayOfFloat = paramArrayOffloat;
    } else {
      arrayOfFloat = new float[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfFloat[b++] = getSampleFloat(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfFloat;
  }
  
  public double[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    double[] arrayOfDouble;
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || i < paramInt1 || i > this.width || paramInt2 < 0 || j < paramInt2 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates"); 
    if (paramArrayOfdouble != null) {
      arrayOfDouble = paramArrayOfdouble;
    } else {
      arrayOfDouble = new double[paramInt3 * paramInt4];
    } 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        arrayOfDouble[b++] = getSampleDouble(m, k, paramInt5, paramDataBuffer); 
    } 
    return arrayOfDouble;
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOfint[b], paramDataBuffer); 
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOffloat[b], paramDataBuffer); 
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    for (byte b = 0; b < this.numBands; b++)
      setSample(paramInt1, paramInt2, b, paramArrayOfdouble[b], paramDataBuffer); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOfint[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOffloat[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++) {
        for (byte b1 = 0; b1 < this.numBands; b1++)
          setSample(m, k, b1, paramArrayOfdouble[b++], paramDataBuffer); 
      } 
    } 
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer) {
    int i = (int)paramFloat;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer) {
    int i = (int)paramDouble;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOfint[b++], paramDataBuffer); 
    } 
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOffloat, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOffloat[b++], paramDataBuffer); 
    } 
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfdouble, DataBuffer paramDataBuffer) {
    byte b = 0;
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if (paramInt1 < 0 || paramInt1 >= this.width || paramInt3 > this.width || i < 0 || i > this.width || paramInt2 < 0 || paramInt2 >= this.height || paramInt4 > this.height || j < 0 || j > this.height)
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates."); 
    for (int k = paramInt2; k < j; k++) {
      for (int m = paramInt1; m < i; m++)
        setSample(m, k, paramInt5, paramArrayOfdouble[b++], paramDataBuffer); 
    } 
  }
  
  private static native void initIDs();
  
  public abstract int getNumDataElements();
  
  public abstract Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public abstract void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public abstract int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer);
  
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer);
  
  public abstract SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2);
  
  public abstract SampleModel createSubsetSampleModel(int[] paramArrayOfint);
  
  public abstract DataBuffer createDataBuffer();
  
  public abstract int[] getSampleSize();
  
  public abstract int getSampleSize(int paramInt);
}
