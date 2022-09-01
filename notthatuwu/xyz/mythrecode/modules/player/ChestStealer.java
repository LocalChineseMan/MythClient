package notthatuwu.xyz.mythrecode.modules.player;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ItemUtils;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.display.HUD;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@Info(name = "ChestStealer", category = Category.PLAYER)
public class ChestStealer extends Module {
  public NumberSetting delaySet = new NumberSetting("Delay", this, 100.0D, 0.0D, 1000.0D, true);
  
  public BooleanSetting silent = new BooleanSetting("Silent", this, false);
  
  public BooleanSetting badItems = new BooleanSetting("Bad Items", this, true);
  
  public BooleanSetting autoClose = new BooleanSetting("Auto Close", this, true);
  
  private final TimeHelper timeHelper = new TimeHelper();
  
  private double delay;
  
  public static boolean hideGui;
  
  public static boolean hideGuiMouse;
  
  @EventTarget
  private void onUpdate(EventUpdate event) {
    if (event.isPre()) {
      setSuffix(Integer.valueOf(this.delaySet.getValueInt()));
      if (isChestEmpty())
        setDelay(); 
      if (this.silent.getValue().booleanValue() && mc.currentScreen instanceof GuiChest) {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(true);
      } 
      if (mc.currentScreen instanceof GuiChest) {
        hideGui = this.silent.getValue().booleanValue();
        if (hideGui && ModuleManager.getModuleByName("InvMove").isEnabled())
          hideGuiMouse = true; 
        GuiChest chest = (GuiChest)mc.currentScreen;
        boolean close = this.autoClose.getValue().booleanValue();
        if (isValidChest(chest)) {
          if ((isChestEmpty() || ItemUtils.isInventoryFull()) && close && this.timeHelper.hasReached((long)this.delay)) {
            (Minecraft.getMinecraft()).thePlayer.closeScreen();
            this.timeHelper.reset();
            return;
          } 
          if (this.timeHelper.hasReached((long)this.delay)) {
            for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
              ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
              if (stack != null && this.timeHelper.hasReached((long)this.delay) && (!ItemUtils.isBad(stack) || !this.badItems.getValue().booleanValue())) {
                mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, (EntityPlayer)mc.thePlayer);
                setDelay();
                this.timeHelper.reset();
              } 
            } 
            this.timeHelper.reset();
          } 
        } 
      } else {
        hideGui = false;
        hideGuiMouse = false;
      } 
    } 
  }
  
  @EventTarget
  public void onMove(EventMove event) {}
  
  @EventTarget
  public void onRender2D(Event2D event) {
    if (mc.currentScreen instanceof GuiChest && this.silent.getValue().booleanValue())
      FontLoaders.Sfui20.drawCenteredString("Silent Stealing... press Escape to close the chest", (HUD.width() / 2), ((HUD.height() / 2 + FontLoaders.Sfui20.getStringHeight("Silent Stealing... press Escape to close the chest") / 2) + 3.0F), -1); 
  }
  
  @EventTarget
  public void onReceivePacket(EventReceivePacket event) {}
  
  public void onDisable() {
    hideGui = false;
    super.onDisable();
  }
  
  private boolean isChestEmpty() {
    if (mc.currentScreen instanceof GuiChest) {
      GuiChest chest = (GuiChest)mc.currentScreen;
      for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
        ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
        if (stack != null && (!ItemUtils.isBad(stack) || !this.badItems.getValue().booleanValue()))
          return false; 
      } 
    } 
    return true;
  }
  
  private void setDelay() {
    if (this.delaySet.getValue().doubleValue() <= 40.0D) {
      this.delay = this.delaySet.getValue().doubleValue();
    } else {
      this.delay = this.delaySet.getValue().doubleValue() + ThreadLocalRandom.current().nextDouble(-40.0D, 40.0D);
    } 
  }
  
  private boolean isValidChest(GuiChest chest) {
    int radius = 5;
    for (int x = -radius; x < radius; x++) {
      for (int y = radius; y > -radius; y--) {
        for (int z = -radius; z < radius; z++) {
          double xPos = mc.thePlayer.posX + x;
          double yPos = mc.thePlayer.posY + y;
          double zPos = mc.thePlayer.posZ + z;
          BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
          Block block = mc.theWorld.getBlockState(blockPos).getBlock();
          if (block instanceof net.minecraft.block.BlockChest)
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  private boolean isValidItem(ItemStack itemStack) {
    return (itemStack.getItem() instanceof net.minecraft.item.ItemArmor || itemStack.getItem() instanceof net.minecraft.item.ItemSword || itemStack
      .getItem() instanceof net.minecraft.item.ItemTool || itemStack.getItem() instanceof net.minecraft.item.ItemFood || itemStack
      .getItem() instanceof net.minecraft.item.ItemPotion || itemStack.getItem() instanceof net.minecraft.item.ItemBlock);
  }
  
  private boolean isChestEmpty(GuiChest chest) {
    for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
      ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
      if (stack != null && 
        isValidItem(stack))
        return false; 
    } 
    return true;
  }
  
  private boolean isInventoryFull() {
    for (int index = 9; index <= 44; index++) {
      ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
      if (stack == null)
        return false; 
    } 
    return true;
  }
}
