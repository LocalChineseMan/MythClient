package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class VarIntType extends Type<Integer> implements TypeConverter<Integer> {
  public VarIntType() {
    super("VarInt", Integer.class);
  }
  
  public int readPrimitive(ByteBuf buffer) {
    int out = 0;
    int bytes = 0;
    while (true) {
      byte in = buffer.readByte();
      out |= (in & Byte.MAX_VALUE) << bytes++ * 7;
      if (bytes > 5)
        throw new RuntimeException("VarInt too big"); 
      if ((in & 0x80) != 128)
        return out; 
    } 
  }
  
  public void writePrimitive(ByteBuf buffer, int object) {
    do {
      int part = object & 0x7F;
      object >>>= 7;
      if (object != 0)
        part |= 0x80; 
      buffer.writeByte(part);
    } while (object != 0);
  }
  
  @Deprecated
  public Integer read(ByteBuf buffer) {
    return Integer.valueOf(readPrimitive(buffer));
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Integer object) {
    writePrimitive(buffer, object.intValue());
  }
  
  public Integer from(Object o) {
    if (o instanceof Number)
      return Integer.valueOf(((Number)o).intValue()); 
    if (o instanceof Boolean)
      return Integer.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
    return (Integer)o;
  }
}
