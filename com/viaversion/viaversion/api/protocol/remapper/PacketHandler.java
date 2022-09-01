package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;

@FunctionalInterface
public interface PacketHandler {
  void handle(PacketWrapper paramPacketWrapper) throws Exception;
}
