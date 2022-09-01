package sun.nio.fs;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.FileChannelImpl;
import sun.nio.ch.ThreadPool;
import sun.nio.ch.WindowsAsynchronousFileChannelImpl;

class WindowsChannelFactory {
  private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  static final OpenOption OPEN_REPARSE_POINT = new OpenOption() {
    
    };
  
  private static class Flags {
    boolean read;
    
    boolean write;
    
    boolean append;
    
    boolean truncateExisting;
    
    boolean create;
    
    boolean createNew;
    
    boolean deleteOnClose;
    
    boolean sparse;
    
    boolean overlapped;
    
    boolean sync;
    
    boolean dsync;
    
    boolean shareRead = true;
    
    boolean shareWrite = true;
    
    boolean shareDelete = true;
    
    boolean noFollowLinks;
    
    boolean openReparsePoint;
    
    static Flags toFlags(Set<? extends OpenOption> param1Set) {
      Flags flags = new Flags();
      for (OpenOption openOption : param1Set) {
        if (openOption instanceof StandardOpenOption) {
          switch ((StandardOpenOption)openOption) {
            case NOSHARE_READ:
              flags.read = true;
              continue;
            case NOSHARE_WRITE:
              flags.write = true;
              continue;
            case NOSHARE_DELETE:
              flags.append = true;
              continue;
            case null:
              flags.truncateExisting = true;
              continue;
            case null:
              flags.create = true;
              continue;
            case null:
              flags.createNew = true;
              continue;
            case null:
              flags.deleteOnClose = true;
              continue;
            case null:
              flags.sparse = true;
              continue;
            case null:
              flags.sync = true;
              continue;
            case null:
              flags.dsync = true;
              continue;
          } 
          throw new UnsupportedOperationException();
        } 
        if (openOption instanceof ExtendedOpenOption) {
          switch ((ExtendedOpenOption)openOption) {
            case NOSHARE_READ:
              flags.shareRead = false;
              continue;
            case NOSHARE_WRITE:
              flags.shareWrite = false;
              continue;
            case NOSHARE_DELETE:
              flags.shareDelete = false;
              continue;
          } 
          throw new UnsupportedOperationException();
        } 
        if (openOption == LinkOption.NOFOLLOW_LINKS) {
          flags.noFollowLinks = true;
          continue;
        } 
        if (openOption == WindowsChannelFactory.OPEN_REPARSE_POINT) {
          flags.openReparsePoint = true;
          continue;
        } 
        if (openOption == null)
          throw new NullPointerException(); 
        throw new UnsupportedOperationException();
      } 
      return flags;
    }
  }
  
  static FileChannel newFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong) throws WindowsException {
    Flags flags = Flags.toFlags(paramSet);
    if (!flags.read && !flags.write)
      if (flags.append) {
        flags.write = true;
      } else {
        flags.read = true;
      }  
    if (flags.read && flags.append)
      throw new IllegalArgumentException("READ + APPEND not allowed"); 
    if (flags.append && flags.truncateExisting)
      throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed"); 
    FileDescriptor fileDescriptor = open(paramString1, paramString2, flags, paramLong);
    return FileChannelImpl.open(fileDescriptor, paramString1, flags.read, flags.write, flags.append, null);
  }
  
  static AsynchronousFileChannel newAsynchronousFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong, ThreadPool paramThreadPool) throws IOException {
    FileDescriptor fileDescriptor;
    Flags flags = Flags.toFlags(paramSet);
    flags.overlapped = true;
    if (!flags.read && !flags.write)
      flags.read = true; 
    if (flags.append)
      throw new UnsupportedOperationException("APPEND not allowed"); 
    try {
      fileDescriptor = open(paramString1, paramString2, flags, paramLong);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramString1);
      return null;
    } 
    try {
      return WindowsAsynchronousFileChannelImpl.open(fileDescriptor, flags.read, flags.write, paramThreadPool);
    } catch (IOException iOException) {
      long l = fdAccess.getHandle(fileDescriptor);
      WindowsNativeDispatcher.CloseHandle(l);
      throw iOException;
    } 
  }
  
  private static FileDescriptor open(String paramString1, String paramString2, Flags paramFlags, long paramLong) throws WindowsException {
    boolean bool1 = false;
    int i = 0;
    if (paramFlags.read)
      i |= Integer.MIN_VALUE; 
    if (paramFlags.write)
      i |= 0x40000000; 
    int j = 0;
    if (paramFlags.shareRead)
      j |= 0x1; 
    if (paramFlags.shareWrite)
      j |= 0x2; 
    if (paramFlags.shareDelete)
      j |= 0x4; 
    int k = 128;
    byte b = 3;
    if (paramFlags.write)
      if (paramFlags.createNew) {
        b = 1;
        k |= 0x200000;
      } else {
        if (paramFlags.create)
          b = 4; 
        if (paramFlags.truncateExisting)
          if (b == 4) {
            bool1 = true;
          } else {
            b = 5;
          }  
      }  
    if (paramFlags.dsync || paramFlags.sync)
      k |= Integer.MIN_VALUE; 
    if (paramFlags.overlapped)
      k |= 0x40000000; 
    if (paramFlags.deleteOnClose)
      k |= 0x4000000; 
    boolean bool2 = true;
    if (b != 1 && (paramFlags.noFollowLinks || paramFlags.openReparsePoint || paramFlags.deleteOnClose)) {
      if (paramFlags.noFollowLinks || paramFlags.deleteOnClose)
        bool2 = false; 
      k |= 0x200000;
    } 
    if (paramString2 != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        if (paramFlags.read)
          securityManager.checkRead(paramString2); 
        if (paramFlags.write)
          securityManager.checkWrite(paramString2); 
        if (paramFlags.deleteOnClose)
          securityManager.checkDelete(paramString2); 
      } 
    } 
    long l = WindowsNativeDispatcher.CreateFile(paramString1, i, j, paramLong, b, k);
    if (!bool2)
      try {
        if (WindowsFileAttributes.readAttributes(l).isSymbolicLink())
          throw new WindowsException("File is symbolic link"); 
      } catch (WindowsException windowsException) {
        WindowsNativeDispatcher.CloseHandle(l);
        throw windowsException;
      }  
    if (bool1)
      try {
        WindowsNativeDispatcher.SetEndOfFile(l);
      } catch (WindowsException windowsException) {
        WindowsNativeDispatcher.CloseHandle(l);
        throw windowsException;
      }  
    if (b == 1 && paramFlags.sparse)
      try {
        WindowsNativeDispatcher.DeviceIoControlSetSparse(l);
      } catch (WindowsException windowsException) {} 
    FileDescriptor fileDescriptor = new FileDescriptor();
    fdAccess.setHandle(fileDescriptor, l);
    return fileDescriptor;
  }
}
