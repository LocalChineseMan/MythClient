package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.SettingComponent;
import notthatuwu.xyz.mythrecode.api.utils.animation.Animation;
import notthatuwu.xyz.mythrecode.api.utils.animation.Easings;
import notthatuwu.xyz.mythrecode.api.utils.font.CFontRenderer;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.StringUtil;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;

public final class AddonComponent extends ExpandableComponent implements SettingComponent {
  private final AddonSetting property;
  
  public final Animation animation = new Animation();
  
  public AddonComponent(Component parent, AddonSetting property, int x, int y, int width, int height) {
    super(parent, property.name, x, y, width, height);
    this.property = property;
  }
  
  public AddonSetting getProperty() {
    return this.property;
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    super.renderComponent(scaledResolution, mouseX, mouseY);
    this.animation.updateAnimation();
    int x = getX();
    int y = getY();
    int width = getWidth();
    int height = getHeight();
    int dropDownBoxY = y + 10;
    int textColor = 16777215;
    Gui.drawRect(x + 2.5D, (dropDownBoxY + 1), (x + getWidth()) - 2.5D, (dropDownBoxY + 12), (new Color(27, 28, 29, 50)).getRGB());
    Gui.drawRect((x + 4.5F) - 1.5D, (dropDownBoxY + 1.5F + 9.0F + 1.0F), (x + 4.5F) + 2.5D, (dropDownBoxY + 5 - 4), (new Color(55, 57, 61)).getRGB());
    Gui.drawRect((x + getWidth()) - 2.5D - 3.0D, (dropDownBoxY + 1.5F + 9.0F + 1.0F), (x + 4.5F + 5.0F + 105.0F), (dropDownBoxY + 5 - 4), (new Color(55, 57, 61)).getRGB());
    FontLoaders.Sfui20.drawString(getName(), (x + 1 + calculateMiddle(getName(), FontLoaders.Sfui20, 57.0D, 1.0D)), dropDownBoxY + 2.8F, -1);
    if (isExpanded() || this.animation.getValue() != 0.0D) {
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      Gui.drawRect((x + 10), (y + height), (x + width - 10), (y + getExpandedHeight()), (new Color(32, 33, 36, 50)).getRGB());
      handleRender(x, y + getHeight() + 2, width, 16777215);
    } 
  }
  
  public void onMouseClick(int mouseX, int mouseY, int button) {
    super.onMouseClick(mouseX, mouseY, button);
    if (button == 1)
      this.animation.animate(isExpanded() ? 1.0D : 0.0D, (20 * (getProperty()).addons.size()), Easings.NONE); 
    if (isExpanded())
      handleClick(mouseX, mouseY, getX(), getY() + getHeight() + 2, getWidth()); 
  }
  
  private <T extends Enum<T>> void handleRender(int x, int y, int width, int textColor) {
    AddonSetting property = this.property;
    for (String e : property.getAddons()) {
      GlStateManager.resetColor();
      ClickGuiMod ClickGUI = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
      if (property.toggled.contains(e)) {
        FontLoaders.Sfui20.drawString(StringUtil.upperSnakeCaseToPascal(e), (x + 1 + calculateMiddle(StringUtil.upperSnakeCaseToPascal(e), FontLoaders.Sfui20, 54.0D, 1.0D)), y, ClickGUI.extra.getColor());
      } else {
        FontLoaders.Sfui20.drawString(StringUtil.upperSnakeCaseToPascal(e), (x + 1 + calculateMiddle(StringUtil.upperSnakeCaseToPascal(e), FontLoaders.Sfui20, 54.0D, 1.0D)), y, -1);
      } 
      y += 12;
    } 
  }
  
  private <T extends Enum<T>> void handleClick(int mouseX, int mouseY, int x, int y, int width) {
    AddonSetting property = this.property;
    for (String e : property.getAddons()) {
      if (mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + 15 - 3)
        property.toggle(e); 
      y += 12;
    } 
  }
  
  public int calculateMiddle(String text, CFontRenderer fontRenderer, double x, double width) {
    return (int)((float)(x + width) - fontRenderer.getStringWidth(text) / 2.0F - (float)width / 2.0F);
  }
  
  public int getHeightus() {
    return this.property.getAddons().size() * 12;
  }
  
  public int getExpandedHeight() {
    return (int)(getHeight() + getHeightus() * this.animation.getValue());
  }
  
  public void onClick(int mouseX, int mouseY, int button) {}
  
  public boolean canExpand() {
    return (this.property.getAddons().size() > 1);
  }
}
