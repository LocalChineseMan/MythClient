package com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.Protocol1_9_4To1_10;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.ObjectType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityPackets1_10 extends LegacyEntityRewriter<Protocol1_9_4To1_10> {
  public EntityPackets1_10(Protocol1_9_4To1_10 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.BYTE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            handler(EntityPackets1_10.this.getObjectTrackerHandler());
            handler(EntityPackets1_10.this.getObjectRewriter(id -> (ObjectType)Entity1_11Types.ObjectType.findById(id.byteValue()).orElse(null)));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Optional<Entity1_12Types.ObjectType> type = Entity1_12Types.ObjectType.findById(((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue());
                    if (type.isPresent() && type.get() == Entity1_12Types.ObjectType.FALLING_BLOCK) {
                      int objectData = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int objType = objectData & 0xFFF;
                      int data = objectData >> 12 & 0xF;
                      Block block = ((Protocol1_9_4To1_10)EntityPackets1_10.this.protocol).getItemRewriter().handleBlock(objType, data);
                      if (block == null)
                        return; 
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(block.getId() | block.getData() << 12));
                    } 
                  }
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_10Types.EntityType.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_10Types.EntityType.WEATHER);
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_9.METADATA_LIST);
            handler(EntityPackets1_10.this.getTrackerHandler((Type)Type.UNSIGNED_BYTE, 0));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityType type = EntityPackets1_10.this.tracker(wrapper.user()).entityType(entityId);
                    List<Metadata> metadata = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    EntityPackets1_10.this.handleMetadata(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), metadata, wrapper.user());
                    EntityData entityData = EntityPackets1_10.this.entityDataForType(type);
                    if (entityData != null) {
                      WrappedMetadata storage = new WrappedMetadata(metadata);
                      wrapper.set((Type)Type.UNSIGNED_BYTE, 0, Short.valueOf((short)entityData.replacementId()));
                      if (entityData.hasBaseMeta())
                        entityData.defaultMeta().createMeta(storage); 
                    } 
                  }
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PAINTING, (EntityType)Entity1_10Types.EntityType.PAINTING);
    registerJoinGame((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, (EntityType)Entity1_10Types.EntityType.PLAYER);
    registerRespawn((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN);
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_9.METADATA_LIST);
            handler(EntityPackets1_10.this.getTrackerAndMetaHandler(Types1_9.METADATA_LIST, (EntityType)Entity1_11Types.EntityType.PLAYER));
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
  }
  
  protected void registerRewrites() {
    mapEntityTypeWithData((EntityType)Entity1_10Types.EntityType.POLAR_BEAR, (EntityType)Entity1_10Types.EntityType.SHEEP).mobName("Polar Bear");
    filter().type((EntityType)Entity1_10Types.EntityType.POLAR_BEAR).index(13).handler((event, meta) -> {
          boolean b = ((Boolean)meta.getValue()).booleanValue();
          meta.setTypeAndValue((MetaType)MetaType1_9.Byte, Byte.valueOf(b ? 14 : 0));
        });
    filter().type((EntityType)Entity1_10Types.EntityType.ZOMBIE).index(13).handler((event, meta) -> {
          if (((Integer)meta.getValue()).intValue() == 6)
            meta.setValue(Integer.valueOf(0)); 
        });
    filter().type((EntityType)Entity1_10Types.EntityType.SKELETON).index(12).handler((event, meta) -> {
          if (((Integer)meta.getValue()).intValue() == 2)
            meta.setValue(Integer.valueOf(0)); 
        });
    filter().removeIndex(5);
  }
  
  public EntityType typeFromId(int typeId) {
    return (EntityType)Entity1_10Types.getTypeFromId(typeId, false);
  }
  
  protected EntityType getObjectTypeFromId(int typeId) {
    return (EntityType)Entity1_10Types.getTypeFromId(typeId, true);
  }
}
