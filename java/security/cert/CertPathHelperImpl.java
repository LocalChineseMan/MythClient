package java.security.cert;

import java.util.Date;
import java.util.Set;
import sun.security.provider.certpath.CertPathHelper;
import sun.security.x509.GeneralNameInterface;

class CertPathHelperImpl extends CertPathHelper {
  static synchronized void initialize() {
    if (CertPathHelper.instance == null)
      CertPathHelper.instance = new CertPathHelperImpl(); 
  }
  
  protected void implSetPathToNames(X509CertSelector paramX509CertSelector, Set<GeneralNameInterface> paramSet) {
    paramX509CertSelector.setPathToNamesInternal(paramSet);
  }
  
  protected void implSetDateAndTime(X509CRLSelector paramX509CRLSelector, Date paramDate, long paramLong) {
    paramX509CRLSelector.setDateAndTime(paramDate, paramLong);
  }
}
