package sun.dc;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathException;
import sun.dc.pr.PRException;
import sun.dc.pr.PathDasher;
import sun.dc.pr.PathStroker;
import sun.dc.pr.Rasterizer;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;

public class DuctusRenderingEngine extends RenderingEngine {
  static final float PenUnits = 0.01F;
  
  static final int MinPenUnits = 100;
  
  static final int MinPenUnitsAA = 20;
  
  static final float MinPenSizeAA = 0.19999999F;
  
  static final float UPPER_BND = 1.7014117E38F;
  
  static final float LOWER_BND = -1.7014117E38F;
  
  private static final int[] RasterizerCaps = new int[] { 30, 10, 20 };
  
  private static final int[] RasterizerCorners = new int[] { 50, 10, 40 };
  
  private static Rasterizer theRasterizer;
  
  static float[] getTransformMatrix(AffineTransform paramAffineTransform) {
    float[] arrayOfFloat = new float[4];
    double[] arrayOfDouble = new double[6];
    paramAffineTransform.getMatrix(arrayOfDouble);
    for (byte b = 0; b < 4; b++)
      arrayOfFloat[b] = (float)arrayOfDouble[b]; 
    return arrayOfFloat;
  }
  
  public Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOffloat, float paramFloat3) {
    FillAdapter fillAdapter = new FillAdapter(this);
    PathStroker pathStroker = new PathStroker((PathConsumer)fillAdapter);
    PathDasher pathDasher = null;
    try {
      PathStroker pathStroker1;
      pathStroker.setPenDiameter(paramFloat1);
      pathStroker.setPenT4(null);
      pathStroker.setCaps(RasterizerCaps[paramInt1]);
      pathStroker.setCorners(RasterizerCorners[paramInt2], paramFloat2);
      if (paramArrayOffloat != null) {
        pathDasher = new PathDasher((PathConsumer)pathStroker);
        pathDasher.setDash(paramArrayOffloat, paramFloat3);
        pathDasher.setDashT4(null);
        PathDasher pathDasher1 = pathDasher;
      } else {
        pathStroker1 = pathStroker;
      } 
      feedConsumer((PathConsumer)pathStroker1, paramShape.getPathIterator(null));
    } finally {
      pathStroker.dispose();
      if (pathDasher != null)
        pathDasher.dispose(); 
    } 
    return fillAdapter.getShape();
  }
  
  public void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D) {
    PathDasher pathDasher;
    PathConsumer pathConsumer;
    PathStroker pathStroker1 = new PathStroker(paramPathConsumer2D);
    PathStroker pathStroker2 = pathStroker1;
    float[] arrayOfFloat1 = null;
    if (!paramBoolean1) {
      pathStroker1.setPenDiameter(paramBasicStroke.getLineWidth());
      if (paramAffineTransform != null)
        arrayOfFloat1 = getTransformMatrix(paramAffineTransform); 
      pathStroker1.setPenT4(arrayOfFloat1);
      pathStroker1.setPenFitting(0.01F, 100);
    } 
    pathStroker1.setCaps(RasterizerCaps[paramBasicStroke.getEndCap()]);
    pathStroker1.setCorners(RasterizerCorners[paramBasicStroke.getLineJoin()], paramBasicStroke
        .getMiterLimit());
    float[] arrayOfFloat2 = paramBasicStroke.getDashArray();
    if (arrayOfFloat2 != null) {
      PathDasher pathDasher1 = new PathDasher((PathConsumer)pathStroker1);
      pathDasher1.setDash(arrayOfFloat2, paramBasicStroke.getDashPhase());
      if (paramAffineTransform != null && arrayOfFloat1 == null)
        arrayOfFloat1 = getTransformMatrix(paramAffineTransform); 
      pathDasher1.setDashT4(arrayOfFloat1);
      pathDasher = pathDasher1;
    } 
    try {
      PathIterator pathIterator = paramShape.getPathIterator(paramAffineTransform);
      feedConsumer(pathIterator, (PathConsumer)pathDasher, paramBoolean2, 0.25F);
    } catch (PathException pathException) {
      throw new InternalError("Unable to Stroke shape (" + pathException
          .getMessage() + ")", pathException);
    } finally {
      while (pathConsumer != null && pathConsumer != paramPathConsumer2D) {
        PathConsumer pathConsumer1 = pathConsumer.getConsumer();
        pathConsumer.dispose();
        pathConsumer = pathConsumer1;
      } 
    } 
  }
  
  public static void feedConsumer(PathIterator paramPathIterator, PathConsumer paramPathConsumer, boolean paramBoolean, float paramFloat) throws PathException {
    paramPathConsumer.beginPath();
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float[] arrayOfFloat = new float[6];
    float f3 = 0.5F - paramFloat;
    float f4 = 0.0F;
    float f5 = 0.0F;
    while (!paramPathIterator.isDone()) {
      int i = paramPathIterator.currentSegment(arrayOfFloat);
      if (bool1 == true) {
        bool1 = false;
        if (i != 0) {
          paramPathConsumer.beginSubpath(f1, f2);
          bool3 = true;
        } 
      } 
      if (paramBoolean) {
        byte b;
        switch (i) {
          case 3:
            b = 4;
            break;
          case 2:
            b = 2;
            break;
          case 0:
          case 1:
            b = 0;
            break;
          default:
            b = -1;
            break;
        } 
        if (b >= 0) {
          float f6 = arrayOfFloat[b];
          float f7 = arrayOfFloat[b + 1];
          float f8 = (float)Math.floor((f6 + f3)) + paramFloat;
          float f9 = (float)Math.floor((f7 + f3)) + paramFloat;
          arrayOfFloat[b] = f8;
          arrayOfFloat[b + 1] = f9;
          f8 -= f6;
          f9 -= f7;
          switch (i) {
            case 3:
              arrayOfFloat[0] = arrayOfFloat[0] + f4;
              arrayOfFloat[1] = arrayOfFloat[1] + f5;
              arrayOfFloat[2] = arrayOfFloat[2] + f8;
              arrayOfFloat[3] = arrayOfFloat[3] + f9;
              break;
            case 2:
              arrayOfFloat[0] = arrayOfFloat[0] + (f8 + f4) / 2.0F;
              arrayOfFloat[1] = arrayOfFloat[1] + (f9 + f5) / 2.0F;
              break;
          } 
          f4 = f8;
          f5 = f9;
        } 
      } 
      switch (i) {
        case 0:
          if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
            f1 = arrayOfFloat[0];
            f2 = arrayOfFloat[1];
            paramPathConsumer.beginSubpath(f1, f2);
            bool3 = true;
            bool2 = false;
            break;
          } 
          bool2 = true;
          break;
        case 1:
          if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
            if (bool2) {
              paramPathConsumer.beginSubpath(arrayOfFloat[0], arrayOfFloat[1]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            paramPathConsumer.appendLine(arrayOfFloat[0], arrayOfFloat[1]);
          } 
          break;
        case 2:
          if (arrayOfFloat[2] < 1.7014117E38F && arrayOfFloat[2] > -1.7014117E38F && arrayOfFloat[3] < 1.7014117E38F && arrayOfFloat[3] > -1.7014117E38F) {
            if (bool2) {
              paramPathConsumer.beginSubpath(arrayOfFloat[2], arrayOfFloat[3]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
              paramPathConsumer.appendQuadratic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
              break;
            } 
            paramPathConsumer.appendLine(arrayOfFloat[2], arrayOfFloat[3]);
          } 
          break;
        case 3:
          if (arrayOfFloat[4] < 1.7014117E38F && arrayOfFloat[4] > -1.7014117E38F && arrayOfFloat[5] < 1.7014117E38F && arrayOfFloat[5] > -1.7014117E38F) {
            if (bool2) {
              paramPathConsumer.beginSubpath(arrayOfFloat[4], arrayOfFloat[5]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F && arrayOfFloat[2] < 1.7014117E38F && arrayOfFloat[2] > -1.7014117E38F && arrayOfFloat[3] < 1.7014117E38F && arrayOfFloat[3] > -1.7014117E38F) {
              paramPathConsumer.appendCubic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
              break;
            } 
            paramPathConsumer.appendLine(arrayOfFloat[4], arrayOfFloat[5]);
          } 
          break;
        case 4:
          if (bool3) {
            paramPathConsumer.closedSubpath();
            bool3 = false;
            bool1 = true;
          } 
          break;
      } 
      paramPathIterator.next();
    } 
    paramPathConsumer.endPath();
  }
  
  public static synchronized Rasterizer getRasterizer() {
    Rasterizer rasterizer = theRasterizer;
    if (rasterizer == null) {
      rasterizer = new Rasterizer();
    } else {
      theRasterizer = null;
    } 
    return rasterizer;
  }
  
  public static synchronized void dropRasterizer(Rasterizer paramRasterizer) {
    paramRasterizer.reset();
    theRasterizer = paramRasterizer;
  }
  
  public float getMinimumAAPenSize() {
    return 0.19999999F;
  }
  
  public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfint) {
    Rasterizer rasterizer = getRasterizer();
    PathIterator pathIterator = paramShape.getPathIterator(paramAffineTransform);
    if (paramBasicStroke != null) {
      float[] arrayOfFloat1 = null;
      rasterizer.setUsage(3);
      if (paramBoolean1) {
        rasterizer.setPenDiameter(0.19999999F);
      } else {
        rasterizer.setPenDiameter(paramBasicStroke.getLineWidth());
        if (paramAffineTransform != null) {
          arrayOfFloat1 = getTransformMatrix(paramAffineTransform);
          rasterizer.setPenT4(arrayOfFloat1);
        } 
        rasterizer.setPenFitting(0.01F, 20);
      } 
      rasterizer.setCaps(RasterizerCaps[paramBasicStroke.getEndCap()]);
      rasterizer.setCorners(RasterizerCorners[paramBasicStroke.getLineJoin()], paramBasicStroke
          .getMiterLimit());
      float[] arrayOfFloat2 = paramBasicStroke.getDashArray();
      if (arrayOfFloat2 != null) {
        rasterizer.setDash(arrayOfFloat2, paramBasicStroke.getDashPhase());
        if (paramAffineTransform != null && arrayOfFloat1 == null)
          arrayOfFloat1 = getTransformMatrix(paramAffineTransform); 
        rasterizer.setDashT4(arrayOfFloat1);
      } 
    } else {
      rasterizer.setUsage((pathIterator.getWindingRule() == 0) ? 1 : 2);
    } 
    rasterizer.beginPath();
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float[] arrayOfFloat = new float[6];
    float f3 = 0.0F;
    float f4 = 0.0F;
    while (!pathIterator.isDone()) {
      int i = pathIterator.currentSegment(arrayOfFloat);
      if (bool1 == true) {
        bool1 = false;
        if (i != 0) {
          rasterizer.beginSubpath(f1, f2);
          bool3 = true;
        } 
      } 
      if (paramBoolean2) {
        byte b;
        switch (i) {
          case 3:
            b = 4;
            break;
          case 2:
            b = 2;
            break;
          case 0:
          case 1:
            b = 0;
            break;
          default:
            b = -1;
            break;
        } 
        if (b >= 0) {
          float f5 = arrayOfFloat[b];
          float f6 = arrayOfFloat[b + 1];
          float f7 = (float)Math.floor(f5) + 0.5F;
          float f8 = (float)Math.floor(f6) + 0.5F;
          arrayOfFloat[b] = f7;
          arrayOfFloat[b + 1] = f8;
          f7 -= f5;
          f8 -= f6;
          switch (i) {
            case 3:
              arrayOfFloat[0] = arrayOfFloat[0] + f3;
              arrayOfFloat[1] = arrayOfFloat[1] + f4;
              arrayOfFloat[2] = arrayOfFloat[2] + f7;
              arrayOfFloat[3] = arrayOfFloat[3] + f8;
              break;
            case 2:
              arrayOfFloat[0] = arrayOfFloat[0] + (f7 + f3) / 2.0F;
              arrayOfFloat[1] = arrayOfFloat[1] + (f8 + f4) / 2.0F;
              break;
          } 
          f3 = f7;
          f4 = f8;
        } 
      } 
      switch (i) {
        case 0:
          if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
            f1 = arrayOfFloat[0];
            f2 = arrayOfFloat[1];
            rasterizer.beginSubpath(f1, f2);
            bool3 = true;
            bool2 = false;
            break;
          } 
          bool2 = true;
          break;
        case 1:
          if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
            if (bool2) {
              rasterizer.beginSubpath(arrayOfFloat[0], arrayOfFloat[1]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            rasterizer.appendLine(arrayOfFloat[0], arrayOfFloat[1]);
          } 
          break;
        case 2:
          if (arrayOfFloat[2] < 1.7014117E38F && arrayOfFloat[2] > -1.7014117E38F && arrayOfFloat[3] < 1.7014117E38F && arrayOfFloat[3] > -1.7014117E38F) {
            if (bool2) {
              rasterizer.beginSubpath(arrayOfFloat[2], arrayOfFloat[3]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F) {
              rasterizer.appendQuadratic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
              break;
            } 
            rasterizer.appendLine(arrayOfFloat[2], arrayOfFloat[3]);
          } 
          break;
        case 3:
          if (arrayOfFloat[4] < 1.7014117E38F && arrayOfFloat[4] > -1.7014117E38F && arrayOfFloat[5] < 1.7014117E38F && arrayOfFloat[5] > -1.7014117E38F) {
            if (bool2) {
              rasterizer.beginSubpath(arrayOfFloat[4], arrayOfFloat[5]);
              bool3 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat[0] < 1.7014117E38F && arrayOfFloat[0] > -1.7014117E38F && arrayOfFloat[1] < 1.7014117E38F && arrayOfFloat[1] > -1.7014117E38F && arrayOfFloat[2] < 1.7014117E38F && arrayOfFloat[2] > -1.7014117E38F && arrayOfFloat[3] < 1.7014117E38F && arrayOfFloat[3] > -1.7014117E38F) {
              rasterizer.appendCubic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
              break;
            } 
            rasterizer.appendLine(arrayOfFloat[4], arrayOfFloat[5]);
          } 
          break;
        case 4:
          if (bool3) {
            rasterizer.closedSubpath();
            bool3 = false;
            bool1 = true;
          } 
          break;
      } 
      pathIterator.next();
    } 
    try {
      rasterizer.endPath();
      rasterizer.getAlphaBox(paramArrayOfint);
      paramRegion.clipBoxToBounds(paramArrayOfint);
      if (paramArrayOfint[0] >= paramArrayOfint[2] || paramArrayOfint[1] >= paramArrayOfint[3]) {
        dropRasterizer(rasterizer);
        return null;
      } 
      rasterizer.setOutputArea(paramArrayOfint[0], paramArrayOfint[1], paramArrayOfint[2] - paramArrayOfint[0], paramArrayOfint[3] - paramArrayOfint[1]);
    } catch (PRException pRException) {
      System.err.println("DuctusRenderingEngine.getAATileGenerator: " + pRException);
    } 
    return (AATileGenerator)rasterizer;
  }
  
  public AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfint) {
    double d1, d2, d3, d4;
    boolean bool = (paramDouble7 > 0.0D && paramDouble8 > 0.0D) ? true : false;
    if (bool) {
      d1 = paramDouble3 * paramDouble7;
      d2 = paramDouble4 * paramDouble7;
      d3 = paramDouble5 * paramDouble8;
      d4 = paramDouble6 * paramDouble8;
      paramDouble1 -= (d1 + d3) / 2.0D;
      paramDouble2 -= (d2 + d4) / 2.0D;
      paramDouble3 += d1;
      paramDouble4 += d2;
      paramDouble5 += d3;
      paramDouble6 += d4;
      if (paramDouble7 > 1.0D && paramDouble8 > 1.0D)
        bool = false; 
    } else {
      d1 = d2 = d3 = d4 = 0.0D;
    } 
    Rasterizer rasterizer = getRasterizer();
    rasterizer.setUsage(1);
    rasterizer.beginPath();
    rasterizer.beginSubpath((float)paramDouble1, (float)paramDouble2);
    rasterizer.appendLine((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
    rasterizer.appendLine((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
    rasterizer.appendLine((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
    rasterizer.closedSubpath();
    if (bool) {
      paramDouble1 += d1 + d3;
      paramDouble2 += d2 + d4;
      paramDouble3 -= 2.0D * d1;
      paramDouble4 -= 2.0D * d2;
      paramDouble5 -= 2.0D * d3;
      paramDouble6 -= 2.0D * d4;
      rasterizer.beginSubpath((float)paramDouble1, (float)paramDouble2);
      rasterizer.appendLine((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
      rasterizer.appendLine((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
      rasterizer.appendLine((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
      rasterizer.closedSubpath();
    } 
    try {
      rasterizer.endPath();
      rasterizer.getAlphaBox(paramArrayOfint);
      paramRegion.clipBoxToBounds(paramArrayOfint);
      if (paramArrayOfint[0] >= paramArrayOfint[2] || paramArrayOfint[1] >= paramArrayOfint[3]) {
        dropRasterizer(rasterizer);
        return null;
      } 
      rasterizer.setOutputArea(paramArrayOfint[0], paramArrayOfint[1], paramArrayOfint[2] - paramArrayOfint[0], paramArrayOfint[3] - paramArrayOfint[1]);
    } catch (PRException pRException) {
      System.err.println("DuctusRenderingEngine.getAATileGenerator: " + pRException);
    } 
    return (AATileGenerator)rasterizer;
  }
  
  private void feedConsumer(PathConsumer paramPathConsumer, PathIterator paramPathIterator) {
    try {
      paramPathConsumer.beginPath();
      boolean bool = false;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float[] arrayOfFloat = new float[6];
      while (!paramPathIterator.isDone()) {
        int i = paramPathIterator.currentSegment(arrayOfFloat);
        if (bool == true) {
          bool = false;
          if (i != 0)
            paramPathConsumer.beginSubpath(f1, f2); 
        } 
        switch (i) {
          case 0:
            f1 = arrayOfFloat[0];
            f2 = arrayOfFloat[1];
            paramPathConsumer.beginSubpath(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 1:
            paramPathConsumer.appendLine(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 2:
            paramPathConsumer.appendQuadratic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
            break;
          case 3:
            paramPathConsumer.appendCubic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
            break;
          case 4:
            paramPathConsumer.closedSubpath();
            bool = true;
            break;
        } 
        paramPathIterator.next();
      } 
      paramPathConsumer.endPath();
    } catch (PathException pathException) {
      throw new InternalError("Unable to Stroke shape (" + pathException
          .getMessage() + ")", pathException);
    } 
  }
  
  private class DuctusRenderingEngine {}
}
