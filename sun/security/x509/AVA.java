package sun.security.x509;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.AccessController;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import sun.security.action.GetBooleanAction;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class AVA implements DerEncoder {
  private static final Debug debug = Debug.getInstance("x509", "\t[AVA]");
  
  private static final boolean PRESERVE_OLD_DC_ENCODING = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("com.sun.security.preserveOldDCEncoding"))).booleanValue();
  
  static final int DEFAULT = 1;
  
  static final int RFC1779 = 2;
  
  static final int RFC2253 = 3;
  
  final ObjectIdentifier oid;
  
  final DerValue value;
  
  private static final String specialChars1779 = ",=\n+<>#;\\\"";
  
  private static final String specialChars2253 = ",=+<>#;\\\"";
  
  private static final String specialCharsDefault = ",=\n+<>#;\\\" ";
  
  private static final String escapedDefault = ",+<>;\"";
  
  private static final String hexDigits = "0123456789ABCDEF";
  
  public AVA(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue) {
    if (paramObjectIdentifier == null || paramDerValue == null)
      throw new NullPointerException(); 
    this.oid = paramObjectIdentifier;
    this.value = paramDerValue;
  }
  
  AVA(Reader paramReader) throws IOException {
    this(paramReader, 1);
  }
  
  AVA(Reader paramReader, Map<String, String> paramMap) throws IOException {
    this(paramReader, 1, paramMap);
  }
  
  AVA(Reader paramReader, int paramInt) throws IOException {
    this(paramReader, paramInt, Collections.emptyMap());
  }
  
  AVA(Reader paramReader, int paramInt, Map<String, String> paramMap) throws IOException {
    int i;
    StringBuilder stringBuilder = new StringBuilder();
    while (true) {
      i = readChar(paramReader, "Incorrect AVA format");
      if (i == 61)
        break; 
      stringBuilder.append((char)i);
    } 
    this.oid = AVAKeyword.getOID(stringBuilder.toString(), paramInt, paramMap);
    stringBuilder.setLength(0);
    if (paramInt == 3) {
      i = paramReader.read();
      if (i == 32)
        throw new IOException("Incorrect AVA RFC2253 format - leading space must be escaped"); 
    } else {
      do {
        i = paramReader.read();
      } while (i == 32 || i == 10);
    } 
    if (i == -1) {
      this.value = new DerValue("");
      return;
    } 
    if (i == 35) {
      this.value = parseHexString(paramReader, paramInt);
    } else if (i == 34 && paramInt != 3) {
      this.value = parseQuotedString(paramReader, stringBuilder);
    } else {
      this.value = parseString(paramReader, i, paramInt, stringBuilder);
    } 
  }
  
  public ObjectIdentifier getObjectIdentifier() {
    return this.oid;
  }
  
  public DerValue getDerValue() {
    return this.value;
  }
  
  public String getValueString() {
    try {
      String str = this.value.getAsString();
      if (str == null)
        throw new RuntimeException("AVA string is null"); 
      return str;
    } catch (IOException iOException) {
      throw new RuntimeException("AVA error: " + iOException, iOException);
    } 
  }
  
  private static DerValue parseHexString(Reader paramReader, int paramInt) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte b = 0;
    byte b1 = 0;
    while (true) {
      int i = paramReader.read();
      if (isTerminator(i, paramInt))
        break; 
      int j = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)i));
      if (j == -1)
        throw new IOException("AVA parse, invalid hex digit: " + (char)i); 
      if (b1 % 2 == 1) {
        b = (byte)(b * 16 + (byte)j);
        byteArrayOutputStream.write(b);
      } else {
        b = (byte)j;
      } 
      b1++;
    } 
    if (b1 == 0)
      throw new IOException("AVA parse, zero hex digits"); 
    if (b1 % 2 == 1)
      throw new IOException("AVA parse, odd number of hex digits"); 
    return new DerValue(byteArrayOutputStream.toByteArray());
  }
  
  private DerValue parseQuotedString(Reader paramReader, StringBuilder paramStringBuilder) throws IOException {
    int i = readChar(paramReader, "Quoted string did not end in quote");
    ArrayList<Byte> arrayList = new ArrayList();
    boolean bool = true;
    while (i != 34) {
      if (i == 92) {
        i = readChar(paramReader, "Quoted string did not end in quote");
        Byte byte_ = null;
        if ((byte_ = getEmbeddedHexPair(i, paramReader)) != null) {
          bool = false;
          arrayList.add(byte_);
          i = paramReader.read();
          continue;
        } 
        if (",=\n+<>#;\\\"".indexOf((char)i) < 0)
          throw new IOException("Invalid escaped character in AVA: " + (char)i); 
      } 
      if (arrayList.size() > 0) {
        String str = getEmbeddedHexString(arrayList);
        paramStringBuilder.append(str);
        arrayList.clear();
      } 
      bool &= DerValue.isPrintableStringChar((char)i);
      paramStringBuilder.append((char)i);
      i = readChar(paramReader, "Quoted string did not end in quote");
    } 
    if (arrayList.size() > 0) {
      String str = getEmbeddedHexString(arrayList);
      paramStringBuilder.append(str);
      arrayList.clear();
    } 
    while (true) {
      i = paramReader.read();
      if (i != 10 && i != 32) {
        if (i != -1)
          throw new IOException("AVA had characters other than whitespace after terminating quote"); 
        if (this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID) || (this.oid
          .equals(X500Name.DOMAIN_COMPONENT_OID) && !PRESERVE_OLD_DC_ENCODING))
          return new DerValue((byte)22, paramStringBuilder
              .toString().trim()); 
        if (bool)
          return new DerValue(paramStringBuilder.toString().trim()); 
        return new DerValue((byte)12, paramStringBuilder
            .toString().trim());
      } 
    } 
  }
  
  private DerValue parseString(Reader paramReader, int paramInt1, int paramInt2, StringBuilder paramStringBuilder) throws IOException {
    // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore #5
    //   9: iconst_1
    //   10: istore #6
    //   12: iconst_0
    //   13: istore #7
    //   15: iconst_1
    //   16: istore #8
    //   18: iconst_0
    //   19: istore #9
    //   21: iconst_0
    //   22: istore #7
    //   24: iload_2
    //   25: bipush #92
    //   27: if_icmpne -> 230
    //   30: iconst_1
    //   31: istore #7
    //   33: aload_1
    //   34: ldc 'Invalid trailing backslash'
    //   36: invokestatic readChar : (Ljava/io/Reader;Ljava/lang/String;)I
    //   39: istore_2
    //   40: aconst_null
    //   41: astore #10
    //   43: iload_2
    //   44: aload_1
    //   45: invokestatic getEmbeddedHexPair : (ILjava/io/Reader;)Ljava/lang/Byte;
    //   48: dup
    //   49: astore #10
    //   51: ifnull -> 78
    //   54: iconst_0
    //   55: istore #6
    //   57: aload #5
    //   59: aload #10
    //   61: invokeinterface add : (Ljava/lang/Object;)Z
    //   66: pop
    //   67: aload_1
    //   68: invokevirtual read : ()I
    //   71: istore_2
    //   72: iconst_0
    //   73: istore #8
    //   75: goto -> 458
    //   78: iload_3
    //   79: iconst_1
    //   80: if_icmpne -> 127
    //   83: ldc ',=\\n+<>#;\" '
    //   85: iload_2
    //   86: i2c
    //   87: invokevirtual indexOf : (I)I
    //   90: iconst_m1
    //   91: if_icmpne -> 127
    //   94: new java/io/IOException
    //   97: dup
    //   98: new java/lang/StringBuilder
    //   101: dup
    //   102: invokespecial <init> : ()V
    //   105: ldc 'Invalid escaped character in AVA: ''
    //   107: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: iload_2
    //   111: i2c
    //   112: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   115: ldc '''
    //   117: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual toString : ()Ljava/lang/String;
    //   123: invokespecial <init> : (Ljava/lang/String;)V
    //   126: athrow
    //   127: iload_3
    //   128: iconst_3
    //   129: if_icmpne -> 227
    //   132: iload_2
    //   133: bipush #32
    //   135: if_icmpne -> 161
    //   138: iload #8
    //   140: ifne -> 227
    //   143: aload_1
    //   144: invokestatic trailingSpace : (Ljava/io/Reader;)Z
    //   147: ifne -> 227
    //   150: new java/io/IOException
    //   153: dup
    //   154: ldc_w 'Invalid escaped space character in AVA.  Only a leading or trailing space character can be escaped.'
    //   157: invokespecial <init> : (Ljava/lang/String;)V
    //   160: athrow
    //   161: iload_2
    //   162: bipush #35
    //   164: if_icmpne -> 183
    //   167: iload #8
    //   169: ifne -> 227
    //   172: new java/io/IOException
    //   175: dup
    //   176: ldc_w 'Invalid escaped '#' character in AVA.  Only a leading '#' can be escaped.'
    //   179: invokespecial <init> : (Ljava/lang/String;)V
    //   182: athrow
    //   183: ldc ',=+<>#;\"'
    //   185: iload_2
    //   186: i2c
    //   187: invokevirtual indexOf : (I)I
    //   190: iconst_m1
    //   191: if_icmpne -> 227
    //   194: new java/io/IOException
    //   197: dup
    //   198: new java/lang/StringBuilder
    //   201: dup
    //   202: invokespecial <init> : ()V
    //   205: ldc 'Invalid escaped character in AVA: ''
    //   207: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: iload_2
    //   211: i2c
    //   212: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   215: ldc '''
    //   217: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: invokevirtual toString : ()Ljava/lang/String;
    //   223: invokespecial <init> : (Ljava/lang/String;)V
    //   226: athrow
    //   227: goto -> 327
    //   230: iload_3
    //   231: iconst_3
    //   232: if_icmpne -> 281
    //   235: ldc ',=+<>#;\"'
    //   237: iload_2
    //   238: i2c
    //   239: invokevirtual indexOf : (I)I
    //   242: iconst_m1
    //   243: if_icmpeq -> 327
    //   246: new java/io/IOException
    //   249: dup
    //   250: new java/lang/StringBuilder
    //   253: dup
    //   254: invokespecial <init> : ()V
    //   257: ldc_w 'Character ''
    //   260: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: iload_2
    //   264: i2c
    //   265: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   268: ldc_w '' in AVA appears without escape'
    //   271: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: invokevirtual toString : ()Ljava/lang/String;
    //   277: invokespecial <init> : (Ljava/lang/String;)V
    //   280: athrow
    //   281: ldc ',+<>;"'
    //   283: iload_2
    //   284: i2c
    //   285: invokevirtual indexOf : (I)I
    //   288: iconst_m1
    //   289: if_icmpeq -> 327
    //   292: new java/io/IOException
    //   295: dup
    //   296: new java/lang/StringBuilder
    //   299: dup
    //   300: invokespecial <init> : ()V
    //   303: ldc_w 'Character ''
    //   306: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   309: iload_2
    //   310: i2c
    //   311: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   314: ldc_w '' in AVA appears without escape'
    //   317: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   320: invokevirtual toString : ()Ljava/lang/String;
    //   323: invokespecial <init> : (Ljava/lang/String;)V
    //   326: athrow
    //   327: aload #5
    //   329: invokeinterface size : ()I
    //   334: ifle -> 387
    //   337: iconst_0
    //   338: istore #10
    //   340: iload #10
    //   342: iload #9
    //   344: if_icmpge -> 362
    //   347: aload #4
    //   349: ldc_w ' '
    //   352: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: pop
    //   356: iinc #10, 1
    //   359: goto -> 340
    //   362: iconst_0
    //   363: istore #9
    //   365: aload #5
    //   367: invokestatic getEmbeddedHexString : (Ljava/util/List;)Ljava/lang/String;
    //   370: astore #10
    //   372: aload #4
    //   374: aload #10
    //   376: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: pop
    //   380: aload #5
    //   382: invokeinterface clear : ()V
    //   387: iload #6
    //   389: iload_2
    //   390: i2c
    //   391: invokestatic isPrintableStringChar : (C)Z
    //   394: iand
    //   395: istore #6
    //   397: iload_2
    //   398: bipush #32
    //   400: if_icmpne -> 414
    //   403: iload #7
    //   405: ifne -> 414
    //   408: iinc #9, 1
    //   411: goto -> 450
    //   414: iconst_0
    //   415: istore #10
    //   417: iload #10
    //   419: iload #9
    //   421: if_icmpge -> 439
    //   424: aload #4
    //   426: ldc_w ' '
    //   429: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   432: pop
    //   433: iinc #10, 1
    //   436: goto -> 417
    //   439: iconst_0
    //   440: istore #9
    //   442: aload #4
    //   444: iload_2
    //   445: i2c
    //   446: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   449: pop
    //   450: aload_1
    //   451: invokevirtual read : ()I
    //   454: istore_2
    //   455: iconst_0
    //   456: istore #8
    //   458: iload_2
    //   459: iload_3
    //   460: invokestatic isTerminator : (II)Z
    //   463: ifeq -> 21
    //   466: iload_3
    //   467: iconst_3
    //   468: if_icmpne -> 487
    //   471: iload #9
    //   473: ifle -> 487
    //   476: new java/io/IOException
    //   479: dup
    //   480: ldc_w 'Incorrect AVA RFC2253 format - trailing space must be escaped'
    //   483: invokespecial <init> : (Ljava/lang/String;)V
    //   486: athrow
    //   487: aload #5
    //   489: invokeinterface size : ()I
    //   494: ifle -> 519
    //   497: aload #5
    //   499: invokestatic getEmbeddedHexString : (Ljava/util/List;)Ljava/lang/String;
    //   502: astore #10
    //   504: aload #4
    //   506: aload #10
    //   508: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   511: pop
    //   512: aload #5
    //   514: invokeinterface clear : ()V
    //   519: aload_0
    //   520: getfield oid : Lsun/security/util/ObjectIdentifier;
    //   523: getstatic sun/security/pkcs/PKCS9Attribute.EMAIL_ADDRESS_OID : Lsun/security/util/ObjectIdentifier;
    //   526: invokevirtual equals : (Ljava/lang/Object;)Z
    //   529: ifne -> 551
    //   532: aload_0
    //   533: getfield oid : Lsun/security/util/ObjectIdentifier;
    //   536: getstatic sun/security/x509/X500Name.DOMAIN_COMPONENT_OID : Lsun/security/util/ObjectIdentifier;
    //   539: invokevirtual equals : (Ljava/lang/Object;)Z
    //   542: ifeq -> 566
    //   545: getstatic sun/security/x509/AVA.PRESERVE_OLD_DC_ENCODING : Z
    //   548: ifne -> 566
    //   551: new sun/security/util/DerValue
    //   554: dup
    //   555: bipush #22
    //   557: aload #4
    //   559: invokevirtual toString : ()Ljava/lang/String;
    //   562: invokespecial <init> : (BLjava/lang/String;)V
    //   565: areturn
    //   566: iload #6
    //   568: ifeq -> 584
    //   571: new sun/security/util/DerValue
    //   574: dup
    //   575: aload #4
    //   577: invokevirtual toString : ()Ljava/lang/String;
    //   580: invokespecial <init> : (Ljava/lang/String;)V
    //   583: areturn
    //   584: new sun/security/util/DerValue
    //   587: dup
    //   588: bipush #12
    //   590: aload #4
    //   592: invokevirtual toString : ()Ljava/lang/String;
    //   595: invokespecial <init> : (BLjava/lang/String;)V
    //   598: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #384	-> 0
    //   #385	-> 9
    //   #386	-> 12
    //   #387	-> 15
    //   #388	-> 18
    //   #390	-> 21
    //   #391	-> 24
    //   #392	-> 30
    //   #393	-> 33
    //   #396	-> 40
    //   #397	-> 43
    //   #400	-> 54
    //   #404	-> 57
    //   #405	-> 67
    //   #406	-> 72
    //   #407	-> 75
    //   #411	-> 78
    //   #412	-> 87
    //   #413	-> 94
    //   #416	-> 127
    //   #417	-> 132
    //   #419	-> 138
    //   #420	-> 150
    //   #425	-> 161
    //   #427	-> 167
    //   #428	-> 172
    //   #432	-> 183
    //   #433	-> 194
    //   #438	-> 227
    //   #440	-> 230
    //   #441	-> 235
    //   #442	-> 246
    //   #446	-> 281
    //   #447	-> 292
    //   #454	-> 327
    //   #456	-> 337
    //   #457	-> 347
    //   #456	-> 356
    //   #459	-> 362
    //   #461	-> 365
    //   #462	-> 372
    //   #463	-> 380
    //   #467	-> 387
    //   #468	-> 397
    //   #471	-> 408
    //   #474	-> 414
    //   #475	-> 424
    //   #474	-> 433
    //   #477	-> 439
    //   #478	-> 442
    //   #480	-> 450
    //   #481	-> 455
    //   #482	-> 458
    //   #484	-> 466
    //   #485	-> 476
    //   #490	-> 487
    //   #491	-> 497
    //   #492	-> 504
    //   #493	-> 512
    //   #498	-> 519
    //   #499	-> 539
    //   #502	-> 551
    //   #503	-> 566
    //   #504	-> 571
    //   #506	-> 584
  }
  
  private static Byte getEmbeddedHexPair(int paramInt, Reader paramReader) throws IOException {
    if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)paramInt)) >= 0) {
      int i = readChar(paramReader, "unexpected EOF - escaped hex value must include two valid digits");
      if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)i)) >= 0) {
        int j = Character.digit((char)paramInt, 16);
        int k = Character.digit((char)i, 16);
        return new Byte((byte)((j << 4) + k));
      } 
      throw new IOException("escaped hex value must include two valid digits");
    } 
    return null;
  }
  
  private static String getEmbeddedHexString(List<Byte> paramList) throws IOException {
    int i = paramList.size();
    byte[] arrayOfByte = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = ((Byte)paramList.get(b)).byteValue(); 
    return new String(arrayOfByte, "UTF8");
  }
  
  private static boolean isTerminator(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case -1:
      case 43:
      case 44:
        return true;
      case 59:
        return (paramInt2 != 3);
    } 
    return false;
  }
  
  private static int readChar(Reader paramReader, String paramString) throws IOException {
    int i = paramReader.read();
    if (i == -1)
      throw new IOException(paramString); 
    return i;
  }
  
  private static boolean trailingSpace(Reader paramReader) throws IOException {
    boolean bool = false;
    if (!paramReader.markSupported())
      return true; 
    paramReader.mark(9999);
    while (true) {
      int i = paramReader.read();
      if (i == -1) {
        bool = true;
        break;
      } 
      if (i == 32)
        continue; 
      if (i == 92) {
        int j = paramReader.read();
        if (j != 32) {
          bool = false;
          break;
        } 
        continue;
      } 
      bool = false;
      break;
    } 
    paramReader.reset();
    return bool;
  }
  
  AVA(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("AVA not a sequence"); 
    this.oid = X500Name.intern(paramDerValue.data.getOID());
    this.value = paramDerValue.data.getDerValue();
    if (paramDerValue.data.available() != 0)
      throw new IOException("AVA, extra bytes = " + paramDerValue.data
          .available()); 
  }
  
  AVA(DerInputStream paramDerInputStream) throws IOException {
    this(paramDerInputStream.getDerValue());
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AVA))
      return false; 
    AVA aVA = (AVA)paramObject;
    return toRFC2253CanonicalString().equals(aVA.toRFC2253CanonicalString());
  }
  
  public int hashCode() {
    return toRFC2253CanonicalString().hashCode();
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    derEncode(paramDerOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putOID(this.oid);
    this.value.encode(derOutputStream1);
    derOutputStream2.write((byte)48, derOutputStream1);
    paramOutputStream.write(derOutputStream2.toByteArray());
  }
  
  private String toKeyword(int paramInt, Map<String, String> paramMap) {
    return AVAKeyword.getKeyword(this.oid, paramInt, paramMap);
  }
  
  public String toString() {
    return toKeywordValueString(toKeyword(1, Collections.emptyMap()));
  }
  
  public String toRFC1779String() {
    return toRFC1779String(Collections.emptyMap());
  }
  
  public String toRFC1779String(Map<String, String> paramMap) {
    return toKeywordValueString(toKeyword(2, paramMap));
  }
  
  public String toRFC2253String() {
    return toRFC2253String(Collections.emptyMap());
  }
  
  public String toRFC2253String(Map<String, String> paramMap) {
    StringBuilder stringBuilder = new StringBuilder(100);
    stringBuilder.append(toKeyword(3, paramMap));
    stringBuilder.append('=');
    if ((stringBuilder.charAt(0) >= '0' && stringBuilder.charAt(0) <= '9') || 
      !isDerString(this.value, false)) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = this.value.toByteArray();
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      stringBuilder.append('#');
      for (byte b = 0; b < arrayOfByte.length; b++) {
        byte b1 = arrayOfByte[b];
        stringBuilder.append(Character.forDigit(0xF & b1 >>> 4, 16));
        stringBuilder.append(Character.forDigit(0xF & b1, 16));
      } 
    } else {
      String str = null;
      try {
        str = new String(this.value.getDataBytes(), "UTF8");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      StringBuilder stringBuilder1 = new StringBuilder();
      for (byte b1 = 0; b1 < str.length(); b1++) {
        char c = str.charAt(b1);
        if (DerValue.isPrintableStringChar(c) || ",=+<>#;\"\\"
          .indexOf(c) >= 0) {
          if (",=+<>#;\"\\".indexOf(c) >= 0)
            stringBuilder1.append('\\'); 
          stringBuilder1.append(c);
        } else if (c == '\000') {
          stringBuilder1.append("\\00");
        } else if (debug != null && Debug.isOn("ava")) {
          byte[] arrayOfByte = null;
          try {
            arrayOfByte = Character.toString(c).getBytes("UTF8");
          } catch (IOException iOException) {
            throw new IllegalArgumentException("DER Value conversion");
          } 
          for (byte b = 0; b < arrayOfByte.length; b++) {
            stringBuilder1.append('\\');
            char c1 = Character.forDigit(0xF & arrayOfByte[b] >>> 4, 16);
            stringBuilder1.append(Character.toUpperCase(c1));
            c1 = Character.forDigit(0xF & arrayOfByte[b], 16);
            stringBuilder1.append(Character.toUpperCase(c1));
          } 
        } else {
          stringBuilder1.append(c);
        } 
      } 
      char[] arrayOfChar = stringBuilder1.toString().toCharArray();
      stringBuilder1 = new StringBuilder();
      byte b2;
      for (b2 = 0; b2 < arrayOfChar.length && (
        arrayOfChar[b2] == ' ' || arrayOfChar[b2] == '\r'); b2++);
      int i;
      for (i = arrayOfChar.length - 1; i >= 0 && (
        arrayOfChar[i] == ' ' || arrayOfChar[i] == '\r'); i--);
      for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
        char c = arrayOfChar[b3];
        if (b3 < b2 || b3 > i)
          stringBuilder1.append('\\'); 
        stringBuilder1.append(c);
      } 
      stringBuilder.append(stringBuilder1.toString());
    } 
    return stringBuilder.toString();
  }
  
  public String toRFC2253CanonicalString() {
    StringBuilder stringBuilder = new StringBuilder(40);
    stringBuilder
      .append(toKeyword(3, Collections.emptyMap()));
    stringBuilder.append('=');
    if ((stringBuilder.charAt(0) >= '0' && stringBuilder.charAt(0) <= '9') || 
      !isDerString(this.value, true)) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = this.value.toByteArray();
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      stringBuilder.append('#');
      for (byte b = 0; b < arrayOfByte.length; b++) {
        byte b1 = arrayOfByte[b];
        stringBuilder.append(Character.forDigit(0xF & b1 >>> 4, 16));
        stringBuilder.append(Character.forDigit(0xF & b1, 16));
      } 
    } else {
      String str1 = null;
      try {
        str1 = new String(this.value.getDataBytes(), "UTF8");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      StringBuilder stringBuilder1 = new StringBuilder();
      boolean bool = false;
      for (byte b = 0; b < str1.length(); b++) {
        char c = str1.charAt(b);
        if (DerValue.isPrintableStringChar(c) || ",+<>;\"\\"
          .indexOf(c) >= 0 || (b == 0 && c == '#')) {
          if ((b == 0 && c == '#') || ",+<>;\"\\".indexOf(c) >= 0)
            stringBuilder1.append('\\'); 
          if (!Character.isWhitespace(c)) {
            bool = false;
            stringBuilder1.append(c);
          } else if (!bool) {
            bool = true;
            stringBuilder1.append(c);
          } 
        } else if (debug != null && Debug.isOn("ava")) {
          bool = false;
          byte[] arrayOfByte = null;
          try {
            arrayOfByte = Character.toString(c).getBytes("UTF8");
          } catch (IOException iOException) {
            throw new IllegalArgumentException("DER Value conversion");
          } 
          for (byte b1 = 0; b1 < arrayOfByte.length; b1++) {
            stringBuilder1.append('\\');
            stringBuilder1.append(
                Character.forDigit(0xF & arrayOfByte[b1] >>> 4, 16));
            stringBuilder1.append(
                Character.forDigit(0xF & arrayOfByte[b1], 16));
          } 
        } else {
          bool = false;
          stringBuilder1.append(c);
        } 
      } 
      stringBuilder.append(stringBuilder1.toString().trim());
    } 
    String str = stringBuilder.toString();
    str = str.toUpperCase(Locale.US).toLowerCase(Locale.US);
    return Normalizer.normalize(str, Normalizer.Form.NFKD);
  }
  
  private static boolean isDerString(DerValue paramDerValue, boolean paramBoolean) {
    if (paramBoolean) {
      switch (paramDerValue.tag) {
        case 12:
        case 19:
          return true;
      } 
      return false;
    } 
    switch (paramDerValue.tag) {
      case 12:
      case 19:
      case 20:
      case 22:
      case 27:
      case 30:
        return true;
    } 
    return false;
  }
  
  boolean hasRFC2253Keyword() {
    return AVAKeyword.hasKeyword(this.oid, 3);
  }
  
  private String toKeywordValueString(String paramString) {
    StringBuilder stringBuilder = new StringBuilder(40);
    stringBuilder.append(paramString);
    stringBuilder.append("=");
    try {
      String str = this.value.getAsString();
      if (str == null) {
        byte[] arrayOfByte = this.value.toByteArray();
        stringBuilder.append('#');
        for (byte b = 0; b < arrayOfByte.length; b++) {
          stringBuilder.append("0123456789ABCDEF".charAt(arrayOfByte[b] >> 4 & 0xF));
          stringBuilder.append("0123456789ABCDEF".charAt(arrayOfByte[b] & 0xF));
        } 
      } else {
        boolean bool1 = false;
        StringBuilder stringBuilder1 = new StringBuilder();
        boolean bool2 = false;
        int i = str.length();
        boolean bool3 = (i > 1 && str.charAt(0) == '"' && str.charAt(i - 1) == '"') ? true : false;
        char c;
        for (c = Character.MIN_VALUE; c < i; c++) {
          char c1 = str.charAt(c);
          if (bool3 && (c == Character.MIN_VALUE || c == i - 1)) {
            stringBuilder1.append(c1);
          } else if (DerValue.isPrintableStringChar(c1) || ",+=\n<>#;\\\""
            .indexOf(c1) >= 0) {
            if (!bool1 && ((c == Character.MIN_VALUE && (c1 == ' ' || c1 == '\n')) || ",+=\n<>#;\\\""
              
              .indexOf(c1) >= 0))
              bool1 = true; 
            if (c1 != ' ' && c1 != '\n') {
              if (c1 == '"' || c1 == '\\')
                stringBuilder1.append('\\'); 
              bool2 = false;
            } else {
              if (!bool1 && bool2)
                bool1 = true; 
              bool2 = true;
            } 
            stringBuilder1.append(c1);
          } else if (debug != null && Debug.isOn("ava")) {
            bool2 = false;
            byte[] arrayOfByte = Character.toString(c1).getBytes("UTF8");
            for (byte b = 0; b < arrayOfByte.length; b++) {
              stringBuilder1.append('\\');
              char c2 = Character.forDigit(0xF & arrayOfByte[b] >>> 4, 16);
              stringBuilder1.append(Character.toUpperCase(c2));
              c2 = Character.forDigit(0xF & arrayOfByte[b], 16);
              stringBuilder1.append(Character.toUpperCase(c2));
            } 
          } else {
            bool2 = false;
            stringBuilder1.append(c1);
          } 
        } 
        if (stringBuilder1.length() > 0) {
          c = stringBuilder1.charAt(stringBuilder1.length() - 1);
          if (c == ' ' || c == '\n')
            bool1 = true; 
        } 
        if (!bool3 && bool1) {
          stringBuilder.append("\"" + stringBuilder1.toString() + "\"");
        } else {
          stringBuilder.append(stringBuilder1.toString());
        } 
      } 
    } catch (IOException iOException) {
      throw new IllegalArgumentException("DER Value conversion");
    } 
    return stringBuilder.toString();
  }
}
