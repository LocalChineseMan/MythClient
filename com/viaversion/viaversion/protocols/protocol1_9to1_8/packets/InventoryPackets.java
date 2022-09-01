package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.InventoryTracker;

public class InventoryPackets {
  public static void register(Protocol protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short windowId = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    short value = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equalsIgnoreCase("minecraft:enchanting_table") && 
                      property > 3 && property < 7) {
                      short level = (short)(value >> 8);
                      short enchantID = (short)(value & 0xFF);
                      wrapper.create(wrapper.getId(), (PacketHandler)new Object(this, windowId, property, enchantID))
                        
                        .scheduleSend(Protocol1_9To1_8.class);
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(property + 3)));
                      wrapper.set((Type)Type.SHORT, 1, Short.valueOf(level));
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String inventory = (String)wrapper.get(Type.STRING, 0);
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(inventory);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String inventory = (String)wrapper.get(Type.STRING, 0);
                    if (inventory.equals("minecraft:brewing_stand"))
                      wrapper.set((Type)Type.UNSIGNED_BYTE, 1, Short.valueOf((short)(((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 1)).shortValue() + 1))); 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    boolean showShieldWhenSwordInHand = (Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking());
                    if (showShieldWhenSwordInHand) {
                      InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      short slotID = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      byte windowId = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).byteValue();
                      inventoryTracker.setItemId((short)windowId, slotID, (stack == null) ? 0 : stack.identifier());
                      entityTracker.syncShieldWithSword();
                    } 
                    ItemRewriter.toClient(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    short slotID = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand") && 
                      slotID >= 4)
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(slotID + 1))); 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.ITEM_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item[] stacks = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                    Short windowId = (Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0);
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    boolean showShieldWhenSwordInHand = (Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking());
                    short i;
                    for (i = 0; i < stacks.length; i = (short)(i + 1)) {
                      Item stack = stacks[i];
                      if (showShieldWhenSwordInHand)
                        inventoryTracker.setItemId(windowId.shortValue(), i, (stack == null) ? 0 : stack.identifier()); 
                      ItemRewriter.toClient(stack);
                    } 
                    if (showShieldWhenSwordInHand)
                      entityTracker.syncShieldWithSword(); 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                      Item[] oldStack = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                      Item[] newStack = new Item[oldStack.length + 1];
                      for (int i = 0; i < newStack.length; i++) {
                        if (i > 4) {
                          newStack[i] = oldStack[i - 1];
                        } else if (i != 4) {
                          newStack[i] = oldStack[i];
                        } 
                      } 
                      wrapper.set(Type.ITEM_ARRAY, 0, newStack);
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(null);
                    inventoryTracker.resetInventory(((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue());
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    boolean showShieldWhenSwordInHand = (Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking());
                    if (showShieldWhenSwordInHand) {
                      InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      short slotID = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      inventoryTracker.setItemId((short)0, slotID, (stack == null) ? 0 : stack.identifier());
                      entityTracker.syncShieldWithSword();
                    } 
                    ItemRewriter.toServer(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short slot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    boolean throwItem = (slot == 45);
                    if (throwItem) {
                      wrapper.create((PacketType)ClientboundPackets1_9.SET_SLOT, (PacketHandler)new Object(this, slot))
                        
                        .send(Protocol1_9To1_8.class);
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)-999));
                    } 
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT, (Type)Type.BYTE);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    if (Via.getConfig().isShowShieldWhenSwordInHand()) {
                      Short windowId = (Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0);
                      byte mode = ((Byte)wrapper.get((Type)Type.BYTE, 1)).byteValue();
                      short hoverSlot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      byte button = ((Byte)wrapper.get((Type)Type.BYTE, 0)).byteValue();
                      InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                      inventoryTracker.handleWindowClick(wrapper.user(), windowId.shortValue(), mode, hoverSlot, button);
                    } 
                    ItemRewriter.toServer(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short windowID = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    short slot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    boolean throwItem = (slot == 45 && windowID == 0);
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                      if (slot == 4)
                        throwItem = true; 
                      if (slot > 4)
                        wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(slot - 1))); 
                    } 
                    if (throwItem) {
                      wrapper.create((PacketType)ClientboundPackets1_9.SET_SLOT, (PacketHandler)new Object(this, windowID, slot))
                        
                        .scheduleSend(Protocol1_9To1_8.class);
                      wrapper.set((Type)Type.BYTE, 0, Byte.valueOf((byte)0));
                      wrapper.set((Type)Type.BYTE, 1, Byte.valueOf((byte)0));
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)-999));
                    } 
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(null);
                    inventoryTracker.resetInventory(((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue());
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.HELD_ITEM_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    boolean showShieldWhenSwordInHand = (Via.getConfig().isShowShieldWhenSwordInHand() && Via.getConfig().isShieldBlocking());
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (entityTracker.isBlocking()) {
                      entityTracker.setBlocking(false);
                      if (!showShieldWhenSwordInHand)
                        entityTracker.setSecondHand(null); 
                    } 
                    if (showShieldWhenSwordInHand) {
                      entityTracker.setHeldItemSlot(((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue());
                      entityTracker.syncShieldWithSword();
                    } 
                  }
                });
          }
        });
  }
}
