package sun.awt;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public final class AppContext {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
  
  public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
  
  public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
  
  public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
  
  private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap<>());
  
  public static Set<AppContext> getAppContexts() {
    synchronized (threadGroup2appContext) {
      return new HashSet<>(threadGroup2appContext.values());
    } 
  }
  
  private static volatile AppContext mainAppContext = null;
  
  private static class GetAppContextLock {
    private GetAppContextLock() {}
  }
  
  private static final Object getAppContextLock = new GetAppContextLock();
  
  private final Map<Object, Object> table = new HashMap<>();
  
  private final ThreadGroup threadGroup;
  
  private PropertyChangeSupport changeSupport = null;
  
  public static final String DISPOSED_PROPERTY_NAME = "disposed";
  
  public static final String GUI_DISPOSED = "guidisposed";
  
  private enum State {
    VALID, BEING_DISPOSED, DISPOSED;
  }
  
  private volatile State state = State.VALID;
  
  public boolean isDisposed() {
    return (this.state == State.DISPOSED);
  }
  
  private static final AtomicInteger numAppContexts = new AtomicInteger(0);
  
  private final ClassLoader contextClassLoader;
  
  private static final ThreadLocal<AppContext> threadAppContext = new ThreadLocal<>();
  
  private long DISPOSAL_TIMEOUT;
  
  private long THREAD_INTERRUPT_TIMEOUT;
  
  private MostRecentKeyValue mostRecentKeyValue;
  
  private MostRecentKeyValue shadowMostRecentKeyValue;
  
  private static final void initMainAppContext() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
            ThreadGroup threadGroup2 = threadGroup1.getParent();
            while (threadGroup2 != null) {
              threadGroup1 = threadGroup2;
              threadGroup2 = threadGroup1.getParent();
            } 
            AppContext.mainAppContext = SunToolkit.createNewAppContext(threadGroup1);
            return null;
          }
        });
  }
  
  public static final AppContext getAppContext() {
    if (numAppContexts.get() == 1 && mainAppContext != null)
      return mainAppContext; 
    AppContext appContext = threadAppContext.get();
    if (null == appContext)
      appContext = AccessController.<AppContext>doPrivileged(new PrivilegedAction<AppContext>() {
            public AppContext run() {
              ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
              ThreadGroup threadGroup2 = threadGroup1;
              synchronized (AppContext.getAppContextLock) {
                if (AppContext.numAppContexts.get() == 0)
                  if (System.getProperty("javaplugin.version") == null && 
                    System.getProperty("javawebstart.version") == null) {
                    AppContext.initMainAppContext();
                  } else if (System.getProperty("javafx.version") != null && threadGroup2
                    .getParent() != null) {
                    SunToolkit.createNewAppContext();
                  }  
              } 
              AppContext appContext = (AppContext)AppContext.threadGroup2appContext.get(threadGroup2);
              while (appContext == null) {
                threadGroup2 = threadGroup2.getParent();
                if (threadGroup2 == null) {
                  SecurityManager securityManager = System.getSecurityManager();
                  if (securityManager != null) {
                    ThreadGroup threadGroup = securityManager.getThreadGroup();
                    if (threadGroup != null)
                      return (AppContext)AppContext.threadGroup2appContext.get(threadGroup); 
                  } 
                  return null;
                } 
                appContext = (AppContext)AppContext.threadGroup2appContext.get(threadGroup2);
              } 
              for (ThreadGroup threadGroup3 = threadGroup1; threadGroup3 != threadGroup2; threadGroup3 = threadGroup3.getParent())
                AppContext.threadGroup2appContext.put(threadGroup3, appContext); 
              AppContext.threadAppContext.set(appContext);
              return appContext;
            }
          }); 
    return appContext;
  }
  
  public static final boolean isMainContext(AppContext paramAppContext) {
    return (paramAppContext != null && paramAppContext == mainAppContext);
  }
  
  private static final AppContext getExecutionAppContext() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && securityManager instanceof AWTSecurityManager) {
      AWTSecurityManager aWTSecurityManager = (AWTSecurityManager)securityManager;
      return aWTSecurityManager.getAppContext();
    } 
    return null;
  }
  
  AppContext(ThreadGroup paramThreadGroup) {
    this.DISPOSAL_TIMEOUT = 5000L;
    this.THREAD_INTERRUPT_TIMEOUT = 1000L;
    this.mostRecentKeyValue = null;
    this.shadowMostRecentKeyValue = null;
    numAppContexts.incrementAndGet();
    this.threadGroup = paramThreadGroup;
    threadGroup2appContext.put(paramThreadGroup, this);
    this.contextClassLoader = AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
          }
        });
    ReentrantLock reentrantLock = new ReentrantLock();
    put(EVENT_QUEUE_LOCK_KEY, reentrantLock);
    Condition condition = reentrantLock.newCondition();
    put(EVENT_QUEUE_COND_KEY, condition);
  }
  
  public void dispose() throws IllegalThreadStateException {
    if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup()))
      throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed."); 
    synchronized (this) {
      if (this.state != State.VALID)
        return; 
      this.state = State.BEING_DISPOSED;
    } 
    PropertyChangeSupport propertyChangeSupport = this.changeSupport;
    if (propertyChangeSupport != null)
      propertyChangeSupport.firePropertyChange("disposed", false, true); 
    Object object1 = new Object();
    Object object2 = new Object(this, propertyChangeSupport, object1);
    synchronized (object1) {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)object2));
      try {
        object1.wait(this.DISPOSAL_TIMEOUT);
      } catch (InterruptedException interruptedException) {}
    } 
    object2 = new Object(this, object1);
    synchronized (object1) {
      SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)object2));
      try {
        object1.wait(this.DISPOSAL_TIMEOUT);
      } catch (InterruptedException interruptedException) {}
    } 
    synchronized (this) {
      this.state = State.DISPOSED;
    } 
    this.threadGroup.interrupt();
    long l1 = System.currentTimeMillis();
    long l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
    while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < l2) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException interruptedException) {}
    } 
    this.threadGroup.stop();
    l1 = System.currentTimeMillis();
    l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
    while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < l2) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException interruptedException) {}
    } 
    int i = this.threadGroup.activeGroupCount();
    if (i > 0) {
      ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
      i = this.threadGroup.enumerate(arrayOfThreadGroup);
      for (byte b = 0; b < i; b++)
        threadGroup2appContext.remove(arrayOfThreadGroup[b]); 
    } 
    threadGroup2appContext.remove(this.threadGroup);
    threadAppContext.set(null);
    try {
      this.threadGroup.destroy();
    } catch (IllegalThreadStateException illegalThreadStateException) {}
    synchronized (this.table) {
      this.table.clear();
    } 
    numAppContexts.decrementAndGet();
    this.mostRecentKeyValue = null;
  }
  
  static final class PostShutdownEventRunnable implements Runnable {
    private final AppContext appContext;
    
    public PostShutdownEventRunnable(AppContext param1AppContext) {
      this.appContext = param1AppContext;
    }
    
    public void run() {
      EventQueue eventQueue = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
      if (eventQueue != null)
        eventQueue.postEvent(AWTAutoShutdown.getShutdownEvent()); 
    }
  }
  
  static void stopEventDispatchThreads() {
    for (AppContext appContext : getAppContexts()) {
      if (appContext.isDisposed())
        continue; 
      PostShutdownEventRunnable postShutdownEventRunnable = new PostShutdownEventRunnable(appContext);
      if (appContext != getAppContext()) {
        CreateThreadAction createThreadAction = new CreateThreadAction(appContext, postShutdownEventRunnable);
        Thread thread = AccessController.<Thread>doPrivileged(createThreadAction);
        thread.start();
        continue;
      } 
      postShutdownEventRunnable.run();
    } 
  }
  
  public Object get(Object paramObject) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
      if (mostRecentKeyValue != null && mostRecentKeyValue.key == paramObject)
        return mostRecentKeyValue.value; 
      Object object = this.table.get(paramObject);
      if (this.mostRecentKeyValue == null) {
        this.mostRecentKeyValue = new MostRecentKeyValue(paramObject, object);
        this.shadowMostRecentKeyValue = new MostRecentKeyValue(paramObject, object);
      } else {
        MostRecentKeyValue mostRecentKeyValue1 = this.mostRecentKeyValue;
        this.shadowMostRecentKeyValue.setPair(paramObject, object);
        this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
        this.shadowMostRecentKeyValue = mostRecentKeyValue1;
      } 
      return object;
    } 
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
      if (mostRecentKeyValue != null && mostRecentKeyValue.key == paramObject1)
        mostRecentKeyValue.value = paramObject2; 
      return this.table.put(paramObject1, paramObject2);
    } 
  }
  
  public Object remove(Object paramObject) {
    synchronized (this.table) {
      MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
      if (mostRecentKeyValue != null && mostRecentKeyValue.key == paramObject)
        mostRecentKeyValue.value = null; 
      return this.table.remove(paramObject);
    } 
  }
  
  public ThreadGroup getThreadGroup() {
    return this.threadGroup;
  }
  
  public ClassLoader getContextClassLoader() {
    return this.contextClassLoader;
  }
  
  public String toString() {
    return getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]";
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
    if (this.changeSupport == null)
      return new PropertyChangeListener[0]; 
    return this.changeSupport.getPropertyChangeListeners();
  }
  
  public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    if (this.changeSupport == null)
      this.changeSupport = new PropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null || this.changeSupport == null)
      return; 
    this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString) {
    if (this.changeSupport == null)
      return new PropertyChangeListener[0]; 
    return this.changeSupport.getPropertyChangeListeners(paramString);
  }
  
  static {
    SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
          private boolean hasRootThreadGroup(AppContext param1AppContext) {
            return ((Boolean)AccessController.<Boolean>doPrivileged((PrivilegedAction<Boolean>)new Object(this, param1AppContext))).booleanValue();
          }
          
          public Object getAppletContext() {
            if (AppContext.numAppContexts.get() == 0)
              return null; 
            AppContext appContext = AppContext.getExecutionAppContext();
            if (AppContext.numAppContexts.get() > 0)
              appContext = (appContext != null) ? appContext : AppContext.getAppContext(); 
            boolean bool = (appContext == null || AppContext.mainAppContext == appContext || (AppContext.mainAppContext == null && hasRootThreadGroup(appContext))) ? true : false;
            return bool ? null : appContext;
          }
        });
  }
  
  public static <T> T getSoftReferenceValue(Object paramObject, Supplier<T> paramSupplier) {
    AppContext appContext = getAppContext();
    SoftReference<Object> softReference = (SoftReference)appContext.get(paramObject);
    if (softReference != null) {
      T t1 = (T)softReference.get();
      if (t1 != null)
        return t1; 
    } 
    T t = paramSupplier.get();
    softReference = new SoftReference<>((Object)t);
    appContext.put(paramObject, softReference);
    return t;
  }
  
  static final class AppContext {}
}
