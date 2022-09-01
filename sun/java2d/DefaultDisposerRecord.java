package sun.java2d;

public class DefaultDisposerRecord implements DisposerRecord {
  private long dataPointer;
  
  private long disposerMethodPointer;
  
  public DefaultDisposerRecord(long paramLong1, long paramLong2) {
    this.disposerMethodPointer = paramLong1;
    this.dataPointer = paramLong2;
  }
  
  public void dispose() {
    invokeNativeDispose(this.disposerMethodPointer, this.dataPointer);
  }
  
  public long getDataPointer() {
    return this.dataPointer;
  }
  
  public long getDisposerMethodPointer() {
    return this.disposerMethodPointer;
  }
  
  public static native void invokeNativeDispose(long paramLong1, long paramLong2);
}
