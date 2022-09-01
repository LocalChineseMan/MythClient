package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings.AddonComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings.BooleanComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings.ColorComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings.ModeComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings.NumberComponent;
import notthatuwu.xyz.mythrecode.api.utils.animation.Animation;
import notthatuwu.xyz.mythrecode.api.utils.animation.Easings;
import notthatuwu.xyz.mythrecode.api.utils.font.CFontRenderer;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.shader.impl.StencilUtil;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;

public final class ModuleComponent extends ExpandableComponent {
  private final Module module;
  
  private final Animation animation = new Animation();
  
  public ModuleComponent(Component parent, Module module, int x, int y, int width, int height) {
    super(parent, module.getName(), x, y, width, height);
    this.module = module;
    List<Setting> properties = Client.INSTANCE.settingsManager.getSettingsFromModule(module);
    int propertyX = 1;
    int propertyY = height;
    for (Setting property : properties) {
      AddonComponent addonComponent;
      Component component = null;
      if (property instanceof BooleanSetting)
        BooleanComponent booleanComponent = new BooleanComponent((Component)this, (BooleanSetting)property, 1, propertyY, width - 2, 15); 
      if (property instanceof ColorSetting) {
        ColorComponent colorComponent = new ColorComponent((Component)this, (ColorSetting)property, 1, propertyY, width - 2, 15);
      } else if (property instanceof NumberSetting) {
        NumberComponent numberComponent = new NumberComponent((Component)this, (NumberSetting)property, 1, propertyY, width - 2, 15);
      } else if (property instanceof ModeSetting) {
        ModeComponent modeComponent = new ModeComponent((Component)this, (ModeSetting)property, 1, propertyY, width - 2, 22);
      } else if (property instanceof AddonSetting) {
        addonComponent = new AddonComponent((Component)this, (AddonSetting)property, 1, propertyY, width - 2, 22);
      } 
      if (addonComponent != null) {
        this.children.add(addonComponent);
        propertyY += addonComponent.getHeight();
      } 
    } 
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    GlStateManager.resetColor();
    this.animation.updateAnimation();
    int x = getX();
    int y = getY();
    int width = getWidth();
    int height = getHeight();
    double fortnite = getExpandedHeight();
    Gui.drawRect(x, y, (x + width), (float)(y + fortnite), getColor());
    Gui.drawRect(x, y, (x + width), (float)(y + fortnite), getColor());
    Gui.drawRect(x, y, (x + width), (float)(y + fortnite), getColor());
    Gui.drawRect(x, y, (x + width), (float)(y + fortnite), getColor());
    Gui.drawRect(x, y, (x + width), (float)(y + fortnite), getColor());
    if (isExpanded() || this.animation.getValue() != 0.0D) {
      StencilUtil.initStencilToWrite();
      Gui.drawRect(x, (y + height), (x + width), (y + getExpandedHeight()), -1);
      StencilUtil.readStencilBuffer(1);
      int childY = 15;
      for (Component child : this.children) {
        int cHeight = child.getHeight();
        if (child instanceof SettingComponent) {
          SettingComponent propertyComponent = (SettingComponent)child;
          if (!propertyComponent.getProperty().isVisible())
            continue; 
        } 
        if (child instanceof ExpandableComponent) {
          ExpandableComponent expandableComponent = (ExpandableComponent)child;
          cHeight = expandableComponent.getExpandedHeight();
        } 
        child.setY(childY);
        child.renderComponent(scaledResolution, mouseX, mouseY);
        childY += cHeight;
      } 
      StencilUtil.uninitStencilBuffer();
    } 
    int moduleColor = 0;
    ClickGuiMod ClickGUI = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    moduleColor = this.module.isEnabled() ? ClickGUI.extra.getColor() : -1;
    Gui.drawRect(x, y, (x + width), (y + height), (new Color(27, 28, 29, 50)).getRGB());
    FontLoaders.Sfui20.drawString(getName(), (x + 2) + calculateMiddle(getName(), FontLoaders.Sfui20, 54.0D, 1.0D), y + height / 2.0F - 4.0F, moduleColor);
  }
  
  public boolean canExpand() {
    return !this.children.isEmpty();
  }
  
  public void onClick(int mouseX, int mouseY, int button) {
    switch (button) {
      case 0:
        this.module.toggle();
        break;
      case 1:
        this.animation.animate(isExpanded() ? 0.0D : 1.0D, (20 * this.children.size()), Easings.NONE);
        break;
    } 
  }
  
  public void onKeyPress(int keyCode) {}
  
  public int getExpandedHeight() {
    return (int)(getHeightus() * this.animation.getValue()) + getHeight();
  }
  
  public int getHeightus() {
    int height = 0;
    for (Component child : this.children) {
      int heightus = child.getHeight();
      if (child instanceof SettingComponent) {
        SettingComponent propertyComponent = (SettingComponent)child;
        if (!propertyComponent.getProperty().isVisible())
          continue; 
      } 
      if (child instanceof ExpandableComponent) {
        ExpandableComponent expandableComponent = (ExpandableComponent)child;
        heightus = expandableComponent.getExpandedHeight();
      } 
      height += heightus;
    } 
    return height;
  }
  
  public int getColor() {
    ClickGuiMod clickGui = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    return clickGui.paneladdons.isEnabled("Transparent") ? (new Color(27, 28, 29, 50)).getRGB() : (new Color(25, 25, 25)).getRGB();
  }
  
  public int calculateMiddle(String text, CFontRenderer fontRenderer, double x, double width) {
    return (int)((float)(x + width) - fontRenderer.getStringWidth(text) / 2.0F - (float)width / 2.0F);
  }
}
