package java.awt.geom;

import java.awt.Shape;

public final class GeneralPath extends Path2D.Float {
  private static final long serialVersionUID = -8327096662768731142L;
  
  public GeneralPath() {
    super(1, 20);
  }
  
  public GeneralPath(int paramInt) {
    super(paramInt, 20);
  }
  
  public GeneralPath(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
  }
  
  public GeneralPath(Shape paramShape) {
    super(paramShape, (AffineTransform)null);
  }
  
  GeneralPath(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, float[] paramArrayOffloat, int paramInt3) {
    this.windingRule = paramInt1;
    this.pointTypes = paramArrayOfbyte;
    this.numTypes = paramInt2;
    this.floatCoords = paramArrayOffloat;
    this.numCoords = paramInt3;
  }
}
