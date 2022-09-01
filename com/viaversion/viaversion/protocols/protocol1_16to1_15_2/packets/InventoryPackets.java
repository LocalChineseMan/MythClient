package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.UUIDIntArrayType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import java.util.UUID;

public class InventoryPackets extends ItemRewriter<Protocol1_16To1_15_2> {
  public InventoryPackets(Protocol1_16To1_15_2 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    final PacketHandler cursorRemapper = wrapper -> {
        PacketWrapper clearPacket = wrapper.create((PacketType)ClientboundPackets1_16.SET_SLOT);
        clearPacket.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)-1));
        clearPacket.write((Type)Type.SHORT, Short.valueOf((short)-1));
        clearPacket.write(Type.FLAT_VAR_INT_ITEM, null);
        clearPacket.send(Protocol1_16To1_15_2.class);
      };
    ((Protocol1_16To1_15_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.COMPONENT);
            handler(wrapper -> {
                  int windowType = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                  if (windowType >= 20)
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(++windowType)); 
                });
            handler(cursorRemapper);
          }
        });
    ((Protocol1_16To1_15_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(cursorRemapper);
          }
        });
    ((Protocol1_16To1_15_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(wrapper -> {
                  short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (property >= 4 && property <= 6) {
                    short enchantmentId = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                    if (enchantmentId >= 11) {
                      enchantmentId = (short)(enchantmentId + 1);
                      wrapper.set((Type)Type.SHORT, 1, Short.valueOf(enchantmentId));
                    } 
                  } 
                });
          }
        });
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_15.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_15.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerTradeList((ClientboundPacketType)ClientboundPackets1_15.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_15.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_15.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_16To1_15_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int slot = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)slot));
                  InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                });
          }
        });
    (new RecipeRewriter1_14(this.protocol)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_15.DECLARE_RECIPES);
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_16.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_16To1_15_2)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_16.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.DOUBLE);
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    if (item.identifier() == 771 && item.tag() != null) {
      CompoundTag tag = item.tag();
      Tag ownerTag = tag.get("SkullOwner");
      if (ownerTag instanceof CompoundTag) {
        CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
        Tag idTag = ownerCompundTag.get("Id");
        if (idTag instanceof StringTag) {
          UUID id = UUID.fromString((String)idTag.getValue());
          ownerCompundTag.put("Id", (Tag)new IntArrayTag(UUIDIntArrayType.uuidToIntArray(id)));
        } 
      } 
    } 
    oldToNewAttributes(item);
    item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getNewItemId(item.identifier()));
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getOldItemId(item.identifier()));
    if (item.identifier() == 771 && item.tag() != null) {
      CompoundTag tag = item.tag();
      Tag ownerTag = tag.get("SkullOwner");
      if (ownerTag instanceof CompoundTag) {
        CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
        Tag idTag = ownerCompundTag.get("Id");
        if (idTag instanceof IntArrayTag) {
          UUID id = UUIDIntArrayType.uuidFromIntArray((int[])idTag.getValue());
          ownerCompundTag.put("Id", (Tag)new StringTag(id.toString()));
        } 
      } 
    } 
    newToOldAttributes(item);
    return item;
  }
  
  public static void oldToNewAttributes(Item item) {
    if (item.tag() == null)
      return; 
    ListTag attributes = (ListTag)item.tag().get("AttributeModifiers");
    if (attributes == null)
      return; 
    for (Tag tag : attributes) {
      CompoundTag attribute = (CompoundTag)tag;
      rewriteAttributeName(attribute, "AttributeName", false);
      rewriteAttributeName(attribute, "Name", false);
      Tag leastTag = attribute.get("UUIDLeast");
      if (leastTag != null) {
        Tag mostTag = attribute.get("UUIDMost");
        int[] uuidIntArray = UUIDIntArrayType.bitsToIntArray(((NumberTag)leastTag).asLong(), ((NumberTag)mostTag).asLong());
        attribute.put("UUID", (Tag)new IntArrayTag(uuidIntArray));
      } 
    } 
  }
  
  public static void newToOldAttributes(Item item) {
    if (item.tag() == null)
      return; 
    ListTag attributes = (ListTag)item.tag().get("AttributeModifiers");
    if (attributes == null)
      return; 
    for (Tag tag : attributes) {
      CompoundTag attribute = (CompoundTag)tag;
      rewriteAttributeName(attribute, "AttributeName", true);
      rewriteAttributeName(attribute, "Name", true);
      IntArrayTag uuidTag = (IntArrayTag)attribute.get("UUID");
      if (uuidTag != null && (uuidTag.getValue()).length == 4) {
        UUID uuid = UUIDIntArrayType.uuidFromIntArray(uuidTag.getValue());
        attribute.put("UUIDLeast", (Tag)new LongTag(uuid.getLeastSignificantBits()));
        attribute.put("UUIDMost", (Tag)new LongTag(uuid.getMostSignificantBits()));
      } 
    } 
  }
  
  public static void rewriteAttributeName(CompoundTag compoundTag, String entryName, boolean inverse) {
    StringTag attributeNameTag = (StringTag)compoundTag.get(entryName);
    if (attributeNameTag == null)
      return; 
    String attributeName = attributeNameTag.getValue();
    if (inverse && !attributeName.startsWith("minecraft:"))
      attributeName = "minecraft:" + attributeName; 
    String mappedAttribute = (String)(inverse ? Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings().inverse() : Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings()).get(attributeName);
    if (mappedAttribute == null)
      return; 
    attributeNameTag.setValue(mappedAttribute);
  }
}
