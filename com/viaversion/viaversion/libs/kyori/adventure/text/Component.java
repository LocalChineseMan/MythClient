package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translatable;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.adventure.util.IntFunction2;
import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonExtendable
public interface Component extends ComponentBuilderApplicable, ComponentLike, Examinable, HoverEventSource<Component> {
  public static final BiPredicate<? super Component, ? super Component> EQUALS = Objects::equals;
  
  public static final BiPredicate<? super Component, ? super Component> EQUALS_IDENTITY;
  
  static {
    EQUALS_IDENTITY = ((a, b) -> (a == b));
  }
  
  @NotNull
  static TextComponent empty() {
    return TextComponentImpl.EMPTY;
  }
  
  @NotNull
  static TextComponent newline() {
    return TextComponentImpl.NEWLINE;
  }
  
  @NotNull
  static TextComponent space() {
    return TextComponentImpl.SPACE;
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent join(@NotNull ComponentLike separator, @NotNull ComponentLike... components) {
    return join(separator, Arrays.asList(components));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent join(@NotNull ComponentLike separator, Iterable<? extends ComponentLike> components) {
    Iterator<? extends ComponentLike> it = components.iterator();
    if (!it.hasNext())
      return empty(); 
    TextComponent.Builder builder = text();
    while (it.hasNext()) {
      builder.append(it.next());
      if (it.hasNext())
        builder.append(separator); 
    } 
    return builder.build();
  }
  
  @NotNull
  static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent() {
    return toComponent(empty());
  }
  
  @NotNull
  static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent(@NotNull Component separator) {
    return Collector.of(Component::text, (builder, add) -> {
          if (separator != empty() && !builder.children().isEmpty())
            builder.append(separator); 
          builder.append(add);
        }(a, b) -> {
          List<Component> aChildren = a.children();
          TextComponent.Builder ret = text().append((Iterable)aChildren);
          if (!aChildren.isEmpty())
            ret.append(separator); 
          ret.append((Iterable)b.children());
          return ret;
        }ComponentBuilder::build, new Collector.Characteristics[0]);
  }
  
  @Contract(pure = true)
  static BlockNBTComponent.Builder blockNBT() {
    return (BlockNBTComponent.Builder)new BlockNBTComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static BlockNBTComponent blockNBT(@NotNull Consumer<? super BlockNBTComponent.Builder> consumer) {
    return (BlockNBTComponent)Buildable.configureAndBuild((Buildable.Builder)blockNBT(), consumer);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static BlockNBTComponent blockNBT(@NotNull String nbtPath, BlockNBTComponent.Pos pos) {
    return blockNBT(nbtPath, false, pos);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, BlockNBTComponent.Pos pos) {
    return blockNBT(nbtPath, interpret, null, pos);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, BlockNBTComponent.Pos pos) {
    return (BlockNBTComponent)new BlockNBTComponentImpl(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, pos);
  }
  
  @Contract(pure = true)
  static EntityNBTComponent.Builder entityNBT() {
    return (EntityNBTComponent.Builder)new EntityNBTComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static EntityNBTComponent entityNBT(@NotNull Consumer<? super EntityNBTComponent.Builder> consumer) {
    return (EntityNBTComponent)Buildable.configureAndBuild((Buildable.Builder)entityNBT(), consumer);
  }
  
  @Contract("_, _ -> new")
  @NotNull
  static EntityNBTComponent entityNBT(@NotNull String nbtPath, @NotNull String selector) {
    return (EntityNBTComponent)((EntityNBTComponent.Builder)entityNBT().nbtPath(nbtPath)).selector(selector).build();
  }
  
  @Contract(pure = true)
  static KeybindComponent.Builder keybind() {
    return (KeybindComponent.Builder)new KeybindComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static KeybindComponent keybind(@NotNull Consumer<? super KeybindComponent.Builder> consumer) {
    return (KeybindComponent)Buildable.configureAndBuild((Buildable.Builder)keybind(), consumer);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static KeybindComponent keybind(@NotNull String keybind) {
    return keybind(keybind, Style.empty());
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static KeybindComponent keybind(@NotNull String keybind, @NotNull Style style) {
    return (KeybindComponent)new KeybindComponentImpl(Collections.emptyList(), style, keybind);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color) {
    return keybind(keybind, Style.style(color));
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, TextDecoration... decorations) {
    return keybind(keybind, Style.style(color, decorations));
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return keybind(keybind, Style.style(color, decorations));
  }
  
  @Contract(pure = true)
  static ScoreComponent.Builder score() {
    return (ScoreComponent.Builder)new ScoreComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static ScoreComponent score(@NotNull Consumer<? super ScoreComponent.Builder> consumer) {
    return (ScoreComponent)Buildable.configureAndBuild((Buildable.Builder)score(), consumer);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static ScoreComponent score(@NotNull String name, @NotNull String objective) {
    return score(name, objective, null);
  }
  
  @Deprecated
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static ScoreComponent score(@NotNull String name, @NotNull String objective, @Nullable String value) {
    return (ScoreComponent)new ScoreComponentImpl(Collections.emptyList(), Style.empty(), name, objective, value);
  }
  
  @Contract(pure = true)
  static SelectorComponent.Builder selector() {
    return (SelectorComponent.Builder)new SelectorComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static SelectorComponent selector(@NotNull Consumer<? super SelectorComponent.Builder> consumer) {
    return (SelectorComponent)Buildable.configureAndBuild((Buildable.Builder)selector(), consumer);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static SelectorComponent selector(@NotNull String pattern) {
    return selector(pattern, null);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static SelectorComponent selector(@NotNull String pattern, @Nullable ComponentLike separator) {
    return (SelectorComponent)new SelectorComponentImpl(Collections.emptyList(), Style.empty(), pattern, separator);
  }
  
  @Contract(pure = true)
  static StorageNBTComponent.Builder storageNBT() {
    return (StorageNBTComponent.Builder)new StorageNBTComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static StorageNBTComponent storageNBT(@NotNull Consumer<? super StorageNBTComponent.Builder> consumer) {
    return (StorageNBTComponent)Buildable.configureAndBuild((Buildable.Builder)storageNBT(), consumer);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static StorageNBTComponent storageNBT(@NotNull String nbtPath, @NotNull Key storage) {
    return storageNBT(nbtPath, false, storage);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @NotNull Key storage) {
    return storageNBT(nbtPath, interpret, null, storage);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull Key storage) {
    return (StorageNBTComponent)new StorageNBTComponentImpl(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, storage);
  }
  
  @Contract(pure = true)
  static TextComponent.Builder text() {
    return new TextComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static TextComponent text(@NotNull Consumer<? super TextComponent.Builder> consumer) {
    return (TextComponent)Buildable.configureAndBuild(text(), consumer);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(@NotNull String content) {
    if (content.isEmpty())
      return empty(); 
    return new TextComponentImpl(Collections.emptyList(), Style.empty(), content);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(@NotNull String content, @NotNull Style style) {
    return new TextComponentImpl(Collections.emptyList(), style, content);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(@NotNull String content, @Nullable TextColor color) {
    return new TextComponentImpl(Collections.emptyList(), Style.style(color), content);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(@NotNull String content, @Nullable TextColor color, TextDecoration... decorations) {
    return new TextComponentImpl(Collections.emptyList(), Style.style(color, decorations), content);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(@NotNull String content, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return new TextComponentImpl(Collections.emptyList(), Style.style(color, decorations), content);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(boolean value) {
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(boolean value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(boolean value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(boolean value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(boolean value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(pure = true)
  @NotNull
  static TextComponent text(char value) {
    if (value == '\n')
      return newline(); 
    if (value == ' ')
      return space(); 
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(char value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(char value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(char value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(char value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(double value) {
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(double value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(double value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(double value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(double value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(float value) {
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(float value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(float value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(float value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(float value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(int value) {
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(int value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(int value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(int value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(int value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TextComponent text(long value) {
    return text(String.valueOf(value));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(long value, @NotNull Style style) {
    return text(String.valueOf(value), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TextComponent text(long value, @Nullable TextColor color) {
    return text(String.valueOf(value), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(long value, @Nullable TextColor color, TextDecoration... decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TextComponent text(long value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return text(String.valueOf(value), color, decorations);
  }
  
  @Contract(pure = true)
  static TranslatableComponent.Builder translatable() {
    return (TranslatableComponent.Builder)new TranslatableComponentImpl.BuilderImpl();
  }
  
  @Contract("_ -> new")
  @NotNull
  static TranslatableComponent translatable(@NotNull Consumer<? super TranslatableComponent.Builder> consumer) {
    return (TranslatableComponent)Buildable.configureAndBuild((Buildable.Builder)translatable(), consumer);
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key) {
    return translatable(key, Style.empty());
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), Style.empty());
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @NotNull Style style) {
    return (TranslatableComponent)new TranslatableComponentImpl(Collections.emptyList(), style, key, Collections.emptyList());
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color) {
    return translatable(key, Style.style(color));
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, TextDecoration... decorations) {
    return translatable(key, Style.style(color, decorations));
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, TextDecoration... decorations) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return translatable(key, Style.style(color, decorations));
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @NotNull ComponentLike... args) {
    return translatable(key, Style.empty(), args);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull ComponentLike... args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, @NotNull ComponentLike... args) {
    return (TranslatableComponent)new TranslatableComponentImpl(Collections.emptyList(), style, key, args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, @NotNull ComponentLike... args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style, args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull ComponentLike... args) {
    return translatable(key, Style.style(color), args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull ComponentLike... args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, args);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull ComponentLike... args) {
    return translatable(key, Style.style(color, decorations), args);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull ComponentLike... args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations, args);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @NotNull List<? extends ComponentLike> args) {
    return (TranslatableComponent)new TranslatableComponentImpl(Collections.emptyList(), Style.empty(), key, args);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull List<? extends ComponentLike> args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
    return (TranslatableComponent)new TranslatableComponentImpl(Collections.emptyList(), style, key, args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style, args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
    return translatable(key, Style.style(color), args);
  }
  
  @Contract(value = "_, _, _ -> new", pure = true)
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, args);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
    return translatable(key, Style.style(color, decorations), args);
  }
  
  @Contract(value = "_, _, _, _ -> new", pure = true)
  @NotNull
  static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
    return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations, args);
  }
  
  default boolean contains(@NotNull Component that) {
    return contains(that, EQUALS_IDENTITY);
  }
  
  default boolean contains(@NotNull Component that, @NotNull BiPredicate<? super Component, ? super Component> equals) {
    if (equals.test(this, that))
      return true; 
    for (Component child : children()) {
      if (child.contains(that, equals))
        return true; 
    } 
    HoverEvent<?> hoverEvent = hoverEvent();
    if (hoverEvent != null) {
      Object value = hoverEvent.value();
      Component component = null;
      if (value instanceof Component) {
        component = (Component)hoverEvent.value();
      } else if (value instanceof HoverEvent.ShowEntity) {
        component = ((HoverEvent.ShowEntity)value).name();
      } 
      if (component != null) {
        if (equals.test(that, component))
          return true; 
        for (Component child : component.children()) {
          if (child.contains(that, equals))
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  @Deprecated
  default void detectCycle(@NotNull Component that) {
    if (that.contains(this))
      throw new IllegalStateException("Component cycle detected between " + this + " and " + that); 
  }
  
  @NotNull
  default Component append(@NotNull ComponentLike component) {
    return append(component.asComponent());
  }
  
  @Contract(pure = true)
  @NotNull
  default Component append(@NotNull ComponentBuilder<?, ?> builder) {
    return append((Component)builder.build());
  }
  
  @Contract(pure = true)
  @NotNull
  default Component style(@NotNull Consumer<Style.Builder> consumer) {
    return style(style().edit(consumer));
  }
  
  @Contract(pure = true)
  @NotNull
  default Component style(@NotNull Consumer<Style.Builder> consumer, Style.Merge.Strategy strategy) {
    return style(style().edit(consumer, strategy));
  }
  
  @Contract(pure = true)
  @NotNull
  default Component style(Style.Builder style) {
    return style(style.build());
  }
  
  @Contract(pure = true)
  @NotNull
  default Component mergeStyle(@NotNull Component that) {
    return mergeStyle(that, Style.Merge.all());
  }
  
  @Contract(pure = true)
  @NotNull
  Component mergeStyle(@NotNull Component that, Style.Merge... merges) {
    return mergeStyle(that, Style.Merge.of(merges));
  }
  
  @Contract(pure = true)
  @NotNull
  default Component mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
    return style(style().merge(that.style(), merges));
  }
  
  @Nullable
  default TextColor color() {
    return style().color();
  }
  
  @Contract(pure = true)
  @NotNull
  default Component color(@Nullable TextColor color) {
    return style(style().color(color));
  }
  
  @Contract(pure = true)
  @NotNull
  default Component colorIfAbsent(@Nullable TextColor color) {
    if (color() == null)
      return color(color); 
    return this;
  }
  
  default boolean hasDecoration(@NotNull TextDecoration decoration) {
    return (decoration(decoration) == TextDecoration.State.TRUE);
  }
  
  @Contract(pure = true)
  @NotNull
  default Component decorate(@NotNull TextDecoration decoration) {
    return decoration(decoration, TextDecoration.State.TRUE);
  }
  
  default TextDecoration.State decoration(@NotNull TextDecoration decoration) {
    return style().decoration(decoration);
  }
  
  @Contract(pure = true)
  @NotNull
  default Component decoration(@NotNull TextDecoration decoration, boolean flag) {
    return decoration(decoration, TextDecoration.State.byBoolean(flag));
  }
  
  @Contract(pure = true)
  @NotNull
  default Component decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    return style(style().decoration(decoration, state));
  }
  
  @NotNull
  default Map<TextDecoration, TextDecoration.State> decorations() {
    return style().decorations();
  }
  
  @Contract(pure = true)
  @NotNull
  default Component decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    return style(style().decorations(decorations));
  }
  
  @Nullable
  default ClickEvent clickEvent() {
    return style().clickEvent();
  }
  
  @Contract(pure = true)
  @NotNull
  default Component clickEvent(@Nullable ClickEvent event) {
    return style(style().clickEvent(event));
  }
  
  @Nullable
  default HoverEvent<?> hoverEvent() {
    return style().hoverEvent();
  }
  
  @Contract(pure = true)
  @NotNull
  default Component hoverEvent(@Nullable HoverEventSource<?> source) {
    return style(style().hoverEvent(source));
  }
  
  @Nullable
  default String insertion() {
    return style().insertion();
  }
  
  @Contract(pure = true)
  @NotNull
  default Component insertion(@Nullable String insertion) {
    return style(style().insertion(insertion));
  }
  
  default boolean hasStyling() {
    return !style().isEmpty();
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement) {
    return replaceText(b -> b.matchLiteral(search).replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
    return replaceText(b -> b.match(pattern).replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceFirstText(@NotNull String search, @Nullable ComponentLike replacement) {
    return replaceText(b -> b.matchLiteral(search).once().replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceFirstText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
    return replaceText(b -> b.match(pattern).once().replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, int numberOfReplacements) {
    return replaceText(b -> b.matchLiteral(search).times(numberOfReplacements).replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement, int numberOfReplacements) {
    return replaceText(b -> b.match(pattern).times(numberOfReplacements).replacement(replacement));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
    return replaceText(b -> b.matchLiteral(search).replacement(replacement).condition(fn));
  }
  
  @Deprecated
  @ScheduledForRemoval
  @Contract(pure = true)
  @NotNull
  default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
    return replaceText(b -> b.match(pattern).replacement(replacement).condition(fn));
  }
  
  default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
    component.append(this);
  }
  
  @NotNull
  default Component asComponent() {
    return this;
  }
  
  @NotNull
  default HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
    return HoverEvent.showText(op.apply(this));
  }
  
  @NotNull
  List<Component> children();
  
  @Contract(pure = true)
  @NotNull
  Component children(@NotNull List<? extends ComponentLike> paramList);
  
  @Contract(pure = true)
  @NotNull
  Component append(@NotNull Component paramComponent);
  
  @NotNull
  Style style();
  
  @Contract(pure = true)
  @NotNull
  Component style(@NotNull Style paramStyle);
  
  @Contract(pure = true)
  @NotNull
  Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> paramConsumer);
  
  @Contract(pure = true)
  @NotNull
  Component replaceText(@NotNull TextReplacementConfig paramTextReplacementConfig);
}
