package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferByte extends DataBuffer {
  byte[] data;
  
  byte[][] bankdata;
  
  public DataBufferByte(int paramInt) {
    super(StateTrackable.State.STABLE, 0, paramInt);
    this.data = new byte[paramInt];
    this.bankdata = new byte[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferByte(int paramInt1, int paramInt2) {
    super(StateTrackable.State.STABLE, 0, paramInt1, paramInt2);
    this.bankdata = new byte[paramInt2][];
    for (byte b = 0; b < paramInt2; b++)
      this.bankdata[b] = new byte[paramInt1]; 
    this.data = this.bankdata[0];
  }
  
  public DataBufferByte(byte[] paramArrayOfbyte, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt);
    this.data = paramArrayOfbyte;
    this.bankdata = new byte[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferByte(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt1, 1, paramInt2);
    this.data = paramArrayOfbyte;
    this.bankdata = new byte[1][];
    this.bankdata[0] = this.data;
  }
  
  public DataBufferByte(byte[][] paramArrayOfbyte, int paramInt) {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt, paramArrayOfbyte.length);
    this.bankdata = (byte[][])paramArrayOfbyte.clone();
    this.data = this.bankdata[0];
  }
  
  public DataBufferByte(byte[][] paramArrayOfbyte, int paramInt, int[] paramArrayOfint) {
    super(StateTrackable.State.UNTRACKABLE, 0, paramInt, paramArrayOfbyte.length, paramArrayOfint);
    this.bankdata = (byte[][])paramArrayOfbyte.clone();
    this.data = this.bankdata[0];
  }
  
  public byte[] getData() {
    this.theTrackable.setUntrackable();
    return this.data;
  }
  
  public byte[] getData(int paramInt) {
    this.theTrackable.setUntrackable();
    return this.bankdata[paramInt];
  }
  
  public byte[][] getBankData() {
    this.theTrackable.setUntrackable();
    return (byte[][])this.bankdata.clone();
  }
  
  public int getElem(int paramInt) {
    return this.data[paramInt + this.offset] & 0xFF;
  }
  
  public int getElem(int paramInt1, int paramInt2) {
    return this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] & 0xFF;
  }
  
  public void setElem(int paramInt1, int paramInt2) {
    this.data[paramInt1 + this.offset] = (byte)paramInt2;
    this.theTrackable.markDirty();
  }
  
  public void setElem(int paramInt1, int paramInt2, int paramInt3) {
    this.bankdata[paramInt1][paramInt2 + this.offsets[paramInt1]] = (byte)paramInt3;
    this.theTrackable.markDirty();
  }
}
