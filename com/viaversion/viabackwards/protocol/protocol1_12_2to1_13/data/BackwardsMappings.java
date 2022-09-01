package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.data.VBMappings;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.StatisticMappings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BackwardsMappings extends BackwardsMappings {
  private final Int2ObjectMap<String> statisticMappings = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap();
  
  private final Map<String, String> translateMappings = new HashMap<>();
  
  private Mappings enchantmentMappings;
  
  public BackwardsMappings() {
    super("1.13", "1.12", Protocol1_13To1_12_2.class, true);
  }
  
  public void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
    this.enchantmentMappings = (Mappings)new VBMappings(oldMappings.getAsJsonObject("enchantments"), newMappings.getAsJsonObject("enchantments"), false);
    for (Map.Entry<String, Integer> entry : (Iterable<Map.Entry<String, Integer>>)StatisticMappings.CUSTOM_STATS.entrySet())
      this.statisticMappings.put(((Integer)entry.getValue()).intValue(), entry.getKey()); 
    for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)Protocol1_13To1_12_2.MAPPINGS.getTranslateMapping().entrySet())
      this.translateMappings.put(entry.getValue(), entry.getKey()); 
  }
  
  private static void mapIdentifiers(int[] output, JsonObject newIdentifiers, JsonObject oldIdentifiers, JsonObject mapping) {
    Object2IntMap newIdentifierMap = MappingDataLoader.indexedObjectToMap(oldIdentifiers);
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)newIdentifiers.entrySet()) {
      String key = ((JsonElement)entry.getValue()).getAsString();
      int value = newIdentifierMap.getInt(key);
      short hardId = -1;
      if (value == -1) {
        JsonPrimitive replacement = mapping.getAsJsonPrimitive(key);
        int propertyIndex;
        if (replacement == null && (propertyIndex = key.indexOf('[')) != -1)
          replacement = mapping.getAsJsonPrimitive(key.substring(0, propertyIndex)); 
        if (replacement != null)
          if (replacement.getAsString().startsWith("id:")) {
            String id = replacement.getAsString().replace("id:", "");
            hardId = Short.parseShort(id);
            value = newIdentifierMap.getInt(oldIdentifiers.getAsJsonPrimitive(id).getAsString());
          } else {
            value = newIdentifierMap.getInt(replacement.getAsString());
          }  
        if (value == -1) {
          if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
            if (replacement != null) {
              ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + "/" + replacement.getAsString() + " :( ");
              continue;
            } 
            ViaBackwards.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( ");
          } 
          continue;
        } 
      } 
      output[Integer.parseInt((String)entry.getKey())] = (hardId != -1) ? hardId : (short)value;
    } 
  }
  
  protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings, String key) {
    if (key.equals("blockstates")) {
      int[] oldToNew = new int[8582];
      Arrays.fill(oldToNew, -1);
      mapIdentifiers(oldToNew, oldMappings.getAsJsonObject("blockstates"), newMappings.getAsJsonObject("blocks"), diffMappings.getAsJsonObject("blockstates"));
      return (Mappings)new IntArrayMappings(oldToNew);
    } 
    return super.loadFromObject(oldMappings, newMappings, diffMappings, key);
  }
  
  public int getNewBlockStateId(int id) {
    int mappedId = super.getNewBlockStateId(id);
    switch (mappedId) {
      case 1595:
      case 1596:
      case 1597:
        return 1584;
      case 1611:
      case 1612:
      case 1613:
        return 1600;
    } 
    return mappedId;
  }
  
  protected int checkValidity(int id, int mappedId, String type) {
    return mappedId;
  }
  
  protected boolean shouldWarnOnMissing(String key) {
    return (super.shouldWarnOnMissing(key) && !key.equals("items"));
  }
  
  public Int2ObjectMap<String> getStatisticMappings() {
    return this.statisticMappings;
  }
  
  public Map<String, String> getTranslateMappings() {
    return this.translateMappings;
  }
  
  public Mappings getEnchantmentMappings() {
    return this.enchantmentMappings;
  }
}
