package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Gui {
  public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
  
  public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
  
  public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
  
  protected static float zLevel;
  
  protected void drawHorizontalLine(int startX, int endX, int y, int color) {
    if (endX < startX) {
      int i = startX;
      startX = endX;
      endX = i;
    } 
    drawRect(startX, y, (endX + 1), (y + 1), color);
  }
  
  protected void drawVerticalLine(int x, int startY, int endY, int color) {
    if (endY < startY) {
      int i = startY;
      startY = endY;
      endY = i;
    } 
    drawRect(x, (startY + 1), (x + 1), endY, color);
  }
  
  public static void drawRect(double x, double y, double d, double e, int color) {
    if (x < d) {
      double i = x;
      x = d;
      d = i;
    } 
    if (y < e) {
      double j = y;
      y = e;
      e = j;
    } 
    float f3 = (color >> 24 & 0xFF) / 255.0F;
    float f = (color >> 16 & 0xFF) / 255.0F;
    float f1 = (color >> 8 & 0xFF) / 255.0F;
    float f2 = (color & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(f, f1, f2, f3);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(x, e, 0.0D).endVertex();
    worldrenderer.pos(d, e, 0.0D).endVertex();
    worldrenderer.pos(d, y, 0.0D).endVertex();
    worldrenderer.pos(x, y, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static Gui gui = new Gui();
  
  public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    float f = (startColor >> 24 & 0xFF) / 255.0F;
    float f1 = (startColor >> 16 & 0xFF) / 255.0F;
    float f2 = (startColor >> 8 & 0xFF) / 255.0F;
    float f3 = (startColor & 0xFF) / 255.0F;
    float f4 = (endColor >> 24 & 0xFF) / 255.0F;
    float f5 = (endColor >> 16 & 0xFF) / 255.0F;
    float f6 = (endColor >> 8 & 0xFF) / 255.0F;
    float f7 = (endColor & 0xFF) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
    worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
    worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
    worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
  
  public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
    fontRendererIn.drawStringWithShadow(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
  }
  
  public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
    fontRendererIn.drawStringWithShadow(text, x, y, color);
  }
  
  public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    this;
    worldrenderer.pos((x + 0), (y + height), zLevel).tex(((textureX + 0) * f), ((textureY + height) * f1)).endVertex();
    this;
    worldrenderer.pos((x + width), (y + height), zLevel).tex(((textureX + width) * f), ((textureY + height) * f1)).endVertex();
    this;
    worldrenderer.pos((x + width), (y + 0), zLevel).tex(((textureX + width) * f), ((textureY + 0) * f1)).endVertex();
    this;
    worldrenderer.pos((x + 0), (y + 0), zLevel).tex(((textureX + 0) * f), ((textureY + 0) * f1)).endVertex();
    tessellator.draw();
  }
  
  public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    this;
    worldrenderer.pos((xCoord + 0.0F), (yCoord + maxV), zLevel).tex(((minU + 0) * f), ((minV + maxV) * f1)).endVertex();
    this;
    worldrenderer.pos((xCoord + maxU), (yCoord + maxV), zLevel).tex(((minU + maxU) * f), ((minV + maxV) * f1)).endVertex();
    this;
    worldrenderer.pos((xCoord + maxU), (yCoord + 0.0F), zLevel).tex(((minU + maxU) * f), ((minV + 0) * f1)).endVertex();
    this;
    worldrenderer.pos((xCoord + 0.0F), (yCoord + 0.0F), zLevel).tex(((minU + 0) * f), ((minV + 0) * f1)).endVertex();
    tessellator.draw();
  }
  
  public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    this;
    worldrenderer.pos((xCoord + 0), (yCoord + heightIn), zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
    this;
    worldrenderer.pos((xCoord + widthIn), (yCoord + heightIn), zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
    this;
    worldrenderer.pos((xCoord + widthIn), (yCoord + 0), zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
    this;
    worldrenderer.pos((xCoord + 0), (yCoord + 0), zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
    tessellator.draw();
  }
  
  public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
    float f = 1.0F / textureWidth;
    float f1 = 1.0F / textureHeight;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(x, (y + height), 0.0D).tex((u * f), ((v + height) * f1)).endVertex();
    worldrenderer.pos((x + width), (y + height), 0.0D).tex(((u + width) * f), ((v + height) * f1)).endVertex();
    worldrenderer.pos((x + width), y, 0.0D).tex(((u + width) * f), (v * f1)).endVertex();
    worldrenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }
  
  public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
    float f = (col1 >> 24 & 0xFF) / 255.0F;
    float f2 = (col1 >> 16 & 0xFF) / 255.0F;
    float f3 = (col1 >> 8 & 0xFF) / 255.0F;
    float f4 = (col1 & 0xFF) / 255.0F;
    float f5 = (col2 >> 24 & 0xFF) / 255.0F;
    float f6 = (col2 >> 16 & 0xFF) / 255.0F;
    float f7 = (col2 >> 8 & 0xFF) / 255.0F;
    float f8 = (col2 & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glShadeModel(7425);
    GL11.glPushMatrix();
    GL11.glBegin(7);
    GL11.glColor4f(f2, f3, f4, f);
    GL11.glVertex2d(left, top);
    GL11.glVertex2d(left, bottom);
    GL11.glColor4f(f6, f7, f8, f5);
    GL11.glVertex2d(right, bottom);
    GL11.glVertex2d(right, top);
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
    GL11.glShadeModel(7424);
  }
  
  public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
    float f = 1.0F / tileWidth;
    float f1 = 1.0F / tileHeight;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(x, (y + height), 0.0D).tex((u * f), ((v + vHeight) * f1)).endVertex();
    worldrenderer.pos((x + width), (y + height), 0.0D).tex(((u + uWidth) * f), ((v + vHeight) * f1)).endVertex();
    worldrenderer.pos((x + width), y, 0.0D).tex(((u + uWidth) * f), (v * f1)).endVertex();
    worldrenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }
}
