package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class PositionType extends Type<Position> {
  public PositionType() {
    super(Position.class);
  }
  
  public Position read(ByteBuf buffer) {
    long val = buffer.readLong();
    long x = val >> 38L;
    long y = val >> 26L & 0xFFFL;
    long z = val << 38L >> 38L;
    return new Position((int)x, (short)(int)y, (int)z);
  }
  
  public void write(ByteBuf buffer, Position object) {
    buffer.writeLong((object.getX() & 0x3FFFFFFL) << 38L | (object
        .getY() & 0xFFFL) << 26L | (object
        .getZ() & 0x3FFFFFF));
  }
}
