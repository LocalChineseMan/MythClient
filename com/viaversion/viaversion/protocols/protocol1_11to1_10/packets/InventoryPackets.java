package com.viaversion.viaversion.protocols.protocol1_11to1_10.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets extends ItemRewriter<Protocol1_11To1_10> {
  public InventoryPackets(Protocol1_11To1_10 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_9_3.SET_SLOT, Type.ITEM);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_9_3.WINDOW_ITEMS, Type.ITEM_ARRAY);
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
    ((Protocol1_11To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        EntityIdRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        EntityIdRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          EntityIdRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM)); 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_9_3.CLICK_WINDOW, Type.ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_9_3.CREATIVE_INVENTORY_ACTION, Type.ITEM);
  }
  
  public Item handleItemToClient(Item item) {
    EntityIdRewriter.toClientItem(item);
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    EntityIdRewriter.toServerItem(item);
    if (item == null)
      return null; 
    boolean newItem = (item.identifier() >= 218 && item.identifier() <= 234);
    int i = newItem | ((item.identifier() == 449 || item.identifier() == 450) ? 1 : 0);
    if (i != 0) {
      item.setIdentifier(1);
      item.setData((short)0);
    } 
    return item;
  }
}
