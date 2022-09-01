package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public final class AlgorithmChecker extends PKIXCertPathChecker {
  private final AlgorithmConstraints constraints;
  
  private final PublicKey trustedPubKey;
  
  private PublicKey prevPubKey;
  
  private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  
  private static final DisabledAlgorithmConstraints certPathDefaultConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor) {
    this(paramTrustAnchor, certPathDefaultConstraints);
  }
  
  public AlgorithmChecker(AlgorithmConstraints paramAlgorithmConstraints) {
    this.prevPubKey = null;
    this.trustedPubKey = null;
    this.constraints = paramAlgorithmConstraints;
  }
  
  public AlgorithmChecker(TrustAnchor paramTrustAnchor, AlgorithmConstraints paramAlgorithmConstraints) {
    if (paramTrustAnchor == null)
      throw new IllegalArgumentException("The trust anchor cannot be null"); 
    if (paramTrustAnchor.getTrustedCert() != null) {
      this.trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
    } else {
      this.trustedPubKey = paramTrustAnchor.getCAPublicKey();
    } 
    this.prevPubKey = this.trustedPubKey;
    this.constraints = paramAlgorithmConstraints;
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (!paramBoolean) {
      if (this.trustedPubKey != null) {
        this.prevPubKey = this.trustedPubKey;
      } else {
        this.prevPubKey = null;
      } 
    } else {
      throw new CertPathValidatorException("forward checking not supported");
    } 
  }
  
  public boolean isForwardCheckingSupported() {
    return false;
  }
  
  public Set<String> getSupportedExtensions() {
    return null;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    if (!(paramCertificate instanceof X509Certificate) || this.constraints == null)
      return; 
    X509CertImpl x509CertImpl = null;
    try {
      x509CertImpl = X509CertImpl.toImpl((X509Certificate)paramCertificate);
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    PublicKey publicKey = x509CertImpl.getPublicKey();
    String str = x509CertImpl.getSigAlgName();
    AlgorithmId algorithmId = null;
    try {
      algorithmId = (AlgorithmId)x509CertImpl.get("x509.algorithm");
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    AlgorithmParameters algorithmParameters = algorithmId.getParameters();
    if (!this.constraints.permits(SIGNATURE_PRIMITIVE_SET, str, algorithmParameters))
      throw new CertPathValidatorException("Algorithm constraints check failed: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
    boolean[] arrayOfBoolean = x509CertImpl.getKeyUsage();
    if (arrayOfBoolean != null && arrayOfBoolean.length < 9)
      throw new CertPathValidatorException("incorrect KeyUsage extension", null, null, -1, PKIXReason.INVALID_KEY_USAGE); 
    if (arrayOfBoolean != null) {
      EnumSet<CryptoPrimitive> enumSet = EnumSet.noneOf(CryptoPrimitive.class);
      if (arrayOfBoolean[0] || arrayOfBoolean[1] || arrayOfBoolean[5] || arrayOfBoolean[6])
        enumSet.add(CryptoPrimitive.SIGNATURE); 
      if (arrayOfBoolean[2])
        enumSet.add(CryptoPrimitive.KEY_ENCAPSULATION); 
      if (arrayOfBoolean[3])
        enumSet.add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION); 
      if (arrayOfBoolean[4])
        enumSet.add(CryptoPrimitive.KEY_AGREEMENT); 
      if (!enumSet.isEmpty() && 
        !this.constraints.permits(enumSet, publicKey))
        throw new CertPathValidatorException("algorithm constraints check failed", null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
    } 
    if (this.prevPubKey != null) {
      if (str != null && 
        !this.constraints.permits(SIGNATURE_PRIMITIVE_SET, str, this.prevPubKey, algorithmParameters))
        throw new CertPathValidatorException("Algorithm constraints check failed: " + str, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
      if (PKIX.isDSAPublicKeyWithoutParams(publicKey)) {
        if (!(this.prevPubKey instanceof DSAPublicKey))
          throw new CertPathValidatorException("Input key is not of a appropriate type for inheriting parameters"); 
        DSAParams dSAParams = ((DSAPublicKey)this.prevPubKey).getParams();
        if (dSAParams == null)
          throw new CertPathValidatorException("Key parameters missing"); 
        try {
          BigInteger bigInteger = ((DSAPublicKey)publicKey).getY();
          KeyFactory keyFactory = KeyFactory.getInstance("DSA");
          DSAPublicKeySpec dSAPublicKeySpec = new DSAPublicKeySpec(bigInteger, dSAParams.getP(), dSAParams.getQ(), dSAParams.getG());
          publicKey = keyFactory.generatePublic(dSAPublicKeySpec);
        } catch (GeneralSecurityException generalSecurityException) {
          throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + generalSecurityException
              .getMessage(), generalSecurityException);
        } 
      } 
    } 
    this.prevPubKey = publicKey;
  }
  
  void trySetTrustAnchor(TrustAnchor paramTrustAnchor) {
    if (this.prevPubKey == null) {
      if (paramTrustAnchor == null)
        throw new IllegalArgumentException("The trust anchor cannot be null"); 
      if (paramTrustAnchor.getTrustedCert() != null) {
        this.prevPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
      } else {
        this.prevPubKey = paramTrustAnchor.getCAPublicKey();
      } 
    } 
  }
  
  static void check(PublicKey paramPublicKey, X509CRL paramX509CRL) throws CertPathValidatorException {
    X509CRLImpl x509CRLImpl = null;
    try {
      x509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
    } catch (CRLException cRLException) {
      throw new CertPathValidatorException(cRLException);
    } 
    AlgorithmId algorithmId = x509CRLImpl.getSigAlgId();
    check(paramPublicKey, algorithmId);
  }
  
  static void check(PublicKey paramPublicKey, AlgorithmId paramAlgorithmId) throws CertPathValidatorException {
    String str = paramAlgorithmId.getName();
    AlgorithmParameters algorithmParameters = paramAlgorithmId.getParameters();
    if (!certPathDefaultConstraints.permits(SIGNATURE_PRIMITIVE_SET, str, paramPublicKey, algorithmParameters))
      throw new CertPathValidatorException("algorithm check failed: " + str + " is disabled", null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED); 
  }
}
