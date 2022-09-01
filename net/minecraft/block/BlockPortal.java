package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
  public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, (Enum[])new EnumFacing.Axis[] { EnumFacing.Axis.X, EnumFacing.Axis.Z });
  
  public BlockPortal() {
    super(Material.portal, false);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.X));
    setTickRandomly(true);
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    super.updateTick(worldIn, pos, state, rand);
    if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
      int i = pos.getY();
      BlockPos blockpos;
      for (blockpos = pos; !World.doesBlockHaveSolidTopSurface((IBlockAccess)worldIn, blockpos) && blockpos.getY() > 0; blockpos = blockpos.down());
      if (i > 0 && !worldIn.getBlockState(blockpos.up()).getBlock().isNormalCube()) {
        Entity entity = ItemMonsterPlacer.spawnCreature(worldIn, 57, blockpos.getX() + 0.5D, blockpos.getY() + 1.1D, blockpos.getZ() + 0.5D);
        if (entity != null)
          entity.timeUntilPortal = entity.getPortalCooldown(); 
      } 
    } 
  }
  
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    return null;
  }
  
  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
    EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)worldIn.getBlockState(pos).getValue((IProperty)AXIS);
    float f = 0.125F;
    float f1 = 0.125F;
    if (enumfacing$axis == EnumFacing.Axis.X)
      f = 0.5F; 
    if (enumfacing$axis == EnumFacing.Axis.Z)
      f1 = 0.5F; 
    setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
  }
  
  public static int getMetaForAxis(EnumFacing.Axis axis) {
    return (axis == EnumFacing.Axis.X) ? 1 : ((axis == EnumFacing.Axis.Z) ? 2 : 0);
  }
  
  public boolean isFullCube() {
    return false;
  }
  
  public boolean func_176548_d(World worldIn, BlockPos p_176548_2_) {
    Size blockportal$size = new Size(worldIn, p_176548_2_, EnumFacing.Axis.X);
    if (blockportal$size.func_150860_b() && Size.access$000(blockportal$size) == 0) {
      blockportal$size.func_150859_c();
      return true;
    } 
    Size blockportal$size1 = new Size(worldIn, p_176548_2_, EnumFacing.Axis.Z);
    if (blockportal$size1.func_150860_b() && Size.access$000(blockportal$size1) == 0) {
      blockportal$size1.func_150859_c();
      return true;
    } 
    return false;
  }
  
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
    EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)state.getValue((IProperty)AXIS);
    if (enumfacing$axis == EnumFacing.Axis.X) {
      Size blockportal$size = new Size(worldIn, pos, EnumFacing.Axis.X);
      if (!blockportal$size.func_150860_b() || Size.access$000(blockportal$size) < Size.access$100(blockportal$size) * Size.access$200(blockportal$size))
        worldIn.setBlockState(pos, Blocks.air.getDefaultState()); 
    } else if (enumfacing$axis == EnumFacing.Axis.Z) {
      Size blockportal$size1 = new Size(worldIn, pos, EnumFacing.Axis.Z);
      if (!blockportal$size1.func_150860_b() || Size.access$000(blockportal$size1) < Size.access$100(blockportal$size1) * Size.access$200(blockportal$size1))
        worldIn.setBlockState(pos, Blocks.air.getDefaultState()); 
    } 
  }
  
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    EnumFacing.Axis enumfacing$axis = null;
    IBlockState iblockstate = worldIn.getBlockState(pos);
    if (worldIn.getBlockState(pos).getBlock() == this) {
      enumfacing$axis = (EnumFacing.Axis)iblockstate.getValue((IProperty)AXIS);
      if (enumfacing$axis == null)
        return false; 
      if (enumfacing$axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST)
        return false; 
      if (enumfacing$axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH)
        return false; 
    } 
    boolean flag = (worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this);
    boolean flag1 = (worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this);
    boolean flag2 = (worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this);
    boolean flag3 = (worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this);
    boolean flag4 = (flag || flag1 || enumfacing$axis == EnumFacing.Axis.X);
    boolean flag5 = (flag2 || flag3 || enumfacing$axis == EnumFacing.Axis.Z);
    return ((flag4 && side == EnumFacing.WEST) || (flag4 && side == EnumFacing.EAST) || (flag5 && side == EnumFacing.NORTH) || (flag5 && side == EnumFacing.SOUTH));
  }
  
  public int quantityDropped(Random random) {
    return 0;
  }
  
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.TRANSLUCENT;
  }
  
  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null)
      entityIn.func_181015_d(pos); 
  }
  
  public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (rand.nextInt(100) == 0)
      worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false); 
    for (int i = 0; i < 4; i++) {
      double d0 = (pos.getX() + rand.nextFloat());
      double d1 = (pos.getY() + rand.nextFloat());
      double d2 = (pos.getZ() + rand.nextFloat());
      double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
      double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
      double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
      int j = rand.nextInt(2) * 2 - 1;
      if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
        d0 = pos.getX() + 0.5D + 0.25D * j;
        d3 = (rand.nextFloat() * 2.0F * j);
      } else {
        d2 = pos.getZ() + 0.5D + 0.25D * j;
        d5 = (rand.nextFloat() * 2.0F * j);
      } 
      worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
    } 
  }
  
  public Item getItem(World worldIn, BlockPos pos) {
    return null;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)AXIS, ((meta & 0x3) == 2) ? (Comparable)EnumFacing.Axis.Z : (Comparable)EnumFacing.Axis.X);
  }
  
  public int getMetaFromState(IBlockState state) {
    return getMetaForAxis((EnumFacing.Axis)state.getValue((IProperty)AXIS));
  }
  
  protected BlockState createBlockState() {
    return new BlockState((Block)this, new IProperty[] { (IProperty)AXIS });
  }
  
  public BlockPattern.PatternHelper func_181089_f(World p_181089_1_, BlockPos p_181089_2_) {
    EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
    Size blockportal$size = new Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.X);
    LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.func_181627_a(p_181089_1_, true);
    if (!blockportal$size.func_150860_b()) {
      enumfacing$axis = EnumFacing.Axis.X;
      blockportal$size = new Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.Z);
    } 
    if (!blockportal$size.func_150860_b())
      return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1); 
    int[] aint = new int[(EnumFacing.AxisDirection.values()).length];
    EnumFacing enumfacing = Size.access$300(blockportal$size).rotateYCCW();
    BlockPos blockpos = Size.access$400(blockportal$size).up(blockportal$size.func_181100_a() - 1);
    for (EnumFacing.AxisDirection enumfacing$axisdirection : EnumFacing.AxisDirection.values()) {
      BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper((enumfacing.getAxisDirection() == enumfacing$axisdirection) ? blockpos : blockpos.offset(Size.access$300(blockportal$size), blockportal$size.func_181101_b() - 1), EnumFacing.func_181076_a(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.func_181101_b(), blockportal$size.func_181100_a(), 1);
      for (int i = 0; i < blockportal$size.func_181101_b(); i++) {
        for (int j = 0; j < blockportal$size.func_181100_a(); j++) {
          BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);
          if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getBlock().getMaterial() != Material.air)
            aint[enumfacing$axisdirection.ordinal()] = aint[enumfacing$axisdirection.ordinal()] + 1; 
        } 
      } 
    } 
    EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;
    for (EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
      if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()])
        enumfacing$axisdirection1 = enumfacing$axisdirection2; 
    } 
    return new BlockPattern.PatternHelper((enumfacing.getAxisDirection() == enumfacing$axisdirection1) ? blockpos : blockpos.offset(Size.access$300(blockportal$size), blockportal$size.func_181101_b() - 1), EnumFacing.func_181076_a(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.func_181101_b(), blockportal$size.func_181100_a(), 1);
  }
  
  public static class BlockPortal {}
}
