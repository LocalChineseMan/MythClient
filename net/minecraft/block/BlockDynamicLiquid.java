package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
  int adjacentSourceBlocks;
  
  protected BlockDynamicLiquid(Material materialIn) {
    super(materialIn);
  }
  
  private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState) {
    worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty((IProperty)LEVEL, currentState.getValue((IProperty)LEVEL)), 2);
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    int i = ((Integer)state.getValue((IProperty)LEVEL)).intValue();
    int j = 1;
    if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize())
      j = 2; 
    int k = tickRate(worldIn);
    if (i > 0) {
      int l = -100;
      this.adjacentSourceBlocks = 0;
      for (Object enumfacing : EnumFacing.Plane.HORIZONTAL)
        l = checkAdjacentBlock(worldIn, pos.offset((EnumFacing)enumfacing), l); 
      int i1 = l + j;
      if (i1 >= 8 || l < 0)
        i1 = -1; 
      if (getLevel((IBlockAccess)worldIn, pos.up()) >= 0) {
        int j1 = getLevel((IBlockAccess)worldIn, pos.up());
        if (j1 >= 8) {
          i1 = j1;
        } else {
          i1 = j1 + 8;
        } 
      } 
      if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
        IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
        if (iblockstate1.getBlock().getMaterial().isSolid()) {
          i1 = 0;
        } else if (iblockstate1.getBlock().getMaterial() == this.blockMaterial && ((Integer)iblockstate1.getValue((IProperty)LEVEL)).intValue() == 0) {
          i1 = 0;
        } 
      } 
      if (this.blockMaterial == Material.lava && i < 8 && i1 < 8 && i1 > i && rand.nextInt(4) != 0)
        k *= 4; 
      if (i1 == i) {
        placeStaticBlock(worldIn, pos, state);
      } else {
        i = i1;
        if (i1 < 0) {
          worldIn.setBlockToAir(pos);
        } else {
          state = state.withProperty((IProperty)LEVEL, Integer.valueOf(i1));
          worldIn.setBlockState(pos, state, 2);
          worldIn.scheduleUpdate(pos, this, k);
          worldIn.notifyNeighborsOfStateChange(pos, this);
        } 
      } 
    } else {
      placeStaticBlock(worldIn, pos, state);
    } 
    IBlockState iblockstate = worldIn.getBlockState(pos.down());
    if (canFlowInto(worldIn, pos.down(), iblockstate)) {
      if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water) {
        worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
        triggerMixEffects(worldIn, pos.down());
        return;
      } 
      if (i >= 8) {
        tryFlowInto(worldIn, pos.down(), iblockstate, i);
      } else {
        tryFlowInto(worldIn, pos.down(), iblockstate, i + 8);
      } 
    } else if (i >= 0 && (i == 0 || isBlocked(worldIn, pos.down(), iblockstate))) {
      Set<EnumFacing> set = getPossibleFlowDirections(worldIn, pos);
      int k1 = i + j;
      if (i >= 8)
        k1 = 1; 
      if (k1 >= 8)
        return; 
      for (EnumFacing enumfacing1 : set)
        tryFlowInto(worldIn, pos.offset(enumfacing1), worldIn.getBlockState(pos.offset(enumfacing1)), k1); 
    } 
  }
  
  private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
    if (canFlowInto(worldIn, pos, state)) {
      if (state.getBlock() != Blocks.air)
        if (this.blockMaterial == Material.lava) {
          triggerMixEffects(worldIn, pos);
        } else {
          state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
        }  
      worldIn.setBlockState(pos, getDefaultState().withProperty((IProperty)LEVEL, Integer.valueOf(level)), 3);
    } 
  }
  
  private int func_176374_a(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost) {
    int i = 1000;
    for (Object enumfacing : EnumFacing.Plane.HORIZONTAL) {
      if (enumfacing != calculateFlowCost) {
        BlockPos blockpos = pos.offset((EnumFacing)enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        if (!isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || ((Integer)iblockstate.getValue((IProperty)LEVEL)).intValue() > 0)) {
          if (!isBlocked(worldIn, blockpos.down(), iblockstate))
            return distance; 
          if (distance < 4) {
            int j = func_176374_a(worldIn, blockpos, distance + 1, ((EnumFacing)enumfacing).getOpposite());
            if (j < i)
              i = j; 
          } 
        } 
      } 
    } 
    return i;
  }
  
  private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
    int i = 1000;
    Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
    for (Object enumfacing : EnumFacing.Plane.HORIZONTAL) {
      BlockPos blockpos = pos.offset((EnumFacing)enumfacing);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (!isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || ((Integer)iblockstate.getValue((IProperty)LEVEL)).intValue() > 0)) {
        int j;
        if (isBlocked(worldIn, blockpos.down(), worldIn.getBlockState(blockpos.down()))) {
          j = func_176374_a(worldIn, blockpos, 1, ((EnumFacing)enumfacing).getOpposite());
        } else {
          j = 0;
        } 
        if (j < i)
          set.clear(); 
        if (j <= i) {
          set.add((EnumFacing)enumfacing);
          i = j;
        } 
      } 
    } 
    return set;
  }
  
  private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
    Block block = worldIn.getBlockState(pos).getBlock();
    return (block instanceof BlockDoor || block == Blocks.standing_sign || block == Blocks.ladder || block == Blocks.reeds || block.blockMaterial == Material.portal || block.blockMaterial.blocksMovement());
  }
  
  protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel) {
    int i = getLevel((IBlockAccess)worldIn, pos);
    if (i < 0)
      return currentMinLevel; 
    if (i == 0)
      this.adjacentSourceBlocks++; 
    if (i >= 8)
      i = 0; 
    return (currentMinLevel >= 0 && i >= currentMinLevel) ? currentMinLevel : i;
  }
  
  private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
    Material material = state.getBlock().getMaterial();
    return (material != this.blockMaterial && material != Material.lava && !isBlocked(worldIn, pos, state));
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    if (!checkForMixing(worldIn, pos, state))
      worldIn.scheduleUpdate(pos, this, tickRate(worldIn)); 
  }
}
