package sun.java2d.pipe;

import java.util.HashSet;
import java.util.Set;
import sun.awt.SunToolkit;

public abstract class RenderQueue {
  protected Set refSet = new HashSet();
  
  protected RenderBuffer buf = RenderBuffer.allocate(32000);
  
  private static final int BUFFER_SIZE = 32000;
  
  public final void lock() {
    SunToolkit.awtLock();
  }
  
  public final boolean tryLock() {
    return SunToolkit.awtTryLock();
  }
  
  public final void unlock() {
    SunToolkit.awtUnlock();
  }
  
  public final void addReference(Object paramObject) {
    this.refSet.add(paramObject);
  }
  
  public final RenderBuffer getBuffer() {
    return this.buf;
  }
  
  public final void ensureCapacity(int paramInt) {
    if (this.buf.remaining() < paramInt)
      flushNow(); 
  }
  
  public final void ensureCapacityAndAlignment(int paramInt1, int paramInt2) {
    ensureCapacity(paramInt1 + 4);
    ensureAlignment(paramInt2);
  }
  
  public final void ensureAlignment(int paramInt) {
    int i = this.buf.position() + paramInt;
    if ((i & 0x7) != 0)
      this.buf.putInt(90); 
  }
  
  public abstract void flushNow();
  
  public abstract void flushAndInvokeNow(Runnable paramRunnable);
  
  public void flushNow(int paramInt) {
    this.buf.position(paramInt);
    flushNow();
  }
}
