package org.apache.logging.log4j.core.filter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.message.Message;

@Plugin(name = "BurstFilter", category = "Core", elementType = "filter", printObject = true)
public final class BurstFilter extends AbstractFilter {
  private static final long NANOS_IN_SECONDS = 1000000000L;
  
  private static final int DEFAULT_RATE = 10;
  
  private static final int DEFAULT_RATE_MULTIPLE = 100;
  
  private static final int HASH_SHIFT = 32;
  
  private final Level level;
  
  private final long burstInterval;
  
  private final DelayQueue<LogDelay> history = new DelayQueue<>();
  
  private final Queue<LogDelay> available = new ConcurrentLinkedQueue<>();
  
  static LogDelay createLogDelay(long expireTime) {
    return new LogDelay(expireTime);
  }
  
  private BurstFilter(Level level, float rate, long maxBurst, Filter.Result onMatch, Filter.Result onMismatch) {
    super(onMatch, onMismatch);
    this.level = level;
    this.burstInterval = (long)(1.0E9F * (float)maxBurst / rate);
    for (int i = 0; i < maxBurst; i++)
      this.available.add(createLogDelay(0L)); 
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
    return filter(level);
  }
  
  public Filter.Result filter(LogEvent event) {
    return filter(event.getLevel());
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
    return filter(level);
  }
  
  public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
    return filter(level);
  }
  
  private Filter.Result filter(Level level) {
    if (this.level.isMoreSpecificThan(level)) {
      LogDelay delay = this.history.poll();
      while (delay != null) {
        this.available.add(delay);
        delay = this.history.poll();
      } 
      delay = this.available.poll();
      if (delay != null) {
        delay.setDelay(this.burstInterval);
        this.history.add(delay);
        return this.onMatch;
      } 
      return this.onMismatch;
    } 
    return this.onMatch;
  }
  
  public int getAvailable() {
    return this.available.size();
  }
  
  public void clear() {
    for (LogDelay delay : this.history) {
      this.history.remove(delay);
      this.available.add(delay);
    } 
  }
  
  public String toString() {
    return "level=" + this.level.toString() + ", interval=" + this.burstInterval + ", max=" + this.history.size();
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class BurstFilter {}
  
  private static class BurstFilter {}
}
