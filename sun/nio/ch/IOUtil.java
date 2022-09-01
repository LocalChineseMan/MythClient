package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class IOUtil {
  static int write(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher) throws IOException {
    if (paramByteBuffer instanceof DirectBuffer)
      return writeFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher); 
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    ByteBuffer byteBuffer = Util.getTemporaryDirectBuffer(bool);
    try {
      byteBuffer.put(paramByteBuffer);
      byteBuffer.flip();
      paramByteBuffer.position(i);
      int k = writeFromNativeBuffer(paramFileDescriptor, byteBuffer, paramLong, paramNativeDispatcher);
      if (k > 0)
        paramByteBuffer.position(i + k); 
      return k;
    } finally {
      Util.offerFirstTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  private static int writeFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher) throws IOException {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    int k = 0;
    if (!bool)
      return 0; 
    if (paramLong != -1L) {
      k = paramNativeDispatcher.pwrite(paramFileDescriptor, ((DirectBuffer)paramByteBuffer)
          .address() + i, bool, paramLong);
    } else {
      k = paramNativeDispatcher.write(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, bool);
    } 
    if (k > 0)
      paramByteBuffer.position(i + k); 
    return k;
  }
  
  static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher) throws IOException {
    return write(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
  }
  
  static long write(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher) throws IOException {
    IOVecWrapper iOVecWrapper = IOVecWrapper.get(paramInt2);
    boolean bool = false;
    byte b = 0;
    try {
      int i = paramInt1 + paramInt2;
      int j = paramInt1;
      while (j < i && b < IOV_MAX) {
        ByteBuffer byteBuffer = paramArrayOfByteBuffer[j];
        int k = byteBuffer.position();
        int m = byteBuffer.limit();
        assert k <= m;
        boolean bool1 = (k <= m) ? (m - k) : false;
        if (bool1) {
          iOVecWrapper.setBuffer(b, byteBuffer, k, bool1);
          if (!(byteBuffer instanceof DirectBuffer)) {
            ByteBuffer byteBuffer1 = Util.getTemporaryDirectBuffer(bool1);
            byteBuffer1.put(byteBuffer);
            byteBuffer1.flip();
            iOVecWrapper.setShadow(b, byteBuffer1);
            byteBuffer.position(k);
            byteBuffer = byteBuffer1;
            k = byteBuffer1.position();
          } 
          iOVecWrapper.putBase(b, ((DirectBuffer)byteBuffer).address() + k);
          iOVecWrapper.putLen(b, bool1);
          b++;
        } 
        j++;
      } 
      if (b == 0)
        return 0L; 
      long l1 = paramNativeDispatcher.writev(paramFileDescriptor, iOVecWrapper.address, b);
      long l2 = l1;
      for (byte b1 = 0; b1 < b; b1++) {
        if (l2 > 0L) {
          ByteBuffer byteBuffer1 = iOVecWrapper.getBuffer(b1);
          int k = iOVecWrapper.getPosition(b1);
          int m = iOVecWrapper.getRemaining(b1);
          int n = (l2 > m) ? m : (int)l2;
          byteBuffer1.position(k + n);
          l2 -= n;
        } 
        ByteBuffer byteBuffer = iOVecWrapper.getShadow(b1);
        if (byteBuffer != null)
          Util.offerLastTemporaryDirectBuffer(byteBuffer); 
        iOVecWrapper.clearRefs(b1);
      } 
      bool = true;
      return l1;
    } finally {
      if (!bool)
        for (byte b1 = 0; b1 < b; b1++) {
          ByteBuffer byteBuffer = iOVecWrapper.getShadow(b1);
          if (byteBuffer != null)
            Util.offerLastTemporaryDirectBuffer(byteBuffer); 
          iOVecWrapper.clearRefs(b1);
        }  
    } 
  }
  
  static int read(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher) throws IOException {
    if (paramByteBuffer.isReadOnly())
      throw new IllegalArgumentException("Read-only buffer"); 
    if (paramByteBuffer instanceof DirectBuffer)
      return readIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, paramLong, paramNativeDispatcher); 
    ByteBuffer byteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
    try {
      int i = readIntoNativeBuffer(paramFileDescriptor, byteBuffer, paramLong, paramNativeDispatcher);
      byteBuffer.flip();
      if (i > 0)
        paramByteBuffer.put(byteBuffer); 
      return i;
    } finally {
      Util.offerFirstTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  private static int readIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, long paramLong, NativeDispatcher paramNativeDispatcher) throws IOException {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert i <= j;
    boolean bool = (i <= j) ? (j - i) : false;
    if (!bool)
      return 0; 
    int k = 0;
    if (paramLong != -1L) {
      k = paramNativeDispatcher.pread(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, bool, paramLong);
    } else {
      k = paramNativeDispatcher.read(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, bool);
    } 
    if (k > 0)
      paramByteBuffer.position(i + k); 
    return k;
  }
  
  static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, NativeDispatcher paramNativeDispatcher) throws IOException {
    return read(paramFileDescriptor, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramNativeDispatcher);
  }
  
  static long read(FileDescriptor paramFileDescriptor, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, NativeDispatcher paramNativeDispatcher) throws IOException {
    IOVecWrapper iOVecWrapper = IOVecWrapper.get(paramInt2);
    boolean bool = false;
    byte b = 0;
    try {
      int i = paramInt1 + paramInt2;
      int j = paramInt1;
      while (j < i && b < IOV_MAX) {
        ByteBuffer byteBuffer = paramArrayOfByteBuffer[j];
        if (byteBuffer.isReadOnly())
          throw new IllegalArgumentException("Read-only buffer"); 
        int k = byteBuffer.position();
        int m = byteBuffer.limit();
        assert k <= m;
        boolean bool1 = (k <= m) ? (m - k) : false;
        if (bool1) {
          iOVecWrapper.setBuffer(b, byteBuffer, k, bool1);
          if (!(byteBuffer instanceof DirectBuffer)) {
            ByteBuffer byteBuffer1 = Util.getTemporaryDirectBuffer(bool1);
            iOVecWrapper.setShadow(b, byteBuffer1);
            byteBuffer = byteBuffer1;
            k = byteBuffer1.position();
          } 
          iOVecWrapper.putBase(b, ((DirectBuffer)byteBuffer).address() + k);
          iOVecWrapper.putLen(b, bool1);
          b++;
        } 
        j++;
      } 
      if (b == 0)
        return 0L; 
      long l1 = paramNativeDispatcher.readv(paramFileDescriptor, iOVecWrapper.address, b);
      long l2 = l1;
      for (byte b1 = 0; b1 < b; b1++) {
        ByteBuffer byteBuffer = iOVecWrapper.getShadow(b1);
        if (l2 > 0L) {
          ByteBuffer byteBuffer1 = iOVecWrapper.getBuffer(b1);
          int k = iOVecWrapper.getRemaining(b1);
          int m = (l2 > k) ? k : (int)l2;
          if (byteBuffer == null) {
            int n = iOVecWrapper.getPosition(b1);
            byteBuffer1.position(n + m);
          } else {
            byteBuffer.limit(byteBuffer.position() + m);
            byteBuffer1.put(byteBuffer);
          } 
          l2 -= m;
        } 
        if (byteBuffer != null)
          Util.offerLastTemporaryDirectBuffer(byteBuffer); 
        iOVecWrapper.clearRefs(b1);
      } 
      bool = true;
      return l1;
    } finally {
      if (!bool)
        for (byte b1 = 0; b1 < b; b1++) {
          ByteBuffer byteBuffer = iOVecWrapper.getShadow(b1);
          if (byteBuffer != null)
            Util.offerLastTemporaryDirectBuffer(byteBuffer); 
          iOVecWrapper.clearRefs(b1);
        }  
    } 
  }
  
  public static FileDescriptor newFD(int paramInt) {
    FileDescriptor fileDescriptor = new FileDescriptor();
    setfdVal(fileDescriptor, paramInt);
    return fileDescriptor;
  }
  
  static native boolean randomBytes(byte[] paramArrayOfbyte);
  
  static native long makePipe(boolean paramBoolean);
  
  static native boolean drain(int paramInt) throws IOException;
  
  public static native void configureBlocking(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException;
  
  public static native int fdVal(FileDescriptor paramFileDescriptor);
  
  static native void setfdVal(FileDescriptor paramFileDescriptor, int paramInt);
  
  static native int fdLimit();
  
  static native int iovMax();
  
  static native void initIDs();
  
  public static void load() {}
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            System.loadLibrary("nio");
            return null;
          }
        });
    initIDs();
  }
  
  static final int IOV_MAX = iovMax();
}
