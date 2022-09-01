package java.io;

@FunctionalInterface
public interface FilenameFilter {
  boolean accept(File paramFile, String paramString);
}
