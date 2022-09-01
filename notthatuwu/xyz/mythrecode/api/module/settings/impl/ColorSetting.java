package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import java.awt.Color;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class ColorSetting extends Setting {
  private Color color;
  
  private float hue;
  
  private float saturation;
  
  private float brightness;
  
  public ColorSetting(String name, Module parent, Color defaultColor) {
    super(name, parent);
    setColor(defaultColor);
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public ColorSetting(String name, Module parent, Color defaultColor, Supplier<Boolean> supplier) {
    super(name, parent);
    setColor(defaultColor);
    setVisible(supplier);
  }
  
  public Color getValue() {
    return Color.getHSBColor(this.hue, this.saturation, this.brightness);
  }
  
  public void setValue(int hex) {
    float[] hsb = getHSBFromColor(hex);
    this.hue = hsb[0];
    this.saturation = hsb[1];
    this.brightness = hsb[2];
  }
  
  public int getColor() {
    return getValue().getRGB();
  }
  
  public int getRed() {
    return getValue().getRed();
  }
  
  public int getGreen() {
    return getValue().getGreen();
  }
  
  public int getBlue() {
    return getValue().getBlue();
  }
  
  public void setColor(Color color) {
    float[] colors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    this.hue = colors[0];
    this.saturation = colors[1];
    this.brightness = colors[2];
  }
  
  public float getSaturation() {
    return this.saturation;
  }
  
  public void setSaturation(float saturation) {
    this.saturation = saturation;
  }
  
  public float getBrightness() {
    return this.brightness;
  }
  
  public void setBrightness(float brightness) {
    this.brightness = brightness;
  }
  
  public float getHue() {
    return this.hue;
  }
  
  public void setHue(float hue) {
    this.hue = hue;
  }
  
  public float[] getHSBFromColor(int hex) {
    int r = hex >> 16 & 0xFF;
    int g = hex >> 8 & 0xFF;
    int b = hex & 0xFF;
    return Color.RGBtoHSB(r, g, b, null);
  }
}
