package java.io;

public class IOException extends Exception {
  static final long serialVersionUID = 7818375828146090155L;
  
  public IOException() {}
  
  public IOException(String paramString) {
    super(paramString);
  }
  
  public IOException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public IOException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
