package com.viaversion.viaversion.libs.kyori.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectorComponent extends BuildableComponent<SelectorComponent, SelectorComponent.Builder>, ScopedComponent<SelectorComponent> {
  @NotNull
  String pattern();
  
  @Contract(pure = true)
  @NotNull
  SelectorComponent pattern(@NotNull String paramString);
  
  @Nullable
  Component separator();
  
  @NotNull
  SelectorComponent separator(@Nullable ComponentLike paramComponentLike);
  
  public static interface SelectorComponent {}
}
