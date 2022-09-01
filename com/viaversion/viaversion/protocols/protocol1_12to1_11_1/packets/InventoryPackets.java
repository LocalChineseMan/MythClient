package com.viaversion.viaversion.protocols.protocol1_12to1_11_1.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.BedRewriter;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets extends ItemRewriter<Protocol1_12To1_11_1> {
  public InventoryPackets(Protocol1_12To1_11_1 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_9_3.SET_SLOT, Type.ITEM);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_9_3.WINDOW_ITEMS, Type.ITEM_ARRAY);
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
    ((Protocol1_12To1_11_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM)); 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    ((Protocol1_12To1_11_1)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_12.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = (Item)wrapper.get(Type.ITEM, 0);
                    if (!Via.getConfig().is1_12QuickMoveActionFix()) {
                      BedRewriter.toServerItem(item);
                      return;
                    } 
                    byte button = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                    int mode = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (mode == 1 && button == 0 && item == null) {
                      short windowId = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                      short slotId = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      short actionId = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                      InventoryQuickMoveProvider provider = (InventoryQuickMoveProvider)Via.getManager().getProviders().get(InventoryQuickMoveProvider.class);
                      boolean succeed = provider.registerQuickMoveAction(windowId, slotId, actionId, wrapper.user());
                      if (succeed)
                        wrapper.cancel(); 
                    } else {
                      BedRewriter.toServerItem(item);
                    } 
                  }
                });
          }
        });
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_12.CREATIVE_INVENTORY_ACTION, Type.ITEM);
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    BedRewriter.toServerItem(item);
    boolean newItem = (item.identifier() >= 235 && item.identifier() <= 252);
    int i = newItem | ((item.identifier() == 453) ? 1 : 0);
    if (i != 0) {
      item.setIdentifier(1);
      item.setData((short)0);
    } 
    return item;
  }
}
