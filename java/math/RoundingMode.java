package java.math;

public enum RoundingMode {
  UP(0),
  DOWN(1),
  CEILING(2),
  FLOOR(3),
  HALF_UP(4),
  HALF_DOWN(5),
  HALF_EVEN(6),
  UNNECESSARY(7);
  
  final int oldMode;
  
  RoundingMode(int paramInt1) {
    this.oldMode = paramInt1;
  }
}
