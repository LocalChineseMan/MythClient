package com.viaversion.viabackwards.api.rewriters;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.List;

public abstract class EntityRewriterBase<T extends BackwardsProtocol> extends EntityRewriter<T> {
  private final Int2ObjectMap<EntityData> entityDataMappings = (Int2ObjectMap<EntityData>)new Int2ObjectOpenHashMap();
  
  private final MetaType displayNameMetaType;
  
  private final MetaType displayVisibilityMetaType;
  
  private final int displayNameIndex;
  
  private final int displayVisibilityIndex;
  
  EntityRewriterBase(T protocol, MetaType displayNameMetaType, int displayNameIndex, MetaType displayVisibilityMetaType, int displayVisibilityIndex) {
    super((Protocol)protocol, false);
    this.displayNameMetaType = displayNameMetaType;
    this.displayNameIndex = displayNameIndex;
    this.displayVisibilityMetaType = displayVisibilityMetaType;
    this.displayVisibilityIndex = displayVisibilityIndex;
  }
  
  public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
    EntityType type = tracker(connection).entityType(entityId);
    if (type == null)
      return; 
    super.handleMetadata(entityId, metadataList, connection);
    EntityData entityData = entityDataForType(type);
    Metadata meta = getMeta(this.displayNameIndex, metadataList);
    if (meta != null && entityData != null && entityData.mobName() != null && (meta
      .getValue() == null || meta.getValue().toString().isEmpty()) && meta
      .metaType().typeId() == this.displayNameMetaType.typeId()) {
      meta.setValue(entityData.mobName());
      if (ViaBackwards.getConfig().alwaysShowOriginalMobName()) {
        removeMeta(this.displayVisibilityIndex, metadataList);
        metadataList.add(new Metadata(this.displayVisibilityIndex, this.displayVisibilityMetaType, Boolean.valueOf(true)));
      } 
    } 
    if (entityData != null && entityData.hasBaseMeta())
      entityData.defaultMeta().createMeta(new WrappedMetadata(metadataList)); 
  }
  
  protected Metadata getMeta(int metaIndex, List<Metadata> metadataList) {
    for (Metadata metadata : metadataList) {
      if (metadata.id() == metaIndex)
        return metadata; 
    } 
    return null;
  }
  
  protected void removeMeta(int metaIndex, List<Metadata> metadataList) {
    metadataList.removeIf(meta -> (meta.id() == metaIndex));
  }
  
  protected boolean hasData(EntityType type) {
    return this.entityDataMappings.containsKey(type.getId());
  }
  
  protected EntityData entityDataForType(EntityType type) {
    return (EntityData)this.entityDataMappings.get(type.getId());
  }
  
  protected StoredEntityData storedEntityData(MetaHandlerEvent event) {
    return tracker(event.user()).entityData(event.entityId());
  }
  
  protected EntityData mapEntityTypeWithData(EntityType type, EntityType mappedType) {
    Preconditions.checkArgument((type.getClass() == mappedType.getClass()));
    int mappedReplacementId = newEntityId(mappedType.getId());
    EntityData data = new EntityData(type.getId(), mappedReplacementId);
    mapEntityType(type.getId(), mappedReplacementId);
    this.entityDataMappings.put(type.getId(), data);
    return data;
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
      } catch (IllegalArgumentException illegalArgumentException) {}
    } 
  }
  
  public void registerMetaTypeHandler(MetaType itemType, MetaType blockType, MetaType particleType, MetaType optChatType) {
    filter().handler((event, meta) -> {
          MetaType type = meta.metaType();
          if (itemType != null && type == itemType) {
            ((BackwardsProtocol)this.protocol).getItemRewriter().handleItemToClient((Item)meta.value());
          } else if (blockType != null && type == blockType) {
            int data = ((Integer)meta.value()).intValue();
            meta.setValue(Integer.valueOf(((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(data)));
          } else if (particleType != null && type == particleType) {
            rewriteParticle((Particle)meta.value());
          } else if (optChatType != null && type == optChatType) {
            JsonElement text = (JsonElement)meta.value();
            if (text != null)
              ((BackwardsProtocol)this.protocol).getTranslatableRewriter().processText(text); 
          } 
        });
  }
  
  protected PacketHandler getTrackerHandler(Type<? extends Number> intType, int typeIndex) {
    return wrapper -> {
        Number id = (Number)wrapper.get(intType, typeIndex);
        tracker(wrapper.user()).addEntity(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), typeFromId(id.intValue()));
      };
  }
  
  protected PacketHandler getTrackerHandler() {
    return getTrackerHandler((Type<? extends Number>)Type.VAR_INT, 1);
  }
  
  protected PacketHandler getTrackerHandler(EntityType entityType, Type<? extends Number> intType) {
    return wrapper -> tracker(wrapper.user()).addEntity(((Integer)wrapper.get(intType, 0)).intValue(), entityType);
  }
  
  protected PacketHandler getDimensionHandler(int index) {
    return wrapper -> {
        ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
        int dimensionId = ((Integer)wrapper.get((Type)Type.INT, index)).intValue();
        clientWorld.setEnvironment(dimensionId);
      };
  }
}
