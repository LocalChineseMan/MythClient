package com.viaversion.viaversion.api.minecraft.metadata;

import com.google.common.base.Preconditions;
import java.util.Objects;

public final class Metadata {
  private int id;
  
  private MetaType metaType;
  
  private Object value;
  
  public Metadata(int id, MetaType metaType, Object value) {
    this.id = id;
    this.metaType = metaType;
    this.value = checkValue(metaType, value);
  }
  
  public int id() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public MetaType metaType() {
    return this.metaType;
  }
  
  public void setMetaType(MetaType metaType) {
    checkValue(metaType, this.value);
    this.metaType = metaType;
  }
  
  public <T> T value() {
    return (T)this.value;
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public void setValue(Object value) {
    this.value = checkValue(this.metaType, value);
  }
  
  public void setTypeAndValue(MetaType metaType, Object value) {
    this.value = checkValue(metaType, value);
    this.metaType = metaType;
  }
  
  private Object checkValue(MetaType metaType, Object value) {
    Preconditions.checkNotNull(metaType);
    if (value != null && !metaType.type().getOutputClass().isAssignableFrom(value.getClass()))
      throw new IllegalArgumentException("Metadata value and metaType are incompatible. Type=" + metaType + ", value=" + (
          (value != null) ? (value + " (" + value.getClass().getSimpleName() + ")") : "null")); 
    return value;
  }
  
  @Deprecated
  public void setMetaTypeUnsafe(MetaType type) {
    this.metaType = type;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    Metadata metadata = (Metadata)o;
    if (this.id != metadata.id)
      return false; 
    if (this.metaType != this.metaType)
      return false; 
    return Objects.equals(this.value, metadata.value);
  }
  
  public int hashCode() {
    int result = this.id;
    result = 31 * result + this.metaType.hashCode();
    result = 31 * result + ((this.value != null) ? this.value.hashCode() : 0);
    return result;
  }
  
  public String toString() {
    return "Metadata{id=" + this.id + ", metaType=" + this.metaType + ", value=" + this.value + '}';
  }
}
