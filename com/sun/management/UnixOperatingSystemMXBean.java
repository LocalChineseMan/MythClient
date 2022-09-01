package com.sun.management;

import jdk.Exported;

@Exported
public interface UnixOperatingSystemMXBean extends OperatingSystemMXBean {
  long getOpenFileDescriptorCount();
  
  long getMaxFileDescriptorCount();
}
