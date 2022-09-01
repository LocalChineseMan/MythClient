package sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;

class AppInputStream extends InputStream {
  private static final byte[] SKIP_ARRAY = new byte[1024];
  
  private SSLSocketImpl c;
  
  InputRecord r;
  
  private final byte[] oneByte = new byte[1];
  
  AppInputStream(SSLSocketImpl paramSSLSocketImpl) {
    this.r = new InputRecord();
    this.c = paramSSLSocketImpl;
  }
  
  public int available() throws IOException {
    if (this.c.checkEOF() || !this.r.isAppDataValid())
      return 0; 
    return this.r.available();
  }
  
  public synchronized int read() throws IOException {
    int i = read(this.oneByte, 0, 1);
    if (i <= 0)
      return -1; 
    return this.oneByte[0] & 0xFF;
  }
  
  public synchronized int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfbyte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfbyte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    if (this.c.checkEOF())
      return -1; 
    try {
      while (this.r.available() == 0) {
        this.c.readDataRecord(this.r);
        if (this.c.checkEOF())
          return -1; 
      } 
      int i = Math.min(paramInt2, this.r.available());
      i = this.r.read(paramArrayOfbyte, paramInt1, i);
      return i;
    } catch (Exception exception) {
      this.c.handleException(exception);
      return -1;
    } 
  }
  
  public synchronized long skip(long paramLong) throws IOException {
    long l = 0L;
    while (paramLong > 0L) {
      int i = (int)Math.min(paramLong, SKIP_ARRAY.length);
      int j = read(SKIP_ARRAY, 0, i);
      if (j <= 0)
        break; 
      paramLong -= j;
      l += j;
    } 
    return l;
  }
  
  public void close() throws IOException {
    this.c.close();
  }
}
