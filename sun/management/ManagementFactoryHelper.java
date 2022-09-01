package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.nio.ch.FileChannelImpl;
import sun.util.logging.LoggingSupport;

public class ManagementFactoryHelper {
  private static VMManagement jvm;
  
  private static ClassLoadingImpl classMBean = null;
  
  private static MemoryImpl memoryMBean = null;
  
  private static ThreadImpl threadMBean = null;
  
  private static RuntimeImpl runtimeMBean = null;
  
  private static CompilationImpl compileMBean = null;
  
  private static OperatingSystemImpl osMBean = null;
  
  public static synchronized ClassLoadingMXBean getClassLoadingMXBean() {
    if (classMBean == null)
      classMBean = new ClassLoadingImpl(jvm); 
    return classMBean;
  }
  
  public static synchronized MemoryMXBean getMemoryMXBean() {
    if (memoryMBean == null)
      memoryMBean = new MemoryImpl(jvm); 
    return memoryMBean;
  }
  
  public static synchronized ThreadMXBean getThreadMXBean() {
    if (threadMBean == null)
      threadMBean = new ThreadImpl(jvm); 
    return threadMBean;
  }
  
  public static synchronized RuntimeMXBean getRuntimeMXBean() {
    if (runtimeMBean == null)
      runtimeMBean = new RuntimeImpl(jvm); 
    return runtimeMBean;
  }
  
  public static synchronized CompilationMXBean getCompilationMXBean() {
    if (compileMBean == null && jvm.getCompilerName() != null)
      compileMBean = new CompilationImpl(jvm); 
    return compileMBean;
  }
  
  public static synchronized OperatingSystemMXBean getOperatingSystemMXBean() {
    if (osMBean == null)
      osMBean = new OperatingSystemImpl(jvm); 
    return osMBean;
  }
  
  public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
    MemoryPoolMXBean[] arrayOfMemoryPoolMXBean = MemoryImpl.getMemoryPools();
    ArrayList<MemoryPoolMXBean> arrayList = new ArrayList(arrayOfMemoryPoolMXBean.length);
    for (MemoryPoolMXBean memoryPoolMXBean : arrayOfMemoryPoolMXBean)
      arrayList.add(memoryPoolMXBean); 
    return arrayList;
  }
  
  public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean = MemoryImpl.getMemoryManagers();
    ArrayList<MemoryManagerMXBean> arrayList = new ArrayList(arrayOfMemoryManagerMXBean.length);
    for (MemoryManagerMXBean memoryManagerMXBean : arrayOfMemoryManagerMXBean)
      arrayList.add(memoryManagerMXBean); 
    return arrayList;
  }
  
  public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean = MemoryImpl.getMemoryManagers();
    ArrayList<GarbageCollectorMXBean> arrayList = new ArrayList(arrayOfMemoryManagerMXBean.length);
    for (MemoryManagerMXBean memoryManagerMXBean : arrayOfMemoryManagerMXBean) {
      if (GarbageCollectorMXBean.class.isInstance(memoryManagerMXBean))
        arrayList.add(GarbageCollectorMXBean.class.cast(memoryManagerMXBean)); 
    } 
    return arrayList;
  }
  
  public static PlatformLoggingMXBean getPlatformLoggingMXBean() {
    if (LoggingSupport.isAvailable())
      return PlatformLoggingImpl.instance; 
    return null;
  }
  
  static class PlatformLoggingImpl implements LoggingMXBean {
    static final PlatformLoggingMXBean instance = new PlatformLoggingImpl();
    
    static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
    
    private volatile ObjectName objname;
    
    public ObjectName getObjectName() {
      ObjectName objectName = this.objname;
      if (objectName == null)
        synchronized (this) {
          objectName = this.objname;
          if (objectName == null) {
            objectName = Util.newObjectName("java.util.logging:type=Logging");
            this.objname = objectName;
          } 
        }  
      return objectName;
    }
    
    public List<String> getLoggerNames() {
      return LoggingSupport.getLoggerNames();
    }
    
    public String getLoggerLevel(String param1String) {
      return LoggingSupport.getLoggerLevel(param1String);
    }
    
    public void setLoggerLevel(String param1String1, String param1String2) {
      LoggingSupport.setLoggerLevel(param1String1, param1String2);
    }
    
    public String getParentLoggerName(String param1String) {
      return LoggingSupport.getParentLoggerName(param1String);
    }
  }
  
  private static List<BufferPoolMXBean> bufferPools = null;
  
  private static final String BUFFER_POOL_MXBEAN_NAME = "java.nio:type=BufferPool";
  
  public static synchronized List<BufferPoolMXBean> getBufferPoolMXBeans() {
    if (bufferPools == null) {
      bufferPools = new ArrayList<>(2);
      bufferPools.add(createBufferPoolMXBean(SharedSecrets.getJavaNioAccess()
            .getDirectBufferPool()));
      bufferPools.add(createBufferPoolMXBean(
            FileChannelImpl.getMappedBufferPool()));
    } 
    return bufferPools;
  }
  
  private static BufferPoolMXBean createBufferPoolMXBean(final JavaNioAccess.BufferPool pool) {
    return new BufferPoolMXBean() {
        private volatile ObjectName objname;
        
        public ObjectName getObjectName() {
          ObjectName objectName = this.objname;
          if (objectName == null)
            synchronized (this) {
              objectName = this.objname;
              if (objectName == null) {
                objectName = Util.newObjectName("java.nio:type=BufferPool,name=" + pool
                    .getName());
                this.objname = objectName;
              } 
            }  
          return objectName;
        }
        
        public String getName() {
          return pool.getName();
        }
        
        public long getCount() {
          return pool.getCount();
        }
        
        public long getTotalCapacity() {
          return pool.getTotalCapacity();
        }
        
        public long getMemoryUsed() {
          return pool.getMemoryUsed();
        }
      };
  }
  
  private static HotSpotDiagnostic hsDiagMBean = null;
  
  private static HotspotRuntime hsRuntimeMBean = null;
  
  private static HotspotClassLoading hsClassMBean = null;
  
  private static HotspotThread hsThreadMBean = null;
  
  private static HotspotCompilation hsCompileMBean = null;
  
  private static HotspotMemory hsMemoryMBean = null;
  
  private static DiagnosticCommandImpl hsDiagCommandMBean = null;
  
  private static final String HOTSPOT_CLASS_LOADING_MBEAN_NAME = "sun.management:type=HotspotClassLoading";
  
  private static final String HOTSPOT_COMPILATION_MBEAN_NAME = "sun.management:type=HotspotCompilation";
  
  private static final String HOTSPOT_MEMORY_MBEAN_NAME = "sun.management:type=HotspotMemory";
  
  private static final String HOTSPOT_RUNTIME_MBEAN_NAME = "sun.management:type=HotspotRuntime";
  
  private static final String HOTSPOT_THREAD_MBEAN_NAME = "sun.management:type=HotspotThreading";
  
  static final String HOTSPOT_DIAGNOSTIC_COMMAND_MBEAN_NAME = "com.sun.management:type=DiagnosticCommand";
  
  private static final int JMM_THREAD_STATE_FLAG_MASK = -1048576;
  
  private static final int JMM_THREAD_STATE_FLAG_SUSPENDED = 1048576;
  
  private static final int JMM_THREAD_STATE_FLAG_NATIVE = 4194304;
  
  public static synchronized HotSpotDiagnosticMXBean getDiagnosticMXBean() {
    if (hsDiagMBean == null)
      hsDiagMBean = new HotSpotDiagnostic(); 
    return hsDiagMBean;
  }
  
  public static synchronized HotspotRuntimeMBean getHotspotRuntimeMBean() {
    if (hsRuntimeMBean == null)
      hsRuntimeMBean = new HotspotRuntime(jvm); 
    return hsRuntimeMBean;
  }
  
  public static synchronized HotspotClassLoadingMBean getHotspotClassLoadingMBean() {
    if (hsClassMBean == null)
      hsClassMBean = new HotspotClassLoading(jvm); 
    return hsClassMBean;
  }
  
  public static synchronized HotspotThreadMBean getHotspotThreadMBean() {
    if (hsThreadMBean == null)
      hsThreadMBean = new HotspotThread(jvm); 
    return hsThreadMBean;
  }
  
  public static synchronized HotspotMemoryMBean getHotspotMemoryMBean() {
    if (hsMemoryMBean == null)
      hsMemoryMBean = new HotspotMemory(jvm); 
    return hsMemoryMBean;
  }
  
  public static synchronized DiagnosticCommandMBean getDiagnosticCommandMBean() {
    if (hsDiagCommandMBean == null && jvm.isRemoteDiagnosticCommandsSupported())
      hsDiagCommandMBean = new DiagnosticCommandImpl(jvm); 
    return hsDiagCommandMBean;
  }
  
  public static synchronized HotspotCompilationMBean getHotspotCompilationMBean() {
    if (hsCompileMBean == null)
      hsCompileMBean = new HotspotCompilation(jvm); 
    return hsCompileMBean;
  }
  
  private static void addMBean(MBeanServer paramMBeanServer, Object paramObject, String paramString) {
    try {
      ObjectName objectName = Util.newObjectName(paramString);
      MBeanServer mBeanServer = paramMBeanServer;
      Object object = paramObject;
      AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Object(mBeanServer, object, objectName));
    } catch (PrivilegedActionException privilegedActionException) {
      throw Util.newException(privilegedActionException.getException());
    } 
  }
  
  public static HashMap<ObjectName, DynamicMBean> getPlatformDynamicMBeans() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    DiagnosticCommandMBean diagnosticCommandMBean = getDiagnosticCommandMBean();
    if (diagnosticCommandMBean != null)
      hashMap.put(Util.newObjectName("com.sun.management:type=DiagnosticCommand"), diagnosticCommandMBean); 
    return (HashMap)hashMap;
  }
  
  static void registerInternalMBeans(MBeanServer paramMBeanServer) {
    addMBean(paramMBeanServer, getHotspotClassLoadingMBean(), "sun.management:type=HotspotClassLoading");
    addMBean(paramMBeanServer, getHotspotMemoryMBean(), "sun.management:type=HotspotMemory");
    addMBean(paramMBeanServer, getHotspotRuntimeMBean(), "sun.management:type=HotspotRuntime");
    addMBean(paramMBeanServer, getHotspotThreadMBean(), "sun.management:type=HotspotThreading");
    if (getCompilationMXBean() != null)
      addMBean(paramMBeanServer, getHotspotCompilationMBean(), "sun.management:type=HotspotCompilation"); 
  }
  
  private static void unregisterMBean(MBeanServer paramMBeanServer, String paramString) {
    try {
      ObjectName objectName = Util.newObjectName(paramString);
      MBeanServer mBeanServer = paramMBeanServer;
      AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Object(mBeanServer, objectName));
    } catch (PrivilegedActionException privilegedActionException) {
      throw Util.newException(privilegedActionException.getException());
    } 
  }
  
  static void unregisterInternalMBeans(MBeanServer paramMBeanServer) {
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotClassLoading");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotMemory");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotRuntime");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotThreading");
    if (getCompilationMXBean() != null)
      unregisterMBean(paramMBeanServer, "sun.management:type=HotspotCompilation"); 
  }
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("management");
            return null;
          }
        });
    jvm = new VMManagementImpl();
  }
  
  public static boolean isThreadSuspended(int paramInt) {
    return ((paramInt & 0x100000) != 0);
  }
  
  public static boolean isThreadRunningNative(int paramInt) {
    return ((paramInt & 0x400000) != 0);
  }
  
  public static Thread.State toThreadState(int paramInt) {
    int i = paramInt & 0xFFFFF;
    return VM.toThreadState(i);
  }
  
  public static interface LoggingMXBean extends PlatformLoggingMXBean, java.util.logging.LoggingMXBean {}
}
