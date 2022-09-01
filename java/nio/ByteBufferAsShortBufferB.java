package java.nio;

class ByteBufferAsShortBufferB extends ShortBuffer {
  protected final ByteBuffer bb;
  
  protected final int offset;
  
  ByteBufferAsShortBufferB(ByteBuffer paramByteBuffer) {
    super(-1, 0, paramByteBuffer
        .remaining() >> 1, paramByteBuffer
        .remaining() >> 1);
    this.bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert j <= i;
    this.offset = j;
  }
  
  ByteBufferAsShortBufferB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.bb = paramByteBuffer;
    this.offset = paramInt5;
  }
  
  public ShortBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = (i << 1) + this.offset;
    assert k >= 0;
    return new ByteBufferAsShortBufferB(this.bb, -1, 0, bool, bool, k);
  }
  
  public ShortBuffer duplicate() {
    return new ByteBufferAsShortBufferB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  public ShortBuffer asReadOnlyBuffer() {
    return new ByteBufferAsShortBufferRB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  protected int ix(int paramInt) {
    return (paramInt << 1) + this.offset;
  }
  
  public short get() {
    return Bits.getShortB(this.bb, ix(nextGetIndex()));
  }
  
  public short get(int paramInt) {
    return Bits.getShortB(this.bb, ix(checkIndex(paramInt)));
  }
  
  public ShortBuffer put(short paramShort) {
    Bits.putShortB(this.bb, ix(nextPutIndex()), paramShort);
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort) {
    Bits.putShortB(this.bb, ix(checkIndex(paramInt)), paramShort);
    return this;
  }
  
  public ShortBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    ByteBuffer byteBuffer1 = this.bb.duplicate();
    byteBuffer1.limit(ix(j));
    byteBuffer1.position(ix(0));
    ByteBuffer byteBuffer2 = byteBuffer1.slice();
    byteBuffer2.position(i << 1);
    byteBuffer2.compact();
    position(bool);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect() {
    return this.bb.isDirect();
  }
  
  public boolean isReadOnly() {
    return false;
  }
  
  public ByteOrder order() {
    return ByteOrder.BIG_ENDIAN;
  }
}
