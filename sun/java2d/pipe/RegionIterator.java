package sun.java2d.pipe;

public class RegionIterator {
  Region region;
  
  int curIndex;
  
  int numXbands;
  
  RegionIterator(Region paramRegion) {
    this.region = paramRegion;
  }
  
  public RegionIterator createCopy() {
    RegionIterator regionIterator = new RegionIterator(this.region);
    regionIterator.curIndex = this.curIndex;
    regionIterator.numXbands = this.numXbands;
    return regionIterator;
  }
  
  public void copyStateFrom(RegionIterator paramRegionIterator) {
    if (this.region != paramRegionIterator.region)
      throw new InternalError("region mismatch"); 
    this.curIndex = paramRegionIterator.curIndex;
    this.numXbands = paramRegionIterator.numXbands;
  }
  
  public boolean nextYRange(int[] paramArrayOfint) {
    this.curIndex += this.numXbands * 2;
    this.numXbands = 0;
    if (this.curIndex >= this.region.endIndex)
      return false; 
    paramArrayOfint[1] = this.region.bands[this.curIndex++];
    paramArrayOfint[3] = this.region.bands[this.curIndex++];
    this.numXbands = this.region.bands[this.curIndex++];
    return true;
  }
  
  public boolean nextXBand(int[] paramArrayOfint) {
    if (this.numXbands <= 0)
      return false; 
    this.numXbands--;
    paramArrayOfint[0] = this.region.bands[this.curIndex++];
    paramArrayOfint[2] = this.region.bands[this.curIndex++];
    return true;
  }
}
