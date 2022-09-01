package com.viaversion.viabackwards.api.data;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class VBMappingDataLoader {
  public static JsonObject loadFromDataDir(String name) {
    File file = new File(ViaBackwards.getPlatform().getDataFolder(), name);
    if (!file.exists())
      return loadData(name); 
    try {
      FileReader reader = new FileReader(file);
      try {
        JsonObject jsonObject = (JsonObject)GsonUtil.getGson().fromJson(reader, JsonObject.class);
        reader.close();
        return jsonObject;
      } catch (Throwable throwable) {
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (JsonSyntaxException e) {
      ViaBackwards.getPlatform().getLogger().warning(name + " is badly formatted!");
      e.printStackTrace();
      ViaBackwards.getPlatform().getLogger().warning("Falling back to resource's file!");
      return loadData(name);
    } catch (IOException|com.viaversion.viaversion.libs.gson.JsonIOException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static JsonObject loadData(String name) {
    InputStream stream = VBMappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viabackwards/data/" + name);
    try {
      InputStreamReader reader = new InputStreamReader(stream);
      try {
        JsonObject jsonObject = (JsonObject)GsonUtil.getGson().fromJson(reader, JsonObject.class);
        reader.close();
        return jsonObject;
      } catch (Throwable throwable) {
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static void mapIdentifiers(int[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, JsonObject diffIdentifiers) {
    mapIdentifiers(output, oldIdentifiers, newIdentifiers, diffIdentifiers, true);
  }
  
  public static void mapIdentifiers(int[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, JsonObject diffIdentifiers, boolean warnOnMissing) {
    Object2IntMap newIdentifierMap = MappingDataLoader.indexedObjectToMap(newIdentifiers);
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)oldIdentifiers.entrySet()) {
      String key = ((JsonElement)entry.getValue()).getAsString();
      int mappedId = newIdentifierMap.getInt(key);
      if (mappedId == -1) {
        if (diffIdentifiers != null) {
          JsonPrimitive diffValueJson = diffIdentifiers.getAsJsonPrimitive(key);
          String diffValue = (diffValueJson != null) ? diffValueJson.getAsString() : null;
          int dataIndex;
          if (diffValue == null && (dataIndex = key.indexOf('[')) != -1 && (
            diffValueJson = diffIdentifiers.getAsJsonPrimitive(key.substring(0, dataIndex))) != null) {
            diffValue = diffValueJson.getAsString();
            if (diffValue.endsWith("["))
              diffValue = diffValue + key.substring(dataIndex + 1); 
          } 
          if (diffValue != null)
            mappedId = newIdentifierMap.getInt(diffValue); 
        } 
        if (mappedId == -1) {
          if ((warnOnMissing && !Via.getConfig().isSuppressConversionWarnings()) || Via.getManager().isDebug())
            ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( "); 
          continue;
        } 
      } 
      output[Integer.parseInt((String)entry.getKey())] = mappedId;
    } 
  }
  
  public static Map<String, String> objectToMap(JsonObject object) {
    Map<String, String> mappings = new HashMap<>();
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)object.entrySet()) {
      String key = entry.getKey();
      if (key.indexOf(':') == -1)
        key = "minecraft:" + key; 
      String value = ((JsonElement)entry.getValue()).getAsString();
      if (value.indexOf(':') == -1)
        value = "minecraft:" + value; 
      mappings.put(key, value);
    } 
    return mappings;
  }
  
  public static Int2ObjectMap<MappedItem> loadItemMappings(JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping) {
    return loadItemMappings(oldMapping, newMapping, diffMapping, false);
  }
  
  public static Int2ObjectMap<MappedItem> loadItemMappings(JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping, boolean warnOnMissing) {
    Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap(diffMapping.size(), 1.0F);
    Object2IntMap<String> newIdenfierMap = MappingDataLoader.indexedObjectToMap(newMapping);
    Object2IntMap<String> oldIdenfierMap = MappingDataLoader.indexedObjectToMap(oldMapping);
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)diffMapping.entrySet()) {
      JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
      String mappedIdName = object.getAsJsonPrimitive("id").getAsString();
      int mappedId = newIdenfierMap.getInt(mappedIdName);
      if (mappedId == -1) {
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
          ViaBackwards.getPlatform().getLogger().warning("No key for " + mappedIdName + " :( "); 
        continue;
      } 
      int oldId = oldIdenfierMap.getInt(entry.getKey());
      if (oldId == -1) {
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())
          ViaBackwards.getPlatform().getLogger().warning("No old entry for " + mappedIdName + " :( "); 
        continue;
      } 
      String name = object.getAsJsonPrimitive("name").getAsString();
      int2ObjectOpenHashMap.put(oldId, new MappedItem(mappedId, name));
    } 
    if (warnOnMissing && !Via.getConfig().isSuppressConversionWarnings())
      for (ObjectIterator<Object2IntMap.Entry<String>> objectIterator = oldIdenfierMap.object2IntEntrySet().iterator(); objectIterator.hasNext(); ) {
        Object2IntMap.Entry<String> entry = objectIterator.next();
        if (!newIdenfierMap.containsKey(entry.getKey()) && !int2ObjectOpenHashMap.containsKey(entry.getIntValue()))
          ViaBackwards.getPlatform().getLogger().warning("No item mapping for " + (String)entry.getKey() + " :( "); 
      }  
    return (Int2ObjectMap<MappedItem>)int2ObjectOpenHashMap;
  }
}
