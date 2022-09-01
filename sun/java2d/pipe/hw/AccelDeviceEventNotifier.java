package sun.java2d.pipe.hw;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AccelDeviceEventNotifier {
  private static AccelDeviceEventNotifier theInstance;
  
  public static final int DEVICE_RESET = 0;
  
  public static final int DEVICE_DISPOSED = 1;
  
  private final Map<AccelDeviceEventListener, Integer> listeners = Collections.synchronizedMap(new HashMap<>(1));
  
  private static synchronized AccelDeviceEventNotifier getInstance(boolean paramBoolean) {
    if (theInstance == null && paramBoolean)
      theInstance = new AccelDeviceEventNotifier(); 
    return theInstance;
  }
  
  public static final void eventOccured(int paramInt1, int paramInt2) {
    AccelDeviceEventNotifier accelDeviceEventNotifier = getInstance(false);
    if (accelDeviceEventNotifier != null)
      accelDeviceEventNotifier.notifyListeners(paramInt2, paramInt1); 
  }
  
  public static final void addListener(AccelDeviceEventListener paramAccelDeviceEventListener, int paramInt) {
    getInstance(true).add(paramAccelDeviceEventListener, paramInt);
  }
  
  public static final void removeListener(AccelDeviceEventListener paramAccelDeviceEventListener) {
    getInstance(true).remove(paramAccelDeviceEventListener);
  }
  
  private final void add(AccelDeviceEventListener paramAccelDeviceEventListener, int paramInt) {
    this.listeners.put(paramAccelDeviceEventListener, Integer.valueOf(paramInt));
  }
  
  private final void remove(AccelDeviceEventListener paramAccelDeviceEventListener) {
    this.listeners.remove(paramAccelDeviceEventListener);
  }
  
  private final void notifyListeners(int paramInt1, int paramInt2) {
    HashMap<AccelDeviceEventListener, Integer> hashMap;
    synchronized (this.listeners) {
      hashMap = new HashMap<>(this.listeners);
    } 
    Set<AccelDeviceEventListener> set = hashMap.keySet();
    Iterator<AccelDeviceEventListener> iterator = set.iterator();
    while (iterator.hasNext()) {
      AccelDeviceEventListener accelDeviceEventListener = iterator.next();
      Integer integer = hashMap.get(accelDeviceEventListener);
      if (integer != null && integer.intValue() != paramInt2)
        continue; 
      if (paramInt1 == 0) {
        accelDeviceEventListener.onDeviceReset();
        continue;
      } 
      if (paramInt1 == 1)
        accelDeviceEventListener.onDeviceDispose(); 
    } 
  }
}
