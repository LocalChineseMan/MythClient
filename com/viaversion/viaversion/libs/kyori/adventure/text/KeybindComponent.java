package com.viaversion.viaversion.libs.kyori.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface KeybindComponent extends BuildableComponent<KeybindComponent, KeybindComponent.Builder>, ScopedComponent<KeybindComponent> {
  @NotNull
  String keybind();
  
  @Contract(pure = true)
  @NotNull
  KeybindComponent keybind(@NotNull String paramString);
  
  public static interface KeybindComponent {}
}
