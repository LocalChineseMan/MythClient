package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumberTag {
  public static final int ID = 1;
  
  private byte value;
  
  public ByteTag() {
    this((byte)0);
  }
  
  public ByteTag(byte value) {
    this.value = value;
  }
  
  @Deprecated
  public Byte getValue() {
    return Byte.valueOf(this.value);
  }
  
  public void setValue(byte value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readByte();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeByte(this.value);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    ByteTag byteTag = (ByteTag)o;
    return (this.value == byteTag.value);
  }
  
  public int hashCode() {
    return this.value;
  }
  
  public final ByteTag clone() {
    return new ByteTag(this.value);
  }
  
  public byte asByte() {
    return this.value;
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
    return 1;
  }
}
