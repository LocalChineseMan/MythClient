package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;

@Info(name = "Scoreboard", category = Category.VISUAL)
public class Scoreboard extends Module {
  public BooleanSetting hide = new BooleanSetting("Hide Scoreboard", this, false);
  
  public BooleanSetting watermark = new BooleanSetting("Watermark", this, false);
}
