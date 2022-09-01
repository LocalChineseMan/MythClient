package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_16_2;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.types.Chunk1_16_2Type;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class WorldPackets {
  public static void register(final Protocol1_17To1_16_4 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, Type.POSITION1_14);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_16_2.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_16_2.BLOCK_CHANGE);
    blockRewriter.registerVarLongMultiBlockChange((ClientboundPacketType)ClientboundPackets1_16_2.MULTI_BLOCK_CHANGE);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_16_2.ACKNOWLEDGE_PLAYER_DIGGING);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.WORLD_BORDER, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  ClientboundPackets1_17 clientboundPackets1_176;
                  ClientboundPackets1_17 clientboundPackets1_175;
                  ClientboundPackets1_17 clientboundPackets1_174;
                  ClientboundPackets1_17 clientboundPackets1_173;
                  ClientboundPackets1_17 clientboundPackets1_172;
                  ClientboundPackets1_17 clientboundPackets1_171;
                  int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  switch (type) {
                    case 0:
                      clientboundPackets1_176 = ClientboundPackets1_17.WORLD_BORDER_SIZE;
                      break;
                    case 1:
                      clientboundPackets1_175 = ClientboundPackets1_17.WORLD_BORDER_LERP_SIZE;
                      break;
                    case 2:
                      clientboundPackets1_174 = ClientboundPackets1_17.WORLD_BORDER_CENTER;
                      break;
                    case 3:
                      clientboundPackets1_173 = ClientboundPackets1_17.WORLD_BORDER_INIT;
                      break;
                    case 4:
                      clientboundPackets1_172 = ClientboundPackets1_17.WORLD_BORDER_WARNING_DELAY;
                      break;
                    case 5:
                      clientboundPackets1_171 = ClientboundPackets1_17.WORLD_BORDER_WARNING_DISTANCE;
                      break;
                    default:
                      throw new IllegalArgumentException("Invalid world border type received: " + type);
                  } 
                  wrapper.setId(clientboundPackets1_171.getId());
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.UPDATE_LIGHT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  int skyLightMask = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  int blockLightMask = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write(Type.LONG_ARRAY_PRIMITIVE, toBitSetLongArray(skyLightMask));
                  wrapper.write(Type.LONG_ARRAY_PRIMITIVE, toBitSetLongArray(blockLightMask));
                  wrapper.write(Type.LONG_ARRAY_PRIMITIVE, toBitSetLongArray(((Integer)wrapper.read((Type)Type.VAR_INT)).intValue()));
                  wrapper.write(Type.LONG_ARRAY_PRIMITIVE, toBitSetLongArray(((Integer)wrapper.read((Type)Type.VAR_INT)).intValue()));
                  writeLightArrays(wrapper, skyLightMask);
                  writeLightArrays(wrapper, blockLightMask);
                });
          }
          
          private void writeLightArrays(PacketWrapper wrapper, int bitMask) throws Exception {
            List<byte[]> light = (List)new ArrayList<>();
            for (int i = 0; i < 18; i++) {
              if (isSet(bitMask, i))
                light.add((byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE)); 
            } 
            wrapper.write((Type)Type.VAR_INT, Integer.valueOf(light.size()));
            for (byte[] bytes : light)
              wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, bytes); 
          }
          
          private long[] toBitSetLongArray(int bitmask) {
            return new long[] { bitmask };
          }
          
          private boolean isSet(int mask, int i) {
            return ((mask & 1 << i) != 0);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_16_2Type());
                  if (!chunk.isFullChunk()) {
                    WorldPackets.writeMultiBlockChangePacket(wrapper, chunk);
                    wrapper.cancel();
                    return;
                  } 
                  wrapper.write((Type)new Chunk1_17Type((chunk.getSections()).length), chunk);
                  chunk.setChunkMask(BitSet.valueOf(new long[] { chunk.getBitmask() }));
                  for (int s = 0; s < (chunk.getSections()).length; s++) {
                    ChunkSection section = chunk.getSections()[s];
                    if (section != null)
                      for (int i = 0; i < section.getPaletteSize(); i++) {
                        int old = section.getPaletteEntry(i);
                        section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(old));
                      }  
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.BYTE);
            map(Type.STRING_ARRAY);
            map(Type.NBT);
            map(Type.NBT);
            handler(wrapper -> {
                  CompoundTag dimensionRegistry = (CompoundTag)((CompoundTag)wrapper.get(Type.NBT, 0)).get("minecraft:dimension_type");
                  ListTag dimensions = (ListTag)dimensionRegistry.get("value");
                  for (Tag dimension : dimensions) {
                    CompoundTag dimensionCompound = (CompoundTag)((CompoundTag)dimension).get("element");
                    WorldPackets.addNewDimensionData(dimensionCompound);
                  } 
                  CompoundTag currentDimensionTag = (CompoundTag)wrapper.get(Type.NBT, 1);
                  WorldPackets.addNewDimensionData(currentDimensionTag);
                  UserConnection user = wrapper.user();
                  user.getEntityTracker(Protocol1_17To1_16_4.class).addEntity(((Integer)wrapper.get((Type)Type.INT, 0)).intValue(), (EntityType)Entity1_17Types.PLAYER);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  CompoundTag dimensionData = (CompoundTag)wrapper.passthrough(Type.NBT);
                  WorldPackets.addNewDimensionData(dimensionData);
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_16_2.EFFECT, 1010, 2001);
  }
  
  private static void writeMultiBlockChangePacket(PacketWrapper wrapper, Chunk chunk) throws Exception {
    long chunkPosition = (chunk.getX() & 0x3FFFFFL) << 42L;
    chunkPosition |= (chunk.getZ() & 0x3FFFFFL) << 20L;
    ChunkSection[] sections = chunk.getSections();
    for (int chunkY = 0; chunkY < sections.length; chunkY++) {
      ChunkSection section = sections[chunkY];
      if (section != null) {
        PacketWrapper blockChangePacket = wrapper.create((PacketType)ClientboundPackets1_17.MULTI_BLOCK_CHANGE);
        blockChangePacket.write((Type)Type.LONG, Long.valueOf(chunkPosition | chunkY & 0xFFFFFL));
        blockChangePacket.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
        BlockChangeRecord[] blockChangeRecords = new BlockChangeRecord[4096];
        int j = 0;
        for (int x = 0; x < 16; x++) {
          for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
              int blockStateId = Protocol1_17To1_16_4.MAPPINGS.getNewBlockStateId(section.getFlatBlock(x, y, z));
              blockChangeRecords[j++] = (BlockChangeRecord)new BlockChangeRecord1_16_2(x, y, z, blockStateId);
            } 
          } 
        } 
        blockChangePacket.write(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY, blockChangeRecords);
        blockChangePacket.send(Protocol1_17To1_16_4.class);
      } 
    } 
  }
  
  private static void addNewDimensionData(CompoundTag tag) {
    tag.put("min_y", (Tag)new IntTag(0));
    tag.put("height", (Tag)new IntTag(256));
  }
}
