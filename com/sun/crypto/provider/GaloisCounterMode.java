package com.sun.crypto.provider;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import javax.crypto.AEADBadTagException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

final class GaloisCounterMode extends FeedbackCipher {
  static int DEFAULT_TAG_LEN = 16;
  
  static int DEFAULT_IV_LEN = 12;
  
  private ByteArrayOutputStream aadBuffer = new ByteArrayOutputStream();
  
  private int sizeOfAAD = 0;
  
  private ByteArrayOutputStream ibuffer = null;
  
  private int tagLenBytes = DEFAULT_TAG_LEN;
  
  private byte[] subkeyH = null;
  
  private byte[] preCounterBlock = null;
  
  private GCTR gctrPAndC = null;
  
  private GHASH ghashAllToS = null;
  
  private int processed = 0;
  
  private byte[] aadBufferSave = null;
  
  private int sizeOfAADSave = 0;
  
  private byte[] ibufferSave = null;
  
  private int processedSave = 0;
  
  static void increment32(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 16)
      throw new ProviderException("Illegal counter block length"); 
    int i = paramArrayOfbyte.length - 1;
    for (paramArrayOfbyte[i] = (byte)(paramArrayOfbyte[i] + 1); i >= paramArrayOfbyte.length - 4 && (byte)(paramArrayOfbyte[i] + 1) == 0;)
      i--; 
  }
  
  private static byte[] getLengthBlock(int paramInt) {
    byte[] arrayOfByte = new byte[16];
    arrayOfByte[12] = (byte)(paramInt >>> 24);
    arrayOfByte[13] = (byte)(paramInt >>> 16);
    arrayOfByte[14] = (byte)(paramInt >>> 8);
    arrayOfByte[15] = (byte)paramInt;
    return arrayOfByte;
  }
  
  private static byte[] getLengthBlock(int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[16];
    arrayOfByte[4] = (byte)(paramInt1 >>> 24);
    arrayOfByte[5] = (byte)(paramInt1 >>> 16);
    arrayOfByte[6] = (byte)(paramInt1 >>> 8);
    arrayOfByte[7] = (byte)paramInt1;
    arrayOfByte[12] = (byte)(paramInt2 >>> 24);
    arrayOfByte[13] = (byte)(paramInt2 >>> 16);
    arrayOfByte[14] = (byte)(paramInt2 >>> 8);
    arrayOfByte[15] = (byte)paramInt2;
    return arrayOfByte;
  }
  
  private static byte[] expandToOneBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 > 16)
      throw new ProviderException("input " + paramInt2 + " too long"); 
    if (paramInt2 == 16 && paramInt1 == 0)
      return paramArrayOfbyte; 
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  private static byte[] getJ0(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte[] arrayOfByte;
    if (paramArrayOfbyte1.length == 12) {
      arrayOfByte = expandToOneBlock(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
      arrayOfByte[15] = 1;
    } else {
      GHASH gHASH = new GHASH(paramArrayOfbyte2);
      int i = paramArrayOfbyte1.length % 16;
      if (i != 0) {
        gHASH.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length - i);
        byte[] arrayOfByte2 = expandToOneBlock(paramArrayOfbyte1, paramArrayOfbyte1.length - i, i);
        gHASH.update(arrayOfByte2);
      } else {
        gHASH.update(paramArrayOfbyte1);
      } 
      byte[] arrayOfByte1 = getLengthBlock(paramArrayOfbyte1.length * 8);
      gHASH.update(arrayOfByte1);
      arrayOfByte = gHASH.digest();
    } 
    return arrayOfByte;
  }
  
  GaloisCounterMode(SymmetricCipher paramSymmetricCipher) {
    super(paramSymmetricCipher);
    this.aadBuffer = new ByteArrayOutputStream();
  }
  
  String getFeedback() {
    return "GCM";
  }
  
  void reset() {
    if (this.aadBuffer == null) {
      this.aadBuffer = new ByteArrayOutputStream();
    } else {
      this.aadBuffer.reset();
    } 
    if (this.gctrPAndC != null)
      this.gctrPAndC.reset(); 
    if (this.ghashAllToS != null)
      this.ghashAllToS.reset(); 
    this.processed = 0;
    this.sizeOfAAD = 0;
    if (this.ibuffer != null)
      this.ibuffer.reset(); 
  }
  
  void save() {
    this.processedSave = this.processed;
    this.sizeOfAADSave = this.sizeOfAAD;
    this
      
      .aadBufferSave = (this.aadBuffer == null || this.aadBuffer.size() == 0) ? null : this.aadBuffer.toByteArray();
    if (this.gctrPAndC != null)
      this.gctrPAndC.save(); 
    if (this.ghashAllToS != null)
      this.ghashAllToS.save(); 
    if (this.ibuffer != null)
      this.ibufferSave = this.ibuffer.toByteArray(); 
  }
  
  void restore() {
    this.processed = this.processedSave;
    this.sizeOfAAD = this.sizeOfAADSave;
    if (this.aadBuffer != null) {
      this.aadBuffer.reset();
      if (this.aadBufferSave != null)
        this.aadBuffer.write(this.aadBufferSave, 0, this.aadBufferSave.length); 
    } 
    if (this.gctrPAndC != null)
      this.gctrPAndC.restore(); 
    if (this.ghashAllToS != null)
      this.ghashAllToS.restore(); 
    if (this.ibuffer != null) {
      this.ibuffer.reset();
      this.ibuffer.write(this.ibufferSave, 0, this.ibufferSave.length);
    } 
  }
  
  void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws InvalidKeyException {
    init(paramBoolean, paramString, paramArrayOfbyte1, paramArrayOfbyte2, DEFAULT_TAG_LEN);
  }
  
  void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) throws InvalidKeyException {
    if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null)
      throw new InvalidKeyException("Internal error"); 
    this.embeddedCipher.init(false, paramString, paramArrayOfbyte1);
    this.subkeyH = new byte[16];
    this.embeddedCipher.encryptBlock(new byte[16], 0, this.subkeyH, 0);
    this.iv = (byte[])paramArrayOfbyte2.clone();
    this.preCounterBlock = getJ0(this.iv, this.subkeyH);
    byte[] arrayOfByte = (byte[])this.preCounterBlock.clone();
    increment32(arrayOfByte);
    this.gctrPAndC = new GCTR(this.embeddedCipher, arrayOfByte);
    this.ghashAllToS = new GHASH(this.subkeyH);
    this.tagLenBytes = paramInt;
    if (this.aadBuffer == null) {
      this.aadBuffer = new ByteArrayOutputStream();
    } else {
      this.aadBuffer.reset();
    } 
    this.processed = 0;
    this.sizeOfAAD = 0;
    if (paramBoolean)
      this.ibuffer = new ByteArrayOutputStream(); 
  }
  
  void updateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.aadBuffer != null) {
      this.aadBuffer.write(paramArrayOfbyte, paramInt1, paramInt2);
    } else {
      throw new IllegalStateException("Update has been called; no more AAD data");
    } 
  }
  
  void processAAD() {
    if (this.aadBuffer != null && this.aadBuffer.size() > 0) {
      byte[] arrayOfByte = this.aadBuffer.toByteArray();
      this.sizeOfAAD = arrayOfByte.length;
      this.aadBuffer = null;
      int i = arrayOfByte.length % 16;
      if (i != 0) {
        this.ghashAllToS.update(arrayOfByte, 0, arrayOfByte.length - i);
        byte[] arrayOfByte1 = expandToOneBlock(arrayOfByte, arrayOfByte.length - i, i);
        this.ghashAllToS.update(arrayOfByte1);
      } else {
        this.ghashAllToS.update(arrayOfByte);
      } 
    } 
  }
  
  void doLastBlock(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3, boolean paramBoolean) throws IllegalBlockSizeException {
    byte[] arrayOfByte;
    int i;
    this.gctrPAndC.doFinal(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
    this.processed += paramInt2;
    if (paramBoolean) {
      arrayOfByte = paramArrayOfbyte2;
      i = paramInt3;
    } else {
      arrayOfByte = paramArrayOfbyte1;
      i = paramInt1;
    } 
    int j = paramInt2 % 16;
    if (j != 0) {
      this.ghashAllToS.update(arrayOfByte, i, paramInt2 - j);
      byte[] arrayOfByte1 = expandToOneBlock(arrayOfByte, i + paramInt2 - j, j);
      this.ghashAllToS.update(arrayOfByte1);
    } else {
      this.ghashAllToS.update(arrayOfByte, i, paramInt2);
    } 
  }
  
  int encrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    processAAD();
    if (paramInt2 > 0) {
      this.gctrPAndC.update(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
      this.processed += paramInt2;
      this.ghashAllToS.update(paramArrayOfbyte2, paramInt3, paramInt2);
    } 
    return paramInt2;
  }
  
  int encryptFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, ShortBufferException {
    if (paramArrayOfbyte2.length - paramInt3 < paramInt2 + this.tagLenBytes)
      throw new ShortBufferException("Output buffer too small"); 
    processAAD();
    if (paramInt2 > 0)
      doLastBlock(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3, true); 
    byte[] arrayOfByte1 = getLengthBlock(this.sizeOfAAD * 8, this.processed * 8);
    this.ghashAllToS.update(arrayOfByte1);
    byte[] arrayOfByte2 = this.ghashAllToS.digest();
    byte[] arrayOfByte3 = new byte[arrayOfByte2.length];
    GCTR gCTR = new GCTR(this.embeddedCipher, this.preCounterBlock);
    gCTR.doFinal(arrayOfByte2, 0, arrayOfByte2.length, arrayOfByte3, 0);
    System.arraycopy(arrayOfByte3, 0, paramArrayOfbyte2, paramInt3 + paramInt2, this.tagLenBytes);
    return paramInt2 + this.tagLenBytes;
  }
  
  int decrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    processAAD();
    if (paramInt2 > 0)
      this.ibuffer.write(paramArrayOfbyte1, paramInt1, paramInt2); 
    return 0;
  }
  
  int decryptFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
    if (paramInt2 < this.tagLenBytes)
      throw new AEADBadTagException("Input too short - need tag"); 
    if (paramArrayOfbyte2.length - paramInt3 < this.ibuffer.size() + paramInt2 - this.tagLenBytes)
      throw new ShortBufferException("Output buffer too small"); 
    processAAD();
    if (paramInt2 != 0)
      this.ibuffer.write(paramArrayOfbyte1, paramInt1, paramInt2); 
    paramArrayOfbyte1 = this.ibuffer.toByteArray();
    paramInt1 = 0;
    paramInt2 = paramArrayOfbyte1.length;
    this.ibuffer.reset();
    byte[] arrayOfByte1 = new byte[this.tagLenBytes];
    System.arraycopy(paramArrayOfbyte1, paramInt2 - this.tagLenBytes, arrayOfByte1, 0, this.tagLenBytes);
    paramInt2 -= this.tagLenBytes;
    if (paramInt2 > 0)
      doLastBlock(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3, false); 
    byte[] arrayOfByte2 = getLengthBlock(this.sizeOfAAD * 8, this.processed * 8);
    this.ghashAllToS.update(arrayOfByte2);
    byte[] arrayOfByte3 = this.ghashAllToS.digest();
    byte[] arrayOfByte4 = new byte[arrayOfByte3.length];
    GCTR gCTR = new GCTR(this.embeddedCipher, this.preCounterBlock);
    gCTR.doFinal(arrayOfByte3, 0, arrayOfByte3.length, arrayOfByte4, 0);
    for (byte b = 0; b < this.tagLenBytes; b++) {
      if (arrayOfByte1[b] != arrayOfByte4[b])
        throw new AEADBadTagException("Tag mismatch!"); 
    } 
    return paramInt2;
  }
  
  int getTagLen() {
    return this.tagLenBytes;
  }
  
  int getBufferedLength() {
    if (this.ibuffer == null)
      return 0; 
    return this.ibuffer.size();
  }
}
