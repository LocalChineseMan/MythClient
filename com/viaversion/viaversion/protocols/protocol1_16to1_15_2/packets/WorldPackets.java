package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.UUIDIntArrayType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.types.Chunk1_15Type;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.types.Chunk1_16Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import java.util.Map;
import java.util.UUID;

public class WorldPackets {
  public static void register(final Protocol1_16To1_15_2 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, Type.POSITION1_14);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_15.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_15.BLOCK_CHANGE);
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_15.MULTI_BLOCK_CHANGE);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_15.ACKNOWLEDGE_PLAYER_DIGGING);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.UPDATE_LIGHT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true)));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_15Type());
                  wrapper.write((Type)new Chunk1_16Type(), chunk);
                  chunk.setIgnoreOldLightData(chunk.isFullChunk());
                  for (int s = 0; s < (chunk.getSections()).length; s++) {
                    ChunkSection section = chunk.getSections()[s];
                    if (section != null)
                      for (int i = 0; i < section.getPaletteSize(); i++) {
                        int old = section.getPaletteEntry(i);
                        section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(old));
                      }  
                  } 
                  CompoundTag heightMaps = chunk.getHeightMap();
                  for (Tag heightMapTag : heightMaps.values()) {
                    LongArrayTag heightMap = (LongArrayTag)heightMapTag;
                    int[] heightMapData = new int[256];
                    CompactArrayUtil.iterateCompactArray(9, heightMapData.length, heightMap.getValue(), ());
                    heightMap.setValue(CompactArrayUtil.createCompactArrayWithPadding(9, heightMapData.length, ()));
                  } 
                  if (chunk.getBlockEntities() == null)
                    return; 
                  for (CompoundTag blockEntity : chunk.getBlockEntities())
                    WorldPackets.handleBlockEntity(blockEntity); 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_15.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Position position = (Position)wrapper.passthrough(Type.POSITION1_14);
                  short action = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                  CompoundTag tag = (CompoundTag)wrapper.passthrough(Type.NBT);
                  WorldPackets.handleBlockEntity(tag);
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_15.EFFECT, 1010, 2001);
  }
  
  private static void handleBlockEntity(CompoundTag compoundTag) {
    StringTag idTag = (StringTag)compoundTag.get("id");
    if (idTag == null)
      return; 
    String id = idTag.getValue();
    if (id.equals("minecraft:conduit")) {
      Tag targetUuidTag = compoundTag.remove("target_uuid");
      if (!(targetUuidTag instanceof StringTag))
        return; 
      UUID targetUuid = UUID.fromString((String)targetUuidTag.getValue());
      compoundTag.put("Target", (Tag)new IntArrayTag(UUIDIntArrayType.uuidToIntArray(targetUuid)));
    } else if (id.equals("minecraft:skull") && compoundTag.get("Owner") instanceof CompoundTag) {
      CompoundTag ownerTag = (CompoundTag)compoundTag.remove("Owner");
      StringTag ownerUuidTag = (StringTag)ownerTag.remove("Id");
      if (ownerUuidTag != null) {
        UUID ownerUuid = UUID.fromString(ownerUuidTag.getValue());
        ownerTag.put("Id", (Tag)new IntArrayTag(UUIDIntArrayType.uuidToIntArray(ownerUuid)));
      } 
      CompoundTag skullOwnerTag = new CompoundTag();
      for (Map.Entry<String, Tag> entry : (Iterable<Map.Entry<String, Tag>>)ownerTag.entrySet())
        skullOwnerTag.put(entry.getKey(), entry.getValue()); 
      compoundTag.put("SkullOwner", (Tag)skullOwnerTag);
    } 
  }
}
