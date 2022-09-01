package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumberTag {
  public static final int ID = 2;
  
  private short value;
  
  public ShortTag() {
    this((short)0);
  }
  
  public ShortTag(short value) {
    this.value = value;
  }
  
  @Deprecated
  public Short getValue() {
    return Short.valueOf(this.value);
  }
  
  public void setValue(short value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readShort();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeShort(this.value);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    ShortTag shortTag = (ShortTag)o;
    return (this.value == shortTag.value);
  }
  
  public int hashCode() {
    return this.value;
  }
  
  public final ShortTag clone() {
    return new ShortTag(this.value);
  }
  
  public byte asByte() {
    return (byte)this.value;
  }
  
  public short asShort() {
    return this.value;
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
    return 2;
  }
}
