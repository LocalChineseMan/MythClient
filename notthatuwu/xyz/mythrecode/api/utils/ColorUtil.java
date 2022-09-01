package notthatuwu.xyz.mythrecode.api.utils;

import java.awt.Color;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.modules.display.HUD;
import org.lwjgl.opengl.GL11;

public class ColorUtil {
  public static double rainbowState;
  
  public static int getColor(String mode, int delay) {
    int color = 0;
    switch (mode) {
      case "Myth":
        color = mythColor(delay);
        break;
      case "Rainbow":
        color = rainbow(delay).getRGB();
        break;
      case "Client":
        color = getClientColor(delay);
        break;
    } 
    return color;
  }
  
  public static int getClientColor(float counter) {
    HUD hud = (HUD)Client.INSTANCE.moduleManager.getModuleByClass(HUD.class);
    float brightness = 1.0F - MathHelper.abs(MathHelper.sin((float)((counter % 6000.0F / 6000.0F) * Math.PI * 2.0D)) * 0.6F);
    float[] hsb = hud.colorSetting.getHSBFromColor(hud.colorSetting.getColor());
    Color color = Color.getHSBColor(hsb[0], hsb[1], brightness);
    return (new Color(color.getRed(), color.getGreen(), color.getBlue(), 250)).getRGB();
  }
  
  public static Color blend(Color color1, Color color2, double ratio) {
    float r = (float)ratio;
    float ir = 1.0F - r;
    float[] rgb1 = color1.getColorComponents(new float[3]);
    float[] rgb2 = color2.getColorComponents(new float[3]);
    float red = rgb1[0] * r + rgb2[0] * ir;
    float green = rgb1[1] * r + rgb2[1] * ir;
    float blue = rgb1[2] * r + rgb2[2] * ir;
    if (red < 0.0F) {
      red = 0.0F;
    } else if (red > 255.0F) {
      red = 255.0F;
    } 
    if (green < 0.0F) {
      green = 0.0F;
    } else if (green > 255.0F) {
      green = 255.0F;
    } 
    if (blue < 0.0F) {
      blue = 0.0F;
    } else if (blue > 255.0F) {
      blue = 255.0F;
    } 
    Color color3 = null;
    try {
      color3 = new Color(red, green, blue);
    } catch (IllegalArgumentException illegalArgumentException) {}
    return color3;
  }
  
  public static int mythColor(int delay) {
    float speed = 3200.0F;
    float hue = (float)(System.currentTimeMillis() % (int)speed) + (delay / 2);
    while (hue > speed)
      hue -= speed; 
    hue /= speed;
    if (hue > 0.5D)
      hue = 0.5F - hue - 0.5F; 
    hue += 0.5F;
    return Color.HSBtoRGB(hue, 0.5F, 1.0F);
  }
  
  public static Color rainbow(int delay) {
    rainbowState = Math.ceil(((System.currentTimeMillis() + delay) / 75L));
    rainbowState %= 90.0D;
    return Color.getHSBColor((float)(rainbowState / 45.0D), 0.5F, 1.0F);
  }
  
  public static Color gatoPulseBrightness(Color color, int index, int count) {
    float[] hsb = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
    float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
    brightness = 0.5F + 0.5F * brightness;
    return new Color(Color.HSBtoRGB(hsb[0], hsb[1], brightness % 2.0F));
  }
  
  public static int getHealthColor(EntityLivingBase player) {
    float f = player.getHealth();
    float f2 = player.getMaxHealth();
    float f3 = Math.max(0.0F, Math.min(f, f2) / f2);
    return Color.HSBtoRGB(f3 / 3.0F, 1.0F, 0.75F) | 0xFF000000;
  }
  
  public static String translateColor(String original) {
    return original.replaceAll("&([0-9a-fk-or])", "§$1");
  }
  
  public static float[] toGLColor(int color) {
    float f = (color >> 16 & 0xFF) / 255.0F;
    float f1 = (color >> 8 & 0xFF) / 255.0F;
    float f2 = (color & 0xFF) / 255.0F;
    float f3 = (color >> 24 & 0xFF) / 255.0F;
    return new float[] { f, f1, f2, f3 };
  }
  
  public static void doColor(int color) {
    float[] rgba = toGLColor(color);
    GL11.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
  }
}
