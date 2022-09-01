package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

class FileDispatcherImpl extends FileDispatcher {
  private final boolean append;
  
  static {
    IOUtil.load();
  }
  
  FileDispatcherImpl(boolean paramBoolean) {
    this.append = paramBoolean;
  }
  
  FileDispatcherImpl() {
    this(false);
  }
  
  boolean needsPositionLock() {
    return true;
  }
  
  int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
    return read0(paramFileDescriptor, paramLong, paramInt);
  }
  
  int pread(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException {
    return pread0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
  }
  
  long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
    return readv0(paramFileDescriptor, paramLong, paramInt);
  }
  
  int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
    return write0(paramFileDescriptor, paramLong, paramInt, this.append);
  }
  
  int pwrite(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException {
    return pwrite0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
  }
  
  long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
    return writev0(paramFileDescriptor, paramLong, paramInt, this.append);
  }
  
  int force(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException {
    return force0(paramFileDescriptor, paramBoolean);
  }
  
  int truncate(FileDescriptor paramFileDescriptor, long paramLong) throws IOException {
    return truncate0(paramFileDescriptor, paramLong);
  }
  
  long size(FileDescriptor paramFileDescriptor) throws IOException {
    return size0(paramFileDescriptor);
  }
  
  int lock(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2) throws IOException {
    return lock0(paramFileDescriptor, paramBoolean1, paramLong1, paramLong2, paramBoolean2);
  }
  
  void release(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2) throws IOException {
    release0(paramFileDescriptor, paramLong1, paramLong2);
  }
  
  void close(FileDescriptor paramFileDescriptor) throws IOException {
    close0(paramFileDescriptor);
  }
  
  FileDescriptor duplicateForMapping(FileDescriptor paramFileDescriptor) throws IOException {
    JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    FileDescriptor fileDescriptor = new FileDescriptor();
    long l = duplicateHandle(javaIOFileDescriptorAccess.getHandle(paramFileDescriptor));
    javaIOFileDescriptorAccess.setHandle(fileDescriptor, l);
    return fileDescriptor;
  }
  
  static native int read0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  static native int pread0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException;
  
  static native long readv0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  static native int write0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean) throws IOException;
  
  static native int pwrite0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException;
  
  static native long writev0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean) throws IOException;
  
  static native int force0(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException;
  
  static native int truncate0(FileDescriptor paramFileDescriptor, long paramLong) throws IOException;
  
  static native long size0(FileDescriptor paramFileDescriptor) throws IOException;
  
  static native int lock0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2) throws IOException;
  
  static native void release0(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2) throws IOException;
  
  static native void close0(FileDescriptor paramFileDescriptor) throws IOException;
  
  static native void closeByHandle(long paramLong) throws IOException;
  
  static native long duplicateHandle(long paramLong) throws IOException;
}
