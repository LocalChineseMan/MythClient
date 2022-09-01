package javax.imageio.stream;

import com.sun.imageio.stream.StreamFinalizer;
import java.io.IOException;
import java.io.InputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class MemoryCacheImageInputStream extends ImageInputStreamImpl {
  private InputStream stream;
  
  private MemoryCache cache = new MemoryCache();
  
  private final Object disposerReferent;
  
  private final DisposerRecord disposerRecord;
  
  public MemoryCacheImageInputStream(InputStream paramInputStream) {
    if (paramInputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    this.stream = paramInputStream;
    this.disposerRecord = new StreamDisposerRecord(this.cache);
    if (getClass() == MemoryCacheImageInputStream.class) {
      this.disposerReferent = new Object();
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    } else {
      this.disposerReferent = new StreamFinalizer(this);
    } 
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    long l = this.cache.loadFromStream(this.stream, this.streamPos + 1L);
    if (l >= this.streamPos + 1L)
      return this.cache.read(this.streamPos++); 
    return -1;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    checkClosed();
    if (paramArrayOfbyte == null)
      throw new NullPointerException("b == null!"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfbyte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!"); 
    this.bitOffset = 0;
    if (paramInt2 == 0)
      return 0; 
    long l = this.cache.loadFromStream(this.stream, this.streamPos + paramInt2);
    paramInt2 = (int)(l - this.streamPos);
    if (paramInt2 > 0) {
      this.cache.read(paramArrayOfbyte, paramInt1, paramInt2, this.streamPos);
      this.streamPos += paramInt2;
      return paramInt2;
    } 
    return -1;
  }
  
  public void flushBefore(long paramLong) throws IOException {
    super.flushBefore(paramLong);
    this.cache.disposeBefore(paramLong);
  }
  
  public boolean isCached() {
    return true;
  }
  
  public boolean isCachedFile() {
    return false;
  }
  
  public boolean isCachedMemory() {
    return true;
  }
  
  public void close() throws IOException {
    super.close();
    this.disposerRecord.dispose();
    this.stream = null;
    this.cache = null;
  }
  
  protected void finalize() throws Throwable {}
  
  private static class StreamDisposerRecord implements DisposerRecord {
    private MemoryCache cache;
    
    public StreamDisposerRecord(MemoryCache param1MemoryCache) {
      this.cache = param1MemoryCache;
    }
    
    public synchronized void dispose() {
      if (this.cache != null) {
        this.cache.reset();
        this.cache = null;
      } 
    }
  }
}
