package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.util.Codec;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface LegacyHoverEventSerializer {
  HoverEvent.ShowItem deserializeShowItem(@NotNull Component paramComponent) throws IOException;
  
  HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component paramComponent, Codec.Decoder<Component, String, ? extends RuntimeException> paramDecoder) throws IOException;
  
  @NotNull
  Component serializeShowItem(HoverEvent.ShowItem paramShowItem) throws IOException;
  
  @NotNull
  Component serializeShowEntity(HoverEvent.ShowEntity paramShowEntity, Codec.Encoder<Component, String, ? extends RuntimeException> paramEncoder) throws IOException;
}
