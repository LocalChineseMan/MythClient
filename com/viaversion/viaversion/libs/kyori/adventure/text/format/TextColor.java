package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.util.HSVLike;
import com.viaversion.viaversion.libs.kyori.adventure.util.RGBLike;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TextColor extends Comparable<TextColor>, Examinable, RGBLike, StyleBuilderApplicable, TextFormat {
  @NotNull
  static TextColor color(int value) {
    int truncatedValue = value & 0xFFFFFF;
    NamedTextColor named = NamedTextColor.ofExact(truncatedValue);
    return (named != null) ? named : (TextColor)new TextColorImpl(truncatedValue);
  }
  
  @NotNull
  static TextColor color(@NotNull RGBLike rgb) {
    return color(rgb.red(), rgb.green(), rgb.blue());
  }
  
  @NotNull
  static TextColor color(@NotNull HSVLike hsv) {
    float s = hsv.s();
    float v = hsv.v();
    if (s == 0.0F)
      return color(v, v, v); 
    float h = hsv.h() * 6.0F;
    int i = (int)Math.floor(h);
    float f = h - i;
    float p = v * (1.0F - s);
    float q = v * (1.0F - s * f);
    float t = v * (1.0F - s * (1.0F - f));
    if (i == 0)
      return color(v, t, p); 
    if (i == 1)
      return color(q, v, p); 
    if (i == 2)
      return color(p, v, t); 
    if (i == 3)
      return color(p, q, v); 
    if (i == 4)
      return color(t, p, v); 
    return color(v, p, q);
  }
  
  @NotNull
  static TextColor color(int r, int g, int b) {
    return color((r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
  }
  
  @NotNull
  static TextColor color(float r, float g, float b) {
    return color((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
  }
  
  @Nullable
  static TextColor fromHexString(@NotNull String string) {
    if (string.startsWith("#"))
      try {
        int hex = Integer.parseInt(string.substring(1), 16);
        return color(hex);
      } catch (NumberFormatException e) {
        return null;
      }  
    return null;
  }
  
  @Nullable
  static TextColor fromCSSHexString(@NotNull String string) {
    if (string.startsWith("#")) {
      int hex;
      String hexString = string.substring(1);
      if (hexString.length() != 3 && hexString.length() != 6)
        return null; 
      try {
        hex = Integer.parseInt(hexString, 16);
      } catch (NumberFormatException e) {
        return null;
      } 
      if (hexString.length() == 6)
        return color(hex); 
      int red = (hex & 0xF00) >> 8 | (hex & 0xF00) >> 4;
      int green = (hex & 0xF0) >> 4 | hex & 0xF0;
      int blue = (hex & 0xF) << 4 | hex & 0xF;
      return color(red, green, blue);
    } 
    return null;
  }
  
  @NotNull
  default String asHexString() {
    return String.format("#%06x", new Object[] { Integer.valueOf(value()) });
  }
  
  default int red() {
    return value() >> 16 & 0xFF;
  }
  
  default int green() {
    return value() >> 8 & 0xFF;
  }
  
  default int blue() {
    return value() & 0xFF;
  }
  
  @NotNull
  static TextColor lerp(float t, @NotNull RGBLike a, @NotNull RGBLike b) {
    float clampedT = Math.min(1.0F, Math.max(0.0F, t));
    int ar = a.red();
    int br = b.red();
    int ag = a.green();
    int bg = b.green();
    int ab = a.blue();
    int bb = b.blue();
    return color(
        Math.round(ar + clampedT * (br - ar)), 
        Math.round(ag + clampedT * (bg - ag)), 
        Math.round(ab + clampedT * (bb - ab)));
  }
  
  default void styleApply(Style.Builder style) {
    style.color(this);
  }
  
  default int compareTo(TextColor that) {
    return Integer.compare(value(), that.value());
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("value", asHexString()));
  }
  
  int value();
}
