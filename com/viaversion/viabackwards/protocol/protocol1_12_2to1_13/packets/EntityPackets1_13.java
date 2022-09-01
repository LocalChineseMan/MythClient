package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityPositionHandler;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.EntityTypeMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.PaintingMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.ParticleMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.PlayerPositionStorage1_13;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_12;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_12;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.Optional;

public class EntityPackets1_13 extends LegacyEntityRewriter<Protocol1_12_2To1_13> {
  public EntityPackets1_13(Protocol1_12_2To1_13 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.PLAYER_POSITION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (!ViaBackwards.getConfig().isFix1_13FacePlayer())
                      return; 
                    PlayerPositionStorage1_13 playerStorage = (PlayerPositionStorage1_13)wrapper.user().get(PlayerPositionStorage1_13.class);
                    byte bitField = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    playerStorage.setX(toSet(bitField, 0, playerStorage.getX(), ((Double)wrapper.get((Type)Type.DOUBLE, 0)).doubleValue()));
                    playerStorage.setY(toSet(bitField, 1, playerStorage.getY(), ((Double)wrapper.get((Type)Type.DOUBLE, 1)).doubleValue()));
                    playerStorage.setZ(toSet(bitField, 2, playerStorage.getZ(), ((Double)wrapper.get((Type)Type.DOUBLE, 2)).doubleValue()));
                  }
                  
                  private double toSet(int field, int bitIndex, double origin, double packetValue) {
                    return ((field & 1 << bitIndex) != 0) ? (origin + packetValue) : packetValue;
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
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
            handler(EntityPackets1_13.this.getObjectTrackerHandler());
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Optional<Entity1_13Types.ObjectType> optionalType = Entity1_13Types.ObjectType.findById(((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue());
                    if (!optionalType.isPresent())
                      return; 
                    Entity1_13Types.ObjectType type = optionalType.get();
                    if (type == Entity1_13Types.ObjectType.FALLING_BLOCK) {
                      int blockState = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int combined = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(blockState);
                      combined = combined >> 4 & 0xFFF | (combined & 0xF) << 12;
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(combined));
                    } else if (type == Entity1_13Types.ObjectType.ITEM_FRAME) {
                      int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      switch (data) {
                        case 3:
                          data = 0;
                          break;
                        case 4:
                          data = 1;
                          break;
                        case 5:
                          data = 3;
                          break;
                      } 
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(data));
                    } else if (type == Entity1_13Types.ObjectType.TRIDENT) {
                      wrapper.set((Type)Type.BYTE, 0, Byte.valueOf((byte)Entity1_13Types.ObjectType.TIPPED_ARROW.getId()));
                    } 
                  }
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_13.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_13Types.EntityType.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_13.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_13Types.EntityType.LIGHTNING_BOLT);
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    Entity1_13Types.EntityType entityType = Entity1_13Types.getTypeFromId(type, false);
                    EntityPackets1_13.this.tracker(wrapper.user()).addEntity(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), (EntityType)entityType);
                    int oldId = EntityTypeMapping.getOldId(type);
                    if (oldId == -1) {
                      if (!EntityPackets1_13.this.hasData((EntityType)entityType))
                        ViaBackwards.getPlatform().getLogger().warning("Could not find 1.12 entity type for 1.13 entity type " + type + "/" + entityType); 
                    } else {
                      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(oldId));
                    } 
                  }
                });
            handler(EntityPackets1_13.this.getMobSpawnRewriter(Types1_12.METADATA_LIST));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
            handler(EntityPackets1_13.this.getTrackerAndMetaHandler(Types1_12.METADATA_LIST, (EntityType)Entity1_13Types.EntityType.PLAYER));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            handler(EntityPackets1_13.this.getTrackerHandler((EntityType)Entity1_13Types.EntityType.PAINTING, (Type)Type.VAR_INT));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int motive = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    String title = PaintingMapping.getStringId(motive);
                    wrapper.write(Type.STRING, title);
                  }
                });
          }
        });
    registerJoinGame((ClientboundPacketType)ClientboundPackets1_13.JOIN_GAME, (EntityType)Entity1_13Types.EntityType.PLAYER);
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(EntityPackets1_13.this.getDimensionHandler(0));
            handler(wrapper -> ((BackwardsBlockStorage)wrapper.user().get(BackwardsBlockStorage.class)).clear());
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.FACE_PLAYER, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.cancel();
                    if (!ViaBackwards.getConfig().isFix1_13FacePlayer())
                      return; 
                    int anchor = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    double x = ((Double)wrapper.read((Type)Type.DOUBLE)).doubleValue();
                    double y = ((Double)wrapper.read((Type)Type.DOUBLE)).doubleValue();
                    double z = ((Double)wrapper.read((Type)Type.DOUBLE)).doubleValue();
                    PlayerPositionStorage1_13 positionStorage = (PlayerPositionStorage1_13)wrapper.user().get(PlayerPositionStorage1_13.class);
                    PacketWrapper positionAndLook = wrapper.create((PacketType)ClientboundPackets1_12_1.PLAYER_POSITION);
                    positionAndLook.write((Type)Type.DOUBLE, Double.valueOf(0.0D));
                    positionAndLook.write((Type)Type.DOUBLE, Double.valueOf(0.0D));
                    positionAndLook.write((Type)Type.DOUBLE, Double.valueOf(0.0D));
                    EntityPositionHandler.writeFacingDegrees(positionAndLook, positionStorage.getX(), 
                        (anchor == 1) ? (positionStorage.getY() + 1.62D) : positionStorage.getY(), positionStorage
                        .getZ(), x, y, z);
                    positionAndLook.write((Type)Type.BYTE, Byte.valueOf((byte)7));
                    positionAndLook.write((Type)Type.VAR_INT, Integer.valueOf(-1));
                    positionAndLook.send(Protocol1_12_2To1_13.class);
                  }
                });
          }
        });
    if (ViaBackwards.getConfig().isFix1_13FacePlayer()) {
      Object object = new Object(this);
      ((Protocol1_12_2To1_13)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.PLAYER_POSITION, (PacketRemapper)object);
      ((Protocol1_12_2To1_13)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.PLAYER_POSITION_AND_ROTATION, (PacketRemapper)object);
      ((Protocol1_12_2To1_13)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.VEHICLE_MOVE, (PacketRemapper)object);
    } 
  }
  
  protected void registerRewrites() {
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.DROWNED, (EntityType)Entity1_13Types.EntityType.ZOMBIE_VILLAGER).mobName("Drowned");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.COD, (EntityType)Entity1_13Types.EntityType.SQUID).mobName("Cod");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.SALMON, (EntityType)Entity1_13Types.EntityType.SQUID).mobName("Salmon");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.PUFFERFISH, (EntityType)Entity1_13Types.EntityType.SQUID).mobName("Puffer Fish");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.TROPICAL_FISH, (EntityType)Entity1_13Types.EntityType.SQUID).mobName("Tropical Fish");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.PHANTOM, (EntityType)Entity1_13Types.EntityType.PARROT).mobName("Phantom").spawnMetadata(storage -> storage.add(new Metadata(15, (MetaType)MetaType1_12.VarInt, Integer.valueOf(3))));
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.DOLPHIN, (EntityType)Entity1_13Types.EntityType.SQUID).mobName("Dolphin");
    mapEntityTypeWithData((EntityType)Entity1_13Types.EntityType.TURTLE, (EntityType)Entity1_13Types.EntityType.OCELOT).mobName("Turtle");
    filter().handler((event, meta) -> {
          int typeId = meta.metaType().typeId();
          if (typeId == 5) {
            meta.setTypeAndValue((MetaType)MetaType1_12.String, (meta.getValue() != null) ? meta.getValue().toString() : "");
          } else if (typeId == 6) {
            Item item = (Item)meta.getValue();
            meta.setTypeAndValue((MetaType)MetaType1_12.Slot, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToClient(item));
          } else if (typeId == 15) {
            event.cancel();
          } else if (typeId > 5) {
            meta.setMetaType((MetaType)MetaType1_12.byId(typeId - 1));
          } 
        });
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.ENTITY).index(2).handler((event, meta) -> {
          String value = meta.getValue().toString();
          if (!value.isEmpty())
            meta.setValue(ChatRewriter.jsonToLegacyText(value)); 
        });
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.ZOMBIE).removeIndex(15);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(13);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(14);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(15);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(16);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(17);
    filter().type((EntityType)Entity1_13Types.EntityType.TURTLE).cancel(18);
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.ABSTRACT_FISHES).cancel(12);
    filter().filterFamily((EntityType)Entity1_13Types.EntityType.ABSTRACT_FISHES).cancel(13);
    filter().type((EntityType)Entity1_13Types.EntityType.PHANTOM).cancel(12);
    filter().type((EntityType)Entity1_13Types.EntityType.BOAT).cancel(12);
    filter().type((EntityType)Entity1_13Types.EntityType.TRIDENT).cancel(7);
    filter().type((EntityType)Entity1_13Types.EntityType.WOLF).index(17).handler((event, meta) -> meta.setValue(Integer.valueOf(15 - ((Integer)meta.getValue()).intValue())));
    filter().type((EntityType)Entity1_13Types.EntityType.AREA_EFFECT_CLOUD).index(9).handler((event, meta) -> {
          Particle particle = (Particle)meta.getValue();
          ParticleMapping.ParticleData data = ParticleMapping.getMapping(particle.getId());
          int firstArg = 0;
          int secondArg = 0;
          int[] particleArgs = data.rewriteMeta((Protocol1_12_2To1_13)this.protocol, particle.getArguments());
          if (particleArgs != null && particleArgs.length != 0) {
            if (data.getHandler().isBlockHandler() && particleArgs[0] == 0)
              particleArgs[0] = 102; 
            firstArg = particleArgs[0];
            secondArg = (particleArgs.length == 2) ? particleArgs[1] : 0;
          } 
          event.createExtraMeta(new Metadata(9, (MetaType)MetaType1_12.VarInt, Integer.valueOf(data.getHistoryId())));
          event.createExtraMeta(new Metadata(10, (MetaType)MetaType1_12.VarInt, Integer.valueOf(firstArg)));
          event.createExtraMeta(new Metadata(11, (MetaType)MetaType1_12.VarInt, Integer.valueOf(secondArg)));
          event.cancel();
        });
  }
  
  public EntityType typeFromId(int typeId) {
    return (EntityType)Entity1_13Types.getTypeFromId(typeId, false);
  }
  
  protected EntityType getObjectTypeFromId(int typeId) {
    return (EntityType)Entity1_13Types.getTypeFromId(typeId, true);
  }
  
  public int newEntityId(int newId) {
    return EntityTypeMapping.getOldId(newId);
  }
}
