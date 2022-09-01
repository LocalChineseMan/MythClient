package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.api.rewriters.MapColorRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.data.MapColorRewrites;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.storage.PingRequests;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.types.Chunk1_16_2Type;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public final class BlockItemPackets1_17 extends ItemRewriter<Protocol1_16_4To1_17> {
  public BlockItemPackets1_17(Protocol1_16_4To1_17 protocol, TranslatableRewriter translatableRewriter) {
    super((BackwardsProtocol)protocol, translatableRewriter);
  }
  
  protected void registerPackets() {
    BlockRewriter blockRewriter = new BlockRewriter(this.protocol, Type.POSITION1_14);
    (new RecipeRewriter1_16(this.protocol)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_17.DECLARE_RECIPES);
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_17.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_17.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_17.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerEntityEquipmentArray((ClientboundPacketType)ClientboundPackets1_17.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    registerTradeList((ClientboundPacketType)ClientboundPackets1_17.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_17.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_17.ACKNOWLEDGE_PLAYER_DIGGING);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_17.BLOCK_ACTION);
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_17.EFFECT, 1010, 2001);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16_2.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_16_4To1_17)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> BlockItemPackets1_17.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT, (Type)Type.NOTHING);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  BlockItemPackets1_17.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.WINDOW_CONFIRMATION, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.cancel();
                  if (!ViaBackwards.getConfig().handlePingsAsInvAcknowledgements())
                    return; 
                  short inventoryId = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  short confirmationId = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                  boolean accepted = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  if (inventoryId == 0 && accepted && ((PingRequests)wrapper.user().get(PingRequests.class)).removeId(confirmationId)) {
                    PacketWrapper pongPacket = wrapper.create((PacketType)ServerboundPackets1_17.PONG);
                    pongPacket.write((Type)Type.INT, Integer.valueOf(confirmationId));
                    pongPacket.sendToServer(Protocol1_16_4To1_17.class);
                  } 
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.INT);
            handler(wrapper -> {
                  int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  if (id == 16) {
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.read((Type)Type.FLOAT);
                    wrapper.read((Type)Type.FLOAT);
                    wrapper.read((Type)Type.FLOAT);
                  } else if (id == 37) {
                    wrapper.cancel();
                  } 
                });
            handler(BlockItemPackets1_17.this.getSpawnParticleHandler(Type.FLAT_VAR_INT_ITEM));
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_SIZE, ClientboundPackets1_16_2.WORLD_BORDER, 0);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_LERP_SIZE, ClientboundPackets1_16_2.WORLD_BORDER, 1);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_CENTER, ClientboundPackets1_16_2.WORLD_BORDER, 2);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_INIT, ClientboundPackets1_16_2.WORLD_BORDER, 3);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_WARNING_DELAY, ClientboundPackets1_16_2.WORLD_BORDER, 4);
    ((Protocol1_16_4To1_17)this.protocol).mergePacket(ClientboundPackets1_17.WORLD_BORDER_WARNING_DISTANCE, ClientboundPackets1_16_2.WORLD_BORDER, 5);
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.UPDATE_LIGHT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  EntityTracker tracker = wrapper.user().getEntityTracker(Protocol1_16_4To1_17.class);
                  int startFromSection = Math.max(0, -(tracker.currentMinY() >> 4));
                  long[] skyLightMask = (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE);
                  long[] blockLightMask = (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE);
                  int cutSkyLightMask = BlockItemPackets1_17.this.cutLightMask(skyLightMask, startFromSection);
                  int cutBlockLightMask = BlockItemPackets1_17.this.cutLightMask(blockLightMask, startFromSection);
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(cutSkyLightMask));
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(cutBlockLightMask));
                  long[] emptySkyLightMask = (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE);
                  long[] emptyBlockLightMask = (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE);
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(BlockItemPackets1_17.this.cutLightMask(emptySkyLightMask, startFromSection)));
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(BlockItemPackets1_17.this.cutLightMask(emptyBlockLightMask, startFromSection)));
                  writeLightArrays(wrapper, BitSet.valueOf(skyLightMask), cutSkyLightMask, startFromSection, tracker.currentWorldSectionHeight());
                  writeLightArrays(wrapper, BitSet.valueOf(blockLightMask), cutBlockLightMask, startFromSection, tracker.currentWorldSectionHeight());
                });
          }
          
          private void writeLightArrays(PacketWrapper wrapper, BitSet bitMask, int cutBitMask, int startFromSection, int sectionHeight) throws Exception {
            wrapper.read((Type)Type.VAR_INT);
            List<byte[]> light = (List)new ArrayList<>();
            int i;
            for (i = 0; i < startFromSection; i++) {
              if (bitMask.get(i))
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); 
            } 
            for (i = 0; i < 18; i++) {
              if (isSet(cutBitMask, i))
                light.add((byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE)); 
            } 
            for (i = startFromSection + 18; i < sectionHeight + 2; i++) {
              if (bitMask.get(i))
                wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); 
            } 
            for (byte[] bytes : light)
              wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, bytes); 
          }
          
          private boolean isSet(int mask, int i) {
            return ((mask & 1 << i) != 0);
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.LONG);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  long chunkPos = ((Long)wrapper.get((Type)Type.LONG, 0)).longValue();
                  int chunkY = (int)(chunkPos << 44L >> 44L);
                  if (chunkY < 0 || chunkY > 15) {
                    wrapper.cancel();
                    return;
                  } 
                  BlockChangeRecord[] records = (BlockChangeRecord[])wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY);
                  for (BlockChangeRecord record : records)
                    record.setBlockId(((Protocol1_16_4To1_17)BlockItemPackets1_17.this.protocol).getMappingData().getNewBlockStateId(record.getBlockId())); 
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int y = ((Position)wrapper.get(Type.POSITION1_14, 0)).getY();
                  if (y < 0 || y > 255) {
                    wrapper.cancel();
                    return;
                  } 
                  wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(((Protocol1_16_4To1_17)BlockItemPackets1_17.this.protocol).getMappingData().getNewBlockStateId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue())));
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  EntityTracker tracker = wrapper.user().getEntityTracker(Protocol1_16_4To1_17.class);
                  int currentWorldSectionHeight = tracker.currentWorldSectionHeight();
                  Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_17Type(currentWorldSectionHeight));
                  wrapper.write((Type)new Chunk1_16_2Type(), chunk);
                  int startFromSection = Math.max(0, -(tracker.currentMinY() >> 4));
                  chunk.setBiomeData(Arrays.copyOfRange(chunk.getBiomeData(), startFromSection * 64, startFromSection * 64 + 1024));
                  chunk.setBitmask(BlockItemPackets1_17.this.cutMask(chunk.getChunkMask(), startFromSection, false));
                  chunk.setChunkMask(null);
                  ChunkSection[] sections = Arrays.<ChunkSection>copyOfRange(chunk.getSections(), startFromSection, startFromSection + 16);
                  chunk.setSections(sections);
                  for (int i = 0; i < 16; i++) {
                    ChunkSection section = sections[i];
                    if (section != null)
                      for (int j = 0; j < section.getPaletteSize(); j++) {
                        int old = section.getPaletteEntry(j);
                        section.setPaletteEntry(j, ((Protocol1_16_4To1_17)BlockItemPackets1_17.this.protocol).getMappingData().getNewBlockStateId(old));
                      }  
                  } 
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int y = ((Position)wrapper.passthrough(Type.POSITION1_14)).getY();
                  if (y < 0 || y > 255)
                    wrapper.cancel(); 
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int y = ((Position)wrapper.passthrough(Type.POSITION1_14)).getY();
                  if (y < 0 || y > 255)
                    wrapper.cancel(); 
                });
          }
        });
    ((Protocol1_16_4To1_17)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_17.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            handler(wrapper -> wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true)));
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  boolean hasMarkers = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  if (!hasMarkers) {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  } else {
                    MapColorRewriter.getRewriteHandler(MapColorRewrites::getMappedColor).handle(wrapper);
                  } 
                });
          }
        });
  }
  
  private int cutLightMask(long[] mask, int startFromSection) {
    if (mask.length == 0)
      return 0; 
    return cutMask(BitSet.valueOf(mask), startFromSection, true);
  }
  
  private int cutMask(BitSet mask, int startFromSection, boolean lightMask) {
    int cutMask = 0;
    int to = startFromSection + (lightMask ? 18 : 16);
    for (int i = startFromSection, j = 0; i < to; i++, j++) {
      if (mask.get(i))
        cutMask |= 1 << j; 
    } 
    return cutMask;
  }
}
