package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_16;
import com.viaversion.viaversion.api.type.types.minecraft.ModernMetaType;

public class Metadata1_16Type extends ModernMetaType {
  protected MetaType getType(int index) {
    return (MetaType)MetaType1_16.byId(index);
  }
}
