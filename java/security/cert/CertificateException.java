package java.security.cert;

import java.security.GeneralSecurityException;

public class CertificateException extends GeneralSecurityException {
  private static final long serialVersionUID = 3192535253797119798L;
  
  public CertificateException() {}
  
  public CertificateException(String paramString) {
    super(paramString);
  }
  
  public CertificateException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public CertificateException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}
