package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class DoubleType extends Type<Double> implements TypeConverter<Double> {
  public DoubleType() {
    super(Double.class);
  }
  
  @Deprecated
  public Double read(ByteBuf buffer) {
    return Double.valueOf(buffer.readDouble());
  }
  
  public double readPrimitive(ByteBuf buffer) {
    return buffer.readDouble();
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Double object) {
    buffer.writeDouble(object.doubleValue());
  }
  
  public void writePrimitive(ByteBuf buffer, double object) {
    buffer.writeDouble(object);
  }
  
  public Double from(Object o) {
    if (o instanceof Number)
      return Double.valueOf(((Number)o).doubleValue()); 
    if (o instanceof Boolean)
      return Double.valueOf(((Boolean)o).booleanValue() ? 1.0D : 0.0D); 
    return (Double)o;
  }
}
