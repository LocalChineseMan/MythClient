package sun.nio.ch;

public final class IOStatus {
  public static final int EOF = -1;
  
  public static final int UNAVAILABLE = -2;
  
  public static final int INTERRUPTED = -3;
  
  public static final int UNSUPPORTED = -4;
  
  public static final int THROWN = -5;
  
  public static final int UNSUPPORTED_CASE = -6;
  
  public static int normalize(int paramInt) {
    if (paramInt == -2)
      return 0; 
    return paramInt;
  }
  
  public static boolean check(int paramInt) {
    return (paramInt >= -2);
  }
  
  public static long normalize(long paramLong) {
    if (paramLong == -2L)
      return 0L; 
    return paramLong;
  }
  
  public static boolean check(long paramLong) {
    return (paramLong >= -2L);
  }
  
  public static boolean checkAll(long paramLong) {
    return (paramLong > -1L || paramLong < -6L);
  }
}
