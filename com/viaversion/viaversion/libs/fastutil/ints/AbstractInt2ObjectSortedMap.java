package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractInt2ObjectSortedMap<V> extends AbstractInt2ObjectMap<V> implements Int2ObjectSortedMap<V> {
  private static final long serialVersionUID = -1773560792952436569L;
  
  public IntSortedSet keySet() {
    return (IntSortedSet)new KeySet(this);
  }
  
  public ObjectCollection<V> values() {
    return (ObjectCollection<V>)new ValuesCollection(this);
  }
  
  protected class AbstractInt2ObjectSortedMap {}
  
  protected class AbstractInt2ObjectSortedMap {}
  
  protected static class AbstractInt2ObjectSortedMap {}
  
  protected static class AbstractInt2ObjectSortedMap {}
}
