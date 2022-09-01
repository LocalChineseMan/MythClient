package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public final class InventoryPackets extends ItemRewriter<Protocol1_17To1_16_4> {
  public InventoryPackets(Protocol1_17To1_16_4 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_16_2.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_16_2.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerTradeList((ClientboundPacketType)ClientboundPackets1_16_2.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_16_2.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_16_2.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    registerEntityEquipmentArray((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.DOUBLE);
    (new RecipeRewriter1_16(this.protocol)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_16_2.DECLARE_RECIPES);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_17.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_17To1_16_4)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_17.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_17.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            handler(wrapper -> wrapper.write((Type)Type.SHORT, Short.valueOf((short)0)));
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int length = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < length; i++) {
                    wrapper.read((Type)Type.SHORT);
                    wrapper.read(Type.FLAT_VAR_INT_ITEM);
                  } 
                  Item item = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                  int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  if (action == 5) {
                    item = null;
                  } else {
                    InventoryPackets.this.handleItemToServer(item);
                  } 
                  wrapper.write(Type.FLAT_VAR_INT_ITEM, item);
                });
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.WINDOW_CONFIRMATION, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short inventoryId = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  short confirmationId = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                  boolean accepted = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  if (!accepted) {
                    int id = 0x40000000 | inventoryId << 16 | confirmationId & 0xFFFF;
                    ((InventoryAcknowledgements)wrapper.user().get(InventoryAcknowledgements.class)).addId(id);
                    PacketWrapper pingPacket = wrapper.create((PacketType)ClientboundPackets1_17.PING);
                    pingPacket.write((Type)Type.INT, Integer.valueOf(id));
                    pingPacket.send(Protocol1_17To1_16_4.class);
                  } 
                  wrapper.cancel();
                });
          }
        });
    ((Protocol1_17To1_16_4)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_17.PONG, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int id = ((Integer)wrapper.read((Type)Type.INT)).intValue();
                  if ((id & 0x40000000) != 0 && ((InventoryAcknowledgements)wrapper.user().get(InventoryAcknowledgements.class)).removeId(id)) {
                    short inventoryId = (short)(id >> 16 & 0xFF);
                    short confirmationId = (short)(id & 0xFFFF);
                    PacketWrapper packet = wrapper.create((PacketType)ServerboundPackets1_16_2.WINDOW_CONFIRMATION);
                    packet.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(inventoryId));
                    packet.write((Type)Type.SHORT, Short.valueOf(confirmationId));
                    packet.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                    packet.sendToServer(Protocol1_17To1_16_4.class);
                  } 
                  wrapper.cancel();
                });
          }
        });
  }
}
