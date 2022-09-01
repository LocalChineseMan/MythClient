package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;

@FunctionalInterface
public interface ValueWriter<T> {
  void write(PacketWrapper paramPacketWrapper, T paramT) throws Exception;
}
