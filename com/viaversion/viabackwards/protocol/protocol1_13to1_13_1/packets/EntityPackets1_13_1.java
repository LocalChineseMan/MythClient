package com.viaversion.viabackwards.protocol.protocol1_13to1_13_1.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13to1_13_1.Protocol1_13To1_13_1;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.List;

public class EntityPackets1_13_1 extends LegacyEntityRewriter<Protocol1_13To1_13_1> {
  public EntityPackets1_13_1(Protocol1_13To1_13_1 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_13To1_13_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
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
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    byte type = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    Entity1_13Types.EntityType entType = Entity1_13Types.getTypeFromId(type, true);
                    if (entType == null) {
                      ViaBackwards.getPlatform().getLogger().warning("Could not find 1.13 entity type " + type);
                      return;
                    } 
                    if (entType.is((EntityType)Entity1_13Types.EntityType.FALLING_BLOCK)) {
                      int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(((Protocol1_13To1_13_1)EntityPackets1_13_1.this.protocol).getMappingData().getNewBlockStateId(data)));
                    } 
                    EntityPackets1_13_1.this.tracker(wrapper.user()).addEntity(entityId, (EntityType)entType);
                  }
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_13.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_13Types.EntityType.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_13.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_13Types.EntityType.LIGHTNING_BOLT);
    ((Protocol1_13To1_13_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_13.METADATA_LIST);
            handler(EntityPackets1_13_1.this.getTrackerHandler());
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadata = (List<Metadata>)wrapper.get(Types1_13.METADATA_LIST, 0);
                    EntityPackets1_13_1.this.handleMetadata(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), metadata, wrapper.user());
                  }
                });
          }
        });
    ((Protocol1_13To1_13_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_13.METADATA_LIST);
            handler(EntityPackets1_13_1.this.getTrackerAndMetaHandler(Types1_13.METADATA_LIST, (EntityType)Entity1_13Types.EntityType.PLAYER));
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PAINTING, (EntityType)Entity1_13Types.EntityType.PAINTING);
    registerJoinGame((ClientboundPacketType)ClientboundPackets1_13.JOIN_GAME, (EntityType)Entity1_13Types.EntityType.PLAYER);
    registerRespawn((ClientboundPacketType)ClientboundPackets1_13.RESPAWN);
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST);
  }
  
  protected void registerRewrites() {
    filter().handler((event, meta) -> {
          if (meta.metaType() == MetaType1_13.Slot) {
            ((Protocol1_13To1_13_1)this.protocol).getItemRewriter().handleItemToClient((Item)meta.getValue());
          } else if (meta.metaType() == MetaType1_13.BlockID) {
            int data = ((Integer)meta.getValue()).intValue();
            meta.setValue(Integer.valueOf(((Protocol1_13To1_13_1)this.protocol).getMappingData().getNewBlockStateId(data)));
          } else if (meta.metaType() == MetaType1_13.PARTICLE) {
            rewriteParticle((Particle)meta.getValue());
          } 
        });
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.ABSTRACT_ARROW).cancel(7);
    filter().type((EntityType)Entity1_13Types.EntityType.SPECTRAL_ARROW).index(8).toIndex(7);
    filter().type((EntityType)Entity1_13Types.EntityType.TRIDENT).index(8).toIndex(7);
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT).index(9).handler((event, meta) -> {
          int data = ((Integer)meta.getValue()).intValue();
          meta.setValue(Integer.valueOf(((Protocol1_13To1_13_1)this.protocol).getMappingData().getNewBlockStateId(data)));
        });
  }
  
  public EntityType typeFromId(int typeId) {
    return (EntityType)Entity1_13Types.getTypeFromId(typeId, false);
  }
  
  protected EntityType getObjectTypeFromId(int typeId) {
    return (EntityType)Entity1_13Types.getTypeFromId(typeId, true);
  }
}
