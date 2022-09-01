package java.io;

public class FileWriter extends OutputStreamWriter {
  public FileWriter(String paramString) throws IOException {
    super(new FileOutputStream(paramString));
  }
  
  public FileWriter(String paramString, boolean paramBoolean) throws IOException {
    super(new FileOutputStream(paramString, paramBoolean));
  }
  
  public FileWriter(File paramFile) throws IOException {
    super(new FileOutputStream(paramFile));
  }
  
  public FileWriter(File paramFile, boolean paramBoolean) throws IOException {
    super(new FileOutputStream(paramFile, paramBoolean));
  }
  
  public FileWriter(FileDescriptor paramFileDescriptor) {
    super(new FileOutputStream(paramFileDescriptor));
  }
}
