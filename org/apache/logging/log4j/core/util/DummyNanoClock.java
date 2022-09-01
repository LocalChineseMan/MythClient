package org.apache.logging.log4j.core.util;

public final class DummyNanoClock implements NanoClock {
  private final long fixedNanoTime;
  
  public DummyNanoClock() {
    this(0L);
  }
  
  public DummyNanoClock(long fixedNanoTime) {
    this.fixedNanoTime = fixedNanoTime;
  }
  
  public long nanoTime() {
    return this.fixedNanoTime;
  }
}
