package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public final class GeneralRenderer {
  static final int OUTCODE_TOP = 1;
  
  static final int OUTCODE_BOTTOM = 2;
  
  static final int OUTCODE_LEFT = 4;
  
  static final int OUTCODE_RIGHT = 8;
  
  public static void register() {
    Class<GeneralRenderer> clazz = GeneralRenderer.class;
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { 
        new GraphicsPrimitiveProxy(clazz, "SetFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), 
        new GraphicsPrimitiveProxy(clazz, "XorDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawGlyphListANY", DrawGlyphList.methodSignature, DrawGlyphList.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawGlyphListAAANY", DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  static void doDrawPoly(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2, Region paramRegion, int paramInt3, int paramInt4, boolean paramBoolean) {
    int[] arrayOfInt = null;
    if (paramInt2 <= 0)
      return; 
    int k = paramArrayOfint1[paramInt1] + paramInt3, i = k;
    int m = paramArrayOfint2[paramInt1] + paramInt4, j = m;
    while (--paramInt2 > 0) {
      paramInt1++;
      int n = paramArrayOfint1[paramInt1] + paramInt3;
      int i1 = paramArrayOfint2[paramInt1] + paramInt4;
      arrayOfInt = doDrawLine(paramSurfaceData, paramPixelWriter, arrayOfInt, paramRegion, k, m, n, i1);
      k = n;
      m = i1;
    } 
    if (paramBoolean && (k != i || m != j))
      arrayOfInt = doDrawLine(paramSurfaceData, paramPixelWriter, arrayOfInt, paramRegion, k, m, i, j); 
  }
  
  static void doSetRect(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    WritableRaster writableRaster = (WritableRaster)paramSurfaceData.getRaster(paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
    paramPixelWriter.setRaster(writableRaster);
    while (paramInt2 < paramInt4) {
      for (int i = paramInt1; i < paramInt3; i++)
        paramPixelWriter.writePixel(i, paramInt2); 
      paramInt2++;
    } 
  }
  
  static int[] doDrawLine(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int[] paramArrayOfint, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramArrayOfint == null)
      paramArrayOfint = new int[8]; 
    paramArrayOfint[0] = paramInt1;
    paramArrayOfint[1] = paramInt2;
    paramArrayOfint[2] = paramInt3;
    paramArrayOfint[3] = paramInt4;
    if (!adjustLine(paramArrayOfint, paramRegion
        .getLoX(), paramRegion.getLoY(), paramRegion
        .getHiX(), paramRegion.getHiY()))
      return paramArrayOfint; 
    int i = paramArrayOfint[0];
    int j = paramArrayOfint[1];
    int k = paramArrayOfint[2];
    int m = paramArrayOfint[3];
    WritableRaster writableRaster = (WritableRaster)paramSurfaceData.getRaster(Math.min(i, k), Math.min(j, m), 
        Math.abs(i - k) + 1, Math.abs(j - m) + 1);
    paramPixelWriter.setRaster(writableRaster);
    if (i == k) {
      if (j > m) {
        do {
          paramPixelWriter.writePixel(i, j);
          --j;
        } while (j >= m);
      } else {
        do {
          paramPixelWriter.writePixel(i, j);
          ++j;
        } while (j <= m);
      } 
    } else if (j == m) {
      if (i > k) {
        do {
          paramPixelWriter.writePixel(i, j);
          --i;
        } while (i >= k);
      } else {
        do {
          paramPixelWriter.writePixel(i, j);
          ++i;
        } while (i <= k);
      } 
    } else {
      int i4;
      byte b1, b2;
      int i5, i6;
      boolean bool;
      int n = paramArrayOfint[4];
      int i1 = paramArrayOfint[5];
      int i2 = paramArrayOfint[6];
      int i3 = paramArrayOfint[7];
      if (i2 >= i3) {
        bool = true;
        i6 = i3 * 2;
        i5 = i2 * 2;
        b1 = (n < 0) ? -1 : 1;
        b2 = (i1 < 0) ? -1 : 1;
        i2 = -i2;
        i4 = k - i;
      } else {
        bool = false;
        i6 = i2 * 2;
        i5 = i3 * 2;
        b1 = (i1 < 0) ? -1 : 1;
        b2 = (n < 0) ? -1 : 1;
        i3 = -i3;
        i4 = m - j;
      } 
      int i7 = -(i5 / 2);
      if (j != paramInt2) {
        int i8 = j - paramInt2;
        if (i8 < 0)
          i8 = -i8; 
        i7 += i8 * i2 * 2;
      } 
      if (i != paramInt1) {
        int i8 = i - paramInt1;
        if (i8 < 0)
          i8 = -i8; 
        i7 += i8 * i3 * 2;
      } 
      if (i4 < 0)
        i4 = -i4; 
      if (bool) {
        do {
          paramPixelWriter.writePixel(i, j);
          i += b1;
          i7 += i6;
          if (i7 < 0)
            continue; 
          j += b2;
          i7 -= i5;
        } while (--i4 >= 0);
      } else {
        do {
          paramPixelWriter.writePixel(i, j);
          j += b1;
          i7 += i6;
          if (i7 < 0)
            continue; 
          i += b2;
          i7 -= i5;
        } while (--i4 >= 0);
      } 
    } 
    return paramArrayOfint;
  }
  
  public static void doDrawRect(PixelWriter paramPixelWriter, SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 < 0 || paramInt4 < 0)
      return; 
    int i = Region.dimAdd(Region.dimAdd(paramInt1, paramInt3), 1);
    int j = Region.dimAdd(Region.dimAdd(paramInt2, paramInt4), 1);
    Region region = paramSunGraphics2D.getCompClip().getBoundsIntersectionXYXY(paramInt1, paramInt2, i, j);
    if (region.isEmpty())
      return; 
    int k = region.getLoX();
    int m = region.getLoY();
    int n = region.getHiX();
    int i1 = region.getHiY();
    if (paramInt3 < 2 || paramInt4 < 2) {
      doSetRect(paramSurfaceData, paramPixelWriter, k, m, n, i1);
      return;
    } 
    if (m == paramInt2)
      doSetRect(paramSurfaceData, paramPixelWriter, k, m, n, m + 1); 
    if (k == paramInt1)
      doSetRect(paramSurfaceData, paramPixelWriter, k, m + 1, k + 1, i1 - 1); 
    if (n == i)
      doSetRect(paramSurfaceData, paramPixelWriter, n - 1, m + 1, n, i1 - 1); 
    if (i1 == j)
      doSetRect(paramSurfaceData, paramPixelWriter, k, i1 - 1, n, i1); 
  }
  
  static void doDrawGlyphList(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, GlyphList paramGlyphList, Region paramRegion) {
    int[] arrayOfInt = paramGlyphList.getBounds();
    paramRegion.clipBoxToBounds(arrayOfInt);
    int i = arrayOfInt[0];
    int j = arrayOfInt[1];
    int k = arrayOfInt[2];
    int m = arrayOfInt[3];
    WritableRaster writableRaster = (WritableRaster)paramSurfaceData.getRaster(i, j, k - i, m - j);
    paramPixelWriter.setRaster(writableRaster);
    int n = paramGlyphList.getNumGlyphs();
    for (byte b = 0; b < n; b++) {
      paramGlyphList.setGlyphIndex(b);
      int[] arrayOfInt1 = paramGlyphList.getMetrics();
      int i1 = arrayOfInt1[0];
      int i2 = arrayOfInt1[1];
      int i3 = arrayOfInt1[2];
      int i4 = i1 + i3;
      int i5 = i2 + arrayOfInt1[3];
      int i6 = 0;
      if (i1 < i) {
        i6 = i - i1;
        i1 = i;
      } 
      if (i2 < j) {
        i6 += (j - i2) * i3;
        i2 = j;
      } 
      if (i4 > k)
        i4 = k; 
      if (i5 > m)
        i5 = m; 
      if (i4 > i1 && i5 > i2) {
        byte[] arrayOfByte = paramGlyphList.getGrayBits();
        i3 -= i4 - i1;
        for (int i7 = i2; i7 < i5; i7++) {
          for (int i8 = i1; i8 < i4; i8++) {
            if (arrayOfByte[i6++] < 0)
              paramPixelWriter.writePixel(i8, i7); 
          } 
          i6 += i3;
        } 
      } 
    } 
  }
  
  static int outcode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    int i;
    if (paramInt2 < paramInt4) {
      i = 1;
    } else if (paramInt2 > paramInt6) {
      i = 2;
    } else {
      i = 0;
    } 
    if (paramInt1 < paramInt3) {
      i |= 0x4;
    } else if (paramInt1 > paramInt5) {
      i |= 0x8;
    } 
    return i;
  }
  
  public static boolean adjustLine(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 - 1;
    int j = paramInt4 - 1;
    int k = paramArrayOfint[0];
    int m = paramArrayOfint[1];
    int n = paramArrayOfint[2];
    int i1 = paramArrayOfint[3];
    if (i < paramInt1 || j < paramInt2)
      return false; 
    if (k == n) {
      if (k < paramInt1 || k > i)
        return false; 
      if (m > i1) {
        int i2 = m;
        m = i1;
        i1 = i2;
      } 
      if (m < paramInt2)
        m = paramInt2; 
      if (i1 > j)
        i1 = j; 
      if (m > i1)
        return false; 
      paramArrayOfint[1] = m;
      paramArrayOfint[3] = i1;
    } else if (m == i1) {
      if (m < paramInt2 || m > j)
        return false; 
      if (k > n) {
        int i2 = k;
        k = n;
        n = i2;
      } 
      if (k < paramInt1)
        k = paramInt1; 
      if (n > i)
        n = i; 
      if (k > n)
        return false; 
      paramArrayOfint[0] = k;
      paramArrayOfint[2] = n;
    } else {
      int i4 = n - k;
      int i5 = i1 - m;
      int i6 = (i4 < 0) ? -i4 : i4;
      int i7 = (i5 < 0) ? -i5 : i5;
      boolean bool = (i6 >= i7) ? true : false;
      int i2 = outcode(k, m, paramInt1, paramInt2, i, j);
      int i3 = outcode(n, i1, paramInt1, paramInt2, i, j);
      while ((i2 | i3) != 0) {
        if ((i2 & i3) != 0)
          return false; 
        if (i2 != 0) {
          if (0 != (i2 & 0x3)) {
            if (0 != (i2 & 0x1)) {
              m = paramInt2;
            } else {
              m = j;
            } 
            int i9 = m - paramArrayOfint[1];
            if (i9 < 0)
              i9 = -i9; 
            int i8 = 2 * i9 * i6 + i7;
            if (bool)
              i8 += i7 - i6 - 1; 
            i8 /= 2 * i7;
            if (i4 < 0)
              i8 = -i8; 
            k = paramArrayOfint[0] + i8;
          } else if (0 != (i2 & 0xC)) {
            if (0 != (i2 & 0x4)) {
              k = paramInt1;
            } else {
              k = i;
            } 
            int i8 = k - paramArrayOfint[0];
            if (i8 < 0)
              i8 = -i8; 
            int i9 = 2 * i8 * i7 + i6;
            if (!bool)
              i9 += i6 - i7 - 1; 
            i9 /= 2 * i6;
            if (i5 < 0)
              i9 = -i9; 
            m = paramArrayOfint[1] + i9;
          } 
          i2 = outcode(k, m, paramInt1, paramInt2, i, j);
          continue;
        } 
        if (0 != (i3 & 0x3)) {
          if (0 != (i3 & 0x1)) {
            i1 = paramInt2;
          } else {
            i1 = j;
          } 
          int i9 = i1 - paramArrayOfint[3];
          if (i9 < 0)
            i9 = -i9; 
          int i8 = 2 * i9 * i6 + i7;
          if (bool) {
            i8 += i7 - i6;
          } else {
            i8--;
          } 
          i8 /= 2 * i7;
          if (i4 > 0)
            i8 = -i8; 
          n = paramArrayOfint[2] + i8;
        } else if (0 != (i3 & 0xC)) {
          if (0 != (i3 & 0x4)) {
            n = paramInt1;
          } else {
            n = i;
          } 
          int i8 = n - paramArrayOfint[2];
          if (i8 < 0)
            i8 = -i8; 
          int i9 = 2 * i8 * i7 + i6;
          if (bool) {
            i9--;
          } else {
            i9 += i6 - i7;
          } 
          i9 /= 2 * i6;
          if (i5 > 0)
            i9 = -i9; 
          i1 = paramArrayOfint[3] + i9;
        } 
        i3 = outcode(n, i1, paramInt1, paramInt2, i, j);
      } 
      paramArrayOfint[0] = k;
      paramArrayOfint[1] = m;
      paramArrayOfint[2] = n;
      paramArrayOfint[3] = i1;
      paramArrayOfint[4] = i4;
      paramArrayOfint[5] = i5;
      paramArrayOfint[6] = i6;
      paramArrayOfint[7] = i7;
    } 
    return true;
  }
  
  static PixelWriter createSolidPixelWriter(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData) {
    ColorModel colorModel = paramSurfaceData.getColorModel();
    Object object = colorModel.getDataElements(paramSunGraphics2D.eargb, null);
    return new SolidPixelWriter(object);
  }
  
  static PixelWriter createXorPixelWriter(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData) {
    ColorModel colorModel = paramSurfaceData.getColorModel();
    Object object1 = colorModel.getDataElements(paramSunGraphics2D.eargb, null);
    XORComposite xORComposite = (XORComposite)paramSunGraphics2D.getComposite();
    int i = xORComposite.getXorColor().getRGB();
    Object object2 = colorModel.getDataElements(i, null);
    switch (colorModel.getTransferType()) {
      case 0:
        return new XorPixelWriter.ByteData(object1, object2);
      case 1:
      case 2:
        return new XorPixelWriter.ShortData(object1, object2);
      case 3:
        return new XorPixelWriter.IntData(object1, object2);
      case 4:
        return new XorPixelWriter.FloatData(object1, object2);
      case 5:
        return new XorPixelWriter.DoubleData(object1, object2);
    } 
    throw new InternalError("Unsupported XOR pixel type");
  }
}
