package com.viaversion.viaversion.api.minecraft.item;

import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.Objects;

public class DataItem implements Item {
  @SerializedName(value = "identifier", alternate = {"id"})
  private int identifier;
  
  private byte amount;
  
  private short data;
  
  private CompoundTag tag;
  
  public DataItem() {}
  
  public DataItem(int identifier, byte amount, short data, CompoundTag tag) {
    this.identifier = identifier;
    this.amount = amount;
    this.data = data;
    this.tag = tag;
  }
  
  public DataItem(Item toCopy) {
    this(toCopy.identifier(), (byte)toCopy.amount(), toCopy.data(), toCopy.tag());
  }
  
  public int identifier() {
    return this.identifier;
  }
  
  public void setIdentifier(int identifier) {
    this.identifier = identifier;
  }
  
  public int amount() {
    return this.amount;
  }
  
  public void setAmount(int amount) {
    if (amount > 127 || amount < -128)
      throw new IllegalArgumentException("Invalid item amount: " + amount); 
    this.amount = (byte)amount;
  }
  
  public short data() {
    return this.data;
  }
  
  public void setData(short data) {
    this.data = data;
  }
  
  public CompoundTag tag() {
    return this.tag;
  }
  
  public void setTag(CompoundTag tag) {
    this.tag = tag;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    DataItem item = (DataItem)o;
    if (this.identifier != item.identifier)
      return false; 
    if (this.amount != item.amount)
      return false; 
    if (this.data != item.data)
      return false; 
    return Objects.equals(this.tag, item.tag);
  }
  
  public int hashCode() {
    int result = this.identifier;
    result = 31 * result + this.amount;
    result = 31 * result + this.data;
    result = 31 * result + ((this.tag != null) ? this.tag.hashCode() : 0);
    return result;
  }
  
  public String toString() {
    return "Item{identifier=" + this.identifier + ", amount=" + this.amount + ", data=" + this.data + ", tag=" + this.tag + '}';
  }
}
