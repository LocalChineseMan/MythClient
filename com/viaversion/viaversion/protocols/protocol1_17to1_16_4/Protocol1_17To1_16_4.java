package com.viaversion.viaversion.protocols.protocol1_17to1_16_4;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;

public final class Protocol1_17To1_16_4 extends AbstractProtocol<ClientboundPackets1_16_2, ClientboundPackets1_17, ServerboundPackets1_16_2, ServerboundPackets1_17> {
  public static final MappingData MAPPINGS = (MappingData)new MappingDataBase("1.16.2", "1.17", true);
  
  private static final String[] NEW_GAME_EVENT_TAGS = new String[] { "minecraft:ignore_vibrations_sneaking", "minecraft:vibrations" };
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  private final TagRewriter tagRewriter = new TagRewriter((Protocol)this);
  
  public Protocol1_17To1_16_4() {
    super(ClientboundPackets1_16_2.class, ClientboundPackets1_17.class, ServerboundPackets1_16_2.class, ServerboundPackets1_17.class);
  }
  
  protected void registerPackets() {
    this.entityRewriter.register();
    this.itemRewriter.register();
    WorldPackets.register(this);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.TAGS, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(5));
                  for (RegistryType type : RegistryType.getValues()) {
                    wrapper.write(Type.STRING, type.getResourceLocation());
                    Protocol1_17To1_16_4.this.tagRewriter.handle(wrapper, Protocol1_17To1_16_4.this.tagRewriter.getRewriter(type), Protocol1_17To1_16_4.this.tagRewriter.getNewTags(type));
                    if (type == RegistryType.ENTITY)
                      break; 
                  } 
                  wrapper.write(Type.STRING, RegistryType.GAME_EVENT.getResourceLocation());
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(Protocol1_17To1_16_4.NEW_GAME_EVENT_TAGS.length));
                  for (String tag : Protocol1_17To1_16_4.NEW_GAME_EVENT_TAGS) {
                    wrapper.write(Type.STRING, tag);
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[0]);
                  } 
                });
          }
        });
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16_2.STATISTICS);
    SoundRewriter soundRewriter = new SoundRewriter((Protocol)this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_SOUND);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.RESOURCE_PACK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(Via.getConfig().isForcedUse1_17ResourcePack()));
                  wrapper.write(Type.OPTIONAL_COMPONENT, Via.getConfig().get1_17ResourcePackPrompt());
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough((Type)Type.BYTE);
                  wrapper.read((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  int size = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  if (size != 0) {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(size));
                  } else {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  } 
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.TITLE, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  ClientboundPacketType packetType;
                  int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  switch (type) {
                    case 0:
                      packetType = ClientboundPackets1_17.TITLE_TEXT;
                      break;
                    case 1:
                      packetType = ClientboundPackets1_17.TITLE_SUBTITLE;
                      break;
                    case 2:
                      packetType = ClientboundPackets1_17.ACTIONBAR;
                      break;
                    case 3:
                      packetType = ClientboundPackets1_17.TITLE_TIMES;
                      break;
                    case 4:
                      packetType = ClientboundPackets1_17.CLEAR_TITLES;
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                      break;
                    case 5:
                      packetType = ClientboundPackets1_17.CLEAR_TITLES;
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                      break;
                    default:
                      throw new IllegalArgumentException("Invalid title type received: " + type);
                  } 
                  wrapper.setId(packetType.getId());
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(wrapper -> wrapper.write((Type)Type.VAR_INT, wrapper.read((Type)Type.INT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14);
            handler(wrapper -> wrapper.write((Type)Type.FLOAT, Float.valueOf(0.0F)));
          }
        });
    registerServerbound(ServerboundPackets1_17.CLIENT_SETTINGS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.read((Type)Type.BOOLEAN));
          }
        });
  }
  
  protected void onMappingDataLoaded() {
    this.tagRewriter.loadFromMappingData();
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { 
          "minecraft:candles", "minecraft:ignored_by_piglin_babies", "minecraft:piglin_food", "minecraft:freeze_immune_wearables", "minecraft:axolotl_tempt_items", "minecraft:occludes_vibration_signals", "minecraft:fox_food", "minecraft:diamond_ores", "minecraft:iron_ores", "minecraft:lapis_ores", 
          "minecraft:redstone_ores", "minecraft:coal_ores", "minecraft:copper_ores", "minecraft:emerald_ores", "minecraft:cluster_max_harvestables" });
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { 
          "minecraft:crystal_sound_blocks", "minecraft:candle_cakes", "minecraft:candles", "minecraft:snow_step_sound_blocks", "minecraft:inside_step_sound_blocks", "minecraft:occludes_vibration_signals", "minecraft:dripstone_replaceable_blocks", "minecraft:cave_vines", "minecraft:moss_replaceable", "minecraft:deepslate_ore_replaceables", 
          "minecraft:lush_ground_replaceable", "minecraft:diamond_ores", "minecraft:iron_ores", "minecraft:lapis_ores", "minecraft:redstone_ores", "minecraft:stone_ore_replaceables", "minecraft:coal_ores", "minecraft:copper_ores", "minecraft:emerald_ores", "minecraft:dirt", 
          "minecraft:snow", "minecraft:small_dripleaf_placeable", "minecraft:features_cannot_replace", "minecraft:lava_pool_stone_replaceables", "minecraft:geode_invalid_blocks" });
    this.tagRewriter.addEmptyTags(RegistryType.ENTITY, new String[] { "minecraft:powder_snow_walkable_mobs", "minecraft:axolotl_always_hostiles", "minecraft:axolotl_tempted_hostiles", "minecraft:axolotl_hunt_targets", "minecraft:freeze_hurts_extra_types", "minecraft:freeze_immune_entity_types" });
  }
  
  public void init(UserConnection user) {
    addEntityTracker(user, (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_17Types.PLAYER));
    user.put((StorableObject)new InventoryAcknowledgements());
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
