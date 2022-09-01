package com.viaversion.viaversion.api.protocol.packet;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import java.util.List;

public interface PacketWrapper {
  public static final int PASSTHROUGH_ID = 1000;
  
  static PacketWrapper create(PacketType packetType, UserConnection connection) {
    return create(packetType, (ByteBuf)null, connection);
  }
  
  static PacketWrapper create(PacketType packetType, ByteBuf inputBuffer, UserConnection connection) {
    return Via.getManager().getProtocolManager().createPacketWrapper(packetType, inputBuffer, connection);
  }
  
  @Deprecated
  static PacketWrapper create(int packetId, ByteBuf inputBuffer, UserConnection connection) {
    return Via.getManager().getProtocolManager().createPacketWrapper(packetId, inputBuffer, connection);
  }
  
  <T> T get(Type<T> paramType, int paramInt) throws Exception;
  
  boolean is(Type paramType, int paramInt);
  
  boolean isReadable(Type paramType, int paramInt);
  
  <T> void set(Type<T> paramType, int paramInt, T paramT) throws Exception;
  
  <T> T read(Type<T> paramType) throws Exception;
  
  <T> void write(Type<T> paramType, T paramT);
  
  <T> T passthrough(Type<T> paramType) throws Exception;
  
  void passthroughAll() throws Exception;
  
  void writeToBuffer(ByteBuf paramByteBuf) throws Exception;
  
  void clearInputBuffer();
  
  void clearPacket();
  
  default void send(Class<? extends Protocol> protocol) throws Exception {
    send(protocol, true);
  }
  
  void send(Class<? extends Protocol> paramClass, boolean paramBoolean) throws Exception;
  
  default void scheduleSend(Class<? extends Protocol> protocol) throws Exception {
    scheduleSend(protocol, true);
  }
  
  void scheduleSend(Class<? extends Protocol> paramClass, boolean paramBoolean) throws Exception;
  
  ChannelFuture sendFuture(Class<? extends Protocol> paramClass) throws Exception;
  
  @Deprecated
  default void send() throws Exception {
    sendRaw();
  }
  
  void sendRaw() throws Exception;
  
  void scheduleSendRaw() throws Exception;
  
  default PacketWrapper create(PacketType packetType) {
    return create(packetType.getId());
  }
  
  default PacketWrapper create(PacketType packetType, PacketHandler handler) throws Exception {
    return create(packetType.getId(), handler);
  }
  
  PacketWrapper create(int paramInt);
  
  PacketWrapper create(int paramInt, PacketHandler paramPacketHandler) throws Exception;
  
  PacketWrapper apply(Direction paramDirection, State paramState, int paramInt, List<Protocol> paramList, boolean paramBoolean) throws Exception;
  
  PacketWrapper apply(Direction paramDirection, State paramState, int paramInt, List<Protocol> paramList) throws Exception;
  
  void cancel();
  
  boolean isCancelled();
  
  UserConnection user();
  
  void resetReader();
  
  @Deprecated
  default void sendToServer() throws Exception {
    sendToServerRaw();
  }
  
  void sendToServerRaw() throws Exception;
  
  void scheduleSendToServerRaw() throws Exception;
  
  default void sendToServer(Class<? extends Protocol> protocol) throws Exception {
    sendToServer(protocol, true);
  }
  
  void sendToServer(Class<? extends Protocol> paramClass, boolean paramBoolean) throws Exception;
  
  default void scheduleSendToServer(Class<? extends Protocol> protocol) throws Exception {
    scheduleSendToServer(protocol, true);
  }
  
  void scheduleSendToServer(Class<? extends Protocol> paramClass, boolean paramBoolean) throws Exception;
  
  PacketType getPacketType();
  
  void setPacketType(PacketType paramPacketType);
  
  int getId();
  
  @Deprecated
  default void setId(PacketType packetType) {
    setPacketType(packetType);
  }
  
  @Deprecated
  void setId(int paramInt);
}
