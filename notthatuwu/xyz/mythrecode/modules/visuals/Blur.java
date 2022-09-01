package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;

@Info(name = "Blur", category = Category.VISUAL)
public class Blur extends Module {
  public AddonSetting modules = new AddonSetting("Modules", this, "TargetHUD", new String[] { "TargetHUD", "PlayerList", "Scoreboard", "Watermark", "Notifications", "Radar", "Arraylist", "SessionInfo", "Keystrokes" });
  
  public NumberSetting sigma = new NumberSetting("Blur Sigma", this, 7.0D, 1.0D, 30.0D, true);
  
  public NumberSetting radius = new NumberSetting("Blur Radius", this, 7.0D, 1.0D, 30.0D, true);
  
  public BooleanSetting shadow = new BooleanSetting("Drop Shadow", this, false);
  
  public NumberSetting dropshadowRadius = new NumberSetting("Shadow Radius", this, 25.0D, 1.0D, 30.0D, true, () -> this.shadow.getValue());
}
