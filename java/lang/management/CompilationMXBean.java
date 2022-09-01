package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface CompilationMXBean extends PlatformManagedObject {
  String getName();
  
  boolean isCompilationTimeMonitoringSupported();
  
  long getTotalCompilationTime();
}
