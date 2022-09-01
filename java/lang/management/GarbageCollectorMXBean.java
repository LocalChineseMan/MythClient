package java.lang.management;

import java.lang.management.MemoryManagerMXBean;

public interface GarbageCollectorMXBean extends MemoryManagerMXBean {
  long getCollectionCount();
  
  long getCollectionTime();
}
