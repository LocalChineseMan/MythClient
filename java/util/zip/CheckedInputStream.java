package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheckedInputStream extends FilterInputStream {
  private Checksum cksum;
  
  public CheckedInputStream(InputStream paramInputStream, Checksum paramChecksum) {
    super(paramInputStream);
    this.cksum = paramChecksum;
  }
  
  public int read() throws IOException {
    int i = this.in.read();
    if (i != -1)
      this.cksum.update(i); 
    return i;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    paramInt2 = this.in.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (paramInt2 != -1)
      this.cksum.update(paramArrayOfbyte, paramInt1, paramInt2); 
    return paramInt2;
  }
  
  public long skip(long paramLong) throws IOException {
    byte[] arrayOfByte = new byte[512];
    long l = 0L;
    while (l < paramLong) {
      long l1 = paramLong - l;
      l1 = read(arrayOfByte, 0, (l1 < arrayOfByte.length) ? (int)l1 : arrayOfByte.length);
      if (l1 == -1L)
        return l; 
      l += l1;
    } 
    return l;
  }
  
  public Checksum getChecksum() {
    return this.cksum;
  }
}
