package com.viaversion.viabackwards.protocol.protocol1_14to1_14_1.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14to1_14_1.Protocol1_14To1_14_1;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import java.util.List;

public class EntityPackets1_14_1 extends LegacyEntityRewriter<Protocol1_14To1_14_1> {
  public EntityPackets1_14_1(Protocol1_14To1_14_1 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    registerTracker((ClientboundPacketType)ClientboundPackets1_14.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_14Types.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_14.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_14Types.LIGHTNING_BOLT);
    registerTracker((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PAINTING, (EntityType)Entity1_14Types.PAINTING);
    registerTracker((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PLAYER, (EntityType)Entity1_14Types.PLAYER);
    registerTracker((ClientboundPacketType)ClientboundPackets1_14.JOIN_GAME, (EntityType)Entity1_14Types.PLAYER, (Type)Type.INT);
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_14.DESTROY_ENTITIES);
    ((Protocol1_14To1_14_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(EntityPackets1_14_1.this.getTrackerHandler());
          }
        });
    ((Protocol1_14To1_14_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_14.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    EntityPackets1_14_1.this.tracker(wrapper.user()).addEntity(entityId, Entity1_14Types.getTypeFromId(type));
                    List<Metadata> metadata = (List<Metadata>)wrapper.get(Types1_14.METADATA_LIST, 0);
                    EntityPackets1_14_1.this.handleMetadata(entityId, metadata, wrapper.user());
                  }
                });
          }
        });
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST);
  }
  
  protected void registerRewrites() {
    filter().type((EntityType)Entity1_14Types.VILLAGER).cancel(15);
    filter().type((EntityType)Entity1_14Types.VILLAGER).index(16).toIndex(15);
    filter().type((EntityType)Entity1_14Types.WANDERING_TRADER).cancel(15);
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_14Types.getTypeFromId(typeId);
  }
}
