package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyId;
import sun.security.x509.CertificatePolicySet;
import sun.security.x509.DNSName;
import sun.security.x509.EDIPartyName;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtree;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.IPAddressName;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.OIDName;
import sun.security.x509.OtherName;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PrivateKeyUsageExtension;
import sun.security.x509.RFC822Name;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.URIName;
import sun.security.x509.X400Address;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509Key;

public class X509CertSelector implements CertSelector {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final ObjectIdentifier ANY_EXTENDED_KEY_USAGE = ObjectIdentifier.newInternal(new int[] { 2, 5, 29, 37, 0 });
  
  private BigInteger serialNumber;
  
  private X500Principal issuer;
  
  private X500Principal subject;
  
  private byte[] subjectKeyID;
  
  private byte[] authorityKeyID;
  
  private Date certificateValid;
  
  private Date privateKeyValid;
  
  private ObjectIdentifier subjectPublicKeyAlgID;
  
  private PublicKey subjectPublicKey;
  
  private byte[] subjectPublicKeyBytes;
  
  private boolean[] keyUsage;
  
  private Set<String> keyPurposeSet;
  
  private Set<ObjectIdentifier> keyPurposeOIDSet;
  
  private Set<List<?>> subjectAlternativeNames;
  
  private Set<GeneralNameInterface> subjectAlternativeGeneralNames;
  
  private CertificatePolicySet policy;
  
  private Set<String> policySet;
  
  private Set<List<?>> pathToNames;
  
  private Set<GeneralNameInterface> pathToGeneralNames;
  
  private NameConstraintsExtension nc;
  
  private byte[] ncBytes;
  
  static {
    CertPathHelperImpl.initialize();
  }
  
  private int basicConstraints = -1;
  
  private X509Certificate x509Cert;
  
  private boolean matchAllSubjectAltNames = true;
  
  private static final Boolean FALSE = Boolean.FALSE;
  
  private static final int PRIVATE_KEY_USAGE_ID = 0;
  
  private static final int SUBJECT_ALT_NAME_ID = 1;
  
  private static final int NAME_CONSTRAINTS_ID = 2;
  
  private static final int CERT_POLICIES_ID = 3;
  
  private static final int EXTENDED_KEY_USAGE_ID = 4;
  
  private static final int NUM_OF_EXTENSIONS = 5;
  
  private static final String[] EXTENSION_OIDS = new String[5];
  
  static final int NAME_ANY = 0;
  
  static final int NAME_RFC822 = 1;
  
  static final int NAME_DNS = 2;
  
  static final int NAME_X400 = 3;
  
  static final int NAME_DIRECTORY = 4;
  
  static final int NAME_EDI = 5;
  
  static final int NAME_URI = 6;
  
  static final int NAME_IP = 7;
  
  static final int NAME_OID = 8;
  
  static {
    EXTENSION_OIDS[0] = "2.5.29.16";
    EXTENSION_OIDS[1] = "2.5.29.17";
    EXTENSION_OIDS[2] = "2.5.29.30";
    EXTENSION_OIDS[3] = "2.5.29.32";
    EXTENSION_OIDS[4] = "2.5.29.37";
  }
  
  public void setCertificate(X509Certificate paramX509Certificate) {
    this.x509Cert = paramX509Certificate;
  }
  
  public void setSerialNumber(BigInteger paramBigInteger) {
    this.serialNumber = paramBigInteger;
  }
  
  public void setIssuer(X500Principal paramX500Principal) {
    this.issuer = paramX500Principal;
  }
  
  public void setIssuer(String paramString) throws IOException {
    if (paramString == null) {
      this.issuer = null;
    } else {
      this.issuer = (new X500Name(paramString)).asX500Principal();
    } 
  }
  
  public void setIssuer(byte[] paramArrayOfbyte) throws IOException {
    try {
      this.issuer = (paramArrayOfbyte == null) ? null : new X500Principal(paramArrayOfbyte);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException("Invalid name", illegalArgumentException);
    } 
  }
  
  public void setSubject(X500Principal paramX500Principal) {
    this.subject = paramX500Principal;
  }
  
  public void setSubject(String paramString) throws IOException {
    if (paramString == null) {
      this.subject = null;
    } else {
      this.subject = (new X500Name(paramString)).asX500Principal();
    } 
  }
  
  public void setSubject(byte[] paramArrayOfbyte) throws IOException {
    try {
      this.subject = (paramArrayOfbyte == null) ? null : new X500Principal(paramArrayOfbyte);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException("Invalid name", illegalArgumentException);
    } 
  }
  
  public void setSubjectKeyIdentifier(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null) {
      this.subjectKeyID = null;
    } else {
      this.subjectKeyID = (byte[])paramArrayOfbyte.clone();
    } 
  }
  
  public void setAuthorityKeyIdentifier(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null) {
      this.authorityKeyID = null;
    } else {
      this.authorityKeyID = (byte[])paramArrayOfbyte.clone();
    } 
  }
  
  public void setCertificateValid(Date paramDate) {
    if (paramDate == null) {
      this.certificateValid = null;
    } else {
      this.certificateValid = (Date)paramDate.clone();
    } 
  }
  
  public void setPrivateKeyValid(Date paramDate) {
    if (paramDate == null) {
      this.privateKeyValid = null;
    } else {
      this.privateKeyValid = (Date)paramDate.clone();
    } 
  }
  
  public void setSubjectPublicKeyAlgID(String paramString) throws IOException {
    if (paramString == null) {
      this.subjectPublicKeyAlgID = null;
    } else {
      this.subjectPublicKeyAlgID = new ObjectIdentifier(paramString);
    } 
  }
  
  public void setSubjectPublicKey(PublicKey paramPublicKey) {
    if (paramPublicKey == null) {
      this.subjectPublicKey = null;
      this.subjectPublicKeyBytes = null;
    } else {
      this.subjectPublicKey = paramPublicKey;
      this.subjectPublicKeyBytes = paramPublicKey.getEncoded();
    } 
  }
  
  public void setSubjectPublicKey(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null) {
      this.subjectPublicKey = null;
      this.subjectPublicKeyBytes = null;
    } else {
      this.subjectPublicKeyBytes = (byte[])paramArrayOfbyte.clone();
      this.subjectPublicKey = X509Key.parse(new DerValue(this.subjectPublicKeyBytes));
    } 
  }
  
  public void setKeyUsage(boolean[] paramArrayOfboolean) {
    if (paramArrayOfboolean == null) {
      this.keyUsage = null;
    } else {
      this.keyUsage = (boolean[])paramArrayOfboolean.clone();
    } 
  }
  
  public void setExtendedKeyUsage(Set<String> paramSet) throws IOException {
    if (paramSet == null || paramSet.isEmpty()) {
      this.keyPurposeSet = null;
      this.keyPurposeOIDSet = null;
    } else {
      this
        .keyPurposeSet = Collections.unmodifiableSet(new HashSet<>(paramSet));
      this.keyPurposeOIDSet = new HashSet<>();
      for (String str : this.keyPurposeSet)
        this.keyPurposeOIDSet.add(new ObjectIdentifier(str)); 
    } 
  }
  
  public void setMatchAllSubjectAltNames(boolean paramBoolean) {
    this.matchAllSubjectAltNames = paramBoolean;
  }
  
  public void setSubjectAlternativeNames(Collection<List<?>> paramCollection) throws IOException {
    if (paramCollection == null) {
      this.subjectAlternativeNames = null;
      this.subjectAlternativeGeneralNames = null;
    } else {
      if (paramCollection.isEmpty()) {
        this.subjectAlternativeNames = null;
        this.subjectAlternativeGeneralNames = null;
        return;
      } 
      Set<List<?>> set = cloneAndCheckNames(paramCollection);
      this.subjectAlternativeGeneralNames = parseNames(set);
      this.subjectAlternativeNames = set;
    } 
  }
  
  public void addSubjectAlternativeName(int paramInt, String paramString) throws IOException {
    addSubjectAlternativeNameInternal(paramInt, paramString);
  }
  
  public void addSubjectAlternativeName(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    addSubjectAlternativeNameInternal(paramInt, paramArrayOfbyte.clone());
  }
  
  private void addSubjectAlternativeNameInternal(int paramInt, Object paramObject) throws IOException {
    GeneralNameInterface generalNameInterface = makeGeneralNameInterface(paramInt, paramObject);
    if (this.subjectAlternativeNames == null)
      this.subjectAlternativeNames = new HashSet<>(); 
    if (this.subjectAlternativeGeneralNames == null)
      this.subjectAlternativeGeneralNames = new HashSet<>(); 
    ArrayList<Integer> arrayList = new ArrayList(2);
    arrayList.add(Integer.valueOf(paramInt));
    arrayList.add(paramObject);
    this.subjectAlternativeNames.add(arrayList);
    this.subjectAlternativeGeneralNames.add(generalNameInterface);
  }
  
  private static Set<GeneralNameInterface> parseNames(Collection<List<?>> paramCollection) throws IOException {
    HashSet<GeneralNameInterface> hashSet = new HashSet();
    for (List<Object> list : paramCollection) {
      if (list.size() != 2)
        throw new IOException("name list size not 2"); 
      Integer integer = (Integer)list.get(0);
      if (!(integer instanceof Integer))
        throw new IOException("expected an Integer"); 
      int i = ((Integer)integer).intValue();
      integer = (Integer)list.get(1);
      hashSet.add(makeGeneralNameInterface(i, integer));
    } 
    return hashSet;
  }
  
  static boolean equalNames(Collection<?> paramCollection1, Collection<?> paramCollection2) {
    if (paramCollection1 == null || paramCollection2 == null)
      return (paramCollection1 == paramCollection2); 
    return paramCollection1.equals(paramCollection2);
  }
  
  static GeneralNameInterface makeGeneralNameInterface(int paramInt, Object paramObject) throws IOException {
    OIDName oIDName;
    if (debug != null)
      debug.println("X509CertSelector.makeGeneralNameInterface(" + paramInt + ")..."); 
    if (paramObject instanceof String) {
      RFC822Name rFC822Name;
      DNSName dNSName;
      X500Name x500Name;
      URIName uRIName;
      IPAddressName iPAddressName;
      if (debug != null)
        debug.println("X509CertSelector.makeGeneralNameInterface() name is String: " + paramObject); 
      switch (paramInt) {
        case 1:
          rFC822Name = new RFC822Name((String)paramObject);
          break;
        case 2:
          dNSName = new DNSName((String)paramObject);
          break;
        case 4:
          x500Name = new X500Name((String)paramObject);
          break;
        case 6:
          uRIName = new URIName((String)paramObject);
          break;
        case 7:
          iPAddressName = new IPAddressName((String)paramObject);
          break;
        case 8:
          oIDName = new OIDName((String)paramObject);
          break;
        default:
          throw new IOException("unable to parse String names of type " + paramInt);
      } 
      if (debug != null)
        debug.println("X509CertSelector.makeGeneralNameInterface() result: " + oIDName
            .toString()); 
    } else if (paramObject instanceof byte[]) {
      OtherName otherName;
      RFC822Name rFC822Name;
      DNSName dNSName;
      X400Address x400Address;
      X500Name x500Name;
      EDIPartyName eDIPartyName;
      URIName uRIName;
      IPAddressName iPAddressName;
      DerValue derValue = new DerValue((byte[])paramObject);
      if (debug != null)
        debug
          .println("X509CertSelector.makeGeneralNameInterface() is byte[]"); 
      switch (paramInt) {
        case 0:
          otherName = new OtherName(derValue);
          break;
        case 1:
          rFC822Name = new RFC822Name(derValue);
          break;
        case 2:
          dNSName = new DNSName(derValue);
          break;
        case 3:
          x400Address = new X400Address(derValue);
          break;
        case 4:
          x500Name = new X500Name(derValue);
          break;
        case 5:
          eDIPartyName = new EDIPartyName(derValue);
          break;
        case 6:
          uRIName = new URIName(derValue);
          break;
        case 7:
          iPAddressName = new IPAddressName(derValue);
          break;
        case 8:
          oIDName = new OIDName(derValue);
          break;
        default:
          throw new IOException("unable to parse byte array names of type " + paramInt);
      } 
      if (debug != null)
        debug.println("X509CertSelector.makeGeneralNameInterface() result: " + oIDName
            .toString()); 
    } else {
      if (debug != null)
        debug.println("X509CertSelector.makeGeneralName() input name not String or byte array"); 
      throw new IOException("name not String or byte array");
    } 
    return oIDName;
  }
  
  public void setNameConstraints(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null) {
      this.ncBytes = null;
      this.nc = null;
    } else {
      this.ncBytes = (byte[])paramArrayOfbyte.clone();
      this.nc = new NameConstraintsExtension(FALSE, paramArrayOfbyte);
    } 
  }
  
  public void setBasicConstraints(int paramInt) {
    if (paramInt < -2)
      throw new IllegalArgumentException("basic constraints less than -2"); 
    this.basicConstraints = paramInt;
  }
  
  public void setPolicy(Set<String> paramSet) throws IOException {
    if (paramSet == null) {
      this.policySet = null;
      this.policy = null;
    } else {
      Set<?> set = Collections.unmodifiableSet(new HashSet(paramSet));
      Iterator<?> iterator = set.iterator();
      Vector<CertificatePolicyId> vector = new Vector();
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (!(object instanceof String))
          throw new IOException("non String in certPolicySet"); 
        vector.add(new CertificatePolicyId(new ObjectIdentifier((String)object)));
      } 
      this.policySet = (Set)set;
      this.policy = new CertificatePolicySet(vector);
    } 
  }
  
  public void setPathToNames(Collection<List<?>> paramCollection) throws IOException {
    if (paramCollection == null || paramCollection.isEmpty()) {
      this.pathToNames = null;
      this.pathToGeneralNames = null;
    } else {
      Set<List<?>> set = cloneAndCheckNames(paramCollection);
      this.pathToGeneralNames = parseNames(set);
      this.pathToNames = set;
    } 
  }
  
  void setPathToNamesInternal(Set<GeneralNameInterface> paramSet) {
    this.pathToNames = Collections.emptySet();
    this.pathToGeneralNames = paramSet;
  }
  
  public void addPathToName(int paramInt, String paramString) throws IOException {
    addPathToNameInternal(paramInt, paramString);
  }
  
  public void addPathToName(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    addPathToNameInternal(paramInt, paramArrayOfbyte.clone());
  }
  
  private void addPathToNameInternal(int paramInt, Object paramObject) throws IOException {
    GeneralNameInterface generalNameInterface = makeGeneralNameInterface(paramInt, paramObject);
    if (this.pathToGeneralNames == null) {
      this.pathToNames = new HashSet<>();
      this.pathToGeneralNames = new HashSet<>();
    } 
    ArrayList<Integer> arrayList = new ArrayList(2);
    arrayList.add(Integer.valueOf(paramInt));
    arrayList.add(paramObject);
    this.pathToNames.add(arrayList);
    this.pathToGeneralNames.add(generalNameInterface);
  }
  
  public X509Certificate getCertificate() {
    return this.x509Cert;
  }
  
  public BigInteger getSerialNumber() {
    return this.serialNumber;
  }
  
  public X500Principal getIssuer() {
    return this.issuer;
  }
  
  public String getIssuerAsString() {
    return (this.issuer == null) ? null : this.issuer.getName();
  }
  
  public byte[] getIssuerAsBytes() throws IOException {
    return (this.issuer == null) ? null : this.issuer.getEncoded();
  }
  
  public X500Principal getSubject() {
    return this.subject;
  }
  
  public String getSubjectAsString() {
    return (this.subject == null) ? null : this.subject.getName();
  }
  
  public byte[] getSubjectAsBytes() throws IOException {
    return (this.subject == null) ? null : this.subject.getEncoded();
  }
  
  public byte[] getSubjectKeyIdentifier() {
    if (this.subjectKeyID == null)
      return null; 
    return (byte[])this.subjectKeyID.clone();
  }
  
  public byte[] getAuthorityKeyIdentifier() {
    if (this.authorityKeyID == null)
      return null; 
    return (byte[])this.authorityKeyID.clone();
  }
  
  public Date getCertificateValid() {
    if (this.certificateValid == null)
      return null; 
    return (Date)this.certificateValid.clone();
  }
  
  public Date getPrivateKeyValid() {
    if (this.privateKeyValid == null)
      return null; 
    return (Date)this.privateKeyValid.clone();
  }
  
  public String getSubjectPublicKeyAlgID() {
    if (this.subjectPublicKeyAlgID == null)
      return null; 
    return this.subjectPublicKeyAlgID.toString();
  }
  
  public PublicKey getSubjectPublicKey() {
    return this.subjectPublicKey;
  }
  
  public boolean[] getKeyUsage() {
    if (this.keyUsage == null)
      return null; 
    return (boolean[])this.keyUsage.clone();
  }
  
  public Set<String> getExtendedKeyUsage() {
    return this.keyPurposeSet;
  }
  
  public boolean getMatchAllSubjectAltNames() {
    return this.matchAllSubjectAltNames;
  }
  
  public Collection<List<?>> getSubjectAlternativeNames() {
    if (this.subjectAlternativeNames == null)
      return null; 
    return cloneNames(this.subjectAlternativeNames);
  }
  
  private static Set<List<?>> cloneNames(Collection<List<?>> paramCollection) {
    try {
      return cloneAndCheckNames(paramCollection);
    } catch (IOException iOException) {
      throw new RuntimeException("cloneNames encountered IOException: " + iOException
          .getMessage());
    } 
  }
  
  private static Set<List<?>> cloneAndCheckNames(Collection<List<?>> paramCollection) throws IOException {
    HashSet<List<?>> hashSet = new HashSet();
    for (List<?> list : paramCollection)
      hashSet.add(new ArrayList(list)); 
    for (List<Object> list1 : (Iterable<List<Object>>)hashSet) {
      List<Object> list2 = list1;
      if (list2.size() != 2)
        throw new IOException("name list size not 2"); 
      Integer integer = (Integer)list2.get(0);
      if (!(integer instanceof Integer))
        throw new IOException("expected an Integer"); 
      int i = ((Integer)integer).intValue();
      if (i < 0 || i > 8)
        throw new IOException("name type not 0-8"); 
      byte[] arrayOfByte = (byte[])list2.get(1);
      if (!(arrayOfByte instanceof byte[]) && !(arrayOfByte instanceof String)) {
        if (debug != null)
          debug.println("X509CertSelector.cloneAndCheckNames() name not byte array"); 
        throw new IOException("name not byte array or String");
      } 
      if (arrayOfByte instanceof byte[])
        list2.set(1, ((byte[])arrayOfByte).clone()); 
    } 
    return hashSet;
  }
  
  public byte[] getNameConstraints() {
    if (this.ncBytes == null)
      return null; 
    return (byte[])this.ncBytes.clone();
  }
  
  public int getBasicConstraints() {
    return this.basicConstraints;
  }
  
  public Set<String> getPolicy() {
    return this.policySet;
  }
  
  public Collection<List<?>> getPathToNames() {
    if (this.pathToNames == null)
      return null; 
    return cloneNames(this.pathToNames);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("X509CertSelector: [\n");
    if (this.x509Cert != null)
      stringBuffer.append("  Certificate: " + this.x509Cert.toString() + "\n"); 
    if (this.serialNumber != null)
      stringBuffer.append("  Serial Number: " + this.serialNumber.toString() + "\n"); 
    if (this.issuer != null)
      stringBuffer.append("  Issuer: " + getIssuerAsString() + "\n"); 
    if (this.subject != null)
      stringBuffer.append("  Subject: " + getSubjectAsString() + "\n"); 
    stringBuffer.append("  matchAllSubjectAltNames flag: " + 
        String.valueOf(this.matchAllSubjectAltNames) + "\n");
    if (this.subjectAlternativeNames != null) {
      stringBuffer.append("  SubjectAlternativeNames:\n");
      Iterator<List<?>> iterator = this.subjectAlternativeNames.iterator();
      while (iterator.hasNext()) {
        List<String> list = (List)iterator.next();
        stringBuffer.append("    type " + list.get(0) + ", name " + list
            .get(1) + "\n");
      } 
    } 
    if (this.subjectKeyID != null) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      stringBuffer.append("  Subject Key Identifier: " + hexDumpEncoder
          .encodeBuffer(this.subjectKeyID) + "\n");
    } 
    if (this.authorityKeyID != null) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      stringBuffer.append("  Authority Key Identifier: " + hexDumpEncoder
          .encodeBuffer(this.authorityKeyID) + "\n");
    } 
    if (this.certificateValid != null)
      stringBuffer.append("  Certificate Valid: " + this.certificateValid
          .toString() + "\n"); 
    if (this.privateKeyValid != null)
      stringBuffer.append("  Private Key Valid: " + this.privateKeyValid
          .toString() + "\n"); 
    if (this.subjectPublicKeyAlgID != null)
      stringBuffer.append("  Subject Public Key AlgID: " + this.subjectPublicKeyAlgID
          .toString() + "\n"); 
    if (this.subjectPublicKey != null)
      stringBuffer.append("  Subject Public Key: " + this.subjectPublicKey
          .toString() + "\n"); 
    if (this.keyUsage != null)
      stringBuffer.append("  Key Usage: " + keyUsageToString(this.keyUsage) + "\n"); 
    if (this.keyPurposeSet != null)
      stringBuffer.append("  Extended Key Usage: " + this.keyPurposeSet
          .toString() + "\n"); 
    if (this.policy != null)
      stringBuffer.append("  Policy: " + this.policy.toString() + "\n"); 
    if (this.pathToGeneralNames != null) {
      stringBuffer.append("  Path to names:\n");
      Iterator<GeneralNameInterface> iterator = this.pathToGeneralNames.iterator();
      while (iterator.hasNext())
        stringBuffer.append("    " + iterator.next() + "\n"); 
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  private static String keyUsageToString(boolean[] paramArrayOfboolean) {
    String str = "KeyUsage [\n";
    try {
      if (paramArrayOfboolean[0])
        str = str + "  DigitalSignature\n"; 
      if (paramArrayOfboolean[1])
        str = str + "  Non_repudiation\n"; 
      if (paramArrayOfboolean[2])
        str = str + "  Key_Encipherment\n"; 
      if (paramArrayOfboolean[3])
        str = str + "  Data_Encipherment\n"; 
      if (paramArrayOfboolean[4])
        str = str + "  Key_Agreement\n"; 
      if (paramArrayOfboolean[5])
        str = str + "  Key_CertSign\n"; 
      if (paramArrayOfboolean[6])
        str = str + "  Crl_Sign\n"; 
      if (paramArrayOfboolean[7])
        str = str + "  Encipher_Only\n"; 
      if (paramArrayOfboolean[8])
        str = str + "  Decipher_Only\n"; 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    str = str + "]\n";
    return str;
  }
  
  private static Extension getExtensionObject(X509Certificate paramX509Certificate, int paramInt) throws IOException {
    if (paramX509Certificate instanceof X509CertImpl) {
      X509CertImpl x509CertImpl = (X509CertImpl)paramX509Certificate;
      switch (paramInt) {
        case 0:
          return x509CertImpl.getPrivateKeyUsageExtension();
        case 1:
          return x509CertImpl.getSubjectAlternativeNameExtension();
        case 2:
          return x509CertImpl.getNameConstraintsExtension();
        case 3:
          return x509CertImpl.getCertificatePoliciesExtension();
        case 4:
          return x509CertImpl.getExtendedKeyUsageExtension();
      } 
      return null;
    } 
    byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue(EXTENSION_OIDS[paramInt]);
    if (arrayOfByte1 == null)
      return null; 
    DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
    byte[] arrayOfByte2 = derInputStream.getOctetString();
    switch (paramInt) {
      case 0:
        try {
          return new PrivateKeyUsageExtension(FALSE, arrayOfByte2);
        } catch (CertificateException certificateException) {
          throw new IOException(certificateException.getMessage());
        } 
      case 1:
        return new SubjectAlternativeNameExtension(FALSE, arrayOfByte2);
      case 2:
        return new NameConstraintsExtension(FALSE, arrayOfByte2);
      case 3:
        return new CertificatePoliciesExtension(FALSE, arrayOfByte2);
      case 4:
        return new ExtendedKeyUsageExtension(FALSE, arrayOfByte2);
    } 
    return null;
  }
  
  public boolean match(Certificate paramCertificate) {
    if (!(paramCertificate instanceof X509Certificate))
      return false; 
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    if (debug != null)
      debug.println("X509CertSelector.match(SN: " + x509Certificate
          .getSerialNumber().toString(16) + "\n  Issuer: " + x509Certificate
          .getIssuerDN() + "\n  Subject: " + x509Certificate.getSubjectDN() + ")"); 
    if (this.x509Cert != null && 
      !this.x509Cert.equals(x509Certificate)) {
      if (debug != null)
        debug.println("X509CertSelector.match: certs don't match"); 
      return false;
    } 
    if (this.serialNumber != null && 
      !this.serialNumber.equals(x509Certificate.getSerialNumber())) {
      if (debug != null)
        debug.println("X509CertSelector.match: serial numbers don't match"); 
      return false;
    } 
    if (this.issuer != null && 
      !this.issuer.equals(x509Certificate.getIssuerX500Principal())) {
      if (debug != null)
        debug.println("X509CertSelector.match: issuer DNs don't match"); 
      return false;
    } 
    if (this.subject != null && 
      !this.subject.equals(x509Certificate.getSubjectX500Principal())) {
      if (debug != null)
        debug.println("X509CertSelector.match: subject DNs don't match"); 
      return false;
    } 
    if (this.certificateValid != null)
      try {
        x509Certificate.checkValidity(this.certificateValid);
      } catch (CertificateException certificateException) {
        if (debug != null)
          debug.println("X509CertSelector.match: certificate not within validity period"); 
        return false;
      }  
    if (this.subjectPublicKeyBytes != null) {
      byte[] arrayOfByte = x509Certificate.getPublicKey().getEncoded();
      if (!Arrays.equals(this.subjectPublicKeyBytes, arrayOfByte)) {
        if (debug != null)
          debug.println("X509CertSelector.match: subject public keys don't match"); 
        return false;
      } 
    } 
    boolean bool = (matchBasicConstraints(x509Certificate) && matchKeyUsage(x509Certificate) && matchExtendedKeyUsage(x509Certificate) && matchSubjectKeyID(x509Certificate) && matchAuthorityKeyID(x509Certificate) && matchPrivateKeyValid(x509Certificate) && matchSubjectPublicKeyAlgID(x509Certificate) && matchPolicy(x509Certificate) && matchSubjectAlternativeNames(x509Certificate) && matchPathToNames(x509Certificate) && matchNameConstraints(x509Certificate)) ? true : false;
    if (bool && debug != null)
      debug.println("X509CertSelector.match returning: true"); 
    return bool;
  }
  
  private boolean matchSubjectKeyID(X509Certificate paramX509Certificate) {
    if (this.subjectKeyID == null)
      return true; 
    try {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
      if (arrayOfByte1 == null) {
        if (debug != null)
          debug.println("X509CertSelector.match: no subject key ID extension"); 
        return false;
      } 
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = derInputStream.getOctetString();
      if (arrayOfByte2 == null || 
        !Arrays.equals(this.subjectKeyID, arrayOfByte2)) {
        if (debug != null)
          debug.println("X509CertSelector.match: subject key IDs don't match"); 
        return false;
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: exception in subject key ID check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchAuthorityKeyID(X509Certificate paramX509Certificate) {
    if (this.authorityKeyID == null)
      return true; 
    try {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.35");
      if (arrayOfByte1 == null) {
        if (debug != null)
          debug.println("X509CertSelector.match: no authority key ID extension"); 
        return false;
      } 
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = derInputStream.getOctetString();
      if (arrayOfByte2 == null || 
        !Arrays.equals(this.authorityKeyID, arrayOfByte2)) {
        if (debug != null)
          debug.println("X509CertSelector.match: authority key IDs don't match"); 
        return false;
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: exception in authority key ID check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchPrivateKeyValid(X509Certificate paramX509Certificate) {
    if (this.privateKeyValid == null)
      return true; 
    PrivateKeyUsageExtension privateKeyUsageExtension = null;
    try {
      privateKeyUsageExtension = (PrivateKeyUsageExtension)getExtensionObject(paramX509Certificate, 0);
      if (privateKeyUsageExtension != null)
        privateKeyUsageExtension.valid(this.privateKeyValid); 
    } catch (CertificateExpiredException certificateExpiredException) {
      if (debug != null) {
        String str = "n/a";
        try {
          Date date = privateKeyUsageExtension.get("not_after");
          str = date.toString();
        } catch (CertificateException certificateException) {}
        debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_After: " + str + "; X509CertSelector: " + 
            
            toString());
        certificateExpiredException.printStackTrace();
      } 
      return false;
    } catch (CertificateNotYetValidException certificateNotYetValidException) {
      if (debug != null) {
        String str = "n/a";
        try {
          Date date = privateKeyUsageExtension.get("not_before");
          str = date.toString();
        } catch (CertificateException certificateException) {}
        debug.println("X509CertSelector.match: private key usage not within validity date; ext.NOT_BEFORE: " + str + "; X509CertSelector: " + 
            
            toString());
        certificateNotYetValidException.printStackTrace();
      } 
      return false;
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("X509CertSelector.match: IOException in private key usage check; X509CertSelector: " + 
            
            toString());
        iOException.printStackTrace();
      } 
      return false;
    } 
    return true;
  }
  
  private boolean matchSubjectPublicKeyAlgID(X509Certificate paramX509Certificate) {
    if (this.subjectPublicKeyAlgID == null)
      return true; 
    try {
      byte[] arrayOfByte = paramX509Certificate.getPublicKey().getEncoded();
      DerValue derValue = new DerValue(arrayOfByte);
      if (derValue.tag != 48)
        throw new IOException("invalid key format"); 
      AlgorithmId algorithmId = AlgorithmId.parse(derValue.data.getDerValue());
      if (debug != null)
        debug.println("X509CertSelector.match: subjectPublicKeyAlgID = " + this.subjectPublicKeyAlgID + ", xcert subjectPublicKeyAlgID = " + algorithmId
            
            .getOID()); 
      if (!this.subjectPublicKeyAlgID.equals(algorithmId.getOID())) {
        if (debug != null)
          debug.println("X509CertSelector.match: subject public key alg IDs don't match"); 
        return false;
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in subject public key algorithm OID check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchKeyUsage(X509Certificate paramX509Certificate) {
    if (this.keyUsage == null)
      return true; 
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if (arrayOfBoolean != null)
      for (byte b = 0; b < this.keyUsage.length; b++) {
        if (this.keyUsage[b] && (b >= arrayOfBoolean.length || !arrayOfBoolean[b])) {
          if (debug != null)
            debug.println("X509CertSelector.match: key usage bits don't match"); 
          return false;
        } 
      }  
    return true;
  }
  
  private boolean matchExtendedKeyUsage(X509Certificate paramX509Certificate) {
    if (this.keyPurposeSet == null || this.keyPurposeSet.isEmpty())
      return true; 
    try {
      ExtendedKeyUsageExtension extendedKeyUsageExtension = (ExtendedKeyUsageExtension)getExtensionObject(paramX509Certificate, 4);
      if (extendedKeyUsageExtension != null) {
        Vector<ObjectIdentifier> vector = extendedKeyUsageExtension.get("usages");
        if (!vector.contains(ANY_EXTENDED_KEY_USAGE) && 
          !vector.containsAll(this.keyPurposeOIDSet)) {
          if (debug != null)
            debug.println("X509CertSelector.match: cert failed extendedKeyUsage criterion"); 
          return false;
        } 
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in extended key usage check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchSubjectAlternativeNames(X509Certificate paramX509Certificate) {
    if (this.subjectAlternativeNames == null || this.subjectAlternativeNames.isEmpty())
      return true; 
    try {
      SubjectAlternativeNameExtension subjectAlternativeNameExtension = (SubjectAlternativeNameExtension)getExtensionObject(paramX509Certificate, 1);
      if (subjectAlternativeNameExtension == null) {
        if (debug != null)
          debug.println("X509CertSelector.match: no subject alternative name extension"); 
        return false;
      } 
      GeneralNames generalNames = subjectAlternativeNameExtension.get("subject_name");
      Iterator<GeneralNameInterface> iterator = this.subjectAlternativeGeneralNames.iterator();
      while (iterator.hasNext()) {
        GeneralNameInterface generalNameInterface = iterator.next();
        boolean bool = false;
        Iterator<GeneralName> iterator1 = generalNames.iterator();
        while (iterator1.hasNext() && !bool) {
          GeneralNameInterface generalNameInterface1 = ((GeneralName)iterator1.next()).getName();
          bool = generalNameInterface1.equals(generalNameInterface);
        } 
        if (!bool && (this.matchAllSubjectAltNames || !iterator.hasNext())) {
          if (debug != null)
            debug.println("X509CertSelector.match: subject alternative name " + generalNameInterface + " not found"); 
          return false;
        } 
        if (bool && !this.matchAllSubjectAltNames)
          break; 
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in subject alternative name check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchNameConstraints(X509Certificate paramX509Certificate) {
    if (this.nc == null)
      return true; 
    try {
      if (!this.nc.verify(paramX509Certificate)) {
        if (debug != null)
          debug.println("X509CertSelector.match: name constraints not satisfied"); 
        return false;
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in name constraints check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchPolicy(X509Certificate paramX509Certificate) {
    if (this.policy == null)
      return true; 
    try {
      CertificatePoliciesExtension certificatePoliciesExtension = (CertificatePoliciesExtension)getExtensionObject(paramX509Certificate, 3);
      if (certificatePoliciesExtension == null) {
        if (debug != null)
          debug.println("X509CertSelector.match: no certificate policy extension"); 
        return false;
      } 
      List<PolicyInformation> list = certificatePoliciesExtension.get("policies");
      ArrayList<CertificatePolicyId> arrayList = new ArrayList(list.size());
      for (PolicyInformation policyInformation : list)
        arrayList.add(policyInformation.getPolicyIdentifier()); 
      if (this.policy != null) {
        boolean bool = false;
        if (this.policy.getCertPolicyIds().isEmpty()) {
          if (arrayList.isEmpty()) {
            if (debug != null)
              debug.println("X509CertSelector.match: cert failed policyAny criterion"); 
            return false;
          } 
        } else {
          for (CertificatePolicyId certificatePolicyId : this.policy.getCertPolicyIds()) {
            if (arrayList.contains(certificatePolicyId)) {
              bool = true;
              break;
            } 
          } 
          if (!bool) {
            if (debug != null)
              debug.println("X509CertSelector.match: cert failed policyAny criterion"); 
            return false;
          } 
        } 
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in certificate policy ID check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchPathToNames(X509Certificate paramX509Certificate) {
    if (this.pathToGeneralNames == null)
      return true; 
    try {
      NameConstraintsExtension nameConstraintsExtension = (NameConstraintsExtension)getExtensionObject(paramX509Certificate, 2);
      if (nameConstraintsExtension == null)
        return true; 
      if (debug != null && Debug.isOn("certpath")) {
        debug.println("X509CertSelector.match pathToNames:\n");
        Iterator<GeneralNameInterface> iterator = this.pathToGeneralNames.iterator();
        while (iterator.hasNext())
          debug.println("    " + iterator.next() + "\n"); 
      } 
      GeneralSubtrees generalSubtrees1 = nameConstraintsExtension.get("permitted_subtrees");
      GeneralSubtrees generalSubtrees2 = nameConstraintsExtension.get("excluded_subtrees");
      if (generalSubtrees2 != null && 
        !matchExcluded(generalSubtrees2))
        return false; 
      if (generalSubtrees1 != null && 
        !matchPermitted(generalSubtrees1))
        return false; 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("X509CertSelector.match: IOException in name constraints check"); 
      return false;
    } 
    return true;
  }
  
  private boolean matchExcluded(GeneralSubtrees paramGeneralSubtrees) {
    for (GeneralSubtree generalSubtree : paramGeneralSubtrees) {
      GeneralNameInterface generalNameInterface = generalSubtree.getName().getName();
      Iterator<GeneralNameInterface> iterator = this.pathToGeneralNames.iterator();
      while (iterator.hasNext()) {
        GeneralNameInterface generalNameInterface1 = iterator.next();
        if (generalNameInterface.getType() == generalNameInterface1.getType())
          switch (generalNameInterface1.constrains(generalNameInterface)) {
            case 0:
            case 2:
              if (debug != null) {
                debug.println("X509CertSelector.match: name constraints inhibit path to specified name");
                debug.println("X509CertSelector.match: excluded name: " + generalNameInterface1);
              } 
              return false;
          }  
      } 
    } 
    return true;
  }
  
  private boolean matchPermitted(GeneralSubtrees paramGeneralSubtrees) {
    Iterator<GeneralNameInterface> iterator = this.pathToGeneralNames.iterator();
    while (iterator.hasNext()) {
      GeneralNameInterface generalNameInterface = iterator.next();
      Iterator<GeneralSubtree> iterator1 = paramGeneralSubtrees.iterator();
      boolean bool1 = false;
      boolean bool2 = false;
      String str = "";
      while (iterator1.hasNext() && !bool1) {
        GeneralSubtree generalSubtree = iterator1.next();
        GeneralNameInterface generalNameInterface1 = generalSubtree.getName().getName();
        if (generalNameInterface1.getType() == generalNameInterface.getType()) {
          bool2 = true;
          str = str + "  " + generalNameInterface1;
          switch (generalNameInterface.constrains(generalNameInterface1)) {
            case 0:
            case 2:
              bool1 = true;
          } 
        } 
      } 
      if (!bool1 && bool2) {
        if (debug != null)
          debug.println("X509CertSelector.match: name constraints inhibit path to specified name; permitted names of type " + generalNameInterface
              
              .getType() + ": " + str); 
        return false;
      } 
    } 
    return true;
  }
  
  private boolean matchBasicConstraints(X509Certificate paramX509Certificate) {
    if (this.basicConstraints == -1)
      return true; 
    int i = paramX509Certificate.getBasicConstraints();
    if (this.basicConstraints == -2) {
      if (i != -1) {
        if (debug != null)
          debug.println("X509CertSelector.match: not an EE cert"); 
        return false;
      } 
    } else if (i < this.basicConstraints) {
      if (debug != null)
        debug.println("X509CertSelector.match: maxPathLen too small (" + i + " < " + this.basicConstraints + ")"); 
      return false;
    } 
    return true;
  }
  
  private static <T> Set<T> cloneSet(Set<T> paramSet) {
    if (paramSet instanceof HashSet) {
      Object object = ((HashSet)paramSet).clone();
      return (Set<T>)object;
    } 
    return new HashSet<>(paramSet);
  }
  
  public Object clone() {
    try {
      X509CertSelector x509CertSelector = (X509CertSelector)super.clone();
      if (this.subjectAlternativeNames != null) {
        x509CertSelector
          .subjectAlternativeNames = cloneSet(this.subjectAlternativeNames);
        x509CertSelector
          .subjectAlternativeGeneralNames = cloneSet(this.subjectAlternativeGeneralNames);
      } 
      if (this.pathToGeneralNames != null) {
        x509CertSelector.pathToNames = cloneSet(this.pathToNames);
        x509CertSelector.pathToGeneralNames = cloneSet(this.pathToGeneralNames);
      } 
      return x509CertSelector;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
}
