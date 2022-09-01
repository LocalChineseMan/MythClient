package java.nio;

import java.io.FileDescriptor;
import sun.nio.ch.DirectBuffer;

class DirectByteBufferR extends DirectByteBuffer implements DirectBuffer {
  DirectByteBufferR(int paramInt) {
    super(paramInt);
  }
  
  protected DirectByteBufferR(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable) {
    super(paramInt, paramLong, paramFileDescriptor, paramRunnable);
  }
  
  DirectByteBufferR(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public ByteBuffer slice() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = i << 0;
    assert k >= 0;
    return new DirectByteBufferR(this, -1, 0, bool, bool, k);
  }
  
  public ByteBuffer duplicate() {
    return new DirectByteBufferR(this, 
        markValue(), 
        position(), 
        limit(), 
        capacity(), 0);
  }
  
  public ByteBuffer asReadOnlyBuffer() {
    return duplicate();
  }
  
  public ByteBuffer put(byte paramByte) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(int paramInt, byte paramByte) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer compact() {
    throw new ReadOnlyBufferException();
  }
  
  public boolean isDirect() {
    return true;
  }
  
  public boolean isReadOnly() {
    return true;
  }
  
  byte _get(int paramInt) {
    return unsafe.getByte(this.address + paramInt);
  }
  
  void _put(int paramInt, byte paramByte) {
    throw new ReadOnlyBufferException();
  }
  
  private ByteBuffer putChar(long paramLong, char paramChar) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putChar(char paramChar) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putChar(int paramInt, char paramChar) {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer asCharBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 1;
    if (!unaligned && (this.address + i) % 2L != 0L)
      return this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsCharBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectCharBufferRU(this, -1, 0, k, k, i) : new DirectCharBufferRS(this, -1, 0, k, k, i);
  }
  
  private ByteBuffer putShort(long paramLong, short paramShort) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putShort(short paramShort) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putShort(int paramInt, short paramShort) {
    throw new ReadOnlyBufferException();
  }
  
  public ShortBuffer asShortBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 1;
    if (!unaligned && (this.address + i) % 2L != 0L)
      return this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsShortBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectShortBufferRU(this, -1, 0, k, k, i) : new DirectShortBufferRS(this, -1, 0, k, k, i);
  }
  
  private ByteBuffer putInt(long paramLong, int paramInt) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putInt(int paramInt) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2) {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer asIntBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 2;
    if (!unaligned && (this.address + i) % 4L != 0L)
      return this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsIntBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectIntBufferRU(this, -1, 0, k, k, i) : new DirectIntBufferRS(this, -1, 0, k, k, i);
  }
  
  private ByteBuffer putLong(long paramLong1, long paramLong2) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putLong(long paramLong) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putLong(int paramInt, long paramLong) {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer asLongBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 3;
    if (!unaligned && (this.address + i) % 8L != 0L)
      return this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsLongBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectLongBufferRU(this, -1, 0, k, k, i) : new DirectLongBufferRS(this, -1, 0, k, k, i);
  }
  
  private ByteBuffer putFloat(long paramLong, float paramFloat) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putFloat(float paramFloat) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat) {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer asFloatBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 2;
    if (!unaligned && (this.address + i) % 4L != 0L)
      return this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsFloatBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectFloatBufferRU(this, -1, 0, k, k, i) : new DirectFloatBufferRS(this, -1, 0, k, k, i);
  }
  
  private ByteBuffer putDouble(long paramLong, double paramDouble) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putDouble(double paramDouble) {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble) {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer asDoubleBuffer() {
    int i = position();
    int j = limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = bool >> 3;
    if (!unaligned && (this.address + i) % 8L != 0L)
      return this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, k, k, i) : new ByteBufferAsDoubleBufferRL(this, -1, 0, k, k, i); 
    return this.nativeByteOrder ? new DirectDoubleBufferRU(this, -1, 0, k, k, i) : new DirectDoubleBufferRS(this, -1, 0, k, k, i);
  }
}
