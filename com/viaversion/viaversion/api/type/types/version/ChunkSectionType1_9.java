package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.CompactArrayUtil;
import io.netty.buffer.ByteBuf;
import java.util.Objects;

public class ChunkSectionType1_9 extends Type<ChunkSection> {
  private static final int GLOBAL_PALETTE = 13;
  
  public ChunkSectionType1_9() {
    super("Chunk Section Type", ChunkSection.class);
  }
  
  public ChunkSection read(ByteBuf buffer) throws Exception {
    int bitsPerBlock = buffer.readUnsignedByte();
    int originalBitsPerBlock = bitsPerBlock;
    if (bitsPerBlock == 0)
      bitsPerBlock = 13; 
    if (bitsPerBlock < 4)
      bitsPerBlock = 4; 
    if (bitsPerBlock > 8)
      bitsPerBlock = 13; 
    int paletteLength = Type.VAR_INT.readPrimitive(buffer);
    ChunkSectionImpl chunkSectionImpl = (bitsPerBlock != 13) ? new ChunkSectionImpl(true, paletteLength) : new ChunkSectionImpl(true);
    for (int i = 0; i < paletteLength; i++) {
      if (bitsPerBlock != 13) {
        chunkSectionImpl.addPaletteEntry(Type.VAR_INT.readPrimitive(buffer));
      } else {
        Type.VAR_INT.readPrimitive(buffer);
      } 
    } 
    long[] blockData = new long[Type.VAR_INT.readPrimitive(buffer)];
    if (blockData.length > 0) {
      int expectedLength = (int)Math.ceil((4096 * bitsPerBlock) / 64.0D);
      if (blockData.length != expectedLength)
        throw new IllegalStateException("Block data length (" + blockData.length + ") does not match expected length (" + expectedLength + ")! bitsPerBlock=" + bitsPerBlock + ", originalBitsPerBlock=" + originalBitsPerBlock); 
      for (int j = 0; j < blockData.length; j++)
        blockData[j] = buffer.readLong(); 
      Objects.requireNonNull(chunkSectionImpl);
      Objects.requireNonNull(chunkSectionImpl);
      CompactArrayUtil.iterateCompactArray(bitsPerBlock, 4096, blockData, (bitsPerBlock == 13) ? chunkSectionImpl::setFlatBlock : chunkSectionImpl::setPaletteIndex);
    } 
    return (ChunkSection)chunkSectionImpl;
  }
  
  public void write(ByteBuf buffer, ChunkSection chunkSection) throws Exception {
    int bitsPerBlock = 4;
    while (chunkSection.getPaletteSize() > 1 << bitsPerBlock)
      bitsPerBlock++; 
    if (bitsPerBlock > 8)
      bitsPerBlock = 13; 
    long maxEntryValue = (1L << bitsPerBlock) - 1L;
    buffer.writeByte(bitsPerBlock);
    if (bitsPerBlock != 13) {
      Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteSize());
      for (int i = 0; i < chunkSection.getPaletteSize(); i++)
        Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteEntry(i)); 
    } else {
      Type.VAR_INT.writePrimitive(buffer, 0);
    } 
    Objects.requireNonNull(chunkSection);
    Objects.requireNonNull(chunkSection);
    long[] data = CompactArrayUtil.createCompactArray(bitsPerBlock, 4096, (bitsPerBlock == 13) ? chunkSection::getFlatBlock : chunkSection::getPaletteIndex);
    Type.VAR_INT.writePrimitive(buffer, data.length);
    for (long l : data)
      buffer.writeLong(l); 
  }
}
