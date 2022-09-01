package notthatuwu.xyz.mythrecode.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;

public class ItemUtils {
  protected static Minecraft mc = Minecraft.getMinecraft();
  
  public static boolean isInventoryFull() {
    for (int index = 9; index <= 44; index++) {
      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
      if (stack == null)
        return false; 
    } 
    return true;
  }
  
  public static boolean isBad(ItemStack item) {
    return (!(item.getItem() instanceof net.minecraft.item.ItemArmor) && !(item.getItem() instanceof net.minecraft.item.ItemTool) && !(item.getItem() instanceof net.minecraft.item.ItemBlock) && !(item.getItem() instanceof net.minecraft.item.ItemSword) && !(item.getItem() instanceof net.minecraft.item.ItemEnderPearl) && !(item.getItem() instanceof net.minecraft.item.ItemFood) && (!(item.getItem() instanceof ItemPotion) || isBadPotion(item)) && !item.getDisplayName().toLowerCase().contains(EnumChatFormatting.GRAY + "(right click)"));
  }
  
  public static double nextDouble(double max, double min) {
    return Math.random() * (max - min) + min;
  }
  
  public static boolean isBadPotion(ItemStack stack) {
    if (stack != null && stack.getItem() instanceof ItemPotion) {
      ItemPotion potion = (ItemPotion)stack.getItem();
      if (ItemPotion.isSplash(stack.getItemDamage()))
        for (Object o : potion.getEffects(stack)) {
          PotionEffect effect = (PotionEffect)o;
          if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId())
            return true; 
        }  
    } 
    return false;
  }
}
