package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viabackwards.api.rewriters.LegacyEntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.PotionSplashHandler;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.ChestedHorseStorage;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.ObjectType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityPackets1_11 extends LegacyEntityRewriter<Protocol1_10To1_11> {
  public EntityPackets1_11(Protocol1_10To1_11 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            handler(wrapper -> {
                  int type = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  if (type == 2002 || type == 2007) {
                    if (type == 2007)
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(2002)); 
                    int mappedData = PotionSplashHandler.getOldData(((Integer)wrapper.get((Type)Type.INT, 1)).intValue());
                    if (mappedData != -1)
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(mappedData)); 
                  } 
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
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
            handler(EntityPackets1_11.this.getObjectTrackerHandler());
            handler(EntityPackets1_11.this.getObjectRewriter(id -> (ObjectType)Entity1_11Types.ObjectType.findById(id.byteValue()).orElse(null)));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Optional<Entity1_12Types.ObjectType> type = Entity1_12Types.ObjectType.findById(((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue());
                    if (type.isPresent() && type.get() == Entity1_12Types.ObjectType.FALLING_BLOCK) {
                      int objectData = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int objType = objectData & 0xFFF;
                      int data = objectData >> 12 & 0xF;
                      Block block = ((Protocol1_10To1_11)EntityPackets1_11.this.protocol).getItemRewriter().handleBlock(objType, data);
                      if (block == null)
                        return; 
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(block.getId() | block.getData() << 12));
                    } 
                  }
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_11Types.EntityType.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_GLOBAL_ENTITY, (EntityType)Entity1_11Types.EntityType.WEATHER);
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT, (Type)Type.UNSIGNED_BYTE);
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
            handler(EntityPackets1_11.this.getTrackerHandler((Type)Type.UNSIGNED_BYTE, 0));
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityType type = EntityPackets1_11.this.tracker(wrapper.user()).entityType(entityId);
                  List<Metadata> list = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                  EntityPackets1_11.this.handleMetadata(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), list, wrapper.user());
                  EntityData entityData = EntityPackets1_11.this.entityDataForType(type);
                  if (entityData != null) {
                    wrapper.set((Type)Type.UNSIGNED_BYTE, 0, Short.valueOf((short)entityData.replacementId()));
                    if (entityData.hasBaseMeta())
                      entityData.defaultMeta().createMeta(new WrappedMetadata(list)); 
                  } 
                  if (list.isEmpty())
                    list.add(new Metadata(0, (MetaType)MetaType1_9.Byte, Byte.valueOf((byte)0))); 
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PAINTING, (EntityType)Entity1_11Types.EntityType.PAINTING);
    registerJoinGame((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, (EntityType)Entity1_11Types.EntityType.PLAYER);
    registerRespawn((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN);
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_9.METADATA_LIST);
            handler(EntityPackets1_11.this.getTrackerAndMetaHandler(Types1_9.METADATA_LIST, (EntityType)Entity1_11Types.EntityType.PLAYER));
            handler(wrapper -> {
                  List<Metadata> metadata = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                  if (metadata.isEmpty())
                    metadata.add(new Metadata(0, (MetaType)MetaType1_9.Byte, Byte.valueOf((byte)0))); 
                });
          }
        });
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_STATUS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    byte b = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    if (b == 35) {
                      wrapper.clearPacket();
                      wrapper.setId(30);
                      wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)10));
                      wrapper.write((Type)Type.FLOAT, Float.valueOf(0.0F));
                    } 
                  }
                });
          }
        });
  }
  
  protected void registerRewrites() {
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.ELDER_GUARDIAN, (EntityType)Entity1_11Types.EntityType.GUARDIAN);
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.WITHER_SKELETON, (EntityType)Entity1_11Types.EntityType.SKELETON).mobName("Wither Skeleton").spawnMetadata(storage -> storage.add(getSkeletonTypeMeta(1)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.STRAY, (EntityType)Entity1_11Types.EntityType.SKELETON).mobName("Stray").spawnMetadata(storage -> storage.add(getSkeletonTypeMeta(2)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.HUSK, (EntityType)Entity1_11Types.EntityType.ZOMBIE).mobName("Husk").spawnMetadata(storage -> handleZombieType(storage, 6));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.ZOMBIE_VILLAGER, (EntityType)Entity1_11Types.EntityType.ZOMBIE).spawnMetadata(storage -> handleZombieType(storage, 1));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.HORSE, (EntityType)Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(getHorseMetaType(0)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.DONKEY, (EntityType)Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(getHorseMetaType(1)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.MULE, (EntityType)Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(getHorseMetaType(2)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.SKELETON_HORSE, (EntityType)Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(getHorseMetaType(4)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.ZOMBIE_HORSE, (EntityType)Entity1_11Types.EntityType.HORSE).spawnMetadata(storage -> storage.add(getHorseMetaType(3)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.EVOCATION_FANGS, (EntityType)Entity1_11Types.EntityType.SHULKER);
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.EVOCATION_ILLAGER, (EntityType)Entity1_11Types.EntityType.VILLAGER).mobName("Evoker");
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.VEX, (EntityType)Entity1_11Types.EntityType.BAT).mobName("Vex");
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.VINDICATION_ILLAGER, (EntityType)Entity1_11Types.EntityType.VILLAGER).mobName("Vindicator").spawnMetadata(storage -> storage.add(new Metadata(13, (MetaType)MetaType1_9.VarInt, Integer.valueOf(4))));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.LIAMA, (EntityType)Entity1_11Types.EntityType.HORSE).mobName("Llama").spawnMetadata(storage -> storage.add(getHorseMetaType(1)));
    mapEntityTypeWithData((EntityType)Entity1_11Types.EntityType.LIAMA_SPIT, (EntityType)Entity1_11Types.EntityType.SNOWBALL);
    mapObjectType((ObjectType)Entity1_11Types.ObjectType.LIAMA_SPIT, (ObjectType)Entity1_11Types.ObjectType.SNOWBALL, -1);
    mapObjectType((ObjectType)Entity1_11Types.ObjectType.EVOCATION_FANGS, (ObjectType)Entity1_11Types.ObjectType.FALLING_BLOCK, 4294);
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.GUARDIAN).index(12).handler((event, meta) -> {
          boolean b = ((Boolean)meta.getValue()).booleanValue();
          int bitmask = b ? 2 : 0;
          if (event.entityType() == Entity1_11Types.EntityType.ELDER_GUARDIAN)
            bitmask |= 0x4; 
          meta.setTypeAndValue((MetaType)MetaType1_9.Byte, Byte.valueOf((byte)bitmask));
        });
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.ABSTRACT_SKELETON).index(12).toIndex(13);
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.ZOMBIE).handler((event, meta) -> {
          switch (meta.id()) {
            case 13:
              event.cancel();
              return;
            case 14:
              event.setIndex(15);
              break;
            case 15:
              event.setIndex(14);
              break;
            case 16:
              event.setIndex(13);
              meta.setValue(Integer.valueOf(1 + ((Integer)meta.getValue()).intValue()));
              break;
          } 
        });
    filter().type((EntityType)Entity1_11Types.EntityType.EVOCATION_ILLAGER).index(12).handler((event, meta) -> {
          event.setIndex(13);
          meta.setTypeAndValue((MetaType)MetaType1_9.VarInt, Integer.valueOf(((Byte)meta.getValue()).intValue()));
        });
    filter().type((EntityType)Entity1_11Types.EntityType.VEX).index(12).handler((event, meta) -> meta.setValue(Byte.valueOf((byte)0)));
    filter().type((EntityType)Entity1_11Types.EntityType.VINDICATION_ILLAGER).index(12).handler((event, meta) -> {
          event.setIndex(13);
          meta.setTypeAndValue((MetaType)MetaType1_9.VarInt, Integer.valueOf((((Number)meta.getValue()).intValue() == 1) ? 2 : 4));
        });
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.ABSTRACT_HORSE).index(13).handler((event, meta) -> {
          StoredEntityData data = storedEntityData(event);
          byte b = ((Byte)meta.getValue()).byteValue();
          if (data.has(ChestedHorseStorage.class) && ((ChestedHorseStorage)data.get(ChestedHorseStorage.class)).isChested()) {
            b = (byte)(b | 0x8);
            meta.setValue(Byte.valueOf(b));
          } 
        });
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.CHESTED_HORSE).handler((event, meta) -> {
          StoredEntityData data = storedEntityData(event);
          if (!data.has(ChestedHorseStorage.class))
            data.put(new ChestedHorseStorage()); 
        });
    filter().type((EntityType)Entity1_11Types.EntityType.HORSE).index(16).toIndex(17);
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.CHESTED_HORSE).index(15).handler((event, meta) -> {
          StoredEntityData data = storedEntityData(event);
          ChestedHorseStorage storage = (ChestedHorseStorage)data.get(ChestedHorseStorage.class);
          boolean b = ((Boolean)meta.getValue()).booleanValue();
          storage.setChested(b);
          event.cancel();
        });
    filter().type((EntityType)Entity1_11Types.EntityType.LIAMA).handler((event, meta) -> {
          StoredEntityData data = storedEntityData(event);
          ChestedHorseStorage storage = (ChestedHorseStorage)data.get(ChestedHorseStorage.class);
          int index = event.index();
          switch (index) {
            case 16:
              storage.setLiamaStrength(((Integer)meta.getValue()).intValue());
              event.cancel();
              break;
            case 17:
              storage.setLiamaCarpetColor(((Integer)meta.getValue()).intValue());
              event.cancel();
              break;
            case 18:
              storage.setLiamaVariant(((Integer)meta.getValue()).intValue());
              event.cancel();
              break;
          } 
        });
    filter().filterFamily((EntityType)Entity1_11Types.EntityType.ABSTRACT_HORSE).index(14).toIndex(16);
    filter().type((EntityType)Entity1_11Types.EntityType.VILLAGER).index(13).handler((event, meta) -> {
          if (((Integer)meta.getValue()).intValue() == 5)
            meta.setValue(Integer.valueOf(0)); 
        });
    filter().type((EntityType)Entity1_11Types.EntityType.SHULKER).cancel(15);
  }
  
  private Metadata getSkeletonTypeMeta(int type) {
    return new Metadata(12, (MetaType)MetaType1_9.VarInt, Integer.valueOf(type));
  }
  
  private Metadata getZombieTypeMeta(int type) {
    return new Metadata(13, (MetaType)MetaType1_9.VarInt, Integer.valueOf(type));
  }
  
  private void handleZombieType(WrappedMetadata storage, int type) {
    Metadata meta = storage.get(13);
    if (meta == null)
      storage.add(getZombieTypeMeta(type)); 
  }
  
  private Metadata getHorseMetaType(int type) {
    return new Metadata(14, (MetaType)MetaType1_9.VarInt, Integer.valueOf(type));
  }
  
  public EntityType typeFromId(int typeId) {
    return (EntityType)Entity1_11Types.getTypeFromId(typeId, false);
  }
  
  protected EntityType getObjectTypeFromId(int typeId) {
    return (EntityType)Entity1_11Types.getTypeFromId(typeId, true);
  }
}
