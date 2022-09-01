package com.viaversion.viaversion.rewriter;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.rewriter.meta.MetaFilter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEventImpl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class EntityRewriter<T extends Protocol> extends RewriterBase<T> implements EntityRewriter<T> {
  private static final Metadata[] EMPTY_ARRAY = new Metadata[0];
  
  protected final List<MetaFilter> metadataFilters = new ArrayList<>();
  
  protected final boolean trackMappedType;
  
  protected Int2IntMap typeMappings;
  
  protected EntityRewriter(T protocol) {
    this(protocol, true);
  }
  
  protected EntityRewriter(T protocol, boolean trackMappedType) {
    super((Protocol)protocol);
    this.trackMappedType = trackMappedType;
    protocol.put(this);
  }
  
  public MetaFilter.Builder filter() {
    return new MetaFilter.Builder(this);
  }
  
  public void registerFilter(MetaFilter filter) {
    Preconditions.checkArgument(!this.metadataFilters.contains(filter));
    this.metadataFilters.add(filter);
  }
  
  public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
    EntityType type = tracker(connection).entityType(entityId);
    int i = 0;
    for (Metadata metadata : (Metadata[])metadataList.<Metadata>toArray(EMPTY_ARRAY)) {
      if (!callOldMetaHandler(entityId, type, metadata, metadataList, connection)) {
        metadataList.remove(i--);
      } else {
        MetaHandlerEventImpl metaHandlerEventImpl;
        MetaHandlerEvent event = null;
        for (MetaFilter filter : this.metadataFilters) {
          if (!filter.isFiltered(type, metadata))
            continue; 
          if (event == null)
            metaHandlerEventImpl = new MetaHandlerEventImpl(connection, type, entityId, metadata, metadataList); 
          try {
            filter.handler().handle((MetaHandlerEvent)metaHandlerEventImpl, metadata);
          } catch (Exception e) {
            logException(e, type, metadataList, metadata);
            metadataList.remove(i--);
            break;
          } 
          if (metaHandlerEventImpl.cancelled()) {
            metadataList.remove(i--);
            break;
          } 
        } 
        if (metaHandlerEventImpl != null && metaHandlerEventImpl.extraMeta() != null)
          metadataList.addAll(metaHandlerEventImpl.extraMeta()); 
        i++;
      } 
    } 
  }
  
  @Deprecated
  private boolean callOldMetaHandler(int entityId, EntityType type, Metadata metadata, List<Metadata> metadataList, UserConnection connection) {
    try {
      handleMetadata(entityId, type, metadata, metadataList, connection);
      return true;
    } catch (Exception e) {
      logException(e, type, metadataList, metadata);
      return false;
    } 
  }
  
  @Deprecated
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {}
  
  public int newEntityId(int id) {
    return (this.typeMappings != null) ? this.typeMappings.getOrDefault(id, id) : id;
  }
  
  public void mapEntityType(EntityType type, EntityType mappedType) {
    Preconditions.checkArgument((type.getClass() != mappedType.getClass()), "EntityTypes should not be of the same class/enum");
    mapEntityType(type.getId(), mappedType.getId());
  }
  
  protected void mapEntityType(int id, int mappedId) {
    if (this.typeMappings == null) {
      this.typeMappings = (Int2IntMap)new Int2IntOpenHashMap();
      this.typeMappings.defaultReturnValue(-1);
    } 
    this.typeMappings.put(id, mappedId);
  }
  
  public <E extends Enum<E> & EntityType> void mapTypes(EntityType[] oldTypes, Class<E> newTypeClass) {
    if (this.typeMappings == null) {
      this.typeMappings = (Int2IntMap)new Int2IntOpenHashMap(oldTypes.length, 1.0F);
      this.typeMappings.defaultReturnValue(-1);
    } 
    for (EntityType oldType : oldTypes) {
      try {
        E newType = Enum.valueOf(newTypeClass, oldType.name());
        this.typeMappings.put(oldType.getId(), ((EntityType)newType).getId());
      } catch (IllegalArgumentException notFound) {
        if (!this.typeMappings.containsKey(oldType.getId()))
          Via.getPlatform().getLogger().warning("Could not find new entity type for " + oldType + "! Old type: " + oldType
              .getClass().getEnclosingClass().getSimpleName() + ", new type: " + newTypeClass.getEnclosingClass().getSimpleName()); 
      } 
    } 
  }
  
  public void registerMetaTypeHandler(MetaType itemType, MetaType blockType, MetaType particleType) {
    filter().handler((event, meta) -> {
          if (itemType != null && meta.metaType() == itemType) {
            this.protocol.getItemRewriter().handleItemToClient((Item)meta.value());
          } else if (blockType != null && meta.metaType() == blockType) {
            int data = ((Integer)meta.value()).intValue();
            meta.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(data)));
          } else if (particleType != null && meta.metaType() == particleType) {
            rewriteParticle((Particle)meta.value());
          } 
        });
  }
  
  public void registerTracker(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(EntityRewriter.this.trackerHandler());
          }
        });
  }
  
  public void registerTrackerWithData(ClientboundPacketType packetType, final EntityType fallingBlockType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            handler(EntityRewriter.this.trackerHandler());
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityType entityType = EntityRewriter.this.tracker(wrapper.user()).entityType(entityId);
                  if (entityType == fallingBlockType)
                    wrapper.set((Type)Type.INT, 0, Integer.valueOf(EntityRewriter.this.protocol.getMappingData().getNewBlockStateId(((Integer)wrapper.get((Type)Type.INT, 0)).intValue()))); 
                });
          }
        });
  }
  
  public void registerTracker(ClientboundPacketType packetType, final EntityType entityType, final Type<?> intType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.passthrough(intType)).intValue();
                  EntityRewriter.this.tracker(wrapper.user()).addEntity(entityId, entityType);
                });
          }
        });
  }
  
  public void registerTracker(ClientboundPacketType packetType, EntityType entityType) {
    registerTracker(packetType, entityType, (Type<?>)Type.VAR_INT);
  }
  
  public void registerRemoveEntities(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int[] entityIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                  EntityTracker entityTracker = EntityRewriter.this.tracker(wrapper.user());
                  for (int entity : entityIds)
                    entityTracker.removeEntity(entity); 
                });
          }
        });
  }
  
  public void registerRemoveEntity(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, (PacketRemapper)new Object(this));
  }
  
  public void registerMetadataRewriter(ClientboundPacketType packetType, final Type<List<Metadata>> oldMetaType, final Type<List<Metadata>> newMetaType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            if (oldMetaType != null) {
              map(oldMetaType, newMetaType);
            } else {
              map(newMetaType);
            } 
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  List<Metadata> metadata = (List<Metadata>)wrapper.get(newMetaType, 0);
                  EntityRewriter.this.handleMetadata(entityId, metadata, wrapper.user());
                });
          }
        });
  }
  
  public void registerMetadataRewriter(ClientboundPacketType packetType, Type<List<Metadata>> metaType) {
    registerMetadataRewriter(packetType, (Type<List<Metadata>>)null, metaType);
  }
  
  public PacketHandler trackerHandler() {
    return trackerAndRewriterHandler((Type<List<Metadata>>)null);
  }
  
  protected PacketHandler worldDataTrackerHandler(int nbtIndex) {
    return wrapper -> {
        EntityTracker tracker = tracker(wrapper.user());
        CompoundTag registryData = (CompoundTag)wrapper.get(Type.NBT, nbtIndex);
        Tag height = registryData.get("height");
        if (height instanceof IntTag) {
          int blockHeight = ((IntTag)height).asInt();
          tracker.setCurrentWorldSectionHeight(blockHeight >> 4);
        } else {
          Via.getPlatform().getLogger().warning("Height missing in dimension data: " + registryData);
        } 
        Tag minY = registryData.get("min_y");
        if (minY instanceof IntTag) {
          tracker.setCurrentMinY(((IntTag)minY).asInt());
        } else {
          Via.getPlatform().getLogger().warning("Min Y missing in dimension data: " + registryData);
        } 
      };
  }
  
  public PacketHandler trackerAndRewriterHandler(Type<List<Metadata>> metaType) {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
        int newType = newEntityId(type);
        if (newType != type)
          wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(newType)); 
        EntityType entType = typeFromId(this.trackMappedType ? newType : type);
        tracker(wrapper.user()).addEntity(entityId, entType);
        if (metaType != null)
          handleMetadata(entityId, (List<Metadata>)wrapper.get(metaType, 0), wrapper.user()); 
      };
  }
  
  public PacketHandler trackerAndRewriterHandler(Type<List<Metadata>> metaType, EntityType entityType) {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        tracker(wrapper.user()).addEntity(entityId, entityType);
        if (metaType != null)
          handleMetadata(entityId, (List<Metadata>)wrapper.get(metaType, 0), wrapper.user()); 
      };
  }
  
  public PacketHandler objectTrackerHandler() {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        byte type = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
        EntityType entType = objectTypeFromId(type);
        tracker(wrapper.user()).addEntity(entityId, entType);
      };
  }
  
  @Deprecated
  protected Metadata metaByIndex(int index, List<Metadata> metadataList) {
    for (Metadata metadata : metadataList) {
      if (metadata.id() == index)
        return metadata; 
    } 
    return null;
  }
  
  protected void rewriteParticle(Particle particle) {
    ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
    int id = particle.getId();
    if (id == mappings.getBlockId() || id == mappings.getFallingDustId()) {
      Particle.ParticleData data = particle.getArguments().get(0);
      data.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(((Integer)data.get()).intValue())));
    } else if (id == mappings.getItemId()) {
      Particle.ParticleData data = particle.getArguments().get(0);
      data.setValue(Integer.valueOf(this.protocol.getMappingData().getNewItemId(((Integer)data.get()).intValue())));
    } 
    particle.setId(this.protocol.getMappingData().getNewParticleId(id));
  }
  
  private void logException(Exception e, EntityType type, List<Metadata> metadataList, Metadata metadata) {
    if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
      Logger logger = Via.getPlatform().getLogger();
      logger.severe("An error occurred in metadata handler " + getClass().getSimpleName() + " for " + (
          (type != null) ? type.name() : "untracked") + " entity type: " + metadata);
      logger.severe(metadataList.stream().sorted(Comparator.comparingInt(Metadata::id))
          .map(Metadata::toString).collect(Collectors.joining("\n", "Full metadata: ", "")));
      e.printStackTrace();
    } 
  }
}
