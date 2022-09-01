package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class Path2D implements Shape, Cloneable {
  public static final int WIND_EVEN_ODD = 0;
  
  public static final int WIND_NON_ZERO = 1;
  
  private static final byte SEG_MOVETO = 0;
  
  private static final byte SEG_LINETO = 1;
  
  private static final byte SEG_QUADTO = 2;
  
  private static final byte SEG_CUBICTO = 3;
  
  private static final byte SEG_CLOSE = 4;
  
  transient byte[] pointTypes;
  
  transient int numTypes;
  
  transient int numCoords;
  
  transient int windingRule;
  
  static final int INIT_SIZE = 20;
  
  static final int EXPAND_MAX = 500;
  
  private static final byte SERIAL_STORAGE_FLT_ARRAY = 48;
  
  private static final byte SERIAL_STORAGE_DBL_ARRAY = 49;
  
  private static final byte SERIAL_SEG_FLT_MOVETO = 64;
  
  private static final byte SERIAL_SEG_FLT_LINETO = 65;
  
  private static final byte SERIAL_SEG_FLT_QUADTO = 66;
  
  private static final byte SERIAL_SEG_FLT_CUBICTO = 67;
  
  private static final byte SERIAL_SEG_DBL_MOVETO = 80;
  
  private static final byte SERIAL_SEG_DBL_LINETO = 81;
  
  private static final byte SERIAL_SEG_DBL_QUADTO = 82;
  
  private static final byte SERIAL_SEG_DBL_CUBICTO = 83;
  
  private static final byte SERIAL_SEG_CLOSE = 96;
  
  private static final byte SERIAL_PATH_END = 97;
  
  Path2D() {}
  
  Path2D(int paramInt1, int paramInt2) {
    setWindingRule(paramInt1);
    this.pointTypes = new byte[paramInt2];
  }
  
  abstract float[] cloneCoordsFloat(AffineTransform paramAffineTransform);
  
  abstract double[] cloneCoordsDouble(AffineTransform paramAffineTransform);
  
  abstract void append(float paramFloat1, float paramFloat2);
  
  abstract void append(double paramDouble1, double paramDouble2);
  
  abstract Point2D getPoint(int paramInt);
  
  abstract void needRoom(boolean paramBoolean, int paramInt);
  
  abstract int pointCrossings(double paramDouble1, double paramDouble2);
  
  abstract int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public abstract void moveTo(double paramDouble1, double paramDouble2);
  
  public abstract void lineTo(double paramDouble1, double paramDouble2);
  
  public abstract void quadTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public abstract void curveTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  static abstract class Path2D {}
  
  public static class Path2D {}
  
  public static class Float extends Path2D implements Serializable {
    transient float[] floatCoords;
    
    private static final long serialVersionUID = 6990832515060788886L;
    
    public Float() {
      this(1, 20);
    }
    
    public Float(int param1Int) {
      this(param1Int, 20);
    }
    
    public Float(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.floatCoords = new float[param1Int2 * 2];
    }
    
    public Float(Shape param1Shape) {
      this(param1Shape, (AffineTransform)null);
    }
    
    public Float(Shape param1Shape, AffineTransform param1AffineTransform) {
      if (param1Shape instanceof Path2D) {
        Path2D path2D = (Path2D)param1Shape;
        setWindingRule(path2D.windingRule);
        this.numTypes = path2D.numTypes;
        this.pointTypes = Arrays.copyOf(path2D.pointTypes, path2D.pointTypes.length);
        this.numCoords = path2D.numCoords;
        this.floatCoords = path2D.cloneCoordsFloat(param1AffineTransform);
      } else {
        PathIterator pathIterator = param1Shape.getPathIterator(param1AffineTransform);
        setWindingRule(pathIterator.getWindingRule());
        this.pointTypes = new byte[20];
        this.floatCoords = new float[40];
        append(pathIterator, false);
      } 
    }
    
    float[] cloneCoordsFloat(AffineTransform param1AffineTransform) {
      float[] arrayOfFloat;
      if (param1AffineTransform == null) {
        arrayOfFloat = Arrays.copyOf(this.floatCoords, this.floatCoords.length);
      } else {
        arrayOfFloat = new float[this.floatCoords.length];
        param1AffineTransform.transform(this.floatCoords, 0, arrayOfFloat, 0, this.numCoords / 2);
      } 
      return arrayOfFloat;
    }
    
    double[] cloneCoordsDouble(AffineTransform param1AffineTransform) {
      double[] arrayOfDouble = new double[this.floatCoords.length];
      if (param1AffineTransform == null) {
        for (byte b = 0; b < this.numCoords; b++)
          arrayOfDouble[b] = this.floatCoords[b]; 
      } else {
        param1AffineTransform.transform(this.floatCoords, 0, arrayOfDouble, 0, this.numCoords / 2);
      } 
      return arrayOfDouble;
    }
    
    void append(float param1Float1, float param1Float2) {
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
    }
    
    void append(double param1Double1, double param1Double2) {
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
    }
    
    Point2D getPoint(int param1Int) {
      return new Point2D.Float(this.floatCoords[param1Int], this.floatCoords[param1Int + 1]);
    }
    
    void needRoom(boolean param1Boolean, int param1Int) {
      if (param1Boolean && this.numTypes == 0)
        throw new IllegalPathStateException("missing initial moveto in path definition"); 
      int i = this.pointTypes.length;
      if (this.numTypes >= i) {
        int j = i;
        if (j > 500) {
          j = 500;
        } else if (j == 0) {
          j = 1;
        } 
        this.pointTypes = Arrays.copyOf(this.pointTypes, i + j);
      } 
      i = this.floatCoords.length;
      if (this.numCoords + param1Int > i) {
        int j = i;
        if (j > 1000)
          j = 1000; 
        if (j < param1Int)
          j = param1Int; 
        this.floatCoords = Arrays.copyOf(this.floatCoords, i + j);
      } 
    }
    
    public final synchronized void moveTo(double param1Double1, double param1Double2) {
      if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
        this.floatCoords[this.numCoords - 2] = (float)param1Double1;
        this.floatCoords[this.numCoords - 1] = (float)param1Double2;
      } else {
        needRoom(false, 2);
        this.pointTypes[this.numTypes++] = 0;
        this.floatCoords[this.numCoords++] = (float)param1Double1;
        this.floatCoords[this.numCoords++] = (float)param1Double2;
      } 
    }
    
    public final synchronized void moveTo(float param1Float1, float param1Float2) {
      if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
        this.floatCoords[this.numCoords - 2] = param1Float1;
        this.floatCoords[this.numCoords - 1] = param1Float2;
      } else {
        needRoom(false, 2);
        this.pointTypes[this.numTypes++] = 0;
        this.floatCoords[this.numCoords++] = param1Float1;
        this.floatCoords[this.numCoords++] = param1Float2;
      } 
    }
    
    public final synchronized void lineTo(double param1Double1, double param1Double2) {
      needRoom(true, 2);
      this.pointTypes[this.numTypes++] = 1;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
    }
    
    public final synchronized void lineTo(float param1Float1, float param1Float2) {
      needRoom(true, 2);
      this.pointTypes[this.numTypes++] = 1;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
    }
    
    public final synchronized void quadTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      needRoom(true, 4);
      this.pointTypes[this.numTypes++] = 2;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
      this.floatCoords[this.numCoords++] = (float)param1Double3;
      this.floatCoords[this.numCoords++] = (float)param1Double4;
    }
    
    public final synchronized void quadTo(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      needRoom(true, 4);
      this.pointTypes[this.numTypes++] = 2;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
      this.floatCoords[this.numCoords++] = param1Float3;
      this.floatCoords[this.numCoords++] = param1Float4;
    }
    
    public final synchronized void curveTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      needRoom(true, 6);
      this.pointTypes[this.numTypes++] = 3;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
      this.floatCoords[this.numCoords++] = (float)param1Double3;
      this.floatCoords[this.numCoords++] = (float)param1Double4;
      this.floatCoords[this.numCoords++] = (float)param1Double5;
      this.floatCoords[this.numCoords++] = (float)param1Double6;
    }
    
    public final synchronized void curveTo(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6) {
      needRoom(true, 6);
      this.pointTypes[this.numTypes++] = 3;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
      this.floatCoords[this.numCoords++] = param1Float3;
      this.floatCoords[this.numCoords++] = param1Float4;
      this.floatCoords[this.numCoords++] = param1Float5;
      this.floatCoords[this.numCoords++] = param1Float6;
    }
    
    int pointCrossings(double param1Double1, double param1Double2) {
      float[] arrayOfFloat = this.floatCoords;
      double d1 = arrayOfFloat[0], d3 = d1;
      double d2 = arrayOfFloat[1], d4 = d2;
      int i = 0;
      byte b1 = 2;
      for (byte b2 = 1; b2 < this.numTypes; b2++) {
        double d5;
        double d6;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d4 != d2)
              i += 
                Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d1 = d3 = arrayOfFloat[b1++];
            d2 = d4 = arrayOfFloat[b1++];
          case 1:
            i += 
              Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++]);
            d3 = d5;
            d4 = d6;
          case 2:
            i += 
              Curve.pointCrossingsForQuad(param1Double1, param1Double2, d3, d4, arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 3:
            i += 
              Curve.pointCrossingsForCubic(param1Double1, param1Double2, d3, d4, arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 4:
            if (d4 != d2)
              i += 
                Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d3 = d1;
            d4 = d2;
            break;
        } 
      } 
      if (d4 != d2)
        i += 
          Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
      return i;
    }
    
    int rectCrossings(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      float[] arrayOfFloat = this.floatCoords;
      double d3 = arrayOfFloat[0], d1 = d3;
      double d4 = arrayOfFloat[1], d2 = d4;
      int i = 0;
      byte b1 = 2;
      byte b2 = 1;
      for (; i != Integer.MIN_VALUE && b2 < this.numTypes; 
        b2++) {
        double d5;
        double d6;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d3 = d1 = arrayOfFloat[b1++];
            d4 = d2 = arrayOfFloat[b1++];
          case 1:
            i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++]);
            d1 = d5;
            d2 = d6;
          case 2:
            i = Curve.rectCrossingsForQuad(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 3:
            i = Curve.rectCrossingsForCubic(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 4:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d1 = d3;
            d2 = d4;
            break;
        } 
      } 
      if (i != Integer.MIN_VALUE && (d1 != d3 || d2 != d4))
        i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
      return i;
    }
    
    public final void append(PathIterator param1PathIterator, boolean param1Boolean) {
      float[] arrayOfFloat = new float[6];
      while (!param1PathIterator.isDone()) {
        switch (param1PathIterator.currentSegment(arrayOfFloat)) {
          case 0:
            if (!param1Boolean || this.numTypes < 1 || this.numCoords < 1) {
              moveTo(arrayOfFloat[0], arrayOfFloat[1]);
              break;
            } 
            if (this.pointTypes[this.numTypes - 1] != 4 && this.floatCoords[this.numCoords - 2] == arrayOfFloat[0] && this.floatCoords[this.numCoords - 1] == arrayOfFloat[1])
              break; 
            lineTo(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 1:
            lineTo(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 2:
            quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
            break;
          case 3:
            curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
            break;
          case 4:
            closePath();
            break;
        } 
        param1PathIterator.next();
        param1Boolean = false;
      } 
    }
    
    public final void transform(AffineTransform param1AffineTransform) {
      param1AffineTransform.transform(this.floatCoords, 0, this.floatCoords, 0, this.numCoords / 2);
    }
    
    public final synchronized Rectangle2D getBounds2D() {
      float f1, f2, f3, f4;
      int i = this.numCoords;
      if (i > 0) {
        f2 = f4 = this.floatCoords[--i];
        f1 = f3 = this.floatCoords[--i];
        while (i > 0) {
          float f5 = this.floatCoords[--i];
          float f6 = this.floatCoords[--i];
          if (f6 < f1)
            f1 = f6; 
          if (f5 < f2)
            f2 = f5; 
          if (f6 > f3)
            f3 = f6; 
          if (f5 > f4)
            f4 = f5; 
        } 
      } else {
        f1 = f2 = f3 = f4 = 0.0F;
      } 
      return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
    }
    
    public final PathIterator getPathIterator(AffineTransform param1AffineTransform) {
      if (param1AffineTransform == null)
        return new CopyIterator(this); 
      return new TxIterator(this, param1AffineTransform);
    }
    
    public final Object clone() {
      if (this instanceof GeneralPath)
        return new GeneralPath((Shape)this); 
      return new Float((Shape)this);
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      writeObject(param1ObjectOutputStream, false);
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException {
      readObject(param1ObjectInputStream, false);
    }
    
    static class Float {}
    
    static class Float {}
  }
  
  public final synchronized void closePath() {
    if (this.numTypes == 0 || this.pointTypes[this.numTypes - 1] != 4) {
      needRoom(true, 0);
      this.pointTypes[this.numTypes++] = 4;
    } 
  }
  
  public final void append(Shape paramShape, boolean paramBoolean) {
    append(paramShape.getPathIterator(null), paramBoolean);
  }
  
  public abstract void append(PathIterator paramPathIterator, boolean paramBoolean);
  
  public final synchronized int getWindingRule() {
    return this.windingRule;
  }
  
  public final void setWindingRule(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO"); 
    this.windingRule = paramInt;
  }
  
  public final synchronized Point2D getCurrentPoint() {
    int i = this.numCoords;
    if (this.numTypes < 1 || i < 1)
      return null; 
    if (this.pointTypes[this.numTypes - 1] == 4)
      for (int j = this.numTypes - 2; j > 0; j--) {
        switch (this.pointTypes[j]) {
          case 0:
            break;
          case 1:
            i -= 2;
            break;
          case 2:
            i -= 4;
            break;
          case 3:
            i -= 6;
            break;
        } 
      }  
    return getPoint(i - 2);
  }
  
  public final synchronized void reset() {
    this.numTypes = this.numCoords = 0;
  }
  
  public abstract void transform(AffineTransform paramAffineTransform);
  
  public final synchronized Shape createTransformedShape(AffineTransform paramAffineTransform) {
    Path2D path2D = (Path2D)clone();
    if (paramAffineTransform != null)
      path2D.transform(paramAffineTransform); 
    return (Shape)path2D;
  }
  
  public final Rectangle getBounds() {
    return getBounds2D().getBounds();
  }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2) {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D) {
      byte b = (paramPathIterator.getWindingRule() == 1) ? -1 : 1;
      int i = Curve.pointCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2);
      return ((i & b) != 0);
    } 
    return false;
  }
  
  public static boolean contains(PathIterator paramPathIterator, Point2D paramPoint2D) {
    return contains(paramPathIterator, paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public final boolean contains(double paramDouble1, double paramDouble2) {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D) {
      if (this.numTypes < 2)
        return false; 
      boolean bool = (this.windingRule == 1) ? true : true;
      return ((pointCrossings(paramDouble1, paramDouble2) & bool) != 0);
    } 
    return false;
  }
  
  public final boolean contains(Point2D paramPoint2D) {
    return contains(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    byte b = (paramPathIterator.getWindingRule() == 1) ? -1 : 2;
    int i = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (i != Integer.MIN_VALUE && (i & b) != 0);
  }
  
  public static boolean contains(PathIterator paramPathIterator, Rectangle2D paramRectangle2D) {
    return contains(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    byte b = (this.windingRule == 1) ? -1 : 2;
    int i = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (i != Integer.MIN_VALUE && (i & b) != 0);
  }
  
  public final boolean contains(Rectangle2D paramRectangle2D) {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public static boolean intersects(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    byte b = (paramPathIterator.getWindingRule() == 1) ? -1 : 2;
    int i = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (i == Integer.MIN_VALUE || (i & b) != 0);
  }
  
  public static boolean intersects(PathIterator paramPathIterator, Rectangle2D paramRectangle2D) {
    return intersects(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    byte b = (this.windingRule == 1) ? -1 : 2;
    int i = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (i == Integer.MIN_VALUE || (i & b) != 0);
  }
  
  public final boolean intersects(Rectangle2D paramRectangle2D) {
    return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) {
    return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble);
  }
  
  public abstract Object clone();
  
  final void writeObject(ObjectOutputStream paramObjectOutputStream, boolean paramBoolean) throws IOException {
    float[] arrayOfFloat;
    Object object;
    paramObjectOutputStream.defaultWriteObject();
    if (paramBoolean) {
      object = ((Double)this).doubleCoords;
      arrayOfFloat = null;
    } else {
      arrayOfFloat = ((Float)this).floatCoords;
      object = null;
    } 
    int i = this.numTypes;
    paramObjectOutputStream.writeByte(paramBoolean ? 49 : 48);
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(this.numCoords);
    paramObjectOutputStream.writeByte((byte)this.windingRule);
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      byte b3, b4;
      switch (this.pointTypes[b2]) {
        case 0:
          b3 = 1;
          b4 = paramBoolean ? 80 : 64;
          break;
        case 1:
          b3 = 1;
          b4 = paramBoolean ? 81 : 65;
          break;
        case 2:
          b3 = 2;
          b4 = paramBoolean ? 82 : 66;
          break;
        case 3:
          b3 = 3;
          b4 = paramBoolean ? 83 : 67;
          break;
        case 4:
          b3 = 0;
          b4 = 96;
          break;
        default:
          throw new InternalError("unrecognized path type");
      } 
      paramObjectOutputStream.writeByte(b4);
      while (--b3 >= 0) {
        if (paramBoolean) {
          paramObjectOutputStream.writeDouble(object[b1++]);
          paramObjectOutputStream.writeDouble(object[b1++]);
          continue;
        } 
        paramObjectOutputStream.writeFloat(arrayOfFloat[b1++]);
        paramObjectOutputStream.writeFloat(arrayOfFloat[b1++]);
      } 
    } 
    paramObjectOutputStream.writeByte(97);
  }
  
  final void readObject(ObjectInputStream paramObjectInputStream, boolean paramBoolean) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readByte();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    try {
      setWindingRule(paramObjectInputStream.readByte());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException(illegalArgumentException.getMessage());
    } 
    this.pointTypes = new byte[(i < 0) ? 20 : i];
    if (j < 0)
      j = 40; 
    if (paramBoolean) {
      ((Double)this).doubleCoords = new double[j];
    } else {
      ((Float)this).floatCoords = new float[j];
    } 
    for (byte b = 0; i < 0 || b < i; b++) {
      boolean bool;
      byte b1, b2;
      byte b3 = paramObjectInputStream.readByte();
      switch (b3) {
        case 64:
          bool = false;
          b1 = 1;
          b2 = 0;
          break;
        case 65:
          bool = false;
          b1 = 1;
          b2 = 1;
          break;
        case 66:
          bool = false;
          b1 = 2;
          b2 = 2;
          break;
        case 67:
          bool = false;
          b1 = 3;
          b2 = 3;
          break;
        case 80:
          bool = true;
          b1 = 1;
          b2 = 0;
          break;
        case 81:
          bool = true;
          b1 = 1;
          b2 = 1;
          break;
        case 82:
          bool = true;
          b1 = 2;
          b2 = 2;
          break;
        case 83:
          bool = true;
          b1 = 3;
          b2 = 3;
          break;
        case 96:
          bool = false;
          b1 = 0;
          b2 = 4;
          break;
        case 97:
          if (i < 0)
            break; 
          throw new StreamCorruptedException("unexpected PATH_END");
        default:
          throw new StreamCorruptedException("unrecognized path type");
      } 
      needRoom((b2 != 0), b1 * 2);
      if (bool) {
        while (--b1 >= 0)
          append(paramObjectInputStream.readDouble(), paramObjectInputStream.readDouble()); 
      } else {
        while (--b1 >= 0)
          append(paramObjectInputStream.readFloat(), paramObjectInputStream.readFloat()); 
      } 
      this.pointTypes[this.numTypes++] = b2;
    } 
    if (i >= 0 && paramObjectInputStream.readByte() != 97)
      throw new StreamCorruptedException("missing PATH_END"); 
  }
}
