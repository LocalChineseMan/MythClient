package sun.security.x509;

import sun.security.util.ObjectIdentifier;

public class PKIXExtensions {
  private static final int[] AuthorityKey_data = new int[] { 2, 5, 29, 35 };
  
  private static final int[] SubjectKey_data = new int[] { 2, 5, 29, 14 };
  
  private static final int[] KeyUsage_data = new int[] { 2, 5, 29, 15 };
  
  private static final int[] PrivateKeyUsage_data = new int[] { 2, 5, 29, 16 };
  
  private static final int[] CertificatePolicies_data = new int[] { 2, 5, 29, 32 };
  
  private static final int[] PolicyMappings_data = new int[] { 2, 5, 29, 33 };
  
  private static final int[] SubjectAlternativeName_data = new int[] { 2, 5, 29, 17 };
  
  private static final int[] IssuerAlternativeName_data = new int[] { 2, 5, 29, 18 };
  
  private static final int[] SubjectDirectoryAttributes_data = new int[] { 2, 5, 29, 9 };
  
  private static final int[] BasicConstraints_data = new int[] { 2, 5, 29, 19 };
  
  private static final int[] NameConstraints_data = new int[] { 2, 5, 29, 30 };
  
  private static final int[] PolicyConstraints_data = new int[] { 2, 5, 29, 36 };
  
  private static final int[] CRLDistributionPoints_data = new int[] { 2, 5, 29, 31 };
  
  private static final int[] CRLNumber_data = new int[] { 2, 5, 29, 20 };
  
  private static final int[] IssuingDistributionPoint_data = new int[] { 2, 5, 29, 28 };
  
  private static final int[] DeltaCRLIndicator_data = new int[] { 2, 5, 29, 27 };
  
  private static final int[] ReasonCode_data = new int[] { 2, 5, 29, 21 };
  
  private static final int[] HoldInstructionCode_data = new int[] { 2, 5, 29, 23 };
  
  private static final int[] InvalidityDate_data = new int[] { 2, 5, 29, 24 };
  
  private static final int[] ExtendedKeyUsage_data = new int[] { 2, 5, 29, 37 };
  
  private static final int[] InhibitAnyPolicy_data = new int[] { 2, 5, 29, 54 };
  
  private static final int[] CertificateIssuer_data = new int[] { 2, 5, 29, 29 };
  
  private static final int[] AuthInfoAccess_data = new int[] { 1, 3, 6, 1, 5, 5, 7, 1, 1 };
  
  private static final int[] SubjectInfoAccess_data = new int[] { 1, 3, 6, 1, 5, 5, 7, 1, 11 };
  
  private static final int[] FreshestCRL_data = new int[] { 2, 5, 29, 46 };
  
  private static final int[] OCSPNoCheck_data = new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 5 };
  
  public static final ObjectIdentifier AuthorityKey_Id = ObjectIdentifier.newInternal(AuthorityKey_data);
  
  public static final ObjectIdentifier SubjectKey_Id = ObjectIdentifier.newInternal(SubjectKey_data);
  
  public static final ObjectIdentifier KeyUsage_Id = ObjectIdentifier.newInternal(KeyUsage_data);
  
  public static final ObjectIdentifier PrivateKeyUsage_Id = ObjectIdentifier.newInternal(PrivateKeyUsage_data);
  
  public static final ObjectIdentifier CertificatePolicies_Id = ObjectIdentifier.newInternal(CertificatePolicies_data);
  
  public static final ObjectIdentifier PolicyMappings_Id = ObjectIdentifier.newInternal(PolicyMappings_data);
  
  public static final ObjectIdentifier SubjectAlternativeName_Id = ObjectIdentifier.newInternal(SubjectAlternativeName_data);
  
  public static final ObjectIdentifier IssuerAlternativeName_Id = ObjectIdentifier.newInternal(IssuerAlternativeName_data);
  
  public static final ObjectIdentifier SubjectDirectoryAttributes_Id;
  
  public static final ObjectIdentifier BasicConstraints_Id;
  
  public static final ObjectIdentifier NameConstraints_Id;
  
  public static final ObjectIdentifier PolicyConstraints_Id;
  
  public static final ObjectIdentifier CRLDistributionPoints_Id;
  
  public static final ObjectIdentifier CRLNumber_Id;
  
  public static final ObjectIdentifier IssuingDistributionPoint_Id;
  
  public static final ObjectIdentifier DeltaCRLIndicator_Id;
  
  public static final ObjectIdentifier ReasonCode_Id;
  
  public static final ObjectIdentifier HoldInstructionCode_Id;
  
  public static final ObjectIdentifier InvalidityDate_Id;
  
  public static final ObjectIdentifier ExtendedKeyUsage_Id = ObjectIdentifier.newInternal(ExtendedKeyUsage_data);
  
  public static final ObjectIdentifier InhibitAnyPolicy_Id = ObjectIdentifier.newInternal(InhibitAnyPolicy_data);
  
  public static final ObjectIdentifier CertificateIssuer_Id;
  
  public static final ObjectIdentifier AuthInfoAccess_Id;
  
  public static final ObjectIdentifier SubjectInfoAccess_Id;
  
  public static final ObjectIdentifier FreshestCRL_Id;
  
  public static final ObjectIdentifier OCSPNoCheck_Id;
  
  static {
    SubjectDirectoryAttributes_Id = ObjectIdentifier.newInternal(SubjectDirectoryAttributes_data);
    BasicConstraints_Id = ObjectIdentifier.newInternal(BasicConstraints_data);
    ReasonCode_Id = ObjectIdentifier.newInternal(ReasonCode_data);
    HoldInstructionCode_Id = ObjectIdentifier.newInternal(HoldInstructionCode_data);
    InvalidityDate_Id = ObjectIdentifier.newInternal(InvalidityDate_data);
    NameConstraints_Id = ObjectIdentifier.newInternal(NameConstraints_data);
    PolicyConstraints_Id = ObjectIdentifier.newInternal(PolicyConstraints_data);
    CRLDistributionPoints_Id = ObjectIdentifier.newInternal(CRLDistributionPoints_data);
    CRLNumber_Id = ObjectIdentifier.newInternal(CRLNumber_data);
    IssuingDistributionPoint_Id = ObjectIdentifier.newInternal(IssuingDistributionPoint_data);
    DeltaCRLIndicator_Id = ObjectIdentifier.newInternal(DeltaCRLIndicator_data);
    CertificateIssuer_Id = ObjectIdentifier.newInternal(CertificateIssuer_data);
    AuthInfoAccess_Id = ObjectIdentifier.newInternal(AuthInfoAccess_data);
    SubjectInfoAccess_Id = ObjectIdentifier.newInternal(SubjectInfoAccess_data);
    FreshestCRL_Id = ObjectIdentifier.newInternal(FreshestCRL_data);
    OCSPNoCheck_Id = ObjectIdentifier.newInternal(OCSPNoCheck_data);
  }
}
