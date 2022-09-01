package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface MemoryManagerMXBean extends PlatformManagedObject {
  String getName();
  
  boolean isValid();
  
  String[] getMemoryPoolNames();
}
