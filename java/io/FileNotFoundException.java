package java.io;

public class FileNotFoundException extends IOException {
  private static final long serialVersionUID = -897856973823710492L;
  
  public FileNotFoundException() {}
  
  public FileNotFoundException(String paramString) {
    super(paramString);
  }
  
  private FileNotFoundException(String paramString1, String paramString2) {
    super(paramString1 + ((paramString2 == null) ? "" : (" (" + paramString2 + ")")));
  }
}
