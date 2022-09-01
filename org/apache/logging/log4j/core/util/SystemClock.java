package org.apache.logging.log4j.core.util;

public final class SystemClock implements Clock {
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
