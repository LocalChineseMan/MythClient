package sun.security.ssl;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.crypto.BadPaddingException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import sun.misc.HexDumpEncoder;

class InputRecord extends ByteArrayInputStream implements Record {
  private HandshakeHash handshakeHash;
  
  private int lastHashed;
  
  boolean formatVerified = true;
  
  private boolean isClosed;
  
  private boolean appDataValid;
  
  private ProtocolVersion helloVersion;
  
  static final Debug debug = Debug.getInstance("ssl");
  
  private int exlen;
  
  private byte[] v2Buf;
  
  InputRecord() {
    super(new byte[16921]);
    setHelloVersion(ProtocolVersion.DEFAULT_HELLO);
    this.pos = 5;
    this.count = 5;
    this.lastHashed = this.count;
    this.exlen = 0;
    this.v2Buf = null;
  }
  
  void setHelloVersion(ProtocolVersion paramProtocolVersion) {
    this.helloVersion = paramProtocolVersion;
  }
  
  ProtocolVersion getHelloVersion() {
    return this.helloVersion;
  }
  
  void enableFormatChecks() {
    this.formatVerified = false;
  }
  
  boolean isAppDataValid() {
    return this.appDataValid;
  }
  
  void setAppDataValid(boolean paramBoolean) {
    this.appDataValid = paramBoolean;
  }
  
  byte contentType() {
    return this.buf[0];
  }
  
  void setHandshakeHash(HandshakeHash paramHandshakeHash) {
    this.handshakeHash = paramHandshakeHash;
  }
  
  HandshakeHash getHandshakeHash() {
    return this.handshakeHash;
  }
  
  void decrypt(Authenticator paramAuthenticator, CipherBox paramCipherBox) throws BadPaddingException {
    BadPaddingException badPaddingException = null;
    byte b = (paramAuthenticator instanceof MAC) ? ((MAC)paramAuthenticator).MAClen() : 0;
    int i = this.count - 5;
    if (!paramCipherBox.isNullCipher())
      try {
        int j = paramCipherBox.applyExplicitNonce(paramAuthenticator, 
            contentType(), this.buf, 5, i);
        this.pos = 5 + j;
        this.lastHashed = this.pos;
        int k = 5;
        if (paramCipherBox.isAEADMode())
          k += j; 
        this
          .count = k + paramCipherBox.decrypt(this.buf, k, this.count - k, b);
      } catch (BadPaddingException badPaddingException1) {
        badPaddingException = badPaddingException1;
      }  
    if (paramAuthenticator instanceof MAC && b) {
      MAC mAC = (MAC)paramAuthenticator;
      int j = this.count - b;
      int k = j - this.pos;
      if (k < 0) {
        if (badPaddingException == null)
          badPaddingException = new BadPaddingException("bad record"); 
        j = 5 + i - b;
        k = j - 5;
      } 
      this.count -= b;
      if (checkMacTags(contentType(), this.buf, this.pos, k, mAC, false))
        if (badPaddingException == null)
          badPaddingException = new BadPaddingException("bad record MAC");  
      if (paramCipherBox.isCBCMode()) {
        int m = calculateRemainingLen(mAC, i, k);
        if (m > this.buf.length)
          throw new RuntimeException("Internal buffer capacity error"); 
        checkMacTags(contentType(), this.buf, 0, m, mAC, true);
      } 
    } 
    if (badPaddingException != null)
      throw badPaddingException; 
  }
  
  static boolean checkMacTags(byte paramByte, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, MAC paramMAC, boolean paramBoolean) {
    int i = paramMAC.MAClen();
    byte[] arrayOfByte = paramMAC.compute(paramByte, paramArrayOfbyte, paramInt1, paramInt2, paramBoolean);
    if (arrayOfByte == null || i != arrayOfByte.length)
      throw new RuntimeException("Internal MAC error"); 
    int[] arrayOfInt = compareMacTags(paramArrayOfbyte, paramInt1 + paramInt2, arrayOfByte);
    return (arrayOfInt[0] != 0);
  }
  
  private static int[] compareMacTags(byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) {
    int[] arrayOfInt = { 0, 0 };
    for (byte b = 0; b < paramArrayOfbyte2.length; b++) {
      if (paramArrayOfbyte1[paramInt + b] != paramArrayOfbyte2[b]) {
        arrayOfInt[0] = arrayOfInt[0] + 1;
      } else {
        arrayOfInt[1] = arrayOfInt[1] + 1;
      } 
    } 
    return arrayOfInt;
  }
  
  static int calculateRemainingLen(MAC paramMAC, int paramInt1, int paramInt2) {
    int i = paramMAC.hashBlockLen();
    int j = paramMAC.minimalPaddingLen();
    paramInt1 += 13 - i - j;
    paramInt2 += 13 - i - j;
    return 1 + (int)(Math.ceil(paramInt1 / 1.0D * i) - Math.ceil(paramInt2 / 1.0D * i)) * paramMAC.hashBlockLen();
  }
  
  void ignore(int paramInt) {
    if (paramInt > 0) {
      this.pos += paramInt;
      this.lastHashed = this.pos;
    } 
  }
  
  void doHashes() {
    int i = this.pos - this.lastHashed;
    if (i > 0) {
      hashInternal(this.buf, this.lastHashed, i);
      this.lastHashed = this.pos;
    } 
  }
  
  private void hashInternal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (debug != null && Debug.isOn("data"))
      try {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        System.out.println("[read] MD5 and SHA1 hashes:  len = " + paramInt2);
        hexDumpEncoder.encodeBuffer(new ByteArrayInputStream(paramArrayOfbyte, paramInt1, paramInt2), System.out);
      } catch (IOException iOException) {} 
    this.handshakeHash.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  void queueHandshake(InputRecord paramInputRecord) throws IOException {
    doHashes();
    if (this.pos > 5) {
      int j = this.count - this.pos;
      if (j != 0)
        System.arraycopy(this.buf, this.pos, this.buf, 5, j); 
      this.pos = 5;
      this.lastHashed = this.pos;
      this.count = 5 + j;
    } 
    int i = paramInputRecord.available() + this.count;
    if (this.buf.length < i) {
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    } 
    System.arraycopy(paramInputRecord.buf, paramInputRecord.pos, this.buf, this.count, i - this.count);
    this.count = i;
    i = paramInputRecord.lastHashed - paramInputRecord.pos;
    if (this.pos == 5) {
      this.lastHashed += i;
    } else {
      throw new SSLProtocolException("?? confused buffer hashing ??");
    } 
    paramInputRecord.pos = paramInputRecord.count;
  }
  
  public void close() {
    this.appDataValid = false;
    this.isClosed = true;
    this.mark = 0;
    this.pos = 0;
    this.count = 0;
  }
  
  private static final byte[] v2NoCipher = new byte[] { Byte.MIN_VALUE, 3, 0, 0, 1 };
  
  private int readFully(InputStream paramInputStream, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = 0;
    while (i < paramInt2) {
      int j = paramInputStream.read(paramArrayOfbyte, paramInt1 + i, paramInt2 - i);
      if (j < 0)
        return j; 
      if (debug != null && Debug.isOn("packet"))
        try {
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte, paramInt1 + i, j);
          System.out.println("[Raw read]: length = " + byteBuffer
              .remaining());
          hexDumpEncoder.encodeBuffer(byteBuffer, System.out);
        } catch (IOException iOException) {} 
      i += j;
      this.exlen += j;
    } 
    return i;
  }
  
  void read(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    if (this.isClosed)
      return; 
    if (this.exlen < 5) {
      int i = readFully(paramInputStream, this.buf, this.exlen, 5 - this.exlen);
      if (i < 0)
        throw new EOFException("SSL peer shut down incorrectly"); 
      this.pos = 5;
      this.count = 5;
      this.lastHashed = this.pos;
    } 
    if (!this.formatVerified) {
      this.formatVerified = true;
      if (this.buf[0] != 22 && this.buf[0] != 21) {
        handleUnknownRecord(paramInputStream, paramOutputStream);
      } else {
        readV3Record(paramInputStream, paramOutputStream);
      } 
    } else {
      readV3Record(paramInputStream, paramOutputStream);
    } 
  }
  
  static void checkRecordVersion(ProtocolVersion paramProtocolVersion, boolean paramBoolean) throws SSLException {
    if (paramProtocolVersion.v < ProtocolVersion.MIN.v || (paramProtocolVersion.major & 0xFF) > (ProtocolVersion.MAX.major & 0xFF))
      if (!paramBoolean || paramProtocolVersion.v != ProtocolVersion.SSL20Hello.v)
        throw new SSLException("Unsupported record version " + paramProtocolVersion);  
  }
  
  private void readV3Record(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    ProtocolVersion protocolVersion = ProtocolVersion.valueOf(this.buf[1], this.buf[2]);
    checkRecordVersion(protocolVersion, false);
    int i = ((this.buf[3] & 0xFF) << 8) + (this.buf[4] & 0xFF);
    if (i < 0 || i > 33300)
      throw new SSLProtocolException("Bad InputRecord size, count = " + i + ", buf.length = " + this.buf.length); 
    if (i > this.buf.length - 5) {
      byte[] arrayOfByte = new byte[i + 5];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, 5);
      this.buf = arrayOfByte;
    } 
    if (this.exlen < i + 5) {
      int j = readFully(paramInputStream, this.buf, this.exlen, i + 5 - this.exlen);
      if (j < 0)
        throw new SSLException("SSL peer shut down incorrectly"); 
    } 
    this.count = i + 5;
    this.exlen = 0;
    if (debug != null && Debug.isOn("record")) {
      if (this.count < 0 || this.count > 16916)
        System.out.println(Thread.currentThread().getName() + ", Bad InputRecord size" + ", count = " + this.count); 
      System.out.println(Thread.currentThread().getName() + ", READ: " + protocolVersion + " " + 
          
          contentName(contentType()) + ", length = " + available());
    } 
  }
  
  private void handleUnknownRecord(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    if ((this.buf[0] & 0x80) != 0 && this.buf[2] == 1) {
      if (this.helloVersion != ProtocolVersion.SSL20Hello)
        throw new SSLHandshakeException("SSLv2Hello is disabled"); 
      ProtocolVersion protocolVersion = ProtocolVersion.valueOf(this.buf[3], this.buf[4]);
      if (protocolVersion == ProtocolVersion.SSL20Hello) {
        try {
          writeBuffer(paramOutputStream, v2NoCipher, 0, v2NoCipher.length);
        } catch (Exception exception) {}
        throw new SSLException("Unsupported SSL v2.0 ClientHello");
      } 
      int i = ((this.buf[0] & Byte.MAX_VALUE) << 8) + (this.buf[1] & 0xFF) - 3;
      if (this.v2Buf == null)
        this.v2Buf = new byte[i]; 
      if (this.exlen < i + 5) {
        int j = readFully(paramInputStream, this.v2Buf, this.exlen - 5, i + 5 - this.exlen);
        if (j < 0)
          throw new EOFException("SSL peer shut down incorrectly"); 
      } 
      this.exlen = 0;
      hashInternal(this.buf, 2, 3);
      hashInternal(this.v2Buf, 0, i);
      V2toV3ClientHello(this.v2Buf);
      this.v2Buf = null;
      this.lastHashed = this.count;
      if (debug != null && Debug.isOn("record"))
        System.out.println(
            Thread.currentThread().getName() + ", READ:  SSL v2, contentType = " + 
            
            contentName(contentType()) + ", translated length = " + 
            available()); 
      return;
    } 
    if ((this.buf[0] & 0x80) != 0 && this.buf[2] == 4)
      throw new SSLException("SSL V2.0 servers are not supported."); 
    for (byte b = 0; b < v2NoCipher.length; b++) {
      if (this.buf[b] != v2NoCipher[b])
        throw new SSLException("Unrecognized SSL message, plaintext connection?"); 
    } 
    throw new SSLException("SSL V2.0 servers are not supported.");
  }
  
  void writeBuffer(OutputStream paramOutputStream, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    paramOutputStream.write(paramArrayOfbyte, 0, paramInt2);
    paramOutputStream.flush();
  }
  
  private void V2toV3ClientHello(byte[] paramArrayOfbyte) throws SSLException {
    this.buf[0] = 22;
    this.buf[1] = this.buf[3];
    this.buf[2] = this.buf[4];
    this.buf[5] = 1;
    this.buf[9] = this.buf[1];
    this.buf[10] = this.buf[2];
    this.count = 11;
    int i = ((paramArrayOfbyte[0] & 0xFF) << 8) + (paramArrayOfbyte[1] & 0xFF);
    int j = ((paramArrayOfbyte[2] & 0xFF) << 8) + (paramArrayOfbyte[3] & 0xFF);
    int k = ((paramArrayOfbyte[4] & 0xFF) << 8) + (paramArrayOfbyte[5] & 0xFF);
    int m = 6 + i + j;
    if (k < 32) {
      for (byte b1 = 0; b1 < 32 - k; b1++)
        this.buf[this.count++] = 0; 
      System.arraycopy(paramArrayOfbyte, m, this.buf, this.count, k);
      this.count += k;
    } else {
      System.arraycopy(paramArrayOfbyte, m + k - 32, this.buf, this.count, 32);
      this.count += 32;
    } 
    m -= j;
    this.buf[this.count++] = (byte)j;
    System.arraycopy(paramArrayOfbyte, m, this.buf, this.count, j);
    this.count += j;
    m -= i;
    int n = this.count + 2;
    for (byte b = 0; b < i; b += 3) {
      if (paramArrayOfbyte[m + b] == 0) {
        this.buf[n++] = paramArrayOfbyte[m + b + 1];
        this.buf[n++] = paramArrayOfbyte[m + b + 2];
      } 
    } 
    n -= this.count + 2;
    this.buf[this.count++] = (byte)(n >>> 8);
    this.buf[this.count++] = (byte)n;
    this.count += n;
    this.buf[this.count++] = 1;
    this.buf[this.count++] = 0;
    this.buf[3] = (byte)(this.count - 5);
    this.buf[4] = (byte)(this.count - 5 >>> 8);
    this.buf[6] = 0;
    this.buf[7] = (byte)(this.count - 5 - 4 >>> 8);
    this.buf[8] = (byte)(this.count - 5 - 4);
    this.pos = 5;
  }
  
  static String contentName(int paramInt) {
    switch (paramInt) {
      case 20:
        return "Change Cipher Spec";
      case 21:
        return "Alert";
      case 22:
        return "Handshake";
      case 23:
        return "Application Data";
    } 
    return "contentType = " + paramInt;
  }
}
