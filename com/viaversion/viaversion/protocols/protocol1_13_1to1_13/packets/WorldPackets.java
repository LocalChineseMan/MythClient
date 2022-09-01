package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public class WorldPackets {
  public static void register(final Protocol protocol) {
    BlockRewriter blockRewriter = new BlockRewriter(protocol, Type.POSITION);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)new Chunk1_13Type(clientWorld));
                    for (ChunkSection section : chunk.getSections()) {
                      if (section != null)
                        for (int i = 0; i < section.getPaletteSize(); i++)
                          section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(section.getPaletteEntry(i)));  
                    } 
                  }
                });
          }
        });
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_13.BLOCK_CHANGE);
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_13.EFFECT, 1010, 2001);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    clientChunks.setEnvironment(dimensionId);
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                  }
                });
          }
        });
  }
}
