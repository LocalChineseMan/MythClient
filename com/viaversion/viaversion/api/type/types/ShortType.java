package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class ShortType extends Type<Short> implements TypeConverter<Short> {
  public ShortType() {
    super(Short.class);
  }
  
  public short readPrimitive(ByteBuf buffer) {
    return buffer.readShort();
  }
  
  public void writePrimitive(ByteBuf buffer, short object) {
    buffer.writeShort(object);
  }
  
  @Deprecated
  public Short read(ByteBuf buffer) {
    return Short.valueOf(buffer.readShort());
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Short object) {
    buffer.writeShort(object.shortValue());
  }
  
  public Short from(Object o) {
    if (o instanceof Number)
      return Short.valueOf(((Number)o).shortValue()); 
    if (o instanceof Boolean)
      return Short.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
    return (Short)o;
  }
}
