package sun.security.rsa;

import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import sun.security.jca.JCAUtil;

public final class RSAPadding {
  public static final int PAD_BLOCKTYPE_1 = 1;
  
  public static final int PAD_BLOCKTYPE_2 = 2;
  
  public static final int PAD_NONE = 3;
  
  public static final int PAD_OAEP_MGF1 = 4;
  
  private final int type;
  
  private final int paddedSize;
  
  private SecureRandom random;
  
  private final int maxDataSize;
  
  private MessageDigest md;
  
  private MessageDigest mgfMd;
  
  private byte[] lHash;
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2) throws InvalidKeyException, InvalidAlgorithmParameterException {
    return new RSAPadding(paramInt1, paramInt2, null, null);
  }
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, null);
  }
  
  public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, paramOAEPParameterSpec);
  }
  
  private RSAPadding(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    String str1, str2;
    byte[] arrayOfByte;
    int i;
    this.type = paramInt1;
    this.paddedSize = paramInt2;
    this.random = paramSecureRandom;
    if (paramInt2 < 64)
      throw new InvalidKeyException("Padded size must be at least 64"); 
    switch (paramInt1) {
      case 1:
      case 2:
        this.maxDataSize = paramInt2 - 11;
        return;
      case 3:
        this.maxDataSize = paramInt2;
        return;
      case 4:
        str1 = "SHA-1";
        str2 = "SHA-1";
        arrayOfByte = null;
        try {
          if (paramOAEPParameterSpec != null) {
            str1 = paramOAEPParameterSpec.getDigestAlgorithm();
            String str3 = paramOAEPParameterSpec.getMGFAlgorithm();
            if (!str3.equalsIgnoreCase("MGF1"))
              throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + str3); 
            str2 = ((MGF1ParameterSpec)paramOAEPParameterSpec.getMGFParameters()).getDigestAlgorithm();
            PSource pSource = paramOAEPParameterSpec.getPSource();
            String str4 = pSource.getAlgorithm();
            if (!str4.equalsIgnoreCase("PSpecified"))
              throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + str4); 
            arrayOfByte = ((PSource.PSpecified)pSource).getValue();
          } 
          this.md = MessageDigest.getInstance(str1);
          this.mgfMd = MessageDigest.getInstance(str2);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new InvalidKeyException("Digest " + str1 + " not available", noSuchAlgorithmException);
        } 
        this.lHash = getInitialHash(this.md, arrayOfByte);
        i = this.lHash.length;
        this.maxDataSize = paramInt2 - 2 - 2 * i;
        if (this.maxDataSize <= 0)
          throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + str1 + " and MGF1" + str2); 
        return;
    } 
    throw new InvalidKeyException("Invalid padding: " + paramInt1);
  }
  
  private static final Map<String, byte[]> emptyHashes = (Map)Collections.synchronizedMap(new HashMap<>());
  
  private static byte[] getInitialHash(MessageDigest paramMessageDigest, byte[] paramArrayOfbyte) {
    byte[] arrayOfByte;
    if (paramArrayOfbyte == null || paramArrayOfbyte.length == 0) {
      String str = paramMessageDigest.getAlgorithm();
      arrayOfByte = emptyHashes.get(str);
      if (arrayOfByte == null) {
        arrayOfByte = paramMessageDigest.digest();
        emptyHashes.put(str, arrayOfByte);
      } 
    } else {
      arrayOfByte = paramMessageDigest.digest(paramArrayOfbyte);
    } 
    return arrayOfByte;
  }
  
  public int getMaxDataSize() {
    return this.maxDataSize;
  }
  
  public byte[] pad(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException {
    return pad(RSACore.convert(paramArrayOfbyte, paramInt1, paramInt2));
  }
  
  public byte[] pad(byte[] paramArrayOfbyte) throws BadPaddingException {
    if (paramArrayOfbyte.length > this.maxDataSize)
      throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes"); 
    switch (this.type) {
      case 3:
        return paramArrayOfbyte;
      case 1:
      case 2:
        return padV15(paramArrayOfbyte);
      case 4:
        return padOAEP(paramArrayOfbyte);
    } 
    throw new AssertionError();
  }
  
  public byte[] unpad(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException {
    return unpad(RSACore.convert(paramArrayOfbyte, paramInt1, paramInt2));
  }
  
  public byte[] unpad(byte[] paramArrayOfbyte) throws BadPaddingException {
    if (paramArrayOfbyte.length != this.paddedSize)
      throw new BadPaddingException("Decryption error"); 
    switch (this.type) {
      case 3:
        return paramArrayOfbyte;
      case 1:
      case 2:
        return unpadV15(paramArrayOfbyte);
      case 4:
        return unpadOAEP(paramArrayOfbyte);
    } 
    throw new AssertionError();
  }
  
  private byte[] padV15(byte[] paramArrayOfbyte) throws BadPaddingException {
    byte[] arrayOfByte = new byte[this.paddedSize];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, this.paddedSize - paramArrayOfbyte.length, paramArrayOfbyte.length);
    int i = this.paddedSize - 3 - paramArrayOfbyte.length;
    byte b = 0;
    arrayOfByte[b++] = 0;
    arrayOfByte[b++] = (byte)this.type;
    if (this.type == 1) {
      while (i-- > 0)
        arrayOfByte[b++] = -1; 
    } else {
      if (this.random == null)
        this.random = JCAUtil.getSecureRandom(); 
      byte[] arrayOfByte1 = new byte[64];
      int j = -1;
      while (i-- > 0) {
        while (true) {
          if (j < 0) {
            this.random.nextBytes(arrayOfByte1);
            j = arrayOfByte1.length - 1;
          } 
          int k = arrayOfByte1[j--] & 0xFF;
          if (k != 0)
            arrayOfByte[b++] = (byte)k; 
        } 
      } 
    } 
    return arrayOfByte;
  }
  
  private byte[] unpadV15(byte[] paramArrayOfbyte) throws BadPaddingException {
    byte b1 = 0;
    boolean bool = false;
    if (paramArrayOfbyte[b1++] != 0)
      bool = true; 
    if (paramArrayOfbyte[b1++] != this.type)
      bool = true; 
    byte b2 = 0;
    while (b1 < paramArrayOfbyte.length) {
      int j = paramArrayOfbyte[b1++] & 0xFF;
      if (j == 0 && !b2)
        b2 = b1; 
      if (b1 == paramArrayOfbyte.length && b2 == 0)
        bool = true; 
      if (this.type == 1 && j != 255 && b2 == 0)
        bool = true; 
    } 
    int i = paramArrayOfbyte.length - b2;
    if (i > this.maxDataSize)
      bool = true; 
    byte[] arrayOfByte1 = new byte[b2];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte1, 0, b2);
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(paramArrayOfbyte, b2, arrayOfByte2, 0, i);
    BadPaddingException badPaddingException = new BadPaddingException("Decryption error");
    if (bool)
      throw badPaddingException; 
    return arrayOfByte2;
  }
  
  private byte[] padOAEP(byte[] paramArrayOfbyte) throws BadPaddingException {
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    int i = this.lHash.length;
    byte[] arrayOfByte1 = new byte[i];
    this.random.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[this.paddedSize];
    boolean bool = true;
    int j = i;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, bool, j);
    int k = i + 1;
    int m = arrayOfByte2.length - k;
    int n = this.paddedSize - paramArrayOfbyte.length;
    System.arraycopy(this.lHash, 0, arrayOfByte2, k, i);
    arrayOfByte2[n - 1] = 1;
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte2, n, paramArrayOfbyte.length);
    mgf1(arrayOfByte2, bool, j, arrayOfByte2, k, m);
    mgf1(arrayOfByte2, k, m, arrayOfByte2, bool, j);
    return arrayOfByte2;
  }
  
  private byte[] unpadOAEP(byte[] paramArrayOfbyte) throws BadPaddingException {
    byte[] arrayOfByte1 = paramArrayOfbyte;
    boolean bool1 = false;
    int i = this.lHash.length;
    if (arrayOfByte1[0] != 0)
      bool1 = true; 
    boolean bool2 = true;
    int j = i;
    int k = i + 1;
    int m = arrayOfByte1.length - k;
    mgf1(arrayOfByte1, k, m, arrayOfByte1, bool2, j);
    mgf1(arrayOfByte1, bool2, j, arrayOfByte1, k, m);
    int n;
    for (n = 0; n < i; n++) {
      if (this.lHash[n] != arrayOfByte1[k + n])
        bool1 = true; 
    } 
    n = k + i;
    int i1 = -1;
    int i2;
    for (i2 = n; i2 < arrayOfByte1.length; i2++) {
      byte b = arrayOfByte1[i2];
      if (i1 == -1 && 
        b != 0)
        if (b == 1) {
          i1 = i2;
        } else {
          bool1 = true;
        }  
    } 
    if (i1 == -1) {
      bool1 = true;
      i1 = arrayOfByte1.length - 1;
    } 
    i2 = i1 + 1;
    byte[] arrayOfByte2 = new byte[i2 - n];
    System.arraycopy(arrayOfByte1, n, arrayOfByte2, 0, arrayOfByte2.length);
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length - i2];
    System.arraycopy(arrayOfByte1, i2, arrayOfByte3, 0, arrayOfByte3.length);
    BadPaddingException badPaddingException = new BadPaddingException("Decryption error");
    if (bool1)
      throw badPaddingException; 
    return arrayOfByte3;
  }
  
  private void mgf1(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3, int paramInt4) throws BadPaddingException {
    byte[] arrayOfByte1 = new byte[4];
    byte[] arrayOfByte2 = new byte[this.mgfMd.getDigestLength()];
    while (paramInt4 > 0) {
      this.mgfMd.update(paramArrayOfbyte1, paramInt1, paramInt2);
      this.mgfMd.update(arrayOfByte1);
      try {
        this.mgfMd.digest(arrayOfByte2, 0, arrayOfByte2.length);
      } catch (DigestException digestException) {
        throw new BadPaddingException(digestException.toString());
      } 
      int i;
      for (i = 0; i < arrayOfByte2.length && paramInt4 > 0; paramInt4--)
        paramArrayOfbyte2[paramInt3++] = (byte)(paramArrayOfbyte2[paramInt3++] ^ arrayOfByte2[i++]); 
      if (paramInt4 > 0)
        for (i = arrayOfByte1.length - 1, arrayOfByte1[i] = (byte)(arrayOfByte1[i] + 1); (byte)(arrayOfByte1[i] + 1) == 0 && i > 0; i--); 
    } 
  }
}
