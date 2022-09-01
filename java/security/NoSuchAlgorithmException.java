package java.security;

public class NoSuchAlgorithmException extends GeneralSecurityException {
  private static final long serialVersionUID = -7443947487218346562L;
  
  public NoSuchAlgorithmException() {}
  
  public NoSuchAlgorithmException(String paramString) {
    super(paramString);
  }
  
  public NoSuchAlgorithmException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public NoSuchAlgorithmException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
