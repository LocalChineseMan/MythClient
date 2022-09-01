package sun.security.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Hashtable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import sun.misc.HexDumpEncoder;

final class CipherBox {
  static final CipherBox NULL = new CipherBox();
  
  private static final Debug debug = Debug.getInstance("ssl");
  
  private final ProtocolVersion protocolVersion;
  
  private final Cipher cipher;
  
  private SecureRandom random;
  
  private final byte[] fixedIv;
  
  private final Key key;
  
  private final int mode;
  
  private final int tagSize;
  
  private final int recordIvSize;
  
  private final CipherSuite.CipherType cipherType;
  
  private static Hashtable<Integer, IvParameterSpec> masks;
  
  private CipherBox() {
    this.protocolVersion = ProtocolVersion.DEFAULT;
    this.cipher = null;
    this.cipherType = CipherSuite.CipherType.STREAM_CIPHER;
    this.fixedIv = new byte[0];
    this.key = null;
    this.mode = 1;
    this.random = null;
    this.tagSize = 0;
    this.recordIvSize = 0;
  }
  
  private CipherBox(ProtocolVersion paramProtocolVersion, CipherSuite.BulkCipher paramBulkCipher, SecretKey paramSecretKey, IvParameterSpec paramIvParameterSpec, SecureRandom paramSecureRandom, boolean paramBoolean) throws NoSuchAlgorithmException {
    try {
      this.protocolVersion = paramProtocolVersion;
      this.cipher = JsseJce.getCipher(paramBulkCipher.transformation);
      this.mode = paramBoolean ? 1 : 2;
      if (paramSecureRandom == null)
        paramSecureRandom = JsseJce.getSecureRandom(); 
      this.random = paramSecureRandom;
      this.cipherType = paramBulkCipher.cipherType;
      if (paramIvParameterSpec == null && paramBulkCipher.ivSize != 0 && this.mode == 2 && paramProtocolVersion.v >= ProtocolVersion.TLS11.v)
        paramIvParameterSpec = getFixedMask(paramBulkCipher.ivSize); 
      if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
        paramBulkCipher.getClass();
        this.tagSize = 16;
        this.key = paramSecretKey;
        this.fixedIv = paramIvParameterSpec.getIV();
        if (this.fixedIv == null || this.fixedIv.length != paramBulkCipher.fixedIvSize)
          throw new RuntimeException("Improper fixed IV for AEAD"); 
        this.recordIvSize = paramBulkCipher.ivSize - paramBulkCipher.fixedIvSize;
      } else {
        this.tagSize = 0;
        this.fixedIv = new byte[0];
        this.recordIvSize = 0;
        this.key = null;
        this.cipher.init(this.mode, paramSecretKey, paramIvParameterSpec, paramSecureRandom);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw noSuchAlgorithmException;
    } catch (Exception exception) {
      throw new NoSuchAlgorithmException("Could not create cipher " + paramBulkCipher, exception);
    } catch (ExceptionInInitializerError exceptionInInitializerError) {
      throw new NoSuchAlgorithmException("Could not create cipher " + paramBulkCipher, exceptionInInitializerError);
    } 
  }
  
  static CipherBox newCipherBox(ProtocolVersion paramProtocolVersion, CipherSuite.BulkCipher paramBulkCipher, SecretKey paramSecretKey, IvParameterSpec paramIvParameterSpec, SecureRandom paramSecureRandom, boolean paramBoolean) throws NoSuchAlgorithmException {
    if (!paramBulkCipher.allowed)
      throw new NoSuchAlgorithmException("Unsupported cipher " + paramBulkCipher); 
    if (paramBulkCipher == CipherSuite.B_NULL)
      return NULL; 
    return new CipherBox(paramProtocolVersion, paramBulkCipher, paramSecretKey, paramIvParameterSpec, paramSecureRandom, paramBoolean);
  }
  
  private static IvParameterSpec getFixedMask(int paramInt) {
    if (masks == null)
      masks = new Hashtable<>(5); 
    IvParameterSpec ivParameterSpec = masks.get(Integer.valueOf(paramInt));
    if (ivParameterSpec == null) {
      ivParameterSpec = new IvParameterSpec(new byte[paramInt]);
      masks.put(Integer.valueOf(paramInt), ivParameterSpec);
    } 
    return ivParameterSpec;
  }
  
  int encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.cipher == null)
      return paramInt2; 
    try {
      int i = this.cipher.getBlockSize();
      if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER)
        paramInt2 = addPadding(paramArrayOfbyte, paramInt1, paramInt2, i); 
      if (debug != null && Debug.isOn("plaintext"))
        try {
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          System.out.println("Padded plaintext before ENCRYPTION:  len = " + paramInt2);
          hexDumpEncoder.encodeBuffer(new ByteArrayInputStream(paramArrayOfbyte, paramInt1, paramInt2), System.out);
        } catch (IOException iOException) {} 
      if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER)
        try {
          return this.cipher.doFinal(paramArrayOfbyte, paramInt1, paramInt2, paramArrayOfbyte, paramInt1);
        } catch (IllegalBlockSizeException|BadPaddingException illegalBlockSizeException) {
          throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher
              
              .getProvider().getName(), illegalBlockSizeException);
        }  
      int j = this.cipher.update(paramArrayOfbyte, paramInt1, paramInt2, paramArrayOfbyte, paramInt1);
      if (j != paramInt2)
        throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
            .getProvider().getName()); 
      return j;
    } catch (ShortBufferException shortBufferException) {
      throw new ArrayIndexOutOfBoundsException(shortBufferException.toString());
    } 
  }
  
  int encrypt(ByteBuffer paramByteBuffer, int paramInt) {
    int m, i = paramByteBuffer.remaining();
    if (this.cipher == null) {
      paramByteBuffer.position(paramByteBuffer.limit());
      return i;
    } 
    int j = paramByteBuffer.position();
    int k = this.cipher.getBlockSize();
    if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
      i = addPadding(paramByteBuffer, k);
      paramByteBuffer.position(j);
    } 
    if (debug != null && Debug.isOn("plaintext"))
      try {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        System.out.println("Padded plaintext before ENCRYPTION:  len = " + i);
        hexDumpEncoder.encodeBuffer(paramByteBuffer.duplicate(), System.out);
      } catch (IOException iOException) {} 
    ByteBuffer byteBuffer = paramByteBuffer.duplicate();
    if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER)
      try {
        m = this.cipher.getOutputSize(byteBuffer.remaining());
        if (m > paramByteBuffer.remaining()) {
          if (paramInt < j + m)
            throw new ShortBufferException("need more space in output buffer"); 
          paramByteBuffer.limit(j + m);
        } 
        int n = this.cipher.doFinal(byteBuffer, paramByteBuffer);
        if (n != m)
          throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
              
              .getProvider().getName()); 
        return n;
      } catch (IllegalBlockSizeException|BadPaddingException|ShortBufferException illegalBlockSizeException) {
        throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher
            
            .getProvider().getName(), illegalBlockSizeException);
      }  
    try {
      m = this.cipher.update(byteBuffer, paramByteBuffer);
    } catch (ShortBufferException shortBufferException) {
      throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
          .getProvider().getName());
    } 
    if (paramByteBuffer.position() != byteBuffer.position())
      throw new RuntimeException("bytebuffer padding error"); 
    if (m != i)
      throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
          .getProvider().getName()); 
    return m;
  }
  
  int decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws BadPaddingException {
    if (this.cipher == null)
      return paramInt2; 
    try {
      int i;
      if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
        try {
          i = this.cipher.doFinal(paramArrayOfbyte, paramInt1, paramInt2, paramArrayOfbyte, paramInt1);
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
          throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher
              
              .getProvider().getName(), illegalBlockSizeException);
        } 
      } else {
        i = this.cipher.update(paramArrayOfbyte, paramInt1, paramInt2, paramArrayOfbyte, paramInt1);
        if (i != paramInt2)
          throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
              .getProvider().getName()); 
      } 
      if (debug != null && Debug.isOn("plaintext"))
        try {
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          System.out.println("Padded plaintext after DECRYPTION:  len = " + i);
          hexDumpEncoder.encodeBuffer(new ByteArrayInputStream(paramArrayOfbyte, paramInt1, i), System.out);
        } catch (IOException iOException) {} 
      if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
        int j = this.cipher.getBlockSize();
        i = removePadding(paramArrayOfbyte, paramInt1, i, paramInt3, j, this.protocolVersion);
        if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && 
          i < j)
          throw new BadPaddingException("invalid explicit IV"); 
      } 
      return i;
    } catch (ShortBufferException shortBufferException) {
      throw new ArrayIndexOutOfBoundsException(shortBufferException.toString());
    } 
  }
  
  int decrypt(ByteBuffer paramByteBuffer, int paramInt) throws BadPaddingException {
    int i = paramByteBuffer.remaining();
    if (this.cipher == null) {
      paramByteBuffer.position(paramByteBuffer.limit());
      return i;
    } 
    try {
      int k, j = paramByteBuffer.position();
      ByteBuffer byteBuffer = paramByteBuffer.duplicate();
      if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
        try {
          k = this.cipher.doFinal(byteBuffer, paramByteBuffer);
        } catch (IllegalBlockSizeException illegalBlockSizeException) {
          throw new RuntimeException("Cipher error in AEAD mode \"" + illegalBlockSizeException
              .getMessage() + " \"in JCE provider " + this.cipher
              .getProvider().getName());
        } 
      } else {
        k = this.cipher.update(byteBuffer, paramByteBuffer);
        if (k != i)
          throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher
              .getProvider().getName()); 
      } 
      paramByteBuffer.limit(j + k);
      if (debug != null && Debug.isOn("plaintext"))
        try {
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          System.out.println("Padded plaintext after DECRYPTION:  len = " + k);
          hexDumpEncoder.encodeBuffer((ByteBuffer)paramByteBuffer
              .duplicate().position(j), System.out);
        } catch (IOException iOException) {} 
      if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
        int m = this.cipher.getBlockSize();
        paramByteBuffer.position(j);
        k = removePadding(paramByteBuffer, paramInt, m, this.protocolVersion);
        if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
          if (k < m)
            throw new BadPaddingException("invalid explicit IV"); 
          paramByteBuffer.position(paramByteBuffer.limit());
        } 
      } 
      return k;
    } catch (ShortBufferException shortBufferException) {
      throw new ArrayIndexOutOfBoundsException(shortBufferException.toString());
    } 
  }
  
  private static int addPadding(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2 + 1;
    if (i % paramInt3 != 0) {
      i += paramInt3 - 1;
      i -= i % paramInt3;
    } 
    byte b = (byte)(i - paramInt2);
    if (paramArrayOfbyte.length < i + paramInt1)
      throw new IllegalArgumentException("no space to pad buffer"); 
    for (byte b1 = 0; b1 < b; b1++)
      paramArrayOfbyte[paramInt1++] = (byte)(b - 1); 
    return i;
  }
  
  private static int addPadding(ByteBuffer paramByteBuffer, int paramInt) {
    int i = paramByteBuffer.remaining();
    int j = paramByteBuffer.position();
    int k = i + 1;
    if (k % paramInt != 0) {
      k += paramInt - 1;
      k -= k % paramInt;
    } 
    byte b = (byte)(k - i);
    paramByteBuffer.limit(k + j);
    for (byte b1 = 0; b1 < b; b1++)
      paramByteBuffer.put(j++, (byte)(b - 1)); 
    paramByteBuffer.position(j);
    paramByteBuffer.limit(j);
    return k;
  }
  
  private static int[] checkPadding(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, byte paramByte) {
    if (paramInt2 <= 0)
      throw new RuntimeException("padding len must be positive"); 
    int[] arrayOfInt = { 0, 0 };
    for (byte b = 0; b <= 'Ā';) {
      for (byte b1 = 0; b1 < paramInt2 && b <= 'Ā'; b1++, b++) {
        if (paramArrayOfbyte[paramInt1 + b1] != paramByte) {
          arrayOfInt[0] = arrayOfInt[0] + 1;
        } else {
          arrayOfInt[1] = arrayOfInt[1] + 1;
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  private static int[] checkPadding(ByteBuffer paramByteBuffer, byte paramByte) {
    if (!paramByteBuffer.hasRemaining())
      throw new RuntimeException("hasRemaining() must be positive"); 
    int[] arrayOfInt = { 0, 0 };
    paramByteBuffer.mark();
    for (byte b = 0; b <= 'Ā'; paramByteBuffer.reset()) {
      for (; paramByteBuffer.hasRemaining() && b <= 'Ā'; b++) {
        if (paramByteBuffer.get() != paramByte) {
          arrayOfInt[0] = arrayOfInt[0] + 1;
        } else {
          arrayOfInt[1] = arrayOfInt[1] + 1;
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  private static int removePadding(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ProtocolVersion paramProtocolVersion) throws BadPaddingException {
    int i = paramInt1 + paramInt2 - 1;
    int j = paramArrayOfbyte[i] & 0xFF;
    int k = paramInt2 - j + 1;
    if (k - paramInt3 < 0) {
      checkPadding(paramArrayOfbyte, paramInt1, paramInt2, (byte)(j & 0xFF));
      throw new BadPaddingException("Invalid Padding length: " + j);
    } 
    int[] arrayOfInt = checkPadding(paramArrayOfbyte, paramInt1 + k, j + 1, (byte)(j & 0xFF));
    if (paramProtocolVersion.v >= ProtocolVersion.TLS10.v) {
      if (arrayOfInt[0] != 0)
        throw new BadPaddingException("Invalid TLS padding data"); 
    } else if (j > paramInt4) {
      throw new BadPaddingException("Invalid SSLv3 padding");
    } 
    return k;
  }
  
  private static int removePadding(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, ProtocolVersion paramProtocolVersion) throws BadPaddingException {
    int i = paramByteBuffer.remaining();
    int j = paramByteBuffer.position();
    int k = j + i - 1;
    int m = paramByteBuffer.get(k) & 0xFF;
    int n = i - m + 1;
    if (n - paramInt1 < 0) {
      checkPadding(paramByteBuffer.duplicate(), (byte)(m & 0xFF));
      throw new BadPaddingException("Invalid Padding length: " + m);
    } 
    int[] arrayOfInt = checkPadding((ByteBuffer)paramByteBuffer
        .duplicate().position(j + n), (byte)(m & 0xFF));
    if (paramProtocolVersion.v >= ProtocolVersion.TLS10.v) {
      if (arrayOfInt[0] != 0)
        throw new BadPaddingException("Invalid TLS padding data"); 
    } else if (m > paramInt2) {
      throw new BadPaddingException("Invalid SSLv3 padding");
    } 
    paramByteBuffer.position(j + n);
    paramByteBuffer.limit(j + n);
    return n;
  }
  
  void dispose() {
    try {
      if (this.cipher != null)
        this.cipher.doFinal(); 
    } catch (Exception exception) {}
  }
  
  boolean isCBCMode() {
    return (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER);
  }
  
  boolean isAEADMode() {
    return (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER);
  }
  
  boolean isNullCipher() {
    return (this.cipher == null);
  }
  
  int getExplicitNonceSize() {
    switch (this.cipherType) {
      case BLOCK_CIPHER:
        if (this.protocolVersion.v >= ProtocolVersion.TLS11.v)
          return this.cipher.getBlockSize(); 
        break;
      case AEAD_CIPHER:
        return this.recordIvSize;
    } 
    return 0;
  }
  
  int applyExplicitNonce(Authenticator paramAuthenticator, byte paramByte, ByteBuffer paramByteBuffer) throws BadPaddingException {
    boolean bool;
    byte[] arrayOfByte1;
    GCMParameterSpec gCMParameterSpec;
    byte[] arrayOfByte2;
    switch (this.cipherType) {
      case BLOCK_CIPHER:
        bool = (paramAuthenticator instanceof MAC) ? ((MAC)paramAuthenticator).MAClen() : false;
        if (bool && 
          !sanityCheck(bool, paramByteBuffer.remaining()))
          throw new BadPaddingException("ciphertext sanity check failed"); 
        if (this.protocolVersion.v >= ProtocolVersion.TLS11.v)
          return this.cipher.getBlockSize(); 
        break;
      case AEAD_CIPHER:
        if (paramByteBuffer.remaining() < this.recordIvSize + this.tagSize)
          throw new BadPaddingException("invalid AEAD cipher fragment"); 
        arrayOfByte1 = Arrays.copyOf(this.fixedIv, this.fixedIv.length + this.recordIvSize);
        paramByteBuffer.get(arrayOfByte1, this.fixedIv.length, this.recordIvSize);
        paramByteBuffer.position(paramByteBuffer.position() - this.recordIvSize);
        gCMParameterSpec = new GCMParameterSpec(this.tagSize * 8, arrayOfByte1);
        try {
          this.cipher.init(this.mode, this.key, gCMParameterSpec, this.random);
        } catch (InvalidKeyException|java.security.InvalidAlgorithmParameterException invalidKeyException) {
          throw new RuntimeException("invalid key or spec in GCM mode", invalidKeyException);
        } 
        arrayOfByte2 = paramAuthenticator.acquireAuthenticationBytes(paramByte, paramByteBuffer
            .remaining() - this.recordIvSize - this.tagSize);
        this.cipher.updateAAD(arrayOfByte2);
        return this.recordIvSize;
    } 
    return 0;
  }
  
  int applyExplicitNonce(Authenticator paramAuthenticator, byte paramByte, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException {
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte, paramInt1, paramInt2);
    return applyExplicitNonce(paramAuthenticator, paramByte, byteBuffer);
  }
  
  byte[] createExplicitNonce(Authenticator paramAuthenticator, byte paramByte, int paramInt) {
    byte[] arrayOfByte2;
    GCMParameterSpec gCMParameterSpec;
    byte[] arrayOfByte3, arrayOfByte1 = new byte[0];
    switch (this.cipherType) {
      case BLOCK_CIPHER:
        if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
          arrayOfByte1 = new byte[this.cipher.getBlockSize()];
          this.random.nextBytes(arrayOfByte1);
        } 
        break;
      case AEAD_CIPHER:
        arrayOfByte1 = paramAuthenticator.sequenceNumber();
        arrayOfByte2 = Arrays.copyOf(this.fixedIv, this.fixedIv.length + arrayOfByte1.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, this.fixedIv.length, arrayOfByte1.length);
        gCMParameterSpec = new GCMParameterSpec(this.tagSize * 8, arrayOfByte2);
        try {
          this.cipher.init(this.mode, this.key, gCMParameterSpec, this.random);
        } catch (InvalidKeyException|java.security.InvalidAlgorithmParameterException invalidKeyException) {
          throw new RuntimeException("invalid key or spec in GCM mode", invalidKeyException);
        } 
        arrayOfByte3 = paramAuthenticator.acquireAuthenticationBytes(paramByte, paramInt);
        this.cipher.updateAAD(arrayOfByte3);
        break;
    } 
    return arrayOfByte1;
  }
  
  Boolean isAvailable() {
    if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER)
      try {
        Authenticator authenticator = new Authenticator(this.protocolVersion);
        byte[] arrayOfByte1 = authenticator.sequenceNumber();
        byte[] arrayOfByte2 = Arrays.copyOf(this.fixedIv, this.fixedIv.length + arrayOfByte1.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, this.fixedIv.length, arrayOfByte1.length);
        GCMParameterSpec gCMParameterSpec = new GCMParameterSpec(this.tagSize * 8, arrayOfByte2);
        this.cipher.init(this.mode, this.key, gCMParameterSpec, this.random);
      } catch (Exception exception) {
        return Boolean.FALSE;
      }  
    return Boolean.TRUE;
  }
  
  private boolean sanityCheck(int paramInt1, int paramInt2) {
    if (!isCBCMode())
      return (paramInt2 >= paramInt1); 
    int i = this.cipher.getBlockSize();
    if (paramInt2 % i == 0) {
      int j = paramInt1 + 1;
      j = (j >= i) ? j : i;
      if (this.protocolVersion.v >= ProtocolVersion.TLS11.v)
        j += i; 
      return (paramInt2 >= j);
    } 
    return false;
  }
}
