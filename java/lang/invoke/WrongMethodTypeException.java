package java.lang.invoke;

public class WrongMethodTypeException extends RuntimeException {
  private static final long serialVersionUID = 292L;
  
  public WrongMethodTypeException() {}
  
  public WrongMethodTypeException(String paramString) {
    super(paramString);
  }
  
  WrongMethodTypeException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  WrongMethodTypeException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
