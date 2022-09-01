package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.EntityPositionHandler;
import com.viaversion.viabackwards.api.rewriters.EntityRewriterBase;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.EntityPositionStorage1_14;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13_2;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.meta.MetaHandler;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.function.Supplier;

public class EntityPackets1_14 extends LegacyEntityRewriter<Protocol1_13_2To1_14> {
  private EntityPositionHandler positionHandler;
  
  public EntityPackets1_14(Protocol1_13_2To1_14 protocol) {
    super((BackwardsProtocol)protocol, (MetaType)MetaType1_13_2.OptChat, (MetaType)MetaType1_13_2.Boolean);
  }
  
  protected void addTrackedEntity(PacketWrapper wrapper, int entityId, EntityType type) throws Exception {
    super.addTrackedEntity(wrapper, entityId, type);
    if (type == Entity1_14Types.PAINTING) {
      Position position = (Position)wrapper.get(Type.POSITION, 0);
      this.positionHandler.cacheEntityPosition(wrapper, position.getX(), position.getY(), position.getZ(), true, false);
    } else if (wrapper.getId() != ClientboundPackets1_14.JOIN_GAME.getId()) {
      this.positionHandler.cacheEntityPosition(wrapper, true, false);
    } 
  }
  
  protected void registerPackets() {
    this.positionHandler = new EntityPositionHandler((EntityRewriterBase)this, EntityPositionStorage1_14.class, EntityPositionStorage1_14::new);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_STATUS, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                  byte status = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
                  if (status != 3)
                    return; 
                  EntityTracker tracker = EntityPackets1_14.this.tracker(wrapper.user());
                  EntityType entityType = tracker.entityType(entityId);
                  if (entityType != Entity1_14Types.PLAYER)
                    return; 
                  for (int i = 0; i <= 5; i++) {
                    PacketWrapper equipmentPacket = wrapper.create((PacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT);
                    equipmentPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                    equipmentPacket.write((Type)Type.VAR_INT, Integer.valueOf(i));
                    equipmentPacket.write(Type.FLAT_VAR_INT_ITEM, null);
                    equipmentPacket.send(Protocol1_13_2To1_14.class);
                  } 
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_TELEPORT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            handler(wrapper -> EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, false, false));
          }
        });
    PacketRemapper relativeMoveHandler = new PacketRemapper() {
        public void registerMap() {
          map((Type)Type.VAR_INT);
          map((Type)Type.SHORT);
          map((Type)Type.SHORT);
          map((Type)Type.SHORT);
          handler(new PacketHandler() {
                public void handle(PacketWrapper wrapper) throws Exception {
                  double x = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue() / 4096.0D;
                  double y = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue() / 4096.0D;
                  double z = ((Short)wrapper.get((Type)Type.SHORT, 2)).shortValue() / 4096.0D;
                  EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, x, y, z, false, true);
                }
              });
        }
      };
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_POSITION, relativeMoveHandler);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_POSITION_AND_ROTATION, relativeMoveHandler);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT, (Type)Type.BYTE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(EntityPackets1_14.this.getObjectTrackerHandler());
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Entity1_13Types.ObjectType objectType;
                    int id = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    int mappedId = EntityPackets1_14.this.newEntityId(id);
                    Entity1_13Types.EntityType entityType = Entity1_13Types.getTypeFromId(mappedId, false);
                    if (entityType.isOrHasParent((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT)) {
                      objectType = Entity1_13Types.ObjectType.MINECART;
                      int i = 0;
                      switch (EntityPackets1_14.null.$SwitchMap$com$viaversion$viaversion$api$minecraft$entities$Entity1_13Types$EntityType[entityType.ordinal()]) {
                        case 1:
                          i = 1;
                          break;
                        case 2:
                          i = 2;
                          break;
                        case 3:
                          i = 3;
                          break;
                        case 4:
                          i = 4;
                          break;
                        case 5:
                          i = 5;
                          break;
                        case 6:
                          i = 6;
                          break;
                      } 
                      if (i != 0)
                        wrapper.set((Type)Type.INT, 0, Integer.valueOf(i)); 
                    } else {
                      objectType = Entity1_13Types.ObjectType.fromEntityType(entityType).orElse(null);
                    } 
                    if (objectType == null)
                      return; 
                    wrapper.set((Type)Type.BYTE, 0, Byte.valueOf((byte)objectType.getId()));
                    int data = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    if (objectType == Entity1_13Types.ObjectType.FALLING_BLOCK) {
                      int blockState = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int combined = ((Protocol1_13_2To1_14)EntityPackets1_14.this.protocol).getMappingData().getNewBlockStateId(blockState);
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(combined));
                    } else if (entityType.isOrHasParent((EntityType)Entity1_13Types.EntityType.ABSTRACT_ARROW)) {
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(data + 1));
                    } 
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    EntityType entityType = Entity1_14Types.getTypeFromId(type);
                    EntityPackets1_14.this.addTrackedEntity(wrapper, ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), entityType);
                    int oldId = EntityPackets1_14.this.newEntityId(type);
                    if (oldId == -1) {
                      EntityData entityData = EntityPackets1_14.this.entityDataForType(entityType);
                      if (entityData == null) {
                        ViaBackwards.getPlatform().getLogger().warning("Could not find 1.13.2 entity type for 1.14 entity type " + type + "/" + entityType);
                        wrapper.cancel();
                      } else {
                        wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(entityData.replacementId()));
                      } 
                    } else {
                      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(oldId));
                    } 
                  }
                });
            handler(EntityPackets1_14.this.getMobSpawnRewriter(Types1_13_2.METADATA_LIST));
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), (EntityType)Entity1_14Types.EXPERIENCE_ORB));
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), (EntityType)Entity1_14Types.LIGHTNING_BOLT));
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map(Type.POSITION1_14, Type.POSITION);
            map((Type)Type.BYTE);
            handler(wrapper -> EntityPackets1_14.this.addTrackedEntity(wrapper, ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), (EntityType)Entity1_14Types.PAINTING));
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
            handler(EntityPackets1_14.this.getTrackerAndMetaHandler(Types1_13_2.METADATA_LIST, (EntityType)Entity1_14Types.PLAYER));
            handler(wrapper -> EntityPackets1_14.this.positionHandler.cacheEntityPosition(wrapper, true, false));
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_14.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST, Types1_13_2.METADATA_LIST);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(EntityPackets1_14.this.getTrackerHandler((EntityType)Entity1_14Types.PLAYER, (Type)Type.INT));
            handler(EntityPackets1_14.this.getDimensionHandler(1));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                    wrapper.passthrough(Type.STRING);
                    wrapper.read((Type)Type.VAR_INT);
                    int entitiyId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    StoredEntityData storedEntity = ((Protocol1_13_2To1_14)EntityPackets1_14.this.protocol).getEntityRewriter().tracker(wrapper.user()).entityData(entitiyId);
                    storedEntity.put(new EntityPositionStorage1_14());
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).clear();
                  }
                });
          }
        });
  }
  
  protected void registerRewrites() {
    mapTypes((EntityType[])Entity1_14Types.values(), Entity1_13Types.EntityType.class);
    mapEntityTypeWithData((EntityType)Entity1_14Types.CAT, (EntityType)Entity1_14Types.OCELOT).jsonName("Cat");
    mapEntityTypeWithData((EntityType)Entity1_14Types.TRADER_LLAMA, (EntityType)Entity1_14Types.LLAMA).jsonName("Trader Llama");
    mapEntityTypeWithData((EntityType)Entity1_14Types.FOX, (EntityType)Entity1_14Types.WOLF).jsonName("Fox");
    mapEntityTypeWithData((EntityType)Entity1_14Types.PANDA, (EntityType)Entity1_14Types.POLAR_BEAR).jsonName("Panda");
    mapEntityTypeWithData((EntityType)Entity1_14Types.PILLAGER, (EntityType)Entity1_14Types.VILLAGER).jsonName("Pillager");
    mapEntityTypeWithData((EntityType)Entity1_14Types.WANDERING_TRADER, (EntityType)Entity1_14Types.VILLAGER).jsonName("Wandering Trader");
    mapEntityTypeWithData((EntityType)Entity1_14Types.RAVAGER, (EntityType)Entity1_14Types.COW).jsonName("Ravager");
    filter().handler((event, meta) -> {
          int typeId = meta.metaType().typeId();
          if (typeId <= 15)
            meta.setMetaType((MetaType)MetaType1_13_2.byId(typeId)); 
          MetaType type = meta.metaType();
          if (type == MetaType1_13_2.Slot) {
            Item item = (Item)meta.getValue();
            meta.setValue(((Protocol1_13_2To1_14)this.protocol).getItemRewriter().handleItemToClient(item));
          } else if (type == MetaType1_13_2.BlockID) {
            int blockstate = ((Integer)meta.getValue()).intValue();
            meta.setValue(Integer.valueOf(((Protocol1_13_2To1_14)this.protocol).getMappingData().getNewBlockStateId(blockstate)));
          } 
        });
    filter().type((EntityType)Entity1_14Types.PILLAGER).cancel(15);
    filter().type((EntityType)Entity1_14Types.FOX).cancel(15);
    filter().type((EntityType)Entity1_14Types.FOX).cancel(16);
    filter().type((EntityType)Entity1_14Types.FOX).cancel(17);
    filter().type((EntityType)Entity1_14Types.FOX).cancel(18);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(15);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(16);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(17);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(18);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(19);
    filter().type((EntityType)Entity1_14Types.PANDA).cancel(20);
    filter().type((EntityType)Entity1_14Types.CAT).cancel(18);
    filter().type((EntityType)Entity1_14Types.CAT).cancel(19);
    filter().type((EntityType)Entity1_14Types.CAT).cancel(20);
    filter().handler((event, meta) -> {
          EntityType type = event.entityType();
          if (type == null)
            return; 
          if (type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ILLAGER_BASE) || type == Entity1_14Types.RAVAGER || type == Entity1_14Types.WITCH) {
            int index = event.index();
            if (index == 14) {
              event.cancel();
            } else if (index > 14) {
              event.setIndex(index - 1);
            } 
          } 
        });
    filter().type((EntityType)Entity1_14Types.AREA_EFFECT_CLOUD).index(10).handler((event, meta) -> rewriteParticle((Particle)meta.getValue()));
    filter().type((EntityType)Entity1_14Types.FIREWORK_ROCKET).index(8).handler((event, meta) -> {
          meta.setMetaType((MetaType)MetaType1_13_2.VarInt);
          Integer value = (Integer)meta.getValue();
          if (value == null)
            meta.setValue(Integer.valueOf(0)); 
        });
    filter().filterFamily((EntityType)Entity1_14Types.ABSTRACT_ARROW).removeIndex(9);
    filter().type((EntityType)Entity1_14Types.VILLAGER).cancel(15);
    MetaHandler villagerDataHandler = (event, meta) -> {
        VillagerData villagerData = (VillagerData)meta.getValue();
        meta.setTypeAndValue((MetaType)MetaType1_13_2.VarInt, Integer.valueOf(villagerDataToProfession(villagerData)));
        if (meta.id() == 16)
          event.setIndex(15); 
      };
    filter().type((EntityType)Entity1_14Types.ZOMBIE_VILLAGER).index(18).handler(villagerDataHandler);
    filter().type((EntityType)Entity1_14Types.VILLAGER).index(16).handler(villagerDataHandler);
    filter().filterFamily((EntityType)Entity1_14Types.ABSTRACT_SKELETON).index(13).handler((event, meta) -> {
          byte value = ((Byte)meta.getValue()).byteValue();
          if ((value & 0x4) != 0)
            event.createExtraMeta(new Metadata(14, (MetaType)MetaType1_13_2.Boolean, Boolean.valueOf(true))); 
        });
    filter().filterFamily((EntityType)Entity1_14Types.ZOMBIE).index(13).handler((event, meta) -> {
          byte value = ((Byte)meta.getValue()).byteValue();
          if ((value & 0x4) != 0)
            event.createExtraMeta(new Metadata(16, (MetaType)MetaType1_13_2.Boolean, Boolean.valueOf(true))); 
        });
    filter().filterFamily((EntityType)Entity1_14Types.ZOMBIE).addIndex(16);
    filter().filterFamily((EntityType)Entity1_14Types.LIVINGENTITY).handler((event, meta) -> {
          int index = event.index();
          if (index == 12) {
            Position position = (Position)meta.getValue();
            if (position != null) {
              PacketWrapper wrapper = PacketWrapper.create((PacketType)ClientboundPackets1_13.USE_BED, null, event.user());
              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(event.entityId()));
              wrapper.write(Type.POSITION, position);
              try {
                wrapper.scheduleSend(Protocol1_13_2To1_14.class);
              } catch (Exception ex) {
                ex.printStackTrace();
              } 
            } 
            event.cancel();
          } else if (index > 12) {
            event.setIndex(index - 1);
          } 
        });
    filter().removeIndex(6);
    filter().type((EntityType)Entity1_14Types.OCELOT).index(13).handler((event, meta) -> {
          event.setIndex(15);
          meta.setTypeAndValue((MetaType)MetaType1_13_2.VarInt, Integer.valueOf(0));
        });
    filter().type((EntityType)Entity1_14Types.CAT).handler((event, meta) -> {
          if (event.index() == 15) {
            meta.setValue(Integer.valueOf(1));
          } else if (event.index() == 13) {
            meta.setValue(Byte.valueOf((byte)(((Byte)meta.getValue()).byteValue() & 0x4)));
          } 
        });
  }
  
  public int villagerDataToProfession(VillagerData data) {
    switch (data.getProfession()) {
      case 1:
      case 10:
      case 13:
      case 14:
        return 3;
      case 2:
      case 8:
        return 4;
      case 3:
      case 9:
        return 1;
      case 4:
        return 2;
      case 5:
      case 6:
      case 7:
      case 12:
        return 0;
    } 
    return 5;
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_14Types.getTypeFromId(typeId);
  }
}
