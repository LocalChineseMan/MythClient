package com.viaversion.viaversion.api.minecraft.item;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public interface Item {
  int identifier();
  
  void setIdentifier(int paramInt);
  
  int amount();
  
  void setAmount(int paramInt);
  
  default short data() {
    return 0;
  }
  
  default void setData(short data) {
    throw new UnsupportedOperationException();
  }
  
  CompoundTag tag();
  
  void setTag(CompoundTag paramCompoundTag);
}
