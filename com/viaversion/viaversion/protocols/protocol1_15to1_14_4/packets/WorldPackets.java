package com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.types.Chunk1_15Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public class WorldPackets {
  public static void register(final Protocol1_15To1_14_4 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, Type.POSITION1_14);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_14.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_14.BLOCK_CHANGE);
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_14.MULTI_BLOCK_CHANGE);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_14.ACKNOWLEDGE_PLAYER_DIGGING);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_14Type());
                    wrapper.write((Type)new Chunk1_15Type(), chunk);
                    if (chunk.isFullChunk()) {
                      int[] biomeData = chunk.getBiomeData();
                      int[] newBiomeData = new int[1024];
                      if (biomeData != null) {
                        int i;
                        for (i = 0; i < 4; i++) {
                          for (int j = 0; j < 4; j++) {
                            int x = (j << 2) + 2;
                            int z = (i << 2) + 2;
                            int oldIndex = z << 4 | x;
                            newBiomeData[i << 2 | j] = biomeData[oldIndex];
                          } 
                        } 
                        for (i = 1; i < 64; i++)
                          System.arraycopy(newBiomeData, 0, newBiomeData, i * 16, 16); 
                      } 
                      chunk.setBiomeData(newBiomeData);
                    } 
                    for (int s = 0; s < (chunk.getSections()).length; s++) {
                      ChunkSection section = chunk.getSections()[s];
                      if (section != null)
                        for (int i = 0; i < section.getPaletteSize(); i++) {
                          int old = section.getPaletteEntry(i);
                          int newId = protocol.getMappingData().getNewBlockStateId(old);
                          section.setPaletteEntry(i, newId);
                        }  
                    } 
                  }
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_14.EFFECT, 1010, 2001);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.FLOAT, (Type)Type.DOUBLE);
            map((Type)Type.FLOAT, (Type)Type.DOUBLE);
            map((Type)Type.FLOAT, (Type)Type.DOUBLE);
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
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                    } else if (id == 32) {
                      protocol.getItemRewriter().handleItemToClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                    } 
                  }
                });
          }
        });
  }
}
