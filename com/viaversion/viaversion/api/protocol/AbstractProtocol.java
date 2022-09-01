package com.viaversion.viaversion.api.protocol;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractProtocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType> implements Protocol<C1, C2, S1, S2> {
  private final Map<Packet, ProtocolPacket> serverbound = new HashMap<>();
  
  private final Map<Packet, ProtocolPacket> clientbound = new HashMap<>();
  
  private final Map<Class<?>, Object> storedObjects = new HashMap<>();
  
  protected final Class<C1> oldClientboundPacketEnum;
  
  protected final Class<C2> newClientboundPacketEnum;
  
  protected final Class<S1> oldServerboundPacketEnum;
  
  protected final Class<S2> newServerboundPacketEnum;
  
  private boolean initialized;
  
  protected AbstractProtocol() {
    this((Class<C1>)null, (Class<C2>)null, (Class<S1>)null, (Class<S2>)null);
  }
  
  protected AbstractProtocol(Class<C1> oldClientboundPacketEnum, Class<C2> clientboundPacketEnum, Class<S1> oldServerboundPacketEnum, Class<S2> serverboundPacketEnum) {
    this.oldClientboundPacketEnum = oldClientboundPacketEnum;
    this.newClientboundPacketEnum = clientboundPacketEnum;
    this.oldServerboundPacketEnum = oldServerboundPacketEnum;
    this.newServerboundPacketEnum = serverboundPacketEnum;
  }
  
  public final void initialize() {
    Preconditions.checkArgument(!this.initialized);
    this.initialized = true;
    registerPackets();
    if (this.oldClientboundPacketEnum != null && this.newClientboundPacketEnum != null && this.oldClientboundPacketEnum != this.newClientboundPacketEnum)
      registerClientboundChannelIdChanges(); 
    if (this.oldServerboundPacketEnum != null && this.newServerboundPacketEnum != null && this.oldServerboundPacketEnum != this.newServerboundPacketEnum)
      registerServerboundChannelIdChanges(); 
  }
  
  protected void registerClientboundChannelIdChanges() {
    ClientboundPacketType[] arrayOfClientboundPacketType = (ClientboundPacketType[])this.newClientboundPacketEnum.getEnumConstants();
    Map<String, C2> newClientboundPackets = new HashMap<>(arrayOfClientboundPacketType.length);
    for (ClientboundPacketType clientboundPacketType : arrayOfClientboundPacketType)
      newClientboundPackets.put(clientboundPacketType.getName(), (C2)clientboundPacketType); 
    for (ClientboundPacketType clientboundPacketType1 : (ClientboundPacketType[])this.oldClientboundPacketEnum.getEnumConstants()) {
      ClientboundPacketType clientboundPacketType2 = (ClientboundPacketType)newClientboundPackets.get(clientboundPacketType1.getName());
      if (clientboundPacketType2 == null) {
        Preconditions.checkArgument(hasRegisteredClientbound((C1)clientboundPacketType1), "Packet " + clientboundPacketType1 + " in " + 
            getClass().getSimpleName() + " has no mapping - it needs to be manually cancelled or remapped!");
      } else if (!hasRegisteredClientbound((C1)clientboundPacketType1)) {
        registerClientbound(clientboundPacketType1, clientboundPacketType2);
      } 
    } 
  }
  
  protected void registerServerboundChannelIdChanges() {
    ServerboundPacketType[] arrayOfServerboundPacketType = (ServerboundPacketType[])this.oldServerboundPacketEnum.getEnumConstants();
    Map<String, S1> oldServerboundConstants = new HashMap<>(arrayOfServerboundPacketType.length);
    for (ServerboundPacketType serverboundPacketType : arrayOfServerboundPacketType)
      oldServerboundConstants.put(serverboundPacketType.getName(), (S1)serverboundPacketType); 
    for (ServerboundPacketType serverboundPacketType1 : (ServerboundPacketType[])this.newServerboundPacketEnum.getEnumConstants()) {
      ServerboundPacketType serverboundPacketType2 = (ServerboundPacketType)oldServerboundConstants.get(serverboundPacketType1.getName());
      if (serverboundPacketType2 == null) {
        Preconditions.checkArgument(hasRegisteredServerbound((S2)serverboundPacketType1), "Packet " + serverboundPacketType1 + " in " + 
            getClass().getSimpleName() + " has no mapping - it needs to be manually cancelled or remapped!");
      } else if (!hasRegisteredServerbound((S2)serverboundPacketType1)) {
        registerServerbound(serverboundPacketType1, serverboundPacketType2);
      } 
    } 
  }
  
  protected void registerPackets() {}
  
  public final void loadMappingData() {
    getMappingData().load();
    onMappingDataLoaded();
  }
  
  protected void onMappingDataLoaded() {}
  
  protected void addEntityTracker(UserConnection connection, EntityTracker tracker) {
    connection.addEntityTracker(getClass(), tracker);
  }
  
  public void registerServerbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper, boolean override) {
    ProtocolPacket protocolPacket = new ProtocolPacket(state, oldPacketID, newPacketID, packetRemapper);
    Packet packet = new Packet(state, newPacketID);
    if (!override && this.serverbound.containsKey(packet))
      Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If this override is intentional, set override to true. Stacktrace: ", new Exception()); 
    this.serverbound.put(packet, protocolPacket);
  }
  
  public void cancelServerbound(State state, int oldPacketID, int newPacketID) {
    registerServerbound(state, oldPacketID, newPacketID, new PacketRemapper() {
          public void registerMap() {
            handler(PacketWrapper::cancel);
          }
        });
  }
  
  public void cancelClientbound(State state, int oldPacketID, int newPacketID) {
    registerClientbound(state, oldPacketID, newPacketID, (PacketRemapper)new Object(this));
  }
  
  public void registerClientbound(State state, int oldPacketID, int newPacketID, PacketRemapper packetRemapper, boolean override) {
    ProtocolPacket protocolPacket = new ProtocolPacket(state, oldPacketID, newPacketID, packetRemapper);
    Packet packet = new Packet(state, oldPacketID);
    if (!override && this.clientbound.containsKey(packet))
      Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If override is intentional, set override to true. Stacktrace: ", new Exception()); 
    this.clientbound.put(packet, protocolPacket);
  }
  
  public void registerClientbound(C1 packetType, PacketRemapper packetRemapper) {
    checkPacketType((PacketType)packetType, (this.oldClientboundPacketEnum == null || packetType.getClass() == this.oldClientboundPacketEnum));
    C2 mappedPacket = (this.oldClientboundPacketEnum == this.newClientboundPacketEnum) ? (C2)packetType : (C2)Arrays.<ClientboundPacketType>stream((ClientboundPacketType[])this.newClientboundPacketEnum.getEnumConstants()).filter(en -> en.getName().equals(packetType.getName())).findAny().orElse(null);
    Preconditions.checkNotNull(mappedPacket, "Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " could not be automatically mapped!");
    registerClientbound((ClientboundPacketType)packetType, (ClientboundPacketType)mappedPacket, packetRemapper);
  }
  
  public void registerClientbound(C1 packetType, C2 mappedPacketType, PacketRemapper packetRemapper, boolean override) {
    register(this.clientbound, (PacketType)packetType, (PacketType)mappedPacketType, (Class)this.oldClientboundPacketEnum, (Class)this.newClientboundPacketEnum, packetRemapper, override);
  }
  
  public void cancelClientbound(C1 packetType) {
    registerClientbound((ClientboundPacketType)packetType, null, new PacketRemapper() {
          public void registerMap() {
            handler(PacketWrapper::cancel);
          }
        });
  }
  
  public void registerServerbound(S2 packetType, PacketRemapper packetRemapper) {
    checkPacketType((PacketType)packetType, (this.newServerboundPacketEnum == null || packetType.getClass() == this.newServerboundPacketEnum));
    S1 mappedPacket = (this.oldServerboundPacketEnum == this.newServerboundPacketEnum) ? (S1)packetType : (S1)Arrays.<ServerboundPacketType>stream((ServerboundPacketType[])this.oldServerboundPacketEnum.getEnumConstants()).filter(en -> en.getName().equals(packetType.getName())).findAny().orElse(null);
    Preconditions.checkNotNull(mappedPacket, "Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " could not be automatically mapped!");
    registerServerbound((ServerboundPacketType)packetType, (ServerboundPacketType)mappedPacket, packetRemapper);
  }
  
  public void registerServerbound(S2 packetType, S1 mappedPacketType, PacketRemapper packetRemapper, boolean override) {
    register(this.serverbound, (PacketType)packetType, (PacketType)mappedPacketType, (Class)this.newServerboundPacketEnum, (Class)this.oldServerboundPacketEnum, packetRemapper, override);
  }
  
  public void cancelServerbound(S2 packetType) {
    registerServerbound((ServerboundPacketType)packetType, null, new PacketRemapper() {
          public void registerMap() {
            handler(PacketWrapper::cancel);
          }
        });
  }
  
  private void register(Map<Packet, ProtocolPacket> packetMap, PacketType packetType, PacketType mappedPacketType, Class<? extends PacketType> unmappedPacketEnum, Class<? extends PacketType> mappedPacketEnum, PacketRemapper remapper, boolean override) {
    checkPacketType(packetType, (unmappedPacketEnum == null || packetType.getClass() == unmappedPacketEnum));
    checkPacketType(mappedPacketType, (mappedPacketType == null || mappedPacketEnum == null || mappedPacketType.getClass() == mappedPacketEnum));
    Preconditions.checkArgument((mappedPacketType == null || packetType.state() == mappedPacketType.state()), "Packet type state does not match mapped packet type state");
    ProtocolPacket protocolPacket = new ProtocolPacket(packetType.state(), packetType, mappedPacketType, remapper);
    Packet packet = new Packet(packetType.state(), packetType.getId());
    if (!override && packetMap.containsKey(packet))
      Via.getPlatform().getLogger().log(Level.WARNING, packet + " already registered! If override is intentional, set override to true. Stacktrace: ", new Exception()); 
    packetMap.put(packet, protocolPacket);
  }
  
  public boolean hasRegisteredClientbound(C1 packetType) {
    return hasRegisteredClientbound(packetType.state(), packetType.getId());
  }
  
  public boolean hasRegisteredServerbound(S2 packetType) {
    return hasRegisteredServerbound(packetType.state(), packetType.getId());
  }
  
  public boolean hasRegisteredClientbound(State state, int unmappedPacketId) {
    Packet packet = new Packet(state, unmappedPacketId);
    return this.clientbound.containsKey(packet);
  }
  
  public boolean hasRegisteredServerbound(State state, int unmappedPacketId) {
    Packet packet = new Packet(state, unmappedPacketId);
    return this.serverbound.containsKey(packet);
  }
  
  public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
    Packet statePacket = new Packet(state, packetWrapper.getId());
    Map<Packet, ProtocolPacket> packetMap = (direction == Direction.CLIENTBOUND) ? this.clientbound : this.serverbound;
    ProtocolPacket protocolPacket = packetMap.get(statePacket);
    if (protocolPacket == null)
      return; 
    int unmappedId = packetWrapper.getId();
    if (protocolPacket.isMappedOverTypes()) {
      packetWrapper.setPacketType(protocolPacket.getMappedPacketType());
    } else {
      int mappedId = (direction == Direction.CLIENTBOUND) ? protocolPacket.getNewId() : protocolPacket.getOldId();
      if (unmappedId != mappedId)
        packetWrapper.setId(mappedId); 
    } 
    PacketRemapper remapper = protocolPacket.getRemapper();
    if (remapper != null) {
      try {
        remapper.remap(packetWrapper);
      } catch (InformativeException e) {
        throwRemapError(direction, state, unmappedId, packetWrapper.getId(), e);
        return;
      } 
      if (packetWrapper.isCancelled())
        throw CancelException.generate(); 
    } 
  }
  
  private void throwRemapError(Direction direction, State state, int oldId, int newId, InformativeException e) throws InformativeException {
    if (state == State.HANDSHAKE)
      throw e; 
    Class<? extends PacketType> packetTypeClass = (state == State.PLAY) ? ((direction == Direction.CLIENTBOUND) ? (Class)this.oldClientboundPacketEnum : (Class)this.newServerboundPacketEnum) : null;
    if (packetTypeClass != null) {
      PacketType[] enumConstants = packetTypeClass.getEnumConstants();
      PacketType packetType = (oldId < enumConstants.length && oldId >= 0) ? enumConstants[oldId] : null;
      Via.getPlatform().getLogger().warning("ERROR IN " + getClass().getSimpleName() + " IN REMAP OF " + packetType + " (" + toNiceHex(oldId) + ")");
    } else {
      Via.getPlatform().getLogger().warning("ERROR IN " + getClass().getSimpleName() + " IN REMAP OF " + 
          toNiceHex(oldId) + "->" + toNiceHex(newId));
    } 
    throw e;
  }
  
  private String toNiceHex(int id) {
    String hex = Integer.toHexString(id).toUpperCase();
    return ((hex.length() == 1) ? "0x0" : "0x") + hex;
  }
  
  private void checkPacketType(PacketType packetType, boolean isValid) {
    if (!isValid)
      throw new IllegalArgumentException("Packet type " + packetType + " in " + packetType.getClass().getSimpleName() + " is taken from the wrong enum"); 
  }
  
  public <T> T get(Class<T> objectClass) {
    return (T)this.storedObjects.get(objectClass);
  }
  
  public void put(Object object) {
    this.storedObjects.put(object.getClass(), object);
  }
  
  public boolean hasMappingDataToLoad() {
    return (getMappingData() != null);
  }
  
  public String toString() {
    return "Protocol:" + getClass().getSimpleName();
  }
  
  public static final class Packet {
    private final State state;
    
    private final int packetId;
    
    public Packet(State state, int packetId) {
      this.state = state;
      this.packetId = packetId;
    }
    
    public State getState() {
      return this.state;
    }
    
    public int getPacketId() {
      return this.packetId;
    }
    
    public String toString() {
      return "Packet{state=" + this.state + ", packetId=" + this.packetId + '}';
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      Packet that = (Packet)o;
      return (this.packetId == that.packetId && this.state == that.state);
    }
    
    public int hashCode() {
      int result = (this.state != null) ? this.state.hashCode() : 0;
      result = 31 * result + this.packetId;
      return result;
    }
  }
  
  public static final class ProtocolPacket {
    private final State state;
    
    private final int oldId;
    
    private final int newId;
    
    private final PacketType unmappedPacketType;
    
    private final PacketType mappedPacketType;
    
    private final PacketRemapper remapper;
    
    @Deprecated
    public ProtocolPacket(State state, int oldId, int newId, PacketRemapper remapper) {
      this.state = state;
      this.oldId = oldId;
      this.newId = newId;
      this.remapper = remapper;
      this.unmappedPacketType = null;
      this.mappedPacketType = null;
    }
    
    public ProtocolPacket(State state, PacketType unmappedPacketType, PacketType mappedPacketType, PacketRemapper remapper) {
      this.state = state;
      this.unmappedPacketType = unmappedPacketType;
      if (unmappedPacketType.direction() == Direction.CLIENTBOUND) {
        this.oldId = unmappedPacketType.getId();
        this.newId = (mappedPacketType != null) ? mappedPacketType.getId() : -1;
      } else {
        this.oldId = (mappedPacketType != null) ? mappedPacketType.getId() : -1;
        this.newId = unmappedPacketType.getId();
      } 
      this.mappedPacketType = mappedPacketType;
      this.remapper = remapper;
    }
    
    public State getState() {
      return this.state;
    }
    
    @Deprecated
    public int getOldId() {
      return this.oldId;
    }
    
    @Deprecated
    public int getNewId() {
      return this.newId;
    }
    
    public PacketType getUnmappedPacketType() {
      return this.unmappedPacketType;
    }
    
    public PacketType getMappedPacketType() {
      return this.mappedPacketType;
    }
    
    public boolean isMappedOverTypes() {
      return (this.unmappedPacketType != null);
    }
    
    public PacketRemapper getRemapper() {
      return this.remapper;
    }
  }
}
