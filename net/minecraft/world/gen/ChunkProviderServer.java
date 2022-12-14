package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
  private static final Logger logger = LogManager.getLogger();
  
  private final Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  private final Chunk dummyChunk;
  
  private final IChunkProvider serverChunkGenerator;
  
  private final IChunkLoader chunkLoader;
  
  public boolean chunkLoadOverride = true;
  
  private final LongHashMap id2ChunkMap = new LongHashMap();
  
  private final List<Chunk> loadedChunks = Lists.newArrayList();
  
  private final WorldServer worldObj;
  
  public ChunkProviderServer(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_) {
    this.dummyChunk = (Chunk)new EmptyChunk((World)p_i1520_1_, 0, 0);
    this.worldObj = p_i1520_1_;
    this.chunkLoader = p_i1520_2_;
    this.serverChunkGenerator = p_i1520_3_;
  }
  
  public boolean chunkExists(int x, int z) {
    return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
  }
  
  public List<Chunk> func_152380_a() {
    return this.loadedChunks;
  }
  
  public void dropChunk(int p_73241_1_, int p_73241_2_) {
    if (this.worldObj.provider.canRespawnHere()) {
      if (!this.worldObj.isSpawnChunk(p_73241_1_, p_73241_2_))
        this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_))); 
    } else {
      this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_)));
    } 
  }
  
  public void unloadAllChunks() {
    for (Chunk chunk : this.loadedChunks)
      dropChunk(chunk.xPosition, chunk.zPosition); 
  }
  
  public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
    long i = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
    this.droppedChunksSet.remove(Long.valueOf(i));
    Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(i);
    if (chunk == null) {
      chunk = loadChunkFromFile(p_73158_1_, p_73158_2_);
      if (chunk == null)
        if (this.serverChunkGenerator == null) {
          chunk = this.dummyChunk;
        } else {
          try {
            chunk = this.serverChunkGenerator.provideChunk(p_73158_1_, p_73158_2_);
          } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
            crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[] { Integer.valueOf(p_73158_1_), Integer.valueOf(p_73158_2_) }));
            crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
            crashreportcategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
            throw new ReportedException(crashreport);
          } 
        }  
      this.id2ChunkMap.add(i, chunk);
      this.loadedChunks.add(chunk);
      chunk.onChunkLoad();
      chunk.populateChunk(this, this, p_73158_1_, p_73158_2_);
    } 
    return chunk;
  }
  
  public Chunk provideChunk(int x, int z) {
    Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
    return (chunk == null) ? ((!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride) ? this.dummyChunk : loadChunk(x, z)) : chunk;
  }
  
  private Chunk loadChunkFromFile(int x, int z) {
    if (this.chunkLoader == null)
      return null; 
    try {
      Chunk chunk = this.chunkLoader.loadChunk((World)this.worldObj, x, z);
      if (chunk != null) {
        chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
        if (this.serverChunkGenerator != null)
          this.serverChunkGenerator.recreateStructures(chunk, x, z); 
      } 
      return chunk;
    } catch (Exception exception) {
      logger.error("Couldn't load chunk", exception);
      return null;
    } 
  }
  
  private void saveChunkExtraData(Chunk p_73243_1_) {
    if (this.chunkLoader != null)
      try {
        this.chunkLoader.saveExtraChunkData((World)this.worldObj, p_73243_1_);
      } catch (Exception exception) {
        logger.error("Couldn't save entities", exception);
      }  
  }
  
  private void saveChunkData(Chunk p_73242_1_) {
    if (this.chunkLoader != null)
      try {
        p_73242_1_.setLastSaveTime(this.worldObj.getTotalWorldTime());
        this.chunkLoader.saveChunk((World)this.worldObj, p_73242_1_);
      } catch (IOException ioexception) {
        logger.error("Couldn't save chunk", ioexception);
      } catch (MinecraftException minecraftexception) {
        logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)minecraftexception);
      }  
  }
  
  public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
    Chunk chunk = provideChunk(p_73153_2_, p_73153_3_);
    if (!chunk.isTerrainPopulated()) {
      chunk.func_150809_p();
      if (this.serverChunkGenerator != null) {
        this.serverChunkGenerator.populate(p_73153_1_, p_73153_2_, p_73153_3_);
        chunk.setChunkModified();
      } 
    } 
  }
  
  public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
    if (this.serverChunkGenerator != null && this.serverChunkGenerator.func_177460_a(p_177460_1_, p_177460_2_, p_177460_3_, p_177460_4_)) {
      Chunk chunk = provideChunk(p_177460_3_, p_177460_4_);
      chunk.setChunkModified();
      return true;
    } 
    return false;
  }
  
  public boolean saveChunks(boolean p_73151_1_, IProgressUpdate progressCallback) {
    int i = 0;
    List<Chunk> list = Lists.newArrayList(this.loadedChunks);
    for (int j = 0; j < list.size(); j++) {
      Chunk chunk = list.get(j);
      if (p_73151_1_)
        saveChunkExtraData(chunk); 
      if (chunk.needsSaving(p_73151_1_)) {
        saveChunkData(chunk);
        chunk.setModified(false);
        i++;
        if (i == 24 && !p_73151_1_)
          return false; 
      } 
    } 
    return true;
  }
  
  public void saveExtraData() {
    if (this.chunkLoader != null)
      this.chunkLoader.saveExtraData(); 
  }
  
  public boolean unloadQueuedChunks() {
    if (!this.worldObj.disableLevelSaving) {
      for (int i = 0; i < 100; i++) {
        if (!this.droppedChunksSet.isEmpty()) {
          Long olong = this.droppedChunksSet.iterator().next();
          Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(olong.longValue());
          if (chunk != null) {
            chunk.onChunkUnload();
            saveChunkData(chunk);
            saveChunkExtraData(chunk);
            this.id2ChunkMap.remove(olong.longValue());
            this.loadedChunks.remove(chunk);
          } 
          this.droppedChunksSet.remove(olong);
        } 
      } 
      if (this.chunkLoader != null)
        this.chunkLoader.chunkTick(); 
    } 
    return this.serverChunkGenerator.unloadQueuedChunks();
  }
  
  public boolean canSave() {
    return !this.worldObj.disableLevelSaving;
  }
  
  public String makeString() {
    return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
  }
  
  public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
    return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
  }
  
  public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
    return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
  }
  
  public int getLoadedChunkCount() {
    return this.id2ChunkMap.getNumHashElements();
  }
  
  public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_) {}
  
  public Chunk provideChunk(BlockPos blockPosIn) {
    return provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
  }
}
