package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface BufferPoolMXBean extends PlatformManagedObject {
  String getName();
  
  long getCount();
  
  long getTotalCapacity();
  
  long getMemoryUsed();
}
