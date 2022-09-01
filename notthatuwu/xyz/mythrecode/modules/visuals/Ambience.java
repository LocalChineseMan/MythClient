package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;

@Info(name = "Ambience", category = Category.VISUAL)
public class Ambience extends Module {
  public NumberSetting worldTime = new NumberSetting("World Time", this, 13000.0D, 0.0D, 23000.0D, true);
  
  public int getWorldTime() {
    return this.worldTime.getValueInt();
  }
}
