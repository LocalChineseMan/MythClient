package com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.metadata.MetadataRewriter1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;

public class Protocol1_16_2To1_16_1 extends AbstractProtocol<ClientboundPackets1_16, ClientboundPackets1_16_2, ServerboundPackets1_16, ServerboundPackets1_16_2> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private final EntityRewriter metadataRewriter = (EntityRewriter)new MetadataRewriter1_16_2To1_16_1(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  private TagRewriter tagRewriter;
  
  public Protocol1_16_2To1_16_1() {
    super(ClientboundPackets1_16.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    this.metadataRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    WorldPackets.register((Protocol)this);
    this.tagRewriter = new TagRewriter((Protocol)this);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_16.TAGS, RegistryType.ENTITY);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16.STATISTICS);
    SoundRewriter soundRewriter = new SoundRewriter((Protocol)this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.ENTITY_SOUND);
    registerServerbound(ServerboundPackets1_16_2.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int recipeType = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  boolean open = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  boolean filter = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf((recipeType == 0)));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf((recipeType == 1)));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf((recipeType == 2)));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf((recipeType == 3)));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(filter));
                });
          }
        });
    registerServerbound(ServerboundPackets1_16_2.SEEN_RECIPE, (ServerboundPacketType)ServerboundPackets1_16.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String recipe = (String)wrapper.read(Type.STRING);
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  wrapper.write(Type.STRING, recipe);
                });
          }
        });
  }
  
  protected void onMappingDataLoaded() {
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:stone_crafting_materials", new int[] { 14, 962 });
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:mushroom_grow_block");
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { "minecraft:soul_fire_base_blocks", "minecraft:furnace_materials", "minecraft:crimson_stems", "minecraft:gold_ores", "minecraft:piglin_loved", "minecraft:piglin_repellents", "minecraft:creeper_drop_music_discs", "minecraft:logs_that_burn", "minecraft:stone_tool_materials", "minecraft:warped_stems" });
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { 
          "minecraft:infiniburn_nether", "minecraft:crimson_stems", "minecraft:wither_summon_base_blocks", "minecraft:infiniburn_overworld", "minecraft:piglin_repellents", "minecraft:hoglin_repellents", "minecraft:prevent_mob_spawning_inside", "minecraft:wart_blocks", "minecraft:stone_pressure_plates", "minecraft:nylium", 
          "minecraft:gold_ores", "minecraft:pressure_plates", "minecraft:logs_that_burn", "minecraft:strider_warm_blocks", "minecraft:warped_stems", "minecraft:infiniburn_end", "minecraft:base_stone_nether", "minecraft:base_stone_overworld" });
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(userConnection, (EntityType)Entity1_16_2Types.PLAYER));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.metadataRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
