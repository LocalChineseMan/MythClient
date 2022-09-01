package com.viaversion.viabackwards.api.entities.storage;

public class EntityObjectData extends EntityData {
  private final boolean isObject;
  
  private final int objectData;
  
  public EntityObjectData(int id, boolean isObject, int replacementId, int objectData) {
    super(id, replacementId);
    this.isObject = isObject;
    this.objectData = objectData;
  }
  
  public boolean isObjectType() {
    return this.isObject;
  }
  
  public int objectData() {
    return this.objectData;
  }
}
