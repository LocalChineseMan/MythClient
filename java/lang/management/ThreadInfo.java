package java.lang.management;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import javax.management.openmbean.CompositeData;
import sun.management.ManagementFactoryHelper;
import sun.management.ThreadInfoCompositeData;

public class ThreadInfo {
  private String threadName;
  
  private long threadId;
  
  private long blockedTime;
  
  private long blockedCount;
  
  private long waitedTime;
  
  private long waitedCount;
  
  private LockInfo lock;
  
  private String lockName;
  
  private long lockOwnerId;
  
  private String lockOwnerName;
  
  private boolean inNative;
  
  private boolean suspended;
  
  private Thread.State threadState;
  
  private StackTraceElement[] stackTrace;
  
  private MonitorInfo[] lockedMonitors;
  
  private LockInfo[] lockedSynchronizers;
  
  private static MonitorInfo[] EMPTY_MONITORS = new MonitorInfo[0];
  
  private static LockInfo[] EMPTY_SYNCS = new LockInfo[0];
  
  private static final int MAX_FRAMES = 8;
  
  private ThreadInfo(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement) {
    initialize(paramThread1, paramInt, paramObject, paramThread2, paramLong1, paramLong2, paramLong3, paramLong4, paramArrayOfStackTraceElement, EMPTY_MONITORS, EMPTY_SYNCS);
  }
  
  private ThreadInfo(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement, Object[] paramArrayOfObject1, int[] paramArrayOfint, Object[] paramArrayOfObject2) {
    MonitorInfo[] arrayOfMonitorInfo;
    Object object;
    byte b1 = (paramArrayOfObject1 == null) ? 0 : paramArrayOfObject1.length;
    if (!b1) {
      arrayOfMonitorInfo = EMPTY_MONITORS;
    } else {
      arrayOfMonitorInfo = new MonitorInfo[b1];
      for (byte b = 0; b < b1; b++) {
        object = paramArrayOfObject1[b];
        String str = object.getClass().getName();
        int i = System.identityHashCode(object);
        int j = paramArrayOfint[b];
        StackTraceElement stackTraceElement = (j >= 0) ? paramArrayOfStackTraceElement[j] : null;
        arrayOfMonitorInfo[b] = new MonitorInfo(str, i, j, stackTraceElement);
      } 
    } 
    byte b2 = (paramArrayOfObject2 == null) ? 0 : paramArrayOfObject2.length;
    if (!b2) {
      object = EMPTY_SYNCS;
    } else {
      object = new LockInfo[b2];
      for (byte b = 0; b < b2; b++) {
        Object object1 = paramArrayOfObject2[b];
        String str = object1.getClass().getName();
        int i = System.identityHashCode(object1);
        object[b] = new LockInfo(str, i);
      } 
    } 
    initialize(paramThread1, paramInt, paramObject, paramThread2, paramLong1, paramLong2, paramLong3, paramLong4, paramArrayOfStackTraceElement, arrayOfMonitorInfo, (LockInfo[])object);
  }
  
  private void initialize(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement, MonitorInfo[] paramArrayOfMonitorInfo, LockInfo[] paramArrayOfLockInfo) {
    this.threadId = paramThread1.getId();
    this.threadName = paramThread1.getName();
    this.threadState = ManagementFactoryHelper.toThreadState(paramInt);
    this.suspended = ManagementFactoryHelper.isThreadSuspended(paramInt);
    this.inNative = ManagementFactoryHelper.isThreadRunningNative(paramInt);
    this.blockedCount = paramLong1;
    this.blockedTime = paramLong2;
    this.waitedCount = paramLong3;
    this.waitedTime = paramLong4;
    if (paramObject == null) {
      this.lock = null;
      this.lockName = null;
    } else {
      this.lock = new LockInfo(paramObject);
      this
        
        .lockName = this.lock.getClassName() + '@' + Integer.toHexString(this.lock.getIdentityHashCode());
    } 
    if (paramThread2 == null) {
      this.lockOwnerId = -1L;
      this.lockOwnerName = null;
    } else {
      this.lockOwnerId = paramThread2.getId();
      this.lockOwnerName = paramThread2.getName();
    } 
    if (paramArrayOfStackTraceElement == null) {
      this.stackTrace = NO_STACK_TRACE;
    } else {
      this.stackTrace = paramArrayOfStackTraceElement;
    } 
    this.lockedMonitors = paramArrayOfMonitorInfo;
    this.lockedSynchronizers = paramArrayOfLockInfo;
  }
  
  private ThreadInfo(CompositeData paramCompositeData) {
    ThreadInfoCompositeData threadInfoCompositeData = ThreadInfoCompositeData.getInstance(paramCompositeData);
    this.threadId = threadInfoCompositeData.threadId();
    this.threadName = threadInfoCompositeData.threadName();
    this.blockedTime = threadInfoCompositeData.blockedTime();
    this.blockedCount = threadInfoCompositeData.blockedCount();
    this.waitedTime = threadInfoCompositeData.waitedTime();
    this.waitedCount = threadInfoCompositeData.waitedCount();
    this.lockName = threadInfoCompositeData.lockName();
    this.lockOwnerId = threadInfoCompositeData.lockOwnerId();
    this.lockOwnerName = threadInfoCompositeData.lockOwnerName();
    this.threadState = threadInfoCompositeData.threadState();
    this.suspended = threadInfoCompositeData.suspended();
    this.inNative = threadInfoCompositeData.inNative();
    this.stackTrace = threadInfoCompositeData.stackTrace();
    if (threadInfoCompositeData.isCurrentVersion()) {
      this.lock = threadInfoCompositeData.lockInfo();
      this.lockedMonitors = threadInfoCompositeData.lockedMonitors();
      this.lockedSynchronizers = threadInfoCompositeData.lockedSynchronizers();
    } else {
      if (this.lockName != null) {
        String[] arrayOfString = this.lockName.split("@");
        if (arrayOfString.length == 2) {
          int i = Integer.parseInt(arrayOfString[1], 16);
          this.lock = new LockInfo(arrayOfString[0], i);
        } else {
          assert arrayOfString.length == 2;
          this.lock = null;
        } 
      } else {
        this.lock = null;
      } 
      this.lockedMonitors = EMPTY_MONITORS;
      this.lockedSynchronizers = EMPTY_SYNCS;
    } 
  }
  
  public long getThreadId() {
    return this.threadId;
  }
  
  public String getThreadName() {
    return this.threadName;
  }
  
  public Thread.State getThreadState() {
    return this.threadState;
  }
  
  public long getBlockedTime() {
    return this.blockedTime;
  }
  
  public long getBlockedCount() {
    return this.blockedCount;
  }
  
  public long getWaitedTime() {
    return this.waitedTime;
  }
  
  public long getWaitedCount() {
    return this.waitedCount;
  }
  
  public LockInfo getLockInfo() {
    return this.lock;
  }
  
  public String getLockName() {
    return this.lockName;
  }
  
  public long getLockOwnerId() {
    return this.lockOwnerId;
  }
  
  public String getLockOwnerName() {
    return this.lockOwnerName;
  }
  
  public StackTraceElement[] getStackTrace() {
    return this.stackTrace;
  }
  
  public boolean isSuspended() {
    return this.suspended;
  }
  
  public boolean isInNative() {
    return this.inNative;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("\"" + getThreadName() + "\"" + " Id=" + getThreadId() + " " + getThreadState());
    if (getLockName() != null)
      stringBuilder.append(" on " + getLockName()); 
    if (getLockOwnerName() != null)
      stringBuilder.append(" owned by \"" + getLockOwnerName() + "\" Id=" + 
          getLockOwnerId()); 
    if (isSuspended())
      stringBuilder.append(" (suspended)"); 
    if (isInNative())
      stringBuilder.append(" (in native)"); 
    stringBuilder.append('\n');
    byte b = 0;
    for (; b < this.stackTrace.length && b < 8; b++) {
      StackTraceElement stackTraceElement = this.stackTrace[b];
      stringBuilder.append("\tat " + stackTraceElement.toString());
      stringBuilder.append('\n');
      if (b == 0 && getLockInfo() != null) {
        Thread.State state = getThreadState();
        switch (null.$SwitchMap$java$lang$Thread$State[state.ordinal()]) {
          case 1:
            stringBuilder.append("\t-  blocked on " + getLockInfo());
            stringBuilder.append('\n');
            break;
          case 2:
            stringBuilder.append("\t-  waiting on " + getLockInfo());
            stringBuilder.append('\n');
            break;
          case 3:
            stringBuilder.append("\t-  waiting on " + getLockInfo());
            stringBuilder.append('\n');
            break;
        } 
      } 
      for (MonitorInfo monitorInfo : this.lockedMonitors) {
        if (monitorInfo.getLockedStackDepth() == b) {
          stringBuilder.append("\t-  locked " + monitorInfo);
          stringBuilder.append('\n');
        } 
      } 
    } 
    if (b < this.stackTrace.length) {
      stringBuilder.append("\t...");
      stringBuilder.append('\n');
    } 
    LockInfo[] arrayOfLockInfo = getLockedSynchronizers();
    if (arrayOfLockInfo.length > 0) {
      stringBuilder.append("\n\tNumber of locked synchronizers = " + arrayOfLockInfo.length);
      stringBuilder.append('\n');
      for (LockInfo lockInfo : arrayOfLockInfo) {
        stringBuilder.append("\t- " + lockInfo);
        stringBuilder.append('\n');
      } 
    } 
    stringBuilder.append('\n');
    return stringBuilder.toString();
  }
  
  public static ThreadInfo from(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      return null; 
    if (paramCompositeData instanceof ThreadInfoCompositeData)
      return ((ThreadInfoCompositeData)paramCompositeData).getThreadInfo(); 
    return new ThreadInfo(paramCompositeData);
  }
  
  public MonitorInfo[] getLockedMonitors() {
    return this.lockedMonitors;
  }
  
  public LockInfo[] getLockedSynchronizers() {
    return this.lockedSynchronizers;
  }
  
  private static final StackTraceElement[] NO_STACK_TRACE = new StackTraceElement[0];
}
