package java.io;

public class PushbackInputStream extends FilterInputStream {
  protected byte[] buf;
  
  protected int pos;
  
  private void ensureOpen() throws IOException {
    if (this.in == null)
      throw new IOException("Stream closed"); 
  }
  
  public PushbackInputStream(InputStream paramInputStream, int paramInt) {
    super(paramInputStream);
    if (paramInt <= 0)
      throw new IllegalArgumentException("size <= 0"); 
    this.buf = new byte[paramInt];
    this.pos = paramInt;
  }
  
  public PushbackInputStream(InputStream paramInputStream) {
    this(paramInputStream, 1);
  }
  
  public int read() throws IOException {
    ensureOpen();
    if (this.pos < this.buf.length)
      return this.buf[this.pos++] & 0xFF; 
    return super.read();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramArrayOfbyte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfbyte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = this.buf.length - this.pos;
    if (i > 0) {
      if (paramInt2 < i)
        i = paramInt2; 
      System.arraycopy(this.buf, this.pos, paramArrayOfbyte, paramInt1, i);
      this.pos += i;
      paramInt1 += i;
      paramInt2 -= i;
    } 
    if (paramInt2 > 0) {
      paramInt2 = super.read(paramArrayOfbyte, paramInt1, paramInt2);
      if (paramInt2 == -1)
        return (i == 0) ? -1 : i; 
      return i + paramInt2;
    } 
    return i;
  }
  
  public void unread(int paramInt) throws IOException {
    ensureOpen();
    if (this.pos == 0)
      throw new IOException("Push back buffer is full"); 
    this.buf[--this.pos] = (byte)paramInt;
  }
  
  public void unread(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramInt2 > this.pos)
      throw new IOException("Push back buffer is full"); 
    this.pos -= paramInt2;
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.pos, paramInt2);
  }
  
  public void unread(byte[] paramArrayOfbyte) throws IOException {
    unread(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public int available() throws IOException {
    ensureOpen();
    int i = this.buf.length - this.pos;
    int j = super.available();
    return (i > Integer.MAX_VALUE - j) ? Integer.MAX_VALUE : (i + j);
  }
  
  public long skip(long paramLong) throws IOException {
    ensureOpen();
    if (paramLong <= 0L)
      return 0L; 
    long l = (this.buf.length - this.pos);
    if (l > 0L) {
      if (paramLong < l)
        l = paramLong; 
      this.pos = (int)(this.pos + l);
      paramLong -= l;
    } 
    if (paramLong > 0L)
      l += super.skip(paramLong); 
    return l;
  }
  
  public boolean markSupported() {
    return false;
  }
  
  public synchronized void mark(int paramInt) {}
  
  public synchronized void reset() throws IOException {
    throw new IOException("mark/reset not supported");
  }
  
  public synchronized void close() throws IOException {
    if (this.in == null)
      return; 
    this.in.close();
    this.in = null;
    this.buf = null;
  }
}
