package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface OperatingSystemMXBean extends PlatformManagedObject {
  String getName();
  
  String getArch();
  
  String getVersion();
  
  int getAvailableProcessors();
  
  double getSystemLoadAverage();
}
