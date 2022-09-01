package sun.nio.fs;

import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import sun.misc.Unsafe;
import sun.nio.ch.ThreadPool;

public class WindowsFileSystemProvider extends AbstractFileSystemProvider {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final String USER_DIR = "user.dir";
  
  private final WindowsFileSystem theFileSystem = new WindowsFileSystem(this, System.getProperty("user.dir"));
  
  public String getScheme() {
    return "file";
  }
  
  private void checkUri(URI paramURI) {
    if (!paramURI.getScheme().equalsIgnoreCase(getScheme()))
      throw new IllegalArgumentException("URI does not match this provider"); 
    if (paramURI.getAuthority() != null)
      throw new IllegalArgumentException("Authority component present"); 
    if (paramURI.getPath() == null)
      throw new IllegalArgumentException("Path component is undefined"); 
    if (!paramURI.getPath().equals("/"))
      throw new IllegalArgumentException("Path component should be '/'"); 
    if (paramURI.getQuery() != null)
      throw new IllegalArgumentException("Query component present"); 
    if (paramURI.getFragment() != null)
      throw new IllegalArgumentException("Fragment component present"); 
  }
  
  public FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap) throws IOException {
    checkUri(paramURI);
    throw new FileSystemAlreadyExistsException();
  }
  
  public final FileSystem getFileSystem(URI paramURI) {
    checkUri(paramURI);
    return this.theFileSystem;
  }
  
  public Path getPath(URI paramURI) {
    return WindowsUriSupport.fromUri(this.theFileSystem, paramURI);
  }
  
  public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs) throws IOException {
    if (paramPath == null)
      throw new NullPointerException(); 
    if (!(paramPath instanceof WindowsPath))
      throw new ProviderMismatchException(); 
    WindowsPath windowsPath = (WindowsPath)paramPath;
    WindowsSecurityDescriptor windowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try {
      return WindowsChannelFactory.newFileChannel(windowsPath.getPathForWin32Calls(), windowsPath
          .getPathForPermissionCheck(), paramSet, windowsSecurityDescriptor
          
          .address());
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath);
      return null;
    } finally {
      if (windowsSecurityDescriptor != null)
        windowsSecurityDescriptor.release(); 
    } 
  }
  
  public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>... paramVarArgs) throws IOException {
    if (paramPath == null)
      throw new NullPointerException(); 
    if (!(paramPath instanceof WindowsPath))
      throw new ProviderMismatchException(); 
    WindowsPath windowsPath = (WindowsPath)paramPath;
    ThreadPool threadPool = (paramExecutorService == null) ? null : ThreadPool.wrap(paramExecutorService, 0);
    WindowsSecurityDescriptor windowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try {
      return WindowsChannelFactory.newAsynchronousFileChannel(windowsPath.getPathForWin32Calls(), windowsPath
          .getPathForPermissionCheck(), paramSet, windowsSecurityDescriptor
          
          .address(), threadPool);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath);
      return null;
    } finally {
      if (windowsSecurityDescriptor != null)
        windowsSecurityDescriptor.release(); 
    } 
  }
  
  public <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption... paramVarArgs) {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    if (paramClass == null)
      throw new NullPointerException(); 
    boolean bool = Util.followLinks(paramVarArgs);
    if (paramClass == BasicFileAttributeView.class)
      return (V)WindowsFileAttributeViews.createBasicView(windowsPath, bool); 
    if (paramClass == DosFileAttributeView.class)
      return (V)WindowsFileAttributeViews.createDosView(windowsPath, bool); 
    if (paramClass == AclFileAttributeView.class)
      return (V)new WindowsAclFileAttributeView(windowsPath, bool); 
    if (paramClass == FileOwnerAttributeView.class)
      return (V)new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(windowsPath, bool)); 
    if (paramClass == UserDefinedFileAttributeView.class)
      return (V)new WindowsUserDefinedFileAttributeView(windowsPath, bool); 
    return (V)null;
  }
  
  public <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption... paramVarArgs) throws IOException {
    Class<DosFileAttributeView> clazz;
    if (paramClass == BasicFileAttributes.class) {
      Class<BasicFileAttributeView> clazz1 = BasicFileAttributeView.class;
    } else if (paramClass == DosFileAttributes.class) {
      clazz = DosFileAttributeView.class;
    } else {
      if (paramClass == null)
        throw new NullPointerException(); 
      throw new UnsupportedOperationException();
    } 
    return (A)((BasicFileAttributeView)getFileAttributeView(paramPath, (Class)clazz, paramVarArgs)).readAttributes();
  }
  
  public DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption... paramVarArgs) {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    boolean bool = Util.followLinks(paramVarArgs);
    if (paramString.equals("basic"))
      return WindowsFileAttributeViews.createBasicView(windowsPath, bool); 
    if (paramString.equals("dos"))
      return WindowsFileAttributeViews.createDosView(windowsPath, bool); 
    if (paramString.equals("acl"))
      return new WindowsAclFileAttributeView(windowsPath, bool); 
    if (paramString.equals("owner"))
      return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(windowsPath, bool)); 
    if (paramString.equals("user"))
      return new WindowsUserDefinedFileAttributeView(windowsPath, bool); 
    return null;
  }
  
  public SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    WindowsSecurityDescriptor windowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try {
      return WindowsChannelFactory.newFileChannel(windowsPath.getPathForWin32Calls(), windowsPath
          .getPathForPermissionCheck(), paramSet, windowsSecurityDescriptor
          
          .address());
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath);
      return null;
    } finally {
      windowsSecurityDescriptor.release();
    } 
  }
  
  boolean implDelete(Path paramPath, boolean paramBoolean) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    windowsPath.checkDelete();
    WindowsFileAttributes windowsFileAttributes = null;
    try {
      windowsFileAttributes = WindowsFileAttributes.get(windowsPath, false);
      if (windowsFileAttributes.isDirectory() || windowsFileAttributes.isDirectoryLink()) {
        WindowsNativeDispatcher.RemoveDirectory(windowsPath.getPathForWin32Calls());
      } else {
        WindowsNativeDispatcher.DeleteFile(windowsPath.getPathForWin32Calls());
      } 
      return true;
    } catch (WindowsException windowsException) {
      if (!paramBoolean && (windowsException
        .lastError() == 2 || windowsException
        .lastError() == 3))
        return false; 
      if (windowsFileAttributes != null && windowsFileAttributes.isDirectory())
        if (windowsException.lastError() == 145 || windowsException
          .lastError() == 183)
          throw new DirectoryNotEmptyException(windowsPath
              .getPathForExceptionMessage());  
      windowsException.rethrowAsIOException(windowsPath);
      return false;
    } 
  }
  
  public void copy(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    WindowsFileCopy.copy(WindowsPath.toWindowsPath(paramPath1), 
        WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
  }
  
  public void move(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    WindowsFileCopy.move(WindowsPath.toWindowsPath(paramPath1), 
        WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
  }
  
  private static boolean hasDesiredAccess(WindowsPath paramWindowsPath, int paramInt) throws IOException {
    boolean bool = false;
    String str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
    NativeBuffer nativeBuffer = WindowsAclFileAttributeView.getFileSecurity(str, 7);
    try {
      bool = WindowsSecurity.checkAccessMask(nativeBuffer.address(), paramInt, 1179785, 1179926, 1179808, 2032127);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
    } finally {
      nativeBuffer.release();
    } 
    return bool;
  }
  
  private void checkReadAccess(WindowsPath paramWindowsPath) throws IOException {
    try {
      Set<?> set = Collections.emptySet();
      FileChannel fileChannel = WindowsChannelFactory.newFileChannel(paramWindowsPath.getPathForWin32Calls(), paramWindowsPath
          .getPathForPermissionCheck(), (Set)set, 0L);
      fileChannel.close();
    } catch (WindowsException windowsException) {
      try {
        (new WindowsDirectoryStream(paramWindowsPath, null)).close();
      } catch (IOException iOException) {
        windowsException.rethrowAsIOException(paramWindowsPath);
      } 
    } 
  }
  
  public void checkAccess(Path paramPath, AccessMode... paramVarArgs) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    for (AccessMode accessMode : paramVarArgs) {
      switch (null.$SwitchMap$java$nio$file$AccessMode[accessMode.ordinal()]) {
        case 1:
          bool1 = true;
          break;
        case 2:
          bool2 = true;
          break;
        case 3:
          bool3 = true;
          break;
        default:
          throw new AssertionError("Should not get here");
      } 
    } 
    if (!bool2 && !bool3) {
      checkReadAccess(windowsPath);
      return;
    } 
    int i = 0;
    if (bool1) {
      windowsPath.checkRead();
      i |= 0x1;
    } 
    if (bool2) {
      windowsPath.checkWrite();
      i |= 0x2;
    } 
    if (bool3) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkExec(windowsPath.getPathForPermissionCheck()); 
      i |= 0x20;
    } 
    if (!hasDesiredAccess(windowsPath, i))
      throw new AccessDeniedException(windowsPath
          .getPathForExceptionMessage(), null, "Permissions does not allow requested access"); 
    if (bool2) {
      try {
        WindowsFileAttributes windowsFileAttributes = WindowsFileAttributes.get(windowsPath, true);
        if (!windowsFileAttributes.isDirectory() && windowsFileAttributes.isReadOnly())
          throw new AccessDeniedException(windowsPath
              .getPathForExceptionMessage(), null, "DOS readonly attribute is set"); 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(windowsPath);
      } 
      if (WindowsFileStore.create(windowsPath).isReadOnly())
        throw new AccessDeniedException(windowsPath
            .getPathForExceptionMessage(), null, "Read-only file system"); 
    } 
  }
  
  public boolean isSameFile(Path paramPath1, Path paramPath2) throws IOException {
    WindowsPath windowsPath1 = WindowsPath.toWindowsPath(paramPath1);
    if (windowsPath1.equals(paramPath2))
      return true; 
    if (paramPath2 == null)
      throw new NullPointerException(); 
    if (!(paramPath2 instanceof WindowsPath))
      return false; 
    WindowsPath windowsPath2 = (WindowsPath)paramPath2;
    windowsPath1.checkRead();
    windowsPath2.checkRead();
    long l = 0L;
    try {
      l = windowsPath1.openForReadAttributeAccess(true);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath1);
    } 
    try {
      WindowsFileAttributes windowsFileAttributes = null;
      try {
        windowsFileAttributes = WindowsFileAttributes.readAttributes(l);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(windowsPath1);
      } 
      long l1 = 0L;
      try {
        l1 = windowsPath2.openForReadAttributeAccess(true);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(windowsPath2);
      } 
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
  }
  
  public boolean isHidden(Path paramPath) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    windowsPath.checkRead();
    WindowsFileAttributes windowsFileAttributes = null;
    try {
      windowsFileAttributes = WindowsFileAttributes.get(windowsPath, true);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath);
    } 
    if (windowsFileAttributes.isDirectory())
      return false; 
    return windowsFileAttributes.isHidden();
  }
  
  public FileStore getFileStore(Path paramPath) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      windowsPath.checkRead();
    } 
    return WindowsFileStore.create(windowsPath);
  }
  
  public void createDirectory(Path paramPath, FileAttribute<?>... paramVarArgs) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    windowsPath.checkWrite();
    WindowsSecurityDescriptor windowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
    try {
      WindowsNativeDispatcher.CreateDirectory(windowsPath.getPathForWin32Calls(), windowsSecurityDescriptor.address());
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() == 5)
        try {
          if (WindowsFileAttributes.get(windowsPath, false).isDirectory())
            throw new FileAlreadyExistsException(windowsPath.toString()); 
        } catch (WindowsException windowsException1) {} 
      windowsException.rethrowAsIOException(windowsPath);
    } finally {
      windowsSecurityDescriptor.release();
    } 
  }
  
  public DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    windowsPath.checkRead();
    if (paramFilter == null)
      throw new NullPointerException(); 
    return new WindowsDirectoryStream(windowsPath, paramFilter);
  }
  
  public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>... paramVarArgs) throws IOException {
    WindowsPath windowsPath3, windowsPath1 = WindowsPath.toWindowsPath(paramPath1);
    WindowsPath windowsPath2 = WindowsPath.toWindowsPath(paramPath2);
    if (!windowsPath1.getFileSystem().supportsLinks())
      throw new UnsupportedOperationException("Symbolic links not supported on this operating system"); 
    if (paramVarArgs.length > 0) {
      WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
      throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new LinkPermission("symbolic"));
      windowsPath1.checkWrite();
    } 
    if (windowsPath2.type() == WindowsPathType.DRIVE_RELATIVE)
      throw new IOException("Cannot create symbolic link to working directory relative target"); 
    if (windowsPath2.type() == WindowsPathType.RELATIVE) {
      WindowsPath windowsPath = windowsPath1.getParent();
      windowsPath3 = (windowsPath == null) ? windowsPath2 : windowsPath.resolve(windowsPath2);
    } else {
      windowsPath3 = windowsPath1.resolve(windowsPath2);
    } 
    int i = 0;
    try {
      WindowsFileAttributes windowsFileAttributes = WindowsFileAttributes.get(windowsPath3, false);
      if (windowsFileAttributes.isDirectory() || windowsFileAttributes.isDirectoryLink())
        i |= 0x1; 
    } catch (WindowsException windowsException) {}
    try {
      WindowsNativeDispatcher.CreateSymbolicLink(windowsPath1.getPathForWin32Calls(), 
          WindowsPath.addPrefixIfNeeded(windowsPath2.toString()), i);
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() == 4392) {
        windowsException.rethrowAsIOException(windowsPath1, windowsPath2);
      } else {
        windowsException.rethrowAsIOException(windowsPath1);
      } 
    } 
  }
  
  public void createLink(Path paramPath1, Path paramPath2) throws IOException {
    WindowsPath windowsPath1 = WindowsPath.toWindowsPath(paramPath1);
    WindowsPath windowsPath2 = WindowsPath.toWindowsPath(paramPath2);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new LinkPermission("hard"));
      windowsPath1.checkWrite();
      windowsPath2.checkWrite();
    } 
    try {
      WindowsNativeDispatcher.CreateHardLink(windowsPath1.getPathForWin32Calls(), windowsPath2
          .getPathForWin32Calls());
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(windowsPath1, windowsPath2);
    } 
  }
  
  public Path readSymbolicLink(Path paramPath) throws IOException {
    WindowsPath windowsPath = WindowsPath.toWindowsPath(paramPath);
    WindowsFileSystem windowsFileSystem = windowsPath.getFileSystem();
    if (!windowsFileSystem.supportsLinks())
      throw new UnsupportedOperationException("symbolic links not supported"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      FilePermission filePermission = new FilePermission(windowsPath.getPathForPermissionCheck(), "readlink");
      securityManager.checkPermission(filePermission);
    } 
    String str = WindowsLinkSupport.readLink(windowsPath);
    return WindowsPath.createFromNormalizedPath(windowsFileSystem, str);
  }
}
