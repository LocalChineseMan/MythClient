package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.BlockNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.BuildableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentBuilder;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.EntityNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.StorageNBTComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl implements JsonDeserializer<Component>, JsonSerializer<Component> {
  static final String TEXT = "text";
  
  static final String TRANSLATE = "translate";
  
  static final String TRANSLATE_WITH = "with";
  
  static final String SCORE = "score";
  
  static final String SCORE_NAME = "name";
  
  static final String SCORE_OBJECTIVE = "objective";
  
  static final String SCORE_VALUE = "value";
  
  static final String SELECTOR = "selector";
  
  static final String KEYBIND = "keybind";
  
  static final String EXTRA = "extra";
  
  static final String NBT = "nbt";
  
  static final String NBT_INTERPRET = "interpret";
  
  static final String NBT_BLOCK = "block";
  
  static final String NBT_ENTITY = "entity";
  
  static final String NBT_STORAGE = "storage";
  
  static final String SEPARATOR = "separator";
  
  public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return (Component)deserialize0(json, context);
  }
  
  private BuildableComponent<?, ?> deserialize0(JsonElement element, JsonDeserializationContext context) throws JsonParseException {
    StorageNBTComponent.Builder builder;
    if (element.isJsonPrimitive())
      return (BuildableComponent<?, ?>)Component.text(element.getAsString()); 
    if (element.isJsonArray()) {
      ComponentBuilder<?, ?> parent = null;
      for (JsonElement childElement : element.getAsJsonArray()) {
        BuildableComponent<?, ?> child = deserialize0(childElement, context);
        if (parent == null) {
          parent = child.toBuilder();
          continue;
        } 
        parent.append((Component)child);
      } 
      if (parent == null)
        throw notSureHowToDeserialize(element); 
      return parent.build();
    } 
    if (!element.isJsonObject())
      throw notSureHowToDeserialize(element); 
    JsonObject object = element.getAsJsonObject();
    if (object.has("text")) {
      TextComponent.Builder builder1 = Component.text().content(object.get("text").getAsString());
    } else if (object.has("translate")) {
      String key = object.get("translate").getAsString();
      if (!object.has("with")) {
        TranslatableComponent.Builder builder1 = Component.translatable().key(key);
      } else {
        JsonArray with = object.getAsJsonArray("with");
        List<Component> args = new ArrayList<>(with.size());
        for (int i = 0, size = with.size(); i < size; i++) {
          JsonElement argElement = with.get(i);
          args.add(deserialize0(argElement, context));
        } 
        TranslatableComponent.Builder builder1 = Component.translatable().key(key).args(args);
      } 
    } else if (object.has("score")) {
      JsonObject score = object.getAsJsonObject("score");
      if (!score.has("name") || !score.has("objective"))
        throw new JsonParseException("A score component requires a name and objective"); 
      ScoreComponent.Builder builder1 = Component.score().name(score.get("name").getAsString()).objective(score.get("objective").getAsString());
      if (score.has("value")) {
        ScoreComponent.Builder builder2 = builder1.value(score.get("value").getAsString());
      } else {
        ScoreComponent.Builder builder2 = builder1;
      } 
    } else if (object.has("selector")) {
      Component separator = deserializeSeparator(object, context);
      SelectorComponent.Builder builder1 = Component.selector().pattern(object.get("selector").getAsString()).separator((ComponentLike)separator);
    } else if (object.has("keybind")) {
      KeybindComponent.Builder builder1 = Component.keybind().keybind(object.get("keybind").getAsString());
    } else if (object.has("nbt")) {
      String nbt = object.get("nbt").getAsString();
      boolean interpret = (object.has("interpret") && object.getAsJsonPrimitive("interpret").getAsBoolean());
      Component separator = deserializeSeparator(object, context);
      if (object.has("block")) {
        BlockNBTComponent.Pos pos = (BlockNBTComponent.Pos)context.deserialize(object.get("block"), BlockNBTComponent.Pos.class);
        BlockNBTComponent.Builder builder1 = ((BlockNBTComponent.Builder)nbt(Component.blockNBT(), nbt, interpret, separator)).pos(pos);
      } else if (object.has("entity")) {
        EntityNBTComponent.Builder builder1 = ((EntityNBTComponent.Builder)nbt(Component.entityNBT(), nbt, interpret, separator)).selector(object.get("entity").getAsString());
      } else if (object.has("storage")) {
        builder = ((StorageNBTComponent.Builder)nbt(Component.storageNBT(), nbt, interpret, separator)).storage((Key)context.deserialize(object.get("storage"), Key.class));
      } else {
        throw notSureHowToDeserialize(element);
      } 
    } else {
      throw notSureHowToDeserialize(element);
    } 
    if (object.has("extra")) {
      JsonArray extra = object.getAsJsonArray("extra");
      for (int i = 0, size = extra.size(); i < size; i++) {
        JsonElement extraElement = extra.get(i);
        builder.append((Component)deserialize0(extraElement, context));
      } 
    } 
    Style style = (Style)context.deserialize(element, Style.class);
    if (!style.isEmpty())
      builder.style(style); 
    return builder.build();
  }
  
  @Nullable
  private Component deserializeSeparator(JsonObject json, JsonDeserializationContext context) {
    if (json.has("separator"))
      return (Component)deserialize0(json.get("separator"), context); 
    return null;
  }
  
  private static <C extends NBTComponent<C, B>, B extends com.viaversion.viaversion.libs.kyori.adventure.text.NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
    return (B)builder
      .nbtPath(nbt)
      .interpret(interpret)
      .separator((ComponentLike)separator);
  }
  
  public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    if (src.hasStyling()) {
      JsonElement style = context.serialize(src.style());
      if (style.isJsonObject())
        for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)((JsonObject)style).entrySet())
          object.add(entry.getKey(), entry.getValue());  
    } 
    List<Component> children = src.children();
    if (!children.isEmpty()) {
      JsonArray extra = new JsonArray();
      for (Component child : children)
        extra.add(context.serialize(child)); 
      object.add("extra", (JsonElement)extra);
    } 
    if (src instanceof TextComponent) {
      object.addProperty("text", ((TextComponent)src).content());
    } else if (src instanceof TranslatableComponent) {
      TranslatableComponent tc = (TranslatableComponent)src;
      object.addProperty("translate", tc.key());
      if (!tc.args().isEmpty()) {
        JsonArray with = new JsonArray();
        for (Component arg : tc.args())
          with.add(context.serialize(arg)); 
        object.add("with", (JsonElement)with);
      } 
    } else if (src instanceof ScoreComponent) {
      ScoreComponent sc = (ScoreComponent)src;
      JsonObject score = new JsonObject();
      score.addProperty("name", sc.name());
      score.addProperty("objective", sc.objective());
      String value = sc.value();
      if (value != null)
        score.addProperty("value", value); 
      object.add("score", (JsonElement)score);
    } else if (src instanceof SelectorComponent) {
      SelectorComponent sc = (SelectorComponent)src;
      object.addProperty("selector", sc.pattern());
      serializeSeparator(context, object, sc.separator());
    } else if (src instanceof KeybindComponent) {
      object.addProperty("keybind", ((KeybindComponent)src).keybind());
    } else if (src instanceof NBTComponent) {
      NBTComponent<?, ?> nc = (NBTComponent<?, ?>)src;
      object.addProperty("nbt", nc.nbtPath());
      object.addProperty("interpret", Boolean.valueOf(nc.interpret()));
      if (src instanceof BlockNBTComponent) {
        JsonElement position = context.serialize(((BlockNBTComponent)nc).pos());
        object.add("block", position);
        serializeSeparator(context, object, nc.separator());
      } else if (src instanceof EntityNBTComponent) {
        object.addProperty("entity", ((EntityNBTComponent)nc).selector());
      } else if (src instanceof StorageNBTComponent) {
        object.add("storage", context.serialize(((StorageNBTComponent)nc).storage()));
      } else {
        throw notSureHowToSerialize(src);
      } 
    } else {
      throw notSureHowToSerialize(src);
    } 
    return (JsonElement)object;
  }
  
  private void serializeSeparator(JsonSerializationContext context, JsonObject json, @Nullable Component separator) {
    if (separator != null)
      json.add("separator", context.serialize(separator)); 
  }
  
  static JsonParseException notSureHowToDeserialize(Object element) {
    return new JsonParseException("Don't know how to turn " + element + " into a Component");
  }
  
  private static IllegalArgumentException notSureHowToSerialize(Component component) {
    return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
  }
}
