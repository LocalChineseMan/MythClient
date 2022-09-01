package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import java.lang.reflect.Type;
import java.util.UUID;

final class ShowEntitySerializer implements JsonDeserializer<HoverEvent.ShowEntity>, JsonSerializer<HoverEvent.ShowEntity> {
  static final String TYPE = "type";
  
  static final String ID = "id";
  
  static final String NAME = "name";
  
  public HoverEvent.ShowEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    if (!object.has("type") || !object.has("id"))
      throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized"); 
    Key type = (Key)context.deserialize((JsonElement)object.getAsJsonPrimitive("type"), Key.class);
    UUID id = UUID.fromString(object.getAsJsonPrimitive("id").getAsString());
    Component name = null;
    if (object.has("name"))
      name = (Component)context.deserialize(object.get("name"), Component.class); 
    return HoverEvent.ShowEntity.of(type, id, name);
  }
  
  public JsonElement serialize(HoverEvent.ShowEntity src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.add("type", context.serialize(src.type()));
    json.addProperty("id", src.id().toString());
    Component name = src.name();
    if (name != null)
      json.add("name", context.serialize(name)); 
    return (JsonElement)json;
  }
}
