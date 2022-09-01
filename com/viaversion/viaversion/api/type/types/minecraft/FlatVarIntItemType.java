package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;

public class FlatVarIntItemType extends BaseItemType {
  public FlatVarIntItemType() {
    super("FlatVarIntItem");
  }
  
  public Item read(ByteBuf buffer) throws Exception {
    boolean present = buffer.readBoolean();
    if (!present)
      return null; 
    DataItem dataItem = new DataItem();
    dataItem.setIdentifier(VAR_INT.readPrimitive(buffer));
    dataItem.setAmount(buffer.readByte());
    dataItem.setTag((CompoundTag)NBT.read(buffer));
    return (Item)dataItem;
  }
  
  public void write(ByteBuf buffer, Item object) throws Exception {
    if (object == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      VAR_INT.writePrimitive(buffer, object.identifier());
      buffer.writeByte(object.amount());
      NBT.write(buffer, object.tag());
    } 
  }
}
