package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.adventure.util.Services;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class GsonComponentSerializerImpl implements GsonComponentSerializer {
  private static final Optional<GsonComponentSerializer.Provider> SERVICE = Services.service(GsonComponentSerializer.Provider.class);
  
  static final Consumer<GsonComponentSerializer.Builder> BUILDER = SERVICE
    .<Consumer<GsonComponentSerializer.Builder>>map(GsonComponentSerializer.Provider::builder)
    .orElseGet(() -> ());
  
  private final Gson serializer;
  
  private final UnaryOperator<GsonBuilder> populator;
  
  private final boolean downsampleColor;
  
  @Nullable
  private final LegacyHoverEventSerializer legacyHoverSerializer;
  
  private final boolean emitLegacyHover;
  
  static final class Instances {
    static final GsonComponentSerializer INSTANCE = GsonComponentSerializerImpl.SERVICE
      .map(GsonComponentSerializer.Provider::gson)
      .orElseGet(() -> new GsonComponentSerializerImpl(false, null, false));
    
    static final GsonComponentSerializer LEGACY_INSTANCE = GsonComponentSerializerImpl.SERVICE
      .map(GsonComponentSerializer.Provider::gsonLegacy)
      .orElseGet(() -> new GsonComponentSerializerImpl(true, null, true));
  }
  
  GsonComponentSerializerImpl(boolean downsampleColor, @Nullable LegacyHoverEventSerializer legacyHoverSerializer, boolean emitLegacyHover) {
    this.downsampleColor = downsampleColor;
    this.legacyHoverSerializer = legacyHoverSerializer;
    this.emitLegacyHover = emitLegacyHover;
    this.populator = (builder -> {
        builder.registerTypeHierarchyAdapter(Key.class, KeySerializer.INSTANCE);
        builder.registerTypeHierarchyAdapter(Component.class, new ComponentSerializerImpl());
        builder.registerTypeHierarchyAdapter(Style.class, new StyleSerializer(legacyHoverSerializer, emitLegacyHover));
        builder.registerTypeAdapter(ClickEvent.Action.class, IndexedSerializer.of("click action", ClickEvent.Action.NAMES));
        builder.registerTypeAdapter(HoverEvent.Action.class, IndexedSerializer.of("hover action", HoverEvent.Action.NAMES));
        builder.registerTypeAdapter(HoverEvent.ShowItem.class, new ShowItemSerializer());
        builder.registerTypeAdapter(HoverEvent.ShowEntity.class, new ShowEntitySerializer());
        builder.registerTypeAdapter(TextColorWrapper.class, new TextColorWrapper.Serializer());
        builder.registerTypeHierarchyAdapter(TextColor.class, downsampleColor ? TextColorSerializer.DOWNSAMPLE_COLOR : TextColorSerializer.INSTANCE);
        builder.registerTypeAdapter(TextDecoration.class, IndexedSerializer.of("text decoration", TextDecoration.NAMES));
        builder.registerTypeHierarchyAdapter(BlockNBTComponent.Pos.class, BlockNBTComponentPosSerializer.INSTANCE);
        return builder;
      });
    this.serializer = ((GsonBuilder)this.populator.apply(new GsonBuilder())).create();
  }
  
  @NotNull
  public Gson serializer() {
    return this.serializer;
  }
  
  @NotNull
  public UnaryOperator<GsonBuilder> populator() {
    return this.populator;
  }
  
  @NotNull
  public Component deserialize(@NotNull String string) {
    Component component = (Component)serializer().fromJson(string, Component.class);
    if (component == null)
      throw ComponentSerializerImpl.notSureHowToDeserialize(string); 
    return component;
  }
  
  @NotNull
  public String serialize(@NotNull Component component) {
    return serializer().toJson(component);
  }
  
  @NotNull
  public Component deserializeFromTree(@NotNull JsonElement input) {
    Component component = (Component)serializer().fromJson(input, Component.class);
    if (component == null)
      throw ComponentSerializerImpl.notSureHowToDeserialize(input); 
    return component;
  }
  
  @NotNull
  public JsonElement serializeToTree(@NotNull Component component) {
    return serializer().toJsonTree(component);
  }
  
  @NotNull
  public GsonComponentSerializer.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl implements GsonComponentSerializer.Builder {
    private boolean downsampleColor = false;
    
    @Nullable
    private LegacyHoverEventSerializer legacyHoverSerializer;
    
    private boolean emitLegacyHover = false;
    
    BuilderImpl() {
      GsonComponentSerializerImpl.BUILDER.accept(this);
    }
    
    BuilderImpl(GsonComponentSerializerImpl serializer) {
      this();
      this.downsampleColor = serializer.downsampleColor;
      this.emitLegacyHover = serializer.emitLegacyHover;
      this.legacyHoverSerializer = serializer.legacyHoverSerializer;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder downsampleColors() {
      this.downsampleColor = true;
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
      this.legacyHoverSerializer = serializer;
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder emitLegacyHoverEvent() {
      this.emitLegacyHover = true;
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer build() {
      if (this.legacyHoverSerializer == null)
        return this.downsampleColor ? GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE : GsonComponentSerializerImpl.Instances.INSTANCE; 
      return new GsonComponentSerializerImpl(this.downsampleColor, this.legacyHoverSerializer, this.emitLegacyHover);
    }
  }
}
