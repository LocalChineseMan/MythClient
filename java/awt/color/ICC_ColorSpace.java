package java.awt.color;

import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public class ICC_ColorSpace extends ColorSpace {
  static final long serialVersionUID = 3455889114070431483L;
  
  private ICC_Profile thisProfile;
  
  private float[] minVal;
  
  private float[] maxVal;
  
  private float[] diffMinMax;
  
  private float[] invDiffMinMax;
  
  private boolean needScaleInit = true;
  
  private transient ColorTransform this2srgb;
  
  private transient ColorTransform srgb2this;
  
  private transient ColorTransform this2xyz;
  
  private transient ColorTransform xyz2this;
  
  public ICC_ColorSpace(ICC_Profile paramICC_Profile) {
    super(paramICC_Profile.getColorSpaceType(), paramICC_Profile.getNumComponents());
    int i = paramICC_Profile.getProfileClass();
    if (i != 0 && i != 1 && i != 2 && i != 4 && i != 6 && i != 5)
      throw new IllegalArgumentException("Invalid profile type"); 
    this.thisProfile = paramICC_Profile;
    setMinMax();
  }
  
  public ICC_Profile getProfile() {
    return this.thisProfile;
  }
  
  public float[] toRGB(float[] paramArrayOffloat) {
    if (this.this2srgb == null) {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
      PCMM pCMM = CMSManager.getModule();
      arrayOfColorTransform[0] = pCMM.createTransform(this.thisProfile, -1, 1);
      arrayOfColorTransform[1] = pCMM.createTransform(iCC_ColorSpace
          .getProfile(), -1, 2);
      this.this2srgb = pCMM.createTransform(arrayOfColorTransform);
      if (this.needScaleInit)
        setComponentScaling(); 
    } 
    int i = getNumComponents();
    short[] arrayOfShort = new short[i];
    for (byte b1 = 0; b1 < i; b1++)
      arrayOfShort[b1] = (short)(int)((paramArrayOffloat[b1] - this.minVal[b1]) * this.invDiffMinMax[b1] + 0.5F); 
    arrayOfShort = this.this2srgb.colorConvert(arrayOfShort, (short[])null);
    float[] arrayOfFloat = new float[3];
    for (byte b2 = 0; b2 < 3; b2++)
      arrayOfFloat[b2] = (arrayOfShort[b2] & 0xFFFF) / 65535.0F; 
    return arrayOfFloat;
  }
  
  public float[] fromRGB(float[] paramArrayOffloat) {
    if (this.srgb2this == null) {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
      PCMM pCMM = CMSManager.getModule();
      arrayOfColorTransform[0] = pCMM.createTransform(iCC_ColorSpace
          .getProfile(), -1, 1);
      arrayOfColorTransform[1] = pCMM.createTransform(this.thisProfile, -1, 2);
      this.srgb2this = pCMM.createTransform(arrayOfColorTransform);
      if (this.needScaleInit)
        setComponentScaling(); 
    } 
    short[] arrayOfShort = new short[3];
    int i;
    for (i = 0; i < 3; i++)
      arrayOfShort[i] = (short)(int)(paramArrayOffloat[i] * 65535.0F + 0.5F); 
    arrayOfShort = this.srgb2this.colorConvert(arrayOfShort, (short[])null);
    i = getNumComponents();
    float[] arrayOfFloat = new float[i];
    for (byte b = 0; b < i; b++)
      arrayOfFloat[b] = (arrayOfShort[b] & 0xFFFF) / 65535.0F * this.diffMinMax[b] + this.minVal[b]; 
    return arrayOfFloat;
  }
  
  public float[] toCIEXYZ(float[] paramArrayOffloat) {
    if (this.this2xyz == null) {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
      PCMM pCMM = CMSManager.getModule();
      try {
        arrayOfColorTransform[0] = pCMM.createTransform(this.thisProfile, 1, 1);
      } catch (CMMException cMMException) {
        arrayOfColorTransform[0] = pCMM.createTransform(this.thisProfile, -1, 1);
      } 
      arrayOfColorTransform[1] = pCMM.createTransform(iCC_ColorSpace
          .getProfile(), -1, 2);
      this.this2xyz = pCMM.createTransform(arrayOfColorTransform);
      if (this.needScaleInit)
        setComponentScaling(); 
    } 
    int i = getNumComponents();
    short[] arrayOfShort = new short[i];
    for (byte b1 = 0; b1 < i; b1++)
      arrayOfShort[b1] = (short)(int)((paramArrayOffloat[b1] - this.minVal[b1]) * this.invDiffMinMax[b1] + 0.5F); 
    arrayOfShort = this.this2xyz.colorConvert(arrayOfShort, (short[])null);
    float f = 1.9999695F;
    float[] arrayOfFloat = new float[3];
    for (byte b2 = 0; b2 < 3; b2++)
      arrayOfFloat[b2] = (arrayOfShort[b2] & 0xFFFF) / 65535.0F * f; 
    return arrayOfFloat;
  }
  
  public float[] fromCIEXYZ(float[] paramArrayOffloat) {
    if (this.xyz2this == null) {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
      PCMM pCMM = CMSManager.getModule();
      arrayOfColorTransform[0] = pCMM.createTransform(iCC_ColorSpace
          .getProfile(), -1, 1);
      try {
        arrayOfColorTransform[1] = pCMM.createTransform(this.thisProfile, 1, 2);
      } catch (CMMException cMMException) {
        arrayOfColorTransform[1] = CMSManager.getModule().createTransform(this.thisProfile, -1, 2);
      } 
      this.xyz2this = pCMM.createTransform(arrayOfColorTransform);
      if (this.needScaleInit)
        setComponentScaling(); 
    } 
    short[] arrayOfShort = new short[3];
    float f1 = 1.9999695F;
    float f2 = 65535.0F / f1;
    int i;
    for (i = 0; i < 3; i++)
      arrayOfShort[i] = (short)(int)(paramArrayOffloat[i] * f2 + 0.5F); 
    arrayOfShort = this.xyz2this.colorConvert(arrayOfShort, (short[])null);
    i = getNumComponents();
    float[] arrayOfFloat = new float[i];
    for (byte b = 0; b < i; b++)
      arrayOfFloat[b] = (arrayOfShort[b] & 0xFFFF) / 65535.0F * this.diffMinMax[b] + this.minVal[b]; 
    return arrayOfFloat;
  }
  
  public float getMinValue(int paramInt) {
    if (paramInt < 0 || paramInt > getNumComponents() - 1)
      throw new IllegalArgumentException("Component index out of range: + component"); 
    return this.minVal[paramInt];
  }
  
  public float getMaxValue(int paramInt) {
    if (paramInt < 0 || paramInt > getNumComponents() - 1)
      throw new IllegalArgumentException("Component index out of range: + component"); 
    return this.maxVal[paramInt];
  }
  
  private void setMinMax() {
    int i = getNumComponents();
    int j = getType();
    this.minVal = new float[i];
    this.maxVal = new float[i];
    if (j == 1) {
      this.minVal[0] = 0.0F;
      this.maxVal[0] = 100.0F;
      this.minVal[1] = -128.0F;
      this.maxVal[1] = 127.0F;
      this.minVal[2] = -128.0F;
      this.maxVal[2] = 127.0F;
    } else if (j == 0) {
      this.minVal[2] = 0.0F;
      this.minVal[1] = 0.0F;
      this.minVal[0] = 0.0F;
      this.maxVal[2] = 1.9999695F;
      this.maxVal[1] = 1.9999695F;
      this.maxVal[0] = 1.9999695F;
    } else {
      for (byte b = 0; b < i; b++) {
        this.minVal[b] = 0.0F;
        this.maxVal[b] = 1.0F;
      } 
    } 
  }
  
  private void setComponentScaling() {
    int i = getNumComponents();
    this.diffMinMax = new float[i];
    this.invDiffMinMax = new float[i];
    for (byte b = 0; b < i; b++) {
      this.minVal[b] = getMinValue(b);
      this.maxVal[b] = getMaxValue(b);
      this.diffMinMax[b] = this.maxVal[b] - this.minVal[b];
      this.invDiffMinMax[b] = 65535.0F / this.diffMinMax[b];
    } 
    this.needScaleInit = false;
  }
}
