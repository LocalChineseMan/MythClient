package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.ServerboundPackets1_7;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items.ItemRewriter;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Windows;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import de.gerrygames.viarewind.utils.ChatUtil;
import java.util.UUID;

public class InventoryPackets {
  public static void register(Protocol1_7_6_10TO1_8 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  String windowType = (String)packetWrapper.read(Type.STRING);
                  short windowtypeId = (short)Windows.getInventoryType(windowType);
                  ((Windows)packetWrapper.user().get(Windows.class)).types.put(Short.valueOf(windowId), Short.valueOf(windowtypeId));
                  packetWrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(windowtypeId));
                  JsonElement titleComponent = (JsonElement)packetWrapper.read(Type.COMPONENT);
                  String title = ChatUtil.jsonToLegacy(titleComponent);
                  title = ChatUtil.removeUnusedColor(title, '8');
                  if (title.length() > 32)
                    title = title.substring(0, 32); 
                  packetWrapper.write(Type.STRING, title);
                  packetWrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                  packetWrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
                  if (windowtypeId == 11)
                    packetWrapper.passthrough((Type)Type.INT); 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  short windowsId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  ((Windows)packetWrapper.user().get(Windows.class)).remove(windowsId);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  short windowType = ((Windows)packetWrapper.user().get(Windows.class)).get(windowId);
                  packetWrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(windowId));
                  short slot = ((Short)packetWrapper.read((Type)Type.SHORT)).shortValue();
                  if (windowType == 4) {
                    if (slot == 1) {
                      packetWrapper.cancel();
                      return;
                    } 
                    if (slot >= 2)
                      slot = (short)(slot - 1); 
                  } 
                  packetWrapper.write((Type)Type.SHORT, Short.valueOf(slot));
                });
            map(Type.ITEM, Types1_7_6_10.COMPRESSED_NBT_ITEM);
            handler(packetWrapper -> {
                  Item item = (Item)packetWrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0);
                  ItemRewriter.toClient(item);
                  packetWrapper.set(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0, item);
                });
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  if (windowId != 0)
                    return; 
                  short slot = ((Short)packetWrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (slot < 5 || slot > 8)
                    return; 
                  Item item = (Item)packetWrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0);
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  UUID uuid = packetWrapper.user().getProtocolInfo().getUuid();
                  Item[] equipment = tracker.getPlayerEquipment(uuid);
                  if (equipment == null)
                    tracker.setPlayerEquipment(uuid, equipment = new Item[5]); 
                  equipment[9 - slot] = item;
                  if (tracker.getGamemode() == 3)
                    packetWrapper.cancel(); 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  short windowType = ((Windows)packetWrapper.user().get(Windows.class)).get(windowId);
                  packetWrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(windowId));
                  Item[] items = (Item[])packetWrapper.read(Type.ITEM_ARRAY);
                  if (windowType == 4) {
                    Item[] old = items;
                    items = new Item[old.length - 1];
                    items[0] = old[0];
                    System.arraycopy(old, 2, items, 1, old.length - 3);
                  } 
                  for (int i = 0; i < items.length; i++)
                    items[i] = ItemRewriter.toClient(items[i]); 
                  packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY, items);
                });
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  if (windowId != 0)
                    return; 
                  Item[] items = (Item[])packetWrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY, 0);
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  UUID uuid = packetWrapper.user().getProtocolInfo().getUuid();
                  Item[] equipment = tracker.getPlayerEquipment(uuid);
                  if (equipment == null)
                    tracker.setPlayerEquipment(uuid, equipment = new Item[5]); 
                  for (int i = 5; i < 9; i++) {
                    equipment[9 - i] = items[i];
                    if (tracker.getGamemode() == 3)
                      items[i] = null; 
                  } 
                  if (tracker.getGamemode() == 3) {
                    GameProfileStorage.GameProfile profile = ((GameProfileStorage)packetWrapper.user().get(GameProfileStorage.class)).get(uuid);
                    if (profile != null)
                      items[5] = profile.getSkull(); 
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(packetWrapper -> {
                  short windowId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  Windows windows = (Windows)packetWrapper.user().get(Windows.class);
                  short windowType = windows.get(windowId);
                  short property = ((Short)packetWrapper.get((Type)Type.SHORT, 0)).shortValue();
                  short value = ((Short)packetWrapper.get((Type)Type.SHORT, 1)).shortValue();
                  if (windowType == -1)
                    return; 
                  if (windowType == 2) {
                    Windows.Furnace furnace = windows.furnace.computeIfAbsent(Short.valueOf(windowId), ());
                    if (property == 0 || property == 1) {
                      if (property == 0) {
                        furnace.setFuelLeft(value);
                      } else {
                        furnace.setMaxFuel(value);
                      } 
                      if (furnace.getMaxFuel() == 0) {
                        packetWrapper.cancel();
                        return;
                      } 
                      value = (short)(200 * furnace.getFuelLeft() / furnace.getMaxFuel());
                      packetWrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)1));
                      packetWrapper.set((Type)Type.SHORT, 1, Short.valueOf(value));
                    } else if (property == 2 || property == 3) {
                      if (property == 2) {
                        furnace.setProgress(value);
                      } else {
                        furnace.setMaxProgress(value);
                      } 
                      if (furnace.getMaxProgress() == 0) {
                        packetWrapper.cancel();
                        return;
                      } 
                      value = (short)(200 * furnace.getProgress() / furnace.getMaxProgress());
                      packetWrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)0));
                      packetWrapper.set((Type)Type.SHORT, 1, Short.valueOf(value));
                    } 
                  } else if (windowType == 4) {
                    if (property > 2) {
                      packetWrapper.cancel();
                      return;
                    } 
                  } else if (windowType == 8) {
                    windows.levelCost = value;
                    windows.anvilId = windowId;
                  } 
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_7.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(packetWrapper -> {
                  short windowsId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  ((Windows)packetWrapper.user().get(Windows.class)).remove(windowsId);
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_7.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  short windowId = (short)((Byte)packetWrapper.read((Type)Type.BYTE)).byteValue();
                  packetWrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(windowId));
                  short windowType = ((Windows)packetWrapper.user().get(Windows.class)).get(windowId);
                  short slot = ((Short)packetWrapper.read((Type)Type.SHORT)).shortValue();
                  if (windowType == 4 && slot > 0)
                    slot = (short)(slot + 1); 
                  packetWrapper.write((Type)Type.SHORT, Short.valueOf(slot));
                });
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);
            handler(packetWrapper -> {
                  Item item = (Item)packetWrapper.get(Type.ITEM, 0);
                  ItemRewriter.toServer(item);
                  packetWrapper.set(Type.ITEM, 0, item);
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_7.WINDOW_CONFIRMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BOOLEAN);
            handler(packetWrapper -> {
                  int action = ((Short)packetWrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (action == -89)
                    packetWrapper.cancel(); 
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_7.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);
            handler(packetWrapper -> {
                  Item item = (Item)packetWrapper.get(Type.ITEM, 0);
                  ItemRewriter.toServer(item);
                  packetWrapper.set(Type.ITEM, 0, item);
                });
          }
        });
  }
}
