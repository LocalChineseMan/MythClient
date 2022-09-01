package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class FlatItemArrayType extends BaseItemArrayType {
  public FlatItemArrayType() {
    super("Flat Item Array");
  }
  
  public Item[] read(ByteBuf buffer) throws Exception {
    int amount = Type.SHORT.readPrimitive(buffer);
    Item[] array = new Item[amount];
    for (int i = 0; i < amount; i++)
      array[i] = (Item)Type.FLAT_ITEM.read(buffer); 
    return array;
  }
  
  public void write(ByteBuf buffer, Item[] object) throws Exception {
    Type.SHORT.writePrimitive(buffer, (short)object.length);
    for (Item o : object)
      Type.FLAT_ITEM.write(buffer, o); 
  }
}
