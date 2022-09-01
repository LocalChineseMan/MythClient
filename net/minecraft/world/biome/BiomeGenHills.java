package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
  private final WorldGenerator theWorldGenerator = (WorldGenerator)new WorldGenMinable(Blocks.monster_egg.getDefaultState().withProperty((IProperty)BlockSilverfish.VARIANT, (Comparable)BlockSilverfish.EnumType.STONE), 9);
  
  private final WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
  
  private final int field_150635_aE = 0;
  
  private final int field_150636_aF = 1;
  
  private final int field_150637_aG = 2;
  
  private int field_150638_aH;
  
  protected BiomeGenHills(int p_i45373_1_, boolean p_i45373_2_) {
    super(p_i45373_1_);
    getClass();
    this.field_150638_aH = 0;
    if (p_i45373_2_) {
      this.theBiomeDecorator.treesPerChunk = 3;
      getClass();
      this.field_150638_aH = 1;
    } 
  }
  
  public WorldGenAbstractTree genBigTreeChance(Random rand) {
    return (rand.nextInt(3) > 0) ? (WorldGenAbstractTree)this.field_150634_aD : super.genBigTreeChance(rand);
  }
  
  public void decorate(World worldIn, Random rand, BlockPos pos) {
    super.decorate(worldIn, rand, pos);
    int i = 3 + rand.nextInt(6);
    for (int j = 0; j < i; j++) {
      int k = rand.nextInt(16);
      int l = rand.nextInt(28) + 4;
      int i1 = rand.nextInt(16);
      BlockPos blockpos = pos.add(k, l, i1);
      if (worldIn.getBlockState(blockpos).getBlock() == Blocks.stone)
        worldIn.setBlockState(blockpos, Blocks.emerald_ore.getDefaultState(), 2); 
    } 
    for (i = 0; i < 7; i++) {
      int j1 = rand.nextInt(16);
      int k1 = rand.nextInt(64);
      int l1 = rand.nextInt(16);
      this.theWorldGenerator.generate(worldIn, rand, pos.add(j1, k1, l1));
    } 
  }
  
  public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180622_4_, int p_180622_5_, double p_180622_6_) {
    this.topBlock = Blocks.grass.getDefaultState();
    this.fillerBlock = Blocks.dirt.getDefaultState();
    getClass();
    if ((p_180622_6_ < -1.0D || p_180622_6_ > 2.0D) && this.field_150638_aH == 2) {
      this.topBlock = Blocks.gravel.getDefaultState();
      this.fillerBlock = Blocks.gravel.getDefaultState();
    } else {
      getClass();
      if (p_180622_6_ > 1.0D && this.field_150638_aH != 1) {
        this.topBlock = Blocks.stone.getDefaultState();
        this.fillerBlock = Blocks.stone.getDefaultState();
      } 
    } 
    generateBiomeTerrain(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
  }
  
  private BiomeGenHills mutateHills(BiomeGenBase p_150633_1_) {
    getClass();
    this.field_150638_aH = 2;
    func_150557_a(p_150633_1_.color, true);
    setBiomeName(p_150633_1_.biomeName + " M");
    setHeight(new BiomeGenBase.Height(p_150633_1_.minHeight, p_150633_1_.maxHeight));
    setTemperatureRainfall(p_150633_1_.temperature, p_150633_1_.rainfall);
    return this;
  }
  
  protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
    return (new BiomeGenHills(p_180277_1_, false)).mutateHills(this);
  }
}
