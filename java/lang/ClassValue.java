package java.lang;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassValue<T> {
  protected abstract T computeValue(Class<?> paramClass);
  
  public T get(Class<?> paramClass) {
    Entry[] arrayOfEntry;
    Entry<?> entry = ClassValueMap.probeHomeLocation((Entry<?>[])(arrayOfEntry = (Entry[])getCacheCarefully(paramClass)), this);
    if (match(entry))
      return (T)entry.value(); 
    return getFromBackup((Entry<?>[])arrayOfEntry, paramClass);
  }
  
  public void remove(Class<?> paramClass) {
    ClassValueMap classValueMap = getMap(paramClass);
    classValueMap.removeEntry(this);
  }
  
  void put(Class<?> paramClass, T paramT) {
    ClassValueMap classValueMap = getMap(paramClass);
    classValueMap.changeEntry(this, paramT);
  }
  
  private static Entry<?>[] getCacheCarefully(Class<?> paramClass) {
    ClassValueMap classValueMap = paramClass.classValueMap;
    if (classValueMap == null)
      return EMPTY_CACHE; 
    return classValueMap.getCache();
  }
  
  private static final Entry<?>[] EMPTY_CACHE = new Entry[] { null };
  
  private T getFromBackup(Entry<?>[] paramArrayOfEntry, Class<?> paramClass) {
    Entry<?> entry = ClassValueMap.probeBackupLocations(paramArrayOfEntry, this);
    if (entry != null)
      return (T)entry.value(); 
    return getFromHashMap(paramClass);
  }
  
  Entry<T> castEntry(Entry<?> paramEntry) {
    return (Entry)paramEntry;
  }
  
  private T getFromHashMap(Class<?> paramClass) {
    ClassValueMap classValueMap = getMap(paramClass);
    while (true) {
      Entry<?> entry = classValueMap.startEntry(this);
      if (!entry.isPromise())
        return (T)entry.value(); 
      try {
        entry = makeEntry(entry.version(), computeValue(paramClass));
      } finally {
        entry = classValueMap.finishEntry(this, entry);
      } 
      if (entry != null)
        return (T)entry.value(); 
    } 
  }
  
  boolean match(Entry<?> paramEntry) {
    return (paramEntry != null && paramEntry.get() == this.version);
  }
  
  final int hashCodeForCache = nextHashCode.getAndAdd(1640531527) & 0x3FFFFFFF;
  
  private static final AtomicInteger nextHashCode = new AtomicInteger();
  
  private static final int HASH_INCREMENT = 1640531527;
  
  static final int HASH_MASK = 1073741823;
  
  static class Identity {}
  
  final Identity identity = new Identity();
  
  private volatile Version<T> version = new Version<>(this);
  
  Version<T> version() {
    return this.version;
  }
  
  void bumpVersion() {
    this.version = new Version<>(this);
  }
  
  static class Version<T> {
    private final ClassValue<T> classValue;
    
    private final ClassValue.Entry<T> promise = new ClassValue.Entry<>(this);
    
    Version(ClassValue<T> param1ClassValue) {
      this.classValue = param1ClassValue;
    }
    
    ClassValue<T> classValue() {
      return this.classValue;
    }
    
    ClassValue.Entry<T> promise() {
      return this.promise;
    }
    
    boolean isLive() {
      return (this.classValue.version() == this);
    }
  }
  
  static class Entry<T> extends WeakReference<Version<T>> {
    final Object value;
    
    Entry(ClassValue.Version<T> param1Version, T param1T) {
      super(param1Version);
      this.value = param1T;
    }
    
    private void assertNotPromise() {
      assert !isPromise();
    }
    
    Entry(ClassValue.Version<T> param1Version) {
      super(param1Version);
      this.value = this;
    }
    
    T value() {
      assertNotPromise();
      return (T)this.value;
    }
    
    boolean isPromise() {
      return (this.value == this);
    }
    
    ClassValue.Version<T> version() {
      return get();
    }
    
    ClassValue<T> classValueOrNull() {
      ClassValue.Version<T> version = version();
      return (version == null) ? null : version.classValue();
    }
    
    boolean isLive() {
      ClassValue.Version<T> version = version();
      if (version == null)
        return false; 
      if (version.isLive())
        return true; 
      clear();
      return false;
    }
    
    Entry<T> refreshVersion(ClassValue.Version<T> param1Version) {
      assertNotPromise();
      Entry<T> entry = new Entry(param1Version, (T)this.value);
      clear();
      return entry;
    }
    
    static final Entry<?> DEAD_ENTRY = new Entry(null, null);
  }
  
  private static ClassValueMap getMap(Class<?> paramClass) {
    ClassValueMap classValueMap = paramClass.classValueMap;
    if (classValueMap != null)
      return classValueMap; 
    return initializeMap(paramClass);
  }
  
  private static final Object CRITICAL_SECTION = new Object();
  
  private static ClassValueMap initializeMap(Class<?> paramClass) {
    ClassValueMap classValueMap;
    synchronized (CRITICAL_SECTION) {
      if ((classValueMap = paramClass.classValueMap) == null)
        paramClass.classValueMap = classValueMap = new ClassValueMap(paramClass); 
    } 
    return classValueMap;
  }
  
  static <T> Entry<T> makeEntry(Version<T> paramVersion, T paramT) {
    return new Entry<>(paramVersion, paramT);
  }
  
  static class ClassValue {}
}
