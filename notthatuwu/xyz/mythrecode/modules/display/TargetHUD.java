package notthatuwu.xyz.mythrecode.modules.display;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ColorUtil;
import notthatuwu.xyz.mythrecode.api.utils.font.CFontRenderer;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.DropShadowUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.Event3D;
import notthatuwu.xyz.mythrecode.manager.InstanceManager;
import notthatuwu.xyz.mythrecode.modules.combat.KillAura;
import notthatuwu.xyz.mythrecode.modules.visuals.Blur;
import org.lwjgl.opengl.GL11;

@Info(name = "TargetHUD", category = Category.DISPLAY)
public class TargetHUD extends Module {
  public ModeSetting mode = new ModeSetting("Type", this, new String[] { "1", "2", "3", "4", "Astolfo", "Exhibition" }, "1");
  
  public BooleanSetting pointed = new BooleanSetting("Pointed Entity", this, false);
  
  public BooleanSetting render3D = new BooleanSetting("3D", this, false, () -> Boolean.valueOf(false));
  
  public NumberSetting minScale = new NumberSetting("Min Scale", this, 0.15D, 0.1D, 3.0D, false, () -> this.render3D.getValue());
  
  public NumberSetting maxScale = new NumberSetting("Min Scale", this, 0.15D, 0.1D, 3.0D, false, () -> this.render3D.getValue());
  
  public NumberSetting xPos = new NumberSetting("X", this, 200.0D, 0.0D, 1000.0D, true, 10.0D);
  
  public NumberSetting yPos = new NumberSetting("Y", this, 200.0D, 0.0D, 1000.0D, true, 10.0D);
  
  @EventTarget
  public void onRender(Event2D event) {
    this.render3D.setValue(Boolean.valueOf(false));
    if (!this.render3D.getValue().booleanValue()) {
      EntityPlayerSP entityPlayerSP;
      EntityLivingBase target = this.pointed.getValue().booleanValue() ? (EntityLivingBase)mc.pointedEntity : KillAura.target;
      if (target == null && (mc.currentScreen instanceof notthatuwu.xyz.mythrecode.api.ui.dropdown.ClickGui || mc.currentScreen instanceof net.minecraft.client.gui.GuiChat))
        entityPlayerSP = mc.thePlayer; 
      if (!(entityPlayerSP instanceof net.minecraft.entity.player.EntityPlayer))
        return; 
      Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
      float x = this.xPos.getValueInt(), y = this.yPos.getValueInt();
      switch (this.mode.getValue()) {
        case "1":
          drawFirst(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "2":
          drawSecond(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "3":
          drawThird(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "4":
          drawFourth(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "Astolfo":
          drawFifth(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "Exhibition":
          drawExhibiton(x, y, (EntityLivingBase)entityPlayerSP);
          break;
      } 
    } 
  }
  
  @EventTarget
  public void render3D(Event3D event) {
    if (this.render3D.getValue().booleanValue()) {
      EntityPlayerSP entityPlayerSP;
      EntityLivingBase target = this.pointed.getValue().booleanValue() ? (EntityLivingBase)mc.pointedEntity : KillAura.target;
      if (target == null && (mc.currentScreen instanceof notthatuwu.xyz.mythrecode.api.ui.dropdown.ClickGui || mc.currentScreen instanceof net.minecraft.client.gui.GuiChat))
        entityPlayerSP = mc.thePlayer; 
      if (!(entityPlayerSP instanceof net.minecraft.entity.player.EntityPlayer))
        return; 
      GL11.glPushMatrix();
      Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
      float saveX = 0.0F, saveY = 0.0F, saveZ = 0.0F;
      mc.getRenderManager();
      saveX = (float)(((EntityLivingBase)entityPlayerSP).lastTickPosX + (((EntityLivingBase)entityPlayerSP).posX - ((EntityLivingBase)entityPlayerSP).lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX);
      mc.getRenderManager();
      saveY = (float)(((EntityLivingBase)entityPlayerSP).lastTickPosY + (((EntityLivingBase)entityPlayerSP).posY - ((EntityLivingBase)entityPlayerSP).lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY);
      mc.getRenderManager();
      saveZ = (float)(((EntityLivingBase)entityPlayerSP).lastTickPosZ + (((EntityLivingBase)entityPlayerSP).posZ - ((EntityLivingBase)entityPlayerSP).lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ);
      GL11.glTranslated(saveX, (saveY + ((EntityLivingBase)entityPlayerSP).height / 2.0F) + 0.2D, saveZ);
      GL11.glScalef(-0.06F, -0.06F, -0.06F);
      GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
      GL11.glRotated((mc.getRenderManager()).playerViewX, (((getGameSettings()).thirdPersonView == 2) ? -1 : true), 0.0D, 0.0D);
      float scaleAutism = (float)(1.6D * Math.abs(mc.thePlayer.getDistanceToEntity((Entity)entityPlayerSP) / 25.0F));
      scaleAutism = MathHelper.clamp_float(scaleAutism, this.minScale.getValueInt(), this.maxScale.getValueInt());
      GL11.glScaled(scaleAutism, scaleAutism, scaleAutism);
      GL11.glDisable(2929);
      float x = 0.0F;
      float y = 0.0F;
      switch (this.mode.getValue()) {
        case "1":
          drawFirst(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "2":
          drawSecond(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "3":
          drawThird(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "4":
          drawFourth(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "Astolfo":
          drawFifth(x, y, blur, (EntityLivingBase)entityPlayerSP);
          break;
        case "Exhibition":
          drawExhibiton(x, y, (EntityLivingBase)entityPlayerSP);
          break;
      } 
      GL11.glEnable(2929);
      GL11.glPopMatrix();
    } 
  }
  
  private void drawFirst(float x, float y, Blur blur, EntityLivingBase target) {
    GlStateManager.disableBlend();
    float width = 170.0F, height = 60.0F;
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && blur.shadow.getValue().booleanValue() && !this.render3D.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2(x, y, (x + 170.0F), (y + 60.0F), 5.0D, (new Color(0, 0, 0, 110)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.startBlur(); 
    RenderUtils.drawRoundedRect2(x, y, (x + 170.0F), (y + 60.0F), 5.0D, (new Color(0, 0, 0, 110)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    double hpPercentage = (target.getHealth() / target.getMaxHealth());
    RenderUtils.drawHead((AbstractClientPlayer)target, (int)(x + 10.0F), (int)(y + 8.0F), 28, 28);
    FontLoaders.Sfui20.drawString(target.getName(), x + 45.0F, y + 8.0F, -1);
    FontLoaders.Sfui20.drawString(Math.round(mc.thePlayer.getDistanceToEntity((Entity)target)) + "m - " + Math.round(target.getHealth()) + "HP", x + 45.0F, y + 20.0F, -1);
    Gui.drawRect((x + 10.0F), y + 54.5D, (x + 10.0F + target.getTotalArmorValue() / 20.0F * 150.0F), (y + 60.0F - 57.0F + 53.0F - 4.0F), (new Color(10, 12, 150)).getRGB());
    Gui.drawRect((x + 10.0F), (y + 58.0F), (x + 10.0F) + hpPercentage * 150.0D, (y + 60.0F - 57.0F + 53.0F), ColorUtil.getHealthColor(target));
  }
  
  private void drawSecond(float x, float y, Blur blur, EntityLivingBase target) {
    GlStateManager.disableBlend();
    float width = 140.0F, height = 45.0F;
    double hpPercentage = (target.getHealth() / target.getMaxHealth());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && blur.shadow.getValue().booleanValue() && !this.render3D.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2(x, y, (x + 140.0F), (y + 45.0F), 7.0D, (new Color(0, 0, 0, 110)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.startBlur(); 
    RenderUtils.drawRoundedRect2(x, y, (x + 140.0F), (y + 45.0F), 7.0D, (new Color(0, 0, 0, 110)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    RenderUtils.drawHead((AbstractClientPlayer)target, (int)(x + 10.0F), (int)(y + 11.3D), 28, 28);
    FontLoaders.Sfui20.drawString(target.getName(), x + 44.0F, y + 14.0F, -1);
    Gui.drawGradientSideways((x + 45.0F), (y + 3.0F + 25.0F), (x + 45.0F) + hpPercentage * 80.0D, (y + 4.0F + 35.0F), (new Color(180, 10, 120)).getRGB(), Color.BLUE.darker().getRGB());
  }
  
  private void drawThird(float x, float y, Blur blur, EntityLivingBase target) {
    GlStateManager.disableBlend();
    float width = 140.0F, height = 45.0F;
    double hpPercentage = (target.getHealth() / target.getMaxHealth());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && blur.shadow.getValue().booleanValue() && !this.render3D.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2(x, y, (x + 140.0F), (y + 45.0F), 7.0D, (new Color(0, 0, 0, 110)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.startBlur(); 
    RenderUtils.drawRoundedRect2(x, y, (x + 140.0F), (y + 45.0F), 7.0D, (new Color(0, 0, 0, 110)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    RenderUtils.drawUnfilledRoundedRect(x, y, 140.0F, 45.0F, 7.0F, ColorUtil.rainbow(60).getRGB());
    RenderUtils.drawHead((AbstractClientPlayer)target, (int)(x + 10.0F), (int)(y + 11.3D), 28, 28);
    FontLoaders.Sfui20.drawString(target.getName(), x + 44.0F, y + 14.0F, -1);
    RenderUtils.drawRoundedRect2((x + 45.0F), (y + 3.0F + 25.0F), (x + 45.0F + 80.0F), (y + 4.0F + 35.0F), 4.0D, (new Color(11, 11, 11, 120)).getRGB());
    RenderUtils.drawRoundedRect2((x + 45.0F), (y + 3.0F + 25.0F), (x + 45.0F) + hpPercentage * 80.0D, (y + 4.0F + 35.0F), 4.0D, ColorUtil.rainbow(60).getRGB());
    FontLoaders.Sfui16.drawString((Math.round(target.getHealth()) * 5) + "%", x + 45.0F + (80.0F - FontLoaders.Sfui16.getStringWidth((Math.round(target.getHealth()) * 5) + "%")) / 2.0F, y + 3.0F + 25.0F + 3.0F, -1);
  }
  
  private void drawFourth(float x, float y, Blur blur, EntityLivingBase target) {
    GlStateManager.disableBlend();
    float width = 160.0F, height = 60.0F;
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && blur.shadow.getValue().booleanValue() && !this.render3D.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2(x, (y + 10.0F), (x + 160.0F), (y + 60.0F), 10.0D, (new Color(0, 0, 0, 110)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.startBlur(); 
    RenderUtils.drawRoundedRect2(x, (y + 10.0F), (x + 160.0F), (y + 60.0F), 10.0D, (new Color(0, 0, 0, 110)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    double hpPercentage = (target.getHealth() / target.getMaxHealth());
    if (target instanceof net.minecraft.entity.player.EntityPlayer)
      RenderUtils.drawHead((AbstractClientPlayer)target, (int)(x + 10.0F), (int)(y + 19.0F), 28, 28); 
    int distance = Math.round(mc.thePlayer.getDistanceToEntity((Entity)target));
    int health = Math.round(target.getHealth());
    FontLoaders.Sfui20.drawString(target.getName(), x + 45.0F, y + 22.0F, -1);
    FontLoaders.Sfui16.drawString(EnumChatFormatting.GRAY + "Health: " + health, x + 45.0F, y + 33.0F, -1);
    FontLoaders.Sfui16.drawString(EnumChatFormatting.GRAY + "Distance: " + distance + "m", x + 45.0F, y + 41.0F, -1);
    Gui.drawRect((x + 10.0F), y + 54.5D, (x + 10.0F) + hpPercentage * 140.0D, (y + 60.0F - 8.0F), ColorUtil.getHealthColor(target));
  }
  
  private void drawFifth(float x, float y, Blur blur, EntityLivingBase target) {
    HUD hud = (HUD)Client.INSTANCE.moduleManager.getModuleByClass(HUD.class);
    double addX = 0.0D;
    int color = hud.arrayListColorMode.is("Client") ? hud.colorSetting.getColor() : Color.RED.getRGB();
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && blur.shadow.getValue().booleanValue() && !this.render3D.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRect((x - 1.0F), (y + 2.0F), 155.0D, 57.0D, (new Color(-1459157241, true)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.startBlur(); 
    RenderUtils.drawRect((x - 1.0F), (y + 2.0F), 155.0D, 57.0D, (new Color(-1459157241, true)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("TargetHUD") && !this.render3D.getValue().booleanValue())
      InstanceManager.BLUR_SHADER.stopBlur(); 
    mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 31.0F, y + 6.0F, -1);
    GL11.glPushMatrix();
    GlStateManager.translate(x, y, 1.0F);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    GlStateManager.translate(-x, -y, 1.0F);
    mc.fontRendererObj.drawStringWithShadow((Math.round((target.getHealth() / 2.0F) * 10.0D) / 10.0D) + "❤", x + 16.0F, y + 13.0F, (new Color(color)).darker().getRGB());
    GL11.glPopMatrix();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glEnable(2896);
    GL11.glEnable(3042);
    GlStateManager.resetColor();
    try {
      if (this.render3D.getValue().booleanValue()) {
        GL11.glClearDepth(1.0D);
        GL11.glEnable(2929);
        GL11.glClear(256);
        GL11.glDepthFunc(515);
      } 
      GuiInventory.drawEntityOnScreen((int)(x + 16.0F), (int)(y + 55.0F), this.render3D.getValue().booleanValue() ? 1.0F : 50.0F, 18, getYaw(), target.rotationPitch * -1.0F, target);
    } catch (ReportedException e1) {
      e1.printStackTrace();
    } 
    GlStateManager.blendFunc(770, 771);
    int xHealthbar = 30;
    int yHealthbar = 46;
    float add = 120.0F;
    RenderUtils.drawRect((x + xHealthbar), (y + yHealthbar), add, 8.0D, (new Color(color)).darker().darker().darker().getRGB());
    RenderUtils.drawRect((float)(addX - 3.0D), (y + yHealthbar), 3.0D, 8.0D, (new Color(-1979711488, true)).getRGB());
    RenderUtils.drawRect((x + xHealthbar), (y + yHealthbar), (target.getHealth() / target.getMaxHealth() * add), 8.0D, (new Color(color)).getRGB());
  }
  
  private void drawExhibiton(float x, float y, EntityLivingBase target) {
    CFontRenderer font = FontLoaders.Sfui20;
    CFontRenderer fontSmall = FontLoaders.Sfui13;
    double width = Math.max(130, font.getStringWidth(target.getName()) + 45), height = 50.0D;
    Gui.drawRect(x, y, x + width, y + height, Color.BLACK.getRGB());
    Gui.drawRect((x + 1.0F), (y + 1.0F), x + width - 1.0D, y + height - 1.0D, (new Color(37, 37, 37)).getRGB());
    Gui.drawRect((x + 4.0F), (y + 4.0F), x + width - 4.0D, y + height - 4.0D, (new Color(22, 22, 22)).getRGB());
    Gui.drawRect((x + 4.0F), (y + 4.0F), x + width - 4.0D, y + height - 4.0D, (new Color(16, 16, 16)).getRGB());
    Gui.drawRect((x + 5.0F), (y + 5.0F), x + width - 5.0D, y + height - 5.0D, (new Color(22, 22, 22)).getRGB());
    GL11.glEnable(2896);
    GL11.glEnable(3042);
    GlStateManager.resetColor();
    try {
      if (this.render3D.getValue().booleanValue()) {
        GL11.glClearDepth(1.0D);
        GL11.glEnable(2929);
        GL11.glClear(256);
        GL11.glDepthFunc(515);
        GlStateManager.blendFunc(770, 771);
      } 
      GuiInventory.drawEntityOnScreen((int)(x + 20.0F), (int)(y + 42.0F), this.render3D.getValue().booleanValue() ? 1.0F : 17.0F, 18, target.rotationYaw, -target.rotationPitch, target);
    } catch (ReportedException e1) {
      e1.printStackTrace();
    } 
    font.drawString(target.getName(), x + 40.0F, y + 7.0F, -1);
    fontSmall.drawString("HP: " + (int)target.getHealth() + " | Dist: " + (int)mc.thePlayer.getDistanceToEntity((Entity)target), x + 40.0F, y + 25.0F, -1);
    double barWidth = width - 50.0D, barHeight = 6.0D;
    double barOffset = 16.0D;
    Color healthColor = new Color(ColorUtil.getHealthColor(target));
    Gui.drawRect((x + 40.0F), y + barOffset, (x + 40.0F) + barWidth, y + barOffset + barHeight, Color.BLACK.getRGB());
    Gui.drawRect((x + 40.0F + 1.0F), y + barOffset + 1.0D, (x + 40.0F) + barWidth - 1.0D, y + barOffset + barHeight - 1.0D, healthColor.darker().darker().getRGB());
    Gui.drawRect((x + 40.0F + 1.0F), y + barOffset + 1.0D, (x + 40.0F) + (target.getHealth() / target.getMaxHealth()) * barWidth - 1.0D, y + barOffset + barHeight - 1.0D, healthColor.getRGB());
    int amount = (int)(barWidth / 10.0D);
    int length = (int)(barWidth / amount);
    for (int i = 1; i <= amount; i++)
      Gui.drawRect((x + 40.0F + (i * length) - 1.0F), y + barOffset, (x + 40.0F + (i * length)), y + barOffset + barHeight, Color.BLACK.getRGB()); 
    ArrayList<ItemStack> items = new ArrayList<>();
    int j;
    for (j = 3; j >= 0; j--) {
      ItemStack stack = target.getCurrentArmor(j);
      if (stack != null)
        items.add(stack); 
    } 
    if (target.getHeldItem() != null)
      items.add(target.getHeldItem()); 
    j = 0;
    for (ItemStack stack : items) {
      mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int)(x + 40.0F + (j * 15)), (int)(y + 29.0F));
      j++;
    } 
  }
  
  public static double getPos(double newPos, double oldPos) {
    return oldPos + (newPos - oldPos) * mc.timer.renderPartialTicks;
  }
}
