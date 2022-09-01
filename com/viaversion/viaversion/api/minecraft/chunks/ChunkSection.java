package com.viaversion.viaversion.api.minecraft.chunks;

public interface ChunkSection {
  public static final int SIZE = 4096;
  
  static int index(int x, int y, int z) {
    return y << 8 | z << 4 | x;
  }
  
  int getFlatBlock(int paramInt);
  
  default int getFlatBlock(int x, int y, int z) {
    return getFlatBlock(index(x, y, z));
  }
  
  void setFlatBlock(int paramInt1, int paramInt2);
  
  default void setFlatBlock(int x, int y, int z, int id) {
    setFlatBlock(index(x, y, z), id);
  }
  
  default int getBlockWithoutData(int x, int y, int z) {
    return getFlatBlock(x, y, z) >> 4;
  }
  
  default int getBlockData(int x, int y, int z) {
    return getFlatBlock(x, y, z) & 0xF;
  }
  
  default void setBlockWithData(int x, int y, int z, int type, int data) {
    setFlatBlock(index(x, y, z), type << 4 | data & 0xF);
  }
  
  default void setBlockWithData(int idx, int type, int data) {
    setFlatBlock(idx, type << 4 | data & 0xF);
  }
  
  void setPaletteIndex(int paramInt1, int paramInt2);
  
  int getPaletteIndex(int paramInt);
  
  int getPaletteSize();
  
  int getPaletteEntry(int paramInt);
  
  void setPaletteEntry(int paramInt1, int paramInt2);
  
  void replacePaletteEntry(int paramInt1, int paramInt2);
  
  void addPaletteEntry(int paramInt);
  
  void clearPalette();
  
  int getNonAirBlocksCount();
  
  void setNonAirBlocksCount(int paramInt);
  
  default boolean hasLight() {
    return (getLight() != null);
  }
  
  ChunkSectionLight getLight();
  
  void setLight(ChunkSectionLight paramChunkSectionLight);
}
