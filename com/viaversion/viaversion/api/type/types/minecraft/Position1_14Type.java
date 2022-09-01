package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class Position1_14Type extends Type<Position> {
  public Position1_14Type() {
    super(Position.class);
  }
  
  public Position read(ByteBuf buffer) {
    long val = buffer.readLong();
    long x = val >> 38L;
    long y = val << 52L >> 52L;
    long z = val << 26L >> 38L;
    return new Position((int)x, (int)y, (int)z);
  }
  
  public void write(ByteBuf buffer, Position object) {
    buffer.writeLong((object.getX() & 0x3FFFFFFL) << 38L | (object
        .getY() & 0xFFF) | (object
        .getZ() & 0x3FFFFFFL) << 12L);
  }
}
