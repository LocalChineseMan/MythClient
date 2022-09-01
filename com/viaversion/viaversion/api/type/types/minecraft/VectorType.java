package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VectorType extends Type<Vector> {
  public VectorType() {
    super(Vector.class);
  }
  
  public Vector read(ByteBuf buffer) throws Exception {
    int x = Type.INT.read(buffer).intValue();
    int y = Type.INT.read(buffer).intValue();
    int z = Type.INT.read(buffer).intValue();
    return new Vector(x, y, z);
  }
  
  public void write(ByteBuf buffer, Vector object) throws Exception {
    Type.INT.write(buffer, Integer.valueOf(object.getBlockX()));
    Type.INT.write(buffer, Integer.valueOf(object.getBlockY()));
    Type.INT.write(buffer, Integer.valueOf(object.getBlockZ()));
  }
}
