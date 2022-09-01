package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.DropShadowUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;

@Info(name = "Keystrokes", category = Category.VISUAL)
public class Keystrokes extends Module {
  public BlurShader blurShader = new BlurShader();
  
  public ScaledResolution sr = new ScaledResolution(mc);
  
  public NumberSetting radius = new NumberSetting("Radius", this, 3.0D, 1.0D, 20.0D, false);
  
  public NumberSetting x = new NumberSetting("X", this, 3.0D, 0.0D, (this.sr.getScaledWidth() * 2), true);
  
  public NumberSetting y = new NumberSetting("Y", this, 45.0D, 0.0D, (this.sr.getScaledHeight() * 2), true);
  
  int lastA = 0;
  
  int lastW = 0;
  
  int lastS = 0;
  
  int lastD = 0;
  
  int lastJump = 0;
  
  double lastX = 0.0D;
  
  double lastZ = 0.0D;
  
  @EventTarget
  public void onRender(Event2D event) {
    boolean A = mc.gameSettings.keyBindLeft.pressed;
    boolean W = mc.gameSettings.keyBindForward.pressed;
    boolean S = mc.gameSettings.keyBindBack.pressed;
    boolean D = mc.gameSettings.keyBindRight.pressed;
    boolean JUMP = mc.gameSettings.keyBindJump.pressed;
    Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
    int alphaA = A ? 255 : 0;
    int alphaW = W ? 255 : 0;
    int alphaS = S ? 255 : 0;
    int alphaD = D ? 255 : 0;
    int alphaJump = JUMP ? 255 : 0;
    if (this.lastJump != alphaJump) {
      float diff = (alphaJump - this.lastJump);
      this.lastJump = (int)(this.lastJump + diff / 15.0F);
    } 
    if (this.lastA != alphaA) {
      float diff = (alphaA - this.lastA);
      this.lastA = (int)(this.lastA + diff / 15.0F);
    } 
    if (this.lastW != alphaW) {
      float diff = (alphaW - this.lastW);
      this.lastW = (int)(this.lastW + diff / 15.0F);
    } 
    if (this.lastS != alphaS) {
      float diff = (alphaS - this.lastS);
      this.lastS = (int)(this.lastS + diff / 15.0F);
    } 
    if (this.lastD != alphaD) {
      float diff = (alphaD - this.lastD);
      this.lastD = (int)(this.lastD + diff / 15.0F);
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("Keystrokes") && blur.shadow.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2((5 + this.x.getValueInt()), (49 + this.y.getValueInt()), (25 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastA, this.lastA, this.lastA, 150)).getRGB());
      RenderUtils.drawRoundedRect2((27 + this.x.getValueInt()), (27 + this.y.getValueInt()), (47 + this.x.getValueInt()), (47 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastW, this.lastW, this.lastW, 150)).getRGB());
      RenderUtils.drawRoundedRect2((27 + this.x.getValueInt()), (49 + this.y.getValueInt()), (47 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastS, this.lastS, this.lastS, 150)).getRGB());
      RenderUtils.drawRoundedRect2((49 + this.x.getValueInt()), (49 + this.y.getValueInt()), (69 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastD, this.lastD, this.lastD, 150)).getRGB());
      RenderUtils.drawRoundedRect2((5 + this.x.getValueInt()), (75 + this.y.getValueInt()), (70 + this.x.getValueInt()), (87 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastJump, this.lastJump, this.lastJump, 150)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("Keystrokes"))
      this.blurShader.startBlur(); 
    RenderUtils.drawRoundedRect2((5 + this.x.getValueInt()), (49 + this.y.getValueInt()), (25 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastA, this.lastA, this.lastA, 150)).getRGB());
    RenderUtils.drawRoundedRect2((27 + this.x.getValueInt()), (27 + this.y.getValueInt()), (47 + this.x.getValueInt()), (47 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastW, this.lastW, this.lastW, 150)).getRGB());
    RenderUtils.drawRoundedRect2((27 + this.x.getValueInt()), (49 + this.y.getValueInt()), (47 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastS, this.lastS, this.lastS, 150)).getRGB());
    RenderUtils.drawRoundedRect2((49 + this.x.getValueInt()), (49 + this.y.getValueInt()), (69 + this.x.getValueInt()), (69 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastD, this.lastD, this.lastD, 150)).getRGB());
    RenderUtils.drawRoundedRect2((5 + this.x.getValueInt()), (75 + this.y.getValueInt()), (70 + this.x.getValueInt()), (87 + this.y.getValueInt()), this.radius.getValue().doubleValue(), (new Color(this.lastJump, this.lastJump, this.lastJump, 150)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("Keystrokes"))
      this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    FontLoaders.Sfui20.drawCenteredString("D", 58.5D + this.x.getValueInt(), (55 + this.y.getValueInt()), (new Color(flop(this.lastD, 255), flop(this.lastD, 255), flop(this.lastD, 255), 255)).getRGB());
    FontLoaders.Sfui20.drawCenteredString("A", (15 + this.x.getValueInt()), (55 + this.y.getValueInt()), (new Color(flop(this.lastA, 255), flop(this.lastA, 255), flop(this.lastA, 255), 255)).getRGB());
    FontLoaders.Sfui20.drawCenteredString("W", 36.5D + this.x.getValueInt(), (33 + this.y.getValueInt()), (new Color(flop(this.lastW, 255), flop(this.lastW, 255), flop(this.lastW, 255), 255)).getRGB());
    FontLoaders.Sfui20.drawCenteredString("S", 36.5D + this.x.getValueInt(), (55 + this.y.getValueInt()), (new Color(flop(this.lastS, 255), flop(this.lastS, 255), flop(this.lastS, 255), 255)).getRGB());
    FontLoaders.Sfui20.drawCenteredString("______", (35 + this.x.getValueInt()), (75 + this.y.getValueInt()), (new Color(flop(this.lastJump, 255), flop(this.lastJump, 255), flop(this.lastJump, 255), 255)).getRGB());
  }
  
  public int flop(int a, int b) {
    return b - a;
  }
}
