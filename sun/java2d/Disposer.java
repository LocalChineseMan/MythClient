package sun.java2d;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.misc.ThreadGroupUtils;
import sun.security.action.GetPropertyAction;

public class Disposer implements Runnable {
  private static final ReferenceQueue queue = new ReferenceQueue();
  
  private static final Hashtable records = new Hashtable<>();
  
  private static Disposer disposerInstance;
  
  public static final int WEAK = 0;
  
  public static final int PHANTOM = 1;
  
  public static int refType = 1;
  
  private static ArrayList<DisposerRecord> deferredRecords;
  
  public static volatile boolean pollingQueue;
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("awt");
            return null;
          }
        });
    initIDs();
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("sun.java2d.reftype"));
    if (str != null)
      if (str.equals("weak")) {
        refType = 0;
        System.err.println("Using WEAK refs");
      } else {
        refType = 1;
        System.err.println("Using PHANTOM refs");
      }  
    disposerInstance = new Disposer();
    AccessController.doPrivileged(() -> {
          ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
          Thread thread = new Thread(threadGroup, disposerInstance, "Java2D Disposer");
          thread.setContextClassLoader(null);
          thread.setDaemon(true);
          thread.setPriority(10);
          thread.start();
          return null;
        });
    deferredRecords = null;
    pollingQueue = false;
  }
  
  public static interface PollDisposable {}
  
  public static void addRecord(Object paramObject, long paramLong1, long paramLong2) {
    disposerInstance.add(paramObject, new DefaultDisposerRecord(paramLong1, paramLong2));
  }
  
  public static void addRecord(Object paramObject, DisposerRecord paramDisposerRecord) {
    disposerInstance.add(paramObject, paramDisposerRecord);
  }
  
  synchronized void add(Object paramObject, DisposerRecord paramDisposerRecord) {
    WeakReference weakReference;
    if (paramObject instanceof DisposerTarget)
      paramObject = ((DisposerTarget)paramObject).getDisposerReferent(); 
    if (refType == 1) {
      PhantomReference phantomReference = new PhantomReference(paramObject, queue);
    } else {
      weakReference = new WeakReference(paramObject, queue);
    } 
    records.put(weakReference, paramDisposerRecord);
  }
  
  public void run() {
    while (true) {
      try {
        while (true) {
          Reference reference = queue.remove();
          reference.clear();
          DisposerRecord disposerRecord = (DisposerRecord)records.remove(reference);
          disposerRecord.dispose();
          reference = null;
          disposerRecord = null;
          clearDeferredRecords();
        } 
        break;
      } catch (Exception exception) {
        System.out.println("Exception while removing reference.");
      } 
    } 
  }
  
  private static void clearDeferredRecords() {
    if (deferredRecords == null || deferredRecords.isEmpty())
      return; 
    for (byte b = 0; b < deferredRecords.size(); b++) {
      try {
        DisposerRecord disposerRecord = deferredRecords.get(b);
        disposerRecord.dispose();
      } catch (Exception exception) {
        System.out.println("Exception while disposing deferred rec.");
      } 
    } 
    deferredRecords.clear();
  }
  
  public static void pollRemove() {
    if (pollingQueue)
      return; 
    pollingQueue = true;
    byte b1 = 0;
    byte b2 = 0;
    try {
      Reference reference;
      while ((reference = queue.poll()) != null && b1 < '✐' && b2 < 100) {
        b1++;
        reference.clear();
        DisposerRecord disposerRecord = (DisposerRecord)records.remove(reference);
        if (disposerRecord instanceof PollDisposable) {
          disposerRecord.dispose();
          reference = null;
          disposerRecord = null;
          continue;
        } 
        if (disposerRecord == null)
          continue; 
        b2++;
        if (deferredRecords == null)
          deferredRecords = new ArrayList<>(5); 
        deferredRecords.add(disposerRecord);
      } 
    } catch (Exception exception) {
      System.out.println("Exception while removing reference.");
    } finally {
      pollingQueue = false;
    } 
  }
  
  public static void addReference(Reference paramReference, DisposerRecord paramDisposerRecord) {
    records.put(paramReference, paramDisposerRecord);
  }
  
  public static void addObjectRecord(Object paramObject, DisposerRecord paramDisposerRecord) {
    records.put(new WeakReference(paramObject, queue), paramDisposerRecord);
  }
  
  public static ReferenceQueue getQueue() {
    return queue;
  }
  
  private static native void initIDs();
}
