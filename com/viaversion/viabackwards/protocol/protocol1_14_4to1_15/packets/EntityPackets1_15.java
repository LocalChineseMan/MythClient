package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.EntityTypeMapping;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.ImmediateRespawn;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.ArrayList;

public class EntityPackets1_15 extends EntityRewriter<Protocol1_14_4To1_15> {
  public EntityPackets1_15(Protocol1_14_4To1_15 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.UPDATE_HEALTH, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  float health = ((Float)wrapper.passthrough((Type)Type.FLOAT)).floatValue();
                  if (health > 0.0F)
                    return; 
                  if (!((ImmediateRespawn)wrapper.user().get(ImmediateRespawn.class)).isImmediateRespawn())
                    return; 
                  PacketWrapper statusPacket = wrapper.create((PacketType)ServerboundPackets1_14.CLIENT_STATUS);
                  statusPacket.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  statusPacket.sendToServer(Protocol1_14_4To1_15.class);
                });
          }
        });
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.GAME_EVENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.FLOAT);
            handler(wrapper -> {
                  if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 11)
                    ((ImmediateRespawn)wrapper.user().get(ImmediateRespawn.class)).setImmediateRespawn((((Float)wrapper.get((Type)Type.FLOAT, 0)).floatValue() == 1.0F)); 
                });
          }
        });
    registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_15.SPAWN_ENTITY, (EntityType)Entity1_15Types.FALLING_BLOCK);
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.SPAWN_MOB, new PacketRemapper() {
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
            handler(wrapper -> wrapper.write(Types1_14.METADATA_LIST, new ArrayList()));
            handler(wrapper -> {
                  int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                  EntityType entityType = Entity1_15Types.getTypeFromId(type);
                  EntityPackets1_15.this.tracker(wrapper.user()).addEntity(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), entityType);
                  wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(EntityTypeMapping.getOldEntityId(type)));
                });
          }
        });
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.LONG, (Type)Type.NOTHING);
          }
        });
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            map((Type)Type.LONG, (Type)Type.NOTHING);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            handler(EntityPackets1_15.this.getTrackerHandler((EntityType)Entity1_15Types.PLAYER, (Type)Type.INT));
            handler(wrapper -> {
                  boolean immediateRespawn = !((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  ((ImmediateRespawn)wrapper.user().get(ImmediateRespawn.class)).setImmediateRespawn(immediateRespawn);
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_15Types.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_15Types.LIGHTNING_BOLT);
    registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PAINTING, (EntityType)Entity1_15Types.PAINTING);
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            handler(wrapper -> wrapper.write(Types1_14.METADATA_LIST, new ArrayList()));
            handler(EntityPackets1_15.this.getTrackerHandler((EntityType)Entity1_15Types.PLAYER, (Type)Type.VAR_INT));
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_15.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_15.ENTITY_METADATA, Types1_14.METADATA_LIST);
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityType entityType = EntityPackets1_15.this.tracker(wrapper.user()).entityType(entityId);
                  if (entityType != Entity1_15Types.BEE)
                    return; 
                  int size = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  int newSize = size;
                  for (int i = 0; i < size; i++) {
                    String key = (String)wrapper.read(Type.STRING);
                    if (key.equals("generic.flyingSpeed")) {
                      newSize--;
                      wrapper.read((Type)Type.DOUBLE);
                      int modSize = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      for (int j = 0; j < modSize; j++) {
                        wrapper.read(Type.UUID);
                        wrapper.read((Type)Type.DOUBLE);
                        wrapper.read((Type)Type.BYTE);
                      } 
                    } else {
                      wrapper.write(Type.STRING, key);
                      wrapper.passthrough((Type)Type.DOUBLE);
                      int modSize = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      for (int j = 0; j < modSize; j++) {
                        wrapper.passthrough(Type.UUID);
                        wrapper.passthrough((Type)Type.DOUBLE);
                        wrapper.passthrough((Type)Type.BYTE);
                      } 
                    } 
                  } 
                  if (newSize != size)
                    wrapper.set((Type)Type.INT, 0, Integer.valueOf(newSize)); 
                });
          }
        });
  }
  
  protected void registerRewrites() {
    registerMetaTypeHandler((MetaType)MetaType1_14.Slot, (MetaType)MetaType1_14.BlockID, (MetaType)MetaType1_14.PARTICLE, null);
    filter().filterFamily((EntityType)Entity1_15Types.LIVINGENTITY).removeIndex(12);
    filter().type((EntityType)Entity1_15Types.BEE).cancel(15);
    filter().type((EntityType)Entity1_15Types.BEE).cancel(16);
    mapEntityTypeWithData((EntityType)Entity1_15Types.BEE, (EntityType)Entity1_15Types.PUFFERFISH).jsonName("Bee").spawnMetadata(storage -> {
          storage.add(new Metadata(14, (MetaType)MetaType1_14.Boolean, Boolean.valueOf(false)));
          storage.add(new Metadata(15, (MetaType)MetaType1_14.VarInt, Integer.valueOf(2)));
        });
    filter().type((EntityType)Entity1_15Types.ENDERMAN).cancel(16);
    filter().type((EntityType)Entity1_15Types.TRIDENT).cancel(10);
    filter().type((EntityType)Entity1_15Types.WOLF).addIndex(17);
    filter().type((EntityType)Entity1_15Types.WOLF).index(8).handler((event, meta) -> event.createExtraMeta(new Metadata(17, (MetaType)MetaType1_14.Float, event.meta().value())));
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_15Types.getTypeFromId(typeId);
  }
  
  public int newEntityId(int newId) {
    return EntityTypeMapping.getOldEntityId(newId);
  }
}
