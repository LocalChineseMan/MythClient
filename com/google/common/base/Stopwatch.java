package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.TimeUnit;

@Beta
@GwtCompatible(emulated = true)
public final class Stopwatch {
  private final Ticker ticker;
  
  private boolean isRunning;
  
  private long elapsedNanos;
  
  private long startTick;
  
  public static Stopwatch createUnstarted() {
    return new Stopwatch();
  }
  
  public static Stopwatch createUnstarted(Ticker ticker) {
    return new Stopwatch(ticker);
  }
  
  public static Stopwatch createStarted() {
    return (new Stopwatch()).start();
  }
  
  public static Stopwatch createStarted(Ticker ticker) {
    return (new Stopwatch(ticker)).start();
  }
  
  @Deprecated
  Stopwatch() {
    this(Ticker.systemTicker());
  }
  
  @Deprecated
  Stopwatch(Ticker ticker) {
    this.ticker = Preconditions.<Ticker>checkNotNull(ticker, "ticker");
  }
  
  public boolean isRunning() {
    return this.isRunning;
  }
  
  public Stopwatch start() {
    Preconditions.checkState(!this.isRunning, "This stopwatch is already running.");
    this.isRunning = true;
    this.startTick = this.ticker.read();
    return this;
  }
  
  public Stopwatch stop() {
    long tick = this.ticker.read();
    Preconditions.checkState(this.isRunning, "This stopwatch is already stopped.");
    this.isRunning = false;
    this.elapsedNanos += tick - this.startTick;
    return this;
  }
  
  public Stopwatch reset() {
    this.elapsedNanos = 0L;
    this.isRunning = false;
    return this;
  }
  
  private long elapsedNanos() {
    return this.isRunning ? (this.ticker.read() - this.startTick + this.elapsedNanos) : this.elapsedNanos;
  }
  
  public long elapsed(TimeUnit desiredUnit) {
    return desiredUnit.convert(elapsedNanos(), TimeUnit.NANOSECONDS);
  }
  
  @GwtIncompatible("String.format()")
  public String toString() {
    long nanos = elapsedNanos();
    TimeUnit unit = chooseUnit(nanos);
    double value = nanos / TimeUnit.NANOSECONDS.convert(1L, unit);
    return String.format("%.4g %s", new Object[] { Double.valueOf(value), abbreviate(unit) });
  }
  
  private static TimeUnit chooseUnit(long nanos) {
    if (TimeUnit.DAYS.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.DAYS; 
    if (TimeUnit.HOURS.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.HOURS; 
    if (TimeUnit.MINUTES.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.MINUTES; 
    if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.SECONDS; 
    if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.MILLISECONDS; 
    if (TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L)
      return TimeUnit.MICROSECONDS; 
    return TimeUnit.NANOSECONDS;
  }
  
  private static String abbreviate(TimeUnit unit) {
    switch (null.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
      case 1:
        return "ns";
      case 2:
        return "μs";
      case 3:
        return "ms";
      case 4:
        return "s";
      case 5:
        return "min";
      case 6:
        return "h";
      case 7:
        return "d";
    } 
    throw new AssertionError();
  }
}
