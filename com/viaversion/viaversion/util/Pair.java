package com.viaversion.viaversion.util;

import java.util.Objects;

public class Pair<X, Y> {
  private final X key;
  
  private Y value;
  
  public Pair(X key, Y value) {
    this.key = key;
    this.value = value;
  }
  
  public X getKey() {
    return this.key;
  }
  
  public Y getValue() {
    return this.value;
  }
  
  public void setValue(Y value) {
    this.value = value;
  }
  
  public String toString() {
    return "Pair{" + this.key + ", " + this.value + '}';
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    Pair<?, ?> pair = (Pair<?, ?>)o;
    if (!Objects.equals(this.key, pair.key))
      return false; 
    return Objects.equals(this.value, pair.value);
  }
  
  public int hashCode() {
    int result = (this.key != null) ? this.key.hashCode() : 0;
    result = 31 * result + ((this.value != null) ? this.value.hashCode() : 0);
    return result;
  }
}
