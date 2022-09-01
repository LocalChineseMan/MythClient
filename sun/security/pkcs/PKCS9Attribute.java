package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.CertificateExtensions;

public class PKCS9Attribute implements DerEncoder {
  private static final Debug debug = Debug.getInstance("jar");
  
  static final ObjectIdentifier[] PKCS9_OIDS = new ObjectIdentifier[18];
  
  private static final Class<?> BYTE_ARRAY_CLASS;
  
  static {
    for (byte b = 1; b < PKCS9_OIDS.length - 2; b++) {
      PKCS9_OIDS[b] = 
        ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, b });
    } 
    PKCS9_OIDS[PKCS9_OIDS.length - 2] = 
      ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 12 });
    PKCS9_OIDS[PKCS9_OIDS.length - 1] = 
      ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 14 });
    try {
      BYTE_ARRAY_CLASS = Class.forName("[B");
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ExceptionInInitializerError(classNotFoundException.toString());
    } 
  }
  
  public static final ObjectIdentifier EMAIL_ADDRESS_OID = PKCS9_OIDS[1];
  
  public static final ObjectIdentifier UNSTRUCTURED_NAME_OID = PKCS9_OIDS[2];
  
  public static final ObjectIdentifier CONTENT_TYPE_OID = PKCS9_OIDS[3];
  
  public static final ObjectIdentifier MESSAGE_DIGEST_OID = PKCS9_OIDS[4];
  
  public static final ObjectIdentifier SIGNING_TIME_OID = PKCS9_OIDS[5];
  
  public static final ObjectIdentifier COUNTERSIGNATURE_OID = PKCS9_OIDS[6];
  
  public static final ObjectIdentifier CHALLENGE_PASSWORD_OID = PKCS9_OIDS[7];
  
  public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID = PKCS9_OIDS[8];
  
  public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID = PKCS9_OIDS[9];
  
  public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID = PKCS9_OIDS[10];
  
  public static final ObjectIdentifier EXTENSION_REQUEST_OID = PKCS9_OIDS[14];
  
  public static final ObjectIdentifier SMIME_CAPABILITY_OID = PKCS9_OIDS[15];
  
  public static final ObjectIdentifier SIGNING_CERTIFICATE_OID = PKCS9_OIDS[16];
  
  public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID = PKCS9_OIDS[17];
  
  public static final String EMAIL_ADDRESS_STR = "EmailAddress";
  
  public static final String UNSTRUCTURED_NAME_STR = "UnstructuredName";
  
  public static final String CONTENT_TYPE_STR = "ContentType";
  
  public static final String MESSAGE_DIGEST_STR = "MessageDigest";
  
  public static final String SIGNING_TIME_STR = "SigningTime";
  
  public static final String COUNTERSIGNATURE_STR = "Countersignature";
  
  public static final String CHALLENGE_PASSWORD_STR = "ChallengePassword";
  
  public static final String UNSTRUCTURED_ADDRESS_STR = "UnstructuredAddress";
  
  public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR = "ExtendedCertificateAttributes";
  
  public static final String ISSUER_SERIALNUMBER_STR = "IssuerAndSerialNumber";
  
  private static final String RSA_PROPRIETARY_STR = "RSAProprietary";
  
  private static final String SMIME_SIGNING_DESC_STR = "SMIMESigningDesc";
  
  public static final String EXTENSION_REQUEST_STR = "ExtensionRequest";
  
  public static final String SMIME_CAPABILITY_STR = "SMIMECapability";
  
  public static final String SIGNING_CERTIFICATE_STR = "SigningCertificate";
  
  public static final String SIGNATURE_TIMESTAMP_TOKEN_STR = "SignatureTimestampToken";
  
  private static final Hashtable<String, ObjectIdentifier> NAME_OID_TABLE = new Hashtable<>(18);
  
  static {
    NAME_OID_TABLE.put("emailaddress", PKCS9_OIDS[1]);
    NAME_OID_TABLE.put("unstructuredname", PKCS9_OIDS[2]);
    NAME_OID_TABLE.put("contenttype", PKCS9_OIDS[3]);
    NAME_OID_TABLE.put("messagedigest", PKCS9_OIDS[4]);
    NAME_OID_TABLE.put("signingtime", PKCS9_OIDS[5]);
    NAME_OID_TABLE.put("countersignature", PKCS9_OIDS[6]);
    NAME_OID_TABLE.put("challengepassword", PKCS9_OIDS[7]);
    NAME_OID_TABLE.put("unstructuredaddress", PKCS9_OIDS[8]);
    NAME_OID_TABLE.put("extendedcertificateattributes", PKCS9_OIDS[9]);
    NAME_OID_TABLE.put("issuerandserialnumber", PKCS9_OIDS[10]);
    NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[11]);
    NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[12]);
    NAME_OID_TABLE.put("signingdescription", PKCS9_OIDS[13]);
    NAME_OID_TABLE.put("extensionrequest", PKCS9_OIDS[14]);
    NAME_OID_TABLE.put("smimecapability", PKCS9_OIDS[15]);
    NAME_OID_TABLE.put("signingcertificate", PKCS9_OIDS[16]);
    NAME_OID_TABLE.put("signaturetimestamptoken", PKCS9_OIDS[17]);
  }
  
  private static final Hashtable<ObjectIdentifier, String> OID_NAME_TABLE = new Hashtable<>(16);
  
  static {
    OID_NAME_TABLE.put(PKCS9_OIDS[1], "EmailAddress");
    OID_NAME_TABLE.put(PKCS9_OIDS[2], "UnstructuredName");
    OID_NAME_TABLE.put(PKCS9_OIDS[3], "ContentType");
    OID_NAME_TABLE.put(PKCS9_OIDS[4], "MessageDigest");
    OID_NAME_TABLE.put(PKCS9_OIDS[5], "SigningTime");
    OID_NAME_TABLE.put(PKCS9_OIDS[6], "Countersignature");
    OID_NAME_TABLE.put(PKCS9_OIDS[7], "ChallengePassword");
    OID_NAME_TABLE.put(PKCS9_OIDS[8], "UnstructuredAddress");
    OID_NAME_TABLE.put(PKCS9_OIDS[9], "ExtendedCertificateAttributes");
    OID_NAME_TABLE.put(PKCS9_OIDS[10], "IssuerAndSerialNumber");
    OID_NAME_TABLE.put(PKCS9_OIDS[11], "RSAProprietary");
    OID_NAME_TABLE.put(PKCS9_OIDS[12], "RSAProprietary");
    OID_NAME_TABLE.put(PKCS9_OIDS[13], "SMIMESigningDesc");
    OID_NAME_TABLE.put(PKCS9_OIDS[14], "ExtensionRequest");
    OID_NAME_TABLE.put(PKCS9_OIDS[15], "SMIMECapability");
    OID_NAME_TABLE.put(PKCS9_OIDS[16], "SigningCertificate");
    OID_NAME_TABLE.put(PKCS9_OIDS[17], "SignatureTimestampToken");
  }
  
  private static final Byte[][] PKCS9_VALUE_TAGS = new Byte[][] { 
      null, { new Byte((byte)22) }, { new Byte((byte)22), new Byte((byte)19) }, { new Byte((byte)6) }, { new Byte((byte)4) }, { new Byte((byte)23) }, { new Byte((byte)48) }, { new Byte((byte)19), new Byte((byte)20) }, { new Byte((byte)19), new Byte((byte)20) }, { new Byte((byte)49) }, 
      { new Byte((byte)48) }, null, null, null, { new Byte((byte)48) }, { new Byte((byte)48) }, { new Byte((byte)48) }, { new Byte((byte)48) } };
  
  private static final Class<?>[] VALUE_CLASSES = new Class[18];
  
  static {
    try {
      Class<?> clazz = Class.forName("[Ljava.lang.String;");
      VALUE_CLASSES[0] = null;
      VALUE_CLASSES[1] = clazz;
      VALUE_CLASSES[2] = clazz;
      VALUE_CLASSES[3] = 
        Class.forName("sun.security.util.ObjectIdentifier");
      VALUE_CLASSES[4] = BYTE_ARRAY_CLASS;
      VALUE_CLASSES[5] = Class.forName("java.util.Date");
      VALUE_CLASSES[6] = 
        Class.forName("[Lsun.security.pkcs.SignerInfo;");
      VALUE_CLASSES[7] = 
        Class.forName("java.lang.String");
      VALUE_CLASSES[8] = clazz;
      VALUE_CLASSES[9] = null;
      VALUE_CLASSES[10] = null;
      VALUE_CLASSES[11] = null;
      VALUE_CLASSES[12] = null;
      VALUE_CLASSES[13] = null;
      VALUE_CLASSES[14] = 
        Class.forName("sun.security.x509.CertificateExtensions");
      VALUE_CLASSES[15] = null;
      VALUE_CLASSES[16] = null;
      VALUE_CLASSES[17] = BYTE_ARRAY_CLASS;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ExceptionInInitializerError(classNotFoundException.toString());
    } 
  }
  
  private static final boolean[] SINGLE_VALUED = new boolean[] { 
      false, false, false, true, true, true, false, true, false, false, 
      true, false, false, false, true, true, true, true };
  
  private ObjectIdentifier oid;
  
  private int index;
  
  private Object value;
  
  public PKCS9Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject) throws IllegalArgumentException {
    init(paramObjectIdentifier, paramObject);
  }
  
  public PKCS9Attribute(String paramString, Object paramObject) throws IllegalArgumentException {
    ObjectIdentifier objectIdentifier = getOID(paramString);
    if (objectIdentifier == null)
      throw new IllegalArgumentException("Unrecognized attribute name " + paramString + " constructing PKCS9Attribute."); 
    init(objectIdentifier, paramObject);
  }
  
  private void init(ObjectIdentifier paramObjectIdentifier, Object paramObject) throws IllegalArgumentException {
    this.oid = paramObjectIdentifier;
    this.index = indexOf(paramObjectIdentifier, (Object[])PKCS9_OIDS, 1);
    Class<?> clazz = (this.index == -1) ? BYTE_ARRAY_CLASS : VALUE_CLASSES[this.index];
    if (!clazz.isInstance(paramObject))
      throw new IllegalArgumentException("Wrong value class  for attribute " + paramObjectIdentifier + " constructing PKCS9Attribute; was " + paramObject
          
          .getClass().toString() + ", should be " + clazz
          .toString()); 
    this.value = paramObject;
  }
  
  public PKCS9Attribute(DerValue paramDerValue) throws IOException {
    String[] arrayOfString;
    SignerInfo[] arrayOfSignerInfo;
    byte b2;
    DerInputStream derInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue1 = derInputStream.getSequence(2);
    if (derInputStream.available() != 0)
      throw new IOException("Excess data parsing PKCS9Attribute"); 
    if (arrayOfDerValue1.length != 2)
      throw new IOException("PKCS9Attribute doesn't have two components"); 
    this.oid = arrayOfDerValue1[0].getOID();
    byte[] arrayOfByte = arrayOfDerValue1[1].toByteArray();
    DerValue[] arrayOfDerValue2 = (new DerInputStream(arrayOfByte)).getSet(1);
    this.index = indexOf(this.oid, (Object[])PKCS9_OIDS, 1);
    if (this.index == -1) {
      if (debug != null)
        debug.println("Unsupported signer attribute: " + this.oid); 
      this.value = arrayOfByte;
      return;
    } 
    if (SINGLE_VALUED[this.index] && arrayOfDerValue2.length > 1)
      throwSingleValuedException(); 
    for (byte b1 = 0; b1 < arrayOfDerValue2.length; b1++) {
      Byte byte_ = new Byte((arrayOfDerValue2[b1]).tag);
      if (indexOf(byte_, (Object[])PKCS9_VALUE_TAGS[this.index], 0) == -1)
        throwTagException(byte_); 
    } 
    switch (this.index) {
      case 1:
      case 2:
      case 8:
        arrayOfString = new String[arrayOfDerValue2.length];
        for (b2 = 0; b2 < arrayOfDerValue2.length; b2++)
          arrayOfString[b2] = arrayOfDerValue2[b2].getAsString(); 
        this.value = arrayOfString;
        break;
      case 3:
        this.value = arrayOfDerValue2[0].getOID();
        break;
      case 4:
        this.value = arrayOfDerValue2[0].getOctetString();
        break;
      case 5:
        this.value = (new DerInputStream(arrayOfDerValue2[0].toByteArray())).getUTCTime();
        break;
      case 6:
        arrayOfSignerInfo = new SignerInfo[arrayOfDerValue2.length];
        for (b2 = 0; b2 < arrayOfDerValue2.length; b2++)
          arrayOfSignerInfo[b2] = new SignerInfo(arrayOfDerValue2[b2]
              .toDerInputStream()); 
        this.value = arrayOfSignerInfo;
        break;
      case 7:
        this.value = arrayOfDerValue2[0].getAsString();
        break;
      case 9:
        throw new IOException("PKCS9 extended-certificate attribute not supported.");
      case 10:
        throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
      case 11:
      case 12:
        throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
      case 13:
        throw new IOException("PKCS9 attribute #13 not supported.");
      case 14:
        this
          .value = new CertificateExtensions(new DerInputStream(arrayOfDerValue2[0].toByteArray()));
        break;
      case 15:
        throw new IOException("PKCS9 SMIMECapability attribute not supported.");
      case 16:
        this.value = new SigningCertificateInfo(arrayOfDerValue2[0].toByteArray());
        break;
      case 17:
        this.value = arrayOfDerValue2[0].toByteArray();
        break;
    } 
  }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    String[] arrayOfString2;
    DerOutputStream derOutputStream3;
    String[] arrayOfString1;
    DerOutputStream[] arrayOfDerOutputStream;
    CertificateExtensions certificateExtensions;
    byte b;
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putOID(this.oid);
    switch (this.index) {
      case -1:
        derOutputStream1.write((byte[])this.value);
        break;
      case 1:
      case 2:
        arrayOfString2 = (String[])this.value;
        arrayOfDerOutputStream = new DerOutputStream[arrayOfString2.length];
        for (b = 0; b < arrayOfString2.length; b++) {
          arrayOfDerOutputStream[b] = new DerOutputStream();
          arrayOfDerOutputStream[b].putIA5String(arrayOfString2[b]);
        } 
        derOutputStream1.putOrderedSetOf((byte)49, (DerEncoder[])arrayOfDerOutputStream);
        break;
      case 3:
        derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOID((ObjectIdentifier)this.value);
        derOutputStream1.write((byte)49, derOutputStream3.toByteArray());
        break;
      case 4:
        derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString((byte[])this.value);
        derOutputStream1.write((byte)49, derOutputStream3.toByteArray());
        break;
      case 5:
        derOutputStream3 = new DerOutputStream();
        derOutputStream3.putUTCTime((Date)this.value);
        derOutputStream1.write((byte)49, derOutputStream3.toByteArray());
        break;
      case 6:
        derOutputStream1.putOrderedSetOf((byte)49, (DerEncoder[])this.value);
        break;
      case 7:
        derOutputStream3 = new DerOutputStream();
        derOutputStream3.putPrintableString((String)this.value);
        derOutputStream1.write((byte)49, derOutputStream3.toByteArray());
        break;
      case 8:
        arrayOfString1 = (String[])this.value;
        arrayOfDerOutputStream = new DerOutputStream[arrayOfString1.length];
        for (b = 0; b < arrayOfString1.length; b++) {
          arrayOfDerOutputStream[b] = new DerOutputStream();
          arrayOfDerOutputStream[b].putPrintableString(arrayOfString1[b]);
        } 
        derOutputStream1.putOrderedSetOf((byte)49, (DerEncoder[])arrayOfDerOutputStream);
        break;
      case 9:
        throw new IOException("PKCS9 extended-certificate attribute not supported.");
      case 10:
        throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
      case 11:
      case 12:
        throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
      case 13:
        throw new IOException("PKCS9 attribute #13 not supported.");
      case 14:
        derOutputStream2 = new DerOutputStream();
        certificateExtensions = (CertificateExtensions)this.value;
        try {
          certificateExtensions.encode(derOutputStream2, true);
        } catch (CertificateException certificateException) {
          throw new IOException(certificateException.toString());
        } 
        derOutputStream1.write((byte)49, derOutputStream2.toByteArray());
        break;
      case 15:
        throw new IOException("PKCS9 attribute #15 not supported.");
      case 16:
        throw new IOException("PKCS9 SigningCertificate attribute not supported.");
      case 17:
        derOutputStream1.write((byte)49, (byte[])this.value);
        break;
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1.toByteArray());
    paramOutputStream.write(derOutputStream2.toByteArray());
  }
  
  public boolean isKnown() {
    return (this.index != -1);
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public boolean isSingleValued() {
    return (this.index == -1 || SINGLE_VALUED[this.index]);
  }
  
  public ObjectIdentifier getOID() {
    return this.oid;
  }
  
  public String getName() {
    return (this.index == -1) ? this.oid
      .toString() : OID_NAME_TABLE
      .get(PKCS9_OIDS[this.index]);
  }
  
  public static ObjectIdentifier getOID(String paramString) {
    return NAME_OID_TABLE.get(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static String getName(ObjectIdentifier paramObjectIdentifier) {
    return OID_NAME_TABLE.get(paramObjectIdentifier);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(100);
    stringBuffer.append("[");
    if (this.index == -1) {
      stringBuffer.append(this.oid.toString());
    } else {
      stringBuffer.append(OID_NAME_TABLE.get(PKCS9_OIDS[this.index]));
    } 
    stringBuffer.append(": ");
    if (this.index == -1 || SINGLE_VALUED[this.index]) {
      if (this.value instanceof byte[]) {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        stringBuffer.append(hexDumpEncoder.encodeBuffer((byte[])this.value));
      } else {
        stringBuffer.append(this.value.toString());
      } 
      stringBuffer.append("]");
      return stringBuffer.toString();
    } 
    boolean bool = true;
    Object[] arrayOfObject = (Object[])this.value;
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (bool) {
        bool = false;
      } else {
        stringBuffer.append(", ");
      } 
      stringBuffer.append(arrayOfObject[b].toString());
    } 
    return stringBuffer.toString();
  }
  
  static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt) {
    for (int i = paramInt; i < paramArrayOfObject.length; i++) {
      if (paramObject.equals(paramArrayOfObject[i]))
        return i; 
    } 
    return -1;
  }
  
  private void throwSingleValuedException() throws IOException {
    throw new IOException("Single-value attribute " + this.oid + " (" + 
        getName() + ")" + " has multiple values.");
  }
  
  private void throwTagException(Byte paramByte) throws IOException {
    Byte[] arrayOfByte = PKCS9_VALUE_TAGS[this.index];
    StringBuffer stringBuffer = new StringBuffer(100);
    stringBuffer.append("Value of attribute ");
    stringBuffer.append(this.oid.toString());
    stringBuffer.append(" (");
    stringBuffer.append(getName());
    stringBuffer.append(") has wrong tag: ");
    stringBuffer.append(paramByte.toString());
    stringBuffer.append(".  Expected tags: ");
    stringBuffer.append(arrayOfByte[0].toString());
    for (byte b = 1; b < arrayOfByte.length; b++) {
      stringBuffer.append(", ");
      stringBuffer.append(arrayOfByte[b].toString());
    } 
    stringBuffer.append(".");
    throw new IOException(stringBuffer.toString());
  }
}
