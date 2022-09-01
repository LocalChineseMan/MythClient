package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicBoolean implements Serializable {
  private static final long serialVersionUID = 4654671469794556979L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long valueOffset;
  
  private volatile int value;
  
  static {
    try {
      valueOffset = unsafe.objectFieldOffset(AtomicBoolean.class.getDeclaredField("value"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public AtomicBoolean(boolean paramBoolean) {
    this.value = paramBoolean ? 1 : 0;
  }
  
  public AtomicBoolean() {}
  
  public final boolean get() {
    return (this.value != 0);
  }
  
  public final boolean compareAndSet(boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool1 = paramBoolean1 ? true : false;
    boolean bool2 = paramBoolean2 ? true : false;
    return unsafe.compareAndSwapInt(this, valueOffset, bool1, bool2);
  }
  
  public boolean weakCompareAndSet(boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool1 = paramBoolean1 ? true : false;
    boolean bool2 = paramBoolean2 ? true : false;
    return unsafe.compareAndSwapInt(this, valueOffset, bool1, bool2);
  }
  
  public final void set(boolean paramBoolean) {
    this.value = paramBoolean ? 1 : 0;
  }
  
  public final void lazySet(boolean paramBoolean) {
    boolean bool = paramBoolean ? true : false;
    unsafe.putOrderedInt(this, valueOffset, bool);
  }
  
  public final boolean getAndSet(boolean paramBoolean) {
    while (true) {
      boolean bool = get();
      if (compareAndSet(bool, paramBoolean))
        return bool; 
    } 
  }
  
  public String toString() {
    return Boolean.toString(get());
  }
}
