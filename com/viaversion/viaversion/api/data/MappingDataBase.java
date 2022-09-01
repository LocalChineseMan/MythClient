package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.Int2IntBiHashMap;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MappingDataBase implements MappingData {
  protected final String oldVersion;
  
  protected final String newVersion;
  
  protected final boolean hasDiffFile;
  
  protected Int2IntBiMap itemMappings;
  
  protected ParticleMappings particleMappings;
  
  protected Mappings blockMappings;
  
  protected Mappings blockStateMappings;
  
  protected Mappings soundMappings;
  
  protected Mappings statisticsMappings;
  
  protected Map<RegistryType, List<TagData>> tags;
  
  protected boolean loadItems = true;
  
  public MappingDataBase(String oldVersion, String newVersion) {
    this(oldVersion, newVersion, false);
  }
  
  public MappingDataBase(String oldVersion, String newVersion, boolean hasDiffFile) {
    this.oldVersion = oldVersion;
    this.newVersion = newVersion;
    this.hasDiffFile = hasDiffFile;
  }
  
  public void load() {
    getLogger().info("Loading " + this.oldVersion + " -> " + this.newVersion + " mappings...");
    JsonObject diffmapping = this.hasDiffFile ? loadDiffFile() : null;
    JsonObject oldMappings = MappingDataLoader.loadData("mapping-" + this.oldVersion + ".json", true);
    JsonObject newMappings = MappingDataLoader.loadData("mapping-" + this.newVersion + ".json", true);
    this.blockMappings = loadFromObject(oldMappings, newMappings, diffmapping, "blocks");
    this.blockStateMappings = loadFromObject(oldMappings, newMappings, diffmapping, "blockstates");
    this.soundMappings = loadFromArray(oldMappings, newMappings, diffmapping, "sounds");
    this.statisticsMappings = loadFromArray(oldMappings, newMappings, diffmapping, "statistics");
    Mappings particles = loadFromArray(oldMappings, newMappings, diffmapping, "particles");
    if (particles != null)
      this.particleMappings = new ParticleMappings(oldMappings.getAsJsonArray("particles"), particles); 
    if (this.loadItems && newMappings.has("items")) {
      this.itemMappings = (Int2IntBiMap)new Int2IntBiHashMap();
      this.itemMappings.defaultReturnValue(-1);
      MappingDataLoader.mapIdentifiers(this.itemMappings, oldMappings.getAsJsonObject("items"), newMappings.getAsJsonObject("items"), 
          (diffmapping != null) ? diffmapping.getAsJsonObject("items") : null);
    } 
    if (diffmapping != null && diffmapping.has("tags")) {
      this.tags = new EnumMap<>(RegistryType.class);
      JsonObject tags = diffmapping.getAsJsonObject("tags");
      if (tags.has(RegistryType.ITEM.getResourceLocation()))
        loadTags(RegistryType.ITEM, tags, MappingDataLoader.indexedObjectToMap(newMappings.getAsJsonObject("items"))); 
      if (tags.has(RegistryType.BLOCK.getResourceLocation()))
        loadTags(RegistryType.BLOCK, tags, MappingDataLoader.indexedObjectToMap(newMappings.getAsJsonObject("blocks"))); 
    } 
    loadExtras(oldMappings, newMappings, diffmapping);
  }
  
  private void loadTags(RegistryType type, JsonObject object, Object2IntMap<String> typeMapping) {
    JsonObject tags = object.getAsJsonObject(type.getResourceLocation());
    List<TagData> tagsList = new ArrayList<>(tags.size());
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)tags.entrySet()) {
      JsonArray array = ((JsonElement)entry.getValue()).getAsJsonArray();
      int[] entries = new int[array.size()];
      int i = 0;
      for (JsonElement element : array) {
        String stringId = element.getAsString();
        if (!typeMapping.containsKey(stringId) && !typeMapping.containsKey(stringId = stringId.replace("minecraft:", ""))) {
          getLogger().warning(type + " Tags contains invalid type identifier " + stringId + " in tag " + (String)entry.getKey());
          continue;
        } 
        entries[i++] = typeMapping.getInt(stringId);
      } 
      tagsList.add(new TagData(entry.getKey(), entries));
    } 
    this.tags.put(type, tagsList);
  }
  
  public int getNewBlockStateId(int id) {
    return checkValidity(id, this.blockStateMappings.getNewId(id), "blockstate");
  }
  
  public int getNewBlockId(int id) {
    return checkValidity(id, this.blockMappings.getNewId(id), "block");
  }
  
  public int getNewItemId(int id) {
    return checkValidity(id, this.itemMappings.get(id), "item");
  }
  
  public int getOldItemId(int id) {
    int oldId = this.itemMappings.inverse().get(id);
    return (oldId != -1) ? oldId : 1;
  }
  
  public int getNewParticleId(int id) {
    return checkValidity(id, this.particleMappings.getMappings().getNewId(id), "particles");
  }
  
  public List<TagData> getTags(RegistryType type) {
    return (this.tags != null) ? this.tags.get(type) : null;
  }
  
  public Int2IntBiMap getItemMappings() {
    return this.itemMappings;
  }
  
  public ParticleMappings getParticleMappings() {
    return this.particleMappings;
  }
  
  public Mappings getBlockMappings() {
    return this.blockMappings;
  }
  
  public Mappings getBlockStateMappings() {
    return this.blockStateMappings;
  }
  
  public Mappings getSoundMappings() {
    return this.soundMappings;
  }
  
  public Mappings getStatisticsMappings() {
    return this.statisticsMappings;
  }
  
  protected Mappings loadFromArray(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings, String key) {
    if (!oldMappings.has(key) || !newMappings.has(key))
      return null; 
    JsonObject diff = (diffMappings != null) ? diffMappings.getAsJsonObject(key) : null;
    return new IntArrayMappings(oldMappings.getAsJsonArray(key), newMappings.getAsJsonArray(key), diff);
  }
  
  protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings, String key) {
    if (!oldMappings.has(key) || !newMappings.has(key))
      return null; 
    JsonObject diff = (diffMappings != null) ? diffMappings.getAsJsonObject(key) : null;
    return new IntArrayMappings(oldMappings.getAsJsonObject(key), newMappings.getAsJsonObject(key), diff);
  }
  
  protected JsonObject loadDiffFile() {
    return MappingDataLoader.loadData("mappingdiff-" + this.oldVersion + "to" + this.newVersion + ".json");
  }
  
  protected Logger getLogger() {
    return Via.getPlatform().getLogger();
  }
  
  protected int checkValidity(int id, int mappedId, String type) {
    if (mappedId == -1) {
      getLogger().warning(String.format("Missing %s %s for %s %s %d", new Object[] { this.newVersion, type, this.oldVersion, type, Integer.valueOf(id) }));
      return 0;
    } 
    return mappedId;
  }
  
  protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {}
}
