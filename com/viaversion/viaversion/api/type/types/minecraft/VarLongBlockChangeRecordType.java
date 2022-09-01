package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_16_2;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VarLongBlockChangeRecordType extends Type<BlockChangeRecord> {
  public VarLongBlockChangeRecordType() {
    super(BlockChangeRecord.class);
  }
  
  public BlockChangeRecord read(ByteBuf buffer) throws Exception {
    long data = Type.VAR_LONG.readPrimitive(buffer);
    short position = (short)(int)(data & 0xFFFL);
    return (BlockChangeRecord)new BlockChangeRecord1_16_2(position >>> 8 & 0xF, position & 0xF, position >>> 4 & 0xF, (int)(data >>> 12L));
  }
  
  public void write(ByteBuf buffer, BlockChangeRecord object) throws Exception {
    short position = (short)(object.getSectionX() << 8 | object.getSectionZ() << 4 | object.getSectionY());
    Type.VAR_LONG.writePrimitive(buffer, object.getBlockId() << 12L | position);
  }
}
