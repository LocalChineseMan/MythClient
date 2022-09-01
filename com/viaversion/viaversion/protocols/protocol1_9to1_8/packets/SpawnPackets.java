package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import java.util.List;

public class SpawnPackets {
  public static final ValueTransformer<Integer, Double> toNewDouble = new ValueTransformer<Integer, Double>((Type)Type.DOUBLE) {
      public Double transform(PacketWrapper wrapper, Integer inputValue) {
        return Double.valueOf(inputValue.intValue() / 32.0D);
      }
    };
  
  public static void register(final Protocol1_9To1_8 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeID = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.getTypeFromId(typeID, true));
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    short vX = 0, vY = 0, vZ = 0;
                    if (data > 0) {
                      vX = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                      vY = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                      vZ = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                    } 
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vX));
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vY));
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vZ));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int typeID = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    if (Entity1_10Types.getTypeFromId(typeID, true) == Entity1_10Types.EntityType.SPLASH_POTION) {
                      PacketWrapper metaPacket = wrapper.create(57, (PacketHandler)new Object(this, entityID, data));
                      metaPacket.scheduleSend(Protocol1_9To1_8.class);
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.SHORT);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.LIGHTNING);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeID = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.getTypeFromId(typeID, false));
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (tracker.hasEntity(entityId)) {
                      ((MetadataRewriter1_9To1_8)protocol.get(MetadataRewriter1_9To1_8.class)).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                      Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                      metadataList.clear();
                    } 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.handleMetadata(entityID, metadataList);
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.PAINTING);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map(Type.STRING);
            map(Type.POSITION);
            map((Type)Type.BYTE);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.PLAYER);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short item = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                    if (item != 0) {
                      PacketWrapper packet = PacketWrapper.create((PacketType)ClientboundPackets1_9.ENTITY_EQUIPMENT, null, wrapper.user());
                      packet.write((Type)Type.VAR_INT, wrapper.get((Type)Type.VAR_INT, 0));
                      packet.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      packet.write(Type.ITEM, new DataItem(item, (byte)1, (short)0, null));
                      try {
                        packet.send(Protocol1_9To1_8.class);
                      } catch (Exception e) {
                        e.printStackTrace();
                      } 
                    } 
                  }
                });
            map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (tracker.hasEntity(entityId)) {
                      ((MetadataRewriter1_9To1_8)protocol.get(MetadataRewriter1_9To1_8.class)).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                      Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                      metadataList.clear();
                    } 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.handleMetadata(entityID, metadataList);
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.DESTROY_ENTITIES, new PacketRemapper() {
          public void registerMap() {
            map(Type.VAR_INT_ARRAY_PRIMITIVE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int[] entities = (int[])wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0);
                    for (int entity : entities)
                      wrapper.user().getEntityTracker(Protocol1_9To1_8.class).removeEntity(entity); 
                  }
                });
          }
        });
  }
}
