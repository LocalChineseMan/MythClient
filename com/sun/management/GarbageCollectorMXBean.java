package com.sun.management;

import java.lang.management.GarbageCollectorMXBean;
import jdk.Exported;

@Exported
public interface GarbageCollectorMXBean extends GarbageCollectorMXBean {
  GcInfo getLastGcInfo();
}
