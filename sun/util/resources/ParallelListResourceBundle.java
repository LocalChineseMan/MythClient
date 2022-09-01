package sun.util.resources;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicMarkableReference;

public abstract class ParallelListResourceBundle extends ResourceBundle {
  private volatile ConcurrentMap<String, Object> lookup;
  
  private volatile Set<String> keyset;
  
  private final AtomicMarkableReference<Object[][]> parallelContents = new AtomicMarkableReference(null, false);
  
  protected abstract Object[][] getContents();
  
  ResourceBundle getParent() {
    return this.parent;
  }
  
  public void setParallelContents(OpenListResourceBundle paramOpenListResourceBundle) {
    if (paramOpenListResourceBundle == null) {
      this.parallelContents.compareAndSet(null, null, false, true);
    } else {
      this.parallelContents.compareAndSet(null, paramOpenListResourceBundle.getContents(), false, false);
    } 
  }
  
  boolean areParallelContentsComplete() {
    if (this.parallelContents.isMarked())
      return true; 
    boolean[] arrayOfBoolean = new boolean[1];
    Object[][] arrayOfObject = this.parallelContents.get(arrayOfBoolean);
    return (arrayOfObject != null || arrayOfBoolean[0]);
  }
  
  protected Object handleGetObject(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    loadLookupTablesIfNecessary();
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() {
    return Collections.enumeration(keySet());
  }
  
  public boolean containsKey(String paramString) {
    return keySet().contains(paramString);
  }
  
  protected Set<String> handleKeySet() {
    loadLookupTablesIfNecessary();
    return this.lookup.keySet();
  }
  
  public Set<String> keySet() {
    Set<String> set;
    while ((set = this.keyset) == null) {
      set = (Set<String>)new KeySet(handleKeySet(), this.parent);
      synchronized (this) {
        if (this.keyset == null)
          this.keyset = set; 
      } 
    } 
    return set;
  }
  
  synchronized void resetKeySet() {
    this.keyset = null;
  }
  
  void loadLookupTablesIfNecessary() {
    ConcurrentMap<String, Object> concurrentMap = this.lookup;
    if (concurrentMap == null) {
      concurrentMap = new ConcurrentHashMap<>();
      for (Object[] arrayOfObject1 : getContents())
        concurrentMap.put((String)arrayOfObject1[0], arrayOfObject1[1]); 
    } 
    Object[][] arrayOfObject = this.parallelContents.getReference();
    if (arrayOfObject != null) {
      for (Object[] arrayOfObject1 : arrayOfObject)
        concurrentMap.putIfAbsent((String)arrayOfObject1[0], arrayOfObject1[1]); 
      this.parallelContents.set(null, true);
    } 
    if (this.lookup == null)
      synchronized (this) {
        if (this.lookup == null)
          this.lookup = concurrentMap; 
      }  
  }
  
  private static class KeySet extends AbstractSet<String> {
    private final Set<String> set;
    
    private final ResourceBundle parent;
    
    private KeySet(Set<String> param1Set, ResourceBundle param1ResourceBundle) {
      this.set = param1Set;
      this.parent = param1ResourceBundle;
    }
    
    public boolean contains(Object param1Object) {
      if (this.set.contains(param1Object))
        return true; 
      return (this.parent != null) ? this.parent.containsKey((String)param1Object) : false;
    }
    
    public Iterator<String> iterator() {
      if (this.parent == null)
        return this.set.iterator(); 
      return (Iterator<String>)new Object(this);
    }
    
    public int size() {
      if (this.parent == null)
        return this.set.size(); 
      HashSet<String> hashSet = new HashSet<>(this.set);
      hashSet.addAll(this.parent.keySet());
      return hashSet.size();
    }
  }
}
