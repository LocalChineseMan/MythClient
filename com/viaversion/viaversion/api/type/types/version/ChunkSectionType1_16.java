package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.CompactArrayUtil;
import io.netty.buffer.ByteBuf;
import java.util.Objects;

public class ChunkSectionType1_16 extends Type<ChunkSection> {
  private static final int GLOBAL_PALETTE = 15;
  
  public ChunkSectionType1_16() {
    super("Chunk Section Type", ChunkSection.class);
  }
  
  public ChunkSection read(ByteBuf buffer) throws Exception {
    ChunkSectionImpl chunkSectionImpl;
    int bitsPerBlock = buffer.readUnsignedByte();
    int originalBitsPerBlock = bitsPerBlock;
    if (bitsPerBlock == 0 || bitsPerBlock > 8)
      bitsPerBlock = 15; 
    if (bitsPerBlock != 15) {
      int paletteLength = Type.VAR_INT.readPrimitive(buffer);
      chunkSectionImpl = new ChunkSectionImpl(false, paletteLength);
      for (int i = 0; i < paletteLength; i++)
        chunkSectionImpl.addPaletteEntry(Type.VAR_INT.readPrimitive(buffer)); 
    } else {
      chunkSectionImpl = new ChunkSectionImpl(false);
    } 
    long[] blockData = new long[Type.VAR_INT.readPrimitive(buffer)];
    if (blockData.length > 0) {
      char valuesPerLong = (char)(64 / bitsPerBlock);
      int expectedLength = (4096 + valuesPerLong - 1) / valuesPerLong;
      if (blockData.length != expectedLength)
        throw new IllegalStateException("Block data length (" + blockData.length + ") does not match expected length (" + expectedLength + ")! bitsPerBlock=" + bitsPerBlock + ", originalBitsPerBlock=" + originalBitsPerBlock); 
      for (int i = 0; i < blockData.length; i++)
        blockData[i] = buffer.readLong(); 
      Objects.requireNonNull(chunkSectionImpl);
      Objects.requireNonNull(chunkSectionImpl);
      CompactArrayUtil.iterateCompactArrayWithPadding(bitsPerBlock, 4096, blockData, (bitsPerBlock == 15) ? chunkSectionImpl::setFlatBlock : chunkSectionImpl::setPaletteIndex);
    } 
    return (ChunkSection)chunkSectionImpl;
  }
  
  public void write(ByteBuf buffer, ChunkSection chunkSection) throws Exception {
    int bitsPerBlock = 4;
    while (chunkSection.getPaletteSize() > 1 << bitsPerBlock)
      bitsPerBlock++; 
    if (bitsPerBlock > 8)
      bitsPerBlock = 15; 
    buffer.writeByte(bitsPerBlock);
    if (bitsPerBlock != 15) {
      Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteSize());
      for (int i = 0; i < chunkSection.getPaletteSize(); i++)
        Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteEntry(i)); 
    } 
    Objects.requireNonNull(chunkSection);
    Objects.requireNonNull(chunkSection);
    long[] data = CompactArrayUtil.createCompactArrayWithPadding(bitsPerBlock, 4096, (bitsPerBlock == 15) ? chunkSection::getFlatBlock : chunkSection::getPaletteIndex);
    Type.VAR_INT.writePrimitive(buffer, data.length);
    for (long l : data)
      buffer.writeLong(l); 
  }
}
