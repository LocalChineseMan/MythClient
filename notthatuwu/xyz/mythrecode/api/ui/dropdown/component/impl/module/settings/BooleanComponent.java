package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.settings;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.module.SettingComponent;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.animation.Animation;
import notthatuwu.xyz.mythrecode.api.utils.animation.Easings;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.gl.OGLUtils;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;

public final class BooleanComponent extends Component implements SettingComponent {
  private final BooleanSetting booleanProperty;
  
  private int buttonLeft;
  
  private int buttonTop;
  
  private int buttonRight;
  
  private int buttonBottom;
  
  public TimeHelper timeHelper = new TimeHelper();
  
  public int x;
  
  public final Animation animation = new Animation();
  
  public BooleanComponent(Component parent, BooleanSetting booleanProperty, int x, int y, int width, int height) {
    super(parent, booleanProperty.name, x, y, width, height);
    this.booleanProperty = booleanProperty;
    if (booleanProperty.getValue().booleanValue())
      this.animation.setValue(1.0D); 
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    int x = getX();
    int y = getY();
    int middleHeight = getHeight() / 2;
    int btnRight = x + 3 + middleHeight;
    float maxWidth = (FontLoaders.Sfui20.getStringWidth(getName()) + middleHeight) + 6.0F;
    boolean hovered = isHovered(mouseX, mouseY);
    boolean tooWide = (maxWidth > getWidth());
    boolean needScissorBox = (tooWide && !hovered);
    this.animation.updateAnimation();
    if (needScissorBox) {
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      OGLUtils.startScissorBox(sr, x, y, getWidth(), getHeight());
    } 
    FontLoaders.Sfui20.drawString(getName(), (btnRight - 9 + 5), (y + middleHeight - 3), -1);
    if (needScissorBox)
      OGLUtils.endScissorBox(); 
    ClickGuiMod ClickGUI = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    int buttonLeft = x + 2;
    this.buttonLeft = buttonLeft;
    float n = buttonLeft;
    int buttonTop = y + middleHeight - middleHeight / 2;
    this.buttonTop = buttonTop;
    float n2 = buttonTop;
    int buttonRight = btnRight;
    this.buttonRight = buttonRight;
    float n3 = buttonRight;
    int buttonBottom = y + middleHeight + middleHeight / 2 + 2;
    this.buttonBottom = buttonBottom;
    Gui.drawRect((n + 111.0F), n2, (n3 + 66.0F) + 18.5D, buttonBottom, this.booleanProperty.getValue().booleanValue() ? ClickGUI.extra.getColor() : (new Color(12, 12, 12)).getRGB());
    Gui.drawRect(n + 99.5D + 10.5D * this.animation.getValue(), n2 - 2.5D, n3 + 85.2D + 9.5D * this.animation.getValue(), (buttonBottom + 3.0F), this.booleanProperty.getValue().booleanValue() ? ClickGUI.primary.getColor() : (new Color(23, 23, 23)).getRGB());
  }
  
  public void onMouseClick(int mouseX, int mouseY, int button) {
    if (button == 0 && mouseX > this.buttonLeft - 2 && mouseY > this.buttonTop && mouseX < this.buttonRight + 120 && mouseY < this.buttonBottom) {
      this.animation.animate(this.booleanProperty.getValue().booleanValue() ? 0.0D : 1.0D, 400.0D, Easings.EXPO_OUT);
      this.booleanProperty.setValue(Boolean.valueOf(!this.booleanProperty.getValue().booleanValue()));
    } 
  }
  
  public BooleanSetting getProperty() {
    return this.booleanProperty;
  }
}
