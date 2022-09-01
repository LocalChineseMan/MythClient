package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.Item;
import io.netty.buffer.ByteBuf;

public class FlatVarIntItemArrayType extends BaseItemArrayType {
  public FlatVarIntItemArrayType() {
    super("Flat Item Array");
  }
  
  public Item[] read(ByteBuf buffer) throws Exception {
    int amount = SHORT.readPrimitive(buffer);
    Item[] array = new Item[amount];
    for (int i = 0; i < amount; i++)
      array[i] = (Item)FLAT_VAR_INT_ITEM.read(buffer); 
    return array;
  }
  
  public void write(ByteBuf buffer, Item[] object) throws Exception {
    SHORT.writePrimitive(buffer, (short)object.length);
    for (Item o : object)
      FLAT_VAR_INT_ITEM.write(buffer, o); 
  }
}
