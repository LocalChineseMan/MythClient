package java.security;

public class GeneralSecurityException extends Exception {
  private static final long serialVersionUID = 894798122053539237L;
  
  public GeneralSecurityException() {}
  
  public GeneralSecurityException(String paramString) {
    super(paramString);
  }
  
  public GeneralSecurityException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public GeneralSecurityException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
