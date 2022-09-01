package de.gerrygames.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.items.ItemRewriter;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage.Windows;

public class InventoryPackets {
  public static void register(Protocol<ClientboundPackets1_9, ClientboundPackets1_8, ServerboundPackets1_9, ServerboundPackets1_8> protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  short windowsId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  ((Windows)packetWrapper.user().get(Windows.class)).remove(windowsId);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.COMPONENT);
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  String type = (String)packetWrapper.get(Type.STRING, 0);
                  if (type.equals("EntityHorse"))
                    packetWrapper.passthrough((Type)Type.INT); 
                });
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  String windowType = (String)packetWrapper.get(Type.STRING, 0);
                  ((Windows)packetWrapper.user().get(Windows.class)).put(windowId, windowType);
                });
            handler(packetWrapper -> {
                  String type = (String)packetWrapper.get(Type.STRING, 0);
                  if (type.equalsIgnoreCase("minecraft:shulker_box"))
                    packetWrapper.set(Type.STRING, 0, type = "minecraft:container"); 
                  String name = ((JsonElement)packetWrapper.get(Type.COMPONENT, 0)).toString();
                  if (name.equalsIgnoreCase("{\"translate\":\"container.shulkerBox\"}"))
                    packetWrapper.set(Type.COMPONENT, 0, JsonParser.parseString("{\"text\":\"Shulker Box\"}")); 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  Item[] items = (Item[])packetWrapper.read(Type.ITEM_ARRAY);
                  for (int i = 0; i < items.length; i++)
                    items[i] = ItemRewriter.toClient(items[i]); 
                  if (windowId == 0 && items.length == 46) {
                    Item[] old = items;
                    items = new Item[45];
                    System.arraycopy(old, 0, items, 0, 45);
                  } else {
                    String type = ((Windows)packetWrapper.user().get(Windows.class)).get(windowId);
                    if (type != null && type.equalsIgnoreCase("minecraft:brewing_stand")) {
                      System.arraycopy(items, 0, ((Windows)packetWrapper.user().get(Windows.class)).getBrewingItems(windowId), 0, 4);
                      Windows.updateBrewingStand(packetWrapper.user(), items[4], windowId);
                      Item[] old = items;
                      items = new Item[old.length - 1];
                      System.arraycopy(old, 0, items, 0, 4);
                      System.arraycopy(old, 5, items, 4, old.length - 5);
                    } 
                  } 
                  packetWrapper.write(Type.ITEM_ARRAY, items);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(packetWrapper -> {
                  packetWrapper.set(Type.ITEM, 0, ItemRewriter.toClient((Item)packetWrapper.get(Type.ITEM, 0)));
                  byte windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).byteValue();
                  short slot = ((Short)packetWrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (windowId == 0 && slot == 45) {
                    packetWrapper.cancel();
                    return;
                  } 
                  String type = ((Windows)packetWrapper.user().get(Windows.class)).get((short)windowId);
                  if (type == null)
                    return; 
                  if (type.equalsIgnoreCase("minecraft:brewing_stand"))
                    if (slot > 4) {
                      slot = (short)(slot - 1);
                      packetWrapper.set((Type)Type.SHORT, 0, Short.valueOf(slot));
                    } else {
                      if (slot == 4) {
                        packetWrapper.cancel();
                        Windows.updateBrewingStand(packetWrapper.user(), (Item)packetWrapper.get(Type.ITEM, 0), (short)windowId);
                        return;
                      } 
                      ((Windows)packetWrapper.user().get(Windows.class)).getBrewingItems((short)windowId)[slot] = (Item)packetWrapper.get(Type.ITEM, 0);
                    }  
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_8.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  short windowsId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  ((Windows)packetWrapper.user().get(Windows.class)).remove(windowsId);
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_8.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE, (Type)Type.VAR_INT);
            map(Type.ITEM);
            handler(packetWrapper -> packetWrapper.set(Type.ITEM, 0, ItemRewriter.toServer((Item)packetWrapper.get(Type.ITEM, 0))));
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  Windows windows = (Windows)packetWrapper.user().get(Windows.class);
                  String type = windows.get(windowId);
                  if (type == null)
                    return; 
                  if (type.equalsIgnoreCase("minecraft:brewing_stand")) {
                    short slot = ((Short)packetWrapper.get((Type)Type.SHORT, 0)).shortValue();
                    if (slot > 3) {
                      slot = (short)(slot + 1);
                      packetWrapper.set((Type)Type.SHORT, 0, Short.valueOf(slot));
                    } 
                  } 
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_8.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(packetWrapper -> packetWrapper.set(Type.ITEM, 0, ItemRewriter.toServer((Item)packetWrapper.get(Type.ITEM, 0))));
          }
        });
  }
}
