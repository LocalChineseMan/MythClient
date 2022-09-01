package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import java.util.Arrays;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TextComponent extends BuildableComponent<TextComponent, TextComponent.Builder>, ScopedComponent<TextComponent> {
  @NotNull
  static TextComponent ofChildren(@NotNull ComponentLike... components) {
    if (components.length == 0)
      return Component.empty(); 
    return new TextComponentImpl(Arrays.asList(components), Style.empty(), "");
  }
  
  @NotNull
  String content();
  
  @Contract(pure = true)
  @NotNull
  TextComponent content(@NotNull String paramString);
  
  public static interface Builder extends ComponentBuilder<TextComponent, Builder> {
    @NotNull
    String content();
    
    @Contract("_ -> this")
    @NotNull
    Builder content(@NotNull String param1String);
  }
}
