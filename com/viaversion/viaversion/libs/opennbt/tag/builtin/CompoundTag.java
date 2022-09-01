package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.libs.opennbt.tag.TagCreateException;
import com.viaversion.viaversion.libs.opennbt.tag.TagRegistry;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class CompoundTag extends Tag implements Iterable<Map.Entry<String, Tag>> {
  public static final int ID = 10;
  
  private Map<String, Tag> value;
  
  public CompoundTag() {
    this(new LinkedHashMap<>());
  }
  
  public CompoundTag(Map<String, Tag> value) {
    this.value = new LinkedHashMap<>(value);
  }
  
  public CompoundTag(LinkedHashMap<String, Tag> value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }
  
  public Map<String, Tag> getValue() {
    return this.value;
  }
  
  public void setValue(Map<String, Tag> value) {
    Preconditions.checkNotNull(value);
    this.value = new LinkedHashMap<>(value);
  }
  
  public void setValue(LinkedHashMap<String, Tag> value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }
  
  public boolean isEmpty() {
    return this.value.isEmpty();
  }
  
  public boolean contains(String tagName) {
    return this.value.containsKey(tagName);
  }
  
  @Nullable
  public <T extends Tag> T get(String tagName) {
    return (T)this.value.get(tagName);
  }
  
  @Nullable
  public <T extends Tag> T put(String tagName, T tag) {
    return (T)this.value.put(tagName, (Tag)tag);
  }
  
  @Nullable
  public <T extends Tag> T remove(String tagName) {
    return (T)this.value.remove(tagName);
  }
  
  public Set<String> keySet() {
    return this.value.keySet();
  }
  
  public Collection<Tag> values() {
    return this.value.values();
  }
  
  public Set<Map.Entry<String, Tag>> entrySet() {
    return this.value.entrySet();
  }
  
  public int size() {
    return this.value.size();
  }
  
  public void clear() {
    this.value.clear();
  }
  
  public Iterator<Map.Entry<String, Tag>> iterator() {
    return this.value.entrySet().iterator();
  }
  
  public void read(DataInput in) throws IOException {
    try {
      while (true) {
        int id = in.readByte();
        if (id == 0)
          break; 
        String name = in.readUTF();
        Tag tag = TagRegistry.createInstance(id);
        tag.read(in);
        this.value.put(name, tag);
      } 
    } catch (TagCreateException e) {
      throw new IOException("Failed to create tag.", e);
    } catch (EOFException e) {
      throw new IOException("Closing tag was not found!");
    } 
  }
  
  public void write(DataOutput out) throws IOException {
    for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
      Tag tag = entry.getValue();
      out.writeByte(tag.getTagId());
      out.writeUTF(entry.getKey());
      tag.write(out);
    } 
    out.writeByte(0);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    CompoundTag tags = (CompoundTag)o;
    return this.value.equals(tags.value);
  }
  
  public int hashCode() {
    return this.value.hashCode();
  }
  
  public final CompoundTag clone() {
    LinkedHashMap<String, Tag> newMap = new LinkedHashMap<>();
    for (Map.Entry<String, Tag> entry : this.value.entrySet())
      newMap.put(entry.getKey(), ((Tag)entry.getValue()).clone()); 
    return new CompoundTag(newMap);
  }
  
  public int getTagId() {
    return 10;
  }
}
