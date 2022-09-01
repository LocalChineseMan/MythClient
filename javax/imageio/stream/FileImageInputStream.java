package javax.imageio.stream;

import com.sun.imageio.stream.CloseableDisposerRecord;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import sun.java2d.Disposer;

public class FileImageInputStream extends ImageInputStreamImpl {
  private RandomAccessFile raf;
  
  private final Object disposerReferent;
  
  private final CloseableDisposerRecord disposerRecord;
  
  public FileImageInputStream(File paramFile) throws FileNotFoundException, IOException {
    this((paramFile == null) ? null : new RandomAccessFile(paramFile, "r"));
  }
  
  public FileImageInputStream(RandomAccessFile paramRandomAccessFile) {
    if (paramRandomAccessFile == null)
      throw new IllegalArgumentException("raf == null!"); 
    this.raf = paramRandomAccessFile;
    this.disposerRecord = new CloseableDisposerRecord(paramRandomAccessFile);
    if (getClass() == FileImageInputStream.class) {
      this.disposerReferent = new Object();
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    } else {
      this.disposerReferent = new StreamFinalizer(this);
    } 
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.raf.read();
    if (i != -1)
      this.streamPos++; 
    return i;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.raf.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (i != -1)
      this.streamPos += i; 
    return i;
  }
  
  public long length() {
    try {
      checkClosed();
      return this.raf.length();
    } catch (IOException iOException) {
      return -1L;
    } 
  }
  
  public void seek(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    this.bitOffset = 0;
    this.raf.seek(paramLong);
    this.streamPos = this.raf.getFilePointer();
  }
  
  public void close() throws IOException {
    super.close();
    this.disposerRecord.dispose();
    this.raf = null;
  }
  
  protected void finalize() throws Throwable {}
}
