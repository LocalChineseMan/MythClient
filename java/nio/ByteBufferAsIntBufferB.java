package java.nio;

class ByteBufferAsIntBufferB extends IntBuffer {
  protected final ByteBuffer bb;
  
  protected final int offset;
  
  ByteBufferAsIntBufferB(ByteBuffer paramByteBuffer) {
    super(-1, 0, paramByteBuffer
        .remaining() >> 2, paramByteBuffer
        .remaining() >> 2);
    this.bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert j <= i;
    this.offset = j;
  }
  
  ByteBufferAsIntBufferB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.bb = paramByteBuffer;
    this.offset = paramInt5;
  }
  
  public IntBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = (i << 2) + this.offset;
    assert k >= 0;
    return new ByteBufferAsIntBufferB(this.bb, -1, 0, bool, bool, k);
  }
  
  public IntBuffer duplicate() {
    return new ByteBufferAsIntBufferB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  public IntBuffer asReadOnlyBuffer() {
    return new ByteBufferAsIntBufferRB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  protected int ix(int paramInt) {
    return (paramInt << 2) + this.offset;
  }
  
  public int get() {
    return Bits.getIntB(this.bb, ix(nextGetIndex()));
  }
  
  public int get(int paramInt) {
    return Bits.getIntB(this.bb, ix(checkIndex(paramInt)));
  }
  
  public IntBuffer put(int paramInt) {
    Bits.putIntB(this.bb, ix(nextPutIndex()), paramInt);
    return this;
  }
  
  public IntBuffer put(int paramInt1, int paramInt2) {
    Bits.putIntB(this.bb, ix(checkIndex(paramInt1)), paramInt2);
    return this;
  }
  
  public IntBuffer compact() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    ByteBuffer byteBuffer1 = this.bb.duplicate();
    byteBuffer1.limit(ix(j));
    byteBuffer1.position(ix(0));
    ByteBuffer byteBuffer2 = byteBuffer1.slice();
    byteBuffer2.position(i << 2);
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
