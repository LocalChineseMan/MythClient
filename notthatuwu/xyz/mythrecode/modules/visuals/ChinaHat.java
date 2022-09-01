package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.client.renderer.entity.RenderManager;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.utils.ColorUtil;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.events.Event3D;
import org.lwjgl.opengl.GL11;

@Info(name = "ChinaHat", category = Category.VISUAL)
public class ChinaHat extends Module {
  public ColorSetting color = new ColorSetting("Color", this, Color.BLUE.darker());
  
  public BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);
  
  @EventTarget
  public void onRender(Event3D event) {
    if (mc.gameSettings.thirdPersonView != 0) {
      this;
      this;
      this;
      float x = (float)((float)(mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks()) - RenderManager.renderPosX);
      this;
      this;
      this;
      float y = (float)(((float)(mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks()) + 2.2F) - RenderManager.renderPosY);
      this;
      this;
      this;
      float z = (float)((float)(mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks()) - RenderManager.renderPosZ);
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glDisable(2896);
      GL11.glDepthMask(false);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(1);
      float i;
      for (i = 0.0F; i <= 360.0F; i = (float)(i + 0.1D)) {
        Color c = this.color.getValue();
        if (this.rainbow.getValue().booleanValue()) {
          GL11.glColor4f(ColorUtil.rainbow(450).getRed() / 255.0F, ColorUtil.rainbow(450).getGreen() / 255.0F, ColorUtil.rainbow(450).getRed() / 255.0F, 0.2F);
        } else {
          GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 0.2F);
        } 
        GL11.glVertex3d(x + Math.sin(i * Math.PI / 180.0D) * 0.5D, y - 0.2D, z + Math.cos(i * Math.PI / 180.0D) * 0.5D);
        GL11.glVertex3d(x, y + 0.05D, z);
      } 
      GL11.glEnd();
      RenderUtils.post3D();
    } 
  }
}
