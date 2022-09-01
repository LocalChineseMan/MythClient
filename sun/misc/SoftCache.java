package sun.misc;

import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftCache extends AbstractMap implements Map {
  private Map hash;
  
  private ReferenceQueue queue = new ReferenceQueue();
  
  private Set entrySet;
  
  private void processQueue() {
    ValueCell valueCell;
    while ((valueCell = (ValueCell)this.queue.poll()) != null) {
      if (ValueCell.access$000(valueCell)) {
        this.hash.remove(ValueCell.access$100(valueCell));
        continue;
      } 
      ValueCell.access$210();
    } 
  }
  
  public int size() {
    return entrySet().size();
  }
  
  public boolean isEmpty() {
    return entrySet().isEmpty();
  }
  
  public boolean containsKey(Object paramObject) {
    return (ValueCell.access$300(this.hash.get(paramObject), false) != null);
  }
  
  protected Object fill(Object paramObject) {
    return null;
  }
  
  public Object get(Object paramObject) {
    processQueue();
    Object object = this.hash.get(paramObject);
    if (object == null) {
      object = fill(paramObject);
      if (object != null) {
        this.hash.put(paramObject, ValueCell.access$400(paramObject, object, this.queue));
        return object;
      } 
    } 
    return ValueCell.access$300(object, false);
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    processQueue();
    ValueCell valueCell = ValueCell.access$400(paramObject1, paramObject2, this.queue);
    return ValueCell.access$300(this.hash.put(paramObject1, valueCell), true);
  }
  
  public Object remove(Object paramObject) {
    processQueue();
    return ValueCell.access$300(this.hash.remove(paramObject), true);
  }
  
  public void clear() {
    processQueue();
    this.hash.clear();
  }
  
  private static boolean valEquals(Object paramObject1, Object paramObject2) {
    return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2);
  }
  
  public SoftCache(int paramInt, float paramFloat) {
    this.entrySet = null;
    this.hash = new HashMap<>(paramInt, paramFloat);
  }
  
  public SoftCache(int paramInt) {
    this.entrySet = null;
    this.hash = new HashMap<>(paramInt);
  }
  
  public SoftCache() {
    this.entrySet = null;
    this.hash = new HashMap<>();
  }
  
  public Set entrySet() {
    if (this.entrySet == null)
      this.entrySet = (Set)new EntrySet(this, null); 
    return this.entrySet;
  }
  
  private class SoftCache {}
  
  private class SoftCache {}
  
  private static class SoftCache {}
}
