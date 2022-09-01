package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class PistonHandler implements BackwardsBlockEntityProvider.BackwardsBlockEntityHandler {
  private final Map<String, Integer> pistonIds = new HashMap<>();
  
  public PistonHandler() {
    if (Via.getConfig().isServersideBlockConnections()) {
      Map<String, Integer> keyToId;
      try {
        Field field = ConnectionData.class.getDeclaredField("keyToId");
        field.setAccessible(true);
        keyToId = (Map<String, Integer>)field.get(null);
      } catch (IllegalAccessException|NoSuchFieldException e) {
        e.printStackTrace();
        return;
      } 
      for (Map.Entry<String, Integer> entry : keyToId.entrySet()) {
        if (!((String)entry.getKey()).contains("piston"))
          continue; 
        addEntries(entry.getKey(), ((Integer)entry.getValue()).intValue());
      } 
    } else {
      JsonObject mappings = ((JsonObject)MappingDataLoader.getMappingsCache().get("mapping-1.13.json")).getAsJsonObject("blockstates");
      for (Map.Entry<String, JsonElement> blockState : (Iterable<Map.Entry<String, JsonElement>>)mappings.entrySet()) {
        String key = ((JsonElement)blockState.getValue()).getAsString();
        if (!key.contains("piston"))
          continue; 
        addEntries(key, Integer.parseInt(blockState.getKey()));
      } 
    } 
  }
  
  private void addEntries(String data, int id) {
    id = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(id);
    this.pistonIds.put(data, Integer.valueOf(id));
    String substring = data.substring(10);
    if (!substring.startsWith("piston") && !substring.startsWith("sticky_piston"))
      return; 
    String[] split = data.substring(0, data.length() - 1).split("\\[");
    String[] properties = split[1].split(",");
    data = split[0] + "[" + properties[1] + "," + properties[0] + "]";
    this.pistonIds.put(data, Integer.valueOf(id));
  }
  
  public CompoundTag transform(UserConnection user, int blockId, CompoundTag tag) {
    CompoundTag blockState = (CompoundTag)tag.get("blockState");
    if (blockState == null)
      return tag; 
    String dataFromTag = getDataFromTag(blockState);
    if (dataFromTag == null)
      return tag; 
    Integer id = this.pistonIds.get(dataFromTag);
    if (id == null)
      return tag; 
    tag.put("blockId", (Tag)new IntTag(id.intValue() >> 4));
    tag.put("blockData", (Tag)new IntTag(id.intValue() & 0xF));
    return tag;
  }
  
  private String getDataFromTag(CompoundTag tag) {
    StringTag name = (StringTag)tag.get("Name");
    if (name == null)
      return null; 
    CompoundTag properties = (CompoundTag)tag.get("Properties");
    if (properties == null)
      return name.getValue(); 
    StringJoiner joiner = new StringJoiner(",", name.getValue() + "[", "]");
    for (Map.Entry<String, Tag> entry : (Iterable<Map.Entry<String, Tag>>)properties) {
      if (!(entry.getValue() instanceof StringTag))
        continue; 
      joiner.add((String)entry.getKey() + "=" + ((StringTag)entry.getValue()).getValue());
    } 
    return joiner.toString();
  }
}
