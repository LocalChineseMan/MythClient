package java.security.cert;

public class CertificateParsingException extends CertificateException {
  private static final long serialVersionUID = -7989222416793322029L;
  
  public CertificateParsingException() {}
  
  public CertificateParsingException(String paramString) {
    super(paramString);
  }
  
  public CertificateParsingException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public CertificateParsingException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
