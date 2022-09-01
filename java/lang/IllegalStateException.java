package java.lang;

public class IllegalStateException extends RuntimeException {
  static final long serialVersionUID = -1848914673093119416L;
  
  public IllegalStateException() {}
  
  public IllegalStateException(String paramString) {
    super(paramString);
  }
  
  public IllegalStateException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public IllegalStateException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
