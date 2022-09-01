package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import java.util.List;
import java.util.Optional;

public class WorldPackets {
  private static final IntSet VALID_BIOMES = (IntSet)new IntOpenHashSet(70, 1.0F);
  
  static {
    int i;
    for (i = 0; i < 50; i++)
      VALID_BIOMES.add(i); 
    VALID_BIOMES.add(127);
    for (i = 129; i <= 134; i++)
      VALID_BIOMES.add(i); 
    VALID_BIOMES.add(140);
    VALID_BIOMES.add(149);
    VALID_BIOMES.add(151);
    for (i = 155; i <= 158; i++)
      VALID_BIOMES.add(i); 
    for (i = 160; i <= 167; i++)
      VALID_BIOMES.add(i); 
  }
  
  public static void register(Protocol protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    PaintingProvider provider = (PaintingProvider)Via.getManager().getProviders().get(PaintingProvider.class);
                    String motive = (String)wrapper.read(Type.STRING);
                    Optional<Integer> id = provider.getIntByIdentifier(motive);
                    if (!id.isPresent() && (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()))
                      Via.getPlatform().getLogger().warning("Could not find painting motive: " + motive + " falling back to default (0)"); 
                    wrapper.write((Type)Type.VAR_INT, id.orElse(Integer.valueOf(0)));
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.get(Type.POSITION, 0);
                    short action = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                    BlockEntityProvider provider = (BlockEntityProvider)Via.getManager().getProviders().get(BlockEntityProvider.class);
                    int newId = provider.transform(wrapper.user(), position, tag, true);
                    if (newId != -1) {
                      BlockStorage storage = (BlockStorage)wrapper.user().get(BlockStorage.class);
                      BlockStorage.ReplacementData replacementData = storage.get(position);
                      if (replacementData != null)
                        replacementData.setReplacement(newId); 
                    } 
                    if (action == 5)
                      wrapper.cancel(); 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position pos = (Position)wrapper.get(Type.POSITION, 0);
                    short action = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    short param = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 1)).shortValue();
                    int blockId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (blockId == 25) {
                      blockId = 73;
                    } else if (blockId == 33) {
                      blockId = 99;
                    } else if (blockId == 29) {
                      blockId = 92;
                    } else if (blockId == 54) {
                      blockId = 142;
                    } else if (blockId == 146) {
                      blockId = 305;
                    } else if (blockId == 130) {
                      blockId = 249;
                    } else if (blockId == 138) {
                      blockId = 257;
                    } else if (blockId == 52) {
                      blockId = 140;
                    } else if (blockId == 209) {
                      blockId = 472;
                    } else if (blockId >= 219 && blockId <= 234) {
                      blockId = blockId - 219 + 483;
                    } 
                    if (blockId == 73) {
                      PacketWrapper blockChange = wrapper.create(11);
                      blockChange.write(Type.POSITION, new Position(pos));
                      blockChange.write((Type)Type.VAR_INT, Integer.valueOf(249 + action * 24 * 2 + param * 2));
                      blockChange.send(Protocol1_13To1_12_2.class);
                    } 
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(blockId));
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.get(Type.POSITION, 0);
                    int newId = WorldPackets.toNewId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue());
                    UserConnection userConnection = wrapper.user();
                    if (Via.getConfig().isServersideBlockConnections()) {
                      ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), newId);
                      newId = ConnectionData.connect(userConnection, position, newId);
                    } 
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(WorldPackets.checkStorage(wrapper.user(), position, newId)));
                    if (Via.getConfig().isServersideBlockConnections()) {
                      wrapper.send(Protocol1_13To1_12_2.class);
                      wrapper.cancel();
                      ConnectionData.update(userConnection, position);
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int chunkX = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int chunkZ = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    UserConnection userConnection = wrapper.user();
                    BlockChangeRecord[] records = (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
                    for (BlockChangeRecord record : records) {
                      int newBlock = WorldPackets.toNewId(record.getBlockId());
                      Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                      if (Via.getConfig().isServersideBlockConnections())
                        ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), newBlock); 
                      record.setBlockId(WorldPackets.checkStorage(wrapper.user(), position, newBlock));
                    } 
                    if (Via.getConfig().isServersideBlockConnections()) {
                      for (BlockChangeRecord record : records) {
                        int blockState = record.getBlockId();
                        Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                        ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
                        if (handler != null) {
                          blockState = handler.connect(userConnection, position, blockState);
                          record.setBlockId(blockState);
                        } 
                      } 
                      wrapper.send(Protocol1_13To1_12_2.class);
                      wrapper.cancel();
                      for (BlockChangeRecord record : records) {
                        Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                        ConnectionData.update(userConnection, position);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            if (!Via.getConfig().isServersideBlockConnections())
              return; 
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    UserConnection userConnection = wrapper.user();
                    int x = (int)Math.floor(((Float)wrapper.get((Type)Type.FLOAT, 0)).floatValue());
                    int y = (int)Math.floor(((Float)wrapper.get((Type)Type.FLOAT, 1)).floatValue());
                    int z = (int)Math.floor(((Float)wrapper.get((Type)Type.FLOAT, 2)).floatValue());
                    int recordCount = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    Position[] records = new Position[recordCount];
                    int i;
                    for (i = 0; i < recordCount; i++) {
                      Position position = new Position(x + ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue(), (short)(y + ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue()), z + ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue());
                      records[i] = position;
                      ConnectionData.updateBlockStorage(userConnection, position.getX(), position.getY(), position.getZ(), 0);
                    } 
                    wrapper.send(Protocol1_13To1_12_2.class);
                    wrapper.cancel();
                    for (i = 0; i < recordCount; i++)
                      ConnectionData.update(userConnection, records[i]); 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.UNLOAD_CHUNK, new PacketRemapper() {
          public void registerMap() {
            if (Via.getConfig().isServersideBlockConnections())
              handler(new PacketHandler() {
                    public void handle(PacketWrapper wrapper) throws Exception {
                      int x = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      int z = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      ConnectionData.blockConnectionProvider.unloadChunk(wrapper.user(), x, z);
                    }
                  }); 
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String sound = ((String)wrapper.get(Type.STRING, 0)).replace("minecraft:", "");
                    String newSoundId = NamedSoundRewriter.getNewId(sound);
                    wrapper.set(Type.STRING, 0, newSoundId);
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    BlockStorage storage = (BlockStorage)wrapper.user().get(BlockStorage.class);
                    Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                    Chunk1_13Type type1_13 = new Chunk1_13Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.read((Type)type);
                    wrapper.write((Type)type1_13, chunk);
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null) {
                        for (int p = 0; p < section.getPaletteSize(); p++) {
                          int old = section.getPaletteEntry(p);
                          int newId = WorldPackets.toNewId(old);
                          section.setPaletteEntry(p, newId);
                        } 
                        boolean willSaveToStorage = false;
                        for (int j = 0; j < section.getPaletteSize(); j++) {
                          int newId = section.getPaletteEntry(j);
                          if (storage.isWelcome(newId)) {
                            willSaveToStorage = true;
                            break;
                          } 
                        } 
                        boolean willSaveConnection = false;
                        if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks())
                          for (int k = 0; k < section.getPaletteSize(); k++) {
                            int newId = section.getPaletteEntry(k);
                            if (ConnectionData.isWelcome(newId)) {
                              willSaveConnection = true;
                              break;
                            } 
                          }  
                        if (willSaveToStorage)
                          for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                              for (int x = 0; x < 16; x++) {
                                int block = section.getFlatBlock(x, y, z);
                                if (storage.isWelcome(block))
                                  storage.store(new Position(x + (chunk
                                        .getX() << 4), (short)(y + (i << 4)), z + (chunk
                                        
                                        .getZ() << 4)), block); 
                              } 
                            } 
                          }  
                        if (willSaveConnection)
                          for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                              for (int x = 0; x < 16; x++) {
                                int block = section.getFlatBlock(x, y, z);
                                if (ConnectionData.isWelcome(block))
                                  ConnectionData.blockConnectionProvider.storeBlock(wrapper.user(), x + (chunk.getX() << 4), y + (i << 4), z + (chunk
                                      
                                      .getZ() << 4), block); 
                              } 
                            } 
                          }  
                      } 
                    } 
                    if (chunk.isBiomeData()) {
                      int latestBiomeWarn = Integer.MIN_VALUE;
                      for (int j = 0; j < 256; j++) {
                        int biome = chunk.getBiomeData()[j];
                        if (!WorldPackets.VALID_BIOMES.contains(biome)) {
                          if (biome != 255 && latestBiomeWarn != biome) {
                            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
                              Via.getPlatform().getLogger().warning("Received invalid biome id " + biome); 
                            latestBiomeWarn = biome;
                          } 
                          chunk.getBiomeData()[j] = 1;
                        } 
                      } 
                    } 
                    BlockEntityProvider provider = (BlockEntityProvider)Via.getManager().getProviders().get(BlockEntityProvider.class);
                    for (CompoundTag tag : chunk.getBlockEntities()) {
                      int newId = provider.transform(wrapper.user(), null, tag, false);
                      if (newId != -1) {
                        int x = ((NumberTag)tag.get("x")).asInt();
                        int y = ((NumberTag)tag.get("y")).asInt();
                        int z = ((NumberTag)tag.get("z")).asInt();
                        Position position = new Position(x, (short)y, z);
                        BlockStorage.ReplacementData replacementData = storage.get(position);
                        if (replacementData != null)
                          replacementData.setReplacement(newId); 
                        chunk.getSections()[y >> 4].setFlatBlock(x & 0xF, y & 0xF, z & 0xF, newId);
                      } 
                    } 
                    if (Via.getConfig().isServersideBlockConnections()) {
                      ConnectionData.connectBlocks(wrapper.user(), chunk);
                      wrapper.send(Protocol1_13To1_12_2.class);
                      wrapper.cancel();
                      for (int j = 0; j < (chunk.getSections()).length; j++) {
                        ChunkSection section = chunk.getSections()[j];
                        if (section != null)
                          ConnectionData.updateChunkSectionNeighbours(wrapper.user(), chunk.getX(), chunk.getZ(), j); 
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.SPAWN_PARTICLE, new PacketRemapper() {
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
                    int particleId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    int dataCount = 0;
                    if (particleId == 37 || particleId == 38 || particleId == 46) {
                      dataCount = 1;
                    } else if (particleId == 36) {
                      dataCount = 2;
                    } 
                    Integer[] data = new Integer[dataCount];
                    for (int i = 0; i < data.length; i++)
                      data[i] = (Integer)wrapper.read((Type)Type.VAR_INT); 
                    Particle particle = ParticleRewriter.rewriteParticle(particleId, data);
                    if (particle == null || particle.getId() == -1) {
                      wrapper.cancel();
                      return;
                    } 
                    if (particle.getId() == 11) {
                      int count = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                      float speed = ((Float)wrapper.get((Type)Type.FLOAT, 6)).floatValue();
                      if (count == 0) {
                        wrapper.set((Type)Type.INT, 1, Integer.valueOf(1));
                        wrapper.set((Type)Type.FLOAT, 6, Float.valueOf(0.0F));
                        List<Particle.ParticleData> arguments = particle.getArguments();
                        for (int j = 0; j < 3; j++) {
                          float colorValue = ((Float)wrapper.get((Type)Type.FLOAT, j + 3)).floatValue() * speed;
                          if (colorValue == 0.0F && j == 0)
                            colorValue = 1.0F; 
                          ((Particle.ParticleData)arguments.get(j)).setValue(Float.valueOf(colorValue));
                          wrapper.set((Type)Type.FLOAT, j + 3, Float.valueOf(0.0F));
                        } 
                      } 
                    } 
                    wrapper.set((Type)Type.INT, 0, Integer.valueOf(particle.getId()));
                    for (Particle.ParticleData particleData : particle.getArguments())
                      wrapper.write(particleData.getType(), particleData.getValue()); 
                  }
                });
          }
        });
  }
  
  public static int toNewId(int oldId) {
    if (oldId < 0)
      oldId = 0; 
    int newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId);
    if (newId != -1)
      return newId; 
    newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId & 0xFFFFFFF0);
    if (newId != -1) {
      if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Missing block " + oldId); 
      return newId;
    } 
    if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
      Via.getPlatform().getLogger().warning("Missing block completely " + oldId); 
    return 1;
  }
  
  private static int checkStorage(UserConnection user, Position position, int newId) {
    BlockStorage storage = (BlockStorage)user.get(BlockStorage.class);
    if (storage.contains(position)) {
      BlockStorage.ReplacementData data = storage.get(position);
      if (data.getOriginal() == newId) {
        if (data.getReplacement() != -1)
          return data.getReplacement(); 
      } else {
        storage.remove(position);
        if (storage.isWelcome(newId))
          storage.store(position, newId); 
      } 
    } else if (storage.isWelcome(newId)) {
      storage.store(position, newId);
    } 
    return newId;
  }
}
