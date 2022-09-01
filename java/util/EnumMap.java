package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import sun.misc.SharedSecrets;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Serializable, Cloneable {
  private final Class<K> keyType;
  
  private transient K[] keyUniverse;
  
  private transient Object[] vals;
  
  private transient int size = 0;
  
  private static final Object NULL = new Object() {
      public int hashCode() {
        return 0;
      }
      
      public String toString() {
        return "java.util.EnumMap.NULL";
      }
    };
  
  private Object maskNull(Object paramObject) {
    return (paramObject == null) ? NULL : paramObject;
  }
  
  private V unmaskNull(Object paramObject) {
    return (paramObject == NULL) ? null : (V)paramObject;
  }
  
  private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = (Enum<?>[])new Enum[0];
  
  private transient Set<Map.Entry<K, V>> entrySet;
  
  private static final long serialVersionUID = 458661240069192865L;
  
  public EnumMap(Class<K> paramClass) {
    this.keyType = paramClass;
    this.keyUniverse = getKeyUniverse(paramClass);
    this.vals = new Object[this.keyUniverse.length];
  }
  
  public EnumMap(EnumMap<K, ? extends V> paramEnumMap) {
    this.keyType = paramEnumMap.keyType;
    this.keyUniverse = paramEnumMap.keyUniverse;
    this.vals = (Object[])paramEnumMap.vals.clone();
    this.size = paramEnumMap.size;
  }
  
  public EnumMap(Map<K, ? extends V> paramMap) {
    if (paramMap instanceof EnumMap) {
      EnumMap enumMap = (EnumMap)paramMap;
      this.keyType = enumMap.keyType;
      this.keyUniverse = enumMap.keyUniverse;
      this.vals = (Object[])enumMap.vals.clone();
      this.size = enumMap.size;
    } else {
      if (paramMap.isEmpty())
        throw new IllegalArgumentException("Specified map is empty"); 
      this.keyType = ((Enum<K>)paramMap.keySet().iterator().next()).getDeclaringClass();
      this.keyUniverse = getKeyUniverse(this.keyType);
      this.vals = new Object[this.keyUniverse.length];
      putAll(paramMap);
    } 
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean containsValue(Object paramObject) {
    paramObject = maskNull(paramObject);
    for (Object object : this.vals) {
      if (paramObject.equals(object))
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(Object paramObject) {
    return (isValidKey(paramObject) && this.vals[((Enum)paramObject).ordinal()] != null);
  }
  
  private boolean containsMapping(Object paramObject1, Object paramObject2) {
    return (isValidKey(paramObject1) && 
      maskNull(paramObject2).equals(this.vals[((Enum)paramObject1).ordinal()]));
  }
  
  public V get(Object paramObject) {
    return isValidKey(paramObject) ? 
      unmaskNull(this.vals[((Enum)paramObject).ordinal()]) : null;
  }
  
  public V put(K paramK, V paramV) {
    typeCheck(paramK);
    int i = paramK.ordinal();
    Object object = this.vals[i];
    this.vals[i] = maskNull(paramV);
    if (object == null)
      this.size++; 
    return unmaskNull(object);
  }
  
  public V remove(Object paramObject) {
    if (!isValidKey(paramObject))
      return null; 
    int i = ((Enum)paramObject).ordinal();
    Object object = this.vals[i];
    this.vals[i] = null;
    if (object != null)
      this.size--; 
    return unmaskNull(object);
  }
  
  private boolean removeMapping(Object paramObject1, Object paramObject2) {
    if (!isValidKey(paramObject1))
      return false; 
    int i = ((Enum)paramObject1).ordinal();
    if (maskNull(paramObject2).equals(this.vals[i])) {
      this.vals[i] = null;
      this.size--;
      return true;
    } 
    return false;
  }
  
  private boolean isValidKey(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class<?> clazz = paramObject.getClass();
    return (clazz == this.keyType || clazz.getSuperclass() == this.keyType);
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    if (paramMap instanceof EnumMap) {
      EnumMap enumMap = (EnumMap)paramMap;
      if (enumMap.keyType != this.keyType) {
        if (enumMap.isEmpty())
          return; 
        throw new ClassCastException(enumMap.keyType + " != " + this.keyType);
      } 
      for (byte b = 0; b < this.keyUniverse.length; b++) {
        Object object = enumMap.vals[b];
        if (object != null) {
          if (this.vals[b] == null)
            this.size++; 
          this.vals[b] = object;
        } 
      } 
    } else {
      super.putAll(paramMap);
    } 
  }
  
  public void clear() {
    Arrays.fill(this.vals, (Object)null);
    this.size = 0;
  }
  
  public Set<K> keySet() {
    Set<K> set = this.keySet;
    if (set != null)
      return set; 
    return this.keySet = new KeySet();
  }
  
  private class KeySet extends AbstractSet<K> {
    private KeySet() {}
    
    public Iterator<K> iterator() {
      return new EnumMap.KeyIterator();
    }
    
    public int size() {
      return EnumMap.this.size;
    }
    
    public boolean contains(Object param1Object) {
      return EnumMap.this.containsKey(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      int i = EnumMap.this.size;
      EnumMap.this.remove(param1Object);
      return (EnumMap.this.size != i);
    }
    
    public void clear() {
      EnumMap.this.clear();
    }
  }
  
  public Collection<V> values() {
    Collection<V> collection = this.values;
    if (collection != null)
      return collection; 
    return this.values = new Values();
  }
  
  private class Values extends AbstractCollection<V> {
    private Values() {}
    
    public Iterator<V> iterator() {
      return new EnumMap.ValueIterator();
    }
    
    public int size() {
      return EnumMap.this.size;
    }
    
    public boolean contains(Object param1Object) {
      return EnumMap.this.containsValue(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      param1Object = EnumMap.this.maskNull(param1Object);
      for (byte b = 0; b < EnumMap.this.vals.length; b++) {
        if (param1Object.equals(EnumMap.this.vals[b])) {
          EnumMap.this.vals[b] = null;
          EnumMap.this.size--;
          return true;
        } 
      } 
      return false;
    }
    
    public void clear() {
      EnumMap.this.clear();
    }
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> set = this.entrySet;
    if (set != null)
      return set; 
    return this.entrySet = new EntrySet();
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new EnumMap.EntryIterator();
    }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return EnumMap.this.containsMapping(entry.getKey(), entry.getValue());
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return EnumMap.this.removeMapping(entry.getKey(), entry.getValue());
    }
    
    public int size() {
      return EnumMap.this.size;
    }
    
    public void clear() {
      EnumMap.this.clear();
    }
    
    public Object[] toArray() {
      return fillEntryArray(new Object[EnumMap.this.size]);
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = size();
      if (param1ArrayOfT.length < i)
        param1ArrayOfT = (T[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), i); 
      if (param1ArrayOfT.length > i)
        param1ArrayOfT[i] = null; 
      return (T[])fillEntryArray((Object[])param1ArrayOfT);
    }
    
    private Object[] fillEntryArray(Object[] param1ArrayOfObject) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < EnumMap.this.vals.length; b2++) {
        if (EnumMap.this.vals[b2] != null)
          param1ArrayOfObject[b1++] = new AbstractMap.SimpleEntry<>(EnumMap.this
              .keyUniverse[b2], EnumMap.this.unmaskNull(EnumMap.this.vals[b2])); 
      } 
      return param1ArrayOfObject;
    }
  }
  
  private abstract class EnumMapIterator<T> implements Iterator<T> {
    int index = 0;
    
    int lastReturnedIndex = -1;
    
    public boolean hasNext() {
      while (this.index < EnumMap.this.vals.length && EnumMap.this.vals[this.index] == null)
        this.index++; 
      return (this.index != EnumMap.this.vals.length);
    }
    
    public void remove() {
      checkLastReturnedIndex();
      if (EnumMap.this.vals[this.lastReturnedIndex] != null) {
        EnumMap.this.vals[this.lastReturnedIndex] = null;
        EnumMap.this.size--;
      } 
      this.lastReturnedIndex = -1;
    }
    
    private void checkLastReturnedIndex() {
      if (this.lastReturnedIndex < 0)
        throw new IllegalStateException(); 
    }
    
    private EnumMapIterator() {}
  }
  
  private class KeyIterator extends EnumMapIterator<K> {
    private KeyIterator() {}
    
    public K next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.lastReturnedIndex = this.index++;
      return (K)EnumMap.this.keyUniverse[this.lastReturnedIndex];
    }
  }
  
  private class ValueIterator extends EnumMapIterator<V> {
    private ValueIterator() {}
    
    public V next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.lastReturnedIndex = this.index++;
      return EnumMap.this.unmaskNull(EnumMap.this.vals[this.lastReturnedIndex]);
    }
  }
  
  private class EntryIterator extends EnumMapIterator<Map.Entry<K, V>> {
    private Entry lastReturnedEntry;
    
    private EntryIterator() {}
    
    public Map.Entry<K, V> next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.lastReturnedEntry = new Entry(this.index++);
      return this.lastReturnedEntry;
    }
    
    public void remove() {
      this
        .lastReturnedIndex = (null == this.lastReturnedEntry) ? -1 : this.lastReturnedEntry.index;
      super.remove();
      this.lastReturnedEntry.index = this.lastReturnedIndex;
      this.lastReturnedEntry = null;
    }
    
    private class Entry implements Map.Entry<K, V> {
      private int index;
      
      private Entry(int param2Int) {
        this.index = param2Int;
      }
      
      public K getKey() {
        checkIndexForEntryUse();
        return (K)EnumMap.this.keyUniverse[this.index];
      }
      
      public V getValue() {
        checkIndexForEntryUse();
        return EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
      }
      
      public V setValue(V param2V) {
        checkIndexForEntryUse();
        Object object = EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
        EnumMap.this.vals[this.index] = EnumMap.this.maskNull(param2V);
        return (V)object;
      }
      
      public boolean equals(Object param2Object) {
        if (this.index < 0)
          return (param2Object == this); 
        if (!(param2Object instanceof Map.Entry))
          return false; 
        Map.Entry entry = (Map.Entry)param2Object;
        Object object1 = EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
        Object object2 = entry.getValue();
        return (entry.getKey() == EnumMap.this.keyUniverse[this.index] && (object1 == object2 || (object1 != null && object1
          
          .equals(object2))));
      }
      
      public int hashCode() {
        if (this.index < 0)
          return super.hashCode(); 
        return EnumMap.this.entryHashCode(this.index);
      }
      
      public String toString() {
        if (this.index < 0)
          return super.toString(); 
        return EnumMap.this.keyUniverse[this.index] + "=" + EnumMap.this
          .unmaskNull(EnumMap.this.vals[this.index]);
      }
      
      private void checkIndexForEntryUse() {
        if (this.index < 0)
          throw new IllegalStateException("Entry was removed"); 
      }
    }
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof EnumMap)
      return equals((EnumMap<?, ?>)paramObject); 
    if (!(paramObject instanceof Map))
      return false; 
    Map map = (Map)paramObject;
    if (this.size != map.size())
      return false; 
    for (byte b = 0; b < this.keyUniverse.length; b++) {
      if (null != this.vals[b]) {
        K k = this.keyUniverse[b];
        V v = unmaskNull(this.vals[b]);
        if (null == v) {
          if (null != map.get(k) || !map.containsKey(k))
            return false; 
        } else if (!v.equals(map.get(k))) {
          return false;
        } 
      } 
    } 
    return true;
  }
  
  private boolean equals(EnumMap<?, ?> paramEnumMap) {
    if (paramEnumMap.keyType != this.keyType)
      return (this.size == 0 && paramEnumMap.size == 0); 
    for (byte b = 0; b < this.keyUniverse.length; b++) {
      Object object1 = this.vals[b];
      Object object2 = paramEnumMap.vals[b];
      if (object2 != object1 && (object2 == null || 
        !object2.equals(object1)))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.keyUniverse.length; b++) {
      if (null != this.vals[b])
        i += entryHashCode(b); 
    } 
    return i;
  }
  
  private int entryHashCode(int paramInt) {
    return this.keyUniverse[paramInt].hashCode() ^ this.vals[paramInt].hashCode();
  }
  
  public EnumMap<K, V> clone() {
    EnumMap<K, V> enumMap = null;
    try {
      enumMap = (EnumMap)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError();
    } 
    enumMap.vals = (Object[])enumMap.vals.clone();
    enumMap.entrySet = null;
    return enumMap;
  }
  
  private void typeCheck(K paramK) {
    Class<?> clazz = paramK.getClass();
    if (clazz != this.keyType && clazz.getSuperclass() != this.keyType)
      throw new ClassCastException(clazz + " != " + this.keyType); 
  }
  
  private static <K extends Enum<K>> K[] getKeyUniverse(Class<K> paramClass) {
    return (K[])SharedSecrets.getJavaLangAccess().getEnumConstantsShared(paramClass);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    int i = this.size;
    for (byte b = 0; i > 0; b++) {
      if (null != this.vals[b]) {
        paramObjectOutputStream.writeObject(this.keyUniverse[b]);
        paramObjectOutputStream.writeObject(unmaskNull(this.vals[b]));
        i--;
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.keyUniverse = getKeyUniverse(this.keyType);
    this.vals = new Object[this.keyUniverse.length];
    int i = paramObjectInputStream.readInt();
    for (byte b = 0; b < i; b++) {
      Enum enum_ = (Enum)paramObjectInputStream.readObject();
      Object object = paramObjectInputStream.readObject();
      put((K)enum_, (V)object);
    } 
  }
}
