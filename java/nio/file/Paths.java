package java.nio.file;

import java.net.URI;
import java.nio.file.spi.FileSystemProvider;

public final class Paths {
  public static Path get(String paramString, String... paramVarArgs) {
    return FileSystems.getDefault().getPath(paramString, paramVarArgs);
  }
  
  public static Path get(URI paramURI) {
    String str = paramURI.getScheme();
    if (str == null)
      throw new IllegalArgumentException("Missing scheme"); 
    if (str.equalsIgnoreCase("file"))
      return FileSystems.getDefault().provider().getPath(paramURI); 
    for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
      if (fileSystemProvider.getScheme().equalsIgnoreCase(str))
        return fileSystemProvider.getPath(paramURI); 
    } 
    throw new FileSystemNotFoundException("Provider \"" + str + "\" not installed");
  }
}
