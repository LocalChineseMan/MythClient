package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferU extends DoubleBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(double[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() {
    return this.att;
  }
  
  public Cleaner cleaner() {
    return null;
  }
  
  DirectDoubleBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public DoubleBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = i << 3;
    assert k >= 0;
    return new DirectDoubleBufferU(this, -1, 0, bool, bool, k);
  }
  
  public DoubleBuffer duplicate() {
    return new DirectDoubleBufferU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public DoubleBuffer asReadOnlyBuffer() {
    return new DirectDoubleBufferRU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public long address() {
    return this.address;
  }
  
  private long ix(int paramInt) {
    return this.address + (paramInt << 3);
  }
  
  public double get() {
    return unsafe.getDouble(ix(nextGetIndex()));
  }
  
  public double get(int paramInt) {
    return unsafe.getDouble(ix(checkIndex(paramInt)));
  }
  
  public DoubleBuffer get(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOfdouble.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToLongArray(ix(i), paramArrayOfdouble, (paramInt1 << 3), (paramInt2 << 3));
      } else {
        Bits.copyToArray(ix(i), paramArrayOfdouble, arrayBaseOffset, (paramInt1 << 3), (paramInt2 << 3));
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfdouble, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public DoubleBuffer put(double paramDouble) {
    unsafe.putDouble(ix(nextPutIndex()), paramDouble);
    return this;
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble) {
    unsafe.putDouble(ix(checkIndex(paramInt)), paramDouble);
    return this;
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer) {
    if (paramDoubleBuffer instanceof DirectDoubleBufferU) {
      if (paramDoubleBuffer == this)
        throw new IllegalArgumentException(); 
      DirectDoubleBufferU directDoubleBufferU = (DirectDoubleBufferU)paramDoubleBuffer;
      int i = directDoubleBufferU.position();
      int j = directDoubleBufferU.limit();
      assert i <= j;
      byte b1 = (i <= j) ? (j - i) : 0;
      int k = position();
      int m = limit();
      assert k <= m;
      byte b2 = (k <= m) ? (m - k) : 0;
      if (b1 > b2)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directDoubleBufferU.ix(i), ix(k), (b1 << 3));
      directDoubleBufferU.position(i + b1);
      position(k + b1);
    } else if (paramDoubleBuffer.hb != null) {
      int i = paramDoubleBuffer.position();
      int j = paramDoubleBuffer.limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      put(paramDoubleBuffer.hb, paramDoubleBuffer.offset + i, b);
      paramDoubleBuffer.position(i + b);
    } else {
      super.put(paramDoubleBuffer);
    } 
    return this;
  }
  
  public DoubleBuffer put(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    if (paramInt2 << 3 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOfdouble.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromLongArray(paramArrayOfdouble, (paramInt1 << 3), 
            ix(i), (paramInt2 << 3));
      } else {
        Bits.copyFromArray(paramArrayOfdouble, arrayBaseOffset, (paramInt1 << 3), 
            ix(i), (paramInt2 << 3));
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfdouble, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public DoubleBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    byte b = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), (b << 3));
    position(b);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() {
    return true;
  }
  
  public boolean isReadOnly() {
    return false;
  }
  
  public ByteOrder order() {
    return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}
