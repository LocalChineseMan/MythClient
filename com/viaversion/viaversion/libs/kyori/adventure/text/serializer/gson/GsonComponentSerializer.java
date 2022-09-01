package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.ComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GsonComponentSerializer extends ComponentSerializer<Component, Component, String>, Buildable<GsonComponentSerializer, GsonComponentSerializer.Builder> {
  @NotNull
  static GsonComponentSerializer gson() {
    return GsonComponentSerializerImpl.Instances.INSTANCE;
  }
  
  @NotNull
  static GsonComponentSerializer colorDownsamplingGson() {
    return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
  }
  
  static Builder builder() {
    return new GsonComponentSerializerImpl.BuilderImpl();
  }
  
  @NotNull
  Gson serializer();
  
  @NotNull
  UnaryOperator<GsonBuilder> populator();
  
  @NotNull
  Component deserializeFromTree(@NotNull JsonElement paramJsonElement);
  
  @NotNull
  JsonElement serializeToTree(@NotNull Component paramComponent);
  
  @Internal
  public static interface Provider {
    @Internal
    @NotNull
    GsonComponentSerializer gson();
    
    @Internal
    @NotNull
    GsonComponentSerializer gsonLegacy();
    
    @Internal
    @NotNull
    Consumer<GsonComponentSerializer.Builder> builder();
  }
  
  public static interface Builder extends Buildable.Builder<GsonComponentSerializer> {
    @NotNull
    Builder downsampleColors();
    
    @NotNull
    Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer param1LegacyHoverEventSerializer);
    
    @NotNull
    Builder emitLegacyHoverEvent();
    
    @NotNull
    GsonComponentSerializer build();
  }
}
