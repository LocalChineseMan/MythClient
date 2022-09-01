package com.viaversion.viaversion.connection;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManagerImpl implements ConnectionManager {
  protected final Map<UUID, UserConnection> clients = new ConcurrentHashMap<>();
  
  protected final Set<UserConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public void onLoginSuccess(UserConnection connection) {
    Objects.requireNonNull(connection, "connection is null!");
    this.connections.add(connection);
    if (isFrontEnd(connection)) {
      UUID id = connection.getProtocolInfo().getUuid();
      if (this.clients.put(id, connection) != null)
        Via.getPlatform().getLogger().warning("Duplicate UUID on frontend connection! (" + id + ")"); 
    } 
    if (connection.getChannel() != null)
      connection.getChannel().closeFuture().addListener((GenericFutureListener)(future -> onDisconnect(connection))); 
  }
  
  public void onDisconnect(UserConnection connection) {
    Objects.requireNonNull(connection, "connection is null!");
    this.connections.remove(connection);
    if (isFrontEnd(connection)) {
      UUID id = connection.getProtocolInfo().getUuid();
      this.clients.remove(id);
    } 
  }
  
  public Map<UUID, UserConnection> getConnectedClients() {
    return Collections.unmodifiableMap(this.clients);
  }
  
  public UserConnection getConnectedClient(UUID clientIdentifier) {
    return this.clients.get(clientIdentifier);
  }
  
  public UUID getConnectedClientId(UserConnection connection) {
    if (connection.getProtocolInfo() == null)
      return null; 
    UUID uuid = connection.getProtocolInfo().getUuid();
    UserConnection client = this.clients.get(uuid);
    if (connection.equals(client))
      return uuid; 
    return null;
  }
  
  public Set<UserConnection> getConnections() {
    return Collections.unmodifiableSet(this.connections);
  }
  
  public boolean isClientConnected(UUID playerId) {
    return this.clients.containsKey(playerId);
  }
}
