package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.EulerAngle;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class EulerAngleType extends Type<EulerAngle> {
  public EulerAngleType() {
    super(EulerAngle.class);
  }
  
  public EulerAngle read(ByteBuf buffer) throws Exception {
    float x = Type.FLOAT.readPrimitive(buffer);
    float y = Type.FLOAT.readPrimitive(buffer);
    float z = Type.FLOAT.readPrimitive(buffer);
    return new EulerAngle(x, y, z);
  }
  
  public void write(ByteBuf buffer, EulerAngle object) throws Exception {
    Type.FLOAT.writePrimitive(buffer, object.getX());
    Type.FLOAT.writePrimitive(buffer, object.getY());
    Type.FLOAT.writePrimitive(buffer, object.getZ());
  }
}
