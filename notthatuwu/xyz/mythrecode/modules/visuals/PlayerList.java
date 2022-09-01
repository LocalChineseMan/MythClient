package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.events.Event2D;

@Info(name = "PlayerList", category = Category.VISUAL)
public class PlayerList extends Module {
  static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
  
  public NumberSetting X = new NumberSetting("X", this, 3.0D, 0.0D, (sr.getScaledWidth() * 2), true);
  
  public NumberSetting Y = new NumberSetting("Y", this, 45.0D, 0.0D, (sr.getScaledHeight() * 2), true);
  
  public NumberSetting Alpha = new NumberSetting("Alpha", this, 125.0D, 0.0D, 255.0D, true);
  
  public BooleanSetting validname = new BooleanSetting("Valid Name", this, false);
  
  public ColorSetting colors = new ColorSetting("Color", this, Color.RED);
  
  private double x;
  
  private double y;
  
  public BlurShader blurShader = new BlurShader();
  
  @EventTarget
  public void on2d(Event2D event) {
    Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
    setX(this.X.getValue().doubleValue());
    setY(this.Y.getValue().doubleValue());
    float offset = 17.0F;
    if (blur.isEnabled() && blur.modules.isEnabled("PlayerList"))
      this.blurShader.startBlur(); 
    Gui.drawRect((float)(3.0D + getX()), (float)(-1.0D + getY()), (float)(151.0D + getX()), (float)(0.0D + getY()), this.colors.getColor());
    if (this.Alpha.getValue().doubleValue() > 210.0D) {
      Gui.drawRect((float)(3.0D + getX()), (float)(0.0D + getY()), (float)(151.0D + getX()), (float)(17.0D + getY()), (new Color(12, 12, 12, 255)).getRGB());
    } else {
      Gui.drawRect((float)(3.0D + getX()), (float)(0.0D + getY()), (float)(151.0D + getX()), (float)(17.0D + getY()), (new Color(12, 12, 12, (int)(this.Alpha.getValue().doubleValue() + 45.0D))).getRGB());
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("PlayerList"))
      this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    FontLoaders.Sfui22.drawCenteredString("Players", (int)(74.0D + getX()), (int)(2.5D + getY() + 2.5D), -1);
    for (EntityPlayer entity : mc.theWorld.playerEntities) {
      GlStateManager.resetColor();
      if (this.validname.getValue().booleanValue() && (
        entity.isInvisible() || entity.getName().contains("(") || entity.getName().contains(")") || entity.getName().contains("-") || entity.getName().contains("§")))
        return; 
      if (entity.isDead)
        return; 
      if (blur.isEnabled() && blur.modules.isEnabled("PlayerList"))
        this.blurShader.startBlur(); 
      Gui.drawRect((float)(3.0D + getX()), (float)(0.0D + getY() + offset), (float)(151.0D + getX()), (float)(17.0D + getY() + offset), (new Color(20, 20, 20, this.Alpha.getValueInt())).getRGB());
      if (blur.isEnabled() && blur.modules.isEnabled("PlayerList"))
        this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
      if (entity.getName().equals(mc.thePlayer.getName())) {
        FontLoaders.Sfui22.drawCenteredString(entity.getName() + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + "You" + EnumChatFormatting.GRAY + "]", (int)(74.0D + getX()), (int)(2.5D + offset + getY() + 2.0D), -1);
      } else {
        FontLoaders.Sfui22.drawCenteredString(entity.getName() + getDistance(entity), (int)(74.0D + getX()), (int)(2.5D + offset + getY() + 2.0D), -1);
      } 
      offset += 17.0F;
    } 
  }
  
  public String getDistance(EntityPlayer entity) {
    return EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + Math.round(entity.getMaxHealth()) + "HP" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + Math.round(entity.getDistanceToEntity((Entity)mc.thePlayer)) + "m" + EnumChatFormatting.GRAY + "]";
  }
  
  public static boolean hovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
    return (mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom);
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
}
