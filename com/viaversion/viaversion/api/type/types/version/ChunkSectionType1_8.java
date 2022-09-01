package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;

public class ChunkSectionType1_8 extends Type<ChunkSection> {
  public ChunkSectionType1_8() {
    super("Chunk Section Type", ChunkSection.class);
  }
  
  public ChunkSection read(ByteBuf buffer) throws Exception {
    ChunkSectionImpl chunkSectionImpl = new ChunkSectionImpl(true);
    chunkSectionImpl.addPaletteEntry(0);
    ByteBuf littleEndianView = buffer.order(ByteOrder.LITTLE_ENDIAN);
    for (int i = 0; i < 4096; i++) {
      int mask = littleEndianView.readShort();
      int type = mask >> 4;
      int data = mask & 0xF;
      chunkSectionImpl.setBlockWithData(i, type, data);
    } 
    return (ChunkSection)chunkSectionImpl;
  }
  
  public void write(ByteBuf buffer, ChunkSection chunkSection) throws Exception {
    throw new UnsupportedOperationException();
  }
}
