package java.lang.management;

import java.lang.management.MemoryUsage;
import java.lang.management.PlatformManagedObject;

public interface MemoryMXBean extends PlatformManagedObject {
  int getObjectPendingFinalizationCount();
  
  MemoryUsage getHeapMemoryUsage();
  
  MemoryUsage getNonHeapMemoryUsage();
  
  boolean isVerbose();
  
  void setVerbose(boolean paramBoolean);
  
  void gc();
}
