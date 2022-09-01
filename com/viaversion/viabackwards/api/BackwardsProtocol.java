package com.viaversion.viabackwards.api;

import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public abstract class BackwardsProtocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType> extends AbstractProtocol<C1, C2, S1, S2> {
  protected BackwardsProtocol() {}
  
  protected BackwardsProtocol(Class<C1> oldClientboundPacketEnum, Class<C2> clientboundPacketEnum, Class<S1> oldServerboundPacketEnum, Class<S2> serverboundPacketEnum) {
    super(oldClientboundPacketEnum, clientboundPacketEnum, oldServerboundPacketEnum, serverboundPacketEnum);
  }
  
  protected void executeAsyncAfterLoaded(Class<? extends Protocol> protocolClass, Runnable runnable) {
    Via.getManager().getProtocolManager().addMappingLoaderFuture(getClass(), protocolClass, runnable);
  }
  
  public boolean hasMappingDataToLoad() {
    return false;
  }
  
  public BackwardsMappings getMappingData() {
    return null;
  }
  
  public TranslatableRewriter getTranslatableRewriter() {
    return null;
  }
}
