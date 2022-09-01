package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.google.common.collect.Sets;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryPackets extends ItemRewriter<Protocol1_14To1_13_2> {
  private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_14To1_13_2.class.getSimpleName();
  
  private static final Set<String> REMOVED_RECIPE_TYPES = Sets.newHashSet((Object[])new String[] { "crafting_special_banneraddpattern", "crafting_special_repairitem" });
  
  private static final ComponentRewriter COMPONENT_REWRITER = new ComponentRewriter() {
      protected void handleTranslate(JsonObject object, String translate) {
        super.handleTranslate(object, translate);
        if (translate.startsWith("block.") && translate.endsWith(".name"))
          object.addProperty("translate", translate.substring(0, translate.length() - 5)); 
      }
    };
  
  public InventoryPackets(Protocol1_14To1_13_2 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_13.COOLDOWN);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_13.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_14To1_13_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.OPEN_WINDOW, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Short windowId = (Short)wrapper.read((Type)Type.UNSIGNED_BYTE);
                    String type = (String)wrapper.read(Type.STRING);
                    JsonElement title = (JsonElement)wrapper.read(Type.COMPONENT);
                    InventoryPackets.COMPONENT_REWRITER.processText(title);
                    Short slots = (Short)wrapper.read((Type)Type.UNSIGNED_BYTE);
                    if (type.equals("EntityHorse")) {
                      wrapper.setId(31);
                      int entityId = ((Integer)wrapper.read((Type)Type.INT)).intValue();
                      wrapper.write((Type)Type.UNSIGNED_BYTE, windowId);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(slots.intValue()));
                      wrapper.write((Type)Type.INT, Integer.valueOf(entityId));
                    } else {
                      wrapper.setId(46);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(windowId.intValue()));
                      int typeId = -1;
                      switch (type) {
                        case "minecraft:crafting_table":
                          typeId = 11;
                          break;
                        case "minecraft:furnace":
                          typeId = 13;
                          break;
                        case "minecraft:dropper":
                        case "minecraft:dispenser":
                          typeId = 6;
                          break;
                        case "minecraft:enchanting_table":
                          typeId = 12;
                          break;
                        case "minecraft:brewing_stand":
                          typeId = 10;
                          break;
                        case "minecraft:villager":
                          typeId = 18;
                          break;
                        case "minecraft:beacon":
                          typeId = 8;
                          break;
                        case "minecraft:anvil":
                          typeId = 7;
                          break;
                        case "minecraft:hopper":
                          typeId = 15;
                          break;
                        case "minecraft:shulker_box":
                          typeId = 19;
                          break;
                        default:
                          if (slots.shortValue() > 0 && slots.shortValue() <= 54)
                            typeId = slots.shortValue() / 9 - 1; 
                          break;
                      } 
                      if (typeId == -1)
                        Via.getPlatform().getLogger().warning("Can't open inventory for 1.14 player! Type: " + type + " Size: " + slots); 
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(typeId));
                      wrapper.write(Type.COMPONENT, title);
                    } 
                  }
                });
          }
        });
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_13.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_13.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_14To1_13_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.get(Type.STRING, 0);
                    if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                      wrapper.setId(39);
                      wrapper.resetReader();
                      wrapper.read(Type.STRING);
                      int windowId = ((Integer)wrapper.read((Type)Type.INT)).intValue();
                      EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                      tracker.setLatestTradeWindowId(windowId);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(windowId));
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)); 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.write((Type)Type.INT, Integer.valueOf(0));
                        wrapper.write((Type)Type.INT, Integer.valueOf(0));
                        wrapper.write((Type)Type.FLOAT, Float.valueOf(0.0F));
                      } 
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    } else if (channel.equals("minecraft:book_open") || channel.equals("book_open")) {
                      int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      wrapper.clearPacket();
                      wrapper.setId(45);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(hand));
                    } 
                  }
                });
          }
        });
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    final RecipeRewriter1_13_2 recipeRewriter = new RecipeRewriter1_13_2(this.protocol);
    ((Protocol1_14To1_13_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.DECLARE_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  int deleted = 0;
                  for (int i = 0; i < size; i++) {
                    String id = (String)wrapper.read(Type.STRING);
                    String type = (String)wrapper.read(Type.STRING);
                    if (InventoryPackets.REMOVED_RECIPE_TYPES.contains(type)) {
                      deleted++;
                    } else {
                      wrapper.write(Type.STRING, type);
                      wrapper.write(Type.STRING, id);
                      recipeRewriter.handle(wrapper, type);
                    } 
                  } 
                  wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(size - deleted));
                });
          }
        });
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_14.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_14To1_13_2)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_14.SELECT_TRADE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    PacketWrapper resyncPacket = wrapper.create(8);
                    EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                    resyncPacket.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)tracker.getLatestTradeWindowId()));
                    resyncPacket.write((Type)Type.SHORT, Short.valueOf((short)-999));
                    resyncPacket.write((Type)Type.BYTE, Byte.valueOf((byte)2));
                    resyncPacket.write((Type)Type.SHORT, Short.valueOf((short)ThreadLocalRandom.current().nextInt()));
                    resyncPacket.write((Type)Type.VAR_INT, Integer.valueOf(5));
                    CompoundTag tag = new CompoundTag();
                    tag.put("force_resync", (Tag)new DoubleTag(Double.NaN));
                    resyncPacket.write(Type.FLAT_VAR_INT_ITEM, new DataItem(1, (byte)1, (short)0, tag));
                    resyncPacket.scheduleSendToServer(Protocol1_14To1_13_2.class);
                  }
                });
          }
        });
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.FLOAT);
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getNewItemId(item.identifier()));
    if (item.tag() == null)
      return item; 
    Tag displayTag = item.tag().get("display");
    if (displayTag instanceof CompoundTag) {
      CompoundTag display = (CompoundTag)displayTag;
      Tag loreTag = display.get("Lore");
      if (loreTag instanceof ListTag) {
        ListTag lore = (ListTag)loreTag;
        display.put(NBT_TAG_NAME + "|Lore", (Tag)new ListTag(lore.clone().getValue()));
        for (Tag loreEntry : lore) {
          if (loreEntry instanceof StringTag) {
            String jsonText = ChatRewriter.legacyTextToJsonString(((StringTag)loreEntry).getValue(), true);
            ((StringTag)loreEntry).setValue(jsonText);
          } 
        } 
      } 
    } 
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getOldItemId(item.identifier()));
    if (item.tag() == null)
      return item; 
    Tag displayTag = item.tag().get("display");
    if (displayTag instanceof CompoundTag) {
      CompoundTag display = (CompoundTag)displayTag;
      Tag loreTag = display.get("Lore");
      if (loreTag instanceof ListTag) {
        ListTag lore = (ListTag)loreTag;
        ListTag savedLore = (ListTag)display.remove(NBT_TAG_NAME + "|Lore");
        if (savedLore != null) {
          display.put("Lore", (Tag)new ListTag(savedLore.getValue()));
        } else {
          for (Tag loreEntry : lore) {
            if (loreEntry instanceof StringTag)
              ((StringTag)loreEntry).setValue(ChatRewriter.jsonToLegacyText(((StringTag)loreEntry).getValue())); 
          } 
        } 
      } 
    } 
    return item;
  }
}
