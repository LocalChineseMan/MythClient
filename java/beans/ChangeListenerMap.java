package java.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class ChangeListenerMap<L extends EventListener> {
  private Map<String, L[]> map;
  
  protected abstract L[] newArray(int paramInt);
  
  protected abstract L newProxy(String paramString, L paramL);
  
  public final synchronized void add(String paramString, L paramL) {
    if (this.map == null)
      this.map = new HashMap<>(); 
    EventListener[] arrayOfEventListener = (EventListener[])this.map.get(paramString);
    byte b = (arrayOfEventListener != null) ? arrayOfEventListener.length : 0;
    L[] arrayOfL = newArray(b + 1);
    arrayOfL[b] = paramL;
    if (arrayOfEventListener != null)
      System.arraycopy(arrayOfEventListener, 0, arrayOfL, 0, b); 
    this.map.put(paramString, arrayOfL);
  }
  
  public final synchronized void remove(String paramString, L paramL) {
    if (this.map != null) {
      EventListener[] arrayOfEventListener = (EventListener[])this.map.get(paramString);
      if (arrayOfEventListener != null)
        for (byte b = 0; b < arrayOfEventListener.length; b++) {
          if (paramL.equals(arrayOfEventListener[b])) {
            int i = arrayOfEventListener.length - 1;
            if (i > 0) {
              L[] arrayOfL = newArray(i);
              System.arraycopy(arrayOfEventListener, 0, arrayOfL, 0, b);
              System.arraycopy(arrayOfEventListener, b + 1, arrayOfL, b, i - b);
              this.map.put(paramString, arrayOfL);
              break;
            } 
            this.map.remove(paramString);
            if (this.map.isEmpty())
              this.map = null; 
            break;
          } 
        }  
    } 
  }
  
  public final synchronized L[] get(String paramString) {
    return (this.map != null) ? this.map
      .get(paramString) : null;
  }
  
  public final void set(String paramString, L[] paramArrayOfL) {
    if (paramArrayOfL != null) {
      if (this.map == null)
        this.map = new HashMap<>(); 
      this.map.put(paramString, paramArrayOfL);
    } else if (this.map != null) {
      this.map.remove(paramString);
      if (this.map.isEmpty())
        this.map = null; 
    } 
  }
  
  public final synchronized L[] getListeners() {
    if (this.map == null)
      return newArray(0); 
    ArrayList<EventListener> arrayList = new ArrayList();
    EventListener[] arrayOfEventListener = (EventListener[])this.map.get(null);
    if (arrayOfEventListener != null)
      for (EventListener eventListener : arrayOfEventListener)
        arrayList.add(eventListener);  
    for (Map.Entry<String, L> entry : this.map.entrySet()) {
      String str = (String)entry.getKey();
      if (str != null)
        for (EventListener eventListener : (EventListener[])entry.getValue())
          arrayList.add((EventListener)newProxy(str, (L)eventListener));  
    } 
    return (L[])arrayList.<EventListener>toArray((EventListener[])newArray(arrayList.size()));
  }
  
  public final L[] getListeners(String paramString) {
    if (paramString != null) {
      L[] arrayOfL = get(paramString);
      if (arrayOfL != null)
        return (L[])arrayOfL.clone(); 
    } 
    return newArray(0);
  }
  
  public final synchronized boolean hasListeners(String paramString) {
    if (this.map == null)
      return false; 
    EventListener[] arrayOfEventListener = (EventListener[])this.map.get(null);
    return (arrayOfEventListener != null || (paramString != null && null != this.map.get(paramString)));
  }
  
  public final Set<Map.Entry<String, L[]>> getEntries() {
    return (this.map != null) ? this.map
      .entrySet() : 
      Collections.<Map.Entry<String, L[]>>emptySet();
  }
  
  public abstract L extract(L paramL);
}
