package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;

public interface RGBLike {
  int red();
  
  int green();
  
  int blue();
  
  @NotNull
  default HSVLike asHSV() {
    return HSVLike.fromRGB(red(), green(), blue());
  }
}
