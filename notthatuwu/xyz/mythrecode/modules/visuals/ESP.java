package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ColorUtil;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.gl.OGLUtils;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.Event3D;
import notthatuwu.xyz.mythrecode.modules.combat.AntiBot;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@Info(name = "ESP", category = Category.VISUAL)
public class ESP extends Module {
  public AddonSetting addons = new AddonSetting("Addons", this, "Box", new String[] { "Outline", "Health Bar", "Chams", "Box", "Image" });
  
  public ModeSetting image = new ModeSetting("Image", this, new String[] { "Blackman", "Hizzy" }, "Hizzy", () -> Boolean.valueOf(this.addons.isEnabled("Image")));
  
  public ModeSetting healthbar = new ModeSetting("Health Bar", this, new String[] { "Rect", "Gardient" }, "Rect", () -> Boolean.valueOf(this.addons.isEnabled("Health Bar")));
  
  public ModeSetting gardientcolor = new ModeSetting("Gardient", this, new String[] { "Custom", "Health" }, "Health", () -> Boolean.valueOf(this.healthbar.is("Gardient")));
  
  public ColorSetting color1 = new ColorSetting("Gardient 1", this, Color.RED, () -> Boolean.valueOf((this.gardientcolor.is("Custom") && this.healthbar.is("Gardient"))));
  
  public ColorSetting color2 = new ColorSetting("Gardient 2", this, Color.blue.darker(), () -> Boolean.valueOf((this.gardientcolor.is("Custom") && this.healthbar.is("Gardient"))));
  
  public ColorSetting boxColor = new ColorSetting("Box Color", this, Color.WHITE, () -> Boolean.valueOf(this.addons.isEnabled("Box")));
  
  public ColorSetting outlineColor = new ColorSetting("Outline Color", this, Color.WHITE, () -> Boolean.valueOf(this.addons.isEnabled("Outline")));
  
  public NumberSetting boxWidth = new NumberSetting("Box Width", this, 1.3D, 0.5D, 3.0D, false, () -> Boolean.valueOf(this.addons.isEnabled("Box")));
  
  public BooleanSetting rainbowBox = new BooleanSetting("Rainbow Box", this, false, () -> Boolean.valueOf(this.addons.isEnabled("Box")));
  
  public BooleanSetting healthText = new BooleanSetting("Health Text", this, false, () -> Boolean.valueOf(this.addons.isEnabled("Health Bar")));
  
  private static Map<EntityPlayer, float[][]> entities = (Map)new HashMap<>();
  
  private final Map<EntityPlayer, float[]> entityPosMap = (Map)new HashMap<>();
  
  private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
  
  private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
  
  private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
  
  private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
  
  @EventTarget
  public void onRender3D(Event3D event) {
    entities.keySet().removeIf(player -> !mc.theWorld.playerEntities.contains(player));
    if (!this.entityPosMap.isEmpty())
      this.entityPosMap.clear(); 
    ScaledResolution sr = new ScaledResolution(mc);
    int scaleFactor = sr.getScaleFactor();
    for (EntityPlayer player : mc.theWorld.playerEntities) {
      ResourceLocation currentimage = null;
      switch (this.image.getValue()) {
        case "Blackman":
          currentimage = new ResourceLocation("myth/esp/blackman.png");
          break;
        case "Hizzy":
          currentimage = new ResourceLocation("myth/esp/hizzy.png");
          break;
      } 
      if (this.addons.isEnabled("Image") && 
        !AntiBot.isBot((EntityLivingBase)player) && !player.isInvisible() && player.isEntityAlive() && player != mc.thePlayer) {
        mc.getRenderManager();
        double d1 = RenderUtils.getPos(player.posX, player.lastTickPosX) - RenderManager.renderPosX;
        mc.getRenderManager();
        double d2 = RenderUtils.getPos(player.posY, player.lastTickPosY) - RenderManager.renderPosY;
        mc.getRenderManager();
        double d3 = RenderUtils.getPos(player.posZ, player.lastTickPosZ) - RenderManager.renderPosZ;
        float dist = MathHelper.clamp_float(mc.thePlayer.getDistanceToEntity((Entity)player), 20.0F, Float.MAX_VALUE);
        double size = 0.005D * dist;
        GlStateManager.pushMatrix();
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        GL11.glDisable(2929);
        GlStateManager.translate(d1, d2, d3);
        GlStateManager.rotate(-(mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-0.1D, -0.1D, 0.0D);
        mc.getTextureManager().bindTexture(currentimage);
        Gui.drawScaledCustomSizeModalRect((int)(player.width / 2.0F - dist / 3.0F), (int)(-player.height - dist), 0.0F, 0.0F, 1, 1, (int)(252.0D * size / 2.0D), (int)(476.0D * size / 2.0D), 1.0F, 1.0F);
        GL11.glEnable(2929);
        GlStateManager.popMatrix();
      } 
      if (player.isDead || player.isInvisible())
        continue; 
      if (player.getDistanceToEntity((Entity)mc.thePlayer) < 1.0F)
        continue; 
      GL11.glPushMatrix();
      Vec3 vec3 = getVec3(player);
      float posX = (float)(vec3.xCoord - RenderManager.viewerPosX);
      float posY = (float)(vec3.yCoord - RenderManager.viewerPosY);
      float posZ = (float)(vec3.zCoord - RenderManager.viewerPosZ);
      double halfWidth = player.width / 2.0D + 0.18000000715255737D;
      AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth, (posY + player.height) + 0.18D, posZ + halfWidth);
      double[][] vectors = { { bb.minX, bb.minY, bb.minZ }, { bb.minX, bb.maxY, bb.minZ }, { bb.minX, bb.maxY, bb.maxZ }, { bb.minX, bb.minY, bb.maxZ }, { bb.maxX, bb.minY, bb.minZ }, { bb.maxX, bb.maxY, bb.minZ }, { bb.maxX, bb.maxY, bb.maxZ }, { bb.maxX, bb.minY, bb.maxZ } };
      Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
      for (double[] vec : vectors) {
        Vector3f projection = project2D((float)vec[0], (float)vec[1], (float)vec[2], scaleFactor);
        if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
          position.x = Math.min(position.x, projection.x);
          position.y = Math.min(position.y, projection.y);
          position.z = Math.max(position.z, projection.x);
          position.w = Math.max(position.w, projection.y);
        } 
      } 
      this.entityPosMap.put(player, new float[] { position.x, position.z, position.y, position.w });
      GL11.glPopMatrix();
    } 
  }
  
  @EventTarget
  public void onRender2D(Event2D event) {
    for (EntityPlayer player : this.entityPosMap.keySet()) {
      GlStateManager.resetColor();
      this;
      ScaledResolution sr = new ScaledResolution(mc);
      GL11.glPushMatrix();
      float[] positions = this.entityPosMap.get(player);
      float x = positions[0];
      float x2 = positions[1];
      float y = positions[2];
      float y2 = positions[3];
      if (this.addons.isEnabled("Box"))
        Gui.drawRect(x - 2.5D, (y - 0.5F), (x - 0.5F), (y2 + 0.5F), (new Color(0, 0, 0, 0)).getRGB()); 
      float health = player.getHealth();
      float maxHealth = player.getMaxHealth();
      float healthPercentage = health / maxHealth;
      boolean needScissor = (health < maxHealth);
      float heightDif = y - y2;
      float healthBarHeight = heightDif * healthPercentage;
      if (this.addons.isEnabled("Health Bar")) {
        if (needScissor)
          OGLUtils.startScissorBox(sr, (int)x - 2, (int)(y2 + healthBarHeight), 2, (int)-healthBarHeight + 1); 
        GL11.glPushMatrix();
        if (this.healthText.getValue().booleanValue())
          FontLoaders.Sfui14.drawString(Math.round(player.getHealth() * 5.0F) + "%", (int)(x - 17.0F), ((int)y + 2), -1); 
        GL11.glPopMatrix();
        switch (this.healthbar.getValue()) {
          case "Rect":
            Gui.drawRect((x - 2.0F), y, (x - 1.0F), y2, ColorUtil.getHealthColor((EntityLivingBase)player));
            break;
          case "Gardient":
            switch (this.gardientcolor.getValue()) {
              case "Health":
                Gui.drawGradientRect((int)(x - 2.0F), (int)y, (int)(x - 1.0F), (int)y2, (new Color(0, 200, 0)).getRGB(), (new Color(200, 200, 0)).getRGB());
                break;
              case "Custom":
                Gui.drawGradientRect((int)(x - 2.0F), (int)y, (int)(x - 1.0F), (int)y2, this.color1.getColor(), this.color2.getColor());
                break;
            } 
            break;
        } 
        if (needScissor)
          OGLUtils.endScissorBox(); 
      } 
      if (this.addons.isEnabled("Box")) {
        GL11.glDisable(3553);
        GlStateManager.enableAlpha();
        GL11.glLineWidth(this.boxWidth.getValueInt());
        if (this.rainbowBox.getValue().booleanValue()) {
          GL11.glColor3f(ColorUtil.rainbow(450).getRed() / 255.0F, ColorUtil.rainbow(450).getGreen() / 255.0F, ColorUtil.rainbow(450).getRed() / 255.0F);
        } else {
          GL11.glColor3f(this.boxColor.getRed() / 255.0F, this.boxColor.getGreen() / 255.0F, this.boxColor.getBlue() / 255.0F);
        } 
        GL11.glBegin(2);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x2, y);
        GL11.glEnd();
        GlStateManager.disableAlpha();
        GL11.glEnable(3553);
      } 
      GL11.glPopMatrix();
    } 
  }
  
  private Vector3f project2D(float x, float y, float z, int scaleFactor) {
    GL11.glGetFloat(2982, this.modelMatrix);
    GL11.glGetFloat(2983, this.projectionMatrix);
    GL11.glGetInteger(2978, this.viewport);
    if (GLU.gluProject(x, y, z, this.modelMatrix, this.projectionMatrix, this.viewport, this.windowPosition))
      return new Vector3f(this.windowPosition.get(0) / scaleFactor, (mc.displayHeight - this.windowPosition.get(1)) / scaleFactor, this.windowPosition.get(2)); 
    return null;
  }
  
  private Vec3 getVec3(EntityPlayer var0) {
    float timer = mc.timer.renderPartialTicks;
    double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
    double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
    double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
    return new Vec3(x, y, z);
  }
}
