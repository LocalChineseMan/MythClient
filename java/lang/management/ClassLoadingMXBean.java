package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface ClassLoadingMXBean extends PlatformManagedObject {
  long getTotalLoadedClassCount();
  
  int getLoadedClassCount();
  
  long getUnloadedClassCount();
  
  boolean isVerbose();
  
  void setVerbose(boolean paramBoolean);
}
