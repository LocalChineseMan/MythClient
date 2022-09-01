package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileRGB extends ICC_Profile {
  static final long serialVersionUID = 8505067385152579334L;
  
  public static final int REDCOMPONENT = 0;
  
  public static final int GREENCOMPONENT = 1;
  
  public static final int BLUECOMPONENT = 2;
  
  ICC_ProfileRGB(Profile paramProfile) {
    super(paramProfile);
  }
  
  ICC_ProfileRGB(ProfileDeferralInfo paramProfileDeferralInfo) {
    super(paramProfileDeferralInfo);
  }
  
  public float[] getMediaWhitePoint() {
    return super.getMediaWhitePoint();
  }
  
  public float[][] getMatrix() {
    float[][] arrayOfFloat = new float[3][3];
    float[] arrayOfFloat1 = getXYZTag(1918392666);
    arrayOfFloat[0][0] = arrayOfFloat1[0];
    arrayOfFloat[1][0] = arrayOfFloat1[1];
    arrayOfFloat[2][0] = arrayOfFloat1[2];
    arrayOfFloat1 = getXYZTag(1733843290);
    arrayOfFloat[0][1] = arrayOfFloat1[0];
    arrayOfFloat[1][1] = arrayOfFloat1[1];
    arrayOfFloat[2][1] = arrayOfFloat1[2];
    arrayOfFloat1 = getXYZTag(1649957210);
    arrayOfFloat[0][2] = arrayOfFloat1[0];
    arrayOfFloat[1][2] = arrayOfFloat1[1];
    arrayOfFloat[2][2] = arrayOfFloat1[2];
    return arrayOfFloat;
  }
  
  public float getGamma(int paramInt) {
    float f;
    int i;
    switch (paramInt) {
      case 0:
        i = 1918128707;
        f = super.getGamma(i);
        return f;
      case 1:
        i = 1733579331;
        f = super.getGamma(i);
        return f;
      case 2:
        i = 1649693251;
        f = super.getGamma(i);
        return f;
    } 
    throw new IllegalArgumentException("Must be Red, Green, or Blue");
  }
  
  public short[] getTRC(int paramInt) {
    short[] arrayOfShort;
    int i;
    switch (paramInt) {
      case 0:
        i = 1918128707;
        arrayOfShort = super.getTRC(i);
        return arrayOfShort;
      case 1:
        i = 1733579331;
        arrayOfShort = super.getTRC(i);
        return arrayOfShort;
      case 2:
        i = 1649693251;
        arrayOfShort = super.getTRC(i);
        return arrayOfShort;
    } 
    throw new IllegalArgumentException("Must be Red, Green, or Blue");
  }
}
