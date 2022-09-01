package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings;

import java.awt.Color;
import java.math.BigDecimal;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.SettingComponent;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.gl.OGLUtils;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;

public final class NumberComponent extends Component implements SettingComponent {
  private final NumberSetting doubleProperty;
  
  private boolean sliding;
  
  public NumberComponent(Component parent, NumberSetting property, int x, int y, int width, int height) {
    super(parent, property.name, x, y, width, height);
    this.doubleProperty = property;
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    super.renderComponent(scaledResolution, mouseX, mouseY);
    int x = getX();
    int y = getY();
    int width = getWidth();
    int height = getHeight();
    double min = this.doubleProperty.getMin();
    double max = this.doubleProperty.getMax();
    Double dValue = this.doubleProperty.getValue();
    double value = dValue.doubleValue();
    double sliderBackground = (max - min) / (this.doubleProperty.getMax() - min);
    double sliderPercentage = (value - min) / (this.doubleProperty.getMax() - min);
    boolean hovered = isHovered(mouseX, mouseY);
    if (this.sliding)
      this.doubleProperty.setValue(MathHelper.clamp_double(roundToFirstDecimalPlace((mouseX - x) * (max - min) / width + min), min, max)); 
    String name = getName();
    int middleHeight = getHeight() / 2;
    String valueString = Double.toString(value);
    float valueWidth = FontLoaders.Sfui20.getStringWidth(valueString) + 2.0F;
    float overflowWidth = FontLoaders.Sfui20.getStringWidth(name) + 3.0F - width - valueWidth;
    boolean needOverflowBox = (overflowWidth > 0.0F);
    boolean showOverflowBox = (hovered && needOverflowBox);
    boolean needScissorBox = (needOverflowBox && !hovered);
    ClickGuiMod ClickGUI = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    Gui.drawRect(x, y + 14.0D, x + width * sliderBackground, (y + height) - 2.0D, (new Color(29, 29, 29)).getRGB());
    Gui.drawRect(x, y + 14.0D, x + width * sliderPercentage, (y + height) - 2.0D, ClickGUI.extra.getColor());
    if (needScissorBox)
      OGLUtils.startScissorBox(scaledResolution, x, y, (int)(width - valueWidth - 4.0F), height); 
    FontLoaders.Sfui20.drawString(name, (x + 2) - (showOverflowBox ? overflowWidth : 0.0F), (float)((y + middleHeight) - 4.5D), -1);
    if (needScissorBox)
      OGLUtils.endScissorBox(); 
    FontLoaders.Sfui20.drawString(valueString, (x + width) - valueWidth, (float)((y + middleHeight) - 4.5D), -1);
  }
  
  private double roundToFirstDecimalPlace(double value) {
    double inc = this.doubleProperty.getInc();
    double halfOfInc = inc / 2.0D;
    double floored = Math.floor(value / inc) * inc;
    if (value >= floored + halfOfInc)
      return (new BigDecimal(Math.ceil(value / inc) * inc)).setScale(2, 4).doubleValue(); 
    return (new BigDecimal(floored)).setScale(2, 4).doubleValue();
  }
  
  public void onMouseClick(int mouseX, int mouseY, int button) {
    if (!this.sliding && button == 0 && isHovered(mouseX, mouseY))
      this.sliding = true; 
  }
  
  public void onMouseRelease(int button) {
    this.sliding = false;
  }
  
  public NumberSetting getProperty() {
    return this.doubleProperty;
  }
}
