package sun.security.ssl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import sun.misc.HexDumpEncoder;

class OutputRecord extends ByteArrayOutputStream implements Record {
  private HandshakeHash handshakeHash;
  
  private int lastHashed;
  
  private boolean firstMessage;
  
  private final byte contentType;
  
  private int headerOffset;
  
  ProtocolVersion protocolVersion;
  
  private ProtocolVersion helloVersion;
  
  static final Debug debug = Debug.getInstance("ssl");
  
  OutputRecord(byte paramByte, int paramInt) {
    super(paramInt);
    this.protocolVersion = ProtocolVersion.DEFAULT;
    this.helloVersion = ProtocolVersion.DEFAULT_HELLO;
    this.firstMessage = true;
    this.count = 261;
    this.contentType = paramByte;
    this.lastHashed = this.count;
    this.headerOffset = 256;
  }
  
  OutputRecord(byte paramByte) {
    this(paramByte, recordSize(paramByte));
  }
  
  private static int recordSize(byte paramByte) {
    if (paramByte == 20 || paramByte == 21)
      return 539; 
    return 16921;
  }
  
  synchronized void setVersion(ProtocolVersion paramProtocolVersion) {
    this.protocolVersion = paramProtocolVersion;
  }
  
  synchronized void setHelloVersion(ProtocolVersion paramProtocolVersion) {
    this.helloVersion = paramProtocolVersion;
  }
  
  public synchronized void reset() {
    super.reset();
    this.count = 261;
    this.lastHashed = this.count;
    this.headerOffset = 256;
  }
  
  void setHandshakeHash(HandshakeHash paramHandshakeHash) {
    assert this.contentType == 22;
    this.handshakeHash = paramHandshakeHash;
  }
  
  void doHashes() {
    int i = this.count - this.lastHashed;
    if (i > 0) {
      hashInternal(this.buf, this.lastHashed, i);
      this.lastHashed = this.count;
    } 
  }
  
  private void hashInternal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (debug != null && Debug.isOn("data"))
      try {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        System.out.println("[write] MD5 and SHA1 hashes:  len = " + paramInt2);
        hexDumpEncoder.encodeBuffer(new ByteArrayInputStream(paramArrayOfbyte, this.lastHashed, paramInt2), System.out);
      } catch (IOException iOException) {} 
    this.handshakeHash.update(paramArrayOfbyte, this.lastHashed, paramInt2);
    this.lastHashed = this.count;
  }
  
  boolean isEmpty() {
    return (this.count == 261);
  }
  
  boolean isAlert(byte paramByte) {
    if (this.count > 262 && this.contentType == 21)
      return (this.buf[262] == paramByte); 
    return false;
  }
  
  void encrypt(Authenticator paramAuthenticator, CipherBox paramCipherBox) throws IOException {
    if (this.contentType == 22)
      doHashes(); 
    if (paramAuthenticator instanceof MAC) {
      MAC mAC = (MAC)paramAuthenticator;
      if (mAC.MAClen() != 0) {
        byte[] arrayOfByte = mAC.compute(this.contentType, this.buf, 261, this.count - 261, false);
        write(arrayOfByte);
      } 
    } 
    if (!paramCipherBox.isNullCipher()) {
      if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && (paramCipherBox
        .isCBCMode() || paramCipherBox.isAEADMode())) {
        byte[] arrayOfByte = paramCipherBox.createExplicitNonce(paramAuthenticator, this.contentType, this.count - 261);
        int j = 261 - arrayOfByte.length;
        System.arraycopy(arrayOfByte, 0, this.buf, j, arrayOfByte.length);
        this.headerOffset = j - 5;
      } else {
        this.headerOffset = 256;
      } 
      int i = 261;
      if (!paramCipherBox.isAEADMode())
        i = this.headerOffset + 5; 
      this.count = i + paramCipherBox.encrypt(this.buf, i, this.count - i);
    } 
  }
  
  final int availableDataBytes() {
    int i = this.count - 261;
    return 16384 - i;
  }
  
  private void ensureCapacity(int paramInt) {
    if (paramInt > this.buf.length)
      this.buf = Arrays.copyOf(this.buf, paramInt); 
  }
  
  final byte contentType() {
    return this.contentType;
  }
  
  void write(OutputStream paramOutputStream, boolean paramBoolean, ByteArrayOutputStream paramByteArrayOutputStream) throws IOException {
    if (this.count == 261)
      return; 
    int i = this.count - this.headerOffset - 5;
    if (i < 0)
      throw new SSLException("output record size too small: " + i); 
    if (debug != null && (
      Debug.isOn("record") || Debug.isOn("handshake")) && ((
      debug != null && Debug.isOn("record")) || 
      contentType() == 20))
      System.out.println(Thread.currentThread().getName() + ", WRITE: " + this.protocolVersion + " " + 
          
          InputRecord.contentName(contentType()) + ", length = " + i); 
    if (this.firstMessage && useV2Hello()) {
      byte[] arrayOfByte = new byte[i - 4];
      System.arraycopy(this.buf, 265, arrayOfByte, 0, arrayOfByte.length);
      this.headerOffset = 0;
      V3toV2ClientHello(arrayOfByte);
      this.handshakeHash.reset();
      this.lastHashed = 2;
      doHashes();
      if (debug != null && Debug.isOn("record"))
        System.out.println(
            Thread.currentThread().getName() + ", WRITE: SSLv2 client hello message" + ", length = " + (this.count - 2)); 
    } else {
      this.buf[this.headerOffset + 0] = this.contentType;
      this.buf[this.headerOffset + 1] = this.protocolVersion.major;
      this.buf[this.headerOffset + 2] = this.protocolVersion.minor;
      this.buf[this.headerOffset + 3] = (byte)(i >> 8);
      this.buf[this.headerOffset + 4] = (byte)i;
    } 
    this.firstMessage = false;
    int j = 0;
    if (paramBoolean) {
      writeBuffer(paramByteArrayOutputStream, this.buf, this.headerOffset, this.count - this.headerOffset, j);
    } else {
      if (paramByteArrayOutputStream != null && paramByteArrayOutputStream.size() > 0) {
        int k = paramByteArrayOutputStream.size();
        int m = this.count + k - this.headerOffset;
        ensureCapacity(m);
        System.arraycopy(this.buf, this.headerOffset, this.buf, k, this.count - this.headerOffset);
        System.arraycopy(paramByteArrayOutputStream
            .toByteArray(), 0, this.buf, 0, k);
        this.count = m;
        this.headerOffset = 0;
        paramByteArrayOutputStream.reset();
        j = k;
      } 
      writeBuffer(paramOutputStream, this.buf, this.headerOffset, this.count - this.headerOffset, j);
    } 
    reset();
  }
  
  void writeBuffer(OutputStream paramOutputStream, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    paramOutputStream.write(paramArrayOfbyte, paramInt1, paramInt2);
    paramOutputStream.flush();
    if (debug != null && Debug.isOn("packet"))
      try {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        System.out.println("[Raw write]: length = " + (paramInt2 - paramInt3));
        hexDumpEncoder.encodeBuffer(new ByteArrayInputStream(paramArrayOfbyte, paramInt1 + paramInt3, paramInt2 - paramInt3), System.out);
      } catch (IOException iOException) {} 
  }
  
  private boolean useV2Hello() {
    return (this.firstMessage && this.helloVersion == ProtocolVersion.SSL20Hello && this.contentType == 22 && this.buf[this.headerOffset + 5] == 1 && this.buf[299] == 0);
  }
  
  private void V3toV2ClientHello(byte[] paramArrayOfbyte) throws SSLException {
    byte b1 = 34;
    byte b = paramArrayOfbyte[b1];
    int i = b1 + 1 + b;
    int j = ((paramArrayOfbyte[i] & 0xFF) << 8) + (paramArrayOfbyte[i + 1] & 0xFF);
    int k = j / 2;
    int m = i + 2;
    int n = 0;
    this.count = 11;
    boolean bool = false;
    for (byte b2 = 0; b2 < k; b2++) {
      byte b3 = paramArrayOfbyte[m++];
      byte b4 = paramArrayOfbyte[m++];
      n += V3toV2CipherSuite(b3, b4);
      if (!bool && b3 == 0 && b4 == -1)
        bool = true; 
    } 
    if (!bool)
      n += V3toV2CipherSuite((byte)0, (byte)-1); 
    this.buf[2] = 1;
    this.buf[3] = paramArrayOfbyte[0];
    this.buf[4] = paramArrayOfbyte[1];
    this.buf[5] = (byte)(n >>> 8);
    this.buf[6] = (byte)n;
    this.buf[7] = 0;
    this.buf[8] = 0;
    this.buf[9] = 0;
    this.buf[10] = 32;
    System.arraycopy(paramArrayOfbyte, 2, this.buf, this.count, 32);
    this.count += 32;
    this.count -= 2;
    this.buf[0] = (byte)(this.count >>> 8);
    this.buf[0] = (byte)(this.buf[0] | 0x80);
    this.buf[1] = (byte)this.count;
    this.count += 2;
  }
  
  private static int[] V3toV2CipherMap1 = new int[] { 
      -1, -1, -1, 2, 1, -1, 4, 5, -1, 6, 
      7 };
  
  private static int[] V3toV2CipherMap3 = new int[] { 
      -1, -1, -1, 128, 128, -1, 128, 128, -1, 64, 
      192 };
  
  private int V3toV2CipherSuite(byte paramByte1, byte paramByte2) {
    this.buf[this.count++] = 0;
    this.buf[this.count++] = paramByte1;
    this.buf[this.count++] = paramByte2;
    if ((paramByte2 & 0xFF) > 10 || V3toV2CipherMap1[paramByte2] == -1)
      return 3; 
    this.buf[this.count++] = (byte)V3toV2CipherMap1[paramByte2];
    this.buf[this.count++] = 0;
    this.buf[this.count++] = (byte)V3toV2CipherMap3[paramByte2];
    return 6;
  }
}
