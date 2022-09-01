package sun.font;

public class CompositeFontDescriptor {
  private String faceName;
  
  private int coreComponentCount;
  
  private String[] componentFaceNames;
  
  private String[] componentFileNames;
  
  private int[] exclusionRanges;
  
  private int[] exclusionRangeLimits;
  
  public CompositeFontDescriptor(String paramString, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    this.faceName = paramString;
    this.coreComponentCount = paramInt;
    this.componentFaceNames = paramArrayOfString1;
    this.componentFileNames = paramArrayOfString2;
    this.exclusionRanges = paramArrayOfint1;
    this.exclusionRangeLimits = paramArrayOfint2;
  }
  
  public String getFaceName() {
    return this.faceName;
  }
  
  public int getCoreComponentCount() {
    return this.coreComponentCount;
  }
  
  public String[] getComponentFaceNames() {
    return this.componentFaceNames;
  }
  
  public String[] getComponentFileNames() {
    return this.componentFileNames;
  }
  
  public int[] getExclusionRanges() {
    return this.exclusionRanges;
  }
  
  public int[] getExclusionRangeLimits() {
    return this.exclusionRangeLimits;
  }
}
