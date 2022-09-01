package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumberTag {
  public static final int ID = 3;
  
  private int value;
  
  public IntTag() {
    this(0);
  }
  
  public IntTag(int value) {
    this.value = value;
  }
  
  @Deprecated
  public Integer getValue() {
    return Integer.valueOf(this.value);
  }
  
  public void setValue(int value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readInt();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeInt(this.value);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    IntTag intTag = (IntTag)o;
    return (this.value == intTag.value);
  }
  
  public int hashCode() {
    return this.value;
  }
  
  public final IntTag clone() {
    return new IntTag(this.value);
  }
  
  public byte asByte() {
    return (byte)this.value;
  }
  
  public short asShort() {
    return (short)this.value;
  }
  
  public int asInt() {
    return this.value;
  }
  
  public long asLong() {
    return this.value;
  }
  
  public float asFloat() {
    return this.value;
  }
  
  public double asDouble() {
    return this.value;
  }
  
  public int getTagId() {
    return 3;
  }
}
