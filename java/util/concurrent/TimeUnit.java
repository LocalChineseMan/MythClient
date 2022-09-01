package java.util.concurrent;

public enum TimeUnit {
  NANOSECONDS {
    public long toNanos(long param1Long) {
      return param1Long;
    }
    
    public long toMicros(long param1Long) {
      return param1Long / 1000L;
    }
    
    public long toMillis(long param1Long) {
      return param1Long / 1000000L;
    }
    
    public long toSeconds(long param1Long) {
      return param1Long / 1000000000L;
    }
    
    public long toMinutes(long param1Long) {
      return param1Long / 60000000000L;
    }
    
    public long toHours(long param1Long) {
      return param1Long / 3600000000000L;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 86400000000000L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toNanos(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return (int)(param1Long1 - param1Long2 * 1000000L);
    }
  },
  MICROSECONDS {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 1000L, 9223372036854775L);
    }
    
    public long toMicros(long param1Long) {
      return param1Long;
    }
    
    public long toMillis(long param1Long) {
      return param1Long / 1000L;
    }
    
    public long toSeconds(long param1Long) {
      return param1Long / 1000000L;
    }
    
    public long toMinutes(long param1Long) {
      return param1Long / 60000000L;
    }
    
    public long toHours(long param1Long) {
      return param1Long / 3600000000L;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 86400000000L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toMicros(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return (int)(param1Long1 * 1000L - param1Long2 * 1000000L);
    }
  },
  MILLISECONDS {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 1000000L, 9223372036854L);
    }
    
    public long toMicros(long param1Long) {
      return null.x(param1Long, 1000L, 9223372036854775L);
    }
    
    public long toMillis(long param1Long) {
      return param1Long;
    }
    
    public long toSeconds(long param1Long) {
      return param1Long / 1000L;
    }
    
    public long toMinutes(long param1Long) {
      return param1Long / 60000L;
    }
    
    public long toHours(long param1Long) {
      return param1Long / 3600000L;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 86400000L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toMillis(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return 0;
    }
  },
  SECONDS {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 1000000000L, 9223372036L);
    }
    
    public long toMicros(long param1Long) {
      return null.x(param1Long, 1000000L, 9223372036854L);
    }
    
    public long toMillis(long param1Long) {
      return null.x(param1Long, 1000L, 9223372036854775L);
    }
    
    public long toSeconds(long param1Long) {
      return param1Long;
    }
    
    public long toMinutes(long param1Long) {
      return param1Long / 60L;
    }
    
    public long toHours(long param1Long) {
      return param1Long / 3600L;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 86400L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toSeconds(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return 0;
    }
  },
  MINUTES {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 60000000000L, 153722867L);
    }
    
    public long toMicros(long param1Long) {
      return null.x(param1Long, 60000000L, 153722867280L);
    }
    
    public long toMillis(long param1Long) {
      return null.x(param1Long, 60000L, 153722867280912L);
    }
    
    public long toSeconds(long param1Long) {
      return null.x(param1Long, 60L, 153722867280912930L);
    }
    
    public long toMinutes(long param1Long) {
      return param1Long;
    }
    
    public long toHours(long param1Long) {
      return param1Long / 60L;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 1440L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toMinutes(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return 0;
    }
  },
  HOURS {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 3600000000000L, 2562047L);
    }
    
    public long toMicros(long param1Long) {
      return null.x(param1Long, 3600000000L, 2562047788L);
    }
    
    public long toMillis(long param1Long) {
      return null.x(param1Long, 3600000L, 2562047788015L);
    }
    
    public long toSeconds(long param1Long) {
      return null.x(param1Long, 3600L, 2562047788015215L);
    }
    
    public long toMinutes(long param1Long) {
      return null.x(param1Long, 60L, 153722867280912930L);
    }
    
    public long toHours(long param1Long) {
      return param1Long;
    }
    
    public long toDays(long param1Long) {
      return param1Long / 24L;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toHours(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return 0;
    }
  },
  DAYS {
    public long toNanos(long param1Long) {
      return null.x(param1Long, 86400000000000L, 106751L);
    }
    
    public long toMicros(long param1Long) {
      return null.x(param1Long, 86400000000L, 106751991L);
    }
    
    public long toMillis(long param1Long) {
      return null.x(param1Long, 86400000L, 106751991167L);
    }
    
    public long toSeconds(long param1Long) {
      return null.x(param1Long, 86400L, 106751991167300L);
    }
    
    public long toMinutes(long param1Long) {
      return null.x(param1Long, 1440L, 6405119470038038L);
    }
    
    public long toHours(long param1Long) {
      return null.x(param1Long, 24L, 384307168202282325L);
    }
    
    public long toDays(long param1Long) {
      return param1Long;
    }
    
    public long convert(long param1Long, TimeUnit param1TimeUnit) {
      return param1TimeUnit.toDays(param1Long);
    }
    
    int excessNanos(long param1Long1, long param1Long2) {
      return 0;
    }
  };
  
  static final long C0 = 1L;
  
  static final long C1 = 1000L;
  
  static final long C2 = 1000000L;
  
  static final long C3 = 1000000000L;
  
  static final long C4 = 60000000000L;
  
  static final long C5 = 3600000000000L;
  
  static final long C6 = 86400000000000L;
  
  static final long MAX = 9223372036854775807L;
  
  static long x(long paramLong1, long paramLong2, long paramLong3) {
    if (paramLong1 > paramLong3)
      return Long.MAX_VALUE; 
    if (paramLong1 < -paramLong3)
      return Long.MIN_VALUE; 
    return paramLong1 * paramLong2;
  }
  
  public long convert(long paramLong, TimeUnit paramTimeUnit) {
    throw new AbstractMethodError();
  }
  
  public long toNanos(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toMicros(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toMillis(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toSeconds(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toMinutes(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toHours(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public long toDays(long paramLong) {
    throw new AbstractMethodError();
  }
  
  public void timedWait(Object paramObject, long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramObject.wait(l, i);
    } 
  }
  
  public void timedJoin(Thread paramThread, long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramThread.join(l, i);
    } 
  }
  
  public void sleep(long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      Thread.sleep(l, i);
    } 
  }
  
  abstract int excessNanos(long paramLong1, long paramLong2);
}
