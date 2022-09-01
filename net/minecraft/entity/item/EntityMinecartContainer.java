package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
  private ItemStack[] minecartContainerItems = new ItemStack[36];
  
  private boolean dropContentsWhenDead = true;
  
  public EntityMinecartContainer(World worldIn) {
    super(worldIn);
  }
  
  public EntityMinecartContainer(World worldIn, double p_i1717_2_, double p_i1717_4_, double p_i1717_6_) {
    super(worldIn, p_i1717_2_, p_i1717_4_, p_i1717_6_);
  }
  
  public void killMinecart(DamageSource p_94095_1_) {
    super.killMinecart(p_94095_1_);
    if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
      InventoryHelper.func_180176_a(this.worldObj, this, (IInventory)this); 
  }
  
  public ItemStack getStackInSlot(int index) {
    return this.minecartContainerItems[index];
  }
  
  public ItemStack decrStackSize(int index, int count) {
    if (this.minecartContainerItems[index] != null) {
      if ((this.minecartContainerItems[index]).stackSize <= count) {
        ItemStack itemstack1 = this.minecartContainerItems[index];
        this.minecartContainerItems[index] = null;
        return itemstack1;
      } 
      ItemStack itemstack = this.minecartContainerItems[index].splitStack(count);
      if ((this.minecartContainerItems[index]).stackSize == 0)
        this.minecartContainerItems[index] = null; 
      return itemstack;
    } 
    return null;
  }
  
  public ItemStack removeStackFromSlot(int index) {
    if (this.minecartContainerItems[index] != null) {
      ItemStack itemstack = this.minecartContainerItems[index];
      this.minecartContainerItems[index] = null;
      return itemstack;
    } 
    return null;
  }
  
  public void setInventorySlotContents(int index, ItemStack stack) {
    this.minecartContainerItems[index] = stack;
    if (stack != null && stack.stackSize > getInventoryStackLimit())
      stack.stackSize = getInventoryStackLimit(); 
  }
  
  public void markDirty() {}
  
  public boolean isUseableByPlayer(EntityPlayer player) {
    return (!this.isDead && player.getDistanceSqToEntity(this) <= 64.0D);
  }
  
  public void openInventory(EntityPlayer player) {}
  
  public void closeInventory(EntityPlayer player) {}
  
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return true;
  }
  
  public String getName() {
    return hasCustomName() ? getCustomNameTag() : "container.minecart";
  }
  
  public int getInventoryStackLimit() {
    return 64;
  }
  
  public void travelToDimension(int dimensionId) {
    this.dropContentsWhenDead = false;
    super.travelToDimension(dimensionId);
  }
  
  public void setDead() {
    if (this.dropContentsWhenDead)
      InventoryHelper.func_180176_a(this.worldObj, this, (IInventory)this); 
    super.setDead();
  }
  
  protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    super.writeEntityToNBT(tagCompound);
    NBTTagList nbttaglist = new NBTTagList();
    for (int i = 0; i < this.minecartContainerItems.length; i++) {
      if (this.minecartContainerItems[i] != null) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setByte("Slot", (byte)i);
        this.minecartContainerItems[i].writeToNBT(nbttagcompound);
        nbttaglist.appendTag((NBTBase)nbttagcompound);
      } 
    } 
    tagCompound.setTag("Items", (NBTBase)nbttaglist);
  }
  
  protected void readEntityFromNBT(NBTTagCompound tagCompund) {
    super.readEntityFromNBT(tagCompund);
    NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);
    this.minecartContainerItems = new ItemStack[getSizeInventory()];
    for (int i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
      int j = nbttagcompound.getByte("Slot") & 0xFF;
      if (j >= 0 && j < this.minecartContainerItems.length)
        this.minecartContainerItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound); 
    } 
  }
  
  public boolean interactFirst(EntityPlayer playerIn) {
    if (!this.worldObj.isRemote)
      playerIn.displayGUIChest((IInventory)this); 
    return true;
  }
  
  protected void applyDrag() {
    int i = 15 - Container.calcRedstoneFromInventory((IInventory)this);
    float f = 0.98F + i * 0.001F;
    this.motionX *= f;
    this.motionY *= 0.0D;
    this.motionZ *= f;
  }
  
  public int getField(int id) {
    return 0;
  }
  
  public void setField(int id, int value) {}
  
  public int getFieldCount() {
    return 0;
  }
  
  public boolean isLocked() {
    return false;
  }
  
  public void setLockCode(LockCode code) {}
  
  public LockCode getLockCode() {
    return LockCode.EMPTY_CODE;
  }
  
  public void clear() {
    for (int i = 0; i < this.minecartContainerItems.length; i++)
      this.minecartContainerItems[i] = null; 
  }
}
