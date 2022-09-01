package java.nio;

class ByteBufferAsCharBufferB extends CharBuffer {
  protected final ByteBuffer bb;
  
  protected final int offset;
  
  ByteBufferAsCharBufferB(ByteBuffer paramByteBuffer) {
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
  
  ByteBufferAsCharBufferB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.bb = paramByteBuffer;
    this.offset = paramInt5;
  }
  
  public CharBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = (i << 1) + this.offset;
    assert k >= 0;
    return new ByteBufferAsCharBufferB(this.bb, -1, 0, bool, bool, k);
  }
  
  public CharBuffer duplicate() {
    return new ByteBufferAsCharBufferB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  public CharBuffer asReadOnlyBuffer() {
    return new ByteBufferAsCharBufferRB(this.bb, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), this.offset);
  }
  
  protected int ix(int paramInt) {
    return (paramInt << 1) + this.offset;
  }
  
  public char get() {
    return Bits.getCharB(this.bb, ix(nextGetIndex()));
  }
  
  public char get(int paramInt) {
    return Bits.getCharB(this.bb, ix(checkIndex(paramInt)));
  }
  
  char getUnchecked(int paramInt) {
    return Bits.getCharB(this.bb, ix(paramInt));
  }
  
  public CharBuffer put(char paramChar) {
    Bits.putCharB(this.bb, ix(nextPutIndex()), paramChar);
    return this;
  }
  
  public CharBuffer put(int paramInt, char paramChar) {
    Bits.putCharB(this.bb, ix(checkIndex(paramInt)), paramChar);
    return this;
  }
  
  public CharBuffer compact() {
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
  
  public String toString(int paramInt1, int paramInt2) {
    if (paramInt2 > limit() || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    try {
      int i = paramInt2 - paramInt1;
      char[] arrayOfChar = new char[i];
      CharBuffer charBuffer1 = CharBuffer.wrap(arrayOfChar);
      CharBuffer charBuffer2 = duplicate();
      charBuffer2.position(paramInt1);
      charBuffer2.limit(paramInt2);
      charBuffer1.put(charBuffer2);
      return new String(arrayOfChar);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public CharBuffer subSequence(int paramInt1, int paramInt2) {
    int i = position();
    int j = limit();
    assert i <= j;
    i = (i <= j) ? i : j;
    int k = j - i;
    if (paramInt1 < 0 || paramInt2 > k || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    return new ByteBufferAsCharBufferB(this.bb, -1, i + paramInt1, i + paramInt2, 
        
        capacity(), this.offset);
  }
  
  public ByteOrder order() {
    return ByteOrder.BIG_ENDIAN;
  }
}
