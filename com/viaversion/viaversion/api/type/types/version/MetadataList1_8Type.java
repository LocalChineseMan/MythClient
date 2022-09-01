package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.AbstractMetaListType;
import io.netty.buffer.ByteBuf;

public class MetadataList1_8Type extends AbstractMetaListType {
  protected Type<Metadata> getType() {
    return Types1_8.METADATA;
  }
  
  protected void writeEnd(Type<Metadata> type, ByteBuf buffer) throws Exception {
    buffer.writeByte(127);
  }
}
