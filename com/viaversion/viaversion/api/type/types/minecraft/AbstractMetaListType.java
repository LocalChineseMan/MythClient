package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMetaListType extends MetaListTypeTemplate {
  public List<Metadata> read(ByteBuf buffer) throws Exception {
    Type<Metadata> type = getType();
    List<Metadata> list = new ArrayList<>();
    while (true) {
      Metadata meta = (Metadata)type.read(buffer);
      if (meta != null)
        list.add(meta); 
      if (meta == null)
        return list; 
    } 
  }
  
  public void write(ByteBuf buffer, List<Metadata> object) throws Exception {
    Type<Metadata> type = getType();
    for (Metadata metadata : object)
      type.write(buffer, metadata); 
    writeEnd(type, buffer);
  }
  
  protected abstract Type<Metadata> getType();
  
  protected abstract void writeEnd(Type<Metadata> paramType, ByteBuf paramByteBuf) throws Exception;
}
