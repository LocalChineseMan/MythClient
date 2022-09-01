package java.security;

public class PrivilegedActionException extends Exception {
  private static final long serialVersionUID = 4724086851538908602L;
  
  private Exception exception;
  
  public PrivilegedActionException(Exception paramException) {
    super((Throwable)null);
    this.exception = paramException;
  }
  
  public Exception getException() {
    return this.exception;
  }
  
  public Throwable getCause() {
    return this.exception;
  }
  
  public String toString() {
    String str = getClass().getName();
    return (this.exception != null) ? (str + ": " + this.exception.toString()) : str;
  }
}
