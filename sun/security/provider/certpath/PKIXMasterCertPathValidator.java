package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;

class PKIXMasterCertPathValidator {
  private static final Debug debug = Debug.getInstance("certpath");
  
  static void validate(CertPath paramCertPath, List<X509Certificate> paramList, List<PKIXCertPathChecker> paramList1) throws CertPathValidatorException {
    int i = paramList.size();
    if (debug != null) {
      debug.println("--------------------------------------------------------------");
      debug.println("Executing PKIX certification path validation algorithm.");
    } 
    for (byte b = 0; b < i; b++) {
      if (debug != null)
        debug.println("Checking cert" + (b + 1) + " ..."); 
      X509Certificate x509Certificate = paramList.get(b);
      Set<String> set = x509Certificate.getCriticalExtensionOIDs();
      if (set == null)
        set = Collections.emptySet(); 
      if (debug != null && !set.isEmpty()) {
        debug.println("Set of critical extensions:");
        for (String str : set)
          debug.println(str); 
      } 
      for (byte b1 = 0; b1 < paramList1.size(); b1++) {
        PKIXCertPathChecker pKIXCertPathChecker = paramList1.get(b1);
        if (debug != null)
          debug.println("-Using checker" + (b1 + 1) + " ... [" + pKIXCertPathChecker
              .getClass().getName() + "]"); 
        if (b == 0)
          pKIXCertPathChecker.init(false); 
        try {
          pKIXCertPathChecker.check(x509Certificate, set);
          if (debug != null)
            debug.println("-checker" + (b1 + 1) + " validation succeeded"); 
        } catch (CertPathValidatorException certPathValidatorException) {
          throw new CertPathValidatorException(certPathValidatorException.getMessage(), certPathValidatorException
              .getCause(), paramCertPath, i - b + 1, certPathValidatorException
              .getReason());
        } 
      } 
      if (!set.isEmpty())
        throw new CertPathValidatorException("unrecognized critical extension(s)", null, paramCertPath, i - b + 1, PKIXReason.UNRECOGNIZED_CRIT_EXT); 
      if (debug != null)
        debug.println("\ncert" + (b + 1) + " validation succeeded.\n"); 
    } 
    if (debug != null) {
      debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
      debug.println("--------------------------------------------------------------");
    } 
  }
}
