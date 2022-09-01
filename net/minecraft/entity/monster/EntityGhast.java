package net.minecraft.entity.monster;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
  private int explosionStrength = 1;
  
  public EntityGhast(World worldIn) {
    super(worldIn);
    setSize(4.0F, 4.0F);
    this.isImmuneToFire = true;
    this.experienceValue = 5;
    this.moveHelper = (EntityMoveHelper)new GhastMoveHelper(this);
    this.tasks.addTask(5, (EntityAIBase)new AIRandomFly(this));
    this.tasks.addTask(7, (EntityAIBase)new AILookAround(this));
    this.tasks.addTask(7, (EntityAIBase)new AIFireballAttack(this));
    this.targetTasks.addTask(1, (EntityAIBase)new EntityAIFindEntityNearestPlayer((EntityLiving)this));
  }
  
  public boolean isAttacking() {
    return (this.dataWatcher.getWatchableObjectByte(16) != 0);
  }
  
  public void setAttacking(boolean p_175454_1_) {
    this.dataWatcher.updateObject(16, Byte.valueOf((byte)(p_175454_1_ ? 1 : 0)));
  }
  
  public int getFireballStrength() {
    return this.explosionStrength;
  }
  
  public void onUpdate() {
    super.onUpdate();
    if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL)
      setDead(); 
  }
  
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if (isEntityInvulnerable(source))
      return false; 
    if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
      super.attackEntityFrom(source, 1000.0F);
      ((EntityPlayer)source.getEntity()).triggerAchievement((StatBase)AchievementList.ghast);
      return true;
    } 
    return super.attackEntityFrom(source, amount);
  }
  
  protected void entityInit() {
    super.entityInit();
    this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
  }
  
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(100.0D);
  }
  
  protected String getLivingSound() {
    return "mob.ghast.moan";
  }
  
  protected String getHurtSound() {
    return "mob.ghast.scream";
  }
  
  protected String getDeathSound() {
    return "mob.ghast.death";
  }
  
  protected Item getDropItem() {
    return Items.gunpowder;
  }
  
  protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    int i = this.rand.nextInt(2) + this.rand.nextInt(1 + p_70628_2_);
    for (int j = 0; j < i; j++)
      dropItem(Items.ghast_tear, 1); 
    i = this.rand.nextInt(3) + this.rand.nextInt(1 + p_70628_2_);
    for (int k = 0; k < i; k++)
      dropItem(Items.gunpowder, 1); 
  }
  
  protected float getSoundVolume() {
    return 10.0F;
  }
  
  public boolean getCanSpawnHere() {
    return (this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL);
  }
  
  public int getMaxSpawnedInChunk() {
    return 1;
  }
  
  public void writeEntityToNBT(NBTTagCompound tagCompound) {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setInteger("ExplosionPower", this.explosionStrength);
  }
  
  public void readEntityFromNBT(NBTTagCompound tagCompund) {
    super.readEntityFromNBT(tagCompund);
    if (tagCompund.hasKey("ExplosionPower", 99))
      this.explosionStrength = tagCompund.getInteger("ExplosionPower"); 
  }
  
  public float getEyeHeight() {
    return 2.6F;
  }
  
  static class EntityGhast {}
  
  static class EntityGhast {}
  
  static class EntityGhast {}
  
  static class EntityGhast {}
}
