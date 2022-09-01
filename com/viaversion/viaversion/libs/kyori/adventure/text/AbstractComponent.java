package com.viaversion.viaversion.libs.kyori.adventure.text;

import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug.Renderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Renderer(text = "this.debuggerString()", childrenArray = "this.children().toArray()", hasChildren = "!this.children().isEmpty()")
public abstract class AbstractComponent implements Component {
  private static final Predicate<Component> NOT_EMPTY;
  
  protected final List<Component> children;
  
  protected final Style style;
  
  static {
    NOT_EMPTY = (component -> (component != Component.empty()));
  }
  
  protected AbstractComponent(@NotNull List<? extends ComponentLike> children, @NotNull Style style) {
    this.children = ComponentLike.asComponents(children, NOT_EMPTY);
    this.style = style;
  }
  
  @NotNull
  public final List<Component> children() {
    return this.children;
  }
  
  @NotNull
  public final Style style() {
    return this.style;
  }
  
  @NotNull
  public Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> configurer) {
    Objects.requireNonNull(configurer, "configurer");
    return replaceText((TextReplacementConfig)Buildable.configureAndBuild((Buildable.Builder)TextReplacementConfig.builder(), configurer));
  }
  
  @NotNull
  public Component replaceText(@NotNull TextReplacementConfig config) {
    Objects.requireNonNull(config, "replacement");
    if (!(config instanceof TextReplacementConfigImpl))
      throw new IllegalArgumentException("Provided replacement was a custom TextReplacementConfig implementation, which is not supported."); 
    return TextReplacementRenderer.INSTANCE.render(this, ((TextReplacementConfigImpl)config).createState());
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof AbstractComponent))
      return false; 
    AbstractComponent that = (AbstractComponent)other;
    return (Objects.equals(this.children, that.children) && 
      Objects.equals(this.style, that.style));
  }
  
  public int hashCode() {
    int result = this.children.hashCode();
    result = 31 * result + this.style.hashCode();
    return result;
  }
  
  private String debuggerString() {
    return (String)StringExaminer.simpleEscaping().examine(examinableName(), examinablePropertiesWithoutChildren());
  }
  
  protected Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren() {
    return Stream.of(ExaminableProperty.of("style", this.style));
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        examinablePropertiesWithoutChildren(), 
        Stream.of(
          ExaminableProperty.of("children", this.children)));
  }
  
  public String toString() {
    return (String)examine((Examiner)StringExaminer.simpleEscaping());
  }
}
