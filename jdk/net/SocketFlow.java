package jdk.net;

import jdk.Exported;

@Exported
public class SocketFlow {
  private static final int UNSET = -1;
  
  public static final int NORMAL_PRIORITY = 1;
  
  public static final int HIGH_PRIORITY = 2;
  
  private int priority = 1;
  
  private long bandwidth = -1L;
  
  private Status status = Status.NO_STATUS;
  
  public static SocketFlow create() {
    return new SocketFlow();
  }
  
  public SocketFlow priority(int paramInt) {
    if (paramInt != 1 && paramInt != 2)
      throw new IllegalArgumentException("invalid priority"); 
    this.priority = paramInt;
    return this;
  }
  
  public SocketFlow bandwidth(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("invalid bandwidth"); 
    this.bandwidth = paramLong;
    return this;
  }
  
  public int priority() {
    return this.priority;
  }
  
  public long bandwidth() {
    return this.bandwidth;
  }
  
  public Status status() {
    return this.status;
  }
  
  public enum SocketFlow {
  
  }
}
