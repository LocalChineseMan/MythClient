package sun.security.x509;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.provider.X509Factory;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X509CertImpl extends X509Certificate implements DerEncoder {
  private static final long serialVersionUID = -3457612960190864406L;
  
  private static final String DOT = ".";
  
  public static final String NAME = "x509";
  
  public static final String INFO = "info";
  
  public static final String ALG_ID = "algorithm";
  
  public static final String SIGNATURE = "signature";
  
  public static final String SIGNED_CERT = "signed_cert";
  
  public static final String SUBJECT_DN = "x509.info.subject.dname";
  
  public static final String ISSUER_DN = "x509.info.issuer.dname";
  
  public static final String SERIAL_ID = "x509.info.serialNumber.number";
  
  public static final String PUBLIC_KEY = "x509.info.key.value";
  
  public static final String VERSION = "x509.info.version.number";
  
  public static final String SIG_ALG = "x509.algorithm";
  
  public static final String SIG = "x509.signature";
  
  private boolean readOnly = false;
  
  private byte[] signedCert = null;
  
  protected X509CertInfo info = null;
  
  protected AlgorithmId algId = null;
  
  protected byte[] signature = null;
  
  private static final String KEY_USAGE_OID = "2.5.29.15";
  
  private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
  
  private static final String BASIC_CONSTRAINT_OID = "2.5.29.19";
  
  private static final String SUBJECT_ALT_NAME_OID = "2.5.29.17";
  
  private static final String ISSUER_ALT_NAME_OID = "2.5.29.18";
  
  private static final String AUTH_INFO_ACCESS_OID = "1.3.6.1.5.5.7.1.1";
  
  private static final int NUM_STANDARD_KEY_USAGE = 9;
  
  private Collection<List<?>> subjectAlternativeNames;
  
  private Collection<List<?>> issuerAlternativeNames;
  
  private List<String> extKeyUsage;
  
  private Set<AccessDescription> authInfoAccess;
  
  private PublicKey verifiedPublicKey;
  
  private String verifiedProvider;
  
  private boolean verificationResult;
  
  private ConcurrentHashMap<String, String> fingerprints;
  
  private DerValue readRFC1421Cert(InputStream paramInputStream) throws IOException {
    DerValue derValue = null;
    String str = null;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "ASCII"));
    try {
      str = bufferedReader.readLine();
    } catch (IOException iOException) {
      throw new IOException("Unable to read InputStream: " + iOException
          .getMessage());
    } 
    if (str.equals("-----BEGIN CERTIFICATE-----")) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        while ((str = bufferedReader.readLine()) != null) {
          if (str.equals("-----END CERTIFICATE-----")) {
            derValue = new DerValue(byteArrayOutputStream.toByteArray());
            break;
          } 
          byteArrayOutputStream.write(Base64.getMimeDecoder().decode(str));
        } 
      } catch (IOException iOException) {
        throw new IOException("Unable to read InputStream: " + iOException
            .getMessage());
      } 
    } else {
      throw new IOException("InputStream is not RFC1421 hex-encoded DER bytes");
    } 
    return derValue;
  }
  
  public void encode(OutputStream paramOutputStream) throws CertificateEncodingException {
    if (this.signedCert == null)
      throw new CertificateEncodingException("Null certificate to encode"); 
    try {
      paramOutputStream.write((byte[])this.signedCert.clone());
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } 
  }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    if (this.signedCert == null)
      throw new IOException("Null certificate to encode"); 
    paramOutputStream.write((byte[])this.signedCert.clone());
  }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    return (byte[])getEncodedInternal().clone();
  }
  
  public byte[] getEncodedInternal() throws CertificateEncodingException {
    if (this.signedCert == null)
      throw new CertificateEncodingException("Null certificate to encode"); 
    return this.signedCert;
  }
  
  public void verify(PublicKey paramPublicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    verify(paramPublicKey, "");
  }
  
  public synchronized void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    if (paramString == null)
      paramString = ""; 
    if (this.verifiedPublicKey != null && this.verifiedPublicKey.equals(paramPublicKey))
      if (paramString.equals(this.verifiedProvider)) {
        if (this.verificationResult)
          return; 
        throw new SignatureException("Signature does not match.");
      }  
    if (this.signedCert == null)
      throw new CertificateEncodingException("Uninitialized certificate"); 
    Signature signature = null;
    if (paramString.length() == 0) {
      signature = Signature.getInstance(this.algId.getName());
    } else {
      signature = Signature.getInstance(this.algId.getName(), paramString);
    } 
    signature.initVerify(paramPublicKey);
    byte[] arrayOfByte = this.info.getEncodedInfo();
    signature.update(arrayOfByte, 0, arrayOfByte.length);
    this.verificationResult = signature.verify(this.signature);
    this.verifiedPublicKey = paramPublicKey;
    this.verifiedProvider = paramString;
    if (!this.verificationResult)
      throw new SignatureException("Signature does not match."); 
  }
  
  public synchronized void verify(PublicKey paramPublicKey, Provider paramProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (this.signedCert == null)
      throw new CertificateEncodingException("Uninitialized certificate"); 
    Signature signature = null;
    if (paramProvider == null) {
      signature = Signature.getInstance(this.algId.getName());
    } else {
      signature = Signature.getInstance(this.algId.getName(), paramProvider);
    } 
    signature.initVerify(paramPublicKey);
    byte[] arrayOfByte = this.info.getEncodedInfo();
    signature.update(arrayOfByte, 0, arrayOfByte.length);
    this.verificationResult = signature.verify(this.signature);
    this.verifiedPublicKey = paramPublicKey;
    if (!this.verificationResult)
      throw new SignatureException("Signature does not match."); 
  }
  
  public static void verify(X509Certificate paramX509Certificate, PublicKey paramPublicKey, Provider paramProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    paramX509Certificate.verify(paramPublicKey, paramProvider);
  }
  
  public void sign(PrivateKey paramPrivateKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    sign(paramPrivateKey, paramString, null);
  }
  
  public void sign(PrivateKey paramPrivateKey, String paramString1, String paramString2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    try {
      if (this.readOnly)
        throw new CertificateEncodingException("cannot over-write existing certificate"); 
      Signature signature = null;
      if (paramString2 == null || paramString2.length() == 0) {
        signature = Signature.getInstance(paramString1);
      } else {
        signature = Signature.getInstance(paramString1, paramString2);
      } 
      signature.initSign(paramPrivateKey);
      this.algId = AlgorithmId.get(signature.getAlgorithm());
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      this.info.encode(derOutputStream2);
      byte[] arrayOfByte = derOutputStream2.toByteArray();
      this.algId.encode(derOutputStream2);
      signature.update(arrayOfByte, 0, arrayOfByte.length);
      this.signature = signature.sign();
      derOutputStream2.putBitString(this.signature);
      derOutputStream1.write((byte)48, derOutputStream2);
      this.signedCert = derOutputStream1.toByteArray();
      this.readOnly = true;
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } 
  }
  
  public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
    Date date = new Date();
    checkValidity(date);
  }
  
  public void checkValidity(Date paramDate) throws CertificateExpiredException, CertificateNotYetValidException {
    CertificateValidity certificateValidity = null;
    try {
      certificateValidity = (CertificateValidity)this.info.get("validity");
    } catch (Exception exception) {
      throw new CertificateNotYetValidException("Incorrect validity period");
    } 
    if (certificateValidity == null)
      throw new CertificateNotYetValidException("Null validity period"); 
    certificateValidity.valid(paramDate);
  }
  
  public Object get(String paramString) throws CertificateParsingException {
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    String str = x509AttributeName.getPrefix();
    if (!str.equalsIgnoreCase("x509"))
      throw new CertificateParsingException("Invalid root of attribute name, expected [x509], received [" + str + "]"); 
    x509AttributeName = new X509AttributeName(x509AttributeName.getSuffix());
    str = x509AttributeName.getPrefix();
    if (str.equalsIgnoreCase("info")) {
      if (this.info == null)
        return null; 
      if (x509AttributeName.getSuffix() != null)
        try {
          return this.info.get(x509AttributeName.getSuffix());
        } catch (IOException iOException) {
          throw new CertificateParsingException(iOException.toString());
        } catch (CertificateException certificateException) {
          throw new CertificateParsingException(certificateException.toString());
        }  
      return this.info;
    } 
    if (str.equalsIgnoreCase("algorithm"))
      return this.algId; 
    if (str.equalsIgnoreCase("signature")) {
      if (this.signature != null)
        return this.signature.clone(); 
      return null;
    } 
    if (str.equalsIgnoreCase("signed_cert")) {
      if (this.signedCert != null)
        return this.signedCert.clone(); 
      return null;
    } 
    throw new CertificateParsingException("Attribute name not recognized or get() not allowed for the same: " + str);
  }
  
  public void set(String paramString, Object paramObject) throws CertificateException, IOException {
    if (this.readOnly)
      throw new CertificateException("cannot over-write existing certificate"); 
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    String str = x509AttributeName.getPrefix();
    if (!str.equalsIgnoreCase("x509"))
      throw new CertificateException("Invalid root of attribute name, expected [x509], received " + str); 
    x509AttributeName = new X509AttributeName(x509AttributeName.getSuffix());
    str = x509AttributeName.getPrefix();
    if (str.equalsIgnoreCase("info")) {
      if (x509AttributeName.getSuffix() == null) {
        if (!(paramObject instanceof X509CertInfo))
          throw new CertificateException("Attribute value should be of type X509CertInfo."); 
        this.info = (X509CertInfo)paramObject;
        this.signedCert = null;
      } else {
        this.info.set(x509AttributeName.getSuffix(), paramObject);
        this.signedCert = null;
      } 
    } else {
      throw new CertificateException("Attribute name not recognized or set() not allowed for the same: " + str);
    } 
  }
  
  public void delete(String paramString) throws CertificateException, IOException {
    if (this.readOnly)
      throw new CertificateException("cannot over-write existing certificate"); 
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    String str = x509AttributeName.getPrefix();
    if (!str.equalsIgnoreCase("x509"))
      throw new CertificateException("Invalid root of attribute name, expected [x509], received " + str); 
    x509AttributeName = new X509AttributeName(x509AttributeName.getSuffix());
    str = x509AttributeName.getPrefix();
    if (str.equalsIgnoreCase("info")) {
      if (x509AttributeName.getSuffix() != null) {
        this.info = null;
      } else {
        this.info.delete(x509AttributeName.getSuffix());
      } 
    } else if (str.equalsIgnoreCase("algorithm")) {
      this.algId = null;
    } else if (str.equalsIgnoreCase("signature")) {
      this.signature = null;
    } else if (str.equalsIgnoreCase("signed_cert")) {
      this.signedCert = null;
    } else {
      throw new CertificateException("Attribute name not recognized or delete() not allowed for the same: " + str);
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("x509.info");
    attributeNameEnumeration.addElement("x509.algorithm");
    attributeNameEnumeration.addElement("x509.signature");
    attributeNameEnumeration.addElement("x509.signed_cert");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() {
    return "x509";
  }
  
  public String toString() {
    if (this.info == null || this.algId == null || this.signature == null)
      return ""; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[\n");
    stringBuilder.append(this.info.toString() + "\n");
    stringBuilder.append("  Algorithm: [" + this.algId.toString() + "]\n");
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    stringBuilder.append("  Signature:\n" + hexDumpEncoder.encodeBuffer(this.signature));
    stringBuilder.append("\n]");
    return stringBuilder.toString();
  }
  
  public PublicKey getPublicKey() {
    if (this.info == null)
      return null; 
    try {
      return (PublicKey)this.info.get("key.value");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public int getVersion() {
    if (this.info == null)
      return -1; 
    try {
      int i = ((Integer)this.info.get("version.number")).intValue();
      return i + 1;
    } catch (Exception exception) {
      return -1;
    } 
  }
  
  public BigInteger getSerialNumber() {
    SerialNumber serialNumber = getSerialNumberObject();
    return (serialNumber != null) ? serialNumber.getNumber() : null;
  }
  
  public SerialNumber getSerialNumberObject() {
    if (this.info == null)
      return null; 
    try {
      return (SerialNumber)this.info.get("serialNumber.number");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Principal getSubjectDN() {
    if (this.info == null)
      return null; 
    try {
      return (Principal)this.info.get("subject.dname");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public X500Principal getSubjectX500Principal() {
    if (this.info == null)
      return null; 
    try {
      return (X500Principal)this.info.get("subject.x500principal");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Principal getIssuerDN() {
    if (this.info == null)
      return null; 
    try {
      return (Principal)this.info.get("issuer.dname");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public X500Principal getIssuerX500Principal() {
    if (this.info == null)
      return null; 
    try {
      return (X500Principal)this.info.get("issuer.x500principal");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Date getNotBefore() {
    if (this.info == null)
      return null; 
    try {
      return (Date)this.info.get("validity.notBefore");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Date getNotAfter() {
    if (this.info == null)
      return null; 
    try {
      return (Date)this.info.get("validity.notAfter");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public byte[] getTBSCertificate() throws CertificateEncodingException {
    if (this.info != null)
      return this.info.getEncodedInfo(); 
    throw new CertificateEncodingException("Uninitialized certificate");
  }
  
  public byte[] getSignature() {
    if (this.signature == null)
      return null; 
    byte[] arrayOfByte = new byte[this.signature.length];
    System.arraycopy(this.signature, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public String getSigAlgName() {
    if (this.algId == null)
      return null; 
    return this.algId.getName();
  }
  
  public String getSigAlgOID() {
    if (this.algId == null)
      return null; 
    ObjectIdentifier objectIdentifier = this.algId.getOID();
    return objectIdentifier.toString();
  }
  
  public byte[] getSigAlgParams() {
    if (this.algId == null)
      return null; 
    try {
      return this.algId.getEncodedParams();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public boolean[] getIssuerUniqueID() {
    if (this.info == null)
      return null; 
    try {
      UniqueIdentity uniqueIdentity = (UniqueIdentity)this.info.get("issuerID");
      if (uniqueIdentity == null)
        return null; 
      return uniqueIdentity.getId();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public boolean[] getSubjectUniqueID() {
    if (this.info == null)
      return null; 
    try {
      UniqueIdentity uniqueIdentity = (UniqueIdentity)this.info.get("subjectID");
      if (uniqueIdentity == null)
        return null; 
      return uniqueIdentity.getId();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public KeyIdentifier getAuthKeyId() {
    AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = getAuthorityKeyIdentifierExtension();
    if (authorityKeyIdentifierExtension != null)
      try {
        return (KeyIdentifier)authorityKeyIdentifierExtension.get("key_id");
      } catch (IOException iOException) {} 
    return null;
  }
  
  public KeyIdentifier getSubjectKeyId() {
    SubjectKeyIdentifierExtension subjectKeyIdentifierExtension = getSubjectKeyIdentifierExtension();
    if (subjectKeyIdentifierExtension != null)
      try {
        return subjectKeyIdentifierExtension.get("key_id");
      } catch (IOException iOException) {} 
    return null;
  }
  
  public AuthorityKeyIdentifierExtension getAuthorityKeyIdentifierExtension() {
    return (AuthorityKeyIdentifierExtension)
      getExtension(PKIXExtensions.AuthorityKey_Id);
  }
  
  public BasicConstraintsExtension getBasicConstraintsExtension() {
    return (BasicConstraintsExtension)
      getExtension(PKIXExtensions.BasicConstraints_Id);
  }
  
  public CertificatePoliciesExtension getCertificatePoliciesExtension() {
    return (CertificatePoliciesExtension)
      getExtension(PKIXExtensions.CertificatePolicies_Id);
  }
  
  public ExtendedKeyUsageExtension getExtendedKeyUsageExtension() {
    return (ExtendedKeyUsageExtension)
      getExtension(PKIXExtensions.ExtendedKeyUsage_Id);
  }
  
  public IssuerAlternativeNameExtension getIssuerAlternativeNameExtension() {
    return (IssuerAlternativeNameExtension)
      getExtension(PKIXExtensions.IssuerAlternativeName_Id);
  }
  
  public NameConstraintsExtension getNameConstraintsExtension() {
    return (NameConstraintsExtension)
      getExtension(PKIXExtensions.NameConstraints_Id);
  }
  
  public PolicyConstraintsExtension getPolicyConstraintsExtension() {
    return (PolicyConstraintsExtension)
      getExtension(PKIXExtensions.PolicyConstraints_Id);
  }
  
  public PolicyMappingsExtension getPolicyMappingsExtension() {
    return (PolicyMappingsExtension)
      getExtension(PKIXExtensions.PolicyMappings_Id);
  }
  
  public PrivateKeyUsageExtension getPrivateKeyUsageExtension() {
    return (PrivateKeyUsageExtension)
      getExtension(PKIXExtensions.PrivateKeyUsage_Id);
  }
  
  public SubjectAlternativeNameExtension getSubjectAlternativeNameExtension() {
    return (SubjectAlternativeNameExtension)
      getExtension(PKIXExtensions.SubjectAlternativeName_Id);
  }
  
  public SubjectKeyIdentifierExtension getSubjectKeyIdentifierExtension() {
    return (SubjectKeyIdentifierExtension)
      getExtension(PKIXExtensions.SubjectKey_Id);
  }
  
  public CRLDistributionPointsExtension getCRLDistributionPointsExtension() {
    return (CRLDistributionPointsExtension)
      getExtension(PKIXExtensions.CRLDistributionPoints_Id);
  }
  
  public boolean hasUnsupportedCriticalExtension() {
    if (this.info == null)
      return false; 
    try {
      CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      if (certificateExtensions == null)
        return false; 
      return certificateExtensions.hasUnsupportedCriticalExtension();
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public Set<String> getCriticalExtensionOIDs() {
    if (this.info == null)
      return null; 
    try {
      CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      if (certificateExtensions == null)
        return null; 
      TreeSet<String> treeSet = new TreeSet();
      for (Extension extension : certificateExtensions.getAllExtensions()) {
        if (extension.isCritical())
          treeSet.add(extension.getExtensionId().toString()); 
      } 
      return treeSet;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Set<String> getNonCriticalExtensionOIDs() {
    if (this.info == null)
      return null; 
    try {
      CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      if (certificateExtensions == null)
        return null; 
      TreeSet<String> treeSet = new TreeSet();
      for (Extension extension : certificateExtensions.getAllExtensions()) {
        if (!extension.isCritical())
          treeSet.add(extension.getExtensionId().toString()); 
      } 
      treeSet.addAll(certificateExtensions.getUnparseableExtensions().keySet());
      return treeSet;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Extension getExtension(ObjectIdentifier paramObjectIdentifier) {
    if (this.info == null)
      return null; 
    try {
      CertificateExtensions certificateExtensions;
      try {
        certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      } catch (CertificateException certificateException) {
        return null;
      } 
      if (certificateExtensions == null)
        return null; 
      Extension extension = certificateExtensions.getExtension(paramObjectIdentifier.toString());
      if (extension != null)
        return extension; 
      for (Extension extension1 : certificateExtensions.getAllExtensions()) {
        if (extension1.getExtensionId().equals(paramObjectIdentifier))
          return extension1; 
      } 
      return null;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public Extension getUnparseableExtension(ObjectIdentifier paramObjectIdentifier) {
    if (this.info == null)
      return null; 
    try {
      CertificateExtensions certificateExtensions;
      try {
        certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      } catch (CertificateException certificateException) {
        return null;
      } 
      if (certificateExtensions == null)
        return null; 
      return certificateExtensions.getUnparseableExtensions().get(paramObjectIdentifier.toString());
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public byte[] getExtensionValue(String paramString) {
    try {
      ObjectIdentifier objectIdentifier = new ObjectIdentifier(paramString);
      String str = OIDMap.getName(objectIdentifier);
      Extension extension = null;
      CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
      if (str == null) {
        if (certificateExtensions == null)
          return null; 
        for (Extension extension1 : certificateExtensions.getAllExtensions()) {
          ObjectIdentifier objectIdentifier1 = extension1.getExtensionId();
          if (objectIdentifier1.equals(objectIdentifier)) {
            extension = extension1;
            break;
          } 
        } 
      } else {
        try {
          extension = (Extension)get(str);
        } catch (CertificateException certificateException) {}
      } 
      if (extension == null) {
        if (certificateExtensions != null)
          extension = certificateExtensions.getUnparseableExtensions().get(paramString); 
        if (extension == null)
          return null; 
      } 
      byte[] arrayOfByte = extension.getExtensionValue();
      if (arrayOfByte == null)
        return null; 
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putOctetString(arrayOfByte);
      return derOutputStream.toByteArray();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public boolean[] getKeyUsage() {
    try {
      String str = OIDMap.getName(PKIXExtensions.KeyUsage_Id);
      if (str == null)
        return null; 
      KeyUsageExtension keyUsageExtension = (KeyUsageExtension)get(str);
      if (keyUsageExtension == null)
        return null; 
      boolean[] arrayOfBoolean = keyUsageExtension.getBits();
      if (arrayOfBoolean.length < 9) {
        boolean[] arrayOfBoolean1 = new boolean[9];
        System.arraycopy(arrayOfBoolean, 0, arrayOfBoolean1, 0, arrayOfBoolean.length);
        arrayOfBoolean = arrayOfBoolean1;
      } 
      return arrayOfBoolean;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public synchronized List<String> getExtendedKeyUsage() throws CertificateParsingException {
    if (this.readOnly && this.extKeyUsage != null)
      return this.extKeyUsage; 
    ExtendedKeyUsageExtension extendedKeyUsageExtension = getExtendedKeyUsageExtension();
    if (extendedKeyUsageExtension == null)
      return null; 
    this
      .extKeyUsage = Collections.unmodifiableList(extendedKeyUsageExtension.getExtendedKeyUsage());
    return this.extKeyUsage;
  }
  
  public static List<String> getExtendedKeyUsage(X509Certificate paramX509Certificate) throws CertificateParsingException {
    try {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.37");
      if (arrayOfByte1 == null)
        return null; 
      DerValue derValue = new DerValue(arrayOfByte1);
      byte[] arrayOfByte2 = derValue.getOctetString();
      ExtendedKeyUsageExtension extendedKeyUsageExtension = new ExtendedKeyUsageExtension(Boolean.FALSE, arrayOfByte2);
      return Collections.unmodifiableList(extendedKeyUsageExtension.getExtendedKeyUsage());
    } catch (IOException iOException) {
      throw new CertificateParsingException(iOException);
    } 
  }
  
  public int getBasicConstraints() {
    try {
      String str = OIDMap.getName(PKIXExtensions.BasicConstraints_Id);
      if (str == null)
        return -1; 
      BasicConstraintsExtension basicConstraintsExtension = (BasicConstraintsExtension)get(str);
      if (basicConstraintsExtension == null)
        return -1; 
      if (((Boolean)basicConstraintsExtension.get("is_ca"))
        .booleanValue() == true)
        return ((Integer)basicConstraintsExtension.get("path_len")).intValue(); 
      return -1;
    } catch (Exception exception) {
      return -1;
    } 
  }
  
  private static Collection<List<?>> makeAltNames(GeneralNames paramGeneralNames) {
    if (paramGeneralNames.isEmpty())
      return Collections.emptySet(); 
    ArrayList<? extends List<?>> arrayList = new ArrayList();
    for (GeneralName generalName : paramGeneralNames.names()) {
      DerOutputStream derOutputStream;
      GeneralNameInterface generalNameInterface = generalName.getName();
      ArrayList<Integer> arrayList1 = new ArrayList(2);
      arrayList1.add(Integer.valueOf(generalNameInterface.getType()));
      switch (generalNameInterface.getType()) {
        case 1:
          arrayList1.add(((RFC822Name)generalNameInterface).getName());
          break;
        case 2:
          arrayList1.add(((DNSName)generalNameInterface).getName());
          break;
        case 4:
          arrayList1.add(((X500Name)generalNameInterface).getRFC2253Name());
          break;
        case 6:
          arrayList1.add(((URIName)generalNameInterface).getName());
          break;
        case 7:
          try {
            arrayList1.add(((IPAddressName)generalNameInterface).getName());
          } catch (IOException iOException) {
            throw new RuntimeException("IPAddress cannot be parsed", iOException);
          } 
          break;
        case 8:
          arrayList1.add(((OIDName)generalNameInterface).getOID().toString());
          break;
        default:
          derOutputStream = new DerOutputStream();
          try {
            generalNameInterface.encode(derOutputStream);
          } catch (IOException iOException) {
            throw new RuntimeException("name cannot be encoded", iOException);
          } 
          arrayList1.add(derOutputStream.toByteArray());
          break;
      } 
      arrayList.add(Collections.unmodifiableList(arrayList1));
    } 
    return Collections.unmodifiableCollection(arrayList);
  }
  
  private static Collection<List<?>> cloneAltNames(Collection<List<?>> paramCollection) {
    boolean bool = false;
    for (List<?> list : paramCollection) {
      if (list.get(1) instanceof byte[])
        bool = true; 
    } 
    if (bool) {
      ArrayList<List<Object>> arrayList = new ArrayList();
      for (List<Object> list : paramCollection) {
        byte[] arrayOfByte = (byte[])list.get(1);
        if (arrayOfByte instanceof byte[]) {
          ArrayList<Object> arrayList1 = new ArrayList<>((Collection)list);
          arrayList1.set(1, ((byte[])arrayOfByte).clone());
          arrayList.add(Collections.unmodifiableList((List)arrayList1));
          continue;
        } 
        arrayList.add(list);
      } 
      return Collections.unmodifiableCollection(arrayList);
    } 
    return paramCollection;
  }
  
  public synchronized Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
    GeneralNames generalNames;
    if (this.readOnly && this.subjectAlternativeNames != null)
      return cloneAltNames(this.subjectAlternativeNames); 
    SubjectAlternativeNameExtension subjectAlternativeNameExtension = getSubjectAlternativeNameExtension();
    if (subjectAlternativeNameExtension == null)
      return null; 
    try {
      generalNames = subjectAlternativeNameExtension.get("subject_name");
    } catch (IOException iOException) {
      return Collections.emptySet();
    } 
    this.subjectAlternativeNames = makeAltNames(generalNames);
    return this.subjectAlternativeNames;
  }
  
  public static Collection<List<?>> getSubjectAlternativeNames(X509Certificate paramX509Certificate) throws CertificateParsingException {
    try {
      GeneralNames generalNames;
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.17");
      if (arrayOfByte1 == null)
        return null; 
      DerValue derValue = new DerValue(arrayOfByte1);
      byte[] arrayOfByte2 = derValue.getOctetString();
      SubjectAlternativeNameExtension subjectAlternativeNameExtension = new SubjectAlternativeNameExtension(Boolean.FALSE, arrayOfByte2);
      try {
        generalNames = subjectAlternativeNameExtension.get("subject_name");
      } catch (IOException iOException) {
        return Collections.emptySet();
      } 
      return makeAltNames(generalNames);
    } catch (IOException iOException) {
      throw new CertificateParsingException(iOException);
    } 
  }
  
  public synchronized Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
    GeneralNames generalNames;
    if (this.readOnly && this.issuerAlternativeNames != null)
      return cloneAltNames(this.issuerAlternativeNames); 
    IssuerAlternativeNameExtension issuerAlternativeNameExtension = getIssuerAlternativeNameExtension();
    if (issuerAlternativeNameExtension == null)
      return null; 
    try {
      generalNames = issuerAlternativeNameExtension.get("issuer_name");
    } catch (IOException iOException) {
      return Collections.emptySet();
    } 
    this.issuerAlternativeNames = makeAltNames(generalNames);
    return this.issuerAlternativeNames;
  }
  
  public static Collection<List<?>> getIssuerAlternativeNames(X509Certificate paramX509Certificate) throws CertificateParsingException {
    try {
      GeneralNames generalNames;
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.18");
      if (arrayOfByte1 == null)
        return null; 
      DerValue derValue = new DerValue(arrayOfByte1);
      byte[] arrayOfByte2 = derValue.getOctetString();
      IssuerAlternativeNameExtension issuerAlternativeNameExtension = new IssuerAlternativeNameExtension(Boolean.FALSE, arrayOfByte2);
      try {
        generalNames = issuerAlternativeNameExtension.get("issuer_name");
      } catch (IOException iOException) {
        return Collections.emptySet();
      } 
      return makeAltNames(generalNames);
    } catch (IOException iOException) {
      throw new CertificateParsingException(iOException);
    } 
  }
  
  public AuthorityInfoAccessExtension getAuthorityInfoAccessExtension() {
    return (AuthorityInfoAccessExtension)
      getExtension(PKIXExtensions.AuthInfoAccess_Id);
  }
  
  private void parse(DerValue paramDerValue) throws CertificateException, IOException {
    if (this.readOnly)
      throw new CertificateParsingException("cannot over-write existing certificate"); 
    if (paramDerValue.data == null || paramDerValue.tag != 48)
      throw new CertificateParsingException("invalid DER-encoded certificate data"); 
    this.signedCert = paramDerValue.toByteArray();
    DerValue[] arrayOfDerValue = new DerValue[3];
    arrayOfDerValue[0] = paramDerValue.data.getDerValue();
    arrayOfDerValue[1] = paramDerValue.data.getDerValue();
    arrayOfDerValue[2] = paramDerValue.data.getDerValue();
    if (paramDerValue.data.available() != 0)
      throw new CertificateParsingException("signed overrun, bytes = " + paramDerValue.data
          .available()); 
    if ((arrayOfDerValue[0]).tag != 48)
      throw new CertificateParsingException("signed fields invalid"); 
    this.algId = AlgorithmId.parse(arrayOfDerValue[1]);
    this.signature = arrayOfDerValue[2].getBitString();
    if ((arrayOfDerValue[1]).data.available() != 0)
      throw new CertificateParsingException("algid field overrun"); 
    if ((arrayOfDerValue[2]).data.available() != 0)
      throw new CertificateParsingException("signed fields overrun"); 
    this.info = new X509CertInfo(arrayOfDerValue[0]);
    AlgorithmId algorithmId = (AlgorithmId)this.info.get("algorithmID.algorithm");
    if (!this.algId.equals(algorithmId))
      throw new CertificateException("Signature algorithm mismatch"); 
    this.readOnly = true;
  }
  
  private static X500Principal getX500Principal(X509Certificate paramX509Certificate, boolean paramBoolean) throws Exception {
    byte[] arrayOfByte1 = paramX509Certificate.getEncoded();
    DerInputStream derInputStream1 = new DerInputStream(arrayOfByte1);
    DerValue derValue1 = derInputStream1.getSequence(3)[0];
    DerInputStream derInputStream2 = derValue1.data;
    DerValue derValue2 = derInputStream2.getDerValue();
    if (derValue2.isContextSpecific((byte)0))
      derValue2 = derInputStream2.getDerValue(); 
    derValue2 = derInputStream2.getDerValue();
    derValue2 = derInputStream2.getDerValue();
    if (!paramBoolean) {
      derValue2 = derInputStream2.getDerValue();
      derValue2 = derInputStream2.getDerValue();
    } 
    byte[] arrayOfByte2 = derValue2.toByteArray();
    return new X500Principal(arrayOfByte2);
  }
  
  public static X500Principal getSubjectX500Principal(X509Certificate paramX509Certificate) {
    try {
      return getX500Principal(paramX509Certificate, false);
    } catch (Exception exception) {
      throw new RuntimeException("Could not parse subject", exception);
    } 
  }
  
  public static X500Principal getIssuerX500Principal(X509Certificate paramX509Certificate) {
    try {
      return getX500Principal(paramX509Certificate, true);
    } catch (Exception exception) {
      throw new RuntimeException("Could not parse issuer", exception);
    } 
  }
  
  public static byte[] getEncodedInternal(Certificate paramCertificate) throws CertificateEncodingException {
    if (paramCertificate instanceof X509CertImpl)
      return ((X509CertImpl)paramCertificate).getEncodedInternal(); 
    return paramCertificate.getEncoded();
  }
  
  public static X509CertImpl toImpl(X509Certificate paramX509Certificate) throws CertificateException {
    if (paramX509Certificate instanceof X509CertImpl)
      return (X509CertImpl)paramX509Certificate; 
    return X509Factory.intern(paramX509Certificate);
  }
  
  public static boolean isSelfIssued(X509Certificate paramX509Certificate) {
    X500Principal x500Principal1 = paramX509Certificate.getSubjectX500Principal();
    X500Principal x500Principal2 = paramX509Certificate.getIssuerX500Principal();
    return x500Principal1.equals(x500Principal2);
  }
  
  public static boolean isSelfSigned(X509Certificate paramX509Certificate, String paramString) {
    if (isSelfIssued(paramX509Certificate))
      try {
        if (paramString == null) {
          paramX509Certificate.verify(paramX509Certificate.getPublicKey());
        } else {
          paramX509Certificate.verify(paramX509Certificate.getPublicKey(), paramString);
        } 
        return true;
      } catch (Exception exception) {} 
    return false;
  }
  
  public X509CertImpl() {
    this.fingerprints = new ConcurrentHashMap<>(2);
  }
  
  public X509CertImpl(byte[] paramArrayOfbyte) throws CertificateException {
    this.fingerprints = new ConcurrentHashMap<>(2);
    try {
      parse(new DerValue(paramArrayOfbyte));
    } catch (IOException iOException) {
      this.signedCert = null;
      throw new CertificateException("Unable to initialize, " + iOException, iOException);
    } 
  }
  
  public X509CertImpl(InputStream paramInputStream) throws CertificateException {
    this.fingerprints = new ConcurrentHashMap<>(2);
    DerValue derValue = null;
    BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
    try {
      bufferedInputStream.mark(2147483647);
      derValue = readRFC1421Cert(bufferedInputStream);
    } catch (IOException iOException) {
      try {
        bufferedInputStream.reset();
        derValue = new DerValue(bufferedInputStream);
      } catch (IOException iOException1) {
        throw new CertificateException("Input stream must be either DER-encoded bytes or RFC1421 hex-encoded DER-encoded bytes: " + iOException1.getMessage(), iOException1);
      } 
    } 
    try {
      parse(derValue);
    } catch (IOException iOException) {
      this.signedCert = null;
      throw new CertificateException("Unable to parse DER value of certificate, " + iOException, iOException);
    } 
  }
  
  public X509CertImpl(X509CertInfo paramX509CertInfo) {
    this.fingerprints = new ConcurrentHashMap<>(2);
    this.info = paramX509CertInfo;
  }
  
  public X509CertImpl(DerValue paramDerValue) throws CertificateException {
    this.fingerprints = new ConcurrentHashMap<>(2);
    try {
      parse(paramDerValue);
    } catch (IOException iOException) {
      this.signedCert = null;
      throw new CertificateException("Unable to initialize, " + iOException, iOException);
    } 
  }
  
  public String getFingerprint(String paramString) {
    return this.fingerprints.computeIfAbsent(paramString, paramString -> getCertificateFingerPrint(paramString));
  }
  
  private String getCertificateFingerPrint(String paramString) {
    String str = "";
    try {
      byte[] arrayOfByte1 = getEncoded();
      MessageDigest messageDigest = MessageDigest.getInstance(paramString);
      byte[] arrayOfByte2 = messageDigest.digest(arrayOfByte1);
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < arrayOfByte2.length; b++)
        byte2hex(arrayOfByte2[b], stringBuffer); 
      str = stringBuffer.toString();
    } catch (NoSuchAlgorithmException|CertificateEncodingException noSuchAlgorithmException) {}
    return str;
  }
  
  private static void byte2hex(byte paramByte, StringBuffer paramStringBuffer) {
    char[] arrayOfChar = { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F' };
    int i = (paramByte & 0xF0) >> 4;
    int j = paramByte & 0xF;
    paramStringBuffer.append(arrayOfChar[i]);
    paramStringBuffer.append(arrayOfChar[j]);
  }
}
