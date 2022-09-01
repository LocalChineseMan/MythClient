package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class CipherFeedback extends FeedbackCipher {
  private final byte[] k;
  
  private final byte[] register;
  
  private int numBytes;
  
  private byte[] registerSave = null;
  
  CipherFeedback(SymmetricCipher paramSymmetricCipher, int paramInt) {
    super(paramSymmetricCipher);
    if (paramInt > this.blockSize)
      paramInt = this.blockSize; 
    this.numBytes = paramInt;
    this.k = new byte[this.blockSize];
    this.register = new byte[this.blockSize];
  }
  
  String getFeedback() {
    return "CFB";
  }
  
  void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws InvalidKeyException {
    if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null || paramArrayOfbyte2.length != this.blockSize)
      throw new InvalidKeyException("Internal error"); 
    this.iv = paramArrayOfbyte2;
    reset();
    this.embeddedCipher.init(false, paramString, paramArrayOfbyte1);
  }
  
  void reset() {
    System.arraycopy(this.iv, 0, this.register, 0, this.blockSize);
  }
  
  void save() {
    if (this.registerSave == null)
      this.registerSave = new byte[this.blockSize]; 
    System.arraycopy(this.register, 0, this.registerSave, 0, this.blockSize);
  }
  
  void restore() {
    System.arraycopy(this.registerSave, 0, this.register, 0, this.blockSize);
  }
  
  int encrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    int i = this.blockSize - this.numBytes;
    int j = paramInt2 / this.numBytes;
    int k = paramInt2 % this.numBytes;
    if (i == 0) {
      for (; j > 0; 
        paramInt1 += this.numBytes, paramInt3 += this.numBytes, 
        j--) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        for (byte b = 0; b < this.blockSize; b++) {
          paramArrayOfbyte2[b + paramInt3] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
          this.register[b] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
        } 
      } 
      if (k > 0) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        for (byte b = 0; b < k; b++) {
          paramArrayOfbyte2[b + paramInt3] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
          this.register[b] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
        } 
      } 
    } else {
      for (; j > 0; 
        paramInt1 += this.numBytes, paramInt3 += this.numBytes, 
        j--) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        System.arraycopy(this.register, this.numBytes, this.register, 0, i);
        for (byte b = 0; b < this.numBytes; b++) {
          paramArrayOfbyte2[b + paramInt3] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
          this.register[b + i] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
        } 
      } 
      if (k != 0) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        System.arraycopy(this.register, this.numBytes, this.register, 0, i);
        for (byte b = 0; b < k; b++) {
          paramArrayOfbyte2[b + paramInt3] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
          this.register[b + i] = (byte)(this.k[b] ^ paramArrayOfbyte1[b + paramInt1]);
        } 
      } 
    } 
    return paramInt2;
  }
  
  int decrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    int i = this.blockSize - this.numBytes;
    int j = paramInt2 / this.numBytes;
    int k = paramInt2 % this.numBytes;
    if (i == 0) {
      for (; j > 0; 
        paramInt3 += this.numBytes, paramInt1 += this.numBytes, 
        j--) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        for (byte b = 0; b < this.blockSize; b++) {
          this.register[b] = paramArrayOfbyte1[b + paramInt1];
          paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ this.k[b]);
        } 
      } 
      if (k > 0) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        for (byte b = 0; b < k; b++) {
          this.register[b] = paramArrayOfbyte1[b + paramInt1];
          paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ this.k[b]);
        } 
      } 
    } else {
      for (; j > 0; 
        paramInt3 += this.numBytes, paramInt1 += this.numBytes, 
        j--) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        System.arraycopy(this.register, this.numBytes, this.register, 0, i);
        for (byte b = 0; b < this.numBytes; b++) {
          this.register[b + i] = paramArrayOfbyte1[b + paramInt1];
          paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ this.k[b]);
        } 
      } 
      if (k != 0) {
        this.embeddedCipher.encryptBlock(this.register, 0, this.k, 0);
        System.arraycopy(this.register, this.numBytes, this.register, 0, i);
        for (byte b = 0; b < k; b++) {
          this.register[b + i] = paramArrayOfbyte1[b + paramInt1];
          paramArrayOfbyte2[b + paramInt3] = (byte)(paramArrayOfbyte1[b + paramInt1] ^ this.k[b]);
        } 
      } 
    } 
    return paramInt2;
  }
}
