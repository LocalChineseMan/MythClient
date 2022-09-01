package com.sun.imageio.plugins.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class ReaderUtil {
  private static void computeUpdatedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int[] paramArrayOfint, int paramInt10) {
    boolean bool = false;
    int i = -1;
    int j = -1;
    int k = -1;
    for (byte b = 0; b < paramInt8; b++) {
      int m = paramInt7 + b * paramInt9;
      if (m >= paramInt1)
        if ((m - paramInt1) % paramInt6 == 0) {
          if (m >= paramInt1 + paramInt2)
            break; 
          int n = paramInt3 + (m - paramInt1) / paramInt6;
          if (n >= paramInt4) {
            if (n > paramInt5)
              break; 
            if (!bool) {
              i = n;
              bool = true;
            } else if (j == -1) {
              j = n;
            } 
            k = n;
          } 
        }  
    } 
    paramArrayOfint[paramInt10] = i;
    if (!bool) {
      paramArrayOfint[paramInt10 + 2] = 0;
    } else {
      paramArrayOfint[paramInt10 + 2] = k - i + 1;
    } 
    paramArrayOfint[paramInt10 + 4] = Math.max(j - i, 1);
  }
  
  public static int[] computeUpdatedPixels(Rectangle paramRectangle, Point paramPoint, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12) {
    int[] arrayOfInt = new int[6];
    computeUpdatedPixels(paramRectangle.x, paramRectangle.width, paramPoint.x, paramInt1, paramInt3, paramInt5, paramInt7, paramInt9, paramInt11, arrayOfInt, 0);
    computeUpdatedPixels(paramRectangle.y, paramRectangle.height, paramPoint.y, paramInt2, paramInt4, paramInt6, paramInt8, paramInt10, paramInt12, arrayOfInt, 1);
    return arrayOfInt;
  }
  
  public static int readMultiByteInteger(ImageInputStream paramImageInputStream) throws IOException {
    byte b = paramImageInputStream.readByte();
    int i = b & Byte.MAX_VALUE;
    while ((b & 0x80) == 128) {
      i <<= 7;
      b = paramImageInputStream.readByte();
      i |= b & Byte.MAX_VALUE;
    } 
    return i;
  }
}
