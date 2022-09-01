package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import sun.misc.IOUtils;

public class DerValue {
  public static final byte TAG_UNIVERSAL = 0;
  
  public static final byte TAG_APPLICATION = 64;
  
  public static final byte TAG_CONTEXT = -128;
  
  public static final byte TAG_PRIVATE = -64;
  
  public byte tag;
  
  protected DerInputBuffer buffer;
  
  public final DerInputStream data;
  
  private int length;
  
  public static final byte tag_Boolean = 1;
  
  public static final byte tag_Integer = 2;
  
  public static final byte tag_BitString = 3;
  
  public static final byte tag_OctetString = 4;
  
  public static final byte tag_Null = 5;
  
  public static final byte tag_ObjectId = 6;
  
  public static final byte tag_Enumerated = 10;
  
  public static final byte tag_UTF8String = 12;
  
  public static final byte tag_PrintableString = 19;
  
  public static final byte tag_T61String = 20;
  
  public static final byte tag_IA5String = 22;
  
  public static final byte tag_UtcTime = 23;
  
  public static final byte tag_GeneralizedTime = 24;
  
  public static final byte tag_GeneralString = 27;
  
  public static final byte tag_UniversalString = 28;
  
  public static final byte tag_BMPString = 30;
  
  public static final byte tag_Sequence = 48;
  
  public static final byte tag_SequenceOf = 48;
  
  public static final byte tag_Set = 49;
  
  public static final byte tag_SetOf = 49;
  
  public boolean isUniversal() {
    return ((this.tag & 0xC0) == 0);
  }
  
  public boolean isApplication() {
    return ((this.tag & 0xC0) == 64);
  }
  
  public boolean isContextSpecific() {
    return ((this.tag & 0xC0) == 128);
  }
  
  public boolean isContextSpecific(byte paramByte) {
    if (!isContextSpecific())
      return false; 
    return ((this.tag & 0x1F) == paramByte);
  }
  
  boolean isPrivate() {
    return ((this.tag & 0xC0) == 192);
  }
  
  public boolean isConstructed() {
    return ((this.tag & 0x20) == 32);
  }
  
  public boolean isConstructed(byte paramByte) {
    if (!isConstructed())
      return false; 
    return ((this.tag & 0x1F) == paramByte);
  }
  
  public DerValue(String paramString) throws IOException {
    boolean bool = true;
    for (byte b = 0; b < paramString.length(); b++) {
      if (!isPrintableStringChar(paramString.charAt(b))) {
        bool = false;
        break;
      } 
    } 
    this.data = init(bool ? 19 : 12, paramString);
  }
  
  public DerValue(byte paramByte, String paramString) throws IOException {
    this.data = init(paramByte, paramString);
  }
  
  public DerValue(byte paramByte, byte[] paramArrayOfbyte) {
    this.tag = paramByte;
    this.buffer = new DerInputBuffer((byte[])paramArrayOfbyte.clone());
    this.length = paramArrayOfbyte.length;
    this.data = new DerInputStream(this.buffer);
    this.data.mark(2147483647);
  }
  
  DerValue(DerInputBuffer paramDerInputBuffer) throws IOException {
    this.tag = (byte)paramDerInputBuffer.read();
    byte b = (byte)paramDerInputBuffer.read();
    this.length = DerInputStream.getLength(b & 0xFF, paramDerInputBuffer);
    if (this.length == -1) {
      DerInputBuffer derInputBuffer = paramDerInputBuffer.dup();
      int i = derInputBuffer.available();
      byte b1 = 2;
      byte[] arrayOfByte = new byte[i + b1];
      arrayOfByte[0] = this.tag;
      arrayOfByte[1] = b;
      DataInputStream dataInputStream = new DataInputStream(derInputBuffer);
      dataInputStream.readFully(arrayOfByte, b1, i);
      dataInputStream.close();
      DerIndefLenConverter derIndefLenConverter = new DerIndefLenConverter();
      derInputBuffer = new DerInputBuffer(derIndefLenConverter.convert(arrayOfByte));
      if (this.tag != derInputBuffer.read())
        throw new IOException("Indefinite length encoding not supported"); 
      this.length = DerInputStream.getLength(derInputBuffer);
      this.buffer = derInputBuffer.dup();
      this.buffer.truncate(this.length);
      this.data = new DerInputStream(this.buffer);
      paramDerInputBuffer.skip((this.length + b1));
    } else {
      this.buffer = paramDerInputBuffer.dup();
      this.buffer.truncate(this.length);
      this.data = new DerInputStream(this.buffer);
      paramDerInputBuffer.skip(this.length);
    } 
  }
  
  public DerValue(byte[] paramArrayOfbyte) throws IOException {
    this.data = init(true, new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public DerValue(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.data = init(true, new ByteArrayInputStream(paramArrayOfbyte, paramInt1, paramInt2));
  }
  
  public DerValue(InputStream paramInputStream) throws IOException {
    this.data = init(false, paramInputStream);
  }
  
  private DerInputStream init(byte paramByte, String paramString) throws IOException {
    byte[] arrayOfByte;
    DerInputStream derInputStream;
    String str = null;
    this.tag = paramByte;
    switch (paramByte) {
      case 19:
      case 22:
      case 27:
        str = "ASCII";
        arrayOfByte = paramString.getBytes(str);
        this.length = arrayOfByte.length;
        this.buffer = new DerInputBuffer(arrayOfByte);
        derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(2147483647);
        return derInputStream;
      case 20:
        str = "ISO-8859-1";
        arrayOfByte = paramString.getBytes(str);
        this.length = arrayOfByte.length;
        this.buffer = new DerInputBuffer(arrayOfByte);
        derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(2147483647);
        return derInputStream;
      case 30:
        str = "UnicodeBigUnmarked";
        arrayOfByte = paramString.getBytes(str);
        this.length = arrayOfByte.length;
        this.buffer = new DerInputBuffer(arrayOfByte);
        derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(2147483647);
        return derInputStream;
      case 12:
        str = "UTF8";
        arrayOfByte = paramString.getBytes(str);
        this.length = arrayOfByte.length;
        this.buffer = new DerInputBuffer(arrayOfByte);
        derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(2147483647);
        return derInputStream;
    } 
    throw new IllegalArgumentException("Unsupported DER string type");
  }
  
  private DerInputStream init(boolean paramBoolean, InputStream paramInputStream) throws IOException {
    this.tag = (byte)paramInputStream.read();
    byte b = (byte)paramInputStream.read();
    this.length = DerInputStream.getLength(b & 0xFF, paramInputStream);
    if (this.length == -1) {
      int i = paramInputStream.available();
      byte b1 = 2;
      byte[] arrayOfByte1 = new byte[i + b1];
      arrayOfByte1[0] = this.tag;
      arrayOfByte1[1] = b;
      DataInputStream dataInputStream = new DataInputStream(paramInputStream);
      dataInputStream.readFully(arrayOfByte1, b1, i);
      dataInputStream.close();
      DerIndefLenConverter derIndefLenConverter = new DerIndefLenConverter();
      paramInputStream = new ByteArrayInputStream(derIndefLenConverter.convert(arrayOfByte1));
      if (this.tag != paramInputStream.read())
        throw new IOException("Indefinite length encoding not supported"); 
      this.length = DerInputStream.getLength(paramInputStream);
    } 
    if (paramBoolean && paramInputStream.available() != this.length)
      throw new IOException("extra data given to DerValue constructor"); 
    byte[] arrayOfByte = IOUtils.readFully(paramInputStream, this.length, true);
    this.buffer = new DerInputBuffer(arrayOfByte);
    return new DerInputStream(this.buffer);
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    paramDerOutputStream.write(this.tag);
    paramDerOutputStream.putLength(this.length);
    if (this.length > 0) {
      byte[] arrayOfByte = new byte[this.length];
      synchronized (this.data) {
        this.buffer.reset();
        if (this.buffer.read(arrayOfByte) != this.length)
          throw new IOException("short DER value read (encode)"); 
        paramDerOutputStream.write(arrayOfByte);
      } 
    } 
  }
  
  public final DerInputStream getData() {
    return this.data;
  }
  
  public final byte getTag() {
    return this.tag;
  }
  
  public boolean getBoolean() throws IOException {
    if (this.tag != 1)
      throw new IOException("DerValue.getBoolean, not a BOOLEAN " + this.tag); 
    if (this.length != 1)
      throw new IOException("DerValue.getBoolean, invalid length " + this.length); 
    if (this.buffer.read() != 0)
      return true; 
    return false;
  }
  
  public ObjectIdentifier getOID() throws IOException {
    if (this.tag != 6)
      throw new IOException("DerValue.getOID, not an OID " + this.tag); 
    return new ObjectIdentifier(this.buffer);
  }
  
  private byte[] append(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null)
      return paramArrayOfbyte2; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, paramArrayOfbyte1.length);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    return arrayOfByte;
  }
  
  public byte[] getOctetString() throws IOException {
    if (this.tag != 4 && !isConstructed((byte)4))
      throw new IOException("DerValue.getOctetString, not an Octet String: " + this.tag); 
    byte[] arrayOfByte = new byte[this.length];
    if (this.length == 0)
      return arrayOfByte; 
    if (this.buffer.read(arrayOfByte) != this.length)
      throw new IOException("short read on DerValue buffer"); 
    if (isConstructed()) {
      DerInputStream derInputStream = new DerInputStream(arrayOfByte);
      arrayOfByte = null;
      while (derInputStream.available() != 0)
        arrayOfByte = append(arrayOfByte, derInputStream.getOctetString()); 
    } 
    return arrayOfByte;
  }
  
  public int getInteger() throws IOException {
    if (this.tag != 2)
      throw new IOException("DerValue.getInteger, not an int " + this.tag); 
    return this.buffer.getInteger(this.data.available());
  }
  
  public BigInteger getBigInteger() throws IOException {
    if (this.tag != 2)
      throw new IOException("DerValue.getBigInteger, not an int " + this.tag); 
    return this.buffer.getBigInteger(this.data.available(), false);
  }
  
  public BigInteger getPositiveBigInteger() throws IOException {
    if (this.tag != 2)
      throw new IOException("DerValue.getBigInteger, not an int " + this.tag); 
    return this.buffer.getBigInteger(this.data.available(), true);
  }
  
  public int getEnumerated() throws IOException {
    if (this.tag != 10)
      throw new IOException("DerValue.getEnumerated, incorrect tag: " + this.tag); 
    return this.buffer.getInteger(this.data.available());
  }
  
  public byte[] getBitString() throws IOException {
    if (this.tag != 3)
      throw new IOException("DerValue.getBitString, not a bit string " + this.tag); 
    return this.buffer.getBitString();
  }
  
  public BitArray getUnalignedBitString() throws IOException {
    if (this.tag != 3)
      throw new IOException("DerValue.getBitString, not a bit string " + this.tag); 
    return this.buffer.getUnalignedBitString();
  }
  
  public String getAsString() throws IOException {
    if (this.tag == 12)
      return getUTF8String(); 
    if (this.tag == 19)
      return getPrintableString(); 
    if (this.tag == 20)
      return getT61String(); 
    if (this.tag == 22)
      return getIA5String(); 
    if (this.tag == 30)
      return getBMPString(); 
    if (this.tag == 27)
      return getGeneralString(); 
    return null;
  }
  
  public byte[] getBitString(boolean paramBoolean) throws IOException {
    if (!paramBoolean && 
      this.tag != 3)
      throw new IOException("DerValue.getBitString, not a bit string " + this.tag); 
    return this.buffer.getBitString();
  }
  
  public BitArray getUnalignedBitString(boolean paramBoolean) throws IOException {
    if (!paramBoolean && 
      this.tag != 3)
      throw new IOException("DerValue.getBitString, not a bit string " + this.tag); 
    return this.buffer.getUnalignedBitString();
  }
  
  public byte[] getDataBytes() throws IOException {
    byte[] arrayOfByte = new byte[this.length];
    synchronized (this.data) {
      this.data.reset();
      this.data.getBytes(arrayOfByte);
    } 
    return arrayOfByte;
  }
  
  public String getPrintableString() throws IOException {
    if (this.tag != 19)
      throw new IOException("DerValue.getPrintableString, not a string " + this.tag); 
    return new String(getDataBytes(), "ASCII");
  }
  
  public String getT61String() throws IOException {
    if (this.tag != 20)
      throw new IOException("DerValue.getT61String, not T61 " + this.tag); 
    return new String(getDataBytes(), "ISO-8859-1");
  }
  
  public String getIA5String() throws IOException {
    if (this.tag != 22)
      throw new IOException("DerValue.getIA5String, not IA5 " + this.tag); 
    return new String(getDataBytes(), "ASCII");
  }
  
  public String getBMPString() throws IOException {
    if (this.tag != 30)
      throw new IOException("DerValue.getBMPString, not BMP " + this.tag); 
    return new String(getDataBytes(), "UnicodeBigUnmarked");
  }
  
  public String getUTF8String() throws IOException {
    if (this.tag != 12)
      throw new IOException("DerValue.getUTF8String, not UTF-8 " + this.tag); 
    return new String(getDataBytes(), "UTF8");
  }
  
  public String getGeneralString() throws IOException {
    if (this.tag != 27)
      throw new IOException("DerValue.getGeneralString, not GeneralString " + this.tag); 
    return new String(getDataBytes(), "ASCII");
  }
  
  public Date getUTCTime() throws IOException {
    if (this.tag != 23)
      throw new IOException("DerValue.getUTCTime, not a UtcTime: " + this.tag); 
    return this.buffer.getUTCTime(this.data.available());
  }
  
  public Date getGeneralizedTime() throws IOException {
    if (this.tag != 24)
      throw new IOException("DerValue.getGeneralizedTime, not a GeneralizedTime: " + this.tag); 
    return this.buffer.getGeneralizedTime(this.data.available());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof DerValue)
      return equals((DerValue)paramObject); 
    return false;
  }
  
  public boolean equals(DerValue paramDerValue) {
    if (this == paramDerValue)
      return true; 
    if (this.tag != paramDerValue.tag)
      return false; 
    if (this.data == paramDerValue.data)
      return true; 
    return 
      (System.identityHashCode(this.data) > System.identityHashCode(paramDerValue.data)) ? 
      doEquals(this, paramDerValue) : 
      doEquals(paramDerValue, this);
  }
  
  private static boolean doEquals(DerValue paramDerValue1, DerValue paramDerValue2) {
    synchronized (paramDerValue1.data) {
      synchronized (paramDerValue2.data) {
        paramDerValue1.data.reset();
        paramDerValue2.data.reset();
        return paramDerValue1.buffer.equals(paramDerValue2.buffer);
      } 
    } 
  }
  
  public String toString() {
    try {
      String str = getAsString();
      if (str != null)
        return "\"" + str + "\""; 
      if (this.tag == 5)
        return "[DerValue, null]"; 
      if (this.tag == 6)
        return "OID." + getOID(); 
      return "[DerValue, tag = " + this.tag + ", length = " + this.length + "]";
    } catch (IOException iOException) {
      throw new IllegalArgumentException("misformatted DER value");
    } 
  }
  
  public byte[] toByteArray() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    encode(derOutputStream);
    this.data.reset();
    return derOutputStream.toByteArray();
  }
  
  public DerInputStream toDerInputStream() throws IOException {
    if (this.tag == 48 || this.tag == 49)
      return new DerInputStream(this.buffer); 
    throw new IOException("toDerInputStream rejects tag type " + this.tag);
  }
  
  public int length() {
    return this.length;
  }
  
  public static boolean isPrintableStringChar(char paramChar) {
    if ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z') || (paramChar >= '0' && paramChar <= '9'))
      return true; 
    switch (paramChar) {
      case ' ':
      case '\'':
      case '(':
      case ')':
      case '+':
      case ',':
      case '-':
      case '.':
      case '/':
      case ':':
      case '=':
      case '?':
        return true;
    } 
    return false;
  }
  
  public static byte createTag(byte paramByte1, boolean paramBoolean, byte paramByte2) {
    byte b = (byte)(paramByte1 | paramByte2);
    if (paramBoolean)
      b = (byte)(b | 0x20); 
    return b;
  }
  
  public void resetTag(byte paramByte) {
    this.tag = paramByte;
  }
  
  public int hashCode() {
    return toString().hashCode();
  }
}
