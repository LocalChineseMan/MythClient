package notthatuwu.xyz.mythrecode.api.utils.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import notthatuwu.xyz.mythrecode.api.module.Module;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class RenderUtils {
  public static double zLevel;
  
  public static double[] getInterpolatedPosition(Entity entity) {
    Module.mc
      .getRenderManager();
    double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * Module.mc.timer.renderPartialTicks - RenderManager.viewerPosX;
    Module.mc
      .getRenderManager();
    double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * Module.mc.timer.renderPartialTicks - RenderManager.viewerPosY;
    Module.mc
      .getRenderManager();
    double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * Module.mc.timer.renderPartialTicks - RenderManager.viewerPosZ;
    return new double[] { posX, posY, posZ };
  }
  
  public static double animate(double target, double current, double speed) {
    boolean larger = (target > current);
    if (speed < 0.0D) {
      speed = 0.0D;
    } else if (speed > 1.0D) {
      speed = 1.0D;
    } 
    double dif = Math.max(target, current) - Math.min(target, current);
    double factor = dif * speed;
    if (factor < 0.1D)
      factor = 0.1D; 
    if (larger) {
      current += factor;
    } else {
      current -= factor;
    } 
    return current;
  }
  
  public static void scissor(double x, double y, double width, double height) {
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    double scale = sr.getScaleFactor();
    double finalHeight = height * scale;
    double finalY = (sr.getScaledHeight() - y) * scale;
    double finalX = x * scale;
    double finalWidth = width * scale;
    GL11.glScissor((int)finalX, (int)(finalY - finalHeight), (int)finalWidth, (int)finalHeight);
  }
  
  public static void drawHead(AbstractClientPlayer target, int x, int y, int width, int height) {
    ResourceLocation skin = target.getLocationSkin();
    Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
    Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, width, height, 64.0F, 64.0F);
  }
  
  public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    float f = (startColor >> 24 & 0xFF) / 255.0F;
    float f2 = (startColor >> 16 & 0xFF) / 255.0F;
    float f3 = (startColor >> 8 & 0xFF) / 255.0F;
    float f4 = (startColor & 0xFF) / 255.0F;
    float f5 = (endColor >> 24 & 0xFF) / 255.0F;
    float f6 = (endColor >> 16 & 0xFF) / 255.0F;
    float f7 = (endColor >> 8 & 0xFF) / 255.0F;
    float f8 = (endColor & 0xFF) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(right, top, zLevel).color(f2, f3, f4, f).endVertex();
    worldrenderer.pos(left, top, zLevel).color(f2, f3, f4, f).endVertex();
    worldrenderer.pos(left, bottom, zLevel).color(f6, f7, f8, f5).endVertex();
    worldrenderer.pos(right, bottom, zLevel).color(f6, f7, f8, f5).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
  
  public static void rectangle(double left, double top, double right, double bottom, int color) {
    if (left < right) {
      double var5 = left;
      left = right;
      right = var5;
    } 
    if (top < bottom) {
      double var5 = top;
      top = bottom;
      bottom = var5;
    } 
    float var11 = (color >> 24 & 0xFF) / 255.0F;
    float var6 = (color >> 16 & 0xFF) / 255.0F;
    float var7 = (color >> 8 & 0xFF) / 255.0F;
    float var8 = (color & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(var6, var7, var8, var11);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(left, bottom, 0.0D).endVertex();
    worldRenderer.pos(right, bottom, 0.0D).endVertex();
    worldRenderer.pos(right, top, 0.0D).endVertex();
    worldRenderer.pos(left, top, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
    rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    rectangle(x + width, y, x1 - width, y + width, borderColor);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    rectangle(x, y, x + width, y1, borderColor);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    rectangle(x1 - width, y, x1, y1, borderColor);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void pre3D() {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDisable(2896);
    GL11.glDepthMask(false);
    GL11.glHint(3154, 4354);
  }
  
  public static void post3D() {
    GL11.glDepthMask(true);
    GL11.glEnable(2929);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void drawFilledBoundingBox(AxisAlignedBB box) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    tessellator.draw();
  }
  
  public static void drawUnfilledRoundedRect(float x, float y, float width, float height, float radius, int color) {
    float x1 = x + width;
    float y1 = y + height;
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f1 = (color >> 16 & 0xFF) / 255.0F;
    float f2 = (color >> 8 & 0xFF) / 255.0F;
    float f3 = (color & 0xFF) / 255.0F;
    GL11.glPushAttrib(0);
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0F;
    y *= 2.0F;
    x1 *= 2.0F;
    y1 *= 2.0F;
    GL11.glDisable(3553);
    GL11.glColor4f(f1, f2, f3, f);
    GlStateManager.enableBlend();
    GL11.glEnable(2848);
    GL11.glBegin(2);
    double v = 0.017453292519943295D;
    int i;
    for (i = 0; i <= 90; i += 3)
      GL11.glVertex2d((x + radius + MathHelper.sin((float)(i * 0.017453292519943295D)) * radius * -1.0F), (y + radius + MathHelper.cos((float)(i * 0.017453292519943295D)) * radius * -1.0F)); 
    for (i = 90; i <= 180; i += 3)
      GL11.glVertex2d((x + radius + MathHelper.sin((float)(i * 0.017453292519943295D)) * radius * -1.0F), (y1 - radius + MathHelper.cos((float)(i * 0.017453292519943295D)) * radius * -1.0F)); 
    for (i = 0; i <= 90; i += 3)
      GL11.glVertex2d((x1 - radius + MathHelper.sin((float)(i * 0.017453292519943295D)) * radius), (y1 - radius + MathHelper.cos((float)(i * 0.017453292519943295D)) * radius)); 
    for (i = 90; i <= 180; i += 3)
      GL11.glVertex2d((x1 - radius + MathHelper.sin((float)(i * 0.017453292519943295D)) * radius), (y + radius + MathHelper.cos((float)(i * 0.017453292519943295D)) * radius)); 
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopAttrib();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void color(int color, float alpha) {
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }
  
  public static void color(int color) {
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f1 = (color >> 16 & 0xFF) / 255.0F;
    float f2 = (color >> 8 & 0xFF) / 255.0F;
    float f3 = (color & 0xFF) / 255.0F;
    GL11.glColor4f(f1, f2, f3, f);
  }
  
  public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor) {
    Gui.drawRect(left - borderWidth, top - borderWidth, right + borderWidth, bottom + borderWidth, borderColor);
    Gui.drawRect(left, top, right, bottom, insideColor);
  }
  
  public static void drawRoundedRect2(double x, double y, double x2, double y2, double radius, int color) {
    drawRoundedRect(x, y, x2 - x, y2 - y, radius, color);
  }
  
  public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    double x1 = x + width;
    double y1 = y + height;
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f1 = (color >> 16 & 0xFF) / 255.0F;
    float f2 = (color >> 8 & 0xFF) / 255.0F;
    float f3 = (color & 0xFF) / 255.0F;
    GL11.glPushAttrib(0);
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    GL11.glDisable(3553);
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glEnable(2848);
    GL11.glBegin(9);
    int i;
    for (i = 0; i <= 90; i += 3)
      GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
    for (i = 90; i <= 180; i += 3)
      GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D); 
    for (i = 0; i <= 90; i += 3)
      GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius); 
    for (i = 90; i <= 180; i += 3)
      GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius); 
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopAttrib();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static double getPos(double newPos, double oldPos) {
    return oldPos + (newPos - oldPos) * Module.mc.timer.renderPartialTicks;
  }
  
  public static void drawImage(int x, int y, int width, int height, ResourceLocation image, Color color) {
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glDepthMask(false);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(image);
    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
  }
  
  public static void drawImage(int x, int y, int width, int height, ResourceLocation image) {
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glDepthMask(false);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    Minecraft.getMinecraft().getTextureManager().bindTexture(image);
    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
  }
  
  public static void drawRect(double x, double y, double width, double height, Color color) {
    Gui.drawRect(x, y, x + width, y + height, color.getRGB());
  }
  
  public static void drawRect(double x, double y, double width, double height, int color) {
    Gui.drawRect(x, y, x + width, y + height, color);
  }
  
  public static void drawRect(int x, int y, int width, int height, Color color) {
    drawRect(x, y, width, height, color);
  }
  
  public static boolean glEnableBlend() {
    boolean gotEnabled = GL11.glIsEnabled(3042);
    if (!gotEnabled) {
      GL11.glEnable(3042);
      GL14.glBlendFuncSeparate(770, 771, 1, 0);
    } 
    return gotEnabled;
  }
  
  public static float getDeltaTime() {
    int targetedFps = 120;
    int fps = Minecraft.getDebugFPS();
    if (fps == 0)
      return 0.0F; 
    return targetedFps / fps;
  }
}
