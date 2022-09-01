package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

class WindowsFileSystem extends FileSystem {
  private final WindowsFileSystemProvider provider;
  
  private final String defaultDirectory;
  
  private final String defaultRoot;
  
  private final boolean supportsLinks;
  
  private final boolean supportsStreamEnumeration;
  
  WindowsFileSystem(WindowsFileSystemProvider paramWindowsFileSystemProvider, String paramString) {
    this.provider = paramWindowsFileSystemProvider;
    WindowsPathParser.Result result = WindowsPathParser.parse(paramString);
    if (result.type() != WindowsPathType.ABSOLUTE && result
      .type() != WindowsPathType.UNC)
      throw new AssertionError("Default directory is not an absolute path"); 
    this.defaultDirectory = result.path();
    this.defaultRoot = result.root();
    GetPropertyAction getPropertyAction = new GetPropertyAction("os.version");
    String str = AccessController.<String>doPrivileged(getPropertyAction);
    String[] arrayOfString = Util.split(str, '.');
    int i = Integer.parseInt(arrayOfString[0]);
    int j = Integer.parseInt(arrayOfString[1]);
    this.supportsLinks = (i >= 6);
    this.supportsStreamEnumeration = (i >= 6 || (i == 5 && j >= 2));
  }
  
  String defaultDirectory() {
    return this.defaultDirectory;
  }
  
  String defaultRoot() {
    return this.defaultRoot;
  }
  
  boolean supportsLinks() {
    return this.supportsLinks;
  }
  
  boolean supportsStreamEnumeration() {
    return this.supportsStreamEnumeration;
  }
  
  public FileSystemProvider provider() {
    return this.provider;
  }
  
  public String getSeparator() {
    return "\\";
  }
  
  public boolean isOpen() {
    return true;
  }
  
  public boolean isReadOnly() {
    return false;
  }
  
  public void close() throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public Iterable<Path> getRootDirectories() {
    int i = 0;
    try {
      i = WindowsNativeDispatcher.GetLogicalDrives();
    } catch (WindowsException windowsException) {
      throw new AssertionError(windowsException.getMessage());
    } 
    ArrayList<WindowsPath> arrayList = new ArrayList();
    SecurityManager securityManager = System.getSecurityManager();
    for (byte b = 0; b <= 25; b++) {
      if ((i & 1 << b) != 0) {
        StringBuilder stringBuilder = new StringBuilder(3);
        stringBuilder.append((char)(65 + b));
        stringBuilder.append(":\\");
        String str = stringBuilder.toString();
        if (securityManager != null)
          try {
            securityManager.checkRead(str);
          } catch (SecurityException securityException) {} 
        arrayList.add(WindowsPath.createFromNormalizedPath(this, str));
      } 
    } 
    return Collections.unmodifiableList((List)arrayList);
  }
  
  public Iterable<FileStore> getFileStores() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      } catch (SecurityException securityException) {
        return Collections.emptyList();
      }  
    return (Iterable<FileStore>)new Object(this);
  }
  
  private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "basic", "dos", "acl", "owner", "user" })));
  
  private static final String GLOB_SYNTAX = "glob";
  
  private static final String REGEX_SYNTAX = "regex";
  
  public Set<String> supportedFileAttributeViews() {
    return supportedFileAttributeViews;
  }
  
  public final Path getPath(String paramString, String... paramVarArgs) {
    String str;
    if (paramVarArgs.length == 0) {
      str = paramString;
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      for (String str1 : paramVarArgs) {
        if (str1.length() > 0) {
          if (stringBuilder.length() > 0)
            stringBuilder.append('\\'); 
          stringBuilder.append(str1);
        } 
      } 
      str = stringBuilder.toString();
    } 
    return WindowsPath.parse(this, str);
  }
  
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    return LookupService.instance;
  }
  
  public PathMatcher getPathMatcher(String paramString) {
    String str3;
    int i = paramString.indexOf(':');
    if (i <= 0 || i == paramString.length())
      throw new IllegalArgumentException(); 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    if (str1.equals("glob")) {
      str3 = Globs.toWindowsRegexPattern(str2);
    } else if (str1.equals("regex")) {
      str3 = str2;
    } else {
      throw new UnsupportedOperationException("Syntax '" + str1 + "' not recognized");
    } 
    Pattern pattern = Pattern.compile(str3, 66);
    return (PathMatcher)new Object(this, pattern);
  }
  
  public WatchService newWatchService() throws IOException {
    return new WindowsWatchService(this);
  }
  
  private static class WindowsFileSystem {}
  
  private class WindowsFileSystem {}
}
