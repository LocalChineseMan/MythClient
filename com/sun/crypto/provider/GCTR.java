package com.sun.crypto.provider;

import javax.crypto.IllegalBlockSizeException;

final class GCTR {
  private final SymmetricCipher aes;
  
  private final byte[] icb;
  
  private byte[] counter;
  
  private byte[] counterSave = null;
  
  GCTR(SymmetricCipher paramSymmetricCipher, byte[] paramArrayOfbyte) {
    this.aes = paramSymmetricCipher;
    if (paramArrayOfbyte.length != 16)
      throw new RuntimeException("length of initial counter block (" + paramArrayOfbyte.length + ") not equal to AES_BLOCK_SIZE (" + '\020' + ")"); 
    this.icb = paramArrayOfbyte;
    this.counter = (byte[])this.icb.clone();
  }
  
  int update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (paramInt2 - paramInt1 > paramArrayOfbyte1.length)
      throw new RuntimeException("input length out of bound"); 
    if (paramInt2 < 0 || paramInt2 % 16 != 0)
      throw new RuntimeException("input length unsupported"); 
    if (paramArrayOfbyte2.length - paramInt3 < paramInt2)
      throw new RuntimeException("output buffer too small"); 
    byte[] arrayOfByte = new byte[16];
    int i = paramInt2 / 16;
    for (byte b = 0; b < i; b++) {
      this.aes.encryptBlock(this.counter, 0, arrayOfByte, 0);
      for (byte b1 = 0; b1 < 16; b1++) {
        int j = b * 16 + b1;
        paramArrayOfbyte2[paramInt3 + j] = (byte)(paramArrayOfbyte1[paramInt1 + j] ^ arrayOfByte[b1]);
      } 
      GaloisCounterMode.increment32(this.counter);
    } 
    return paramInt2;
  }
  
  protected int doFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException {
    try {
      if (paramInt2 < 0)
        throw new IllegalBlockSizeException("Negative input size!"); 
      if (paramInt2 > 0) {
        int i = paramInt2 % 16;
        int j = paramInt2 - i;
        update(paramArrayOfbyte1, paramInt1, j, paramArrayOfbyte2, paramInt3);
        if (i != 0) {
          byte[] arrayOfByte = new byte[16];
          this.aes.encryptBlock(this.counter, 0, arrayOfByte, 0);
          for (byte b = 0; b < i; b++)
            paramArrayOfbyte2[paramInt3 + j + b] = (byte)(paramArrayOfbyte1[paramInt1 + j + b] ^ arrayOfByte[b]); 
        } 
      } 
    } finally {
      reset();
    } 
    return paramInt2;
  }
  
  void reset() {
    System.arraycopy(this.icb, 0, this.counter, 0, this.icb.length);
    this.counterSave = null;
  }
  
  void save() {
    this.counterSave = (byte[])this.counter.clone();
  }
  
  void restore() {
    if (this.counterSave != null)
      this.counter = this.counterSave; 
  }
}
