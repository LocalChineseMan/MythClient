package com.viaversion.viaversion.api.protocol;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;

public interface Protocol<C1 extends com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType, C2 extends com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType, S1 extends com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType, S2 extends com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType> {
  default void registerServerbound(State state, int oldPacketID, int newPacketID) {
    registerServerbound(state, oldPacketID, newPacketID, (PacketRemapper)null);
  }
  
  default void registerServerbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper) {
    registerServerbound(state, oldPacketID, newPacketID, packetRemapper, false);
  }
  
  void registerServerbound(State paramState, int paramInt1, int paramInt2, PacketRemapper paramPacketRemapper, boolean paramBoolean);
  
  void cancelServerbound(State paramState, int paramInt1, int paramInt2);
  
  default void cancelServerbound(State state, int newPacketID) {
    cancelServerbound(state, -1, newPacketID);
  }
  
  default void registerClientbound(State state, int oldPacketID, int newPacketID) {
    registerClientbound(state, oldPacketID, newPacketID, (PacketRemapper)null);
  }
  
  default void registerClientbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper) {
    registerClientbound(state, oldPacketID, newPacketID, packetRemapper, false);
  }
  
  void cancelClientbound(State paramState, int paramInt1, int paramInt2);
  
  default void cancelClientbound(State state, int oldPacketID) {
    cancelClientbound(state, oldPacketID, -1);
  }
  
  void registerClientbound(State paramState, int paramInt1, int paramInt2, PacketRemapper paramPacketRemapper, boolean paramBoolean);
  
  void registerClientbound(C1 paramC1, PacketRemapper paramPacketRemapper);
  
  default void registerClientbound(C1 packetType, C2 mappedPacketType) {
    registerClientbound(packetType, mappedPacketType, (PacketRemapper)null);
  }
  
  default void registerClientbound(C1 packetType, C2 mappedPacketType, PacketRemapper packetRemapper) {
    registerClientbound(packetType, mappedPacketType, packetRemapper, false);
  }
  
  void registerClientbound(C1 paramC1, C2 paramC2, PacketRemapper paramPacketRemapper, boolean paramBoolean);
  
  void cancelClientbound(C1 paramC1);
  
  default void registerServerbound(S2 packetType, S1 mappedPacketType) {
    registerServerbound(packetType, mappedPacketType, (PacketRemapper)null);
  }
  
  void registerServerbound(S2 paramS2, PacketRemapper paramPacketRemapper);
  
  default void registerServerbound(S2 packetType, S1 mappedPacketType, PacketRemapper packetRemapper) {
    registerServerbound(packetType, mappedPacketType, packetRemapper, false);
  }
  
  void registerServerbound(S2 paramS2, S1 paramS1, PacketRemapper paramPacketRemapper, boolean paramBoolean);
  
  void cancelServerbound(S2 paramS2);
  
  boolean hasRegisteredClientbound(C1 paramC1);
  
  boolean hasRegisteredServerbound(S2 paramS2);
  
  boolean hasRegisteredClientbound(State paramState, int paramInt);
  
  boolean hasRegisteredServerbound(State paramState, int paramInt);
  
  void transform(Direction paramDirection, State paramState, PacketWrapper paramPacketWrapper) throws Exception;
  
  <T> T get(Class<T> paramClass);
  
  void put(Object paramObject);
  
  void initialize();
  
  boolean hasMappingDataToLoad();
  
  void loadMappingData();
  
  default void register(ViaProviders providers) {}
  
  default void init(UserConnection userConnection) {}
  
  default MappingData getMappingData() {
    return null;
  }
  
  default EntityRewriter getEntityRewriter() {
    return null;
  }
  
  default ItemRewriter getItemRewriter() {
    return null;
  }
  
  default boolean isBaseProtocol() {
    return false;
  }
}
