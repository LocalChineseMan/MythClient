package java.awt.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.util.Arrays;

public class ComponentColorModel extends ColorModel {
  private boolean signed;
  
  private boolean is_sRGB_stdScale;
  
  private boolean is_LinearRGB_stdScale;
  
  private boolean is_LinearGray_stdScale;
  
  private boolean is_ICCGray_stdScale;
  
  private byte[] tosRGB8LUT;
  
  private byte[] fromsRGB8LUT8;
  
  private short[] fromsRGB8LUT16;
  
  private byte[] fromLinearGray16ToOtherGray8LUT;
  
  private short[] fromLinearGray16ToOtherGray16LUT;
  
  private boolean needScaleInit;
  
  private boolean noUnnorm;
  
  private boolean nonStdScale;
  
  private float[] min;
  
  private float[] diffMinMax;
  
  private float[] compOffset;
  
  private float[] compScale;
  
  public ComponentColorModel(ColorSpace paramColorSpace, int[] paramArrayOfint, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    super(bitsHelper(paramInt2, paramColorSpace, paramBoolean1), 
        bitsArrayHelper(paramArrayOfint, paramInt2, paramColorSpace, paramBoolean1), paramColorSpace, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
    switch (paramInt2) {
      case 0:
      case 1:
      case 3:
        this.signed = false;
        this.needScaleInit = true;
        break;
      case 2:
        this.signed = true;
        this.needScaleInit = true;
        break;
      case 4:
      case 5:
        this.signed = true;
        this.needScaleInit = false;
        this.noUnnorm = true;
        this.nonStdScale = false;
        break;
      default:
        throw new IllegalArgumentException("This constructor is not compatible with transferType " + paramInt2);
    } 
    setupLUTs();
  }
  
  public ComponentColorModel(ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    this(paramColorSpace, (int[])null, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
  }
  
  private static int bitsHelper(int paramInt, ColorSpace paramColorSpace, boolean paramBoolean) {
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean)
      j++; 
    return i * j;
  }
  
  private static int[] bitsArrayHelper(int[] paramArrayOfint, int paramInt, ColorSpace paramColorSpace, boolean paramBoolean) {
    switch (paramInt) {
      case 0:
      case 1:
      case 3:
        if (paramArrayOfint != null)
          return paramArrayOfint; 
        break;
    } 
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean)
      j++; 
    int[] arrayOfInt = new int[j];
    for (byte b = 0; b < j; b++)
      arrayOfInt[b] = i; 
    return arrayOfInt;
  }
  
  private void setupLUTs() {
    if (this.is_sRGB) {
      this.is_sRGB_stdScale = true;
      this.nonStdScale = false;
    } else if (ColorModel.isLinearRGBspace(this.colorSpace)) {
      this.is_LinearRGB_stdScale = true;
      this.nonStdScale = false;
      if (this.transferType == 0) {
        this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
        this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
      } else {
        this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
        this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      } 
    } else if (this.colorSpaceType == 6 && this.colorSpace instanceof ICC_ColorSpace && this.colorSpace
      
      .getMinValue(0) == 0.0F && this.colorSpace
      .getMaxValue(0) == 1.0F) {
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)this.colorSpace;
      this.is_ICCGray_stdScale = true;
      this.nonStdScale = false;
      this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      if (ColorModel.isLinearGRAYspace(iCC_ColorSpace)) {
        this.is_LinearGray_stdScale = true;
        if (this.transferType == 0) {
          this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(iCC_ColorSpace);
        } else {
          this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(iCC_ColorSpace);
        } 
      } else if (this.transferType == 0) {
        this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(iCC_ColorSpace);
        this
          .fromLinearGray16ToOtherGray8LUT = ColorModel.getLinearGray16ToOtherGray8LUT(iCC_ColorSpace);
      } else {
        this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(iCC_ColorSpace);
        this
          .fromLinearGray16ToOtherGray16LUT = ColorModel.getLinearGray16ToOtherGray16LUT(iCC_ColorSpace);
      } 
    } else if (this.needScaleInit) {
      this.nonStdScale = false;
      byte b;
      for (b = 0; b < this.numColorComponents; b++) {
        if (this.colorSpace.getMinValue(b) != 0.0F || this.colorSpace
          .getMaxValue(b) != 1.0F) {
          this.nonStdScale = true;
          break;
        } 
      } 
      if (this.nonStdScale) {
        this.min = new float[this.numColorComponents];
        this.diffMinMax = new float[this.numColorComponents];
        for (b = 0; b < this.numColorComponents; b++) {
          this.min[b] = this.colorSpace.getMinValue(b);
          this.diffMinMax[b] = this.colorSpace.getMaxValue(b) - this.min[b];
        } 
      } 
    } 
  }
  
  private void initScale() {
    float[] arrayOfFloat1, arrayOfFloat2;
    byte[] arrayOfByte;
    short[] arrayOfShort2;
    int[] arrayOfInt;
    short[] arrayOfShort1;
    byte b2;
    this.needScaleInit = false;
    if (this.nonStdScale || this.signed) {
      this.noUnnorm = true;
    } else {
      this.noUnnorm = false;
    } 
    switch (this.transferType) {
      case 0:
        arrayOfByte = new byte[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfByte[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfByte[this.numColorComponents] = (byte)((1 << this.nBits[this.numColorComponents]) - 1); 
        arrayOfFloat1 = getNormalizedComponents(arrayOfByte, (float[])null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfByte[b2] = (byte)((1 << this.nBits[b2]) - 1); 
        arrayOfFloat2 = getNormalizedComponents(arrayOfByte, (float[])null, 0);
        break;
      case 1:
        arrayOfShort2 = new short[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort2[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfShort2[this.numColorComponents] = (short)((1 << this.nBits[this.numColorComponents]) - 1); 
        arrayOfFloat1 = getNormalizedComponents(arrayOfShort2, (float[])null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort2[b2] = (short)((1 << this.nBits[b2]) - 1); 
        arrayOfFloat2 = getNormalizedComponents(arrayOfShort2, (float[])null, 0);
        break;
      case 3:
        arrayOfInt = new int[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfInt[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfInt[this.numColorComponents] = (1 << this.nBits[this.numColorComponents]) - 1; 
        arrayOfFloat1 = getNormalizedComponents(arrayOfInt, (float[])null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfInt[b2] = (1 << this.nBits[b2]) - 1; 
        arrayOfFloat2 = getNormalizedComponents(arrayOfInt, (float[])null, 0);
        break;
      case 2:
        arrayOfShort1 = new short[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort1[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfShort1[this.numColorComponents] = Short.MAX_VALUE; 
        arrayOfFloat1 = getNormalizedComponents(arrayOfShort1, (float[])null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort1[b2] = Short.MAX_VALUE; 
        arrayOfFloat2 = getNormalizedComponents(arrayOfShort1, (float[])null, 0);
        break;
      default:
        arrayOfFloat1 = arrayOfFloat2 = null;
        break;
    } 
    this.nonStdScale = false;
    byte b1;
    for (b1 = 0; b1 < this.numColorComponents; b1++) {
      if (arrayOfFloat1[b1] != 0.0F || arrayOfFloat2[b1] != 1.0F) {
        this.nonStdScale = true;
        break;
      } 
    } 
    if (this.nonStdScale) {
      this.noUnnorm = true;
      this.is_sRGB_stdScale = false;
      this.is_LinearRGB_stdScale = false;
      this.is_LinearGray_stdScale = false;
      this.is_ICCGray_stdScale = false;
      this.compOffset = new float[this.numColorComponents];
      this.compScale = new float[this.numColorComponents];
      for (b1 = 0; b1 < this.numColorComponents; b1++) {
        this.compOffset[b1] = arrayOfFloat1[b1];
        this.compScale[b1] = 1.0F / (arrayOfFloat2[b1] - arrayOfFloat1[b1]);
      } 
    } 
  }
  
  private int getRGBComponent(int paramInt1, int paramInt2) {
    short[] arrayOfShort1;
    int[] arrayOfInt1;
    byte[] arrayOfByte2;
    short[] arrayOfShort2;
    int[] arrayOfInt2;
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    if (this.needScaleInit)
      initScale(); 
    byte[] arrayOfByte1 = null;
    switch (this.transferType) {
      case 0:
        arrayOfByte2 = new byte[] { (byte)paramInt1 };
        arrayOfByte1 = arrayOfByte2;
        break;
      case 1:
        arrayOfShort2 = new short[] { (short)paramInt1 };
        arrayOfShort1 = arrayOfShort2;
        break;
      case 3:
        arrayOfInt2 = new int[] { paramInt1 };
        arrayOfInt1 = arrayOfInt2;
        break;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(arrayOfInt1, (float[])null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return (int)(arrayOfFloat2[paramInt2] * 255.0F + 0.5F);
  }
  
  public int getRed(int paramInt) {
    return getRGBComponent(paramInt, 0);
  }
  
  public int getGreen(int paramInt) {
    return getRGBComponent(paramInt, 1);
  }
  
  public int getBlue(int paramInt) {
    return getRGBComponent(paramInt, 2);
  }
  
  public int getAlpha(int paramInt) {
    if (!this.supportsAlpha)
      return 255; 
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    return (int)(paramInt / ((1 << this.nBits[0]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(int paramInt) {
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0;
  }
  
  private int extractComponent(Object paramObject, int paramInt1, int paramInt2) {
    int j;
    short[] arrayOfShort1;
    float[] arrayOfFloat;
    double[] arrayOfDouble;
    byte[] arrayOfByte;
    float f;
    double d;
    short[] arrayOfShort2;
    int[] arrayOfInt;
    boolean bool = (this.supportsAlpha && this.isAlphaPremultiplied) ? true : false;
    int i = 0;
    int k = (1 << this.nBits[paramInt1]) - 1;
    switch (this.transferType) {
      case 2:
        arrayOfShort1 = (short[])paramObject;
        f = ((1 << paramInt2) - 1);
        if (bool) {
          short s = arrayOfShort1[this.numColorComponents];
          if (s != 0)
            return (int)(arrayOfShort1[paramInt1] / s * f + 0.5F); 
          return 0;
        } 
        return (int)(arrayOfShort1[paramInt1] / 32767.0F * f + 0.5F);
      case 4:
        arrayOfFloat = (float[])paramObject;
        f = ((1 << paramInt2) - 1);
        if (bool) {
          float f1 = arrayOfFloat[this.numColorComponents];
          if (f1 != 0.0F)
            return (int)(arrayOfFloat[paramInt1] / f1 * f + 0.5F); 
          return 0;
        } 
        return (int)(arrayOfFloat[paramInt1] * f + 0.5F);
      case 5:
        arrayOfDouble = (double[])paramObject;
        d = ((1 << paramInt2) - 1);
        if (bool) {
          double d1 = arrayOfDouble[this.numColorComponents];
          if (d1 != 0.0D)
            return (int)(arrayOfDouble[paramInt1] / d1 * d + 0.5D); 
          return 0;
        } 
        return (int)(arrayOfDouble[paramInt1] * d + 0.5D);
      case 0:
        arrayOfByte = (byte[])paramObject;
        j = arrayOfByte[paramInt1] & k;
        paramInt2 = 8;
        if (bool)
          i = arrayOfByte[this.numColorComponents] & k; 
        break;
      case 1:
        arrayOfShort2 = (short[])paramObject;
        j = arrayOfShort2[paramInt1] & k;
        if (bool)
          i = arrayOfShort2[this.numColorComponents] & k; 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        j = arrayOfInt[paramInt1];
        if (bool)
          i = arrayOfInt[this.numColorComponents]; 
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (bool) {
      if (i != 0) {
        float f1 = ((1 << paramInt2) - 1);
        float f2 = j / k;
        float f3 = ((1 << this.nBits[this.numColorComponents]) - 1) / i;
        return (int)(f2 * f3 * f1 + 0.5F);
      } 
      return 0;
    } 
    if (this.nBits[paramInt1] != paramInt2) {
      float f1 = ((1 << paramInt2) - 1);
      float f2 = j / k;
      return (int)(f2 * f1 + 0.5F);
    } 
    return j;
  }
  
  private int getRGBComponent(Object paramObject, int paramInt) {
    if (this.needScaleInit)
      initScale(); 
    if (this.is_sRGB_stdScale)
      return extractComponent(paramObject, paramInt, 8); 
    if (this.is_LinearRGB_stdScale) {
      int i = extractComponent(paramObject, paramInt, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    } 
    if (this.is_ICCGray_stdScale) {
      int i = extractComponent(paramObject, 0, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, (float[])null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return (int)(arrayOfFloat2[paramInt] * 255.0F + 0.5F);
  }
  
  public int getRed(Object paramObject) {
    return getRGBComponent(paramObject, 0);
  }
  
  public int getGreen(Object paramObject) {
    return getRGBComponent(paramObject, 1);
  }
  
  public int getBlue(Object paramObject) {
    return getRGBComponent(paramObject, 2);
  }
  
  public int getAlpha(Object paramObject) {
    short[] arrayOfShort1;
    float[] arrayOfFloat;
    double[] arrayOfDouble;
    byte[] arrayOfByte;
    short[] arrayOfShort2;
    int[] arrayOfInt;
    if (!this.supportsAlpha)
      return 255; 
    int i = 0;
    int j = this.numColorComponents;
    int k = (1 << this.nBits[j]) - 1;
    switch (this.transferType) {
      case 2:
        arrayOfShort1 = (short[])paramObject;
        i = (int)(arrayOfShort1[j] / 32767.0F * 255.0F + 0.5F);
        return i;
      case 4:
        arrayOfFloat = (float[])paramObject;
        i = (int)(arrayOfFloat[j] * 255.0F + 0.5F);
        return i;
      case 5:
        arrayOfDouble = (double[])paramObject;
        i = (int)(arrayOfDouble[j] * 255.0D + 0.5D);
        return i;
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[j] & k;
        break;
      case 1:
        arrayOfShort2 = (short[])paramObject;
        i = arrayOfShort2[j] & k;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[j];
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (this.nBits[j] == 8)
      return i; 
    return (int)(i / ((1 << this.nBits[j]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(Object paramObject) {
    if (this.needScaleInit)
      initScale(); 
    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale)
      return getAlpha(paramObject) << 24 | getRed(paramObject) << 16 | getGreen(paramObject) << 8 | getBlue(paramObject); 
    if (this.colorSpaceType == 6) {
      int i = getRed(paramObject);
      return getAlpha(paramObject) << 24 | i << 16 | i << 8 | i;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, (float[])null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return getAlpha(paramObject) << 24 | (int)(arrayOfFloat2[0] * 255.0F + 0.5F) << 16 | (int)(arrayOfFloat2[1] * 255.0F + 0.5F) << 8 | (int)(arrayOfFloat2[2] * 255.0F + 0.5F) << 0;
  }
  
  public Object getDataElements(int paramInt, Object paramObject) {
    int[] arrayOfInt;
    byte[] arrayOfByte;
    short[] arrayOfShort;
    byte b;
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    if (this.needScaleInit)
      initScale(); 
    if (this.signed) {
      short[] arrayOfShort1;
      float[] arrayOfFloat;
      double[] arrayOfDouble;
      switch (this.transferType) {
        case 2:
          if (paramObject == null) {
            arrayOfShort1 = new short[this.numComponents];
          } else {
            arrayOfShort1 = (short[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            float f = 128.49803F;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              f = 0.49999237F;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfShort1[3] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f = m * f * 0.003921569F; 
            } 
            arrayOfShort1[0] = (short)(int)(i * f + 0.5F);
            arrayOfShort1[1] = (short)(int)(j * f + 0.5F);
            arrayOfShort1[2] = (short)(int)(k * f + 0.5F);
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            float f2 = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
            float f1 = 32767.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfShort1[1] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f1 = m * f1 * 0.003921569F; 
            } 
            arrayOfShort1[0] = (short)(int)(f2 * f1 + 0.5F);
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            m = this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF;
            float f = 0.49999237F;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfShort1[1] = (short)(int)(n * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f = n * f * 0.003921569F; 
            } 
            arrayOfShort1[0] = (short)(int)(m * f + 0.5F);
          } else {
            float f = 0.003921569F;
            float[] arrayOfFloat1 = new float[3];
            arrayOfFloat1[0] = i * f;
            arrayOfFloat1[1] = j * f;
            arrayOfFloat1[2] = k * f;
            arrayOfFloat1 = this.colorSpace.fromRGB(arrayOfFloat1);
            if (this.nonStdScale)
              for (byte b2 = 0; b2 < this.numColorComponents; b2++) {
                arrayOfFloat1[b2] = (arrayOfFloat1[b2] - this.compOffset[b2]) * this.compScale[b2];
                if (arrayOfFloat1[b2] < 0.0F)
                  arrayOfFloat1[b2] = 0.0F; 
                if (arrayOfFloat1[b2] > 1.0F)
                  arrayOfFloat1[b2] = 1.0F; 
              }  
            f = 32767.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfShort1[this.numColorComponents] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f *= m * 0.003921569F; 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfShort1[b1] = (short)(int)(arrayOfFloat1[b1] * f + 0.5F); 
          } 
          return arrayOfShort1;
        case 4:
          if (paramObject == null) {
            arrayOfFloat = new float[this.numComponents];
          } else {
            arrayOfFloat = (float[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            float f;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              f = 1.5259022E-5F;
            } else {
              f = 0.003921569F;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfFloat[3] = m * 0.003921569F;
              if (this.isAlphaPremultiplied)
                f *= arrayOfFloat[3]; 
            } 
            arrayOfFloat[0] = i * f;
            arrayOfFloat[1] = j * f;
            arrayOfFloat[2] = k * f;
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            arrayOfFloat[0] = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfFloat[1] = m * 0.003921569F;
              if (this.isAlphaPremultiplied)
                arrayOfFloat[0] = arrayOfFloat[0] * arrayOfFloat[1]; 
            } 
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            arrayOfFloat[0] = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0F;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfFloat[1] = n * 0.003921569F;
              if (this.isAlphaPremultiplied)
                arrayOfFloat[0] = arrayOfFloat[0] * arrayOfFloat[1]; 
            } 
          } else {
            float[] arrayOfFloat1 = new float[3];
            float f = 0.003921569F;
            arrayOfFloat1[0] = i * f;
            arrayOfFloat1[1] = j * f;
            arrayOfFloat1[2] = k * f;
            arrayOfFloat1 = this.colorSpace.fromRGB(arrayOfFloat1);
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfFloat[this.numColorComponents] = m * f;
              if (this.isAlphaPremultiplied) {
                f *= m;
                for (byte b2 = 0; b2 < this.numColorComponents; b2++)
                  arrayOfFloat1[b2] = arrayOfFloat1[b2] * f; 
              } 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfFloat[b1] = arrayOfFloat1[b1]; 
          } 
          return arrayOfFloat;
        case 5:
          if (paramObject == null) {
            arrayOfDouble = new double[this.numComponents];
          } else {
            arrayOfDouble = (double[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            double d;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              d = 1.5259021896696422E-5D;
            } else {
              d = 0.00392156862745098D;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfDouble[3] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                d *= arrayOfDouble[3]; 
            } 
            arrayOfDouble[0] = i * d;
            arrayOfDouble[1] = j * d;
            arrayOfDouble[2] = k * d;
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            arrayOfDouble[0] = (0.2125D * i + 0.7154D * j + 0.0721D * k) / 65535.0D;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfDouble[1] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                arrayOfDouble[0] = arrayOfDouble[0] * arrayOfDouble[1]; 
            } 
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            arrayOfDouble[0] = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0D;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfDouble[1] = n * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                arrayOfDouble[0] = arrayOfDouble[0] * arrayOfDouble[1]; 
            } 
          } else {
            float f = 0.003921569F;
            float[] arrayOfFloat1 = new float[3];
            arrayOfFloat1[0] = i * f;
            arrayOfFloat1[1] = j * f;
            arrayOfFloat1[2] = k * f;
            arrayOfFloat1 = this.colorSpace.fromRGB(arrayOfFloat1);
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfDouble[this.numColorComponents] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied) {
                f *= m;
                for (byte b2 = 0; b2 < this.numColorComponents; b2++)
                  arrayOfFloat1[b2] = arrayOfFloat1[b2] * f; 
              } 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfDouble[b1] = arrayOfFloat1[b1]; 
          } 
          return arrayOfDouble;
      } 
    } 
    if (this.transferType == 3 && paramObject != null) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = new int[this.numComponents];
    } 
    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
      byte b1;
      float f;
      if (this.is_LinearRGB_stdScale) {
        if (this.transferType == 0) {
          i = this.fromsRGB8LUT8[i] & 0xFF;
          j = this.fromsRGB8LUT8[j] & 0xFF;
          k = this.fromsRGB8LUT8[k] & 0xFF;
          b1 = 8;
          f = 0.003921569F;
        } else {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          b1 = 16;
          f = 1.5259022E-5F;
        } 
      } else {
        b1 = 8;
        f = 0.003921569F;
      } 
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[3] == 8) {
          arrayOfInt[3] = m;
        } else {
          arrayOfInt[3] = (int)(m * 0.003921569F * ((1 << this.nBits[3]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied) {
          f *= m * 0.003921569F;
          b1 = -1;
        } 
      } 
      if (this.nBits[0] == b1) {
        arrayOfInt[0] = i;
      } else {
        arrayOfInt[0] = (int)(i * f * ((1 << this.nBits[0]) - 1) + 0.5F);
      } 
      if (this.nBits[1] == b1) {
        arrayOfInt[1] = j;
      } else {
        arrayOfInt[1] = (int)(j * f * ((1 << this.nBits[1]) - 1) + 0.5F);
      } 
      if (this.nBits[2] == b1) {
        arrayOfInt[2] = k;
      } else {
        arrayOfInt[2] = (int)(k * f * ((1 << this.nBits[2]) - 1) + 0.5F);
      } 
    } else if (this.is_LinearGray_stdScale) {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      float f = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          arrayOfInt[1] = m;
        } else {
          arrayOfInt[1] = (int)(m * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied)
          f *= m * 0.003921569F; 
      } 
      arrayOfInt[0] = (int)(f * ((1 << this.nBits[0]) - 1) + 0.5F);
    } else if (this.is_ICCGray_stdScale) {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
      float f = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0F;
      if (this.supportsAlpha) {
        int n = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          arrayOfInt[1] = n;
        } else {
          arrayOfInt[1] = (int)(n * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied)
          f *= n * 0.003921569F; 
      } 
      arrayOfInt[0] = (int)(f * ((1 << this.nBits[0]) - 1) + 0.5F);
    } else {
      float[] arrayOfFloat = new float[3];
      float f = 0.003921569F;
      arrayOfFloat[0] = i * f;
      arrayOfFloat[1] = j * f;
      arrayOfFloat[2] = k * f;
      arrayOfFloat = this.colorSpace.fromRGB(arrayOfFloat);
      if (this.nonStdScale)
        for (byte b2 = 0; b2 < this.numColorComponents; b2++) {
          arrayOfFloat[b2] = (arrayOfFloat[b2] - this.compOffset[b2]) * this.compScale[b2];
          if (arrayOfFloat[b2] < 0.0F)
            arrayOfFloat[b2] = 0.0F; 
          if (arrayOfFloat[b2] > 1.0F)
            arrayOfFloat[b2] = 1.0F; 
        }  
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[this.numColorComponents] == 8) {
          arrayOfInt[this.numColorComponents] = m;
        } else {
          arrayOfInt[this.numColorComponents] = (int)(m * f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied) {
          f *= m;
          for (byte b2 = 0; b2 < this.numColorComponents; b2++)
            arrayOfFloat[b2] = arrayOfFloat[b2] * f; 
        } 
      } 
      for (byte b1 = 0; b1 < this.numColorComponents; b1++)
        arrayOfInt[b1] = (int)(arrayOfFloat[b1] * ((1 << this.nBits[b1]) - 1) + 0.5F); 
    } 
    switch (this.transferType) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[this.numComponents];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfByte[b] = (byte)(0xFF & arrayOfInt[b]); 
        return arrayOfByte;
      case 1:
        if (paramObject == null) {
          arrayOfShort = new short[this.numComponents];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfShort[b] = (short)(arrayOfInt[b] & 0xFFFF); 
        return arrayOfShort;
      case 3:
        if (this.maxBits > 23)
          for (byte b1 = 0; b1 < this.numComponents; b1++) {
            if (arrayOfInt[b1] > (1 << this.nBits[b1]) - 1)
              arrayOfInt[b1] = (1 << this.nBits[b1]) - 1; 
          }  
        return arrayOfInt;
    } 
    throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfint, int paramInt2) {
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramArrayOfint == null)
      paramArrayOfint = new int[paramInt2 + 1]; 
    paramArrayOfint[paramInt2 + 0] = paramInt1 & (1 << this.nBits[0]) - 1;
    return paramArrayOfint;
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfint, int paramInt) {
    int[] arrayOfInt;
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramObject instanceof int[]) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = DataBuffer.toIntArray(paramObject);
      if (arrayOfInt == null)
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType); 
    } 
    if (arrayOfInt.length < this.numComponents)
      throw new IllegalArgumentException("Length of pixel array < number of components in model"); 
    if (paramArrayOfint == null) {
      paramArrayOfint = new int[paramInt + this.numComponents];
    } else if (paramArrayOfint.length - paramInt < this.numComponents) {
      throw new IllegalArgumentException("Length of components array < number of components in model");
    } 
    System.arraycopy(arrayOfInt, 0, paramArrayOfint, paramInt, this.numComponents);
    return paramArrayOfint;
  }
  
  public int[] getUnnormalizedComponents(float[] paramArrayOffloat, int paramInt1, int[] paramArrayOfint, int paramInt2) {
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    return super.getUnnormalizedComponents(paramArrayOffloat, paramInt1, paramArrayOfint, paramInt2);
  }
  
  public float[] getNormalizedComponents(int[] paramArrayOfint, int paramInt1, float[] paramArrayOffloat, int paramInt2) {
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    return super.getNormalizedComponents(paramArrayOfint, paramInt1, paramArrayOffloat, paramInt2);
  }
  
  public int getDataElement(int[] paramArrayOfint, int paramInt) {
    if (this.needScaleInit)
      initScale(); 
    if (this.numComponents == 1) {
      if (this.noUnnorm)
        throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
      return paramArrayOfint[paramInt + 0];
    } 
    throw new IllegalArgumentException("This model returns " + this.numComponents + " elements in the pixel array.");
  }
  
  public Object getDataElements(int[] paramArrayOfint, int paramInt, Object paramObject) {
    int[] arrayOfInt;
    byte[] arrayOfByte;
    short[] arrayOfShort;
    byte b;
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramArrayOfint.length - paramInt < this.numComponents)
      throw new IllegalArgumentException("Component array too small (should be " + this.numComponents); 
    switch (this.transferType) {
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[this.numComponents];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        System.arraycopy(paramArrayOfint, paramInt, arrayOfInt, 0, this.numComponents);
        return arrayOfInt;
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[this.numComponents];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfByte[b] = (byte)(paramArrayOfint[paramInt + b] & 0xFF); 
        return arrayOfByte;
      case 1:
        if (paramObject == null) {
          arrayOfShort = new short[this.numComponents];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfShort[b] = (short)(paramArrayOfint[paramInt + b] & 0xFFFF); 
        return arrayOfShort;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getDataElement(float[] paramArrayOffloat, int paramInt) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    if (this.needScaleInit)
      initScale(); 
    Object object = getDataElements(paramArrayOffloat, paramInt, (Object)null);
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])object;
        return arrayOfByte[0] & 0xFF;
      case 1:
        arrayOfShort = (short[])object;
        return arrayOfShort[0] & 0xFFFF;
      case 3:
        arrayOfInt = (int[])object;
        return arrayOfInt[0];
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public Object getDataElements(float[] paramArrayOffloat, int paramInt, Object paramObject) {
    float[] arrayOfFloat1;
    byte[] arrayOfByte;
    short[] arrayOfShort1;
    int[] arrayOfInt;
    short[] arrayOfShort2;
    float[] arrayOfFloat2;
    double[] arrayOfDouble;
    boolean bool = (this.supportsAlpha && this.isAlphaPremultiplied) ? true : false;
    if (this.needScaleInit)
      initScale(); 
    if (this.nonStdScale) {
      arrayOfFloat1 = new float[this.numComponents];
      byte b;
      int i;
      for (b = 0, i = paramInt; b < this.numColorComponents; 
        b++, i++) {
        arrayOfFloat1[b] = (paramArrayOffloat[i] - this.compOffset[b]) * this.compScale[b];
        if (arrayOfFloat1[b] < 0.0F)
          arrayOfFloat1[b] = 0.0F; 
        if (arrayOfFloat1[b] > 1.0F)
          arrayOfFloat1[b] = 1.0F; 
      } 
      if (this.supportsAlpha)
        arrayOfFloat1[this.numColorComponents] = paramArrayOffloat[this.numColorComponents + paramInt]; 
      paramInt = 0;
    } else {
      arrayOfFloat1 = paramArrayOffloat;
    } 
    switch (this.transferType) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[this.numComponents];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfByte[b] = (byte)(int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F); 
          arrayOfByte[this.numColorComponents] = (byte)(int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfByte[b] = (byte)(int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F); 
        } 
        return arrayOfByte;
      case 1:
        if (paramObject == null) {
          arrayOfShort1 = new short[this.numComponents];
        } else {
          arrayOfShort1 = (short[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfShort1[b] = (short)(int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F); 
          arrayOfShort1[this.numColorComponents] = (short)(int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfShort1[b] = (short)(int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F); 
        } 
        return arrayOfShort1;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[this.numComponents];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfInt[b] = (int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F); 
          arrayOfInt[this.numColorComponents] = (int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfInt[b] = (int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F); 
        } 
        return arrayOfInt;
      case 2:
        if (paramObject == null) {
          arrayOfShort2 = new short[this.numComponents];
        } else {
          arrayOfShort2 = (short[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfShort2[b] = (short)(int)(arrayOfFloat1[i] * f * 32767.0F + 0.5F); 
          arrayOfShort2[this.numColorComponents] = (short)(int)(f * 32767.0F + 0.5F);
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfShort2[b] = (short)(int)(arrayOfFloat1[i] * 32767.0F + 0.5F); 
        } 
        return arrayOfShort2;
      case 4:
        if (paramObject == null) {
          arrayOfFloat2 = new float[this.numComponents];
        } else {
          arrayOfFloat2 = (float[])paramObject;
        } 
        if (bool) {
          float f = paramArrayOffloat[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfFloat2[b] = paramArrayOffloat[i] * f; 
          arrayOfFloat2[this.numColorComponents] = f;
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfFloat2[b] = paramArrayOffloat[i]; 
        } 
        return arrayOfFloat2;
      case 5:
        if (paramObject == null) {
          arrayOfDouble = new double[this.numComponents];
        } else {
          arrayOfDouble = (double[])paramObject;
        } 
        if (bool) {
          double d = paramArrayOffloat[this.numColorComponents + paramInt];
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numColorComponents; 
            b++, i++)
            arrayOfDouble[b] = paramArrayOffloat[i] * d; 
          arrayOfDouble[this.numColorComponents] = d;
        } else {
          byte b;
          int i;
          for (b = 0, i = paramInt; b < this.numComponents; 
            b++, i++)
            arrayOfDouble[b] = paramArrayOffloat[i]; 
        } 
        return arrayOfDouble;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public float[] getNormalizedComponents(Object paramObject, float[] paramArrayOffloat, int paramInt) {
    byte[] arrayOfByte;
    byte b;
    short[] arrayOfShort1;
    int i;
    int[] arrayOfInt;
    int j;
    short[] arrayOfShort2;
    int k;
    float[] arrayOfFloat;
    int m;
    double[] arrayOfDouble;
    int n;
    int i1;
    if (paramArrayOffloat == null)
      paramArrayOffloat = new float[this.numComponents + paramInt]; 
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        for (b = 0, i = paramInt; b < this.numComponents; b++, i++)
          paramArrayOffloat[i] = (arrayOfByte[b] & 0xFF) / ((1 << this.nBits[b]) - 1); 
        break;
      case 1:
        arrayOfShort1 = (short[])paramObject;
        for (i = 0, j = paramInt; i < this.numComponents; i++, j++)
          paramArrayOffloat[j] = (arrayOfShort1[i] & 0xFFFF) / ((1 << this.nBits[i]) - 1); 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        for (j = 0, k = paramInt; j < this.numComponents; j++, k++)
          paramArrayOffloat[k] = arrayOfInt[j] / ((1 << this.nBits[j]) - 1); 
        break;
      case 2:
        arrayOfShort2 = (short[])paramObject;
        for (k = 0, m = paramInt; k < this.numComponents; k++, m++)
          paramArrayOffloat[m] = arrayOfShort2[k] / 32767.0F; 
        break;
      case 4:
        arrayOfFloat = (float[])paramObject;
        for (m = 0, n = paramInt; m < this.numComponents; m++, n++)
          paramArrayOffloat[n] = arrayOfFloat[m]; 
        break;
      case 5:
        arrayOfDouble = (double[])paramObject;
        for (n = 0, i1 = paramInt; n < this.numComponents; n++, i1++)
          paramArrayOffloat[i1] = (float)arrayOfDouble[n]; 
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (this.supportsAlpha && this.isAlphaPremultiplied) {
      float f = paramArrayOffloat[this.numColorComponents + paramInt];
      if (f != 0.0F) {
        float f1 = 1.0F / f;
        for (int i2 = paramInt; i2 < this.numColorComponents + paramInt; 
          i2++)
          paramArrayOffloat[i2] = paramArrayOffloat[i2] * f1; 
      } 
    } 
    if (this.min != null)
      for (byte b1 = 0; b1 < this.numColorComponents; b1++)
        paramArrayOffloat[b1 + paramInt] = this.min[b1] + this.diffMinMax[b1] * paramArrayOffloat[b1 + paramInt];  
    return paramArrayOffloat;
  }
  
  public ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean) {
    if (!this.supportsAlpha || this.isAlphaPremultiplied == paramBoolean)
      return this; 
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = paramWritableRaster.getNumBands() - 1;
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    if (paramBoolean) {
      byte[] arrayOfByte1;
      byte[] arrayOfByte2;
      float f;
      byte b1;
      byte b2;
      switch (this.transferType) {
        case 0:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              arrayOfByte1 = (byte[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              float f1 = (arrayOfByte1[k] & 0xFF) * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfByte1[b3] = (byte)(int)((arrayOfByte1[b3] & 0xFF) * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfByte1);
              } else {
                if (arrayOfByte2 == null) {
                  arrayOfByte2 = new byte[this.numComponents];
                  Arrays.fill(arrayOfByte2, (byte)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfByte2);
              } 
            } 
          } 
          break;
        case 1:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              short[] arrayOfShort = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              float f1 = (arrayOfShort[k] & 0xFFFF) * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfShort[b3] = (short)(int)((arrayOfShort[b3] & 0xFFFF) * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort);
              } else {
                short[] arrayOfShort1;
                if (arrayOfByte2 == null) {
                  arrayOfShort1 = new short[this.numComponents];
                  Arrays.fill(arrayOfShort1, (short)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort1);
              } 
            } 
          } 
          break;
        case 3:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              int[] arrayOfInt = (int[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              float f1 = arrayOfInt[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfInt[b3] = (int)(arrayOfInt[b3] * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfInt);
              } else {
                int[] arrayOfInt1;
                if (arrayOfByte2 == null) {
                  arrayOfInt1 = new int[this.numComponents];
                  Arrays.fill(arrayOfInt1, 0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfInt1);
              } 
            } 
          } 
          break;
        case 2:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          f = 3.051851E-5F;
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              short[] arrayOfShort = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              float f1 = arrayOfShort[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfShort[b3] = (short)(int)(arrayOfShort[b3] * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort);
              } else {
                short[] arrayOfShort1;
                if (arrayOfByte2 == null) {
                  arrayOfShort1 = new short[this.numComponents];
                  Arrays.fill(arrayOfShort1, (short)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort1);
              } 
            } 
          } 
          break;
        case 4:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          for (b1 = 0; b1 < j; b1++, n++) {
            int i1 = m;
            for (b2 = 0; b2 < i; b2++, i1++) {
              float[] arrayOfFloat = (float[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              float f1 = arrayOfFloat[k];
              if (f1 != 0.0F) {
                for (byte b = 0; b < k; b++)
                  arrayOfFloat[b] = arrayOfFloat[b] * f1; 
                paramWritableRaster.setDataElements(i1, n, arrayOfFloat);
              } else {
                float[] arrayOfFloat1;
                if (arrayOfByte2 == null) {
                  arrayOfFloat1 = new float[this.numComponents];
                  Arrays.fill(arrayOfFloat1, 0.0F);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfFloat1);
              } 
            } 
          } 
          break;
        case 5:
          arrayOfByte1 = null;
          arrayOfByte2 = null;
          for (b1 = 0; b1 < j; b1++, n++) {
            int i1 = m;
            for (b2 = 0; b2 < i; b2++, i1++) {
              double[] arrayOfDouble = (double[])paramWritableRaster.getDataElements(i1, n, arrayOfByte1);
              double d = arrayOfDouble[k];
              if (d != 0.0D) {
                for (byte b = 0; b < k; b++)
                  arrayOfDouble[b] = arrayOfDouble[b] * d; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } else {
                double[] arrayOfDouble1;
                if (arrayOfByte2 == null) {
                  arrayOfDouble1 = new double[this.numComponents];
                  Arrays.fill(arrayOfDouble1, 0.0D);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } 
            } 
          } 
          break;
        default:
          throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      } 
    } else {
      byte[] arrayOfByte;
      float f;
      byte b1;
      byte b2;
      switch (this.transferType) {
        case 0:
          arrayOfByte = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              arrayOfByte = (byte[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              float f1 = (arrayOfByte[k] & 0xFF) * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfByte[b3] = (byte)(int)((arrayOfByte[b3] & 0xFF) * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfByte);
              } 
            } 
          } 
          break;
        case 1:
          arrayOfByte = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              short[] arrayOfShort = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              float f1 = (arrayOfShort[k] & 0xFFFF) * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfShort[b3] = (short)(int)((arrayOfShort[b3] & 0xFFFF) * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort);
              } 
            } 
          } 
          break;
        case 3:
          arrayOfByte = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              int[] arrayOfInt = (int[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              float f1 = arrayOfInt[k] * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfInt[b3] = (int)(arrayOfInt[b3] * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfInt);
              } 
            } 
          } 
          break;
        case 2:
          arrayOfByte = null;
          f = 3.051851E-5F;
          for (b2 = 0; b2 < j; b2++, n++) {
            int i1 = m;
            for (byte b = 0; b < i; b++, i1++) {
              short[] arrayOfShort = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              float f1 = arrayOfShort[k] * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfShort[b3] = (short)(int)(arrayOfShort[b3] * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfShort);
              } 
            } 
          } 
          break;
        case 4:
          arrayOfByte = null;
          for (b1 = 0; b1 < j; b1++, n++) {
            int i1 = m;
            for (b2 = 0; b2 < i; b2++, i1++) {
              float[] arrayOfFloat = (float[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              float f1 = arrayOfFloat[k];
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b = 0; b < k; b++)
                  arrayOfFloat[b] = arrayOfFloat[b] * f2; 
                paramWritableRaster.setDataElements(i1, n, arrayOfFloat);
              } 
            } 
          } 
          break;
        case 5:
          arrayOfByte = null;
          for (b1 = 0; b1 < j; b1++, n++) {
            int i1 = m;
            for (b2 = 0; b2 < i; b2++, i1++) {
              double[] arrayOfDouble = (double[])paramWritableRaster.getDataElements(i1, n, arrayOfByte);
              double d = arrayOfDouble[k];
              if (d != 0.0D) {
                double d1 = 1.0D / d;
                for (byte b = 0; b < k; b++)
                  arrayOfDouble[b] = arrayOfDouble[b] * d1; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
            } 
          } 
          break;
        default:
          throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      } 
    } 
    if (!this.signed)
      return new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, paramBoolean, this.transparency, this.transferType); 
    return new ComponentColorModel(this.colorSpace, this.supportsAlpha, paramBoolean, this.transparency, this.transferType);
  }
  
  public boolean isCompatibleRaster(Raster paramRaster) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel instanceof ComponentSampleModel) {
      if (sampleModel.getNumBands() != getNumComponents())
        return false; 
      for (byte b = 0; b < this.nBits.length; b++) {
        if (sampleModel.getSampleSize(b) < this.nBits[b])
          return false; 
      } 
      return (paramRaster.getTransferType() == this.transferType);
    } 
    return false;
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    int i = paramInt1 * paramInt2 * this.numComponents;
    WritableRaster writableRaster = null;
    switch (this.transferType) {
      case 0:
      case 1:
        writableRaster = Raster.createInterleavedRaster(this.transferType, paramInt1, paramInt2, this.numComponents, null);
        return writableRaster;
    } 
    SampleModel sampleModel = createCompatibleSampleModel(paramInt1, paramInt2);
    DataBuffer dataBuffer = sampleModel.createDataBuffer();
    writableRaster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
    return writableRaster;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[this.numComponents];
    for (byte b = 0; b < this.numComponents; b++)
      arrayOfInt[b] = b; 
    switch (this.transferType) {
      case 0:
      case 1:
        return new PixelInterleavedSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
    } 
    return new ComponentSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel) {
    if (!(paramSampleModel instanceof ComponentSampleModel))
      return false; 
    if (this.numComponents != paramSampleModel.getNumBands())
      return false; 
    if (paramSampleModel.getTransferType() != this.transferType)
      return false; 
    return true;
  }
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster) {
    if (!hasAlpha())
      return null; 
    int i = paramWritableRaster.getMinX();
    int j = paramWritableRaster.getMinY();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramWritableRaster.getNumBands() - 1;
    return paramWritableRaster.createWritableChild(i, j, paramWritableRaster.getWidth(), paramWritableRaster
        .getHeight(), i, j, arrayOfInt);
  }
  
  public boolean equals(Object paramObject) {
    if (!super.equals(paramObject))
      return false; 
    if (paramObject.getClass() != getClass())
      return false; 
    return true;
  }
}
