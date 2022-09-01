package com.viaversion.viabackwards.protocol.protocol1_13to1_13_1.packets;

import com.viaversion.viabackwards.protocol.protocol1_13to1_13_1.Protocol1_13To1_13_1;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets1_13_1 extends ItemRewriter<Protocol1_13To1_13_1> {
  public InventoryPackets1_13_1(Protocol1_13To1_13_1 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_13.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_13.WINDOW_ITEMS, Type.FLAT_ITEM_ARRAY);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_13.SET_SLOT, Type.FLAT_ITEM);
    ((Protocol1_13To1_13_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.passthrough(Type.STRING);
                    if (channel.equals("minecraft:trader_list")) {
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        Item input = (Item)wrapper.passthrough(Type.FLAT_ITEM);
                        InventoryPackets1_13_1.this.handleItemToClient(input);
                        Item output = (Item)wrapper.passthrough(Type.FLAT_ITEM);
                        InventoryPackets1_13_1.this.handleItemToClient(output);
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem) {
                          Item second = (Item)wrapper.passthrough(Type.FLAT_ITEM);
                          InventoryPackets1_13_1.this.handleItemToClient(second);
                        } 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.FLAT_ITEM);
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_13.CLICK_WINDOW, Type.FLAT_ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, Type.FLAT_ITEM);
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PARTICLE, Type.FLAT_ITEM, (Type)Type.FLOAT);
  }
}
