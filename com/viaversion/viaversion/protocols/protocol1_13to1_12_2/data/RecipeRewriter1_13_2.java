package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.RecipeRewriter;

public class RecipeRewriter1_13_2 extends RecipeRewriter {
  public RecipeRewriter1_13_2(Protocol protocol) {
    super(protocol);
    this.recipeHandlers.put("crafting_shapeless", this::handleCraftingShapeless);
    this.recipeHandlers.put("crafting_shaped", this::handleCraftingShaped);
    this.recipeHandlers.put("smelting", this::handleSmelting);
  }
  
  public void handleSmelting(PacketWrapper wrapper) throws Exception {
    wrapper.passthrough(Type.STRING);
    Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
    for (Item item : items)
      rewrite(item); 
    rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
    wrapper.passthrough((Type)Type.FLOAT);
    wrapper.passthrough((Type)Type.VAR_INT);
  }
  
  public void handleCraftingShaped(PacketWrapper wrapper) throws Exception {
    int ingredientsNo = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue() * ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
    wrapper.passthrough(Type.STRING);
    for (int j = 0; j < ingredientsNo; j++) {
      Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
      for (Item item : items)
        rewrite(item); 
    } 
    rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
  
  public void handleCraftingShapeless(PacketWrapper wrapper) throws Exception {
    wrapper.passthrough(Type.STRING);
    int ingredientsNo = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
    for (int j = 0; j < ingredientsNo; j++) {
      Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
      for (Item item : items)
        rewrite(item); 
    } 
    rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
}
