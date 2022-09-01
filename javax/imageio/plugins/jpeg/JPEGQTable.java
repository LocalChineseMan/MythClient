package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGQTable {
  private static final int[] k1 = new int[] { 
      16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 
      14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 
      40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 
      80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 
      24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 
      78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 
      112, 100, 103, 99 };
  
  private static final int[] k1div2 = new int[] { 
      8, 6, 5, 8, 12, 20, 26, 31, 6, 6, 
      7, 10, 13, 29, 30, 28, 7, 7, 8, 12, 
      20, 29, 35, 28, 7, 9, 11, 15, 26, 44, 
      40, 31, 9, 11, 19, 28, 34, 55, 52, 39, 
      12, 18, 28, 32, 41, 52, 57, 46, 25, 32, 
      39, 44, 52, 61, 60, 51, 36, 46, 48, 49, 
      56, 50, 52, 50 };
  
  private static final int[] k2 = new int[] { 
      17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 
      26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 
      99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 
      99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 
      99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 
      99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 
      99, 99, 99, 99 };
  
  private static final int[] k2div2 = new int[] { 
      9, 9, 12, 24, 50, 50, 50, 50, 9, 11, 
      13, 33, 50, 50, 50, 50, 12, 13, 28, 50, 
      50, 50, 50, 50, 24, 33, 50, 50, 50, 50, 
      50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 
      50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 
      50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 
      50, 50, 50, 50 };
  
  public static final JPEGQTable K1Luminance = new JPEGQTable(k1, false);
  
  public static final JPEGQTable K1Div2Luminance = new JPEGQTable(k1div2, false);
  
  public static final JPEGQTable K2Chrominance = new JPEGQTable(k2, false);
  
  public static final JPEGQTable K2Div2Chrominance = new JPEGQTable(k2div2, false);
  
  private int[] qTable;
  
  private JPEGQTable(int[] paramArrayOfint, boolean paramBoolean) {
    this.qTable = paramBoolean ? Arrays.copyOf(paramArrayOfint, paramArrayOfint.length) : paramArrayOfint;
  }
  
  public JPEGQTable(int[] paramArrayOfint) {
    if (paramArrayOfint == null)
      throw new IllegalArgumentException("table must not be null."); 
    if (paramArrayOfint.length != 64)
      throw new IllegalArgumentException("table.length != 64"); 
    this.qTable = Arrays.copyOf(paramArrayOfint, paramArrayOfint.length);
  }
  
  public int[] getTable() {
    return Arrays.copyOf(this.qTable, this.qTable.length);
  }
  
  public JPEGQTable getScaledInstance(float paramFloat, boolean paramBoolean) {
    char c = paramBoolean ? 'ÿ' : '翿';
    int[] arrayOfInt = new int[this.qTable.length];
    for (byte b = 0; b < this.qTable.length; b++) {
      int i = (int)(this.qTable[b] * paramFloat + 0.5F);
      if (i < 1)
        i = 1; 
      if (i > c)
        i = c; 
      arrayOfInt[b] = i;
    } 
    return new JPEGQTable(arrayOfInt);
  }
  
  public String toString() {
    String str = System.getProperty("line.separator", "\n");
    StringBuilder stringBuilder = new StringBuilder("JPEGQTable:" + str);
    for (byte b = 0; b < this.qTable.length; b++) {
      if (b % 8 == 0)
        stringBuilder.append('\t'); 
      stringBuilder.append(this.qTable[b]);
      stringBuilder.append((b % 8 == 7) ? str : Character.valueOf(' '));
    } 
    return stringBuilder.toString();
  }
}
