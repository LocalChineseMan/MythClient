package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.panel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.ModuleComponent;
import notthatuwu.xyz.mythrecode.api.utils.MouseUtil;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.render.StringUtil;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;
import org.lwjgl.input.Mouse;

public final class CategoryPanel extends DraggablePanel {
  ArrayList<Module> mods = new ArrayList<>();
  
  public BlurShader blurShader = new BlurShader();
  
  private double scroll;
  
  private double scrollSpeed;
  
  public CategoryPanel(Category category, int x, int y) {
    super((Component)null, StringUtil.upperSnakeCaseToPascal(category.name()), x, y, 120, 17);
    int moduleY = 17;
    for (Module m : Client.INSTANCE.moduleManager.getModulesByCategory(category))
      this.mods.add(m); 
    this.mods.sort(Comparator.<Module>comparingInt(m -> FontLoaders.Sfui20.getStringWidth(((Module)m).getName())).reversed());
    for (Module m : this.mods) {
      this.children.add(new ModuleComponent((Component)this, m, 1, moduleY, 118, 15));
      moduleY += 15;
      getChildren().sort(Comparator.comparingDouble(Component::getX));
    } 
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    super.renderComponent(scaledResolution, mouseX, mouseY);
    int x = getX();
    int y = getY();
    int width = getWidth();
    int height = getHeight();
    setExpanded(true);
    int trueHeight = Math.min(getExpandedHeight(), 300);
    if (isExpanded()) {
      if (Mouse.hasWheel() && MouseUtil.isHovered(mouseX, mouseY, x, y, width, trueHeight)) {
        float wheel = Mouse.getDWheel();
        if (wheel < 0.0F) {
          if (this.scrollSpeed < 0.0D)
            this.scrollSpeed = 0.0D; 
          this.scrollSpeed += (5.0F * RenderUtils.getDeltaTime());
        } else if (wheel > 0.0F) {
          if (this.scrollSpeed > 0.0D)
            this.scrollSpeed = 0.0D; 
          this.scrollSpeed -= (5.0F * RenderUtils.getDeltaTime());
        } 
        this.scrollSpeed -= this.scrollSpeed / 10.0D * RenderUtils.getDeltaTime();
        this.scroll += this.scrollSpeed;
        if (getExpandedHeight() < trueHeight) {
          this.scrollSpeed = 0.0D;
          this.scroll = 0.0D;
        } 
      } 
      if (this.scroll < 0.0D)
        this.scroll = 0.0D; 
      if (getExpandedHeight() >= trueHeight && 
        this.scroll > (getExpandedHeight() - trueHeight))
        this.scroll = (getExpandedHeight() - trueHeight); 
    } 
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    Gui.drawRect(x, y, (x + width), (y + height), getColor());
    FontLoaders.Sfui20.drawString(getName(), x + width / 2.0F - FontLoaders.Sfui20.getStringWidth(getName()) / 2.0F - 1.0F, y + 8.5F - 4.0F, -1);
    if (isExpanded()) {
      int moduleY = height;
      for (Component child : this.children) {
        child.setY(moduleY);
        child.renderComponent(scaledResolution, mouseX, mouseY);
        int cHeight = child.getHeight();
        if (child instanceof ExpandableComponent) {
          ExpandableComponent expandableComponent = (ExpandableComponent)child;
          cHeight = expandableComponent.getExpandedHeight();
        } 
        moduleY += cHeight;
      } 
    } 
  }
  
  public boolean canExpand() {
    return !this.mods.isEmpty();
  }
  
  public int getExpandedHeight() {
    int height = getHeight();
    if (isExpanded())
      for (Component child : this.children) {
        int cHeight = child.getHeight();
        if (child instanceof ExpandableComponent) {
          ExpandableComponent expandableComponent = (ExpandableComponent)child;
          cHeight = expandableComponent.getExpandedHeight();
        } 
        height += cHeight;
      }  
    return height;
  }
  
  public int getColor() {
    ClickGuiMod clickGui = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    return clickGui.paneladdons.isEnabled("Transparent") ? (new Color(16, 17, 17, 50)).getRGB() : (new Color(21, 21, 21)).getRGB();
  }
}
