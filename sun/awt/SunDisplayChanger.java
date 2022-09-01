package sun.awt;

import java.awt.IllegalComponentStateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;

public class SunDisplayChanger {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
  
  private Map<DisplayChangedListener, Void> listeners = Collections.synchronizedMap(new WeakHashMap<>(1));
  
  public void add(DisplayChangedListener paramDisplayChangedListener) {
    if (log.isLoggable(PlatformLogger.Level.FINE) && 
      paramDisplayChangedListener == null)
      log.fine("Assertion (theListener != null) failed"); 
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("Adding listener: " + paramDisplayChangedListener); 
    this.listeners.put(paramDisplayChangedListener, null);
  }
  
  public void remove(DisplayChangedListener paramDisplayChangedListener) {
    if (log.isLoggable(PlatformLogger.Level.FINE) && 
      paramDisplayChangedListener == null)
      log.fine("Assertion (theListener != null) failed"); 
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("Removing listener: " + paramDisplayChangedListener); 
    this.listeners.remove(paramDisplayChangedListener);
  }
  
  public void notifyListeners() {
    HashSet hashSet;
    if (log.isLoggable(PlatformLogger.Level.FINEST))
      log.finest("notifyListeners"); 
    synchronized (this.listeners) {
      hashSet = new HashSet(this.listeners.keySet());
    } 
    Iterator<DisplayChangedListener> iterator = hashSet.iterator();
    while (iterator.hasNext()) {
      DisplayChangedListener displayChangedListener = iterator.next();
      try {
        if (log.isLoggable(PlatformLogger.Level.FINEST))
          log.finest("displayChanged for listener: " + displayChangedListener); 
        displayChangedListener.displayChanged();
      } catch (IllegalComponentStateException illegalComponentStateException) {
        this.listeners.remove(displayChangedListener);
      } 
    } 
  }
  
  public void notifyPaletteChanged() {
    HashSet hashSet;
    if (log.isLoggable(PlatformLogger.Level.FINEST))
      log.finest("notifyPaletteChanged"); 
    synchronized (this.listeners) {
      hashSet = new HashSet(this.listeners.keySet());
    } 
    Iterator<DisplayChangedListener> iterator = hashSet.iterator();
    while (iterator.hasNext()) {
      DisplayChangedListener displayChangedListener = iterator.next();
      try {
        if (log.isLoggable(PlatformLogger.Level.FINEST))
          log.finest("paletteChanged for listener: " + displayChangedListener); 
        displayChangedListener.paletteChanged();
      } catch (IllegalComponentStateException illegalComponentStateException) {
        this.listeners.remove(displayChangedListener);
      } 
    } 
  }
}
