package java.util.concurrent;

public class ExecutionException extends Exception {
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected ExecutionException() {}
  
  protected ExecutionException(String paramString) {
    super(paramString);
  }
  
  public ExecutionException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public ExecutionException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
