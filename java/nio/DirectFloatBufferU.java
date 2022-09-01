package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectFloatBufferU extends FloatBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(float[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() {
    return this.att;
  }
  
  public Cleaner cleaner() {
    return null;
  }
  
  DirectFloatBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public FloatBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = i << 2;
    assert k >= 0;
    return new DirectFloatBufferU(this, -1, 0, bool, bool, k);
  }
  
  public FloatBuffer duplicate() {
    return new DirectFloatBufferU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public FloatBuffer asReadOnlyBuffer() {
    return new DirectFloatBufferRU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public long address() {
    return this.address;
  }
  
  private long ix(int paramInt) {
    return this.address + (paramInt << 2);
  }
  
  public float get() {
    return unsafe.getFloat(ix(nextGetIndex()));
  }
  
  public float get(int paramInt) {
    return unsafe.getFloat(ix(checkIndex(paramInt)));
  }
  
  public FloatBuffer get(float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOffloat.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToIntArray(ix(i), paramArrayOffloat, (paramInt1 << 2), (paramInt2 << 2));
      } else {
        Bits.copyToArray(ix(i), paramArrayOffloat, arrayBaseOffset, (paramInt1 << 2), (paramInt2 << 2));
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOffloat, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public FloatBuffer put(float paramFloat) {
    unsafe.putFloat(ix(nextPutIndex()), paramFloat);
    return this;
  }
  
  public FloatBuffer put(int paramInt, float paramFloat) {
    unsafe.putFloat(ix(checkIndex(paramInt)), paramFloat);
    return this;
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) {
    if (paramFloatBuffer instanceof DirectFloatBufferU) {
      if (paramFloatBuffer == this)
        throw new IllegalArgumentException(); 
      DirectFloatBufferU directFloatBufferU = (DirectFloatBufferU)paramFloatBuffer;
      int i = directFloatBufferU.position();
      int j = directFloatBufferU.limit();
      assert i <= j;
      byte b1 = (i <= j) ? (j - i) : 0;
      int k = position();
      int m = limit();
      assert k <= m;
      byte b2 = (k <= m) ? (m - k) : 0;
      if (b1 > b2)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directFloatBufferU.ix(i), ix(k), (b1 << 2));
      directFloatBufferU.position(i + b1);
      position(k + b1);
    } else if (paramFloatBuffer.hb != null) {
      int i = paramFloatBuffer.position();
      int j = paramFloatBuffer.limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      put(paramFloatBuffer.hb, paramFloatBuffer.offset + i, b);
      paramFloatBuffer.position(i + b);
    } else {
      super.put(paramFloatBuffer);
    } 
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    if (paramInt2 << 2 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOffloat.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromIntArray(paramArrayOffloat, (paramInt1 << 2), 
            ix(i), (paramInt2 << 2));
      } else {
        Bits.copyFromArray(paramArrayOffloat, arrayBaseOffset, (paramInt1 << 2), 
            ix(i), (paramInt2 << 2));
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOffloat, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public FloatBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    byte b = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), (b << 2));
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
