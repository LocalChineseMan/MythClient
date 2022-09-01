package notthatuwu.xyz.mythrecode.modules.display;

import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.ClickGui;

@Info(name = "ClickGui", category = Category.DISPLAY)
public class ClickGuiMod extends Module {
  public AddonSetting addons = new AddonSetting("Background Settings", this, "Blur", new String[] { "Blur", "Gardient", "Anime" });
  
  public AddonSetting paneladdons = new AddonSetting("Panel Settings", this, "Transparent", new String[] { "Transparent", "Test" });
  
  public ModeSetting anime = new ModeSetting("Anime", this, new String[] { "Asna", "SchoolGirl", "Rem", "Rem2", "Kirigaya", "Miku", "Astolfo", "Misaka", "Shiina Mashiro", "Akeno" }, "Rem", () -> Boolean.valueOf(this.addons.isEnabled("Anime")));
  
  public ColorSetting primary = new ColorSetting("Primary", this, new Color(180, 10, 120));
  
  public ColorSetting extra = new ColorSetting("Extra", this, new Color(180, 10, 120));
  
  public ColorSetting gardient = new ColorSetting("Gardient Color", this, new Color(10, 12, 116), () -> Boolean.valueOf(this.addons.isEnabled("Gardient")));
  
  public NumberSetting blurradius = new NumberSetting("Blur Radius", this, 5.0D, 1.0D, 25.0D, false, () -> Boolean.valueOf(this.addons.isEnabled("Blur")));
  
  private ClickGui dropdown;
  
  public void onEnable() {
    if (this.dropdown == null)
      this.dropdown = new ClickGui(); 
    MC.displayGuiScreen((GuiScreen)this.dropdown);
    setEnabled(false);
    super.onEnable();
  }
}
