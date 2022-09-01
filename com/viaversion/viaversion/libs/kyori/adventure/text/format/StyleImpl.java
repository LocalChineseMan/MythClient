package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class StyleImpl implements Style {
  static final StyleImpl EMPTY = new StyleImpl(null, null, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
  
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();
  
  @Nullable
  private final Key font;
  
  @Nullable
  private final TextColor color;
  
  private final TextDecoration.State obfuscated;
  
  private final TextDecoration.State bold;
  
  private final TextDecoration.State strikethrough;
  
  private final TextDecoration.State underlined;
  
  private final TextDecoration.State italic;
  
  @Nullable
  private final ClickEvent clickEvent;
  
  @Nullable
  private final HoverEvent<?> hoverEvent;
  
  @Nullable
  private final String insertion;
  
  static void decorate(Style.Builder builder, TextDecoration[] decorations) {
    for (int i = 0, length = decorations.length; i < length; i++) {
      TextDecoration decoration = decorations[i];
      builder.decoration(decoration, true);
    } 
  }
  
  StyleImpl(@Nullable Key font, @Nullable TextColor color, TextDecoration.State obfuscated, TextDecoration.State bold, TextDecoration.State strikethrough, TextDecoration.State underlined, TextDecoration.State italic, @Nullable ClickEvent clickEvent, @Nullable HoverEvent<?> hoverEvent, @Nullable String insertion) {
    this.font = font;
    this.color = color;
    this.obfuscated = obfuscated;
    this.bold = bold;
    this.strikethrough = strikethrough;
    this.underlined = underlined;
    this.italic = italic;
    this.clickEvent = clickEvent;
    this.hoverEvent = hoverEvent;
    this.insertion = insertion;
  }
  
  @Nullable
  public Key font() {
    return this.font;
  }
  
  @NotNull
  public Style font(@Nullable Key font) {
    if (Objects.equals(this.font, font))
      return this; 
    return new StyleImpl(font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public TextColor color() {
    return this.color;
  }
  
  @NotNull
  public Style color(@Nullable TextColor color) {
    if (Objects.equals(this.color, color))
      return this; 
    return new StyleImpl(this.font, color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @NotNull
  public Style colorIfAbsent(@Nullable TextColor color) {
    if (this.color == null)
      return color(color); 
    return this;
  }
  
  public TextDecoration.State decoration(@NotNull TextDecoration decoration) {
    if (decoration == TextDecoration.BOLD)
      return this.bold; 
    if (decoration == TextDecoration.ITALIC)
      return this.italic; 
    if (decoration == TextDecoration.UNDERLINED)
      return this.underlined; 
    if (decoration == TextDecoration.STRIKETHROUGH)
      return this.strikethrough; 
    if (decoration == TextDecoration.OBFUSCATED)
      return this.obfuscated; 
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
  }
  
  @NotNull
  public Style decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    Objects.requireNonNull(state, "state");
    if (decoration == TextDecoration.BOLD)
      return new StyleImpl(this.font, this.color, this.obfuscated, state, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion); 
    if (decoration == TextDecoration.ITALIC)
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, state, this.clickEvent, this.hoverEvent, this.insertion); 
    if (decoration == TextDecoration.UNDERLINED)
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, state, this.italic, this.clickEvent, this.hoverEvent, this.insertion); 
    if (decoration == TextDecoration.STRIKETHROUGH)
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, state, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion); 
    if (decoration == TextDecoration.OBFUSCATED)
      return new StyleImpl(this.font, this.color, state, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion); 
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
  }
  
  @NotNull
  public Map<TextDecoration, TextDecoration.State> decorations() {
    Map<TextDecoration, TextDecoration.State> decorations = new EnumMap<>(TextDecoration.class);
    for (int i = 0, length = DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DECORATIONS[i];
      TextDecoration.State value = decoration(decoration);
      decorations.put(decoration, value);
    } 
    return decorations;
  }
  
  @NotNull
  public Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    TextDecoration.State obfuscated = decorations.getOrDefault(TextDecoration.OBFUSCATED, this.obfuscated);
    TextDecoration.State bold = decorations.getOrDefault(TextDecoration.BOLD, this.bold);
    TextDecoration.State strikethrough = decorations.getOrDefault(TextDecoration.STRIKETHROUGH, this.strikethrough);
    TextDecoration.State underlined = decorations.getOrDefault(TextDecoration.UNDERLINED, this.underlined);
    TextDecoration.State italic = decorations.getOrDefault(TextDecoration.ITALIC, this.italic);
    return new StyleImpl(this.font, this.color, obfuscated, bold, strikethrough, underlined, italic, this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public ClickEvent clickEvent() {
    return this.clickEvent;
  }
  
  @NotNull
  public Style clickEvent(@Nullable ClickEvent event) {
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, event, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public HoverEvent<?> hoverEvent() {
    return this.hoverEvent;
  }
  
  @NotNull
  public Style hoverEvent(@Nullable HoverEventSource<?> source) {
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
  }
  
  @Nullable
  public String insertion() {
    return this.insertion;
  }
  
  @NotNull
  public Style insertion(@Nullable String insertion) {
    if (Objects.equals(this.insertion, insertion))
      return this; 
    return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, insertion);
  }
  
  @NotNull
  public Style merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
    if (that.isEmpty() || strategy == Style.Merge.Strategy.NEVER || merges.isEmpty())
      return this; 
    if (isEmpty() && Style.Merge.hasAll(merges))
      return that; 
    Style.Builder builder = toBuilder();
    builder.merge(that, strategy, merges);
    return builder.build();
  }
  
  public boolean isEmpty() {
    return (this == EMPTY);
  }
  
  @NotNull
  public Style.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("color", this.color), 
          ExaminableProperty.of("obfuscated", this.obfuscated), 
          ExaminableProperty.of("bold", this.bold), 
          ExaminableProperty.of("strikethrough", this.strikethrough), 
          ExaminableProperty.of("underlined", this.underlined), 
          ExaminableProperty.of("italic", this.italic), 
          ExaminableProperty.of("clickEvent", this.clickEvent), 
          ExaminableProperty.of("hoverEvent", this.hoverEvent), 
          ExaminableProperty.of("insertion", this.insertion), 
          ExaminableProperty.of("font", this.font) });
  }
  
  @NotNull
  public String toString() {
    return (String)examine((Examiner)StringExaminer.simpleEscaping());
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof StyleImpl))
      return false; 
    StyleImpl that = (StyleImpl)other;
    return (Objects.equals(this.color, that.color) && this.obfuscated == that.obfuscated && this.bold == that.bold && this.strikethrough == that.strikethrough && this.underlined == that.underlined && this.italic == that.italic && 
      
      Objects.equals(this.clickEvent, that.clickEvent) && 
      Objects.equals(this.hoverEvent, that.hoverEvent) && 
      Objects.equals(this.insertion, that.insertion) && 
      Objects.equals(this.font, that.font));
  }
  
  public int hashCode() {
    int result = Objects.hashCode(this.color);
    result = 31 * result + this.obfuscated.hashCode();
    result = 31 * result + this.bold.hashCode();
    result = 31 * result + this.strikethrough.hashCode();
    result = 31 * result + this.underlined.hashCode();
    result = 31 * result + this.italic.hashCode();
    result = 31 * result + Objects.hashCode(this.clickEvent);
    result = 31 * result + Objects.hashCode(this.hoverEvent);
    result = 31 * result + Objects.hashCode(this.insertion);
    result = 31 * result + Objects.hashCode(this.font);
    return result;
  }
  
  static final class BuilderImpl implements Style.Builder {
    @Nullable
    Key font;
    
    @Nullable
    TextColor color;
    
    TextDecoration.State obfuscated = TextDecoration.State.NOT_SET;
    
    TextDecoration.State bold = TextDecoration.State.NOT_SET;
    
    TextDecoration.State strikethrough = TextDecoration.State.NOT_SET;
    
    TextDecoration.State underlined = TextDecoration.State.NOT_SET;
    
    TextDecoration.State italic = TextDecoration.State.NOT_SET;
    
    @Nullable
    ClickEvent clickEvent;
    
    @Nullable
    HoverEvent<?> hoverEvent;
    
    @Nullable
    String insertion;
    
    BuilderImpl(@NotNull StyleImpl style) {
      this.color = style.color;
      this.obfuscated = style.obfuscated;
      this.bold = style.bold;
      this.strikethrough = style.strikethrough;
      this.underlined = style.underlined;
      this.italic = style.italic;
      this.clickEvent = style.clickEvent;
      this.hoverEvent = style.hoverEvent;
      this.insertion = style.insertion;
      this.font = style.font;
    }
    
    @NotNull
    public Style.Builder font(@Nullable Key font) {
      this.font = font;
      return this;
    }
    
    @NotNull
    public Style.Builder color(@Nullable TextColor color) {
      this.color = color;
      return this;
    }
    
    @NotNull
    public Style.Builder colorIfAbsent(@Nullable TextColor color) {
      if (this.color == null)
        this.color = color; 
      return this;
    }
    
    @NotNull
    public Style.Builder decorate(@NotNull TextDecoration decoration) {
      return decoration(decoration, TextDecoration.State.TRUE);
    }
    
    @NotNull
    public Style.Builder decorate(@NotNull TextDecoration... decorations) {
      for (int i = 0, length = decorations.length; i < length; i++)
        decorate(decorations[i]); 
      return this;
    }
    
    @NotNull
    public Style.Builder decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
      Objects.requireNonNull(state, "state");
      if (decoration == TextDecoration.BOLD) {
        this.bold = state;
        return this;
      } 
      if (decoration == TextDecoration.ITALIC) {
        this.italic = state;
        return this;
      } 
      if (decoration == TextDecoration.UNDERLINED) {
        this.underlined = state;
        return this;
      } 
      if (decoration == TextDecoration.STRIKETHROUGH) {
        this.strikethrough = state;
        return this;
      } 
      if (decoration == TextDecoration.OBFUSCATED) {
        this.obfuscated = state;
        return this;
      } 
      throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
    }
    
    @NotNull
    Style.Builder decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
      Objects.requireNonNull(state, "state");
      if (decoration == TextDecoration.BOLD && this.bold == TextDecoration.State.NOT_SET) {
        this.bold = state;
        return this;
      } 
      if (decoration == TextDecoration.ITALIC && this.italic == TextDecoration.State.NOT_SET) {
        this.italic = state;
        return this;
      } 
      if (decoration == TextDecoration.UNDERLINED && this.underlined == TextDecoration.State.NOT_SET) {
        this.underlined = state;
        return this;
      } 
      if (decoration == TextDecoration.STRIKETHROUGH && this.strikethrough == TextDecoration.State.NOT_SET) {
        this.strikethrough = state;
        return this;
      } 
      if (decoration == TextDecoration.OBFUSCATED && this.obfuscated == TextDecoration.State.NOT_SET) {
        this.obfuscated = state;
        return this;
      } 
      throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
    }
    
    @NotNull
    public Style.Builder clickEvent(@Nullable ClickEvent event) {
      this.clickEvent = event;
      return this;
    }
    
    @NotNull
    public Style.Builder hoverEvent(@Nullable HoverEventSource<?> source) {
      this.hoverEvent = HoverEventSource.unbox(source);
      return this;
    }
    
    @NotNull
    public Style.Builder insertion(@Nullable String insertion) {
      this.insertion = insertion;
      return this;
    }
    
    @NotNull
    public Style.Builder merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
      if (strategy == Style.Merge.Strategy.NEVER || that.isEmpty() || merges.isEmpty())
        return this; 
      Merger merger = merger(strategy);
      if (merges.contains(Style.Merge.COLOR)) {
        TextColor color = that.color();
        if (color != null)
          merger.mergeColor(this, color); 
      } 
      if (merges.contains(Style.Merge.DECORATIONS))
        for (int i = 0, length = StyleImpl.DECORATIONS.length; i < length; i++) {
          TextDecoration decoration = StyleImpl.DECORATIONS[i];
          TextDecoration.State state = that.decoration(decoration);
          if (state != TextDecoration.State.NOT_SET)
            merger.mergeDecoration(this, decoration, state); 
        }  
      if (merges.contains(Style.Merge.EVENTS)) {
        ClickEvent clickEvent = that.clickEvent();
        if (clickEvent != null)
          merger.mergeClickEvent(this, clickEvent); 
        HoverEvent<?> hoverEvent = that.hoverEvent();
        if (hoverEvent != null)
          merger.mergeHoverEvent(this, hoverEvent); 
      } 
      if (merges.contains(Style.Merge.INSERTION)) {
        String insertion = that.insertion();
        if (insertion != null)
          merger.mergeInsertion(this, insertion); 
      } 
      if (merges.contains(Style.Merge.FONT)) {
        Key font = that.font();
        if (font != null)
          merger.mergeFont(this, font); 
      } 
      return this;
    }
    
    private static Merger merger(Style.Merge.Strategy strategy) {
      if (strategy == Style.Merge.Strategy.ALWAYS)
        return (Merger)AlwaysMerger.INSTANCE; 
      if (strategy == Style.Merge.Strategy.NEVER)
        throw new UnsupportedOperationException(); 
      if (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET)
        return (Merger)IfAbsentOnTargetMerger.INSTANCE; 
      throw new IllegalArgumentException(strategy.name());
    }
    
    @NotNull
    public StyleImpl build() {
      if (isEmpty())
        return StyleImpl.EMPTY; 
      return new StyleImpl(this.font, this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }
    
    private boolean isEmpty() {
      return (this.color == null && this.obfuscated == TextDecoration.State.NOT_SET && this.bold == TextDecoration.State.NOT_SET && this.strikethrough == TextDecoration.State.NOT_SET && this.underlined == TextDecoration.State.NOT_SET && this.italic == TextDecoration.State.NOT_SET && this.clickEvent == null && this.hoverEvent == null && this.insertion == null && this.font == null);
    }
    
    BuilderImpl() {}
  }
}
