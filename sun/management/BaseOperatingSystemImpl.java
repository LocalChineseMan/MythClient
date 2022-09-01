package sun.management;

import java.lang.management.OperatingSystemMXBean;
import javax.management.ObjectName;
import sun.misc.Unsafe;

public class BaseOperatingSystemImpl implements OperatingSystemMXBean {
  private final VMManagement jvm;
  
  protected BaseOperatingSystemImpl(VMManagement paramVMManagement) {
    this.loadavg = new double[1];
    this.jvm = paramVMManagement;
  }
  
  public String getName() {
    return this.jvm.getOsName();
  }
  
  public String getArch() {
    return this.jvm.getOsArch();
  }
  
  public String getVersion() {
    return this.jvm.getOsVersion();
  }
  
  public int getAvailableProcessors() {
    return this.jvm.getAvailableProcessors();
  }
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private double[] loadavg;
  
  public double getSystemLoadAverage() {
    if (unsafe.getLoadAverage(this.loadavg, 1) == 1)
      return this.loadavg[0]; 
    return -1.0D;
  }
  
  public ObjectName getObjectName() {
    return Util.newObjectName("java.lang:type=OperatingSystem");
  }
}
