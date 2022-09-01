package sun.net.www.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PosterOutputStream extends ByteArrayOutputStream {
  private boolean closed;
  
  public PosterOutputStream() {
    super(256);
  }
  
  public synchronized void write(int paramInt) {
    if (this.closed)
      return; 
    super.write(paramInt);
  }
  
  public synchronized void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.closed)
      return; 
    super.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public synchronized void reset() {
    if (this.closed)
      return; 
    super.reset();
  }
  
  public synchronized void close() throws IOException {
    this.closed = true;
    super.close();
  }
}
