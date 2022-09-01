package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;

public abstract class ItemRewriter<T extends Protocol> extends RewriterBase<T> implements ItemRewriter<T> {
  protected ItemRewriter(T protocol) {
    super((Protocol)protocol);
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null)
      item.setIdentifier(this.protocol.getMappingData().getNewItemId(item.identifier())); 
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null)
      item.setIdentifier(this.protocol.getMappingData().getOldItemId(item.identifier())); 
    return item;
  }
  
  public void registerWindowItems(ClientboundPacketType packetType, final Type<Item[]> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(type);
            handler(ItemRewriter.this.itemArrayHandler(type));
          }
        });
  }
  
  public void registerSetSlot(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(type);
            handler(ItemRewriter.this.itemToClientHandler(type));
          }
        });
  }
  
  public void registerEntityEquipment(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(type);
            handler(ItemRewriter.this.itemToClientHandler(type));
          }
        });
  }
  
  public void registerEntityEquipmentArray(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  byte slot;
                  do {
                    slot = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
                    ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                  } while ((slot & Byte.MIN_VALUE) != 0);
                });
          }
        });
  }
  
  public void registerCreativeInvAction(ServerboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerServerbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(type);
            handler(ItemRewriter.this.itemToServerHandler(type));
          }
        });
  }
  
  public void registerClickWindow(ServerboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerServerbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(type);
            handler(ItemRewriter.this.itemToServerHandler(type));
          }
        });
  }
  
  public void registerClickWindow1_17(ServerboundPacketType packetType, Type<Item> type) {
    this.protocol.registerServerbound(packetType, (PacketRemapper)new Object(this, type));
  }
  
  public void registerSetCooldown(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int itemId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(ItemRewriter.this.protocol.getMappingData().getNewItemId(itemId)));
                });
          }
        });
  }
  
  public void registerTradeList(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  for (int i = 0; i < size; i++) {
                    ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                    ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                    if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                      ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type)); 
                    wrapper.passthrough((Type)Type.BOOLEAN);
                    wrapper.passthrough((Type)Type.INT);
                    wrapper.passthrough((Type)Type.INT);
                    wrapper.passthrough((Type)Type.INT);
                    wrapper.passthrough((Type)Type.INT);
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.passthrough((Type)Type.INT);
                  } 
                });
          }
        });
  }
  
  public void registerAdvancements(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    wrapper.passthrough(Type.STRING);
                    if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                      wrapper.passthrough(Type.STRING); 
                    if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue()) {
                      wrapper.passthrough(Type.COMPONENT);
                      wrapper.passthrough(Type.COMPONENT);
                      ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                      wrapper.passthrough((Type)Type.VAR_INT);
                      int flags = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      if ((flags & 0x1) != 0)
                        wrapper.passthrough(Type.STRING); 
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                    } 
                    wrapper.passthrough(Type.STRING_ARRAY);
                    int arrayLength = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int array = 0; array < arrayLength; array++)
                      wrapper.passthrough(Type.STRING_ARRAY); 
                  } 
                });
          }
        });
  }
  
  public void registerSpawnParticle(ClientboundPacketType packetType, final Type<Item> itemType, final Type<?> coordType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map(coordType);
            map(coordType);
            map(coordType);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.INT);
            handler(ItemRewriter.this.getSpawnParticleHandler(itemType));
          }
        });
  }
  
  public PacketHandler getSpawnParticleHandler(Type<Item> itemType) {
    return wrapper -> {
        int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
        if (id == -1)
          return; 
        ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
        if (id == mappings.getBlockId() || id == mappings.getFallingDustId()) {
          int data = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
          wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(data)));
        } else if (id == mappings.getItemId()) {
          handleItemToClient((Item)wrapper.passthrough(itemType));
        } 
        int newId = this.protocol.getMappingData().getNewParticleId(id);
        if (newId != id)
          wrapper.set((Type)Type.INT, 0, Integer.valueOf(newId)); 
      };
  }
  
  public PacketHandler itemArrayHandler(Type<Item[]> type) {
    return wrapper -> {
        Item[] items = (Item[])wrapper.get(type, 0);
        for (Item item : items)
          handleItemToClient(item); 
      };
  }
  
  public PacketHandler itemToClientHandler(Type<Item> type) {
    return wrapper -> handleItemToClient((Item)wrapper.get(type, 0));
  }
  
  public PacketHandler itemToServerHandler(Type<Item> type) {
    return wrapper -> handleItemToServer((Item)wrapper.get(type, 0));
  }
}
