package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.MonkeyBars;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScopedComponent<C extends Component> extends Component {
  @NotNull
  C children(@NotNull List<? extends ComponentLike> paramList);
  
  @NotNull
  C style(@NotNull Style paramStyle);
  
  @NotNull
  default C style(@NotNull Consumer<Style.Builder> style) {
    return (C)super.style(style);
  }
  
  @NotNull
  default C style(Style.Builder style) {
    return (C)super.style(style);
  }
  
  @NotNull
  default C mergeStyle(@NotNull Component that) {
    return (C)super.mergeStyle(that);
  }
  
  @NotNull
  C mergeStyle(@NotNull Component that, Style.Merge... merges) {
    return (C)super.mergeStyle(that, merges);
  }
  
  @NotNull
  default C append(@NotNull Component component) {
    if (component == Component.empty())
      return (C)this; 
    List<Component> oldChildren = children();
    return children(MonkeyBars.addOne(oldChildren, component));
  }
  
  @NotNull
  default C append(@NotNull ComponentLike component) {
    return (C)super.append(component);
  }
  
  @NotNull
  default C append(@NotNull ComponentBuilder<?, ?> builder) {
    return (C)super.append(builder);
  }
  
  @NotNull
  default C mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
    return (C)super.mergeStyle(that, merges);
  }
  
  @NotNull
  default C color(@Nullable TextColor color) {
    return (C)super.color(color);
  }
  
  @NotNull
  default C colorIfAbsent(@Nullable TextColor color) {
    return (C)super.colorIfAbsent(color);
  }
  
  @NotNull
  default Component decorate(@NotNull TextDecoration decoration) {
    return super.decorate(decoration);
  }
  
  @NotNull
  default C decoration(@NotNull TextDecoration decoration, boolean flag) {
    return (C)super.decoration(decoration, flag);
  }
  
  @NotNull
  default C decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    return (C)super.decoration(decoration, state);
  }
  
  @NotNull
  default C clickEvent(@Nullable ClickEvent event) {
    return (C)super.clickEvent(event);
  }
  
  @NotNull
  default C hoverEvent(@Nullable HoverEventSource<?> event) {
    return (C)super.hoverEvent(event);
  }
  
  @NotNull
  default C insertion(@Nullable String insertion) {
    return (C)super.insertion(insertion);
  }
}
