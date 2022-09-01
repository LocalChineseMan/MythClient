package java.time;

import java.util.Objects;

public abstract class Clock {
  public static Clock systemUTC() {
    return new SystemClock(ZoneOffset.UTC);
  }
  
  public static Clock systemDefaultZone() {
    return new SystemClock(ZoneId.systemDefault());
  }
  
  public static Clock system(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    return new SystemClock(paramZoneId);
  }
  
  public static Clock tickSeconds(ZoneId paramZoneId) {
    return new TickClock(system(paramZoneId), 1000000000L);
  }
  
  public static Clock tickMinutes(ZoneId paramZoneId) {
    return new TickClock(system(paramZoneId), 60000000000L);
  }
  
  public static Clock tick(Clock paramClock, Duration paramDuration) {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "tickDuration");
    if (paramDuration.isNegative())
      throw new IllegalArgumentException("Tick duration must not be negative"); 
    long l = paramDuration.toNanos();
    if (l % 1000000L != 0L)
      if (1000000000L % l != 0L)
        throw new IllegalArgumentException("Invalid tick duration");  
    if (l <= 1L)
      return paramClock; 
    return new TickClock(paramClock, l);
  }
  
  public static Clock fixed(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "fixedInstant");
    Objects.requireNonNull(paramZoneId, "zone");
    return new FixedClock(paramInstant, paramZoneId);
  }
  
  public static Clock offset(Clock paramClock, Duration paramDuration) {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "offsetDuration");
    if (paramDuration.equals(Duration.ZERO))
      return paramClock; 
    return new OffsetClock(paramClock, paramDuration);
  }
  
  public abstract ZoneId getZone();
  
  public abstract Clock withZone(ZoneId paramZoneId);
  
  public long millis() {
    return instant().toEpochMilli();
  }
  
  public abstract Instant instant();
  
  public boolean equals(Object paramObject) {
    return super.equals(paramObject);
  }
  
  public int hashCode() {
    return super.hashCode();
  }
  
  static final class Clock {}
  
  static final class Clock {}
  
  static final class Clock {}
  
  static final class Clock {}
}
