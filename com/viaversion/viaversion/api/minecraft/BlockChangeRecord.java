package com.viaversion.viaversion.api.minecraft;

public interface BlockChangeRecord {
  byte getSectionX();
  
  byte getSectionY();
  
  byte getSectionZ();
  
  short getY(int paramInt);
  
  @Deprecated
  default short getY() {
    return getY(-1);
  }
  
  int getBlockId();
  
  void setBlockId(int paramInt);
}
