package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends Buildable.Builder<C>, ComponentBuilderApplicable, ComponentLike {
  @Contract("_ -> this")
  @NotNull
  default B append(@NotNull ComponentLike component) {
    return append(component.asComponent());
  }
  
  @Contract("_ -> this")
  @NotNull
  default B append(@NotNull ComponentBuilder<?, ?> builder) {
    return append((Component)builder.build());
  }
  
  @Contract("_ -> this")
  @NotNull
  default B apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
    consumer.accept(this);
    return (B)this;
  }
  
  @Contract("_, _ -> this")
  @NotNull
  default B decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
    TextDecoration.State state = TextDecoration.State.byBoolean(flag);
    decorations.forEach(decoration -> decoration(decoration, state));
    return (B)this;
  }
  
  @Contract("_ -> this")
  @NotNull
  default B decorate(@NotNull TextDecoration decoration) {
    return decoration(decoration, TextDecoration.State.TRUE);
  }
  
  @Contract("_ -> this")
  @NotNull
  B decorate(@NotNull TextDecoration... decorations) {
    for (int i = 0, length = decorations.length; i < length; i++)
      decorate(decorations[i]); 
    return (B)this;
  }
  
  @Contract("_, _ -> this")
  @NotNull
  default B decoration(@NotNull TextDecoration decoration, boolean flag) {
    return decoration(decoration, TextDecoration.State.byBoolean(flag));
  }
  
  @Contract("_ -> this")
  @NotNull
  default B mergeStyle(@NotNull Component that) {
    return mergeStyle(that, Style.Merge.all());
  }
  
  @Contract("_, _ -> this")
  @NotNull
  B mergeStyle(@NotNull Component that, Style.Merge... merges) {
    return mergeStyle(that, Style.Merge.of(merges));
  }
  
  @Contract("_ -> this")
  @NotNull
  default B applicableApply(@NotNull ComponentBuilderApplicable applicable) {
    applicable.componentBuilderApply(this);
    return (B)this;
  }
  
  default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
    component.append(this);
  }
  
  @NotNull
  default Component asComponent() {
    return (Component)build();
  }
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Component paramComponent);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Component... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull ComponentLike... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Iterable<? extends ComponentLike> paramIterable);
  
  @Contract("_ -> this")
  @NotNull
  B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> paramConsumer);
  
  @Contract("_ -> this")
  @NotNull
  B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
  
  @Contract("_ -> this")
  @NotNull
  B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
  
  @NotNull
  List<Component> children();
  
  @Contract("_ -> this")
  @NotNull
  B style(@NotNull Style paramStyle);
  
  @Contract("_ -> this")
  @NotNull
  B style(@NotNull Consumer<Style.Builder> paramConsumer);
  
  @Contract("_ -> this")
  @NotNull
  B font(@Nullable Key paramKey);
  
  @Contract("_ -> this")
  @NotNull
  B color(@Nullable TextColor paramTextColor);
  
  @Contract("_ -> this")
  @NotNull
  B colorIfAbsent(@Nullable TextColor paramTextColor);
  
  @Contract("_, _ -> this")
  @NotNull
  B decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @Contract("_ -> this")
  @NotNull
  B clickEvent(@Nullable ClickEvent paramClickEvent);
  
  @Contract("_ -> this")
  @NotNull
  B hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
  
  @Contract("_ -> this")
  @NotNull
  B insertion(@Nullable String paramString);
  
  @Contract("_, _ -> this")
  @NotNull
  B mergeStyle(@NotNull Component paramComponent, @NotNull Set<Style.Merge> paramSet);
  
  @NotNull
  B resetStyle();
  
  @NotNull
  C build();
}
