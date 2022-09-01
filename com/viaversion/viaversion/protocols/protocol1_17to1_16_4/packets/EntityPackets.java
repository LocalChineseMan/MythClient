package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_17;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;

public final class EntityPackets extends EntityRewriter<Protocol1_17To1_16_4> {
  public EntityPackets(Protocol1_17To1_16_4 protocol) {
    super((Protocol)protocol);
    mapTypes((EntityType[])Entity1_16_2Types.values(), Entity1_17Types.class);
  }
  
  public void registerPackets() {
    registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_ENTITY, (EntityType)Entity1_17Types.FALLING_BLOCK);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_MOB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_PLAYER, (EntityType)Entity1_17Types.PLAYER);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_METADATA, Types1_16.METADATA_LIST, Types1_17.METADATA_LIST);
    ((Protocol1_17To1_16_4)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.DESTROY_ENTITIES, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int[] entityIds = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                  wrapper.cancel();
                  EntityTracker entityTracker = wrapper.user().getEntityTracker(Protocol1_17To1_16_4.class);
                  for (int entityId : entityIds) {
                    entityTracker.removeEntity(entityId);
                    PacketWrapper newPacket = wrapper.create((PacketType)ClientboundPackets1_17.REMOVE_ENTITY);
                    newPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                    newPacket.send(Protocol1_17To1_16_4.class);
                  } 
                });
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.write((Type)Type.VAR_INT, wrapper.read((Type)Type.INT)));
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.PLAYER_POSITION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false)));
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.COMBAT_EVENT, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  ClientboundPackets1_17 clientboundPackets1_173;
                  ClientboundPackets1_17 clientboundPackets1_172;
                  ClientboundPackets1_17 clientboundPackets1_171;
                  int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  switch (type) {
                    case 0:
                      clientboundPackets1_173 = ClientboundPackets1_17.COMBAT_ENTER;
                      break;
                    case 1:
                      clientboundPackets1_172 = ClientboundPackets1_17.COMBAT_END;
                      break;
                    case 2:
                      clientboundPackets1_171 = ClientboundPackets1_17.COMBAT_KILL;
                      break;
                    default:
                      throw new IllegalArgumentException("Invalid combat type received: " + type);
                  } 
                  wrapper.setId(clientboundPackets1_171.getId());
                });
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).cancelClientbound((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_MOVEMENT);
  }
  
  protected void registerRewrites() {
    filter().handler((event, meta) -> {
          meta.setMetaType((MetaType)MetaType1_17.byId(meta.metaType().typeId()));
          if (meta.metaType() == MetaType1_17.POSE) {
            int pose = ((Integer)meta.value()).intValue();
            if (pose > 5)
              meta.setValue(Integer.valueOf(pose + 1)); 
          } 
        });
    registerMetaTypeHandler((MetaType)MetaType1_17.ITEM, (MetaType)MetaType1_17.BLOCK_STATE, (MetaType)MetaType1_17.PARTICLE);
    filter().filterFamily((EntityType)Entity1_17Types.ENTITY).addIndex(7);
    filter().filterFamily((EntityType)Entity1_17Types.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
          int data = ((Integer)meta.getValue()).intValue();
          meta.setValue(Integer.valueOf(((Protocol1_17To1_16_4)this.protocol).getMappingData().getNewBlockStateId(data)));
        });
    filter().type((EntityType)Entity1_17Types.SHULKER).removeIndex(17);
  }
  
  public EntityType typeFromId(int type) {
    return Entity1_17Types.getTypeFromId(type);
  }
}
