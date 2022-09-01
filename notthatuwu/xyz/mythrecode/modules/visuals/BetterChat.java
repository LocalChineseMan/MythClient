package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;

@Info(name = "BetterChat", category = Category.VISUAL)
public class BetterChat extends Module {
  public BooleanSetting blur = new BooleanSetting("Blur", this, false);
  
  public BooleanSetting dropshadow = new BooleanSetting("Shadow", this, false);
  
  public BooleanSetting customFont = new BooleanSetting("Custom Font", this, true);
}
