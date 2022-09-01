package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class ContentInfo {
  private static int[] pkcs7 = new int[] { 1, 2, 840, 113549, 1, 7 };
  
  private static int[] data = new int[] { 1, 2, 840, 113549, 1, 7, 1 };
  
  private static int[] sdata = new int[] { 1, 2, 840, 113549, 1, 7, 2 };
  
  private static int[] edata = new int[] { 1, 2, 840, 113549, 1, 7, 3 };
  
  private static int[] sedata = new int[] { 1, 2, 840, 113549, 1, 7, 4 };
  
  private static int[] ddata = new int[] { 1, 2, 840, 113549, 1, 7, 5 };
  
  private static int[] crdata = new int[] { 1, 2, 840, 113549, 1, 7, 6 };
  
  private static int[] nsdata = new int[] { 2, 16, 840, 1, 113730, 2, 5 };
  
  private static int[] tstInfo = new int[] { 1, 2, 840, 113549, 1, 9, 16, 1, 4 };
  
  private static final int[] OLD_SDATA = new int[] { 1, 2, 840, 1113549, 1, 7, 2 };
  
  private static final int[] OLD_DATA = new int[] { 1, 2, 840, 1113549, 1, 7, 1 };
  
  public static ObjectIdentifier PKCS7_OID = ObjectIdentifier.newInternal(pkcs7);
  
  public static ObjectIdentifier DATA_OID = ObjectIdentifier.newInternal(data);
  
  public static ObjectIdentifier SIGNED_DATA_OID = ObjectIdentifier.newInternal(sdata);
  
  public static ObjectIdentifier ENVELOPED_DATA_OID = ObjectIdentifier.newInternal(edata);
  
  public static ObjectIdentifier SIGNED_AND_ENVELOPED_DATA_OID = ObjectIdentifier.newInternal(sedata);
  
  public static ObjectIdentifier DIGESTED_DATA_OID = ObjectIdentifier.newInternal(ddata);
  
  public static ObjectIdentifier ENCRYPTED_DATA_OID = ObjectIdentifier.newInternal(crdata);
  
  public static ObjectIdentifier OLD_SIGNED_DATA_OID = ObjectIdentifier.newInternal(OLD_SDATA);
  
  public static ObjectIdentifier OLD_DATA_OID = ObjectIdentifier.newInternal(OLD_DATA);
  
  public static ObjectIdentifier NETSCAPE_CERT_SEQUENCE_OID = ObjectIdentifier.newInternal(nsdata);
  
  public static ObjectIdentifier TIMESTAMP_TOKEN_INFO_OID = ObjectIdentifier.newInternal(tstInfo);
  
  ObjectIdentifier contentType;
  
  DerValue content;
  
  public ContentInfo(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue) {
    this.contentType = paramObjectIdentifier;
    this.content = paramDerValue;
  }
  
  public ContentInfo(byte[] paramArrayOfbyte) {
    DerValue derValue = new DerValue((byte)4, paramArrayOfbyte);
    this.contentType = DATA_OID;
    this.content = derValue;
  }
  
  public ContentInfo(DerInputStream paramDerInputStream) throws IOException, ParsingException {
    this(paramDerInputStream, false);
  }
  
  public ContentInfo(DerInputStream paramDerInputStream, boolean paramBoolean) throws IOException, ParsingException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    DerValue derValue = arrayOfDerValue[0];
    DerInputStream derInputStream = new DerInputStream(derValue.toByteArray());
    this.contentType = derInputStream.getOID();
    if (paramBoolean) {
      this.content = arrayOfDerValue[1];
    } else if (arrayOfDerValue.length > 1) {
      DerValue derValue1 = arrayOfDerValue[1];
      DerInputStream derInputStream1 = new DerInputStream(derValue1.toByteArray());
      DerValue[] arrayOfDerValue1 = derInputStream1.getSet(1, true);
      this.content = arrayOfDerValue1[0];
    } 
  }
  
  public DerValue getContent() {
    return this.content;
  }
  
  public ObjectIdentifier getContentType() {
    return this.contentType;
  }
  
  public byte[] getData() throws IOException {
    if (this.contentType.equals(DATA_OID) || this.contentType
      .equals(OLD_DATA_OID) || this.contentType
      .equals(TIMESTAMP_TOKEN_INFO_OID)) {
      if (this.content == null)
        return null; 
      return this.content.getOctetString();
    } 
    throw new IOException("content type is not DATA: " + this.contentType);
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(this.contentType);
    if (this.content != null) {
      DerValue derValue = null;
      DerOutputStream derOutputStream1 = new DerOutputStream();
      this.content.encode(derOutputStream1);
      derValue = new DerValue((byte)-96, derOutputStream1.toByteArray());
      derOutputStream.putDerValue(derValue);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public byte[] getContentBytes() throws IOException {
    if (this.content == null)
      return null; 
    DerInputStream derInputStream = new DerInputStream(this.content.toByteArray());
    return derInputStream.getOctetString();
  }
  
  public String toString() {
    String str = "";
    str = str + "Content Info Sequence\n\tContent type: " + this.contentType + "\n";
    str = str + "\tContent: " + this.content;
    return str;
  }
}
