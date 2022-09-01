package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class LongType extends Type<Long> implements TypeConverter<Long> {
  public LongType() {
    super(Long.class);
  }
  
  @Deprecated
  public Long read(ByteBuf buffer) {
    return Long.valueOf(buffer.readLong());
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Long object) {
    buffer.writeLong(object.longValue());
  }
  
  public Long from(Object o) {
    if (o instanceof Number)
      return Long.valueOf(((Number)o).longValue()); 
    if (o instanceof Boolean)
      return Long.valueOf(((Boolean)o).booleanValue() ? 1L : 0L); 
    return (Long)o;
  }
  
  public long readPrimitive(ByteBuf buffer) {
    return buffer.readLong();
  }
  
  public void writePrimitive(ByteBuf buffer, long object) {
    buffer.writeLong(object);
  }
}
