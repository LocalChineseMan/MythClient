package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.google.common.primitives.Ints;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.FlowerPotHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class BlockItemPackets1_13 extends ItemRewriter<Protocol1_12_2To1_13> {
  private final Map<String, String> enchantmentMappings = new HashMap<>();
  
  private final String extraNbtTag;
  
  public BlockItemPackets1_13(Protocol1_12_2To1_13 protocol) {
    super((BackwardsProtocol)protocol, null);
    this.extraNbtTag = "VB|" + protocol.getClass().getSimpleName() + "|2";
  }
  
  public static boolean isDamageable(int id) {
    return ((id >= 256 && id <= 259) || id == 261 || (id >= 267 && id <= 279) || (id >= 283 && id <= 286) || (id >= 290 && id <= 294) || (id >= 298 && id <= 317) || id == 346 || id == 359 || id == 398 || id == 442 || id == 443);
  }
  
  protected void registerPackets() {
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.COOLDOWN, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int itemId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int oldId = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getItemMappings().get(itemId);
                    if (oldId != -1) {
                      Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
                      if (eggEntityId.isPresent()) {
                        itemId = 25100288;
                      } else {
                        itemId = oldId >> 4 << 16 | oldId & 0xF;
                      } 
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(itemId));
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int blockId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (blockId == 73) {
                      blockId = 25;
                    } else if (blockId == 99) {
                      blockId = 33;
                    } else if (blockId == 92) {
                      blockId = 29;
                    } else if (blockId == 142) {
                      blockId = 54;
                    } else if (blockId == 305) {
                      blockId = 146;
                    } else if (blockId == 249) {
                      blockId = 130;
                    } else if (blockId == 257) {
                      blockId = 138;
                    } else if (blockId == 140) {
                      blockId = 52;
                    } else if (blockId == 472) {
                      blockId = 209;
                    } else if (blockId >= 483 && blockId <= 498) {
                      blockId = blockId - 483 + 219;
                    } 
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(blockId));
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    BackwardsBlockEntityProvider provider = (BackwardsBlockEntityProvider)Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
                    if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 5)
                      wrapper.cancel(); 
                    wrapper.set(Type.NBT, 0, provider
                        .transform(wrapper
                          .user(), (Position)wrapper
                          .get(Type.POSITION, 0), (CompoundTag)wrapper
                          .get(Type.NBT, 0)));
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.UNLOAD_CHUNK, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int chunkMinX = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue() << 4;
                    int chunkMinZ = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue() << 4;
                    int chunkMaxX = chunkMinX + 15;
                    int chunkMaxZ = chunkMinZ + 15;
                    BackwardsBlockStorage blockStorage = (BackwardsBlockStorage)wrapper.user().get(BackwardsBlockStorage.class);
                    blockStorage.getBlocks().entrySet().removeIf(entry -> {
                          Position position = (Position)entry.getKey();
                          return (position.getX() >= chunkMinX && position.getZ() >= chunkMinZ && position.getX() <= chunkMaxX && position.getZ() <= chunkMaxZ);
                        });
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int blockState = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    Position position = (Position)wrapper.get(Type.POSITION, 0);
                    BackwardsBlockStorage storage = (BackwardsBlockStorage)wrapper.user().get(BackwardsBlockStorage.class);
                    storage.checkAndStore(position, blockState);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(blockState)));
                    BlockItemPackets1_13.flowerPotSpecialTreatment(wrapper.user(), blockState, position);
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    BackwardsBlockStorage storage = (BackwardsBlockStorage)wrapper.user().get(BackwardsBlockStorage.class);
                    for (BlockChangeRecord record : (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                      int chunkX = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int chunkZ = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                      int block = record.getBlockId();
                      Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                      storage.checkAndStore(position, block);
                      BlockItemPackets1_13.flowerPotSpecialTreatment(wrapper.user(), block, position);
                      record.setBlockId(((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(block));
                    } 
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.FLAT_ITEM_ARRAY, Type.ITEM_ARRAY);
            handler(BlockItemPackets1_13.this.itemArrayHandler(Type.ITEM_ARRAY));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.FLAT_ITEM, Type.ITEM);
            handler(BlockItemPackets1_13.this.itemToClientHandler(Type.ITEM));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  Chunk1_9_3_4Type type_old = new Chunk1_9_3_4Type(clientWorld);
                  Chunk1_13Type type = new Chunk1_13Type(clientWorld);
                  Chunk chunk = (Chunk)wrapper.read((Type)type);
                  BackwardsBlockEntityProvider provider = (BackwardsBlockEntityProvider)Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
                  BackwardsBlockStorage storage = (BackwardsBlockStorage)wrapper.user().get(BackwardsBlockStorage.class);
                  for (CompoundTag tag : chunk.getBlockEntities()) {
                    Tag idTag = tag.get("id");
                    if (idTag == null)
                      continue; 
                    String id = (String)idTag.getValue();
                    if (!provider.isHandled(id))
                      continue; 
                    int sectionIndex = ((NumberTag)tag.get("y")).asInt() >> 4;
                    if (sectionIndex < 0 || sectionIndex > 15)
                      continue; 
                    ChunkSection section = chunk.getSections()[sectionIndex];
                    int x = ((NumberTag)tag.get("x")).asInt();
                    int y = ((NumberTag)tag.get("y")).asInt();
                    int z = ((NumberTag)tag.get("z")).asInt();
                    Position position = new Position(x, (short)y, z);
                    int block = section.getFlatBlock(x & 0xF, y & 0xF, z & 0xF);
                    storage.checkAndStore(position, block);
                    provider.transform(wrapper.user(), position, tag);
                  } 
                  int i;
                  for (i = 0; i < (chunk.getSections()).length; i++) {
                    ChunkSection section = chunk.getSections()[i];
                    if (section != null) {
                      for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                          for (int x = 0; x < 16; x++) {
                            int block = section.getFlatBlock(x, y, z);
                            if (FlowerPotHandler.isFlowah(block)) {
                              Position pos = new Position(x + (chunk.getX() << 4), (short)(y + (i << 4)), z + (chunk.getZ() << 4));
                              storage.checkAndStore(pos, block);
                              CompoundTag nbt = provider.transform(wrapper.user(), pos, "minecraft:flower_pot");
                              chunk.getBlockEntities().add(nbt);
                            } 
                          } 
                        } 
                      } 
                      for (int p = 0; p < section.getPaletteSize(); p++) {
                        int old = section.getPaletteEntry(p);
                        if (old != 0) {
                          int oldId = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(old);
                          section.setPaletteEntry(p, oldId);
                        } 
                      } 
                    } 
                  } 
                  if (chunk.isBiomeData())
                    for (i = 0; i < 256; i++) {
                      int biome = chunk.getBiomeData()[i];
                      int newId = -1;
                      switch (biome) {
                        case 40:
                        case 41:
                        case 42:
                        case 43:
                          newId = 9;
                          break;
                        case 47:
                        case 48:
                        case 49:
                          newId = 24;
                          break;
                        case 50:
                          newId = 10;
                          break;
                        case 44:
                        case 45:
                        case 46:
                          newId = 0;
                          break;
                      } 
                      if (newId != -1)
                        chunk.getBiomeData()[i] = newId; 
                    }  
                  wrapper.write((Type)type_old, chunk);
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int data = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    if (id == 1010) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getItemMappings().get(data) >> 4));
                    } else if (id == 2001) {
                      data = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(data);
                      int blockId = data >> 4;
                      int blockData = data & 0xF;
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(blockId & 0xFFF | blockData << 12));
                    } 
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int iconCount = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int i = 0; i < iconCount; i++) {
                      int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      byte x = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                      byte z = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                      byte direction = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                      if (((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue())
                        wrapper.read(Type.COMPONENT); 
                      if (type > 9) {
                        wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue() - 1));
                      } else {
                        wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)(type << 4 | direction & 0xF)));
                        wrapper.write((Type)Type.BYTE, Byte.valueOf(x));
                        wrapper.write((Type)Type.BYTE, Byte.valueOf(z));
                      } 
                    } 
                  }
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.FLAT_ITEM, Type.ITEM);
            handler(BlockItemPackets1_13.this.itemToClientHandler(Type.ITEM));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(wrapper -> {
                  short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (property >= 4 && property <= 6) {
                    short oldId = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                    wrapper.set((Type)Type.SHORT, 1, Short.valueOf((short)((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getEnchantmentMappings().getNewId(oldId)));
                  } 
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Type.ITEM, Type.FLAT_ITEM);
            handler(BlockItemPackets1_13.this.itemToServerHandler(Type.FLAT_ITEM));
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(Type.ITEM, Type.FLAT_ITEM);
            handler(BlockItemPackets1_13.this.itemToServerHandler(Type.FLAT_ITEM));
          }
        });
  }
  
  protected void registerRewrites() {
    this.enchantmentMappings.put("minecraft:loyalty", "§7Loyalty");
    this.enchantmentMappings.put("minecraft:impaling", "§7Impaling");
    this.enchantmentMappings.put("minecraft:riptide", "§7Riptide");
    this.enchantmentMappings.put("minecraft:channeling", "§7Channeling");
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    int originalId = item.identifier();
    Integer rawId = null;
    boolean gotRawIdFromTag = false;
    CompoundTag tag = item.tag();
    Tag originalIdTag;
    if (tag != null && (originalIdTag = tag.remove(this.extraNbtTag)) != null) {
      rawId = Integer.valueOf(((NumberTag)originalIdTag).asInt());
      gotRawIdFromTag = true;
    } 
    if (rawId == null) {
      super.handleItemToClient(item);
      if (item.identifier() == -1) {
        if (originalId == 362) {
          rawId = Integer.valueOf(15007744);
        } else {
          if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
            ViaBackwards.getPlatform().getLogger().warning("Failed to get 1.12 item for " + originalId); 
          rawId = Integer.valueOf(65536);
        } 
      } else {
        if (tag == null)
          tag = item.tag(); 
        rawId = Integer.valueOf(itemIdToRaw(item.identifier(), item, tag));
      } 
    } 
    item.setIdentifier(rawId.intValue() >> 16);
    item.setData((short)(rawId.intValue() & 0xFFFF));
    if (tag != null) {
      if (isDamageable(item.identifier())) {
        Tag damageTag = tag.remove("Damage");
        if (!gotRawIdFromTag && damageTag instanceof IntTag)
          item.setData((short)((Integer)damageTag.getValue()).intValue()); 
      } 
      if (item.identifier() == 358) {
        Tag mapTag = tag.remove("map");
        if (!gotRawIdFromTag && mapTag instanceof IntTag)
          item.setData((short)((Integer)mapTag.getValue()).intValue()); 
      } 
      invertShieldAndBannerId(item, tag);
      CompoundTag display = (CompoundTag)tag.get("display");
      if (display != null) {
        StringTag name = (StringTag)display.get("Name");
        if (name != null) {
          display.put(this.extraNbtTag + "|Name", (Tag)new StringTag(name.getValue()));
          name.setValue(ChatRewriter.jsonToLegacyText(name.getValue()));
        } 
      } 
      rewriteEnchantmentsToClient(tag, false);
      rewriteEnchantmentsToClient(tag, true);
      rewriteCanPlaceToClient(tag, "CanPlaceOn");
      rewriteCanPlaceToClient(tag, "CanDestroy");
    } 
    return item;
  }
  
  private int itemIdToRaw(int oldId, Item item, CompoundTag tag) {
    Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
    if (eggEntityId.isPresent()) {
      if (tag == null)
        item.setTag(tag = new CompoundTag()); 
      if (!tag.contains("EntityTag")) {
        CompoundTag entityTag = new CompoundTag();
        entityTag.put("id", (Tag)new StringTag(eggEntityId.get()));
        tag.put("EntityTag", (Tag)entityTag);
      } 
      return 25100288;
    } 
    return oldId >> 4 << 16 | oldId & 0xF;
  }
  
  private void rewriteCanPlaceToClient(CompoundTag tag, String tagName) {
    if (!(tag.get(tagName) instanceof ListTag))
      return; 
    ListTag blockTag = (ListTag)tag.get(tagName);
    if (blockTag == null)
      return; 
    ListTag newCanPlaceOn = new ListTag(StringTag.class);
    tag.put(this.extraNbtTag + "|" + tagName, ConverterRegistry.convertToTag(ConverterRegistry.convertToValue((Tag)blockTag)));
    for (Tag oldTag : blockTag) {
      Object value = oldTag.getValue();
      String[] newValues = (value instanceof String) ? (String[])BlockIdData.fallbackReverseMapping.get(((String)value).replace("minecraft:", "")) : null;
      if (newValues != null) {
        for (String newValue : newValues)
          newCanPlaceOn.add((Tag)new StringTag(newValue)); 
        continue;
      } 
      newCanPlaceOn.add(oldTag);
    } 
    tag.put(tagName, (Tag)newCanPlaceOn);
  }
  
  private void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnch) {
    String key = storedEnch ? "StoredEnchantments" : "Enchantments";
    ListTag enchantments = (ListTag)tag.get(key);
    if (enchantments == null)
      return; 
    ListTag noMapped = new ListTag(CompoundTag.class);
    ListTag newEnchantments = new ListTag(CompoundTag.class);
    List<Tag> lore = new ArrayList<>();
    boolean hasValidEnchants = false;
    for (Tag enchantmentEntryTag : enchantments.clone()) {
      CompoundTag enchantmentEntry = (CompoundTag)enchantmentEntryTag;
      Tag idTag = enchantmentEntry.get("id");
      if (!(idTag instanceof StringTag))
        continue; 
      String newId = (String)idTag.getValue();
      int levelValue = ((NumberTag)enchantmentEntry.get("lvl")).asInt();
      short level = (levelValue < 32767) ? (short)levelValue : Short.MAX_VALUE;
      String mappedEnchantmentId = this.enchantmentMappings.get(newId);
      if (mappedEnchantmentId != null) {
        lore.add(new StringTag(mappedEnchantmentId + " " + EnchantmentRewriter.getRomanNumber(level)));
        noMapped.add((Tag)enchantmentEntry);
        continue;
      } 
      if (!newId.isEmpty()) {
        Short oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
        if (oldId == null) {
          if (!newId.startsWith("viaversion:legacy/")) {
            noMapped.add((Tag)enchantmentEntry);
            if (ViaBackwards.getConfig().addCustomEnchantsToLore()) {
              String name = newId;
              int index = name.indexOf(':') + 1;
              if (index != 0 && index != name.length())
                name = name.substring(index); 
              name = "§7" + Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase(Locale.ENGLISH);
              lore.add(new StringTag(name + " " + EnchantmentRewriter.getRomanNumber(level)));
            } 
            if (Via.getManager().isDebug())
              ViaBackwards.getPlatform().getLogger().warning("Found unknown enchant: " + newId); 
            continue;
          } 
          oldId = Short.valueOf(newId.substring(18));
        } 
        if (level != 0)
          hasValidEnchants = true; 
        CompoundTag newEntry = new CompoundTag();
        newEntry.put("id", (Tag)new ShortTag(oldId.shortValue()));
        newEntry.put("lvl", (Tag)new ShortTag(level));
        newEnchantments.add((Tag)newEntry);
      } 
    } 
    if (!storedEnch && !hasValidEnchants) {
      IntTag hideFlags = (IntTag)tag.get("HideFlags");
      if (hideFlags == null) {
        hideFlags = new IntTag();
        tag.put(this.extraNbtTag + "|DummyEnchant", (Tag)new ByteTag());
      } else {
        tag.put(this.extraNbtTag + "|OldHideFlags", (Tag)new IntTag(hideFlags.asByte()));
      } 
      if (newEnchantments.size() == 0) {
        CompoundTag enchEntry = new CompoundTag();
        enchEntry.put("id", (Tag)new ShortTag((short)0));
        enchEntry.put("lvl", (Tag)new ShortTag((short)0));
        newEnchantments.add((Tag)enchEntry);
      } 
      int value = hideFlags.asByte() | 0x1;
      hideFlags.setValue(value);
      tag.put("HideFlags", (Tag)hideFlags);
    } 
    if (noMapped.size() != 0) {
      tag.put(this.extraNbtTag + "|" + key, (Tag)noMapped);
      if (!lore.isEmpty()) {
        CompoundTag display = (CompoundTag)tag.get("display");
        if (display == null)
          tag.put("display", (Tag)(display = new CompoundTag())); 
        ListTag loreTag = (ListTag)display.get("Lore");
        if (loreTag == null) {
          display.put("Lore", (Tag)(loreTag = new ListTag(StringTag.class)));
          tag.put(this.extraNbtTag + "|DummyLore", (Tag)new ByteTag());
        } else if (loreTag.size() != 0) {
          ListTag oldLore = new ListTag(StringTag.class);
          for (Tag value : loreTag)
            oldLore.add(value.clone()); 
          tag.put(this.extraNbtTag + "|OldLore", (Tag)oldLore);
          lore.addAll(loreTag.getValue());
        } 
        loreTag.setValue(lore);
      } 
    } 
    tag.remove("Enchantments");
    tag.put(storedEnch ? key : "ench", (Tag)newEnchantments);
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    CompoundTag tag = item.tag();
    int originalId = item.identifier() << 16 | item.data() & 0xFFFF;
    int rawId = item.identifier() << 4 | item.data() & 0xF;
    if (isDamageable(item.identifier())) {
      if (tag == null)
        item.setTag(tag = new CompoundTag()); 
      tag.put("Damage", (Tag)new IntTag(item.data()));
    } 
    if (item.identifier() == 358) {
      if (tag == null)
        item.setTag(tag = new CompoundTag()); 
      tag.put("map", (Tag)new IntTag(item.data()));
    } 
    if (tag != null) {
      invertShieldAndBannerId(item, tag);
      Tag display = tag.get("display");
      if (display instanceof CompoundTag) {
        CompoundTag displayTag = (CompoundTag)display;
        StringTag name = (StringTag)displayTag.get("Name");
        if (name != null) {
          StringTag via = (StringTag)displayTag.remove(this.extraNbtTag + "|Name");
          name.setValue((via != null) ? via.getValue() : ChatRewriter.legacyTextToJsonString(name.getValue()));
        } 
      } 
      rewriteEnchantmentsToServer(tag, false);
      rewriteEnchantmentsToServer(tag, true);
      rewriteCanPlaceToServer(tag, "CanPlaceOn");
      rewriteCanPlaceToServer(tag, "CanDestroy");
      if (item.identifier() == 383) {
        CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
        StringTag stringTag;
        if (entityTag != null && (stringTag = (StringTag)entityTag.get("id")) != null) {
          rawId = SpawnEggRewriter.getSpawnEggId(stringTag.getValue());
          if (rawId == -1) {
            rawId = 25100288;
          } else {
            entityTag.remove("id");
            if (entityTag.isEmpty())
              tag.remove("EntityTag"); 
          } 
        } else {
          rawId = 25100288;
        } 
      } 
      if (tag.isEmpty())
        item.setTag(tag = null); 
    } 
    int identifier = item.identifier();
    item.setIdentifier(rawId);
    super.handleItemToServer(item);
    if (item.identifier() != rawId && item.identifier() != -1)
      return item; 
    item.setIdentifier(identifier);
    int newId = -1;
    if (!((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().containsKey(rawId)) {
      if (!isDamageable(item.identifier()) && item.identifier() != 358) {
        if (tag == null)
          item.setTag(tag = new CompoundTag()); 
        tag.put(this.extraNbtTag, (Tag)new IntTag(originalId));
      } 
      if (item.identifier() == 229) {
        newId = 362;
      } else if (item.identifier() == 31 && item.data() == 0) {
        rawId = 512;
      } else if (((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().containsKey(rawId & 0xFFFFFFF0)) {
        rawId &= 0xFFFFFFF0;
      } else {
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
          ViaBackwards.getPlatform().getLogger().warning("Failed to get 1.13 item for " + item.identifier()); 
        rawId = 16;
      } 
    } 
    if (newId == -1)
      newId = ((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().get(rawId); 
    item.setIdentifier(newId);
    item.setData((short)0);
    return item;
  }
  
  private void rewriteCanPlaceToServer(CompoundTag tag, String tagName) {
    if (!(tag.get(tagName) instanceof ListTag))
      return; 
    ListTag blockTag = (ListTag)tag.remove(this.extraNbtTag + "|" + tagName);
    if (blockTag != null) {
      tag.put(tagName, ConverterRegistry.convertToTag(ConverterRegistry.convertToValue((Tag)blockTag)));
    } else if ((blockTag = (ListTag)tag.get(tagName)) != null) {
      ListTag newCanPlaceOn = new ListTag(StringTag.class);
      for (Tag oldTag : blockTag) {
        Object value = oldTag.getValue();
        String oldId = value.toString().replace("minecraft:", "");
        int key = Ints.tryParse(oldId).intValue();
        String numberConverted = (String)BlockIdData.numberIdToString.get(key);
        if (numberConverted != null)
          oldId = numberConverted; 
        String lowerCaseId = oldId.toLowerCase(Locale.ROOT);
        String[] newValues = (String[])BlockIdData.blockIdMapping.get(lowerCaseId);
        if (newValues != null) {
          for (String newValue : newValues)
            newCanPlaceOn.add((Tag)new StringTag(newValue)); 
          continue;
        } 
        newCanPlaceOn.add((Tag)new StringTag(lowerCaseId));
      } 
      tag.put(tagName, (Tag)newCanPlaceOn);
    } 
  }
  
  private void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnch) {
    String key = storedEnch ? "StoredEnchantments" : "Enchantments";
    ListTag enchantments = (ListTag)tag.get(storedEnch ? key : "ench");
    if (enchantments == null)
      return; 
    ListTag newEnchantments = new ListTag(CompoundTag.class);
    boolean dummyEnchant = false;
    if (!storedEnch) {
      IntTag hideFlags = (IntTag)tag.remove(this.extraNbtTag + "|OldHideFlags");
      if (hideFlags != null) {
        tag.put("HideFlags", (Tag)new IntTag(hideFlags.asByte()));
        dummyEnchant = true;
      } else if (tag.remove(this.extraNbtTag + "|DummyEnchant") != null) {
        tag.remove("HideFlags");
        dummyEnchant = true;
      } 
    } 
    for (Tag enchEntry : enchantments) {
      CompoundTag enchantmentEntry = new CompoundTag();
      short oldId = ((NumberTag)((CompoundTag)enchEntry).get("id")).asShort();
      short level = ((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort();
      if (dummyEnchant && oldId == 0 && level == 0)
        continue; 
      String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(Short.valueOf(oldId));
      if (newId == null)
        newId = "viaversion:legacy/" + oldId; 
      enchantmentEntry.put("id", (Tag)new StringTag(newId));
      enchantmentEntry.put("lvl", (Tag)new ShortTag(level));
      newEnchantments.add((Tag)enchantmentEntry);
    } 
    ListTag noMapped = (ListTag)tag.remove(this.extraNbtTag + "|Enchantments");
    if (noMapped != null)
      for (Tag value : noMapped)
        newEnchantments.add(value);  
    CompoundTag display = (CompoundTag)tag.get("display");
    if (display == null)
      tag.put("display", (Tag)(display = new CompoundTag())); 
    ListTag oldLore = (ListTag)tag.remove(this.extraNbtTag + "|OldLore");
    if (oldLore != null) {
      ListTag lore = (ListTag)display.get("Lore");
      if (lore == null)
        tag.put("Lore", (Tag)(lore = new ListTag())); 
      lore.setValue(oldLore.getValue());
    } else if (tag.remove(this.extraNbtTag + "|DummyLore") != null) {
      display.remove("Lore");
      if (display.isEmpty())
        tag.remove("display"); 
    } 
    if (!storedEnch)
      tag.remove("ench"); 
    tag.put(key, (Tag)newEnchantments);
  }
  
  private void invertShieldAndBannerId(Item item, CompoundTag tag) {
    if (item.identifier() != 442 && item.identifier() != 425)
      return; 
    Tag blockEntityTag = tag.get("BlockEntityTag");
    if (!(blockEntityTag instanceof CompoundTag))
      return; 
    CompoundTag blockEntityCompoundTag = (CompoundTag)blockEntityTag;
    Tag base = blockEntityCompoundTag.get("Base");
    if (base instanceof IntTag) {
      IntTag baseTag = (IntTag)base;
      baseTag.setValue(15 - baseTag.asInt());
    } 
    Tag patterns = blockEntityCompoundTag.get("Patterns");
    if (patterns instanceof ListTag) {
      ListTag patternsTag = (ListTag)patterns;
      for (Tag pattern : patternsTag) {
        if (!(pattern instanceof CompoundTag))
          continue; 
        IntTag colorTag = (IntTag)((CompoundTag)pattern).get("Color");
        colorTag.setValue(15 - colorTag.asInt());
      } 
    } 
  }
  
  private static void flowerPotSpecialTreatment(UserConnection user, int blockState, Position position) throws Exception {
    if (FlowerPotHandler.isFlowah(blockState)) {
      BackwardsBlockEntityProvider beProvider = (BackwardsBlockEntityProvider)Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
      CompoundTag nbt = beProvider.transform(user, position, "minecraft:flower_pot");
      PacketWrapper blockUpdateRemove = PacketWrapper.create(11, null, user);
      blockUpdateRemove.write(Type.POSITION, position);
      blockUpdateRemove.write((Type)Type.VAR_INT, Integer.valueOf(0));
      blockUpdateRemove.scheduleSend(Protocol1_12_2To1_13.class);
      PacketWrapper blockCreate = PacketWrapper.create(11, null, user);
      blockCreate.write(Type.POSITION, position);
      blockCreate.write((Type)Type.VAR_INT, Integer.valueOf(Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(blockState)));
      blockCreate.scheduleSend(Protocol1_12_2To1_13.class);
      PacketWrapper wrapper = PacketWrapper.create(9, null, user);
      wrapper.write(Type.POSITION, position);
      wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)5));
      wrapper.write(Type.NBT, nbt);
      wrapper.scheduleSend(Protocol1_12_2To1_13.class);
    } 
  }
}
