package javax.imageio.stream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Stack;
import javax.imageio.IIOException;

public abstract class ImageInputStreamImpl implements ImageInputStream {
  private Stack markByteStack = new Stack();
  
  private Stack markBitStack = new Stack();
  
  private boolean isClosed = false;
  
  private static final int BYTE_BUF_LENGTH = 8192;
  
  byte[] byteBuf = new byte[8192];
  
  protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
  
  protected long streamPos;
  
  protected int bitOffset;
  
  protected long flushedPos = 0L;
  
  protected final void checkClosed() throws IOException {
    if (this.isClosed)
      throw new IOException("closed"); 
  }
  
  public void setByteOrder(ByteOrder paramByteOrder) {
    this.byteOrder = paramByteOrder;
  }
  
  public ByteOrder getByteOrder() {
    return this.byteOrder;
  }
  
  public abstract int read() throws IOException;
  
  public int read(byte[] paramArrayOfbyte) throws IOException {
    return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public abstract int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public void readBytes(IIOByteBuffer paramIIOByteBuffer, int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("len < 0!"); 
    if (paramIIOByteBuffer == null)
      throw new NullPointerException("buf == null!"); 
    byte[] arrayOfByte = new byte[paramInt];
    paramInt = read(arrayOfByte, 0, paramInt);
    paramIIOByteBuffer.setData(arrayOfByte);
    paramIIOByteBuffer.setOffset(0);
    paramIIOByteBuffer.setLength(paramInt);
  }
  
  public boolean readBoolean() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (i != 0);
  }
  
  public byte readByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (byte)i;
  }
  
  public int readUnsignedByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return i;
  }
  
  public short readShort() throws IOException {
    if (read(this.byteBuf, 0, 2) < 0)
      throw new EOFException(); 
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
      return (short)((this.byteBuf[0] & 0xFF) << 8 | (this.byteBuf[1] & 0xFF) << 0); 
    return (short)((this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0);
  }
  
  public int readUnsignedShort() throws IOException {
    return readShort() & 0xFFFF;
  }
  
  public char readChar() throws IOException {
    return (char)readShort();
  }
  
  public int readInt() throws IOException {
    if (read(this.byteBuf, 0, 4) < 0)
      throw new EOFException(); 
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
      return (this.byteBuf[0] & 0xFF) << 24 | (this.byteBuf[1] & 0xFF) << 16 | (this.byteBuf[2] & 0xFF) << 8 | (this.byteBuf[3] & 0xFF) << 0; 
    return (this.byteBuf[3] & 0xFF) << 24 | (this.byteBuf[2] & 0xFF) << 16 | (this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0;
  }
  
  public long readUnsignedInt() throws IOException {
    return readInt() & 0xFFFFFFFFL;
  }
  
  public long readLong() throws IOException {
    int i = readInt();
    int j = readInt();
    if (this.byteOrder == ByteOrder.BIG_ENDIAN)
      return (i << 32L) + (j & 0xFFFFFFFFL); 
    return (j << 32L) + (i & 0xFFFFFFFFL);
  }
  
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }
  
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }
  
  public String readLine() throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    int i = -1;
    boolean bool = false;
    while (!bool) {
      long l;
      switch (i = read()) {
        case -1:
        case 10:
          bool = true;
          continue;
        case 13:
          bool = true;
          l = getStreamPosition();
          if (read() != 10)
            seek(l); 
          continue;
      } 
      stringBuffer.append((char)i);
    } 
    if (i == -1 && stringBuffer.length() == 0)
      return null; 
    return stringBuffer.toString();
  }
  
  public String readUTF() throws IOException {
    String str;
    this.bitOffset = 0;
    ByteOrder byteOrder = getByteOrder();
    setByteOrder(ByteOrder.BIG_ENDIAN);
    try {
      str = DataInputStream.readUTF(this);
    } catch (IOException iOException) {
      setByteOrder(byteOrder);
      throw iOException;
    } 
    setByteOrder(byteOrder);
    return str;
  }
  
  public void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfbyte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!"); 
    while (paramInt2 > 0) {
      int i = read(paramArrayOfbyte, paramInt1, paramInt2);
      if (i == -1)
        throw new EOFException(); 
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(byte[] paramArrayOfbyte) throws IOException {
    readFully(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void readFully(short[] paramArrayOfshort, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfshort.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, i * 2);
      toShorts(this.byteBuf, paramArrayOfshort, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfchar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, i * 2);
      toChars(this.byteBuf, paramArrayOfchar, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(int[] paramArrayOfint, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfint.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, i * 4);
      toInts(this.byteBuf, paramArrayOfint, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(long[] paramArrayOflong, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOflong.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, i * 8);
      toLongs(this.byteBuf, paramArrayOflong, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(float[] paramArrayOffloat, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOffloat.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, i * 4);
      toFloats(this.byteBuf, paramArrayOffloat, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(double[] paramArrayOfdouble, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfdouble.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, i * 8);
      toDoubles(this.byteBuf, paramArrayOfdouble, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  private void toShorts(byte[] paramArrayOfbyte, short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        paramArrayOfshort[paramInt1 + b1] = (short)(b2 << 8 | i);
        b += 2;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 1];
        int i = paramArrayOfbyte[b] & 0xFF;
        paramArrayOfshort[paramInt1 + b1] = (short)(b2 << 8 | i);
        b += 2;
      } 
    } 
  }
  
  private void toChars(byte[] paramArrayOfbyte, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        paramArrayOfchar[paramInt1 + b1] = (char)(b2 << 8 | i);
        b += 2;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 1];
        int i = paramArrayOfbyte[b] & 0xFF;
        paramArrayOfchar[paramInt1 + b1] = (char)(b2 << 8 | i);
        b += 2;
      } 
    } 
  }
  
  private void toInts(byte[] paramArrayOfbyte, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        int j = paramArrayOfbyte[b + 2] & 0xFF;
        int k = paramArrayOfbyte[b + 3] & 0xFF;
        paramArrayOfint[paramInt1 + b1] = b2 << 24 | i << 16 | j << 8 | k;
        b += 4;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 3];
        int i = paramArrayOfbyte[b + 2] & 0xFF;
        int j = paramArrayOfbyte[b + 1] & 0xFF;
        int k = paramArrayOfbyte[b] & 0xFF;
        paramArrayOfint[paramInt1 + b1] = b2 << 24 | i << 16 | j << 8 | k;
        b += 4;
      } 
    } 
  }
  
  private void toLongs(byte[] paramArrayOfbyte, long[] paramArrayOflong, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        int j = paramArrayOfbyte[b + 2] & 0xFF;
        int k = paramArrayOfbyte[b + 3] & 0xFF;
        byte b3 = paramArrayOfbyte[b + 4];
        int m = paramArrayOfbyte[b + 5] & 0xFF;
        int n = paramArrayOfbyte[b + 6] & 0xFF;
        int i1 = paramArrayOfbyte[b + 7] & 0xFF;
        int i2 = b2 << 24 | i << 16 | j << 8 | k;
        int i3 = b3 << 24 | m << 16 | n << 8 | i1;
        paramArrayOflong[paramInt1 + b1] = i2 << 32L | i3 & 0xFFFFFFFFL;
        b += 8;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 7];
        int i = paramArrayOfbyte[b + 6] & 0xFF;
        int j = paramArrayOfbyte[b + 5] & 0xFF;
        int k = paramArrayOfbyte[b + 4] & 0xFF;
        byte b3 = paramArrayOfbyte[b + 3];
        int m = paramArrayOfbyte[b + 2] & 0xFF;
        int n = paramArrayOfbyte[b + 1] & 0xFF;
        int i1 = paramArrayOfbyte[b] & 0xFF;
        int i2 = b2 << 24 | i << 16 | j << 8 | k;
        int i3 = b3 << 24 | m << 16 | n << 8 | i1;
        paramArrayOflong[paramInt1 + b1] = i2 << 32L | i3 & 0xFFFFFFFFL;
        b += 8;
      } 
    } 
  }
  
  private void toFloats(byte[] paramArrayOfbyte, float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        int j = paramArrayOfbyte[b + 2] & 0xFF;
        int k = paramArrayOfbyte[b + 3] & 0xFF;
        int m = b2 << 24 | i << 16 | j << 8 | k;
        paramArrayOffloat[paramInt1 + b1] = Float.intBitsToFloat(m);
        b += 4;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 3];
        int i = paramArrayOfbyte[b + 2] & 0xFF;
        int j = paramArrayOfbyte[b + 1] & 0xFF;
        int k = paramArrayOfbyte[b + 0] & 0xFF;
        int m = b2 << 24 | i << 16 | j << 8 | k;
        paramArrayOffloat[paramInt1 + b1] = Float.intBitsToFloat(m);
        b += 4;
      } 
    } 
  }
  
  private void toDoubles(byte[] paramArrayOfbyte, double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b];
        int i = paramArrayOfbyte[b + 1] & 0xFF;
        int j = paramArrayOfbyte[b + 2] & 0xFF;
        int k = paramArrayOfbyte[b + 3] & 0xFF;
        byte b3 = paramArrayOfbyte[b + 4];
        int m = paramArrayOfbyte[b + 5] & 0xFF;
        int n = paramArrayOfbyte[b + 6] & 0xFF;
        int i1 = paramArrayOfbyte[b + 7] & 0xFF;
        int i2 = b2 << 24 | i << 16 | j << 8 | k;
        int i3 = b3 << 24 | m << 16 | n << 8 | i1;
        long l = i2 << 32L | i3 & 0xFFFFFFFFL;
        paramArrayOfdouble[paramInt1 + b1] = Double.longBitsToDouble(l);
        b += 8;
      } 
    } else {
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        byte b2 = paramArrayOfbyte[b + 7];
        int i = paramArrayOfbyte[b + 6] & 0xFF;
        int j = paramArrayOfbyte[b + 5] & 0xFF;
        int k = paramArrayOfbyte[b + 4] & 0xFF;
        byte b3 = paramArrayOfbyte[b + 3];
        int m = paramArrayOfbyte[b + 2] & 0xFF;
        int n = paramArrayOfbyte[b + 1] & 0xFF;
        int i1 = paramArrayOfbyte[b] & 0xFF;
        int i2 = b2 << 24 | i << 16 | j << 8 | k;
        int i3 = b3 << 24 | m << 16 | n << 8 | i1;
        long l = i2 << 32L | i3 & 0xFFFFFFFFL;
        paramArrayOfdouble[paramInt1 + b1] = Double.longBitsToDouble(l);
        b += 8;
      } 
    } 
  }
  
  public long getStreamPosition() throws IOException {
    checkClosed();
    return this.streamPos;
  }
  
  public int getBitOffset() throws IOException {
    checkClosed();
    return this.bitOffset;
  }
  
  public void setBitOffset(int paramInt) throws IOException {
    checkClosed();
    if (paramInt < 0 || paramInt > 7)
      throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!"); 
    this.bitOffset = paramInt;
  }
  
  public int readBit() throws IOException {
    checkClosed();
    int i = this.bitOffset + 1 & 0x7;
    int j = read();
    if (j == -1)
      throw new EOFException(); 
    if (i != 0) {
      seek(getStreamPosition() - 1L);
      j >>= 8 - i;
    } 
    this.bitOffset = i;
    return j & 0x1;
  }
  
  public long readBits(int paramInt) throws IOException {
    checkClosed();
    if (paramInt < 0 || paramInt > 64)
      throw new IllegalArgumentException(); 
    if (paramInt == 0)
      return 0L; 
    int i = paramInt + this.bitOffset;
    int j = this.bitOffset + paramInt & 0x7;
    long l = 0L;
    while (i > 0) {
      int k = read();
      if (k == -1)
        throw new EOFException(); 
      l <<= 8L;
      l |= k;
      i -= 8;
    } 
    if (j != 0)
      seek(getStreamPosition() - 1L); 
    this.bitOffset = j;
    l >>>= -i;
    l &= -1L >>> 64 - paramInt;
    return l;
  }
  
  public long length() {
    return -1L;
  }
  
  public int skipBytes(int paramInt) throws IOException {
    long l = getStreamPosition();
    seek(l + paramInt);
    return (int)(getStreamPosition() - l);
  }
  
  public long skipBytes(long paramLong) throws IOException {
    long l = getStreamPosition();
    seek(l + paramLong);
    return getStreamPosition() - l;
  }
  
  public void seek(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    this.streamPos = paramLong;
    this.bitOffset = 0;
  }
  
  public void mark() {
    try {
      this.markByteStack.push(Long.valueOf(getStreamPosition()));
      this.markBitStack.push(Integer.valueOf(getBitOffset()));
    } catch (IOException iOException) {}
  }
  
  public void reset() throws IOException {
    if (this.markByteStack.empty())
      return; 
    long l = ((Long)this.markByteStack.pop()).longValue();
    if (l < this.flushedPos)
      throw new IIOException("Previous marked position has been discarded!"); 
    seek(l);
    int i = ((Integer)this.markBitStack.pop()).intValue();
    setBitOffset(i);
  }
  
  public void flushBefore(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    if (paramLong > getStreamPosition())
      throw new IndexOutOfBoundsException("pos > getStreamPosition()!"); 
    this.flushedPos = paramLong;
  }
  
  public void flush() throws IOException {
    flushBefore(getStreamPosition());
  }
  
  public long getFlushedPosition() {
    return this.flushedPos;
  }
  
  public boolean isCached() {
    return false;
  }
  
  public boolean isCachedMemory() {
    return false;
  }
  
  public boolean isCachedFile() {
    return false;
  }
  
  public void close() throws IOException {
    checkClosed();
    this.isClosed = true;
  }
  
  protected void finalize() throws Throwable {
    if (!this.isClosed)
      try {
        close();
      } catch (IOException iOException) {} 
    super.finalize();
  }
}
