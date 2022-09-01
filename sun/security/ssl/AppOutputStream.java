package sun.security.ssl;

import java.io.IOException;
import java.io.OutputStream;

class AppOutputStream extends OutputStream {
  private SSLSocketImpl c;
  
  OutputRecord r;
  
  private final byte[] oneByte = new byte[1];
  
  AppOutputStream(SSLSocketImpl paramSSLSocketImpl) {
    this.r = new OutputRecord((byte)23);
    this.c = paramSSLSocketImpl;
  }
  
  public synchronized void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfbyte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfbyte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    this.c.checkWrite();
    boolean bool = true;
    try {
      do {
        int i;
        boolean bool1 = false;
        if (bool && this.c.needToSplitPayload()) {
          i = Math.min(1, this.r.availableDataBytes());
          if (paramInt2 != 1 && i == 1)
            bool1 = true; 
        } else {
          i = Math.min(paramInt2, this.r.availableDataBytes());
        } 
        if (bool && i != 0)
          bool = false; 
        if (i > 0) {
          this.r.write(paramArrayOfbyte, paramInt1, i);
          paramInt1 += i;
          paramInt2 -= i;
        } 
        this.c.writeRecord(this.r, bool1);
        this.c.checkWrite();
      } while (paramInt2 > 0);
    } catch (Exception exception) {
      this.c.handleException(exception);
    } 
  }
  
  public synchronized void write(int paramInt) throws IOException {
    this.oneByte[0] = (byte)paramInt;
    write(this.oneByte, 0, 1);
  }
  
  public void close() throws IOException {
    this.c.close();
  }
}
