package com.viaversion.viaversion.protocols.protocol1_9to1_8.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.Provider;

public class HandItemProvider implements Provider {
  public Item getHandItem(UserConnection info) {
    return (Item)new DataItem(0, (byte)0, (short)0, null);
  }
}
