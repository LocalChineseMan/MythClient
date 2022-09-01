package com.viaversion.viaversion.api.minecraft;

import java.util.HashMap;
import java.util.Map;

public enum RegistryType {
  BLOCK("block"),
  ITEM("item"),
  FLUID("fluid"),
  ENTITY("entity_type"),
  GAME_EVENT("game_event");
  
  private static final Map<String, RegistryType> MAP;
  
  private static final RegistryType[] VALUES;
  
  private final String resourceLocation;
  
  static {
    MAP = new HashMap<>();
    VALUES = values();
    for (RegistryType type : getValues())
      MAP.put(type.resourceLocation, type); 
  }
  
  public static RegistryType[] getValues() {
    return VALUES;
  }
  
  public static RegistryType getByKey(String resourceKey) {
    return MAP.get(resourceKey);
  }
  
  RegistryType(String resourceLocation) {
    this.resourceLocation = resourceLocation;
  }
  
  public String getResourceLocation() {
    return this.resourceLocation;
  }
}
