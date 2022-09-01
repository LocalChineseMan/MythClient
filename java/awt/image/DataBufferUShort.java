package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferUShort extends DataBuffer {
  short[] data;
  
  short[][] bankdata;
  
  public DataBufferUShort(int paramInt) {
    super(StateTrackable.State.STABLE, 1, paramInt);
    this.data = new short[paramInt];
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferUShort(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 1, paramInt1, paramInt2);
    this.bankdata = new short[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new short[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferUShort(short[] paramArrayOfshort, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt);
    if (paramArrayOfshort == null)
      throw new NullPointerException("dataArray is null"); 
    this.data = paramArrayOfshort;
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferUShort(short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt1, 1, paramInt2);
    if (paramArrayOfshort == null)
      throw new NullPointerException("dataArray is null"); 
    if (paramInt1 + paramInt2 > paramArrayOfshort.length)
      throw new IllegalArgumentException("Length of dataArray is less  than size+offset."); 
    this.data = paramArrayOfshort;
    this.bankdata = new short[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferUShort(short[][] paramArrayOfshort, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt, paramArrayOfshort.length);
    if (paramArrayOfshort == null)
      throw new NullPointerException("dataArray is null"); 
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      if (paramArrayOfshort[b] == null)
        throw new NullPointerException("dataArray[" + b + "] is null"); 
    } 
    this.bankdata = (short[][])paramArrayOfshort.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferUShort(short[][] paramArrayOfshort, int paramInt, int[] paramArrayOfint) {
    super(StateTrackable.State.UNTRACKABLE, 1, paramInt, paramArrayOfshort.length, paramArrayOfint);
    if (paramArrayOfshort == null)
      throw new NullPointerException("dataArray is null"); 
    for (byte b = 0; b < paramArrayOfshort.length; b++) {
      if (paramArrayOfshort[b] == null)
        throw new NullPointerException("dataArray[" + b + "] is null"); 
      if (paramInt + paramArrayOfint[b] > (paramArrayOfshort[b]).length)
        throw new IllegalArgumentException("Length of dataArray[" + b + "] is less than size+" + "offsets[" + b + "]."); 
    } 
    this.bankdata = (short[][])paramArrayOfshort.clone();
    this.data = this.bankdata[0];
  }
  
  public short[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public short[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public short[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (short[][])this.bankdata.clone();
  }
  
  public int getElem(int paramInt) {
    return this.data[paramInt + this.offset] & 0xFFFF;
  }
  
  public int getElem(int paramInt1, int paramInt2) {
    return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] & 0xFFFF;
  }
  
  public void setElem(int paramInt1, int paramInt2) {
    this.data[paramInt1 + this.offset] = (short)(paramInt2 & 0xFFFF);
    this.theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = (short)(paramInt3 & 0xFFFF);
    this.theTrackable.markDirty();
  }
}
