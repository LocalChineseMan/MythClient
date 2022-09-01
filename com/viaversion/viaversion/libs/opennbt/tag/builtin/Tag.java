package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;

public abstract class Tag implements Cloneable {
  public String toString() {
    String value = "";
    if (getValue() != null) {
      value = getValue().toString();
      if (getValue().getClass().isArray()) {
        StringBuilder build = new StringBuilder();
        build.append("[");
        for (int index = 0; index < Array.getLength(getValue()); index++) {
          if (index > 0)
            build.append(", "); 
          build.append(Array.get(getValue(), index));
        } 
        build.append("]");
        value = build.toString();
      } 
    } 
    return getClass().getSimpleName() + " { " + value + " }";
  }
  
  public abstract Tag clone();
  
  public abstract int getTagId();
  
  public abstract void write(DataOutput paramDataOutput) throws IOException;
  
  public abstract void read(DataInput paramDataInput) throws IOException;
  
  public abstract Object getValue();
}
