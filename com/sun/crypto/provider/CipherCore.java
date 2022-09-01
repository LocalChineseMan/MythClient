package com.sun.crypto.provider;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;

final class CipherCore {
  private byte[] buffer = null;
  
  private int blockSize = 0;
  
  private int unitBytes = 0;
  
  private int buffered = 0;
  
  private int minBytes = 0;
  
  private int diffBlocksize = 0;
  
  private Padding padding = null;
  
  private FeedbackCipher cipher = null;
  
  private int cipherMode = 0;
  
  private boolean decrypting = false;
  
  private static final int ECB_MODE = 0;
  
  private static final int CBC_MODE = 1;
  
  private static final int CFB_MODE = 2;
  
  private static final int OFB_MODE = 3;
  
  private static final int PCBC_MODE = 4;
  
  private static final int CTR_MODE = 5;
  
  private static final int CTS_MODE = 6;
  
  private static final int GCM_MODE = 7;
  
  private boolean requireReinit = false;
  
  private byte[] lastEncKey = null;
  
  private byte[] lastEncIv = null;
  
  CipherCore(SymmetricCipher paramSymmetricCipher, int paramInt) {
    this.blockSize = paramInt;
    this.unitBytes = paramInt;
    this.diffBlocksize = paramInt;
    this.buffer = new byte[this.blockSize * 2];
    this.cipher = new ElectronicCodeBook(paramSymmetricCipher);
    this.padding = new PKCS5Padding(this.blockSize);
  }
  
  void setMode(String paramString) throws NoSuchAlgorithmException {
    if (paramString == null)
      throw new NoSuchAlgorithmException("null mode"); 
    String str = paramString.toUpperCase(Locale.ENGLISH);
    if (str.equals("ECB"))
      return; 
    SymmetricCipher symmetricCipher = this.cipher.getEmbeddedCipher();
    if (str.equals("CBC")) {
      this.cipherMode = 1;
      this.cipher = new CipherBlockChaining(symmetricCipher);
    } else if (str.equals("CTS")) {
      this.cipherMode = 6;
      this.cipher = new CipherTextStealing(symmetricCipher);
      this.minBytes = this.blockSize + 1;
      this.padding = null;
    } else if (str.equals("CTR")) {
      this.cipherMode = 5;
      this.cipher = new CounterMode(symmetricCipher);
      this.unitBytes = 1;
      this.padding = null;
    } else if (str.startsWith("GCM")) {
      if (this.blockSize != 16)
        throw new NoSuchAlgorithmException("GCM mode can only be used for AES cipher"); 
      this.cipherMode = 7;
      this.cipher = new GaloisCounterMode(symmetricCipher);
      this.padding = null;
    } else if (str.startsWith("CFB")) {
      this.cipherMode = 2;
      this.unitBytes = getNumOfUnit(paramString, "CFB".length(), this.blockSize);
      this.cipher = new CipherFeedback(symmetricCipher, this.unitBytes);
    } else if (str.startsWith("OFB")) {
      this.cipherMode = 3;
      this.unitBytes = getNumOfUnit(paramString, "OFB".length(), this.blockSize);
      this.cipher = new OutputFeedback(symmetricCipher, this.unitBytes);
    } else if (str.equals("PCBC")) {
      this.cipherMode = 4;
      this.cipher = new PCBC(symmetricCipher);
    } else {
      throw new NoSuchAlgorithmException("Cipher mode: " + paramString + " not found");
    } 
  }
  
  private static int getNumOfUnit(String paramString, int paramInt1, int paramInt2) throws NoSuchAlgorithmException {
    int i = paramInt2;
    if (paramString.length() > paramInt1) {
      int j;
      try {
        Integer integer = Integer.valueOf(paramString.substring(paramInt1));
        j = integer.intValue();
        i = j >> 3;
      } catch (NumberFormatException numberFormatException) {
        throw new NoSuchAlgorithmException("Algorithm mode: " + paramString + " not implemented");
      } 
      if (j % 8 != 0 || i > paramInt2)
        throw new NoSuchAlgorithmException("Invalid algorithm mode: " + paramString); 
    } 
    return i;
  }
  
  void setPadding(String paramString) throws NoSuchPaddingException {
    if (paramString == null)
      throw new NoSuchPaddingException("null padding"); 
    if (paramString.equalsIgnoreCase("NoPadding")) {
      this.padding = null;
    } else if (paramString.equalsIgnoreCase("ISO10126Padding")) {
      this.padding = new ISO10126Padding(this.blockSize);
    } else if (!paramString.equalsIgnoreCase("PKCS5Padding")) {
      throw new NoSuchPaddingException("Padding: " + paramString + " not implemented");
    } 
    if (this.padding != null && (this.cipherMode == 5 || this.cipherMode == 6 || this.cipherMode == 7)) {
      this.padding = null;
      String str = null;
      switch (this.cipherMode) {
        case 5:
          str = "CTR";
          break;
        case 7:
          str = "GCM";
          break;
        case 6:
          str = "CTS";
          break;
      } 
      if (str != null)
        throw new NoSuchPaddingException(str + " mode must be used with NoPadding"); 
    } 
  }
  
  int getOutputSize(int paramInt) {
    return getOutputSizeByOperation(paramInt, true);
  }
  
  private int getOutputSizeByOperation(int paramInt, boolean paramBoolean) {
    int i = this.buffered + paramInt + this.cipher.getBufferedLength();
    switch (this.cipherMode) {
      case 7:
        if (paramBoolean) {
          int j = ((GaloisCounterMode)this.cipher).getTagLen();
          if (!this.decrypting) {
            i += j;
          } else {
            i -= j;
          } 
        } 
        if (i < 0)
          i = 0; 
        return i;
    } 
    if (this.padding != null && !this.decrypting)
      if (this.unitBytes != this.blockSize) {
        if (i < this.diffBlocksize) {
          i = this.diffBlocksize;
        } else {
          int j = (i - this.diffBlocksize) % this.blockSize;
          i += this.blockSize - j;
        } 
      } else {
        i += this.padding.padLength(i);
      }  
    return i;
  }
  
  byte[] getIV() {
    byte[] arrayOfByte = this.cipher.getIV();
    return (arrayOfByte == null) ? null : (byte[])arrayOfByte.clone();
  }
  
  AlgorithmParameters getParameters(String paramString) {
    IvParameterSpec ivParameterSpec;
    if (this.cipherMode == 0)
      return null; 
    AlgorithmParameters algorithmParameters = null;
    byte[] arrayOfByte = getIV();
    if (arrayOfByte == null) {
      if (this.cipherMode == 7) {
        arrayOfByte = new byte[GaloisCounterMode.DEFAULT_IV_LEN];
      } else {
        arrayOfByte = new byte[this.blockSize];
      } 
      SunJCE.getRandom().nextBytes(arrayOfByte);
    } 
    if (this.cipherMode == 7) {
      paramString = "GCM";
      GCMParameterSpec gCMParameterSpec = new GCMParameterSpec(((GaloisCounterMode)this.cipher).getTagLen() * 8, arrayOfByte);
    } else if (paramString.equals("RC2")) {
      RC2Crypt rC2Crypt = (RC2Crypt)this.cipher.getEmbeddedCipher();
      RC2ParameterSpec rC2ParameterSpec = new RC2ParameterSpec(rC2Crypt.getEffectiveKeyBits(), arrayOfByte);
    } else {
      ivParameterSpec = new IvParameterSpec(arrayOfByte);
    } 
    try {
      algorithmParameters = AlgorithmParameters.getInstance(paramString, 
          SunJCE.getInstance());
      algorithmParameters.init(ivParameterSpec);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException("Cannot find " + paramString + " AlgorithmParameters implementation in SunJCE provider");
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new RuntimeException(ivParameterSpec.getClass() + " not supported");
    } 
    return algorithmParameters;
  }
  
  void init(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      init(paramInt, paramKey, (AlgorithmParameterSpec)null, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidKeyException(invalidAlgorithmParameterException.getMessage());
    } 
  }
  
  void init(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    this.decrypting = (paramInt == 2 || paramInt == 4);
    byte[] arrayOfByte1 = getKeyBytes(paramKey);
    int i = -1;
    byte[] arrayOfByte2 = null;
    if (paramAlgorithmParameterSpec != null)
      if (this.cipherMode == 7) {
        if (paramAlgorithmParameterSpec instanceof GCMParameterSpec) {
          i = ((GCMParameterSpec)paramAlgorithmParameterSpec).getTLen();
          if (i < 96 || i > 128 || (i & 0x7) != 0)
            throw new InvalidAlgorithmParameterException("Unsupported TLen value; must be one of {128, 120, 112, 104, 96}"); 
          i >>= 3;
          arrayOfByte2 = ((GCMParameterSpec)paramAlgorithmParameterSpec).getIV();
        } else {
          throw new InvalidAlgorithmParameterException("Unsupported parameter: " + paramAlgorithmParameterSpec);
        } 
      } else if (paramAlgorithmParameterSpec instanceof IvParameterSpec) {
        arrayOfByte2 = ((IvParameterSpec)paramAlgorithmParameterSpec).getIV();
        if (arrayOfByte2 == null || arrayOfByte2.length != this.blockSize)
          throw new InvalidAlgorithmParameterException("Wrong IV length: must be " + this.blockSize + " bytes long"); 
      } else if (paramAlgorithmParameterSpec instanceof RC2ParameterSpec) {
        arrayOfByte2 = ((RC2ParameterSpec)paramAlgorithmParameterSpec).getIV();
        if (arrayOfByte2 != null && arrayOfByte2.length != this.blockSize)
          throw new InvalidAlgorithmParameterException("Wrong IV length: must be " + this.blockSize + " bytes long"); 
      } else {
        throw new InvalidAlgorithmParameterException("Unsupported parameter: " + paramAlgorithmParameterSpec);
      }  
    if (this.cipherMode == 0) {
      if (arrayOfByte2 != null)
        throw new InvalidAlgorithmParameterException("ECB mode cannot use IV"); 
    } else if (arrayOfByte2 == null) {
      if (this.decrypting)
        throw new InvalidAlgorithmParameterException("Parameters missing"); 
      if (paramSecureRandom == null)
        paramSecureRandom = SunJCE.getRandom(); 
      if (this.cipherMode == 7) {
        arrayOfByte2 = new byte[GaloisCounterMode.DEFAULT_IV_LEN];
      } else {
        arrayOfByte2 = new byte[this.blockSize];
      } 
      paramSecureRandom.nextBytes(arrayOfByte2);
    } 
    this.buffered = 0;
    this.diffBlocksize = this.blockSize;
    String str = paramKey.getAlgorithm();
    if (this.cipherMode == 7) {
      if (i == -1)
        i = GaloisCounterMode.DEFAULT_TAG_LEN; 
      if (this.decrypting) {
        this.minBytes = i;
      } else {
        this
          
          .requireReinit = (Arrays.equals(arrayOfByte2, this.lastEncIv) && MessageDigest.isEqual(arrayOfByte1, this.lastEncKey));
        if (this.requireReinit)
          throw new InvalidAlgorithmParameterException("Cannot reuse iv for GCM encryption"); 
        this.lastEncIv = arrayOfByte2;
        this.lastEncKey = arrayOfByte1;
      } 
      ((GaloisCounterMode)this.cipher)
        .init(this.decrypting, str, arrayOfByte1, arrayOfByte2, i);
    } else {
      this.cipher.init(this.decrypting, str, arrayOfByte1, arrayOfByte2);
    } 
    this.requireReinit = false;
  }
  
  void init(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    AlgorithmParameterSpec algorithmParameterSpec = null;
    String str = null;
    if (paramAlgorithmParameters != null)
      try {
        if (this.cipherMode == 7) {
          str = "GCM";
          algorithmParameterSpec = paramAlgorithmParameters.getParameterSpec((Class)GCMParameterSpec.class);
        } else {
          str = "IV";
          algorithmParameterSpec = paramAlgorithmParameters.getParameterSpec((Class)IvParameterSpec.class);
        } 
      } catch (InvalidParameterSpecException invalidParameterSpecException) {
        throw new InvalidAlgorithmParameterException("Wrong parameter type: " + str + " expected");
      }  
    init(paramInt, paramKey, algorithmParameterSpec, paramSecureRandom);
  }
  
  static byte[] getKeyBytes(Key paramKey) throws InvalidKeyException {
    if (paramKey == null)
      throw new InvalidKeyException("No key given"); 
    if (!"RAW".equalsIgnoreCase(paramKey.getFormat()))
      throw new InvalidKeyException("Wrong format: RAW bytes needed"); 
    byte[] arrayOfByte = paramKey.getEncoded();
    if (arrayOfByte == null)
      throw new InvalidKeyException("RAW key bytes missing"); 
    return arrayOfByte;
  }
  
  byte[] update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.requireReinit)
      throw new IllegalStateException("Must use either different key or iv for GCM encryption"); 
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = new byte[getOutputSizeByOperation(paramInt2, false)];
      int i = update(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
      if (i == arrayOfByte.length)
        return arrayOfByte; 
      return Arrays.copyOf(arrayOfByte, i);
    } catch (ShortBufferException shortBufferException) {
      throw new ProviderException("Unexpected exception", shortBufferException);
    } 
  }
  
  int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException {
    if (this.requireReinit)
      throw new IllegalStateException("Must use either different key or iv for GCM encryption"); 
    int i = this.buffered + paramInt2 - this.minBytes;
    if (this.padding != null && this.decrypting)
      i -= this.blockSize; 
    i = (i > 0) ? (i - i % this.unitBytes) : 0;
    if (paramArrayOfbyte2 == null || paramArrayOfbyte2.length - paramInt3 < i)
      throw new ShortBufferException("Output buffer must be (at least) " + i + " bytes long"); 
    int j = 0;
    if (i != 0) {
      if (i <= this.buffered) {
        if (this.decrypting) {
          j = this.cipher.decrypt(this.buffer, 0, i, paramArrayOfbyte2, paramInt3);
        } else {
          j = this.cipher.encrypt(this.buffer, 0, i, paramArrayOfbyte2, paramInt3);
        } 
        this.buffered -= i;
        if (this.buffered != 0)
          System.arraycopy(this.buffer, i, this.buffer, 0, this.buffered); 
      } else if (paramArrayOfbyte1 != paramArrayOfbyte2 && this.buffered == 0) {
        if (this.decrypting) {
          j = this.cipher.decrypt(paramArrayOfbyte1, paramInt1, i, paramArrayOfbyte2, paramInt3);
        } else {
          j = this.cipher.encrypt(paramArrayOfbyte1, paramInt1, i, paramArrayOfbyte2, paramInt3);
        } 
        paramInt1 += i;
        paramInt2 -= i;
      } else {
        byte[] arrayOfByte = new byte[i];
        int k = i - this.buffered;
        if (this.buffered != 0) {
          System.arraycopy(this.buffer, 0, arrayOfByte, 0, this.buffered);
          this.buffered = 0;
        } 
        if (k != 0) {
          System.arraycopy(paramArrayOfbyte1, paramInt1, arrayOfByte, i - k, k);
          paramInt1 += k;
          paramInt2 -= k;
        } 
        if (this.decrypting) {
          j = this.cipher.decrypt(arrayOfByte, 0, i, paramArrayOfbyte2, paramInt3);
        } else {
          j = this.cipher.encrypt(arrayOfByte, 0, i, paramArrayOfbyte2, paramInt3);
        } 
      } 
      if (this.unitBytes != this.blockSize)
        if (i < this.diffBlocksize) {
          this.diffBlocksize -= i;
        } else {
          this.diffBlocksize = this.blockSize - (i - this.diffBlocksize) % this.blockSize;
        }  
    } 
    if (paramInt2 > 0) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.buffer, this.buffered, paramInt2);
      this.buffered += paramInt2;
    } 
    return j;
  }
  
  byte[] doFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = new byte[getOutputSizeByOperation(paramInt2, true)];
      int i = doFinal(paramArrayOfbyte, paramInt1, paramInt2, arrayOfByte, 0);
      if (i < arrayOfByte.length)
        return Arrays.copyOf(arrayOfByte, i); 
      return arrayOfByte;
    } catch (ShortBufferException shortBufferException) {
      throw new ProviderException("Unexpected exception", shortBufferException);
    } 
  }
  
  int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
    if (this.requireReinit)
      throw new IllegalStateException("Must use either different key or iv for GCM encryption"); 
    int i = getOutputSizeByOperation(paramInt2, true);
    int j = paramArrayOfbyte2.length - paramInt3;
    int k = this.decrypting ? (i - this.blockSize) : i;
    if (paramArrayOfbyte2 == null || j < k)
      throw new ShortBufferException("Output buffer must be (at least) " + k + " bytes long"); 
    int m = this.buffered + paramInt2;
    int n = m + this.cipher.getBufferedLength();
    int i1 = 0;
    if (this.unitBytes != this.blockSize) {
      if (n < this.diffBlocksize) {
        i1 = this.diffBlocksize - n;
      } else {
        i1 = this.blockSize - (n - this.diffBlocksize) % this.blockSize;
      } 
    } else if (this.padding != null) {
      i1 = this.padding.padLength(n);
    } 
    if (this.decrypting && this.padding != null && i1 > 0 && i1 != this.blockSize)
      throw new IllegalBlockSizeException("Input length must be multiple of " + this.blockSize + " when decrypting with padded cipher"); 
    byte[] arrayOfByte = paramArrayOfbyte1;
    int i2 = paramInt1;
    int i3 = paramInt2;
    if (paramArrayOfbyte1 == paramArrayOfbyte2 || this.buffered != 0 || (!this.decrypting && this.padding != null)) {
      if (this.decrypting || this.padding == null)
        i1 = 0; 
      arrayOfByte = new byte[m + i1];
      i2 = 0;
      if (this.buffered != 0)
        System.arraycopy(this.buffer, 0, arrayOfByte, 0, this.buffered); 
      if (paramInt2 != 0)
        System.arraycopy(paramArrayOfbyte1, paramInt1, arrayOfByte, this.buffered, paramInt2); 
      if (i1 != 0)
        this.padding.padWithLen(arrayOfByte, this.buffered + paramInt2, i1); 
      i3 = arrayOfByte.length;
    } 
    int i4 = 0;
    if (this.decrypting) {
      if (j < i)
        this.cipher.save(); 
      byte[] arrayOfByte1 = new byte[i];
      i4 = finalNoPadding(arrayOfByte, i2, arrayOfByte1, 0, i3);
      if (this.padding != null) {
        int i5 = this.padding.unpad(arrayOfByte1, 0, i4);
        if (i5 < 0)
          throw new BadPaddingException("Given final block not properly padded"); 
        i4 = i5;
      } 
      if (j < i4) {
        this.cipher.restore();
        throw new ShortBufferException("Output buffer too short: " + j + " bytes given, " + i4 + " bytes needed");
      } 
      System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte2, paramInt3, i4);
    } else {
      try {
        i4 = finalNoPadding(arrayOfByte, i2, paramArrayOfbyte2, paramInt3, i3);
      } finally {
        this.requireReinit = (this.cipherMode == 7);
      } 
    } 
    this.buffered = 0;
    this.diffBlocksize = this.blockSize;
    if (this.cipherMode != 0)
      this.cipher.reset(); 
    return i4;
  }
  
  private int finalNoPadding(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
    if (this.cipherMode != 7 && (paramArrayOfbyte1 == null || paramInt3 == 0))
      return 0; 
    if (this.cipherMode != 2 && this.cipherMode != 3 && this.cipherMode != 7 && paramInt3 % this.unitBytes != 0 && this.cipherMode != 6) {
      if (this.padding != null)
        throw new IllegalBlockSizeException("Input length (with padding) not multiple of " + this.unitBytes + " bytes"); 
      throw new IllegalBlockSizeException("Input length not multiple of " + this.unitBytes + " bytes");
    } 
    int i = 0;
    if (this.decrypting) {
      i = this.cipher.decryptFinal(paramArrayOfbyte1, paramInt1, paramInt3, paramArrayOfbyte2, paramInt2);
    } else {
      i = this.cipher.encryptFinal(paramArrayOfbyte1, paramInt1, paramInt3, paramArrayOfbyte2, paramInt2);
    } 
    return i;
  }
  
  byte[] wrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    byte[] arrayOfByte = null;
    try {
      byte[] arrayOfByte1 = paramKey.getEncoded();
      if (arrayOfByte1 == null || arrayOfByte1.length == 0)
        throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped"); 
      arrayOfByte = doFinal(arrayOfByte1, 0, arrayOfByte1.length);
    } catch (BadPaddingException badPaddingException) {}
    return arrayOfByte;
  }
  
  Key unwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = doFinal(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    } catch (BadPaddingException badPaddingException) {
      throw new InvalidKeyException("The wrapped key is not padded correctly");
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new InvalidKeyException("The wrapped key does not have the correct length");
    } 
    return ConstructKeys.constructKey(arrayOfByte, paramString, paramInt);
  }
  
  void updateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.requireReinit)
      throw new IllegalStateException("Must use either different key or iv for GCM encryption"); 
    this.cipher.updateAAD(paramArrayOfbyte, paramInt1, paramInt2);
  }
}
