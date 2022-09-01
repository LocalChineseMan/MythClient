package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.metadata.MetadataRewriter1_16To1_15_2;
import java.util.UUID;

public class EntityPackets {
  private static final PacketHandler DIMENSION_HANDLER;
  
  static {
    DIMENSION_HANDLER = (wrapper -> {
        String dimensionName;
        int dimension = ((Integer)wrapper.read((Type)Type.INT)).intValue();
        switch (dimension) {
          case -1:
            dimensionName = "minecraft:the_nether";
            break;
          case 0:
            dimensionName = "minecraft:overworld";
            break;
          case 1:
            dimensionName = "minecraft:the_end";
            break;
          default:
            Via.getPlatform().getLogger().warning("Invalid dimension id: " + dimension);
            dimensionName = "minecraft:overworld";
            break;
        } 
        wrapper.write(Type.STRING, dimensionName);
        wrapper.write(Type.STRING, dimensionName);
      });
  }
  
  public static final CompoundTag DIMENSIONS_TAG = new CompoundTag();
  
  private static final String[] WORLD_NAMES = new String[] { "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end" };
  
  static {
    ListTag list = new ListTag(CompoundTag.class);
    list.add((Tag)createOverworldEntry());
    list.add((Tag)createOverworldCavesEntry());
    list.add((Tag)createNetherEntry());
    list.add((Tag)createEndEntry());
    DIMENSIONS_TAG.put("dimension", (Tag)list);
  }
  
  private static CompoundTag createOverworldEntry() {
    CompoundTag tag = new CompoundTag();
    tag.put("name", (Tag)new StringTag("minecraft:overworld"));
    tag.put("has_ceiling", (Tag)new ByteTag((byte)0));
    addSharedOverwaldEntries(tag);
    return tag;
  }
  
  private static CompoundTag createOverworldCavesEntry() {
    CompoundTag tag = new CompoundTag();
    tag.put("name", (Tag)new StringTag("minecraft:overworld_caves"));
    tag.put("has_ceiling", (Tag)new ByteTag((byte)1));
    addSharedOverwaldEntries(tag);
    return tag;
  }
  
  private static void addSharedOverwaldEntries(CompoundTag tag) {
    tag.put("piglin_safe", (Tag)new ByteTag((byte)0));
    tag.put("natural", (Tag)new ByteTag((byte)1));
    tag.put("ambient_light", (Tag)new FloatTag(0.0F));
    tag.put("infiniburn", (Tag)new StringTag("minecraft:infiniburn_overworld"));
    tag.put("respawn_anchor_works", (Tag)new ByteTag((byte)0));
    tag.put("has_skylight", (Tag)new ByteTag((byte)1));
    tag.put("bed_works", (Tag)new ByteTag((byte)1));
    tag.put("has_raids", (Tag)new ByteTag((byte)1));
    tag.put("logical_height", (Tag)new IntTag(256));
    tag.put("shrunk", (Tag)new ByteTag((byte)0));
    tag.put("ultrawarm", (Tag)new ByteTag((byte)0));
  }
  
  private static CompoundTag createNetherEntry() {
    CompoundTag tag = new CompoundTag();
    tag.put("piglin_safe", (Tag)new ByteTag((byte)1));
    tag.put("natural", (Tag)new ByteTag((byte)0));
    tag.put("ambient_light", (Tag)new FloatTag(0.1F));
    tag.put("infiniburn", (Tag)new StringTag("minecraft:infiniburn_nether"));
    tag.put("respawn_anchor_works", (Tag)new ByteTag((byte)1));
    tag.put("has_skylight", (Tag)new ByteTag((byte)0));
    tag.put("bed_works", (Tag)new ByteTag((byte)0));
    tag.put("fixed_time", (Tag)new LongTag(18000L));
    tag.put("has_raids", (Tag)new ByteTag((byte)0));
    tag.put("name", (Tag)new StringTag("minecraft:the_nether"));
    tag.put("logical_height", (Tag)new IntTag(128));
    tag.put("shrunk", (Tag)new ByteTag((byte)1));
    tag.put("ultrawarm", (Tag)new ByteTag((byte)1));
    tag.put("has_ceiling", (Tag)new ByteTag((byte)1));
    return tag;
  }
  
  private static CompoundTag createEndEntry() {
    CompoundTag tag = new CompoundTag();
    tag.put("piglin_safe", (Tag)new ByteTag((byte)0));
    tag.put("natural", (Tag)new ByteTag((byte)0));
    tag.put("ambient_light", (Tag)new FloatTag(0.0F));
    tag.put("infiniburn", (Tag)new StringTag("minecraft:infiniburn_end"));
    tag.put("respawn_anchor_works", (Tag)new ByteTag((byte)0));
    tag.put("has_skylight", (Tag)new ByteTag((byte)0));
    tag.put("bed_works", (Tag)new ByteTag((byte)0));
    tag.put("fixed_time", (Tag)new LongTag(6000L));
    tag.put("has_raids", (Tag)new ByteTag((byte)1));
    tag.put("name", (Tag)new StringTag("minecraft:the_end"));
    tag.put("logical_height", (Tag)new IntTag(256));
    tag.put("shrunk", (Tag)new ByteTag((byte)0));
    tag.put("ultrawarm", (Tag)new ByteTag((byte)0));
    tag.put("has_ceiling", (Tag)new ByteTag((byte)0));
    return tag;
  }
  
  public static void register(final Protocol1_16To1_15_2 protocol) {
    MetadataRewriter1_16To1_15_2 metadataRewriter = (MetadataRewriter1_16To1_15_2)protocol.get(MetadataRewriter1_16To1_15_2.class);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY, (ClientboundPacketType)ClientboundPackets1_16.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  wrapper.user().getEntityTracker(Protocol1_16To1_15_2.class).addEntity(entityId, (EntityType)Entity1_16Types.LIGHTNING_BOLT);
                  wrapper.write(Type.UUID, UUID.randomUUID());
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(Entity1_16Types.LIGHTNING_BOLT.getId()));
                  wrapper.read((Type)Type.BYTE);
                  wrapper.passthrough((Type)Type.DOUBLE);
                  wrapper.passthrough((Type)Type.DOUBLE);
                  wrapper.passthrough((Type)Type.DOUBLE);
                  wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)0));
                  wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)0));
                  wrapper.write((Type)Type.INT, Integer.valueOf(0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                });
          }
        });
    metadataRewriter.registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_15.SPAWN_ENTITY, (EntityType)Entity1_16Types.FALLING_BLOCK);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_MOB);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PLAYER, (EntityType)Entity1_16Types.PLAYER);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_15.ENTITY_METADATA, Types1_14.METADATA_LIST, Types1_16.METADATA_LIST);
    metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_15.DESTROY_ENTITIES);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(EntityPackets.DIMENSION_HANDLER);
            map((Type)Type.LONG);
            map((Type)Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)-1));
                  String levelType = (String)wrapper.read(Type.STRING);
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(levelType.equals("flat")));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)-1));
                  wrapper.write(Type.STRING_ARRAY, EntityPackets.WORLD_NAMES);
                  wrapper.write(Type.NBT, EntityPackets.DIMENSIONS_TAG);
                });
            handler(EntityPackets.DIMENSION_HANDLER);
            map((Type)Type.LONG);
            map((Type)Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  wrapper.user().getEntityTracker(Protocol1_16To1_15_2.class).addEntity(((Integer)wrapper.get((Type)Type.INT, 0)).intValue(), (EntityType)Entity1_16Types.PLAYER);
                  String type = (String)wrapper.read(Type.STRING);
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(type.equals("flat")));
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int size = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                  int actualSize = size;
                  for (int i = 0; i < size; i++) {
                    String key = (String)wrapper.read(Type.STRING);
                    String attributeIdentifier = (String)protocol.getMappingData().getAttributeMappings().get(key);
                    if (attributeIdentifier == null) {
                      attributeIdentifier = "minecraft:" + key;
                      if (!MappingData.isValid1_13Channel(attributeIdentifier)) {
                        if (!Via.getConfig().isSuppressConversionWarnings())
                          Via.getPlatform().getLogger().warning("Invalid attribute: " + key); 
                        actualSize--;
                        wrapper.read((Type)Type.DOUBLE);
                        int k = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                        for (int m = 0; m < k; m++) {
                          wrapper.read(Type.UUID);
                          wrapper.read((Type)Type.DOUBLE);
                          wrapper.read((Type)Type.BYTE);
                        } 
                        continue;
                      } 
                    } 
                    wrapper.write(Type.STRING, attributeIdentifier);
                    wrapper.passthrough((Type)Type.DOUBLE);
                    int modifierSize = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int j = 0; j < modifierSize; j++) {
                      wrapper.passthrough(Type.UUID);
                      wrapper.passthrough((Type)Type.DOUBLE);
                      wrapper.passthrough((Type)Type.BYTE);
                    } 
                    continue;
                  } 
                  if (size != actualSize)
                    wrapper.set((Type)Type.INT, 0, Integer.valueOf(actualSize)); 
                });
          }
        });
  }
}
