package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
  private final AISummonSilverfish summonSilverfish;
  
  public EntitySilverfish(World worldIn) {
    super(worldIn);
    setSize(0.4F, 0.3F);
    this.tasks.addTask(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
    this.tasks.addTask(3, (EntityAIBase)(this.summonSilverfish = new AISummonSilverfish(this)));
    this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackOnCollide((EntityCreature)this, EntityPlayer.class, 1.0D, false));
    this.tasks.addTask(5, (EntityAIBase)new AIHideInStone(this));
    this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
    this.targetTasks.addTask(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityPlayer.class, true));
  }
  
  public double getYOffset() {
    return 0.2D;
  }
  
  public float getEyeHeight() {
    return 0.1F;
  }
  
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
  }
  
  protected boolean canTriggerWalking() {
    return false;
  }
  
  protected String getLivingSound() {
    return "mob.silverfish.say";
  }
  
  protected String getHurtSound() {
    return "mob.silverfish.hit";
  }
  
  protected String getDeathSound() {
    return "mob.silverfish.kill";
  }
  
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if (isEntityInvulnerable(source))
      return false; 
    if (source instanceof net.minecraft.util.EntityDamageSource || source == DamageSource.magic)
      this.summonSilverfish.func_179462_f(); 
    return super.attackEntityFrom(source, amount);
  }
  
  protected void playStepSound(BlockPos pos, Block blockIn) {
    playSound("mob.silverfish.step", 0.15F, 1.0F);
  }
  
  protected Item getDropItem() {
    return null;
  }
  
  public void onUpdate() {
    this.renderYawOffset = this.rotationYaw;
    super.onUpdate();
  }
  
  public float getBlockPathWeight(BlockPos pos) {
    return (this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone) ? 10.0F : super.getBlockPathWeight(pos);
  }
  
  protected boolean isValidLightLevel() {
    return true;
  }
  
  public boolean getCanSpawnHere() {
    if (super.getCanSpawnHere()) {
      EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity((Entity)this, 5.0D);
      return (entityplayer == null);
    } 
    return false;
  }
  
  public EnumCreatureAttribute getCreatureAttribute() {
    return EnumCreatureAttribute.ARTHROPOD;
  }
  
  static class EntitySilverfish {}
  
  static class EntitySilverfish {}
}
