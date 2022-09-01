package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.types.Chunk1_15Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public class BlockItemPackets1_15 extends ItemRewriter<Protocol1_14_4To1_15> {
  public BlockItemPackets1_15(Protocol1_14_4To1_15 protocol, TranslatableRewriter translatableRewriter) {
    super((BackwardsProtocol)protocol, translatableRewriter);
  }
  
  protected void registerPackets() {
    BlockRewriter blockRewriter = new BlockRewriter(this.protocol, Type.POSITION1_14);
    (new RecipeRewriter1_14(this.protocol)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_15.DECLARE_RECIPES);
    ((Protocol1_14_4To1_15)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> BlockItemPackets1_15.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_15.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_15.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_15.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerTradeList((ClientboundPacketType)ClientboundPackets1_15.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_15.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_15.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_14.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_15.ACKNOWLEDGE_PLAYER_DIGGING);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_15.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_15.BLOCK_CHANGE);
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_15.MULTI_BLOCK_CHANGE);
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_15Type());
                    wrapper.write((Type)new Chunk1_14Type(), chunk);
                    if (chunk.isFullChunk()) {
                      int[] biomeData = chunk.getBiomeData();
                      int[] newBiomeData = new int[256];
                      for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                          int x = k << 2;
                          int z = j << 2;
                          int newIndex = z << 4 | x;
                          int oldIndex = j << 2 | k;
                          int biome = biomeData[oldIndex];
                          for (int m = 0; m < 4; m++) {
                            int offX = newIndex + (m << 4);
                            for (int l = 0; l < 4; l++)
                              newBiomeData[offX + l] = biome; 
                          } 
                        } 
                      } 
                      chunk.setBiomeData(newBiomeData);
                    } 
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null)
                        for (int j = 0; j < section.getPaletteSize(); j++) {
                          int old = section.getPaletteEntry(j);
                          int newId = ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewBlockStateId(old);
                          section.setPaletteEntry(j, newId);
                        }  
                    } 
                  }
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_15.EFFECT, 1010, 2001);
    ((Protocol1_14_4To1_15)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.DOUBLE, (Type)Type.FLOAT);
            map((Type)Type.DOUBLE, (Type)Type.FLOAT);
            map((Type)Type.DOUBLE, (Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    if (id == 3 || id == 23) {
                      int data = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewBlockStateId(data)));
                    } else if (id == 32) {
                      Item item = BlockItemPackets1_15.this.handleItemToClient((Item)wrapper.read(Type.FLAT_VAR_INT_ITEM));
                      wrapper.write(Type.FLAT_VAR_INT_ITEM, item);
                    } 
                    int mappedId = ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewParticleId(id);
                    if (id != mappedId)
                      wrapper.set((Type)Type.INT, 0, Integer.valueOf(mappedId)); 
                  }
                });
          }
        });
  }
}
