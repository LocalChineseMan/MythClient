package com.sun.management;

import java.lang.management.OperatingSystemMXBean;
import jdk.Exported;

@Exported
public interface OperatingSystemMXBean extends OperatingSystemMXBean {
  long getCommittedVirtualMemorySize();
  
  long getTotalSwapSpaceSize();
  
  long getFreeSwapSpaceSize();
  
  long getProcessCpuTime();
  
  long getFreePhysicalMemorySize();
  
  long getTotalPhysicalMemorySize();
  
  double getSystemCpuLoad();
  
  double getProcessCpuLoad();
}
