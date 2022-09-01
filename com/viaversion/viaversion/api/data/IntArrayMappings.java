package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Arrays;

public class IntArrayMappings implements Mappings {
  protected final int[] oldToNew;
  
  public IntArrayMappings(int[] oldToNew) {
    this.oldToNew = oldToNew;
  }
  
  public IntArrayMappings(int size, JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping) {
    this.oldToNew = new int[size];
    Arrays.fill(this.oldToNew, -1);
    MappingDataLoader.mapIdentifiers(this.oldToNew, oldMapping, newMapping, diffMapping);
  }
  
  public IntArrayMappings(JsonObject oldMapping, JsonObject newMapping, JsonObject diffMapping) {
    this(oldMapping.entrySet().size(), oldMapping, newMapping, diffMapping);
  }
  
  public IntArrayMappings(int size, JsonObject oldMapping, JsonObject newMapping) {
    this.oldToNew = new int[size];
    Arrays.fill(this.oldToNew, -1);
    MappingDataLoader.mapIdentifiers(this.oldToNew, oldMapping, newMapping);
  }
  
  public IntArrayMappings(JsonObject oldMapping, JsonObject newMapping) {
    this(oldMapping.entrySet().size(), oldMapping, newMapping);
  }
  
  public IntArrayMappings(int size, JsonArray oldMapping, JsonArray newMapping, JsonObject diffMapping, boolean warnOnMissing) {
    this.oldToNew = new int[size];
    Arrays.fill(this.oldToNew, -1);
    MappingDataLoader.mapIdentifiers(this.oldToNew, oldMapping, newMapping, diffMapping, warnOnMissing);
  }
  
  public IntArrayMappings(int size, JsonArray oldMapping, JsonArray newMapping, boolean warnOnMissing) {
    this(size, oldMapping, newMapping, null, warnOnMissing);
  }
  
  public IntArrayMappings(JsonArray oldMapping, JsonArray newMapping, boolean warnOnMissing) {
    this(oldMapping.size(), oldMapping, newMapping, warnOnMissing);
  }
  
  public IntArrayMappings(int size, JsonArray oldMapping, JsonArray newMapping) {
    this(size, oldMapping, newMapping, true);
  }
  
  public IntArrayMappings(JsonArray oldMapping, JsonArray newMapping, JsonObject diffMapping) {
    this(oldMapping.size(), oldMapping, newMapping, diffMapping, true);
  }
  
  public IntArrayMappings(JsonArray oldMapping, JsonArray newMapping) {
    this(oldMapping.size(), oldMapping, newMapping, true);
  }
  
  public int getNewId(int id) {
    return (id >= 0 && id < this.oldToNew.length) ? this.oldToNew[id] : -1;
  }
  
  public void setNewId(int id, int newId) {
    this.oldToNew[id] = newId;
  }
  
  public int[] getOldToNew() {
    return this.oldToNew;
  }
}
