package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ServerboundLoginPackets implements ServerboundPacketType {
  HELLO, ENCRYPTION_KEY, CUSTOM_QUERY;
  
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
