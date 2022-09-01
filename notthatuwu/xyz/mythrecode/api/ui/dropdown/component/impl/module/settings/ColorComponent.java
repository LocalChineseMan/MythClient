package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.SettingComponent;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.gl.OGLUtils;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ColorComponent extends ExpandableComponent implements SettingComponent {
  public ColorComponent(Component parent, ColorSetting colorProperty, int x, int y, int width, int height) {
    super(parent, colorProperty.name, x, y, width, height);
    this.colorProperty = colorProperty;
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    super.renderComponent(scaledResolution, mouseX, mouseY);
    int x = getX();
    int y = getY();
    int width = getWidth();
    int height = getHeight();
    int textColor = -1;
    int bgColor = getSecondaryBackgroundColor(isHovered(mouseX, mouseY));
    boolean hovered = isHovered(mouseX, mouseY);
    FontLoaders.Sfui20.drawString(getName(), (float)(x + 6.5D), y + height / 2.0F - 3.0F - 1.0F, -1);
    float left = (x + width - 13);
    float top = y + height / 2.0F - 2.0F;
    float right = (x + width - 2);
    float bottom = y + height / 2.0F + 2.0F;
    RenderUtils.drawRoundedRect2((left - 9.0F), (top - 1.0F), (right - 2.0F), (bottom + 3.0F), 5.0D, this.colorProperty.getValue().getRGB());
    if (isExpanded()) {
      float cpLeft = (x + 2);
      float cpTop = (y + height + 2);
      float cpRight = (x + 80 - 2);
      float cpBottom = (y + height + 80 - 2);
      if (mouseX <= cpLeft || mouseY <= cpTop || mouseX >= cpRight || mouseY >= cpBottom)
        this.colorSelectorDragging = false; 
      float colorSelectorX = this.saturation * (cpRight - cpLeft);
      float colorSelectorY = (1.0F - this.brightness) * (cpBottom - cpTop);
      if (this.colorSelectorDragging) {
        float wWidth = cpRight - cpLeft;
        float xDif = mouseX - cpLeft;
        this.saturation = xDif / wWidth;
        colorSelectorX = xDif;
        float hHeight = cpBottom - cpTop;
        float yDif = mouseY - cpTop;
        this.brightness = 1.0F - yDif / hHeight;
        colorSelectorY = yDif;
        updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
      } 
      drawColorPickerRect(cpLeft + 0.5F, cpTop + 0.5F, cpRight - 0.5F, cpBottom - 0.5F);
      float selectorWidth = 2.0F;
      float outlineWidth = 0.5F;
      float half = 1.0F;
      float csLeft = cpLeft + colorSelectorX - 1.0F;
      float csTop = cpTop + colorSelectorY - 1.0F;
      float csRight = cpLeft + colorSelectorX + 1.0F;
      float csBottom = cpTop + colorSelectorY + 1.0F;
      Gui.drawRect((csLeft - 0.5F), (csTop - 0.5F), (csRight + 0.5F), (csBottom + 0.5F), -16777216);
      Gui.drawRect(csLeft, csTop, csRight, csBottom, Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
      float sLeft = (x + 80 - 1);
      float sTop = (y + height + 2);
      float sRight = sLeft + 5.0F;
      float sBottom = (y + height + 80 - 2);
      if (mouseX <= sLeft || mouseY <= sTop || mouseX >= sRight || mouseY >= sBottom)
        this.hueSelectorDragging = false; 
      float hueSelectorY = this.hue * (sBottom - sTop);
      if (this.hueSelectorDragging) {
        float hsHeight = sBottom - sTop;
        float yDif2 = mouseY - sTop;
        this.hue = yDif2 / hsHeight;
        hueSelectorY = yDif2;
        updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false);
      } 
      float inc = 0.2F;
      float times = 5.0F;
      float sHeight = sBottom - sTop;
      float sY = sTop + 0.5F;
      float size = sHeight / 5.0F;
      for (int i = 0; i < 5.0F; i++) {
        boolean last = (i == 4.0F);
        if (last)
          size--; 
        Gui.drawGradientRect((int)(sLeft + 0.5F), (int)sY, (int)(sRight - 0.5F), (int)(sY + size), Color.HSBtoRGB(0.2F * i, 1.0F, 1.0F), Color.HSBtoRGB(0.2F * (i + 1), 1.0F, 1.0F));
        if (!last)
          sY += size; 
      } 
      float selectorHeight = 2.0F;
      float outlineWidth2 = 0.5F;
      float half2 = 1.0F;
      float csTop2 = sTop + hueSelectorY - 1.0F;
      float csBottom2 = sTop + hueSelectorY + 1.0F;
      Gui.drawRect((sLeft - 0.5F), (csTop2 - 0.5F), (sRight + 0.5F), (csBottom2 + 0.5F), -16777216);
      Gui.drawRect(sLeft, csTop2, sRight, csBottom2, Color.HSBtoRGB(this.hue, 1.0F, 1.0F));
      sLeft = (x + 80 + 6);
      sTop = (y + height + 2);
      sRight = sLeft + 5.0F;
      sBottom = (y + height + 80 - 2);
      if (mouseX <= sLeft || mouseY <= sTop || mouseX >= sRight || mouseY >= sBottom)
        this.alphaSelectorDragging = false; 
      int color = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      float alphaSelectorY = this.alpha * (sBottom - sTop);
      if (this.alphaSelectorDragging) {
        float hsHeight2 = sBottom - sTop;
        float yDif3 = mouseY - sTop;
        this.alpha = yDif3 / hsHeight2;
        alphaSelectorY = yDif3;
        updateColor((new Color(r, g, b, (int)(this.alpha * 255.0F))).getRGB(), true);
      } 
      float selectorHeight2 = 2.0F;
      float outlineWidth3 = 0.5F;
      float half3 = 1.0F;
      float csTop3 = sTop + alphaSelectorY - 1.0F;
      float csBottom3 = sTop + alphaSelectorY + 1.0F;
      float bx = sRight + 0.5F;
      float ay = csTop3 - 0.5F;
      float by = csBottom3 + 0.5F;
      float xOff = 93.0F;
      float sLeft2 = x + 93.0F;
      float sTop2 = (y + height + 2);
      float sRight2 = sLeft2 + width - 93.0F - 3.0F;
      float f1 = (y + height) + 40.0F + 8.0F;
    } 
  }
  
  private void drawCheckeredBackground(float x, float y, float x2, float y2) {
    Gui.drawRect(x, y, x2, y2, -1);
    boolean off = false;
    while (y < y2) {
      float x3;
      for (x3 = x + ((off = !off) ? true : false); x3 < x2; x3 += 2.0F)
        Gui.drawRect(x3, y, (x3 + 1.0F), (y + 1.0F), -8355712); 
      y++;
    } 
  }
  
  private void updateColor(int hex, boolean hasAlpha) {
    if (hasAlpha) {
      this.colorProperty.setValue(hex);
    } else {
      this.colorProperty.setValue((new Color(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, (int)(this.alpha * 255.0F))).getRGB());
    } 
  }
  
  public void onMouseClick(int mouseX, int mouseY, int button) {
    super.onMouseClick(mouseX, mouseY, button);
    if (isExpanded() && button == 0) {
      int x = getX();
      int y = getY();
      float cpLeft = (x + 2);
      float cpTop = (y + getHeight() + 2);
      float cpRight = (x + 80 - 2);
      float cpBottom = (y + getHeight() + 80 - 2);
      float sLeft = (x + 80 - 1);
      float sTop = (y + getHeight() + 2);
      float sRight = sLeft + 5.0F;
      float sBottom = (y + getHeight() + 80 - 2);
      float asLeft = (x + 80 + 6);
      float asTop = (y + getHeight() + 2);
      float asRight = asLeft + 5.0F;
      float asBottom = (y + getHeight() + 80 - 2);
      this.colorSelectorDragging = (!this.colorSelectorDragging && mouseX > cpLeft && mouseY > cpTop && mouseX < cpRight && mouseY < cpBottom);
      this.hueSelectorDragging = (!this.hueSelectorDragging && mouseX > sLeft && mouseY > sTop && mouseX < sRight && mouseY < sBottom);
      this.alphaSelectorDragging = (!this.alphaSelectorDragging && mouseX > asLeft && mouseY > asTop && mouseX < asRight && mouseY < asBottom);
    } 
  }
  
  public void onMouseRelease(int button) {
    if (this.hueSelectorDragging) {
      this.hueSelectorDragging = false;
    } else if (this.colorSelectorDragging) {
      this.colorSelectorDragging = false;
    } else if (this.alphaSelectorDragging) {
      this.alphaSelectorDragging = false;
    } 
  }
  
  private float[] getHSBFromColor(int hex) {
    int r = hex >> 16 & 0xFF;
    int g = hex >> 8 & 0xFF;
    int b = hex & 0xFF;
    return Color.RGBtoHSB(r, g, b, null);
  }
  
  public void drawColorPickerRect(float left, float top, float right, float bottom) {
    int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0F, 1.0F);
    GL11.glDisable(3553);
    OGLUtils.startBlending();
    GL11.glShadeModel(7425);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(right, top, 0.0D).color(hueBasedColor).endVertex();
    worldrenderer.pos(left, top, 0.0D).color(-1).endVertex();
    worldrenderer.pos(left, bottom, 0.0D).color(-1).endVertex();
    worldrenderer.pos(right, bottom, 0.0D).color(hueBasedColor).endVertex();
    tessellator.draw();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(right, top, 0.0D).color(402653184).endVertex();
    worldrenderer.pos(left, top, 0.0D).color(402653184).endVertex();
    worldrenderer.pos(left, bottom, 0.0D).color(-16777216).endVertex();
    worldrenderer.pos(right, bottom, 0.0D).color(-16777216).endVertex();
    tessellator.draw();
    OGLUtils.endBlending();
    GL11.glShadeModel(7424);
    GL11.glEnable(3553);
  }
  
  public boolean canExpand() {
    return true;
  }
  
  public int getExpandedHeight() {
    return getHeight() + (isExpanded() ? 80 : 0);
  }
  
  public void onClick(int mouseX, int mouseY, int button) {}
  
  public ColorSetting getProperty() {
    return this.colorProperty;
  }
  
  public static Tessellator tessellator = Tessellator.getInstance();
  
  public static WorldRenderer worldrenderer = tessellator.getWorldRenderer();
  
  private final ColorSetting colorProperty;
  
  private float hue;
  
  private float saturation;
  
  private float brightness;
  
  private float alpha;
  
  private boolean colorSelectorDragging;
  
  private boolean hueSelectorDragging;
  
  private boolean alphaSelectorDragging;
}
