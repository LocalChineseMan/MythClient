package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "Animations", category = Category.VISUAL)
public class Animations extends Module {
  private static Animations animations;
  
  public ModeSetting animation = new ModeSetting("Mode", this, new String[] { "1.8", "1.7", "Punch", "Size", "Exhibiton" }, "1.7");
  
  public NumberSetting itemScale = new NumberSetting("Item Scale", this, 1.0D, 0.0D, 2.0D, false);
  
  public NumberSetting swingSpeed = new NumberSetting("Slowdown", this, 1.0D, 0.1D, 2.0D, false);
  
  public NumberSetting xItemPos = new NumberSetting("Item X", this, 0.0D, -1.0D, 1.0D, false);
  
  public NumberSetting yItemPos = new NumberSetting("Item Y", this, 0.0D, -1.0D, 1.0D, false);
  
  public NumberSetting zItemPos = new NumberSetting("Item Z", this, 0.0D, -1.0D, 1.0D, false);
  
  public Animations() {
    animations = this;
  }
  
  @EventTarget
  public void onUpdate(EventUpdate eventUpdate) {}
  
  public static Animations getInstance() {
    return animations;
  }
}
