package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {
  public static final IBehaviorDispenseItem itemDispenseBehaviorProvider = (IBehaviorDispenseItem)new Object();
  
  ItemStack dispense(IBlockSource paramIBlockSource, ItemStack paramItemStack);
}
