package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ClientboundStatusPackets implements ClientboundPacketType {
  STATUS_RESPONSE, PONG_RESPONSE;
  
  public final int getId() {
    return ordinal();
  }
  
  public final String getName() {
    return name();
  }
  
  public final State state() {
    return State.STATUS;
  }
}
