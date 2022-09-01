package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.MessageDigest;

final class AESCrypt extends SymmetricCipher implements AESConstants {
  private boolean ROUNDS_12 = false;
  
  private boolean ROUNDS_14 = false;
  
  private Object[] sessionK = null;
  
  private int[] K = null;
  
  private byte[] lastKey = null;
  
  private int limit = 0;
  
  int getBlockSize() {
    return 16;
  }
  
  void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte) throws InvalidKeyException {
    if (!paramString.equalsIgnoreCase("AES") && 
      !paramString.equalsIgnoreCase("Rijndael"))
      throw new InvalidKeyException("Wrong algorithm: AES or Rijndael required"); 
    if (!isKeySizeValid(paramArrayOfbyte.length))
      throw new InvalidKeyException("Invalid AES key length: " + paramArrayOfbyte.length + " bytes"); 
    if (!MessageDigest.isEqual(paramArrayOfbyte, this.lastKey)) {
      makeSessionKey(paramArrayOfbyte);
      this.lastKey = (byte[])paramArrayOfbyte.clone();
    } 
    this.K = (int[])this.sessionK[paramBoolean ? 1 : 0];
  }
  
  private static final int[] expandToSubKey(int[][] paramArrayOfint, boolean paramBoolean) {
    int i = paramArrayOfint.length;
    int[] arrayOfInt = new int[i * 4];
    if (paramBoolean) {
      byte b;
      for (b = 0; b < 4; b++)
        arrayOfInt[b] = paramArrayOfint[i - 1][b]; 
      for (b = 1; b < i; b++) {
        for (byte b1 = 0; b1 < 4; b1++)
          arrayOfInt[b * 4 + b1] = paramArrayOfint[b - 1][b1]; 
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        for (byte b1 = 0; b1 < 4; b1++)
          arrayOfInt[b * 4 + b1] = paramArrayOfint[b][b1]; 
      } 
    } 
    return arrayOfInt;
  }
  
  private static int[] alog = new int[256];
  
  private static int[] log = new int[256];
  
  private static final byte[] S = new byte[256];
  
  private static final byte[] Si = new byte[256];
  
  private static final int[] T1 = new int[256];
  
  private static final int[] T2 = new int[256];
  
  private static final int[] T3 = new int[256];
  
  private static final int[] T4 = new int[256];
  
  private static final int[] T5 = new int[256];
  
  private static final int[] T6 = new int[256];
  
  private static final int[] T7 = new int[256];
  
  private static final int[] T8 = new int[256];
  
  private static final int[] U1 = new int[256];
  
  private static final int[] U2 = new int[256];
  
  private static final int[] U3 = new int[256];
  
  private static final int[] U4 = new int[256];
  
  private static final byte[] rcon = new byte[30];
  
  static {
    char c = 'ě';
    int i = 0;
    alog[0] = 1;
    byte b1;
    for (b1 = 1; b1 < 'Ā'; b1++) {
      i = alog[b1 - 1] << 1 ^ alog[b1 - 1];
      if ((i & 0x100) != 0)
        i ^= c; 
      alog[b1] = i;
    } 
    for (b1 = 1; b1 < 'ÿ'; b1++)
      log[alog[b1]] = b1; 
    byte[][] arrayOfByte1 = { { 1, 1, 1, 1, 1, 0, 0, 0 }, { 0, 1, 1, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 1, 1, 0 }, { 0, 0, 0, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 0, 1, 1, 1 }, { 1, 1, 1, 0, 0, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0, 0, 1 } };
    byte[] arrayOfByte = { 0, 1, 1, 0, 0, 0, 1, 1 };
    byte[][] arrayOfByte2 = new byte[256][8];
    arrayOfByte2[1][7] = 1;
    for (b1 = 2; b1 < 'Ā'; b1++) {
      i = alog[255 - log[b1]];
      for (byte b = 0; b < 8; b++)
        arrayOfByte2[b1][b] = (byte)(i >>> 7 - b & 0x1); 
    } 
    byte[][] arrayOfByte3 = new byte[256][8];
    for (b1 = 0; b1 < 'Ā'; b1++) {
      for (byte b = 0; b < 8; b++) {
        arrayOfByte3[b1][b] = arrayOfByte[b];
        for (i = 0; i < 8; i++)
          arrayOfByte3[b1][b] = (byte)(arrayOfByte3[b1][b] ^ arrayOfByte1[b][i] * arrayOfByte2[b1][i]); 
      } 
    } 
    for (b1 = 0; b1 < 'Ā'; b1++) {
      S[b1] = (byte)(arrayOfByte3[b1][0] << 7);
      for (byte b = 1; b < 8; b++)
        S[b1] = (byte)(S[b1] ^ arrayOfByte3[b1][b] << 7 - b); 
      Si[S[b1] & 0xFF] = (byte)b1;
    } 
    byte[][] arrayOfByte4 = { { 2, 1, 1, 3 }, { 3, 2, 1, 1 }, { 1, 3, 2, 1 }, { 1, 1, 3, 2 } };
    byte[][] arrayOfByte5 = new byte[4][8];
    for (b1 = 0; b1 < 4; b1++) {
      for (i = 0; i < 4; ) {
        arrayOfByte5[b1][i] = arrayOfByte4[b1][i];
        i++;
      } 
      arrayOfByte5[b1][b1 + 4] = 1;
    } 
    byte[][] arrayOfByte6 = new byte[4][4];
    for (b1 = 0; b1 < 4; b1++) {
      byte b3 = arrayOfByte5[b1][b1];
      if (b3 == 0) {
        int k = b1 + 1;
        while (arrayOfByte5[k][b1] == 0 && k < 4)
          k++; 
        if (k == 4)
          throw new RuntimeException("G matrix is not invertible"); 
        for (i = 0; i < 8; i++) {
          byte b4 = arrayOfByte5[b1][i];
          arrayOfByte5[b1][i] = arrayOfByte5[k][i];
          arrayOfByte5[k][i] = b4;
        } 
        b3 = arrayOfByte5[b1][b1];
      } 
      for (i = 0; i < 8; i++) {
        if (arrayOfByte5[b1][i] != 0)
          arrayOfByte5[b1][i] = (byte)alog[(255 + log[arrayOfByte5[b1][i] & 0xFF] - log[b3 & 0xFF]) % 255]; 
      } 
      for (byte b = 0; b < 4; b++) {
        if (b1 != b) {
          for (i = b1 + 1; i < 8; i++)
            arrayOfByte5[b][i] = (byte)(arrayOfByte5[b][i] ^ mul(arrayOfByte5[b1][i], arrayOfByte5[b][b1])); 
          arrayOfByte5[b][b1] = 0;
        } 
      } 
    } 
    for (b1 = 0; b1 < 4; b1++) {
      for (i = 0; i < 4; i++)
        arrayOfByte6[b1][i] = arrayOfByte5[b1][i + 4]; 
    } 
    byte b2;
    for (b2 = 0; b2 < 'Ā'; b2++) {
      byte b = S[b2];
      T1[b2] = mul4(b, arrayOfByte4[0]);
      T2[b2] = mul4(b, arrayOfByte4[1]);
      T3[b2] = mul4(b, arrayOfByte4[2]);
      T4[b2] = mul4(b, arrayOfByte4[3]);
      b = Si[b2];
      T5[b2] = mul4(b, arrayOfByte6[0]);
      T6[b2] = mul4(b, arrayOfByte6[1]);
      T7[b2] = mul4(b, arrayOfByte6[2]);
      T8[b2] = mul4(b, arrayOfByte6[3]);
      U1[b2] = mul4(b2, arrayOfByte6[0]);
      U2[b2] = mul4(b2, arrayOfByte6[1]);
      U3[b2] = mul4(b2, arrayOfByte6[2]);
      U4[b2] = mul4(b2, arrayOfByte6[3]);
    } 
    rcon[0] = 1;
    int j = 1;
    for (b2 = 1; b2 < 30; b2++) {
      j = mul(2, j);
      rcon[b2] = (byte)j;
    } 
    log = null;
    alog = null;
  }
  
  private static final int mul(int paramInt1, int paramInt2) {
    return (paramInt1 != 0 && paramInt2 != 0) ? alog[(log[paramInt1 & 0xFF] + log[paramInt2 & 0xFF]) % 255] : 0;
  }
  
  private static final int mul4(int paramInt, byte[] paramArrayOfbyte) {
    if (paramInt == 0)
      return 0; 
    paramInt = log[paramInt & 0xFF];
    byte b1 = (paramArrayOfbyte[0] != 0) ? (alog[(paramInt + log[paramArrayOfbyte[0] & 0xFF]) % 255] & 0xFF) : 0;
    byte b2 = (paramArrayOfbyte[1] != 0) ? (alog[(paramInt + log[paramArrayOfbyte[1] & 0xFF]) % 255] & 0xFF) : 0;
    byte b3 = (paramArrayOfbyte[2] != 0) ? (alog[(paramInt + log[paramArrayOfbyte[2] & 0xFF]) % 255] & 0xFF) : 0;
    boolean bool = (paramArrayOfbyte[3] != 0) ? (alog[(paramInt + log[paramArrayOfbyte[3] & 0xFF]) % 255] & 0xFF) : false;
    return b1 << 24 | b2 << 16 | b3 << 8 | bool;
  }
  
  static final boolean isKeySizeValid(int paramInt) {
    for (byte b = 0; b < AES_KEYSIZES.length; b++) {
      if (paramInt == AES_KEYSIZES[b])
        return true; 
    } 
    return false;
  }
  
  void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    byte b = 0;
    int i = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int j = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int k = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int m = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    while (b < this.limit) {
      int i1 = T1[i >>> 24] ^ T2[j >>> 16 & 0xFF] ^ T3[k >>> 8 & 0xFF] ^ T4[m & 0xFF] ^ this.K[b++];
      int i2 = T1[j >>> 24] ^ T2[k >>> 16 & 0xFF] ^ T3[m >>> 8 & 0xFF] ^ T4[i & 0xFF] ^ this.K[b++];
      int i3 = T1[k >>> 24] ^ T2[m >>> 16 & 0xFF] ^ T3[i >>> 8 & 0xFF] ^ T4[j & 0xFF] ^ this.K[b++];
      m = T1[m >>> 24] ^ T2[i >>> 16 & 0xFF] ^ T3[j >>> 8 & 0xFF] ^ T4[k & 0xFF] ^ this.K[b++];
      i = i1;
      j = i2;
      k = i3;
    } 
    int n = this.K[b++];
    paramArrayOfbyte2[paramInt2++] = (byte)(S[i >>> 24] ^ n >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[j >>> 16 & 0xFF] ^ n >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[k >>> 8 & 0xFF] ^ n >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[m & 0xFF] ^ n);
    n = this.K[b++];
    paramArrayOfbyte2[paramInt2++] = (byte)(S[j >>> 24] ^ n >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[k >>> 16 & 0xFF] ^ n >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[m >>> 8 & 0xFF] ^ n >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[i & 0xFF] ^ n);
    n = this.K[b++];
    paramArrayOfbyte2[paramInt2++] = (byte)(S[k >>> 24] ^ n >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[m >>> 16 & 0xFF] ^ n >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[i >>> 8 & 0xFF] ^ n >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[j & 0xFF] ^ n);
    n = this.K[b++];
    paramArrayOfbyte2[paramInt2++] = (byte)(S[m >>> 24] ^ n >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[i >>> 16 & 0xFF] ^ n >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(S[j >>> 8 & 0xFF] ^ n >>> 8);
    paramArrayOfbyte2[paramInt2] = (byte)(S[k & 0xFF] ^ n);
  }
  
  void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    byte b = 4;
    int i = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int j = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int k = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1++] & 0xFF) ^ this.K[b++];
    int m = (paramArrayOfbyte1[paramInt1++] << 24 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 16 | (paramArrayOfbyte1[paramInt1++] & 0xFF) << 8 | paramArrayOfbyte1[paramInt1] & 0xFF) ^ this.K[b++];
    if (this.ROUNDS_12) {
      int i3 = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
      int i4 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
      int i5 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
      m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
      i = T5[i3 >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i5 >>> 8 & 0xFF] ^ T8[i4 & 0xFF] ^ this.K[b++];
      j = T5[i4 >>> 24] ^ T6[i3 >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i5 & 0xFF] ^ this.K[b++];
      k = T5[i5 >>> 24] ^ T6[i4 >>> 16 & 0xFF] ^ T7[i3 >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
      m = T5[m >>> 24] ^ T6[i5 >>> 16 & 0xFF] ^ T7[i4 >>> 8 & 0xFF] ^ T8[i3 & 0xFF] ^ this.K[b++];
      if (this.ROUNDS_14) {
        i3 = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
        i4 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
        i5 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
        m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
        i = T5[i3 >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i5 >>> 8 & 0xFF] ^ T8[i4 & 0xFF] ^ this.K[b++];
        j = T5[i4 >>> 24] ^ T6[i3 >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i5 & 0xFF] ^ this.K[b++];
        k = T5[i5 >>> 24] ^ T6[i4 >>> 16 & 0xFF] ^ T7[i3 >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
        m = T5[m >>> 24] ^ T6[i5 >>> 16 & 0xFF] ^ T7[i4 >>> 8 & 0xFF] ^ T8[i3 & 0xFF] ^ this.K[b++];
      } 
    } 
    int n = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
    int i1 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
    int i2 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
    i = T5[n >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i2 >>> 8 & 0xFF] ^ T8[i1 & 0xFF] ^ this.K[b++];
    j = T5[i1 >>> 24] ^ T6[n >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i2 & 0xFF] ^ this.K[b++];
    k = T5[i2 >>> 24] ^ T6[i1 >>> 16 & 0xFF] ^ T7[n >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[i2 >>> 16 & 0xFF] ^ T7[i1 >>> 8 & 0xFF] ^ T8[n & 0xFF] ^ this.K[b++];
    n = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
    i1 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
    i2 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
    i = T5[n >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i2 >>> 8 & 0xFF] ^ T8[i1 & 0xFF] ^ this.K[b++];
    j = T5[i1 >>> 24] ^ T6[n >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i2 & 0xFF] ^ this.K[b++];
    k = T5[i2 >>> 24] ^ T6[i1 >>> 16 & 0xFF] ^ T7[n >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[i2 >>> 16 & 0xFF] ^ T7[i1 >>> 8 & 0xFF] ^ T8[n & 0xFF] ^ this.K[b++];
    n = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
    i1 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
    i2 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
    i = T5[n >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i2 >>> 8 & 0xFF] ^ T8[i1 & 0xFF] ^ this.K[b++];
    j = T5[i1 >>> 24] ^ T6[n >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i2 & 0xFF] ^ this.K[b++];
    k = T5[i2 >>> 24] ^ T6[i1 >>> 16 & 0xFF] ^ T7[n >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[i2 >>> 16 & 0xFF] ^ T7[i1 >>> 8 & 0xFF] ^ T8[n & 0xFF] ^ this.K[b++];
    n = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
    i1 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
    i2 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
    i = T5[n >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[i2 >>> 8 & 0xFF] ^ T8[i1 & 0xFF] ^ this.K[b++];
    j = T5[i1 >>> 24] ^ T6[n >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[i2 & 0xFF] ^ this.K[b++];
    k = T5[i2 >>> 24] ^ T6[i1 >>> 16 & 0xFF] ^ T7[n >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[i2 >>> 16 & 0xFF] ^ T7[i1 >>> 8 & 0xFF] ^ T8[n & 0xFF] ^ this.K[b++];
    n = T5[i >>> 24] ^ T6[m >>> 16 & 0xFF] ^ T7[k >>> 8 & 0xFF] ^ T8[j & 0xFF] ^ this.K[b++];
    i1 = T5[j >>> 24] ^ T6[i >>> 16 & 0xFF] ^ T7[m >>> 8 & 0xFF] ^ T8[k & 0xFF] ^ this.K[b++];
    i2 = T5[k >>> 24] ^ T6[j >>> 16 & 0xFF] ^ T7[i >>> 8 & 0xFF] ^ T8[m & 0xFF] ^ this.K[b++];
    m = T5[m >>> 24] ^ T6[k >>> 16 & 0xFF] ^ T7[j >>> 8 & 0xFF] ^ T8[i & 0xFF] ^ this.K[b++];
    j = this.K[0];
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[n >>> 24] ^ j >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[m >>> 16 & 0xFF] ^ j >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i2 >>> 8 & 0xFF] ^ j >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i1 & 0xFF] ^ j);
    j = this.K[1];
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i1 >>> 24] ^ j >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[n >>> 16 & 0xFF] ^ j >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[m >>> 8 & 0xFF] ^ j >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i2 & 0xFF] ^ j);
    j = this.K[2];
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i2 >>> 24] ^ j >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i1 >>> 16 & 0xFF] ^ j >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[n >>> 8 & 0xFF] ^ j >>> 8);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[m & 0xFF] ^ j);
    j = this.K[3];
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[m >>> 24] ^ j >>> 24);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i2 >>> 16 & 0xFF] ^ j >>> 16);
    paramArrayOfbyte2[paramInt2++] = (byte)(Si[i1 >>> 8 & 0xFF] ^ j >>> 8);
    paramArrayOfbyte2[paramInt2] = (byte)(Si[n & 0xFF] ^ j);
  }
  
  private void makeSessionKey(byte[] paramArrayOfbyte) throws InvalidKeyException {
    if (paramArrayOfbyte == null)
      throw new InvalidKeyException("Empty key"); 
    if (!isKeySizeValid(paramArrayOfbyte.length))
      throw new InvalidKeyException("Invalid AES key length: " + paramArrayOfbyte.length + " bytes"); 
    int i = getRounds(paramArrayOfbyte.length);
    int j = (i + 1) * 4;
    byte b1 = 4;
    int[][] arrayOfInt1 = new int[i + 1][4];
    int[][] arrayOfInt2 = new int[i + 1][4];
    int k = paramArrayOfbyte.length / 4;
    int[] arrayOfInt3 = new int[k];
    int m, n;
    for (m = 0, n = 0; m < k; m++, n += 4)
      arrayOfInt3[m] = paramArrayOfbyte[n] << 24 | (paramArrayOfbyte[n + 1] & 0xFF) << 16 | (paramArrayOfbyte[n + 2] & 0xFF) << 8 | paramArrayOfbyte[n + 3] & 0xFF; 
    byte b2 = 0;
    for (n = 0; n < k && b2 < j; n++, b2++) {
      arrayOfInt1[b2 / 4][b2 % 4] = arrayOfInt3[n];
      arrayOfInt2[i - b2 / 4][b2 % 4] = arrayOfInt3[n];
    } 
    byte b3 = 0;
    while (b2 < j) {
      int i1 = arrayOfInt3[k - 1];
      arrayOfInt3[0] = arrayOfInt3[0] ^ S[i1 >>> 16 & 0xFF] << 24 ^ (S[i1 >>> 8 & 0xFF] & 0xFF) << 16 ^ (S[i1 & 0xFF] & 0xFF) << 8 ^ S[i1 >>> 24] & 0xFF ^ rcon[b3++] << 24;
      if (k != 8) {
        for (m = 1, n = 0; m < k; ) {
          arrayOfInt3[m] = arrayOfInt3[m] ^ arrayOfInt3[n];
          m++;
          n++;
        } 
      } else {
        for (m = 1, n = 0; m < k / 2; ) {
          arrayOfInt3[m] = arrayOfInt3[m] ^ arrayOfInt3[n];
          m++;
          n++;
        } 
        i1 = arrayOfInt3[k / 2 - 1];
        arrayOfInt3[k / 2] = arrayOfInt3[k / 2] ^ S[i1 & 0xFF] & 0xFF ^ (S[i1 >>> 8 & 0xFF] & 0xFF) << 8 ^ (S[i1 >>> 16 & 0xFF] & 0xFF) << 16 ^ S[i1 >>> 24] << 24;
        for (n = k / 2, m = n + 1; m < k; ) {
          arrayOfInt3[m] = arrayOfInt3[m] ^ arrayOfInt3[n];
          m++;
          n++;
        } 
      } 
      for (n = 0; n < k && b2 < j; n++, b2++) {
        arrayOfInt1[b2 / 4][b2 % 4] = arrayOfInt3[n];
        arrayOfInt2[i - b2 / 4][b2 % 4] = arrayOfInt3[n];
      } 
    } 
    for (byte b4 = 1; b4 < i; b4++) {
      for (n = 0; n < b1; n++) {
        int i1 = arrayOfInt2[b4][n];
        arrayOfInt2[b4][n] = U1[i1 >>> 24 & 0xFF] ^ U2[i1 >>> 16 & 0xFF] ^ U3[i1 >>> 8 & 0xFF] ^ U4[i1 & 0xFF];
      } 
    } 
    int[] arrayOfInt4 = expandToSubKey(arrayOfInt1, false);
    int[] arrayOfInt5 = expandToSubKey(arrayOfInt2, true);
    this.ROUNDS_12 = (i >= 12);
    this.ROUNDS_14 = (i == 14);
    this.limit = i * 4;
    this.sessionK = new Object[] { arrayOfInt4, arrayOfInt5 };
  }
  
  private static int getRounds(int paramInt) {
    return (paramInt >> 2) + 6;
  }
}
