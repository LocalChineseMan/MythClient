package sun.security.pkcs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import sun.security.timestamp.HttpTimestamper;
import sun.security.timestamp.TSRequest;
import sun.security.timestamp.TSResponse;
import sun.security.timestamp.TimestampToken;
import sun.security.timestamp.Timestamper;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class PKCS7 {
  private ObjectIdentifier contentType;
  
  private BigInteger version = null;
  
  private AlgorithmId[] digestAlgorithmIds = null;
  
  private ContentInfo contentInfo = null;
  
  private X509Certificate[] certificates = null;
  
  private X509CRL[] crls = null;
  
  private SignerInfo[] signerInfos = null;
  
  private boolean oldStyle = false;
  
  private Principal[] certIssuerNames;
  
  private static final String KP_TIMESTAMPING_OID = "1.3.6.1.5.5.7.3.8";
  
  private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
  
  public PKCS7(InputStream paramInputStream) throws ParsingException, IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    byte[] arrayOfByte = new byte[dataInputStream.available()];
    dataInputStream.readFully(arrayOfByte);
    parse(new DerInputStream(arrayOfByte));
  }
  
  public PKCS7(DerInputStream paramDerInputStream) throws ParsingException {
    parse(paramDerInputStream);
  }
  
  public PKCS7(byte[] paramArrayOfbyte) throws ParsingException {
    try {
      DerInputStream derInputStream = new DerInputStream(paramArrayOfbyte);
      parse(derInputStream);
    } catch (IOException iOException) {
      ParsingException parsingException = new ParsingException("Unable to parse the encoded bytes");
      parsingException.initCause(iOException);
      throw parsingException;
    } 
  }
  
  private void parse(DerInputStream paramDerInputStream) throws ParsingException {
    try {
      paramDerInputStream.mark(paramDerInputStream.available());
      parse(paramDerInputStream, false);
    } catch (IOException iOException) {
      try {
        paramDerInputStream.reset();
        parse(paramDerInputStream, true);
        this.oldStyle = true;
      } catch (IOException iOException1) {
        ParsingException parsingException = new ParsingException(iOException1.getMessage());
        parsingException.initCause(iOException);
        parsingException.addSuppressed(iOException1);
        throw parsingException;
      } 
    } 
  }
  
  private void parse(DerInputStream paramDerInputStream, boolean paramBoolean) throws IOException {
    this.contentInfo = new ContentInfo(paramDerInputStream, paramBoolean);
    this.contentType = this.contentInfo.contentType;
    DerValue derValue = this.contentInfo.getContent();
    if (this.contentType.equals(ContentInfo.SIGNED_DATA_OID)) {
      parseSignedData(derValue);
    } else if (this.contentType.equals(ContentInfo.OLD_SIGNED_DATA_OID)) {
      parseOldSignedData(derValue);
    } else if (this.contentType.equals(ContentInfo.NETSCAPE_CERT_SEQUENCE_OID)) {
      parseNetscapeCertChain(derValue);
    } else {
      throw new ParsingException("content type " + this.contentType + " not supported.");
    } 
  }
  
  public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, X509CRL[] paramArrayOfX509CRL, SignerInfo[] paramArrayOfSignerInfo) {
    this.version = BigInteger.ONE;
    this.digestAlgorithmIds = paramArrayOfAlgorithmId;
    this.contentInfo = paramContentInfo;
    this.certificates = paramArrayOfX509Certificate;
    this.crls = paramArrayOfX509CRL;
    this.signerInfos = paramArrayOfSignerInfo;
  }
  
  public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, SignerInfo[] paramArrayOfSignerInfo) {
    this(paramArrayOfAlgorithmId, paramContentInfo, paramArrayOfX509Certificate, null, paramArrayOfSignerInfo);
  }
  
  private void parseNetscapeCertChain(DerValue paramDerValue) throws ParsingException, IOException {
    DerInputStream derInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
    this.certificates = new X509Certificate[arrayOfDerValue.length];
    CertificateFactory certificateFactory = null;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {}
    for (byte b = 0; b < arrayOfDerValue.length; b++)
      ByteArrayInputStream byteArrayInputStream = null; 
  }
  
  private void parseSignedData(DerValue paramDerValue) throws ParsingException, IOException {
    DerInputStream derInputStream = paramDerValue.toDerInputStream();
    this.version = derInputStream.getBigInteger();
    DerValue[] arrayOfDerValue1 = derInputStream.getSet(1);
    int i = arrayOfDerValue1.length;
    this.digestAlgorithmIds = new AlgorithmId[i];
    try {
      for (byte b1 = 0; b1 < i; b1++) {
        DerValue derValue = arrayOfDerValue1[b1];
        this.digestAlgorithmIds[b1] = AlgorithmId.parse(derValue);
      } 
    } catch (IOException iOException) {
      ParsingException parsingException = new ParsingException("Error parsing digest AlgorithmId IDs: " + iOException.getMessage());
      parsingException.initCause(iOException);
      throw parsingException;
    } 
    this.contentInfo = new ContentInfo(derInputStream);
    CertificateFactory certificateFactory = null;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {}
    if ((byte)derInputStream.peekByte() == -96) {
      DerValue[] arrayOfDerValue = derInputStream.getSet(2, true);
      i = arrayOfDerValue.length;
      this.certificates = new X509Certificate[i];
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++)
        ByteArrayInputStream byteArrayInputStream = null; 
      if (b1 != i)
        this.certificates = Arrays.<X509Certificate>copyOf(this.certificates, b1); 
    } 
    if ((byte)derInputStream.peekByte() == -95) {
      DerValue[] arrayOfDerValue = derInputStream.getSet(1, true);
      i = arrayOfDerValue.length;
      this.crls = new X509CRL[i];
      for (byte b1 = 0; b1 < i; b1++)
        ByteArrayInputStream byteArrayInputStream = null; 
    } 
    DerValue[] arrayOfDerValue2 = derInputStream.getSet(1);
    i = arrayOfDerValue2.length;
    this.signerInfos = new SignerInfo[i];
    for (byte b = 0; b < i; b++) {
      DerInputStream derInputStream1 = arrayOfDerValue2[b].toDerInputStream();
      this.signerInfos[b] = new SignerInfo(derInputStream1);
    } 
  }
  
  private void parseOldSignedData(DerValue paramDerValue) throws ParsingException, IOException {
    DerInputStream derInputStream = paramDerValue.toDerInputStream();
    this.version = derInputStream.getBigInteger();
    DerValue[] arrayOfDerValue1 = derInputStream.getSet(1);
    int i = arrayOfDerValue1.length;
    this.digestAlgorithmIds = new AlgorithmId[i];
    try {
      for (byte b = 0; b < i; b++) {
        DerValue derValue = arrayOfDerValue1[b];
        this.digestAlgorithmIds[b] = AlgorithmId.parse(derValue);
      } 
    } catch (IOException iOException) {
      throw new ParsingException("Error parsing digest AlgorithmId IDs");
    } 
    this.contentInfo = new ContentInfo(derInputStream, true);
    CertificateFactory certificateFactory = null;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {}
    DerValue[] arrayOfDerValue2 = derInputStream.getSet(2);
    i = arrayOfDerValue2.length;
    this.certificates = new X509Certificate[i];
    for (byte b1 = 0; b1 < i; b1++)
      ByteArrayInputStream byteArrayInputStream = null; 
    derInputStream.getSet(0);
    DerValue[] arrayOfDerValue3 = derInputStream.getSet(1);
    i = arrayOfDerValue3.length;
    this.signerInfos = new SignerInfo[i];
    for (byte b2 = 0; b2 < i; b2++) {
      DerInputStream derInputStream1 = arrayOfDerValue3[b2].toDerInputStream();
      this.signerInfos[b2] = new SignerInfo(derInputStream1, true);
    } 
  }
  
  public void encodeSignedData(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    encodeSignedData(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void encodeSignedData(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putInteger(this.version);
    derOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])this.digestAlgorithmIds);
    this.contentInfo.encode(derOutputStream);
    if (this.certificates != null && this.certificates.length != 0) {
      X509CertImpl[] arrayOfX509CertImpl = new X509CertImpl[this.certificates.length];
      for (byte b = 0; b < this.certificates.length; b++) {
        if (this.certificates[b] instanceof X509CertImpl) {
          arrayOfX509CertImpl[b] = (X509CertImpl)this.certificates[b];
        } else {
          try {
            byte[] arrayOfByte = this.certificates[b].getEncoded();
            arrayOfX509CertImpl[b] = new X509CertImpl(arrayOfByte);
          } catch (CertificateException certificateException) {
            throw new IOException(certificateException);
          } 
        } 
      } 
      derOutputStream.putOrderedSetOf((byte)-96, (DerEncoder[])arrayOfX509CertImpl);
    } 
    if (this.crls != null && this.crls.length != 0) {
      HashSet<X509CRLImpl> hashSet = new HashSet(this.crls.length);
      for (X509CRL x509CRL : this.crls) {
        if (x509CRL instanceof X509CRLImpl) {
          hashSet.add((X509CRLImpl)x509CRL);
        } else {
          try {
            byte[] arrayOfByte = x509CRL.getEncoded();
            hashSet.add(new X509CRLImpl(arrayOfByte));
          } catch (CRLException cRLException) {
            throw new IOException(cRLException);
          } 
        } 
      } 
      derOutputStream.putOrderedSetOf((byte)-95, hashSet
          .<DerEncoder>toArray((DerEncoder[])new X509CRLImpl[hashSet.size()]));
    } 
    derOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])this.signerInfos);
    DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
    ContentInfo contentInfo = new ContentInfo(ContentInfo.SIGNED_DATA_OID, derValue);
    contentInfo.encode(paramDerOutputStream);
  }
  
  public SignerInfo verify(SignerInfo paramSignerInfo, byte[] paramArrayOfbyte) throws NoSuchAlgorithmException, SignatureException {
    return paramSignerInfo.verify(this, paramArrayOfbyte);
  }
  
  public SignerInfo[] verify(byte[] paramArrayOfbyte) throws NoSuchAlgorithmException, SignatureException {
    Vector<SignerInfo> vector = new Vector();
    for (byte b = 0; b < this.signerInfos.length; b++) {
      SignerInfo signerInfo = verify(this.signerInfos[b], paramArrayOfbyte);
      if (signerInfo != null)
        vector.addElement(signerInfo); 
    } 
    if (!vector.isEmpty()) {
      SignerInfo[] arrayOfSignerInfo = new SignerInfo[vector.size()];
      vector.copyInto((Object[])arrayOfSignerInfo);
      return arrayOfSignerInfo;
    } 
    return null;
  }
  
  public SignerInfo[] verify() throws NoSuchAlgorithmException, SignatureException {
    return verify(null);
  }
  
  public BigInteger getVersion() {
    return this.version;
  }
  
  public AlgorithmId[] getDigestAlgorithmIds() {
    return this.digestAlgorithmIds;
  }
  
  public ContentInfo getContentInfo() {
    return this.contentInfo;
  }
  
  public X509Certificate[] getCertificates() {
    if (this.certificates != null)
      return (X509Certificate[])this.certificates.clone(); 
    return null;
  }
  
  public X509CRL[] getCRLs() {
    if (this.crls != null)
      return (X509CRL[])this.crls.clone(); 
    return null;
  }
  
  public SignerInfo[] getSignerInfos() {
    return this.signerInfos;
  }
  
  public X509Certificate getCertificate(BigInteger paramBigInteger, X500Name paramX500Name) {
    if (this.certificates != null) {
      if (this.certIssuerNames == null)
        populateCertIssuerNames(); 
      for (byte b = 0; b < this.certificates.length; b++) {
        X509Certificate x509Certificate = this.certificates[b];
        BigInteger bigInteger = x509Certificate.getSerialNumber();
        if (paramBigInteger.equals(bigInteger) && paramX500Name
          .equals(this.certIssuerNames[b]))
          return x509Certificate; 
      } 
    } 
    return null;
  }
  
  private void populateCertIssuerNames() {
    if (this.certificates == null)
      return; 
    this.certIssuerNames = new Principal[this.certificates.length];
    for (byte b = 0; b < this.certificates.length; b++) {
      X509Certificate x509Certificate = this.certificates[b];
      Principal principal = x509Certificate.getIssuerDN();
      if (!(principal instanceof X500Name))
        try {
          X509CertInfo x509CertInfo = new X509CertInfo(x509Certificate.getTBSCertificate());
          principal = (Principal)x509CertInfo.get("issuer.dname");
        } catch (Exception exception) {} 
      this.certIssuerNames[b] = principal;
    } 
  }
  
  public String toString() {
    String str = "";
    str = str + this.contentInfo + "\n";
    if (this.version != null)
      str = str + "PKCS7 :: version: " + Debug.toHexString(this.version) + "\n"; 
    if (this.digestAlgorithmIds != null) {
      str = str + "PKCS7 :: digest AlgorithmIds: \n";
      for (byte b = 0; b < this.digestAlgorithmIds.length; b++)
        str = str + "\t" + this.digestAlgorithmIds[b] + "\n"; 
    } 
    if (this.certificates != null) {
      str = str + "PKCS7 :: certificates: \n";
      for (byte b = 0; b < this.certificates.length; b++)
        str = str + "\t" + b + ".   " + this.certificates[b] + "\n"; 
    } 
    if (this.crls != null) {
      str = str + "PKCS7 :: crls: \n";
      for (byte b = 0; b < this.crls.length; b++)
        str = str + "\t" + b + ".   " + this.crls[b] + "\n"; 
    } 
    if (this.signerInfos != null) {
      str = str + "PKCS7 :: signer infos: \n";
      for (byte b = 0; b < this.signerInfos.length; b++)
        str = str + "\t" + b + ".  " + this.signerInfos[b] + "\n"; 
    } 
    return str;
  }
  
  public boolean isOldStyle() {
    return this.oldStyle;
  }
  
  public static byte[] generateSignedData(byte[] paramArrayOfbyte1, X509Certificate[] paramArrayOfX509Certificate, byte[] paramArrayOfbyte2, String paramString1, URI paramURI, String paramString2) throws CertificateException, IOException, NoSuchAlgorithmException {
    PKCS9Attributes pKCS9Attributes = null;
    if (paramURI != null) {
      HttpTimestamper httpTimestamper = new HttpTimestamper(paramURI);
      byte[] arrayOfByte = generateTimestampToken(httpTimestamper, paramString2, paramArrayOfbyte1);
      pKCS9Attributes = new PKCS9Attributes(new PKCS9Attribute[] { new PKCS9Attribute("SignatureTimestampToken", arrayOfByte) });
    } 
    X500Name x500Name = X500Name.asX500Name(paramArrayOfX509Certificate[0].getIssuerX500Principal());
    BigInteger bigInteger = paramArrayOfX509Certificate[0].getSerialNumber();
    String str1 = AlgorithmId.getEncAlgFromSigAlg(paramString1);
    String str2 = AlgorithmId.getDigAlgFromSigAlg(paramString1);
    SignerInfo signerInfo = new SignerInfo(x500Name, bigInteger, AlgorithmId.get(str2), null, AlgorithmId.get(str1), paramArrayOfbyte1, pKCS9Attributes);
    SignerInfo[] arrayOfSignerInfo = { signerInfo };
    AlgorithmId[] arrayOfAlgorithmId = { signerInfo.getDigestAlgorithmId() };
    ContentInfo contentInfo = (paramArrayOfbyte2 == null) ? new ContentInfo(ContentInfo.DATA_OID, null) : new ContentInfo(paramArrayOfbyte2);
    PKCS7 pKCS7 = new PKCS7(arrayOfAlgorithmId, contentInfo, paramArrayOfX509Certificate, arrayOfSignerInfo);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    pKCS7.encodeSignedData(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  private static byte[] generateTimestampToken(Timestamper paramTimestamper, String paramString, byte[] paramArrayOfbyte) throws IOException, CertificateException {
    MessageDigest messageDigest = null;
    TSRequest tSRequest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA-1");
      tSRequest = new TSRequest(paramString, paramArrayOfbyte, messageDigest);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {}
    BigInteger bigInteger1 = null;
    if (SecureRandomHolder.RANDOM != null) {
      bigInteger1 = new BigInteger(64, SecureRandomHolder.RANDOM);
      tSRequest.setNonce(bigInteger1);
    } 
    tSRequest.requestCertificate(true);
    TSResponse tSResponse = paramTimestamper.generateTimestamp(tSRequest);
    int i = tSResponse.getStatusCode();
    if (i != 0 && i != 1)
      throw new IOException("Error generating timestamp: " + tSResponse
          .getStatusCodeAsText() + " " + tSResponse
          .getFailureCodeAsText()); 
    if (paramString != null && 
      !paramString.equals(tSResponse.getTimestampToken().getPolicyID()))
      throw new IOException("TSAPolicyID changed in timestamp token"); 
    PKCS7 pKCS7 = tSResponse.getToken();
    TimestampToken timestampToken = tSResponse.getTimestampToken();
    if (!timestampToken.getHashAlgorithm().getName().equals("SHA-1"))
      throw new IOException("Digest algorithm not SHA-1 in timestamp token"); 
    if (!MessageDigest.isEqual(timestampToken.getHashedMessage(), tSRequest
        .getHashedMessage()))
      throw new IOException("Digest octets changed in timestamp token"); 
    BigInteger bigInteger2 = timestampToken.getNonce();
    if (bigInteger2 == null && bigInteger1 != null)
      throw new IOException("Nonce missing in timestamp token"); 
    if (bigInteger2 != null && !bigInteger2.equals(bigInteger1))
      throw new IOException("Nonce changed in timestamp token"); 
    for (SignerInfo signerInfo : pKCS7.getSignerInfos()) {
      X509Certificate x509Certificate = signerInfo.getCertificate(pKCS7);
      if (x509Certificate == null)
        throw new CertificateException("Certificate not included in timestamp token"); 
      if (!x509Certificate.getCriticalExtensionOIDs().contains("2.5.29.37"))
        throw new CertificateException("Certificate is not valid for timestamping"); 
      List<String> list = x509Certificate.getExtendedKeyUsage();
      if (list == null || 
        !list.contains("1.3.6.1.5.5.7.3.8"))
        throw new CertificateException("Certificate is not valid for timestamping"); 
    } 
    return tSResponse.getEncodedToken();
  }
  
  private static class PKCS7 {}
}
