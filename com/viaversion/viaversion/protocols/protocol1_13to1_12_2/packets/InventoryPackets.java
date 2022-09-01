package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SoundSource;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class InventoryPackets extends ItemRewriter<Protocol1_13To1_12_2> {
  private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_13To1_12_2.class.getSimpleName();
  
  public InventoryPackets(Protocol1_13To1_12_2 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    ((Protocol1_13To1_12_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.ITEM, Type.FLAT_ITEM);
            handler(InventoryPackets.this.itemToClientHandler(Type.FLAT_ITEM));
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.ITEM_ARRAY, Type.FLAT_ITEM_ARRAY);
            handler(InventoryPackets.this.itemArrayHandler(Type.FLAT_ITEM_ARRAY));
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    if (property >= 4 && property <= 6)
                      wrapper.set((Type)Type.SHORT, 1, Short.valueOf((short)((Protocol1_13To1_12_2)InventoryPackets.this.protocol).getMappingData().getEnchantmentMappings().getNewId(((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue()))); 
                  }
                });
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.get(Type.STRING, 0);
                    if (channel.equalsIgnoreCase("MC|StopSound")) {
                      String originalSource = (String)wrapper.read(Type.STRING);
                      String originalSound = (String)wrapper.read(Type.STRING);
                      wrapper.clearPacket();
                      wrapper.setId(76);
                      byte flags = 0;
                      wrapper.write((Type)Type.BYTE, Byte.valueOf(flags));
                      if (!originalSource.isEmpty()) {
                        flags = (byte)(flags | 0x1);
                        Optional<SoundSource> finalSource = SoundSource.findBySource(originalSource);
                        if (!finalSource.isPresent()) {
                          if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
                            Via.getPlatform().getLogger().info("Could not handle unknown sound source " + originalSource + " falling back to default: master"); 
                          finalSource = Optional.of(SoundSource.MASTER);
                        } 
                        wrapper.write((Type)Type.VAR_INT, Integer.valueOf(((SoundSource)finalSource.get()).getId()));
                      } 
                      if (!originalSound.isEmpty()) {
                        flags = (byte)(flags | 0x2);
                        wrapper.write(Type.STRING, originalSound);
                      } 
                      wrapper.set((Type)Type.BYTE, 0, Byte.valueOf(flags));
                      return;
                    } 
                    if (channel.equalsIgnoreCase("MC|TrList")) {
                      channel = "minecraft:trader_list";
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        Item input = (Item)wrapper.read(Type.ITEM);
                        InventoryPackets.this.handleItemToClient(input);
                        wrapper.write(Type.FLAT_ITEM, input);
                        Item output = (Item)wrapper.read(Type.ITEM);
                        InventoryPackets.this.handleItemToClient(output);
                        wrapper.write(Type.FLAT_ITEM, output);
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem) {
                          Item second = (Item)wrapper.read(Type.ITEM);
                          InventoryPackets.this.handleItemToClient(second);
                          wrapper.write(Type.FLAT_ITEM, second);
                        } 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                      } 
                    } else {
                      String old = channel;
                      channel = InventoryPackets.getNewPluginChannelId(channel);
                      if (channel == null) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
                          Via.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + old); 
                        wrapper.cancel();
                        return;
                      } 
                      if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                        String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\000");
                        List<String> rewrittenChannels = new ArrayList<>();
                        for (int i = 0; i < channels.length; i++) {
                          String rewritten = InventoryPackets.getNewPluginChannelId(channels[i]);
                          if (rewritten != null) {
                            rewrittenChannels.add(rewritten);
                          } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                            Via.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + channels[i]);
                          } 
                        } 
                        if (!rewrittenChannels.isEmpty()) {
                          wrapper.write(Type.REMAINING_BYTES, Joiner.on(false).join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                        } else {
                          wrapper.cancel();
                          return;
                        } 
                      } 
                    } 
                    wrapper.set(Type.STRING, 0, channel);
                  }
                });
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.ITEM, Type.FLAT_ITEM);
            handler(InventoryPackets.this.itemToClientHandler(Type.FLAT_ITEM));
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(Type.FLAT_ITEM, Type.ITEM);
            handler(InventoryPackets.this.itemToServerHandler(Type.ITEM));
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.get(Type.STRING, 0);
                    String old = channel;
                    channel = InventoryPackets.getOldPluginChannelId(channel);
                    if (channel == null) {
                      if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
                        Via.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + old); 
                      wrapper.cancel();
                      return;
                    } 
                    if (channel.equals("REGISTER") || channel.equals("UNREGISTER")) {
                      String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\000");
                      List<String> rewrittenChannels = new ArrayList<>();
                      for (int i = 0; i < channels.length; i++) {
                        String rewritten = InventoryPackets.getOldPluginChannelId(channels[i]);
                        if (rewritten != null) {
                          rewrittenChannels.add(rewritten);
                        } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                          Via.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + channels[i]);
                        } 
                      } 
                      wrapper.write(Type.REMAINING_BYTES, Joiner.on(false).join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                    } 
                    wrapper.set(Type.STRING, 0, channel);
                  }
                });
          }
        });
    ((Protocol1_13To1_12_2)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Type.FLAT_ITEM, Type.ITEM);
            handler(InventoryPackets.this.itemToServerHandler(Type.ITEM));
          }
        });
  }
  
  public Item handleItemToClient(Item item) {
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
      boolean banner = (item.identifier() == 425);
      if ((banner || item.identifier() == 442) && 
        tag.get("BlockEntityTag") instanceof CompoundTag) {
        CompoundTag blockEntityTag = (CompoundTag)tag.get("BlockEntityTag");
        if (blockEntityTag.get("Base") instanceof IntTag) {
          IntTag base = (IntTag)blockEntityTag.get("Base");
          if (banner)
            rawId = 6800 + base.asInt(); 
          base.setValue(15 - base.asInt());
        } 
        if (blockEntityTag.get("Patterns") instanceof ListTag)
          for (Tag pattern : blockEntityTag.get("Patterns")) {
            if (pattern instanceof CompoundTag) {
              IntTag c = (IntTag)((CompoundTag)pattern).get("Color");
              c.setValue(15 - c.asInt());
            } 
          }  
      } 
      if (tag.get("display") instanceof CompoundTag) {
        CompoundTag display = (CompoundTag)tag.get("display");
        if (display.get("Name") instanceof StringTag) {
          StringTag name = (StringTag)display.get("Name");
          display.put(NBT_TAG_NAME + "|Name", (Tag)new StringTag(name.getValue()));
          name.setValue(ChatRewriter.legacyTextToJsonString(name.getValue(), true));
        } 
      } 
      if (tag.get("ench") instanceof ListTag) {
        ListTag ench = (ListTag)tag.get("ench");
        ListTag enchantments = new ListTag(CompoundTag.class);
        for (Tag enchEntry : ench) {
          NumberTag idTag;
          if (enchEntry instanceof CompoundTag && (idTag = (NumberTag)((CompoundTag)enchEntry).get("id")) != null) {
            CompoundTag enchantmentEntry = new CompoundTag();
            short oldId = idTag.asShort();
            String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(Short.valueOf(oldId));
            if (newId == null)
              newId = "viaversion:legacy/" + oldId; 
            enchantmentEntry.put("id", (Tag)new StringTag(newId));
            enchantmentEntry.put("lvl", (Tag)new ShortTag(((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort()));
            enchantments.add((Tag)enchantmentEntry);
          } 
        } 
        tag.remove("ench");
        tag.put("Enchantments", (Tag)enchantments);
      } 
      if (tag.get("StoredEnchantments") instanceof ListTag) {
        ListTag storedEnch = (ListTag)tag.get("StoredEnchantments");
        ListTag newStoredEnch = new ListTag(CompoundTag.class);
        for (Tag enchEntry : storedEnch) {
          if (enchEntry instanceof CompoundTag) {
            CompoundTag enchantmentEntry = new CompoundTag();
            short oldId = ((NumberTag)((CompoundTag)enchEntry).get("id")).asShort();
            String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(Short.valueOf(oldId));
            if (newId == null)
              newId = "viaversion:legacy/" + oldId; 
            enchantmentEntry.put("id", (Tag)new StringTag(newId));
            enchantmentEntry.put("lvl", (Tag)new ShortTag(((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort()));
            newStoredEnch.add((Tag)enchantmentEntry);
          } 
        } 
        tag.remove("StoredEnchantments");
        tag.put("StoredEnchantments", (Tag)newStoredEnch);
      } 
      if (tag.get("CanPlaceOn") instanceof ListTag) {
        ListTag old = (ListTag)tag.get("CanPlaceOn");
        ListTag newCanPlaceOn = new ListTag(StringTag.class);
        tag.put(NBT_TAG_NAME + "|CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue((Tag)old)));
        for (Tag oldTag : old) {
          Object value = oldTag.getValue();
          String oldId = value.toString().replace("minecraft:", "");
          String numberConverted = (String)BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
          if (numberConverted != null)
            oldId = numberConverted; 
          String[] newValues = (String[])BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT));
          if (newValues != null) {
            for (String newValue : newValues)
              newCanPlaceOn.add((Tag)new StringTag(newValue)); 
            continue;
          } 
          newCanPlaceOn.add((Tag)new StringTag(oldId.toLowerCase(Locale.ROOT)));
        } 
        tag.put("CanPlaceOn", (Tag)newCanPlaceOn);
      } 
      if (tag.get("CanDestroy") instanceof ListTag) {
        ListTag old = (ListTag)tag.get("CanDestroy");
        ListTag newCanDestroy = new ListTag(StringTag.class);
        tag.put(NBT_TAG_NAME + "|CanDestroy", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue((Tag)old)));
        for (Tag oldTag : old) {
          Object value = oldTag.getValue();
          String oldId = value.toString().replace("minecraft:", "");
          String numberConverted = (String)BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
          if (numberConverted != null)
            oldId = numberConverted; 
          String[] newValues = (String[])BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT));
          if (newValues != null) {
            for (String newValue : newValues)
              newCanDestroy.add((Tag)new StringTag(newValue)); 
            continue;
          } 
          newCanDestroy.add((Tag)new StringTag(oldId.toLowerCase(Locale.ROOT)));
        } 
        tag.put("CanDestroy", (Tag)newCanDestroy);
      } 
      if (item.identifier() == 383)
        if (tag.get("EntityTag") instanceof CompoundTag) {
          CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
          if (entityTag.get("id") instanceof StringTag) {
            StringTag identifier = (StringTag)entityTag.get("id");
            rawId = SpawnEggRewriter.getSpawnEggId(identifier.getValue());
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
        } else {
          rawId = 25100288;
        }  
      if (tag.isEmpty())
        item.setTag(tag = null); 
    } 
    if (!Protocol1_13To1_12_2.MAPPINGS.getItemMappings().containsKey(rawId)) {
      if (!isDamageable(item.identifier()) && item.identifier() != 358) {
        if (tag == null)
          item.setTag(tag = new CompoundTag()); 
        tag.put(NBT_TAG_NAME, (Tag)new IntTag(originalId));
      } 
      if (item.identifier() == 31 && item.data() == 0) {
        rawId = 512;
      } else if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().containsKey(rawId & 0xFFFFFFF0)) {
        rawId &= 0xFFFFFFF0;
      } else {
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
          Via.getPlatform().getLogger().warning("Failed to get 1.13 item for " + item.identifier()); 
        rawId = 16;
      } 
    } 
    item.setIdentifier(Protocol1_13To1_12_2.MAPPINGS.getItemMappings().get(rawId));
    item.setData((short)0);
    return item;
  }
  
  public static String getNewPluginChannelId(String old) {
    switch (old) {
      case "MC|TrList":
        return "minecraft:trader_list";
      case "MC|Brand":
        return "minecraft:brand";
      case "MC|BOpen":
        return "minecraft:book_open";
      case "MC|DebugPath":
        return "minecraft:debug/paths";
      case "MC|DebugNeighborsUpdate":
        return "minecraft:debug/neighbors_update";
      case "REGISTER":
        return "minecraft:register";
      case "UNREGISTER":
        return "minecraft:unregister";
      case "BungeeCord":
        return "bungeecord:main";
      case "bungeecord:main":
        return null;
    } 
    String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().get(old);
    if (mappedChannel != null)
      return mappedChannel; 
    return MappingData.isValid1_13Channel(old) ? old : null;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    Integer rawId = null;
    boolean gotRawIdFromTag = false;
    CompoundTag tag = item.tag();
    if (tag != null)
      if (tag.get(NBT_TAG_NAME) instanceof IntTag) {
        rawId = Integer.valueOf(((NumberTag)tag.get(NBT_TAG_NAME)).asInt());
        tag.remove(NBT_TAG_NAME);
        gotRawIdFromTag = true;
      }  
    if (rawId == null) {
      int oldId = Protocol1_13To1_12_2.MAPPINGS.getItemMappings().inverse().get(item.identifier());
      if (oldId != -1) {
        Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
        if (eggEntityId.isPresent()) {
          rawId = Integer.valueOf(25100288);
          if (tag == null)
            item.setTag(tag = new CompoundTag()); 
          if (!tag.contains("EntityTag")) {
            CompoundTag entityTag = new CompoundTag();
            entityTag.put("id", (Tag)new StringTag(eggEntityId.get()));
            tag.put("EntityTag", (Tag)entityTag);
          } 
        } else {
          rawId = Integer.valueOf(oldId >> 4 << 16 | oldId & 0xF);
        } 
      } 
    } 
    if (rawId == null) {
      if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Failed to get 1.12 item for " + item.identifier()); 
      rawId = Integer.valueOf(65536);
    } 
    item.setIdentifier((short)(rawId.intValue() >> 16));
    item.setData((short)(rawId.intValue() & 0xFFFF));
    if (tag != null) {
      if (isDamageable(item.identifier()) && 
        tag.get("Damage") instanceof IntTag) {
        if (!gotRawIdFromTag)
          item.setData((short)((Integer)tag.get("Damage").getValue()).intValue()); 
        tag.remove("Damage");
      } 
      if (item.identifier() == 358 && 
        tag.get("map") instanceof IntTag) {
        if (!gotRawIdFromTag)
          item.setData((short)((Integer)tag.get("map").getValue()).intValue()); 
        tag.remove("map");
      } 
      if ((item.identifier() == 442 || item.identifier() == 425) && 
        tag.get("BlockEntityTag") instanceof CompoundTag) {
        CompoundTag blockEntityTag = (CompoundTag)tag.get("BlockEntityTag");
        if (blockEntityTag.get("Base") instanceof IntTag) {
          IntTag base = (IntTag)blockEntityTag.get("Base");
          base.setValue(15 - base.asInt());
        } 
        if (blockEntityTag.get("Patterns") instanceof ListTag)
          for (Tag pattern : blockEntityTag.get("Patterns")) {
            if (pattern instanceof CompoundTag) {
              IntTag c = (IntTag)((CompoundTag)pattern).get("Color");
              c.setValue(15 - c.asInt());
            } 
          }  
      } 
      if (tag.get("display") instanceof CompoundTag) {
        CompoundTag display = (CompoundTag)tag.get("display");
        if (display.get("Name") instanceof StringTag) {
          StringTag name = (StringTag)display.get("Name");
          StringTag via = (StringTag)display.remove(NBT_TAG_NAME + "|Name");
          name.setValue((via != null) ? via.getValue() : ChatRewriter.jsonToLegacyText(name.getValue()));
        } 
      } 
      if (tag.get("Enchantments") instanceof ListTag) {
        ListTag enchantments = (ListTag)tag.get("Enchantments");
        ListTag ench = new ListTag(CompoundTag.class);
        for (Tag enchantmentEntry : enchantments) {
          if (enchantmentEntry instanceof CompoundTag) {
            CompoundTag enchEntry = new CompoundTag();
            String newId = (String)((CompoundTag)enchantmentEntry).get("id").getValue();
            Short oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
            if (oldId == null && newId.startsWith("viaversion:legacy/"))
              oldId = Short.valueOf(newId.substring(18)); 
            if (oldId != null) {
              enchEntry.put("id", (Tag)new ShortTag(oldId.shortValue()));
              enchEntry.put("lvl", (Tag)new ShortTag(((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort()));
              ench.add((Tag)enchEntry);
            } 
          } 
        } 
        tag.remove("Enchantments");
        tag.put("ench", (Tag)ench);
      } 
      if (tag.get("StoredEnchantments") instanceof ListTag) {
        ListTag storedEnch = (ListTag)tag.get("StoredEnchantments");
        ListTag newStoredEnch = new ListTag(CompoundTag.class);
        for (Tag enchantmentEntry : storedEnch) {
          if (enchantmentEntry instanceof CompoundTag) {
            CompoundTag enchEntry = new CompoundTag();
            String newId = (String)((CompoundTag)enchantmentEntry).get("id").getValue();
            Short oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
            if (oldId == null && newId.startsWith("viaversion:legacy/"))
              oldId = Short.valueOf(newId.substring(18)); 
            if (oldId != null) {
              enchEntry.put("id", (Tag)new ShortTag(oldId.shortValue()));
              enchEntry.put("lvl", (Tag)new ShortTag(((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort()));
              newStoredEnch.add((Tag)enchEntry);
            } 
          } 
        } 
        tag.remove("StoredEnchantments");
        tag.put("StoredEnchantments", (Tag)newStoredEnch);
      } 
      if (tag.get(NBT_TAG_NAME + "|CanPlaceOn") instanceof ListTag) {
        tag.put("CanPlaceOn", ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(tag.get(NBT_TAG_NAME + "|CanPlaceOn"))));
        tag.remove(NBT_TAG_NAME + "|CanPlaceOn");
      } else if (tag.get("CanPlaceOn") instanceof ListTag) {
        ListTag old = (ListTag)tag.get("CanPlaceOn");
        ListTag newCanPlaceOn = new ListTag(StringTag.class);
        for (Tag oldTag : old) {
          Object value = oldTag.getValue();
          String[] newValues = (String[])BlockIdData.fallbackReverseMapping.get((value instanceof String) ? (
              (String)value).replace("minecraft:", "") : 
              null);
          if (newValues != null) {
            for (String newValue : newValues)
              newCanPlaceOn.add((Tag)new StringTag(newValue)); 
            continue;
          } 
          newCanPlaceOn.add(oldTag);
        } 
        tag.put("CanPlaceOn", (Tag)newCanPlaceOn);
      } 
      if (tag.get(NBT_TAG_NAME + "|CanDestroy") instanceof ListTag) {
        tag.put("CanDestroy", ConverterRegistry.convertToTag(
              ConverterRegistry.convertToValue(tag.get(NBT_TAG_NAME + "|CanDestroy"))));
        tag.remove(NBT_TAG_NAME + "|CanDestroy");
      } else if (tag.get("CanDestroy") instanceof ListTag) {
        ListTag old = (ListTag)tag.get("CanDestroy");
        ListTag newCanDestroy = new ListTag(StringTag.class);
        for (Tag oldTag : old) {
          Object value = oldTag.getValue();
          String[] newValues = (String[])BlockIdData.fallbackReverseMapping.get((value instanceof String) ? (
              (String)value).replace("minecraft:", "") : 
              null);
          if (newValues != null) {
            for (String newValue : newValues)
              newCanDestroy.add((Tag)new StringTag(newValue)); 
            continue;
          } 
          newCanDestroy.add(oldTag);
        } 
        tag.put("CanDestroy", (Tag)newCanDestroy);
      } 
    } 
    return item;
  }
  
  public static String getOldPluginChannelId(String newId) {
    newId = MappingData.validateNewChannel(newId);
    if (newId == null)
      return null; 
    switch (newId) {
      case "minecraft:trader_list":
        return "MC|TrList";
      case "minecraft:book_open":
        return "MC|BOpen";
      case "minecraft:debug/paths":
        return "MC|DebugPath";
      case "minecraft:debug/neighbors_update":
        return "MC|DebugNeighborsUpdate";
      case "minecraft:register":
        return "REGISTER";
      case "minecraft:unregister":
        return "UNREGISTER";
      case "minecraft:brand":
        return "MC|Brand";
      case "bungeecord:main":
        return "BungeeCord";
    } 
    String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().inverse().get(newId);
    if (mappedChannel != null)
      return mappedChannel; 
    return (newId.length() > 20) ? newId.substring(0, 20) : newId;
  }
  
  public static boolean isDamageable(int id) {
    return ((id >= 256 && id <= 259) || id == 261 || (id >= 267 && id <= 279) || (id >= 283 && id <= 286) || (id >= 290 && id <= 294) || (id >= 298 && id <= 317) || id == 346 || id == 359 || id == 398 || id == 442 || id == 443);
  }
}
