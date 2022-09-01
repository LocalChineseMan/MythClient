package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.DropShadowUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;

@Info(name = "Radar", category = Category.VISUAL)
public class Radar extends Module {
  public NumberSetting x = new NumberSetting("X", this, 15.0D, 1.0D, 1920.0D, true);
  
  public NumberSetting y = new NumberSetting("Y", this, 80.0D, 0.0D, 1080.0D, true);
  
  float hue;
  
  BlurShader blurShader = new BlurShader();
  
  @EventTarget
  public void onRender2D(Event2D e) {
    Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
    int size1 = 125;
    float xOffset = this.x.getValueInt();
    float yOffset = this.y.getValueInt();
    float playerOffsetX = (float)mc.thePlayer.posX;
    float playerOffSetZ = (float)mc.thePlayer.posZ;
    if (this.hue > 255.0F)
      this.hue = 0.0F; 
    this.hue += 0.1F;
    if (blur.isEnabled() && blur.modules.isEnabled("Radar") && blur.shadow.getValue().booleanValue()) {
      DropShadowUtil.start();
      RenderUtils.drawRoundedRect2((xOffset + 3.0F), (yOffset + 3.0F), xOffset + 125.0D - 3.0D, yOffset + 125.0D - 3.0D, 5.0D, (new Color(13, 13, 13, 140)).getRGB());
      RenderUtils.drawRoundedRect2(xOffset, yOffset, xOffset + 125.0D, yOffset + 125.0D, 10.0D, (new Color(1, 1, 1, 140)).getRGB());
      DropShadowUtil.stop();
    } 
    if (blur.isEnabled() && blur.modules.isEnabled("Radar"))
      this.blurShader.startBlur(); 
    RenderUtils.drawRoundedRect2((xOffset + 3.0F), (yOffset + 3.0F), xOffset + 125.0D - 3.0D, yOffset + 125.0D - 3.0D, 5.0D, (new Color(13, 13, 13, 140)).getRGB());
    RenderUtils.drawRoundedRect2(xOffset, yOffset, xOffset + 125.0D, yOffset + 125.0D, 10.0D, (new Color(1, 1, 1, 140)).getRGB());
    if (blur.isEnabled() && blur.modules.isEnabled("Radar"))
      this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
    RenderUtils.rectangle(xOffset + 62.0D, yOffset + 3.5D, xOffset + 63.0D, yOffset + 125.0D - 3.5D, -1);
    RenderUtils.rectangle(xOffset + 3.5D, yOffset + 62.0D, xOffset + 125.0D - 3.5D, yOffset + 63.0D, -1);
    for (Object o : mc.theWorld.getLoadedEntityList()) {
      EntityPlayer ent;
      if (!(o instanceof EntityPlayer) || !(ent = (EntityPlayer)o).isEntityAlive() || ent == mc.thePlayer || ent.isInvisible() || 
        ent.isInvisibleToPlayer((EntityPlayer)mc.thePlayer))
        continue; 
      float pTicks = mc.timer.renderPartialTicks;
      float posX = (float)((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks - playerOffsetX) * 1.0D);
      float posZ = (float)((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks - playerOffSetZ) * 1.0D);
      String formattedText = ent.getDisplayName().getFormattedText();
      int color39 = mc.thePlayer.canEntityBeSeen((Entity)ent) ? (new Color(255, 255, 255)).getRGB() : (new Color(120, 120, 120)).getRGB();
      for (int i = 0; i < formattedText.length(); i++) {
        if (formattedText.charAt(i) == '§' && i + 1 < formattedText.length()) {
          int index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(formattedText.charAt(i + 1)));
          if (index < 16)
            try {
              Color color40 = Color.RED;
              color39 = getColor(color40.getRed(), color40.getGreen(), color40.getBlue(), 255);
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {} 
        } 
      } 
      if (ent.hurtTime > 0)
        color39 = (new Color(255, 0, 0)).getRGB(); 
      float cos = (float)Math.cos(mc.thePlayer.rotationYaw * 0.017453292519943295D);
      float sin = (float)Math.sin(mc.thePlayer.rotationYaw * 0.017453292519943295D);
      float rotY = -posZ * cos - posX * sin;
      float rotX = -posX * cos + posZ * sin;
      if (rotY > 57.0F) {
        rotY = 57.0F;
      } else if (rotY < -67.0F) {
        rotY = -67.0F;
      } 
      if (rotX > 57.0F) {
        rotX = 57.0F;
      } else if (rotX < -67.0F) {
        rotX = -67.0F;
      } 
      RenderUtils.drawRoundedRect2((xOffset + 4.0F + 62.0F + rotX) - 1.5D, (yOffset + 4.0F + 62.0F + rotY) - 1.5D, (xOffset + 4.0F + 62.0F + rotX) + 1.5D, (yOffset + 4.0F + 62.0F + rotY) + 1.5D, 3.0D, color39);
    } 
  }
  
  public int getColor(int p_clamp_int_0_, int p_clamp_int_0_2, int p_clamp_int_0_3, int p_clamp_int_0_4) {
    return MathHelper.clamp_int(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp_int(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp_int(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp_int(p_clamp_int_0_3, 0, 255);
  }
  
  public Object castNumber(String newValueText, Object currentValue) {
    if (newValueText.contains(".")) {
      if (newValueText.toLowerCase().contains("f"))
        return Float.valueOf(Float.parseFloat(newValueText)); 
      return Double.valueOf(Double.parseDouble(newValueText));
    } 
    if (isNumeric(newValueText))
      return Integer.valueOf(Integer.parseInt(newValueText)); 
    return newValueText;
  }
  
  public boolean isNumeric(String text) {
    try {
      Integer.parseInt(text);
      return true;
    } catch (NumberFormatException numberFormatException) {
      return false;
    } 
  }
}
