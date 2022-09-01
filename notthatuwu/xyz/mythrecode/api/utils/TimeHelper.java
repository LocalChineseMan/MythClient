package notthatuwu.xyz.mythrecode.api.utils;

public class TimeHelper {
  private long lastMS = 0L;
  
  public int convertToMS(int d) {
    return 1000 / d;
  }
  
  public long getCurrentMS() {
    return System.nanoTime() / 1000000L;
  }
  
  public boolean hasReached(long milliseconds) {
    return (getCurrentMS() - this.lastMS >= milliseconds);
  }
  
  public long getDelay() {
    return System.currentTimeMillis() - this.lastMS;
  }
  
  public void reset() {
    this.lastMS = getCurrentMS();
  }
  
  public long getLastMS() {
    return this.lastMS;
  }
  
  public void setLastMS() {
    this.lastMS = System.currentTimeMillis();
  }
  
  public void setLastMS(long lastMS) {
    this.lastMS = lastMS;
  }
}
