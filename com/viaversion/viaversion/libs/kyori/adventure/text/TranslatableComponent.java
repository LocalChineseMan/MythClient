package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.translation.Translatable;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TranslatableComponent extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, ScopedComponent<TranslatableComponent> {
  @NotNull
  String key();
  
  @Contract(pure = true)
  @NotNull
  default TranslatableComponent key(@NotNull Translatable translatable) {
    return key(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey());
  }
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent key(@NotNull String paramString);
  
  @NotNull
  List<Component> args();
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent args(@NotNull ComponentLike... paramVarArgs);
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent args(@NotNull List<? extends ComponentLike> paramList);
  
  public static interface TranslatableComponent {}
}
