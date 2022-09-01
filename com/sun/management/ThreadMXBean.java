package com.sun.management;

import java.lang.management.ThreadMXBean;
import jdk.Exported;

@Exported
public interface ThreadMXBean extends ThreadMXBean {
  long[] getThreadCpuTime(long[] paramArrayOflong);
  
  long[] getThreadUserTime(long[] paramArrayOflong);
  
  long getThreadAllocatedBytes(long paramLong);
  
  long[] getThreadAllocatedBytes(long[] paramArrayOflong);
  
  boolean isThreadAllocatedMemorySupported();
  
  boolean isThreadAllocatedMemoryEnabled();
  
  void setThreadAllocatedMemoryEnabled(boolean paramBoolean);
}
