package com.viaversion.viaversion.protocols.protocol1_14_1to1_14.packets;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import com.viaversion.viaversion.protocols.protocol1_14_1to1_14.metadata.MetadataRewriter1_14_1To1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;

public class EntityPackets {
  public static void register(Protocol1_14_1To1_14 protocol) {
    final MetadataRewriter1_14_1To1_14 metadataRewriter = (MetadataRewriter1_14_1To1_14)protocol.get(MetadataRewriter1_14_1To1_14.class);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_MOB, new PacketRemapper() {
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
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_14.METADATA_LIST));
          }
        });
    metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_14.DESTROY_ENTITIES);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_14.METADATA_LIST);
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_14.METADATA_LIST, (EntityType)Entity1_14Types.PLAYER));
          }
        });
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST);
  }
}
