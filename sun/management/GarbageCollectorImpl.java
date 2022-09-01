package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

class GarbageCollectorImpl extends MemoryManagerImpl implements GarbageCollectorMXBean {
  private String[] poolNames;
  
  private GcInfoBuilder gcInfoBuilder;
  
  private static final String notifName = "javax.management.Notification";
  
  GarbageCollectorImpl(String paramString) {
    super(paramString);
    this.poolNames = null;
    this.notifInfo = null;
  }
  
  public native long getCollectionCount();
  
  public native long getCollectionTime();
  
  synchronized String[] getAllPoolNames() {
    if (this.poolNames == null) {
      List<MemoryPoolMXBean> list = ManagementFactory.getMemoryPoolMXBeans();
      this.poolNames = new String[list.size()];
      byte b = 0;
      for (MemoryPoolMXBean memoryPoolMXBean : list)
        this.poolNames[b++] = memoryPoolMXBean.getName(); 
    } 
    return this.poolNames;
  }
  
  private synchronized GcInfoBuilder getGcInfoBuilder() {
    if (this.gcInfoBuilder == null)
      this.gcInfoBuilder = new GcInfoBuilder(this, getAllPoolNames()); 
    return this.gcInfoBuilder;
  }
  
  public GcInfo getLastGcInfo() {
    return getGcInfoBuilder().getLastGcInfo();
  }
  
  private static final String[] gcNotifTypes = new String[] { "com.sun.management.gc.notification" };
  
  private MBeanNotificationInfo[] notifInfo;
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    synchronized (this) {
      if (this.notifInfo == null) {
        this.notifInfo = new MBeanNotificationInfo[1];
        this.notifInfo[0] = new MBeanNotificationInfo(gcNotifTypes, "javax.management.Notification", "GC Notification");
      } 
    } 
    return this.notifInfo;
  }
  
  private static long seqNumber = 0L;
  
  private static long getNextSeqNumber() {
    return ++seqNumber;
  }
  
  void createGCNotification(long paramLong, String paramString1, String paramString2, String paramString3, GcInfo paramGcInfo) {
    if (!hasListeners())
      return; 
    Notification notification = new Notification("com.sun.management.gc.notification", getObjectName(), getNextSeqNumber(), paramLong, paramString1);
    GarbageCollectionNotificationInfo garbageCollectionNotificationInfo = new GarbageCollectionNotificationInfo(paramString1, paramString2, paramString3, paramGcInfo);
    CompositeData compositeData = GarbageCollectionNotifInfoCompositeData.toCompositeData(garbageCollectionNotificationInfo);
    notification.setUserData(compositeData);
    sendNotification(notification);
  }
  
  public synchronized void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    boolean bool1 = hasListeners();
    super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (!bool1 && bool2)
      setNotificationEnabled(this, true); 
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(this, false); 
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(this, false); 
  }
  
  public ObjectName getObjectName() {
    return Util.newObjectName("java.lang:type=GarbageCollector", getName());
  }
  
  native void setNotificationEnabled(GarbageCollectorMXBean paramGarbageCollectorMXBean, boolean paramBoolean);
}
