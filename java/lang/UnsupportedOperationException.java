package java.lang;

public class UnsupportedOperationException extends RuntimeException {
  static final long serialVersionUID = -1242599979055084673L;
  
  public UnsupportedOperationException() {}
  
  public UnsupportedOperationException(String paramString) {
    super(paramString);
  }
  
  public UnsupportedOperationException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public UnsupportedOperationException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
