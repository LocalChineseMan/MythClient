package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
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
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.metadata.MetadataRewriter1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import java.util.LinkedList;
import java.util.List;

public class EntityPackets {
  public static void register(final Protocol1_14To1_13_2 protocol) {
    final MetadataRewriter1_14To1_13_2 metadataRewriter = (MetadataRewriter1_14To1_13_2)protocol.get(MetadataRewriter1_14To1_13_2.class);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.BYTE, (Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeId = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    Entity1_13Types.EntityType type1_13 = Entity1_13Types.getTypeFromId(typeId, true);
                    typeId = metadataRewriter.newEntityId(type1_13.getId());
                    EntityType type1_14 = Entity1_14Types.getTypeFromId(typeId);
                    if (type1_14 != null) {
                      int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      if (type1_14.is((EntityType)Entity1_14Types.FALLING_BLOCK)) {
                        wrapper.set((Type)Type.INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                      } else if (type1_14.is((EntityType)Entity1_14Types.MINECART)) {
                        switch (data) {
                          case 1:
                            typeId = Entity1_14Types.CHEST_MINECART.getId();
                            break;
                          case 2:
                            typeId = Entity1_14Types.FURNACE_MINECART.getId();
                            break;
                          case 3:
                            typeId = Entity1_14Types.TNT_MINECART.getId();
                            break;
                          case 4:
                            typeId = Entity1_14Types.SPAWNER_MINECART.getId();
                            break;
                          case 5:
                            typeId = Entity1_14Types.HOPPER_MINECART.getId();
                            break;
                          case 6:
                            typeId = Entity1_14Types.COMMAND_BLOCK_MINECART.getId();
                            break;
                        } 
                      } else if ((type1_14.is((EntityType)Entity1_14Types.ITEM) && data > 0) || type1_14
                        .isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ARROW)) {
                        if (type1_14.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ARROW))
                          wrapper.set((Type)Type.INT, 0, Integer.valueOf(data - 1)); 
                        PacketWrapper velocity = wrapper.create(69);
                        velocity.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 0));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 1));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 2));
                        velocity.scheduleSend(Protocol1_14To1_13_2.class);
                      } 
                      wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class).addEntity(entityId, type1_14);
                    } 
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(typeId));
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
            map(Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_14.METADATA_LIST));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.BYTE);
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
            map(Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
            handler(metadataRewriter.trackerAndRewriterHandler(Types1_14.METADATA_LIST, (EntityType)Entity1_14Types.PLAYER));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.ENTITY_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short animation = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                    if (animation == 2) {
                      EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                      int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                      tracker.setSleeping(entityId, false);
                      PacketWrapper metadataPacket = wrapper.create((PacketType)ClientboundPackets1_14.ENTITY_METADATA);
                      metadataPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                      List<Metadata> metadataList = new LinkedList<>();
                      if (tracker.clientEntityId() != entityId)
                        metadataList.add(new Metadata(6, (MetaType)MetaType1_14.Pose, Integer.valueOf(MetadataRewriter1_14To1_13_2.recalculatePlayerPose(entityId, tracker)))); 
                      metadataList.add(new Metadata(12, (MetaType)MetaType1_14.OptPosition, null));
                      metadataPacket.write(Types1_14.METADATA_LIST, metadataList);
                      metadataPacket.scheduleSend(Protocol1_14To1_13_2.class);
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.USE_BED, (ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    tracker.setSleeping(entityId, true);
                    Position position = (Position)wrapper.read(Type.POSITION);
                    List<Metadata> metadataList = new LinkedList<>();
                    metadataList.add(new Metadata(12, (MetaType)MetaType1_14.OptPosition, position));
                    if (tracker.clientEntityId() != entityId)
                      metadataList.add(new Metadata(6, (MetaType)MetaType1_14.Pose, Integer.valueOf(MetadataRewriter1_14To1_13_2.recalculatePlayerPose(entityId, tracker)))); 
                    wrapper.write(Types1_14.METADATA_LIST, metadataList);
                  }
                });
          }
        });
    metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
  }
}
