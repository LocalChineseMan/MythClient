package com.viaversion.viaversion.libs.kyori.adventure.text.serializer;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentSerializer<I extends com.viaversion.viaversion.libs.kyori.adventure.text.Component, O extends com.viaversion.viaversion.libs.kyori.adventure.text.Component, R> {
  @NotNull
  O deserialize(@NotNull R paramR);
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default O deseializeOrNull(@Nullable R input) {
    return deserializeOrNull(input);
  }
  
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default O deserializeOrNull(@Nullable R input) {
    return deserializeOr(input, null);
  }
  
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  @Nullable
  default O deserializeOr(@Nullable R input, @Nullable O fallback) {
    if (input == null)
      return fallback; 
    return deserialize(input);
  }
  
  @NotNull
  R serialize(@NotNull I paramI);
  
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default R serializeOrNull(@Nullable I component) {
    return serializeOr(component, null);
  }
  
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  @Nullable
  default R serializeOr(@Nullable I component, @Nullable R fallback) {
    if (component == null)
      return fallback; 
    return serialize(component);
  }
}
