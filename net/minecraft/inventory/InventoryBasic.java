package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
  private String inventoryTitle;
  
  private final int slotsCount;
  
  private final ItemStack[] inventoryContents;
  
  private List<IInvBasic> field_70480_d;
  
  private boolean hasCustomName;
  
  public InventoryBasic(String title, boolean customName, int slotCount) {
    this.inventoryTitle = title;
    this.hasCustomName = customName;
    this.slotsCount = slotCount;
    this.inventoryContents = new ItemStack[slotCount];
  }
  
  public InventoryBasic(IChatComponent title, int slotCount) {
    this(title.getUnformattedText(), true, slotCount);
  }
  
  public void func_110134_a(IInvBasic p_110134_1_) {
    if (this.field_70480_d == null)
      this.field_70480_d = Lists.newArrayList(); 
    this.field_70480_d.add(p_110134_1_);
  }
  
  public void func_110132_b(IInvBasic p_110132_1_) {
    this.field_70480_d.remove(p_110132_1_);
  }
  
  public ItemStack getStackInSlot(int index) {
    return (index >= 0 && index < this.inventoryContents.length) ? this.inventoryContents[index] : null;
  }
  
  public ItemStack decrStackSize(int index, int count) {
    if (this.inventoryContents[index] != null) {
      if ((this.inventoryContents[index]).stackSize <= count) {
        ItemStack itemstack1 = this.inventoryContents[index];
        this.inventoryContents[index] = null;
        markDirty();
        return itemstack1;
      } 
      ItemStack itemstack = this.inventoryContents[index].splitStack(count);
      if ((this.inventoryContents[index]).stackSize == 0)
        this.inventoryContents[index] = null; 
      markDirty();
      return itemstack;
    } 
    return null;
  }
  
  public ItemStack func_174894_a(ItemStack stack) {
    ItemStack itemstack = stack.copy();
    for (int i = 0; i < this.slotsCount; i++) {
      ItemStack itemstack1 = getStackInSlot(i);
      if (itemstack1 == null) {
        setInventorySlotContents(i, itemstack);
        markDirty();
        return null;
      } 
      if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
        int j = Math.min(getInventoryStackLimit(), itemstack1.getMaxStackSize());
        int k = Math.min(itemstack.stackSize, j - itemstack1.stackSize);
        if (k > 0) {
          itemstack1.stackSize += k;
          itemstack.stackSize -= k;
          if (itemstack.stackSize <= 0) {
            markDirty();
            return null;
          } 
        } 
      } 
    } 
    if (itemstack.stackSize != stack.stackSize)
      markDirty(); 
    return itemstack;
  }
  
  public ItemStack removeStackFromSlot(int index) {
    if (this.inventoryContents[index] != null) {
      ItemStack itemstack = this.inventoryContents[index];
      this.inventoryContents[index] = null;
      return itemstack;
    } 
    return null;
  }
  
  public void setInventorySlotContents(int index, ItemStack stack) {
    this.inventoryContents[index] = stack;
    if (stack != null && stack.stackSize > getInventoryStackLimit())
      stack.stackSize = getInventoryStackLimit(); 
    markDirty();
  }
  
  public int getSizeInventory() {
    return this.slotsCount;
  }
  
  public String getName() {
    return this.inventoryTitle;
  }
  
  public boolean hasCustomName() {
    return this.hasCustomName;
  }
  
  public void setCustomName(String inventoryTitleIn) {
    this.hasCustomName = true;
    this.inventoryTitle = inventoryTitleIn;
  }
  
  public IChatComponent getDisplayName() {
    return hasCustomName() ? (IChatComponent)new ChatComponentText(getName()) : (IChatComponent)new ChatComponentTranslation(getName(), new Object[0]);
  }
  
  public int getInventoryStackLimit() {
    return 64;
  }
  
  public void markDirty() {
    if (this.field_70480_d != null)
      for (int i = 0; i < this.field_70480_d.size(); i++)
        ((IInvBasic)this.field_70480_d.get(i)).onInventoryChanged(this);  
  }
  
  public boolean isUseableByPlayer(EntityPlayer player) {
    return true;
  }
  
  public void openInventory(EntityPlayer player) {}
  
  public void closeInventory(EntityPlayer player) {}
  
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return true;
  }
  
  public int getField(int id) {
    return 0;
  }
  
  public void setField(int id, int value) {}
  
  public int getFieldCount() {
    return 0;
  }
  
  public void clear() {
    for (int i = 0; i < this.inventoryContents.length; i++)
      this.inventoryContents[i] = null; 
  }
}
