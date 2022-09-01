package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.metadata.MetadataRewriter1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class EntityPackets {
  public static void register(final Protocol1_13_1To1_13 protocol) {
    final MetadataRewriter1_13_1To1_13 metadataRewriter = (MetadataRewriter1_13_1To1_13)protocol.get(MetadataRewriter1_13_1To1_13.class);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
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
                    if (entType != null) {
                      if (entType.is((EntityType)Entity1_13Types.EntityType.FALLING_BLOCK)) {
                        int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                        wrapper.set((Type)Type.INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                      } 
                      wrapper.user().getEntityTracker(Protocol1_13_1To1_13.class).addEntity(entityId, (EntityType)entType);
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
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
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_13.METADATA_LIST));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_13.METADATA_LIST);
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_13.METADATA_LIST, (EntityType)Entity1_13Types.EntityType.PLAYER));
          }
        });
    metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST);
  }
}
