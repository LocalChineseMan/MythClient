package com.viaversion.viaversion.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.LegacyViaAPI;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.UUID;

public interface ViaAPI<T> {
  default int apiVersion() {
    return 4;
  }
  
  ServerProtocolVersion getServerVersion();
  
  int getPlayerVersion(T paramT);
  
  int getPlayerVersion(UUID paramUUID);
  
  boolean isInjected(UUID paramUUID);
  
  UserConnection getConnection(UUID paramUUID);
  
  String getVersion();
  
  void sendRawPacket(T paramT, ByteBuf paramByteBuf);
  
  void sendRawPacket(UUID paramUUID, ByteBuf paramByteBuf);
  
  SortedSet<Integer> getSupportedVersions();
  
  SortedSet<Integer> getFullSupportedVersions();
  
  LegacyViaAPI<T> legacyAPI();
}
