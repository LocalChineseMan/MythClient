package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.CharStreams;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MappingData extends MappingDataBase {
  private final Map<String, Integer[]> blockTags = (Map)new HashMap<>();
  
  private final Map<String, Integer[]> itemTags = (Map)new HashMap<>();
  
  private final Map<String, Integer[]> fluidTags = (Map)new HashMap<>();
  
  private final BiMap<Short, String> oldEnchantmentsIds = (BiMap<Short, String>)HashBiMap.create();
  
  private final Map<String, String> translateMapping = new HashMap<>();
  
  private final Map<String, String> mojangTranslation = new HashMap<>();
  
  private final BiMap<String, String> channelMappings = (BiMap<String, String>)HashBiMap.create();
  
  private Mappings enchantmentMappings;
  
  public MappingData() {
    super("1.12", "1.13");
  }
  
  public void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
    loadTags(this.blockTags, newMappings.getAsJsonObject("block_tags"));
    loadTags(this.itemTags, newMappings.getAsJsonObject("item_tags"));
    loadTags(this.fluidTags, newMappings.getAsJsonObject("fluid_tags"));
    loadEnchantments((Map<Short, String>)this.oldEnchantmentsIds, oldMappings.getAsJsonObject("enchantments"));
    this.enchantmentMappings = (Mappings)new IntArrayMappings(72, oldMappings.getAsJsonObject("enchantments"), newMappings.getAsJsonObject("enchantments"));
    if (Via.getConfig().isSnowCollisionFix())
      this.blockMappings.setNewId(1248, 3416); 
    if (Via.getConfig().isInfestedBlocksFix()) {
      this.blockMappings.setNewId(1552, 1);
      this.blockMappings.setNewId(1553, 14);
      this.blockMappings.setNewId(1554, 3983);
      this.blockMappings.setNewId(1555, 3984);
      this.blockMappings.setNewId(1556, 3985);
      this.blockMappings.setNewId(1557, 3986);
    } 
    JsonObject object = MappingDataLoader.loadFromDataDir("channelmappings-1.13.json");
    if (object != null)
      for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)object.entrySet()) {
        String oldChannel = entry.getKey();
        String newChannel = ((JsonElement)entry.getValue()).getAsString();
        if (!isValid1_13Channel(newChannel)) {
          Via.getPlatform().getLogger().warning("Channel '" + newChannel + "' is not a valid 1.13 plugin channel, please check your configuration!");
          continue;
        } 
        this.channelMappings.put(oldChannel, newChannel);
      }  
    Map<String, String> translateData = (Map<String, String>)GsonUtil.getGson().fromJson(new InputStreamReader(MappingData.class
          .getClassLoader().getResourceAsStream("assets/viaversion/data/mapping-lang-1.12-1.13.json")), (new TypeToken<Map<String, String>>() {
        
        }).getType());
    try {
      String[] lines;
      Reader reader = new InputStreamReader(MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/en_US.properties"), StandardCharsets.UTF_8);
      try {
        lines = CharStreams.toString(reader).split("\n");
        reader.close();
      } catch (Throwable throwable) {
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      for (String line : lines) {
        if (!line.isEmpty()) {
          String[] keyAndTranslation = line.split("=", 2);
          if (keyAndTranslation.length == 2) {
            String key = keyAndTranslation[0];
            if (!translateData.containsKey(key)) {
              String translation = keyAndTranslation[1].replaceAll("%(\\d\\$)?d", "%$1s");
              this.mojangTranslation.put(key, translation);
            } else {
              String dataValue = translateData.get(key);
              if (dataValue != null)
                this.translateMapping.put(key, dataValue); 
            } 
          } 
        } 
      } 
    } catch (IOException e) {
      String[] lines;
      lines.printStackTrace();
    } 
  }
  
  protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings, String key) {
    if (key.equals("blocks"))
      return (Mappings)new IntArrayMappings(4084, oldMappings.getAsJsonObject("blocks"), newMappings.getAsJsonObject("blockstates")); 
    return super.loadFromObject(oldMappings, newMappings, diffMappings, key);
  }
  
  public static String validateNewChannel(String newId) {
    if (!isValid1_13Channel(newId))
      return null; 
    int separatorIndex = newId.indexOf(':');
    if ((separatorIndex == -1 || separatorIndex == 0) && newId.length() <= 10)
      newId = "minecraft:" + newId; 
    return newId;
  }
  
  public static boolean isValid1_13Channel(String channelId) {
    return channelId.matches("([0-9a-z_.-]+):([0-9a-z_/.-]+)");
  }
  
  private void loadTags(Map<String, Integer[]> output, JsonObject newTags) {
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)newTags.entrySet()) {
      JsonArray ids = ((JsonElement)entry.getValue()).getAsJsonArray();
      Integer[] idsArray = new Integer[ids.size()];
      for (int i = 0; i < ids.size(); i++)
        idsArray[i] = Integer.valueOf(ids.get(i).getAsInt()); 
      output.put(entry.getKey(), idsArray);
    } 
  }
  
  private void loadEnchantments(Map<Short, String> output, JsonObject enchantments) {
    for (Map.Entry<String, JsonElement> enchantment : (Iterable<Map.Entry<String, JsonElement>>)enchantments.entrySet())
      output.put(Short.valueOf(Short.parseShort(enchantment.getKey())), ((JsonElement)enchantment.getValue()).getAsString()); 
  }
  
  public Map<String, Integer[]> getBlockTags() {
    return this.blockTags;
  }
  
  public Map<String, Integer[]> getItemTags() {
    return this.itemTags;
  }
  
  public Map<String, Integer[]> getFluidTags() {
    return this.fluidTags;
  }
  
  public BiMap<Short, String> getOldEnchantmentsIds() {
    return this.oldEnchantmentsIds;
  }
  
  public Map<String, String> getTranslateMapping() {
    return this.translateMapping;
  }
  
  public Map<String, String> getMojangTranslation() {
    return this.mojangTranslation;
  }
  
  public BiMap<String, String> getChannelMappings() {
    return this.channelMappings;
  }
  
  public Mappings getEnchantmentMappings() {
    return this.enchantmentMappings;
  }
}
