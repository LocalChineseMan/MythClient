package notthatuwu.xyz.mythrecode.modules.player;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import org.lwjgl.input.Mouse;

@Info(name = "AutoTool", category = Category.PLAYER)
public class AutoTool extends Module {
  public TimeHelper timeHelper = new TimeHelper();
  
  private int oldSlot = -1;
  
  private boolean wasBreaking = false;
  
  @EventTarget
  public void onUpdate(EventUpdate e) {
    if (mc.currentScreen == null && mc.thePlayer != null && mc.theWorld != null && mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null && mc.objectMouseOver.entityHit == null && Mouse.isButtonDown(0)) {
      float bestSpeed = 1.0F;
      int bestSlot = -1;
      Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
      for (int k = 0; k < 9; k++) {
        ItemStack item = mc.thePlayer.inventory.getStackInSlot(k);
        if (item != null) {
          float speed = item.getStrVsBlock(block);
          if (speed > bestSpeed) {
            bestSpeed = speed;
            bestSlot = k;
          } 
        } 
      } 
      if (bestSlot != -1 && mc.thePlayer.inventory.currentItem != bestSlot) {
        mc.thePlayer.inventory.currentItem = bestSlot;
        this.wasBreaking = true;
      } else if (bestSlot == -1) {
        if (this.wasBreaking) {
          mc.thePlayer.inventory.currentItem = this.oldSlot;
          this.wasBreaking = false;
        } 
        this.oldSlot = mc.thePlayer.inventory.currentItem;
      } 
    } else if (mc.thePlayer != null && mc.theWorld != null) {
      if (this.wasBreaking) {
        mc.thePlayer.inventory.currentItem = this.oldSlot;
        this.wasBreaking = false;
      } 
      this.oldSlot = mc.thePlayer.inventory.currentItem;
    } 
  }
}
