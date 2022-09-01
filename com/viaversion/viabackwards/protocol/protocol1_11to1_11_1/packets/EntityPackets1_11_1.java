package com.viaversion.viabackwards.protocol.protocol1_11to1_11_1.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_11to1_11_1.Protocol1_11To1_11_1;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.ObjectType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import java.util.function.Function;

public class EntityPackets1_11_1 extends LegacyEntityRewriter<Protocol1_11To1_11_1> {
  public EntityPackets1_11_1(Protocol1_11To1_11_1 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_11To1_11_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
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
            handler(EntityPackets1_11_1.this.getObjectTrackerHandler());
            handler(EntityPackets1_11_1.this.getObjectRewriter(id -> (ObjectType)Entity1_11Types.ObjectType.findById(id.byteValue()).orElse(null)));
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_11Types.EntityType.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_11Types.EntityType.WEATHER);
    ((Protocol1_11To1_11_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_9.METADATA_LIST);
            handler(EntityPackets1_11_1.this.getTrackerHandler());
            handler(EntityPackets1_11_1.this.getMobSpawnRewriter(Types1_9.METADATA_LIST));
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PAINTING, (EntityType)Entity1_11Types.EntityType.PAINTING);
    registerJoinGame((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, (EntityType)Entity1_11Types.EntityType.PLAYER);
    registerRespawn((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN);
    ((Protocol1_11To1_11_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_9.METADATA_LIST);
            handler(EntityPackets1_11_1.this.getTrackerAndMetaHandler(Types1_9.METADATA_LIST, (EntityType)Entity1_11Types.EntityType.PLAYER));
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
  }
  
  protected void registerRewrites() {
    filter().type((EntityType)Entity1_11Types.EntityType.FIREWORK).cancel(7);
    filter().type((EntityType)Entity1_11Types.EntityType.PIG).cancel(14);
  }
  
  public EntityType typeFromId(int typeId) {
    return (EntityType)Entity1_11Types.getTypeFromId(typeId, false);
  }
  
  protected EntityType getObjectTypeFromId(int typeId) {
    return (EntityType)Entity1_11Types.getTypeFromId(typeId, true);
  }
}
