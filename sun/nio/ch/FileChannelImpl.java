package sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import sun.misc.Cleaner;
import sun.misc.JavaNioAccess;
import sun.security.action.GetPropertyAction;

public class FileChannelImpl extends FileChannel {
  private static final long allocationGranularity;
  
  private final FileDispatcher nd;
  
  private final FileDescriptor fd;
  
  private final boolean writable;
  
  private final boolean readable;
  
  private final boolean append;
  
  private final Object parent;
  
  private final String path;
  
  private final NativeThreadSet threads = new NativeThreadSet(2);
  
  private final Object positionLock = new Object();
  
  private FileChannelImpl(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject) {
    this.fd = paramFileDescriptor;
    this.readable = paramBoolean1;
    this.writable = paramBoolean2;
    this.append = paramBoolean3;
    this.parent = paramObject;
    this.path = paramString;
    this.nd = new FileDispatcherImpl(paramBoolean3);
  }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject) {
    return (FileChannel)new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, false, paramObject);
  }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject) {
    return (FileChannel)new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
  }
  
  private void ensureOpen() throws IOException {
    if (!isOpen())
      throw new ClosedChannelException(); 
  }
  
  protected void implCloseChannel() throws IOException {
    if (this.fileLockTable != null)
      for (FileLock fileLock : this.fileLockTable.removeAll()) {
        synchronized (fileLock) {
          if (fileLock.isValid()) {
            this.nd.release(this.fd, fileLock.position(), fileLock.size());
            ((FileLockImpl)fileLock).invalidate();
          } 
        } 
      }  
    this.threads.signalAndWait();
    if (this.parent != null) {
      ((Closeable)this.parent).close();
    } else {
      this.nd.close(this.fd);
    } 
  }
  
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    ensureOpen();
    if (!this.readable)
      throw new NonReadableChannelException(); 
    synchronized (this.positionLock) {
      int i = 0;
      int j = -1;
      try {
        begin();
        j = this.threads.add();
        if (!isOpen())
          return 0; 
        do {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, this.nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.threads.remove(j);
        end((i > 0));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    ensureOpen();
    if (!this.readable)
      throw new NonReadableChannelException(); 
    synchronized (this.positionLock) {
      long l = 0L;
      int i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > 0L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    ensureOpen();
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      int i = 0;
      int j = -1;
      try {
        begin();
        j = this.threads.add();
        if (!isOpen())
          return 0; 
        do {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, this.nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.threads.remove(j);
        end((i > 0));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    ensureOpen();
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      long l = 0L;
      int i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > 0L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public long position() throws IOException {
    ensureOpen();
    synchronized (this.positionLock) {
      long l = -1L;
      int i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = this.append ? this.nd.size(this.fd) : position0(this.fd, -1L);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public FileChannel position(long paramLong) throws IOException {
    ensureOpen();
    if (paramLong < 0L)
      throw new IllegalArgumentException(); 
    synchronized (this.positionLock) {
      long l = -1L;
      int i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return null; 
        do {
          l = position0(this.fd, paramLong);
        } while (l == -3L && isOpen());
        return (FileChannel)this;
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public long size() throws IOException {
    ensureOpen();
    synchronized (this.positionLock) {
      long l = -1L;
      int i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return -1L; 
        do {
          l = this.nd.size(this.fd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public FileChannel truncate(long paramLong) throws IOException {
    ensureOpen();
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      int i = -1;
      long l = -1L;
      int j = -1;
      try {
        long l1;
        begin();
        j = this.threads.add();
        if (!isOpen())
          return null; 
        do {
          l1 = this.nd.size(this.fd);
        } while (l1 == -3L && isOpen());
        if (!isOpen())
          return null; 
        do {
          l = position0(this.fd, -1L);
        } while (l == -3L && isOpen());
        if (!isOpen())
          return null; 
        assert l >= 0L;
        if (paramLong < l1) {
          do {
            i = this.nd.truncate(this.fd, paramLong);
          } while (i == -3 && isOpen());
          if (!isOpen())
            return null; 
        } 
        if (l > paramLong)
          l = paramLong; 
        do {
          i = (int)position0(this.fd, l);
        } while (i == -3 && isOpen());
        return (FileChannel)this;
      } finally {
        this.threads.remove(j);
        end((i > -1));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public void force(boolean paramBoolean) throws IOException {
    ensureOpen();
    int i = -1;
    int j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return; 
      do {
        i = this.nd.force(this.fd, paramBoolean);
      } while (i == -3 && isOpen());
    } finally {
      this.threads.remove(j);
      end((i > -1));
      assert IOStatus.check(i);
    } 
  }
  
  private static volatile boolean transferSupported = true;
  
  private static volatile boolean pipeSupported = true;
  
  private static volatile boolean fileSupported = true;
  
  private static final long MAPPED_TRANSFER_SIZE = 8388608L;
  
  private static final int TRANSFER_SIZE = 8192;
  
  private static final int MAP_RO = 0;
  
  private static final int MAP_RW = 1;
  
  private static final int MAP_PV = 2;
  
  private volatile FileLockTable fileLockTable;
  
  private static boolean isSharedFileLockTable;
  
  private static volatile boolean propertyChecked;
  
  private long transferToDirectly(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel) throws IOException {
    if (!transferSupported)
      return -4L; 
    FileDescriptor fileDescriptor = null;
    if (paramWritableByteChannel instanceof FileChannelImpl) {
      if (!fileSupported)
        return -6L; 
      fileDescriptor = ((FileChannelImpl)paramWritableByteChannel).fd;
    } else if (paramWritableByteChannel instanceof SelChImpl) {
      if (paramWritableByteChannel instanceof SinkChannelImpl && !pipeSupported)
        return -6L; 
      fileDescriptor = ((SelChImpl)paramWritableByteChannel).getFD();
    } 
    if (fileDescriptor == null)
      return -4L; 
    int i = IOUtil.fdVal(this.fd);
    int j = IOUtil.fdVal(fileDescriptor);
    if (i == j)
      return -4L; 
    long l = -1L;
    int k = -1;
    try {
      begin();
      k = this.threads.add();
      if (!isOpen())
        return -1L; 
      do {
        l = transferTo0(i, paramLong, paramInt, j);
      } while (l == -3L && isOpen());
      if (l == -6L) {
        if (paramWritableByteChannel instanceof SinkChannelImpl)
          pipeSupported = false; 
        if (paramWritableByteChannel instanceof FileChannelImpl)
          fileSupported = false; 
        return -6L;
      } 
      if (l == -4L) {
        transferSupported = false;
        return -4L;
      } 
      return IOStatus.normalize(l);
    } finally {
      this.threads.remove(k);
      end((l > -1L));
    } 
  }
  
  private long transferToTrustedChannel(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
    boolean bool = paramWritableByteChannel instanceof SelChImpl;
    if (!(paramWritableByteChannel instanceof FileChannelImpl) && !bool)
      return -4L; 
    long l = paramLong2;
    while (l > 0L) {
      long l1 = Math.min(l, 8388608L);
      try {
        MappedByteBuffer mappedByteBuffer = map(FileChannel.MapMode.READ_ONLY, paramLong1, l1);
        try {
          int i = paramWritableByteChannel.write(mappedByteBuffer);
          assert i >= 0;
          l -= i;
          if (bool) {
            unmap(mappedByteBuffer);
            break;
          } 
          assert i > 0;
          paramLong1 += i;
        } finally {
          unmap(mappedByteBuffer);
        } 
        continue;
      } catch (ClosedByInterruptException closedByInterruptException) {
        assert !paramWritableByteChannel.isOpen();
        try {
          close();
        } catch (Throwable throwable) {
          closedByInterruptException.addSuppressed(throwable);
        } 
        throw closedByInterruptException;
      } catch (IOException iOException) {
        if (l == paramLong2)
          throw iOException; 
      } 
      return paramLong2 - l;
    } 
    return paramLong2 - l;
  }
  
  private long transferToArbitraryChannel(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel) throws IOException {
    int i = Math.min(paramInt, 8192);
    ByteBuffer byteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong;
    try {
      Util.erase(byteBuffer);
      while (l1 < paramInt) {
        byteBuffer.limit(Math.min((int)(paramInt - l1), 8192));
        int j = read(byteBuffer, l2);
        if (j <= 0)
          break; 
        byteBuffer.flip();
        int k = paramWritableByteChannel.write(byteBuffer);
        l1 += k;
        if (k != j)
          break; 
        l2 += k;
        byteBuffer.clear();
      } 
      return l1;
    } catch (IOException iOException) {
      if (l1 > 0L)
        return l1; 
      throw iOException;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
    ensureOpen();
    if (!paramWritableByteChannel.isOpen())
      throw new ClosedChannelException(); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    if (paramWritableByteChannel instanceof FileChannelImpl && !((FileChannelImpl)paramWritableByteChannel).writable)
      throw new NonWritableChannelException(); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException(); 
    long l1 = size();
    if (paramLong1 > l1)
      return 0L; 
    int i = (int)Math.min(paramLong2, 2147483647L);
    if (l1 - paramLong1 < i)
      i = (int)(l1 - paramLong1); 
    long l2;
    if ((l2 = transferToDirectly(paramLong1, i, paramWritableByteChannel)) >= 0L)
      return l2; 
    if ((l2 = transferToTrustedChannel(paramLong1, i, paramWritableByteChannel)) >= 0L)
      return l2; 
    return transferToArbitraryChannel(paramLong1, i, paramWritableByteChannel);
  }
  
  private long transferFromFileChannel(FileChannelImpl paramFileChannelImpl, long paramLong1, long paramLong2) throws IOException {
    if (!paramFileChannelImpl.readable)
      throw new NonReadableChannelException(); 
    synchronized (paramFileChannelImpl.positionLock) {
      long l1 = paramFileChannelImpl.position();
      long l2 = Math.min(paramLong2, paramFileChannelImpl.size() - l1);
      long l3 = l2;
      long l4 = l1;
      while (l3 > 0L) {
        long l = Math.min(l3, 8388608L);
        MappedByteBuffer mappedByteBuffer = paramFileChannelImpl.map(FileChannel.MapMode.READ_ONLY, l4, l);
        try {
          long l6 = write(mappedByteBuffer, paramLong1);
          assert l6 > 0L;
          l4 += l6;
          paramLong1 += l6;
          l3 -= l6;
        } catch (IOException iOException) {
          if (l3 == l2)
            throw iOException; 
          break;
        } finally {
          unmap(mappedByteBuffer);
        } 
      } 
      long l5 = l2 - l3;
      paramFileChannelImpl.position(l1 + l5);
      return l5;
    } 
  }
  
  private long transferFromArbitraryChannel(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
    int i = (int)Math.min(paramLong2, 8192L);
    ByteBuffer byteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong1;
    try {
      Util.erase(byteBuffer);
      while (l1 < paramLong2) {
        byteBuffer.limit((int)Math.min(paramLong2 - l1, 8192L));
        int j = paramReadableByteChannel.read(byteBuffer);
        if (j <= 0)
          break; 
        byteBuffer.flip();
        int k = write(byteBuffer, l2);
        l1 += k;
        if (k != j)
          break; 
        l2 += k;
        byteBuffer.clear();
      } 
      return l1;
    } catch (IOException iOException) {
      if (l1 > 0L)
        return l1; 
      throw iOException;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
    ensureOpen();
    if (!paramReadableByteChannel.isOpen())
      throw new ClosedChannelException(); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException(); 
    if (paramLong1 > size())
      return 0L; 
    if (paramReadableByteChannel instanceof FileChannelImpl)
      return transferFromFileChannel((FileChannelImpl)paramReadableByteChannel, paramLong1, paramLong2); 
    return transferFromArbitraryChannel(paramReadableByteChannel, paramLong1, paramLong2);
  }
  
  public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    ensureOpen();
    if (this.nd.needsPositionLock())
      synchronized (this.positionLock) {
        return readInternal(paramByteBuffer, paramLong);
      }  
    return readInternal(paramByteBuffer, paramLong);
  }
  
  private int readInternal(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    assert !this.nd.needsPositionLock() || Thread.holdsLock(this.positionLock);
    int i = 0;
    int j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return -1; 
      do {
        i = IOUtil.read(this.fd, paramByteBuffer, paramLong, this.nd);
      } while (i == -3 && isOpen());
      return IOStatus.normalize(i);
    } finally {
      this.threads.remove(j);
      end((i > 0));
      assert IOStatus.check(i);
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    ensureOpen();
    if (this.nd.needsPositionLock())
      synchronized (this.positionLock) {
        return writeInternal(paramByteBuffer, paramLong);
      }  
    return writeInternal(paramByteBuffer, paramLong);
  }
  
  private int writeInternal(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    assert !this.nd.needsPositionLock() || Thread.holdsLock(this.positionLock);
    int i = 0;
    int j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return -1; 
      do {
        i = IOUtil.write(this.fd, paramByteBuffer, paramLong, this.nd);
      } while (i == -3 && isOpen());
      return IOStatus.normalize(i);
    } finally {
      this.threads.remove(j);
      end((i > 0));
      assert IOStatus.check(i);
    } 
  }
  
  private static void unmap(MappedByteBuffer paramMappedByteBuffer) {
    Cleaner cleaner = ((DirectBuffer)paramMappedByteBuffer).cleaner();
    if (cleaner != null)
      cleaner.clean(); 
  }
  
  public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2) throws IOException {
    ensureOpen();
    if (paramMapMode == null)
      throw new NullPointerException("Mode is null"); 
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (paramLong2 < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (paramLong1 + paramLong2 < 0L)
      throw new IllegalArgumentException("Position + size overflow"); 
    if (paramLong2 > 2147483647L)
      throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE"); 
    byte b = -1;
    if (paramMapMode == FileChannel.MapMode.READ_ONLY) {
      b = 0;
    } else if (paramMapMode == FileChannel.MapMode.READ_WRITE) {
      b = 1;
    } else if (paramMapMode == FileChannel.MapMode.PRIVATE) {
      b = 2;
    } 
    assert b >= 0;
    if (paramMapMode != FileChannel.MapMode.READ_ONLY && !this.writable)
      throw new NonWritableChannelException(); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    long l = -1L;
    int i = -1;
    try {
      long l1;
      FileDescriptor fileDescriptor;
      begin();
      i = this.threads.add();
      if (!isOpen())
        return null; 
      do {
        l1 = this.nd.size(this.fd);
      } while (l1 == -3L && isOpen());
      if (!isOpen())
        return null; 
      if (l1 < paramLong1 + paramLong2) {
        int m;
        if (!this.writable)
          throw new IOException("Channel not open for writing - cannot extend file to required size"); 
        do {
          m = this.nd.truncate(this.fd, paramLong1 + paramLong2);
        } while (m == -3 && isOpen());
        if (!isOpen())
          return null; 
      } 
      if (paramLong2 == 0L) {
        l = 0L;
        FileDescriptor fileDescriptor1 = new FileDescriptor();
        if (!this.writable || b == 0)
          return Util.newMappedByteBufferR(0, 0L, fileDescriptor1, null); 
        return Util.newMappedByteBuffer(0, 0L, fileDescriptor1, null);
      } 
      int j = (int)(paramLong1 % allocationGranularity);
      long l2 = paramLong1 - j;
      long l3 = paramLong2 + j;
      try {
        l = map0(b, l2, l3);
      } catch (OutOfMemoryError outOfMemoryError) {
        System.gc();
        try {
          Thread.sleep(100L);
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        } 
        try {
          l = map0(b, l2, l3);
        } catch (OutOfMemoryError outOfMemoryError1) {
          throw new IOException("Map failed", outOfMemoryError1);
        } 
      } 
      try {
        fileDescriptor = this.nd.duplicateForMapping(this.fd);
      } catch (IOException iOException) {
        unmap0(l, l3);
        throw iOException;
      } 
      assert IOStatus.checkAll(l);
      assert l % allocationGranularity == 0L;
      int k = (int)paramLong2;
      Unmapper unmapper = new Unmapper(l, l3, k, fileDescriptor, null);
      if (!this.writable || b == 0)
        return Util.newMappedByteBufferR(k, l + j, fileDescriptor, unmapper); 
      return Util.newMappedByteBuffer(k, l + j, fileDescriptor, unmapper);
    } finally {
      this.threads.remove(i);
      end(IOStatus.checkAll(l));
    } 
  }
  
  public static JavaNioAccess.BufferPool getMappedBufferPool() {
    return new JavaNioAccess.BufferPool() {
        public String getName() {
          return "mapped";
        }
        
        public long getCount() {
          return FileChannelImpl.Unmapper.count;
        }
        
        public long getTotalCapacity() {
          return FileChannelImpl.Unmapper.totalCapacity;
        }
        
        public long getMemoryUsed() {
          return FileChannelImpl.Unmapper.totalSize;
        }
      };
  }
  
  private static class FileChannelImpl {}
  
  private static class FileChannelImpl {}
  
  private static boolean isSharedFileLockTable() {
    if (!propertyChecked)
      synchronized (FileChannelImpl.class) {
        if (!propertyChecked) {
          String str = AccessController.<String>doPrivileged(new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
          isSharedFileLockTable = (str == null || str.equals("false"));
          propertyChecked = true;
        } 
      }  
    return isSharedFileLockTable;
  }
  
  private FileLockTable fileLockTable() throws IOException {
    if (this.fileLockTable == null)
      synchronized (this) {
        if (this.fileLockTable == null)
          if (isSharedFileLockTable()) {
            int i = this.threads.add();
            try {
              ensureOpen();
              this.fileLockTable = FileLockTable.newSharedFileLockTable((Channel)this, this.fd);
            } finally {
              this.threads.remove(i);
            } 
          } else {
            this.fileLockTable = (FileLockTable)new SimpleFileLockTable();
          }  
      }  
    return this.fileLockTable;
  }
  
  public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
    ensureOpen();
    if (paramBoolean && !this.readable)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writable)
      throw new NonWritableChannelException(); 
    FileLockImpl fileLockImpl = new FileLockImpl((FileChannel)this, paramLong1, paramLong2, paramBoolean);
    FileLockTable fileLockTable = fileLockTable();
    fileLockTable.add(fileLockImpl);
    boolean bool = false;
    int i = -1;
    try {
      int j;
      begin();
      i = this.threads.add();
      if (!isOpen())
        return null; 
      do {
        j = this.nd.lock(this.fd, true, paramLong1, paramLong2, paramBoolean);
      } while (j == 2 && isOpen());
      if (isOpen()) {
        if (j == 1) {
          assert paramBoolean;
          FileLockImpl fileLockImpl1 = new FileLockImpl((FileChannel)this, paramLong1, paramLong2, false);
          fileLockTable.replace(fileLockImpl, fileLockImpl1);
          fileLockImpl = fileLockImpl1;
        } 
        bool = true;
      } 
    } finally {
      if (!bool)
        fileLockTable.remove(fileLockImpl); 
      this.threads.remove(i);
      try {
        end(bool);
      } catch (ClosedByInterruptException closedByInterruptException) {
        throw new FileLockInterruptionException();
      } 
    } 
    return fileLockImpl;
  }
  
  public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
    ensureOpen();
    if (paramBoolean && !this.readable)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writable)
      throw new NonWritableChannelException(); 
    FileLockImpl fileLockImpl = new FileLockImpl((FileChannel)this, paramLong1, paramLong2, paramBoolean);
    FileLockTable fileLockTable = fileLockTable();
    fileLockTable.add(fileLockImpl);
    int i = this.threads.add();
    try {
      int j;
      try {
        ensureOpen();
        j = this.nd.lock(this.fd, false, paramLong1, paramLong2, paramBoolean);
      } catch (IOException iOException) {
        fileLockTable.remove(fileLockImpl);
        throw iOException;
      } 
      if (j == -1) {
        fileLockTable.remove(fileLockImpl);
        return null;
      } 
      if (j == 1) {
        assert paramBoolean;
        FileLockImpl fileLockImpl1 = new FileLockImpl((FileChannel)this, paramLong1, paramLong2, false);
        fileLockTable.replace(fileLockImpl, fileLockImpl1);
        return fileLockImpl1;
      } 
      return fileLockImpl;
    } finally {
      this.threads.remove(i);
    } 
  }
  
  void release(FileLockImpl paramFileLockImpl) throws IOException {
    int i = this.threads.add();
    try {
      ensureOpen();
      this.nd.release(this.fd, paramFileLockImpl.position(), paramFileLockImpl.size());
    } finally {
      this.threads.remove(i);
    } 
    assert this.fileLockTable != null;
    this.fileLockTable.remove(paramFileLockImpl);
  }
  
  static {
    IOUtil.load();
    allocationGranularity = initIDs();
  }
  
  private native long map0(int paramInt, long paramLong1, long paramLong2) throws IOException;
  
  private static native int unmap0(long paramLong1, long paramLong2);
  
  private native long transferTo0(int paramInt1, long paramLong1, long paramLong2, int paramInt2);
  
  private native long position0(FileDescriptor paramFileDescriptor, long paramLong);
  
  private static native long initIDs();
}
