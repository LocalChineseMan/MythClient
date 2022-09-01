package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathValidatorSpi {
  public abstract CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException;
  
  public CertPathChecker engineGetRevocationChecker() {
    throw new UnsupportedOperationException();
  }
}
