package java.security.cert;

public interface CertPathChecker {
  void init(boolean paramBoolean) throws CertPathValidatorException;
  
  boolean isForwardCheckingSupported();
  
  void check(Certificate paramCertificate) throws CertPathValidatorException;
}
