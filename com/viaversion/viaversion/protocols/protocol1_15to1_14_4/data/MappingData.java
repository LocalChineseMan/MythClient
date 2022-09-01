package com.viaversion.viaversion.protocols.protocol1_15to1_14_4.data;

import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class MappingData extends MappingDataBase {
  public MappingData() {
    super("1.14", "1.15", true);
  }
  
  protected Mappings loadFromArray(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings, String key) {
    if (!key.equals("sounds"))
      return super.loadFromArray(oldMappings, newMappings, diffMappings, key); 
    return (Mappings)new IntArrayMappings(oldMappings.getAsJsonArray(key), newMappings.getAsJsonArray(key), false);
  }
}
