package com.viaversion.viaversion.api.connection;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ConnectionManager {
  boolean isClientConnected(UUID paramUUID);
  
  default boolean isFrontEnd(UserConnection connection) {
    return !connection.isClientSide();
  }
  
  UserConnection getConnectedClient(UUID paramUUID);
  
  UUID getConnectedClientId(UserConnection paramUserConnection);
  
  Set<UserConnection> getConnections();
  
  Map<UUID, UserConnection> getConnectedClients();
  
  void onLoginSuccess(UserConnection paramUserConnection);
  
  void onDisconnect(UserConnection paramUserConnection);
}
