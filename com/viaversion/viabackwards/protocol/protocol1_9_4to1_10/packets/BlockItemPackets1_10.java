package com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.LegacyBlockItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.Protocol1_9_4To1_10;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;

public class BlockItemPackets1_10 extends LegacyBlockItemRewriter<Protocol1_9_4To1_10> {
  public BlockItemPackets1_10(Protocol1_9_4To1_10 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_9_3.SET_SLOT, Type.ITEM);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_9_3.WINDOW_ITEMS, Type.ITEM_ARRAY);
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        wrapper.write(Type.ITEM, BlockItemPackets1_10.this.handleItemToClient((Item)wrapper.read(Type.ITEM)));
                        wrapper.write(Type.ITEM, BlockItemPackets1_10.this.handleItemToClient((Item)wrapper.read(Type.ITEM)));
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          wrapper.write(Type.ITEM, BlockItemPackets1_10.this.handleItemToClient((Item)wrapper.read(Type.ITEM))); 
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
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    BlockItemPackets1_10.this.handleChunk(chunk);
                  }
                });
          }
        });
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int idx = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(BlockItemPackets1_10.this.handleBlockID(idx)));
                  }
                });
          }
        });
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (BlockChangeRecord record : (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0))
                      record.setBlockId(BlockItemPackets1_10.this.handleBlockID(record.getBlockId())); 
                  }
                });
          }
        });
    ((Protocol1_9_4To1_10)this.protocol).getEntityRewriter().filter().handler((event, meta) -> {
          if (meta.metaType().type().equals(Type.ITEM))
            meta.setValue(handleItemToClient((Item)meta.getValue())); 
        });
    ((Protocol1_9_4To1_10)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    if (id == 46)
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(38)); 
                  }
                });
          }
        });
  }
}
