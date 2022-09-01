package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.google.common.collect.ImmutableSet;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLightImpl;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13_2;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockItemPackets1_14 extends ItemRewriter<Protocol1_13_2To1_14> {
  private EnchantmentRewriter enchantmentRewriter;
  
  public BlockItemPackets1_14(Protocol1_13_2To1_14 protocol, TranslatableRewriter translatableRewriter) {
    super((BackwardsProtocol)protocol, translatableRewriter);
  }
  
  protected void registerPackets() {
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> BlockItemPackets1_14.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int windowId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)windowId));
                    int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    String stringType = null;
                    String containerTitle = null;
                    int slotSize = 0;
                    if (type < 6) {
                      if (type == 2)
                        containerTitle = "Barrel"; 
                      stringType = "minecraft:container";
                      slotSize = (type + 1) * 9;
                    } else {
                      switch (type) {
                        case 11:
                          stringType = "minecraft:crafting_table";
                          break;
                        case 9:
                        case 13:
                        case 14:
                        case 20:
                          if (type == 9) {
                            containerTitle = "Blast Furnace";
                          } else if (type == 20) {
                            containerTitle = "Smoker";
                          } else if (type == 14) {
                            containerTitle = "Grindstone";
                          } 
                          stringType = "minecraft:furnace";
                          slotSize = 3;
                          break;
                        case 6:
                          stringType = "minecraft:dropper";
                          slotSize = 9;
                          break;
                        case 12:
                          stringType = "minecraft:enchanting_table";
                          break;
                        case 10:
                          stringType = "minecraft:brewing_stand";
                          slotSize = 5;
                          break;
                        case 18:
                          stringType = "minecraft:villager";
                          break;
                        case 8:
                          stringType = "minecraft:beacon";
                          slotSize = 1;
                          break;
                        case 7:
                        case 21:
                          if (type == 21)
                            containerTitle = "Cartography Table"; 
                          stringType = "minecraft:anvil";
                          break;
                        case 15:
                          stringType = "minecraft:hopper";
                          slotSize = 5;
                          break;
                        case 19:
                          stringType = "minecraft:shulker_box";
                          slotSize = 27;
                          break;
                      } 
                    } 
                    if (stringType == null) {
                      ViaBackwards.getPlatform().getLogger().warning("Can't open inventory for 1.13 player! Type: " + type);
                      wrapper.cancel();
                      return;
                    } 
                    wrapper.write(Type.STRING, stringType);
                    JsonElement title = (JsonElement)wrapper.read(Type.COMPONENT);
                    if (containerTitle != null) {
                      JsonObject object;
                      if (title.isJsonObject() && (object = title.getAsJsonObject()).has("translate"))
                        if (type != 2 || object.getAsJsonPrimitive("translate").getAsString().equals("container.barrel"))
                          title = ChatRewriter.legacyTextToJson(containerTitle);  
                    } 
                    wrapper.write(Type.COMPONENT, title);
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)slotSize));
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.OPEN_HORSE_WINDOW, (ClientboundPacketType)ClientboundPackets1_13.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                    wrapper.write(Type.STRING, "EntityHorse");
                    JsonObject object = new JsonObject();
                    object.addProperty("translate", "minecraft.horse");
                    wrapper.write(Type.COMPONENT, object);
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(((Integer)wrapper.read((Type)Type.VAR_INT)).shortValue()));
                    wrapper.passthrough((Type)Type.INT);
                  }
                });
          }
        });
    BlockRewriter blockRewriter = new BlockRewriter(this.protocol, Type.POSITION);
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_14.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_14.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_14.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_14.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.TRADE_LIST, (ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.STRING, "minecraft:trader_list");
                    int windowId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.write((Type)Type.INT, Integer.valueOf(windowId));
                    int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                    for (int i = 0; i < size; i++) {
                      Item input = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                      input = BlockItemPackets1_14.this.handleItemToClient(input);
                      wrapper.write(Type.FLAT_VAR_INT_ITEM, input);
                      Item output = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                      output = BlockItemPackets1_14.this.handleItemToClient(output);
                      wrapper.write(Type.FLAT_VAR_INT_ITEM, output);
                      boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                      if (secondItem) {
                        Item second = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                        second = BlockItemPackets1_14.this.handleItemToClient(second);
                        wrapper.write(Type.FLAT_VAR_INT_ITEM, second);
                      } 
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.read((Type)Type.INT);
                      wrapper.read((Type)Type.INT);
                      wrapper.read((Type)Type.FLOAT);
                    } 
                    wrapper.read((Type)Type.VAR_INT);
                    wrapper.read((Type)Type.VAR_INT);
                    wrapper.read((Type)Type.BOOLEAN);
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.OPEN_BOOK, (ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.STRING, "minecraft:book_open");
                    wrapper.passthrough((Type)Type.VAR_INT);
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.FLAT_VAR_INT_ITEM);
            handler(BlockItemPackets1_14.this.itemToClientHandler(Type.FLAT_VAR_INT_ITEM));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityType entityType = wrapper.user().getEntityTracker(Protocol1_13_2To1_14.class).entityType(entityId);
                    if (entityType == null)
                      return; 
                    if (entityType.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_HORSE)) {
                      wrapper.setId(63);
                      wrapper.resetReader();
                      wrapper.passthrough((Type)Type.VAR_INT);
                      wrapper.read((Type)Type.VAR_INT);
                      Item item = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                      int armorType = (item == null || item.identifier() == 0) ? 0 : (item.identifier() - 726);
                      if (armorType < 0 || armorType > 3) {
                        ViaBackwards.getPlatform().getLogger().warning("Received invalid horse armor: " + item);
                        wrapper.cancel();
                        return;
                      } 
                      List<Metadata> metadataList = new ArrayList<>();
                      metadataList.add(new Metadata(16, (MetaType)MetaType1_13_2.VarInt, Integer.valueOf(armorType)));
                      wrapper.write(Types1_13.METADATA_LIST, metadataList);
                    } 
                  }
                });
          }
        });
    final RecipeRewriter1_13_2 recipeHandler = new RecipeRewriter1_13_2(this.protocol);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.DECLARE_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  private final Set<String> removedTypes = (Set<String>)ImmutableSet.of("crafting_special_suspiciousstew", "blasting", "smoking", "campfire_cooking", "stonecutting");
                  
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    int deleted = 0;
                    for (int i = 0; i < size; i++) {
                      String type = (String)wrapper.read(Type.STRING);
                      String id = (String)wrapper.read(Type.STRING);
                      type = type.replace("minecraft:", "");
                      if (this.removedTypes.contains(type)) {
                        switch (type) {
                          case "blasting":
                          case "smoking":
                          case "campfire_cooking":
                            wrapper.read(Type.STRING);
                            wrapper.read(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
                            wrapper.read(Type.FLAT_VAR_INT_ITEM);
                            wrapper.read((Type)Type.FLOAT);
                            wrapper.read((Type)Type.VAR_INT);
                            break;
                          case "stonecutting":
                            wrapper.read(Type.STRING);
                            wrapper.read(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
                            wrapper.read(Type.FLAT_VAR_INT_ITEM);
                            break;
                        } 
                        deleted++;
                      } else {
                        wrapper.write(Type.STRING, id);
                        wrapper.write(Type.STRING, type);
                        recipeHandler.handle(wrapper, type);
                      } 
                    } 
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(size - deleted));
                  }
                });
          }
        });
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_13.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION1_14, Type.POSITION);
            map((Type)Type.BYTE);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int mappedId = ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue());
                  if (mappedId == -1) {
                    wrapper.cancel();
                    return;
                  } 
                  wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(mappedId));
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockStateId(id)));
                  }
                });
          }
        });
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_14.MULTI_BLOCK_CHANGE);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (int i = 0; i < 3; i++) {
                      float coord = ((Float)wrapper.get((Type)Type.FLOAT, i)).floatValue();
                      if (coord < 0.0F) {
                        coord = (float)Math.floor(coord);
                        wrapper.set((Type)Type.FLOAT, i, Float.valueOf(coord));
                      } 
                    } 
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_14Type());
                    wrapper.write((Type)new Chunk1_13Type(clientWorld), chunk);
                    ChunkLightStorage.ChunkLight chunkLight = ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).getStoredLight(chunk.getX(), chunk.getZ());
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null) {
                        ChunkSectionLightImpl chunkSectionLightImpl = new ChunkSectionLightImpl();
                        section.setLight((ChunkSectionLight)chunkSectionLightImpl);
                        if (chunkLight == null) {
                          chunkSectionLightImpl.setBlockLight(ChunkLightStorage.FULL_LIGHT);
                          if (clientWorld.getEnvironment() == Environment.NORMAL)
                            chunkSectionLightImpl.setSkyLight(ChunkLightStorage.FULL_LIGHT); 
                        } else {
                          byte[] blockLight = chunkLight.getBlockLight()[i];
                          chunkSectionLightImpl.setBlockLight((blockLight != null) ? blockLight : ChunkLightStorage.FULL_LIGHT);
                          if (clientWorld.getEnvironment() == Environment.NORMAL) {
                            byte[] skyLight = chunkLight.getSkyLight()[i];
                            chunkSectionLightImpl.setSkyLight((skyLight != null) ? skyLight : ChunkLightStorage.FULL_LIGHT);
                          } 
                        } 
                        if (Via.getConfig().isNonFullBlockLightFix() && section.getNonAirBlocksCount() != 0 && chunkSectionLightImpl.hasBlockLight())
                          for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                              for (int z = 0; z < 16; z++) {
                                int id = section.getFlatBlock(x, y, z);
                                if (Protocol1_14To1_13_2.MAPPINGS.getNonFullBlocks().contains(id))
                                  chunkSectionLightImpl.getBlockLightNibbleArray().set(x, y, z, 0); 
                              } 
                            } 
                          }  
                        for (int j = 0; j < section.getPaletteSize(); j++) {
                          int old = section.getPaletteEntry(j);
                          int newId = ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockStateId(old);
                          section.setPaletteEntry(j, newId);
                        } 
                      } 
                    } 
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.UNLOAD_CHUNK, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int x = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                    int z = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                    ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).unloadChunk(x, z);
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION1_14, Type.POSITION);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int data = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    if (id == 1010) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewItemId(data)));
                    } else if (id == 2001) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockStateId(data)));
                    } 
                  }
                });
          }
        });
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.FLOAT);
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            map((Type)Type.BOOLEAN, (Type)Type.NOTHING);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
  }
  
  protected void registerRewrites() {
    this.enchantmentRewriter = new EnchantmentRewriter(this, false);
    this.enchantmentRewriter.registerEnchantment("minecraft:multishot", "§7Multishot");
    this.enchantmentRewriter.registerEnchantment("minecraft:quick_charge", "§7Quick Charge");
    this.enchantmentRewriter.registerEnchantment("minecraft:piercing", "§7Piercing");
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    super.handleItemToClient(item);
    CompoundTag tag = item.tag();
    CompoundTag display;
    if (tag != null && (display = (CompoundTag)tag.get("display")) != null) {
      ListTag lore = (ListTag)display.get("Lore");
      if (lore != null) {
        saveListTag(display, lore, "Lore");
        for (Tag loreEntry : lore) {
          if (!(loreEntry instanceof StringTag))
            continue; 
          StringTag loreEntryTag = (StringTag)loreEntry;
          String value = loreEntryTag.getValue();
          if (value != null && !value.isEmpty())
            loreEntryTag.setValue(ChatRewriter.jsonToLegacyText(value)); 
        } 
      } 
    } 
    this.enchantmentRewriter.handleToClient(item);
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    CompoundTag tag = item.tag();
    CompoundTag display;
    if (tag != null && (display = (CompoundTag)tag.get("display")) != null) {
      ListTag lore = (ListTag)display.get("Lore");
      if (lore != null && !hasBackupTag(display, "Lore"))
        for (Tag loreEntry : lore) {
          if (loreEntry instanceof StringTag) {
            StringTag loreEntryTag = (StringTag)loreEntry;
            loreEntryTag.setValue(ChatRewriter.legacyTextToJsonString(loreEntryTag.getValue()));
          } 
        }  
    } 
    this.enchantmentRewriter.handleToServer(item);
    super.handleItemToServer(item);
    return item;
  }
}
