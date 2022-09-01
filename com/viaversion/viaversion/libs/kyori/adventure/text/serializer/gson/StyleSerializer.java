package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.internal.Streams;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.ClickEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEventSource;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.util.Codec;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

final class StyleSerializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
  private static final TextDecoration[] DECORATIONS = new TextDecoration[] { TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED };
  
  static final String FONT = "font";
  
  static final String COLOR = "color";
  
  static final String INSERTION = "insertion";
  
  static final String CLICK_EVENT = "clickEvent";
  
  static final String CLICK_EVENT_ACTION = "action";
  
  static final String CLICK_EVENT_VALUE = "value";
  
  static final String HOVER_EVENT = "hoverEvent";
  
  static final String HOVER_EVENT_ACTION = "action";
  
  static final String HOVER_EVENT_CONTENTS = "contents";
  
  @Deprecated
  static final String HOVER_EVENT_VALUE = "value";
  
  private final LegacyHoverEventSerializer legacyHover;
  
  private final boolean emitLegacyHover;
  
  static {
    Set<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
    for (TextDecoration decoration : DECORATIONS)
      knownDecorations.remove(decoration); 
    if (!knownDecorations.isEmpty())
      throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations); 
  }
  
  StyleSerializer(@Nullable LegacyHoverEventSerializer legacyHover, boolean emitLegacyHover) {
    this.legacyHover = legacyHover;
    this.emitLegacyHover = emitLegacyHover;
  }
  
  public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    return deserialize(object, context);
  }
  
  private Style deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
    Style.Builder style = Style.style();
    if (json.has("font"))
      style.font((Key)context.deserialize(json.get("font"), Key.class)); 
    if (json.has("color")) {
      TextColorWrapper color = (TextColorWrapper)context.deserialize(json.get("color"), TextColorWrapper.class);
      if (color.color != null) {
        style.color(color.color);
      } else if (color.decoration != null) {
        style.decoration(color.decoration, true);
      } 
    } 
    for (int i = 0, length = DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DECORATIONS[i];
      String name = (String)TextDecoration.NAMES.key(decoration);
      if (json.has(name))
        style.decoration(decoration, json.get(name).getAsBoolean()); 
    } 
    if (json.has("insertion"))
      style.insertion(json.get("insertion").getAsString()); 
    if (json.has("clickEvent")) {
      JsonObject clickEvent = json.getAsJsonObject("clickEvent");
      if (clickEvent != null) {
        ClickEvent.Action action = optionallyDeserialize((JsonElement)clickEvent.getAsJsonPrimitive("action"), context, ClickEvent.Action.class);
        if (action != null && action.readable()) {
          JsonPrimitive rawValue = clickEvent.getAsJsonPrimitive("value");
          String value = (rawValue == null) ? null : rawValue.getAsString();
          if (value != null)
            style.clickEvent(ClickEvent.clickEvent(action, value)); 
        } 
      } 
    } 
    if (json.has("hoverEvent")) {
      JsonObject hoverEvent = json.getAsJsonObject("hoverEvent");
      if (hoverEvent != null) {
        HoverEvent.Action<?> action = optionallyDeserialize((JsonElement)hoverEvent.getAsJsonPrimitive("action"), context, HoverEvent.Action.class);
        if (action != null && action.readable()) {
          Object value;
          if (hoverEvent.has("contents")) {
            JsonElement rawValue = hoverEvent.get("contents");
            value = context.deserialize(rawValue, action.type());
          } else if (hoverEvent.has("value")) {
            Component rawValue = (Component)context.deserialize(hoverEvent.get("value"), Component.class);
            value = legacyHoverEventContents(action, rawValue, context);
          } else {
            value = null;
          } 
          if (value != null)
            style.hoverEvent((HoverEventSource)HoverEvent.hoverEvent(action, value)); 
        } 
      } 
    } 
    if (json.has("font"))
      style.font((Key)context.deserialize(json.get("font"), Key.class)); 
    return style.build();
  }
  
  private static <T> T optionallyDeserialize(JsonElement json, JsonDeserializationContext context, Class<T> type) {
    return (json == null) ? null : (T)context.deserialize(json, type);
  }
  
  private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue, JsonDeserializationContext context) {
    if (action == HoverEvent.Action.SHOW_TEXT)
      return rawValue; 
    if (this.legacyHover != null)
      try {
        if (action == HoverEvent.Action.SHOW_ENTITY)
          return this.legacyHover.deserializeShowEntity(rawValue, (Codec.Decoder)decoder(context)); 
        if (action == HoverEvent.Action.SHOW_ITEM)
          return this.legacyHover.deserializeShowItem(rawValue); 
      } catch (IOException ex) {
        throw new JsonParseException(ex);
      }  
    throw new UnsupportedOperationException();
  }
  
  private Codec.Decoder<Component, String, JsonParseException> decoder(JsonDeserializationContext ctx) {
    return string -> {
        JsonReader reader = new JsonReader(new StringReader(string));
        return (Component)ctx.deserialize(Streams.parse(reader), Component.class);
      };
  }
  
  public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    for (int i = 0, length = DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DECORATIONS[i];
      TextDecoration.State state = src.decoration(decoration);
      if (state != TextDecoration.State.NOT_SET) {
        String name = (String)TextDecoration.NAMES.key(decoration);
        assert name != null;
        json.addProperty(name, Boolean.valueOf((state == TextDecoration.State.TRUE)));
      } 
    } 
    TextColor color = src.color();
    if (color != null)
      json.add("color", context.serialize(color)); 
    String insertion = src.insertion();
    if (insertion != null)
      json.addProperty("insertion", insertion); 
    ClickEvent clickEvent = src.clickEvent();
    if (clickEvent != null) {
      JsonObject eventJson = new JsonObject();
      eventJson.add("action", context.serialize(clickEvent.action()));
      eventJson.addProperty("value", clickEvent.value());
      json.add("clickEvent", (JsonElement)eventJson);
    } 
    HoverEvent<?> hoverEvent = src.hoverEvent();
    if (hoverEvent != null) {
      JsonObject eventJson = new JsonObject();
      eventJson.add("action", context.serialize(hoverEvent.action()));
      JsonElement modernContents = context.serialize(hoverEvent.value());
      eventJson.add("contents", modernContents);
      if (this.emitLegacyHover)
        eventJson.add("value", serializeLegacyHoverEvent(hoverEvent, modernContents, context)); 
      json.add("hoverEvent", (JsonElement)eventJson);
    } 
    Key font = src.font();
    if (font != null)
      json.add("font", context.serialize(font)); 
    return (JsonElement)json;
  }
  
  private JsonElement serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonElement modernContents, JsonSerializationContext context) {
    if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT)
      return modernContents; 
    if (this.legacyHover != null) {
      Component serialized = null;
      try {
        if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
          serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), encoder(context));
        } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
          serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
        } 
      } catch (IOException ex) {
        throw new JsonSyntaxException(ex);
      } 
      return (serialized == null) ? (JsonElement)JsonNull.INSTANCE : context.serialize(serialized);
    } 
    return (JsonElement)JsonNull.INSTANCE;
  }
  
  private Codec.Encoder<Component, String, RuntimeException> encoder(JsonSerializationContext ctx) {
    return component -> ctx.serialize(component).toString();
  }
}
