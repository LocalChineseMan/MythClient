package com.viaversion.viaversion.libs.kyori.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScoreComponent extends BuildableComponent<ScoreComponent, ScoreComponent.Builder>, ScopedComponent<ScoreComponent> {
  @NotNull
  String name();
  
  @Contract(pure = true)
  @NotNull
  ScoreComponent name(@NotNull String paramString);
  
  @NotNull
  String objective();
  
  @Contract(pure = true)
  @NotNull
  ScoreComponent objective(@NotNull String paramString);
  
  @Deprecated
  @Nullable
  String value();
  
  @Deprecated
  @Contract(pure = true)
  @NotNull
  ScoreComponent value(@Nullable String paramString);
  
  public static interface ScoreComponent {}
}
