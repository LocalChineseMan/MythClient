package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class UnsignedByteType extends Type<Short> implements TypeConverter<Short> {
  public UnsignedByteType() {
    super("Unsigned Byte", Short.class);
  }
  
  public Short read(ByteBuf buffer) {
    return Short.valueOf(buffer.readUnsignedByte());
  }
  
  public void write(ByteBuf buffer, Short object) {
    buffer.writeByte(object.shortValue());
  }
  
  public Short from(Object o) {
    if (o instanceof Number)
      return Short.valueOf(((Number)o).shortValue()); 
    if (o instanceof Boolean)
      return Short.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
    return (Short)o;
  }
}
