package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonExtendable
public interface Style extends Buildable<Style, Style.Builder>, Examinable {
  public static final Key DEFAULT_FONT = Key.key("default");
  
  @NotNull
  static Style empty() {
    return StyleImpl.EMPTY;
  }
  
  @NotNull
  static Builder style() {
    return new StyleImpl.BuilderImpl();
  }
  
  @NotNull
  static Style style(@NotNull Consumer<Builder> consumer) {
    return (Style)Buildable.configureAndBuild(style(), consumer);
  }
  
  @NotNull
  static Style style(@Nullable TextColor color) {
    if (color == null)
      return empty(); 
    return new StyleImpl(null, color, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
  }
  
  @NotNull
  static Style style(@NotNull TextDecoration decoration) {
    return style().decoration(decoration, true).build();
  }
  
  @NotNull
  static Style style(@Nullable TextColor color, TextDecoration... decorations) {
    Builder builder = style();
    builder.color(color);
    StyleImpl.decorate(builder, decorations);
    return builder.build();
  }
  
  @NotNull
  static Style style(@Nullable TextColor color, Set<TextDecoration> decorations) {
    Builder builder = style();
    builder.color(color);
    if (!decorations.isEmpty())
      for (TextDecoration decoration : decorations)
        builder.decoration(decoration, true);  
    return builder.build();
  }
  
  @NotNull
  static Style style(StyleBuilderApplicable... applicables) {
    if (applicables.length == 0)
      return empty(); 
    Builder builder = style();
    for (int i = 0, length = applicables.length; i < length; i++)
      applicables[i].styleApply(builder); 
    return builder.build();
  }
  
  @NotNull
  static Style style(@NotNull Iterable<? extends StyleBuilderApplicable> applicables) {
    Builder builder = style();
    for (StyleBuilderApplicable applicable : applicables)
      applicable.styleApply(builder); 
    return builder.build();
  }
  
  @NotNull
  default Style edit(@NotNull Consumer<Builder> consumer) {
    return edit(consumer, Merge.Strategy.ALWAYS);
  }
  
  @NotNull
  default Style edit(@NotNull Consumer<Builder> consumer, Merge.Strategy strategy) {
    return style(style -> {
          if (strategy == Merge.Strategy.ALWAYS)
            style.merge(this, strategy); 
          consumer.accept(style);
          if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET)
            style.merge(this, strategy); 
        });
  }
  
  default boolean hasDecoration(@NotNull TextDecoration decoration) {
    return (decoration(decoration) == TextDecoration.State.TRUE);
  }
  
  @NotNull
  default Style decorate(@NotNull TextDecoration decoration) {
    return decoration(decoration, TextDecoration.State.TRUE);
  }
  
  @NotNull
  default Style decoration(@NotNull TextDecoration decoration, boolean flag) {
    return decoration(decoration, TextDecoration.State.byBoolean(flag));
  }
  
  @NotNull
  default Style merge(@NotNull Style that) {
    return merge(that, Merge.all());
  }
  
  @NotNull
  default Style merge(@NotNull Style that, Merge.Strategy strategy) {
    return merge(that, strategy, Merge.all());
  }
  
  @NotNull
  default Style merge(@NotNull Style that, @NotNull Merge merge) {
    return merge(that, Collections.singleton(merge));
  }
  
  @NotNull
  default Style merge(@NotNull Style that, Merge.Strategy strategy, @NotNull Merge merge) {
    return merge(that, strategy, Collections.singleton(merge));
  }
  
  @NotNull
  Style merge(@NotNull Style that, @NotNull Merge... merges) {
    return merge(that, Merge.of(merges));
  }
  
  @NotNull
  Style merge(@NotNull Style that, Merge.Strategy strategy, @NotNull Merge... merges) {
    return merge(that, strategy, Merge.of(merges));
  }
  
  @NotNull
  default Style merge(@NotNull Style that, @NotNull Set<Merge> merges) {
    return merge(that, Merge.Strategy.ALWAYS, merges);
  }
  
  @Nullable
  Key font();
  
  @NotNull
  Style font(@Nullable Key paramKey);
  
  @Nullable
  TextColor color();
  
  @NotNull
  Style color(@Nullable TextColor paramTextColor);
  
  @NotNull
  Style colorIfAbsent(@Nullable TextColor paramTextColor);
  
  TextDecoration.State decoration(@NotNull TextDecoration paramTextDecoration);
  
  @NotNull
  Style decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @NotNull
  Map<TextDecoration, TextDecoration.State> decorations();
  
  @NotNull
  Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> paramMap);
  
  @Nullable
  ClickEvent clickEvent();
  
  @NotNull
  Style clickEvent(@Nullable ClickEvent paramClickEvent);
  
  @Nullable
  HoverEvent<?> hoverEvent();
  
  @NotNull
  Style hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
  
  @Nullable
  String insertion();
  
  @NotNull
  Style insertion(@Nullable String paramString);
  
  @NotNull
  Style merge(@NotNull Style paramStyle, Merge.Strategy paramStrategy, @NotNull Set<Merge> paramSet);
  
  boolean isEmpty();
  
  @NotNull
  Builder toBuilder();
  
  public static interface Builder extends Buildable.Builder<Style> {
    @Contract("_ -> this")
    @NotNull
    default Builder decorate(@NotNull TextDecoration decoration) {
      return decoration(decoration, TextDecoration.State.TRUE);
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder decorate(@NotNull TextDecoration... decorations) {
      for (int i = 0, length = decorations.length; i < length; i++)
        decorate(decorations[i]); 
      return this;
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder decoration(@NotNull TextDecoration decoration, boolean flag) {
      return decoration(decoration, TextDecoration.State.byBoolean(flag));
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder merge(@NotNull Style that) {
      return merge(that, Style.Merge.all());
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder merge(@NotNull Style that, Style.Merge.Strategy strategy) {
      return merge(that, strategy, Style.Merge.all());
    }
    
    @Contract("_, _ -> this")
    @NotNull
    Builder merge(@NotNull Style that, @NotNull Style.Merge... merges) {
      if (merges.length == 0)
        return this; 
      return merge(that, Style.Merge.of(merges));
    }
    
    @Contract("_, _, _ -> this")
    @NotNull
    Builder merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Style.Merge... merges) {
      if (merges.length == 0)
        return this; 
      return merge(that, strategy, Style.Merge.of(merges));
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder merge(@NotNull Style that, @NotNull Set<Style.Merge> merges) {
      return merge(that, Style.Merge.Strategy.ALWAYS, merges);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder apply(@NotNull StyleBuilderApplicable applicable) {
      applicable.styleApply(this);
      return this;
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder font(@Nullable Key param1Key);
    
    @Contract("_ -> this")
    @NotNull
    Builder color(@Nullable TextColor param1TextColor);
    
    @Contract("_ -> this")
    @NotNull
    Builder colorIfAbsent(@Nullable TextColor param1TextColor);
    
    @Contract("_, _ -> this")
    @NotNull
    Builder decoration(@NotNull TextDecoration param1TextDecoration, TextDecoration.State param1State);
    
    @Contract("_ -> this")
    @NotNull
    Builder clickEvent(@Nullable ClickEvent param1ClickEvent);
    
    @Contract("_ -> this")
    @NotNull
    Builder hoverEvent(@Nullable HoverEventSource<?> param1HoverEventSource);
    
    @Contract("_ -> this")
    @NotNull
    Builder insertion(@Nullable String param1String);
    
    @Contract("_, _, _ -> this")
    @NotNull
    Builder merge(@NotNull Style param1Style, Style.Merge.Strategy param1Strategy, @NotNull Set<Style.Merge> param1Set);
    
    @NotNull
    Style build();
  }
  
  public enum Style {
  
  }
  
  public enum Style {
  
  }
}
