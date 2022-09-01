package notthatuwu.xyz.mythrecode.modules.player;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NoteSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "InvManager", category = Category.PLAYER)
public class InvManager extends Module {
  public NoteSetting noteCommonSettings = new NoteSetting("Common Settings", this);
  
  public NumberSetting startDelay = new NumberSetting("Start Delay", this, 150.0D, 0.0D, 1000.0D, true);
  
  public NumberSetting delay = new NumberSetting("Delay", this, 150.0D, 0.0D, 1000.0D, true);
  
  public NumberSetting blockCap = new NumberSetting("Delay", this, 128.0D, 0.0D, 200.0D, true);
  
  public BooleanSetting invCleaner = new BooleanSetting("Cleaner", this, true);
  
  public BooleanSetting autoArmor = new BooleanSetting("Auto Armor", this, true);
  
  public BooleanSetting inventoryOnly = new BooleanSetting("Inventory Only", this, true);
  
  public NoteSetting noteCleanerSettings = new NoteSetting("Items Settings", this);
  
  public BooleanSetting sword = new BooleanSetting("Sword", this, true);
  
  public BooleanSetting sort = new BooleanSetting("Sort", this, true);
  
  public BooleanSetting food = new BooleanSetting("Food", this, true);
  
  public BooleanSetting archery = new BooleanSetting("Archery", this, true);
  
  private boolean openInventory;
  
  private final TimeHelper timeHelper = new TimeHelper();
  
  private final TimeHelper startTimeHelper = new TimeHelper();
  
  private int lastSlot;
  
  final ArrayList<Integer> whitelistedItems = new ArrayList<>();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    setSuffix(Integer.valueOf(this.delay.getValueInt()));
    if (event.isPre()) {
      if (!(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
        this.startTimeHelper.reset(); 
      if (this.startTimeHelper.hasReached(this.startDelay.getValueLong()) && (
        !(mc.thePlayer.openContainer instanceof net.minecraft.inventory.ContainerChest) || !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer))) {
        long delay = this.delay.getValue().longValue();
        if (!this.inventoryOnly.getValue().booleanValue() || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory) {
          if (this.inventoryOnly.getValue().booleanValue() && MoveUtil.isMoving() && ModuleManager.getModuleByName("InvMove").isEnabled())
            return; 
          if (mc.currentScreen == null || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) {
            if (this.timeHelper.hasReached(delay))
              if (!mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
                getBestWeapon(36);
              } else if (!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
                getBestWeapon(36);
              }  
            if (this.timeHelper.hasReached(delay) && 
              this.autoArmor.getValue().booleanValue())
              getBestArmor(); 
            if (this.sort.getValue().booleanValue()) {
              if (this.timeHelper.hasReached(delay))
                getBestPickaxe(37); 
              if (this.timeHelper.hasReached(delay))
                getBestShovel(39); 
              if (this.timeHelper.hasReached(delay))
                getBestAxe(38); 
            } 
            if (this.timeHelper.hasReached(delay) && this.invCleaner.getValue().booleanValue() && !mc.thePlayer.isUsingItem())
              for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                  ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                  if (shouldDrop(is, i)) {
                    drop(i);
                    if (delay > 0L)
                      break; 
                  } 
                } 
              }  
          } 
        } 
      } 
    } 
  }
  
  public void onEnable() {
    this.startTimeHelper.reset();
    super.onEnable();
  }
  
  public void shiftClick(int slot) {
    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, (EntityPlayer)mc.thePlayer);
  }
  
  public void swap(int slot1, int hotbarSlot) {
    open();
    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, (EntityPlayer)mc.thePlayer);
    close();
  }
  
  public void drop(int slot) {
    open();
    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, (EntityPlayer)mc.thePlayer);
    close();
  }
  
  public boolean isBestWeapon(ItemStack stack) {
    float damage = getDamage(stack);
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (getDamage(is) > damage && (is.getItem() instanceof ItemSword || !this.sword.getValue().booleanValue()))
          return false; 
      } 
    } 
    return (stack.getItem() instanceof ItemSword || !this.sword.getValue().booleanValue());
  }
  
  public void getBestWeapon(int slot) {
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (isBestWeapon(is) && getDamage(is) > 0.0F && (is.getItem() instanceof ItemSword || !this.sword.getValue().booleanValue())) {
          swap(i, slot - 36);
          break;
        } 
      } 
    } 
  }
  
  private float getDamage(ItemStack stack) {
    float damage = 0.0F;
    Item item = stack.getItem();
    if (item instanceof ItemTool) {
      ItemTool tool = (ItemTool)item;
      damage += tool.getMaxDamage();
    } 
    if (item instanceof ItemSword) {
      ItemSword sword = (ItemSword)item;
      damage += sword.getDamageVsEntity();
    } 
    damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01F;
    return damage;
  }
  
  public boolean shouldDrop(ItemStack stack, int slot) {
    if (stack.getDisplayName().contains("???"))
      return false; 
    if (stack.getDisplayName().contains("???"))
      return false; 
    if (stack.getDisplayName().toLowerCase().contains("(right click)"))
      return false; 
    if (stack.getDisplayName().toLowerCase().contains("tracking compass"))
      return false; 
    if ((slot != 36 || !isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) && (slot != 37 || !isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(37).getStack())) && (slot != 38 || !isBestAxe(mc.thePlayer.inventoryContainer.getSlot(38).getStack())) && (slot != 39 || !isBestShovel(mc.thePlayer.inventoryContainer.getSlot(39).getStack()))) {
      if (stack.getItem() instanceof ItemArmor)
        for (int type = 1; type < 5; type++) {
          if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
            if (isBestArmor(is, type))
              continue; 
          } 
          if (isBestArmor(stack, type))
            return false; 
          continue;
        }  
      if (this.blockCap.getValue().intValue() != 0 && stack.getItem() instanceof net.minecraft.item.ItemBlock && getBlockCount() > this.blockCap.getValueInt())
        return true; 
      if (stack.getItem() instanceof ItemPotion && isBadPotion(stack))
        return true; 
      if (stack.getItem() instanceof net.minecraft.item.ItemFood && this.food.getValue().booleanValue() && !(stack.getItem() instanceof net.minecraft.item.ItemAppleGold))
        return true; 
      if (!(stack.getItem() instanceof net.minecraft.item.ItemHoe) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemArmor)) {
        if ((stack.getItem() instanceof net.minecraft.item.ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && this.archery.getValue().booleanValue())
          return true; 
        return (stack.getItem().getUnlocalizedName().contains("tnt") || stack.getItem().getUnlocalizedName().contains("stick") || stack.getItem().getUnlocalizedName().contains("egg") || stack.getItem().getUnlocalizedName().contains("string") || stack.getItem().getUnlocalizedName().contains("cake") || stack.getItem().getUnlocalizedName().contains("mushroom") || stack.getItem().getUnlocalizedName().contains("flint") || stack.getItem().getUnlocalizedName().contains("compass") || stack.getItem().getUnlocalizedName().contains("dyePowder") || stack.getItem().getUnlocalizedName().contains("feather") || stack.getItem().getUnlocalizedName().contains("bucket") || (stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect")) || stack.getItem().getUnlocalizedName().contains("snow") || stack.getItem().getUnlocalizedName().contains("fish") || stack.getItem().getUnlocalizedName().contains("enchant") || stack.getItem().getUnlocalizedName().contains("exp") || stack.getItem().getUnlocalizedName().contains("shears") || stack.getItem().getUnlocalizedName().contains("anvil") || stack.getItem().getUnlocalizedName().contains("torch") || stack.getItem().getUnlocalizedName().contains("seeds") || stack.getItem().getUnlocalizedName().contains("leather") || stack.getItem().getUnlocalizedName().contains("reeds") || stack.getItem().getUnlocalizedName().contains("skull") || stack.getItem().getUnlocalizedName().contains("record") || stack.getItem().getUnlocalizedName().contains("snowball") || stack.getItem() instanceof net.minecraft.item.ItemGlassBottle || stack.getItem().getUnlocalizedName().contains("piston"));
      } 
      return true;
    } 
    return false;
  }
  
  public ArrayList<Integer> getWhitelistedItem() {
    return this.whitelistedItems;
  }
  
  private int getBlockCount() {
    int blockCount = 0;
    for (int i = 0; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        Item item = is.getItem();
        if (is.getItem() instanceof net.minecraft.item.ItemBlock)
          blockCount += is.stackSize; 
      } 
    } 
    return blockCount;
  }
  
  private void getBestPickaxe(int slot) {
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (isBestPickaxe(is) && 37 != i && !isBestWeapon(is))
          if (!mc.thePlayer.inventoryContainer.getSlot(37).getHasStack()) {
            swap(i, 1);
            if (this.delay.getValue().longValue() > 0L)
              return; 
          } else if (!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(37).getStack())) {
            swap(i, 1);
            if (this.delay.getValue().longValue() > 0L)
              return; 
          }  
      } 
    } 
  }
  
  private void getBestShovel(int slot) {
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (isBestShovel(is) && 39 != i && !isBestWeapon(is))
          if (!mc.thePlayer.inventoryContainer.getSlot(39).getHasStack()) {
            swap(i, 3);
            this.timeHelper.reset();
            if (this.delay.getValue().longValue() > 0L)
              return; 
          } else if (!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(39).getStack())) {
            swap(i, 3);
            this.timeHelper.reset();
            if (this.delay.getValue().longValue() > 0L)
              return; 
          }  
      } 
    } 
  }
  
  private void getBestAxe(int slot) {
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (isBestAxe(is) && 38 != i && !isBestWeapon(is))
          if (!mc.thePlayer.inventoryContainer.getSlot(38).getHasStack()) {
            swap(i, 2);
            this.timeHelper.reset();
            if (this.delay.getValue().longValue() > 0L)
              return; 
          } else if (!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(38).getStack())) {
            swap(i, 2);
            this.timeHelper.reset();
            if (this.delay.getValue().longValue() > 0L)
              return; 
          }  
      } 
    } 
  }
  
  private boolean isBestPickaxe(ItemStack stack) {
    Item item = stack.getItem();
    if (!(item instanceof net.minecraft.item.ItemPickaxe))
      return false; 
    float value = getToolEffect(stack);
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (getToolEffect(is) > value && is.getItem() instanceof net.minecraft.item.ItemPickaxe)
          return false; 
      } 
    } 
    return true;
  }
  
  private boolean isBestShovel(ItemStack stack) {
    Item item = stack.getItem();
    if (!(item instanceof net.minecraft.item.ItemSpade))
      return false; 
    float value = getToolEffect(stack);
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (getToolEffect(is) > value && is.getItem() instanceof net.minecraft.item.ItemSpade)
          return false; 
      } 
    } 
    return true;
  }
  
  private boolean isBestAxe(ItemStack stack) {
    Item item = stack.getItem();
    if (!(item instanceof net.minecraft.item.ItemAxe))
      return false; 
    float value = getToolEffect(stack);
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (getToolEffect(is) > value && is.getItem() instanceof net.minecraft.item.ItemAxe && !isBestWeapon(stack))
          return false; 
      } 
    } 
    return true;
  }
  
  private float getToolEffect(ItemStack stack) {
    Item item = stack.getItem();
    if (!(item instanceof ItemTool))
      return 0.0F; 
    String name = item.getUnlocalizedName();
    ItemTool tool = (ItemTool)item;
    float value = 1.0F;
    if (item instanceof net.minecraft.item.ItemPickaxe) {
      value = tool.getStrVsBlock(stack, Blocks.stone);
      if (name.toLowerCase().contains("gold"))
        value -= 5.0F; 
    } else if (item instanceof net.minecraft.item.ItemSpade) {
      value = tool.getStrVsBlock(stack, Blocks.dirt);
      if (name.toLowerCase().contains("gold"))
        value -= 5.0F; 
    } else {
      if (!(item instanceof net.minecraft.item.ItemAxe))
        return 1.0F; 
      value = tool.getStrVsBlock(stack, Blocks.log);
      if (name.toLowerCase().contains("gold"))
        value -= 5.0F; 
    } 
    value = (float)(value + EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D);
    value = (float)(value + EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0D);
    return value;
  }
  
  private boolean isBadPotion(ItemStack stack) {
    if (stack != null && stack.getItem() instanceof ItemPotion) {
      ItemPotion potion = (ItemPotion)stack.getItem();
      if (potion.getEffects(stack) == null)
        return true; 
      Iterator var3 = potion.getEffects(stack).iterator();
      while (var3.hasNext()) {
        Object o = var3.next();
        PotionEffect effect = (PotionEffect)o;
        if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId())
          return true; 
      } 
    } 
    return false;
  }
  
  boolean invContainsType(int type) {
    for (int i = 9; i < 45; i++) {
      if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        Item item = is.getItem();
        if (item instanceof ItemArmor) {
          ItemArmor armor = (ItemArmor)item;
          if (type == armor.armorType)
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  public static boolean isBestArmor(ItemStack stack, int type) {
    float prot = getProtection(stack);
    String strType = "";
    if (type == 1) {
      strType = "helmet";
    } else if (type == 2) {
      strType = "chestplate";
    } else if (type == 3) {
      strType = "leggings";
    } else if (type == 4) {
      strType = "boots";
    } 
    if (!stack.getUnlocalizedName().contains(strType))
      return false; 
    for (int i = 5; i < 45; i++) {
      if ((Minecraft.getMinecraft()).thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = (Minecraft.getMinecraft()).thePlayer.inventoryContainer.getSlot(i).getStack();
        if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
          return false; 
      } 
    } 
    return true;
  }
  
  public static float getProtection(ItemStack stack) {
    float prot = 0.0F;
    if (stack.getItem() instanceof ItemArmor) {
      ItemArmor armor = (ItemArmor)stack.getItem();
      prot = (float)(prot + armor.damageReduceAmount + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
      prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0D);
      prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0D);
      prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
      prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
      prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0D);
    } 
    return prot;
  }
  
  public void getBestArmor() {
    for (int type = 1; type < 5; type++) {
      this;
      if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
        this;
        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
        if (isBestArmor(is, type))
          continue; 
        drop(4 + type);
      } 
      for (int i = 9; i < 45; i++) {
        this;
        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
          this;
          ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
          if (isBestArmor(is, type) && getProtection(is) > 0.0F) {
            shiftClick(i);
            if (this.delay.getValue().longValue() > 0L)
              return; 
          } 
        } 
      } 
      continue;
    } 
  }
  
  private void open() {
    if (!this.openInventory) {
      this.timeHelper.reset();
      if (!this.inventoryOnly.getValue().booleanValue())
        openInventory(); 
      this.openInventory = true;
    } 
  }
  
  public void close() {
    if (this.openInventory) {
      if (!this.inventoryOnly.getValue().booleanValue())
        closeInventory(); 
      this.openInventory = false;
    } 
  }
  
  public static void openInventory() {
    mc.thePlayer.sendQueue.sendPacketNoEvent((Packet)new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
  }
  
  public static void closeInventory() {
    mc.thePlayer.sendQueue.sendPacketNoEvent((Packet)new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
  }
}
