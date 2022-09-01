package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.SecureRandom;
import sun.security.action.GetPropertyAction;

class TempFileHelper {
  private static final Path tmpdir = Paths.get(AccessController.<String>doPrivileged(new GetPropertyAction("java.io.tmpdir")), new String[0]);
  
  private static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
  
  private static final SecureRandom random = new SecureRandom();
  
  private static Path generatePath(String paramString1, String paramString2, Path paramPath) {
    long l = random.nextLong();
    l = (l == Long.MIN_VALUE) ? 0L : Math.abs(l);
    Path path = paramPath.getFileSystem().getPath(paramString1 + Long.toString(l) + paramString2, new String[0]);
    if (path.getParent() != null)
      throw new IllegalArgumentException("Invalid prefix or suffix"); 
    return paramPath.resolve(path);
  }
  
  private static Path create(Path paramPath, String paramString1, String paramString2, boolean paramBoolean, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException {
    FileAttribute[] arrayOfFileAttribute;
    if (paramString1 == null)
      paramString1 = ""; 
    if (paramString2 == null)
      paramString2 = paramBoolean ? "" : ".tmp"; 
    if (paramPath == null)
      paramPath = tmpdir; 
    if (isPosix && paramPath.getFileSystem() == FileSystems.getDefault())
      if (paramArrayOfFileAttribute.length == 0) {
        arrayOfFileAttribute = new FileAttribute[1];
        arrayOfFileAttribute[0] = paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions;
      } else {
        boolean bool = false;
        for (byte b = 0; b < arrayOfFileAttribute.length; b++) {
          if (arrayOfFileAttribute[b].name().equals("posix:permissions")) {
            bool = true;
            break;
          } 
        } 
        if (!bool) {
          FileAttribute[] arrayOfFileAttribute1 = new FileAttribute[arrayOfFileAttribute.length + 1];
          System.arraycopy(arrayOfFileAttribute, 0, arrayOfFileAttribute1, 0, arrayOfFileAttribute.length);
          arrayOfFileAttribute = arrayOfFileAttribute1;
          arrayOfFileAttribute[arrayOfFileAttribute.length - 1] = paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions;
        } 
      }  
    SecurityManager securityManager = System.getSecurityManager();
    while (true) {
      Path path;
      try {
        path = generatePath(paramString1, paramString2, paramPath);
      } catch (InvalidPathException invalidPathException) {
        if (securityManager != null)
          throw new IllegalArgumentException("Invalid prefix or suffix"); 
        throw invalidPathException;
      } 
      try {
        if (paramBoolean)
          return Files.createDirectory(path, (FileAttribute<?>[])arrayOfFileAttribute); 
        return Files.createFile(path, (FileAttribute<?>[])arrayOfFileAttribute);
      } catch (SecurityException securityException) {
        if (paramPath == tmpdir && securityManager != null)
          throw new SecurityException("Unable to create temporary file or directory"); 
        throw securityException;
      } catch (FileAlreadyExistsException fileAlreadyExistsException) {}
    } 
  }
  
  static Path createTempFile(Path paramPath, String paramString1, String paramString2, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException {
    return create(paramPath, paramString1, paramString2, false, paramArrayOfFileAttribute);
  }
  
  static Path createTempDirectory(Path paramPath, String paramString, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException {
    return create(paramPath, paramString, null, true, paramArrayOfFileAttribute);
  }
  
  private static class TempFileHelper {}
}
