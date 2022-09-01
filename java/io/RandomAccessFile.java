package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class RandomAccessFile implements DataOutput, DataInput, Closeable {
  private FileDescriptor fd;
  
  private FileChannel channel = null;
  
  private boolean rw;
  
  private final String path;
  
  private Object closeLock = new Object();
  
  private volatile boolean closed = false;
  
  private static final int O_RDONLY = 1;
  
  private static final int O_RDWR = 2;
  
  private static final int O_SYNC = 4;
  
  private static final int O_DSYNC = 8;
  
  public RandomAccessFile(String paramString1, String paramString2) throws FileNotFoundException {
    this((paramString1 != null) ? new File(paramString1) : null, paramString2);
  }
  
  public RandomAccessFile(File paramFile, String paramString) throws FileNotFoundException {
    String str = (paramFile != null) ? paramFile.getPath() : null;
    int i = -1;
    if (paramString.equals("r")) {
      i = 1;
    } else if (paramString.startsWith("rw")) {
      i = 2;
      this.rw = true;
      if (paramString.length() > 2)
        if (paramString.equals("rws")) {
          i |= 0x4;
        } else if (paramString.equals("rwd")) {
          i |= 0x8;
        } else {
          i = -1;
        }  
    } 
    if (i < 0)
      throw new IllegalArgumentException("Illegal mode \"" + paramString + "\" must be one of " + "\"r\", \"rw\", \"rws\"," + " or \"rwd\""); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkRead(str);
      if (this.rw)
        securityManager.checkWrite(str); 
    } 
    if (str == null)
      throw new NullPointerException(); 
    if (paramFile.isInvalid())
      throw new FileNotFoundException("Invalid file path"); 
    this.fd = new FileDescriptor();
    this.fd.attach(this);
    this.path = str;
    open(str, i);
  }
  
  public final FileDescriptor getFD() throws IOException {
    if (this.fd != null)
      return this.fd; 
    throw new IOException();
  }
  
  public final FileChannel getChannel() {
    synchronized (this) {
      if (this.channel == null)
        this.channel = FileChannelImpl.open(this.fd, this.path, true, this.rw, this); 
      return this.channel;
    } 
  }
  
  private void open(String paramString, int paramInt) throws FileNotFoundException {
    open0(paramString, paramInt);
  }
  
  public int read() throws IOException {
    return read0();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    return readBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int read(byte[] paramArrayOfbyte) throws IOException {
    return readBytes(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final void readFully(byte[] paramArrayOfbyte) throws IOException {
    readFully(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public final void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = 0;
    do {
      int j = read(paramArrayOfbyte, paramInt1 + i, paramInt2 - i);
      if (j < 0)
        throw new EOFException(); 
      i += j;
    } while (i < paramInt2);
  }
  
  public int skipBytes(int paramInt) throws IOException {
    if (paramInt <= 0)
      return 0; 
    long l1 = getFilePointer();
    long l2 = length();
    long l3 = l1 + paramInt;
    if (l3 > l2)
      l3 = l2; 
    seek(l3);
    return (int)(l3 - l1);
  }
  
  public void write(int paramInt) throws IOException {
    write0(paramInt);
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    writeBytes(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    writeBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void seek(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IOException("Negative seek offset"); 
    seek0(paramLong);
  }
  
  public void close() throws IOException {
    synchronized (this.closeLock) {
      if (this.closed)
        return; 
      this.closed = true;
    } 
    if (this.channel != null)
      this.channel.close(); 
    this.fd.closeAll(new Closeable() {
          public void close() throws IOException {
            RandomAccessFile.this.close0();
          }
        });
  }
  
  public final boolean readBoolean() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (i != 0);
  }
  
  public final byte readByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (byte)i;
  }
  
  public final int readUnsignedByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return i;
  }
  
  public final short readShort() throws IOException {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (short)((i << 8) + (j << 0));
  }
  
  public final int readUnsignedShort() throws IOException {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (i << 8) + (j << 0);
  }
  
  public final char readChar() throws IOException {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (char)((i << 8) + (j << 0));
  }
  
  public final int readInt() throws IOException {
    int i = read();
    int j = read();
    int k = read();
    int m = read();
    if ((i | j | k | m) < 0)
      throw new EOFException(); 
    return (i << 24) + (j << 16) + (k << 8) + (m << 0);
  }
  
  public final long readLong() throws IOException {
    return (readInt() << 32L) + (readInt() & 0xFFFFFFFFL);
  }
  
  public final float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }
  
  public final double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }
  
  public final String readLine() throws IOException {
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
          l = getFilePointer();
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
  
  public final String readUTF() throws IOException {
    return DataInputStream.readUTF(this);
  }
  
  public final void writeBoolean(boolean paramBoolean) throws IOException {
    write(paramBoolean ? 1 : 0);
  }
  
  public final void writeByte(int paramInt) throws IOException {
    write(paramInt);
  }
  
  public final void writeShort(int paramInt) throws IOException {
    write(paramInt >>> 8 & 0xFF);
    write(paramInt >>> 0 & 0xFF);
  }
  
  public final void writeChar(int paramInt) throws IOException {
    write(paramInt >>> 8 & 0xFF);
    write(paramInt >>> 0 & 0xFF);
  }
  
  public final void writeInt(int paramInt) throws IOException {
    write(paramInt >>> 24 & 0xFF);
    write(paramInt >>> 16 & 0xFF);
    write(paramInt >>> 8 & 0xFF);
    write(paramInt >>> 0 & 0xFF);
  }
  
  public final void writeLong(long paramLong) throws IOException {
    write((int)(paramLong >>> 56L) & 0xFF);
    write((int)(paramLong >>> 48L) & 0xFF);
    write((int)(paramLong >>> 40L) & 0xFF);
    write((int)(paramLong >>> 32L) & 0xFF);
    write((int)(paramLong >>> 24L) & 0xFF);
    write((int)(paramLong >>> 16L) & 0xFF);
    write((int)(paramLong >>> 8L) & 0xFF);
    write((int)(paramLong >>> 0L) & 0xFF);
  }
  
  public final void writeFloat(float paramFloat) throws IOException {
    writeInt(Float.floatToIntBits(paramFloat));
  }
  
  public final void writeDouble(double paramDouble) throws IOException {
    writeLong(Double.doubleToLongBits(paramDouble));
  }
  
  public final void writeBytes(String paramString) throws IOException {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i];
    paramString.getBytes(0, i, arrayOfByte, 0);
    writeBytes(arrayOfByte, 0, i);
  }
  
  public final void writeChars(String paramString) throws IOException {
    int i = paramString.length();
    int j = 2 * i;
    byte[] arrayOfByte = new byte[j];
    char[] arrayOfChar = new char[i];
    paramString.getChars(0, i, arrayOfChar, 0);
    for (byte b1 = 0, b2 = 0; b1 < i; b1++) {
      arrayOfByte[b2++] = (byte)(arrayOfChar[b1] >>> 8);
      arrayOfByte[b2++] = (byte)(arrayOfChar[b1] >>> 0);
    } 
    writeBytes(arrayOfByte, 0, j);
  }
  
  public final void writeUTF(String paramString) throws IOException {
    DataOutputStream.writeUTF(paramString, this);
  }
  
  static {
    initIDs();
  }
  
  private native void open0(String paramString, int paramInt) throws FileNotFoundException;
  
  private native int read0() throws IOException;
  
  private native int readBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  private native void write0(int paramInt) throws IOException;
  
  private native void writeBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public native long getFilePointer() throws IOException;
  
  private native void seek0(long paramLong) throws IOException;
  
  public native long length() throws IOException;
  
  public native void setLength(long paramLong) throws IOException;
  
  private static native void initIDs();
  
  private native void close0() throws IOException;
}
