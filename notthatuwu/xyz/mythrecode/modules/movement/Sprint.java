package notthatuwu.xyz.mythrecode.modules.movement;

import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.events.EventMove;

@Info(name = "Sprint", category = Category.MOVEMENT)
public class Sprint extends Module {
  public BooleanSetting omniSprint = new BooleanSetting("Omni Sprint", this, false);
  
  @EventTarget
  public void onMotion(EventMove event) {
    if (this.omniSprint.getValue().booleanValue()) {
      getPlayer().setSprinting(getPlayer().isMoving());
    } else if (getPlayer().isUsingItem()) {
      if ((getPlayer()).moveForward > 0.0F && (((NoSlow)Client.INSTANCE.moduleManager
        .getModuleByClass(NoSlow.class)).isEnabled() || 
        !getPlayer().isUsingItem()) && 
        !getPlayer().isSneaking() && 
        !(getPlayer()).isCollidedHorizontally && 
        getPlayer().getFoodStats().getFoodLevel() > 6)
        getPlayer().setSprinting(true); 
    } else {
      (getGameSettings()).keyBindSprint.pressed = true;
    } 
  }
  
  public void onDisable() {
    getPlayer().setSprinting(false);
    super.onDisable();
  }
}
