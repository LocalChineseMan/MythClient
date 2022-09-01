package com.viaversion.viabackwards.protocol.protocol1_17to1_17_1;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.protocol.protocol1_17to1_17_1.storage.InventoryStateIds;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;

public final class Protocol1_17To1_17_1 extends BackwardsProtocol<ClientboundPackets1_17_1, ClientboundPackets1_17, ServerboundPackets1_17, ServerboundPackets1_17> {
  private static final int MAX_PAGE_LENGTH = 8192;
  
  private static final int MAX_TITLE_LENGTH = 128;
  
  private static final int MAX_PAGES = 200;
  
  public Protocol1_17To1_17_1() {
    super(ClientboundPackets1_17_1.class, ClientboundPackets1_17.class, ServerboundPackets1_17.class, ServerboundPackets1_17.class);
  }
  
  protected void registerPackets() {
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17_1.REMOVE_ENTITIES, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int[] entityIds = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                  wrapper.cancel();
                  for (int entityId : entityIds) {
                    PacketWrapper newPacket = wrapper.create((PacketType)ClientboundPackets1_17.REMOVE_ENTITY);
                    newPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                    newPacket.send(Protocol1_17To1_17_1.class);
                  } 
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17_1.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short containerId = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17_1.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short containerId = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  int stateId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).setStateId(containerId, stateId);
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17_1.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short containerId = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  int stateId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).setStateId(containerId, stateId);
                  wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY, wrapper.read(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT));
                  wrapper.read(Type.FLAT_VAR_INT_ITEM);
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_17.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short containerId = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_17.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short containerId = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  int stateId = ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf((stateId == Integer.MAX_VALUE) ? 0 : stateId));
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_17.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Item item = (Item)wrapper.read(Type.FLAT_VAR_INT_ITEM);
                  boolean signing = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  wrapper.passthrough((Type)Type.VAR_INT);
                  CompoundTag tag = item.tag();
                  StringTag titleTag = null;
                  ListTag pagesTag;
                  if (tag == null || (pagesTag = (ListTag)tag.get("pages")) == null || (signing && (titleTag = (StringTag)tag.get("title")) == null)) {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    return;
                  } 
                  if (pagesTag.size() > 200)
                    pagesTag = new ListTag(pagesTag.getValue().subList(0, 200)); 
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(pagesTag.size()));
                  for (Tag pageTag : pagesTag) {
                    String page = ((StringTag)pageTag).getValue();
                    if (page.length() > 8192)
                      page = page.substring(0, 8192); 
                    wrapper.write(Type.STRING, page);
                  } 
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(signing));
                  if (signing) {
                    if (titleTag == null)
                      titleTag = (StringTag)tag.get("title"); 
                    String title = titleTag.getValue();
                    if (title.length() > 128)
                      title = title.substring(0, 128); 
                    wrapper.write(Type.STRING, title);
                  } 
                });
          }
        });
  }
  
  public void init(UserConnection connection) {
    connection.put((StorableObject)new InventoryStateIds());
  }
}
