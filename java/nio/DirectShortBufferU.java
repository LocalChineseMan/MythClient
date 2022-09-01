package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectShortBufferU extends ShortBuffer implements DirectBuffer {
  protected static final Unsafe unsafe = Bits.unsafe();
  
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(short[].class);
  
  protected static final boolean unaligned = Bits.unaligned();
  
  private final Object att;
  
  public Object attachment() {
    return this.att;
  }
  
  public Cleaner cleaner() {
    return null;
  }
  
  DirectShortBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.address = paramDirectBuffer.address() + paramInt5;
    this.att = paramDirectBuffer;
  }
  
  public ShortBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = i << 1;
    assert k >= 0;
    return new DirectShortBufferU(this, -1, 0, bool, bool, k);
  }
  
  public ShortBuffer duplicate() {
    return new DirectShortBufferU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public ShortBuffer asReadOnlyBuffer() {
    return new DirectShortBufferRU(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public long address() {
    return this.address;
  }
  
  private long ix(int paramInt) {
    return this.address + (paramInt << 1);
  }
  
  public short get() {
    return unsafe.getShort(ix(nextGetIndex()));
  }
  
  public short get(int paramInt) {
    return unsafe.getShort(ix(checkIndex(paramInt)));
  }
  
  public ShortBuffer get(short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    if (paramInt2 << 1 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOfshort.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferUnderflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToShortArray(ix(i), paramArrayOfshort, (paramInt1 << 1), (paramInt2 << 1));
      } else {
        Bits.copyToArray(ix(i), paramArrayOfshort, arrayBaseOffset, (paramInt1 << 1), (paramInt2 << 1));
      } 
      position(i + paramInt2);
    } else {
      super.get(paramArrayOfshort, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ShortBuffer put(short paramShort) {
    unsafe.putShort(ix(nextPutIndex()), paramShort);
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort) {
    unsafe.putShort(ix(checkIndex(paramInt)), paramShort);
    return this;
  }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer) {
    if (paramShortBuffer instanceof DirectShortBufferU) {
      if (paramShortBuffer == this)
        throw new IllegalArgumentException(); 
      DirectShortBufferU directShortBufferU = (DirectShortBufferU)paramShortBuffer;
      int i = directShortBufferU.position();
      int j = directShortBufferU.limit();
      assert i <= j;
      byte b1 = (i <= j) ? (j - i) : 0;
      int k = position();
      int m = limit();
      assert k <= m;
      byte b2 = (k <= m) ? (m - k) : 0;
      if (b1 > b2)
        throw new BufferOverflowException(); 
      unsafe.copyMemory(directShortBufferU.ix(i), ix(k), (b1 << 1));
      directShortBufferU.position(i + b1);
      position(k + b1);
    } else if (paramShortBuffer.hb != null) {
      int i = paramShortBuffer.position();
      int j = paramShortBuffer.limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      put(paramShortBuffer.hb, paramShortBuffer.offset + i, b);
      paramShortBuffer.position(i + b);
    } else {
      super.put(paramShortBuffer);
    } 
    return this;
  }
  
  public ShortBuffer put(short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    if (paramInt2 << 1 > 6) {
      checkBounds(paramInt1, paramInt2, paramArrayOfshort.length);
      int i = position();
      int j = limit();
      assert i <= j;
      byte b = (i <= j) ? (j - i) : 0;
      if (paramInt2 > b)
        throw new BufferOverflowException(); 
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromShortArray(paramArrayOfshort, (paramInt1 << 1), 
            ix(i), (paramInt2 << 1));
      } else {
        Bits.copyFromArray(paramArrayOfshort, arrayBaseOffset, (paramInt1 << 1), 
            ix(i), (paramInt2 << 1));
      } 
      position(i + paramInt2);
    } else {
      super.put(paramArrayOfshort, paramInt1, paramInt2);
    } 
    return this;
  }
  
  public ShortBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    byte b = (i <= j) ? (j - i) : 0;
    unsafe.copyMemory(ix(i), ix(0), (b << 1));
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
