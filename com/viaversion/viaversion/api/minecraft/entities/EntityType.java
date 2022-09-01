package com.viaversion.viaversion.api.minecraft.entities;

public interface EntityType {
  int getId();
  
  EntityType getParent();
  
  String name();
  
  boolean is(EntityType... types) {
    for (EntityType type : types) {
      if (this == type)
        return true; 
    } 
    return false;
  }
  
  default boolean is(EntityType type) {
    return (this == type);
  }
  
  default boolean isOrHasParent(EntityType type) {
    EntityType parent = this;
    do {
      if (parent == type)
        return true; 
      parent = parent.getParent();
    } while (parent != null);
    return false;
  }
}
