package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;

class PKIX {
  private static final Debug debug = Debug.getInstance("certpath");
  
  static boolean isDSAPublicKeyWithoutParams(PublicKey paramPublicKey) {
    return (paramPublicKey instanceof DSAPublicKey && ((DSAPublicKey)paramPublicKey)
      .getParams() == null);
  }
  
  static ValidatorParams checkParams(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws InvalidAlgorithmParameterException {
    if (!(paramCertPathParameters instanceof PKIXParameters))
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters"); 
    return new ValidatorParams(paramCertPath, (PKIXParameters)paramCertPathParameters);
  }
  
  static BuilderParams checkBuilderParams(CertPathParameters paramCertPathParameters) throws InvalidAlgorithmParameterException {
    if (!(paramCertPathParameters instanceof PKIXBuilderParameters))
      throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters"); 
    return new BuilderParams((PKIXBuilderParameters)paramCertPathParameters);
  }
  
  private static class PKIX {}
  
  static class PKIX {}
  
  static class PKIX {}
  
  static class ValidatorParams {
    private final PKIXParameters params;
    
    private CertPath certPath;
    
    private List<PKIXCertPathChecker> checkers;
    
    private List<CertStore> stores;
    
    private boolean gotDate;
    
    private Date date;
    
    private Set<String> policies;
    
    private boolean gotConstraints;
    
    private CertSelector constraints;
    
    private Set<TrustAnchor> anchors;
    
    private List<X509Certificate> certs;
    
    ValidatorParams(CertPath param1CertPath, PKIXParameters param1PKIXParameters) throws InvalidAlgorithmParameterException {
      this(param1PKIXParameters);
      if (!param1CertPath.getType().equals("X.509") && !param1CertPath.getType().equals("X509"))
        throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509"); 
      this.certPath = param1CertPath;
    }
    
    ValidatorParams(PKIXParameters param1PKIXParameters) throws InvalidAlgorithmParameterException {
      this.anchors = param1PKIXParameters.getTrustAnchors();
      for (TrustAnchor trustAnchor : this.anchors) {
        if (trustAnchor.getNameConstraints() != null)
          throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported"); 
      } 
      this.params = param1PKIXParameters;
    }
    
    CertPath certPath() {
      return this.certPath;
    }
    
    void setCertPath(CertPath param1CertPath) {
      this.certPath = param1CertPath;
    }
    
    List<X509Certificate> certificates() {
      if (this.certs == null)
        if (this.certPath == null) {
          this.certs = Collections.emptyList();
        } else {
          ArrayList<Certificate> arrayList = new ArrayList<>(this.certPath.getCertificates());
          Collections.reverse(arrayList);
          this.certs = (List)arrayList;
        }  
      return this.certs;
    }
    
    List<PKIXCertPathChecker> certPathCheckers() {
      if (this.checkers == null)
        this.checkers = this.params.getCertPathCheckers(); 
      return this.checkers;
    }
    
    List<CertStore> certStores() {
      if (this.stores == null)
        this.stores = this.params.getCertStores(); 
      return this.stores;
    }
    
    Date date() {
      if (!this.gotDate) {
        this.date = this.params.getDate();
        if (this.date == null)
          this.date = new Date(); 
        this.gotDate = true;
      } 
      return this.date;
    }
    
    Set<String> initialPolicies() {
      if (this.policies == null)
        this.policies = this.params.getInitialPolicies(); 
      return this.policies;
    }
    
    CertSelector targetCertConstraints() {
      if (!this.gotConstraints) {
        this.constraints = this.params.getTargetCertConstraints();
        this.gotConstraints = true;
      } 
      return this.constraints;
    }
    
    Set<TrustAnchor> trustAnchors() {
      return this.anchors;
    }
    
    boolean revocationEnabled() {
      return this.params.isRevocationEnabled();
    }
    
    boolean policyMappingInhibited() {
      return this.params.isPolicyMappingInhibited();
    }
    
    boolean explicitPolicyRequired() {
      return this.params.isExplicitPolicyRequired();
    }
    
    boolean policyQualifiersRejected() {
      return this.params.getPolicyQualifiersRejected();
    }
    
    String sigProvider() {
      return this.params.getSigProvider();
    }
    
    boolean anyPolicyInhibited() {
      return this.params.isAnyPolicyInhibited();
    }
    
    PKIXParameters getPKIXParameters() {
      return this.params;
    }
  }
}
