package com.viaversion.viaversion.api.rewriter;

import com.viaversion.viaversion.api.minecraft.item.Item;

public interface ItemRewriter<T extends com.viaversion.viaversion.api.protocol.Protocol> extends Rewriter<T> {
  Item handleItemToClient(Item paramItem);
  
  Item handleItemToServer(Item paramItem);
}
