package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class BooleanType extends Type<Boolean> implements TypeConverter<Boolean> {
  public BooleanType() {
    super(Boolean.class);
  }
  
  public Boolean read(ByteBuf buffer) {
    return Boolean.valueOf(buffer.readBoolean());
  }
  
  public void write(ByteBuf buffer, Boolean object) {
    buffer.writeBoolean(object.booleanValue());
  }
  
  public Boolean from(Object o) {
    if (o instanceof Number)
      return Boolean.valueOf((((Number)o).intValue() == 1)); 
    return (Boolean)o;
  }
}
