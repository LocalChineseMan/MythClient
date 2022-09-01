package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ClientboundLoginPackets implements ClientboundPacketType {
  LOGIN_DISCONNECT, HELLO, GAME_PROFILE, LOGIN_COMPRESSION, CUSTOM_QUERY;
  
  public final int getId() {
    return ordinal();
  }
  
  public final String getName() {
    return name();
  }
  
  public final State state() {
    return State.LOGIN;
  }
}
