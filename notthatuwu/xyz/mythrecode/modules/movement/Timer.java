package notthatuwu.xyz.mythrecode.modules.movement;

import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "Timer", category = Category.MOVEMENT)
public class Timer extends Module {
  public NumberSetting speed = new NumberSetting("Speed", this, 1.0D, 0.1D, 10.0D, false);
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (event.isPre())
      setTimer(this.speed.getValueFloat()); 
  }
  
  public void onDisable() {
    super.onDisable();
    setTimer(1.0F);
  }
}
