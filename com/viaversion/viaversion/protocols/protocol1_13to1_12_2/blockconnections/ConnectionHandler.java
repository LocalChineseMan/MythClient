package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;

public abstract class ConnectionHandler {
  public abstract int connect(UserConnection paramUserConnection, Position paramPosition, int paramInt);
  
  public int getBlockData(UserConnection user, Position position) {
    return ((BlockConnectionProvider)Via.getManager().getProviders().get(BlockConnectionProvider.class)).getBlockData(user, position.getX(), position.getY(), position.getZ());
  }
}
