package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import java.util.Arrays;

public class WorldPackets {
  public static final int SERVERSIDE_VIEW_DISTANCE = 64;
  
  private static final byte[] FULL_LIGHT = new byte[2048];
  
  public static int air;
  
  public static int voidAir;
  
  public static int caveAir;
  
  static {
    Arrays.fill(FULL_LIGHT, (byte)-1);
  }
  
  public static void register(final Protocol1_14To1_13_2 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, null);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.BYTE);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue())));
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(id)));
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SERVER_DIFFICULTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  }
                });
          }
        });
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (int i = 0; i < 3; i++) {
                      float coord = ((Float)wrapper.get((Type)Type.FLOAT, i)).floatValue();
                      if (coord < 0.0F) {
                        coord = (int)coord;
                        wrapper.set((Type)Type.FLOAT, i, Float.valueOf(coord));
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_13Type(clientWorld));
                    wrapper.write((Type)new Chunk1_14Type(), chunk);
                    int[] motionBlocking = new int[256];
                    int[] worldSurface = new int[256];
                    for (int s = 0; s < (chunk.getSections()).length; s++) {
                      ChunkSection section = chunk.getSections()[s];
                      if (section != null) {
                        boolean hasBlock = false;
                        for (int j = 0; j < section.getPaletteSize(); j++) {
                          int old = section.getPaletteEntry(j);
                          int newId = protocol.getMappingData().getNewBlockStateId(old);
                          if (!hasBlock && newId != WorldPackets.air && newId != WorldPackets.voidAir && newId != WorldPackets.caveAir)
                            hasBlock = true; 
                          section.setPaletteEntry(j, newId);
                        } 
                        if (!hasBlock) {
                          section.setNonAirBlocksCount(0);
                        } else {
                          int nonAirBlockCount = 0;
                          for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                              for (int z = 0; z < 16; z++) {
                                int id = section.getFlatBlock(x, y, z);
                                if (id != WorldPackets.air && id != WorldPackets.voidAir && id != WorldPackets.caveAir) {
                                  nonAirBlockCount++;
                                  worldSurface[x + z * 16] = y + s * 16 + 1;
                                } 
                                if (protocol.getMappingData().getMotionBlocking().contains(id))
                                  motionBlocking[x + z * 16] = y + s * 16 + 1; 
                                if (Via.getConfig().isNonFullBlockLightFix() && protocol.getMappingData().getNonFullBlocks().contains(id))
                                  WorldPackets.setNonFullLight(chunk, section, s, x, y, z); 
                              } 
                            } 
                          } 
                          section.setNonAirBlocksCount(nonAirBlockCount);
                        } 
                      } 
                    } 
                    CompoundTag heightMap = new CompoundTag();
                    heightMap.put("MOTION_BLOCKING", (Tag)new LongArrayTag(WorldPackets.encodeHeightMap(motionBlocking)));
                    heightMap.put("WORLD_SURFACE", (Tag)new LongArrayTag(WorldPackets.encodeHeightMap(worldSurface)));
                    chunk.setHeightMap(heightMap);
                    PacketWrapper lightPacket = wrapper.create((PacketType)ClientboundPackets1_14.UPDATE_LIGHT);
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getX()));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getZ()));
                    int skyLightMask = chunk.isFullChunk() ? 262143 : 0;
                    int blockLightMask = 0;
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection sec = chunk.getSections()[i];
                      if (sec != null) {
                        if (!chunk.isFullChunk() && sec.getLight().hasSkyLight())
                          skyLightMask |= 1 << i + 1; 
                        blockLightMask |= 1 << i + 1;
                      } 
                    } 
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(skyLightMask));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(blockLightMask));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    if (chunk.isFullChunk())
                      lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                    for (ChunkSection section : chunk.getSections()) {
                      if (section == null || !section.getLight().hasSkyLight()) {
                        if (chunk.isFullChunk())
                          lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                      } else {
                        lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getSkyLight());
                      } 
                    } 
                    if (chunk.isFullChunk())
                      lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                    for (ChunkSection section : chunk.getSections()) {
                      if (section != null)
                        lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getBlockLight()); 
                    } 
                    EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                    int diffX = Math.abs(entityTracker.getChunkCenterX() - chunk.getX());
                    int diffZ = Math.abs(entityTracker.getChunkCenterZ() - chunk.getZ());
                    if (entityTracker.isForceSendCenterChunk() || diffX >= 64 || diffZ >= 64) {
                      PacketWrapper fakePosLook = wrapper.create((PacketType)ClientboundPackets1_14.UPDATE_VIEW_POSITION);
                      fakePosLook.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getX()));
                      fakePosLook.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getZ()));
                      fakePosLook.send(Protocol1_14To1_13_2.class);
                      entityTracker.setChunkCenterX(chunk.getX());
                      entityTracker.setChunkCenterZ(chunk.getZ());
                    } 
                    lightPacket.send(Protocol1_14To1_13_2.class);
                    for (ChunkSection section : chunk.getSections()) {
                      if (section != null)
                        section.setLight(null); 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int data = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    if (id == 1010) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(protocol.getMappingData().getNewItemId(data)));
                    } else if (id == 2001) {
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                    } 
                  }
                });
          }
        });
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
                    int entityId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    Entity1_14Types entType = Entity1_14Types.PLAYER;
                    EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                    tracker.addEntity(entityId, (EntityType)entType);
                    tracker.setClientEntityId(entityId);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short difficulty = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                    PacketWrapper difficultyPacket = wrapper.create((PacketType)ClientboundPackets1_14.SERVER_DIFFICULTY);
                    difficultyPacket.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(difficulty));
                    difficultyPacket.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    difficultyPacket.scheduleSend(protocol.getClass());
                    wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                    wrapper.passthrough(Type.STRING);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(64));
                  }
                });
            handler(wrapper -> {
                  wrapper.send(Protocol1_14To1_13_2.class);
                  wrapper.cancel();
                  WorldPackets.sendViewDistancePacket(wrapper.user());
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
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
                    EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                    entityTracker.setForceSendCenterChunk(true);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short difficulty = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                    PacketWrapper difficultyPacket = wrapper.create((PacketType)ClientboundPackets1_14.SERVER_DIFFICULTY);
                    difficultyPacket.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(difficulty));
                    difficultyPacket.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    difficultyPacket.scheduleSend(protocol.getClass());
                  }
                });
            handler(wrapper -> {
                  wrapper.send(Protocol1_14To1_13_2.class);
                  wrapper.cancel();
                  WorldPackets.sendViewDistancePacket(wrapper.user());
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
  }
  
  private static void sendViewDistancePacket(UserConnection connection) throws Exception {
    PacketWrapper setViewDistance = PacketWrapper.create((PacketType)ClientboundPackets1_14.UPDATE_VIEW_DISTANCE, null, connection);
    setViewDistance.write((Type)Type.VAR_INT, Integer.valueOf(64));
    setViewDistance.send(Protocol1_14To1_13_2.class);
  }
  
  private static long[] encodeHeightMap(int[] heightMap) {
    return CompactArrayUtil.createCompactArray(9, heightMap.length, i -> heightMap[i]);
  }
  
  private static void setNonFullLight(Chunk chunk, ChunkSection section, int ySection, int x, int y, int z) {
    int skyLight = 0;
    int blockLight = 0;
    for (BlockFace blockFace : BlockFace.values()) {
      NibbleArray skyLightArray = section.getLight().getSkyLightNibbleArray();
      NibbleArray blockLightArray = section.getLight().getBlockLightNibbleArray();
      int neighbourX = x + blockFace.getModX();
      int neighbourY = y + blockFace.getModY();
      int neighbourZ = z + blockFace.getModZ();
      if (blockFace.getModX() != 0) {
        if (neighbourX == 16 || neighbourX == -1)
          continue; 
      } else if (blockFace.getModY() != 0) {
        if (neighbourY == 16 || neighbourY == -1) {
          if (neighbourY == 16) {
            ySection++;
            neighbourY = 0;
          } else {
            ySection--;
            neighbourY = 15;
          } 
          if (ySection == 16 || ySection == -1)
            continue; 
          ChunkSection newSection = chunk.getSections()[ySection];
          if (newSection == null)
            continue; 
          skyLightArray = newSection.getLight().getSkyLightNibbleArray();
          blockLightArray = newSection.getLight().getBlockLightNibbleArray();
        } 
      } else if (blockFace.getModZ() != 0) {
        if (neighbourZ == 16 || neighbourZ == -1)
          continue; 
      } 
      if (blockLightArray != null && blockLight != 15) {
        int neighbourBlockLight = blockLightArray.get(neighbourX, neighbourY, neighbourZ);
        if (neighbourBlockLight == 15) {
          blockLight = 14;
        } else if (neighbourBlockLight > blockLight) {
          blockLight = neighbourBlockLight - 1;
        } 
      } 
      if (skyLightArray != null && skyLight != 15) {
        int neighbourSkyLight = skyLightArray.get(neighbourX, neighbourY, neighbourZ);
        if (neighbourSkyLight == 15) {
          if (blockFace.getModY() == 1) {
            skyLight = 15;
          } else {
            skyLight = 14;
          } 
        } else if (neighbourSkyLight > skyLight) {
          skyLight = neighbourSkyLight - 1;
        } 
      } 
      continue;
    } 
    if (skyLight != 0) {
      if (!section.getLight().hasSkyLight()) {
        byte[] newSkyLight = new byte[2028];
        section.getLight().setSkyLight(newSkyLight);
      } 
      section.getLight().getSkyLightNibbleArray().set(x, y, z, skyLight);
    } 
    if (blockLight != 0)
      section.getLight().getBlockLightNibbleArray().set(x, y, z, blockLight); 
  }
  
  private static long getChunkIndex(int x, int z) {
    return (x & 0x3FFFFFFL) << 38L | z & 0x3FFFFFFL;
  }
}
