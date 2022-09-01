package notthatuwu.xyz.mythrecode.modules.movement;

import net.minecraft.util.MovingObjectPosition;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import org.lwjgl.input.Mouse;

@Info(name = "ClickTP", description = "ClickTP", category = Category.MOVEMENT)
public class ClickTP extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Normal" }, "Normal");
  
  final TimeHelper timeHelper = new TimeHelper();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    switch (this.mode.getValue()) {
      case "Normal":
        if (Mouse.isButtonDown(1) && this.timeHelper.hasReached(500L)) {
          MovingObjectPosition rayTrace = getPlayer().rayTrace(35.0D, 1.0F);
          getPlayer().setPosition(rayTrace.hitVec.xCoord, rayTrace.hitVec.yCoord + 2.0D, rayTrace.hitVec.zCoord);
          toggle();
        } 
        break;
    } 
  }
  
  public void onEnable() {
    super.onEnable();
    this.timeHelper.reset();
  }
}
