package java.security;

public class SignatureException extends GeneralSecurityException {
  private static final long serialVersionUID = 7509989324975124438L;
  
  public SignatureException() {}
  
  public SignatureException(String paramString) {
    super(paramString);
  }
  
  public SignatureException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public SignatureException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
