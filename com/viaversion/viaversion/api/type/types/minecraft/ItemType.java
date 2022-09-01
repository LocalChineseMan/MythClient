package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;

public class ItemType extends BaseItemType {
  public ItemType() {
    super("Item");
  }
  
  public Item read(ByteBuf buffer) throws Exception {
    short id = buffer.readShort();
    if (id < 0)
      return null; 
    DataItem dataItem = new DataItem();
    dataItem.setIdentifier(id);
    dataItem.setAmount(buffer.readByte());
    dataItem.setData(buffer.readShort());
    dataItem.setTag((CompoundTag)NBT.read(buffer));
    return (Item)dataItem;
  }
  
  public void write(ByteBuf buffer, Item object) throws Exception {
    if (object == null) {
      buffer.writeShort(-1);
    } else {
      buffer.writeShort(object.identifier());
      buffer.writeByte(object.amount());
      buffer.writeShort(object.data());
      NBT.write(buffer, object.tag());
    } 
  }
}
