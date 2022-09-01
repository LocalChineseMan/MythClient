package notthatuwu.xyz.mythrecode.modules.movement;

import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.combat.KillAura;

@Info(name = "DamageBoost", category = Category.MOVEMENT)
public class DamageBoost extends Module {
  public NumberSetting speed = new NumberSetting("Speed", this, 0.1D, 0.1D, 1.0D, false);
  
  public NumberSetting airSpeed = new NumberSetting("Air Speed", this, 0.02D, 0.1D, 1.0D, false);
  
  public NumberSetting groundSpeed = new NumberSetting("Ground Speed", this, 0.2D, 0.1D, 1.0D, false);
  
  public NumberSetting timer = new NumberSetting("Timer", this, 1.0D, 0.1D, 3.0D, false);
  
  public NumberSetting hurtTime = new NumberSetting("Hurt Time", this, 1.0D, 1.0D, 9.0D, true);
  
  public BooleanSetting onlyTarget = new BooleanSetting("Only Fighting", this, true);
  
  public BooleanSetting onlySpeed = new BooleanSetting("Ignore Speed", this, true);
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (!this.onlySpeed.getValue().booleanValue() && ((Speed)Client.INSTANCE.moduleManager.getModuleByClass(Speed.class)).isEnabled())
      return; 
    if ((getPlayer()).hurtTime == this.hurtTime.getValueInt()) {
      if (this.onlyTarget.getValue().booleanValue() && KillAura.target == null)
        return; 
      setSpeed(this.speed.getValue().doubleValue());
      (getPlayer()).speedInAir = this.airSpeed.getValueInt();
      (getPlayer()).speedOnGround = this.groundSpeed.getValueInt();
      setTimer(this.timer.getValueFloat());
    } else {
      (getPlayer()).speedInAir = 0.02F;
      (getPlayer()).speedOnGround = 0.1F;
      setTimer(1.0F);
    } 
  }
  
  public void onEnable() {
    (getPlayer()).speedInAir = 0.02F;
    (getPlayer()).speedOnGround = 0.1F;
    setTimer(1.0F);
    super.onEnable();
  }
  
  public void onDisable() {
    super.onDisable();
    setTimer(1.0F);
  }
}
