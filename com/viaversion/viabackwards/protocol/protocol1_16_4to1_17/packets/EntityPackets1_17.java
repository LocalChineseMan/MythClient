package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_16;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.api.type.types.version.Types1_17;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;

public final class EntityPackets1_17 extends EntityRewriter<Protocol1_16_4To1_17> {
  public EntityPackets1_17(Protocol1_16_4To1_17 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_17.SPAWN_ENTITY, (EntityType)Entity1_17Types.FALLING_BLOCK);
    registerSpawnTracker((ClientboundPacketType)ClientboundPackets1_17.SPAWN_MOB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_17.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_17Types.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_17.SPAWN_PAINTING, (EntityType)Entity1_17Types.PAINTING);
    registerTracker((ClientboundPacketType)ClientboundPackets1_17.SPAWN_PLAYER, (EntityType)Entity1_17Types.PLAYER);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_17.ENTITY_METADATA, Types1_17.METADATA_LIST, Types1_16.METADATA_LIST);
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.REMOVE_ENTITY, (ClientboundPacketType)ClientboundPackets1_16_2.DESTROY_ENTITIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  EntityPackets1_17.this.tracker(wrapper.user()).removeEntity(entityId);
                  int[] array = { entityId };
                  wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, array);
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.BYTE);
            map(Type.STRING_ARRAY);
            map(Type.NBT);
            map(Type.NBT);
            handler(wrapper -> {
                  byte previousGamemode = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                  if (previousGamemode == -1)
                    wrapper.set((Type)Type.BYTE, 0, Byte.valueOf((byte)0)); 
                });
            handler(EntityPackets1_17.this.getTrackerHandler((EntityType)Entity1_17Types.PLAYER, (Type)Type.INT));
            handler(EntityPackets1_17.this.worldDataTrackerHandler(1));
            handler(wrapper -> {
                  CompoundTag registry = (CompoundTag)wrapper.get(Type.NBT, 0);
                  CompoundTag biomeRegsitry = (CompoundTag)registry.get("minecraft:worldgen/biome");
                  ListTag biomes = (ListTag)biomeRegsitry.get("value");
                  for (Tag biome : biomes) {
                    CompoundTag biomeCompound = (CompoundTag)((CompoundTag)biome).get("element");
                    StringTag category = (StringTag)biomeCompound.get("category");
                    if (category.getValue().equalsIgnoreCase("underground"))
                      category.setValue("none"); 
                  } 
                  CompoundTag dimensionRegistry = (CompoundTag)registry.get("minecraft:dimension_type");
                  ListTag dimensions = (ListTag)dimensionRegistry.get("value");
                  for (Tag dimension : dimensions) {
                    CompoundTag dimensionCompound = (CompoundTag)((CompoundTag)dimension).get("element");
                    EntityPackets1_17.this.reduceExtendedHeight(dimensionCompound, false);
                  } 
                  EntityPackets1_17.this.reduceExtendedHeight((CompoundTag)wrapper.get(Type.NBT, 1), true);
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.NBT);
            handler(EntityPackets1_17.this.worldDataTrackerHandler(0));
            handler(wrapper -> EntityPackets1_17.this.reduceExtendedHeight((CompoundTag)wrapper.get(Type.NBT, 0), true));
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.PLAYER_POSITION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.read((Type)Type.BOOLEAN));
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.write((Type)Type.INT, wrapper.read((Type)Type.VAR_INT)));
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_ENTER, ClientboundPackets1_16_2.COMBAT_EVENT, 0);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_END, ClientboundPackets1_16_2.COMBAT_EVENT, 1);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.COMBAT_KILL, ClientboundPackets1_16_2.COMBAT_EVENT, 2);
  }
  
  protected void registerRewrites() {
    filter().handler((event, meta) -> {
          meta.setMetaType((MetaType)MetaType1_16.byId(meta.metaType().typeId()));
          MetaType type = meta.metaType();
          if (type == MetaType1_16.PARTICLE) {
            Particle particle = (Particle)meta.getValue();
            if (particle.getId() == 15) {
              particle.getArguments().subList(4, 7).clear();
            } else if (particle.getId() == 36) {
              particle.setId(0);
              particle.getArguments().clear();
              return;
            } 
            rewriteParticle(particle);
          } else if (type == MetaType1_16.POSE) {
            int pose = ((Integer)meta.value()).intValue();
            if (pose == 6) {
              meta.setValue(Integer.valueOf(1));
            } else if (pose > 6) {
              meta.setValue(Integer.valueOf(pose - 1));
            } 
          } 
        });
    registerMetaTypeHandler((MetaType)MetaType1_16.ITEM, (MetaType)MetaType1_16.BLOCK_STATE, null, (MetaType)MetaType1_16.OPT_COMPONENT);
    mapTypes((EntityType[])Entity1_17Types.values(), Entity1_16_2Types.class);
    filter().type((EntityType)Entity1_17Types.AXOLOTL).cancel(17);
    filter().type((EntityType)Entity1_17Types.AXOLOTL).cancel(18);
    filter().type((EntityType)Entity1_17Types.AXOLOTL).cancel(19);
    filter().type((EntityType)Entity1_17Types.GLOW_SQUID).cancel(16);
    filter().type((EntityType)Entity1_17Types.GOAT).cancel(17);
    mapEntityTypeWithData((EntityType)Entity1_17Types.AXOLOTL, (EntityType)Entity1_17Types.TROPICAL_FISH).jsonName("Axolotl");
    mapEntityTypeWithData((EntityType)Entity1_17Types.GOAT, (EntityType)Entity1_17Types.SHEEP).jsonName("Goat");
    mapEntityTypeWithData((EntityType)Entity1_17Types.GLOW_SQUID, (EntityType)Entity1_17Types.SQUID).jsonName("Glow Squid");
    mapEntityTypeWithData((EntityType)Entity1_17Types.GLOW_ITEM_FRAME, (EntityType)Entity1_17Types.ITEM_FRAME);
    filter().type((EntityType)Entity1_17Types.SHULKER).addIndex(17);
    filter().removeIndex(7);
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_17Types.getTypeFromId(typeId);
  }
  
  private void reduceExtendedHeight(CompoundTag tag, boolean warn) {
    IntTag minY = (IntTag)tag.get("min_y");
    IntTag height = (IntTag)tag.get("height");
    IntTag logicalHeight = (IntTag)tag.get("logical_height");
    if (minY.asInt() != 0 || height.asInt() > 256 || logicalHeight.asInt() > 256) {
      if (warn) {
        ViaBackwards.getPlatform().getLogger().severe("Custom worlds heights are NOT SUPPORTED for 1.16 players and older and may lead to errors!");
        ViaBackwards.getPlatform().getLogger().severe("You have min/max set to " + minY.asInt() + "/" + height.asInt());
      } 
      height.setValue(Math.min(256, height.asInt()));
      logicalHeight.setValue(Math.min(256, logicalHeight.asInt()));
    } 
  }
}
