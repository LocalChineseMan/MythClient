package com.viaversion.viaversion.api.protocol;

import com.google.common.collect.Range;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.VersionedPacketTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;

public interface ProtocolManager {
  ServerProtocolVersion getServerProtocolVersion();
  
  <T extends Protocol> T getProtocol(Class<T> paramClass);
  
  default Protocol getProtocol(ProtocolVersion clientVersion, ProtocolVersion serverVersion) {
    return getProtocol(clientVersion.getVersion(), serverVersion.getVersion());
  }
  
  Protocol getProtocol(int paramInt1, int paramInt2);
  
  Protocol getBaseProtocol();
  
  Protocol getBaseProtocol(int paramInt);
  
  default boolean isBaseProtocol(Protocol protocol) {
    return protocol.isBaseProtocol();
  }
  
  void registerProtocol(Protocol paramProtocol, ProtocolVersion paramProtocolVersion1, ProtocolVersion paramProtocolVersion2);
  
  void registerProtocol(Protocol paramProtocol, List<Integer> paramList, int paramInt);
  
  void registerBaseProtocol(Protocol paramProtocol, Range<Integer> paramRange);
  
  List<ProtocolPathEntry> getProtocolPath(int paramInt1, int paramInt2);
  
  <C extends com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType, S extends com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType> VersionedPacketTransformer<C, S> createPacketTransformer(ProtocolVersion paramProtocolVersion, Class<C> paramClass, Class<S> paramClass1);
  
  boolean onlyCheckLoweringPathEntries();
  
  void setOnlyCheckLoweringPathEntries(boolean paramBoolean);
  
  int getMaxProtocolPathSize();
  
  void setMaxProtocolPathSize(int paramInt);
  
  SortedSet<Integer> getSupportedVersions();
  
  boolean isWorkingPipe();
  
  void completeMappingDataLoading(Class<? extends Protocol> paramClass) throws Exception;
  
  boolean checkForMappingCompletion();
  
  void addMappingLoaderFuture(Class<? extends Protocol> paramClass, Runnable paramRunnable);
  
  void addMappingLoaderFuture(Class<? extends Protocol> paramClass1, Class<? extends Protocol> paramClass2, Runnable paramRunnable);
  
  CompletableFuture<Void> getMappingLoaderFuture(Class<? extends Protocol> paramClass);
  
  PacketWrapper createPacketWrapper(PacketType paramPacketType, ByteBuf paramByteBuf, UserConnection paramUserConnection);
  
  @Deprecated
  PacketWrapper createPacketWrapper(int paramInt, ByteBuf paramByteBuf, UserConnection paramUserConnection);
}
